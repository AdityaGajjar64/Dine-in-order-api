package com.example.dine_in_order_api.dto.request;

import com.example.dine_in_order_api.dto.constraints.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank(message = "Enter old Password ")
    private String oldPassword;

    @Password
    private String newPassword;

}
