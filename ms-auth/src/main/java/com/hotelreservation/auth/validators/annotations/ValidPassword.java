package com.hotelreservation.auth.validators.annotations;

import com.hotelreservation.auth.constants.ErrorConstants;
import com.hotelreservation.auth.validators.PasswordConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Custom Bean Validation annotation used to enforce password security requirements. */
@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
  /**
   * Returns the default error message template.
   *
   * @return the error message string.
   */
  String message() default ErrorConstants.Message.DEFAULT_INVALID_PASSWORD;

  /**
   * Allows the specification of validation groups.
   *
   * @return the validation groups.
   */
  Class<?>[] groups() default {};

  /**
   * Can be used by clients of the Jakarta Bean Validation API to assign custom payload objects.
   *
   * @return the payload classes.
   */
  Class<? extends Payload>[] payload() default {};
}
