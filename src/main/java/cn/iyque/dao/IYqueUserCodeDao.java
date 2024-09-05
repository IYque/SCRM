package cn.iyque.dao;

import cn.iyque.entity.IYqueUserCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IYqueUserCodeDao extends JpaRepository<IYqueUserCode,Long> {
    IYqueUserCode findByCodeState(String codeState);
}
