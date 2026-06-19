package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.chain.vectorstore.IYqueVectorStore;
import cn.iyque.dao.IYqueKnowledgeFragmentDao;
import cn.iyque.entity.IYqueKnowledgeAttach;
import cn.iyque.entity.IYqueKnowledgeFragment;
import cn.iyque.service.IYqueEmbeddingService;
import cn.iyque.service.IYqueKnowledgeFragmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class IYqueKnowledgeFragmentServiceImpl implements IYqueKnowledgeFragmentService {

    @Autowired
    private IYqueKnowledgeFragmentDao yqueKnowledgeFragmentDao;

    @Autowired
    private IYqueEmbeddingService yqueEmbeddingService;


    @Autowired
    private IYqueVectorStore iYqueVectorStore;


    @Override
    public Page<IYqueKnowledgeFragment> findAll(IYqueKnowledgeFragment iYqueKnowledgeInfo, Pageable pageable) {
        Specification<IYqueKnowledgeFragment> spec = Specification.where(null);

        if(iYqueKnowledgeInfo.getDocId() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("docId"), iYqueKnowledgeInfo.getDocId()));
        }



        return   yqueKnowledgeFragmentDao.findAll(spec, pageable);
    }

    @Override
    public List<IYqueKnowledgeFragment> nearest(String content, String kid) {
        List<IYqueKnowledgeFragment> fragments=new ArrayList<>();

        //向量库检索相关数据
        List<String> nearest = iYqueVectorStore
                .nearest(yqueEmbeddingService.getQueryVector(content,kid),kid);

        //从数据库获取片段内容
        if(CollectionUtil.isNotEmpty(nearest)){
            List<IYqueKnowledgeFragment> fragmentList = this.findAllByIds(nearest.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toList()));
           if(CollectionUtil.isNotEmpty(fragmentList)){
               fragments=fragmentList;
           }
        }



        return fragments;
    }

    @Override
    public List<IYqueKnowledgeFragment> findAllByIds(List<Long> ids) {
        return yqueKnowledgeFragmentDao.findAllById(ids);
    }
}
