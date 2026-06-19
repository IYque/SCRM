package cn.iyque.controller;


import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.IYqueFriendCircle;
import cn.iyque.domain.ResponseResult;
import cn.iyque.service.IYqueAiService;
import cn.iyque.service.IYqueFriendCircleService;
import cn.iyque.utils.StringUtils;
import cn.iyque.utils.TableSupport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 朋友圈
 */
@RestController
@RequestMapping("/iYqueSys/friendCircle")
public class IYqueFriendCircleController {

    @Autowired
    private IYqueFriendCircleService friendCircleService;

    @Autowired
    private IYqueAiService aiService;

    /**
     * 朋友圈列表
     * @param name
     * @return
     */
    @GetMapping("/list")
    public ResponseResult<IYqueFriendCircle> findAll(String name) {
        PageHelper.startPage(TableSupport.buildPageMybaitsRequest().getPageNum(), TableSupport.buildPageMybaitsRequest().getPageSize());

        List<IYqueFriendCircle> friendCircles = friendCircleService.list(new LambdaQueryWrapper<IYqueFriendCircle>()
                .like(StringUtils.isNotEmpty(name), IYqueFriendCircle::getName, name));

        return new ResponseResult(friendCircles, new PageInfo(friendCircles).getTotal());
    }

    /**
     * 新增朋友圈
     * @param friendCircle
     * @return
     */
    @PostMapping("/create")
    public ResponseResult create(@RequestBody IYqueFriendCircle friendCircle) {
        try {
            friendCircleService.createMoment(friendCircle);
        } catch (Exception e) {
            return new ResponseResult(HttpStatus.ERROR, e.getMessage(), null);
        }

        return new ResponseResult();
    }


    /**
     * 通过id查询朋友圈详情
     * @param id
     * @return
     */
    @GetMapping("/detail/{id}")
    public ResponseResult getDetailById(@PathVariable Long id){
        IYqueFriendCircle detailById = friendCircleService.getDetailById(id);

        return new ResponseResult(detailById);
    }


    /**
     * 通过id删除朋友圈
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteById(@PathVariable Long id) {
        try {
            friendCircleService.deleteById(id);
        } catch (Exception e) {
            return new ResponseResult(HttpStatus.ERROR, e.getMessage(), null);
        }
        return new ResponseResult();
    }

    /**
     * AI智能生成朋友圈内容
     * @param prompt 用户输入的提示词
     * @param modelName AI模型名称
     * @return AI生成的朋友圈内容
     */
    @GetMapping("/ai/generate")
    public ResponseResult<String> aiGenerateContent(String prompt, String modelName) {
        try {
            String result = aiService.aiGenerateFriendCircleContent(prompt, modelName);
            return new ResponseResult(result);
        } catch (Exception e) {
            return new ResponseResult(HttpStatus.ERROR, e.getMessage(), null);
        }
    }


}
