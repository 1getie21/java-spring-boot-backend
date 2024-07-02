package com.insa.TeamOpsSystem.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordRequest {
	@NotBlank
	private String oldPassword;
	@NotBlank
	private String password;
}