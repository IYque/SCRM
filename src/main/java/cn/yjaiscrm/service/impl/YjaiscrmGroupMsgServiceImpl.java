package cn.iyque.service.impl;


import cn.iyque.dao.IYqueGroupMsgDao;
import cn.iyque.dao.IYqueGroupMsgSubDao;
import cn.iyque.entity.IYqueGroupMsg;
import cn.iyque.exception.IYqueException;
import cn.iyque.mass.MassSenderFactoryService;
import cn.iyque.service.IYqueGroupMsgService;
import cn.iyque.service.IYqueMsgAnnexService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;



/**
 * 群发相关
 */
@Service
@Slf4j
public class IYqueGroupMsgServiceImpl implements IYqueGroupMsgService {


    @Autowired
    private IYqueGroupMsgDao iYqueGroupMsgDao;

    @Autowired
    private IYqueGroupMsgSubDao iYqueGroupMsgSubDao;


    @Autowired
    private IYqueMsgAnnexService yqueMsgAnnexService;



    @Autowired
    private MassSenderFactoryService massSenderFactory;

    @Override
    public Page<IYqueGroupMsg> findIYqueGroupMsgPage(IYqueGroupMsg iYqueGroupMsg, Pageable pageable) {
        Specification<IYqueGroupMsg> spec = Specification.where(null);


        if(StringUtils.isNotEmpty(iYqueGroupMsg.getGroupMsgName())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("groupMsgName")), "%" + iYqueGroupMsg.getGroupMsgName() + "%"));

        }

        if (StringUtils.isNotEmpty(iYqueGroupMsg.getContent())) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("content")), "%" + iYqueGroupMsg.getContent() + "%"));
        }

        if (StringUtils.isNotEmpty(iYqueGroupMsg.getChatType())) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("chatType")),  iYqueGroupMsg.getChatType() ));
        }


        return iYqueGroupMsgDao.findAll(spec,pageable);
    }

    @Override
    public IYqueGroupMsg findIYqueGroupMsgById(Long id) {
        IYqueGroupMsg iYqueGroupMsg = iYqueGroupMsgDao.findById(id).get();

        if(null != iYqueGroupMsg){
            //获取群发范围
            iYqueGroupMsg.setGroupMsgSubList(
                    iYqueGroupMsgSubDao.findByGroupMsgId(id)
            );

            //获取附件
            iYqueGroupMsg.setAnnexLists(
                    yqueMsgAnnexService.findIYqueMsgAnnexByMsgId(id)
            );
        }

        return iYqueGroupMsg;
    }

    @Override
    public void buildGroupMsg(IYqueGroupMsg iYqueGroupMsg) throws IYqueException{


        try {
            massSenderFactory.createSender(iYqueGroupMsg.getChatType()).executeMassSend(
                    iYqueGroupMsg
            );
        }catch (IYqueException e){
            throw e;
        }


    }
}
