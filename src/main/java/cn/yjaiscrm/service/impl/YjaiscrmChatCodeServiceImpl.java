package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iyque.constant.CodeStateConstant;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueChatCodeDao;
import cn.iyque.entity.IYqueChat;
import cn.iyque.entity.IYqueChatCode;
import cn.iyque.service.IYqueChatCodeService;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.utils.SnowFlakeUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.bean.external.WxCpGroupJoinWayInfo;
import me.chanjar.weixin.cp.bean.external.WxCpGroupJoinWayResult;
import me.chanjar.weixin.cp.bean.external.WxCpUserExternalGroupChatInfo;
import me.chanjar.weixin.cp.bean.external.WxCpUserExternalGroupChatList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class IYqueChatCodeServiceImpl implements IYqueChatCodeService {

    @Autowired
    private IYqueConfigService iYqueConfigService;

    @Autowired
    private IYqueChatCodeDao iYqueChatCodeDao;


    @Override
    public Page<IYqueChatCode> findAll(Pageable pageable) {
        return iYqueChatCodeDao.findAll(pageable);
    }


    @Override
    public void createChatCode(IYqueChatCode iYqueChatCode) throws Exception {

        try {

            List<IYqueChat> iYqueChatList = iYqueChatCode.getYqueChatList();

            if(CollectionUtil.isNotEmpty(iYqueChatList)){


                iYqueChatCode.setCreateTime(new Date());
                iYqueChatCode.setUpdateTime(new Date());
                iYqueChatCode.setChatCodeState(CodeStateConstant.CHAT_CODE_STATE+ SnowFlakeUtils.nextId());

                WxCpService wxcpservice = iYqueConfigService.findWxcpservice();

                WxCpGroupJoinWayInfo joinWayInfo=new WxCpGroupJoinWayInfo();
                WxCpGroupJoinWayInfo.JoinWay joinWay=new WxCpGroupJoinWayInfo.JoinWay();
                joinWay.setScene(2);
                joinWay.setRemark(iYqueChatCode.getRemark());
                joinWay.setAutoCreateRoom(iYqueChatCode.getAutoCreateRoom());
                joinWay.setRoomBaseName(iYqueChatCode.getRoomBaseName());
                joinWay.setRoomBaseId(iYqueChatCode.getRoomBaseId());
                joinWay.setChatIdList(iYqueChatList.stream().map(IYqueChat::getChatId).collect(Collectors.toList()));
                joinWay.setState(iYqueChatCode.getChatCodeState());
                joinWayInfo.setJoinWay(joinWay);
                WxCpGroupJoinWayResult wxCpGroupJoinWayResult = wxcpservice.getExternalContactService().addJoinWay(joinWayInfo);

                if(null != wxCpGroupJoinWayResult
                        && StrUtil.isNotEmpty(wxCpGroupJoinWayResult.getConfigId())
                ){
                    iYqueChatCode.setConfigId(wxCpGroupJoinWayResult.getConfigId());
                    //获取入群二维码地址
                    WxCpGroupJoinWayInfo wxCpGroupJoinWayInfo
                            = wxcpservice.getExternalContactService().getJoinWay(wxCpGroupJoinWayResult.getConfigId());

                    if(wxCpGroupJoinWayInfo != null){
                        WxCpGroupJoinWayInfo.JoinWay joinWay1 = wxCpGroupJoinWayInfo.getJoinWay();

                        if(null != joinWay1){
                            iYqueChatCode.setChatCodeUrl(joinWay1.getQrCode());
                        }

                    }



                    iYqueChatCodeDao.save(iYqueChatCode);

                }
            }



        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public void updateChatCode(IYqueChatCode iYqueChatCode) throws Exception {
        IYqueChatCode oldIYqueChatCode =
                iYqueChatCodeDao.findById(iYqueChatCode.getId()).get();
        if(null != oldIYqueChatCode){

            if(iYqueChatCode.getAutoCreateRoom().equals(new Integer(0))){
                iYqueChatCode.setRoomBaseName(null);
                iYqueChatCode.setRoomBaseId(null);
            }

            List<IYqueChat> iYqueChatList = iYqueChatCode.getYqueChatList();

            if(CollectionUtil.isNotEmpty(iYqueChatList)){
                WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
                WxCpGroupJoinWayInfo joinWayInfo=new WxCpGroupJoinWayInfo();
                WxCpGroupJoinWayInfo.JoinWay joinWay=new WxCpGroupJoinWayInfo.JoinWay();
                joinWay.setConfigId(oldIYqueChatCode.getConfigId());
                joinWay.setScene(2);
                joinWay.setRemark(iYqueChatCode.getRemark());
                joinWay.setAutoCreateRoom(iYqueChatCode.getAutoCreateRoom());

                if(StringUtils.isNotEmpty(iYqueChatCode.getRoomBaseName())){
                    joinWay.setRoomBaseName(iYqueChatCode.getRoomBaseName());
                }

                if(iYqueChatCode.getRoomBaseId() != null){
                    joinWay.setRoomBaseId(iYqueChatCode.getRoomBaseId());
                }


                joinWay.setChatIdList(iYqueChatList.stream().map(IYqueChat::getChatId).collect(Collectors.toList()));
                joinWay.setState(oldIYqueChatCode.getChatCodeState());
                joinWayInfo.setJoinWay(joinWay);
                WxCpBaseResp wxCpBaseResp = wxcpservice.getExternalContactService().updateJoinWay(joinWayInfo);
                if(null != wxCpBaseResp
                        &&IYqueContant.WECHAT_API_SUCCESS.equals(wxCpBaseResp.getErrcode().intValue())){
                    oldIYqueChatCode.setChatCodeName(iYqueChatCode.getChatCodeName());
                    oldIYqueChatCode.setRemark(iYqueChatCode.getRemark());
                    oldIYqueChatCode.setAutoCreateRoom(iYqueChatCode.getAutoCreateRoom());
                    oldIYqueChatCode.setRoomBaseId(iYqueChatCode.getRoomBaseId());
                    oldIYqueChatCode.setRoomBaseName(iYqueChatCode.getRoomBaseName());
                    oldIYqueChatCode.setUpdateTime(new Date());
                    iYqueChatCodeDao.saveAndFlush(oldIYqueChatCode);
                }

            }

        }


    }


    @Override
    public void batchDelete(Long[] ids) {
        List<IYqueChatCode> iYqueChatCodes = iYqueChatCodeDao.findAllById(Arrays.asList(ids));
        if(CollectionUtil.isNotEmpty(iYqueChatCodes)){

            iYqueChatCodes.stream().forEach(k->{
                k.setDelFlag(IYqueContant.DEL_STATE);

                try {
                    WxCpBaseResp wxCpBaseResp
                            = iYqueConfigService.findWxcpservice().getExternalContactService().deleteContactWay(k.getConfigId());

                    if(null != wxCpBaseResp
                            &&IYqueContant.WECHAT_API_SUCCESS.equals(wxCpBaseResp.getErrcode().intValue())){
                        iYqueChatCodeDao.saveAndFlush(k);

                    }


                }catch (Exception e){
                    log.error("智能群码删除失败:"+e.getMessage());
                }

            });

        }

    }
}
