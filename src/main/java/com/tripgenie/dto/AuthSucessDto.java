package com.tripgenie.dto;

import com.tripgenie.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthSucessDto {
    User user;
    String token;
    String message;
}
