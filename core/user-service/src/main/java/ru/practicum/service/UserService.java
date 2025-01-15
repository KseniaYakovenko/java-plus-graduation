package ru.practicum.service;

import ru.practicum.controller.AdminUsersGetAllParams;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserDto;

import java.util.List;
import java.util.Map;

public interface UserService {

    UserDto add(UserCreateDto userCreateDto);

    List<UserDto> getAll(AdminUsersGetAllParams adminUsersGetAllParams);

    void delete(long userId);

    void checkExistence(long userId);

    UserDto getById(long userId);

    Map<Long, UserDto> getByIds(List<Long> userIds);

}