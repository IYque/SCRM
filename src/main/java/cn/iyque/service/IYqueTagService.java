package cn.iyque.service;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxCpTag;
import me.chanjar.weixin.cp.bean.external.WxCpUserExternalTagGroupInfo;

import java.util.List;

public interface IYqueTagService {

     List<WxCpUserExternalTagGroupInfo.Tag> listAll() throws Exception;
}
