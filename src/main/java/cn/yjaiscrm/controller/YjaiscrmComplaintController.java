package cn.iyque.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.domain.*;
import cn.iyque.entity.IYQueComplain;
import cn.iyque.entity.IYQueComplaintTip;
import cn.iyque.entity.IYqueUserCode;
import cn.iyque.enums.ComplaintContent;
import cn.iyque.service.IYQueComplaintService;
import cn.iyque.utils.JwtUtils;
import cn.iyque.utils.SecurityUtils;
import cn.iyque.utils.TableSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


/**
 * 投诉管理
 */
@RestController
@RequestMapping("/iYqueComplaint")
public class IYQueComplaintController {


    @Autowired
    IYQueComplaintService queComplaintService;


    /**
     * 获取投诉类型列表
     * @return
     */
    @GetMapping("/findComplaint")
    public ResponseResult findComplaint(){

        List<ComplaintContent.TypeWithSubTypes> allTypeWithSubTypes = ComplaintContent.getAllComplaintContent();

        return new ResponseResult(allTypeWithSubTypes);
    }


    /**
     * 创建投诉内容
     * @param iyQueComplain
     * @return
     */
    @PostMapping("/addComplaint")
    public ResponseResult addComplaint(@RequestBody IYQueComplain iyQueComplain){

        queComplaintService.addComplaint(iyQueComplain);

        return new ResponseResult<>();
    }




    /**
     * 获取通知人
     * @return
     */
    @GetMapping("/findIYQueComplaintTips")
    public ResponseResult  findIYQueComplaintTips(){

        List<IYQueComplaintTip> iyQueComplaintTips = queComplaintService.findIYQueComplaintTips();

        return new ResponseResult(iyQueComplaintTips);

    }


    /**
     * 设置通知人
     * @param iyQueComplaintTip
     * @return
     */
    @PostMapping("/setIYQueComplaintTip")
    public ResponseResult setIYQueComplaintTip(@RequestBody List<IYQueComplaintTip> iyQueComplaintTip){
        queComplaintService.setIYQueComplaintTip(iyQueComplaintTip);

        return new ResponseResult();
    }


    /**
     * 投诉下发
     * @param id
     * @return
     */
    @GetMapping("/distributeHandle/{id}")
    public ResponseResult distributeHandle(@PathVariable Long id){
        queComplaintService.distributeHandle(id);
        return new ResponseResult();
    }


    /**
     * 获取投诉列表
     * @param iyQueComplain
     * @return
     */
    @GetMapping("/findComplaintByPage")
    public ResponseResult<IYQueComplain> findComplaintByPcPage(IYQueComplain iyQueComplain){

        Page<IYQueComplain> iyQueComplainPage = queComplaintService.findAll(iyQueComplain, PageRequest.of(
                TableSupport.buildPageRequest().getPageNum(),
                TableSupport.buildPageRequest().getPageSize(),
                Sort.by("complainTime").descending()
        ));
        List<IYQueComplain> iyQueComplains = iyQueComplainPage.getContent();
        if(CollectionUtil.isNotEmpty(iyQueComplains)){
            iyQueComplains.stream().forEach(k->{
                k.setComplainTypeContent(
                        ComplaintContent.getValueByCode(k.getComplainType())
                );
            });
        }



        return new ResponseResult(iyQueComplainPage.getContent(),iyQueComplainPage.getTotalElements());
    }


    /**
     * 获取投诉列表
     * @param id
     * @return
     */
    @GetMapping("/findIYQueComplainById/{id}")
    public ResponseResult<IYQueComplain> findIYQueComplainById(@PathVariable Long id){

        IYQueComplain iYQueComplain = queComplaintService.findIYQueComplainById(id);




        return new ResponseResult(iYQueComplain);
    }




    /**
     * 处理投诉意见
     * @param iyQueComplain
     * @return
     */
    @PostMapping("/handleComplaint")
    public ResponseResult handleComplaint(@RequestBody IYQueComplain iyQueComplain){

        iyQueComplain.setHandleWeUserId(
                SecurityUtils.getCurrentUserName()
        );

        iyQueComplain.setHandleTime(
                new Date()
        );

        iyQueComplain.setHandleState(
                2
        );

        queComplaintService.handleComplaint(iyQueComplain);

        return new ResponseResult<>();
    }

    /**
     * 统计tab
     * @return
     */
    @GetMapping("/countTotalTab")
    public ResponseResult<IYqueComplaintCountVo> countTotalTab(IYQueCountQuery queCountQuery){
        IYqueComplaintCountVo iYqueUserCodeCountVo = queComplaintService.countTotalTab(queCountQuery);

        return new ResponseResult<>(iYqueUserCodeCountVo);
    }


    /**
     * 统计趋势图
     * @param queCountQuery
     * @return
     */
    @GetMapping("/countTrend")
    public ResponseResult<IYQueTrendCount> countTrend(IYQueCountQuery queCountQuery){

        IYQueTrendCount trendCount = queComplaintService.countTrend(queCountQuery);

        return new ResponseResult<>(trendCount);
    }





}
