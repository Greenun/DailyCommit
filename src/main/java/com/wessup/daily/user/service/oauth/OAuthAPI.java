package com.wessup.daily.user.service.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Component
public class OAuthAPI extends BasicAuth {

    private String suffix;

    @Value("${github.client.id}")
    private String clientID;

    public OAuthAPI() {
        this.suffix = "/applications/" + this.clientID + "/token";
    }

    public class RequestComponents {
        public HttpHeaders httpHeaders;
        public HashMap<String, String> body;

        public RequestComponents(HttpHeaders httpHeaders, HashMap body) {
            this.httpHeaders = httpHeaders;
            this.body = body;
        }
    }

    protected HttpHeaders setHeaders(String token) {
        HttpHeaders headers = super.setHeaders(token);
        headers.add("Accept", "application/vnd.github.doctor-strange-preview+json");
        return headers;
    }

    protected HashMap<String, String> setBody(String token) {
        HashMap<String, String> body = new HashMap();
        body.put("access_token", token);
        return body;
    }

    protected RequestComponents setComponents(String token) {
        return new RequestComponents(this.setHeaders(token), this.setBody(token));
    }

    private ResponseEntity apiExchange(RestTemplate restTemplate, String url, String token, HttpMethod method, Class c) {
        RequestComponents rc = this.setComponents(token);
        ResponseEntity response = super.apiExchange(restTemplate, url, method, c, rc.httpHeaders, rc.body);
        return response;
    }

    public OAuthReturnStatus revokeToken(RestTemplate restTemplate, String apiURL, String token) {
        ResponseEntity<HashMap> response =
                this.apiExchange(restTemplate, apiURL + this.suffix, token, HttpMethod.DELETE, HashMap.class);

        OAuthReturnStatus rs = new OAuthReturnStatus(response.getStatusCode());
        rs.setBody(response.getBody());
        return rs;
    }

    public OAuthReturnStatus checkToken(RestTemplate restTemplate, String apiURL, String token) {
        ResponseEntity<HashMap> response =
                this.apiExchange(restTemplate, apiURL + this.suffix, token, HttpMethod.POST, HashMap.class);
        HttpStatus status = response.getStatusCode();
        return new OAuthReturnStatus(status);
    }
}
