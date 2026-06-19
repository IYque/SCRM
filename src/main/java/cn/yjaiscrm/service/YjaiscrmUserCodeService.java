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


    Page<IYqueUserCode> findAll(  IYqueUserCode iYqueUserCode, Pageable pageable);


    void save(IYqueUserCode product) throws Exception;

    List<IYqueKvalStrVo> findIYqueUserCodeKvs();

    void update(IYqueUserCode iYqueUserCode) throws Exception;

    IYqueUserCode findIYqueUserCodeById(Long id);

    void batchDelete(Long[] ids);

    void distributeUserCode(Long id) throws Exception;


    /**
     * 同步员工活码（联系我配置）
     */
    void synchUserCode();

    /**
     * 同步指定的员工活码配置
     * @param configIds 配置ID列表
     */
    void synchUserCodeByConfigIds(List<String> configIds);

    /**
     * 获取所有员工活码的configId列表
     * @return 配置ID列表
     */
    List<IYqueKvalStrVo> getUserCodeConfigIds();

}
