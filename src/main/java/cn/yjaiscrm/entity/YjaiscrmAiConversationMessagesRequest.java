package cn.iyque.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IYqueAiConversationMessagesRequest {

    private List<IYqueAiConversationMessage> messages;
    private Integer deviceType;
}
