# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

**yjaiscrm SCRM 开源版** —— 基于企业微信的私域数智化营销系统。覆盖「拓客 → 管理 → 运营 → 营销 → 服务 → 分析」全链路，并深度集成 AI（RAG 知识库、会话预审、意向分析、智能客服、标签生成等）。

这是一个 **monorepo**，包含三个独立部署单元：

| 子系统 | 路径 | 技术栈 |
|--------|------|--------|
| 后端 API | `src/`、`pom.xml` | Spring Boot 2.7.3 / Java 17 单模块 Maven 工程，包名 `cn.yjaiscrm` |
| PC 管理后台 | `frontEnd/pc/` | Vue 3 + Vite + Pinia + Element Plus + Tailwind |
| 移动端 H5 | `frontEnd/mobile/` | Vue 3 + Vite + Pinia + Vant 4（嵌入微信/企微 WebView） |

后端约 378 个 Java 文件、PC 约 151 个 `.vue`、Mobile 约 20 个 `.vue`。

## 常用命令

### 后端（仓库根目录）

无 Maven wrapper，使用系统 `mvn`（需 JDK 17）：

```bash
mvn clean package                 # 编译并打可执行 jar（spring-boot repackage）
java -jar target/yjaiscrm-code-1.0-SNAPSHOT.jar
mvn spring-boot:run               # 直接运行
```

> 仓库当前**没有 `src/test` 目录与任何测试代码**（虽引入了 `spring-boot-starter-test`）。新增测试请放在 `src/test/java`。

### 前端

```bash
# PC 管理后台
cd frontEnd/pc
npm install
npm run dev          # 开发服务器，端口 2024（test 模式为 2025）
npm run build        # 生产构建
npm run type-check   # vue-tsc 类型检查

# 移动端 H5
cd frontEnd/mobile
npm install
npm run dev          # 开发服务器
npm run build
npm run lint         # eslint --fix（仅 mobile 配了 ESLint/Prettier）
npm run format       # prettier --write src/
```

开发模式下两个前端都通过 Vite proxy 把 `/api` 转发到 `BASE_API`（即后端 `:8085`）。环境（development/test/production）由 `sys.config.ts`(PC) / `sys.config.js`(mobile) 中**根据 `window.location.origin` 匹配 DOMAIN** 自动判断。

### 运行所需的外部依赖

`src/main/resources/application.yml` 默认指向本机服务，启动前需就绪：
- **MySQL 8**：`jdbc:mysql://127.0.0.1:10179/scrm_ky`（注意端口是 **10179** 而非 3306），账号 `root/root`。`spring.jpa.hibernate.ddl-auto=none` —— **不会自动建表**，需先导入建表 SQL。
- **Redis**：`127.0.0.1:6379`，**database 1**（非默认 0）。启动期 `SimpleRedisValidator` 仅做 ping 探测，失败不阻断启动。
- **Milvus 向量库**：`127.0.0.1:19530`（gRPC 端口）。仅 AI 知识库功能需要。
- **大模型 API Key**：默认配置为智谱 GLM-4 / embedding-3（`ai.models`、`ai.vector`）。

## 后端架构（关键约定）

### 启动与全局拦截
- 入口 `cn.yjaiscrm.YjaiscrmApplication`：`@SpringBootApplication(exclude=PageHelperAutoConfiguration.class)` + `@MapperScan("cn.yjaiscrm.mapper")`。
- `@EnableAsync`（`config/AsyncConfig`）、`@EnableScheduling`（`config/ScheduleConfig`）。
- 两个 `HandlerInterceptor`（注册见 `config/JwtConfig`、`config/CORSConfig`）：
  - **`interceptor/JwtInterceptor`**：全局 `/**` 鉴权，仅放行登录/企微回调/文件预览/投诉提交/H5 营销查看等少数白名单路径（白名单硬编码在该类中，新增免登接口需在此追加）。
  - **`interceptor/ReadOnlyInterceptor`**：演示模式守卫。当 `yjaiscrm.demo=true` 时拦截所有写操作（非 GET/HEAD/OPTIONS），返回「演示环境数据无法修改」。改写功能前留意该开关。

### 认证流程
- 账号密码来自配置 `yjaiscrm.userName` / `yjaiscrm.pwd`（**非数据库用户表**，开源版单账号）；企微 H5 走 OAuth2（`weComLogin`）。
- `utils/JwtUtils` 生成/校验 JWT（密钥硬编码，有效期 7 天），subject 存用户名。
- Token 通过 **`Authorization: Bearer <token>`** 头传递；当前用户名需在业务层用 `JwtUtils.getUsernameFromToken()` 自行解析（拦截器不注入 ThreadLocal）。
- `@RateLimit`（`annotation/` + `aop/RateLimitAspect`）基于 Redis 按 IP+方法做限流，用于登录等接口。

