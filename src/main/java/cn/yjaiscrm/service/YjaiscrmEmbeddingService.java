package cn.iyque.service;

import java.util.List;

public interface IYqueEmbeddingService {


    /**
     * 构建向量schema
     * @param kid
     */
    void createSchema(Long kid);


    /**
     * 保存向量数据库
     * @param chunkList 文档按行切分的片段
     * @param kid 知识库ID
     * @param docId 文档ID
     * @param fidList
     */
    void storeEmbeddings(List<String> chunkList, String kid, String docId, List<String> fidList);


    /**
     * 删除知识库
     * @param kid
     */
    void removeByKid(String kid);


    /**
     * 根据内容检索对应的知识库相关数据向量值
     * @param query
     * @param kid
     * @return
     */
    List<Float> getQueryVector(String query, String kid);
}
