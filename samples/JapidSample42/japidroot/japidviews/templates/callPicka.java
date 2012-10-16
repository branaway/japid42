package japidviews.templates;
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
// NOTE: This file was generated from: japidviews/templates/callPicka.html
// Change to this file will be lost next time the template file is compiled.
//
public class callPicka extends cn.bran.play.JapidTemplateBase
{
	public static final String sourceTemplate = "japidviews/templates/callPicka.html";
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


	public callPicka() {
		super(null);
	}
	public callPicka(StringBuilder out) {
		super(out);
	}
/* based on https://github.com/branaway/Japid/issues/12
 */
	public static final String[] argNames = new String[] {/* args of the template*/ };
	public static final String[] argTypes = new String[] {/* arg types of the template*/ };
	public static final Object[] argDefaults= new Object[] { };
	public static java.lang.reflect.Method renderMethod = getRenderMethod(japidviews.templates.callPicka.class);

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
		 t = System.nanoTime();
		try {super.layout();} catch (RuntimeException e) { super.handleException(e);} 
     	String l = "" + (System.nanoTime() - t) / 100000;
		int len = l.length();
		l = l.substring(0, len - 1) + "." +  l.substring(len - 1);

		System.out.println("[callPicka] rendering time(ms): " + l);
		return new cn.bran.japid.template.RenderResultPartial(getHeaders(), getOut(), t, actionRunners, sourceTemplate);
	}
	@Override protected void doLayout() {
		beginDoLayout(sourceTemplate);
//------
;// line 1
		p("\n" + 
"<p>invoke a simple tag</p>\n" + 
"\n" + 
"Another simple tag aTag, which locates in the same directory as this template:\n" + 
"\n");// line 1
		p("\n" + 
"first define something in a Java code block. \n" + 
"\n");// line 8
		 List<String> strings = new ArrayList<String>(){{add("u");add("m");add("everyone");}};// line 11
		p("\n");// line 11
		final aTag _aTag0 = new aTag(getOut()); _aTag0.setActionRunners(getActionRunners()).setOut(getOut()); _aTag0.render(strings); // line 13// line 13
		p("\n" + 
"again: ");// line 13
		final aTag _aTag1 = new aTag(getOut()); _aTag1.setActionRunners(getActionRunners()).setOut(getOut()); _aTag1.render(strings); // line 15// line 15
		p("\n");// line 15
		final picka _picka2 = new picka(getOut()); _picka2.setActionRunners(getActionRunners()).setOut(getOut()); _picka2.render(// line 17
"aa", "b" + "c", new picka.DoBody<String>(){ // line 17
public void render(final String r) { // line 17
// line 17
		p("    the tag chosen: ");// line 17
		p(r);// line 18
		p("\n" + 
"    <p>and we can call a tag recursively? Yes we can!</p>\n" + 
"    \n" + 
"    ");// line 18
		final SampleTag _SampleTag3 = new SampleTag(getOut()); _SampleTag3.setActionRunners(getActionRunners()).setOut(getOut()); _SampleTag3.render(r); // line 21// line 21
		p("    \n" + 
"    <p>yes we can!</p>\n");// line 21
		
}

StringBuilder oriBuffer;
@Override
public void setBuffer(StringBuilder sb) {
	oriBuffer = getOut();
	setOut(sb);
}

@Override
public void resetBuffer() {
	setOut(oriBuffer);
}

}
);// line 17
		p("\n" + 
"\n" + 
"note: the picka tag is defined in the japidviews/_tags directory\n" + 
"</p>\n" + 
"\n" + 
"<p>\n" + 
"we can call without the body part:\n" + 
"\n");// line 24
		final picka _picka4 = new picka(getOut()); _picka4.setActionRunners(getActionRunners()).setOut(getOut()); _picka4.render("cc","dd"); // line 33// line 33
		p("\n" + 
"or with named args\n" + 
"\n");// line 33
		final picka _picka5 = new picka(getOut()); _picka5.setActionRunners(getActionRunners()).setOut(getOut()); _picka5.render(named("a", "aa"), named("b", "bb")); // line 37// line 37
		p("\n" + 
"</p>\n" + 
"<p>\n" + 
"Or using the full path of the tag starting with japidview\n" + 
"</p>\n" + 
"\n");// line 37
		final japidviews.templates.aTag _japidviews_templates_aTag6 = new japidviews.templates.aTag(getOut()); _japidviews_templates_aTag6.setActionRunners(getActionRunners()).setOut(getOut()); _japidviews_templates_aTag6.render(strings); // line 44// line 44
		p("\n" + 
"<p>You can use \".\" instead of \"/\" on the path:</p>\n" + 
"\n");// line 44
		final japidviews.templates.aTag _japidviews_templates_aTag7 = new japidviews.templates.aTag(getOut()); _japidviews_templates_aTag7.setActionRunners(getActionRunners()).setOut(getOut()); _japidviews_templates_aTag7.render(strings); // line 48// line 48
		;// line 48
		
		endDoLayout(sourceTemplate);
	}

}