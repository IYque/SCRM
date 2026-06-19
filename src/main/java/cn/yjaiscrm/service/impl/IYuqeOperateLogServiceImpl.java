package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.dao.IYqueSynchDataRecordDao;
import cn.iyque.dao.IYqueUserDao;
import cn.iyque.dao.IYuqeOperateLogDao;
import cn.iyque.domain.IYQueCustomerInfo;
import cn.iyque.entity.IYqueSynchDataRecord;
import cn.iyque.entity.IYqueUser;
import cn.iyque.entity.IYuqeOperateLog;
import cn.iyque.enums.SynchDataRecordType;
import cn.iyque.service.IYuqeOperateLogService;
import cn.iyque.utils.DateUtils;
import cn.iyque.wxjava.bean.WxOperLogInfo;
import cn.iyque.wxjava.bean.WxOperLogResult;
import cn.iyque.wxjava.service.IYqueWxSecurityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class IYuqeOperateLogServiceImpl implements IYuqeOperateLogService {



    @Autowired
    IYqueWxSecurityService iYqueWxSecurityService;


    @Autowired
    IYuqeOperateLogDao iYuqeOperateLogDao;

    @Autowired
    IYqueSynchDataRecordDao iYqueSynchDataRecordDao;


    @Autowired
    IYqueUserDao iYqueUserDao;


    @Override
    @Async
    public void synchOperateLog(Integer operateType) {

        try {

            List<IYuqeOperateLog> operateLogs=new ArrayList<>();



            Boolean hasMore=true;
            WxOperLogInfo wxOperLogInfo = WxOperLogInfo.builder()
                    .startTime(DateUtils.getDaysAgoInSeconds(new Date(), 7))
                    .endTime(Instant.now().getEpochSecond())
                    .build();


            IYqueSynchDataRecord iYqueSynchDataRecord = iYqueSynchDataRecordDao.findTopBySynchDataTypeOrderByCreateTimeDesc(
                    operateType.equals(new Integer(1))?SynchDataRecordType
                    .RECORD_TYPE_SYNCH_MEMBEWR_OPERATE_LOG.getCode():
                            SynchDataRecordType
                                    .RECORD_TYPE_SYNCH_ADMIN_OPERATE_LOG.getCode());


            if(null != iYqueSynchDataRecord && StringUtils.isNotEmpty(iYqueSynchDataRecord.getNextCursor())){
                wxOperLogInfo.setCursor(
                        iYqueSynchDataRecord.getNextCursor()
                );
            }else{//移除指定时间段的数据避免数据重复
                iYuqeOperateLogDao.deleteByOperateTypeAndCreateTimeBetween(operateType,DateUtils.getDaysAgo(new Date(),7),new Date());
            }


            do {

                WxOperLogResult result=operateType.equals(new Integer(1))
                        ?iYqueWxSecurityService.getMemberOperLog(wxOperLogInfo)
                        :iYqueWxSecurityService.getAdminOperLog(wxOperLogInfo);
                if(result.success()&& CollectionUtil.isNotEmpty(result.getRecordList())){
                    hasMore=result.isHasMore();

                    result.getRecordList().stream().forEach(k->{
                        List<IYqueUser> iYqueUsers = iYqueUserDao.findByUserId(k.getUserid());

                        if(CollectionUtil.isNotEmpty(iYqueUsers)){
                            operateLogs.add(
                                    IYuqeOperateLog.builder()
                                            .userId(k.getUserid())
                                            .userName(iYqueUsers.stream().findFirst().get().getName())
                                            .operateContent(k.getDetailInfo())
                                            .operateIp(k.getIp())
                                            .createTime(new Date(k.getTime() * 1000L))
                                            .operateType(operateType)
                                            .operateTypeSub(k.getOperType())
                                            .build()
                            );
                        }
                    });

                    if(StringUtils.isNotEmpty(result.getNextCursor())){
                        wxOperLogInfo.setCursor(result.getNextCursor());
                        //最后一次同步下标记录
                        iYqueSynchDataRecordDao.save(
                                IYqueSynchDataRecord.builder().synchDataType(SynchDataRecordType
                                                .RECORD_TYPE_SYNCH_CUSTOMER.getCode())
                                        .nextCursor(result.getNextCursor())
                                        .createTime(new Date())
                                        .build()
                        );
                    }

                }else{
                    hasMore=false;
                }





            }while (hasMore);


            if(CollectionUtil.isNotEmpty(operateLogs)){
                iYuqeOperateLogDao.saveAll(operateLogs);
            }




        }catch (Exception e){
            log.error("同步企微操作日志异常:"+e.getMessage());
        }


    }

    @Override
    public Page<IYuqeOperateLog> findAll(IYuqeOperateLog iYuqeOperateLog, Pageable pageable) {
        Specification<IYuqeOperateLog> spec = Specification.where(null);

        if(StringUtils.isNotEmpty(iYuqeOperateLog.getUserName())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("userName")), "%" + iYuqeOperateLog.getUserName().toLowerCase() + "%"));

        }

        if (iYuqeOperateLog.getOperateType() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("operateType")), iYuqeOperateLog.getOperateType()));
        }


        if (StringUtils.isNotEmpty(iYuqeOperateLog.getUserId())) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("userId")), iYuqeOperateLog.getUserId()));
        }

        if (iYuqeOperateLog.getOperateTypeSub() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("operateTypeSub")), iYuqeOperateLog.getOperateTypeSub()));
        }

        //按照时间查询
        if (iYuqeOperateLog.getStartTime() != null && iYuqeOperateLog.getEndTime() != null)  {
            spec = spec.and((root, query, cb) -> cb.between(root.get("createTime"), DateUtils.setTimeToStartOfDay( iYuqeOperateLog.getStartTime()), DateUtils.setTimeToEndOfDay( iYuqeOperateLog.getEndTime())));
        }


        return iYuqeOperateLogDao.findAll(spec, pageable);
    }
}
