package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.dao.IYqueFileSecurityDao;
import cn.iyque.dao.IYqueUserDao;
import cn.iyque.entity.BaseEntity;
import cn.iyque.entity.IYqueFileSecurity;
import cn.iyque.entity.IYqueUser;
import cn.iyque.service.IYqueFileSecurityService;
import cn.iyque.utils.DateUtils;
import cn.iyque.wxjava.bean.WxFileSecurityInfo;
import cn.iyque.wxjava.bean.WxFileSecurityResult;
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
public class IYqueFileSecurityServiceImpl implements IYqueFileSecurityService {


    @Autowired
    private IYqueWxSecurityService wxSecurityService;

    @Autowired
    private IYqueFileSecurityDao fileSecurityDao;


    @Autowired
    private IYqueUserDao iYqueUserDao;




    @Override
    public void synchInfo(BaseEntity baseEntity) {

        try {

            List<IYqueFileSecurity> fileSecurities=new ArrayList<>();

            Boolean hasMore=true;

            do {

                WxFileSecurityResult result = wxSecurityService.getFileOperRecord(
                        WxFileSecurityInfo.builder()
                                .startTime(baseEntity.getStartTime().toInstant().getEpochSecond())
                                .endTime(baseEntity.getEndTime().toInstant().getEpochSecond())
                                .build()
                );
                if(result.success()&& CollectionUtil.isNotEmpty(result.getRecordList())){
                    hasMore=result.isHasMore();

                    result.getRecordList().stream().forEach(k->{
                        IYqueFileSecurity fileSecurity=new IYqueFileSecurity();
                        fileSecurity.setOperateTime(new Date(k.getTime() * 1000L));
                        //操作人为员工
                        if(StringUtils.isNotEmpty(k.getUserid())){
                            List<IYqueUser> iYqueUsers = iYqueUserDao.findByUserId(k.getUserid());
                            fileSecurity.setUserName(CollectionUtil.isNotEmpty(iYqueUsers)?iYqueUsers.stream().findFirst().get().getName():k.getUserid());
                            fileSecurity.setUserType(3);

                        }


                        //操作人为客户
                        if(null != k.getExternalUser()){
                            fileSecurity.setUserName(k.getExternalUser().getName());
                            fileSecurity.setUserType(k.getExternalUser().getType());
                        }

                        //设置操作类型或操作来源
                        if(null != k.getOperation()){
                            fileSecurity.setOperateType(k.getOperation().getType());
                            fileSecurity.setOpreateSource(k.getOperation().getSource());
                        }

                        fileSecurity.setOperateFileInfo(k.getFileInfo());

                        fileSecurities.add(fileSecurity);
                    });


                }else{
                    hasMore=false;
                }

            }while (hasMore);

            if(CollectionUtil.isNotEmpty(fileSecurities)){
                fileSecurityDao.saveAll(fileSecurities);
            }

        }catch (Exception e){
            log.error("同步企微操文件放泄露:"+e.getMessage());
        }



    }

    @Override
    public Page<IYqueFileSecurity> findAll(IYqueFileSecurity iYqueFileSecurity, Pageable pageable) {
        Specification<IYqueFileSecurity> spec = Specification.where(null);



        if (iYqueFileSecurity.getUserType() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("userType")), iYqueFileSecurity.getUserType()));
        }

        if (iYqueFileSecurity.getOpreateSource() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("opreateSource")), iYqueFileSecurity.getOpreateSource()));
        }



        if (iYqueFileSecurity.getOperateType() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("operateType")), iYqueFileSecurity.getOperateType()));
        }

        //按照时间查询
        if (iYqueFileSecurity.getStartTime() != null && iYqueFileSecurity.getEndTime() != null)  {


            spec = spec.and((root, query, cb) -> cb.between(root.get("operateTime"), DateUtils.setTimeToStartOfDay( iYqueFileSecurity.getStartTime()), DateUtils.setTimeToEndOfDay( iYqueFileSecurity.getEndTime())));
        }


        return fileSecurityDao.findAll(spec, pageable);
    }
}