### 统一响应信封
所有接口返回 `domain/ResponseResult<T>`：`{ code, msg, data, count }`（`@JsonInclude(NON_NULL)`）。`code` 复用 HTTP 语义：200 成功、401 重新登录、429 限流、500 错误（见 `constant/HttpStatus`）。分页接口用 `count` 返回总数。前端响应拦截器据此判断（见前端章节）。

### 持久化：JPA 与 MyBatis-Plus 并存 ⚠️
这是本仓库**最容易踩坑的约定**——两套 ORM 共存，按模块二选一，**同一 Entity 不跨框架混用**：

| | Spring Data JPA（主） | MyBatis-Plus（辅） |
|---|---|---|
| 接口包 | `dao/`，继承 `JpaRepository` + `JpaSpecificationExecutor` | `mapper/`，继承 `BaseMapper`；复杂 SQL 写 `resources/mapper/*.xml` |
| Service 实现 | 不继承基类，手写逻辑 | 继承 `ServiceImpl<M,T>`，复用通用 CRUD |
| 实体注解 | `@Entity` / `@Where(clause="delFlag=0")` 软删除 | `@TableName` / `@TableLogic` / `@TableField(fill=...)` |
| 动态查询 | JPA `Specification` | `LambdaQueryWrapper` |

- **分页**：项目用 **JPA `PageRequest`** 为主，分页参数由 `utils/TableSupport.buildPageRequest()` 从请求中提取，**注意它会把 `pageNum` 减 1**（HTTP 从 1 开始、JPA 从 0 开始）。MyBatis-Plus 自带分页插件**被禁用**，改用 PageHelper（但 `PageHelperAutoConfiguration` 已在启动类排除，手动配置见 `config/MybatisPlusConfig`）。
- **`map-underscore-to-camel-case: false`**：DB 用下划线、Java 用驼峰，MyBatis XML 查询**必须显式写列别名/`resultMap`**，不会自动转换。（`application.yml` 中 `type-aliases-package: com.yourproject.entity` 是未修改的占位符，XML 里用全限定 `resultType`。）
- ID 用雪花算法生成（`@GeneratedValue(generator="snowflakeIdGenerator")`）；`config/JacksonConfig` 将 `Long` 序列化为 String 防 JS 精度丢失。
- 分层：`controller → service(接口) → service/impl → dao|mapper → entity`。`domain/` 放 DTO/请求响应模型（与 `entity/` 隔离）；`converter/` + 工厂负责 Entity↔企微 API 对象转换；`enums/`、`constant/` 放枚举与常量。
- **命名约定**：几乎所有类以 `Yjaiscrm` 前缀开头（如 `YjaiscrmAgentController`、`YjaiscrmUserDao`）。

### 企业微信集成（`wxjava/`、`factory/`、`mass/`、`schedule/`）
- 封装库为 **WxJava（`weixin-java-cp` 4.7.6.B）**。
- 企微凭证（corpId/agentSecret/token/aesKey/会话存档配置等）存在数据库 **`yjaiscrm_config` 单条记录**中。`service/YjaiscrmConfigService.findWxcpservice()` 每次**动态**通过 `service/impl/WxCpServiceFactory` 构建 `WxCpService`（不缓存，支持配置热更新）。需要操作企微 API 时统一从这里取实例。
- **回调入口** `controller/IYcallbackController`（`/iycallback/handle`）：GET 验签、POST 用 `YjaiscrmCryptUtil` 解密，再按事件分发：
  - `kf_msg_or_event` → `YjaiscrmKfService`（客服消息：AI 应答 / 转人工 / 排班）
  - `msgaudit_notify` → `YjaiscrmMsgAuditService`（会话存档同步 + AI 预审/意向分析）
  - `change_external_contact` → `YjaiscrmCustomerInfoService`（客户增删/流失同步）
- **群发**（`mass/`）：工厂 + 模板方法 + 策略。`MassSenderFactoryService` 按类型返回 `CustomerMassSender`（客户）或 `GroupMassSender`（客群），二者继承 `AbstractMassSender` 的 `executeMassSend()` 骨架（准备目标 → 调企微 → 落库，事务包裹）。
- **新客户回调动作**用策略模式：`strategy/callback/ActionStrategy` 的实现（`SaveCustomer`/`RemarkCustomer`/`MakeTag`/`SendWelcomeMsg`…）通过 `ActionContext` 串联打标签、备注、欢迎语等。
- 定时任务集中在 `schedule/QuartzJob`（如朋友圈 jobId→momentId，每小时）。数据同步主要靠企微回调推送，而非轮询。

