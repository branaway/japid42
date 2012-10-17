package japidviews.Application;
import java.util.*;
import java.io.*;
import cn.bran.japid.tags.Each;
import japidviews._layouts.*;
import play.mvc.Http.Context.Implicit;
import models.*;
import play.i18n.Lang;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Http.Session;
import play.mvc.Http.Flash;
import play.data.validation.Validation;
import java.util.*;
import static cn.bran.japid.util.WebUtils.*;
import japidviews._tags.*;
import controllers.*;
import static cn.bran.japid.util.StringUtils.*;
//
// NOTE: This file was generated from: japidviews/Application/reverseUrl.html
// Change to this file will be lost next time the template file is compiled.
//
public class reverseUrl extends cn.bran.play.JapidTemplateBase
{
	public static final String sourceTemplate = "japidviews/Application/reverseUrl.html";
	{
		putHeader("Content-Type", "text/html; charset=utf-8");
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


	public reverseUrl() {
		super(null);
	}
	public reverseUrl(StringBuilder out) {
		super(out);
	}
/* based on https://github.com/branaway/Japid/issues/12
 */
	public static final String[] argNames = new String[] {/* args of the template*/ };
	public static final String[] argTypes = new String[] {/* arg types of the template*/ };
	public static final Object[] argDefaults= new Object[] { };
	public static java.lang.reflect.Method renderMethod = getRenderMethod(japidviews.Application.reverseUrl.class);

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
p("OK, it works!");// line 1
		
		endDoLayout(sourceTemplate);
	}

}