package cn.iyque.service;

import cn.iyque.entity.IYqueKnowledgeAttach;
import cn.iyque.entity.IYqueKnowledgeInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IYqueKnowledgeAttachService {

    /**
     * 删除知识附件
     */
    void removeKnowledgeAttach(Long docId);



    /**
     * 获取知识库附件列表
     * @param iYqueKnowledgeInfo
     * @param pageable
     * @return
     */
    Page<IYqueKnowledgeAttach> findAll(IYqueKnowledgeAttach iYqueKnowledgeInfo, Pageable pageable);
}
