package cn.iyque.service;

import cn.iyque.domain.H5MarketRecordCountDto;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Date;
import java.util.List;

public interface IYqueH5MarketRecordService {



    /**
     * 统计头部tab
     * @param h5MarketId
     * @return
     */
    H5MarketRecordCountDto findH5MarketTab(Long h5MarketId);

    /**
     *  数据报表
     * @param startDate
     * @param endDate
     * @param h5MarketId
     * @param page
     * @return
     */
    IPage<H5MarketRecordCountDto> findDailyStats(Date startDate,
                                                 Date endDate, Long h5MarketId, Page<H5MarketRecordCountDto> page);


    /**
     * 获取折线图
     * @param startDate
     * @param endDate
     * @param h5MarketId
     * @return
     */
    List<H5MarketRecordCountDto> findH5MarketTrend(Date startDate, Date endDate, Long h5MarketId);
}
