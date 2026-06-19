package cn.iyque.strategy.impl;

import cn.iyque.factory.AiModelFactory;
import cn.iyque.strategy.HistoryEvictionPolicy;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class SummaryEvictionPolicy implements HistoryEvictionPolicy {

    private final AiModelFactory modelFactory;

    public SummaryEvictionPolicy(AiModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    @Override
    public String getName() {
        return "summary";
    }

    @Override
    public String getDescription() {
        return "摘要淘汰策略：当历史消息超过指定轮数时，会对较早的对话进行摘要总结，保留关键信息后继续对话。适用于需要长期记忆的场景。";
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

        int messagesToRemove = (currentRounds - maxRounds) * 2;

        List<ChatMessage> messagesToSummarize = messages.subList(0, messagesToRemove);
        List<ChatMessage> retainedMessages = messages.subList(messagesToRemove, totalMessages);

        String summary = summarizeHistory(messagesToSummarize);

        List<ChatMessage> result = new ArrayList<>();
        if (summary != null && !summary.isEmpty()) {
            result.add(SystemMessage.from("以下是之前对话的总结，请在此基础上继续对话：\n" + summary));
        }
        result.addAll(retainedMessages);

        log.info("历史对话总结策略生效: 原有{}轮对话, 总结前{}轮, 保留最近{}轮",
                currentRounds, messagesToRemove / 2, maxRounds);

        return result;
    }

    private String summarizeHistory(List<ChatMessage> messagesToSummarize) {
        if (messagesToSummarize == null || messagesToSummarize.isEmpty()) {
            return null;
        }

        try {
            StringBuilder historyText = new StringBuilder();
            for (ChatMessage msg : messagesToSummarize) {
                if (msg instanceof UserMessage) {
                    historyText.append("用户: ").append(((UserMessage) msg).singleText()).append("\n");
                } else if (msg instanceof AiMessage) {
                    historyText.append("AI: ").append(((AiMessage) msg).text()).append("\n");
                }
            }

            String summarizePrompt = "请对以下对话历史进行简洁的总结，保留关键信息和上下文，用于后续对话的参考。总结应该简洁明了，不超过200字：\n\n" + historyText.toString();

            List<String> models = modelFactory.getEnabledModels();
            if (models.isEmpty()) {
                log.warn("无可用模型进行对话总结");
                return null;
            }

            String modelName = models.get(0);
            ChatLanguageModel chatModel = modelFactory.getChatModel(modelName);

            if (chatModel == null) {
                log.warn("无法获取聊天模型进行对话总结");
                return null;
            }

            List<ChatMessage> summarizeMessages = new ArrayList<>();
            summarizeMessages.add(SystemMessage.from("你是一个对话总结助手，请对给定的对话历史进行简洁的总结。"));
            summarizeMessages.add(UserMessage.from(summarizePrompt));

            String summary = chatModel.chat(summarizeMessages).aiMessage().text();

            log.info("对话历史总结完成: {}", summary);
            return summary;

        } catch (Exception e) {
            log.error("总结对话历史失败: {}", e.getMessage(), e);
            return null;
        }
    }
}
