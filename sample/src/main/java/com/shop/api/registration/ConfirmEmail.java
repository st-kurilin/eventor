package com.shop.api.registration;

public class ConfirmEmail {
    public final String email;
    public final String token;

    public ConfirmEmail(String email, String token) {
        this.email = email;
        this.token = token;
    }
}
