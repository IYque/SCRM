package cn.iyque.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "iyque_customer_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYQueCustomerInfo {
    //主键为 externalUserid+"&"+userId
    @Id
    private String eId;

    //客户名称
    private String customerName;

    //客户头像
    private String avatar;

    //客户类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户
    private Integer type;

    //客户id
    private String externalUserid;


    //添加方式  CustomerAddWay
    private String addWay;


    //添加人id
    private String userId;


    //客户标签id，多个使用都好隔开
    private String tagIds;

    //标签名,多个使用逗号隔开
    @Transient
    private String tagNames;

    //添加人名称
    @Transient
    private String userName;


    //添加的渠道标识
    private String state;

    //添加时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;




    // 0正常;1:客户流失;2:员工删除客户
    private Integer status;

}
