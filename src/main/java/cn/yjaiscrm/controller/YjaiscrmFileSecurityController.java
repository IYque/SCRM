package cn.iyque.controller;


import cn.iyque.domain.ResponseResult;
import cn.iyque.domain.SubType;
import cn.iyque.entity.BaseEntity;
import cn.iyque.entity.IYqueFileSecurity;
import cn.iyque.enums.FileSecurityOperation;
import cn.iyque.enums.FileSecuritySource;
import cn.iyque.service.IYqueFileSecurityService;
import cn.iyque.utils.TableSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fileSecurity")
public class IYqueFileSecurityController {


    @Autowired
    private IYqueFileSecurityService fileSecurityService;


    /**
     * 企微文件操作类型
     * @return
     */
    @GetMapping("/getFileSecurityOperations")
    public ResponseResult getFileSecurityOperations(){


        List<SubType> subTypeListFromEnum =
                FileSecurityOperation.getSubTypeListFromEnum();

        return new ResponseResult(subTypeListFromEnum);
    }


    /**
     * 企微文件操作来源
     * @return
     */
    @GetMapping("/getFileSecuritySources")
    public ResponseResult getFileSecuritySources(){

        List<SubType> subTypeListFromEnum =
                FileSecuritySource.getSubTypeListFromEnum();

        return new ResponseResult(subTypeListFromEnum);
    }



    /**
     * 信息同步
     * @return
     */
    @PostMapping("/synchInfo")
    public ResponseResult synchInfo(@RequestBody BaseEntity baseEntity){

        fileSecurityService.synchInfo(baseEntity);

        return new ResponseResult("信息真在同步中,请稍后查看");

    }


    /**
     * 获取操作信息列表
     * @param fileSecurity
     * @return
     */
    @GetMapping("/findAll")
    public ResponseResult<IYqueFileSecurity> findAll(IYqueFileSecurity fileSecurity){

        Page<IYqueFileSecurity> fileSecurities = fileSecurityService.findAll(fileSecurity,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("operateTime").descending()));
        return new ResponseResult(fileSecurities.getContent(),fileSecurities.getTotalElements());
    }




}
