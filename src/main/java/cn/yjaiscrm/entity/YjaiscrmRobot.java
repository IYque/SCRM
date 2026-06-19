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


/**
 * 机器人
 */
@Entity(name = "iyque_robot")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "delFlag = 0")
public class IYqueRobot {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;





    //名称
    private String title;


     //WebHook
    private String webHookUrl;

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
    private List<IYqueRobotSub> robotSubList;


    @Transient
    private Long robotId;


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
