package cn.iyque.service;


import cn.iyque.entity.IYqueChat;
import cn.iyque.entity.IYqueChatCode;
import cn.iyque.entity.IYqueUserCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface IYqueChatCodeService {


    /**
     * 群活码列表
     * @param pageable
     * @return
     */
    Page<IYqueChatCode> findAll(Pageable pageable);



    /**
     * 创建群活码
     * @param iYqueChatCode
     */
    void createChatCode(IYqueChatCode iYqueChatCode) throws Exception;


    /**
     * 更新群活码
     * @param iYqueChatCode
     * @throws Exception
     */
     void updateChatCode(IYqueChatCode iYqueChatCode)  throws Exception;



    /**
     * 删除群活码
     * @param ids
     */
    void batchDelete(Long[] ids);


}
