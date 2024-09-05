package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iyque.dao.IYqueDefaultMsgDao;
import cn.iyque.entity.IYqueAnnexPeriod;
import cn.iyque.entity.IYqueDefaultMsg;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.entity.IYquePeriodMsgAnnex;
import cn.iyque.service.IYqueAnnexPeriodService;
import cn.iyque.service.IYqueDefaultMsgService;
import cn.iyque.service.IYqueMsgAnnexService;
import cn.iyque.service.IYquePeriodMsgAnnexService;
import cn.iyque.utils.DateUtils;
import cn.iyque.utils.SnowFlakeUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.external.WxCpWelcomeMsg;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;
import me.chanjar.weixin.cp.bean.external.msg.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class IYqueDefaultMsgImpl implements IYqueDefaultMsgService {

    @Autowired
    IYqueDefaultMsgDao iYqueDefaultMsgDao;


    @Autowired
    IYqueMsgAnnexService iYqueMsgAnnexService;


    @Autowired
    IYqueAnnexPeriodService iYqueAnnexPeriodService;


    @Autowired
    IYquePeriodMsgAnnexService iYquePeriodMsgAnnexService;





    @Override
    public IYqueDefaultMsg findDefaultMsg() {
        List<IYqueDefaultMsg> iYqueDefaultMsgs = iYqueDefaultMsgDao.findAll();
        if(CollectionUtil.isEmpty(iYqueDefaultMsgs)){
            return new IYqueDefaultMsg();
        }
        IYqueDefaultMsg iYqueDefaultMsg = iYqueDefaultMsgs.stream().findFirst().get();

        if(iYqueDefaultMsg.isStartPeriodAnnex()){
            iYqueDefaultMsg.setPeriodAnnexLists(
                    iYqueAnnexPeriodService.findIYqueAnnexPeriodByMsgId(iYqueDefaultMsg.getId())
            );
        }else{
            iYqueDefaultMsg.setAnnexLists(
                    iYqueMsgAnnexService.findIYqueMsgAnnexByMsgId(iYqueDefaultMsg.getId())
            );
        }

        return iYqueDefaultMsg;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(IYqueDefaultMsg iYqueDefaultMsg) {
        iYqueDefaultMsgDao.saveAndFlush(iYqueDefaultMsg);

        if(iYqueDefaultMsg.isStartPeriodAnnex()){
            List<IYqueAnnexPeriod> periodAnnexLists = iYqueDefaultMsg.getPeriodAnnexLists();
            if(CollectionUtil.isNotEmpty(periodAnnexLists)){
                //时段附件
                List<IYquePeriodMsgAnnex> iYquePeriodMsgAnnexes=new ArrayList<>();
                periodAnnexLists.stream().forEach(k->{
                    k.setMsgId(iYqueDefaultMsg.getId());
                    List<IYquePeriodMsgAnnex> periodMsgAnnexList = k.getPeriodMsgAnnexList();
                    if(CollectionUtil.isNotEmpty(periodMsgAnnexList)){
                        periodMsgAnnexList.stream().forEach(periodMsgAnnex->{
                            periodMsgAnnex.setAnnexPeroidId(k.getId());
                        });
                        iYquePeriodMsgAnnexes.addAll(periodMsgAnnexList);
                    }
                });

                //存储时段
                List<IYqueAnnexPeriod> oldIYqueAnnexPeriod = iYqueAnnexPeriodService
                        .findIYqueAnnexPeriodByMsgId(iYqueDefaultMsg.getId());

                if(CollectionUtil.isNotEmpty(oldIYqueAnnexPeriod)){
                    iYqueAnnexPeriodService.deleteIYqueAnnexPeriodByMsgId(iYqueDefaultMsg.getId());
                    iYquePeriodMsgAnnexService.deleteAllByAnnexPeroidIdIn(
                            oldIYqueAnnexPeriod.stream().map(IYqueAnnexPeriod::getId).collect(Collectors.toList())
                    );
                }

                iYqueAnnexPeriodService.saveAll(periodAnnexLists);
                if(CollectionUtil.isNotEmpty(iYquePeriodMsgAnnexes)){
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


            }


        }else{
            List<IYqueMsgAnnex> annexLists = iYqueDefaultMsg.getAnnexLists();
            iYqueMsgAnnexService.deleteIYqueMsgAnnexByMsgId(iYqueDefaultMsg.getId());
            if(CollectionUtil.isNotEmpty(annexLists)){
                annexLists.stream().forEach(k->{k.setMsgId(iYqueDefaultMsg.getId());});
                iYqueMsgAnnexService.saveAll(annexLists);
            }

        }

    }

    @Override
    public void setDefaultMsg(WxCpWelcomeMsg wxCpWelcomeMsg, Text text) {

        IYqueDefaultMsg defaultMsg = this.findDefaultMsg();
        if(null != defaultMsg){

            if(defaultMsg.isStartPeriodAnnex()){//时段欢迎语
                List<IYqueAnnexPeriod> periodAnnexLists = defaultMsg.getPeriodAnnexLists();
                if(CollectionUtil.isNotEmpty(periodAnnexLists)){
                    // 使用filter来筛选符合条件的周期
                    List<IYqueAnnexPeriod> filteredPeriods = periodAnnexLists.stream()
                            .filter(k -> StrUtil.isNotEmpty(k.getWorkCycle()) &&
                                    StrUtil.isNotEmpty(k.getBeginTime()) &&
                                    StrUtil.isNotEmpty(k.getEndTime()) &&
                                    DateUtils.isWorkCycle(k.getWorkCycle()) &&
                                    DateUtils.isCurrentTimeInRange(k.getBeginTime(), k.getEndTime()))
                            .collect(Collectors.toList());

                    if(CollectionUtil.isNotEmpty(filteredPeriods)){
                        IYqueAnnexPeriod iYqueAnnexPeriod = filteredPeriods.stream().findFirst().get();
                        //文字欢迎语
                        if(StrUtil.isNotEmpty(iYqueAnnexPeriod.getWeclomeMsg())){
                            text.setContent(iYqueAnnexPeriod.getWeclomeMsg());
                        }
                        //欢迎语附件
                        List<IYquePeriodMsgAnnex> periodMsgAnnexList = iYqueAnnexPeriod.getPeriodMsgAnnexList();
                        if(CollectionUtil.isNotEmpty(periodMsgAnnexList)){
                            wxCpWelcomeMsg.setAttachments(SpringUtil.getBean(IYquePeriodMsgAnnexService.class)
                                    .msgAnnexToAttachment(periodMsgAnnexList));
                        }
                    }




                }



            }else{ //非时段欢迎语

                text.setContent(defaultMsg.getDefaultContent());
                List<IYqueMsgAnnex> annexLists = defaultMsg.getAnnexLists();
                if(CollectionUtil.isNotEmpty(annexLists)){
                    List<Attachment> attachments = SpringUtil.getBean(IYqueMsgAnnexService.class)
                            .msgAnnexToAttachment(annexLists);
                    wxCpWelcomeMsg.setAttachments(attachments);
                }
            }

        }

    }


}
