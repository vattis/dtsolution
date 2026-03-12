package com.example.dtsolution.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
    @NotBlank(message = "아이디를 입력해주세요") @Size(min = 3, max = 50, message = "아이디는 3~50자여야 합니다")
        String username,
    @NotBlank(message = "비밀번호를 입력해주세요") @Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다")
        String password,
    @NotBlank(message = "비밀번호 확인을 입력해주세요") String confirmPassword) {}
