package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueCustomerSeasDao;
import cn.iyque.domain.IYqueCustomerSeasVo;
import cn.iyque.entity.IYQueComplaintTip;
import cn.iyque.entity.IYqueConfig;
import cn.iyque.entity.IYqueCustomerSeas;
import cn.iyque.entity.IYqueUser;
import cn.iyque.enums.ComplaintContent;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueCustomerSeasService;
import cn.iyque.utils.DateUtils;
import cn.iyque.utils.SecurityUtils;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
@Slf4j
public class IYqueCustomerSeasServiceImpl implements IYqueCustomerSeasService {

    @Autowired
    IYqueCustomerSeasDao iYqueCustomerSeasDao;


    @Autowired
    IYqueConfigService iYqueConfigService;


    @Autowired
    IYqueParamConfig yqueParamConfig;




    @Override
    public void importData(List<IYqueUser> allocateUsers, MultipartFile file) {

        try {
            EasyExcel.read(file.getInputStream(), IYqueCustomerSeas.class, new PageReadListener<IYqueCustomerSeas>(dataList -> {

                //公海客户平均分配给相关员工
                if(CollectionUtil.isNotEmpty(allocateUsers)){


                    int totalCustomers = dataList.size();
                    int totalEmployees = allocateUsers.size();

                    IntStream.range(0, totalCustomers).forEach(i -> {
                        int employeeIndex = i % totalEmployees;
                        dataList.get(i).setAllocateUserId(allocateUsers.get(employeeIndex).getUserId());
                        dataList.get(i).setAllocateUserName(allocateUsers.get(employeeIndex).getName());
                        dataList.get(i).setCreateTime(new Date());
                        dataList.get(i).setCurrentState(0);
                    });

                    iYqueCustomerSeasDao.saveAll(dataList);

                    //公海通知
                    ThreadUtil.execute(()->{
                        distribute(dataList.stream()
                                .map(IYqueCustomerSeas::getId) // 假设 getId() 返回 Long
                                .toArray(Long[]::new));
                    });


                }
            })).sheet().doRead();
        }catch (Exception e){
            log.error("公海导入异常:"+e.getMessage());
        }


    }

    @Override
    public void updateCustomerSeasState(IYqueCustomerSeas customerSeas) {
        Optional<IYqueCustomerSeas> optional = iYqueCustomerSeasDao.findById(customerSeas.getId());
        if(optional.isPresent()){
            IYqueCustomerSeas iYqueCustomerSeas = optional.get();
            iYqueCustomerSeas.setCurrentState(customerSeas.getCurrentState());
            iYqueCustomerSeasDao.saveAndFlush(iYqueCustomerSeas);
        }

    }

    @Override
    public Page<IYqueCustomerSeas> findAll(IYqueCustomerSeas iYqueCustomerSeas, Pageable pageable) {
        Specification<IYqueCustomerSeas> spec = Specification.where(null);

        if(StringUtils.isNotEmpty(iYqueCustomerSeas.getPhoneNumber())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("phoneNumber")), "%" + iYqueCustomerSeas.getPhoneNumber().toLowerCase() + "%"));

        }
        if(StringUtils.isNotEmpty(iYqueCustomerSeas.getCustomerName())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("customerName")), "%" + iYqueCustomerSeas.getCustomerName().toLowerCase() + "%"));

        }
        if(StringUtils.isNotEmpty(iYqueCustomerSeas.getAllocateUserName())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("allocateUserName")), "%" + iYqueCustomerSeas.getAllocateUserName().toLowerCase() + "%"));

        }

        if (iYqueCustomerSeas.getCurrentState() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("currentState")), iYqueCustomerSeas.getCurrentState()));
        }


        if (StringUtils.isEmpty(iYqueCustomerSeas.getAllocateUserId())) {
            log.error("获取客户公海列表:"+ SecurityUtils.getCurrentUserName());
            if(!SecurityUtils.getCurrentUserName().equals(yqueParamConfig.getUserName())){
                spec = spec.and((root, query, cb) -> cb.equal(root.get("allocateUserId"), SecurityUtils.getCurrentUserName()));
            }
        }else{

        }

        return iYqueCustomerSeasDao.findAll(spec,pageable);
    }

    @Override
    public void batchDelete(Long[] ids) {
        iYqueCustomerSeasDao.deleteAllById(ListUtil.toList(ids));
    }

    @Override
    public void distribute(Long[] ids) throws IYqueException {
        List<IYqueCustomerSeas> iYqueCustomerSeas = iYqueCustomerSeasDao.findAllById(Arrays.asList(ids));


        if(CollectionUtil.isNotEmpty(iYqueCustomerSeas)){
         iYqueCustomerSeas.stream()
                    .collect(Collectors.groupingBy(IYqueCustomerSeas::getAllocateUserId)).forEach((k,v)->{

                     try {

                         IYqueConfig iYqueConfig = iYqueConfigService.findIYqueConfig();

                         if(null != iYqueConfig){
                             WxCpService wxcpservice = iYqueConfigService.findWxcpservice();


                             wxcpservice.getMessageService().send(
                                     WxCpMessage.TEXTCARD()
                                             .toUser(k)
                                             .agentId(new Integer(iYqueConfig.getAgentId()))
                                             .title("客户线索")
                                             .description( String.format(IYqueContant.customerSeaTpl, DateUtils.dateTimeNow(DateUtils.YYYY_MM_DD_HH_MM_SS),v.size()+"个"))
                                             .url(yqueParamConfig.getCustomerSeasUrl())
                                             .btnTxt("点击查看详情").build()
                             );
                         }




                     }catch (Exception e){
                         log.error("提醒通知异常:"+e.getMessage());
                         throw  new IYqueException(("提醒通知异常:"+e.getMessage()));

                     }


                    });
        }




    }
}
