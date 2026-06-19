package cn.iyque.controller;


import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueCategory;
import cn.iyque.service.IYqueCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class IYqueCategoryController {

    @Autowired
    private IYqueCategoryService yqueCategoryService;



    /**
     * 新增或更新分类
     * @param iYqueCategory
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public ResponseResult saveOrUpdate(@RequestBody IYqueCategory iYqueCategory) {
        try {
            yqueCategoryService.saveOrUpdate(iYqueCategory);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }

    /**
     * 获取分类列表
     * @return
     */
    @GetMapping("/findIYqueCategory")
    public ResponseResult<IYqueCategory> findIYqueCategory(IYqueCategory iYqueCategory){
        List<IYqueCategory> iYqueCategories =
                yqueCategoryService.findAll(iYqueCategory);
        return new ResponseResult(iYqueCategories);
    }


    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        yqueCategoryService.batchDelete(ids);

        return new ResponseResult();
    }



}
