package ru.practicum.dto.event.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventDateValidator.class)
public @interface FutureAfterTwoHours {

    String message() default "Время события не может быть раньше, чем через 2 часа от текущего момента";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
