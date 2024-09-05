package cn.iyque.service;



import cn.iyque.entity.IYqueAnnexPeriod;

import java.util.List;

public interface IYqueAnnexPeriodService {
    void saveAll( List<IYqueAnnexPeriod> annexLists);

    List<IYqueAnnexPeriod> findIYqueAnnexPeriodByMsgId(Long msgId);


    void deleteIYqueAnnexPeriodByMsgId(Long msgId);




}
