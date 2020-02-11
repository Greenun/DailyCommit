package com.wessup.daily.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private Authorization auth;

    @Autowired
    public UserService(Authorization auth){
        this.auth = auth;
    }

}
