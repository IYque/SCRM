package cn.iyque.dao;

import cn.iyque.entity.IYqueShortLink;
import cn.iyque.entity.IYqueUserCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IYqueShortLinkDao extends JpaRepository<IYqueShortLink,Long> {

    IYqueShortLink findByCodeState(String codeState);
}
