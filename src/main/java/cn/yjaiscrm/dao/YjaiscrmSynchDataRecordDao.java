package cn.iyque.dao;

import cn.iyque.entity.IYqueSynchDataRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IYqueSynchDataRecordDao extends JpaRepository<IYqueSynchDataRecord,Long>, JpaSpecificationExecutor<IYqueSynchDataRecord> {

    // 直接返回Optional<String>避免NPE
    IYqueSynchDataRecord findTopBySynchDataTypeOrderByCreateTimeDesc(Integer type);

}
