package cn.iyque.controller;


import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueKf;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueKfService;
import cn.iyque.utils.TableSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;


/**
 * 排班客服
 */
@RequestMapping("/shiftKf")
@Slf4j
@RestController
public class IYqueShiftKfController {
    @Autowired
    private IYqueKfService yqueKfService;

    /**
     * 获取客服列表
     * @param iYqueKf
     * @return
     */
    @GetMapping("/findAll")
    public ResponseResult<IYqueKf> findAll(IYqueKf iYqueKf){
        iYqueKf.setKfType(2);
        Page<IYqueKf> iYqueKfs = yqueKfService.findAll(iYqueKf,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("updateTime").descending()));
        return new ResponseResult(iYqueKfs.getContent(),iYqueKfs.getTotalElements());
    }




    /**
     * 新增或编辑客服
     */
    @PostMapping("/saveOrUpdateKf")
    public ResponseResult saveOrUpdateKf(@RequestBody IYqueKf iYqueKf) {

        try {
            iYqueKf.setKfType(2);
            yqueKfService.saveOrUpdateKf(iYqueKf);
        }catch (IYqueException e){
            return new ResponseResult(HttpStatus.ERROR,e.getMsg(),null);
        }

        return new ResponseResult();
    }




    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        yqueKfService.batchDelete(Arrays.asList(ids));

        return new ResponseResult();
    }
}
