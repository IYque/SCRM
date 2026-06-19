package cn.iyque.enums;

/**
 * 客户状态
 */
public enum CustomerStatusType {
    CUSTOMER_STATUS_TYPE_COMMON(0,"正常"),
    CUSTOMER_STATUS_TYPE_LS(1,"客户流失"),
    CUSTOMER_STATUS_TYPE_DEL(2,"员工删除客户");
    private final Integer code;
    private final String info;

    CustomerStatusType(Integer code, String info)
    {
        this.code = code;
        this.info = info;
    }
    public Integer getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
