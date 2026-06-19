package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.entity.IYqueAiConversation;
import cn.iyque.entity.IYqueAiConversationMessage;
import cn.iyque.mapper.IYqueAiConversationMapper;
import cn.iyque.mapper.IYqueAiConversationMessageMapper;
import cn.iyque.service.IYqueAiConversationMessageService;
import cn.iyque.service.IYqueAiConversationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class IYqueAiConversationServiceImpl extends ServiceImpl<IYqueAiConversationMapper, IYqueAiConversation> implements IYqueAiConversationService {

    private final IYqueAiConversationMessageService messageService;



    @Override
    public List<IYqueAiConversation> getConversationList(Integer deviceType) {
        LambdaQueryWrapper<IYqueAiConversation> wrapper = new LambdaQueryWrapper<>();
        if (deviceType != null) {
            wrapper.eq(IYqueAiConversation::getDeviceType, deviceType);
        }
        wrapper.orderByDesc(IYqueAiConversation::getUpdateTime);
        List<IYqueAiConversation> list = list(wrapper);
        
        for (IYqueAiConversation conversation : list) {
            LambdaQueryWrapper<IYqueAiConversationMessage> msgWrapper = new LambdaQueryWrapper<>();
            msgWrapper.eq(IYqueAiConversationMessage::getConversationId, conversation.getConversationId());
            msgWrapper.orderByDesc(IYqueAiConversationMessage::getCreateTime);
            msgWrapper.last("LIMIT 1");
            IYqueAiConversationMessage lastMsg = messageService.getOne(msgWrapper);

            if (lastMsg != null) {
                conversation.setLastMessage(lastMsg.getContent());
                conversation.setLastMessageTime(lastMsg.getCreateTime());
            }
        }
        
        return list;
    }

    @Override
    @Transactional
    public IYqueAiConversation createConversation(IYqueAiConversation conversation) {
        if (conversation.getConversationId() == null || conversation.getConversationId().isEmpty()) {
            conversation.setConversationId(UUID.randomUUID().toString());
        }
        conversation.setCreateTime(new Date());
        conversation.setUpdateTime(new Date());
        conversation.setDeleted(0);
        save(conversation);
        return conversation;
    }

    @Override
    @Transactional
    public IYqueAiConversation updateConversation(IYqueAiConversation conversation) {
        LambdaQueryWrapper<IYqueAiConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IYqueAiConversation::getConversationId, conversation.getConversationId());
        IYqueAiConversation existing = getOne(wrapper);
        
        if (existing != null) {
            existing.setTitle(conversation.getTitle());
            existing.setModelName(conversation.getModelName());
            existing.setRole(conversation.getRole());
            existing.setTemperature(conversation.getTemperature());
            existing.setTopP(conversation.getTopP());
            existing.setMaxHistoryRounds(conversation.getMaxHistoryRounds());
            existing.setUpdateTime(new Date());
            updateById(existing);
            return existing;
        }
        
        conversation.setUpdateTime(new Date());
        updateById(conversation);
        return conversation;
    }

    @Override
    @Transactional
    public void deleteConversation(String conversationId, Integer deviceType) {
        LambdaQueryWrapper<IYqueAiConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IYqueAiConversation::getConversationId, conversationId);
        if (deviceType != null) {
            wrapper.eq(IYqueAiConversation::getDeviceType, deviceType);
        }
        remove(wrapper);

        LambdaQueryWrapper<IYqueAiConversationMessage> msgWrapper = new LambdaQueryWrapper<>();
        msgWrapper.eq(IYqueAiConversationMessage::getConversationId, conversationId);
        messageService.remove(msgWrapper);
    }

    @Override
    public List<IYqueAiConversationMessage> getMessages(String conversationId, Integer deviceType) {
        // 先检查会话是否属于指定设备类型
        LambdaQueryWrapper<IYqueAiConversation> convWrapper = new LambdaQueryWrapper<>();
        convWrapper.eq(IYqueAiConversation::getConversationId, conversationId);
        if (deviceType != null) {
            convWrapper.eq(IYqueAiConversation::getDeviceType, deviceType);
        }
        IYqueAiConversation conversation = getOne(convWrapper);
        
        if (conversation == null) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<IYqueAiConversationMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IYqueAiConversationMessage::getConversationId, conversationId);
        wrapper.orderByAsc(IYqueAiConversationMessage::getCreateTime);
        return messageService.list(wrapper);
    }

    @Override
    @Transactional
    public void saveMessage(IYqueAiConversationMessage message) {
        if (message.getConversationId() == null || message.getConversationId().isEmpty()) {
            return;
        }
        
        LambdaQueryWrapper<IYqueAiConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IYqueAiConversation::getConversationId, message.getConversationId());
        IYqueAiConversation conversation = getOne(wrapper);
        
        if (conversation == null) {
            return;
        }
        
        message.setCreateTime(new Date());
        messageService.save(message);
    }

    // 用于存储同步锁的Map，确保同一conversationId的请求串行处理
    private final Map<String, Object> conversationLocks = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public void saveMessages(String conversationId, List<IYqueAiConversationMessage> messages, Integer deviceType) {
        // 获取当前conversationId的锁对象
        Object lock = conversationLocks.computeIfAbsent(conversationId, k -> new Object());
        
        synchronized (lock) {
            try {
                // 先检查会话是否属于指定设备类型
                LambdaQueryWrapper<IYqueAiConversation> convWrapper = new LambdaQueryWrapper<>();
                convWrapper.eq(IYqueAiConversation::getConversationId, conversationId);
                if (deviceType != null) {
                    convWrapper.eq(IYqueAiConversation::getDeviceType, deviceType);
                }
                IYqueAiConversation conversation = getOne(convWrapper);
                
                if (conversation == null) {
                    return;
                }
                
                if(CollectionUtil.isNotEmpty(messages)){
                    // 先获取现有消息的ID列表
                    LambdaQueryWrapper<IYqueAiConversationMessage> existingWrapper = new LambdaQueryWrapper<>();
                    existingWrapper.eq(IYqueAiConversationMessage::getConversationId, conversationId);
                    List<IYqueAiConversationMessage> existingMessages = messageService.list(existingWrapper);
                    Set<Long> existingIds = existingMessages.stream()
                            .map(IYqueAiConversationMessage::getId)
                            .collect(java.util.stream.Collectors.toSet());
                    
                    // 分离新增和更新的消息
                    List<IYqueAiConversationMessage> newMessages = new ArrayList<>();
                    List<IYqueAiConversationMessage> updateMessages = new ArrayList<>();
                    
                    for (IYqueAiConversationMessage message : messages) {
                        message.setConversationId(conversationId);
                        message.setCreateTime(new Date());
                        if (message.getId() == null || !existingIds.contains(message.getId())) {
                            newMessages.add(message);
                        } else {
                            updateMessages.add(message);
                        }
                    }
                    
                    // 批量保存新增消息
                    if (CollectionUtil.isNotEmpty(newMessages)) {
                        messageService.saveBatch(newMessages);
                    }
                    
                    // 批量更新现有消息
                    if (CollectionUtil.isNotEmpty(updateMessages)) {
                        messageService.updateBatchById(updateMessages);
                    }
                    
                    // 删除不在新列表中的消息
                    Set<Long> newIds = messages.stream()
                            .filter(m -> m.getId() != null)
                            .map(IYqueAiConversationMessage::getId)
                            .collect(java.util.stream.Collectors.toSet());
                    
                    List<Long> idsToDelete = existingIds.stream()
                            .filter(id -> !newIds.contains(id))
                            .collect(java.util.stream.Collectors.toList());
                    
                    if (CollectionUtil.isNotEmpty(idsToDelete)) {
                        messageService.removeByIds(idsToDelete);
                    }
                } else {
                    // 如果消息列表为空，删除所有现有消息
                    LambdaQueryWrapper<IYqueAiConversationMessage> deleteWrapper = new LambdaQueryWrapper<>();
                    deleteWrapper.eq(IYqueAiConversationMessage::getConversationId, conversationId);
                    messageService.remove(deleteWrapper);
                }
            } finally {
                // 处理完成后移除锁对象，避免内存泄漏
                conversationLocks.remove(conversationId);
            }
        }
    }

    @Override
    public Map<String, Object> getConversationWithMessages(String conversationId, Integer deviceType) {
        Map<String, Object> result = new HashMap<>();
        
        LambdaQueryWrapper<IYqueAiConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IYqueAiConversation::getConversationId, conversationId);
        if (deviceType != null) {
            wrapper.eq(IYqueAiConversation::getDeviceType, deviceType);
        }
        IYqueAiConversation conversation = getOne(wrapper);
        
        if (conversation != null) {
            result.put("conversation", conversation);
            result.put("messages", getMessages(conversationId, deviceType));
        }
        
        return result;
    }
}
