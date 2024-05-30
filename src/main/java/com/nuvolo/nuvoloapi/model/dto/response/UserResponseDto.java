package com.nuvolo.nuvoloapi.model.dto.response;

import com.nuvolo.nuvoloapi.model.entity.NuvoloUser;
import com.nuvolo.nuvoloapi.model.enums.RoleName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String token;

    private Boolean isAdmin;

    public static UserResponseDto mapAuthenticatedUserEntity(NuvoloUser user, String token) {
        return UserResponseDto.builder().
                id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .token(token)
                .isAdmin(user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleName.ADMIN)))
                .build();
    }

    public static UserResponseDto mapUserEntity(NuvoloUser user) {
        return UserResponseDto.builder().
                id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .isAdmin(user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleName.ADMIN)))
                .build();
    }
}
