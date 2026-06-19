package cn.iyque.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;


/**
 * 群发子表
 */
@Entity(name = "iyque_group_msg_sub")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueGroupMsgSub {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;



    //群发主表id
    private Long groupMsgId;



    //接受对象的类型(1:客户；2:客群)
    private Integer acceptType;



    //接受对象的id
    private String acceptId;

    //接受对象的名称
    private String acceptName;


    //发送人id
    private String senderId;


    //对应消息的id
    private String msgId;


    //0-未发送 1-已发送  2-发送失败（2-因客户不是好友导致发送失败 3-因客户已经收到其他群发消息导致发送失败  企业微信中返回的这二种状态对应该系统的3）3-无法发送
    private Integer status;


    //具体子状态 0-未发送 1-已发送  2-因客户不是好友导致发送失败 3-因客户已经收到其他群发消息导致发送失败 4-无效或无法发送的external_userid或chatid列表
    private Integer statusSub;



    /**
     * 送达时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;


}
