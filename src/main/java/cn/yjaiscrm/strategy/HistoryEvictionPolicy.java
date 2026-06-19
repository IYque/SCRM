package cn.iyque.strategy;

import dev.langchain4j.data.message.ChatMessage;
import java.util.List;

public interface HistoryEvictionPolicy {

    String getName();

    String getDescription();

    List<ChatMessage> evict(List<ChatMessage> messages, int maxRounds);
}
