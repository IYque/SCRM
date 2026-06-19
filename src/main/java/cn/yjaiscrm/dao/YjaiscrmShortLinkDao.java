package cn.iyque.dao;

import cn.iyque.entity.IYqueShortLink;
import cn.iyque.entity.IYqueUserCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueShortLinkDao extends JpaRepository<IYqueShortLink,Long>, JpaSpecificationExecutor<IYqueShortLink> {

    IYqueShortLink findByCodeState(String codeState);

    IYqueShortLink findByConfigId(String configId);
}
