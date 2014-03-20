package com.eventor.sample.tutorial;

public interface SecurityService {
    String generateRegistrationToken(String email);

    String protectPassword(String email, String password);

    boolean checkRegistrationToken(String email, String token);

    boolean isPasswordCorrect(String email, String passwordProtected, String password);
}
