package com.meishe.net.custom;

/**
 * 网络请求返回的公共Response
 * The public Response returned by the network request
 *
 * @param <T> the type parameter
 */
public class BaseResponse<T> {
    private int code = -1;//适配旧版本1msg
    private String msg;
    private String enMsg;
    /*
     * 适配旧版本1msg
     * Adapt the old version 1msg
     * */
    private String message;
    private int errNo;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getEnMsg() {
        return enMsg;
    }

    public void setEnMsg(String enMsg) {
        this.enMsg = enMsg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrNo() {
        return errNo;
    }

    public void setErrNo(int errNo) {
        this.errNo = errNo;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", enMsg='" + enMsg + '\'' +
                ", message='" + message + '\'' +
                ", errNo=" + errNo +
                ", data=" + data +
                '}';
    }
}
