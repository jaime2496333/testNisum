package com.test.nisum.domain.model;

public class ResponseApi <T> {

    private Object message;
    private ErrorApi error;
    private T body;

    public ResponseApi() {
//        Empty
    }

    public ResponseApi(ErrorApi error, T body, Object message) {
        this.error = error;
        this.body = body;
        this.message = message;
    }

    public ErrorApi getError() {
        return this.error;
    }

    public void setError(ErrorApi error) {
        this.error = error;
    }

    public T getBody() {
        return this.body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public Object getMessage() {
        return this.message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResponseApi{" +
                "error=" + this.error +
                ", body=" + this.body +
                ", message=" + this.message +
                '}';
    }
}
