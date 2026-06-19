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
 * AI客服客户内容总结
 */
@Entity(name = "iyque_summary_kf_msg")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueSummaryKfMsg {
    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;


    //客户名称
    private String nickname;

    //客户头像
    private String avatar;

    //客户id
    private String externalUserId;


    //客户unionid
    private String unionid;


    //聊天开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;


    //聊天结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;


    //当前数据入库创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    //AI总结的内容
    private String summaryContent;

}
