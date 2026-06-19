package cn.iyque.service.impl;

import cn.iyque.chain.vectorizer.Vectorization;
import cn.iyque.chain.vectorstore.IYqueVectorStore;
import cn.iyque.service.IYqueEmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IYqueEmbeddingServiceImpl implements IYqueEmbeddingService {

    @Autowired
    private IYqueVectorStore iYqueVectorStore;


    @Autowired
    private Vectorization vectorization;


    @Override
    public void createSchema(Long kid) {
        iYqueVectorStore.newSchema(String.valueOf(kid));
    }

    @Override
    public void storeEmbeddings(List<String> chunkList, String kid, String docId, List<String> fidList) {

        List<List<Float>> vectorList = vectorization.batchVectorization(chunkList, kid);
        iYqueVectorStore.storeEmbeddings(chunkList,vectorList,kid,docId,fidList);
    }

    @Override
    public void removeByKid(String kid) {
        iYqueVectorStore.removeByKid(kid);
    }

    @Override
    public List<Float> getQueryVector(String query, String kid) {
        return vectorization.singleVectorization(query,kid);
    }
}
