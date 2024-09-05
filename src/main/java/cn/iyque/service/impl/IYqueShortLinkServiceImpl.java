package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.iyque.constant.CodeStateConstant;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueShortLinkDao;
import cn.iyque.domain.IYqueKvalStrVo;
import cn.iyque.entity.*;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.*;
import cn.iyque.utils.SnowFlakeUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionCreateResult;
import me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionInfo;
import me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IYqueShortLinkServiceImpl implements IYqueShortLinkService {

    @Autowired
    private IYqueConfigService iYqueConfigService;

    @Autowired
    private IYqueShortLinkDao iYqueShortLinkDao;

    @Autowired
    private IYqueMsgAnnexService iYqueMsgAnnexService;


    @Autowired
    private IYqueAnnexPeriodService iYqueAnnexPeriodService;


    @Autowired
    private IYquePeriodMsgAnnexService iYquePeriodMsgAnnexService;

    @Override
    public Page<IYqueShortLink> findAll(Pageable pageable) {
        return iYqueShortLinkDao.findAll(pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(IYqueShortLink shortLink) throws Exception {

        try {
            shortLink.setCreateTime(new Date());
            shortLink.setUpdateTime(new Date());
            shortLink.setCodeState(CodeStateConstant.LINK_CODE_STATE+ SnowFlakeUtils.nextId());
            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
            WxCpCustomerAcquisitionRequest request=new WxCpCustomerAcquisitionRequest();
            request.setLinkName(shortLink.getCodeName());
            request.setSkipVerify(shortLink.getSkipVerify());
            WxCpCustomerAcquisitionInfo.Range range=new WxCpCustomerAcquisitionInfo.Range();
            range.setUserList(ListUtil.toList(shortLink.getUserId().split(",")));
            request.setRange(range);

            WxCpCustomerAcquisitionCreateResult createResult = wxcpservice.getExternalContactService().customerAcquisitionLinkCreate(
                    request
            );

                if(createResult == null ){
                    throw new IYqueException("获客短链创建失败");
                }



                WxCpCustomerAcquisitionInfo.Link link = createResult.getLink();

                if(null == link){
                    throw new IYqueException("获客短链创建失败");
                }

                if(null != link && StrUtil.isNotEmpty(link.getLinkId())
                        && StrUtil.isNotEmpty(link.getUrl())){
                    shortLink.setConfigId(link.getLinkId());
                    shortLink.setCodeUrl(link.getUrl()+"?customer_channel="+shortLink.getCodeState() );

                    iYqueShortLinkDao.save(shortLink);


                    //时段欢迎语附件
                    if(shortLink.isStartPeriodAnnex()){


                        List<IYqueAnnexPeriod> periodAnnexLists=shortLink.getPeriodAnnexLists();
                        if(CollectionUtil.isNotEmpty(periodAnnexLists)){
                            //时段附件
                            List<IYquePeriodMsgAnnex> iYquePeriodMsgAnnexes=new ArrayList<>();
                            periodAnnexLists.stream().forEach(k->{
                                k.setMsgId(shortLink.getId());
                            });

                            //存储时段
                            iYqueAnnexPeriodService.saveAll(periodAnnexLists);
                            //存储时段附件
                            periodAnnexLists.stream().forEach(k->{
                                List<IYquePeriodMsgAnnex> periodMsgAnnexList = k.getPeriodMsgAnnexList();
                                if(CollectionUtil.isNotEmpty(periodMsgAnnexList)){
                                    periodMsgAnnexList.stream().forEach(periodMsgAnnex->{
                                        periodMsgAnnex.setAnnexPeroidId(k.getId());
                                    });
                                    iYquePeriodMsgAnnexes.addAll(periodMsgAnnexList);
                                }
                            });

                            iYquePeriodMsgAnnexService.saveAll(iYquePeriodMsgAnnexes);

                        }
                    }else{

                        List<IYqueMsgAnnex> annexLists = shortLink.getAnnexLists();
                        if(CollectionUtil.isNotEmpty(annexLists)){
                            annexLists.stream().forEach(k->{
                                k.setMsgId(shortLink.getId());
                            });
                            iYqueMsgAnnexService.saveAll(annexLists);
                        }
                    }
                }
        }catch (Exception e){
            log.error("获客链接，创建失败:"+e.getMessage());
           throw e;
        }



    }

    @Override
    public List<IYqueKvalStrVo> findIYqueShorkLinkKvs() {
        List<IYqueKvalStrVo> iYqueKvalVos=new ArrayList<>();


        List<IYqueShortLink> iYqueShortLinks = iYqueShortLinkDao.findAll();
        if(CollectionUtil.isNotEmpty(iYqueShortLinks)){
            iYqueShortLinks.stream().forEach(k->{
                iYqueKvalVos.add(
                        IYqueKvalStrVo.builder()
                                .val(k.getId().toString())
                                .key(k.getCodeName())
                                .build()
                );

            });
        }



        return iYqueKvalVos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(IYqueShortLink shortLink) throws Exception {

        try {
            IYqueShortLink oldShortLink = this.findIYqueShortLinkById(shortLink.getId());
            if(null != oldShortLink){

                shortLink.setCodeState(oldShortLink.getCodeState());
                shortLink.setConfigId(oldShortLink.getConfigId());
                WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
                WxCpCustomerAcquisitionRequest request=new WxCpCustomerAcquisitionRequest();
                request.setLinkId(oldShortLink.getConfigId());
                request.setLinkName(shortLink.getCodeName());
                request.setSkipVerify(shortLink.getSkipVerify());
                WxCpCustomerAcquisitionInfo.Range range=new WxCpCustomerAcquisitionInfo.Range();
                range.setUserList(ListUtil.toList(shortLink.getUserId().split(",")));
                request.setRange(range);

                WxCpBaseResp wxCpBaseResp
                        = wxcpservice.getExternalContactService().customerAcquisitionUpdate(request);

                if(null == wxCpBaseResp){
                    throw new IYqueException("获客短链创建失败");
                }

                 if(IYqueContant.COMMON_STATE.equals(wxCpBaseResp.getErrcode().intValue())){

                     shortLink.setUpdateTime(new Date());
                     iYqueShortLinkDao.saveAndFlush(shortLink);


                     if(shortLink.isStartPeriodAnnex()){//开启时段欢迎语

                         List<IYqueAnnexPeriod> periodAnnexLists=shortLink.getPeriodAnnexLists();
                         if(CollectionUtil.isNotEmpty(periodAnnexLists)){

                             //时段附件
                             List<IYquePeriodMsgAnnex> iYquePeriodMsgAnnexes=new ArrayList<>();
                             periodAnnexLists.stream().forEach(k->{
                                 k.setMsgId(shortLink.getId());
                             });

                             //存储时段
                             List<IYqueAnnexPeriod> oldIYqueAnnexPeriod = iYqueAnnexPeriodService
                                     .findIYqueAnnexPeriodByMsgId(shortLink.getId());

                             if(CollectionUtil.isNotEmpty(oldIYqueAnnexPeriod)){
                                 iYqueAnnexPeriodService.deleteIYqueAnnexPeriodByMsgId(shortLink.getId());
                                 iYquePeriodMsgAnnexService.deleteAllByAnnexPeroidIdIn(
                                         oldIYqueAnnexPeriod.stream().map(IYqueAnnexPeriod::getId).collect(Collectors.toList())
                                 );
                             }
                             iYqueAnnexPeriodService.saveAll(periodAnnexLists);


                             periodAnnexLists.stream().forEach(k->{
                                 List<IYquePeriodMsgAnnex> periodMsgAnnexList = k.getPeriodMsgAnnexList();
                                 if(CollectionUtil.isNotEmpty(periodMsgAnnexList)){
                                     periodMsgAnnexList.stream().forEach(periodMsgAnnex->{
                                         periodMsgAnnex.setAnnexPeroidId(k.getId());
                                     });
                                     iYquePeriodMsgAnnexes.addAll(periodMsgAnnexList);
                                 }
                             });
                             iYquePeriodMsgAnnexService.saveAll(iYquePeriodMsgAnnexes);


                         }

                     }else{
                         iYqueMsgAnnexService.deleteIYqueMsgAnnexByMsgId(shortLink.getId());
                         List<IYqueMsgAnnex> annexLists = shortLink.getAnnexLists();
                         if(CollectionUtil.isNotEmpty(annexLists)){
                             annexLists.stream().forEach(k->{
                                 k.setMsgId(shortLink.getId());
                             });
                             iYqueMsgAnnexService.saveAll(annexLists);
                         }

                     }





                 }else{
                     log.error("获客短链更新失败:"+wxCpBaseResp.getErrmsg());
                 }

            }

        }catch (Exception e){
            log.error("获客短链更新失败:"+e.getMessage());
            throw e;
        }

    }

    @Override
    public IYqueShortLink findIYqueShortLinkById(Long id) {
        return iYqueShortLinkDao.getById(id);
    }

    @Override
    public void batchDelete(Long[] ids) {

        List<IYqueShortLink> iYqueShortLinks = iYqueShortLinkDao.findAllById(Arrays.asList(ids));

        if(CollectionUtil.isNotEmpty(iYqueShortLinks)){
            iYqueShortLinks.stream().forEach(k->{
                k.setDelFlag(IYqueContant.DEL_STATE);

                try {
                    iYqueConfigService.findWxcpservice().getExternalContactService().customerAcquisitionLinkDelete(k.getConfigId());
                    iYqueShortLinkDao.saveAndFlush(k);
                }catch (Exception e){
                    log.error("获客链接删除失败:"+e.getMessage());
                }

            });

        }

    }
}
