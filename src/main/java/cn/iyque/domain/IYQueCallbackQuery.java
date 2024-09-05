package cn.iyque.domain;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

@Data
public class IYQueCallbackQuery {


    //业务id,渠道活码id或者获客短链id
    private Long businessId;

    //标签id，多个使用逗号隔开
    private String tagId;


    //标签名
    private String tagName;


    //是否开启时段欢迎语 true:开启时段欢迎语; false:关闭时段欢迎语;
    private boolean startPeriodAnnex;


    //未开启时段欢迎语的附件，如果：startPeriodAnnex 为true 当前字段值为空。
    private String weclomeMsg;



    //备注名字
    private String remarkName;


    //客户备注类型cn.iyque.enums.RemarksType
    private Integer remarkType;


}
