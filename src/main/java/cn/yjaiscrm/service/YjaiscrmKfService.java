package cn.iyque.service;

import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.entity.IYqueKf;
import cn.iyque.exception.IYqueException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IYqueKfService {



    /**
     * 获取客服列表
     * @param iYqueKf
     * @param pageable
     * @return
     */
    Page<IYqueKf> findAll(IYqueKf iYqueKf, Pageable pageable);


    /**
     * 处理回调的客服信息
     * @param callBackBaseMsg
     */
    void handleKfMsg( IYqueCallBackBaseMsg callBackBaseMsg) throws Exception;





    /**
     * 新建或更新客服
     * @param iYqueKf
     */
    void saveOrUpdateKf(IYqueKf iYqueKf) throws IYqueException;





    /**
     * 删除客服
     * @param ids
     */
    void batchDelete(List<Long> ids);


}
