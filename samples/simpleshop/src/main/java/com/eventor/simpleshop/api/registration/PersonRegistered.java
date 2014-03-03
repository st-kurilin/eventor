package com.eventor.simpleshop.api.registration;

public class PersonRegistered {
    public String email;
    public String name;
    public String password;

    public PersonRegistered(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }
}
