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
// NOTE: This file was generated from: japidviews/Application/categories.html
// Change to this file will be lost next time the template file is compiled.
//
public class categories extends cn.bran.play.JapidTemplateBase
{
	public static final String sourceTemplate = "japidviews/Application/categories.html";
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


	public categories() {
		super(null);
	}
	public categories(StringBuilder out) {
		super(out);
	}
/* based on https://github.com/branaway/Japid/issues/12
 */
	public static final String[] argNames = new String[] {/* args of the template*/"categories",  };
	public static final String[] argTypes = new String[] {/* arg types of the template*/"List<Category>",  };
	public static final Object[] argDefaults= new Object[] {null, };
	public static java.lang.reflect.Method renderMethod = getRenderMethod(japidviews.Application.categories.class);

	{
		setRenderMethod(renderMethod);
		setArgNames(argNames);
		setArgTypes(argTypes);
		setArgDefaults(argDefaults);
		setSourceTemplate(sourceTemplate);
	}
////// end of named args stuff

	private List<Category> categories; // line 1
	public cn.bran.japid.template.RenderResult render(List<Category> categories) {
		this.categories = categories;
		long __t = -1;
		try {super.layout();} catch (RuntimeException e) { super.handleException(e);} // line 1
		return new cn.bran.japid.template.RenderResultPartial(getHeaders(), getOut(), __t, actionRunners, sourceTemplate);
	}

	public static cn.bran.japid.template.RenderResult apply(List<Category> categories) {
		return new categories().render(categories);
	}

	@Override protected void doLayout() {
		beginDoLayout(sourceTemplate);
//------
p(" ");// line 1
 if(asBoolean(categories)) {// line 2
		p("     <ul>\n" + 
"       ");// line 2
		for(Category cat: categories) {// line 4
		p("	       <li>\n" + 
"	           <a href=\"\">");// line 4
		p(cat.name);// line 6
		p("</a>\n" + 
"	           ");// line 6
		final categories _this0 = new categories(getOut()); _this0.setActionRunners(getActionRunners()).setOut(getOut()); _this0.render(cat.subCategories); // line 7// line 7
		p("	       </li>\n" + 
"       ");// line 7
		}// line 9
		p("     </ul>\n" + 
" ");// line 9
		}// line 11
		p(" \n" + 
" ");// line 11
		
		endDoLayout(sourceTemplate);
	}

}