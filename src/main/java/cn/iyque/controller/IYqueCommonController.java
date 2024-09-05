package cn.iyque.controller;

import cn.iyque.domain.IYqueKvalVo;
import cn.iyque.domain.ResponseResult;
import cn.iyque.enums.RemarksType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/iYqueCommon")
public class IYqueCommonController {


    /**
     * 获取备注相关类型
     * @return
     */
    @GetMapping("/findRemarksType")
    public ResponseResult<IYqueKvalVo> findRemarksType(){
        List<IYqueKvalVo> yqueKvalVoList=new ArrayList<>();
        Arrays.stream(RemarksType.values()).forEach(k->{
            yqueKvalVoList.add(
                    IYqueKvalVo.builder()
                            .key(k.getCode())
                            .val(k.getInfo())
                            .build()
            );
        });

        return new ResponseResult(yqueKvalVoList);

    }
}
