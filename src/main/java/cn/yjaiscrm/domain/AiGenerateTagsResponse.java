package cn.iyque.domain;

import lombok.Data;
import java.util.List;

@Data
public class AiGenerateTagsResponse {
    private String groupName;
    private List<TagItem> tags;
    
    @Data
    public static class TagItem {
        private String name;
    }
}
