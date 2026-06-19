package cn.iyque.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity(name = "iyque_config")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueConfig {
    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;

    private String corpId;

    private String agentId;

    private String agentSecert;

    private String token;

    private String encodingAESKey;



    //会话存档sdk路径
    private String msgAuditLibPath;

    //消息加密私钥
    @Lob
    private String msgAuditPriKey;


    //会话存档Secret
    private String msgAuditSecret;
}
