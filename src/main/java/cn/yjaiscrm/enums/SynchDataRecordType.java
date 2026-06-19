package cn.iyque.enums;


import lombok.Getter;

@Getter
public enum SynchDataRecordType {
    RECORD_TYPE_SYNCH_CUSTOMER(1,"客户同步"),
    RECORD_TYPE_SYNCH_SHORT_LINK(2,"获客外链同步"),
    RECORD_TYPE_SYNCH_USER_CODE(3,"员工活码同步"),
    RECORD_TYPE_SYNCH_MEMBEWR_OPERATE_LOG(4,"成员操作日志同步"),
    RECORD_TYPE_SYNCH_ADMIN_OPERATE_LOG(5,"管理员操作日志同步");
    private final Integer code;
    private final String info;

    SynchDataRecordType(Integer code, String info)
    {
        this.code = code;
        this.info = info;
    }


}
