package cn.bran.play;

import play.mvc.Http.Context.Implicit;
import play.mvc.Http.Flash;
import cn.bran.japid.util.FlashScope;

/**
 * wrap the access to the Play! Flash scope
 * 
 * @author bran
 *
 */
public class FlashWrapper implements FlashScope{

	@Override
	public Object get(String key) {
		return f().get(key);
	}

	@Override
	public Object error() {
		return f().get(FlashScope.ERROR); // match the literal used in Scope
	}

	@Override
	public Object success() {
		return f().get(FlashScope.SUCCESS); // match the literal 
	}
	
	/**
	 * @return
	 */
	private Flash f() {
		return Implicit.flash();
	}
	
	@Override
	public boolean hasError() {
		return this.error() != null;
	}

	@Override
	public boolean hasSuccess() {
		return this.success() != null;
	}

	public void putSuccess(String message) {
		f().put(FlashScope.SUCCESS, message);
	}

	@Override
	public boolean contains(String key) {
		return f().containsKey(key);
	}
}
