package com.eventor.sample.tutorial;

import java.util.Date;

public class PasswordChanged {
    public final String personEmail;
    public final String newProtectedPassword;
    public final Date changeDate;

    public PasswordChanged(String personEmail, String newProtectedPassword, Date changeDate) {
        this.personEmail = personEmail;
        this.newProtectedPassword = newProtectedPassword;
        this.changeDate = changeDate;
    }
}
