package com.ftn.mobile.data.remote.dto.auth;

public class ChangedPasswordDTO {
    String password;
    String repeatedPassword;
    String email;

    public ChangedPasswordDTO(String password, String email, String repeatedPassword) {
        this.password = password;
        this.email = email;
        this.repeatedPassword = repeatedPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatedPassword() {
        return repeatedPassword;
    }

    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
