package cn.iyque.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Entity(name = "iyque_msg_rule")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueMsgRule {
    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;


    //规则内容
    private String ruleContent;


    //是否启用，true启用,false停用
    private Boolean ruleStatus;

    //默认规则,true是,false不是
    private Boolean defaultRule;


    //规则类型1:客户规则；2:客群规则 3:意向客户分析 4:意向群友分析
    private Integer ruleType=1;


    //当前数据入库创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;



    /**
     * 将IYqueMsgRule列表格式化为指定格式的字符串。
     *
     * @param rules IYqueMsgRule对象列表
     * @return 格式化后的字符串，每条规则前有序号，规则之间用换行符分隔
     */
    public static String formatRules(List<IYqueMsgRule> rules) {
        if (rules == null || rules.isEmpty()) {
            return "";
        }

        return IntStream.rangeClosed(1, rules.size())
                .mapToObj(i -> i + ". " + rules.get(i - 1).getRuleContent())
                .collect(Collectors.joining(""));
    }


}
