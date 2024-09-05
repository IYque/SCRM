package cn.iyque.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.ResponseResult;
import cn.iyque.service.impl.IYqueTagServiceImpl;
import me.chanjar.weixin.cp.bean.WxCpTag;
import me.chanjar.weixin.cp.bean.external.WxCpUserExternalTagGroupInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * 企微标签
 */
@RestController
@RequestMapping("/iYqueTag")
public class IYqueTagController {




    @Autowired
    private IYqueTagServiceImpl iYqueWxCptTagService;


    /**
     * 获取标签
     * @return
     */
    @GetMapping("/findIYqueTag")
    public ResponseResult findIYqueTag(){
        List<WxCpUserExternalTagGroupInfo.Tag> wxCpTags=new ArrayList<>();
        try {
            List<WxCpUserExternalTagGroupInfo.Tag> wxCpTagList = iYqueWxCptTagService.listAll();
            if(CollectionUtil.isNotEmpty(wxCpTagList)){
                wxCpTags.addAll(wxCpTagList);
            }
        }catch (Exception e){
            return new ResponseResult(HttpStatus.WE_ERROR,e.getMessage(),null);
        }


        return new ResponseResult(wxCpTags);
    }
}
