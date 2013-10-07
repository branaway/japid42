/**
 * 
 */
package cn.bran.play.routing;

import play.mvc.SimpleResult;

/**
 * @author bran
 * 
 */
public class WrapProducer extends SimpleResult {
	private String produces;
	private SimpleResult r;

	public WrapProducer(String produces, SimpleResult r) {
		this.produces = produces;
		this.r = r;
	}

//	public play.api.mvc.Result getWrappedResult() {
//		return r.getWrappedResult().as(produces);
//	}

	/* (non-Javadoc)
	 * @see play.mvc.SimpleResult#getWrappedSimpleResult()
	 */
	@Override
	public play.api.mvc.SimpleResult getWrappedSimpleResult() {
		return r.getWrappedSimpleResult().as(produces);
	}
}