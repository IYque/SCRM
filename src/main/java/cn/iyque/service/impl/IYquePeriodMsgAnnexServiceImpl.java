package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.converter.AttachmentConverter;
import cn.iyque.converter.AttachmentConverterFactory;
import cn.iyque.dao.IYquePeriodMsgAnnexDao;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.entity.IYquePeriodMsgAnnex;
import cn.iyque.service.IYquePeriodMsgAnnexService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class IYquePeriodMsgAnnexServiceImpl implements IYquePeriodMsgAnnexService {

    @Autowired
    private IYquePeriodMsgAnnexDao iYquePeriodMsgAnnexDao;

    @Override
    public void saveAll(List<IYquePeriodMsgAnnex> annexLists) {
        iYquePeriodMsgAnnexDao.saveAll(annexLists);
    }

    @Override
    public void deleteAllByAnnexPeroidIdIn(List<Long> annexPeroidIds) {
        iYquePeriodMsgAnnexDao.deleteAllByAnnexPeroidIdIn(annexPeroidIds);
    }

    @Override
    public List<Attachment> msgAnnexToAttachment(List<IYquePeriodMsgAnnex> annexList) {

        List<Attachment> attachments = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(annexList)) {
            try {
                annexList.stream().forEach(annex -> {
                    AttachmentConverter converter = AttachmentConverterFactory.getConverter(annex.getMsgtype());

                    IYqueMsgAnnex iYqueMsgAnnex=IYqueMsgAnnex.builder()
                            .annexContent(annex.getAnnexContent())
                            .msgtype(annex.getMsgtype())
                            .image(annex.getImage())
                            .file(annex.getFile())
                            .video(annex.getVideo())
                            .link(annex.getLink())
                            .miniprogram(annex.getMiniprogram())
                            .build();

                    Attachment attachment = converter.convert(iYqueMsgAnnex);
                    if (attachment != null) {
                        attachments.add(attachment);
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return attachments;
    }

}
