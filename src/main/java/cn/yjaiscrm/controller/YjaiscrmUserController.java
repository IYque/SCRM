package cn.iyque.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueUser;
import cn.iyque.entity.IYqueUserCode;
import cn.iyque.enums.ComplaintContent;
import cn.iyque.service.IYqueUserService;
import cn.iyque.utils.MapUtils;
import cn.iyque.utils.TableSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 企业微信员工相关
 */
@RestController
@RequestMapping("/iYqueUser")
public class IYqueUserController {


    @Autowired
    private IYqueUserService iYqueUserService;


    /**
     * 获取企业微信员工(所有)
     * @return
     */
    @GetMapping("/findIYqueUser")
    public ResponseResult findIYqueUser(){

        List<IYqueUser> iYqueUser = iYqueUserService.findIYqueUser();

        if(CollectionUtil.isNotEmpty(iYqueUser)){
            return new ResponseResult(iYqueUser.stream()
                    .filter(MapUtils.distinctByKey(IYqueUser::getUserId))
                    .collect(Collectors.toList()));
        }
        return new ResponseResult();
    }


    /**
     * 获取企业微信员工(分页)
     * @param iYqueUser
     * @return
     */
    @GetMapping("/findIYqueUserPage")
    public ResponseResult<IYqueUser> findIYqueUserPage(IYqueUser iYqueUser){
        Page<IYqueUser> iYqueUsers = iYqueUserService.findIYqueUserPage(iYqueUser.getName(),PageRequest.of(
                TableSupport.buildPageRequest().getPageNum(), TableSupport.buildPageRequest().getPageSize()));
        return new ResponseResult(iYqueUsers.getContent(),iYqueUsers.getTotalElements());
    }


    /**
     * 同步成员
     * @return
     */
    @PostMapping("/synchIyqueUser")
    public  ResponseResult synchIyqueUser(){

        iYqueUserService.synchIyqueUser();

        return new ResponseResult("员工同步中,请稍后查看");
    }





}
