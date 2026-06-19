package cn.iyque.domain;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.entity.IYqueRobotSub;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class RobotMsgDto {

    //附件类型
    private String msgtype;


    private Text text;


    private Image image;


    private News news;






    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Text {

        //文本内容，最长不超过2048个字节，必须是utf8编码
        private String content;

        //userid的列表，提醒群中的指定成员(@某个成员)，@all表示提醒所有人，如果开发者获取不到userid，可以使用mentioned_mobile_list
        private List<String> mentioned_list;

        //手机号列表，提醒手机号对应的群成员(@某个成员)，@all表示提醒所有人
        private List<String> mentioned_mobile_list;
    }



    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class  Image{

        //图片内容的base64编码
        private String base64;
        //图片内容（base64编码前）的md5值
        private String md5;


    }


    //消息类型，此时固定为news
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class News{
        //图文消息，一个图文消息支持1到8条图文
        private List<Articles> articles;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Articles{

        //标题，不超过128个字节，超过会自动截断
        private String title;
        //描述，不超过512个字节，超过会自动截断
        private String description;
        //点击后跳转的链接。
        private String url;
        //图文消息的图片链接，支持JPG、PNG格式，较好的效果为大图 1068*455，小图150*150。
        private String picurl;

    }

    //构建消息
    public static RobotMsgDto buildRobotMsg(IYqueRobotSub iYqueRobotSub){


        try {

            if(StringUtils.isNotEmpty(iYqueRobotSub.getMsgType())){
                if(iYqueRobotSub.getMsgType().equals(IYqueMsgAnnex.MsgType.MSG_TEXT)){//文本

                    return RobotMsgDto.builder().msgtype(IYqueMsgAnnex.MsgType.MSG_TEXT).text(
                            RobotMsgDto.Text.builder()
                                    .content(iYqueRobotSub.getText().getContent())
                                    .build()
                    ).build();

                }else if(iYqueRobotSub.getMsgType().equals(IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE)){ //图片
                    return RobotMsgDto.builder().msgtype(IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE).image(
                            convertImageToBase64AndMd5(iYqueRobotSub.getImage().getPicUrl())
                    ).build();
                }else if(iYqueRobotSub.getMsgType().equals(IYqueMsgAnnex.MsgType.MSG_TYPE_LINK)) { //图文
                    News build = News.builder().build();

                    Articles articles = Articles.builder().build();
                    articles.setDescription(iYqueRobotSub.getLink().getDesc());
                    articles.setUrl(iYqueRobotSub.getLink().getUrl());
                    articles.setTitle(iYqueRobotSub.getLink().getTitle());
                    articles.setPicurl(iYqueRobotSub.getLink().getPicUrl());
                    build.setArticles(
                            ListUtil.toList(articles)
                    );

                    return RobotMsgDto.builder().msgtype(IYqueMsgAnnex.MsgType.MSG_TYPE_NEWS).news(
                            build
                    ).build();
                }

            }

        }catch (Exception e){
            log.error("构建机器人发送消息失败:"+e.getMessage());
            return null;
        }


        return null;

    }

    /**
     * 将网络图片转换为Base64并计算MD5
     * @param picUrl 图片URL
     * @return 包含Base64和MD5的对象（或Map）
     */
    public static Image convertImageToBase64AndMd5(String picUrl){
        // 1. 下载图片到临时文件
        byte[] imageBytes = HttpUtil.downloadBytes(picUrl);

        // 2. 计算MD5
        String md5 = DigestUtil.md5Hex(imageBytes);

        // 3. 转换为Base64
        String base64 = Base64.encode(imageBytes);

        return Image.builder()
                .base64(base64)
                .md5(md5)
                .build();
    }
}
