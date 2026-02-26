package com.timeboundwallet.models;

public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Boolean active;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getActive() {
        return active;
    }
}
