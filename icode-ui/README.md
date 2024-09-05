# yq

本仓库为 yq 前端源码库，技术栈采用 [[vue3](https://cn.vuejs.org/) [vite](https://cn.vitejs.dev/) [pinia](https://pinia.vuejs.org/zh/) [element-plus](https://element-plus.gitee.io/zh-CN/)]

## 前端结构

```
├── you-project-name			// 后台管理端项目
    ├── public                     # 静态资源
    │   │── static                 # 用于绝对路径的非打包资源，公用基础css等
    │   │── favicon.ico            # favicon图标
    ├── src                        # 源代码
    │   ├── api                    # 后端公共接口请求
    │   ├── assets                 # 主题 字体 svg icons 等静态资源
    │   ├── components             # 全局公用组件
    │   ├── layout                 # 全局基础布局结构组件
    │   ├── router                 # 路由 权限管理等
    │   ├── stores                 # 全局 pinia store管理
    │   ├── styles                 # 全局样式
    │   ├── utils                  # 全局公用方法
    │   ├── views                  # 业务功能所有页面
    │   ├── App.vue                # 入口页面
    │   ├── config.js              # 全局配置文件
    │   ├── main.ts                # 入口文件 加载组件 初始化等
    ├── babel.config.js            # babel-loader 配置
    ├── sys.config.ts                    # 环境变量配置
    ├── index.html                 # html模板
    ├── jsconfig.json              # jsconfig 配置 快捷路径等
    ├── package.json               # package.json
    ├── vite.config.ts             # vite 配置
```

## 系统配置

在 [sys.config.ts](./sys.config.ts) 中配置开发、生产等各个环境的：接口域名、路由基础路径，页面基础路径等

```js
// 环境变量
const envs = {
  development: {
    DOMAIN: 'https://show.iyque.cn', // 站点域名，会根据此处域名判断应用环境
    BASE_URL: '/tools/', // 页面路由基础路径 /*/*/，eg：/a/
    BASE_API: 'https://show.iyque.cn/iyque', // 接口基础路径
  },
  production: {
    DOMAIN: 'https://show.iyque.cn',
    BASE_URL: '/tools/',
    BASE_API: 'https://show.iyque.cn/iyque',
  },
}

let mode =
  process.env.NODE_ENV == 'development' || !globalThis.document
    ? process.env.VUE_APP_ENV
    : Object.keys(envs).find((e) => envs[e].DOMAIN === window?.location.origin)

export const env = { ...envs[mode], ENV: mode }

// 系统常量配置
export const common = {
  SYSTEM_NAME: '源雀', // 系统简称
  SYSTEM_SLOGAN:
    '<a href="https://www.iyque.cn?utm_source=iyquecode" target="_blank">源雀SCRM -基于SpringCloud+Vue架构,100%开放源码的企微私域营销系统</a> ', // 系统标语
  COPYRIGHT: 'Copyright © 2024 源雀 All Rights Reserved.', // 版权信息
  LOGO: env.BASE_URL + 'static/logo.png', // 深色logo
  COOKIEEXPIRES: 0.5, // token在Cookie中存储的天数，默认0.5天
}
```

## 运行与部署

**Node 推荐 16.x 及以上版本**

```sh
# 进入项目目录
cd you-project-name

# 安装依赖
npm i
# 或者使用cnpm
npm install -g cnpm --registry=https://registry.npmmirror.com
cnpm i

# 启动开发服务
npm run dev

# 构建生产环境
npm run build
```
