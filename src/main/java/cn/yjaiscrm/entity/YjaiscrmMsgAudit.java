package cn.iyque.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "iyque_msg_audit")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueMsgAudit extends BaseEntity{



    //对应的消息id
    @Id
    private String msgId;


    //消息发送人id
    private String fromId;

    //消息发送人名称
    private String fromName;

    //消息接收人id
    private String acceptId;

    //消息的接受类型:1:客户或成员;2:客群
    private Integer acceptType;

    //消息接收人名称
    private String acceptName;



    //消息类型
    private String msgType;

    //消息内容
    private String content;

    //当前数据对应的下标
    private long dataSeq;

    //消息的发送时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date  msgTime;

    //当前数据入库创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
