package cn.iyque.mapper;

import cn.iyque.domain.H5MarketRecordCountDto;
import cn.iyque.entity.IYqueH5MarketRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface IYqueH5MarketRecordMapper extends BaseMapper<IYqueH5MarketRecord> {


    /**
     * h5营销相关统计(分页)
     * @param beginTime
     * @param endTime
     * @return
     */
    IPage<H5MarketRecordCountDto> findH5MarketRecordCount(@Param("h5MarketId") Long h5MarketId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, Page<H5MarketRecordCountDto> page);





    /**
     * h5营销相关统计(不分页)
     * @param beginTime
     * @param endTime
     * @return
     */
    List<H5MarketRecordCountDto> findH5MarketRecordCount(@Param("h5MarketId") Long h5MarketId, @Param("beginTime") String beginTime, @Param("endTime") String endTime);



}
