package com.pfe.pfeaccdemie.service.impl;

import com.pfe.pfeaccdemie.repositories.UserRepository;
import com.pfe.pfeaccdemie.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class AuthServiceimpl implements AuthService {
    @Autowired
    private UserRepository userRepository ;

}