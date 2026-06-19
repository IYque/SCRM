package cn.iyque.service;

import cn.iyque.domain.IYqueSummaryKfMsgDto;
import cn.iyque.entity.IYqueKf;
import cn.iyque.entity.IYqueKfMsgSub;
import cn.iyque.entity.IYqueKnowledgeInfo;
import cn.iyque.entity.IYqueSummaryKfMsg;
import me.chanjar.weixin.cp.bean.kf.WxCpKfMsgListResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IYqueKfMsgService {

    /**
     * 客户与客服会话信息入库
     * @param iyqueKf
     * @param item
     * @param isArtificial 是否人工
     */
    void  saveIYqueKfMsg(IYqueKf iyqueKf,WxCpKfMsgListResp.WxCpKfMsgItem item,boolean isArtificial);




    /**
     * 客户与客服会话列表
     * @param iYqueKfMsgSub
     * @param pageable
     * @return
     */
    Page<IYqueKfMsgSub> findAll(IYqueKfMsgSub iYqueKfMsgSub, Pageable pageable);



    /**
     * 根据客户id去重查询相关咨询客户
     * @return
     */
    List<IYqueSummaryKfMsgDto> findGroupAll();


    /**
     * 获取指定客户待分析数据
     * @param externalUserIds
     * @return
     */
    List<IYqueSummaryKfMsgDto> findIYqueSummaryKfMsg(List<String> externalUserIds);


    /**
     * ai总结客服客户会话
     * @param externalUserIds
     */
    void summaryKfmsgByAi(List<String> externalUserIds);



    /**
     * 获取AI内容总结列表
     * @param summaryKfMsg
     * @param pageable
     * @return
     */
    Page<IYqueSummaryKfMsg> findSummaryKfMsgs(IYqueSummaryKfMsg summaryKfMsg, Pageable pageable);

}
