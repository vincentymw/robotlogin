package com.example.robotlogin.beans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterDto {
    @NotEmpty
    @Size(max=10, message = "最长不能超过10位字符")
    private String username;

    @NotEmpty
    @Size(min = 4, max = 16, message = "4-16位字符")
    private String password;
}
