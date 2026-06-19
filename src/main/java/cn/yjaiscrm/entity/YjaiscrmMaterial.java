package cn.iyque.entity;


import cn.hutool.json.JSONUtil;
import cn.iyque.domain.fileType.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;


/**
 * 素材中心相关表
 */
@Entity(name = "iyque_material")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "delFlag = 0")
public class IYqueMaterial {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;

    //附件类型
    private String msgtype;


    //标题
    private String title;


    /**
     * 分类id
     */
    private String categoryId;


    /**
     * 更新人名称
     */
    private String updateBy;


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
     * 素材状态:0 正常 1 删除
     */
    private Integer delFlag;

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
        if(IYqueMsgAnnex.MsgType.MSG_TEXT.equals(msgtype)){
            text = JSONUtil.toBean(annexContent,Text.class);
        }else if (IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE.equals(msgtype)) {
            image = JSONUtil.toBean(annexContent,Image.class);
        } else if (IYqueMsgAnnex.MsgType.MSG_TYPE_LINK.equals(msgtype)) {
            link = JSONUtil.toBean(annexContent,Link.class);
        }
    }

    public void prePersist(IYqueMaterial material) {
        if (this.delFlag == null) {
            this.delFlag = 0;
        }
        if(IYqueMsgAnnex.MsgType.MSG_TEXT.equals(msgtype)){
            if(material.getText() != null){
                annexContent =  JSONUtil.toJsonStr(material.getText());
            }


        }else if (IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE.equals(msgtype)) {
            if(material.getImage() != null){
                annexContent = JSONUtil.toJsonStr(material.getImage());
            }

        } else if (IYqueMsgAnnex.MsgType.MSG_TYPE_LINK.equals(msgtype)) {
            if(null != material.getLink()){
                annexContent = JSONUtil.toJsonStr(material.getLink());
            }

        }
    }
}
