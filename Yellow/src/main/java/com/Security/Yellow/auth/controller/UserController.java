package com.Security.Yellow.auth.controller;

import java.util.Set;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Security.Yellow.auth.dto.ProfileResponse;
import com.Security.Yellow.auth.security.UserPrincipal;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @GetMapping("/profile")
    public ProfileResponse profile(@AuthenticationPrincipal UserPrincipal principal) {
        return new ProfileResponse(principal.getId(), principal.getUsername(), principal.getEmail(),
                Set.copyOf(principal.getRoles()));
    }
}