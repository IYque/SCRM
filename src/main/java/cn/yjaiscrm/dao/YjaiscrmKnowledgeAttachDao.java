package cn.iyque.dao;

import cn.iyque.entity.IYqueKnowledgeAttach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueKnowledgeAttachDao  extends JpaRepository<IYqueKnowledgeAttach,Long>, JpaSpecificationExecutor<IYqueKnowledgeAttach> {
    void deleteByKid(Long kid);
}
