package cn.iyque.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "iyque_ai_analysis_msg_audit")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueAiAnalysisMsgAudit{

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;

    //是否预警，true有异常行为,false无异常行为
    private Boolean warning;


    //当msgAuditType为1的时候，该字段为员工名称;2的时候则为群名称
    private String employeeName;
    //当msgAuditType为1的时候，该字段为员工id;2的时候则为群id
    private String employeeId;

    //客户名称
    private String customerName;

    //客户id
    private String customerId;

    //1:客户规则；2:客群规则 3:意向客户分析 4:意向群友分析
    private Integer msgAuditType;



    //违规提示
    private String msg;

     // 分析时间段数据-开始时间
     @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;

    //分析时间段数据-结束时间
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;


    //分析时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;



    // 在实体被持久化之前自动设置 createTime
    @PrePersist
    protected void onCreate() {
        this.createTime = new Date();
    }


}
