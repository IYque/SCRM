package cn.iyque.dao;

import cn.iyque.entity.IYQueScriptSub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IYQueScriptSubDao extends JpaRepository<IYQueScriptSub,Long> , JpaSpecificationExecutor<IYQueScriptSub> {

    void deleteByIdIsNotIn(List<Long> ids);

//    void deleteByScriptIdIs(List<Long> ids);

    List<IYQueScriptSub> findByScriptId(Long scriptId);
}
