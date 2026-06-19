package cn.iyque.enums;

/**
 * 客户会话状态枚举
 * 根据企业微信获客链接客户列表API的chat_status字段定义
 */
public enum CustomerChatStatus {

    /**
     * 未开口 - 客户未发消息
     */
    NOT_STARTED(0, "未开口"),

    /**
     * 已开口 - 客户已发消息
     */
    STARTED(1, "已开口"),

    /**
     * 已删除 - 客户已被删除或其他异常状态
     */
    DELETED(2, "已删除");

    private final Integer code;
    private final String description;

    CustomerChatStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取枚举
     */
    public static CustomerChatStatus getByCode(Integer code) {
        if (code == null) {
            return NOT_STARTED;
        }

        for (CustomerChatStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return NOT_STARTED; // 默认返回未开口
    }

    /**
     * 根据企业微信API的chat_status值转换为我们的枚举
     * 企业微信API文档：
     * 0 - 客户未发消息
     * 1 - 客户已发消息
     * 2 - 客户发送消息状态未知
     */
    public static CustomerChatStatus convertFromWeChatStatus(Integer weChatStatus) {
        if (weChatStatus == null) {
            return NOT_STARTED;
        }

        switch (weChatStatus) {
            case 0:
                return NOT_STARTED; // 客户未发消息
            case 1:
                return STARTED; // 客户已发消息
            case 2:
                return NOT_STARTED; // 状态未知，归类为未开口
            default:
                return NOT_STARTED; // 默认未开口
        }
    }
}
