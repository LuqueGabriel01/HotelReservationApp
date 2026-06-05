package com.hotelreservation.auth.validators.annotations;

import com.hotelreservation.auth.constants.ErrorConstants;
import com.hotelreservation.auth.validators.PasswordConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/** Custom Bean Validation annotation used to enforce password security requirements. */
@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default ErrorConstants.Message.DEFAULT_INVALID_PASSWORD;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
