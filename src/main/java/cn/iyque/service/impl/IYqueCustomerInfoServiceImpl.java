package cn.iyque.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iyque.constant.CodeStateConstant;
import cn.iyque.dao.IYQueCustomerInfoDao;
import cn.iyque.dao.IYqueShortLinkDao;
import cn.iyque.dao.IYqueUserCodeDao;
import cn.iyque.domain.*;
import cn.iyque.entity.IYqueShortLink;
import cn.iyque.entity.IYqueUserCode;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueCustomerInfoService;
import cn.iyque.strategy.callback.*;
import cn.iyque.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IYqueCustomerInfoServiceImpl implements IYqueCustomerInfoService {

    @Autowired
    IYqueUserCodeDao iYqueUserCodeDao;

    @Autowired
    IYqueConfigService iYqueConfigService;

    @Autowired
    IYQueCustomerInfoDao iyQueCustomerInfoDao;


    @Autowired
    IYqueShortLinkDao iYqueShortLinkDao;


    @Override
    public void addCustomerCallBackAction(IYqueCallBackBaseMsg callBackBaseMsg) {

        try {

            if(StrUtil.isNotEmpty(callBackBaseMsg.getState())){

                IYQueCallbackQuery iyQueCallbackQuery=null;

                //活码
                if(callBackBaseMsg.getState().startsWith(CodeStateConstant.USER_CODE_STATE)){

                    IYqueUserCode iYqueUserCode = iYqueUserCodeDao.findByCodeState(callBackBaseMsg.getState());
                    if(null != iYqueUserCode){
                        iyQueCallbackQuery=new IYQueCallbackQuery();
                        iyQueCallbackQuery.setBusinessId(iYqueUserCode.getId());
                        iyQueCallbackQuery.setTagId(iYqueUserCode.getTagId());
                        iyQueCallbackQuery.setTagName(iYqueUserCode.getTagName());
                        iyQueCallbackQuery.setStartPeriodAnnex(iYqueUserCode.isStartPeriodAnnex());
                        iyQueCallbackQuery.setWeclomeMsg(iYqueUserCode.getWeclomeMsg());
                        iyQueCallbackQuery.setRemarkName(iYqueUserCode.getCodeName());
                        iyQueCallbackQuery.setRemarkType(iYqueUserCode.getRemarkType());
                    }

                //获客短链
                }else if(callBackBaseMsg.getState().startsWith(CodeStateConstant.LINK_CODE_STATE)){
                    IYqueShortLink shortLink = iYqueShortLinkDao.findByCodeState(callBackBaseMsg.getState());
                    if(null != shortLink){
                        iyQueCallbackQuery=new IYQueCallbackQuery();
                        iyQueCallbackQuery.setBusinessId(shortLink.getId());
                        iyQueCallbackQuery.setTagId(shortLink.getTagId());
                        iyQueCallbackQuery.setTagName(shortLink.getTagName());
                        iyQueCallbackQuery.setStartPeriodAnnex(shortLink.isStartPeriodAnnex());
                        iyQueCallbackQuery.setWeclomeMsg(shortLink.getWeclomeMsg());
                        iyQueCallbackQuery.setRemarkName(shortLink.getCodeName());
                        iyQueCallbackQuery.setRemarkType(shortLink.getRemarkType());
                    }



                }


                WxCpExternalContactInfo contactDetail = iYqueConfigService.findWxcpservice().getExternalContactService()
                        .getContactDetail(callBackBaseMsg.getExternalUserID(), null);


                if(null != contactDetail&&iyQueCallbackQuery != null){
                    //发送欢迎语
                    ActionContext actionContext = new ActionContext(iyQueCallbackQuery.isStartPeriodAnnex()?
                            new SendPeriodWelcomeMsgStrategy():new SendWelcomeMsgStrategy());
                    actionContext.executeStrategy(callBackBaseMsg,iyQueCallbackQuery,contactDetail);



                    //自动打标签
                    if (StrUtil.isNotEmpty(iyQueCallbackQuery.getTagId())) {
                        actionContext.setActionStrategy(new MakeTagCustomerStrategy());
                        actionContext.executeStrategy(callBackBaseMsg,iyQueCallbackQuery,contactDetail);
                    }

                    //自动备注
                    if (null != iyQueCallbackQuery.getRemarkType()) {
                        actionContext.setActionStrategy(new RemarkCustomerStrategy());
                        actionContext.executeStrategy(callBackBaseMsg,iyQueCallbackQuery,contactDetail);
                    }

                    //客户相关信息入库
                    actionContext.setActionStrategy(new SaveCustomerStrategy());
                    actionContext.executeStrategy(callBackBaseMsg,iyQueCallbackQuery,contactDetail);

                }

            }


        }catch (Exception e){
            log.error("欢迎语动作执行异常:"+e.getMessage());

        }

    }

    @Override
    public void updateCustomerInfoStatus(String externalUserid, String userId, Integer status) {
        IYQueCustomerInfo iyQueCustomerInfo
                = iyQueCustomerInfoDao.findByExternalUseridAndUserId(externalUserid, userId);

        if(null != iyQueCustomerInfo){
            iyQueCustomerInfo.setStatus(status);
            iyQueCustomerInfoDao.saveAndFlush(
                    iyQueCustomerInfo
            );

        }
    }

    private List<IYQueCustomerInfo> findAllByIdOrNoId(Long userCodeId,boolean codeOrLink){
        List<IYQueCustomerInfo> iyQueCustomerInfos =new ArrayList<>();
        if(null !=userCodeId){
            if(codeOrLink){
                IYqueUserCode iYqueUserCode = iYqueUserCodeDao.findById(userCodeId).get();
                if(iYqueUserCode != null){
                    String codeState = iYqueUserCode.getCodeState();
                    if(StrUtil.isNotEmpty(codeState)){
                        iyQueCustomerInfos=iyQueCustomerInfoDao.findAll(Example.of(
                                IYQueCustomerInfo.builder()
                                        .state(codeState)
                                        .build()
                        ));
                    }

                }
            }else{

                IYqueShortLink shortLink = iYqueShortLinkDao.findById(userCodeId).get();
                if(shortLink != null){
                    String codeState = shortLink.getCodeState();
                    if(StrUtil.isNotEmpty(codeState)){
                        iyQueCustomerInfos=iyQueCustomerInfoDao.findAll(Example.of(
                                IYQueCustomerInfo.builder()
                                        .state(codeState)
                                        .build()
                        ));
                    }

                }

            }

        }else{
            iyQueCustomerInfos=iyQueCustomerInfoDao.findAll();
        }



        return iyQueCustomerInfos;

    }
    @Override
    public IYqueUserCodeCountVo countTotalTab(IYQueCountQuery queCountQuery,boolean codeOrLink) {
        IYqueUserCodeCountVo iYqueUserCodeCountVo
                = IYqueUserCodeCountVo.builder().build();


        List<IYQueCustomerInfo> iyQueCustomerInfos = this.findAllByIdOrNoId(queCountQuery.getUserCodeId(),codeOrLink);
        if(CollectionUtil.isNotEmpty(iyQueCustomerInfos)){

            List<IYQueCustomerInfo> allIYQueCustomerInfo=new ArrayList<>();
            if (queCountQuery.getStartTime() == null && queCountQuery.getEndTime() == null) {
                allIYQueCustomerInfo = iyQueCustomerInfos;
            } else {
                allIYQueCustomerInfo = iyQueCustomerInfos.stream()
                        .filter(info -> info.getAddTime().compareTo(queCountQuery.getStartTime()) >= 0
                                && info.getAddTime().compareTo(queCountQuery.getEndTime()) <= 0)
                        .collect(Collectors.toList());;
            }


            //客户总数
            iYqueUserCodeCountVo.setAddCustomerNumber(
                    allIYQueCustomerInfo.size()
            );


            //客户流失数
            iYqueUserCodeCountVo.setLostCustomerNumber(
                    allIYQueCustomerInfo.stream()
                            .filter(info -> info.getStatus() == 1)
                            .count()
            );

            //员工删除客户数
            iYqueUserCodeCountVo.setDelCustomerNumber(
                    allIYQueCustomerInfo.stream()
                            .filter(info -> info.getStatus() == 2)
                            .count()
            );

            //客户净增数
            iYqueUserCodeCountVo.setNetGrowthCustomerNumber(
                    allIYQueCustomerInfo.stream()
                            .filter(info -> info.getStatus() == 0)
                            .count()
            );


            //今日客户总数
            iYqueUserCodeCountVo.setTdAddCustomerNumber(
                    iyQueCustomerInfos.stream()
                            .filter(customerInfo -> {
                                Date addTime = customerInfo.getAddTime();
                                LocalDate addTimeDate = addTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                return addTimeDate.isEqual(LocalDate.now());
                            }).count()
            );


            //今日客户流失数
            iYqueUserCodeCountVo.setLostCustomerNumber(
                    iyQueCustomerInfos.stream()
                            .filter(customerInfo -> {
                                Date addTime = customerInfo.getAddTime();
                                LocalDate addTimeDate = addTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                return addTimeDate.isEqual(LocalDate.now()) && customerInfo.getStatus() == 1;
                            }).count()
            );


            //今日员工删除客户数
            iYqueUserCodeCountVo.setLostCustomerNumber(
                    iyQueCustomerInfos.stream()
                            .filter(customerInfo -> {
                                Date addTime = customerInfo.getAddTime();
                                LocalDate addTimeDate = addTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                return addTimeDate.isEqual(LocalDate.now()) && customerInfo.getStatus() == 2;
                            }).count()
            );




            //今日客户净增数
            iYqueUserCodeCountVo.setTdNetGrowthCustomerNumber(
                    iyQueCustomerInfos.stream()
                            .filter(customerInfo -> {
                                Date addTime = customerInfo.getAddTime();
                                LocalDate addTimeDate = addTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                return addTimeDate.isEqual(LocalDate.now()) && customerInfo.getStatus() == 0;
                            }).count()
            );

        }

        return iYqueUserCodeCountVo;
    }

    @Override
    public IYQueTrendCount countTrend(IYQueCountQuery queCountQuery,boolean codeOrLink) {
        IYQueTrendCount trendCount=new IYQueTrendCount();

        List<List<Integer>> series=new ArrayList<>();

        trendCount.setXData(
                DateUtils.getTimePeriods(queCountQuery.getStartTime(),queCountQuery.getEndTime())
        );



        List<IYQueCustomerInfo> iyQueCustomerInfos = this.findAllByIdOrNoId(queCountQuery.getUserCodeId(),codeOrLink);

        //新增客户数
        series.add(
                IYqueUserCodeCountVo
                        .getDateCountList(iyQueCustomerInfos, queCountQuery.getStartTime() == null ? null :
                                        queCountQuery.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                                queCountQuery.getEndTime() == null ?
                                        null : queCountQuery.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                , null)
        );


        //流失客户数
        series.add(
                IYqueUserCodeCountVo
                        .getDateCountList(iyQueCustomerInfos, queCountQuery.getStartTime() == null?null:
                                        queCountQuery.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                                queCountQuery.getEndTime()==null?
                                        null: queCountQuery.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                ,1)
        );


        //员工删除客户数
        series.add(
                IYqueUserCodeCountVo
                        .getDateCountList(iyQueCustomerInfos, queCountQuery.getStartTime() == null?null:
                                        queCountQuery.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                                queCountQuery.getEndTime()==null?
                                        null: queCountQuery.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                ,2)

        );

        //净增客户数
        series.add(
                IYqueUserCodeCountVo
                        .getDateCountList(iyQueCustomerInfos, queCountQuery.getStartTime() == null?null:
                                        queCountQuery.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                                queCountQuery.getEndTime()==null?
                                        null: queCountQuery.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                ,0)
        );


        trendCount.setSeries(
                series
        );

        return trendCount;
    }



}
