package cn.iyque.service;


import cn.iyque.domain.IYqueCustomerSeasVo;
import cn.iyque.entity.IYqueCustomerSeas;
import cn.iyque.entity.IYqueUser;
import cn.iyque.exception.IYqueException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IYqueCustomerSeasService {


    /**
     * 读取excel，公海数据入库
     * @param file
     */
    void importData(List<IYqueUser> allocateUsers, MultipartFile file);


    /**
     * 修改公海客户状态
     * @param customerSeas
     */
    void updateCustomerSeasState(IYqueCustomerSeas customerSeas);



    /**
     * 客户公海列表
     * @param iYqueCustomerSeas
     * @param pageable
     * @return
     */
    Page<IYqueCustomerSeas> findAll(IYqueCustomerSeas iYqueCustomerSeas, Pageable pageable);



    /**
     * 客户公海删除
     * @param ids
     */
    void batchDelete(Long[] ids);


    /**
     * 下发通知
     * @param ids
     * @throws Exception
     */
    void distribute(Long[] ids) throws IYqueException;
}
