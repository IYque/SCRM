package cn.iyque.enums;


import lombok.Getter;

@Getter
public enum GroupMsgSendStatus {



    GROUP_MSG_TYPE_WFS(0,0,"未发送"),
    GROUP_MSG_TYPE_YFS(1,1,"已发送"),
    GROUP_MSG_TYPE_NHY(2,2,"因客户不是好友导致发送失败"), //失败
    GROUP_MSG_TYPE_JSSX(2,3,"因客户已经收到其他群发消息导致发送失败"), //失败
    GROUP_MSG_TYPE_WX(3,4,"无效或无法发送的external_userid或chatid列表"), //无效
    GROUP_MSG_TYPE_FAIL(2,5,"任务构建失败,未返回信息id");//失败





    private Integer status;

    private Integer statusSub;


    private String msg;


    GroupMsgSendStatus(Integer status, Integer statusSub, String msg){
        this.status=status;
        this.statusSub=statusSub;
        this.msg=msg;
    }



}
