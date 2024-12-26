package ru.practicum.controller.admin;

import java.util.List;

public record AdminUsersGetAllParams(
        List<Long> ids,
        int from,
        int size
) {
}
