package cn.iyque.controller;


import cn.iyque.entity.IYqueDefaultMsg;
import cn.iyque.domain.ResponseResult;
import cn.iyque.service.IYqueDefaultMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 系统默认欢迎语相关
 */
@RestController
@RequestMapping("/iYqueDefaultMsg")
public class IYqueDefaultMsgController {

    @Autowired
    private IYqueDefaultMsgService iYqueDefaultMsgService;

    /**
     * 获取默认欢迎语
     * @return
     */
    @GetMapping("/findDefaultMsg")
    public ResponseResult<IYqueDefaultMsg> findDefaultMsg(){
        return new ResponseResult<>(
                iYqueDefaultMsgService.findDefaultMsg()
        );
    }


    /**
     * 保存或更新默认欢迎语
     * @param iYqueDefaultMsg
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public ResponseResult saveOrUpdate(@RequestBody IYqueDefaultMsg iYqueDefaultMsg){
        iYqueDefaultMsgService.saveOrUpdate(iYqueDefaultMsg);

        return new ResponseResult();
    }
}
