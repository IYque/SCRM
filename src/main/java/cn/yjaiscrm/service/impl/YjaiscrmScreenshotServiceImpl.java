package cn.iyque.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.dao.IYqueScreenshotDao;
import cn.iyque.dao.IYqueUserDao;
import cn.iyque.domain.IYqueScreenshot;
import cn.iyque.entity.BaseEntity;
//import cn.iyque.entity.IYqueFileSecurity;
import cn.iyque.entity.IYqueUser;
import cn.iyque.service.IYqueScreenshotService;
import cn.iyque.utils.DateUtils;
import cn.iyque.wxjava.bean.WxFileSecurityInfo;
import cn.iyque.wxjava.bean.WxFileSecurityResult;
import cn.iyque.wxjava.bean.WxScreenshotInfo;
import cn.iyque.wxjava.bean.WxScreenshotResult;
import cn.iyque.wxjava.service.IYqueWxSecurityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class IYqueScreenshotServiceImpl implements IYqueScreenshotService {


    @Autowired
    private IYqueWxSecurityService wxSecurityService;


    @Autowired
    private IYqueUserDao iYqueUserDao;

    @Autowired
    private IYqueScreenshotDao iYqueScreenshotDao;



    @Override
    public void synchInfo(BaseEntity baseEntity) {

        try {

            List<IYqueScreenshot> screenshots=new ArrayList<>();

            Boolean hasMore=true;

            do {

                WxScreenshotResult result = wxSecurityService.getScreenOperRecord(
                        WxScreenshotInfo.builder()
                                .startTime(baseEntity.getStartTime().toInstant().getEpochSecond())
                                .endTime(baseEntity.getEndTime().toInstant().getEpochSecond())
                                .build()
                );
                if(result.success()&& CollectionUtil.isNotEmpty(result.getRecordList())){
                    hasMore=result.isHasMore();

                    result.getRecordList().stream().forEach(k->{
                        IYqueScreenshot yqueScreenshot=new IYqueScreenshot();
                        yqueScreenshot.setOperateTime(new Date(k.getTime() * 1000L));
                        //操作人为员工
                        if(StringUtils.isNotEmpty(k.getUserid())){
                            List<IYqueUser> iYqueUsers = iYqueUserDao.findByUserId(k.getUserid());
                            yqueScreenshot.setUserName(CollectionUtil.isNotEmpty(iYqueUsers)?iYqueUsers.stream().findFirst().get().getName():k.getUserid());
                            yqueScreenshot.setUserId(k.getUserid());

                        }

                        yqueScreenshot.setDepetId(k.getDepartmentId());
                        yqueScreenshot.setShotType(k.getScreenShotType());
                        yqueScreenshot.setShotContent(k.getScreenShotContent());
                        yqueScreenshot.setSystemOs(k.getSystem());
                        screenshots.add(yqueScreenshot);
                    });


                }else{
                    hasMore=false;
                }

            }while (hasMore);

            if(CollectionUtil.isNotEmpty(screenshots)){
                iYqueScreenshotDao.saveAll(screenshots);
            }

        }catch (Exception e){
            log.error("同步企微操截屏|录屏信息:"+e.getMessage());
        }

    }

    @Override
    public Page<IYqueScreenshot> findAll(IYqueScreenshot screenshot, Pageable pageable) {
        Specification<IYqueScreenshot> spec = Specification.where(null);

        if (screenshot.getShotType() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("shotType")), screenshot.getShotType()));
        }

        //按照时间查询
        if (screenshot.getStartTime() != null && screenshot.getEndTime() != null)  {


            spec = spec.and((root, query, cb) -> cb.between(root.get("operateTime"), DateUtils.setTimeToStartOfDay( screenshot.getStartTime()), DateUtils.setTimeToEndOfDay( screenshot.getEndTime())));
        }

        return iYqueScreenshotDao.findAll(spec, pageable);
    }
}
