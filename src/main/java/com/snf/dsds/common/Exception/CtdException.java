package com.snf.dsds.common.Exception;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-27 10:09
 **/
public class CtdException extends RuntimeException {
    private static final long serialVersionUID = 1208173750896834281L;

    public CtdException(String message) {
        super(message);
    }
}
