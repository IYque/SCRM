package cn.iyque.domain.fileType;

import lombok.Data;

//小程序
@Data
public class Miniprogram{
    //小程序标题
    private String title;
    //小程序封面地址
    private String picUrl;
    //小程序id
    private String appid;
    //小程序页面
    private String page;
}
