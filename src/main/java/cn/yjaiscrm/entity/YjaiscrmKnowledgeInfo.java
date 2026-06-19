package cn.iyque.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Date;


/**
 * 知识库
 */
@Entity(name = "iyque_knowledge_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "delFlag = 0")
public class IYqueKnowledgeInfo {


    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;



    /**
     * 知识库名称
     */
    private String kname;



    /**
     * 描述
     */
    private String description;




    //附件相关接受参数
    @Transient
    private MultipartFile file;







    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    //是否删除标识
    private Integer delFlag;


    @PrePersist
    @PreUpdate
    private void setDefaultDelFlag() {
        if (this.delFlag == null) {
            this.delFlag = 0;
        }
    }

}
