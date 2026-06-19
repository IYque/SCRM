package cn.iyque.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;


/**
 * 客服信息拉取记录表
 */
@Entity(name = "iyque_kf_msg")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueKfMsg {
    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;

    //上一次调用时返回的next_cursor，第一次拉取可以不填。若不填，从3天内最早的消息开始返回
    private String messageCursor;


    //指定拉取某个客服账号的消息
    private String openKfid;



    //拉取时间
    private Date pullTime;


}
