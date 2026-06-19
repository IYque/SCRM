package cn.iyque.controller;


import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueMaterial;
import cn.iyque.service.IYqueMaterialService;
import cn.iyque.utils.TableSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


/**
 * 素材管理
 */
@RestController
@RequestMapping("/material")
public class IYqueMaterialController {

    @Autowired
    IYqueMaterialService yqueMaterialService;


    @Autowired
    IYqueParamConfig yqueParamConfig;


    /**
     * 新增或更新素材
     * @param iYqueMaterial
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public ResponseResult saveOrUpdate(@RequestBody IYqueMaterial iYqueMaterial) {
        try {
            iYqueMaterial.setUpdateBy(yqueParamConfig.getUserName());
            yqueMaterialService.saveOrUpdate(iYqueMaterial);
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
    public ResponseResult<IYqueMaterial> findAll(IYqueMaterial iYqueMaterial){
        Page<IYqueMaterial> iYqueMaterials = yqueMaterialService.findAll(iYqueMaterial,
                PageRequest.of(TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("updateTime").descending()));

        return new ResponseResult(iYqueMaterials.getContent(),iYqueMaterials.getTotalElements());

    }


    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        yqueMaterialService.batchDelete(ids);

        return new ResponseResult();
    }





}
