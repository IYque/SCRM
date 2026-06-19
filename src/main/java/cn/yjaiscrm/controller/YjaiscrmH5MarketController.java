package cn.iyque.controller;


import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.H5MarketRecordCountDto;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueH5Market;
import cn.iyque.entity.IYqueH5MarketRecord;
import cn.iyque.service.IYqueH5MarketRecordService;
import cn.iyque.service.IYqueH5MarketService;
import cn.iyque.utils.TableSupport;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5营销
 */
@RestController
@RequestMapping("/h5Market")
public class IYqueH5MarketController {

    @Autowired
    private IYqueH5MarketService yqueH5MarketService;

    @Autowired
    private IYqueH5MarketRecordService yqueH5MarketRecordService;

    /**
     * 新增或更新
     * @param iYqueH5Market
     * @return
     */
    @PostMapping("/addOrUpdate")
    public ResponseResult addOrUpdate(@RequestBody IYqueH5Market iYqueH5Market) {
        try {
            yqueH5MarketService.addOrUpdate(iYqueH5Market);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }

    /**
     * 获取列表
     * @return
     */
    @GetMapping("/findH5Markets")
    public ResponseResult<IYqueH5Market> findH5Markets(
            IYqueH5Market iYqueH5Market){
        Page<IYqueH5Market> iYqueH5Markets = yqueH5MarketService.findAll(
                iYqueH5Market,
                PageRequest.of(TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(),
                        Sort.by("updateTime").descending()));
        return new ResponseResult(iYqueH5Markets.getContent(),iYqueH5Markets.getTotalElements());
    }


    /**
     * 移动端获取H5营销详情
     * @param marketId
     * @return
     */
    @GetMapping("/findWeH5MarketById/{marketId}")
    public ResponseResult<IYqueH5Market>  findWeH5MarketById(@PathVariable Long marketId){
        IYqueH5Market weH5Market
                = yqueH5MarketService.findWeH5MarketById(marketId);

        return new ResponseResult(weH5Market);

    }

    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        yqueH5MarketService.batchDelete(ids);

        return new ResponseResult();
    }



    /**
     * 数据统计-头部tab
     * @param h5MarketRecord
     * @return
     */
    @GetMapping("/findH5MarketTab")
    public ResponseResult  findH5MarketTab(IYqueH5MarketRecord h5MarketRecord){

        H5MarketRecordCountDto h5MarketTab = yqueH5MarketRecordService.findH5MarketTab(h5MarketRecord.getH5MarketId());

        return new ResponseResult(h5MarketTab);
    }


    /**
     * 数据统计-折线图
     * @param h5MarketRecord
     * @return
     */
    @GetMapping("/findH5MarketTrend")
    public ResponseResult findH5MarketTrend(IYqueH5MarketRecord h5MarketRecord){
        List<H5MarketRecordCountDto> h5MarketTable = yqueH5MarketRecordService.findH5MarketTrend(h5MarketRecord.getBeginTime(), h5MarketRecord.getEndTime(), h5MarketRecord.getH5MarketId());

        return new ResponseResult(h5MarketTable);
    }

    /**
     * 数据统计-表格
     * @param h5MarketRecord
     * @return
     */
    @GetMapping("/findH5MarketTable")
    public ResponseResult findH5MarketTable(IYqueH5MarketRecord h5MarketRecord){

        IPage<H5MarketRecordCountDto> iPage = yqueH5MarketRecordService.findDailyStats(h5MarketRecord.getBeginTime(), h5MarketRecord.getEndTime(), h5MarketRecord.getH5MarketId(), new com.baomidou.mybatisplus.extension.plugins.pagination.Page<H5MarketRecordCountDto>(
                TableSupport.buildPageRequest().getPageNum(), TableSupport.buildPageRequest().getPageSize()

        ));

        return new ResponseResult(iPage.getRecords(),iPage.getTotal());
    }



}
