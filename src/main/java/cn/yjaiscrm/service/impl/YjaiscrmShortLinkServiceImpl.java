package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.iyque.constant.CodeStateConstant;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueShortLinkDao;
import cn.iyque.domain.IYQueCustomerInfo;
import cn.iyque.domain.IYqueKvalStrVo;
import cn.iyque.entity.*;
import cn.iyque.enums.CustomerChatStatus;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.*;
import cn.iyque.utils.DateUtils;
import cn.iyque.utils.SnowFlakeUtils;
import cn.iyque.dao.IYqueSynchDataRecordDao;
import cn.iyque.enums.SynchDataRecordType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionCreateResult;
import me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionInfo;
import me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionRequest;
import me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionList;
import me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionCustomerList;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IYqueShortLinkServiceImpl implements IYqueShortLinkService {

    @Autowired
    private IYqueConfigService iYqueConfigService;

    @Autowired
    private IYqueShortLinkDao iYqueShortLinkDao;

    @Autowired
    private IYqueMsgAnnexService iYqueMsgAnnexService;


    @Autowired
    private IYqueAnnexPeriodService iYqueAnnexPeriodService;


    @Autowired
    private IYquePeriodMsgAnnexService iYquePeriodMsgAnnexService;

    @Autowired
    private IYqueSynchDataRecordDao iYqueSynchDataRecordDao;

    @Autowired
    private IYqueUserService iYqueUserService;

    @Autowired
    private IYqueCustomerInfoService iYqueCustomerInfoService;

    // 线程池用于并发处理客户数据
    private final Executor customerDataExecutor = Executors.newFixedThreadPool(10);

    @Override
    public Page<IYqueShortLink> findAll(Pageable pageable) {
        return iYqueShortLinkDao.findAll(pageable);
    }

    @Override
    public Page<IYqueShortLink> findAll(IYqueShortLink iYqueShortLink, Pageable pageable) {
        Specification<IYqueShortLink> spec = Specification.where(null);



        // 按外链名称搜索
        if (StringUtils.hasText(iYqueShortLink.getCodeName())) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("codeName")), "%" + iYqueShortLink.getCodeName() + "%"));
        }

        // 按员工名称搜索
        if (StringUtils.hasText(iYqueShortLink.getUserName())) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("userName")), "%" + iYqueShortLink.getUserName() + "%"));
        }


        //按照时间查询
        if (iYqueShortLink.getStartTime() != null && iYqueShortLink.getEndTime() != null)  {
            spec = spec.and((root, query, cb) -> cb.between(root.get("createTime"), DateUtils.setTimeToStartOfDay( iYqueShortLink.getStartTime()), DateUtils.setTimeToEndOfDay( iYqueShortLink.getEndTime())));
        }



        return iYqueShortLinkDao.findAll(spec, pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(IYqueShortLink shortLink) throws Exception {

        try {
            shortLink.setCreateTime(new Date());
            shortLink.setUpdateTime(new Date());
            shortLink.setCodeState(CodeStateConstant.LINK_CODE_STATE+ SnowFlakeUtils.nextId());
            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
            WxCpCustomerAcquisitionRequest request=new WxCpCustomerAcquisitionRequest();
            request.setLinkName(shortLink.getCodeName());
            request.setSkipVerify(shortLink.getSkipVerify());
            WxCpCustomerAcquisitionInfo.Range range=new WxCpCustomerAcquisitionInfo.Range();
            range.setUserList(ListUtil.toList(shortLink.getUserId().split(",")));
            request.setRange(range);

            WxCpCustomerAcquisitionCreateResult createResult = wxcpservice.getExternalContactService().customerAcquisitionLinkCreate(
                    request
            );

            if(createResult == null ){
                throw new IYqueException("获客短链创建失败");
            }



            WxCpCustomerAcquisitionInfo.Link link = createResult.getLink();

            if(null == link){
                throw new IYqueException("获客短链创建失败");
            }

            if(null != link && StrUtil.isNotEmpty(link.getLinkId())
                    && StrUtil.isNotEmpty(link.getUrl())){
                shortLink.setConfigId(link.getLinkId());
                shortLink.setCodeUrl(link.getUrl()+"?customer_channel="+shortLink.getCodeState() );

                iYqueShortLinkDao.save(shortLink);


                //时段欢迎语附件
                if(shortLink.isStartPeriodAnnex()){


                    List<IYqueAnnexPeriod> periodAnnexLists=shortLink.getPeriodAnnexLists();
                    if(CollectionUtil.isNotEmpty(periodAnnexLists)){
                        //时段附件
                        List<IYquePeriodMsgAnnex> iYquePeriodMsgAnnexes=new ArrayList<>();
                        periodAnnexLists.stream().forEach(k->{
                            k.setMsgId(shortLink.getId());
                        });

                        //存储时段
                        iYqueAnnexPeriodService.saveAll(periodAnnexLists);
                        //存储时段附件
                        periodAnnexLists.stream().forEach(k->{
                            List<IYquePeriodMsgAnnex> periodMsgAnnexList = k.getPeriodMsgAnnexList();
                            if(CollectionUtil.isNotEmpty(periodMsgAnnexList)){
                                periodMsgAnnexList.stream().forEach(periodMsgAnnex->{
                                    periodMsgAnnex.setAnnexPeroidId(k.getId());
                                });
                                iYquePeriodMsgAnnexes.addAll(periodMsgAnnexList);
                            }
                        });

                        iYquePeriodMsgAnnexService.saveAll(iYquePeriodMsgAnnexes);

                    }
                }else{

                    List<IYqueMsgAnnex> annexLists = shortLink.getAnnexLists();
                    if(CollectionUtil.isNotEmpty(annexLists)){
                        annexLists.stream().forEach(k->{
                            k.setMsgId(shortLink.getId());
                        });
                        iYqueMsgAnnexService.saveAll(annexLists);
                    }
                }
            }
        }catch (Exception e){
            log.error("获客链接，创建失败:"+e.getMessage());
            throw e;
        }



    }

    @Override
    public List<IYqueKvalStrVo> findIYqueShorkLinkKvs() {
        List<IYqueKvalStrVo> iYqueKvalVos=new ArrayList<>();


        List<IYqueShortLink> iYqueShortLinks = iYqueShortLinkDao.findAll();
        if(CollectionUtil.isNotEmpty(iYqueShortLinks)){
            iYqueShortLinks.stream().forEach(k->{
                iYqueKvalVos.add(
                        IYqueKvalStrVo.builder()
                                .val(k.getId().toString())
                                .key(k.getCodeName())
                                .build()
                );

            });
        }



        return iYqueKvalVos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(IYqueShortLink shortLink) throws Exception {

        try {
            IYqueShortLink oldShortLink = this.findIYqueShortLinkById(shortLink.getId());
            if(null != oldShortLink){

                shortLink.setCodeState(oldShortLink.getCodeState());
                shortLink.setConfigId(oldShortLink.getConfigId());
                WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
                WxCpCustomerAcquisitionRequest request=new WxCpCustomerAcquisitionRequest();
                request.setLinkId(oldShortLink.getConfigId());
                request.setLinkName(shortLink.getCodeName());
                request.setSkipVerify(shortLink.getSkipVerify());
                WxCpCustomerAcquisitionInfo.Range range=new WxCpCustomerAcquisitionInfo.Range();
                range.setUserList(ListUtil.toList(shortLink.getUserId().split(",")));
                request.setRange(range);

                WxCpBaseResp wxCpBaseResp
                        = wxcpservice.getExternalContactService().customerAcquisitionUpdate(request);

                if(null == wxCpBaseResp){
                    throw new IYqueException("获客短链创建失败");
                }

                if(IYqueContant.COMMON_STATE.equals(wxCpBaseResp.getErrcode().intValue())){

                    shortLink.setUpdateTime(new Date());
                    iYqueShortLinkDao.saveAndFlush(shortLink);


                    if(shortLink.isStartPeriodAnnex()){//开启时段欢迎语

                        List<IYqueAnnexPeriod> periodAnnexLists=shortLink.getPeriodAnnexLists();
                        if(CollectionUtil.isNotEmpty(periodAnnexLists)){

                            //时段附件
                            List<IYquePeriodMsgAnnex> iYquePeriodMsgAnnexes=new ArrayList<>();
                            periodAnnexLists.stream().forEach(k->{
                                k.setMsgId(shortLink.getId());
                            });

                            //存储时段
                            List<IYqueAnnexPeriod> oldIYqueAnnexPeriod = iYqueAnnexPeriodService
                                    .findIYqueAnnexPeriodByMsgId(shortLink.getId());

                            if(CollectionUtil.isNotEmpty(oldIYqueAnnexPeriod)){
                                iYqueAnnexPeriodService.deleteIYqueAnnexPeriodByMsgId(shortLink.getId());
                                iYquePeriodMsgAnnexService.deleteAllByAnnexPeroidIdIn(
                                        oldIYqueAnnexPeriod.stream().map(IYqueAnnexPeriod::getId).collect(Collectors.toList())
                                );
                            }
                            iYqueAnnexPeriodService.saveAll(periodAnnexLists);


                            periodAnnexLists.stream().forEach(k->{
                                List<IYquePeriodMsgAnnex> periodMsgAnnexList = k.getPeriodMsgAnnexList();
                                if(CollectionUtil.isNotEmpty(periodMsgAnnexList)){
                                    periodMsgAnnexList.stream().forEach(periodMsgAnnex->{
                                        periodMsgAnnex.setAnnexPeroidId(k.getId());
                                    });
                                    iYquePeriodMsgAnnexes.addAll(periodMsgAnnexList);
                                }
                            });
                            iYquePeriodMsgAnnexService.saveAll(iYquePeriodMsgAnnexes);


                        }

                    }else{
                        iYqueMsgAnnexService.deleteIYqueMsgAnnexByMsgId(shortLink.getId());
                        List<IYqueMsgAnnex> annexLists = shortLink.getAnnexLists();
                        if(CollectionUtil.isNotEmpty(annexLists)){
                            annexLists.stream().forEach(k->{
                                k.setMsgId(shortLink.getId());
                            });
                            iYqueMsgAnnexService.saveAll(annexLists);
                        }

                    }





                }else{
                    log.error("获客短链更新失败:"+wxCpBaseResp.getErrmsg());
                }

            }

        }catch (Exception e){
            log.error("获客短链更新失败:"+e.getMessage());
            throw e;
        }

    }

    @Override
    public IYqueShortLink findIYqueShortLinkById(Long id) {
        return iYqueShortLinkDao.getById(id);
    }

    @Override
    public void batchDelete(Long[] ids) {

        List<IYqueShortLink> iYqueShortLinks = iYqueShortLinkDao.findAllById(Arrays.asList(ids));

        if(CollectionUtil.isNotEmpty(iYqueShortLinks)){
            iYqueShortLinks.stream().forEach(k->{
                k.setDelFlag(IYqueContant.DEL_STATE);

                try {
                    iYqueConfigService.findWxcpservice().getExternalContactService().customerAcquisitionLinkDelete(k.getConfigId());
                    iYqueShortLinkDao.saveAndFlush(k);
                }catch (Exception e){
                    log.error("获客链接删除失败:"+e.getMessage());
                }

            });

        }

    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void synchShortLink() {
        try {
            log.info("开始同步获客外链...");

            List<IYqueShortLink> shortLinks = new ArrayList<>();
            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();

            String cursor = null;
            int limit = 100;

            // 获取上次同步的游标位置
            IYqueSynchDataRecord lastRecord = iYqueSynchDataRecordDao
                    .findTopBySynchDataTypeOrderByCreateTimeDesc(SynchDataRecordType.RECORD_TYPE_SYNCH_SHORT_LINK.getCode());

            if (lastRecord != null && StrUtil.isNotEmpty(lastRecord.getNextCursor())) {
                cursor = lastRecord.getNextCursor();
                log.info("使用上次同步的游标: {}", cursor);
            } else {
                log.info("没有找到上次同步记录，从头开始同步");
            }

            // 为了确保能获取到数据，先尝试重置游标从头开始
            log.warn("为了调试，强制从头开始同步（cursor设为null）");
            cursor = null;

            do {
                // 调用企业微信API获取获客链接列表
                log.info("调用企业微信API获取获客链接列表，limit: {}, cursor: {}", limit, cursor);

                WxCpCustomerAcquisitionList acquisitionList = null;
                try {
                    acquisitionList = wxcpservice.getExternalContactService()
                            .customerAcquisitionLinkList(limit, cursor);
                    log.info("API调用成功，返回结果: {}", acquisitionList != null ? "非空" : "空");
                } catch (Exception apiException) {
                    log.error("调用企业微信API失败: {}", apiException.getMessage(), apiException);
                    break;
                }

                if (acquisitionList != null) {
                    log.info("获取到的linkIdList: {}", acquisitionList.getLinkIdList());
                    log.info("nextCursor: {}", acquisitionList.getNextCursor());

                    if (CollectionUtil.isNotEmpty(acquisitionList.getLinkIdList())) {
                        log.info("找到 {} 个获客外链ID", acquisitionList.getLinkIdList().size());
                    } else {
                        log.warn("获客外链ID列表为空");
                    }
                } else {
                    log.warn("API返回的acquisitionList为空");
                }

                if (acquisitionList != null && CollectionUtil.isNotEmpty(acquisitionList.getLinkIdList())) {
                    for (String linkId : acquisitionList.getLinkIdList()) {
                        try {
                            log.info("正在获取获客链接详情，linkId: {}", linkId);

                            // 获取链接详情
                            WxCpCustomerAcquisitionInfo linkDetail = wxcpservice.getExternalContactService()
                                    .customerAcquisitionLinkGet(linkId);

                            if (linkDetail != null) {
                                log.info("成功获取到linkDetail，开始转换");
                                IYqueShortLink shortLink = convertToShortLink(linkDetail);
                                if (shortLink != null) {
                                    shortLinks.add(shortLink);
                                    log.info("成功转换并添加获客外链，名称: {}", shortLink.getCodeName());
                                } else {
                                    log.warn("转换获客外链失败，linkId: {}", linkId);
                                }
                            } else {
                                log.warn("获取到的linkDetail为空，linkId: {}", linkId);
                            }
                        } catch (Exception e) {
                            log.error("获取获客链接详情失败，linkId: {}, 错误: {}", linkId, e.getMessage(), e);
                        }
                    }
                }

                cursor = acquisitionList != null ? acquisitionList.getNextCursor() : null;
                log.info("更新cursor为: {}", cursor);

                // 记录同步进度
                if (StrUtil.isNotEmpty(cursor)) {
                    iYqueSynchDataRecordDao.save(
                            IYqueSynchDataRecord.builder()
                                    .synchDataType(SynchDataRecordType.RECORD_TYPE_SYNCH_SHORT_LINK.getCode())
                                    .nextCursor(cursor)
                                    .createTime(new Date())
                                    .build()
                    );
                    log.info("保存同步进度记录，cursor: {}", cursor);
                }

            } while (StrUtil.isNotEmpty(cursor));

            // 批量保存同步的数据
            if (CollectionUtil.isNotEmpty(shortLinks)) {
                // 分批保存，避免一次性保存过多数据
                ListUtil.partition(shortLinks, 50).forEach(batch -> {
                    try {
                        iYqueShortLinkDao.saveAll(batch);
                        log.info("保存获客外链数据，数量: {}", batch.size());
                    } catch (Exception e) {
                        log.error("保存获客外链数据失败: {}", e.getMessage());
                    }
                });
            }

            log.info("获客外链同步完成，共同步 {} 条数据", shortLinks.size());

        } catch (Exception e) {
            log.error("获客外链同步失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 将企业微信获客链接信息转换为本地实体对象
     */
    private IYqueShortLink convertToShortLink(WxCpCustomerAcquisitionInfo linkDetail) {
        if (linkDetail == null) {
            log.warn("linkDetail为空");
            return null;
        }

        if (linkDetail.getLink() == null) {
            log.warn("linkDetail.getLink()为空");
            return null;
        }

        try {
            WxCpCustomerAcquisitionInfo.Link link = linkDetail.getLink();
            WxCpCustomerAcquisitionInfo.Range range = linkDetail.getRange();

            // 首先获取真实的configId
            String realConfigId = null;

            // 从URL中提取configId
            if (StrUtil.isNotEmpty(link.getUrl()) && link.getUrl().contains("/ca/")) {
                String[] parts = link.getUrl().split("/ca/");
                if (parts.length > 1) {
                    String linkIdPart = parts[1];
                    if (linkIdPart.contains("?")) {
                        linkIdPart = linkIdPart.split("\\?")[0];
                    }
                    if (StrUtil.isNotEmpty(linkIdPart)) {
                        realConfigId = linkIdPart;
                        log.info("从URL中提取到realConfigId: {}", realConfigId);
                    }
                }
            }

            if (StrUtil.isEmpty(realConfigId)) {
                log.error("无法从URL中提取configId，URL: {}", link.getUrl());
                return null;
            }

            // 检查是否已存在相同configId的记录（使用真实的configId）
            IYqueShortLink existingShortLink = iYqueShortLinkDao.findByConfigId(realConfigId);
            IYqueShortLink shortLink;

            if (existingShortLink != null) {
                // 更新现有记录
                shortLink = existingShortLink;
                shortLink.setUpdateTime(new Date());
                log.info("更新现有获客外链，configId: {}", realConfigId);
            } else {
                // 创建新记录
                shortLink = new IYqueShortLink();
                shortLink.setCreateTime(new Date());
                shortLink.setUpdateTime(new Date());
                shortLink.setDelFlag(0);
                // 生成渠道标识
                shortLink.setCodeState(CodeStateConstant.LINK_CODE_STATE + SnowFlakeUtils.nextId());
                log.info("创建新获客外链，configId: {}", realConfigId);
            }

            // 基本信息
            shortLink.setCodeName(link.getLinkName());

            // 设置configId（使用之前提取的realConfigId）
            shortLink.setConfigId(realConfigId);
            log.info("设置configId: {}", realConfigId);

            shortLink.setCodeUrl(link.getUrl());
            // 从linkDetail中获取skipVerify信息
            shortLink.setSkipVerify(linkDetail.getSkipVerify() != null ? linkDetail.getSkipVerify() : false);

            // 处理员工信息
            if (range != null && CollectionUtil.isNotEmpty(range.getUserList())) {
                shortLink.setUserId(String.join(",", range.getUserList()));

                // 补全员工姓名
                List<String> userNames = new ArrayList<>();
                for (String userId : range.getUserList()) {
                    try {
                        IYqueUser user = iYqueUserService.findOrSaveUser(userId);
                        if (user != null && StrUtil.isNotEmpty(user.getName())) {
                            userNames.add(user.getName());
                        } else {
                            userNames.add(userId); // 如果获取不到姓名，使用userId
                        }
                    } catch (Exception e) {
                        log.warn("获取员工信息失败，userId: {}, 错误: {}", userId, e.getMessage());
                        userNames.add(userId);
                    }
                }
                shortLink.setUserName(String.join(",", userNames));
            }

            // 设置默认值
            shortLink.setIsExclusive(false);
            shortLink.setStartPeriodAnnex(false);
            shortLink.setRemarkType(1); // 默认备注类型

            // 最终验证configId是否正确设置
            if (StrUtil.isEmpty(shortLink.getConfigId())) {
                log.error("转换完成后configId仍为空，realConfigId: {}, linkName: {}",
                        realConfigId, link.getLinkName());
                throw new RuntimeException("configId设置失败");
            }

            log.info("成功转换获客外链数据，configId: {}, 名称: {}", shortLink.getConfigId(), shortLink.getCodeName());
            return shortLink;

        } catch (Exception e) {
            log.error("转换获客链接数据失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Object getCustomerList(String linkId, Integer limit, String cursor) throws Exception {
        return getCustomerList(linkId, limit, cursor, null, null, null, null);
    }

    @Override
    public Object getCustomerList(String linkId, Integer limit, String cursor, String customerName, String followUser, String startDate, String endDate) throws Exception {
        try {
            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();

            // 调用企业微信API获取获客链接的客户列表
            Object result = wxcpservice.getExternalContactService().customerAcquisitionCustomer(linkId, limit, cursor);

            // 处理返回数据，确保字段映射正确
            Object processedResult = processCustomerListData(result);

            // 应用搜索和筛选
            return applySearchAndFilter(processedResult, customerName, followUser, startDate, endDate);

        } catch (Exception e) {
            log.error("获取获客链接客户列表失败，linkId: {}, 错误: {}", linkId, e.getMessage());
            throw new Exception("获取客户列表失败: " + e.getMessage());
        }
    }

    /**
     * 处理客户列表数据，进行字段映射和数据转换
     */
    private Object processCustomerListData(Object rawData) {
        try {
            if (rawData == null) {
                log.warn("原始数据为空");
                return null;
            }

            log.info("开始处理客户列表数据，原始数据类型: {}", rawData.getClass().getSimpleName());

            // 检查是否是 WxCpCustomerAcquisitionCustomerList 类型
            if (rawData instanceof me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionCustomerList) {
                me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionCustomerList customerListData =
                        (me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionCustomerList) rawData;

                // 创建返回的数据结构
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("nextCursor", customerListData.getNextCursor());

                // 处理客户列表
                List<me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionCustomerList.Customer> originalCustomerList =
                        customerListData.getCustomerList();

                if (originalCustomerList != null && !originalCustomerList.isEmpty()) {
                    log.info("开始处理客户列表数据，客户数量: {}", originalCustomerList.size());

                    // 使用并发处理优化性能
                    List<Map<String, Object>> processedCustomerList = processCustomerListConcurrently(originalCustomerList);

                    // 将处理后的数据放入结果
                    resultMap.put("customerList", processedCustomerList);

                    log.info("客户列表数据处理完成，处理后的客户数量: {}", processedCustomerList.size());
                } else {
                    log.warn("未找到客户列表数据或客户列表为空");
                    // 如果没有客户列表，创建一个空的列表
                    resultMap.put("customerList", new ArrayList<>());
                }

                return resultMap;
            } else {
                // 如果不是预期的类型，尝试通过反射处理
                log.info("原始数据不是WxCpCustomerAcquisitionCustomerList类型，尝试通过反射解析: {}", rawData.getClass().getName());
                Map<String, Object> dataMap = convertObjectToMap(rawData);
                if (dataMap == null) {
                    log.warn("无法解析原始数据，直接返回");
                    return rawData;
                }
                return dataMap;
            }

        } catch (Exception e) {
            log.error("处理客户列表数据失败: {}", e.getMessage(), e);
            return rawData; // 处理失败时返回原始数据
        }
    }

    /**
     * 应用搜索和筛选条件
     */
    private Object applySearchAndFilter(Object processedResult, String customerName, String followUser, String startDate, String endDate) {
        try {
            if (processedResult == null) {
                return processedResult;
            }

            // 检查是否有任何搜索条件
            boolean hasSearchConditions = StrUtil.isNotEmpty(customerName) || StrUtil.isNotEmpty(followUser)
                    || StrUtil.isNotEmpty(startDate) || StrUtil.isNotEmpty(endDate);

            if (!hasSearchConditions) {
                return processedResult; // 没有搜索条件，直接返回
            }

            if (processedResult instanceof Map) {
                Map<String, Object> resultMap = (Map<String, Object>) processedResult;
                List<Map<String, Object>> customerList = (List<Map<String, Object>>) resultMap.get("customerList");

                if (customerList != null && !customerList.isEmpty()) {
                    log.info("应用搜索筛选条件 - customerName: {}, followUser: {}, startDate: {}, endDate: {}",
                            customerName, followUser, startDate, endDate);

                    List<Map<String, Object>> filteredList = customerList.stream()
                            .filter(customer -> {
                                // 客户姓名筛选
                                if (StrUtil.isNotEmpty(customerName)) {
                                    String externalName = (String) customer.get("externalName");
                                    if (externalName == null || !externalName.contains(customerName)) {
                                        return false;
                                    }
                                }

                                // 跟进人筛选
                                if (StrUtil.isNotEmpty(followUser)) {
                                    String followUserName = (String) customer.get("followUserName");
                                    if (followUserName == null || !followUserName.contains(followUser)) {
                                        return false;
                                    }
                                }

                                // 时间范围筛选
                                if (StrUtil.isNotEmpty(startDate) || StrUtil.isNotEmpty(endDate)) {
                                    String createTime = (String) customer.get("createTime");
                                    if (createTime == null || createTime.equals("-")) {
                                        return false;
                                    }

                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Date customerCreateTime = sdf.parse(createTime);

                                        if (StrUtil.isNotEmpty(startDate)) {
                                            Date start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
                                            if (customerCreateTime.before(start)) {
                                                return false;
                                            }
                                        }

                                        if (StrUtil.isNotEmpty(endDate)) {
                                            Date end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
                                            // 设置为当天的23:59:59
                                            Calendar calendar = Calendar.getInstance();
                                            calendar.setTime(end);
                                            calendar.set(Calendar.HOUR_OF_DAY, 23);
                                            calendar.set(Calendar.MINUTE, 59);
                                            calendar.set(Calendar.SECOND, 59);
                                            if (customerCreateTime.after(calendar.getTime())) {
                                                return false;
                                            }
                                        }
                                    } catch (Exception e) {
                                        log.warn("解析时间失败: {}", e.getMessage());
                                        return false;
                                    }
                                }

                                return true;
                            })
                            .collect(Collectors.toList());

                    resultMap.put("customerList", filteredList);
                    log.info("筛选完成，原始数量: {}, 筛选后数量: {}", customerList.size(), filteredList.size());
                }
            }

            return processedResult;

        } catch (Exception e) {
            log.error("应用搜索筛选失败: {}", e.getMessage(), e);
            return processedResult; // 出错时返回原始数据
        }
    }

    /**
     * 并发处理客户列表数据，提高性能
     */
    private List<Map<String, Object>> processCustomerListConcurrently(
            List<me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionCustomerList.Customer> originalCustomerList) {

        // 预先批量查询所有需要的数据，减少数据库查询次数
        Map<String, String> customerNameCache = batchGetCustomerNames(originalCustomerList);
        Map<String, String> userNameCache = batchGetUserNames(originalCustomerList);
        Map<String, String> createTimeCache = batchGetCreateTimes(originalCustomerList);

        // 使用并行流处理客户数据
        return originalCustomerList.parallelStream()
                .map(customer -> {
                    Map<String, Object> customerMap = new HashMap<>();

                    // 基本字段映射
                    customerMap.put("externalUserid", customer.getExternalUserid());
                    customerMap.put("userid", customer.getUserid());
                    customerMap.put("chatStatus", customer.getChatStatus());

                    // 从缓存中获取客户姓名
                    String externalUserid = customer.getExternalUserid();
                    if (externalUserid != null) {
                        String customerName = customerNameCache.get(externalUserid);
                        customerMap.put("externalName", customerName != null ? customerName : "未知客户");

                        String createTime = createTimeCache.get(externalUserid + "_" + customer.getUserid());
                        customerMap.put("createTime", createTime != null ? createTime : "-");
                    } else {
                        customerMap.put("externalName", "未知客户");
                        customerMap.put("createTime", "-");
                    }

                    // 从缓存中获取员工姓名
                    String userid = customer.getUserid();
                    if (userid != null) {
                        String followUserName = userNameCache.get(userid);
                        customerMap.put("followUserName", followUserName != null ? followUserName : "未知员工");
                    } else {
                        customerMap.put("followUserName", "未知员工");
                    }

                    // 会话状态转换
                    if (customer.getChatStatus() != null) {
                        customerMap.put("chatStatus", convertChatStatus(customer.getChatStatus()));
                    } else {
                        customerMap.put("chatStatus", CustomerChatStatus.NOT_STARTED.getCode());
                    }

                    return customerMap;
                })
                .collect(Collectors.toList());
    }

    /**
     * 批量获取客户姓名，减少数据库查询次数
     */
    private Map<String, String> batchGetCustomerNames(
            List<me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionCustomerList.Customer> customerList) {

        Map<String, String> nameCache = new ConcurrentHashMap<>();

        // 提取所有的externalUserid
        List<String> externalUserids = customerList.stream()
                .map(me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionCustomerList.Customer::getExternalUserid)
                .filter(id -> id != null && !id.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        if (externalUserids.isEmpty()) {
            return nameCache;
        }

        log.info("批量查询客户姓名，数量: {}", externalUserids.size());

        // 批量从数据库查询
        try {
            List<IYQueCustomerInfo> customerInfos = iYqueCustomerInfoService.findByExternalUseridIn(externalUserids);
            for (IYQueCustomerInfo info : customerInfos) {
                if (info.getExternalUserid() != null && info.getCustomerName() != null) {
                    nameCache.put(info.getExternalUserid(), info.getCustomerName());
                }
            }
        } catch (Exception e) {
            log.warn("批量查询客户信息失败: {}", e.getMessage());
        }

        // 对于数据库中没有的客户，使用并发方式从API获取
        List<String> missingUserids = externalUserids.stream()
                .filter(id -> !nameCache.containsKey(id))
                .collect(Collectors.toList());

        if (!missingUserids.isEmpty()) {
            log.info("从API获取缺失的客户信息，数量: {}", missingUserids.size());

            // 使用CompletableFuture并发获取
            List<CompletableFuture<Void>> futures = missingUserids.stream()
                    .map(externalUserid -> CompletableFuture.runAsync(() -> {
                        try {
                            String customerName = getCustomerNameByExternalUserid(externalUserid);
                            if (customerName != null) {
                                nameCache.put(externalUserid, customerName);
                            }
                        } catch (Exception e) {
                            log.warn("获取客户姓名失败，externalUserid: {}, 错误: {}", externalUserid, e.getMessage());
                        }
                    }, customerDataExecutor))
                    .collect(Collectors.toList());

            // 等待所有任务完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        return nameCache;
    }

    /**
     * 批量获取员工姓名
     */
    private Map<String, String> batchGetUserNames(
            List<me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionCustomerList.Customer> customerList) {

        Map<String, String> userNameCache = new ConcurrentHashMap<>();

        // 提取所有的userid
        List<String> userids = customerList.stream()
                .map(me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionCustomerList.Customer::getUserid)
                .filter(id -> id != null && !id.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        if (userids.isEmpty()) {
            return userNameCache;
        }

        log.info("批量查询员工姓名，数量: {}", userids.size());

        // 批量获取员工信息
        for (String userid : userids) {
            try {
                IYqueUser user = iYqueUserService.findOrSaveUser(userid);
                if (user != null && user.getName() != null) {
                    userNameCache.put(userid, user.getName());
                }
            } catch (Exception e) {
                log.warn("获取员工信息失败，userid: {}, 错误: {}", userid, e.getMessage());
            }
        }

        return userNameCache;
    }

    /**
     * 批量获取客户添加时间
     */
    private Map<String, String> batchGetCreateTimes(
            List<me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionCustomerList.Customer> customerList) {

        Map<String, String> createTimeCache = new ConcurrentHashMap<>();

        // 提取所有的externalUserid
        List<String> externalUserids = customerList.stream()
                .map(me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionCustomerList.Customer::getExternalUserid)
                .filter(id -> id != null && !id.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        if (externalUserids.isEmpty()) {
            return createTimeCache;
        }

        log.info("批量查询客户添加时间，数量: {}", externalUserids.size());

        // 批量从数据库查询
        try {
            List<IYQueCustomerInfo> customerInfos = iYqueCustomerInfoService.findByExternalUseridIn(externalUserids);
            for (IYQueCustomerInfo info : customerInfos) {
                if (info.getExternalUserid() != null && info.getUserId() != null && info.getAddTime() != null) {
                    String key = info.getExternalUserid() + "_" + info.getUserId();
                    String formattedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(info.getAddTime());
                    createTimeCache.put(key, formattedTime);
                }
            }
        } catch (Exception e) {
            log.warn("批量查询客户添加时间失败: {}", e.getMessage());
        }

        return createTimeCache;
    }

    /**
     * 根据external_userid获取客户姓名
     */
    private String getCustomerNameByExternalUserid(String externalUserid) {
        try {
            if (StrUtil.isEmpty(externalUserid)) {
                log.warn("externalUserid为空，无法获取客户姓名");
                return null;
            }

            // 从本地数据库查找客户信息
            List<IYQueCustomerInfo> customerInfos = iYqueCustomerInfoService.findByExternalUserid(externalUserid);
            if (CollectionUtil.isNotEmpty(customerInfos)) {
                IYQueCustomerInfo customerInfo = customerInfos.get(0);
                if (StrUtil.isNotEmpty(customerInfo.getCustomerName())) {
                    log.debug("从本地数据库获取到客户姓名: {}", customerInfo.getCustomerName());
                    return customerInfo.getCustomerName();
                }
            }

            // 如果本地没有，尝试从企业微信API获取
            try {
                WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
                // 使用推荐的API方法：getContactDetail
                WxCpExternalContactInfo externalContact = wxcpservice.getExternalContactService()
                        .getContactDetail(externalUserid, null);

                if (externalContact != null && externalContact.getExternalContact() != null) {
                    String customerName = externalContact.getExternalContact().getName();
                    if (StrUtil.isNotEmpty(customerName)) {
                        log.info("从企业微信API获取到客户姓名: {}", customerName);

                        // 尝试保存到本地数据库以便下次使用
                        try {
                            iYqueCustomerInfoService.saveCustomer(externalUserid);
                            log.debug("已将客户信息保存到本地数据库");
                        } catch (Exception saveException) {
                            log.warn("保存客户信息到本地数据库失败: {}", saveException.getMessage());
                        }

                        return customerName;
                    }
                }
            } catch (Exception apiException) {
                log.warn("从企业微信API获取客户信息失败，externalUserid: {}, 错误: {}", externalUserid, apiException.getMessage());
            }

            log.warn("无法获取客户姓名，externalUserid: {}", externalUserid);
            return null;

        } catch (Exception e) {
            log.error("获取客户姓名失败，externalUserid: {}, 错误: {}", externalUserid, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据userid获取员工姓名
     */
    private String getFollowUserNameByUserid(String userid) {
        try {
            if (StrUtil.isEmpty(userid)) {
                log.warn("userid为空，无法获取员工姓名");
                return null;
            }

            IYqueUser user = iYqueUserService.findOrSaveUser(userid);
            if (user != null && StrUtil.isNotEmpty(user.getName())) {
                log.debug("获取到员工姓名: {}", user.getName());
                return user.getName();
            }

            log.warn("无法获取员工姓名，userid: {}", userid);
            return null;
        } catch (Exception e) {
            log.error("获取员工姓名失败，userid: {}, 错误: {}", userid, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据external_userid和userid获取客户添加时间
     */
    private String getCustomerCreateTimeByExternalUserid(String externalUserid, String userid) {
        try {
            if (StrUtil.isEmpty(externalUserid)) {
                log.warn("externalUserid为空，无法获取客户添加时间");
                return null;
            }

            // 从本地数据库查找客户信息
            List<IYQueCustomerInfo> customerInfos = iYqueCustomerInfoService.findByExternalUserid(externalUserid);
            if (CollectionUtil.isNotEmpty(customerInfos)) {
                // 查找匹配的跟进人记录
                for (IYQueCustomerInfo customerInfo : customerInfos) {
                    if (StrUtil.isNotEmpty(customerInfo.getUserId()) && customerInfo.getUserId().equals(userid)) {
                        if (customerInfo.getAddTime() != null) {
                            String formattedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(customerInfo.getAddTime());
                            log.debug("获取到客户添加时间: {}", formattedTime);
                            return formattedTime;
                        }
                    }
                }

                // 如果没有找到匹配的跟进人，返回第一个记录的时间
                IYQueCustomerInfo customerInfo = customerInfos.get(0);
                if (customerInfo.getAddTime() != null) {
                    String formattedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(customerInfo.getAddTime());
                    log.debug("使用第一个记录的添加时间: {}", formattedTime);
                    return formattedTime;
                }
            }

            log.warn("无法获取客户添加时间，externalUserid: {}, userid: {}", externalUserid, userid);
            return null;

        } catch (Exception e) {
            log.error("获取客户添加时间失败，externalUserid: {}, userid: {}, 错误: {}", externalUserid, userid, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 转换会话状态
     * 企业微信原始状态 -> 新的状态定义
     * 根据API文档：0-客户未发消息 1-客户已发消息 2-客户发送消息状态未知
     */
    private Integer convertChatStatus(Object originalStatus) {
        if (originalStatus == null) {
            return CustomerChatStatus.NOT_STARTED.getCode();
        }

        if (originalStatus instanceof Integer) {
            Integer status = (Integer) originalStatus;
            CustomerChatStatus chatStatus = CustomerChatStatus.convertFromWeChatStatus(status);
            return chatStatus.getCode();
        }

        return CustomerChatStatus.NOT_STARTED.getCode(); // 默认未开口
    }

    /**
     * 将对象转换为Map，用于处理WxJava返回的对象
     */
    private Map<String, Object> convertObjectToMap(Object obj) {
        try {
            if (obj == null) {
                return null;
            }

            Map<String, Object> map = new HashMap<>();
            Class<?> clazz = obj.getClass();

            log.info("尝试解析对象类型: {}", clazz.getName());

            // 尝试获取所有字段
            java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    map.put(field.getName(), value);
                    log.debug("解析字段: {} = {}", field.getName(), value);
                } catch (Exception e) {
                    log.warn("获取字段值失败: {}, 错误: {}", field.getName(), e.getMessage());
                }
            }

            // 尝试获取所有getter方法
            java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
            for (java.lang.reflect.Method method : methods) {
                try {
                    String methodName = method.getName();
                    if ((methodName.startsWith("get") || methodName.startsWith("is"))
                            && method.getParameterCount() == 0
                            && !methodName.equals("getClass")) {

                        method.setAccessible(true);
                        Object value = method.invoke(obj);

                        // 转换方法名为字段名
                        String fieldName;
                        if (methodName.startsWith("get")) {
                            fieldName = methodName.substring(3);
                        } else {
                            fieldName = methodName.substring(2);
                        }

                        if (fieldName.length() > 0) {
                            fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                            map.put(fieldName, value);
                            log.debug("解析方法: {} -> {} = {}", methodName, fieldName, value);
                        }
                    }
                } catch (Exception e) {
                    log.warn("调用方法失败: {}, 错误: {}", method.getName(), e.getMessage());
                }
            }

            log.info("对象解析完成，共解析出 {} 个字段: {}", map.size(), map.keySet());
            return map;

        } catch (Exception e) {
            log.error("对象转换为Map失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void synchShortLinkByLinkIds(List<String> linkIds) {
        if (CollectionUtil.isEmpty(linkIds)) {
            log.warn("获客外链ID列表为空，无需同步");
            return;
        }

        try {
            log.info("开始同步指定的获客外链配置，数量: {}", linkIds.size());

            List<IYqueShortLink> shortLinks = new ArrayList<>();
            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();

            for (String linkId : linkIds) {
                try {
                    // 验证linkId格式
                    if (StrUtil.isEmpty(linkId) || linkId.trim().length() == 0) {
                        log.warn("跳过空的获客外链ID");
                        continue;
                    }

                    linkId = linkId.trim(); // 去除前后空格
                    log.info("正在同步获客外链，linkId: {}", linkId);

                    // 获取获客外链详情
                    WxCpCustomerAcquisitionInfo linkDetail = wxcpservice.getExternalContactService().customerAcquisitionLinkGet(linkId);
                    if (linkDetail != null) {
                        IYqueShortLink shortLink = convertToShortLink(linkDetail);
                        if (shortLink != null) {
                            shortLinks.add(shortLink);
                            log.info("成功同步获客外链，linkId: {}, 名称: {}", linkId, shortLink.getCodeName());
                        }
                    } else {
                        log.warn("获客外链详情为空，linkId: {}", linkId);
                    }
                } catch (Exception e) {
                    log.error("获取获客外链详情失败，linkId: {}, 错误: {}", linkId, e.getMessage());

                    // 检查是否是无效ID错误
                    if (e.getMessage() != null && e.getMessage().contains("invalid link_id")) {
                        log.error("无效的获客外链ID: {}，请检查ID是否正确。获客外链ID应该是企业微信后台生成的链接标识符", linkId);
                    }
                }
            }

            // 批量保存同步的数据
            if (CollectionUtil.isNotEmpty(shortLinks)) {
                // 分批保存，避免一次性保存过多数据
                ListUtil.partition(shortLinks, 50).forEach(batch -> {
                    try {
                        // 保存前验证configId字段
                        for (IYqueShortLink shortLink : batch) {
                            if (StrUtil.isEmpty(shortLink.getConfigId())) {
                                log.error("发现configId为空的记录，名称: {}, ID: {}", shortLink.getCodeName(), shortLink.getId());
                                throw new RuntimeException("configId不能为空");
                            }
                            log.debug("准备保存获客外链，configId: {}, 名称: {}", shortLink.getConfigId(), shortLink.getCodeName());
                        }

                        List<IYqueShortLink> savedLinks = iYqueShortLinkDao.saveAll(batch);
                        log.info("保存获客外链数据，数量: {}", batch.size());

                        // 保存后验证configId是否正确保存
                        for (IYqueShortLink savedLink : savedLinks) {
                            if (StrUtil.isEmpty(savedLink.getConfigId())) {
                                log.error("保存后发现configId为空，记录ID: {}, 名称: {}", savedLink.getId(), savedLink.getCodeName());
                            } else {
                                log.debug("成功保存获客外链，configId: {}, 名称: {}", savedLink.getConfigId(), savedLink.getCodeName());
                            }
                        }
                    } catch (Exception e) {
                        log.error("保存获客外链数据失败: {}", e.getMessage(), e);
                        // 尝试逐个保存，找出问题记录
                        for (IYqueShortLink shortLink : batch) {
                            try {
                                IYqueShortLink saved = iYqueShortLinkDao.save(shortLink);
                                log.info("单独保存成功，configId: {}, 名称: {}", saved.getConfigId(), saved.getCodeName());
                            } catch (Exception singleSaveException) {
                                log.error("单独保存失败，configId: {}, 名称: {}, 错误: {}",
                                        shortLink.getConfigId(), shortLink.getCodeName(), singleSaveException.getMessage());
                            }
                        }
                    }
                });
            }

            log.info("指定获客外链同步完成，共同步 {} 条数据", shortLinks.size());

        } catch (Exception e) {
            log.error("指定获客外链同步失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<IYqueKvalStrVo> getShortLinkConfigIds() {
        List<IYqueKvalStrVo> configIds = new ArrayList<>();

        try {
            // 从数据库获取所有获客外链的configId
            List<IYqueShortLink> shortLinks = iYqueShortLinkDao.findAll();

            for (IYqueShortLink shortLink : shortLinks) {
                if (StrUtil.isNotEmpty(shortLink.getConfigId()) && StrUtil.isNotEmpty(shortLink.getCodeName())) {
                    configIds.add(IYqueKvalStrVo.builder()
                            .val(shortLink.getConfigId())
                            .key(shortLink.getCodeName() + " (" + shortLink.getConfigId() + ")")
                            .build());
                }
            }

            log.info("获取到 {} 个获客外链配置ID", configIds.size());

        } catch (Exception e) {
            log.error("获取获客外链配置ID列表失败: {}", e.getMessage(), e);
        }

        return configIds;
    }

    /**
     * 修复现有数据中configId为空的记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void fixEmptyConfigIds() {
        try {
            log.info("开始修复configId为空的获客外链记录");

            // 查找所有configId为空的记录
            List<IYqueShortLink> emptyConfigIdLinks = iYqueShortLinkDao.findAll().stream()
                    .filter(link -> StrUtil.isEmpty(link.getConfigId()))
                    .collect(Collectors.toList());

            if (emptyConfigIdLinks.isEmpty()) {
                log.info("没有发现configId为空的记录");
                return;
            }

            log.info("发现 {} 条configId为空的记录，开始修复", emptyConfigIdLinks.size());

            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
            int fixedCount = 0;

            for (IYqueShortLink shortLink : emptyConfigIdLinks) {
                try {
                    // 尝试通过codeState或其他信息重新获取configId
                    if (StrUtil.isNotEmpty(shortLink.getCodeState())) {
                        // 如果有codeState，尝试从企业微信API获取所有外链，匹配找到对应的configId
                        String cursor = null;
                        int limit = 100;
                        boolean found = false;

                        do {
                            WxCpCustomerAcquisitionList acquisitionList = wxcpservice.getExternalContactService()
                                    .customerAcquisitionLinkList(limit, cursor);

                            if (acquisitionList != null && CollectionUtil.isNotEmpty(acquisitionList.getLinkIdList())) {
                                for (String linkId : acquisitionList.getLinkIdList()) {
                                    try {
                                        WxCpCustomerAcquisitionInfo linkDetail = wxcpservice.getExternalContactService()
                                                .customerAcquisitionLinkGet(linkId);

                                        if (linkDetail != null && linkDetail.getLink() != null) {
                                            // 通过名称匹配
                                            if (StrUtil.isNotEmpty(shortLink.getCodeName()) &&
                                                    StrUtil.isNotEmpty(linkDetail.getLink().getLinkName()) &&
                                                    shortLink.getCodeName().equals(linkDetail.getLink().getLinkName())) {

                                                // 从URL中提取真实的configId
                                                String realConfigId = null;
                                                String url = linkDetail.getLink().getUrl();
                                                if (StrUtil.isNotEmpty(url) && url.contains("/ca/")) {
                                                    String[] parts = url.split("/ca/");
                                                    if (parts.length > 1) {
                                                        String linkIdPart = parts[1];
                                                        if (linkIdPart.contains("?")) {
                                                            linkIdPart = linkIdPart.split("\\?")[0];
                                                        }
                                                        if (StrUtil.isNotEmpty(linkIdPart)) {
                                                            realConfigId = linkIdPart;
                                                        }
                                                    }
                                                }

                                                if (StrUtil.isNotEmpty(realConfigId)) {
                                                    shortLink.setConfigId(realConfigId);
                                                    iYqueShortLinkDao.save(shortLink);
                                                    fixedCount++;
                                                    found = true;
                                                    log.info("成功修复configId，名称: {}, configId: {}",
                                                            shortLink.getCodeName(), realConfigId);
                                                    break;
                                                } else {
                                                    log.warn("无法从URL中提取configId，URL: {}", url);
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        log.warn("获取外链详情失败，linkId: {}, 错误: {}", linkId, e.getMessage());
                                    }
                                }
                            }

                            cursor = acquisitionList != null ? acquisitionList.getNextCursor() : null;
                        } while (!found && StrUtil.isNotEmpty(cursor));

                        if (!found) {
                            log.warn("无法找到匹配的configId，名称: {}, ID: {}",
                                    shortLink.getCodeName(), shortLink.getId());
                        }
                    }
                } catch (Exception e) {
                    log.error("修复configId失败，记录ID: {}, 名称: {}, 错误: {}",
                            shortLink.getId(), shortLink.getCodeName(), e.getMessage());
                }
            }

            log.info("configId修复完成，共修复 {} 条记录", fixedCount);

        } catch (Exception e) {
            log.error("修复configId失败: {}", e.getMessage(), e);
            throw new RuntimeException("修复configId失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> testWxApi() throws Exception {
        Map<String, Object> result = new HashMap<>();

        try {
            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();

            // 测试1：获取获客外链列表
            log.info("开始测试获客外链列表API");
            WxCpCustomerAcquisitionList acquisitionList = wxcpservice.getExternalContactService()
                    .customerAcquisitionLinkList(10, null);

            if (acquisitionList != null) {
                result.put("listApiSuccess", true);
                result.put("linkCount", acquisitionList.getLinkIdList() != null ? acquisitionList.getLinkIdList().size() : 0);
                result.put("linkIds", acquisitionList.getLinkIdList());
                result.put("nextCursor", acquisitionList.getNextCursor());

                // 测试2：如果有外链ID，获取第一个外链的详情
                if (CollectionUtil.isNotEmpty(acquisitionList.getLinkIdList())) {
                    String firstLinkId = acquisitionList.getLinkIdList().get(0);
                    log.info("开始测试获客外链详情API，linkId: {}", firstLinkId);

                    try {
                        WxCpCustomerAcquisitionInfo linkDetail = wxcpservice.getExternalContactService()
                                .customerAcquisitionLinkGet(firstLinkId);

                        if (linkDetail != null) {
                            result.put("detailApiSuccess", true);

                            if (linkDetail.getLink() != null) {
                                WxCpCustomerAcquisitionInfo.Link link = linkDetail.getLink();

                                Map<String, Object> linkInfo = new HashMap<>();
                                linkInfo.put("linkName", link.getLinkName());
                                linkInfo.put("url", link.getUrl());

                                // 尝试获取linkId
                                try {
                                    String linkId = link.getLinkId();
                                    linkInfo.put("linkId", linkId);
                                    linkInfo.put("linkIdSuccess", true);
                                } catch (Exception e) {
                                    linkInfo.put("linkIdError", e.getMessage());
                                    linkInfo.put("linkIdSuccess", false);
                                }

                                // 使用反射获取所有字段
                                Map<String, Object> allFields = new HashMap<>();
                                try {
                                    java.lang.reflect.Field[] fields = link.getClass().getDeclaredFields();
                                    for (java.lang.reflect.Field field : fields) {
                                        field.setAccessible(true);
                                        try {
                                            Object value = field.get(link);
                                            allFields.put(field.getName(), value);
                                        } catch (Exception fieldException) {
                                            allFields.put(field.getName(), "访问失败: " + fieldException.getMessage());
                                        }
                                    }
                                } catch (Exception reflectException) {
                                    allFields.put("reflectionError", reflectException.getMessage());
                                }
                                linkInfo.put("allFields", allFields);

                                // 使用反射获取所有方法
                                Map<String, Object> allMethods = new HashMap<>();
                                try {
                                    java.lang.reflect.Method[] methods = link.getClass().getMethods();
                                    for (java.lang.reflect.Method method : methods) {
                                        if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
                                            try {
                                                Object value = method.invoke(link);
                                                allMethods.put(method.getName(), value);
                                            } catch (Exception methodException) {
                                                allMethods.put(method.getName(), "调用失败: " + methodException.getMessage());
                                            }
                                        }
                                    }
                                } catch (Exception reflectException) {
                                    allMethods.put("reflectionError", reflectException.getMessage());
                                }
                                linkInfo.put("allMethods", allMethods);

                                result.put("linkDetail", linkInfo);
                            } else {
                                result.put("linkDetailError", "linkDetail.getLink() 返回null");
                            }
                        } else {
                            result.put("detailApiSuccess", false);
                            result.put("detailApiError", "API返回null");
                        }
                    } catch (Exception detailException) {
                        result.put("detailApiSuccess", false);
                        result.put("detailApiError", detailException.getMessage());
                    }
                } else {
                    result.put("noLinksFound", true);
                }
            } else {
                result.put("listApiSuccess", false);
                result.put("listApiError", "API返回null");
            }

        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("success", false);
            log.error("测试企业微信API失败: {}", e.getMessage(), e);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> cleanDuplicateShortLinks() {
        Map<String, Object> result = new HashMap<>();

        try {
            log.info("开始清理重复的获客外链记录");

            // 获取所有记录
            List<IYqueShortLink> allLinks = iYqueShortLinkDao.findAll();
            log.info("总共找到 {} 条获客外链记录", allLinks.size());

            // 按configId分组
            Map<String, List<IYqueShortLink>> groupedByConfigId = allLinks.stream()
                    .filter(link -> StrUtil.isNotEmpty(link.getConfigId()))
                    .collect(Collectors.groupingBy(IYqueShortLink::getConfigId));

            int duplicateGroups = 0;
            int deletedCount = 0;

            for (Map.Entry<String, List<IYqueShortLink>> entry : groupedByConfigId.entrySet()) {
                String configId = entry.getKey();
                List<IYqueShortLink> duplicates = entry.getValue();

                if (duplicates.size() > 1) {
                    duplicateGroups++;
                    log.info("发现重复的configId: {}, 重复数量: {}", configId, duplicates.size());

                    // 保留最新的记录（按updateTime排序）
                    duplicates.sort((a, b) -> {
                        if (a.getUpdateTime() == null && b.getUpdateTime() == null) {
                            return 0;
                        }
                        if (a.getUpdateTime() == null) {
                            return 1;
                        }
                        if (b.getUpdateTime() == null) {
                            return -1;
                        }
                        return b.getUpdateTime().compareTo(a.getUpdateTime());
                    });

                    IYqueShortLink keepRecord = duplicates.get(0);
                    log.info("保留记录 ID: {}, 名称: {}, 更新时间: {}",
                            keepRecord.getId(), keepRecord.getCodeName(), keepRecord.getUpdateTime());

                    // 删除其他重复记录
                    for (int i = 1; i < duplicates.size(); i++) {
                        IYqueShortLink duplicateRecord = duplicates.get(i);
                        log.info("删除重复记录 ID: {}, 名称: {}, 更新时间: {}",
                                duplicateRecord.getId(), duplicateRecord.getCodeName(), duplicateRecord.getUpdateTime());

                        // 软删除
                        duplicateRecord.setDelFlag(1);
                        iYqueShortLinkDao.save(duplicateRecord);
                        deletedCount++;
                    }
                }
            }

            // 处理configId为空的记录
            List<IYqueShortLink> emptyConfigIdLinks = allLinks.stream()
                    .filter(link -> StrUtil.isEmpty(link.getConfigId()))
                    .collect(Collectors.toList());

            if (!emptyConfigIdLinks.isEmpty()) {
                log.info("发现 {} 条configId为空的记录，建议手动处理", emptyConfigIdLinks.size());
                for (IYqueShortLink emptyLink : emptyConfigIdLinks) {
                    log.info("空configId记录 - ID: {}, 名称: {}, URL: {}",
                            emptyLink.getId(), emptyLink.getCodeName(), emptyLink.getCodeUrl());
                }
            }

            result.put("success", true);
            result.put("totalRecords", allLinks.size());
            result.put("duplicateGroups", duplicateGroups);
            result.put("deletedCount", deletedCount);
            result.put("emptyConfigIdCount", emptyConfigIdLinks.size());

            log.info("清理完成 - 总记录数: {}, 重复组数: {}, 删除记录数: {}, 空configId记录数: {}",
                    allLinks.size(), duplicateGroups, deletedCount, emptyConfigIdLinks.size());

        } catch (Exception e) {
            log.error("清理重复记录失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }
}
