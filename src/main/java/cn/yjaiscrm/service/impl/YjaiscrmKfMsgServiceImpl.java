package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONUtil;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.dao.IYqueKfMsgSubDao;
import cn.iyque.dao.IYqueSummaryKfMsgDao;
import cn.iyque.domain.IYqueSummaryKfMsgDto;
import cn.iyque.entity.*;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueAiService;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueKfMsgService;
import cn.iyque.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.external.contact.ExternalContact;
import me.chanjar.weixin.cp.bean.kf.WxCpKfCustomerBatchGetResp;
import me.chanjar.weixin.cp.bean.kf.WxCpKfMsgListResp;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class IYqueKfMsgServiceImpl implements IYqueKfMsgService {

    @Autowired
    private IYqueKfMsgSubDao iYqueKfMsgSubDao;


    @Autowired
    private IYqueConfigService iYqueConfigService;


    @Autowired
    private IYqueParamConfig iYqueParamConfig;


    @Autowired
    private IYqueAiService aiService;


    @Autowired
    private IYqueSummaryKfMsgDao iYqueSummaryKfMsgDao;


    @Autowired
    private TransactionTemplate transactionTemplate;






    private final String promptTpl = "任务描述：\n" +
            "分析以下聊天内容，总结每个客户的意图。\n" +
            "聊天内容：\n" +
            "%s\n\n" +
            "分析要求：\n" +
            "1.逐条分析每个客户聊天内容，并一句话总结每个客户的意图。\n" +
            "2.总结内容最多不超过200字。\n" +
            "3.最终输出结果必须严格为以下格式结构化输出之一,不可包含其他内容：\n" +
            "[{\"nickname\":\"客户名称\", \"avatar\":\"客户头像\", \"externalUserId\":\"客户id\", \"unionid\":\"客户unionid\", \"startTime\":\"聊天开始时间\", \"endTime\":\"聊天结束时间\", \"summaryContent\":\"ai生成总结的内容\"}]\n";

    @Override
    @Async
    public void saveIYqueKfMsg(IYqueKf iyqueKf, WxCpKfMsgListResp.WxCpKfMsgItem item,boolean isArtificial) {


        try {

            WxCpKfCustomerBatchGetResp wxCpKfCustomerBatchGetResp = iYqueConfigService.findWxcpservice().getKfService()
                    .customerBatchGet(ListUtil.toList(item.getExternalUserId()));


            if(wxCpKfCustomerBatchGetResp.success()){

                List<ExternalContact> customerList =
                        wxCpKfCustomerBatchGetResp.getCustomerList();

                if(CollectionUtil.isNotEmpty(customerList)){
                    ExternalContact externalContact = customerList.stream().findFirst().get();

                    IYqueKfMsgSub kfMsgSub = IYqueKfMsgSub.builder()
                            .kfMsgId(iyqueKf.getId())
                            .kfName(iyqueKf.getKfName())
                            .kfPicUrl(iyqueKf.getKfPicUrl())
                            .openKfid(item.getOpenKfid())
                            .switchUserId(item.getServicerUserId())
                            .origin(item.getOrigin())
                            .msgId(item.getMsgId())
                            .externalUserId(item.getExternalUserId())
                            .nickname(externalContact.getNickname())
                            .avatar(externalContact.getAvatar())
                            .unionid(externalContact.getUnionId())
                            .gender(externalContact.getGender())
                            .sendTime(new Date(item.getSendTime() * 1000L))
                            .build();
                    //人工，则设置人工信息
                    if(isArtificial){
                        kfMsgSub.setSwitchUserName(iyqueKf.getSwitchUserNames());
                        kfMsgSub.setSwitchUserId(iyqueKf.getSwitchUserIds());
                    }


                    if(item.getMsgType().equals(IYqueMsgAnnex.MsgType.MSG_TEXT)){//文本




                        kfMsgSub.setMsgType(IYqueMsgAnnex.MsgType.MSG_TEXT);
                        kfMsgSub.setContent(item.getText().getContent());



                    }else if(item.getMsgType().equals(IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE)){ //图片

                        kfMsgSub.setMsgType(IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE);

                        kfMsgSub.setContent(
                                FileUtils.mediaToSaveImg(iYqueParamConfig.getUploadDir(),
                                        iYqueConfigService.findWxcpservice().getMediaService().download(item.getImage().getMediaId())
                                        , iYqueParamConfig.getFileViewUrl())
                        );

                    }


                    iYqueKfMsgSubDao.save(kfMsgSub);

                }

            }


        }catch (Exception e){

            log.error("客服回话入库失败:"+e.getMessage());

        }



    }

    @Override
    public Page<IYqueKfMsgSub> findAll(IYqueKfMsgSub iYqueKfMsgSub, Pageable pageable) {
        Specification<IYqueKfMsgSub> spec = Specification.where(null);

        if(StringUtils.isNotEmpty(iYqueKfMsgSub.getKfName())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("kfName")), "%" + iYqueKfMsgSub.getKfName().toLowerCase() + "%"));

        }

        if(StringUtils.isNotEmpty(iYqueKfMsgSub.getNickname())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("nickname")), "%" + iYqueKfMsgSub.getNickname().toLowerCase() + "%"));
        }

        Page<IYqueKfMsgSub> iYqueKfMsgSubs = iYqueKfMsgSubDao.findAll(spec, pageable);

        return iYqueKfMsgSubs;
    }

    @Override
    public List<IYqueSummaryKfMsgDto> findGroupAll() {
        List<IYqueSummaryKfMsgDto> kfMsgDtos=new ArrayList<>();

        List<IYqueKfMsgSub> msgSubs = iYqueKfMsgSubDao.findAll();
        if(CollectionUtil.isNotEmpty(msgSubs)){
            msgSubs.stream()
                    .collect(Collectors.groupingBy(IYqueKfMsgSub::getExternalUserId)).forEach((k,v)->{

                        kfMsgDtos.add(
                                IYqueSummaryKfMsgDto.builder()
                                        .externalUserId(k)
                                        .avatar(v.stream().findFirst().get().getAvatar())
                                        .nickname(v.stream().findFirst().get().getNickname())
                                        .build()
                        );

                    });
        }


        return kfMsgDtos;
    }

    @Override
    public List<IYqueSummaryKfMsgDto> findIYqueSummaryKfMsg(List<String> externalUserIds) {
        List<IYqueKfMsgSub> kfMsgSubs = iYqueKfMsgSubDao.findByExternalUserIdIn(externalUserIds);
        if(CollectionUtil.isNotEmpty(kfMsgSubs)){
          return   kfMsgSubs.stream()
                    // 按客户ID分组
                    .collect(Collectors.groupingBy(IYqueKfMsgSub::getExternalUserId))
                    .entrySet().stream()
                    .map(entry ->{
                        List<IYqueKfMsgSub> messages = entry.getValue();
                        messages.sort(Comparator.comparing(IYqueKfMsgSub::getSendTime));

                        // 获取第一条消息作为基准
                        IYqueKfMsgSub firstMsg = messages.stream().findFirst().get();

                        // 构建DTO对象
                        IYqueSummaryKfMsgDto dto = new IYqueSummaryKfMsgDto();
                        dto.setNickname(firstMsg.getNickname());
                        dto.setAvatar(firstMsg.getAvatar());
                        dto.setExternalUserId(firstMsg.getExternalUserId());
                        dto.setUnionid(firstMsg.getUnionid());
                        dto.setStartTime( messages.get(0).getSendTime());
                        dto.setEndTime(messages.get(messages.size()-1).getSendTime());
                        dto.setKfMsgs(
                                messages.stream()
                                        .map(IYqueKfMsgSub::getContent)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList())
                        );
                        return dto;
                    }).collect(Collectors.toList());



        }


        return new ArrayList<>();
    }

    @Override
    @Async
    public void summaryKfmsgByAi(List<String> externalUserIds) {

        transactionTemplate.execute(status -> {
            try {
                List<IYqueSummaryKfMsgDto> kfMsgDtos=this.findIYqueSummaryKfMsg(externalUserIds);

                if(CollectionUtil.isNotEmpty(kfMsgDtos)){

                    String prompt = String.format(promptTpl, JSONUtil.toJsonStr(kfMsgDtos));

                    log.info("当前聊天内容分析提示词:"+prompt);

                    String result = aiService.aiHandleCommonContent(prompt);

                    log.info("大模型输出原生结果:"+result);
                    if(StringUtils.isNotEmpty(result)) {
                        // 清理字符串：去除 ```json 和换行符
                        String cleanJsonString = result
                                .replace("```json", "")
                                .replace("```", "")
                                .trim();
                        if(StringUtils.isNotEmpty(cleanJsonString)) {
                            List<IYqueSummaryKfMsg> summaryKfMsgs
                                    = JSONUtil.toList(cleanJsonString, IYqueSummaryKfMsg.class);
                            summaryKfMsgs.stream().forEach(k->{
                                k.setCreateTime(new Date());
                            });
                            iYqueSummaryKfMsgDao.deleteByExternalUserIdIn(externalUserIds);
                            iYqueSummaryKfMsgDao.saveAll(summaryKfMsgs);

                        }
                    }
                }else {

                    log.warn("分析内容不存在");
                }
                return true;
            } catch (Exception e) {
                log.error("ai客服客户会话总结异常:"+e.getMessage());
                status.setRollbackOnly();
                throw e;
            }
        });



    }

    @Override
    public Page<IYqueSummaryKfMsg> findSummaryKfMsgs(IYqueSummaryKfMsg summaryKfMsg, Pageable pageable) {
        Specification<IYqueSummaryKfMsg> spec = Specification.where(null);

        if(StringUtils.isNotEmpty(summaryKfMsg.getNickname())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("nickname")), "%" + summaryKfMsg.getNickname().toLowerCase() + "%"));
        }

        return   iYqueSummaryKfMsgDao.findAll(spec, pageable);
    }
}
