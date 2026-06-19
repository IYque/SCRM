package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.CodeStateConstant;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueUserCodeDao;
import cn.iyque.domain.*;
import cn.iyque.entity.*;
import cn.iyque.service.*;
import cn.iyque.utils.DateUtils;
import cn.iyque.utils.FileUtils;
import cn.iyque.utils.SnowFlakeUtils;
import cn.iyque.enums.SynchDataRecordType;
import cn.iyque.dao.IYqueSynchDataRecordDao;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpExternalContactService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.article.NewArticle;
import me.chanjar.weixin.cp.bean.external.WxCpContactWayInfo;
import me.chanjar.weixin.cp.bean.external.WxCpContactWayResult;
// import me.chanjar.weixin.cp.bean.external.WxCpContactWayList; // 如果WxJava版本不支持，先注释掉
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IYqueUserCodeServiceImpl implements IYqueUserCodeService {

    @Autowired
    private IYqueUserCodeDao iYqueUserCodeDao;

    @Autowired
    private IYqueConfigService iYqueConfigService;

    @Autowired
    private IYqueMsgAnnexService iYqueMsgAnnexService;

    @Autowired
    private IYqueParamConfig iYqueParamConfig;

    @Autowired
    private IYquePeriodMsgAnnexService iYquePeriodMsgAnnexService;

    @Autowired
    private IYqueAnnexPeriodService iYqueAnnexPeriodService;

    @Autowired
    private IYqueSynchDataRecordDao iYqueSynchDataRecordDao;

    @Autowired
    private IYqueUserService iYqueUserService;

    @Override
    public Page<IYqueUserCode> findAll(Pageable pageable) {
        return iYqueUserCodeDao.findAll(pageable);
    }

    @Override
    public Page<IYqueUserCode> findAll(  IYqueUserCode iYqueUserCode, Pageable pageable) {
        Specification<IYqueUserCode> spec = Specification.where(null);



        // 按活码名称搜索
        if (StringUtils.hasText(iYqueUserCode.getCodeName())) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("codeName")), "%" + iYqueUserCode.getCodeName().toLowerCase() + "%"));
        }

        // 按员工名称搜索
        if (StringUtils.hasText(iYqueUserCode.getUserName())) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("userName")), "%" + iYqueUserCode.getUserName().toLowerCase() + "%"));
        }

        //按照时间查询
        if (iYqueUserCode.getStartTime() != null && iYqueUserCode.getEndTime() != null)  {
            spec = spec.and((root, query, cb) -> cb.between(root.get("createTime"), DateUtils.setTimeToStartOfDay( iYqueUserCode.getStartTime()), DateUtils.setTimeToEndOfDay( iYqueUserCode.getEndTime())));
        }


        return iYqueUserCodeDao.findAll(spec, pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(IYqueUserCode iYqueUserCode) throws Exception {
        try {
            iYqueUserCode.setCreateTime(new Date());
            iYqueUserCode.setUpdateTime(new Date());
            iYqueUserCode.setCodeState(CodeStateConstant.USER_CODE_STATE+ SnowFlakeUtils.nextId());

            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();

            WxCpContactWayInfo wxCpGroupJoinWayInfo=new WxCpContactWayInfo();
            NewContactWay contactWay=new NewContactWay();
            contactWay.setType(WxCpContactWayInfo.TYPE.MULTI);
            contactWay.setScene(WxCpContactWayInfo.SCENE.QRCODE);
            contactWay.setSkipVerify(iYqueUserCode.getSkipVerify());
            contactWay.setState(iYqueUserCode.getCodeState());
            contactWay.setUsers(ListUtil.toList(iYqueUserCode.getUserId().split(",")));
            contactWay.setExclusive(iYqueUserCode.getIsExclusive() != null ? iYqueUserCode.getIsExclusive() : false);
            wxCpGroupJoinWayInfo.setContactWay(contactWay);

            WxCpContactWayResult wxCpContactWayResult = wxcpservice.getExternalContactService().addContactWay(wxCpGroupJoinWayInfo);
            if(null != wxCpContactWayResult
                    && StrUtil.isNotEmpty(wxCpContactWayResult.getQrCode())
                    &&StrUtil.isNotEmpty(wxCpContactWayResult.getConfigId())){
                iYqueUserCode.setCodeUrl(wxCpContactWayResult.getQrCode());
                iYqueUserCode.setBackupQrUrl(wxCpContactWayResult.getQrCode());
                //替换自定义logo的二维码
                if(StrUtil.isNotEmpty(iYqueUserCode.getLogoUrl())){
                    String newQrUlr = FileUtils.buildQr(wxCpContactWayResult.getQrCode(),
                            iYqueUserCode.getLogoUrl(), iYqueParamConfig.getUploadDir());
                    if(StrUtil.isNotEmpty(newQrUlr)){
                        iYqueUserCode.setCodeUrl(newQrUlr);
                    }
                }
                iYqueUserCode.setConfigId(wxCpContactWayResult.getConfigId());
                iYqueUserCodeDao.save(iYqueUserCode);

                //时段欢迎语附件
                if(iYqueUserCode.isStartPeriodAnnex()){
                    List<IYqueAnnexPeriod> periodAnnexLists=iYqueUserCode.getPeriodAnnexLists();
                    if(CollectionUtil.isNotEmpty(periodAnnexLists)){
                        //时段附件
                        List<IYquePeriodMsgAnnex> iYquePeriodMsgAnnexes=new ArrayList<>();
                        periodAnnexLists.stream().forEach(k->{
                            k.setMsgId(iYqueUserCode.getId());
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

                    //非时段欢迎语附件
                }else{
                    List<IYqueMsgAnnex> annexLists = iYqueUserCode.getAnnexLists();
                    if(CollectionUtil.isNotEmpty(annexLists)){
                        annexLists.stream().forEach(k->{
                            k.setMsgId(iYqueUserCode.getId());
                        });
                        iYqueMsgAnnexService.saveAll(annexLists);
                    }
                }




            }


        }catch (Exception e){
            throw e;
        }

    }

    @Override
    public List<IYqueKvalStrVo> findIYqueUserCodeKvs() {
        List<IYqueKvalStrVo> iYqueKvalVos=new ArrayList<>();


        List<IYqueUserCode> iYqueUserCodes = iYqueUserCodeDao.findAll();
        if(CollectionUtil.isNotEmpty(iYqueUserCodes)){
            iYqueUserCodes.stream().forEach(k->{

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
    public void update(IYqueUserCode iYqueUserCode) throws Exception {

        try {
            IYqueUserCode oldIYqueUserCode = this.findIYqueUserCodeById(iYqueUserCode.getId());
            if(null != oldIYqueUserCode){
                iYqueUserCode.setCodeUrl(oldIYqueUserCode.getCodeUrl());
                iYqueUserCode.setCodeState(oldIYqueUserCode.getCodeState());
                iYqueUserCode.setConfigId(oldIYqueUserCode.getConfigId());

                WxCpContactWayInfo wxCpGroupJoinWayInfo=new WxCpContactWayInfo();
                NewContactWay contactWay=new NewContactWay();
                contactWay.setConfigId(oldIYqueUserCode.getConfigId());
                contactWay.setSkipVerify(iYqueUserCode.getSkipVerify());
                contactWay.setUsers(ListUtil.toList(iYqueUserCode.getUserId().split(",")));
                contactWay.setExclusive(iYqueUserCode.getIsExclusive() != null ? iYqueUserCode.getIsExclusive() : false);
                wxCpGroupJoinWayInfo.setContactWay(contactWay);

                WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
                WxCpExternalContactService externalContactService = wxcpservice.getExternalContactService();
                externalContactService.updateContactWay(wxCpGroupJoinWayInfo);

                //替换自定义logo的二维码
                if(StrUtil.isNotEmpty(iYqueUserCode.getLogoUrl())){
                    //判断原有logo是否改变，如果改变则更新
                    if(!iYqueUserCode.getLogoUrl().equals(
                            oldIYqueUserCode.getLogoUrl()
                    )){
                        String newQrUlr = FileUtils.buildQr(iYqueUserCode.getBackupQrUrl(),
                                iYqueUserCode.getLogoUrl(), iYqueParamConfig.getUploadDir());
                        if(StrUtil.isNotEmpty(newQrUlr)){
                            iYqueUserCode.setCodeUrl(newQrUlr);
                        }
                    }

                }else{
                    iYqueUserCode.setCodeUrl(oldIYqueUserCode.getBackupQrUrl());
                }
            }

            iYqueUserCode.setUpdateTime(new Date());
            iYqueUserCodeDao.saveAndFlush(iYqueUserCode);

            if(iYqueUserCode.isStartPeriodAnnex()){//开启时段欢迎语

                List<IYqueAnnexPeriod> periodAnnexLists=iYqueUserCode.getPeriodAnnexLists();
                if(CollectionUtil.isNotEmpty(periodAnnexLists)){

                    //时段附件
                    List<IYquePeriodMsgAnnex> iYquePeriodMsgAnnexes=new ArrayList<>();
                    periodAnnexLists.stream().forEach(k->{
                        k.setMsgId(iYqueUserCode.getId());
                    });

                    //存储时段
                    List<IYqueAnnexPeriod> oldIYqueAnnexPeriod = iYqueAnnexPeriodService
                            .findIYqueAnnexPeriodByMsgId(iYqueUserCode.getId());

                    if(CollectionUtil.isNotEmpty(oldIYqueAnnexPeriod)){
                        iYqueAnnexPeriodService.deleteIYqueAnnexPeriodByMsgId(iYqueUserCode.getId());
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
                iYqueMsgAnnexService.deleteIYqueMsgAnnexByMsgId(iYqueUserCode.getId());
                List<IYqueMsgAnnex> annexLists = iYqueUserCode.getAnnexLists();
                if(CollectionUtil.isNotEmpty(annexLists)){
                    annexLists.stream().forEach(k->{
                        k.setMsgId(iYqueUserCode.getId());
                    });
                    iYqueMsgAnnexService.saveAll(annexLists);
                }

            }

        }catch (Exception e){

            throw e;
        }

    }

    @Override
    public IYqueUserCode findIYqueUserCodeById(Long id) {
        return iYqueUserCodeDao.getById(id);
    }

    @Override
    public void batchDelete(Long[] ids){
        List<IYqueUserCode> iYqueUserCodes = iYqueUserCodeDao.findAllById(Arrays.asList(ids));

        if(CollectionUtil.isNotEmpty(iYqueUserCodes)){
            iYqueUserCodes.stream().forEach(k->{
                k.setDelFlag(IYqueContant.DEL_STATE);

                try {
                    iYqueConfigService.findWxcpservice().getExternalContactService().deleteContactWay(k.getConfigId());
                    iYqueUserCodeDao.saveAndFlush(k);

                }catch (Exception e){
                    log.error("活码删除失败:"+e.getMessage());
                }

            });

        }

    }

    @Override
    public void distributeUserCode(Long id) throws Exception {

        try {
            IYqueUserCode iYqueUserCode = findIYqueUserCodeById(id);
            if(iYqueUserCode != null){
                IYqueConfig iYqueConfig = iYqueConfigService.findIYqueConfig();
                if(null != iYqueConfig){
                    WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
                    NewArticle newArticle=new NewArticle();
                    newArticle.setDescription("渠道活码,点击保存即可");
                    newArticle.setTitle(iYqueUserCode.getCodeName());


                    if(StrUtil.isNotEmpty(iYqueUserCode.getCodeUrl())){
                        if (iYqueUserCode.getCodeUrl().startsWith("http://") || iYqueUserCode.getCodeUrl().startsWith("https://")){
                            newArticle.setUrl(iYqueUserCode.getCodeUrl());
                            newArticle.setPicUrl(iYqueUserCode.getCodeUrl());
                        }else {
                            newArticle.setUrl(iYqueParamConfig.getFileViewUrl()+iYqueUserCode.getCodeUrl());
                            newArticle.setPicUrl(iYqueParamConfig.getFileViewUrl()+iYqueUserCode.getCodeUrl());

                        }
                    }


                    wxcpservice.getMessageService().send(WxCpMessage.NEWS()
                            .toUser(iYqueUserCode.getUserId().replace(",", "|"))
                            .agentId(new Integer(iYqueConfig.getAgentId()))
                            .addArticle(newArticle)
                            .build());
                }

            }
        }catch (Exception e){
            throw e;
        }




    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void synchUserCode() {
        try {
            log.info("开始同步员工活码（联系我配置）...");
            long syncStartTime = System.currentTimeMillis();

            List<IYqueUserCode> userCodes = new ArrayList<>();
            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();

            String cursor = null;
            int limit = 100;

            // 获取上次同步的游标位置
            IYqueSynchDataRecord lastRecord = iYqueSynchDataRecordDao
                    .findTopBySynchDataTypeOrderByCreateTimeDesc(SynchDataRecordType.RECORD_TYPE_SYNCH_USER_CODE.getCode());

            if (lastRecord != null && StrUtil.isNotEmpty(lastRecord.getNextCursor())) {
                cursor = lastRecord.getNextCursor();
            }

            // 设置时间范围：获取全部数据
            // 使用0作为起始时间，确保获取所有历史数据
            long endTime = System.currentTimeMillis() / 1000; // 当前时间戳（秒）
            long startTime = 0; // 从最早开始获取所有数据

            do {
                try {
                    // 调用企业微信API获取联系我配置列表
                    log.info("正在获取联系我配置列表，cursor: {}, limit: {}", cursor, limit);

                    // 尝试调用企业微信联系我配置列表API
                    ContactWayListResult result = getContactWayConfigIds(wxcpservice, startTime, endTime, cursor, limit);
                    List<String> configIds = result.getConfigIds();

                    if (CollectionUtil.isNotEmpty(configIds)) {
                        for (String configId : configIds) {
                            try {
                                // 获取联系我配置详情
                                WxCpContactWayInfo contactWayInfo = wxcpservice.getExternalContactService().getContactWay(configId);
                                if (contactWayInfo != null) {
                                    IYqueUserCode userCode = convertToUserCode(contactWayInfo);
                                    if (userCode != null) {
                                        userCodes.add(userCode);
                                    }
                                }
                            } catch (Exception e) {
                                log.error("获取联系我配置详情失败，configId: {}, 错误: {}", configId, e.getMessage());
                            }
                        }

                        // 使用API返回的真实nextCursor
                        cursor = result.getNextCursor();
                        if (StrUtil.isEmpty(cursor) && configIds.size() >= limit) {
                            // 如果API没有返回nextCursor但数据量达到limit，可能还有更多数据
                            // 这种情况下我们停止分页，避免无限循环
                            log.warn("API未返回nextCursor但数据量达到limit，停止分页获取");
                            cursor = null;
                        }
                    } else {
                        cursor = null;
                    }

                } catch (Exception e) {
                    log.error("获取联系我配置列表失败: {}", e.getMessage());
                    break;
                }

                // 记录同步进度
                if (StrUtil.isNotEmpty(cursor)) {
                    iYqueSynchDataRecordDao.save(
                            IYqueSynchDataRecord.builder()
                                    .synchDataType(SynchDataRecordType.RECORD_TYPE_SYNCH_USER_CODE.getCode())
                                    .nextCursor(cursor)
                                    .createTime(new Date())
                                    .build()
                    );
                }

            } while (StrUtil.isNotEmpty(cursor));

            // 批量保存同步的数据
            if (CollectionUtil.isNotEmpty(userCodes)) {
                // 分批保存，避免一次性保存过多数据
                ListUtil.partition(userCodes, 50).forEach(batch -> {
                    try {
                        iYqueUserCodeDao.saveAll(batch);
                        log.info("保存员工活码数据，数量: {}", batch.size());
                    } catch (Exception e) {
                        log.error("保存员工活码数据失败: {}", e.getMessage());
                    }
                });
            }

            // 统计同步结果
            long syncEndTime = System.currentTimeMillis();
            long syncDuration = syncEndTime - syncStartTime;

            // 统计新增和更新的数量
            long newCount = userCodes.stream().filter(code -> code.getId() == null).count();
            long updateCount = userCodes.size() - newCount;

            log.info("员工活码同步完成！");
            log.info("同步统计：总数据量: {}, 新增: {}, 更新: {}", userCodes.size(), newCount, updateCount);
            log.info("同步耗时: {} 毫秒", syncDuration);

            // 检查是否可能有更多数据
            if (userCodes.size() >= limit) {
                log.warn("本次同步数据量达到单次限制({}条)，可能还有更多数据未同步", limit);
                log.warn("建议检查企业微信后台是否有更多联系我配置");
            }

        } catch (Exception e) {
            log.error("员工活码同步失败: {}", e.getMessage(), e);
        }
    }


    /**
     * 将企业微信联系我配置信息转换为本地实体对象
     */
    private IYqueUserCode convertToUserCode(WxCpContactWayInfo contactWayInfo) {
        if (contactWayInfo == null || contactWayInfo.getContactWay() == null) {
            return null;
        }

        try {
            WxCpContactWayInfo.ContactWay contactWay = contactWayInfo.getContactWay();

            // 检查是否已存在相同configId的记录
            IYqueUserCode existingUserCode = iYqueUserCodeDao.findByConfigId(contactWay.getConfigId());
            IYqueUserCode userCode;

            if (existingUserCode != null) {
                // 更新现有记录
                userCode = existingUserCode;
                userCode.setUpdateTime(new Date());
                log.info("更新现有员工活码，configId: {}", contactWay.getConfigId());
            } else {
                // 创建新记录
                userCode = new IYqueUserCode();
                userCode.setCreateTime(new Date());
                userCode.setUpdateTime(new Date());
                userCode.setDelFlag(0);
                // 生成渠道标识
                userCode.setCodeState(CodeStateConstant.USER_CODE_STATE + SnowFlakeUtils.nextId());
                log.info("创建新员工活码，configId: {}", contactWay.getConfigId());
            }

            // 基本信息
            userCode.setConfigId(contactWay.getConfigId());

            // 设置二维码地址 - 增强补全逻辑
            String qrCodeUrl = getQrCodeUrl(contactWayInfo, contactWay);
            if (StrUtil.isNotEmpty(qrCodeUrl)) {
                userCode.setCodeUrl(qrCodeUrl);
                userCode.setBackupQrUrl(qrCodeUrl);
            } else if (StrUtil.isEmpty(userCode.getCodeUrl())) {
                // 如果API没有返回二维码URL，生成一个默认的或者从其他地方获取
                String defaultQrUrl = generateDefaultQrCodeUrl(contactWay.getConfigId());
                userCode.setCodeUrl(defaultQrUrl);
                userCode.setBackupQrUrl(defaultQrUrl);
                log.info("为configId: {} 生成默认二维码URL: {}", contactWay.getConfigId(), defaultQrUrl);
            }

            // 设置验证方式
            userCode.setSkipVerify(contactWay.getSkipVerify() != null ? contactWay.getSkipVerify() : false);

            // 设置是否排他（WxJava 4.7.6.B版本的ContactWay已经支持isExclusive字段）
            userCode.setIsExclusive(contactWay.isExclusive());

            // 设置欢迎语 - 数据补全
            if (StrUtil.isEmpty(userCode.getWeclomeMsg()) && StrUtil.isNotEmpty(contactWay.getRemark())) {
                userCode.setWeclomeMsg(contactWay.getRemark());
            } else if (StrUtil.isEmpty(userCode.getWeclomeMsg())) {
                userCode.setWeclomeMsg("欢迎添加我的企业微信！");
            }

            // 设置Logo URL - 数据补全
            if (StrUtil.isEmpty(userCode.getLogoUrl())) {
                userCode.setLogoUrl("https://work.weixin.qq.com/default_avatar.png"); // 默认头像
            }

            // 处理员工信息
            if (CollectionUtil.isNotEmpty(contactWay.getUsers())) {
                userCode.setUserId(String.join(",", contactWay.getUsers()));

                // 补全员工姓名
                List<String> userNames = new ArrayList<>();
                for (String userId : contactWay.getUsers()) {
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
                userCode.setUserName(String.join(",", userNames));
            }

            // 设置活码名称（如果为空则使用默认名称）- 增强补全逻辑
            if (StrUtil.isEmpty(userCode.getCodeName())) {
                String codeName = generateCodeName(contactWay, userCode);
                userCode.setCodeName(codeName);
            }

            // 设置标签信息 - 数据补全
            if (StrUtil.isEmpty(userCode.getTagName()) && CollectionUtil.isNotEmpty(contactWay.getUsers())) {
                // 基于员工信息生成标签
                userCode.setTagName("员工活码_" + userCode.getUserName());
            }

            // 设置默认值 - 完善补全逻辑
            if (!userCode.isStartPeriodAnnex()) {
                userCode.setStartPeriodAnnex(false);
            }
            if (userCode.getRemarkType() == null) {
                userCode.setRemarkType(1); // 默认备注类型
            }
            if (StrUtil.isEmpty(userCode.getCodeState())) {
                userCode.setCodeState("ACTIVE"); // 默认状态：启用
            }

            return userCode;

        } catch (Exception e) {
            log.error("转换联系我配置数据失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取二维码URL
     */
    private String getQrCodeUrl(WxCpContactWayInfo contactWayInfo, WxCpContactWayInfo.ContactWay contactWay) {
        try {
            // 尝试从contactWayInfo中获取二维码URL
            if (contactWayInfo != null) {
                // 使用反射尝试获取qrCode字段
                try {
                    Object qrCode = contactWayInfo.getClass().getMethod("getQrCode").invoke(contactWayInfo);
                    if (qrCode != null && StrUtil.isNotEmpty(qrCode.toString())) {
                        return qrCode.toString();
                    }
                } catch (Exception e) {
                    log.debug("无法从contactWayInfo获取qrCode: {}", e.getMessage());
                }
            }

            // 尝试从contactWay中获取
            if (contactWay != null) {
                try {
                    Object qrCode = contactWay.getClass().getMethod("getQrCode").invoke(contactWay);
                    if (qrCode != null && StrUtil.isNotEmpty(qrCode.toString())) {
                        return qrCode.toString();
                    }
                } catch (Exception e) {
                    log.debug("无法从contactWay获取qrCode: {}", e.getMessage());
                }
            }

            return null;
        } catch (Exception e) {
            log.warn("获取二维码URL失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 生成默认二维码URL
     */
    private String generateDefaultQrCodeUrl(String configId) {
        // 这里可以根据实际需求生成默认的二维码URL
        // 例如：调用企业微信API生成二维码，或者使用默认的占位符
        return "https://work.weixin.qq.com/ca/cawcde" + configId.substring(0, Math.min(8, configId.length()));
    }

    /**
     * 生成活码名称
     */
    private String generateCodeName(WxCpContactWayInfo.ContactWay contactWay, IYqueUserCode userCode) {
        // 优先使用员工姓名
        if (StrUtil.isNotEmpty(userCode.getUserName())) {
            return userCode.getUserName() + "的联系我";
        }

        // 其次使用configId的一部分
        if (StrUtil.isNotEmpty(contactWay.getConfigId())) {
            return "联系我配置_" + contactWay.getConfigId().substring(0, Math.min(8, contactWay.getConfigId().length()));
        }

        // 最后使用默认名称
        return "企业微信联系我";
    }

    /**
     * 获取联系我配置ID列表和下一页游标
     * 使用WxJava 4.7.x版本的listContactWay方法
     */
    private ContactWayListResult getContactWayConfigIds(WxCpService wxcpservice, long startTime, long endTime, String cursor, int limit) {
        ContactWayListResult result = new ContactWayListResult();
        List<String> configIds = new ArrayList<>();

        try {
            // 使用WxJava 4.7.x版本的listContactWay方法
            log.info("调用企业微信API获取联系我配置列表，startTime: {}, endTime: {}, cursor: {}, limit: {}",
                    startTime, endTime, cursor, limit);

            // 使用反射调用listContactWay方法，因为可能不同版本的WxJava类名不同
            Object contactWayListResult = wxcpservice.getExternalContactService()
                    .getClass()
                    .getMethod("listContactWay", Long.class, Long.class, String.class, Long.class)
                    .invoke(wxcpservice.getExternalContactService(), startTime, endTime, cursor, (long) limit);

            if (contactWayListResult != null) {
                // 通过反射获取contactWay列表
                Object contactWayList = contactWayListResult.getClass().getMethod("getContactWay").invoke(contactWayListResult);
                if (contactWayList instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Object> configList = (List<Object>) contactWayList;

                    for (Object configObj : configList) {
                        try {
                            String configId = (String) configObj.getClass().getMethod("getConfigId").invoke(configObj);
                            if (StrUtil.isNotEmpty(configId)) {
                                configIds.add(configId);
                            }
                        } catch (Exception e) {
                            log.warn("获取configId失败: {}", e.getMessage());
                        }
                    }
                }

                // 尝试获取下一页游标
                try {
                    String nextCursor = (String) contactWayListResult.getClass().getMethod("getNextCursor").invoke(contactWayListResult);
                    result.setNextCursor(nextCursor);
                    if (StrUtil.isNotEmpty(nextCursor)) {
                        log.info("获取到下一页游标: {}", nextCursor);
                    }
                } catch (Exception e) {
                    log.debug("无法获取nextCursor: {}", e.getMessage());
                }
            }

            result.setConfigIds(configIds);
            log.info("成功获取到 {} 个联系我配置", configIds.size());
            return result;

        } catch (Exception e) {
            log.error("获取联系我配置ID列表失败: {}", e.getMessage(), e);
            log.warn("请确保：");
            log.warn("1. WxJava版本支持listContactWay方法（当前版本：4.7.6.B）");
            log.warn("2. 企业微信应用具有外部联系人管理权限");
            log.warn("3. 联系我配置是在2021年7月10日之后创建的");
            result.setConfigIds(configIds);
            return result;
        }
    }

    /**
     * 联系我配置列表结果类
     */
    private static class ContactWayListResult {
        private List<String> configIds = new ArrayList<>();
        private String nextCursor;

        public List<String> getConfigIds() {
            return configIds;
        }

        public void setConfigIds(List<String> configIds) {
            this.configIds = configIds;
        }

        public String getNextCursor() {
            return nextCursor;
        }

        public void setNextCursor(String nextCursor) {
            this.nextCursor = nextCursor;
        }
    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void synchUserCodeByConfigIds(List<String> configIds) {
        if (CollectionUtil.isEmpty(configIds)) {
            log.warn("配置ID列表为空，无需同步");
            return;
        }

        try {
            log.info("开始同步指定的员工活码配置，数量: {}", configIds.size());

            List<IYqueUserCode> userCodes = new ArrayList<>();
            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();

            for (String configId : configIds) {
                try {
                    // 获取联系我配置详情
                    WxCpContactWayInfo contactWayInfo = wxcpservice.getExternalContactService().getContactWay(configId);
                    if (contactWayInfo != null) {
                        IYqueUserCode userCode = convertToUserCode(contactWayInfo);
                        if (userCode != null) {
                            userCodes.add(userCode);
                        }
                    }
                } catch (Exception e) {
                    log.error("获取联系我配置详情失败，configId: {}, 错误: {}", configId, e.getMessage());
                }
            }

            // 批量保存同步的数据
            if (CollectionUtil.isNotEmpty(userCodes)) {
                // 分批保存，避免一次性保存过多数据
                ListUtil.partition(userCodes, 50).forEach(batch -> {
                    try {
                        iYqueUserCodeDao.saveAll(batch);
                        log.info("保存员工活码数据，数量: {}", batch.size());
                    } catch (Exception e) {
                        log.error("保存员工活码数据失败: {}", e.getMessage());
                    }
                });
            }

            log.info("指定配置ID的员工活码同步完成，共同步 {} 条数据", userCodes.size());

        } catch (Exception e) {
            log.error("指定配置ID的员工活码同步失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<IYqueKvalStrVo> getUserCodeConfigIds() {
        List<IYqueKvalStrVo> configIds = new ArrayList<>();

        try {
            // 从数据库获取所有员工活码的configId
            List<IYqueUserCode> userCodes = iYqueUserCodeDao.findAll();

            for (IYqueUserCode userCode : userCodes) {
                if (StrUtil.isNotEmpty(userCode.getConfigId()) && StrUtil.isNotEmpty(userCode.getCodeName())) {
                    configIds.add(IYqueKvalStrVo.builder()
                            .val(userCode.getConfigId())
                            .key(userCode.getCodeName() + " (" + userCode.getConfigId() + ")")
                            .build());
                }
            }

            log.info("获取到 {} 个员工活码配置ID", configIds.size());

        } catch (Exception e) {
            log.error("获取员工活码配置ID列表失败: {}", e.getMessage(), e);
        }

        return configIds;
    }

}
