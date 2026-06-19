package cn.iyque.domain;

import cn.iyque.entity.BaseEntity;
import cn.iyque.entity.IYqueMsgAnnex;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.util.Date;
import java.util.List;


@Data
@TableName("iyque_friend_circle")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueFriendCircle extends BaseEntity {

    /**
     * 主键
     */
     @TableId
     private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 文本内容
     */
     private String content;

    /**
     * 异步任务id，最大长度为64字节，24小时有效；
     */
    private String jobId;


    /**
     * 企业微信朋友圈id
     */
    private String  momentId;


    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    //是否删除标识
    @TableLogic
    private Integer delFlag;


    /**
     * 朋友圈附件
     */
   @TableField(exist = false)
    private List<IYqueMsgAnnex> annexLists;


}
