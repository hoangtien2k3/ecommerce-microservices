package com.hoangtien2k3.proxyclient.business.user.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hoangtien2k3.proxyclient.constant.AppConstant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VerificationTokenDto {

    private Integer verificationTokenId;
    private String token;

    @JsonFormat(pattern = AppConstant.LOCAL_DATE_FORMAT, shape = Shape.STRING)
    @DateTimeFormat(pattern = AppConstant.LOCAL_DATE_FORMAT)
    private LocalDate expireDate;

    @JsonProperty("credential")
    @JsonInclude(value = Include.NON_NULL)
    private CredentialDto credentialDto;

}