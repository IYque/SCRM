package cn.iyque.entity;


import cn.iyque.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 客服
 */
@Entity(name = "iyque_kf")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueKf {


    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;


    //客服id
    private String openKfid;


    //客服类型(1:基础客服;2:排班客服)
    private Integer kfType=1;


    //客服名称
    private String kfName;


    //客服链接
    private String kfUrl;


    //客服链接二维码
    private String kfQrUrl;


    //头像
    private String kfPicUrl;




    //是否使用知识库(默认为true,便于后续拓展)
    private boolean useKnowLedge=true;



    //知识库id,多个使用逗号隔开(当前暂时只支持单个)
    private String kid;

    //知识库名称,多个使用逗号隔开(当前暂时只支持单个)
    private String kname;

    //如果ai知识库匹配不到,转接方式:1:文字;2:直接转人工客服;3:发送外部联系人二维码;4:ai大模型直接回复
    private  Integer switchType;

    //当转接方式为1的时候,转接文字
    private String switchText;

    //当转接方式为2的时候转接人id(目前暂时只支持单个)；当客服类型为2的时候当前字段支持多个id逗号隔开
    private String switchUserIds;


    //当转接方式为2的时候转接人名称(目前暂时只支持单个)；当客服类型为2的时候当前字段支持多个逗号隔开
    private String switchUserNames;


    //非工作时间接待语
    private String oorWelcome;

   //工作周期(1-7分别代表周一到周日，多个使用逗号隔开)
    private String workCycle;

    //开始时间
    private String beginTime;

    //结束时间
    private String endTime;

    //欢迎语
    private String welcomeMsg;






    //当转接方式为3的时候,外部联系人二维码
    private String swichQrUrl;


    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private  Date updateTime;




    /**
     * 检查当前时间是否在客服工作时间内
     * @param iYqueKf 客服欢迎语配置
     * @return true-在工作时间内，false-不在工作时间
     */
    public static boolean isInWorkingTime(IYqueKf iYqueKf) {
        // 1. 检查当前星期是否在工作周期内
        if (!isCurrentDayInWorkCycle(iYqueKf.getWorkCycle())) {
            return false;
        }

        // 2. 检查当前时间是否在时间范围内
        return  new Date().after(DateUtils.hsToDate(iYqueKf.getBeginTime()))&&new Date().before(DateUtils.hsToDate(iYqueKf.getEndTime()));
    }


    /**
     * 检查当前星期是否在工作周期内
     */
    private static boolean isCurrentDayInWorkCycle(String workCycle) {
        if (workCycle == null || workCycle.isEmpty()) {
            return false;
        }

        // 获取当前星期几 (1-7 对应 周一至周日)
        int currentDayOfWeek = getCurrentDayOfWeek();

        // 解析工作周期字符串
        List<String> days = Arrays.asList(workCycle.split(","));

        return days.contains(String.valueOf(currentDayOfWeek));
    }

    /**
     * 获取当前星期几 (1-7 对应 周一至周日)
     */
    private static int getCurrentDayOfWeek() {
        // Calendar中周日是1，周一是2，...，周六是7
        int day = new java.util.Date().getDay();
        // 转换为1(周一)到7(周日)
        return day == 0 ? 7 : day;
    }

    /**
     * 检查当前时间是否在时间范围内
     */
    private static boolean isCurrentTimeInRange(String beginTimeStr, String endTimeStr) {
        try {
            Date now = new Date();
            Date beginTime = parseTime(beginTimeStr);
            Date endTime = parseTime(endTimeStr);

            // 处理跨天的情况（如 22:00 - 02:00）
            if (endTime.before(beginTime)) {
                return !now.before(beginTime) || !now.after(endTime);
            } else {
                return !now.before(beginTime) && !now.after(endTime);
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析时间字符串为Date对象
     */
    private static Date parseTime(String timeStr) throws ParseException {
        Date date = TIME_FORMATTER.parse(timeStr);
        // 因为SimpleDateFormat解析的日期是1970年，我们需要比较时间部分
        return date;
    }


    public static String getRandomSwitchUserId(IYqueKf iYqueKf) {
        if (StringUtils.isEmpty(iYqueKf.getSwitchUserIds())) {
            return null;
        }
        // 使用Stream API处理
        List<String> userIdList = Arrays.stream(iYqueKf.getSwitchUserIds().split(","))
                .collect(Collectors.toList());
        // 生成随机索引
        Random random = new Random();
        int randomIndex = random.nextInt(userIdList.size());
        // 返回随机元素
        return userIdList.get(randomIndex);
    }





}
