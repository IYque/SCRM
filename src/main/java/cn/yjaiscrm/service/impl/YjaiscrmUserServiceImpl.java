package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.iyque.dao.IYqueUserDao;
import cn.iyque.entity.IYqueUser;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueUserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpDepartmentService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.WxCpUserService;
import me.chanjar.weixin.cp.bean.WxCpDepart;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class IYqueUserServiceImpl implements IYqueUserService {

    @Autowired
    private IYqueConfigService iYqueConfigService;

    @Autowired
    private IYqueUserDao  iYqueUserDao;


    @Override
    public void synchIyqueUser() {

        ThreadUtil.execute(()->{

            log.info("异步同步成员start===");

            try {
                WxCpService wxCpServic = iYqueConfigService.findWxcpservice();
                WxCpDepartmentService departmentService = wxCpServic.getDepartmentService();
                List<WxCpDepart> departList = departmentService.list(null);
                List<IYqueUser> iYqueUsers=new ArrayList<>();

                if(CollectionUtil.isNotEmpty(departList)){
                    WxCpUserService userService = wxCpServic.getUserService();
                    departList.stream().forEach(k->{



                        try {
                            List<WxCpUser> wxCpUserList = userService.listByDepartment(k.getId(), true, 0);
                            if(CollectionUtil.isNotEmpty(wxCpUserList)){
                                wxCpUserList.stream().forEach(kk->{

                                    iYqueUsers.add(
                                            IYqueUser.builder()
                                                    .userId(kk.getUserId())
                                                    .name(kk.getName())
                                                    .position(kk.getPosition())
                                                    .status(kk.getStatus())
                                                    .build()
                                    );
                                });
                            }

                        }catch (WxErrorException e) {
                            log.error("员工同步异常:"+e.getMessage());
                        }

                    });


                    iYqueUserDao.deleteAll();

                    if(CollectionUtil.isNotEmpty(iYqueUsers)){

                        List<IYqueUser> distinctUsers = iYqueUsers.stream()
                                .collect(Collectors.toMap(
                                        IYqueUser::getUserId,
                                        user -> user,
                                        (existing, replacement) -> existing
                                ))
                                .values()
                                .stream()
                                .collect(Collectors.toList());

                        if(CollectionUtil.isNotEmpty(distinctUsers)){
                            iYqueUserDao.saveAll(distinctUsers);
                        }

                    }

                }

            }catch (Exception e){

                log.error("同步应用可见范围客户异常:"+e.getMessage());


            }

            log.info("异步同步成员end===");

        });



    }

    @Override
    public List<IYqueUser> findIYqueUser() {

        return  iYqueUserDao.findAll();
    }

    @Override
    public IYqueUser findOrSaveUser(String userId) {
        List<IYqueUser> iYqueUsers
                = iYqueUserDao.findIYqueUserByUserId(userId);
        //不存在则，从企业微信api获取
        if(!CollectionUtil.isNotEmpty(iYqueUsers)){
            try {
                WxCpUser wxCpUser = iYqueConfigService.findWxcpservice()
                        .getUserService().getById(userId);

                if(null != wxCpUser){
                    IYqueUser   iYqueUser= IYqueUser.builder()
                            .userId(wxCpUser.getUserId())
                            .position(wxCpUser.getPosition())
                            .name(wxCpUser.getName())
                            .build();

                    iYqueUserDao.save(
                            iYqueUser
                    );
                    iYqueUsers.add(iYqueUser);
                }



            }catch (Exception e){
                log.error("获取员工失败:"+e.getMessage());
            }

        }


        return iYqueUsers.stream().findFirst().get();
    }

    @Override
    public Page<IYqueUser> findIYqueUserPage(String name, Pageable pageable) {
        Specification<IYqueUser> spec = Specification.where(null);

        if (StringUtils.isNotEmpty(name)) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        return iYqueUserDao.findAll(spec, pageable);
    }


}
