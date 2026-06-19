package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import cn.iyque.dao.IYQueRobotDao;
import cn.iyque.dao.IYQueRobotSubDao;
import cn.iyque.domain.RobotMsgDto;
import cn.iyque.entity.IYqueRobot;
import cn.iyque.entity.IYqueRobotSub;
import cn.iyque.service.IYQueRobotService;
import cn.iyque.service.IYqueConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class IYQueRobotServiceImpl implements IYQueRobotService {


    @Autowired
    IYQueRobotDao iyQueRobotDao;

    @Autowired
    IYQueRobotSubDao iyQueRobotSubDao;


    @Autowired
    IYqueConfigService iYqueConfigService;




    @Override
    public void addOrUpdate(IYqueRobot yqueRobot) {
        iyQueRobotDao.saveAndFlush(yqueRobot);
    }

    @Override
    public void batchDelete(Long[] ids) {
        iyQueRobotDao.deleteAllByIdInBatch(Arrays.asList(ids));
    }

    @Override
    public Page<IYqueRobot> findAll(Pageable pageable) {

        return iyQueRobotDao.findAll(pageable);
    }

    @Override
    public Page<IYqueRobotSub> findRobotSubAll(Long robotId, Pageable pageable) {
        Specification<IYqueRobotSub> spec = Specification.where(null);

        if (robotId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("robotId")), robotId));
        }
        return iyQueRobotSubDao.findAll(spec,pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendRobotMsg(IYqueRobot iYqueRobot) throws Exception {

        Optional<IYqueRobot> optional = iyQueRobotDao.findById(iYqueRobot.getRobotId());
        if(optional.isPresent()){
            List<IYqueRobotSub> robotSubList = iYqueRobot.getRobotSubList();
            if(CollectionUtil.isNotEmpty(robotSubList)){
                IYqueRobotSub iYqueRobotSub = robotSubList.stream().findFirst().get();
                iYqueRobotSub.setMsgTitle(iYqueRobot.getMsgTitle());
                iYqueRobotSub.setRobotId(iYqueRobot.getRobotId());
                iYqueRobotSub.prePersist(iYqueRobotSub);
                iYqueRobotSub.setSendTime(new Date());
                iyQueRobotSubDao.saveAll(robotSubList);

                String webHookUrl = optional.get().getWebHookUrl();
                if(StringUtils.isNotEmpty(webHookUrl)){
                    RobotMsgDto robotMsgDto = RobotMsgDto.buildRobotMsg(iYqueRobotSub);
                    if(null != robotMsgDto){
                        // 发送请求
                        String response = HttpRequest.post(webHookUrl)
                                .header("Content-Type", "application/json") // 设置请求头
                                .body(JSONUtil.toJsonStr(robotMsgDto))                      // 设置请求体
                                .execute()
                                .body();
                    }else{
                        throw new Exception("消息不可为空");
                    }

                }


            }
        }



    }



}
