package ru.yandex.practicum.filmorate.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ReleaseValidator implements ConstraintValidator<IsValidRelease, LocalDate> {

    @Override
    public boolean isValid(LocalDate dateRelease, ConstraintValidatorContext constraintValidatorContext) {
        return dateRelease.isAfter(LocalDate.of(1895,12,28));
    }
}
