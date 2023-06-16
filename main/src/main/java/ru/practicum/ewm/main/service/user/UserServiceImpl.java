package ru.practicum.ewm.main.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.user.NewUserRequestDto;
import ru.practicum.ewm.main.dto.user.UserDto;
import ru.practicum.ewm.main.dto.user.UserMapper;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.helper.Helpers;
import ru.practicum.ewm.main.model.User;
import ru.practicum.ewm.main.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;


    @Transactional
    @Override
    public UserDto saveUser(NewUserRequestDto newUserRequestDto) {
        Optional<User> namedUser = repository.checkUserName(newUserRequestDto.getName());
        if (namedUser.isPresent()) {
            throw new ConflictException("имя уже занято");
        }
        User user = repository.save(UserMapper.toNewUser(newUserRequestDto));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public List<UserDto> getUsers(Integer from, Integer size, List<Long> ids) {
        Pageable page = PageRequest.of(Helpers.getPageNumber(from, size), size);
        List<User> users = repository.findUserByIds(ids, page).getContent();
        return UserMapper.toUserDto(users);
    }

    @Transactional
    @Override
    public List<UserDto> getAllUsers(Integer from, Integer size) {
        Pageable page = PageRequest.of(Helpers.getPageNumber(from, size), size);
        List<User> users = repository.findAll(page).getContent();
        return UserMapper.toUserDto(users);
    }

    @Transactional
    @Override
    public UserDto findUserById(Long id) {
        Optional<User> userOpt = repository.findUserById(id);
        userOpt.orElseThrow(() -> new NotFoundException("пользователь с id=" + id));
        return UserMapper.toUserDto(userOpt.get());
    }

    @Transactional
    @Override
    public void deleteUserById(Long id) {
        Optional<User> user = repository.findUserById(id);
        if (user.isPresent()) {
            repository.delete(user.get());
        }
    }
}
