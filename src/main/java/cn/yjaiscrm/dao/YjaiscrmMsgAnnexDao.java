package cn.iyque.dao;

import cn.iyque.entity.IYqueMsgAnnex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IYqueMsgAnnexDao extends JpaRepository<IYqueMsgAnnex,Long> {
    List<IYqueMsgAnnex> findIYqueMsgAnnexByMsgId(Long msgId);
    void deleteIYqueMsgAnnexByMsgId(Long msgId);
}
