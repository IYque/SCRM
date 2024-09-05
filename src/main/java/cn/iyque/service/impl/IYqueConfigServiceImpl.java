package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.dao.IYqueConfigDao;
import cn.iyque.entity.IYqueConfig;
import cn.iyque.service.IYqueConfigService;
import me.chanjar.weixin.cp.api.WxCpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class IYqueConfigServiceImpl implements IYqueConfigService {

    @Autowired
    IYqueConfigDao iYqueConfigDao;
    @Override
    public IYqueConfig findIYqueConfig() {
        List<IYqueConfig> iYqueConfigs = iYqueConfigDao.findAll();
        if(CollectionUtil.isEmpty(iYqueConfigs)){
            return new IYqueConfig();
        }
        return iYqueConfigs.stream().findFirst().get();
    }

    @Override
    public void saveOrUpdate(IYqueConfig iYqueConfig) {
        iYqueConfigDao.saveAndFlush(iYqueConfig);
    }

    @Override
    public WxCpService findWxcpservice() throws Exception {

        WxCpService config = WxCpServiceFactory.createWxCpService(findIYqueConfig());
        if(null == config){
            throw new Exception("请配置系统应用参数");
        }

        return config;
    }
}
