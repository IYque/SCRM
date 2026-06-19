package cn.iyque.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iyque.chain.vectorstore.IYqueVectorStore;
import cn.iyque.constant.WxFileType;
import cn.iyque.dao.IYqueKfDao;
import cn.iyque.dao.IYqueKfMsgDao;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.entity.*;
import cn.iyque.enums.KfServiceState;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.*;
import cn.iyque.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpKfService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.bean.kf.*;
import me.chanjar.weixin.cp.bean.kf.msg.WxCpKfEventMsg;
import me.chanjar.weixin.cp.bean.kf.msg.WxCpKfLinkMsg;
import me.chanjar.weixin.cp.bean.kf.msg.WxCpKfResourceMsg;
import me.chanjar.weixin.cp.bean.kf.msg.WxCpKfTextMsg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@SuppressWarnings("all")
public class IYqueKfServiceImpl implements IYqueKfService {

    @Autowired
    private IYqueConfigService iYqueConfigService;


    @Autowired
    private IYqueAiService iYqueAiService;




    @Autowired
    private IYqueKfMsgDao kfMsgDao;


    @Autowired
    private IYqueKfDao kfDao;





    @Autowired
    private IYqueKfMsgService yqueKfMsgService;



    @Autowired
    private IYqueVectorStore iYqueVectorStore;


    @Autowired
    private IYqueEmbeddingService yqueEmbeddingService;



    @Autowired
    private IYqueKnowledgeFragmentService yqueKnowledgeFragmentService;



    private final String aiKftpl= "问题:%s\n"
            +"参考内容:\n"
            +"%s\n"
            +"请根据以上问题和参考内容，生成简洁准确的回答。\n"
            +"要求如下:\n"
            +"1.须严格根据我给你的系统上下文内容原文进行回答;\n"
            +"2.请不要自己发挥,回答时保持原来文本的段落层级;\n"
            +"3.如果没有用户需要的相关内容，则返回无相关内容。";



