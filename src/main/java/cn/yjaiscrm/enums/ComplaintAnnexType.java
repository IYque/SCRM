package cn.iyque.enums;

import lombok.Getter;

@Getter
public enum ComplaintAnnexType {

    ONE_TYPE(1,"投诉人"),
    TWO_TYPE(2,"处理人");


    private Integer code;
    private String val;

    ComplaintAnnexType(Integer code, String val){
        this.code=code;
        this.val=val;
    }
}
