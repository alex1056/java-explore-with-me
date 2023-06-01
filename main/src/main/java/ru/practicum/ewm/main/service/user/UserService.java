package ru.practicum.ewm.main.service.user;


import ru.practicum.ewm.main.dto.user.NewUserRequestDto;
import ru.practicum.ewm.main.dto.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto saveUser(NewUserRequestDto newUserRequestDto);

    List<UserDto> getUsers(Integer from, Integer size, List<Long> ids);

    List<UserDto> getAllUsers(Integer from, Integer size);

    UserDto findUserById(Long id);

    void deleteUserById(Long id);


}
