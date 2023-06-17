package ru.practicum.ewm.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.dto.category.NewCategoryDto;
import ru.practicum.ewm.main.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.main.dto.compilation.UpdateCompilationDto;
import ru.practicum.ewm.main.dto.event.*;
import ru.practicum.ewm.main.dto.locationAdmin.LocationAdminDto;
import ru.practicum.ewm.main.dto.locationAdmin.NewLocationAdminDto;
import ru.practicum.ewm.main.dto.locationAdmin.UpdateLocationAdminRequest;
import ru.practicum.ewm.main.dto.user.NewUserRequestDto;
import ru.practicum.ewm.main.dto.user.UserDto;
import ru.practicum.ewm.main.model.LocationAdmin;
import ru.practicum.ewm.main.model.event.EventState;
import ru.practicum.ewm.main.service.category.CategoryService;
import ru.practicum.ewm.main.service.compilation.CompilationService;
import ru.practicum.ewm.main.service.event.EventService;
import ru.practicum.ewm.main.service.locationAdmin.LocationAdminService;
import ru.practicum.ewm.main.service.stat.StatService;
import ru.practicum.ewm.main.service.user.UserService;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
@Validated
public class AdminController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final CompilationService compilationService;
    private final EventService eventService;
    private final StatService statService;
    private final LocationAdminService locationAdminService;

    @GetMapping("/events")
    public List<EventFullDto> getAllEventsAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(name = "rangeStart", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(name = "rangeEnd", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size) {

        List<Long> usersChecked = (users != null && users.get(0) == 0) ? null : users;

        EventAdminSearchParams searchParams = new EventAdminSearchParams(
                usersChecked,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size
        );
        statService.saveHit("/admin/events");
        return eventService.findEventsByParamsAdmin(searchParams);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEventAdmin(@PathVariable Long eventId,
                                         @Validated @RequestBody UpdateEventAdminRequest eventToUpdate
    ) {
        statService.saveHit("/admin/events/" + eventId);
        EventFullDto event = eventService.updateEventAdmin(eventId, eventToUpdate);
        return event;
    }

    @PostMapping("/locations")
    public ResponseEntity<Object> saveNewLocation(@Validated @RequestBody NewLocationAdminDto newLocation) {
        statService.saveHit("/admin/locations");
        LocationAdminDto locationSaved = locationAdminService.saveLocationAdmin(newLocation);
        return new ResponseEntity<>(locationSaved, HttpStatus.CREATED);
    }

    @PatchMapping("/locations/{locationId}")
    public LocationAdminDto updateLocationAdmin(@PathVariable Long locationId,
                                                @Validated @RequestBody UpdateLocationAdminRequest updateLocation
    ) {
        statService.saveHit("/admin/locations/" + locationId);
        return locationAdminService.updateLocationAdmin(locationId, updateLocation);
    }

    @GetMapping("/locations")
    public List<LocationAdminDto> getLocationAdmin() {
        statService.saveHit("/admin/locations");
        return locationAdminService.getLocationAdmin();
    }

    @GetMapping("/locations/{locationId}/events")
    public List<EventFullDto> getAllEventsInLocation(
            @PathVariable Long locationId,
            @RequestParam(defaultValue = "10") @Min(0) Double radius,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        statService.saveHit("/admin/locations/" + locationId + "/events");
        return locationAdminService.getAllEventsInLocation(locationId, radius, from, size);
    }


    @PostMapping("/compilations")
    public ResponseEntity<Object> saveNewCompilation(@Validated @RequestBody NewCompilationDto newCompilationDto) {
        statService.saveHit("/admin/compilations");
        CompilationDto compilation = compilationService.saveCompilation(newCompilationDto);
        return new ResponseEntity<>(compilation, HttpStatus.CREATED);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(
            @PathVariable Long compId,
            @Validated @RequestBody UpdateCompilationDto updateCompilationDto) {
        statService.saveHit("/admin/compilations" + compId);
        return compilationService.updateCompilation(compId, updateCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    public ResponseEntity<Object> deleteCompilation(@PathVariable Long compId) {
        statService.saveHit("/admin/compilations" + compId);
        compilationService.deleteCompilation(compId);
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
    }


    @PostMapping("/users")
    public ResponseEntity<Object> saveNewUser(@Validated @RequestBody NewUserRequestDto newUser) {
        statService.saveHit("/admin/users");
        UserDto savedUser = userService.saveUser(newUser);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @RequestParam(value = "ids", required = false) List<String> ids
    ) {
        statService.saveHit("/admin/users");
        if (ids == null) {
            return userService.getAllUsers(from, size);
        }

        List<Long> idsLong = new ArrayList<>();
        ids.forEach(id -> idsLong.add(Long.parseLong(id)));
        return userService.getUsers(from, size, idsLong);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        statService.saveHit("/admin/users/" + userId);
        userService.deleteUserById(userId);
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
    }

    @PostMapping("/categories")
    public ResponseEntity<Object> saveNewCategory(@Validated @RequestBody NewCategoryDto newCategory) {
        statService.saveHit("/admin/categories");
        CategoryDto category = categoryService.saveCategory(newCategory);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @DeleteMapping("/categories/{catId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable Long catId) {
        statService.saveHit("/admin/categories/" + catId);
        categoryService.deleteCategory(catId);
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@Validated @RequestBody NewCategoryDto newCategory, @PathVariable Long catId) {
        statService.saveHit("/admin/categories/" + catId);
        return categoryService.updateCategory(catId, newCategory);
    }
}
