package cn.iyque.chain.vectorstore;

import java.util.List;


/**
 * 向量存储
 */
public interface IYqueVectorStore {

    /**
     * 向量存储
     * @param chunkList
     * @param vectorList
     * @param kid
     * @param docId
     * @param fidList
     */
    void storeEmbeddings(List<String> chunkList, List<List<Float>> vectorList, String kid, String docId, List<String> fidList);


    /**
     * 根据id删除指定向量数据
     * @param kid
     */
    void removeByKid(String kid);

    /**
     * 查询向量相似数据
     * @param queryVector
     * @param kid
     * @return
     */
    List<String> nearest(List<Float> queryVector, String kid);


    /**
     * 创建向量表
     * @param kid
     */
    void newSchema(String kid);

}
