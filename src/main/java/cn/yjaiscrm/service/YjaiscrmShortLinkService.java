package cn.iyque.service;

import cn.iyque.domain.IYqueKvalStrVo;
import cn.iyque.entity.IYqueShortLink;
import cn.iyque.entity.IYqueUserCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IYqueShortLinkService {


    Page<IYqueShortLink> findAll(Pageable pageable);

    Page<IYqueShortLink> findAll(IYqueShortLink iYqueShortLink, Pageable pageable);


    void save(IYqueShortLink shortLink) throws Exception;


    List<IYqueKvalStrVo> findIYqueShorkLinkKvs();


    void update(IYqueShortLink shortLink) throws Exception;

    IYqueShortLink findIYqueShortLinkById(Long id);

    void batchDelete(Long[] ids);

    /**
     * 同步获客外链
     */
    void synchShortLink();

    /**
     * 同步指定的获客外链
     * @param linkIds 获客外链ID列表
     */
    void synchShortLinkByLinkIds(List<String> linkIds);

    /**
     * 获取所有获客外链的configId列表
     * @return 配置ID列表
     */
    List<IYqueKvalStrVo> getShortLinkConfigIds();

    /**
     * 修复现有数据中configId为空的记录
     */
    void fixEmptyConfigIds();

    /**
     * 测试企业微信API调用
     * @return 测试结果
     */
    Map<String, Object> testWxApi() throws Exception;

    /**
     * 获取获客链接的客户列表
     * @param linkId 获客链接ID
     * @param limit 返回的最大记录数
     * @param cursor 用于分页查询的游标
     * @return 客户列表
     */
    Object getCustomerList(String linkId, Integer limit, String cursor) throws Exception;

    /**
     * 获取获客链接的客户列表（带搜索功能）
     * @param linkId 获客链接ID
     * @param limit 返回的最大记录数
     * @param cursor 用于分页查询的游标
     * @param customerName 客户姓名（可选，用于搜索）
     * @param followUser 跟进人姓名（可选，用于筛选）
     * @param startDate 开始日期（可选，用于时间范围筛选）
     * @param endDate 结束日期（可选，用于时间范围筛选）
     * @return 客户列表
     */
    Object getCustomerList(String linkId, Integer limit, String cursor, String customerName, String followUser, String startDate, String endDate) throws Exception;

    /**
     * 清理重复的获客外链记录
     * @return 清理结果
     */
    Map<String, Object> cleanDuplicateShortLinks();
}
