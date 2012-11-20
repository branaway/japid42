/**
 * 
 */
package cn.bran.play;

import java.util.Map;

import cn.bran.japid.template.AuthenticityCheck;
import cn.bran.japid.util.StringUtils;

import play.api.libs.Crypto;
import play.data.Form;
import play.mvc.Http.Session;

/**
 * add authenticity token check in form binding
 * 
 * @author bran
 * 
 */
public class AuthenticForm<T> extends Form<T> {
	private Class<T> formType;

	public AuthenticForm(Class<T> clazz) {
		super(clazz);
		this.formType = clazz;
	}

	/**
	 * @param name
	 * @param clazz
	 */
	public AuthenticForm(String name, Class<T> clazz) {
		super(name, clazz);
		this.formType = clazz;
	}

	private void checkAuthenticity(Map<String, String> data) {
		AuthenticityCheck anno = this.formType.getAnnotation(AuthenticityCheck.class);
		Session session = play.mvc.Http.Context.current().session();
		String atoken = session.get(AuthenticityCheck.AUTH_TOKEN);
		session.remove(AuthenticityCheck.AUTH_TOKEN);
		String uuid = data.get(AuthenticityCheck.AUTH_TOKEN);

		if (anno != null) {
			if (StringUtils.isEmpty(uuid) || StringUtils.isEmpty(atoken)) {
				alarm();
			} else {
				match(atoken, uuid);
			}
		}
	}

	private void match(String atoken, String uuid) {
		String sign = Crypto.sign(uuid);
		if (!sign.equals(atoken)) {
			alarm();
		}
	}

	private void alarm() {
		throw new SecurityException("The form submission does not have a proper authenticity token");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see play.data.Form#bind(java.util.Map, java.lang.String[])
	 */
	@Override
	public Form<T> bind(Map<String, String> data, String... allowedFields) {
		checkAuthenticity(data);
		return super.bind(data, allowedFields);
	}
}
