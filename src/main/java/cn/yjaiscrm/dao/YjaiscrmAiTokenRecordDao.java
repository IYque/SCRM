package cn.iyque.dao;

import cn.iyque.entity.IYqueAiTokenRecord;
import cn.iyque.entity.IYqueAnnexPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueAiTokenRecordDao extends JpaRepository<IYqueAiTokenRecord,Long> , JpaSpecificationExecutor<IYqueAiTokenRecord> {
}
