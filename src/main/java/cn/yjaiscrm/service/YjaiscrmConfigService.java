package cn.iyque.service;

import cn.iyque.entity.IYqueConfig;
import cn.iyque.exception.IYqueException;
import me.chanjar.weixin.cp.api.WxCpService;

public interface IYqueConfigService {

    IYqueConfig findIYqueConfig();

    void saveOrUpdate(IYqueConfig iYqueConfig);

    WxCpService findWxcpservice() throws Exception;
}
