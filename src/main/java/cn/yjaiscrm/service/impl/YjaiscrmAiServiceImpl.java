package cn.iyque.service.impl;


import cn.iyque.config.IYqueParamConfig;
import cn.iyque.domain.AiGenerateTagsResponse;
import cn.iyque.domain.EmbeddingResponse;
import cn.iyque.exception.IYqueException;
import cn.iyque.factory.AiModelFactory;
import cn.iyque.prompt.AiPromptManager;
import cn.iyque.service.IYqueAiService;
import cn.iyque.service.FunctionRouteService;
import cn.iyque.strategy.HistoryEvictionPolicy;
import cn.iyque.strategy.HistoryEvictionPolicyManager;
import cn.iyque.utils.StringUtils;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class IYqueAiServiceImpl implements IYqueAiService {

//    @Value("${ai.maxHistoryRounds:10}")
//    private int defaultMaxHistoryRounds;
//
    @Value("${ai.historyEvictionPolicy:summary}")
    private String historyEvictionPolicy;

    @Autowired
    private AiModelFactory modelFactory;

    @Autowired
    private AiPromptManager promptManager;

    @Autowired
    private FunctionRouteService functionRouteService;

    @Autowired
    private HistoryEvictionPolicyManager policyManager;

    @Autowired
    private IYqueParamConfig yqueParamConfig;




    @Override
    public List<AiGenerateTagsResponse> generateTags(String prompt, Integer groupCount, Integer tagCountPerGroup) {
        List<AiGenerateTagsResponse> result = new ArrayList<>();
        
        if (groupCount == null || groupCount < 1) {
            groupCount = 2;
        }
        if (tagCountPerGroup == null || tagCountPerGroup < 1) {
            tagCountPerGroup = 3;
        }
        if (groupCount > 100) {
            groupCount = 100;
        }
        if (tagCountPerGroup > 100) {
            tagCountPerGroup = 100;
        }
        
        try {
            List<String> models = modelFactory.getEnabledModels();
            if (models.isEmpty()) {
                throw new IYqueException("无可用 AI 模型，请检查 ai.models 配置");
            }
            
            String modelName = models.get(0);
            ChatLanguageModel chatModel = modelFactory.getChatModel(modelName, 0.3, 0.9);
            
            String aiPrompt = "请根据用户需求生成相关的标签组和标签。\n" +
                    "用户需求：" + prompt + "\n" +
                    "请按照以下JSON格式输出结果：\n" +
                    "{\n" +
                    "  \"tagGroups\": [\n" +
                    "    {\n" +
                    "      \"groupName\": \"标签组名称\",\n" +
                    "      \"tags\": [\n" +
                    "        {\"name\": \"标签1\"},\n" +
                    "        {\"name\": \"标签2\"},\n" +
                    "        {\"name\": \"标签3\"}\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n" +
                    "要求：\n" +
                    "1. 生成" + groupCount + "个标签组，每个标签组包含" + tagCountPerGroup + "个标签\n" +
                    "2. 标签组名称要清晰表达标签的分类逻辑\n" +
                    "3. 标签名称要具体、有实际业务意义\n" +
                    "4. 严格按照指定的JSON格式输出，不要包含任何额外的文本\n" +
                    "5. 确保JSON格式正确，能够被标准JSON解析器解析\n";
            
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(SystemMessage.from("你是一个数据分析助手。请严格只返回JSON格式数据，不要包含任何额外的文本、说明或解释。返回的JSON必须能够被标准解析器直接解析。"));
            messages.add(UserMessage.from(aiPrompt));
            
            String aiResponse = chatModel.chat(messages).aiMessage().text();
            log.info("AI生成标签响应: {}", aiResponse);
            
            String jsonStr = aiResponse.trim();
            if (jsonStr.startsWith("```")) {
                int firstNewline = jsonStr.indexOf('\n');
                int lastNewline = jsonStr.lastIndexOf("```");
                if (firstNewline > 0 && lastNewline > firstNewline) {
                    jsonStr = jsonStr.substring(firstNewline + 1, lastNewline).trim();
                } else if (lastNewline > 3) {
                    jsonStr = jsonStr.substring(3, lastNewline).trim();
                }
            }
            
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(jsonStr);
            com.fasterxml.jackson.databind.JsonNode tagGroupsNode = rootNode.get("tagGroups");
            
            if (tagGroupsNode != null && tagGroupsNode.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode groupNode : tagGroupsNode) {
                    AiGenerateTagsResponse tagGroup = new AiGenerateTagsResponse();
                    
                    com.fasterxml.jackson.databind.JsonNode groupNameNode = groupNode.get("groupName");
                    if (groupNameNode != null && groupNameNode.isTextual()) {
                        tagGroup.setGroupName(groupNameNode.asText());
                    } else {
                        continue;
                    }
                    
                    List<AiGenerateTagsResponse.TagItem> tags = new ArrayList<>();
                    com.fasterxml.jackson.databind.JsonNode tagsNode = groupNode.get("tags");
                    if (tagsNode != null && tagsNode.isArray()) {
                        for (com.fasterxml.jackson.databind.JsonNode tagNode : tagsNode) {
                            com.fasterxml.jackson.databind.JsonNode tagNameNode = tagNode.get("name");
                            if (tagNameNode != null && tagNameNode.isTextual()) {
                                AiGenerateTagsResponse.TagItem tagItem = new AiGenerateTagsResponse.TagItem();
                                tagItem.setName(tagNameNode.asText());
                                tags.add(tagItem);
                            }
                        }
                    }
                    
                    if (!tags.isEmpty()) {
                        tagGroup.setTags(tags);
                        result.add(tagGroup);
                    }
                }
            }
            
            if (result.isEmpty()) {
                throw new IYqueException("AI生成标签失败：未生成有效的标签组");
            }
            
        } catch (IYqueException e) {
            log.error("AI生成标签失败: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("AI生成标签失败: " + e.getMessage(), e);
            throw new IYqueException("AI生成标签失败: " + e.getMessage());
        }
        
        return result;
    }
    


    @Override
    public Flux<String> aiChatWithMemoryStream(String question, String history, String modelName,
        String role, Double temperature, Double topP, Integer maxHistoryRounds) {

        log.info("开始AI流式对话, 问题: {}, 模型: {}, 温度: {}, 核采样: {}", question, modelName, temperature, topP);


        Exception lastError = null;

        // 使用传入的参数或默认值
        String actualRole = StringUtils.isEmpty(role) ?
                promptManager.get("recommend-reply.recommend_reply").getSystem()
                : role;

        Double actualTemperature = temperature != null ? temperature : 0.7;
        Double actualTopP = topP != null ? topP : 0.9;
        Integer actualMaxHistoryRounds = maxHistoryRounds != null ? maxHistoryRounds : 10;

        // 获取流式模型，传入温度和核采样参数
        StreamingChatLanguageModel streamingModel = modelFactory.getStreamingModel(modelName, actualTemperature, actualTopP);

        if(null != streamingModel){

            // 构建消息列表，包含系统提示和历史对话
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(SystemMessage.from(actualRole));

            // 添加历史对话记录（应用淘汰策略）
            if (!StringUtils.isEmpty(history)) {
                List<ChatMessage> historyMessages = new ArrayList<>();
                String[] lines = history.split("\\n");
                for (String line : lines) {
                    if (line.startsWith("用户: ")) {
                        String userContent = line.substring(4).trim();
                        if (!userContent.isEmpty()) {
                            historyMessages.add(UserMessage.from(userContent));
                        }
                    } else if (line.startsWith("AI: ")) {
                        String aiContent = line.substring(4).trim();
                        if (!aiContent.isEmpty()) {
                            historyMessages.add(AiMessage.from(aiContent));
                        }
                    }
                }

                // 应用淘汰策略
                HistoryEvictionPolicy policy = policyManager.getPolicy(historyEvictionPolicy);
                historyMessages = policy.evict(historyMessages, actualMaxHistoryRounds);
                messages.addAll(historyMessages);
            }

            // 添加当前用户问题
            messages.add(UserMessage.from(question));

            // 2. 获取可用模型列表
            List<String> models = modelFactory.getEnabledModels();
            if (models.isEmpty()) {
                return Flux.error(new RuntimeException("无可用 AI 模型，请检查 ai.models 配置"));
            }

            try {

                // 使用Flux.create创建流式响应
                return Flux.create(emitter -> {
                    streamingModel.chat(messages, new StreamingChatResponseHandler() {
                        @Override
                        public void onPartialResponse(String partialResponse) {
                            if (partialResponse != null && !partialResponse.isEmpty()) {
                                log.debug("收到流式数据块: {}", partialResponse);
                                emitter.next(partialResponse);
                            }
                        }

                        @Override
                        public void onCompleteResponse(ChatResponse completeResponse) {
                            log.info("AI流式响应完成, 模型: {}", modelName);
                            emitter.complete();
                        }

                        @Override
                        public void onError(Throwable error) {
                            log.error("AI流式响应错误, 模型: {}: {}", modelName, error.getMessage(), error);
                            emitter.error(new RuntimeException("AI流式响应错误: " + error.getMessage(), error));
                        }
                    });
                }, FluxSink.OverflowStrategy.BUFFER);
            } catch (Exception e) {
                lastError = e;
                log.warn("⚠️ 模型 [{}] 调用失败: {}",
                        modelName, e.getMessage());
            }

        }



        return Flux.error(new RuntimeException("所有 AI 模型均不可用", lastError));
    }
    


    @Override
    public EmbeddingResponse embedding(List<String> texts) {
        return embedding(texts, null);
    }

    @Override
    public EmbeddingResponse embedding(List<String> texts, String modelName) {
        if (texts == null || texts.isEmpty()) {
            return EmbeddingResponse.builder().build();
        }

        try {
            List<String> embeddingModels = modelFactory.getEnabledEmbeddingModels();
            if (embeddingModels.isEmpty()) {
                throw new IllegalArgumentException("无可用向量模型，请检查 ai.vector 配置");
            }

            String actualModelName = modelName;
            if (actualModelName == null || actualModelName.trim().isEmpty()) {
                actualModelName = embeddingModels.get(0);
            }

            if (!embeddingModels.contains(actualModelName)) {
                log.warn("指定的向量模型 [{}] 不可用，使用默认模型: {}", actualModelName, embeddingModels.get(0));
                actualModelName = embeddingModels.get(0);
            }

            Integer dimension = yqueParamConfig.getVector().getDimension();

            EmbeddingModel embeddingModel = modelFactory.getEmbeddingModel(actualModelName, dimension);

            List<EmbeddingResponse.EmbeddingResult> results = new ArrayList<>();
            for (String text : texts) {
                Embedding embedding = embeddingModel.embed(text).content();
                List<Float> vector = new ArrayList<>();
                for (double d : embedding.vector()) {
                    vector.add((float) d);
                }
                results.add(EmbeddingResponse.EmbeddingResult.builder()
                        .text(text)
                        .vector(vector)
                        .build());
            }

            log.info("向量计算完成: {} 条文本, 维度: {}, 模型: {}", texts.size(), dimension, actualModelName);
            return EmbeddingResponse.builder()
                    .embeddings(results)
                    .build();

        } catch (Exception e) {
            log.error("向量计算异常: " + e.getMessage(), e);
        }

        return null;
    }





    @Override
    public Flux<String> aiNavigationChatStream(String question, String modelName,
        String role, Double temperature, Double topP) {

        log.info("开始AI导航推荐流式对话, 问题: {}, 模型: {}", question, modelName);

        Exception lastError = null;

        String actualRole = StringUtils.isEmpty(role) ?
                "你是一个智能导航助手，帮助用户找到系统中的相关功能。请根据用户的问题，推荐合适的功能路径。" : role;

        Double actualTemperature = temperature != null ? temperature : 0.7;
        Double actualTopP = topP != null ? topP : 0.9;

        StreamingChatLanguageModel streamingModel = modelFactory.getStreamingModel(modelName, actualTemperature, actualTopP);

        if(null != streamingModel){

            List<ChatMessage> messages = new ArrayList<>();
            
            String functionRoutesText = functionRouteService.getFunctionRoutesAsText();
            String systemPrompt = actualRole + "\n\n" + functionRoutesText;
            
            messages.add(SystemMessage.from(systemPrompt));

            messages.add(UserMessage.from(question));

            List<String> models = modelFactory.getEnabledModels();
            if (models.isEmpty()) {
                return Flux.error(new RuntimeException("无可用 AI 模型，请检查 ai.models 配置"));
            }

            try {
                return Flux.create(emitter -> {
                    streamingModel.chat(messages, new StreamingChatResponseHandler() {
                        @Override
                        public void onPartialResponse(String partialResponse) {
                            if (partialResponse != null && !partialResponse.isEmpty()) {
                                log.debug("收到导航推荐流式数据块: {}", partialResponse);
                                emitter.next(partialResponse);
                            }
                        }

                        @Override
                        public void onCompleteResponse(ChatResponse completeResponse) {
                            log.info("AI导航推荐流式响应完成, 模型: {}", modelName);
                            emitter.complete();
                        }

                        @Override
                        public void onError(Throwable error) {
                            log.error("AI导航推荐流式响应错误, 模型: {}: {}", modelName, error.getMessage(), error);
                            emitter.error(new RuntimeException("AI导航推荐流式响应错误: " + error.getMessage(), error));
                        }
                    });
                }, FluxSink.OverflowStrategy.BUFFER);
            } catch (Exception e) {
                lastError = e;
                log.warn("⚠️ 模型 [{}] 调用失败: {}", modelName, e.getMessage());
            }
        }

        return Flux.error(new RuntimeException("所有 AI 模型均不可用", lastError));
    }




    /**
     * 通用AI对话（非流式）
     * 使用langchain4j进行同步AI对话
     * @param content 用户输入的内容
     * @return AI的回复内容
     */
    @Override
    public String aiHandleCommonContent(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }

        try {
            List<String> models = modelFactory.getEnabledChatModels();
            if (models.isEmpty()) {
                throw new IllegalArgumentException("无可用聊天模型，请检查 ai.models 配置");
            }

            String modelName = models.get(0);
            ChatLanguageModel chatModel = modelFactory.getChatModel(modelName, 1.0, 0.9);

            List<ChatMessage> messages = new ArrayList<>();
            messages.add(SystemMessage.from("你是一个聊天会话助手。"));
            messages.add(UserMessage.from(content));

            String response = chatModel.chat(messages).aiMessage().text();
            log.info("AI对话完成, 模型: {}, 内容长度: {}", modelName, response.length());

            return response;

        } catch (Exception e) {
            log.error("AI对话处理异常: " + e.getMessage(), e);
            throw new IYqueException("AI对话处理异常: " + e.getMessage());
        }
    }

    /**
     * 通用AI对话返回JSON格式（非流式）
     * 使用langchain4j进行同步AI对话，强制返回JSON格式
     * @param content 用户输入的内容
     * @return AI的JSON格式回复
     */
    @Override
    public String aiHandleCommonContentToJson(String content) throws IYqueException {
        if (StringUtils.isEmpty(content)) {
            throw new IYqueException("输入内容不能为空");
        }

        try {
            List<String> models = modelFactory.getEnabledChatModels();
            if (models.isEmpty()) {
                throw new IllegalArgumentException("无可用聊天模型，请检查 ai.models 配置");
            }

            String modelName = models.get(0);
            ChatLanguageModel chatModel = modelFactory.getChatModel(modelName, 0.3, 0.9);

            String systemPrompt = "你是一个数据分析助手。请严格只返回JSON格式数据，不要包含任何额外的文本、说明或解释。返回的JSON必须能够被标准解析器直接解析。";

            List<ChatMessage> messages = new ArrayList<>();
            messages.add(SystemMessage.from(systemPrompt));
            messages.add(UserMessage.from(content));

            String response = chatModel.chat(messages).aiMessage().text();
            log.info("AI JSON对话完成, 模型: {}, 内容长度: {}", modelName, response.length());

            String jsonStr = response.trim();
            if (jsonStr.startsWith("```")) {
                int firstNewline = jsonStr.indexOf('\n');
                int lastNewline = jsonStr.lastIndexOf("```");
                if (firstNewline > 0 && lastNewline > firstNewline) {
                    jsonStr = jsonStr.substring(firstNewline + 1, lastNewline).trim();
                } else if (lastNewline > 3) {
                    jsonStr = jsonStr.substring(3, lastNewline).trim();
                }
            }

            return jsonStr;

        } catch (IYqueException e) {
            log.error("AI JSON对话处理异常: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("AI JSON对话处理异常: " + e.getMessage(), e);
            throw new IYqueException("AI JSON对话处理异常: " + e.getMessage());
        }
    }


    /**
     * AI智能生成朋友圈内容
     * 根据用户输入的提示词，生成吸引人的朋友圈文案
     * @param prompt 用户输入的提示词
     * @param modelName 指定使用的模型名称
     * @return AI生成的朋友圈内容（JSON格式）
     */
    @Override
    public String aiGenerateFriendCircleContent(String prompt, String modelName) {
        if (StringUtils.isEmpty(prompt)) {
            throw new IYqueException("提示词不能为空");
        }

        try {
            List<String> models = modelFactory.getEnabledChatModels();
            if (models.isEmpty()) {
                throw new IYqueException("无可用聊天模型，请检查 ai.models 配置");
            }

            String actualModelName = modelName;
            if (StringUtils.isEmpty(actualModelName)) {
                actualModelName = models.get(0);
            }

            ChatLanguageModel chatModel = modelFactory.getChatModel(actualModelName, 0.7, 0.9);

            String systemPrompt = "你是一个专业的企业微信朋友圈营销文案专家，擅长生成吸引人、有感染力的朋友圈文案。" +
                    "请根据用户的需求，生成适合朋友圈发布的营销内容。\n\n" +
                    "要求：\n" +
                    "1. 生成的朋友圈文案要简洁有力，适合移动端阅读\n" +
                    "2. 内容要有吸引力和感染力，能够引起用户共鸣\n" +
                    "3. 可以包含适当的emoji表情，增加趣味性\n" +
                    "4. 文案长度严格控制在200-300字之间，避免被微信折叠\n" +
                    "5. 格式紧凑美观，段落清晰，避免过多空行\n" +
                    "6. 标题要简洁有力，10字以内\n" +
                    "7. 正文内容结构清晰，重点突出\n" +
                    "8. 开头第一句话必须非常吸引人，能够引起用户点击展开阅读的兴趣\n" +
                    "9. 使用简短的段落，每段不超过3行\n" +
                    "10. 适当使用emoji表情，但不要过度\n" +
                    "11. 严格按照以下JSON格式输出，不要包含任何额外的文本、说明或解释：\n" +
                    "{\n" +
                    "  \"name\": \"朋友圈标题（简洁有吸引力，10字以内）\",\n" +
                    "  \"content\": \"朋友圈正文内容\"\n" +
                    "}\n" +
                    "12. 返回的JSON必须能够被标准解析器直接解析";

            List<ChatMessage> messages = new ArrayList<>();
            messages.add(SystemMessage.from(systemPrompt));
            messages.add(UserMessage.from("请根据以下需求生成朋友圈内容：\n" + prompt));

            String response = chatModel.chat(messages).aiMessage().text();
            log.info("AI生成朋友圈内容完成, 模型: {}, 内容长度: {}", actualModelName, response.length());

            String jsonStr = response.trim();
            if (jsonStr.startsWith("```")) {
                int firstNewline = jsonStr.indexOf('\n');
                int lastNewline = jsonStr.lastIndexOf("```");
                if (firstNewline > 0 && lastNewline > firstNewline) {
                    jsonStr = jsonStr.substring(firstNewline + 1, lastNewline).trim();
                } else if (lastNewline > 3) {
                    jsonStr = jsonStr.substring(3, lastNewline).trim();
                }
            }

            // 处理JSON字符串中的特殊字符，特别是emoji相关的转义问题
            jsonStr = jsonStr.replaceAll("\\\\u([0-9a-fA-F]{4}|[0-9a-fA-F]{8})", "\\u$1");

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            // 配置ObjectMapper以更宽松地处理JSON
            mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
            com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(jsonStr);
            String name = rootNode.has("name") ? rootNode.get("name").asText() : "AI生成朋友圈";
            String content = rootNode.has("content") ? rootNode.get("content").asText() : jsonStr;

            // 使用ObjectMapper生成正确的JSON字符串
            com.fasterxml.jackson.databind.node.ObjectNode resultNode = mapper.createObjectNode();
            resultNode.put("name", name);
            resultNode.put("content", content);
            return mapper.writeValueAsString(resultNode);

        } catch (IYqueException e) {
            log.error("AI生成朋友圈内容失败: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("AI生成朋友圈内容异常: " + e.getMessage(), e);
            throw new IYqueException("AI生成朋友圈内容异常: " + e.getMessage());
        }
    }




}

