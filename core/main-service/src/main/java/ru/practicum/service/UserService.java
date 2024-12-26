package ru.practicum.service;

import ru.practicum.controller.admin.AdminUsersGetAllParams;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto add(UserCreateDto userCreateDto);

    List<UserDto> getAll(AdminUsersGetAllParams adminUsersGetAllParams);

    void delete(long userId);

}
