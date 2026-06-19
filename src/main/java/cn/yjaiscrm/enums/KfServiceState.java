package cn.iyque.enums;


import lombok.Data;
import lombok.Getter;

@Getter
public enum KfServiceState {


    KF_SERVICE_STATE_WCL(0,"未处理"),
    KF_SERVICE_STATE_RGZNJD(1,"由智能助手接待"),
    KF_SERVICE_STATE_PDZ(2,"待接入池排队中"),
    KF_SERVICE_STATE_RGJD(3,"由人工接待"),
    KF_SERVICE_STATE_JJS(4,"已结束/未开始");

    private Integer state;
    private String val;

    KfServiceState(Integer state,String val){
        this.state=state;
        this.val=val;
    }

}
