package cn.iyque.service;

import cn.iyque.entity.BaseEntity;
import cn.iyque.entity.IYqueFileSecurity;
import cn.iyque.entity.IYuqeOperateLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IYqueFileSecurityService {




    /**
     * 数据同步
     */
    void synchInfo(BaseEntity baseEntity);




    /**
     * 文件安全记录列表
     * @param iYqueFileSecurity
     * @param pageable
     * @return
     */
    Page<IYqueFileSecurity> findAll(IYqueFileSecurity iYqueFileSecurity, Pageable pageable);
}
