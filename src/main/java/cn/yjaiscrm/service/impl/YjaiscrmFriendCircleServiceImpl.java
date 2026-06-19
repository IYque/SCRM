package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iyque.domain.IYqueFriendCircle;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.mapper.IYqueFriendCircleMapper;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueFriendCircleService;
import cn.iyque.service.IYqueMsgAnnexService;
import cn.iyque.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.external.WxCpAddMomentResult;
import me.chanjar.weixin.cp.bean.external.WxCpAddMomentTask;
import me.chanjar.weixin.cp.bean.external.WxCpGetMomentTaskResult;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;
import me.chanjar.weixin.cp.bean.external.msg.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class IYqueFriendCircleServiceImpl extends ServiceImpl<IYqueFriendCircleMapper, IYqueFriendCircle> implements IYqueFriendCircleService {

    @Autowired
    private IYqueMsgAnnexService iYqueMsgAnnexService;

    @Autowired
    private IYqueConfigService iYqueConfigService;

    @Override
    public void createMoment(IYqueFriendCircle friendCircle) throws Exception {
        try {
            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
            WxCpAddMomentTask task=new WxCpAddMomentTask();
            Text text = new Text();
            text.setContent(friendCircle.getContent());
            task.setText(text);
            List<IYqueMsgAnnex> annexLists= friendCircle.getAnnexLists();

            if(CollectionUtil.isNotEmpty(annexLists)){
                List<Attachment> attachments = SpringUtil.getBean(IYqueMsgAnnexService.class)
                        .msgAnnexToAttachment(annexLists,1);
                task.setAttachments(attachments);
            }

            WxCpAddMomentResult wxCpAddMomentResult = wxcpservice.getExternalContactService().addMomentTask(task);
            if(wxCpAddMomentResult.success()){
                friendCircle.setJobId(wxCpAddMomentResult.getJobId());
            }
            friendCircle.setCreateTime(new Date());
            friendCircle.setUpdateTime(new Date());
            if(this.save(friendCircle)){
                if(CollectionUtil.isNotEmpty(annexLists)){
                    annexLists.stream().forEach(k->{
                        k.setMsgId(friendCircle.getId());
                    });
                    iYqueMsgAnnexService.saveAll(annexLists);
                }
            }
        }catch (Exception e){
            log.error("朋友圈创建失败:"+e.getMessage());
            throw e;
        }
    }

    @Override
    public IYqueFriendCircle getDetailById(Long id) {
        IYqueFriendCircle friendCircle = this.getById(id);
        if(null != friendCircle){
            friendCircle.setAnnexLists(
                    iYqueMsgAnnexService.findIYqueMsgAnnexByMsgId(friendCircle.getId())
            );
        }

        return friendCircle;
    }

    @Override
    public void jobIdToMomentId() throws Exception {

        try {

            List<IYqueFriendCircle> friendCircles = this.list(new LambdaQueryWrapper<IYqueFriendCircle>()
                    .isNotNull(IYqueFriendCircle::getJobId)
                    .and(wrapper -> wrapper
                            .isNull(IYqueFriendCircle::getMomentId)   // NULL
                            .or()
                            .eq(IYqueFriendCircle::getMomentId, "")   // 空字符串
                    )
            );


            if(CollectionUtil.isNotEmpty(friendCircles)){

                WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
                friendCircles.stream().forEach(k->{
                    try {
                        WxCpGetMomentTaskResult momentTaskResult
                                = wxcpservice.getExternalContactService().getMomentTaskResult(k.getJobId());
                        if(momentTaskResult.success()&&null != momentTaskResult.getResult()){
                            k.setMomentId( momentTaskResult.getResult().getMomentId());
                        }

                    } catch (WxErrorException e) {
                        throw new RuntimeException(e);
                    }
                });

                this.updateBatchById(friendCircles);


            }

        }catch (Exception e){
            log.error("朋友圈jobid换取momentId失败:"+e.getMessage());
            throw e;
        }



    }

    @Override
    @Transactional
    public void deleteById(Long id) throws Exception {
        try {
            IYqueFriendCircle friendCircle = this.getById(id);
            if(null != friendCircle && this.removeById(id) && StringUtils.isNotEmpty(friendCircle.getMomentId())){
                iYqueConfigService.findWxcpservice().getExternalContactService().cancelMomentTask(friendCircle.getMomentId());
            }

        }catch (Exception e){
            log.error("朋友圈删除失败:"+e.getMessage());
            throw e;
        }

    }


}
