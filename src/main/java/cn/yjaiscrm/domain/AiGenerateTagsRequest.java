package cn.iyque.domain;

import lombok.Data;

@Data
public class AiGenerateTagsRequest {
    private String prompt;
    private Integer groupCount;
    private Integer tagCountPerGroup;
}
