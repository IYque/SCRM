package cn.iyque.service;

import cn.iyque.entity.IYqueCategory;
import cn.iyque.entity.IYqueUserCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IYqueCategoryService {


    /**
     * 获取分类列表
     * @return
     */
    List<IYqueCategory> findAll(IYqueCategory iYqueCategory);


    /**
     * 新增或编辑分类
     * @param iYqueCategory
     */
    void saveOrUpdate(IYqueCategory iYqueCategory);


    /**
     * 删除分类
     * @param ids
     */
    void batchDelete(Long[] ids);
}
