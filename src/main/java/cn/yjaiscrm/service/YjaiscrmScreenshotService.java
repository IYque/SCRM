package cn.iyque.service;

import cn.iyque.domain.IYqueScreenshot;
import cn.iyque.entity.BaseEntity;
import cn.iyque.entity.IYqueFileSecurity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IYqueScreenshotService {



    /**
     * 数据同步
     */
    void synchInfo(BaseEntity baseEntity);

    /**
     * 文件安全记录列表
     * @param screenshot
     * @param pageable
     * @return
     */
    Page<IYqueScreenshot> findAll(IYqueScreenshot screenshot, Pageable pageable);
}
