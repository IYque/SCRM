package cn.iyque.controller;

import cn.iyque.domain.IYQueGroupDto;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueChat;
import cn.iyque.entity.IYqueUser;
import cn.iyque.service.IYqueChatService;
import cn.iyque.utils.TableSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iYqueChat")
public class IYqueChatController {

    @Autowired
    private IYqueChatService iYqueChatService;




    /**
     * 获取群列表
     * @param iYqueChat
     * @return
     */
    @GetMapping("/findIYqueChatPage")
    public ResponseResult<IYqueChat> findIYqueChatPage(IYqueChat iYqueChat){
        Page<IYqueChat> iYqueChats = iYqueChatService.findAll(iYqueChat.getChatName(), PageRequest.of(
                TableSupport.buildPageRequest().getPageNum(), TableSupport.buildPageRequest().getPageSize()));
        return new ResponseResult(iYqueChats.getContent(),iYqueChats.getTotalElements());
    }

    /**
     * 同步客群
     * @return
     */
    @PostMapping("/synchIyqueChat")
    public ResponseResult synchIyqueChat(){

        iYqueChatService.synchIyqueChat();

        return new ResponseResult("客群同步中,请稍后查看");
    }

    /**
     * 客群打标签
     * @param iyQueGroupDto
     * @return
     */
    @PostMapping("/tagGroups")
    public ResponseResult tagGroups(@RequestBody IYQueGroupDto iyQueGroupDto){


        iYqueChatService.makeTag(iyQueGroupDto);


        return new ResponseResult("客群真正同步中,请稍后查看");

    }


}
