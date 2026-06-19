package cn.iyque.service;

import cn.iyque.entity.IYqueMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IYqueMaterialService {

    /**
     * 新增或编辑
     * @param material
     */
    void saveOrUpdate(IYqueMaterial material);



    /**
     * 素材列表
     * @param iYqueMaterial
     * @param pageable
     * @return
     */
    Page<IYqueMaterial> findAll(IYqueMaterial iYqueMaterial, Pageable pageable);


    /**
     * 删除
     * @param ids
     */
    void batchDelete(Long[] ids);
}
