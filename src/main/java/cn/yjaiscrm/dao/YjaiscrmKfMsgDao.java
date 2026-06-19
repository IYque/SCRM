package cn.iyque.dao;

import cn.iyque.entity.IYqueDefaultMsg;
import cn.iyque.entity.IYqueKfMsg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IYqueKfMsgDao extends JpaRepository<IYqueKfMsg,Long> {

    IYqueKfMsg findTopByOpenKfidOrderByPullTimeDesc(String openKfid);
}
