package cn.iyque.dao;

import cn.iyque.entity.IYqueHotWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueHotWordDao extends JpaRepository<IYqueHotWord,Long>, JpaSpecificationExecutor<IYqueHotWord> {
}
