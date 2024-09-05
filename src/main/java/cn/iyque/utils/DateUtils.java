package cn.iyque.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DateUtils {
    public static List<String> getTimePeriods(Date startDate, Date endDate) {
        if (startDate == null && endDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date today = sdf.parse(sdf.format(new Date()));
                Date sevenDaysAgo = new Date(today.getTime() - 6 * 24 * 60 * 60 * 1000); // Adjusted to include today
                startDate = sevenDaysAgo;
                endDate = today;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        List<String> timePeriods = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long oneDay = 24 * 60 * 60 * 1000; // milliseconds in one day

        for (long start = startDate.getTime(); start <= endDate.getTime(); start += oneDay) {
            long end = Math.min(start + oneDay, endDate.getTime());
            String period = sdf.format(new Date(start));
            timePeriods.add(period);
        }

        return timePeriods;
    }


    /**
     * 当前周几是否在该字符串类
     * @param workCycle
     * @return
     */
    public static boolean isWorkCycle(String workCycle){

        LocalDate currentDate = LocalDate.now();
        DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
        int dayOfWeekValue = dayOfWeek.getValue(); // 注意：1代表周一，7代表周日

        int[] weekDays = Arrays.stream(workCycle.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();

        return Arrays.stream(weekDays).anyMatch(day -> day == dayOfWeekValue);
    }


    /**
     * 判断指定时间是否在范围类 HH:mm
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean isCurrentTimeInRange(String beginTime, String endTime) {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime begin = LocalTime.parse(beginTime, formatter);
        LocalTime end = LocalTime.parse(endTime, formatter);

        // 判断当前时间是否在范围内，考虑跨越午夜的情况
        if (end.isBefore(begin)) { // 如果结束时间早于开始时间，说明跨越了午夜
            return (currentTime.isAfter(begin) || currentTime.compareTo(begin) >= 0) ||
                    (currentTime.isBefore(end) || currentTime.compareTo(end) <= 0);
        } else { // 正常情况，没有跨越午夜
            return (currentTime.isAfter(begin) || currentTime.compareTo(begin) >= 0) &&
                    (currentTime.isBefore(end) || currentTime.compareTo(end) <= 0);
        }
    }
}
