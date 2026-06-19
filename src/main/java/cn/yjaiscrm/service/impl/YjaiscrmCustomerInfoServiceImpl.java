package cn.iyque.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.iyque.constant.CodeStateConstant;
import cn.iyque.dao.*;
import cn.iyque.domain.*;
import cn.iyque.entity.*;
import cn.iyque.enums.CustomerAddWay;
import cn.iyque.enums.CustomerStatusType;
import cn.iyque.enums.SynchDataRecordType;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueCustomerInfoService;
import cn.iyque.service.IYqueTagService;
import cn.iyque.strategy.callback.*;
import cn.iyque.utils.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpExternalContactService;
import me.chanjar.weixin.cp.bean.external.contact.ExternalContact;
import me.chanjar.weixin.cp.bean.external.contact.FollowedUser;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactBatchInfo;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
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


    @Autowired
    IYqueSynchDataRecordDao iYqueSynchDataRecordDao;


    @Autowired
    IYqueUserDao iYqueUserDao;

    @Autowired
    IYqueTagService iYqueTagService;


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

    @Override
    public List<IYQueCustomerInfo> saveCustomer(String externalUserid) {
        List<IYQueCustomerInfo> infos=new ArrayList<>();
        try {
            //数据库不存在则直接去企业微信api获取
            WxCpExternalContactInfo externalContact = iYqueConfigService.findWxcpservice().getExternalContactService()
                    .getExternalContact(externalUserid);


            if(null != externalContact){

                List<FollowedUser> followedUsers = externalContact.getFollowedUsers();
                if(CollectionUtil.isNotEmpty(followedUsers)){
                    followedUsers.stream().forEach(kk->{

                        infos.add(
                                IYQueCustomerInfo.builder()
                                        .eId(externalContact.getExternalContact().getExternalUserId()+"&"+kk.getUserId())
                                        .customerName(externalContact.getExternalContact().getName())
                                        .externalUserid(externalContact.getExternalContact().getExternalUserId())
                                        .userId(kk.getUserId())
                                        .state(kk.getState())
                                        .addTime(new Date( kk.getCreateTime() * 1000L))
                                        .status(CustomerStatusType.CUSTOMER_STATUS_TYPE_COMMON.getCode())
                                        .build()
                        );


                    });

                    iyQueCustomerInfoDao.saveAllAndFlush(infos);
                }

            }



        }catch (Exception e){
            log.error("企微用户获取失败:"+externalUserid);
        }

        return infos;




    }

    @Override
    public IYQueCustomerInfo findCustomerInfoByExternalUserId(String externalUserid) {
        IYQueCustomerInfo iyQueCustomerInfo=new IYQueCustomerInfo();
        List<IYQueCustomerInfo> iyQueCustomerInfos = iyQueCustomerInfoDao
                .findByExternalUserid(externalUserid);
        if(CollectionUtil.isNotEmpty(iyQueCustomerInfos)){
            iyQueCustomerInfo=iyQueCustomerInfos.stream().findFirst().get();
        }else{

            try {

                List<IYQueCustomerInfo> iyNewQueCustomerInfos = this.saveCustomer(externalUserid);
                if(CollectionUtil.isNotEmpty(iyNewQueCustomerInfos)){
                    iyQueCustomerInfo=iyNewQueCustomerInfos.stream().findFirst().get();
                }

            }catch (Exception e){
                log.error("获取客户信息异常:"+e.getMessage());
            }

        }

        return iyQueCustomerInfo;
    }

    @Override
    public Page<IYQueCustomerInfo> findAll(IYQueCustomerInfo iyQueCustomerInfo, Pageable pageable) {
        Specification<IYQueCustomerInfo> spec = Specification.where(null);

        if(StringUtils.isNotEmpty(iyQueCustomerInfo.getCustomerName())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("customerName")), "%" + iyQueCustomerInfo.getCustomerName().toLowerCase() + "%"));

        }

        if (iyQueCustomerInfo.getStatus() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("status")), iyQueCustomerInfo.getStatus()));
        }

        if (iyQueCustomerInfo.getType() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("type")), iyQueCustomerInfo.getType()));
        }



        Page<IYQueCustomerInfo> customerInfos = iyQueCustomerInfoDao.findAll(spec, pageable);
        List<IYQueCustomerInfo> content = customerInfos.getContent();
        if(CollectionUtil.isNotEmpty(content)) {
            content.stream().forEach(k->{
                List<IYqueUser> iYqueUsers = iYqueUserDao.findByUserId(k.getUserId());
                if(CollectionUtil.isNotEmpty(iYqueUsers)){
                    k.setUserName(
                            iYqueUsers.stream().findFirst().get().getName()
                    );

                }

                if(StringUtils.isNotEmpty(k.getTagIds())){
                    List<IYqueTag> iYqueTags = iYqueTagService.list(new LambdaQueryWrapper<IYqueTag>()
                            .in(IYqueTag::getTagId,
                                    Arrays.stream(k.getTagIds().split(","))
                                            .map(String::trim)
                                            .filter(id -> !id.isEmpty())
                                            .collect(Collectors.toList())

                                    ));
                    if(CollectionUtil.isNotEmpty(iYqueTags)){
                        k.setTagNames(
                                iYqueTags.stream()
                                        .map(IYqueTag::getName)
                                        .filter(Objects::nonNull)
                                        .map(String::trim)
                                        .filter(name -> !name.isEmpty())
                                        .collect(Collectors.joining(","))
                        );
                    }

                }



            });
        }



        return customerInfos;
    }


    @Override
    public List<IYQueCustomerInfo> findByExternalUserid(String externalUserid) {
        return iyQueCustomerInfoDao.findByExternalUserid(externalUserid);
    }

    @Override
    public List<IYQueCustomerInfo> findByExternalUseridIn(List<String> externalUserids) {
        return iyQueCustomerInfoDao.findByExternalUseridIn(externalUserids);
    }

    @Override
    public void makeTag(IYQueCustomerDto customerDto) {

        IYQueCustomerInfo customerInfo = iyQueCustomerInfoDao.findByExternalUseridAndUserId(customerDto.getExternalUserid()
                , customerDto.getUserId());


        if(null != customerDto){
            customerInfo.setTagIds( String.join(",", customerDto.getTagIds()));
            iyQueCustomerInfoDao.saveAndFlush(customerInfo);
        }


    }

    @Override
    @Async
    public void synchCustomer() {
        try {
            List<IYQueCustomerInfo> customerInfos=new ArrayList<>();

            WxCpExternalContactService externalContactService = iYqueConfigService.findWxcpservice()
                    .getExternalContactService();

            if(null != externalContactService){
                //获取应用可见范围下，拥有外部联系人功能的成员列表
                List<String> listFollowers =
                        externalContactService.listFollowers();
                if(CollectionUtil.isNotEmpty(listFollowers)){
                    log.info("外部联系人列表:"+listFollowers);
                    ListUtil.partition(listFollowers, 100).stream().forEach(kk->{


                        try {
                            String nextCursor="";
                            IYqueSynchDataRecord iYqueSynchDataRecord = iYqueSynchDataRecordDao.findTopBySynchDataTypeOrderByCreateTimeDesc(SynchDataRecordType
                                    .RECORD_TYPE_SYNCH_CUSTOMER.getCode());

                            if(null != iYqueSynchDataRecord && StringUtils.isNotEmpty(iYqueSynchDataRecord.getNextCursor())){
                                nextCursor=iYqueSynchDataRecord.getNextCursor();
                            }



                            do {

                                WxCpExternalContactBatchInfo contactDetailBatch = externalContactService
                                        .getContactDetailBatch(Convert.toStrArray(kk), nextCursor, 100);

                                if(contactDetailBatch.success()){
                                    List<WxCpExternalContactBatchInfo.ExternalContactInfo> externalContactList = contactDetailBatch.getExternalContactList();

                                    for(WxCpExternalContactBatchInfo.ExternalContactInfo item:externalContactList){
                                        //跟进人
                                        FollowedUser followInfo = item.getFollowInfo();


                                        //客户基础信息
                                        ExternalContact externalContact = item.getExternalContact();

                                         IYQueCustomerInfo customerInfo = IYQueCustomerInfo.builder().eId(externalContact.getExternalUserId() + "&" + followInfo.getUserId())
                                                .customerName(externalContact.getName())
                                                .avatar(externalContact.getAvatar())
                                                .type(externalContact.getType())
                                                .externalUserid(externalContact.getExternalUserId())

                                                .userId(followInfo.getUserId())
                                                .state(followInfo.getState())
                                                .addTime(new Date(followInfo.getCreateTime() * 1000L))
                                                .status(CustomerStatusType.CUSTOMER_STATUS_TYPE_COMMON.getCode())
                                                .build();

                                        String addWay = followInfo.getAddWay();
                                        log.info("客户来源类型:"+addWay);
                                        if(StringUtils.isNotEmpty(addWay)){
                                            CustomerAddWay customerAddWay = CustomerAddWay.of(new Integer(addWay));
                                            if(null != customerAddWay){
                                                customerInfo.setAddWay(customerAddWay.getVal());
                                            }
                                        }

                                        customerInfos.add(
                                                customerInfo
                                        );



                                    }
                                    nextCursor=contactDetailBatch.getNextCursor();

                                    if(StringUtils.isNotEmpty(nextCursor)){
                                        //最后一次同步下标记录
                                        iYqueSynchDataRecordDao.save(
                                                IYqueSynchDataRecord.builder().synchDataType(SynchDataRecordType
                                                                .RECORD_TYPE_SYNCH_CUSTOMER.getCode())
                                                        .nextCursor(nextCursor)
                                                        .createTime(new Date())
                                                        .build()
                                        );
                                    }




                                }


                            } while (StringUtils.isNotEmpty(nextCursor));




                        }catch (Exception e){
                            e.printStackTrace();

                            throw new IYqueException(e.getMessage());
                        }

                    });

                }


                if(CollectionUtil.isNotEmpty(customerInfos)){
                    ListUtil.partition(customerInfos, 100).stream().forEach(items->{

                        iyQueCustomerInfoDao.saveAllAndFlush(items);
                    });

                }
            }




        }catch (Exception e){
            log.error("客户同步失败:"+e.getMessage());
        }

    }


}
