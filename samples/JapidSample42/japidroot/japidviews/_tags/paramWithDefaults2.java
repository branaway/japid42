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
// NOTE: This file was generated from: japidviews/_tags/paramWithDefaults2.html
// Change to this file will be lost next time the template file is compiled.
//
public class paramWithDefaults2 extends cn.bran.play.JapidTemplateBase
{
	public static final String sourceTemplate = "japidviews/_tags/paramWithDefaults2.html";
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


	public paramWithDefaults2() {
		super(null);
	}
	public paramWithDefaults2(StringBuilder out) {
		super(out);
	}
/* based on https://github.com/branaway/Japid/issues/12
 */
	public static final String[] argNames = new String[] {/* args of the template*/"name", "url", "type", "data", "reRender", "dataType", "beforeSend", "success", "jsData", "cache", "event",  };
	public static final String[] argTypes = new String[] {/* arg types of the template*/"String", "String", "String", "String", "String", "String", "String", "String", "String", "Boolean", "String",  };
	public static final Object[] argDefaults= new Object[] {null,null,null,null,null,"html",null,null,null,null,null, };
	public static java.lang.reflect.Method renderMethod = getRenderMethod(japidviews._tags.paramWithDefaults2.class);

	{
		setRenderMethod(renderMethod);
		setArgNames(argNames);
		setArgTypes(argTypes);
		setArgDefaults(argDefaults);
		setSourceTemplate(sourceTemplate);
	}
////// end of named args stuff

	private String name; // line 1
	private String url; // line 1
	private String type; // line 1
	private String data; // line 1
	private String reRender; // line 1
	private String dataType; // line 1
	private String beforeSend; // line 1
	private String success; // line 1
	private String jsData; // line 1
	private Boolean cache; // line 1
	private String event; // line 1
	public cn.bran.japid.template.RenderResult render(String name,String url,String type,String data,String reRender,String dataType,String beforeSend,String success,String jsData,Boolean cache,String event) {
		this.name = name;
		this.url = url;
		this.type = type;
		this.data = data;
		this.reRender = reRender;
		this.dataType = dataType;
		this.beforeSend = beforeSend;
		this.success = success;
		this.jsData = jsData;
		this.cache = cache;
		this.event = event;
		long __t = -1;
		try {super.layout();} catch (RuntimeException e) { super.handleException(e);} // line 1
		return new cn.bran.japid.template.RenderResultPartial(getHeaders(), getOut(), __t, actionRunners, sourceTemplate);
	}

	public static cn.bran.japid.template.RenderResult apply(String name,String url,String type,String data,String reRender,String dataType,String beforeSend,String success,String jsData,Boolean cache,String event) {
		return new paramWithDefaults2().render(name, url, type, data, reRender, dataType, beforeSend, success, jsData, cache, event);
	}

	@Override protected void doLayout() {
		beginDoLayout(sourceTemplate);
//------
;// line 1
		p("\n" + 
"   \n" + 
"oh well...\n" + 
"\n");// line 4
		 String fancyname = "fancy: $[name]";// line 8
		p("\n" + 
"The fancy name is ");// line 8
		p(fancyname);// line 10
		;// line 10
		
		endDoLayout(sourceTemplate);
	}

}