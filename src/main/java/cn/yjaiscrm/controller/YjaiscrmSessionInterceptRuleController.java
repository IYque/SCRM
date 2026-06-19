package cn.iyque.controller;

import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueSessionInterceptRule;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueSessionInterceptRuleService;
import cn.iyque.utils.StringUtils;
import cn.iyque.utils.TableSupport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 会话拦截规则
 */
@RestController
@RequestMapping("/iYqueSessionInterceptRule")
public class IYqueSessionInterceptRuleController {

    @Autowired
    private IYqueSessionInterceptRuleService sessionInterceptRuleService;

    /**
     * 新增或更新会话拦截规则
     * @param sessionInterceptRule
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public ResponseResult saveOrUpdate(@RequestBody IYqueSessionInterceptRule sessionInterceptRule) {
        try {

            sessionInterceptRuleService.addOrEdit(sessionInterceptRule);
        } catch (IYqueException e) {
            return new ResponseResult(HttpStatus.ERROR, e.getMsg(), null);
        }
        return new ResponseResult();
    }

    /**
     * 会话拦截规则列表
     * @return
     */
    @GetMapping("/findAll")
    public ResponseResult<IYqueSessionInterceptRule> findAll(String ruleName) {
        PageHelper.startPage(TableSupport.buildPageMybaitsRequest().getPageNum(), TableSupport.buildPageMybaitsRequest().getPageSize());

        List<IYqueSessionInterceptRule> rules = sessionInterceptRuleService.list(new LambdaQueryWrapper<IYqueSessionInterceptRule>()
                .like(StringUtils.isNotEmpty(ruleName), IYqueSessionInterceptRule::getRuleName, ruleName));

        return new ResponseResult(rules,new PageInfo(rules).getTotal());
    }

    /**
     * 通过id删除会话拦截规则
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteById(@PathVariable Long id) {
        try {
            sessionInterceptRuleService.deleteInterceptRuleById(id);
        } catch (Exception e) {
            return new ResponseResult(HttpStatus.ERROR, e.getMessage(), null);
        }
        return new ResponseResult();
    }

    /**
     * 通过id批量删除会话拦截规则
     * @param ids
     * @return
     */
    @DeleteMapping("/batch/{ids}")
    public ResponseResult batchDelete(@PathVariable Long[] ids) {
        try {
            Arrays.stream(ids).forEach(id->{
                sessionInterceptRuleService.deleteInterceptRuleById(id);
            });
        } catch (Exception e) {
            return new ResponseResult(HttpStatus.ERROR, e.getMessage(), null);
        }
        return new ResponseResult();
    }

    /**
     * 通过id获取会话拦截规则
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    public ResponseResult getById(@PathVariable Long id) {
        try {
            IYqueSessionInterceptRule rule = sessionInterceptRuleService.getById(id);
            return new ResponseResult(rule);
        } catch (Exception e) {
            return new ResponseResult(HttpStatus.ERROR, e.getMessage(), null);
        }
    }
}
