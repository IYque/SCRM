package cn.iyque.dao;

import cn.iyque.entity.IYqueCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface IYqueCategoryDao extends JpaRepository<IYqueCategory,Long>, JpaSpecificationExecutor<IYqueCategory> {


}
