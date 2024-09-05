package cn.iyque.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "iyque_customer_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYQueCustomerInfo {
    //主键为id且自增
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    //客户id
    private String externalUserid;


    //添加人id
    private String userId;


    //添加的渠道标识
    private String state;

    //添加时间
    private Date addTime;


    // 0正常;1:客户流失;2:员工删除客户
    private Integer status;

}
