package cn.iyque.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@TableName("iyque_tag_group")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IYqueTagGroup extends BaseEntity{
    /**
     * 主键
     */
    @TableId
    private Long id;


    /**
     * 企业微信返回的id
     */
    private String groupId;

    /**
     * 标签组名
     */
    private String groupName;


    /**
     * 排序
     */
    private Long  orderNumber;


    /**
     * 标签分组类型(1:客户企业标签;2:客群标签)
     */
    private Integer groupTagType;




    @TableLogic
    private Integer delFlag;



    @TableField(exist = false)
    private List<IYqueTag> weTags;


}
