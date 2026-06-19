package cn.iyque.service;

import cn.iyque.entity.IYqueH5Market;
import cn.iyque.entity.IYqueUserCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IYqueH5MarketService {
    /**
     * 新增或编辑H5营销
     * @param iYqueH5Market
     * @return
     */
    IYqueH5Market addOrUpdate(IYqueH5Market iYqueH5Market) throws Exception;

    /**
     * 获取列表
     * @param iYqueH5Market
     * @param pageable
     * @return
     */
    Page<IYqueH5Market> findAll(IYqueH5Market iYqueH5Market, Pageable pageable);


    /**
     * 删除
     * @param ids
     */
    void batchDelete(Long[] ids);


    /**
     * 移动端获取详情
     * @param id
     * @return
     */
    IYqueH5Market findWeH5MarketById(Long id);
}
