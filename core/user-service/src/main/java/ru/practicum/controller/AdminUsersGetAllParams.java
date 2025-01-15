package ru.practicum.controller;

import java.util.List;

public record AdminUsersGetAllParams(
        List<Long> ids,
        int from,
        int size
) {
}
