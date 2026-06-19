package cn.iyque.dao;

import cn.iyque.entity.IYqueKnowledgeFragment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueKnowledgeFragmentDao extends JpaRepository<IYqueKnowledgeFragment,Long>, JpaSpecificationExecutor<IYqueKnowledgeFragment> {


    void  deleteByKid(Long kid);


    void deleteByDocId(Long docId);
}
