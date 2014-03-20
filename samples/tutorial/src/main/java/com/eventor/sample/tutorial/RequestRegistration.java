package com.eventor.sample.tutorial;

public class RequestRegistration {
    public final String email;
    public final String name;
    public final String password;

    public RequestRegistration(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }
}
