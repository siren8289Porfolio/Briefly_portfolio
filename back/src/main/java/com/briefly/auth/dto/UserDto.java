package com.briefly.auth.dto;

import com.briefly.auth.entity.User;

public class UserDto {
    private Long id;
    private String email;
    private String name;
    private String role;

    public static UserDto from(User user) {
        UserDto dto = new UserDto();
        dto.id = user.getId();
        dto.email = user.getEmail();
        dto.name = user.getName();
        dto.role = user.getRole().name();
        return dto;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
}
