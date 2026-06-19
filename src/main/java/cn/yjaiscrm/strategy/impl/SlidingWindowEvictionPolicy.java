package cn.iyque.strategy.impl;

import cn.iyque.strategy.HistoryEvictionPolicy;
import dev.langchain4j.data.message.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class SlidingWindowEvictionPolicy implements HistoryEvictionPolicy {

    @Override
    public String getName() {
        return "sliding_window";
    }

    @Override
    public String getDescription() {
        return "滑动窗口策略：只保留最近N轮对话，直接丢弃较早的消息。简单高效，但会丢失早期上下文信息。适用于短期对话场景。";
    }

    @Override
    public List<ChatMessage> evict(List<ChatMessage> messages, int maxRounds) {
        if (messages == null || messages.isEmpty()) {
            return messages;
        }

        int totalMessages = messages.size();
        int currentRounds = totalMessages / 2;

        if (currentRounds <= maxRounds) {
            return messages;
        }

        int messagesToKeep = maxRounds * 2;
        List<ChatMessage> retainedMessages = messages.subList(totalMessages - messagesToKeep, totalMessages);

        log.info("滑动窗口策略生效: 原有{}轮对话, 保留最近{}轮",
                currentRounds, maxRounds);

        return new ArrayList<>(retainedMessages);
    }
}
