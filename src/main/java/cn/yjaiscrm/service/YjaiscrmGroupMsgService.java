package cn.iyque.service;

import cn.iyque.entity.IYqueGroupMsg;
import cn.iyque.entity.IYqueUser;
import cn.iyque.exception.IYqueException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IYqueGroupMsgService {


    /**
     * 群发列表
     * @param iYqueGroupMsg
     * @param pageable
     * @return
     */
    Page<IYqueGroupMsg> findIYqueGroupMsgPage(IYqueGroupMsg iYqueGroupMsg, Pageable pageable);


    /**
     * 获取群发详情
     * @param id
     * @return
     */
    IYqueGroupMsg findIYqueGroupMsgById(Long id);


    /**
     * 群发构建
     * @param iYqueGroupMsg
     */
    void buildGroupMsg(IYqueGroupMsg iYqueGroupMsg) throws IYqueException;
}