### AI / RAG 子系统（`chain/`、`factory/`、`prompt/`、`strategy/`）
- **两个 AI 框架分工**：**LangChain4j**（`1.0.0-beta3`）负责 LLM 对话/流式/嵌入；**ai4j-spring-boot-starter** 主要提供 `RecursiveCharacterTextSplitter` 等工具。
- **RAG 管道**（`chain/` 下分包）：`loader/`（txt/pdf/docx 加载，工厂选择）→ `split/`（chunkSize=1000, overlap=200）→ `vectorizer/`（调嵌入模型）→ `vectorstore/MilvusVectorStore`（存储/检索）。
- **Milvus 多租户**：每个知识库 `kid` 对应独立 Collection `yjaiscrm_vectors{kid}`，IVF_FLAT 索引 + COSINE，相似度阈值 0.5、取 Top-N。
- **模型工厂** `factory/AiModelFactory` 按 `ai.models`/`ai.vector`（`properties/AiModelsProperties`、`AiVectorProperties`）缓存 `ChatLanguageModel`/`StreamingChatLanguageModel`/`EmbeddingModel`。AI 业务入口为 `service/YjaiscrmAiService(Impl)`。
- **提示词**集中在 `resources/prompts/role-prompts.yml`，由 `prompt/AiPromptManager` 在 `@PostConstruct` 载入，按 `{prefix}.{roleKey}` 取用，每条含 `system`/`user` 模板。
- **对话记忆淘汰**用策略模式：`strategy/HistoryEvictionPolicy`（`SummaryEvictionPolicy` 摘要 / `SlidingWindowEvictionPolicy` 滑窗），由 `HistoryEvictionPolicyManager` 按名选择，默认 `summary`。

## 前端架构

### PC 管理后台（`frontEnd/pc`）
- 启动 `src/main.ts`：先加载 `src/config.js` 注入 `window.sysConfig`（含 BASE_API、token headers），再注册 Element Plus（`ElInput` 默认 `clearable`）、SVG 图标、自动导入 `components/**/*.vue` 为全局异步组件、挂载全局 `$sdk`/`$msgSuccess` 等到 `globalProperties`。
- **请求** `src/utils/request.js`：`requestFactory(gateway)` 工厂创建 axios 实例；开发用 `/api`(走 proxy)、生产用 `window.sysConfig.BASE_API`。请求拦截器在有 token 时把 `window.sysConfig.headers`（含 Bearer token）合并进 header；响应拦截器按 `ResponseResult.code` 处理（200/301 放行、401 弹窗并 `LogOut`、其它报错）。
- **Token 存储**在 **Cookie `Admin-Token`**（`utils/auth.js`，js-cookie）。
- **路由权限是静态的**（`router/routes.js` 硬编码全部菜单），**没有后端动态路由**；`router/permission.js` 仅按 token + 白名单做登录跳转，**真正的权限边界靠后端返回 401 触发登出**。新增页面在 `routes.js` 注册。
- **自动导入**：`ref/reactive/computed/watch/useRouter` 等 Vue/Vue-Router API 已由 `unplugin-auto-import` 全局注入（声明在 `auto-imports.d.ts`），**无需手写 import**。
- **SVG 图标**：放入 `src/assets/icons/svg/`，自动注册为 `icon-[dir]-[name]`，用 `<SvgIcon icon="xxx" />` 引用。
- 路径别名：`@`→`src`，`~`→`src/components`。TS/JS 混用，类型严格度中等。

### 移动端 H5（`frontEnd/mobile`）
- 以 H5 形式**嵌入微信 / 企业微信 WebView**，对应后端的投诉页、客户公海、H5 营销、聊天素材、组合话术等入口，部署在 nginx 的 `/openmobile` 路径下。引入了 **vConsole**（移动调试）和 **Vant 4**。
- **多网关请求** `src/utils/request.js`：导出 `requestOpen`(`/open`)、`requestWeChat`(`/wx-api`)、`requestAi`(`/ai`) 及默认实例，分别对应 `BASE_API` 下不同前缀。token 取自 **`sessionStorage`** 并以 Bearer 头发送；`qs` 序列化数组参数。响应 401 清 sessionStorage 并刷新，501 用于红包等特殊场景。
- **双授权机制**（`router/permission.js`）：默认走**企微** `ww.register`（`getAgentTicket`），页面 `meta.authType==='wechat'` 时走**微信公众号** `wx.config`（`getWxTicket`）；`meta.noAuth` 跳过登录、`meta.wxSDKConfigType` 控制是否注入 JSSDK。会话信息（corpId/agentId/userId/openId 等）存 `sessionStorage`。
- 每个功能模块自带 `api.(js|ts)`（如 `views/customerComplaint/api.js`），不走 Service 类封装。

## 部署拓扑（`configFile/nginx.conf`）

线上由 nginx 统一入口：
- `location ^~ /yjaiscrm/` → 反代到后端 `http://127.0.0.1:8085/`（即前端 `BASE_API` 的 `/yjaiscrm` 前缀）。
- `location /tools` → PC 管理后台静态资源（对应前端 `BASE_URL=/tools/`）。
- `location /openmobile` → 移动端 H5 静态资源。

后端各 URL 前缀（如投诉页 `complaintUrl`、公海 `customerSeasUrl`、H5 营销 `h5MarketUrl`）配置在 `application.yml` 的 `yjaiscrm.*` 下。
