package cn.iyque.chain.vectorizer;

import java.util.List;

/**
 * 向量化
 */
public interface Vectorization {


    /**
     *  多向量
     * @param chunkList
     * @param kid
     * @return
     */
    List<List<Float>> batchVectorization(List<String> chunkList, String kid);


    /**
     *  单一向量
     * @param chunk
     * @param kid
     * @return
     */
    List<Float> singleVectorization(String chunk, String kid);
}