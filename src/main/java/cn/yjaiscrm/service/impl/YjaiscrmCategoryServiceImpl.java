package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueCategoryDao;
import cn.iyque.domain.IYQueCustomerInfo;
import cn.iyque.entity.IYqueCategory;
import cn.iyque.enums.MediaType;
import cn.iyque.service.IYqueCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class IYqueCategoryServiceImpl implements IYqueCategoryService {

    @Autowired
    private IYqueCategoryDao iYqueCategoryDao;

    @Override
    public List<IYqueCategory> findAll(IYqueCategory iYqueCategory) {

        Specification<IYqueCategory> spec = Specification.where(null);


        List<IYqueCategory> iYqueCategories=ListUtil.toList(IYqueCategory.builder()
                        .id(0L)
                        .name("默认分类")
                .build());

            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("mediaType")), StringUtils.isNotEmpty(iYqueCategory.getMediaType())?iYqueCategory.getMediaType():
                    MediaType.C.getKey()));


        List<IYqueCategory> all = iYqueCategoryDao.findAll(spec,Sort.by("createTime").descending());

         if(CollectionUtil.isNotEmpty(all)){
             iYqueCategories.addAll(all);
         }


        return iYqueCategories;
    }

    @Override
    public void saveOrUpdate(IYqueCategory iYqueCategory) {
        if(iYqueCategory.getId() != null){
            iYqueCategory.setUpdateTime(new Date());
        }else{
            iYqueCategory.setCreateTime(new Date());
            iYqueCategory.setUpdateTime(new Date());
        }
        iYqueCategoryDao.saveAndFlush(iYqueCategory);
    }

    @Override
    public void batchDelete(Long[] ids) {

        List<IYqueCategory> iYqueCategorys = iYqueCategoryDao.findAllById(Arrays.asList(ids));

        if(CollectionUtil.isNotEmpty(iYqueCategorys)){
            iYqueCategorys.stream().forEach(k->{
                k.setDelFlag(IYqueContant.DEL_STATE);

                try {
                    iYqueCategoryDao.saveAndFlush(k);

                }catch (Exception e){
                    log.error("分类删除失败:"+e.getMessage());
                }

            });

        }

    }
}
