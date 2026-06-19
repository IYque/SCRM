package cn.iyque.enums;


import lombok.Getter;

@Getter
public enum MediaType {

    A("0","图片"),
    B("4","文本"),
    C("7","热词分类"),
    D("9","图文"),
    E("13","组合话术");


    private String key;
    private String value;

    MediaType(String key,String value){
        this.key=key;
        this.value=value;
    }
}
