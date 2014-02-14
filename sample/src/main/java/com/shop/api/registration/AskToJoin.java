package com.shop.api.registration;

public class AskToJoin {
    public String email;
    public String name;
    public String password;

    public AskToJoin(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }
}
