package cn.iyque.domain.fileType;

import lombok.Data;

//链接
@Data
public class Link{
    //链接标题
    private String title;
    //链接封面
    private String picUrl;
    //链接描述
    private String desc;
    //链接跳转地址
    private String url;
}
