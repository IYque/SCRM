package cn.iyque.dao;

import cn.iyque.entity.IYqueFileSecurity;
import cn.iyque.entity.IYuqeOperateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueFileSecurityDao extends JpaRepository<IYqueFileSecurity,Long>, JpaSpecificationExecutor<IYqueFileSecurity> {
}
