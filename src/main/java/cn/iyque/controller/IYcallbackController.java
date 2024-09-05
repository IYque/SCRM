package cn.iyque.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.iyque.domain.IYQueCallback;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.enums.CustomerStatusType;
import cn.iyque.entity.IYqueConfig;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueCustomerInfoService;
import cn.iyque.utils.IYqueCryptUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.constant.WxCpConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



/**
 * 系统回调
 */
@RestController
@RequestMapping("/iycallback")
@Slf4j
public class IYcallbackController {


    @Autowired
    private IYqueConfigService iYqueConfigService;

    @Autowired
    private IYqueCustomerInfoService iYqueCustomerInfoService;


    /**
     * get数据校验
     * @param queCallback
     * @return
     */
    @GetMapping(value = "/handle")
    public String handle(IYQueCallback queCallback) {

        String tip="error";
        IYqueConfig iYqueConfig = iYqueConfigService.findIYqueConfig();

        if(StrUtil.isNotEmpty(iYqueConfig.getToken())
        &&StrUtil.isNotEmpty(iYqueConfig.getEncodingAESKey())
        &&StrUtil.isNotEmpty(iYqueConfig.getCorpId())){
            IYqueCryptUtil iYqueCryptUtil = new IYqueCryptUtil(iYqueConfig.getToken(), iYqueConfig.getEncodingAESKey(), iYqueConfig.getCorpId());

            try {
                if(StrUtil.isEmpty(queCallback.getEchostr())){
                    return "success";
                }
                tip=iYqueCryptUtil.verifyURL(queCallback.getMsg_signature(), queCallback.getTimestamp(), queCallback.getNonce(), queCallback.getEchostr());
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
        }

        return tip;
    }


    /**
     *  post数据校验
     * @param msg
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    @PostMapping(value = "/handle")
    public String handle(@RequestBody String msg, @RequestParam(name = "msg_signature") String signature,
                         String timestamp, String nonce) {

        IYqueConfig iYqueConfig = iYqueConfigService.findIYqueConfig();
        IYqueCryptUtil iYqueCryptUtil = new IYqueCryptUtil(iYqueConfig.getToken(), iYqueConfig.getEncodingAESKey(), iYqueConfig.getCorpId());
        try {
            String decrypt = iYqueCryptUtil.decrypt(signature, timestamp, nonce, msg);

            IYqueCallBackBaseMsg callBackBaseMsg = XmlUtil.xmlToBean(XmlUtil.parseXml(decrypt).getFirstChild(), IYqueCallBackBaseMsg.class);
            log.info("活码欢迎语回调"+callBackBaseMsg);

            if(null != callBackBaseMsg){
                if(WxCpConsts.EventType.CHANGE_EXTERNAL_CONTACT.equals(callBackBaseMsg.getEvent())){//客户变更
                    //客户新增
                    if(WxCpConsts.ExternalContactChangeType.ADD_EXTERNAL_CONTACT.equals(callBackBaseMsg.getChangeType())){
                        iYqueCustomerInfoService.addCustomerCallBackAction(callBackBaseMsg);
                    }

                    //客户流失(被客户删除)
                    if(WxCpConsts.ExternalContactChangeType.DEL_FOLLOW_USER.equals(callBackBaseMsg.getChangeType())){
                        iYqueCustomerInfoService.updateCustomerInfoStatus(
                                callBackBaseMsg.getExternalUserID(),callBackBaseMsg.getUserID(),
                                CustomerStatusType.CUSTOMER_STATUS_TYPE_LS.getCode()
                        );
                    }

                    //删除客户
                    if(WxCpConsts.ExternalContactChangeType.DEL_EXTERNAL_CONTACT.equals(callBackBaseMsg.getChangeType())){
                        iYqueCustomerInfoService.updateCustomerInfoStatus(
                                callBackBaseMsg.getExternalUserID(),callBackBaseMsg.getUserID(),
                                CustomerStatusType.CUSTOMER_STATUS_TYPE_DEL.getCode()
                                );
                    }

                }
            }

            return decrypt;
        } catch (Exception e) {
            String sRespData = iYqueCryptUtil.getTextRespData("success");
            return iYqueCryptUtil.encrypt(sRespData);
        }
    }






}
