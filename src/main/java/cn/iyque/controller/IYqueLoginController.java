package cn.iyque.controller;


import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.IYQueAuthInfo;
import cn.iyque.domain.JwtResponse;
import cn.iyque.domain.ResponseResult;
import cn.iyque.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 系统登录地址
 */
@RestController
@RequestMapping("/iYqueSys")
public class IYqueLoginController {



    @Autowired
    private IYqueParamConfig iYqueParamConfig;


    /**
     * 简单的登录
     * @return
     */
    @PostMapping("/login")
    public ResponseResult<JwtResponse> login(@RequestBody IYQueAuthInfo iQyqueAuthInfo){


        if(iYqueParamConfig.getUserName().equals(iQyqueAuthInfo.getUsername())
          &&iYqueParamConfig.getPwd().equals(iQyqueAuthInfo.getPassword())
        ){
            return new ResponseResult<>(JwtResponse.builder()
                    .token(JwtUtils.generateToken(iYqueParamConfig.getUserName()))
                    .build());

        }
        return new ResponseResult<>(HttpStatus.ERROR,"账号或密码错误",null);
    }










}
