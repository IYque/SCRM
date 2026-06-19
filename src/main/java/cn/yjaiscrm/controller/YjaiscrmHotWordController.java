package cn.iyque.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.BaseEntity;
import cn.iyque.entity.IYqueConfig;
import cn.iyque.entity.IYqueHotWord;
import cn.iyque.entity.IYqueMsgRule;
import cn.iyque.service.IYqueHotWordService;
import cn.iyque.utils.TableSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 热词
 */
@RestController
@RequestMapping("/hotWord")
@Slf4j
public class IYqueHotWordController {

    @Autowired
    private IYqueHotWordService yqueHotWordService;


    @Autowired
    private IYqueParamConfig yqueParamConfig;


    /**
     * 新增或更新热词
     * @param iYqueHotWord
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public ResponseResult saveOrUpdate(@RequestBody IYqueHotWord iYqueHotWord) {
        try {
            iYqueHotWord.setUpdateBy(yqueParamConfig.getUserName());
            yqueHotWordService.saveOrUpdate(iYqueHotWord);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }

    /**
     * 获取热词列表
     * @return
     */
    @GetMapping("/findIYqueHotWord")
    public ResponseResult<IYqueHotWord> findIYqueHotWord(IYqueHotWord iYqueHotWord){
        Page<IYqueHotWord> iYqueHotWords = yqueHotWordService.findAll(iYqueHotWord,
                PageRequest.of(TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("createTime").descending()));

        return new ResponseResult(iYqueHotWords.getContent(),iYqueHotWords.getTotalElements());

    }






    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        yqueHotWordService.batchDelete(ids);

        return new ResponseResult();
    }
}
