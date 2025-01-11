package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.controller.AdminUsersGetAllParams;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.entity.User;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto add(UserCreateDto userCreateDto) {
        User user = userMapper.userCreateDtoToUser(userCreateDto);
        return userMapper.userToUserDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAll(AdminUsersGetAllParams adminUsersGetAllParams) {
        PageRequest pageRequest = PageRequest.of(
                adminUsersGetAllParams.from(), adminUsersGetAllParams.size());

        List<User> userSearchList = adminUsersGetAllParams.ids() == null
                ? userRepository.findAll(pageRequest).stream().toList()
                : userRepository.findAllByIdIn(adminUsersGetAllParams.ids(), pageRequest);

        return userSearchList.stream()
                .map(userMapper::userToUserDto).toList();
    }

    @Override
    @Transactional
    public void delete(long userId) {
        try {
            userRepository.findById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Required user with id " + userId + " was not found.");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public void checkExistence(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    @Override
    public UserDto getById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        return userMapper.userToUserDto(user);
    }

    @Override
    public Map<Long, UserDto> getByIds(List<Long> userIds) {
        List<User> userList = userRepository.findAllById(userIds);
        Map<Long, UserDto> usersMap = new HashMap<>();
        for (User user : userList) {
            usersMap.put(user.getId(), userMapper.userToUserDto(user));
        }
        return usersMap;
    }


}