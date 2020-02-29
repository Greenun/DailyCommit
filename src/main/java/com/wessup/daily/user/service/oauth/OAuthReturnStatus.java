package com.wessup.daily.user.service.oauth;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OAuthReturnStatus {

    private boolean success;

    private int statusCode;

    private String message;

    private Object body = null;

    public void setBody(Object body) {
        this.body = body;
    }

    public void setMessage(int code) {
        if (code == 200) {
            this.message = "API Success";
        }
        else if (code == 401) {
            this.message = "OAuth Access Token is unavailable";
        }
        else if (code == 404) {
            this.message = "Invalid Token";
        }
        else if (code == 204) {
            this.message = "Token Deleted Successful";
        }
    }

    public OAuthReturnStatus(HttpStatus httpStatus) {
        this.success = httpStatus.is2xxSuccessful();
        this.statusCode = httpStatus.value();
        this.setMessage(this.statusCode);
    }

    public boolean getSuccess() {
        return this.success;
    }
}
