package cn.iyque.entity;


import cn.hutool.json.JSONUtil;
import cn.iyque.domain.fileType.Image;
import cn.iyque.domain.fileType.Link;
import cn.iyque.domain.fileType.Text;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * 话术子表
 */
@Entity(name = "iyque_script_sub")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "delFlag = 0")
public class IYQueScriptSub {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;


    //话术表主键
    private Long scriptId;


    //附件类型
    private String msgtype;

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


    @PrePersist
    @PreUpdate
    private void setDefaultDelFlag() {
        if (this.delFlag == null) {
            this.delFlag = 0;
        }
    }

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

    public void prePersist(IYQueScriptSub scriptSub) {
        if (this.delFlag == null) {
            this.delFlag = 0;
        }
        if(IYqueMsgAnnex.MsgType.MSG_TEXT.equals(msgtype)){
            if(scriptSub.getText() != null){
                annexContent =  JSONUtil.toJsonStr(scriptSub.getText());
            }


        }else if (IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE.equals(msgtype)) {
            if(scriptSub.getImage() != null){
                annexContent = JSONUtil.toJsonStr(scriptSub.getImage());
            }

        } else if (IYqueMsgAnnex.MsgType.MSG_TYPE_LINK.equals(msgtype)) {
            if(null != scriptSub.getLink()){
                annexContent = JSONUtil.toJsonStr(scriptSub.getLink());
            }

        }
    }
}
