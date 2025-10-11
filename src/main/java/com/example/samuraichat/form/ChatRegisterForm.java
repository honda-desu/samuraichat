package com.example.samuraichat.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRegisterForm {
	@NotBlank(message = "チャットグループ名を入力してください。")
	private String name;
}
