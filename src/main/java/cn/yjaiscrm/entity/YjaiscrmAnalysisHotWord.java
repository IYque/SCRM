package cn.iyque.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

@Entity(name = "iyque_analysis_hot_word")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueAnalysisHotWord extends BaseEntity{

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
//    @JsonIgnore
    private Long id;


    //热词的id
    private Long hotWordId;

    //热词
    private String hotWordName;


    //分类id
    private Long categoryId;

    //分类
    private String categoryName;


    //对应的消息id
    private String msgId;


    //消息发送人id
    private String fromId;

    //消息发送人名称
    private String fromName;

    //消息接收人id
    private String acceptId;

    //消息的接受类型:1:客户或成员;2:客群
    private Integer acceptType;

    //消息接收人名称
    private String acceptName;


    //消息内容
    private String content;


    @Transient
    private String hotWordIds;

    //消息的发送时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date msgTime;

    //分析时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date analysisTime;



}
