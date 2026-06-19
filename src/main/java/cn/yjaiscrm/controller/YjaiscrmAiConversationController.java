package cn.iyque.controller;

import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueAiConversation;
import cn.iyque.entity.IYqueAiConversationMessage;
import cn.iyque.entity.IYqueAiConversationMessagesRequest;
import cn.iyque.service.IYqueAiConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai/conversation")
@Slf4j
@RequiredArgsConstructor
public class IYqueAiConversationController {

    private final IYqueAiConversationService conversationService;

    @GetMapping("/list")
    public ResponseResult<List<IYqueAiConversation>> getConversationList(@RequestParam(required = false) Integer deviceType) {
        List<IYqueAiConversation> list = conversationService.getConversationList(deviceType);
        return new ResponseResult<>(list);
    }

    @PostMapping("/create")
    public ResponseResult<IYqueAiConversation> createConversation(@RequestBody IYqueAiConversation conversation) {
        IYqueAiConversation result = conversationService.createConversation(conversation);
        return new ResponseResult<>(result);
    }

    @PutMapping("/update")
    public ResponseResult<IYqueAiConversation> updateConversation(@RequestBody IYqueAiConversation conversation) {
        IYqueAiConversation result = conversationService.updateConversation(conversation);
        return new ResponseResult<>(result);
    }

    @DeleteMapping("/delete/{conversationId}")
    public ResponseResult<Void> deleteConversation(@PathVariable String conversationId, @RequestParam(required = false) Integer deviceType) {
        conversationService.deleteConversation(conversationId, deviceType);
        return new ResponseResult<>();
    }

    @GetMapping("/messages/{conversationId}")
    public ResponseResult<List<IYqueAiConversationMessage>> getMessages(@PathVariable String conversationId, @RequestParam(required = false) Integer deviceType) {
        List<IYqueAiConversationMessage> messages = conversationService.getMessages(conversationId, deviceType);
        return new ResponseResult<>(messages);
    }

    @PostMapping("/message/save")
    public ResponseResult<Void> saveMessage(@RequestBody IYqueAiConversationMessage message) {
        conversationService.saveMessage(message);
        return new ResponseResult<>();
    }

    @PostMapping("/messages/save/{conversationId}")
    public ResponseResult<Void> saveMessages(
            @PathVariable String conversationId,
            @RequestBody IYqueAiConversationMessagesRequest request) {
        List<IYqueAiConversationMessage> messages = request.getMessages();
        Integer deviceType = request.getDeviceType();
        conversationService.saveMessages(conversationId, messages, deviceType);
        return new ResponseResult<>();
    }

    @GetMapping("/detail/{conversationId}")
    public ResponseResult<Map<String, Object>> getConversationWithMessages(@PathVariable String conversationId, @RequestParam(required = false) Integer deviceType) {
        Map<String, Object> result = conversationService.getConversationWithMessages(conversationId, deviceType);
        return new ResponseResult<>(result);
    }
}
