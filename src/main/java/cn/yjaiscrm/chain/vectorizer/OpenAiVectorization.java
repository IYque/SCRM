package cn.iyque.chain.vectorizer;


import cn.iyque.service.IYqueAiService;
//import dev.langchain4j.community.model.zhipu.embedding.EmbeddingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
@RequiredArgsConstructor
public class OpenAiVectorization implements Vectorization{



    @Autowired
    private IYqueAiService yqueAiService;





    @Override
    public List<List<Float>> batchVectorization(List<String> chunkList, String kid) {
        List<List<Float>> vectorList;


//        EmbeddingResponse embeddings =yqueAiService.embedding(chunkList);
//
//        // 处理 OpenAI 返回的嵌入数据
//        vectorList = processOpenAiEmbeddings(embeddings);

//        return vectorList;
        return null;
    }



//    /**
//     * 处理 OpenAI 返回的嵌入数据
//     */
//    private List<List<Float>> processOpenAiEmbeddings(EmbeddingResponse embeddings) {
//        List<List<Float>> vectorList = new ArrayList<>();
//
////        embeddings.getData().forEach(data->{
////            List<Float> collect = data.getEmbedding().stream()
////                    .map(Float::floatValue)
////                    .collect(Collectors.toList());
////            vectorList.add(collect);
////        });
//
//        return vectorList;
//    }





    @Override
    public List<Float> singleVectorization(String chunk, String kid) {
        List<String> chunkList = new ArrayList<>();
        chunkList.add(chunk);
        List<List<Float>> vectorList = batchVectorization(chunkList, kid);
        return vectorList.get(0);
    }
}
