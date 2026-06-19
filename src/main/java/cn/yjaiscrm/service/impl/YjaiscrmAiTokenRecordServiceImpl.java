package cn.iyque.service.impl;

import cn.iyque.dao.IYqueAiTokenRecordDao;
import cn.iyque.entity.IYqueAiTokenRecord;
import cn.iyque.service.IYqueAiTokenRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class IYqueAiTokenRecordServiceImpl implements IYqueAiTokenRecordService {

    @Autowired
    private IYqueAiTokenRecordDao aiTokenRecordDao;


    @Override
    public Long getTotalTokensToday() {
        Specification<IYqueAiTokenRecord> spec = IYqueAiTokenRecord.hasCreateTimeToday();
        return aiTokenRecordDao.findAll(spec).stream()
                .mapToLong(IYqueAiTokenRecord::getTotalTokens)
                .sum();
    }

    @Override
    public void save(IYqueAiTokenRecord tokenRecord) {
        aiTokenRecordDao.save(tokenRecord);
    }
}
