package cn.iyque.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DateUtils {

    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
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

    public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {
        long numOfDays = startDate.until(endDate).getDays() + 1; // 包含结束日期
        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(numOfDays)
                .collect(Collectors.toList());
    }

    public static final String dateTimeNow(final String format)
    {
        return parseDateToStr(format, new Date());
    }
    public static final String parseDateToStr(final String format, final Date date)
    {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 获取当天凌晨时间
     * @return
     */
    public static Date earlyMorning(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * 将 Date 对象的时间部分设置为 00:00:00
     * @param date
     * @return
     */
    public static Date setTimeToStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    /**
     *  将 Date 对象的时间部分设置为 23:59:59
     * @param date
     * @return
     */
    public static Date setTimeToEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }


    /**
     * 获取指定时间指定天数之前的日期，如：当前以及当前七天前的日期
     * @param date
     * @param days
     * @return
     */
    public static Date getDaysAgo(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        return calendar.getTime();
    }

    /**
     * 获取指定时间指定天数之前的日期（返回Unix时间戳，秒）
     * @param date 基准日期
     * @param days 天数（如7表示7天前）
     * @return 时间戳（秒）
     */
    public static long getDaysAgoInSeconds(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        return calendar.getTime().getTime() / 1000; // 转为秒
    }


    /**
     * 传入如00:00  HH:mm类型的时间，拼接当天的时间返回
     * @param timeStr
     * @return
     */
    public static Date hsToDate(String timeStr){

        // 当前日期
        LocalDate today = LocalDate.now();

        // 解析为LocalTime
        LocalTime time = LocalTime.parse(timeStr);
        // 拼接为LocalDateTime
        LocalDateTime dateTime = LocalDateTime.of(today, time);
        // 转换为Date类型

        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());


    }






}
