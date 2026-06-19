package cn.iyque.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class KnowledgeInfoUploadRequest {

    private Long kid;

    private MultipartFile file;
}
