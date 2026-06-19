package cn.iyque.dao;

import cn.iyque.entity.IYqueAnnexPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IYqueAnnexPeriodDao extends JpaRepository<IYqueAnnexPeriod,Long> {
    List<IYqueAnnexPeriod> findIYqueAnnexPeriodByMsgId(Long msgId);

    void deleteIYqueAnnexPeriodByMsgId(Long msgId);
}
