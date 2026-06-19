package cn.iyque.service.impl;

import cn.iyque.dao.IYqueKnowledgeAttachDao;
import cn.iyque.dao.IYqueKnowledgeFragmentDao;
import cn.iyque.entity.IYqueKnowledgeAttach;
import cn.iyque.entity.IYqueKnowledgeInfo;
import cn.iyque.service.IYqueKnowledgeAttachService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class IYqueKnowledgeAttachServiceImpl implements IYqueKnowledgeAttachService {

    @Autowired
    private IYqueKnowledgeAttachDao yqueKnowledgeAttachDao;


    @Autowired
    private IYqueKnowledgeFragmentDao yqueKnowledgeFragmentDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeKnowledgeAttach(Long docId) {
        yqueKnowledgeAttachDao.deleteById(docId);
        yqueKnowledgeFragmentDao.deleteByDocId(docId);
    }

    @Override
    public Page<IYqueKnowledgeAttach> findAll(IYqueKnowledgeAttach iYqueKnowledgeInfo, Pageable pageable) {
        Specification<IYqueKnowledgeAttach> spec = Specification.where(null);

        if(iYqueKnowledgeInfo.getKid() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("kid"), iYqueKnowledgeInfo.getKid()));
        }



        return   yqueKnowledgeAttachDao.findAll(spec, pageable);
    }
}
