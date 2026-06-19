package cn.iyque.service;

import cn.iyque.entity.IYqueAiConversation;
import cn.iyque.entity.IYqueAiConversationMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface IYqueAiConversationService extends IService<IYqueAiConversation> {

    List<IYqueAiConversation> getConversationList(Integer deviceType);

    IYqueAiConversation createConversation(IYqueAiConversation conversation);

    IYqueAiConversation updateConversation(IYqueAiConversation conversation);

    void deleteConversation(String conversationId, Integer deviceType);

    List<IYqueAiConversationMessage> getMessages(String conversationId, Integer deviceType);

    void saveMessage(IYqueAiConversationMessage message);

    void saveMessages(String conversationId, List<IYqueAiConversationMessage> messages, Integer deviceType);

    Map<String, Object> getConversationWithMessages(String conversationId, Integer deviceType);
}
