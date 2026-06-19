package cn.iyque.controller;


import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueAgent;
import cn.iyque.entity.IYqueAgentSub;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueAgentService;
import cn.iyque.utils.TableSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

/**
 * 通知公告
 */
@RequestMapping("/agent")
@Slf4j
@RestController
public class IYqueAgentController {

    @Autowired
    IYqueAgentService iYqueAgentService;

    @Autowired
    IYqueParamConfig yqueParamConfig;


    /**
     * 新增或更新应用
     * @param iYqueAgent
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public ResponseResult saveOrUpdate(@RequestBody IYqueAgent iYqueAgent) {
        try {
            if(iYqueAgent.getCreateTime()==null){
                iYqueAgent.setCreateTime(new Date());
            }
            iYqueAgent.setUpdateBy(yqueParamConfig.getUserName());
            iYqueAgent.setUpdateTime(new Date());
            iYqueAgentService.addOrUpdate(iYqueAgent);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }


    /**
     * 同步应用信息
     * @param id
     * @return
     */
    @GetMapping("/synchAgent/{id}")
    public ResponseResult synchAgent(@PathVariable Long id){
        try {
            iYqueAgentService.synchAgent(id);
        }catch (IYqueException e){
            return new ResponseResult(HttpStatus.ERROR,e.getMsg(),null);
        }

        return new ResponseResult();

    }


    /**
     * 应用列表
     * @return
     */
    @GetMapping("/findAll")
    public ResponseResult<IYqueAgent> findAll(){
        Page<IYqueAgent> iYqueAgents = iYqueAgentService.findAll(
                PageRequest.of(TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("updateTime").descending()));

        return new ResponseResult(iYqueAgents.getContent(),iYqueAgents.getTotalElements());

    }

    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        iYqueAgentService.batchDelete(ids);

        return new ResponseResult();
    }


    /**
     * 发送消息
     * @param iYqueAgent
     * @return
     */
    @PostMapping("/sendAgentMsg")
    public ResponseResult sendAgentMsg(@RequestBody IYqueAgent iYqueAgent) throws Exception {
        iYqueAgentService.sendAgentMsg(iYqueAgent);
        return new ResponseResult();
    }


    /**
     * 获取历史消息列表
     * @param id
     * @return
     */
    @GetMapping("/findAgentSubAll/{id}")
    public ResponseResult<IYqueAgentSub> findAgentSubAll(@PathVariable Integer id){

        Page<IYqueAgentSub> iYqueAgentSubs = iYqueAgentService.findAgentSubAll(id,
                PageRequest.of(TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("sendTime").descending()));

        return new ResponseResult(iYqueAgentSubs.getContent(),iYqueAgentSubs.getTotalElements());

    }






}
