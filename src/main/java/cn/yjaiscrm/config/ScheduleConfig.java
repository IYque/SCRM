package cn.iyque.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务配置类
 * 开启Spring的定时任务支持
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {
}
