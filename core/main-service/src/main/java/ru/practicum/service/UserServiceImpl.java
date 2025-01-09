package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.controller.admin.AdminUsersGetAllParams;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.entity.User;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.repository.UserRepository;

import java.util.List;

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

    @Override
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
}
