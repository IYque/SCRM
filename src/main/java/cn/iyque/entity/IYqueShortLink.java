package cn.iyque.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "iyque_short_link")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "delFlag = 0")
public class IYqueShortLink {
    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;

    //名称
    private String codeName;

    //员工id,多个使用逗号隔开
    private String userId;

    //员工名称,多个使用逗号隔开
    private String userName;

    //是否免验证:true:免验证 false:需验证
    private Boolean skipVerify=false;

    //是否可重复添加: true:可重复添加 false:不可重复添加
    private Boolean isExclusive=false;

    //标签id,多个使用逗号隔开
    private String tagId;

    //标签名,多个使用逗号隔开
    private String tagName;

    //未开启时段欢迎语的附件，如果：startPeriodAnnex 为true 当前字段值为空。
    private String weclomeMsg;


    //渠道标识
    private String codeState;

    //活码地址
    private String codeUrl;

    //联系方式的配置id
    private String configId;



    //是否开启时段欢迎语 true:开启时段欢迎语; false:关闭时段欢迎语;
    @ColumnDefault("false")
    private boolean startPeriodAnnex;



    //客户备注类型cn.iyque.enums.RemarksType
    private Integer remarkType;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    //是否删除标识
    private Integer delFlag;

    //未开启时段欢迎语的附件，如果：startPeriodAnnex 为true 当前字段值为空。
    @Transient
    private List<IYqueMsgAnnex> annexLists;


    //时段欢迎语的附件
    @Transient
    private List<IYqueAnnexPeriod> periodAnnexLists;



    @PrePersist
    @PreUpdate
    private void setDefaultDelFlag() {
        if (this.delFlag == null) {
            this.delFlag = 0;
        }
    }
}
