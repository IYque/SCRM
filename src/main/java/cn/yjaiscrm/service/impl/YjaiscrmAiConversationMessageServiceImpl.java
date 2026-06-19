package cn.iyque.service.impl;

import cn.iyque.entity.IYqueAiConversation;
import cn.iyque.entity.IYqueAiConversationMessage;
import cn.iyque.mapper.IYqueAiConversationMapper;
import cn.iyque.mapper.IYqueAiConversationMessageMapper;
import cn.iyque.service.IYqueAiConversationMessageService;
import cn.iyque.service.IYqueAiConversationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class IYqueAiConversationMessageServiceImpl  extends ServiceImpl<IYqueAiConversationMessageMapper, IYqueAiConversationMessage> implements IYqueAiConversationMessageService {
}
