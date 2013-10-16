package japidviews;
import java.util.List;

import play.i18n.Lang;
import play.mvc.Http.Context.Implicit;
import play.mvc.Http.Flash;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Http.Session;
import cn.bran.japid.tags.Each;
import cn.bran.play.routing.RouteEntry;
//
// NOTE: This file was generated from: japidviews/dev404.html
// Change to this file will be lost next time the template file is compiled.
//
public class dev404 extends cn.bran.play.JapidTemplateBase
{
	public static final String sourceTemplate = "japidviews/dev404.html";
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


	public dev404() {
		super(null);
	}
	public dev404(StringBuilder out) {
		super(out);
	}
/* based on https://github.com/branaway/Japid/issues/12
 */
	public static final String[] argNames = new String[] {/* args of the template*/"reqheader", "routes", "jaxRoutes",  };
	public static final String[] argTypes = new String[] {/* arg types of the template*/"play.mvc.Http.RequestHeader", "List<scala.Tuple3<String, String, String>>", "List<RouteEntry>",  };
	public static final Object[] argDefaults= new Object[] {null,null,null, };
	public static java.lang.reflect.Method renderMethod = getRenderMethod(japidviews.dev404.class);

	{
		setRenderMethod(renderMethod);
		setArgNames(argNames);
		setArgTypes(argTypes);
		setArgDefaults(argDefaults);
		setSourceTemplate(sourceTemplate);
	}
////// end of named args stuff

	private play.mvc.Http.RequestHeader reqheader; // line 2
	private List<scala.Tuple3<String, String, String>> routes; // line 2
	private List<RouteEntry> jaxRoutes; // line 2
	public cn.bran.japid.template.RenderResult render(play.mvc.Http.RequestHeader reqheader,List<scala.Tuple3<String, String, String>> routes,List<RouteEntry> jaxRoutes) {
		this.reqheader = reqheader;
		this.routes = routes;
		this.jaxRoutes = jaxRoutes;
		long __t = -1;
		try {super.layout();} catch (RuntimeException e) { super.handleException(e);} // line 2
		return new cn.bran.japid.template.RenderResultPartial(getHeaders(), getOut(), __t, actionRunners, sourceTemplate);
	}

	public static cn.bran.japid.template.RenderResult apply(play.mvc.Http.RequestHeader reqheader,List<scala.Tuple3<String, String, String>> routes,List<RouteEntry> jaxRoutes) {
		return new dev404().render(reqheader, routes, jaxRoutes);
	}

