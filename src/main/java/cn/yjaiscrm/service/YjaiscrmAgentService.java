package cn.iyque.service;

import cn.iyque.entity.IYqueAgent;
import cn.iyque.entity.IYqueAgentSub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;

public interface IYqueAgentService {

    /**
     * 新增或编辑
     * @param iYqueAgent
     */
    void addOrUpdate(IYqueAgent iYqueAgent);



    /**
     * 删除
     * @param ids
     */
    void batchDelete(Long[] ids);


    /**
     * 应用列表
     * @param pageable
     * @return
     */
    Page<IYqueAgent> findAll(Pageable pageable);


    /**
     * 应用信息同步
     * @param id
     */
    void synchAgent(Long id);




    /**
     * 应用消息附件列表
     * @param pageable
     * @return
     */
    Page<IYqueAgentSub> findAgentSubAll(Integer agentId, Pageable pageable);


    /**
     * 发送应用消息
     * @param iYqueAgent
     */
    void  sendAgentMsg(IYqueAgent iYqueAgent) throws Exception;
}
