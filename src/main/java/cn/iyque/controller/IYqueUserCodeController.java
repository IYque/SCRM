package cn.iyque.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.*;
import cn.iyque.entity.IYqueAnnexPeriod;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.entity.IYqueUserCode;
import cn.iyque.service.IYqueAnnexPeriodService;
import cn.iyque.service.IYqueCustomerInfoService;
import cn.iyque.service.IYqueMsgAnnexService;
import cn.iyque.service.IYqueUserCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 客户引流码相关
 */
@RestController
@RequestMapping("/iyQue")
public class IYqueUserCodeController {

    @Autowired
    private IYqueUserCodeService iYqueUserCodeService;


    @Autowired
    private IYqueMsgAnnexService iYqueMsgAnnexService;


    @Autowired
    private IYqueCustomerInfoService iYqueCustomerInfoService;


    @Autowired
    private IYqueAnnexPeriodService iYqueAnnexPeriodService;


    /**
     * 新增引流码
     * @param iYqueUserCode
     * @return
     */
    @PostMapping("/save")
    public ResponseResult save(@RequestBody IYqueUserCode iYqueUserCode) {
        try {
            iYqueUserCodeService.save(iYqueUserCode);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }


    /**
     * 更新引流码
     * @param iYqueUserCode
     * @return
     */
    @PutMapping("/update")
    public ResponseResult update(@RequestBody IYqueUserCode iYqueUserCode){
        try {
            iYqueUserCodeService.update(iYqueUserCode);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }



    /**
     * 获取引流码列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/findIYqueUserCode")
    public ResponseResult<IYqueUserCode> findIYqueUserCode(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size){
        Page<IYqueUserCode> iYqueUserCodes = iYqueUserCodeService.findAll(PageRequest.of(page, size, Sort.by("updateTime").descending()));
        return new ResponseResult(iYqueUserCodes.getContent(),iYqueUserCodes.getTotalElements());
    }


    /**
     * 获取所有活码id与名称
     * @return
     */
    @GetMapping("/findIYqueUserCodeKvs")
    public ResponseResult<List<IYqueKvalStrVo>>  findIYqueUserCodeKvs(){

        List<IYqueKvalStrVo> iYqueUserCodeKvs = iYqueUserCodeService.findIYqueUserCodeKvs();


        return new ResponseResult<>(iYqueUserCodeKvs);


    }






    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        iYqueUserCodeService.batchDelete(ids);

        return new ResponseResult();
    }


    /**
     * 获取活码附件
     * @param id
     * @return
     */
    @GetMapping("/findIYqueMsgAnnexByMsgId/{id}")
    public ResponseResult<IYqueMsgAnnex> findIYqueMsgAnnexByMsgId(@PathVariable Long id){

        List<IYqueMsgAnnex> iYqueMsgAnnexes = iYqueMsgAnnexService.findIYqueMsgAnnexByMsgId(id);


        return  new ResponseResult(
                CollectionUtil.isNotEmpty(iYqueMsgAnnexes)?iYqueMsgAnnexes:new ArrayList<>()
        );
    }


    /**
     * 获取时段欢迎语活码附件
     * @param id
     * @return
     */
    @GetMapping("/findIYqueMsgPeriodAnnexByMsgId/{id}")
    public ResponseResult<IYqueAnnexPeriod> findIYqueMsgPeriodAnnexByMsgId(@PathVariable Long id){

        List<IYqueAnnexPeriod> iYqueAnnexPeriods = iYqueAnnexPeriodService.findIYqueAnnexPeriodByMsgId(id);

        return  new ResponseResult(
                iYqueAnnexPeriods
        );
    }




    /**
     * 活码下发
     * @param id
     * @return
     */
    @GetMapping("/distributeUserCode/{id}")
    public ResponseResult distributeUserCode(@PathVariable("id") Long id){

        try {
            iYqueUserCodeService.distributeUserCode(id);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }


        return new ResponseResult();
    }


    /**
     * 统计tab
     * @return
     */
    @GetMapping("/countTotalTab")
    public ResponseResult<IYqueUserCodeCountVo> countTotalTab(IYQueCountQuery queCountQuery){
        IYqueUserCodeCountVo iYqueUserCodeCountVo = iYqueCustomerInfoService.countTotalTab(queCountQuery,true);

        return new ResponseResult<>(iYqueUserCodeCountVo);
    }


    /**
     * 统计趋势图
     * @param queCountQuery
     * @return
     */
    @GetMapping("/countTrend")
    public ResponseResult<IYQueTrendCount> countTrend(IYQueCountQuery queCountQuery){

        IYQueTrendCount trendCount = iYqueCustomerInfoService.countTrend(queCountQuery,true);

        return new ResponseResult<>(trendCount);
    }





}
