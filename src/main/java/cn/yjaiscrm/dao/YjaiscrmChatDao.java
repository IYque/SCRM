package cn.iyque.dao;

import cn.iyque.entity.IYqueChat;
import cn.iyque.entity.IYqueUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IYqueChatDao extends JpaRepository<IYqueChat,Long> , JpaSpecificationExecutor<IYqueChat> {

    /**
     * 根据群id获取群
     * @param chatId
     * @return
     */
    List<IYqueChat> findIYqueChatByChatId(String chatId);
}
