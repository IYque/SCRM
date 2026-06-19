package cn.iyque.service;

import cn.iyque.entity.IYqueDefaultMsg;
import me.chanjar.weixin.cp.bean.external.WxCpWelcomeMsg;
import me.chanjar.weixin.cp.bean.external.msg.Text;

public interface IYqueDefaultMsgService {

    IYqueDefaultMsg findDefaultMsg();



    void saveOrUpdate(IYqueDefaultMsg iYqueDefaultMsg);



    //设置默认欢迎语
    void setDefaultMsg(WxCpWelcomeMsg wxCpWelcomeMsg, Text text);




}
