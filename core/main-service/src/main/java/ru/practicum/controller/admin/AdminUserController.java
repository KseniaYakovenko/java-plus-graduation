package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@RequestBody @Valid UserCreateDto userCreateDto) {
        log.info("==> POST. Adding new User: {}", userCreateDto);
        UserDto receivedUserDto = userService.add(userCreateDto);
        log.info("<== POST. User added: {}", receivedUserDto);
        return receivedUserDto;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("userId") int userId) {
        log.info("==> DELETE. Deleting User: {}", userId);
        userService.delete(userId);
        log.info("<== DELETE. User deleted: {}", userId);
    }

    @GetMapping
    public List<UserDto> getAll(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("==> GET. Getting all users by ids: {}, from: {}, size {}", ids, from, size);
        AdminUsersGetAllParams adminUsersGetAllParams = new AdminUsersGetAllParams(ids, from, size);
        List<UserDto> receivedUserDtoList = userService.getAll(adminUsersGetAllParams);
        log.info("<== GET. User list with size: {}", receivedUserDtoList.size());
        return receivedUserDtoList;
    }
}
