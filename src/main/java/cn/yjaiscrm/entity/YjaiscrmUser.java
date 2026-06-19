package cn.iyque.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity(name = "iyque_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueUser {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;


    //员工名称
    private String name;


    //员工对应企微id
    private String userId;



    //职务
    private String position;

    //员工状态：1=已激活，2=已禁用，4=未激活，5=退出企业。
    private Integer status;


    @Transient
    private boolean avg=true;





}
