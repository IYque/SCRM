package cn.iyque.dao;

import cn.iyque.entity.IYqueKf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueKfDao extends JpaRepository<IYqueKf,Long>, JpaSpecificationExecutor<IYqueKf> {

    IYqueKf findByOpenKfid(String openKfid);
}
