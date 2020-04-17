package com.wessup.daily.user.service.users;

import com.wessup.daily.user.service.oauth.BasicAuth;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// api documents
// https://developer.github.com/v3/users/emails/
// https://developer.github.com/v3/users/

@Component
public class UsersAPI extends BasicAuth {
    private String baseSuffix;

    public UsersAPI() {
        this.baseSuffix = "/user";
    }

    public Map<String, Object> getUser(RestTemplate restTemplate, String apiURL, String token) {
        Map<String ,Object> userInfo = this.getUserInfo(restTemplate, apiURL, token);
        // one more request with username (for email)
        String email = this.getEmailByName(restTemplate, apiURL + baseSuffix, token);
        if (email == null) {
            // temp (throw error)
            return null;
        }
        userInfo.put("email", email);

        return userInfo;
    }

    private Map<String ,Object> getUserInfo(RestTemplate restTemplate, String apiURL, String token) {
        ResponseEntity<HashMap> response =
                this.apiExchange(restTemplate, apiURL + baseSuffix,
                        HttpMethod.GET, HashMap.class, this.setHeaders(token), null);
        HashMap<String ,Object> userInfo = response.getBody();
//        userInfo.put("token", token);

        return userInfo;
    }

    private String getEmailByName(RestTemplate restTemplate, String userURL, String token) {
        // Map<String, Object>
        // bad request 처리 필요
        String emailSuffix = "/emails";
        ResponseEntity<List> response =
                this.apiExchange(restTemplate, userURL+ emailSuffix,
                        HttpMethod.GET, List.class, this.setHeaders(token), null);

        HashMap<String, String> info = (HashMap<String, String>) response.getBody().get(0);
        String email = info.get("email");
        if (email == null) {
            // throw error
            return null;
        }
        return email;
    }
}
