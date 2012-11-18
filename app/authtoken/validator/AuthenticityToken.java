package authtoken.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * This class defined a new validation Annotation
 * 
 * @author orefalo
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = AuthenticityTokenValidator.class)
@play.data.Form.Display(name = "constraint.authenticitytoken")
public @interface AuthenticityToken {

	public static final String AUTH_TOKEN = "atoken";

	String message() default AuthenticityTokenValidator.message;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
