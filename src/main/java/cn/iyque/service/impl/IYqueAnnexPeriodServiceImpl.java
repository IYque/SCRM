package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.dao.IYqueAnnexPeriodDao;
import cn.iyque.dao.IYquePeriodMsgAnnexDao;
import cn.iyque.entity.IYqueAnnexPeriod;
import cn.iyque.entity.IYquePeriodMsgAnnex;
import cn.iyque.service.IYqueAnnexPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IYqueAnnexPeriodServiceImpl implements IYqueAnnexPeriodService {

    @Autowired
    private IYqueAnnexPeriodDao iYqueAnnexPeriodDao;

    @Autowired
    private IYquePeriodMsgAnnexDao iYquePeriodMsgAnnexDao;
    @Override
    public void saveAll(List<IYqueAnnexPeriod> annexLists) {
        iYqueAnnexPeriodDao.saveAll(annexLists);
    }

    @Override
    public List<IYqueAnnexPeriod> findIYqueAnnexPeriodByMsgId(Long msgId) {

        List<IYqueAnnexPeriod> iYqueAnnexPeriods=new ArrayList<>();
        List<IYqueAnnexPeriod> iYqueAnnexPeriodByMsgId = iYqueAnnexPeriodDao.findIYqueAnnexPeriodByMsgId(msgId);
        if(CollectionUtil.isNotEmpty(iYqueAnnexPeriodByMsgId)){

            iYqueAnnexPeriodByMsgId.stream().forEach(k->{
                List<IYquePeriodMsgAnnex> iYquePeriodMsgAnnexes = iYquePeriodMsgAnnexDao
                        .findIYquePeriodMsgAnnexByAnnexPeroidId(k.getId());
                if(CollectionUtil.isNotEmpty(iYquePeriodMsgAnnexes)){
                    k.setPeriodMsgAnnexList(iYquePeriodMsgAnnexes);
                }
            });

            iYqueAnnexPeriods.addAll(
                    iYqueAnnexPeriodByMsgId
            );
        }





        return iYqueAnnexPeriods;
    }

    @Override
    public void deleteIYqueAnnexPeriodByMsgId(Long msgId) {
        iYqueAnnexPeriodDao.deleteIYqueAnnexPeriodByMsgId(msgId);
    }
}
