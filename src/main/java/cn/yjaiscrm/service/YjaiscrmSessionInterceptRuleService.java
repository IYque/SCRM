package cn.iyque.service;

import cn.iyque.entity.IYqueSessionInterceptRule;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestBody;

public interface IYqueSessionInterceptRuleService extends IService<IYqueSessionInterceptRule> {

    /**
     * 新增或编辑敏感词
     * @param sessionInterceptRule
     */
    void addOrEdit(IYqueSessionInterceptRule sessionInterceptRule);


    /**
     * 删除规则
     * @param id
     */
    void deleteInterceptRuleById(Long id);
}
