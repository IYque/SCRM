package cn.iyque.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.dao.IYqueH5MarketDao;
import cn.iyque.dao.IYqueH5MarketRecordDao;
import cn.iyque.entity.IYqueH5Market;
import cn.iyque.entity.IYqueH5MarketRecord;
import cn.iyque.entity.IYqueUserCode;
import cn.iyque.service.IYqueH5MarketService;
import cn.iyque.utils.FileUtils;
import cn.iyque.utils.IpUtils;
import cn.iyque.utils.SnowFlakeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;


@Service
@Slf4j
public class IYqueH5MarketServiceImpl implements IYqueH5MarketService {

    @Autowired
    private IYqueParamConfig iYqueParamConfig;

    @Autowired
    private IYqueH5MarketDao iYqueH5MarketDao;

    @Autowired
    private IYqueH5MarketRecordDao iYqueH5MarketRecordDao;


    @Override
    public IYqueH5Market addOrUpdate(IYqueH5Market iYqueH5Market) throws Exception {

        iYqueH5Market.setId(null == iYqueH5Market.getId()? SnowFlakeUtils.nextId(): iYqueH5Market.getId());
        iYqueH5Market.setH5Url(MessageFormat.format(iYqueParamConfig.getH5MarketUrl(),iYqueH5Market.getId().toString()));
        iYqueH5Market.setCreateBy(iYqueParamConfig.getUserName());
        if(null == iYqueH5Market.getId()){
            iYqueH5Market.setCreateTime(new Date());
            iYqueH5Market.setUpdateTime(new Date());
        }else{
            iYqueH5Market.setUpdateTime(new Date());
        }
        String qrCodeUrl = FileUtils.generateQRCode(iYqueH5Market.getH5Url());
        if(StrUtil.isNotEmpty(qrCodeUrl)){
            iYqueH5Market.setH5QrUrl(qrCodeUrl);
        }
        iYqueH5MarketDao.saveAndFlush(iYqueH5Market);
        return iYqueH5Market;
    }

    @Override
    public Page<IYqueH5Market> findAll(IYqueH5Market iYqueH5Market, Pageable pageable) {
        Specification<IYqueH5Market> spec = Specification.where(null);


        // 按活动名称搜索
        if (StringUtils.hasText(iYqueH5Market.getName())) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + iYqueH5Market.getName().toLowerCase() + "%"));
        }

        return iYqueH5MarketDao.findAll(spec, pageable);

    }

    @Override
    public void batchDelete(Long[] ids) {
        iYqueH5MarketDao.deleteAllByIdInBatch(Arrays.asList(ids));

    }

    @Override
    public IYqueH5Market findWeH5MarketById(Long id) {
        IYqueH5Market iYqueH5Market=new IYqueH5Market();
        Optional<IYqueH5Market> optional = iYqueH5MarketDao.findById(id);

        if(optional.isPresent()){
            iYqueH5Market=optional.get();

            iYqueH5MarketRecordDao.save(IYqueH5MarketRecord.builder()
                    .h5MarketId(id)
                    .viewIp(IpUtils.getHostIp())
                    .createTime(new Date())
                    .build());
        }
        return iYqueH5Market;
    }
}
