package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.CodeStateConstant;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueUserCodeDao;
import cn.iyque.domain.*;
import cn.iyque.entity.*;
import cn.iyque.service.*;
import cn.iyque.utils.FileUtils;
import cn.iyque.utils.SnowFlakeUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpExternalContactService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.article.NewArticle;
import me.chanjar.weixin.cp.bean.external.WxCpContactWayInfo;
import me.chanjar.weixin.cp.bean.external.WxCpContactWayResult;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
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
public class IYqueUserCodeServiceImpl implements IYqueUserCodeService {

    @Autowired
    private IYqueUserCodeDao iYqueUserCodeDao;

    @Autowired
    private IYqueConfigService iYqueConfigService;

    @Autowired
    private IYqueMsgAnnexService iYqueMsgAnnexService;

    @Autowired
    private IYqueParamConfig iYqueParamConfig;

    @Autowired
    private IYquePeriodMsgAnnexService iYquePeriodMsgAnnexService;

    @Autowired
    private IYqueAnnexPeriodService iYqueAnnexPeriodService;



    @Override
    public Page<IYqueUserCode> findAll(Pageable pageable) {
        return iYqueUserCodeDao.findAll(pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(IYqueUserCode iYqueUserCode) throws Exception {
        try {
            iYqueUserCode.setCreateTime(new Date());
            iYqueUserCode.setUpdateTime(new Date());
            iYqueUserCode.setCodeState(CodeStateConstant.USER_CODE_STATE+ SnowFlakeUtils.nextId());

            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();

            WxCpContactWayInfo wxCpGroupJoinWayInfo=new WxCpContactWayInfo();
            NewContactWay contactWay=new NewContactWay();
            contactWay.setType(WxCpContactWayInfo.TYPE.MULTI);
            contactWay.setScene(WxCpContactWayInfo.SCENE.QRCODE);
            contactWay.setSkipVerify(iYqueUserCode.getSkipVerify());
            contactWay.setState(iYqueUserCode.getCodeState());
            contactWay.setUsers(ListUtil.toList(iYqueUserCode.getUserId().split(",")));
            contactWay.setIsExclusive(iYqueUserCode.getIsExclusive());
            wxCpGroupJoinWayInfo.setContactWay(contactWay);

            WxCpContactWayResult wxCpContactWayResult = wxcpservice.getExternalContactService().addContactWay(wxCpGroupJoinWayInfo);
            if(null != wxCpContactWayResult
            && StrUtil.isNotEmpty(wxCpContactWayResult.getQrCode())
            &&StrUtil.isNotEmpty(wxCpContactWayResult.getConfigId())){
                iYqueUserCode.setCodeUrl(wxCpContactWayResult.getQrCode());
                iYqueUserCode.setBackupQrUrl(wxCpContactWayResult.getQrCode());
                //替换自定义logo的二维码
                if(StrUtil.isNotEmpty(iYqueUserCode.getLogoUrl())){
                    String newQrUlr = FileUtils.buildQr(wxCpContactWayResult.getQrCode(),
                            iYqueUserCode.getLogoUrl(), iYqueParamConfig.getUploadDir());
                    if(StrUtil.isNotEmpty(newQrUlr)){
                        iYqueUserCode.setCodeUrl(newQrUlr);
                    }
                }
                iYqueUserCode.setConfigId(wxCpContactWayResult.getConfigId());
                iYqueUserCodeDao.save(iYqueUserCode);

                //时段欢迎语附件
                if(iYqueUserCode.isStartPeriodAnnex()){
                    List<IYqueAnnexPeriod> periodAnnexLists=iYqueUserCode.getPeriodAnnexLists();
                    if(CollectionUtil.isNotEmpty(periodAnnexLists)){
                        //时段附件
                        List<IYquePeriodMsgAnnex> iYquePeriodMsgAnnexes=new ArrayList<>();
                        periodAnnexLists.stream().forEach(k->{
                            k.setMsgId(iYqueUserCode.getId());
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

                    //非时段欢迎语附件
                }else{
                    List<IYqueMsgAnnex> annexLists = iYqueUserCode.getAnnexLists();
                    if(CollectionUtil.isNotEmpty(annexLists)){
                        annexLists.stream().forEach(k->{
                            k.setMsgId(iYqueUserCode.getId());
                        });
                        iYqueMsgAnnexService.saveAll(annexLists);
                    }
                }




            }


        }catch (Exception e){
            throw e;
        }

    }

    @Override
    public List<IYqueKvalStrVo> findIYqueUserCodeKvs() {
        List<IYqueKvalStrVo> iYqueKvalVos=new ArrayList<>();


        List<IYqueUserCode> iYqueUserCodes = iYqueUserCodeDao.findAll();
        if(CollectionUtil.isNotEmpty(iYqueUserCodes)){
            iYqueUserCodes.stream().forEach(k->{

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
    public void update(IYqueUserCode iYqueUserCode) throws Exception {

        try {
            IYqueUserCode oldIYqueUserCode = this.findIYqueUserCodeById(iYqueUserCode.getId());
            if(null != oldIYqueUserCode){
                iYqueUserCode.setCodeUrl(oldIYqueUserCode.getCodeUrl());
                iYqueUserCode.setCodeState(oldIYqueUserCode.getCodeState());
                iYqueUserCode.setConfigId(oldIYqueUserCode.getConfigId());

                WxCpContactWayInfo wxCpGroupJoinWayInfo=new WxCpContactWayInfo();
                NewContactWay contactWay=new NewContactWay();
                contactWay.setConfigId(oldIYqueUserCode.getConfigId());
                contactWay.setSkipVerify(iYqueUserCode.getSkipVerify());
                contactWay.setUsers(ListUtil.toList(iYqueUserCode.getUserId().split(",")));
                contactWay.setIsExclusive(iYqueUserCode.getIsExclusive());
                wxCpGroupJoinWayInfo.setContactWay(contactWay);

                WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
                WxCpExternalContactService externalContactService = wxcpservice.getExternalContactService();
                externalContactService.updateContactWay(wxCpGroupJoinWayInfo);

                //替换自定义logo的二维码
                if(StrUtil.isNotEmpty(iYqueUserCode.getLogoUrl())){
                    //判断原有logo是否改变，如果改变则更新
                    if(!iYqueUserCode.getLogoUrl().equals(
                            oldIYqueUserCode.getLogoUrl()
                    )){
                        String newQrUlr = FileUtils.buildQr(iYqueUserCode.getBackupQrUrl(),
                                iYqueUserCode.getLogoUrl(), iYqueParamConfig.getUploadDir());
                        if(StrUtil.isNotEmpty(newQrUlr)){
                            iYqueUserCode.setCodeUrl(newQrUlr);
                        }
                    }

                }else{
                    iYqueUserCode.setCodeUrl(oldIYqueUserCode.getBackupQrUrl());
                }
            }

            iYqueUserCode.setUpdateTime(new Date());
            iYqueUserCodeDao.saveAndFlush(iYqueUserCode);

            if(iYqueUserCode.isStartPeriodAnnex()){//开启时段欢迎语

                List<IYqueAnnexPeriod> periodAnnexLists=iYqueUserCode.getPeriodAnnexLists();
                if(CollectionUtil.isNotEmpty(periodAnnexLists)){

                    //时段附件
                    List<IYquePeriodMsgAnnex> iYquePeriodMsgAnnexes=new ArrayList<>();
                    periodAnnexLists.stream().forEach(k->{
                        k.setMsgId(iYqueUserCode.getId());
                    });

                    //存储时段
                    List<IYqueAnnexPeriod> oldIYqueAnnexPeriod = iYqueAnnexPeriodService
                            .findIYqueAnnexPeriodByMsgId(iYqueUserCode.getId());

                    if(CollectionUtil.isNotEmpty(oldIYqueAnnexPeriod)){
                        iYqueAnnexPeriodService.deleteIYqueAnnexPeriodByMsgId(iYqueUserCode.getId());
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
                iYqueMsgAnnexService.deleteIYqueMsgAnnexByMsgId(iYqueUserCode.getId());
                List<IYqueMsgAnnex> annexLists = iYqueUserCode.getAnnexLists();
                if(CollectionUtil.isNotEmpty(annexLists)){
                    annexLists.stream().forEach(k->{
                        k.setMsgId(iYqueUserCode.getId());
                    });
                    iYqueMsgAnnexService.saveAll(annexLists);
                }

            }

        }catch (Exception e){

            throw e;
        }

    }

    @Override
    public IYqueUserCode findIYqueUserCodeById(Long id) {
        return iYqueUserCodeDao.getById(id);
    }

    @Override
    public void batchDelete(Long[] ids){
        List<IYqueUserCode> iYqueUserCodes = iYqueUserCodeDao.findAllById(Arrays.asList(ids));

        if(CollectionUtil.isNotEmpty(iYqueUserCodes)){
            iYqueUserCodes.stream().forEach(k->{
                k.setDelFlag(IYqueContant.DEL_STATE);

                try {
                    iYqueConfigService.findWxcpservice().getExternalContactService().deleteContactWay(k.getConfigId());
                    iYqueUserCodeDao.saveAndFlush(k);

                }catch (Exception e){
                   log.error("活码删除失败:"+e.getMessage());
                }

            });

        }

    }

    @Override
    public void distributeUserCode(Long id) throws Exception {

        try {
            IYqueUserCode iYqueUserCode = findIYqueUserCodeById(id);
            if(iYqueUserCode != null){
                IYqueConfig iYqueConfig = iYqueConfigService.findIYqueConfig();
                if(null != iYqueConfig){
                    WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
                    NewArticle newArticle=new NewArticle();
                    newArticle.setDescription("渠道活码,点击保存即可");
                    newArticle.setTitle(iYqueUserCode.getCodeName());


                    if(StrUtil.isNotEmpty(iYqueUserCode.getCodeUrl())){
                        if (iYqueUserCode.getCodeUrl().startsWith("http://") || iYqueUserCode.getCodeUrl().startsWith("https://")){
                            newArticle.setUrl(iYqueUserCode.getCodeUrl());
                            newArticle.setPicUrl(iYqueUserCode.getCodeUrl());
                        }else {
                            newArticle.setUrl(iYqueParamConfig.getFileViewUrl()+iYqueUserCode.getCodeUrl());
                            newArticle.setPicUrl(iYqueParamConfig.getFileViewUrl()+iYqueUserCode.getCodeUrl());

                        }
                    }


                    wxcpservice.getMessageService().send(WxCpMessage.NEWS()
                            .toUser(iYqueUserCode.getUserId().replace(",", "|"))
                            .agentId(new Integer(iYqueConfig.getAgentId()))
                            .addArticle(newArticle)
                            .build());
                }

            }
        }catch (Exception e){
           throw e;
        }




    }

}
