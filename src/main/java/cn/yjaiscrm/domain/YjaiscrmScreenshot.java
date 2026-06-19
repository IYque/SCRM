package cn.iyque.domain;


import cn.iyque.entity.BaseEntity;
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

@Entity(name = "iyque_screen_short")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueScreenshot extends BaseEntity {
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

    //操作人id
    private String userId;


    //企业用户部门id。仅当department_id在应用可见范围内才返回
    private Long depetId;


    //int	截屏内容的类型
    //1: 聊天
    //2: 通讯录
    //3: 邮件
    //4: 文件
    //5: 日程
    //6: 其他 对应枚举ScreenShotType
    private Integer shotType;


    //截屏内容
    private String shotContent;


    //企业用户的操作系统，目前支持返回的类型：iOS、iPad
    private String systemOs;


}
