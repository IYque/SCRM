package cn.iyque.dao;

import cn.iyque.entity.IYqueMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueMaterialDao  extends JpaRepository<IYqueMaterial,Long>, JpaSpecificationExecutor<IYqueMaterial> {


}
