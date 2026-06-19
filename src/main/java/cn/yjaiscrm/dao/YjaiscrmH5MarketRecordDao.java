package cn.iyque.dao;

import cn.iyque.entity.IYqueH5MarketRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface IYqueH5MarketRecordDao  extends JpaRepository<IYqueH5MarketRecord,Long>, JpaSpecificationExecutor<IYqueH5MarketRecord> {


    List<IYqueH5MarketRecord> findByH5MarketId(Long h5MarketId);









}