	@Override protected void doLayout() {
		beginDoLayout(sourceTemplate);
//------
;// line 1
		;// line 1
		p("\n" + 
"<!DOCTYPE html>\n" + 
"<html>\n" + 
"	<head>\n" + 
"		<title>Action not found error/404</title>\n" + 
"		<link rel=\"shortcut icon\" href=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAlFJREFUeNqUU8tOFEEUPVVdNV3dPe8xYRBnjGhmBgKjKzCIiQvBoIaNbly5Z+PSv3Aj7DSiP2B0rwkLGVdGgxITSCRIJGSMEQWZR3eVt5sEFBgTb/dN1yvnnHtPNTPG4PqdHgCMXnPRSZrpSuH8vUJu4DE4rYHDGAZDX62BZttHqTiIayM3gGiXQsgYLEvATaqxU+dy1U13YXapXptpNHY8iwn8KyIAzm1KBdtRZWErpI5lEWTXp5Z/vHpZ3/wyKKwYGGOdAYwR0EZwoezTYApBEIObyELl/aE1/83cp40Pt5mxqCKrE4Ck+mVWKKcI5tA8BLEhRBKJLjez6a7MLq7XZtp+yyOawwCBtkiBVZDKzRk4NN7NQBMYPHiZDFhXY+p9ff7F961vVcnl4R5I2ykJ5XFN7Ab7Gc61VoipNBKF+PDyztu5lfrSLT/wIwCxq0CAGtXHZTzqR2jtwQiXONma6hHpj9sLT7YaPxfTXuZdBGA02Wi7FS48YiTfj+i2NhqtdhP5RC8mh2/Op7y0v6eAcWVLFT8D7kWX5S9mepp+C450MV6aWL1cGnvkxbwHtLW2B9AOkLeUd9KEDuh9fl/7CEj7YH5g+3r/lWfF9In7tPz6T4IIwBJOr1SJyIGQMZQbsh5P9uBq5VJtqHh2mo49pdw5WFoEwKWqWHacaWOjQXWGcifKo6vj5RGS6zykI587XeUIQDqJSmAp+lE4qt19W5P9o8+Lma5DcjsC8JiT607lMVkdqQ0Vyh3lHhmh52tfNy78ajXv0rgYzv8nfwswANuk+7sD/Q0aAAAAAElFTkSuQmCC\">\n" + 
"        <style>\n" + 
"		    html, body, pre {\n" + 
"		        margin: 0;\n" + 
"		        padding: 0;\n" + 
"		        font-family: Monaco, 'Lucida Console', monospace;\n" + 
"		        background: #ECECEC;\n" + 
"		    }\n" + 
"		    h1 {\n" + 
"		        margin: 0;\n" + 
"		        background: #AD632A;\n" + 
"		        padding: 20px 45px;\n" + 
"		        color: #fff;\n" + 
"		        text-shadow: 1px 1px 1px rgba(0,0,0,.3);\n" + 
"		        border-bottom: 1px solid #9F5805;\n" + 
"		        font-size: 28px;\n" + 
"		    }\n" + 
"		    p#detail {\n" + 
"		        margin: 0;\n" + 
"		        padding: 15px 45px;\n" + 
"		        background: #F6A960;\n" + 
"		        border-top: 4px solid #D29052;\n" + 
"		        color: #733512;\n" + 
"		        text-shadow: 1px 1px 1px rgba(255,255,255,.3);\n" + 
"		        font-size: 14px;\n" + 
"		        border-bottom: 1px solid #BA7F5B;\n" + 
"		    }\n" + 
"		    h2 {\n" + 
"		        margin: 0;\n" + 
"		        padding: 5px 45px;\n" + 
"		        font-size: 12px;\n" + 
"		        background: #333;\n" + 
"		        color: #fff;\n" + 
"		        text-shadow: 1px 1px 1px rgba(0,0,0,.3);\n" + 
"		        border-top: 4px solid #2a2a2a;\n" + 
"		    }\n" + 
"\n" + 
"		    h3 {\n" + 
"		        padding: 5px 45px;\n" + 
"		        font-size: 12px;\n" + 
"		    }\n" + 
"\n" + 
"			pre {\n" + 
"				margin: 0;\n" + 
"				border-bottom: 1px solid #DDD;\n" + 
"				text-shadow: 1px 1px 1px rgba(255,255,255,.5);\n" + 
"				position: relative;\n" + 
"				font-size: 12px;\n" + 
"				overflow: hidden;\n" + 
"			}\n" + 
"			pre span.line {\n" + 
"			    text-align: right;\n" + 
"			    display: inline-block;\n" + 
"			    padding: 5px 5px;\n" + 
"			    width: 30px;\n" + 
"			    background: #D6D6D6;\n" + 
"			    color: #8B8B8B;\n" + 
"			    text-shadow: 1px 1px 1px rgba(255,255,255,.5);\n" + 
"			    font-weight: bold;\n" + 
"			}\n" + 
"			pre span.route {\n" + 
"			    padding: 5px 5px;\n" + 
"			    position: absolute;\n" + 
"			    right: 0;\n" + 
"			    left: 40px;\n" + 
"			}\n" + 
"			pre span.route span.verb {\n" + 
"			    display: inline-block;\n" + 
"			    width: 5%;\n" + 
"			    min-width: 50px;\n" + 
"			    overflow: hidden;\n" + 
"			    margin-right: 10px;\n" + 
"			}\n" + 
"			pre span.route span.path {\n" + 
"			    display: inline-block;\n" + 
"			    width: 30%;\n" + 
"			    min-width: 200px;\n" + 
"			    overflow: hidden;\n" + 
"			    margin-right: 10px;\n" + 
"			}\n" + 
"			pre span.route span.call {\n" + 
"			    display: inline-block;\n" + 
"			    width: 50%;\n" + 
"			    overflow: hidden;\n" + 
"			    margin-right: 10px;\n" + 
"			}\n" + 
"			pre:first-child span.route {\n" + 
"			    border-top: 4px solid #CDCDCD;\n" + 
"			}\n" + 
"			pre:first-child span.line {\n" + 
"			    border-top: 4px solid #B6B6B6;\n" + 
"			}\n" + 
"			pre.error span.line {\n" + 
"			    background: #A31012;\n" + 
"			    color: #fff;\n" + 
"			    text-shadow: 1px 1px 1px rgba(0,0,0,.3);\n" + 
"			}\n" + 
"		</style>\n" + 
"	</head>\n" + 
"	<body>\n" + 
"		<h1>Action not found(404)</h1>\n" + 
"\n" + 
"		<p id=\"detail\">\n" + 
"			For request '");// line 6
		p(reqheader);// line 115
		p("'\n" + 
"		</p>\n" + 
"		\n" + 
"		    <h2>\n" + 
"    			These following routes have been tried, in this order:\n" + 
"        	</h2>\n" + 
"\n" + 
"		");// line 115
		if(asBoolean(jaxRoutes)) {// line 122
		p("		    <h3>\n" + 
"    			Routes derived from JAX-RS annotations:\n" + 
"        	</h3>\n" + 
"            <div>\n" + 
"    			");// line 122
		final Each _Each0 = new Each(getOut()); _Each0.setOut(getOut()); _Each0.render(// line 127
jaxRoutes, new Each.DoBody<RouteEntry>(){ // line 127
public void render(final RouteEntry r, final int _size, final int _index, final boolean _isOdd, final String _parity, final boolean _isFirst, final boolean _isLast) { // line 127
// line 127
		p("    				<pre><span class=\"line\">");// line 127
		p(_index);// line 128
		p("</span><span class=\"route\"><span class=\"verb\">");// line 128
		p(r.verb);// line 128
		p("</span><span class=\"path\">");// line 128
		p(r.path);// line 128
		p("</span><span class=\"call\">");// line 128
		p(r.action);// line 128
		p("</span></span></pre>\n" + 
"    			");// line 128
		
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
);// line 127
		p("			</div>\n" + 
"		");// line 129
		} else {// line 131
		p("			<h3>\n" + 
"    			No routes derived from JAX-RS annotations found.\n" + 
"    		</h3>\n" + 
"        ");// line 131
		}// line 135
		if(asBoolean(routes)) {// line 137
		p("		    <h3>\n" + 
"    			Routes defined in routes file:\n" + 
"        	</h3>\n" + 
"            <div>\n" + 
"    			");// line 137
		final Each _Each1 = new Each(getOut()); _Each1.setOut(getOut()); _Each1.render(// line 142
routes, new Each.DoBody<scala.Tuple3>(){ // line 142
public void render(final scala.Tuple3 r, final int _size, final int _index, final boolean _isOdd, final String _parity, final boolean _isFirst, final boolean _isLast) { // line 142
// line 142
		p("    				<pre><span class=\"line\">");// line 142
		p(_index);// line 143
		p("</span><span class=\"route\"><span class=\"verb\">");// line 143
		p(r._1());// line 143
		p("</span><span class=\"path\">");// line 143
		p(r._2());// line 143
		p("</span><span class=\"call\">");// line 143
		p(r._3());// line 143
		p("</span></span></pre>\n" + 
"    			");// line 143
		
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
);// line 142
		p("			</div>\n" + 
"		");// line 144
		} else {// line 146
		p("			<h3>\n" + 
"    			No static router defined.\n" + 
"    		</h3>\n" + 
"        ");// line 146
		}// line 150
		p("	</body>\n" + 
"</html>\n" + 
"\n" + 
"\n" + 
"\n" + 
"\n" + 
"\n" + 
"\n" + 
"\n");// line 150
		
		endDoLayout(sourceTemplate);
	}

}