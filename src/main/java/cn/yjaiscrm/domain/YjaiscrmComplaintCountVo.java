package cn.iyque.domain;


import cn.iyque.entity.IYQueComplain;
import cn.iyque.enums.ComplaintAnnexType;
import cn.iyque.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 投诉统计
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IYqueComplaintCountVo {

    //投诉总数
    private int complaintTotalNumber;

    //未投诉总数
    private int noComplaintTotalNumber;

    //已处理投诉总数
    private int yesComplaintTotalNumber;

    //今日投诉总数
    private int tdComplaintTotalNumber;

    //今日未投诉总数
    private int tdNoComplaintTotalNumber;

    //今日已处理投诉总数
    private int tdYesComplaintTotalNumber;


    public static List<Integer> getDateCountList(List<IYQueComplain> iyQueComplains, LocalDate startDate, LocalDate endDate,Integer handleState){

        if(null == iyQueComplains){
            iyQueComplains=new ArrayList<>();
        }
        // 如果指定时间段为空，则使用当前日期以及前七天的数据
        if (startDate == null || endDate == null) {
            LocalDate currentDate = LocalDate.now();
            startDate = currentDate.minusDays(6);
            endDate = currentDate;
        }

        // 如果status不为空，则按照addTime的日期部分进行分组并计算每个日期的数量，同时过滤出status符合条件的记录
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Long> dateCountMap =  new HashMap<>();

        if(handleState == null){
            dateCountMap=iyQueComplains.stream()
                    .collect(Collectors.groupingBy(info -> info.getComplainTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter), Collectors.counting()));

        }else if(new Integer(1).equals(handleState)){//未处理投诉数

            dateCountMap=iyQueComplains.stream()
                    .filter(info -> handleState.equals(info.getHandleState()))
                    .collect(Collectors.groupingBy(info -> info.getComplainTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter), Collectors.counting()));

        }else if(new Integer(2).equals(handleState)){ //已处理投诉数
            dateCountMap=iyQueComplains.stream()
                    .filter(info -> handleState.equals(info.getHandleState()))
                    .collect(Collectors.groupingBy(info -> info.getHandleTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter), Collectors.counting()));


        }



        List<DateCount> dateCountList = new ArrayList<>();

        // 遍历日期范围
        for (LocalDate date:DateUtils.getDatesBetween(startDate, endDate)) {
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
