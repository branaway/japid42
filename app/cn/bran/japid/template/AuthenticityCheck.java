package cn.bran.japid.template;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This class defined a new validation Annotation to be used on form model 
 * to indicate that the form requires authenticity check to combat potential
 * cross site forge attack 
 * 
 * @author bing ran
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface AuthenticityCheck {
	public static final String AUTH_TOKEN = "_atoken";
}
