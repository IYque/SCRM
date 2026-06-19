package cn.iyque.controller;

import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueChat;
import cn.iyque.entity.IYqueChatCode;
import cn.iyque.service.IYqueChatCodeService;
import cn.iyque.service.IYqueChatService;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.utils.TableSupport;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.external.WxCpUserExternalGroupChatList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 智能群码
 */
@RestController
@RequestMapping("/iychatCode")
@Slf4j
public class IYqueChatCodeController {

    @Autowired
    private IYqueChatCodeService iYqueChatCodeService;


    @Autowired
    private IYqueChatService iYqueChatService;




    /**
     * 新增群码
     * @param iYqueChatCode
     * @return
     */
    @PostMapping("/save")
    public ResponseResult save(@RequestBody IYqueChatCode iYqueChatCode) {
        try {
            iYqueChatCodeService.createChatCode(iYqueChatCode);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }


    /**
     * 更新群码
     * @param iYqueChatCode
     * @return
     */
    @PutMapping("/update")
    public ResponseResult update(@RequestBody IYqueChatCode iYqueChatCode){
        try {
            iYqueChatCodeService.updateChatCode(iYqueChatCode);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }



    /**
     * 获取群码列表
     * @return
     */
    @GetMapping("/findIYqueChatCode")
    public ResponseResult<IYqueChatCode> findIYqueChatCode(){
        Page<IYqueChatCode> iYqueChatCodes = iYqueChatCodeService.findAll(PageRequest.of( TableSupport.buildPageRequest().getPageNum(),  TableSupport.buildPageRequest().getPageSize(), Sort.by("updateTime").descending()));
        return new ResponseResult(iYqueChatCodes.getContent(),iYqueChatCodes.getTotalElements());
    }


    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        iYqueChatCodeService.batchDelete(ids);

        return new ResponseResult();
    }


    /**
     * 获取企业微信群
     * @return
     */
    @GetMapping("/findIYqueChat")
    public ResponseResult findIYqueChat(){
        List<IYqueChat> iYqueChats=new ArrayList<>();

        try {

            iYqueChats=iYqueChatService.findAllIYqueChat();
        }catch (Exception e){
            return new ResponseResult(HttpStatus.WE_ERROR,e.getMessage(),null);
        }
        return new ResponseResult(iYqueChats);
    }

}
