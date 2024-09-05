package cn.iyque.domain;

import cn.iyque.constant.HttpStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
public class ResponseResult<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    private long count;

    public ResponseResult(T data,long count) {
        this.code = HttpStatus.SUCCESS;
        this.data = data;
        this.msg="";
        this.count=count;
    }
    public ResponseResult(int code, String message, T data) {
        this.code = code;
        this.msg = message;
        this.data = data;
    }

    public ResponseResult(T data) {
        this.code = HttpStatus.SUCCESS;
        this.data = data;
    }

    public ResponseResult(){
        this.code= HttpStatus.SUCCESS;
    }



}