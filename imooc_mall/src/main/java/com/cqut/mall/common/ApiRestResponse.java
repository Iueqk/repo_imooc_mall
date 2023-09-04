package com.cqut.mall.common;

import com.cqut.mall.exception.ImoocMallExceptionEnum;
import io.swagger.models.auth.In;

public class ApiRestResponse<T> {

    private Integer status;

    private String msg;

    private T data;

    private final static int OK_CODE = 10000;

    private static final String OK_MSG = "SUCCESS";

    public ApiRestResponse() {
        this(OK_CODE, OK_MSG);
    }

    public ApiRestResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ApiRestResponse(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ApiRestResponse<T> success() {
        return new ApiRestResponse<>();
    }

    public static <T> ApiRestResponse<T> success(T result) {
        ApiRestResponse<T> response = new ApiRestResponse<>();
        response.setData(result);
        return response;
    }

    public static <T> ApiRestResponse<T> error(Integer status, String msg) {
        return new ApiRestResponse<>(status, msg);
    }

    public static <T> ApiRestResponse<T> error(ImoocMallExceptionEnum exceptionEnum) {
        return new ApiRestResponse<>(exceptionEnum.getCode(), exceptionEnum.getMsg());
    }

    @Override
    public String toString() {
        return "ApiRestResponse{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
