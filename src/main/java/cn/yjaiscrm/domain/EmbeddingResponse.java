package cn.iyque.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmbeddingResponse {

    private List<EmbeddingResult> embeddings;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmbeddingResult {
        private String text;
        private List<Float> vector;
    }
}
