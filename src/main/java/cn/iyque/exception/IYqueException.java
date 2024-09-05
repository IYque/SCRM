package cn.iyque.exception;


import lombok.Data;

@Data
public class IYqueException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected String msg;

    private Integer code = -1;

    public IYqueException(String msg)
    {
        this.msg = msg;
    }

    public IYqueException(Integer code,String msg)
    {
        this.code=code;
        this.msg = msg;
    }

}
