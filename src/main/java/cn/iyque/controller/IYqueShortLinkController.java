package cn.iyque.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.*;
import cn.iyque.entity.IYqueAnnexPeriod;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.entity.IYqueShortLink;
import cn.iyque.service.IYqueAnnexPeriodService;
import cn.iyque.service.IYqueCustomerInfoService;
import cn.iyque.service.IYqueMsgAnnexService;
import cn.iyque.service.IYqueShortLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 获客链接
 */
@RestController
@RequestMapping("/iyQue/shortLink")
public class IYqueShortLinkController {

    @Autowired
    private IYqueShortLinkService iYqueShortLinkService;

    @Autowired
    private IYqueMsgAnnexService iYqueMsgAnnexService;


    @Autowired
    private IYqueAnnexPeriodService iYqueAnnexPeriodService;


    @Autowired
    private IYqueCustomerInfoService iYqueCustomerInfoService;


    /**
     * 新增
     * @param iYqueShortLink
     * @return
     */
    @PostMapping("/save")
    public ResponseResult save(@RequestBody IYqueShortLink iYqueShortLink) {
        try {
            iYqueShortLinkService.save(iYqueShortLink);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }


    /**
     * 更新
     * @param iYqueShortLink
     * @return
     */
    @PutMapping("/update")
    public ResponseResult update(@RequestBody IYqueShortLink iYqueShortLink){
        try {
            iYqueShortLinkService.update(iYqueShortLink);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }



    /**
     * 获取列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/findIYqueShortLink")
    public ResponseResult<IYqueShortLink> findIYqueShortLink(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size){
        Page<IYqueShortLink> iYqueShortLinks = iYqueShortLinkService.findAll(PageRequest.of(page, size, Sort.by("updateTime").descending()));
        return new ResponseResult(iYqueShortLinks.getContent(),iYqueShortLinks.getTotalElements());
    }


    /**
     * 获取所有活码id与名称
     * @return
     */
    @GetMapping("/findIYqueUserCodeKvs")
    public ResponseResult<List<IYqueKvalStrVo>>  findIYqueUserCodeKvs(){

        List<IYqueKvalStrVo> iYqueUserCodeKvs = iYqueShortLinkService.findIYqueShorkLinkKvs();


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

        iYqueShortLinkService.batchDelete(ids);

        return new ResponseResult();
    }


    /**
     * 获取附件
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
     * 获取时段欢迎语附件
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
     * 统计tab
     * @return
     */
    @GetMapping("/countTotalTab")
    public ResponseResult<IYqueUserCodeCountVo> countTotalTab(IYQueCountQuery queCountQuery){
        IYqueUserCodeCountVo iYqueUserCodeCountVo = iYqueCustomerInfoService.countTotalTab(queCountQuery,false);

        return new ResponseResult<>(iYqueUserCodeCountVo);
    }


    /**
     * 统计趋势图
     * @param queCountQuery
     * @return
     */
    @GetMapping("/countTrend")
    public ResponseResult<IYQueTrendCount> countTrend(IYQueCountQuery queCountQuery){

        IYQueTrendCount trendCount = iYqueCustomerInfoService.countTrend(queCountQuery,false);

        return new ResponseResult<>(trendCount);
    }




}
