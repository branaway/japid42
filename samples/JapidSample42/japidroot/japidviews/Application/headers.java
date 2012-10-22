package japidviews.Application;
import java.util.*;
import java.io.*;
import cn.bran.japid.tags.Each;
import japidviews._layouts.*;
import play.mvc.Http.Context.Implicit;
import models.*;
import play.i18n.Lang;
import play.data.Form;
import play.data.Form.Field;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Http.Session;
import play.mvc.Http.Flash;
import play.data.validation.Validation;
import java.util.*;
import static cn.bran.japid.util.WebUtils.*;
import japidviews._tags.*;
import controllers.*;
//
// NOTE: This file was generated from: japidviews/Application/headers.html
// Change to this file will be lost next time the template file is compiled.
//
public class headers extends superheaders
{
	public static final String sourceTemplate = "japidviews/Application/headers.html";
	{
		putHeader("Date", "Tue, 23 Feb 2010 13:42:34 GMT");
		putHeader("Expires", "Tue, 23 Feb 2010 13:47:34 GMT");
		putHeader("Last-Modified", "Tue, 23 Feb 2010 13:40:01 GMT");
		putHeader("Content-Type", "text/html; charset=utf-8");
		putHeader("Server", "nginx/0.8.26");
		putHeader("Cache-Control", "max-age=600");
		setContentType("text/html; charset=utf-8");
	}

// - add implicit fields with Play

	final Request request = Implicit.request(); 
	final Response response = Implicit.response(); 
	final Session session = Implicit.session();
	final Flash flash = Implicit.flash();
	final Lang lang = Implicit.lang();
	final play.Play _play = new play.Play(); 

// - end of implicit fields with Play 


	public headers() {
		super(null);
	}
	public headers(StringBuilder out) {
		super(out);
	}
/* based on https://github.com/branaway/Japid/issues/12
 */
	public static final String[] argNames = new String[] {/* args of the template*/ };
	public static final String[] argTypes = new String[] {/* arg types of the template*/ };
	public static final Object[] argDefaults= new Object[] { };
	public static java.lang.reflect.Method renderMethod = getRenderMethod(japidviews.Application.headers.class);

	{
		setRenderMethod(renderMethod);
		setArgNames(argNames);
		setArgTypes(argTypes);
		setArgDefaults(argDefaults);
		setSourceTemplate(sourceTemplate);
	}
////// end of named args stuff

	public cn.bran.japid.template.RenderResult render() {
		long t = -1;
		try {super.layout();} catch (RuntimeException e) { super.handleException(e);} 
		return new cn.bran.japid.template.RenderResultPartial(getHeaders(), getOut(), t, actionRunners, sourceTemplate);
	}
	@Override protected void doLayout() {
		beginDoLayout(sourceTemplate);
//------
;// line 1
p("\n" + 
"<p>\n" + 
"\"setHeader\" is for adding a http response header to the response. One cannot use \"\" to surround the value part\n" + 
"</p>\n" + 
"\n" + 
"<p>\n" + 
"The header name and the value are separated by white spaces (space or tab)\n" + 
"</p>\n" + 
"\n" + 
"<p>\n" + 
"Notes: If a response includes both an Expires header and a max-age directive, the max-age directive overrides the Expires header, even if the Expires header is more restrictive.\n" + 
"</p>\n" + 
"\n" + 
"\n");// line 6
		
		endDoLayout(sourceTemplate);
	}

}