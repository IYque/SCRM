package cn.iyque.controller;


import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueRobot;
import cn.iyque.entity.IYqueRobotSub;
import cn.iyque.service.IYQueRobotService;
import cn.iyque.utils.TableSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


/**
 * 机器人
 */
@RestController
@RequestMapping("/robot")
public class IYQueRobotController {


    @Autowired
    IYQueRobotService queRobotService;

    @Autowired
    IYqueParamConfig yqueParamConfig;

    /**
     * 新增或更新机器人
     * @param iYqueRobot
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public ResponseResult saveOrUpdate(@RequestBody IYqueRobot iYqueRobot) {
        try {
            if(iYqueRobot.getCreateTime()==null){
                iYqueRobot.setCreateTime(new Date());
            }
            iYqueRobot.setUpdateBy(yqueParamConfig.getUserName());
            iYqueRobot.setUpdateTime(new Date());
            queRobotService.addOrUpdate(iYqueRobot);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }


    /**
     * 机器人列表
     * @return
     */
    @GetMapping("/findAll")
    public ResponseResult<IYqueRobot> findAll(){
        Page<IYqueRobot> iYqueRobots = queRobotService.findAll(
                PageRequest.of(TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("updateTime").descending()));

        return new ResponseResult(iYqueRobots.getContent(),iYqueRobots.getTotalElements());

    }


    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        queRobotService.batchDelete(ids);

        return new ResponseResult();
    }


    /**
     * 发送消息
     * @param iYqueRobot
     * @return
     */
    @PostMapping("/sendRobotMsg")
    public ResponseResult sendRobotMsg(@RequestBody IYqueRobot iYqueRobot) throws Exception {
        queRobotService.sendRobotMsg(iYqueRobot);
        return new ResponseResult();
    }


    /**
     * 获取历史消息列表
     * @param robotId
     * @return
     */
    @GetMapping("/findRobotSubAll/{robotId}")
    public ResponseResult<IYqueRobotSub> findRobotSubAll(@PathVariable Long robotId){

        Page<IYqueRobotSub> iYqueRobotSubs = queRobotService.findRobotSubAll(robotId,
                PageRequest.of(TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("sendTime").descending()));

        return new ResponseResult(iYqueRobotSubs.getContent(),iYqueRobotSubs.getTotalElements());

    }



}
