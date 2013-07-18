/**
 * 
 */
package cn.bran.play.routing;

import play.mvc.Result;

/**
 * @author bran
 * 
 */
public class WrapProducer implements Result {
	private String produces;
	private play.mvc.Result r;

	public WrapProducer(String produces, Result r) {
		this.produces = produces;
		this.r = r;
	}

	public play.api.mvc.Result getWrappedResult() {
		return r.getWrappedResult().as(produces);
	}
}