package cn.iyque.dao;

import cn.iyque.entity.IYquePeriodMsgAnnex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IYquePeriodMsgAnnexDao extends JpaRepository<IYquePeriodMsgAnnex,Long> {

    List<IYquePeriodMsgAnnex> findIYquePeriodMsgAnnexByAnnexPeroidId(Long annexPeroidId);


    void deleteAllByAnnexPeroidIdIn(List<Long> annexPeroidIds);



}
