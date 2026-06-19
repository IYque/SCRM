package cn.iyque.service;

import cn.iyque.domain.IYqueAnalysisHotWordTabVo;
import cn.iyque.domain.IYqueAnalysisHotWordVo;
import cn.iyque.entity.IYqueAnalysisHotWord;
import cn.iyque.entity.IYqueHotWord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IYqueAnalysisHotWordService {


    /**
     * 热词讨论明细
     * @param iYqueAnalysisHotWord
     * @param pageable
     * @return
     */
    Page<IYqueAnalysisHotWord> findAll(IYqueAnalysisHotWord iYqueAnalysisHotWord, Pageable pageable);


    /**
     * 热词Top5
     * @param iYqueAnalysisHotWord
     * @return
     */
    List<IYqueAnalysisHotWordVo> hotWordTop5(IYqueAnalysisHotWord iYqueAnalysisHotWord);


    /**
     * 热词分类top5
     * @param iYqueAnalysisHotWord
     * @return
     */
    List<IYqueAnalysisHotWordVo> hotWordCategoryTop5(IYqueAnalysisHotWord iYqueAnalysisHotWord);


    /**
     * 获取头部统计tab
     * @return
     */
    IYqueAnalysisHotWordTabVo findHotWordTab();



}
