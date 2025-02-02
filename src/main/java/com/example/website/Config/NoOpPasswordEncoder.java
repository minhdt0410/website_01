package com.example.website.Config;

import org.springframework.security.crypto.password.PasswordEncoder;

public class NoOpPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        // Không mã hóa mật khẩu, trả về mật khẩu nguyên gốc
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // So sánh mật khẩu plaintext
        return rawPassword.toString().equals(encodedPassword);
    }
}