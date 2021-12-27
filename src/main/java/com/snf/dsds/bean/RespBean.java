package com.snf.dsds.bean;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-21 14:55
 **/
public class RespBean {
    private String status;
    private String message;
    private Object data;

    public static RespBean build() {
        return new RespBean();
    }

    public static RespBean ok(String msg) {
        return new RespBean("success", msg, null);
    }

    public static RespBean ok(String msg, Object obj) {
        return new RespBean("success", msg, obj);
    }

    public static RespBean error(String msg) {
        return new RespBean("faild", msg, null);
    }

    public static RespBean error(String msg, Object obj) {
        return new RespBean("faild", msg, obj);
    }

    private RespBean() {
    }

    private RespBean(String status, String msg, Object obj) {
        this.status = status;
        this.message = msg;
        this.data = obj;
    }

    public String getStatus() {
        return status;
    }

    public RespBean setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public RespBean setMessage(String msg) {
        this.message = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public RespBean setData(Object obj) {
        this.data = obj;
        return this;
    }

}
