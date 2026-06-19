package cn.iyque.mass.sender;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.dao.IYqueGroupMsgDao;
import cn.iyque.dao.IYqueGroupMsgSubDao;
import cn.iyque.entity.IYqueGroupMsg;
import cn.iyque.entity.IYqueGroupMsgSub;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueMsgAnnexService;
import cn.iyque.utils.SnowFlakeUtils;
import cn.iyque.utils.SpringUtils;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;

public abstract class AbstractMassSender {



    public final void executeMassSend(IYqueGroupMsg task) throws IYqueException{

        SpringUtils.getBean(TransactionTemplate.class).execute(status -> {

            try {

                task.setId(SnowFlakeUtils.nextId());
                task.setUpdateTime(new Date());
                task.setCreateTime(new Date());
                //处理目标发送对象
                prepareTarget(task);
                //消息群发
                send(task);
                //数据入库
                saveToDatabase(task);
                return true;
            }catch (IYqueException e){

                status.setRollbackOnly();
                throw e;
            }




        });



    }


    /**
     * 处理群发目标
     * @param iYqueGroupMsg
     */
    protected abstract void prepareTarget(IYqueGroupMsg iYqueGroupMsg);


    /**
     * 群发任务入库
     * @param iYqueGroupMsg
     */
    protected void saveToDatabase(IYqueGroupMsg iYqueGroupMsg) {

        try {

            SpringUtils.getBean(IYqueGroupMsgDao.class).save(iYqueGroupMsg);
            List<IYqueGroupMsgSub> groupMsgSubList = iYqueGroupMsg.getGroupMsgSubList();
            if(CollectionUtil.isNotEmpty(groupMsgSubList)){
                SpringUtils.getBean(IYqueGroupMsgSubDao.class).saveAll(groupMsgSubList);
            }

            List<IYqueMsgAnnex> annexLists = iYqueGroupMsg.getAnnexLists();
            if(CollectionUtil.isNotEmpty(annexLists)){
                annexLists.stream().forEach(k->{
                    k.setMsgId(iYqueGroupMsg.getId());
                });
                SpringUtils.getBean(IYqueMsgAnnexService.class).saveAll(annexLists);
            }
        }catch (Exception e){
            throw e;
        }




    }


    // 抽象方法 - 由子类实现具体发送逻辑
    protected abstract void send(IYqueGroupMsg iYqueGroupMsg)  throws IYqueException;


}
