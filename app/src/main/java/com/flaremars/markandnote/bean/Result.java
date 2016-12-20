package com.flaremars.markandnote.bean;

/**
 * Created by FlareMars on 2016/1/28.
 */
public class Result {

    public static final String CODE_SUCCESS = "200";
    public static final String CODE_FAIL = "210";

    public static final String MSG_SUCCESS = "请求成功";
    public static final String MSG_FAIL = "请求失败";

    private String code;

    private String msg;

    private Object data;

    private Long time;

    public Result() {
        this(CODE_SUCCESS,MSG_SUCCESS);
    }

    public Result(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
