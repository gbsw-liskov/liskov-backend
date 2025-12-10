package com.example.liskovbackend.common.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    public Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("인증되지 않은 요청입니다.");
        }
        return Long.valueOf(auth.getPrincipal().toString());
    }
}
