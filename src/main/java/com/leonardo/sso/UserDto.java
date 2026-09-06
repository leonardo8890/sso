package com.leonardo.sso;

public record UserDto(String login, String password, Role role) {
}
