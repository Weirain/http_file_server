package com.yuchao.fileclient.model;

/**
 * 接口返回值
 * @author yuchao
 * @date 2021/3/27 11:34 上午
 **/
public class ResponseVo {
    private int code = -1;
    private String message;

    @Override
    public String toString() {
        return "ResponseVo{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
