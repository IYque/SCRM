package cn.iyque.mass.sender;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.entity.IYqueChat;
import cn.iyque.entity.IYqueGroupMsg;
import cn.iyque.entity.IYqueGroupMsgSub;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.enums.GroupMsgSendStatus;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueChatService;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueMsgAnnexService;
import cn.iyque.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.external.WxCpMsgTemplate;
import me.chanjar.weixin.cp.bean.external.WxCpMsgTemplateAddResult;
import me.chanjar.weixin.cp.bean.external.msg.Text;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 客群群发
 */
@Slf4j
public class GroupMassSender extends AbstractMassSender{


    @Override
    protected void prepareTarget(IYqueGroupMsg iYqueGroupMsg) {

        //如果群发目标群为全部群发,则查询出相关群做设置
        if(iYqueGroupMsg.getScopeType()
                .equals(new Integer(0))){//全部
            List<IYqueChat> allIYqueChat =
                    SpringUtils.getBean(IYqueChatService.class).findAllIYqueChat();

            if(CollectionUtil.isNotEmpty(allIYqueChat)){
                List<IYqueGroupMsgSub> groupMsgSubList=new ArrayList<>();
                allIYqueChat.stream().forEach(k->{
                    groupMsgSubList.add(
                            IYqueGroupMsgSub.builder()
                                    .groupMsgId(iYqueGroupMsg.getId())
                                    .acceptType(2)
                                    .acceptId(k.getChatId())
                                    .acceptName(k.getChatName())
                                    .senderId(k.getOwner())
                                    .status(GroupMsgSendStatus.GROUP_MSG_TYPE_WFS.getStatus())
                                    .statusSub(GroupMsgSendStatus.GROUP_MSG_TYPE_WFS.getStatusSub())
                                    .build()
                    );
                });
                iYqueGroupMsg.setGroupMsgSubList(groupMsgSubList);
            }
        }else{//部分

            List<IYqueGroupMsgSub> groupMsgSubList = iYqueGroupMsg.getGroupMsgSubList();
            if(CollectionUtil.isNotEmpty(groupMsgSubList)){
                groupMsgSubList.stream().forEach(item->{
                    item.setStatus(GroupMsgSendStatus.GROUP_MSG_TYPE_WFS.getStatus());
                    item.setStatusSub(GroupMsgSendStatus.GROUP_MSG_TYPE_WFS.getStatusSub());
                });
            }

        }


    }



    @Override
    protected void send(IYqueGroupMsg iYqueGroupMsg) throws IYqueException{

        List<IYqueGroupMsgSub> groupMsgSubList
                = iYqueGroupMsg.getGroupMsgSubList();
        if(CollectionUtil.isNotEmpty(groupMsgSubList)){
            WxCpMsgTemplate wxCpMsgTemplate=new WxCpMsgTemplate();
            wxCpMsgTemplate.setChatType(iYqueGroupMsg.getChatType());

            //文本
            Text text = new Text();
            text.setContent(iYqueGroupMsg.getContent());
            wxCpMsgTemplate.setText(text);

            //其他附件
            List<IYqueMsgAnnex> annexLists = iYqueGroupMsg.getAnnexLists();
            if(CollectionUtil.isNotEmpty(annexLists)){
                annexLists.stream().forEach(kk->{
                    kk.setMsgId(iYqueGroupMsg.getId());
                });
                wxCpMsgTemplate.setAttachments(
                        SpringUtils.getBean(IYqueMsgAnnexService.class).msgAnnexToAttachment(annexLists)
                );
            }

           iYqueGroupMsg.getGroupMsgSubList().stream()
                    .collect(Collectors.groupingBy(IYqueGroupMsgSub::getSenderId)).forEach((k,v)->{

                        //群发的群目标
                       wxCpMsgTemplate.setChatIdList(v
                               .stream().map(IYqueGroupMsgSub::getAcceptId)
                               .collect(Collectors.toList()));

                       //执行人
                       wxCpMsgTemplate.setSender(
                               k
                       );

                       try {
                           WxCpMsgTemplateAddResult result = SpringUtils.getBean(IYqueConfigService.class).findWxcpservice().getExternalContactService()
                                   .addMsgTemplate(wxCpMsgTemplate);



                           v.stream().forEach(kk->{


                               kk.setGroupMsgId(iYqueGroupMsg.getId());

                               if(StringUtils.isNotEmpty(result.getMsgId())){
                                   kk.setMsgId(result.getMsgId());
                                   kk.setStatus(GroupMsgSendStatus.GROUP_MSG_TYPE_WFS.getStatus());
                                   kk.setStatusSub(GroupMsgSendStatus.GROUP_MSG_TYPE_WFS.getStatusSub());
                                   List<String> failList = result.getFailList();
                                   if(CollectionUtil.isNotEmpty(failList)){
                                       if(failList.contains(kk.getAcceptId())){
                                           kk.setStatus(GroupMsgSendStatus.GROUP_MSG_TYPE_WX.getStatus());
                                           kk.setStatusSub(GroupMsgSendStatus.GROUP_MSG_TYPE_WX.getStatusSub());
                                       }
                                   }
                               }else{
                                   kk.setStatus(GroupMsgSendStatus.GROUP_MSG_TYPE_FAIL.getStatus());
                                   kk.setStatusSub(GroupMsgSendStatus.GROUP_MSG_TYPE_FAIL.getStatusSub());

                               }

                           });


                       }catch (Exception e){

                             log.error("群发任务调用api失败:"+e.getMessage());
                             throw new IYqueException(e.getMessage());

                       }

                   });
        }





    }
}
