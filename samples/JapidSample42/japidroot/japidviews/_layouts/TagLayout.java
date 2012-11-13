package japidviews._layouts;
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
// NOTE: This file was generated from: japidviews/_layouts/TagLayout.html
// Change to this file will be lost next time the template file is compiled.
//
public abstract class TagLayout extends cn.bran.play.JapidTemplateBase
{
	public static final String sourceTemplate = "japidviews/_layouts/TagLayout.html";
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


	public TagLayout() {
		super(null);
	}
	public TagLayout(StringBuilder out) {
		super(out);
	}
	@Override public void layout() {
		beginDoLayout(sourceTemplate);		p("标签布局\n");// line 1
		p("\n");// line 2
		final dummyTag _dummyTag0 = new dummyTag(getOut()); _dummyTag0.setActionRunners(getActionRunners()).setOut(getOut()); _dummyTag0.render("me"); // line 3// line 3
		p("<div>\n" + 
"\n");// line 3
		p("\n" + 
"    \n");// line 6
		doLayout();// line 8
		p("\n" + 
"</div>\n" + 
"\n" + 
"\n");// line 8
				endDoLayout(sourceTemplate);	}

	protected abstract void doLayout();
}