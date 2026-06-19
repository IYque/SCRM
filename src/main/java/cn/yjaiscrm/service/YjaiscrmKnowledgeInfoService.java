package cn.iyque.service;

import cn.iyque.domain.KnowledgeInfoUploadRequest;
import cn.iyque.entity.IYqueKnowledgeInfo;
import cn.iyque.entity.IYqueMsgAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IYqueKnowledgeInfoService {







    /**
     * 获取知识库列表
     * @param iYqueKnowledgeInfo
     * @param pageable
     * @return
     */
    Page<IYqueKnowledgeInfo> findAll(IYqueKnowledgeInfo iYqueKnowledgeInfo, Pageable pageable);


    /**
     * 获取所有知识库
     * @return
     */
    List<IYqueKnowledgeInfo> findAll();


    /**
     * 新增知识库
     * @param iYqueKnowledgeInfo
     */
    void saveOrUpdate(IYqueKnowledgeInfo iYqueKnowledgeInfo);


    /**
     * 根据主键获取对应的知识库信息
     * @param id
     * @return
     */
    IYqueKnowledgeInfo findKnowledgeInfoById(Long id);



    /**
     * 上传附件
     */
    void upload(KnowledgeInfoUploadRequest request);


    /**
     * 删除知识库
     */
    void removeKnowledge(Long id);
}
