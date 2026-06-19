package cn.iyque.service;

import cn.iyque.entity.IYQueScript;
import cn.iyque.entity.IYqueMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IYQueScriptService {

    /**
     * 新增或编辑组合话术
     * @param queScript
     */
    void addOrUpdate(IYQueScript queScript);



    /**
     * 删除
     * @param ids
     */
    void batchDelete(Long[] ids);



    /**
     * 组合话术列表
     * @param iyQueScript
     * @param pageable
     * @return
     */
    Page<IYQueScript> findAll(IYQueScript iyQueScript, Pageable pageable);
}
