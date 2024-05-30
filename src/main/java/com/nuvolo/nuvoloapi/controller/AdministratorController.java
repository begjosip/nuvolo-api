package com.nuvolo.nuvoloapi.controller;

import com.nuvolo.nuvoloapi.model.dto.response.UserResponseDto;
import com.nuvolo.nuvoloapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdministratorController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers() {
        log.info(" > > > GET /api/v1/admin/users");
        List<UserResponseDto> users = userService.getAllUsers();
        log.info(" < < < GET /api/v1/admin/users");
        return ResponseEntity.ok(users);
    }

}
