package cn.iyque.entity;


import cn.hutool.json.JSONUtil;
import cn.iyque.domain.fileType.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;

/**
 * 时段欢迎语附件
 */
@Entity(name = "iyque_period_msg_annex")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYquePeriodMsgAnnex {
    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    @JsonIgnore
    private Long id;

    //时段主键
    private Long annexPeroidId;

    //附件类型
    private String msgtype;


    //附件字符串
    @JsonIgnore
    private String annexContent;

    @Transient
    private Image image;

    @Transient
    private Link link;

    @Transient
    private Miniprogram miniprogram;

    @Transient
    private Video video;

    @Transient
    private File file;

    @PostLoad
    public void postLoad() {
        if (IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE.equals(msgtype)) {
            image = JSONUtil.toBean(annexContent,  Image.class);
        } else if (IYqueMsgAnnex.MsgType.MSG_TYPE_LINK.equals(msgtype)) {
            link = JSONUtil.toBean(annexContent, Link.class);
        } else if (IYqueMsgAnnex.MsgType.MSG_TYPE_MINIPROGRAM.equals(msgtype)) {
            miniprogram = JSONUtil.toBean(annexContent, Miniprogram.class);
        } else if (IYqueMsgAnnex.MsgType.MSG_TYPE_VIDES.equals(msgtype)) {
            video = JSONUtil.toBean(annexContent, Video.class);
        } else if (IYqueMsgAnnex.MsgType.MSG_TYPE_FILE.equals(msgtype)) {
            file = JSONUtil.toBean(annexContent, File.class);
        }
    }

    @PrePersist
    public void prePersist() {
        if (IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE.equals(msgtype)) {
            annexContent = JSONUtil.toJsonStr(image);
        } else if (IYqueMsgAnnex.MsgType.MSG_TYPE_LINK.equals(msgtype)) {
            annexContent = JSONUtil.toJsonStr(link);
        } else if (IYqueMsgAnnex.MsgType.MSG_TYPE_MINIPROGRAM.equals(msgtype)) {
            annexContent = JSONUtil.toJsonStr(miniprogram);
        } else if (IYqueMsgAnnex.MsgType.MSG_TYPE_VIDES.equals(msgtype)) {
            annexContent = JSONUtil.toJsonStr(video);
        } else if (IYqueMsgAnnex.MsgType.MSG_TYPE_FILE.equals(msgtype)) {
            annexContent = JSONUtil.toJsonStr(file);
        }
    }



}