    @Override
    public Page<IYqueKf> findAll(IYqueKf iYqueKf, Pageable pageable) {
        Specification<IYqueKf> spec = Specification.where(null);

        //知识库名称
        if(StringUtils.isNotEmpty(iYqueKf.getKfName())){
            spec = spec.and((root, query, cb) -> cb.like(root.get("kfName"), "%" + iYqueKf.getKfName() + "%"));
        }

        if(iYqueKf.getKfType() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("kfType"), iYqueKf.getKfType()));
        }


        return   kfDao.findAll(spec, pageable);
    }

    @Override
    public void handleKfMsg(IYqueCallBackBaseMsg callBackBaseMsg){

        try {
            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();

            if(null != wxcpservice){

                WxCpKfService kfService = wxcpservice.getKfService();

                if(null != kfService){


                        IYqueKf iyqueKf = kfDao.findByOpenKfid(callBackBaseMsg.getOpenKfId());


                        if(null != iyqueKf){
                            StringBuilder dataCursor=new StringBuilder();


                            //获取上一次的cursor
                            IYqueKfMsg kfMsg
                                    = kfMsgDao.findTopByOpenKfidOrderByPullTimeDesc(callBackBaseMsg.getOpenKfId());

                            if(null != kfMsg){
                                dataCursor.append(kfMsg.getMessageCursor());
                            }

                            //获取客服消息
                            WxCpKfMsgListResp wxCpKfMsgListResp = wxcpservice.getKfService()
                                    .syncMsg(dataCursor.toString(), callBackBaseMsg.getToken(), null, null,
                                            callBackBaseMsg.getOpenKfId());

                            if(null != wxCpKfMsgListResp){
                                kfMsg=IYqueKfMsg.builder()
                                        .messageCursor(wxCpKfMsgListResp.getNextCursor())
                                        .openKfid(callBackBaseMsg.getOpenKfId())
                                        .pullTime(new Date())
                                        .build();
                                kfMsgDao.save(
                                        kfMsg
                                );
                            }


                            log.info("拉取的客服数据:"+ JSONUtil.toJsonStr(wxCpKfMsgListResp));




                            if( wxCpKfMsgListResp.getHasMore().equals(new Integer(0))){

                                List<WxCpKfMsgListResp.WxCpKfMsgItem> msgList =
                                        wxCpKfMsgListResp.getMsgList();




                                if(CollectionUtil.isNotEmpty(msgList)){



                                    if(new Integer(2).equals(iyqueKf.getKfType())){ //排班客服逻辑


                                           this.handleShiftKf(wxCpKfMsgListResp,iyqueKf,callBackBaseMsg);


                                    }else if(new Integer(1).equals(iyqueKf.getKfType())){//基础客服逻辑




                                           this.handleAiBaseKf(msgList,iyqueKf,callBackBaseMsg,kfService);

                                    }






                                }
                            }

                        }else{
                            log.error("当前客服并非通过源雀scrm创建");
                        }




                    }

                    }


        }catch (Exception e){
            log.error("回复客户相关信息失败:"+e.getMessage());
        }
    }

    private void handleAiBaseKf(List<WxCpKfMsgListResp.WxCpKfMsgItem> msgList,IYqueKf iyqueKf,IYqueCallBackBaseMsg callBackBaseMsg,   WxCpKfService kfService) throws Exception {


        //获取发送欢迎语
        List<WxCpKfMsgListResp.WxCpKfMsgItem> weclomeEntitys = msgList.stream().filter(item -> StringUtils.isEmpty(item.getExternalUserId()))
                .collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(weclomeEntitys)){

            weclomeEntitys.stream().forEach(kk->{
                WxCpKfEventMsg event = kk.getEvent();
                if(null != event && StringUtils.isNotEmpty(event.getWelcomeCode())){


                    WxCpKfMsgSendRequest sendRequest=new WxCpKfMsgSendRequest();
                    sendRequest.setCode(event.getWelcomeCode());
                    sendRequest.setMsgType(IYqueMsgAnnex.MsgType.MSG_TEXT);
                    WxCpKfTextMsg textMsg=new WxCpKfTextMsg();
                    textMsg.setContent(iyqueKf.getWelcomeMsg());
                    sendRequest.setText(textMsg);
                    //发送转接欢迎语
                    try {
                        iYqueConfigService.findWxcpservice().getKfService().sendMsgOnEvent(sendRequest);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }


            });

        }




        //非欢迎语处理逻辑
        List<WxCpKfMsgListResp.WxCpKfMsgItem> noWeclomeEntitys = msgList.stream().filter(item -> StringUtils.isNotEmpty(item.getExternalUserId()))
                .collect(Collectors.toList());



        if(CollectionUtil.isNotEmpty(noWeclomeEntitys)){
            Map<String, List<WxCpKfMsgListResp.WxCpKfMsgItem>> listMap = noWeclomeEntitys.stream()
                    .collect(Collectors.groupingBy(WxCpKfMsgListResp.WxCpKfMsgItem::getExternalUserId));
            for (String k : listMap.keySet()) {


                //是否人工
                boolean isArtificial=false;


                //判断消息类型
                WxCpKfMsgListResp.WxCpKfMsgItem lastItem
                        =  listMap.get(k).get( listMap.get(k).size() - 1);





                //获取会话状态,如果为人工则，不进行下一步操作
                WxCpKfServiceStateResp serviceState = iYqueConfigService.findWxcpservice().getKfService()
                        .getServiceState(callBackBaseMsg.getOpenKfId(), k);

                if(serviceState.success()&&KfServiceState.KF_SERVICE_STATE_RGJD.getState().equals(serviceState.getServiceState())){
                    isArtificial=true;
                }

                //客户会话信息入库
                yqueKfMsgService.saveIYqueKfMsg(iyqueKf,lastItem,isArtificial);

                if(!isArtificial){
                    //非人工操作
                    try {
                        //文本类型
                        if(lastItem.getMsgType().equals(IYqueMsgAnnex.MsgType.MSG_TEXT)){

                            if(StringUtils.isNotEmpty(iyqueKf.getKid())){

                                //检索相似数据片段
                                List<IYqueKnowledgeFragment> nearest = yqueKnowledgeFragmentService
                                        .nearest(lastItem.getText().getContent(), iyqueKf.getKid());

                                if(CollUtil.isNotEmpty(nearest)){

                                    String prompt = String.format(aiKftpl,lastItem.getText().getContent(), nearest.stream()
                                            .map(s -> "— " + s)
                                            .collect(Collectors.joining(System.lineSeparator())));
                                    log.info("客服自动会话提示词:"+prompt);


                                    this.sendAiKfMsg(kfService,prompt
                                            ,k, callBackBaseMsg.getOpenKfId(),true);


                                }else{//为空则，按照客服设定的规则响应


                                    switch (iyqueKf.getSwitchType()){
                                        case 1://文字
                                            this.sendKfMsg(IYqueMsgAnnex.MsgType.MSG_TEXT,kfService,iyqueKf.getSwitchText(),k, callBackBaseMsg.getOpenKfId());
                                            break;
                                        case 2://转人工回复
                                            this.kfTransferPersonnel(callBackBaseMsg.getOpenKfId(),  lastItem.getExternalUserId(),iyqueKf.getSwitchUserIds(),iyqueKf.getSwitchUserNames());
                                            break;
                                        case 3: //发布外部联系人二维码
                                            this.sendKfMsg(IYqueMsgAnnex.MsgType.MSG_TYPE_LINK,kfService,iyqueKf.getSwichQrUrl(),k, callBackBaseMsg.getOpenKfId());
                                            break;
                                        case 4: //ai大模型直接回复
                                            this.sendAiKfMsg(kfService,lastItem.getText().getContent(),k, callBackBaseMsg.getOpenKfId(),true);
                                            break;
                                    }



                                }


                            }


                            log.info("文本类型客户消息:"+lastItem.getText().getContent());
                            //非文本类型基于提示
                        }else {
                            this.sendAiKfMsg(kfService,"不支持当前类型消息,请发送文字消息。",k, callBackBaseMsg.getOpenKfId(),false);

                            log.info("其他类型文本消息:"+lastItem);
                        }
                    }catch (Exception e){

                        log.error("消息发送失败:"+e.getMessage());

                    }
                }


            }
        }

    }



    private void handleShiftKf( WxCpKfMsgListResp wxCpKfMsgListResp,IYqueKf iyqueKf,IYqueCallBackBaseMsg callBackBaseMsg) throws Exception {


        //发送欢迎语
        wxCpKfMsgListResp.getMsgList().stream().forEach(kk->{
            WxCpKfEventMsg event = kk.getEvent();
            if(null != event && StringUtils.isNotEmpty(event.getWelcomeCode())){

                String receptionDesk=null;

                if(StringUtils.isNotEmpty(iyqueKf.getWorkCycle())
                        &&IYqueKf.isInWorkingTime(iyqueKf)){

                    receptionDesk=iyqueKf.getWelcomeMsg();
                }

                WxCpKfMsgSendRequest sendRequest=new WxCpKfMsgSendRequest();
                sendRequest.setCode(event.getMsgCode());
                sendRequest.setMsgType(IYqueMsgAnnex.MsgType.MSG_TEXT);
                WxCpKfTextMsg textMsg=new WxCpKfTextMsg();
                textMsg.setContent(StringUtils.isNotEmpty(receptionDesk)?receptionDesk:iyqueKf.getOorWelcome());
                sendRequest.setText(textMsg);

                //发送转接欢迎语
                try {
                    iYqueConfigService.findWxcpservice().getKfService().sendMsgOnEvent(sendRequest);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        });



    }


    @Override
    public void saveOrUpdateKf(IYqueKf iYqueKf) {


        try {

            iYqueKf.setUpdateTime(new Date());
            //上传头像获取头像临时素材id
            WxMediaUploadResult uploadResult = iYqueConfigService.findWxcpservice().getMediaService().upload(WxFileType.IMAGE, FileUtils.downloadImage(
                    iYqueKf.getKfPicUrl()
            ));


            if(StringUtils.isEmpty(uploadResult.getMediaId())){
                throw new IYqueException("头像上传失败");
            }


            if(iYqueKf.getId() == null){//新建


                    //构建客服账号
                    WxCpKfAccountAdd accountAdd=new WxCpKfAccountAdd();
                    accountAdd.setMediaId(uploadResult.getMediaId());
                    accountAdd.setName(iYqueKf.getKfName());
                    WxCpKfAccountAddResp wxCpKfAccountAddResp = iYqueConfigService.findWxcpservice().getKfService().addAccount(accountAdd);
                    if(StringUtils.isEmpty(wxCpKfAccountAddResp.getOpenKfid())){
                        throw new IYqueException("客服创建失败");
                    }
                    iYqueKf.setOpenKfid(wxCpKfAccountAddResp.getOpenKfid());


                  //添加接待人员
                if(iYqueKf.getKfType().equals(1)){
                    if(iYqueKf.getSwitchType().equals(new Integer(2))&&StringUtils.isNotEmpty(iYqueKf.getSwitchUserIds())){
                        iYqueConfigService.findWxcpservice().getKfService().addServicer(wxCpKfAccountAddResp.getOpenKfid(),
                                Arrays.asList(iYqueKf.getSwitchUserIds().split(",")));
                    }
                }else{
                    if(iYqueKf.getKfType().equals(2)){
                        if(StringUtils.isNotEmpty(iYqueKf.getSwitchUserIds())){
                            iYqueConfigService.findWxcpservice().getKfService().addServicer(wxCpKfAccountAddResp.getOpenKfid(),
                                    Arrays.asList(iYqueKf.getSwitchUserIds().split(",")));
                        }
                    }
                }


                //获取客服链接
                WxCpKfAccountLink accountLink=new WxCpKfAccountLink();
                accountLink.setOpenKfid(iYqueKf.getOpenKfid());
                WxCpKfAccountLinkResp accountLinkResp = iYqueConfigService.findWxcpservice().getKfService().getAccountLink(accountLink);
                if(StringUtils.isNotEmpty(accountLinkResp.getUrl())){
                    iYqueKf.setKfUrl(accountLinkResp.getUrl());
                    //客服链接转二维码
                    iYqueKf.setKfQrUrl(
                            FileUtils.generateQRCode(accountLinkResp.getUrl())
                    );

                }

                //存储入库
                kfDao.save(iYqueKf);
            }else{//更新

                //更新客服账号
                if(StringUtils.isNotEmpty(iYqueKf.getOpenKfid())&&StringUtils.isNotEmpty(iYqueKf.getKfUrl())){
                    WxCpKfAccountUpd cpKfAccountUpd=new WxCpKfAccountUpd();
                    cpKfAccountUpd.setMediaId(uploadResult.getMediaId());
                    cpKfAccountUpd.setName(iYqueKf.getKfName());
                    cpKfAccountUpd.setOpenKfid(iYqueKf.getOpenKfid());
                    WxCpBaseResp wxCpBaseResp = iYqueConfigService.findWxcpservice().getKfService().updAccount(cpKfAccountUpd);
                    if(!wxCpBaseResp.success()){
                        throw new IYqueException("客服更新失败");
                    }

                }



                //保证接待人员最终一致性
                if(iYqueKf.getSwitchType().equals(new Integer(2))&&StringUtils.isNotEmpty(iYqueKf.getSwitchUserIds())){
                    //删除接待人员
                    iYqueConfigService.findWxcpservice()
                            .getKfService().delServicer(iYqueKf.getOpenKfid(), Arrays.asList(iYqueKf.getSwitchUserIds().split(",")));
                    //重新添加接待人员
                    iYqueConfigService.findWxcpservice().getKfService().addServicer(iYqueKf.getOpenKfid(),
                            Arrays.asList(iYqueKf.getSwitchUserIds().split(",")));
                }


                kfDao.saveAndFlush(iYqueKf);

            }
        }catch (Exception e){
            String errorMsg=e.getMessage();
            if(e instanceof WxErrorException){
                errorMsg = ((WxErrorException) e).getError().getErrorMsg();
            }
            log.error("客服新增或编辑异常:"+errorMsg);
            throw new IYqueException(errorMsg);
        }



    }



    @Override
    public void batchDelete(List<Long> ids) {

        if(CollectionUtil.isNotEmpty(ids)){

            List<IYqueKf> iYqueKfs = kfDao.findAllById(ids);

            if(CollectionUtil.isNotEmpty(iYqueKfs)){
                iYqueKfs.stream().forEach(k->{

                    try {
                        WxCpKfAccountDel accountDel=new WxCpKfAccountDel();
                        accountDel.setOpenKfid(k.getOpenKfid());
                        WxCpBaseResp wxCpBaseResp = iYqueConfigService.findWxcpservice().getKfService().delAccount(accountDel);
                        if(wxCpBaseResp.success()){
                            kfDao.deleteById(k.getId());
                        }
                    }catch (Exception e){
                        log.error("客服删除失败:"+e.getMessage());
                    }

                });
            }

        }

    }


    /**
     * ai输入msg,回复
     * @param kfService
     * @param content
     * @param toUser
     * @param openKfid
     * @param isAi
     * @throws Exception
     */
    public void sendAiKfMsg( WxCpKfService kfService,String content,String toUser,String openKfid,boolean isAi) throws Exception {


        StringBuilder resContent=new StringBuilder();


        if(isAi){

            resContent.append(
                    iYqueAiService.aiHandleCommonContent(content)
            );

            log.info("ai大模型处理后回复的内容:"+resContent.toString());

        }else{

            resContent.append(content);
        }



        this.sendKfMsg(
                IYqueMsgAnnex.MsgType.MSG_TEXT,kfService,resContent.toString(),toUser,openKfid
        );


    }


    /**
     * 客服转接人工
     * @param openKfid 客服id
     * @param externalUserid 客服id
     * @param servicerUserid 接待人员id
     * @param servicerUserName 接待人员名称
     */
    public void kfTransferPersonnel(String openKfid,String externalUserid,String servicerUserid,String servicerUserName){

        try {

            WxCpKfServiceStateTransResp transResp
                    = iYqueConfigService.findWxcpservice().getKfService().transServiceState(openKfid, externalUserid, KfServiceState.KF_SERVICE_STATE_RGJD.getState(), servicerUserid);
            if(transResp.success()){
                if(StringUtils.isNotEmpty(transResp.getMsgCode())){
                    WxCpKfMsgSendRequest sendRequest=new WxCpKfMsgSendRequest();
                    sendRequest.setCode(transResp.getMsgCode());
                    sendRequest.setMsgType(IYqueMsgAnnex.MsgType.MSG_TEXT);

                    WxCpKfTextMsg textMsg=new WxCpKfTextMsg();
                    textMsg.setContent("当前信息AI客服无法处理,已转接人工客服["+servicerUserName+"],为您服务");
                    sendRequest.setText(textMsg);

                    //发送转接欢迎语
                    iYqueConfigService.findWxcpservice().getKfService().sendMsgOnEvent(sendRequest);

                }
            }

        }catch (Exception e){

            log.error("客服转接人工失败:"+e.getMessage());
        }

    }


    /**
     * 发送客服消息
     * @param msgType
     * @param kfService
     * @param content
     * @param toUser
     * @param openKfid
     * @throws Exception
     */
    private void sendKfMsg(String msgType,WxCpKfService kfService,String content,String toUser,String openKfid)throws Exception {

        WxCpKfMsgSendRequest sendRequest=new WxCpKfMsgSendRequest();
        sendRequest.setToUser(toUser);
        sendRequest.setOpenKfid(openKfid);

        //发送文本内容
        if(msgType.equals(IYqueMsgAnnex.MsgType.MSG_TEXT)){
            sendRequest.setMsgType(IYqueMsgAnnex.MsgType.MSG_TEXT);
            WxCpKfTextMsg textMsg=new WxCpKfTextMsg();
            textMsg.setContent(content);
            sendRequest.setText(textMsg);
        //发送图文链接
        }else if(msgType.equals(IYqueMsgAnnex.MsgType.MSG_TYPE_LINK)){

            sendRequest.setMsgType(IYqueMsgAnnex.MsgType.MSG_TYPE_LINK);
//            WxCpKfResourceMsg resourceMsg=new WxCpKfResourceMsg();
            WxCpKfLinkMsg wxCpKfLinkMsg=new WxCpKfLinkMsg();
            WxMediaUploadResult uploadResult = iYqueConfigService.findWxcpservice().getMediaService().upload(WxFileType.IMAGE, FileUtils.downloadImage(
                    content
            ));


            if(StringUtils.isEmpty(uploadResult.getMediaId())){
                throw new IYqueException("回复内容上传失败");
            }
            wxCpKfLinkMsg.setTitle("添加员工企微");
            wxCpKfLinkMsg.setDesc("当前AI客服无法处理,请添加员工企微");
            wxCpKfLinkMsg.setUrl(content);
            wxCpKfLinkMsg.setThumb_media_id(uploadResult.getMediaId());
//            resourceMsg.setMediaId(uploadResult.getMediaId());

//            sendRequest.setImage(resourceMsg);



            sendRequest.setLink(wxCpKfLinkMsg);
        }




        WxCpKfMsgSendResp wxCpKfMsgSendResp = kfService.sendMsg(sendRequest);
        log.info("客服消息发送:"+wxCpKfMsgSendResp);
    }



}
