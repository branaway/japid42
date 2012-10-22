package japidviews._layouts;
import java.util.*;
import java.io.*;
import cn.bran.japid.tags.Each;
import cn.bran.japid.template.ActionRunner;
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
// NOTE: This file was generated from: japidviews/_layouts/lcomposite2.html
// Change to this file will be lost next time the template file is compiled.
//
public abstract class lcomposite2 extends cn.bran.play.JapidTemplateBase
{
	public static final String sourceTemplate = "japidviews/_layouts/lcomposite2.html";
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


	public lcomposite2() {
		super(null);
	}
	public lcomposite2(StringBuilder out) {
		super(out);
	}
	@Override public void layout() {
		beginDoLayout(sourceTemplate);		p("<p>beginning: lcomposite</p>\n" + 
"\n");// line 1
		p("\n" + 
"\n");// line 3
				actionRunners.put(getOut().length(), new cn.bran.play.CacheablePlayActionRunner("", controllers.Application.class, "foo", "") {
			@Override
			public cn.bran.play.JapidResult runPlayAction()  {
				return (cn.bran.play.JapidResult)controllers.Application.foo(); // line 5
			}
		}); p("\n");// line 5
		p("\n");// line 5
		doLayout();// line 7
		p("\n" + 
"<p>back to layout</p>\n" + 
"\n" + 
"\n");// line 7
				actionRunners.put(getOut().length(), new cn.bran.play.CacheablePlayActionRunner("", controllers.Application.class, "foo", "") {
			@Override
			public cn.bran.play.JapidResult runPlayAction()  {
				return (cn.bran.play.JapidResult)controllers.Application.foo(); // line 12
			}
		}); p("\n");// line 12
		p("\n" + 
"<p>back to layout again</p>\n" + 
"\n" + 
"<p>end of lcomposite</p>\n");// line 12
				endDoLayout(sourceTemplate);	}

	protected abstract void doLayout();
}