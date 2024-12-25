package ru.practicum.controller.admin;

public record AdminUsersGetAllParams(
        Long[] ids,
        int from,
        int size
) {
}
