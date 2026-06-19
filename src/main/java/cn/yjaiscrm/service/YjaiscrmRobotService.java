package cn.iyque.service;

import cn.iyque.entity.IYqueRobot;
import cn.iyque.entity.IYqueRobotSub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IYQueRobotService {

    /**
     * 新增或编辑
     * @param yqueRobot
     */
    void addOrUpdate(IYqueRobot yqueRobot);



    /**
     * 删除
     * @param ids
     */
    void batchDelete(Long[] ids);



    /**
     * 机器人列表
     * @param pageable
     * @return
     */
    Page<IYqueRobot> findAll(Pageable pageable);



    /**
     * 机器人消息附件列表
     * @param pageable
     * @return
     */
    Page<IYqueRobotSub> findRobotSubAll(Long robotId,Pageable pageable);


    /**
     * 发送机器人消息
     * @param iYqueRobot
     */
    void  sendRobotMsg(IYqueRobot iYqueRobot) throws Exception;


}
