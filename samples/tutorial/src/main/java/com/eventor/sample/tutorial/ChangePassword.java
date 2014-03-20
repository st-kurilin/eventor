package com.eventor.sample.tutorial;

public class ChangePassword {
    public final String personEmail;
    public final String oldPassword;
    public final String newPassword;

    public ChangePassword(String personEmail, String oldPassword, String newPassword) {
        this.personEmail = personEmail;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
