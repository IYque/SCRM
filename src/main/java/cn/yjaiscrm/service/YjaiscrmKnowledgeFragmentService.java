package cn.iyque.service;

import cn.iyque.entity.IYqueKnowledgeAttach;
import cn.iyque.entity.IYqueKnowledgeFragment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IYqueKnowledgeFragmentService {

    /**
     * 获取知识库附件片段列表
     * @param iYqueKnowledgeInfo
     * @param pageable
     * @return
     */
    Page<IYqueKnowledgeFragment> findAll(IYqueKnowledgeFragment iYqueKnowledgeInfo, Pageable pageable);


    /**
     * 通过计算向量获取相近的数据
     * @param content 问题内容
     * @param kid 知识库id
     * @return
     */
    List<IYqueKnowledgeFragment> nearest(String content,String kid);


    /**
     * 根据主键批量获取存储片段
     * @param ids
     * @return
     */

    List<IYqueKnowledgeFragment> findAllByIds(List<Long> ids);
}
