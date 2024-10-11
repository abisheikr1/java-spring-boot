package com.billing_software.billing_software.payloads.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenReq {
    @NotBlank(message = "token can't be null/blank")
    public String token;
}
