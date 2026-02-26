package com.timeboundwallet.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    private String name;

    @Email(message = "Email must be valid")
    private String email;
}
