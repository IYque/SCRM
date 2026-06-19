package cn.iyque.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@TableName("iyque_tag")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IYqueTag extends BaseEntity{

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 微信端返回的id
     */
    private String tagId;

    /**
     * 标签组的主键id
     */
    private String groupId;

    /**
     * 标签名
     */
    private String name;


    /**
     * 1:客户企业标签;
     */
    private Integer tagType;

    /**
     * 排序
     */
    private Long  orderNumber;



    /**
     * 0:正常;1:删除;
     */
    @TableLogic
    private Integer delFlag;
}
