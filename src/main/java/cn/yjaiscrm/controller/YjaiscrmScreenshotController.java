package cn.iyque.controller;


import cn.iyque.domain.IYqueScreenshot;
import cn.iyque.domain.ResponseResult;
import cn.iyque.domain.SubType;
import cn.iyque.entity.BaseEntity;
import cn.iyque.entity.IYqueFileSecurity;
import cn.iyque.enums.ScreenShotType;
import cn.iyque.service.IYqueScreenshotService;
import cn.iyque.utils.TableSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 截屏安全
 */
@RestController
@RequestMapping("/screenShot")
public class IYqueScreenshotController {

    @Autowired
    private IYqueScreenshotService yqueScreenshotService;


    /**
     * 截屏内容类型
     * @return
     */
    @GetMapping("/getScreenShotTypes")
    public ResponseResult getScreenShotTypes(){


        List<SubType> subTypeListFromEnum =
                ScreenShotType.getSubTypeListFromEnum();

        return new ResponseResult(subTypeListFromEnum);
    }

    /**
     * 信息同步
     * @return
     */
    @PostMapping("/synchInfo")
    public ResponseResult synchInfo(@RequestBody BaseEntity baseEntity){

        yqueScreenshotService.synchInfo(baseEntity);

        return new ResponseResult("信息真在同步中,请稍后查看");

    }


    /**
     * 获取截屏列表
     * @param screenshot
     * @return
     */
    @GetMapping("/findAll")
    public ResponseResult<IYqueScreenshot> findAll(IYqueScreenshot screenshot){


        Page<IYqueScreenshot> screenshots = yqueScreenshotService.findAll(screenshot,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("operateTime").descending()));
        return new ResponseResult(screenshots.getContent(),screenshots.getTotalElements());
    }
}
