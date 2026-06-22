package com.hotelreservation.auth.validators;

import com.hotelreservation.auth.validators.annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

/** Custom validator implementation for the {@link ValidPassword} annotation. */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

  /**
   * Internal Passay validator configured with explicit complexity rules.
   *
   * <ul>
   *   <li>Length between 8 and 128 characters.
   *   <li>At least one uppercase character.
   *   <li>At least one lowercase character.
   *   <li>At least one numeric digit.
   *   <li>At least one special character.
   *   <li>No whitespace allowed.
   * </ul>
   */
  private final PasswordValidator passwordValidator =
      new PasswordValidator(
          List.of(
              new LengthRule(8, 128),
              new CharacterRule(EnglishCharacterData.UpperCase, 1),
              new CharacterRule(EnglishCharacterData.LowerCase, 1),
              new CharacterRule(EnglishCharacterData.Digit, 1),
              new CharacterRule(EnglishCharacterData.Special, 1),
              new WhitespaceRule()));

  /**
   * Validates the given password against the configured Passay complexity rules.
   *
   * <p>If the password violates any rule, the default constraint message is disabled, and a
   * detailed, comma-separated list of the specific rule failures is generated and attached to the
   * validation context instead.
   *
   * @param password the plaintext password string to validate
   * @param context the context in which the constraint is evaluated
   * @return true if the password is valid or null (delegated to @NotNull); false otherwise
   */
  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    if (password == null) {
      return true;
    }

    RuleResult result = passwordValidator.validate(new PasswordData(password));
    if (result.isValid()) {
      return true;
    }

    context.disableDefaultConstraintViolation();
    context
        .buildConstraintViolationWithTemplate(
            String.join(", ", passwordValidator.getMessages(result)))
        .addConstraintViolation();

    return false;
  }
}
