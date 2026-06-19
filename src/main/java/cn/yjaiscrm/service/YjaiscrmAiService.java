package cn.iyque.service;

import cn.iyque.domain.AiGenerateTagsResponse;
import cn.iyque.domain.EmbeddingResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface IYqueAiService {
    /**
     * AI生成标签
     * @param prompt 提示词
     * @param groupCount 标签组数量
     * @param tagCountPerGroup 每组标签数量
     * @return 标签组列表
     */
    List<AiGenerateTagsResponse> generateTags(String prompt, Integer groupCount, Integer tagCountPerGroup);
    
    /**
     * 文本向量化
     * @param texts 文本列表
     * @return 向量响应
     */
    EmbeddingResponse embedding(List<String> texts);

    /**
     * 文本向量化
     * @param texts 文本列表
     * @param modelName 向量模型名称，如果为null则使用默认模型
     * @return 向量响应
     */
    EmbeddingResponse embedding(List<String> texts, String modelName);




    /**
     * 具有记忆功能的AI流式问答
     * @param question 用户问题
     * @param history 历史对话记录，格式为"用户: 问题\nAI: 回答\n"的字符串
     * @param modelName 指定使用的模型名称，如果为null则使用默认模型
     * @param role AI角色设定，如果为null则使用默认角色
     * @param temperature 温度参数，控制回答的随机性，范围0-1，如果为null则使用默认值0.7
     * @param topP 核采样参数，控制回答的多样性，范围0-1，如果为null则使用默认值0.9
     * @param maxHistoryRounds 历史对话淘汰策略：保留的最大对话轮数，如果为null则使用默认值10
     * @return AI的回答流
     */
     Flux<String> aiChatWithMemoryStream(String question, String history, String modelName, 
         String role, Double temperature, Double topP, Integer maxHistoryRounds);

    /**
     * AI导航推荐流式问答
     * @param question 用户问题
     * @param modelName 模型名称
     * @param role AI角色设定
     * @param temperature 温度参数
     * @param topP 核采样参数
     * @return AI的回答流（包含功能推荐）
     */
    Flux<String> aiNavigationChatStream(String question, String modelName,
        String role, Double temperature, Double topP);

    /**
     * 通用AI对话（非流式）
     * @param content 用户输入的内容
     * @return AI的回复内容
     */
    String aiHandleCommonContent(String content);

    /**
     * 通用AI对话返回JSON格式（非流式）
     * @param content 用户输入的内容
     * @return AI的JSON格式回复
     */
    String aiHandleCommonContentToJson(String content);

    /**
     * AI智能生成朋友圈内容
     * @param prompt 用户输入的提示词，用于生成朋友圈内容
     * @param modelName 指定使用的模型名称，如果为null则使用默认模型
     * @return AI生成的朋友圈内容（JSON格式，包含name和content字段）
     */
    String aiGenerateFriendCircleContent(String prompt, String modelName);

} 
