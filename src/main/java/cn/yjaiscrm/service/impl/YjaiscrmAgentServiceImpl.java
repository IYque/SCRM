package cn.iyque.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.dao.IYqueAgentDao;
import cn.iyque.dao.IYqueAgentSubDao;
import cn.iyque.dao.IYqueUserDao;
import cn.iyque.domain.AgentMsgDto;
import cn.iyque.entity.*;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueAgentService;
import cn.iyque.service.IYqueConfigService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpAgentService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpAgent;
import me.chanjar.weixin.cp.bean.WxCpDepart;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class IYqueAgentServiceImpl implements IYqueAgentService {
    @Autowired
    IYqueAgentDao iYqueAgentDao;

    @Autowired
    IYqueAgentSubDao iYqueAgentSubDao;


    @Autowired
    IYqueConfigService iYqueConfigService;

    @Autowired
    IYqueUserDao iYqueUserDao;



    @Override
    public void addOrUpdate(IYqueAgent iYqueAgent) {
        iYqueAgentDao.saveAndFlush(iYqueAgent);
    }

    @Override
    public void batchDelete(Long[] ids) {
        iYqueAgentDao.deleteAllByIdInBatch(Arrays.asList(ids));
    }

    @Override
    public Page<IYqueAgent> findAll(Pageable pageable) {
        return iYqueAgentDao.findAll(pageable);
    }

    @Override
    public void synchAgent(Long id) {

        try {
            Optional<IYqueAgent> optional = iYqueAgentDao.findById(id);

            if(optional.isPresent()){
                WxCpService wxcpservice = iYqueConfigService
                        .findWxcpservice();
                IYqueAgent iYqueAgent = optional.get();

                WxCpAgent wxCpAgent = wxcpservice
                        .getAgentService().get(iYqueAgent.getAgentId());
                if(null != wxCpAgent){
                    iYqueAgent.setName(
                            wxCpAgent.getName()
                    );

                    iYqueAgent.setLogoUrl(
                            wxCpAgent.getSquareLogoUrl()
                    );

                    //可见范围人员
                    List<WxCpAgent.User> users = wxCpAgent.getAllowUserInfos().getUsers();

                    if(CollectionUtil.isNotEmpty(users)){
                        List<IYqueUser> iYqueUsers = iYqueUserDao.findByUserIds(users.stream().map(WxCpAgent.User::getUserId).
                                collect(Collectors.toList()));
                        if(CollectionUtil.isNotEmpty(iYqueUsers)) {
                            iYqueAgent.setAllowUserinfoName(
                                    iYqueUsers.stream()
                                            .map(IYqueUser::getName)
                                            .collect(Collectors.joining(", "))
                            );
                        }
                    }else{
                        iYqueAgent.setAllowUserinfoName(null);
                    }

                    //可见范围部门
                    List<Long> partyIds = wxCpAgent.getAllowParties().getPartyIds();

                    if(CollectionUtil.isNotEmpty(partyIds)){
                        List<WxCpDepart> wxCpDeparts=new ArrayList<>();
                        partyIds.stream().forEach(k->{
                            try {
                                WxCpDepart wxCpDepart = wxcpservice.getDepartmentService().get(k);

                                if(null != wxCpDepart){
                                    wxCpDeparts.add(wxCpDepart);
                                }

                            } catch (WxErrorException e) {
                                throw new RuntimeException(e);
                            }
                        });

                        if(CollectionUtil.isNotEmpty(wxCpDeparts)){
                            iYqueAgent.setAllowPartyName(
                                    wxCpDeparts.stream()
                                            .map(WxCpDepart::getName)
                                            .collect(Collectors.joining(", "))
                            );
                        }
                    }
                }else{

                    iYqueAgent.setAllowPartyName(null);

                }


                iYqueAgentDao.saveAndFlush(iYqueAgent);

            }
        }catch (Exception e){
            log.error("应用信息同步错误："+e.getMessage());
            throw new IYqueException(e.getMessage());
        }




    }

    @Override
    public Page<IYqueAgentSub> findAgentSubAll(Integer agentId, Pageable pageable) {
        Specification<IYqueAgentSub> spec = Specification.where(null);


        if (agentId != null) {
            IYqueAgent agent = iYqueAgentDao.findByAgentId(agentId);
            if(null != agent){
                spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("agentId")), agent.getId()));
            }
        }
        return iYqueAgentSubDao.findAll(spec,pageable);
    }

    @Override
    public void sendAgentMsg(IYqueAgent iYqueAgent) throws Exception {
        IYqueAgent agent = iYqueAgentDao.findByAgentId(iYqueAgent.getAgentId());
        if(null != agent){
            List<IYqueAgentSub> iYqueAgentSubs = iYqueAgent.getAgentSub();
            if(CollectionUtil.isNotEmpty(iYqueAgentSubs)){
                IYqueAgentSub iYqueAgentSub = iYqueAgentSubs.stream().findFirst().get();
                iYqueAgentSub.setMsgTitle(iYqueAgent.getMsgTitle());
                iYqueAgentSub.setAgentId(agent.getId());
                iYqueAgentSub.setStatus(3);
                iYqueAgentSub.prePersist(iYqueAgentSub);
                iYqueAgentSub.setSendTime(new Date());

                WxCpMessage wxCpMessage = AgentMsgDto.buildAgentMsg(iYqueAgentSub);
                if(null != wxCpMessage){
                    //全部成员
                    if(new Integer(1).equals(iYqueAgentSub.getScopeType())){
                        wxCpMessage.setToUser("@all");
                    }else{
                        if(CollectionUtil.isNotEmpty(iYqueAgentSub.getToUser())){
                            wxCpMessage.setToUser(
                                    String.join(", ", iYqueAgentSub.getToUser())
                            );
                        }
                    }

                    wxCpMessage.setAgentId(iYqueAgent.getAgentId());

                    if(StringUtils.isNotEmpty(wxCpMessage.getToUser())&&wxCpMessage.getAgentId() != null){
                        WxCpMessageSendResult sendResult = iYqueConfigService.findWxcpservice().getMessageService().send(wxCpMessage);

                        if(StringUtils.isNotEmpty(sendResult.getMsgId())){
                            iYqueAgentSub.setMsgId(sendResult.getMsgId());
                            iYqueAgentSub.setStatus(2);
                        }
                    }
                }
                iYqueAgentSubDao.saveAll(iYqueAgentSubs);
            }

        }



    }
}
