package cn.iyque.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "iyque_file_security")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueFileSecurity extends BaseEntity{
    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;


    //操作时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operateTime;

    //操作人名称
    private String userName;


    //用户类型 1：微信用户；2：企业微信用户 3:企业员工
    private Integer userType;

    //操作类型 对应枚举 FileSecurityOperation
    private Integer operateType;

    //文件操作来源 对应枚举 FileSecuritySource
    private Integer opreateSource;

    //文件操作说明
    private String operateFileInfo;
}
