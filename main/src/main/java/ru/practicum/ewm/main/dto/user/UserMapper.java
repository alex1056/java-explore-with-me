package ru.practicum.ewm.main.dto.user;

import ru.practicum.ewm.main.model.User;

import java.util.ArrayList;
import java.util.List;


public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static UserShortDto toUserShorDto(User user) {
        if (user == null) return null;
        return new UserShortDto(
                user.getId(), user.getName());
    }

    public static List<UserDto> toUserDto(Iterable<User> users) {
        List<UserDto> result = new ArrayList<>();
        for (User user : users) {
            result.add(toUserDto(user));
        }
        return result;
    }

    public static User toUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getEmail(), userDto.getName());
    }

    public static User toNewUser(NewUserRequestDto newUserRequestDto) {
        User user = new User();
        user.setName(newUserRequestDto.getName());
        user.setEmail(newUserRequestDto.getEmail());
        return user;
    }
}
