package cn.iyque.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "iyque_complain_annex")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueComplainAnnex {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;


    //投诉id
    private Long complainId;

    //附件类型(1:投诉人;2:处理人)
    private Integer annexType;

    //附件地址
    private String annexUrl;

}
