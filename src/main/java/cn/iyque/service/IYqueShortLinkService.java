package cn.iyque.service;

import cn.iyque.domain.IYqueKvalStrVo;
import cn.iyque.entity.IYqueShortLink;
import cn.iyque.entity.IYqueUserCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IYqueShortLinkService {


    Page<IYqueShortLink> findAll(Pageable pageable);

    void save(IYqueShortLink shortLink) throws Exception;


    List<IYqueKvalStrVo> findIYqueShorkLinkKvs();


    void update(IYqueShortLink shortLink) throws Exception;

    IYqueShortLink findIYqueShortLinkById(Long id);

    void batchDelete(Long[] ids);
}
