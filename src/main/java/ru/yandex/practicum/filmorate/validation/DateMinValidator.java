package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateMinValidator implements ConstraintValidator<DateMin, LocalDate> {

    private LocalDate minValue;

    @Override
    public void initialize(DateMin constraintAnnotation) {
        this.minValue = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(minValue);
    }
}