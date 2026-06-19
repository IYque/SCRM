package cn.iyque.entity;


import cn.iyque.entity.IYquePeriodMsgAnnex;
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
import java.util.List;


/**
 * 时段欢迎语时段
 */
@Entity(name = "iyque_annex_period")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueAnnexPeriod {

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


    //周期(1-7,代表周一到周日),多个使用逗号隔开
    private String workCycle;


    //开始时间
    private String beginTime;

   //结束时间
    private String endTime;

    //欢迎语
    private String weclomeMsg;

    //时段附件
    @Transient
    private List<IYquePeriodMsgAnnex> periodMsgAnnexList;

}
