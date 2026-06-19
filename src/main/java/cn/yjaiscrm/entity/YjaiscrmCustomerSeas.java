package cn.iyque.entity;


import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "iyque_customer_seas")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "delFlag = 0")
public class IYqueCustomerSeas {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    @ExcelIgnore
    private Long id;


    //手机号
    @ExcelProperty(value = "手机号")
    private String phoneNumber;


    //客户姓名
    @ExcelProperty(value = "客户姓名")
    private String customerName;



    //分配员工id
    @ExcelIgnore
    private String allocateUserId;


    //分配员工名称
    @ExcelIgnore
    private String allocateUserName;



    //当前状态(0:待添加；1:待通过;2:已通过)
    @ExcelIgnore
    private Integer currentState;




    //是否删除标识
    @ExcelIgnore
    private Integer delFlag;



    //创建时间，即当前匹次模版导入时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelIgnore
    private Date createTime;


    @PrePersist
    @PreUpdate
    private void setDefaultDelFlag() {
        if (this.delFlag == null) {
            this.delFlag = 0;
        }
    }


}
