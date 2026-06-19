package cn.iyque.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.domain.IYqueAnalysisHotWordTabVo;
import cn.iyque.domain.IYqueAnalysisHotWordVo;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.BaseEntity;
import cn.iyque.entity.IYqueAnalysisHotWord;
import cn.iyque.entity.IYqueHotWord;
import cn.iyque.service.IYqueAnalysisHotWordService;
import cn.iyque.service.IYqueHotWordService;
import cn.iyque.utils.TableSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 热词分析
 */
@RestController
@RequestMapping("/analysisHotWord")
@Slf4j
public class IYqueAnalysisHotWordController {


    @Autowired
    private IYqueAnalysisHotWordService yqueAnalysisHotWordService;


    @Autowired
    private IYqueHotWordService yqueHotWordService;




    /**
     * 获取热词讨论明细
     * @param iYqueAnalysisHotWord
     * @return
     */
    @GetMapping("/findAll")
    public ResponseResult<IYqueAnalysisHotWord> findAll(IYqueAnalysisHotWord iYqueAnalysisHotWord){

        Page<IYqueAnalysisHotWord> iYqueAnalysisHotWords = yqueAnalysisHotWordService.findAll(iYqueAnalysisHotWord,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("msgTime").descending()));
        return new ResponseResult(iYqueAnalysisHotWords.getContent(),iYqueAnalysisHotWords.getTotalElements());
    }


    /**
     * 热词top5
     * @param iYqueAnalysisHotWord
     * @return
     */
    @GetMapping("/hotWordTop5")
    public ResponseResult<IYqueAnalysisHotWordVo> hotWordTop5(IYqueAnalysisHotWord iYqueAnalysisHotWord){
        List<IYqueAnalysisHotWordVo> iYqueAnalysisHotWordVos =
                yqueAnalysisHotWordService.hotWordTop5(iYqueAnalysisHotWord);


        return new ResponseResult(iYqueAnalysisHotWordVos);

    }


    /**
     * 热词分类top5
     * @param iYqueAnalysisHotWord
     * @return
     */
    @GetMapping("/hotWordCategoryTop5")
    public ResponseResult<IYqueAnalysisHotWordVo> hotWordCategoryTop5(IYqueAnalysisHotWord iYqueAnalysisHotWord){
        List<IYqueAnalysisHotWordVo> iYqueAnalysisHotWordVos =
                yqueAnalysisHotWordService.hotWordCategoryTop5(iYqueAnalysisHotWord);

        return new ResponseResult(iYqueAnalysisHotWordVos);
    }


    /**
     * 头部tab
     * @return
     */
    @GetMapping("/findHotWordTab")
    public ResponseResult<IYqueAnalysisHotWordTabVo> findHotWordTab(){
        IYqueAnalysisHotWordTabVo hotWordTab = yqueAnalysisHotWordService.findHotWordTab();
        return new ResponseResult<>(hotWordTab);
    }


    /**
     * ai热词分析
     */
    @GetMapping("/aiHotWordAnalysis")
    public ResponseResult aiHotWordAnalysis(BaseEntity baseEntity){

        List<IYqueHotWord> iYqueHotWords = yqueHotWordService.findAll();

        if(CollectionUtil.isEmpty(iYqueHotWords)){
            return new ResponseResult("请前往热词管理设置热词");
        }
        baseEntity.setMsgAuditType(new Integer(1));

        return new ResponseResult(
                yqueHotWordService.aiHotWordAnalysis(iYqueHotWords,baseEntity)
        );
    }


}
