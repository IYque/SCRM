package cn.iyque.service;

import cn.iyque.domain.IYqueFriendCircle;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.PathVariable;

public interface IYqueFriendCircleService extends IService<IYqueFriendCircle> {


    /**
     * 创建朋友圈
     * @param friendCircle
     * @throws Exception
     */
    void createMoment(IYqueFriendCircle friendCircle) throws Exception ;


    /**
     * 通过id查询朋友圈详情
     * @param id
     * @return
     */
    IYqueFriendCircle getDetailById(Long id);


    /**
     * jobId换取企业微信朋友圈id
     */
    void jobIdToMomentId() throws Exception ;


    /**
     * 删除朋友圈
     * @param id
     */
    void deleteById(Long id) throws Exception ;
}
