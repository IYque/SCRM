package cn.iyque.entity;


import cn.hutool.json.JSONUtil;
import cn.iyque.domain.fileType.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "iyque_chat_code")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "delFlag = 0")
public class IYqueChatCode {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;

    //群码名称
    private String chatCodeName;

    //群码url
    private String chatCodeUrl;

    //群码渠道标识
    private String chatCodeState;

    //群码config
    private String configId;


    //联系方式的备注信息，用于助记，超过30个字符将被截断
    private String remark;

    //当群满了后，是否自动新建群。0-否；1-是。 默认为1
    private Integer autoCreateRoom;


    //自动建群的群名前缀，当auto_create_room为1时有效。最长40个utf8字符
    private String roomBaseName;


    //自动建群的群起始序号，当auto_create_room为1时有效
    private Integer roomBaseId;


    //使用该配置的客户群ID列表，多个使用逗号隔开，最多支持5个
//    private String chatIds;
//
//    //使用该配置的客户群名称列表，多个使用逗号隔开，最多支持5个
//    private String chatNames;


    private String yqueChatListJson; // 用于存储序列化后的字符串


    @Transient
    private List<IYqueChat> yqueChatList;




    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    //是否删除标识
    private Integer delFlag;

    @PostLoad
    public void postLoad() {
        yqueChatList= JSONUtil.toList(yqueChatListJson,  IYqueChat.class);
    }

    @PrePersist
    @PreUpdate
    private void setDefaultDelFlag() {
        yqueChatListJson= JSONUtil.toJsonStr(yqueChatList);
        if (this.delFlag == null) {
            this.delFlag = 0;
        }
    }


}
