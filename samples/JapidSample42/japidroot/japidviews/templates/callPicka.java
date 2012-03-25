package japidviews.templates;
import java.util.*;
import java.io.*;
import cn.bran.japid.tags.Each;
import japidviews ._tags.*;
import play.mvc.Http.Context.Implicit;
import models.*;
import play.i18n.Lang;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Http.Session;
import play.mvc.Http.Flash;
import japidviews ._layouts.*;
import play.data.validation.Validation;
import java.util.*;
import static cn.bran.japid.util.WebUtils.*;
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
		p("\n");// line 8
		final picka _picka0 = new picka(getOut()); _picka0.setActionRunners(getActionRunners()).setOut(getOut()); _picka0.render(// line 9
"a", "b" + "c", new picka.DoBody<String>(){ // line 9
public void render(final String r) { // line 9
// line 9
		p("    the tag chosed: ");// line 9
		p(r);// line 10
		p("\n" + 
"    <p>and we can call a tag recursively?</p>\n" + 
"    ");// line 10
		final SampleTag _SampleTag1 = new SampleTag(getOut()); _SampleTag1.setActionRunners(getActionRunners()).setOut(getOut()); _SampleTag1.render(r); // line 12// line 12
		p("    <p>yes we can!</p>\n");// line 12
		
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
);// line 9
		p("\n" + 
"first define something in a Java code block. \n" + 
"\n");// line 14
		 List<String> strings = new ArrayList<String>(){{add("u");add("m");add("everyone");}};// line 18
		p("\n");// line 18
		final aTag _aTag2 = new aTag(getOut()); _aTag2.setActionRunners(getActionRunners()).setOut(getOut()); _aTag2.render(strings); // line 20// line 20
		p("\n" + 
"note: the picka tag is defined in the japidviews/_tags directory\n" + 
"</p>\n" + 
"\n" + 
"<p>\n" + 
"we can call without the body part:\n" + 
"\n");// line 20
		final picka _picka3 = new picka(getOut()); _picka3.setActionRunners(getActionRunners()).setOut(getOut()); _picka3.render("cc","dd"); // line 28// line 28
		p("\n" + 
"or with named args\n" + 
"\n");// line 28
		final picka _picka4 = new picka(getOut()); _picka4.setActionRunners(getActionRunners()).setOut(getOut()); _picka4.render(named("a", "aa"), named("b", "bb")); // line 32// line 32
		p("\n" + 
"</p>\n" + 
"<p>\n" + 
"Or using the full path of the tag starting with japidview\n" + 
"</p>\n" + 
"\n");// line 32
		final japidviews.templates.aTag _japidviews_templates_aTag5 = new japidviews.templates.aTag(getOut()); _japidviews_templates_aTag5.setActionRunners(getActionRunners()).setOut(getOut()); _japidviews_templates_aTag5.render(strings); // line 39// line 39
		p("\n" + 
"<p>You can use \".\" instead of \"/\" on the path:</p>\n" + 
"\n");// line 39
		final japidviews.templates.aTag _japidviews_templates_aTag6 = new japidviews.templates.aTag(getOut()); _japidviews_templates_aTag6.setActionRunners(getActionRunners()).setOut(getOut()); _japidviews_templates_aTag6.render(strings); // line 43// line 43
		;// line 43
		
		endDoLayout(sourceTemplate);
	}

}