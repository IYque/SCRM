package cn.iyque.dao;

import cn.iyque.entity.IYqueAnalysisHotWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface IYqueAnalysisHotWordDao extends JpaRepository<IYqueAnalysisHotWord,Long>, JpaSpecificationExecutor<IYqueAnalysisHotWord> {


    /**
     * 根据多个 msgId 删除对应的记录
     * @param msgIds 要删除的 msgId 列表
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM iyque_analysis_hot_word y WHERE y.msgId IN :msgIds")
    void deleteByMsgIds(@Param("msgIds") List<String> msgIds);



}
