package cn.iyque.entity;


import cn.hutool.json.JSONUtil;
import cn.iyque.domain.fileType.Image;
import cn.iyque.domain.fileType.Link;
import cn.iyque.domain.fileType.Text;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 机器人附件
 */
@Entity(name = "iyque_robot_sub")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "delFlag = 0")
public class IYqueRobotSub {
    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;

    //机器人主键
    private Long robotId;


    //消息发送标题
    private String msgTitle;


    //附件类型
    private String msgType;

    /**
     * 素材状态:0 正常 1 删除
     */
    private Integer delFlag;


    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;

    //附件字符串
    @JsonIgnore
    private String annexContent;

    //文本
    @Transient
    private Text text;

    //图片
    @Transient
    private Image image;

    //图文
    @Transient
    private Link link;

    @PostLoad
    public void postLoad() {
        if(IYqueMsgAnnex.MsgType.MSG_TEXT.equals(msgType)){
            text = JSONUtil.toBean(annexContent,Text.class);
        }else if (IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE.equals(msgType)) {
            image = JSONUtil.toBean(annexContent,Image.class);
        } else if (IYqueMsgAnnex.MsgType.MSG_TYPE_LINK.equals(msgType)) {
            link = JSONUtil.toBean(annexContent,Link.class);
        }
    }

    public void prePersist(IYqueRobotSub iYqueRobotSub) {
        if (this.delFlag == null) {
            this.delFlag = 0;
        }
        if(IYqueMsgAnnex.MsgType.MSG_TEXT.equals(msgType)){
            if(iYqueRobotSub.getText() != null){
                annexContent =  JSONUtil.toJsonStr(iYqueRobotSub.getText());
            }


        }else if (IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE.equals(msgType)) {
            if(iYqueRobotSub.getImage() != null){
                annexContent = JSONUtil.toJsonStr(iYqueRobotSub.getImage());
            }

        } else if (IYqueMsgAnnex.MsgType.MSG_TYPE_LINK.equals(msgType)) {
            if(null != iYqueRobotSub.getLink()){
                annexContent = JSONUtil.toJsonStr(iYqueRobotSub.getLink());
            }

        }
    }
}
