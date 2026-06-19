package cn.iyque.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 员工活码统计
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IYqueUserCodeCountVo {

    //新增客户数
    private long addCustomerNumber;

    //流失客户数
    private long lostCustomerNumber;
    //员工删除客户数
    private long delCustomerNumber;
    //净增客户数
    private long netGrowthCustomerNumber;

    //今日新增客户数
    private long tdAddCustomerNumber;

    //今日流失客户数
    private long tdLostCustomerNumber;

    //今日员工删除客户数
    private long tddelCustomerNumber;

    //今日净增客户数
    private long tdNetGrowthCustomerNumber;



    public static List<Integer> getDateCountList(List<IYQueCustomerInfo> customerInfoList, LocalDate startDate, LocalDate endDate, Integer status) {


        if(null == customerInfoList){
            customerInfoList=new ArrayList<>();
        }
        // 如果指定时间段为空，则使用当前日期以及前七天的数据
        if (startDate == null || endDate == null) {
            LocalDate currentDate = LocalDate.now();
            startDate = currentDate.minusDays(6);
            endDate = currentDate;
        }

        // 如果status不为空，则按照addTime的日期部分进行分组并计算每个日期的数量，同时过滤出status符合条件的记录
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Long> dateCountMap = status != null ? customerInfoList.stream()
                .filter(info -> status.equals(info.getStatus()))
                .collect(Collectors.groupingBy(info -> info.getAddTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter), Collectors.counting()))
                : customerInfoList.stream()
                .collect(Collectors.groupingBy(info -> info.getAddTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter), Collectors.counting()));

        List<DateCount> dateCountList = new ArrayList<>();

        // 遍历日期范围
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateString = date.format(formatter);
            if (dateCountMap.containsKey(dateString)) {
                // 如果存在该日期的数据，添加到结果列表
                dateCountList.add(new DateCount(java.sql.Date.valueOf(LocalDate.parse(dateString)), dateCountMap.get(dateString).intValue()));
            } else {
                // 如果不存在该日期的数据，创建一个新的DateCount对象并添加到结果列表
                dateCountList.add(new DateCount(java.sql.Date.valueOf(LocalDate.parse(dateString)), 0));
            }
        }

        return dateCountList.stream()
                .sorted(Comparator.comparing(DateCount::getDate)) // 按日期排序
                .map(DateCount::getCount)
                .collect(Collectors.toList());
    }

}
