package cn.iyque.service;

import cn.iyque.domain.IYQueGroupDto;
import cn.iyque.entity.IYqueChat;
import cn.iyque.entity.IYqueChatCode;
import cn.iyque.entity.IYqueUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IYqueChatService {



    /**
     * 获取分页群数据
     * @param pageable
     * @return
     */
    Page<IYqueChat> findAll(String name, Pageable pageable);


    /**
     * 获取所有群数据
     * @return
     */
    List<IYqueChat> findAllIYqueChat();


    /**
     * 同步客群
     */
    void synchIyqueChat();


    /**
     * 根据群id获取群明细，如果不存在则从企微拉取
     * @param chatId
     * @return
     */
    IYqueChat findOrSaveChat(String chatId);


    /**
     * 客群打标签
     * @param iyQueGroupDto
     */
    void makeTag(IYQueGroupDto iyQueGroupDto);
}
