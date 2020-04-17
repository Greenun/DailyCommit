package com.wessup.daily.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MultiMailMessage implements CustomMessage{

    private List<String> emailList;

    public MultiMailMessage(@JsonProperty("body")List<String> emailList) {
        this.emailList = emailList;
    }

    public List<String> getBody() {
        return this.emailList;
    }
}

