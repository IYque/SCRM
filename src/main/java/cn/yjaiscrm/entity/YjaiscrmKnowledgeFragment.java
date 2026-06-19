package cn.iyque.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;


/**
 * 知识片段
 */
@Entity(name = "iyque_knowledge_fragment")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "delFlag = 0")
public class IYqueKnowledgeFragment {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;

    /**
     * 知识库ID
     */
    private Long kid;

    /**
     * 文档ID
     */
    private Long docId;


    /**
     * 片段索引下标
     */
    private Integer idx;

    /**
     * 文档内容
     */
    @Lob // 表示大对象
    @Column(columnDefinition = "TEXT") // 明确指定数据库类型为 TEXT
    private String content;



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
