package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.dao.IYQueScriptDao;
import cn.iyque.dao.IYQueScriptSubDao;
import cn.iyque.entity.IYQueScript;
import cn.iyque.entity.IYQueScriptSub;
import cn.iyque.service.IYQueScriptService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IYQueScriptServiceImpl implements IYQueScriptService {


    @Autowired
    IYQueScriptSubDao queScriptSubDao;


    @Autowired
    IYQueScriptDao queScriptDao;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdate(IYQueScript queScript) {
        IYQueScript saveScript = queScriptDao.saveAndFlush(queScript);

        List<IYQueScriptSub> scriptSubs = queScript.getScriptSubs();

        if(CollectionUtil.isNotEmpty(scriptSubs)){
            scriptSubs.stream().forEach(k->{
                k.setScriptId(saveScript.getId());
                k.prePersist(k);
            });

            queScriptSubDao.deleteByIdIsNotIn(scriptSubs.stream()
                    .map(IYQueScriptSub::getId)
                    .collect(Collectors.toList()));

            queScriptSubDao.saveAll(scriptSubs);

        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(Long[] ids) {
        queScriptDao.deleteAllByIdInBatch(Arrays.asList(ids));

//        queScriptSubDao.deleteByScriptIdIs(Arrays.asList(ids));

    }

    @Override
    public Page<IYQueScript> findAll(IYQueScript iyQueScript, Pageable pageable) {
        Specification<IYQueScript> spec = Specification.where(null);

        if(StringUtils.isNotEmpty(iyQueScript.getTitle())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + iyQueScript.getTitle().toLowerCase() + "%"));

        }

        if (StringUtils.isNotEmpty(iyQueScript.getCategoryId())) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("categoryId")), iyQueScript.getCategoryId()));
        }

        Page<IYQueScript> iyQueScripts = queScriptDao.findAll(spec, pageable);
        if(CollectionUtil.isNotEmpty(iyQueScripts)){

            iyQueScripts.stream().forEach(item->{

                List<IYQueScriptSub> iyQueScriptSubs = queScriptSubDao.findByScriptId(item.getId());

                if(CollectionUtil.isNotEmpty(iyQueScripts)){
                    item.setScriptSubs(iyQueScriptSubs);
                    item.setScriptNumber(iyQueScriptSubs.size());
                }

            });

        }



        return iyQueScripts;
    }


}
