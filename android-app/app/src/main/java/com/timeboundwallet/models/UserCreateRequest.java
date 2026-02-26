package com.timeboundwallet.models;

public class UserCreateRequest {
    private String name;
    private String email;

    public UserCreateRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
