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

@Entity(name = "iyque_complain")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYQueComplain {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;


    //联系方式
    private String complainUserPhone;


    //投诉内容
    private String  complainUserContent;

    //投诉类型,对应枚举ComplaintContent
    private Integer complainType;

    //投诉类型
    @Transient
    private String complainTypeContent;


    //投诉时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date complainTime=new Date();


    //处理人id
    private String handleWeUserId;



    //处理人名称
    @Transient
    private String handleUserName;


    //处理时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date handleTime;


    //处理内容
    private String  handleContent;


    //处理状态(1:未处理;2:已处理)
    private Integer handleState;


    //附件
    @Transient
    private List<IYqueComplainAnnex> complainAnnexList;


    @PrePersist
    public void prePersist() {
        if (handleState == null) {
            handleState = 1;
        }
    }

}
