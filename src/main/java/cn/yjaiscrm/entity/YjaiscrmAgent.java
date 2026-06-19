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
import java.util.List;

@Entity(name = "iyque_agent")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "delFlag = 0")
public class IYqueAgent {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;


    /**
     * 应用ID
     */
    private Integer agentId;

    /**
     * 应用密钥
     */
    private String  secret;


    /**
     * 企业应用名称
     */
    private String name;


    /**
     * logo
     */
    private String logoUrl;


    /**
     *  企业应用可见范围（部门）
     */
    private String allowPartyName;


    /** 企业应用可见范围（人员） */
    private String allowUserinfoName;




    /**
     * 更新人名称
     */
    private String updateBy;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;



    /**
     * 素材状态:0 正常 1 删除
     */
    private Integer delFlag;


    /**
     * 消息附件
     */
    @Transient
    private List<IYqueAgentSub> agentSub;





    @Transient
    private String msgTitle;





    @PrePersist
    @PreUpdate
    private void setDefaultDelFlag() {
        if (this.delFlag == null) {
            this.delFlag = 0;
        }
    }





}
