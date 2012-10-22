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

	final Request request = Implicit.request(); 
	final Response response = Implicit.response(); 
	final Session session = Implicit.session();
	final Flash flash = Implicit.flash();
	final Lang lang = Implicit.lang();
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