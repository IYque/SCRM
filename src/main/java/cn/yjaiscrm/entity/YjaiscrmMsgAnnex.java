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
 * 欢迎语附件
 */
@Entity(name = "iyque_msg_annex")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueMsgAnnex {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    @JsonIgnore
    private Long id;

    //欢迎语id
    private Long msgId;

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
        if (MsgType.MSG_TYPE_IMAGE.equals(msgtype)) {
            image = JSONUtil.toBean(annexContent,Image.class);
        } else if (MsgType.MSG_TYPE_LINK.equals(msgtype)) {
            link = JSONUtil.toBean(annexContent,Link.class);
        } else if (MsgType.MSG_TYPE_MINIPROGRAM.equals(msgtype)) {
            miniprogram = JSONUtil.toBean(annexContent,Miniprogram.class);
        } else if (MsgType.MSG_TYPE_VIDES.equals(msgtype)) {
            video = JSONUtil.toBean(annexContent,Video.class);
        } else if (MsgType.MSG_TYPE_FILE.equals(msgtype)) {
            file = JSONUtil.toBean(annexContent,File.class);
        }
    }

    @PrePersist
    public void prePersist() {
        if (MsgType.MSG_TYPE_IMAGE.equals(msgtype)) {
            annexContent = JSONUtil.toJsonStr(image);
        } else if (MsgType.MSG_TYPE_LINK.equals(msgtype)) {
            annexContent = JSONUtil.toJsonStr(link);
        } else if (MsgType.MSG_TYPE_MINIPROGRAM.equals(msgtype)) {
            annexContent = JSONUtil.toJsonStr(miniprogram);
        } else if (MsgType.MSG_TYPE_VIDES.equals(msgtype)) {
            annexContent = JSONUtil.toJsonStr(video);
        } else if (MsgType.MSG_TYPE_FILE.equals(msgtype)) {
            annexContent = JSONUtil.toJsonStr(file);
        }
    }


    //附件类型
    @Data
    public static class MsgType{

        //文本
        public final static String MSG_TEXT="text";
        //图片
        public final static String MSG_TYPE_IMAGE="image";
        //图文链接
        public final  static String MSG_TYPE_LINK="link";
        //图文卡片链接(机器人消息)
        public final  static String MSG_TYPE_NEWS="news";
        //小程序
        public final static String MSG_TYPE_MINIPROGRAM="miniprogram";
        //视频
        public final static String MSG_TYPE_VIDES="video";
        //文件
        public final static String MSG_TYPE_FILE="file";
    }










}
