package cn.iyque.service;

import cn.iyque.entity.IYqueAiTokenRecord;

public interface IYqueAiTokenRecordService {

    Long getTotalTokensToday();


    void save(IYqueAiTokenRecord tokenRecord);
}
