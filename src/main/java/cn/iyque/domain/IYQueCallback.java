package cn.iyque.domain;


import lombok.Data;

@Data
public class IYQueCallback {
    private  String corpId;
    private String msg_signature;
    private String timestamp;
    private String nonce;
    private String echostr;
}
