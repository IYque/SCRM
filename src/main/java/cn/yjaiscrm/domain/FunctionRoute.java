package cn.iyque.domain;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionRoute {
    private String path;
    private String title;
    private String category;
    private String description;
    
    public FunctionRoute(String path, String title, String category) {
        this.path = path;
        this.title = title;
        this.category = category;
        this.description = "";
    }
}
