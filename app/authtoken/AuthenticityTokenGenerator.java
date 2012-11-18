package authtoken;

import authtoken.validator.AuthenticityToken;
import play.api.libs.Crypto;
import play.mvc.Http.Context;
/**
 * 
 * @author https://github.com/orefalo/play2-authenticitytoken
 *
 */
public class AuthenticityTokenGenerator {

	/**
	 * Generates a UUID and stores its signature in the session, used by the authenticity token
	 * @return
	 */
	public static String generate() {
		String uuid=java.util.UUID.randomUUID().toString();
		String sign=Crypto.sign(uuid);
		Context.current().session().put(AuthenticityToken.AUTH_TOKEN, sign);
		return uuid;
	}
	
}
