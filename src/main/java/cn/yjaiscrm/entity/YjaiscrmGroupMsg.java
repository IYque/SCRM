package cn.iyque.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;


/**
 * 群发主表
 */
@Entity(name = "iyque_group_msg")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueGroupMsg {

    @Id
    private Long id;


    //群发名称
    private String groupMsgName;

    //群发类型：single:客户群发 group:客群群发
    private String chatType;

    //群发范围：0:全部 1:部份
    private Integer scopeType;


    //发送类型: 1:立即发送 2:定时发送
    private Integer sendType;


    //群发文字内容
    private String content;



    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;


    /**
     * 接受对象
     */
    @Transient
    private List<IYqueGroupMsgSub> groupMsgSubList;


    /**
     * 发送附件
     */
    @Transient
    private List<IYqueMsgAnnex> annexLists;






}
