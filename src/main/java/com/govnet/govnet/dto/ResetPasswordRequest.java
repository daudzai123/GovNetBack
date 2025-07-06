package com.govnet.govnet.dto;

public record ResetPasswordRequest(
        String newPassword,
        String confirmNewPassword,
        String otpCode
) {
}
