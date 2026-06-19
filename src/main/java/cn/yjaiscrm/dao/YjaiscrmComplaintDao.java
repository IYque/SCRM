package cn.iyque.dao;

import cn.iyque.entity.IYQueComplain;
import cn.iyque.entity.IYqueChatCode;
import cn.iyque.entity.IYqueUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYQueComplaintDao extends JpaRepository<IYQueComplain,Long>  , JpaSpecificationExecutor<IYQueComplain> {

}
