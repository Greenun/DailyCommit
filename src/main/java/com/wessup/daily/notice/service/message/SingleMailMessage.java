package com.wessup.daily.notice.service.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SingleMailMessage implements CustomMessage{

    private String email;

    public SingleMailMessage(@JsonProperty("body")String email) {
        this.email = email;
    }

    public String getBody() {
        return this.email;
    }
}
