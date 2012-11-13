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
import japidviews.*;
import play.mvc.Http.Response;
import play.mvc.Http.Session;
import play.mvc.Http.Flash;
import play.data.validation.Validation;
import java.util.*;
import static cn.bran.japid.util.WebUtils.*;
import japidviews._tags.*;
import controllers.*;
//
// NOTE: This file was generated from: japidviews/Application/search.html
// Change to this file will be lost next time the template file is compiled.
//
public class search extends cn.bran.play.JapidTemplateBase
{
	public static final String sourceTemplate = "japidviews/Application/search.html";
	{
		putHeader("Content-Type", "text/html; charset=utf-8");
		setContentType("text/html; charset=utf-8");
	}

// - add implicit fields with Play
boolean hasHttpContext = play.mvc.Http.Context.current.get() != null ? true : false;

	final Request request = hasHttpContext? Implicit.request() : null;
	final Response response = hasHttpContext ? Implicit.response() : null;
	final Session session = hasHttpContext ? Implicit.session() : null;
	final Flash flash = hasHttpContext ? Implicit.flash() : null;
	final Lang lang = hasHttpContext ? Implicit.lang() : null;
	final play.Play _play = new play.Play(); 

// - end of implicit fields with Play 


	public search() {
		super(null);
	}
	public search(StringBuilder out) {
		super(out);
	}
/* based on https://github.com/branaway/Japid/issues/12
 */
	public static final String[] argNames = new String[] {/* args of the template*/"sp",  };
	public static final String[] argTypes = new String[] {/* arg types of the template*/"SearchParams",  };
	public static final Object[] argDefaults= new Object[] {null, };
	public static java.lang.reflect.Method renderMethod = getRenderMethod(japidviews.Application.search.class);

	{
		setRenderMethod(renderMethod);
		setArgNames(argNames);
		setArgTypes(argTypes);
		setArgDefaults(argDefaults);
		setSourceTemplate(sourceTemplate);
	}
////// end of named args stuff

	private SearchParams sp; // line 1
	public cn.bran.japid.template.RenderResult render(SearchParams sp) {
		this.sp = sp;
		long __t = -1;
		try {super.layout();} catch (RuntimeException e) { super.handleException(e);} // line 1
		return new cn.bran.japid.template.RenderResultPartial(getHeaders(), getOut(), __t, actionRunners, sourceTemplate);
	}

	public static cn.bran.japid.template.RenderResult apply(SearchParams sp) {
		return new search().render(sp);
	}

	@Override protected void doLayout() {
		beginDoLayout(sourceTemplate);
//------
;// line 1
		p("\n" + 
"\n");// line 1
		String nomode = "no mode";// line 3
		p("keys: ");// line 3
		try { Object o = sp.keywords ; if (o.toString().length() ==0) { p("没有 keywords"); } else { p(o); } } catch (NullPointerException npe) { p("没有 keywords"); }// line 5
		p(", mode: ");// line 5
		try { Object o = sp.mode ; if (o.toString().length() ==0) { p(nomode); } else { p(o); } } catch (NullPointerException npe) { p(nomode); }// line 5
		p("\n" + 
"true/false: ");// line 5
		p(true?"class=\"someclass\"":"");// line 7
		;// line 7
		
		endDoLayout(sourceTemplate);
	}

}