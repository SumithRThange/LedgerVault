package com.timeboundwallet.models;

public class UserUpdateRequest {
    private String name;
    private String email;

    public UserUpdateRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
