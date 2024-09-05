package cn.iyque.service;

import cn.iyque.domain.IYqueKvalStrVo;
import cn.iyque.domain.IYqueKvalVo;
import cn.iyque.entity.IYqueUserCode;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IYqueUserCodeService {

    Page<IYqueUserCode> findAll(Pageable pageable);

    void save(IYqueUserCode product) throws Exception;

    List<IYqueKvalStrVo> findIYqueUserCodeKvs();

    void update(IYqueUserCode iYqueUserCode) throws Exception;

    IYqueUserCode findIYqueUserCodeById(Long id);

    void batchDelete(Long[] ids);

    void distributeUserCode(Long id) throws Exception;

}
