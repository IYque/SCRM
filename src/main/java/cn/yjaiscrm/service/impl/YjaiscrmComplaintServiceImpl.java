package cn.iyque.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYQueComplaintAnnexDao;
import cn.iyque.dao.IYQueComplaintDao;
import cn.iyque.dao.IYQueComplaintTipDao;
import cn.iyque.dao.IYqueUserDao;
import cn.iyque.domain.IYQueCountQuery;
import cn.iyque.domain.IYQueTrendCount;
import cn.iyque.domain.IYqueComplaintCountVo;
import cn.iyque.domain.IYqueUserCodeCountVo;
import cn.iyque.entity.*;
import cn.iyque.enums.ComplaintAnnexType;
import cn.iyque.enums.ComplaintContent;
import cn.iyque.service.IYQueComplaintService;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class IYQueComplaintServiceImpl implements IYQueComplaintService {

    @Autowired
    private IYQueComplaintDao iyQueComplaintDao;

    @Autowired
    private IYQueComplaintAnnexDao iyQueComplaintAnnexDao;

    @Autowired
    private IYQueComplaintTipDao iyQueComplaintTipDao;

    @Autowired
    private IYqueUserDao iYqueUserDao;


    @Autowired
    private IYqueConfigService iYqueConfigService;


    @Autowired
    private IYqueParamConfig iYqueParamConfig;








    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addComplaint(IYQueComplain iyQueComplain) {
        IYQueComplain saveIYQueComplain = iyQueComplaintDao.save(iyQueComplain);

        if(null != saveIYQueComplain){
            List<IYqueComplainAnnex> complainAnnexList =
                    iyQueComplain.getComplainAnnexList();
            if(CollectionUtil.isNotEmpty(complainAnnexList)){
                complainAnnexList.stream().forEach(k->{
                    k.setAnnexType(ComplaintAnnexType.ONE_TYPE.getCode());
                    k.setComplainId(saveIYQueComplain.getId());
                });
                iyQueComplaintAnnexDao.saveAll(complainAnnexList);
            }

            //发送通知
            ThreadUtil.execute(()->{
                distributeHandle(saveIYQueComplain.getId());
            });

        }
    }

    @Override
    public IYQueComplain findIYQueComplainById(Long id) {
        Optional<IYQueComplain> optional = iyQueComplaintDao.findById(id);
        if(optional.isPresent()){
            IYQueComplain iyQueComplain = optional.get();

            iyQueComplain.setComplainAnnexList(
                    iyQueComplaintAnnexDao.findByComplainId(iyQueComplain.getId())
            );

            if(StringUtils.isNotEmpty(iyQueComplain.getHandleWeUserId())){
                List<IYqueUser> iYqueUsers = iYqueUserDao.findByUserId(iyQueComplain.getHandleWeUserId());
                if(CollectionUtil.isNotEmpty(iYqueUsers)){
                    iyQueComplain.setHandleUserName(
                            iYqueUsers.stream().findFirst().get().getName()
                    );

                    iyQueComplain.setComplainTypeContent(
                            ComplaintContent.getValueByCode(iyQueComplain.getComplainType())
                    );
                }
            }

            return iyQueComplain;

        }
        return null;
    }


    /**
     * 处理意见
     * @param iyQueComplain
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleComplaint(IYQueComplain iyQueComplain) {
        iyQueComplain.setHandleState(2);
        iyQueComplain.setHandleTime(new Date());
        iyQueComplaintDao.saveAndFlush(iyQueComplain);
        List<IYqueComplainAnnex> complainAnnexList = iyQueComplain.getComplainAnnexList();
        if(CollectionUtil.isNotEmpty(complainAnnexList)){
            complainAnnexList.stream().forEach(k->{
                k.setAnnexType(ComplaintAnnexType.TWO_TYPE.getCode());
                k.setComplainId(iyQueComplain.getId());
            });
            iyQueComplaintAnnexDao.saveAll(complainAnnexList);

        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setIYQueComplaintTip(List<IYQueComplaintTip> iyQueComplaintTip) {
        iyQueComplaintTipDao.deleteAll();
        iyQueComplaintTipDao.saveAll(iyQueComplaintTip);
    }

    @Override
    public void distributeHandle(Long id) {

        try {

            Optional<IYQueComplain> optional = iyQueComplaintDao.findById(id);
            if(optional.isPresent()){
                IYQueComplain iyQueComplain = optional.get();

                //未处理状态,则发送通知
                if(null != iyQueComplain && new Integer(1).equals(iyQueComplain.getHandleState())){
                    List<IYQueComplaintTip> iyQueComplaintTips = iyQueComplaintTipDao.findAll();

                    if(CollectionUtil.isNotEmpty(iyQueComplaintTips)){
                        IYqueConfig iYqueConfig = iYqueConfigService.findIYqueConfig();

                        if(null != iYqueConfig){
                            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
                            wxcpservice.getMessageService().send(
                                    WxCpMessage.TEXTCARD()
                                            .toUser(iyQueComplaintTips.stream()
                                                    .map(IYQueComplaintTip::getUserId)
                                                    .collect(Collectors.joining("|")))
                                            .agentId(new Integer(iYqueConfig.getAgentId()))
                                            .title("客户投诉")
                                            .description( String.format(IYqueContant.complaintTipTpl, iyQueComplain.getComplainTime(),
                                                    ComplaintContent.getValueByCode(iyQueComplain.getComplainType()),iyQueComplain.getComplainUserPhone()))
                                            .url(iYqueParamConfig.getComplaintUrl())
                                            .btnTxt("更多").build()
                            );
                        }
                    }

                }




            }






        }catch (Exception e){

            log.error("投诉处理通知失败:"+e.getMessage());
        }


    }

    public static void main(String[] args) {

        String valueByCode = ComplaintContent.getValueByCode(102);
        System.out.println(valueByCode);
    }

    @Override
    public List<IYQueComplaintTip> findIYQueComplaintTips() {


        List<IYQueComplaintTip> complaintTips = iyQueComplaintTipDao.findAll();
        if (CollectionUtil.isNotEmpty(complaintTips)) {

            List<IYqueUser> iYqueUsers = iYqueUserDao.findAll();
            if (CollectionUtil.isNotEmpty(iYqueUsers)) {

                complaintTips.stream().forEach(k -> {

                    List<IYqueUser> tipUser = iYqueUsers.stream()
                            .filter(item -> item.getUserId().equals(k.getUserId())).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(tipUser)) {
                        k.setUserName(
                                tipUser.stream().findFirst().get().getName()
                        );
                    }
                });
            }
        }
        return complaintTips;
    }

    @Override
    public Page<IYQueComplain> findAll(IYQueComplain iyQueComplain,Pageable pageable) {
        Specification<IYQueComplain> spec = Specification.where(null);

        if(iyQueComplain.getComplainType() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("complainType"), iyQueComplain.getComplainType()));
        }

        if(iyQueComplain.getHandleState() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("handleState"), iyQueComplain.getHandleState()));
        }

        Page<IYQueComplain> iyQueComplains = iyQueComplaintDao.findAll(spec, pageable);
        List<IYQueComplain> content = iyQueComplains.getContent();
        if(CollectionUtil.isNotEmpty(content)){

            List<IYqueComplainAnnex> complainAnnexList = iyQueComplaintAnnexDao.findAll();

            List<IYqueUser> iYqueUsers = iYqueUserDao.findAll();

            //设置附件与处理人名称
            content.stream().forEach(k->{

                if(CollectionUtil.isNotEmpty(complainAnnexList)){
                    //附件
                    k.setComplainAnnexList(
                            complainAnnexList.stream().filter(item->item.getComplainId().equals(k.getId())).collect(Collectors.toList())
                    );
                }


                if(CollectionUtil.isNotEmpty(iYqueUsers)){
                    Optional<IYqueUser> optional = iYqueUsers.stream().filter(item -> item.getUserId().equals(k.getHandleWeUserId()))
                            .findFirst();

                   if(optional.isPresent()){
                       k.setHandleUserName(
                               optional.get().getName()
                       );
                   }
                }
            });

        }

        return iyQueComplains;
    }

    @Override
    public IYQueTrendCount countTrend(IYQueCountQuery queCountQuery) {
        IYQueTrendCount trendCount=new IYQueTrendCount();

        List<List<Integer>> series=new ArrayList<>();

        trendCount.setXData(
                DateUtils.getTimePeriods(queCountQuery.getStartTime(),queCountQuery.getEndTime())
        );

        List<IYQueComplain> iyQueComplains = iyQueComplaintDao.findAll();

        //总投诉数
        series.add(
                IYqueComplaintCountVo.getDateCountList(iyQueComplains,
                        queCountQuery.getStartTime() == null ? null :
                                queCountQuery.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        queCountQuery.getEndTime() == null ?
                                null : queCountQuery.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),null)
        );

        //总投诉数
        series.add(
                IYqueComplaintCountVo.getDateCountList(iyQueComplains,
                        queCountQuery.getStartTime() == null ? null :
                                queCountQuery.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        queCountQuery.getEndTime() == null ?
                                null : queCountQuery.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),null)
        );

        //待处理投诉
        series.add(
                IYqueComplaintCountVo.getDateCountList(iyQueComplains,
                        queCountQuery.getStartTime() == null ? null :
                                queCountQuery.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        queCountQuery.getEndTime() == null ?
                                null : queCountQuery.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),1)
        );


        //已处理投诉
        series.add(
                IYqueComplaintCountVo.getDateCountList(iyQueComplains,
                        queCountQuery.getStartTime() == null ? null :
                                queCountQuery.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        queCountQuery.getEndTime() == null ?
                                null : queCountQuery.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),2)
        );

        trendCount.setSeries(series);




        return trendCount;
    }

    @Override
    public IYqueComplaintCountVo countTotalTab(IYQueCountQuery queCountQuery) {
        IYqueComplaintCountVo iYqueComplaintCountVo=new IYqueComplaintCountVo();

        List<IYQueComplain> iyQueComplains = iyQueComplaintDao.findAll();
        if(CollectionUtil.isNotEmpty(iyQueComplains)){
            iYqueComplaintCountVo.setComplaintTotalNumber(
                    iyQueComplains.size()
            );
            iYqueComplaintCountVo.setNoComplaintTotalNumber(
                    (int) iyQueComplains.stream()
                            .filter(complain ->new Integer(1).equals(complain.getHandleState() ))
                            .count()
            );
            iYqueComplaintCountVo.setYesComplaintTotalNumber(
                    (int) iyQueComplains.stream()
                            .filter(complain ->new Integer(2).equals(complain.getHandleState() ))
                            .count()
            );



            iYqueComplaintCountVo.setTdComplaintTotalNumber(
                    (int)iyQueComplains.stream()
                            .filter(item -> {
                                Date addTime = item.getComplainTime();
                                if(null == addTime){
                                    return false;
                                }
                                LocalDate addTimeDate = addTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                return addTimeDate.isEqual(LocalDate.now());
                            }).count()
            );



            iYqueComplaintCountVo.setTdNoComplaintTotalNumber(
                    (int) iyQueComplains.stream()
                            .filter(item -> item.getComplainTime().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                    .isEqual(LocalDate.now())
                                    && item.getHandleState() == 1)
                            .count()
            );


            iYqueComplaintCountVo.setTdYesComplaintTotalNumber(
                    (int) iyQueComplains.stream()
                            .filter(item -> item.getComplainTime().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                    .isEqual(LocalDate.now())
                                    && item.getHandleState() == 2)
                            .count()
            );




        }




        return iYqueComplaintCountVo;
    }
}
