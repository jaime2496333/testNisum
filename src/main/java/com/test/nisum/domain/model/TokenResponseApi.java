package com.test.nisum.domain.model;

public class TokenResponseApi {

    private String token;

    public TokenResponseApi() {
//    Empty
    }

    public TokenResponseApi(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "TokenResponseBody{" +
                "token='" + token + '\'' +
                '}';
    }
}
