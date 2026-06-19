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
import javax.persistence.Transient;
import java.util.Date;

@Entity(name = "iyque_operate_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYuqeOperateLog extends BaseEntity{
    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;

    //操作人id
    private String userId;

    //操作人名称
    private String userName;


    //操作类型type
    private Integer operateType;


    //实际操作类型
    private Integer operateTypeSub;

    //操作内容
    private String operateContent;

    //操作人ip
    private String operateIp;

    //操作时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
