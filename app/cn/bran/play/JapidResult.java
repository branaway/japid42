package cn.bran.play;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import play.mvc.Content;
import play.mvc.Results;
import scala.collection.Seq;
import cn.bran.japid.template.RenderResult;

/**
 * 
 * The return type of the renderJapid(...) method in JapidController.
 * 
 * An object of this class is a valid value for a controller action to return.  Doing this allows
 * headers set in the Japid content to be carried over to the response. 
 * 
 * If the headers are not a concern, the JapidResult is also a valid argument type for ok() and other 
 * helper methods in a controller. 
 * 
 * <pre>
 * 	public static Result foo() {
 * 		return renderJapid(); // which return an instance of this class.
 * }
 * 
 * 	public static Result bar() {
 * 		return ok(renderJapid()); // which allows more response manipulations. 
 * }
 * 
 * </pre>
 * 
 * @author bran
 * 
 */
public class JapidResult extends Results.Status implements Externalizable, Content {
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CACHE_CONTROL = "Cache-Control";

	private RenderResult renderResult;
	private Map<String, String> headers = new HashMap<String, String>();
//	private boolean eager = false;

//	String resultContent = "";

	// public JapidResult(String contentType) {
	// super();
	// this.contentType = contentType;
	// }
	//
	// public JapidResult(String contentType2, String string) {
	// this.contentType = contentType2;
	// this.content = string;
	// }

	public JapidResult(RenderResult r) {
		super(play.core.j.JavaResults.Status(200), r.getContent().toString(), play.api.mvc.Codec.javaSupported("utf-8") );
		this.renderResult = r;
		this.setHeaders(r.getHeaders());
    	Seq<scala.Tuple2<String, String>> seq = scala.collection.JavaConversions.mapAsScalaMap(renderResult.getHeaders()).toSeq();
		super.getWrappedSimpleResult().withHeaders(seq);
	}

	public JapidResult() {
		super(play.core.j.JavaResults.Status(200));
	}


//	/**
//	 * extract content now and once. Eager evaluation of RenderResult
//	 */
//	public JapidResult eval() {
//		this.eager = true;
//		this.resultContent = extractContent();
//		return this;
//	}

	/**
	 * @param r
	 */
	public String extractContent() {
		String content = "";
		StringBuilder sb = renderResult.getContent();
		if (sb != null)
			content = sb.toString();
		return content;
	}

	public RenderResult getRenderResult() {
		return renderResult;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(renderResult);
		out.writeObject(getHeaders());
//		out.writeBoolean(eager);
//		out.writeUTF(resultContent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		renderResult = (RenderResult) in.readObject();
		setHeaders((Map<String, String>) in.readObject());
	}

	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * @param headers the headers to set
	 */
	private void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	@Override
	public String toString() {
		return extractContent();
	}

	/* (non-Javadoc)
	 * @see play.api.mvc.Content#body()
	 */
	@Override
	public String body() {
		return renderResult == null? null : renderResult.getText();
	}

	/* (non-Javadoc)
	 * @see play.api.mvc.Content#contentType()
	 */
	@Override
	public String contentType() {
		return renderResult == null? null : renderResult.getContentType();
	}

//	/* (non-Javadoc)
//	 * @see play.mvc.SimpleResult#getWrappedSimpleResult()
//	 */
//	@Override
//	public SimpleResult getWrappedSimpleResult() {
//		String content = extractContent();
//		Results.Status st = Results.ok(content);
////		
////    	play.api.mvc.Results.Status sta = play.core.j.JavaResults.Status(200);
////    	Seq<Tuple2<String, String>> seq = JavaConversions.mapAsScalaMap(renderResult.getHeaders()).toSeq();
////    	st.withHeaders(seq);
////		status = new play.mvc.Results.Status(sta, content,  play.api.mvc.Codec.javaSupported("utf-8"));
////	}
//    return st;
//    }

//	/* (non-Javadoc)
//	 * @see play.mvc.Result#getWrappedResult()
//	 */
//	@Override
//	public Future<SimpleResult> getWrappedResult() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
