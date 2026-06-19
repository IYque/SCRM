package cn.iyque.dao;

import cn.iyque.entity.IYqueKnowledgeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueKnowledgeInfoDao extends JpaRepository<IYqueKnowledgeInfo,Long>, JpaSpecificationExecutor<IYqueKnowledgeInfo> {
}
