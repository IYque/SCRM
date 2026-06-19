package cn.iyque.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "iyque_chat")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueChat {


    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;

    //群id
     private String chatId;


     //群名
     private String chatName;


     //群主id
     private String owner;


    //客群标签id，多个使用都好隔开
    private String tagIds;

    //标签名,多个使用逗号隔开
    @Transient
    private String tagNames;


     //群主名称
     @Transient
     private String ownerName;


     //群创建时间
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     private Date createTime;


}
