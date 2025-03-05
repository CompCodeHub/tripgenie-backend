package com.tripgenie.controller;

import com.tripgenie.dto.AuthSucessDto;
import com.tripgenie.dto.LoginRequestDto;
import com.tripgenie.model.User;
import com.tripgenie.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {

        Map<String, Object> registered = userService.register(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registered);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        AuthSucessDto login = userService.login(loginRequestDto.getUserNameOrEmail(), loginRequestDto.getPassword());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(login);
    }

    @GetMapping("/expired-token")
    public ResponseEntity<String> expiredToken() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("{\"error\": \"Token has expired\"}");
    }

    @GetMapping("/secured")
    public ResponseEntity<String> secured() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Hello World!");
    }


}
