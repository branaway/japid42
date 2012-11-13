package japidviews._tags;
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
// NOTE: This file was generated from: japidviews/_tags/Tag2.html
// Change to this file will be lost next time the template file is compiled.
//
public class Tag2 extends cn.bran.play.JapidTemplateBase
{
	public static final String sourceTemplate = "japidviews/_tags/Tag2.html";
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


	public Tag2() {
		super(null);
	}
	public Tag2(StringBuilder out) {
		super(out);
	}
/* based on https://github.com/branaway/Japid/issues/12
 */
	public static final String[] argNames = new String[] {/* args of the template*/"msg", "m2", "age",  };
	public static final String[] argTypes = new String[] {/* arg types of the template*/"String", "String", "Integer",  };
	public static final Object[] argDefaults= new Object[] {null,new String("m2message"),null, };
	public static java.lang.reflect.Method renderMethod = getRenderMethod(japidviews._tags.Tag2.class);

	{
		setRenderMethod(renderMethod);
		setArgNames(argNames);
		setArgTypes(argTypes);
		setArgDefaults(argDefaults);
		setSourceTemplate(sourceTemplate);
	}
////// end of named args stuff

	private String msg; // line 1
	private String m2; // line 1
	private Integer age; // line 1
	public cn.bran.japid.template.RenderResult render(String msg,String m2,Integer age) {
		this.msg = msg;
		this.m2 = m2;
		this.age = age;
		long __t = -1;
		try {super.layout();} catch (RuntimeException e) { super.handleException(e);} // line 1
		return new cn.bran.japid.template.RenderResultPartial(getHeaders(), getOut(), __t, actionRunners, sourceTemplate);
	}

	public static cn.bran.japid.template.RenderResult apply(String msg,String m2,Integer age) {
		return new Tag2().render(msg, m2, age);
	}

	@Override protected void doLayout() {
		beginDoLayout(sourceTemplate);
//------
;// line 1
		p("<span>");// line 1
		p(msg);// line 2
		p("</span>\n" + 
"<span>");// line 2
		p(m2);// line 3
		p("</span>\n" + 
"<span>");// line 3
		p(age);// line 4
		p("</span>\n" + 
"\n");// line 4
		
		endDoLayout(sourceTemplate);
	}

}