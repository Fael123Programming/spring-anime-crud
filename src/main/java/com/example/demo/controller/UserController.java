package com.example.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping
    public String index(@AuthenticationPrincipal UserDetails userDetails) {
        return String.format("""
                <html>
                    <header>
                        <title>Welcome to your user page</title>
                    </header>
                    <body>
                        <h1>Hello, <b>%s</b>!</h1>
                        <h6>Made with Spring Boot</h6>
                    </body>
                </html>
                """, userDetails.getUsername());
    }
}
