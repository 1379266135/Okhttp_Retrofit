package com.elaine.okretrolib;

/**
 * Created by elaine on 2016/11/17.
 */

public class BaseResult<T> {
    private String status;
    private String message;

    private T result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
