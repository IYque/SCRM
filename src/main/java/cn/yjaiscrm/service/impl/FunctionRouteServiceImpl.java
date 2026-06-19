package cn.iyque.service.impl;

import cn.iyque.domain.FunctionRoute;
import cn.iyque.service.FunctionRouteService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FunctionRouteServiceImpl implements FunctionRouteService {

    private static final List<FunctionRoute> FUNCTION_ROUTES = new ArrayList<>();

    static {
        FUNCTION_ROUTES.add(new FunctionRoute("/customerLink", "获客外链", "获客-私域获客", "快速构建企业专属客户池"));
        FUNCTION_ROUTES.add(new FunctionRoute("/index", "员工活码", "获客-私域获客", "员工二维码管理"));
        FUNCTION_ROUTES.add(new FunctionRoute("/groupCode", "客群活码", "获客-私域获客", "客户群二维码管理"));
        FUNCTION_ROUTES.add(new FunctionRoute("/customerSea", "客户公海", "获客-私域获客", "公共客户资源池"));
        
        FUNCTION_ROUTES.add(new FunctionRoute("/manage/customer", "客户列表", "管理-私域管理", "高效维护企业客户"));
        FUNCTION_ROUTES.add(new FunctionRoute("/manage/user", "员工列表", "管理-私域管理", "员工信息管理"));
        FUNCTION_ROUTES.add(new FunctionRoute("/manage/groupChat", "客群列表", "管理-私域管理", "客户群管理"));
        FUNCTION_ROUTES.add(new FunctionRoute("/manage/customerTag", "AI客户标签", "管理-私域管理", "AI智能客户标签管理"));
        FUNCTION_ROUTES.add(new FunctionRoute("/manage/groupTag", "AI客群标签", "管理-私域管理", "AI智能客群标签管理"));
        
        FUNCTION_ROUTES.add(new FunctionRoute("/marketing/massMarketing/customer", "客户群发", "营销-私域营销", "高效触达私域客户"));
        FUNCTION_ROUTES.add(new FunctionRoute("/marketing/massMarketing/group", "客群群发", "营销-私域营销", "群发营销管理"));
        FUNCTION_ROUTES.add(new FunctionRoute("/marketing/marketingTools/H5Marketing/list", "H5营销", "营销-私域营销", "H5营销活动管理"));
        
        FUNCTION_ROUTES.add(new FunctionRoute("/content/commonMaterial", "普通素材", "内容-内容中心", "企业私有知识库"));
        FUNCTION_ROUTES.add(new FunctionRoute("/content/combinedRhetoric/index", "组合话术", "内容-内容中心", "话术管理"));
        
        FUNCTION_ROUTES.add(new FunctionRoute("/customerService/corpCustomerService/customerServiceManage", "基础客服", "客服-AI会话存档", "企微客服管理"));
        FUNCTION_ROUTES.add(new FunctionRoute("/customerService/corpCustomerService/customerServiceManageScheduled", "排班客服", "客服-AI会话存档", "客服排班管理"));
        FUNCTION_ROUTES.add(new FunctionRoute("/customerService/corpCustomerService/KBM", "AI知识库管理", "客服-AI会话存档", "知识库管理"));
        FUNCTION_ROUTES.add(new FunctionRoute("/customerService/corpCustomerService/serviceRecord", "服务记录", "客服-AI会话存档", "客服服务记录"));
        FUNCTION_ROUTES.add(new FunctionRoute("/customerService/corpCustomerService/sessionSummary", "AI会话总结", "客服-AI会话存档", "AI智能会话总结"));
        FUNCTION_ROUTES.add(new FunctionRoute("/customerService/complaint/complaintManage", "投诉管理", "客服-AI会话存档", "客户投诉管理"));
        
        FUNCTION_ROUTES.add(new FunctionRoute("/chat/khchat/index", "客户会话内容", "AI会话-AI会话存档", "企微会话内容存档"));
        FUNCTION_ROUTES.add(new FunctionRoute("/chat/khchat/inquiryCustomer", "AI会话预审", "AI会话-AI会话存档", "客户会话预审"));
        FUNCTION_ROUTES.add(new FunctionRoute("/chat/khchat/intentionCustomer", "AI意向客户", "AI会话-AI会话存档", "AI识别意向客户"));
        FUNCTION_ROUTES.add(new FunctionRoute("/chat/kqchat/kqchat", "客群会话内容", "AI会话-AI会话存档", "客群会话存档"));
        FUNCTION_ROUTES.add(new FunctionRoute("/chat/kqchat/inquiryKqChat", "客群会话预审", "AI会话-AI会话存档", "客群会话预审"));
        FUNCTION_ROUTES.add(new FunctionRoute("/chat/kqchat/intentionGroupFriend", "AI意向群友", "AI会话-AI会话存档", "AI识别意向群友"));
        FUNCTION_ROUTES.add(new FunctionRoute("/chat/conversationalInsights/hotWordManage", "热词管理", "AI会话-AI会话存档", "会话热词管理"));
        FUNCTION_ROUTES.add(new FunctionRoute("/chat/conversationalInsights/hotWordAnalysis", "热词分析", "AI会话-AI会话存档", "会话热词分析"));
        FUNCTION_ROUTES.add(new FunctionRoute("/chat/riskControl/violationIntercept", "违规拦截", "AI会话-AI会话存档", "风控审计管理"));
        
        FUNCTION_ROUTES.add(new FunctionRoute("/config/index", "系统配置", "配置-配置中心", "企微&系统配置"));
        FUNCTION_ROUTES.add(new FunctionRoute("/config/operateLog", "操作日志", "配置-配置中心", "系统操作日志"));
        FUNCTION_ROUTES.add(new FunctionRoute("/config/groupRobot", "群机器人", "配置-配置中心", "群机器人管理"));
        FUNCTION_ROUTES.add(new FunctionRoute("/config/enterpriseNotice", "企业公告", "配置-配置中心", "企业公告管理"));
        FUNCTION_ROUTES.add(new FunctionRoute("/config/fileSecurity", "文件安全", "配置-配置中心", "文件安全管理"));
        FUNCTION_ROUTES.add(new FunctionRoute("/config/screenShot", "截屏安全", "配置-配置中心", "截屏安全管理"));
    }

    @Override
    public List<FunctionRoute> getAllFunctionRoutes() {
        return new ArrayList<>(FUNCTION_ROUTES);
    }

    @Override
    public String getFunctionRoutesAsText() {
        StringBuilder sb = new StringBuilder();
        sb.append("以下是系统中所有可用的功能模块：\n\n");
        
        String currentCategory = "";
        for (FunctionRoute route : FUNCTION_ROUTES) {
            if (!route.getCategory().equals(currentCategory)) {
                currentCategory = route.getCategory();
                sb.append("【").append(currentCategory).append("】\n");
            }
            sb.append("  - ").append(route.getTitle())
              .append("（路径：").append(route.getPath()).append("）");
            if (route.getDescription() != null && !route.getDescription().isEmpty()) {
                sb.append("：").append(route.getDescription());
            }
            sb.append("\n");
        }
        
        sb.append("\n当用户询问与上述功能相关的问题时，请推荐合适的功能路径。");
        sb.append("如果用户的问题与系统功能无关，请直接回答用户的问题。");
        sb.append("\n\n**重要：推荐功能时，请使用Markdown链接格式，让用户可以直接点击跳转：**\n");
        sb.append("格式：[功能名称](功能路径)\n");
        sb.append("例如：[客户列表](/manage/customer)\n");
        sb.append("\n请按以下格式回复：\n");
        sb.append("根据您的需求，推荐以下功能：\n\n");
        sb.append("1. [功能名称](功能路径) - 推荐理由\n");
        sb.append("2. [功能名称](功能路径) - 推荐理由\n");
        sb.append("\n如果没有匹配的功能，请直接回答用户的问题，不要推荐功能。\n");
        
        return sb.toString();
    }
}
