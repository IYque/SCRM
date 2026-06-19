package cn.iyque.service;

import cn.iyque.entity.IYuqeOperateLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IYuqeOperateLogService {

    /**
     * 同步企微相关操作日志
     * @param operateType 1：成员操作日志 2:管理员操作日志
     */
    void synchOperateLog(Integer operateType);



    /**
     * 日志列表
     * @param iYuqeOperateLog
     * @param pageable
     * @return
     */
    Page<IYuqeOperateLog> findAll(IYuqeOperateLog iYuqeOperateLog, Pageable pageable);
}
