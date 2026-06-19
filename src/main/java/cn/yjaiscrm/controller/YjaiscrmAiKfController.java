package cn.iyque.controller;


import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.IYqueKfDto;
import cn.iyque.domain.IYqueSummaryKfMsgDto;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueKf;
import cn.iyque.entity.IYqueKfMsgSub;
import cn.iyque.entity.IYqueSummaryKfMsg;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueKfMsgService;
import cn.iyque.service.IYqueKfService;
import cn.iyque.utils.TableSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;


/**
 * ai客服相关(基础客服)
 */
@RequestMapping("/kf")
@Slf4j
@RestController
public class IYqueAiKfController {


    @Autowired
    private IYqueKfService yqueKfService;


    @Autowired
    private IYqueKfMsgService yqueKfMsgService;




    /**
     * 获取客服列表
     * @param iYqueKf
     * @return
     */
    @GetMapping("/findAll")
    public ResponseResult<IYqueKf> findAll(IYqueKf iYqueKf){
        iYqueKf.setKfType(1);
        Page<IYqueKf> iYqueKfs = yqueKfService.findAll(iYqueKf,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("updateTime").descending()));
        return new ResponseResult(iYqueKfs.getContent(),iYqueKfs.getTotalElements());
    }




    /**
     * 新增或编辑客服
     */
    @PostMapping("/saveOrUpdateKf")
    public ResponseResult saveOrUpdateKf(@RequestBody IYqueKf iYqueKf) {

        try {
            iYqueKf.setKfType(1);
            yqueKfService.saveOrUpdateKf(iYqueKf);
        }catch (IYqueException e){
            return new ResponseResult(HttpStatus.ERROR,e.getMsg(),null);
        }

        return new ResponseResult();
    }




    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        yqueKfService.batchDelete(Arrays.asList(ids));

        return new ResponseResult();
    }



    /**
     * 获取客服会话列表
     * @param iYqueKfMsgSub
     * @return
     */
    @GetMapping("/findKfMsgAll")
    public ResponseResult<IYqueKfMsgSub> findKfMsgAll(IYqueKfMsgSub iYqueKfMsgSub){

        Page<IYqueKfMsgSub> kfMsgSubs = yqueKfMsgService.findAll(iYqueKfMsgSub,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("sendTime").descending()));
        return new ResponseResult(kfMsgSubs.getContent(),kfMsgSubs.getTotalElements());
    }


    /**
     * 获取圈选客户列表
     * @return
     */
    @GetMapping("/findGroupAll")
    public ResponseResult findGroupAll(){
        List<IYqueSummaryKfMsgDto> groupAll = yqueKfMsgService.findGroupAll();
        return new ResponseResult(groupAll);
    }

    /**
     * AI服务内容总结
     * @param iYqueKfDto
     * @return
     */
    @PostMapping(path = "/summaryKfmsgByAi")
    public ResponseResult summaryKfmsgByAi(@RequestBody IYqueKfDto iYqueKfDto){

        yqueKfMsgService.summaryKfmsgByAi(iYqueKfDto.getExternalUserIds());

        return new ResponseResult("当前记录AI正在总结中,请稍后查看");
    }


    /**
     * AI会话总结列表
     * @param summaryKfMsg
     * @return
     */
    @GetMapping("/findSummaryKfMsgs")
    public ResponseResult<IYqueSummaryKfMsg> findSummaryKfMsgs(IYqueSummaryKfMsg summaryKfMsg){
        Page<IYqueSummaryKfMsg> iYqueKfs = yqueKfMsgService.findSummaryKfMsgs(summaryKfMsg,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("createTime").descending()));
        return new ResponseResult(iYqueKfs.getContent(),iYqueKfs.getTotalElements());
    }
}
