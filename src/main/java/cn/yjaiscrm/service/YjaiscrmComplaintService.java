package cn.iyque.service;

import cn.iyque.domain.IYQueCountQuery;
import cn.iyque.domain.IYQueTrendCount;
import cn.iyque.domain.IYqueComplaintCountVo;
import cn.iyque.domain.IYqueUserCodeCountVo;
import cn.iyque.entity.IYQueComplain;
import cn.iyque.entity.IYQueComplaintTip;
import cn.iyque.entity.IYqueUserCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IYQueComplaintService {

    /**
     * 创建投诉
     * @param iyQueComplain
     */
    void addComplaint(IYQueComplain iyQueComplain);


    /**
     * 投诉详情
     * @param id
     * @return
     */
    IYQueComplain findIYQueComplainById(Long id);


    /**
     * 处理投诉
     * @param iyQueComplain
     */
    void handleComplaint(IYQueComplain iyQueComplain);


    /**
     * 设置通知人
     * @param iyQueComplaintTip
     */
    void setIYQueComplaintTip(List<IYQueComplaintTip> iyQueComplaintTip);


    /**
     * 通知处理投诉
     * @param id
     */
    void distributeHandle(Long id);

    /**
     * 获取通知人
     * @return
     */
    List<IYQueComplaintTip> findIYQueComplaintTips();


    /**
     * 分页获取投诉列表
     * @param iyQueComplain
     * @param pageable
     * @return
     */
    Page<IYQueComplain> findAll(IYQueComplain iyQueComplain,Pageable pageable);


    /**
     * 投诉统计
     * @param queCountQuery
     * @return
     */
    IYQueTrendCount countTrend(IYQueCountQuery queCountQuery);


    /**
     * 头部tab统计
     * @param queCountQuery
     * @return
     */
    IYqueComplaintCountVo countTotalTab(IYQueCountQuery queCountQuery);

}
