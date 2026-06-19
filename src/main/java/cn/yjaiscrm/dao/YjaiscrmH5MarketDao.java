package cn.iyque.dao;

import cn.iyque.entity.IYqueH5Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueH5MarketDao extends JpaRepository<IYqueH5Market,Long>, JpaSpecificationExecutor<IYqueH5Market> {
}
