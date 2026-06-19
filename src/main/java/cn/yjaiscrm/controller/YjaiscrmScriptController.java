package cn.iyque.controller;


import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYQueScript;
import cn.iyque.service.IYQueScriptService;
import cn.iyque.utils.TableSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


/**
 * 组合话术
 */
@RestController
@RequestMapping("/script")
public class IYQueScriptController {

    @Autowired
    IYQueScriptService iyQueScriptService;

    @Autowired
    IYqueParamConfig yqueParamConfig;


    /**
     * 新增或更新素材
     * @param iyQueScript
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public ResponseResult saveOrUpdate(@RequestBody IYQueScript iyQueScript) {
        try {
            iyQueScript.setUpdateTime(new Date());
            if(iyQueScript.getCreateTime() == null){
                iyQueScript.setCreateTime(new Date());
            }
            iyQueScript.setUpdateBy(yqueParamConfig.getUserName());
            iyQueScriptService.addOrUpdate(iyQueScript);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }


    /**
     * 素材列表
     * @return
     */
    @GetMapping("/findAll")
    public ResponseResult<IYQueScript> findAll(IYQueScript iyQueScript){
        Page<IYQueScript> iyQueScripts = iyQueScriptService.findAll(iyQueScript,
                PageRequest.of(TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("updateTime").descending()));

        return new ResponseResult(iyQueScripts.getContent(),iyQueScripts.getTotalElements());

    }


    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        iyQueScriptService.batchDelete(ids);

        return new ResponseResult();
    }
}
