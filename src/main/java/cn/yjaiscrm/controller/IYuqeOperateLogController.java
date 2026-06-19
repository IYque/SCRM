package cn.iyque.controller;


import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYuqeOperateLog;
import cn.iyque.enums.OperateLogType;
import cn.iyque.service.IYuqeOperateLogService;
import cn.iyque.utils.TableSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * 企微操作日志相关
 */
@RestController
@RequestMapping("/log")
@Slf4j
public class IYuqeOperateLogController {


    @Autowired
    IYuqeOperateLogService yuqeOperateLogService;


    /**
     * 获取操作类型
     * @param operateType 1:成员操作类型；2:管理员操作类型
     * @return
     */
    @GetMapping("/findOperateLogTypes")
    public ResponseResult<OperateLogType.SubType>  findOperateLogTypes(Integer operateType){
        List<OperateLogType.SubType> subTypesByGroupTypes =
                OperateLogType.getSubTypesByGroupType(operateType);

        return new ResponseResult(subTypesByGroupTypes);
    }


    /**
     * 获取操作日志列表
     * @param iYuqeOperateLog
     * @return
     */
    @GetMapping("/findAll")
    public ResponseResult<IYuqeOperateLog> findAll(IYuqeOperateLog iYuqeOperateLog){

        Page<IYuqeOperateLog> operateLogs = yuqeOperateLogService.findAll(iYuqeOperateLog,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("createTime").descending()));
        return new ResponseResult(operateLogs.getContent(),operateLogs.getTotalElements());
    }


    /**
     * 同步操作日志 1：成员操作日志 2:管理员操作日志
     * @return
     */
    @PostMapping("/synchOperateLog")
    public ResponseResult synchOperateLog(@RequestBody IYuqeOperateLog iYuqeOperateLog){

        yuqeOperateLogService.synchOperateLog(iYuqeOperateLog.getOperateType());

        return new ResponseResult("客户真正同步中,请稍后查看");

    }


}
