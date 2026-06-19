package cn.iyque.service;

import cn.iyque.entity.BaseEntity;
import cn.iyque.entity.IYqueHotWord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface IYqueHotWordService {
    /**
     * 获取热词列表
     * @return
     */
    Page<IYqueHotWord> findAll(IYqueHotWord iYqueHotWord, Pageable pageable);


    /**
     * 新增或编辑分类
     * @param iYqueHotWord
     */
    void saveOrUpdate(IYqueHotWord iYqueHotWord);

    /**
     * 获取所有热词
     * @return
     */
    List<IYqueHotWord> findAll();


    /**
     * 删除分类
     * @param ids
     */
    void batchDelete(Long[] ids);


    /**
     * ai热词分析
     * @param iYqueHotWords
     * @param baseEntity
     */
    String aiHotWordAnalysis( List<IYqueHotWord> iYqueHotWords,BaseEntity baseEntity);
}
