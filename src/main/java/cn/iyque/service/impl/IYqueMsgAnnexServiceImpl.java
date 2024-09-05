package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.converter.AttachmentConverter;
import cn.iyque.converter.AttachmentConverterFactory;
import cn.iyque.dao.IYqueMsgAnnexDao;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueMsgAnnexService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class IYqueMsgAnnexServiceImpl implements IYqueMsgAnnexService {

    @Autowired
    IYqueMsgAnnexDao iYqueMsgAnnexDao;


    @Autowired
    IYqueConfigService iYqueConfigService;


    @Override
    public List<IYqueMsgAnnex> findIYqueMsgAnnexByMsgId(Long msgId) {
        return iYqueMsgAnnexDao.findIYqueMsgAnnexByMsgId(msgId);
    }

    @Override
    public void deleteIYqueMsgAnnexByMsgId(Long msgId) {
        iYqueMsgAnnexDao.deleteIYqueMsgAnnexByMsgId(msgId);
    }

    @Override
    public void saveAll(List<IYqueMsgAnnex> annexLists) {
        iYqueMsgAnnexDao.saveAll(annexLists);
    }

    //附件转化为企微附件
    @Override
    public List<Attachment> msgAnnexToAttachment(List<IYqueMsgAnnex> annexList) {
        List<Attachment> attachments = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(annexList)) {
            try {
                annexList.stream().forEach(annex -> {
                    AttachmentConverter converter = AttachmentConverterFactory.getConverter(annex.getMsgtype());
                    Attachment attachment = converter.convert(annex);
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
