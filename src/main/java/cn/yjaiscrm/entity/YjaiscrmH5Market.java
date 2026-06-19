package cn.iyque.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "iyque_h5_market")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueH5Market {

    @Id
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型（1：图片h5）
     */
    private Integer type;


    /**
     *  控件类型(1:客服;2:活码;3链接)
     */
    private Integer controlType;


    /**
     * 控件子类型(control_type为1,当前为1:客服)(control_type为2,当前为1:员工活码;2:无限群活码;3:锁客群码;4:红包活码;5:门店导购码;6:门店群活码)（control_type为3,当前为1:获客链接,2:表单链接;3:公域短链;4:自定义链接)
     */
    private Integer controlSubType;

    /**
     * 控件链接
     */
    private String controlUrl;


    /**
     * 预览背景图
     */
    private String backgroundUrl;


    /**
     * 主标题
     */
    private String title;

    /**
     * 副标题
     */
    private String titleSub;


    /**
     * 引导文案
     */
    private String guideTip;


    /**
     * h5营销链接
     */
    private String h5Url;


    /**
     * h5营销链接活码
     */
    private String h5QrUrl;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;


    /**
     * 创建人
     */
    private String createBy;


    /**
     * 状态:0 正常 1 删除
     */
    private Integer delFlag;

    @PrePersist
    @PreUpdate
    private void setDefaultDelFlag() {
        if (this.delFlag == null) {
            this.delFlag = 0;
        }
    }
}
