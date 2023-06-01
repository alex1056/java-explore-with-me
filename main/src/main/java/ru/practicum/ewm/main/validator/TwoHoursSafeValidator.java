package ru.practicum.ewm.main.validator;

import ru.practicum.ewm.main.annotations.TwoHoursSafe;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class TwoHoursSafeValidator implements ConstraintValidator<TwoHoursSafe, LocalDateTime> {

    public boolean isValid(LocalDateTime dateTime, ConstraintValidatorContext context) {
        if (dateTime == null) return true;
        return dateTime.isAfter(LocalDateTime.now().plusHours(2));
    }
}
