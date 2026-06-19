package cn.iyque.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * 客服信息内容明细表
 */
@Entity(name = "iyque_kf_msg_sub")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueKfMsgSub {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;



    //客服信息主表id主键
    private Long kfMsgId;


    //所属客服的id
    private String openKfid;


    //客服名称
    private String kfName;


    //客服头像
    private String kfPicUrl;


    //客户id
    private String externalUserId;


    //接待人员的id
    private String switchUserId;

    //接待人员名称
    private String switchUserName;


    //消息来源。3-微信客户发送的消息 4-系统推送的事件消息 5-接待人员在企业微信客户端发送的消息
    private Integer origin;


    //消息id
    private String msgId;


    //客户名称
    private String nickname;

    //客户头像
    private String avatar;

    //客户unionid
    private String unionid;


    //外部联系人性别 0-未知 1-男性 2-女性
    private Integer gender;


    //聊天内容类型(text:文本;image:图片)
    private String msgType;


    //会话内容
    private String content;


    //会话时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;



}
