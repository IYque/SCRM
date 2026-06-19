package cn.iyque.dao;

import cn.iyque.entity.IYQueScript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYQueScriptDao extends JpaRepository<IYQueScript,Long> , JpaSpecificationExecutor<IYQueScript> {
}
