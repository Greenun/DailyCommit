package com.wessup.daily.user.service.oauth;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

public class BasicAuth {

    protected HttpHeaders setHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "token " + token);
        return headers;
    }

    protected ResponseEntity apiExchange(RestTemplate restTemplate, String url,
                                         HttpMethod method, Class c, HttpHeaders headers, @Nullable HashMap body) {
        HttpEntity<String> request = null;
        if (body != null) {
            request = new HttpEntity(body, headers);
        }
        else {
            request = new HttpEntity("", headers);
        }
        ResponseEntity<Class> response = restTemplate.exchange(url, method, request, c);

        return response;
    }
}

