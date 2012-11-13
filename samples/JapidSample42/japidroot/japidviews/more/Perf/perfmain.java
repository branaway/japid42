package japidviews.more.Perf;
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
// NOTE: This file was generated from: japidviews/more/Perf/perfmain.html
// Change to this file will be lost next time the template file is compiled.
//
public abstract class perfmain extends cn.bran.play.JapidTemplateBase
{
	public static final String sourceTemplate = "japidviews/more/Perf/perfmain.html";
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


	public perfmain() {
		super(null);
	}
	public perfmain(StringBuilder out) {
		super(out);
	}
	private DataModel.User loggedInUser; // line 1
	 public void layout(DataModel.User loggedInUser) {
		this.loggedInUser = loggedInUser;
		beginDoLayout(sourceTemplate);		;// line 1
		p("\n" + 
"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n" + 
"        \"http://www.w3.org/TR/html4/loose.dtd\">\n" + 
"<html>\n" + 
"<head>\n" + 
"    <title>");// line 1
		title();p("</title>\n" + 
"</head>\n" + 
"<body>\n" + 
"\n");// line 7
		if (loggedInUser != null) {// line 11
		p("	<div>\n" + 
"	    Hello ");// line 11
		p(loggedInUser.getUserName());// line 13
		p(", You have ");// line 13
		p(loggedInUser.getFriends().size());// line 13
		p(" friends\n" + 
"	</div>\n");// line 13
		}// line 15
		p("\n" + 
"<h1>Entries</h1>\n" + 
"    ");// line 15
		doLayout();// line 18
		p("</body>\n" + 
"</html>\n");// line 18
				endDoLayout(sourceTemplate);	}
	 protected void title() {};

	protected abstract void doLayout();
}