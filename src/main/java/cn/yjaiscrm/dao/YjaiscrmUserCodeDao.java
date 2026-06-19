package cn.iyque.dao;

import cn.iyque.entity.IYqueUserCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueUserCodeDao  extends JpaRepository<IYqueUserCode,Long>, JpaSpecificationExecutor<IYqueUserCode> {
    IYqueUserCode findByCodeState(String codeState);

    IYqueUserCode findByConfigId(String configId);
}
