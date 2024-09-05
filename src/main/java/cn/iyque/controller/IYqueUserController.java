package cn.iyque.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.ResponseResult;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.utils.MapUtils;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpDepartmentService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.WxCpUserService;
import me.chanjar.weixin.cp.bean.WxCpDepart;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 企业微信员工相关
 */
@RestController
@RequestMapping("/iYqueUser")
public class IYqueUserController {

    @Autowired
    private IYqueConfigService iYqueConfigService;


    /**
     * 获取企业微信员工
     * @return
     */
    @GetMapping("/findIYqueUser")
    public ResponseResult findIYqueUser(){

        List<WxCpUser> wxCpUsers=new ArrayList<>();
        try {
            WxCpService wxCpServic = iYqueConfigService.findWxcpservice();
            WxCpDepartmentService departmentService = wxCpServic.getDepartmentService();
            List<WxCpDepart> departList = departmentService.list(null);

            if(CollectionUtil.isNotEmpty(departList)){
                WxCpUserService userService = wxCpServic.getUserService();
                departList.stream().forEach(k->{

                    try {
                        List<WxCpUser> wxCpUserList = userService.listByDepartment(k.getId(), true, 0);
                        if(CollectionUtil.isNotEmpty(wxCpUserList)){
                            wxCpUsers.addAll(wxCpUserList);
                        }

                    }catch (WxErrorException e) {
                        throw new RuntimeException(e);
                    }

                });
            }


        }catch (Exception e){
            return new ResponseResult(HttpStatus.WE_ERROR,e.getMessage(),null);

        }

        return new ResponseResult(wxCpUsers.stream()
                .filter(MapUtils.distinctByKey(WxCpUser::getUserId))
                .collect(Collectors.toList()));


    }
}
