package cn.iyque.service.impl;

import cn.iyque.dao.IYqueMaterialDao;
import cn.iyque.entity.IYqueMaterial;
import cn.iyque.service.IYqueMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;

@Service
@Slf4j
public class IYqueMaterialServiceImpl implements IYqueMaterialService {

    @Autowired
    IYqueMaterialDao yqueMaterialDao;

    @Override
    public void saveOrUpdate(IYqueMaterial material) {
        material.setUpdateTime(new Date());
        material.setCreateTime(new Date());
        material.prePersist(material);
        yqueMaterialDao.saveAndFlush(material);

    }

    @Override
    public Page<IYqueMaterial> findAll(IYqueMaterial iYqueMaterial, Pageable pageable) {
        Specification<IYqueMaterial> spec = Specification.where(null);

        if(StringUtils.isNotEmpty(iYqueMaterial.getTitle())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + iYqueMaterial.getTitle().toLowerCase() + "%"));

        }

        if (StringUtils.isNotEmpty(iYqueMaterial.getCategoryId())) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("categoryId")), iYqueMaterial.getCategoryId()));
        }


        if (StringUtils.isNotEmpty( iYqueMaterial.getMsgtype())) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("msgtype")), iYqueMaterial.getMsgtype()));
        }



        return yqueMaterialDao.findAll(spec, pageable);
    }

    @Override
    public void batchDelete(Long[] ids) {
        yqueMaterialDao.deleteAllByIdInBatch(Arrays.asList(ids));
    }
}
