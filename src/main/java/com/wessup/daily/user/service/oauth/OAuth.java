package com.wessup.daily.user.service.oauth;

import com.wessup.daily.user.entity.PushAllowed;
import com.wessup.daily.user.entity.User;
import com.wessup.daily.user.repository.PushAllowedRepository;
import com.wessup.daily.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// rest template http request
// handshake everytime...
// https://a1010100z.tistory.com/entry/SpringBoot-RestTemplate-vs-Webclient%EC%9E%91%EC%84%B1%EC%A4%91
// front : login redirect + transfer token to backend(in redirected url)

@Component
public class OAuth {
    private static final Logger logger = LoggerFactory.getLogger(OAuth.class);

    private UserRepository userRepository;

    private PushAllowedRepository paRepository;

    private final RestTemplate restTemplate;

    private OAuthAPI oAuthAPI;

    @Value("${github.client.id}")
    private String clientID;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.url}")
    private String github;

    @Value("${github.oauth.access}")
    private String tokenURL;

    @Value("${github.api}")
    private String apiURL;

    @Autowired
    public OAuth(RestTemplateBuilder restTemplateBuilder, UserRepository userRepository,
                 PushAllowedRepository paRepository){
        this.userRepository = userRepository;
        this.paRepository = paRepository;
        this.restTemplate = restTemplateBuilder.build();
        this.oAuthAPI = new OAuthAPI(this.clientID);
    }

    public String githubConfirm(){
        String url = this.github + "?client_id=" + this.clientID;
        // scope setting
        url += "&scope=user%20repo";
        return url;
    }

    protected HttpHeaders setHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "token " + token);
        return headers;
    }

    protected ResponseEntity apiExchange(String url, String token, HttpMethod method, Class c) {
        HttpHeaders headers = this.setHeaders(token);
        HttpEntity<String> request = new HttpEntity("", headers);
        ResponseEntity<Class> response = this.restTemplate.exchange(url, method, request, c);

        return response;
    }

    public String githubAccess(String code){
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("code", code);
        body.add("client_id", this.clientID);
        body.add("client_secret", this.clientSecret);
        RequestEntity<MultiValueMap> request = RequestEntity
                .post(URI.create(this.tokenURL))
                .accept(MediaType.APPLICATION_JSON)
                .body(body);
        HashMap<String, String> info = this.restTemplate.postForObject(this.tokenURL, request, HashMap.class);

        logger.info(info.get("access_token"));
        this.getUserInfo(info.get("access_token"));
        return info.get("access_token");
    }

    public Map<String ,String> getUserInfo(String token) {
        String suffix = "/user";
        ResponseEntity<HashMap> response = this.apiExchange(this.apiURL + suffix, token, HttpMethod.GET, HashMap.class);

        HashMap<String ,String> userInfo = response.getBody();
        logger.info(userInfo.get("access_token"));
        userInfo.put("token", token);
        // one more request with username (for email)
        String email = this.getEmailByName(userInfo.get("login"), token);
        userInfo.put("email", email);

        return userInfo;
    }

    public String getEmailByName(String username, String token) {
        // Map<String, String>
        // bad request 처리 필요
        String suffix = "/user/emails";
        ResponseEntity<List> response = this.apiExchange(this.apiURL + suffix, token, HttpMethod.GET, List.class);

        logger.info(response.getBody().get(0).toString()); // temp (for test)
        HashMap<String, String> info = (HashMap<String, String>) response.getBody().get(0);
        String email = info.get("email");
        if (email == null) {
            // throw error
            return null;
        }
        return email;
    }

    public void saveUser(Map<String, String> info) {
        String username = info.get("login");
        String token = info.get("token");
        User user = User.builder()
                .email(info.get("email"))
                .username(username)
                .token(token)
                .nodeId(info.get("node_id"))
                .userId(Long.parseLong(info.get("id")))
                .build();
        this.userRepository.save(user);
    }

    public void savePush(String username) {
        User user = this.userRepository.findByUsername(username);
        PushAllowed pa = PushAllowed.builder().user(user).build();
        this.paRepository.save(pa);
    }

    public boolean revokeToken(String username) {
        // retrieve token by username (order by time desc)
        User user = this.userRepository.findByUsername(username);
        String token = user.getToken();

        OAuthReturnStatus response = this.oAuthAPI.revokeToken(this.restTemplate, this.apiURL, token);
        if (response.getSuccess()) {
            logger.info(response.getMessage());
            logger.info(response.getBody().toString());
        }
        else {
            logger.info(response.getMessage());
        }
        return response.getSuccess();
    }

    public void checkToken(String username) {
        User user = this.userRepository.findByUsername(username);
        String token = user.getToken();

    }
}

