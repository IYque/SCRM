package cn.iyque.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.domain.H5MarketRecordCountDto;
import cn.iyque.mapper.IYqueH5MarketRecordMapper;
import cn.iyque.service.IYqueH5MarketRecordService;
import cn.iyque.utils.DateUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class IYqueH5MarketRecordServiceImpl implements IYqueH5MarketRecordService {


    @Autowired
    private IYqueH5MarketRecordMapper iYqueH5MarketRecordMapper;


    @Override
    public H5MarketRecordCountDto findH5MarketTab(Long h5MarketId) {
        H5MarketRecordCountDto countDto=new H5MarketRecordCountDto();



        List<H5MarketRecordCountDto> h5MarketRecordCount = iYqueH5MarketRecordMapper
                .findH5MarketRecordCount(h5MarketId, DateUtils.dateTimeNow(DateUtils.YYYY_MM_DD)
                        , DateUtils.dateTimeNow(DateUtils.YYYY_MM_DD));

        if(CollectionUtil.isNotEmpty(h5MarketRecordCount)){
            countDto=h5MarketRecordCount.stream().findFirst().get();
        }
        return countDto;
    }

    @Override
    public IPage<H5MarketRecordCountDto> findDailyStats(Date startDate, Date endDate, Long h5MarketId, Page<H5MarketRecordCountDto> page) {

        IPage<H5MarketRecordCountDto> h5MarketRecordCount = iYqueH5MarketRecordMapper
                .findH5MarketRecordCount(h5MarketId, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD,startDate)
                        , DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD,endDate),page);

        return h5MarketRecordCount;
    }

    @Override
    public List<H5MarketRecordCountDto> findH5MarketTrend(Date startDate, Date endDate, Long h5MarketId) {

        List<H5MarketRecordCountDto> h5MarketRecordCount = iYqueH5MarketRecordMapper
                .findH5MarketRecordCount(h5MarketId, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD,startDate)
                        , DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD,endDate));


        return h5MarketRecordCount;
    }
}
