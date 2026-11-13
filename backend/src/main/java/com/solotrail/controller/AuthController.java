package com.solotrail.controller;

import com.solotrail.dto.LoginRequest;
import com.solotrail.dto.RegisterRequest;
import com.solotrail.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String registerUser(@RequestBody RegisterRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody LoginRequest request) {

        return authService.loginUser(
                request.getEmail(),
                request.getPassword()
        );
    }
}

