package cn.bran.play;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import play.mvc.Result;
import play.api.mvc.SimpleResult;

import play.mvc.Content;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Results.Status;
import scala.Tuple2;
import scala.collection.JavaConversions;
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
public class JapidResult implements Result,  Externalizable, Content {
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CACHE_CONTROL = "Cache-Control";

	private RenderResult renderResult;
	private Map<String, String> headers = new HashMap<String, String>();
	private boolean eager = false;

	String resultContent = "";

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
		this.renderResult = r;
		this.setHeaders(r.getHeaders());
       
	}

	public JapidResult() {
	}


	/**
	 * extract content now and once. Eager evaluation of RenderResult
	 */
	public JapidResult eval() {
		this.eager = true;
		this.resultContent = extractContent();
		return this;
	}

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
		out.writeBoolean(eager);
		out.writeUTF(resultContent);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		renderResult = (RenderResult) in.readObject();
		setHeaders((Map<String, String>) in.readObject());
		eager = in.readBoolean();
		resultContent = in.readUTF();
	}

    final private play.api.mvc.Result wrappedResult = null;


    public play.api.mvc.Result getWrappedResult() {
		String content = resultContent;
		if (!eager)
			// late evaluation
			content = extractContent();
    	play.api.mvc.Results.Status sta = play.core.j.JavaResults.Status(200);
    	Seq<Tuple2<String, String>> seq = JavaConversions.mapAsScalaMap(renderResult.getHeaders()).toSeq();
    	sta.withHeaders(seq);
		Status re = new play.mvc.Results.Status(sta, content,  play.api.mvc.Codec.javaSupported("utf-8"));
        return re.getWrappedResult();
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
	public String body() {
		return this.resultContent;
	}

	@Override
	public String contentType() {
		return headers.get("Content-Type");
	}

	@Override
	public String toString() {
		return resultContent;
	}
}
