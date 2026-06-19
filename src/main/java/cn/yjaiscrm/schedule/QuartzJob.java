package cn.iyque.schedule;

import cn.iyque.service.IYqueFriendCircleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 定时任务执行类
 * 提供每小时执行一次的定时任务示例
 */
@Slf4j
@Component
public class QuartzJob {

    @Autowired
    private IYqueFriendCircleService friendCircleService;

    /**
     * 朋友圈jobId换取momentId
     * cron表达式：0 0 * * * ? （每小时的第0分0秒执行）
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void jobIdToMomentId() {
        try {
            log.info("朋友圈jobId换取momentId任务开始，当前时间：{}", new Date());

            friendCircleService.jobIdToMomentId();

            log.info("朋友圈jobId换取momentId任务结束，当前时间：{}", new Date());

        }catch (Exception e){
            log.error("朋友圈jobId换取momentId任务执行失败:"+e.getMessage());
        }

    }

//    /**
//     * 每5秒执行一次的定时任务（用于测试）
//     * cron表达式：0 0/5 * * * ? （每5秒执行一次）
//     */
//    @Scheduled(cron = "*/5 * * * * *")
//    public void fiveMinuteJob() {
//        try {
//            log.info("朋友圈jobId换取momentId任务开始，当前时间：{}", new Date());
//
//            friendCircleService.jobIdToMomentId();
//
//            log.info("朋友圈jobId换取momentId任务结束，当前时间：{}", new Date());
//
//        }catch (Exception e){
//            log.error("朋友圈jobId换取momentId任务执行失败:"+e.getMessage());
//        }
//    }
}
