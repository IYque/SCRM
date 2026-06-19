package cn.iyque.enums;



import java.util.stream.Stream;

/**
 * 客户备注类型
 */
public enum RemarksType {

    REMARKS_TYPE_STATENAME(1,"渠道名"),
    REMARKS_TYPE_TAGS(2,"新客标签"),
    REMARKS_TYPE_ADDTIME(3,"添加时间");

    private final Integer code;
    private final String info;

    RemarksType(Integer code, String info)
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

    public static  RemarksType of(Integer type){
        return Stream.of(values()).filter(s->s.getCode().equals(type)).findFirst().orElseGet(null);
    }

}
