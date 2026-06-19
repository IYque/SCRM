package cn.iyque.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Calendar;
import java.util.Date;


/**
 * ai访问token记录
 */
@Entity(name = "iyque_ai_token_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueAiTokenRecord {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;

    //模型
    private String model;

    //ai响应的id
    private String aiResId;

    //问题消耗的token
    private long promptTokens;

    //回复消耗的token
    private long completionTokens;

    //总共消耗的token
    private long totalTokens;

    //时间
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    public static Specification<IYqueAiTokenRecord> hasCreateTimeToday() {
        return new Specification<IYqueAiTokenRecord>() {
            @Override
            public Predicate toPredicate(Root<IYqueAiTokenRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Calendar startOfDay = Calendar.getInstance();
                startOfDay.set(Calendar.HOUR_OF_DAY, 0);
                startOfDay.set(Calendar.MINUTE, 0);
                startOfDay.set(Calendar.SECOND, 0);
                startOfDay.set(Calendar.MILLISECOND, 0);
                Date startDate = startOfDay.getTime();

                Calendar endOfDay = Calendar.getInstance();
                endOfDay.set(Calendar.HOUR_OF_DAY, 23);
                endOfDay.set(Calendar.MINUTE, 59);
                endOfDay.set(Calendar.SECOND, 59);
                endOfDay.set(Calendar.MILLISECOND, 999);
                Date endDate = endOfDay.getTime();
                return cb.between(root.get("createTime"), startDate, endDate);
            }
        };
    }


}
