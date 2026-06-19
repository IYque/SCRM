package cn.iyque.dao;

import cn.iyque.entity.IYqueUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IYqueUserDao extends JpaRepository<IYqueUser,Long> ,JpaSpecificationExecutor<IYqueUser> {


    List<IYqueUser> findIYqueUserByUserId(String userId);


    List<IYqueUser> findByUserId(String userId);

    @Query("SELECT u FROM cn.iyque.entity.IYqueUser u WHERE u.userId IN :userIds")
    List<IYqueUser> findByUserIds(@Param("userIds") List<String> userIds);
}
