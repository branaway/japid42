package cn.bran.play;

import java.util.HashMap;
import java.util.Map;

import play.cache.Cache;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Results;
import cn.bran.japid.compiler.NamedArgRuntime;
import cn.bran.japid.template.JapidRenderer;
import cn.bran.japid.template.JapidTemplateBaseWithoutPlay;
import cn.bran.japid.template.RenderResult;
import cn.bran.japid.util.DirUtil;
import cn.bran.japid.util.RenderInvokerUtils;
import cn.bran.japid.util.StackTraceUtils;

/**
 * a helper class. for hiding the template API from user eyes. not really needed
 * since the template invocation API is simple enough.
 * 
 * @author Bing Ran<bing_ran@hotmail.com>
 */
public class JapidController extends Controller {
	public static ThreadLocal<Map<String, String>> threadData = new ThreadLocal<Map<String, String>>() {
		@Override
		protected Map<String, String> initialValue() {
			return new HashMap<String, String>();
		}
	};
	
	/**
	 * 
	 */
	private static final String CONTROLLERS = "controllers.";
	public static final char DOT = '.';
	/**
	 * render an array of objects to a template defined by a Template class.
	 * 
	 * @param <T>
	 *            a sub-class type of JapidTemplateBase
	 * @param c
	 *            a sub-class of JapidTemplateBase
	 * @param args
	 *            arguments
	 */
	public static <T extends JapidTemplateBaseWithoutPlay> JapidResult render(Class<T> c, Object... args) {
		try {
			RenderResult rr = RenderInvokerUtils.invokeRender(c, args);
			JapidResult japidResult = new JapidResult(rr);
			postProcess(japidResult);
			return japidResult;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static JapidResult postProcess(JapidResult japidResult) {
		// XXX play2 does not guarantee the response
		
		if (Context.current.get() != null) {
			// apply headers
			try {
				Map<String, String> currentHeaders = response().getHeaders();

				Map<String, String> headers = japidResult.getHeaders();
				for (String k : headers.keySet()) {
					if (!currentHeaders.containsKey(k))
						response().setHeader(k, headers.get(k));
				}
			} catch (RuntimeException e) {
			}
		}

		// eagerly evaluate. The consequence is that there is no cache support
		// in each included part
		// no we need cache support
		return japidResult/* .eval() */;
	}

	/**
	 * just hide the result throwing
	 * 
	 * @param rr
	 */
	protected static JapidResult render(RenderResult rr) {
		return postProcess(new JapidResult(rr));
	}

	/**
	 * pickup the Japid renderer in the conventional location and render it.
	 * Positional match is used to assign values to parameters
	 * 
	 * TODO: the signature would be confusing for cases where there is a single
	 * argument and the type is an array! In that case the user must cast it to
	 * Object: <code>renderJapid((Object)myArray);</code>
	 * 
	 * @param objects
	 */
	public static JapidResult renderJapid(Object... objects) {
		String action = template("renderJapid");
		return renderJapidWith(action, objects);
	}

	public static JapidResult renderJapidByName(NamedArgRuntime... namedArgs) {
		String action = template("renderJapidByName");
		return renderJapidWith(action, namedArgs);
	}

	/**
	 * 
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param templateName the script name or a fully qualified class name under which the renderer class
	 * is registered.
	 * 
	 * @param args
	 * @return
	 */
	public static JapidResult renderJapidWith(String templateName, Object... args) {
			templateName = getFullViewName(templateName);
			JapidResult japidResult = new JapidResult(JapidRenderer.renderWith(templateName, args));
			postProcess(japidResult);
			return japidResult;
	}

	private static JapidResult generateErrorResult(Exception e) {
		JapidResult result = new JapidResult(JapidRenderer.handleException(e));
		postProcess(result);
		return result;
	}

	/**
	 * render data to a template string, which is parsed and compiled to Java bytecode before use.
	 * 
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param template the content of the template
	 * @param args the arguments
	 * @return
	 */
	public static JapidResult renderDynamic(String template, Object... args) {
		try {
			RenderResult rr = JapidRenderer.renderDynamic(template, args);
			return new JapidResult(rr);
		} catch (Throwable e) {
			return new JapidResult(JapidRenderer.handleException(e));
		}
	}
	
	public static JapidResult renderDynamicByKey(String key, Object... args) {
		try {
			RenderResult rr = JapidRenderer.renderDynamicByKey(key, args);
			return new JapidResult(rr);
		} catch (Throwable e) {
			return new JapidResult(JapidRenderer.handleException(e));
		}
	}
	
	
	private static String getFullViewName(String template) {
		if (template.startsWith("@")) {
			template = template.substring(1);
			// get parent path
			String defaultView = template("renderJapidWith");
			String parent = defaultView.substring(0, defaultView.lastIndexOf('.'));
			template = parent + "." + template;
		}
		return template;
	}

	public static JapidResult renderJapidWith(String template, NamedArgRuntime[] namedArgs) {
		template = getFullViewName(template);
		JapidResult japidResult = new JapidResult(JapidRenderer.getRenderResultWith(template, namedArgs));
		return postProcess(japidResult);
	}

	public static String template(String method) {
		// first check if there is a method hint in the session
		String japidControllerInvoker = threadData.get().remove(GlobalSettingsWithJapid.ACTION_METHOD);
		// use the thread local is not reliable nor correct if the action forwards to another action. 
		// disable it
		if (true || japidControllerInvoker == null) {
			// return StackTraceUtils.getJapidRenderInvoker();
			japidControllerInvoker = StackTraceUtils.getJapidControllerInvoker(method);
		}

		if (japidControllerInvoker.startsWith(CONTROLLERS))
			japidControllerInvoker = japidControllerInvoker.substring(CONTROLLERS.length());

		String expr = japidControllerInvoker;

		// some content negotiation
		// TODO: shall we set the response content type accordingly?
		String format = resolveFormat(Context.current.get());
		if ("html".equals(format)) {
			return expr;
		} else {
			String expr_format = expr + "_" + format;
			try {
				Class<?> appClass = JapidRenderer.getClass(JapidRenderer.getTemplateClassName(expr_format));
				if (appClass != null)
					return expr_format;
				else {
					return expr;
				}
			} catch (RuntimeException e) {
				return expr;
			}
		}
	}

	public static String resolveFormat(Context context) {
		if (context == null)
			return "html";

		Map<String, String[]> headers = context.request().headers();
		String format = "html";

		if (headers.get("accept") == null && headers.get("Accept") == null && headers.get("ACCEPT") == null) {
			return format;
		}

		String accept = "";
		if (headers.get("accept") != null)
			accept = headers.get("accept")[0];
		else if (headers.get("Accept") != null)
			accept = headers.get("Accept")[0];
		else if (headers.get("ACCEPT") != null)
			accept = headers.get("ACCEPT")[0];

		if (accept.indexOf("application/xhtml") != -1 || accept.indexOf("text/html") != -1 || accept.startsWith("*/*")) {
			format = "html";
		} else if (accept.indexOf("application/xml") != -1 || accept.indexOf("text/xml") != -1) {
			format = "xml";
		} else if (accept.indexOf("text/plain") != -1) {
			format = "txt";
		} else if (accept.indexOf("application/json") != -1 || accept.indexOf("text/javascript") != -1) {
			format = "json";
		} else if (accept.endsWith("*/*")) {
			format = "html";
		}
		return format;
	}

	/**
	 * mind the cost associated with this and the key building issues, as stated
	 * in the cache() method
	 * 
	 * @param objs
	 * @return
	 */

	protected static RenderResult getFromCache(Object... objs) {
		// the key building with caller info and the arguments
		if (RenderResultCache.shouldIgnoreCache())
			return null;
		String caller = buildKey(null, objs);
		Object object = Cache.get(caller);
		if (object instanceof RenderResult) {
			return (RenderResult) object;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param keyBase
	 *            usually the fully qualified method name of the controller
	 *            action
	 * @param objs
	 * @return
	 */
	protected static RenderResult getFromCache(String keyBase, Object... objs) {
		// the key building with caller info and the arguments
		if (RenderResultCache.shouldIgnoreCache())
			return null;
		String caller = buildKey(keyBase, objs);
		Object object = Cache.get(caller);
		if (object instanceof RenderResult) {
			return (RenderResult) object;
		} else {
			return null;
		}
	}

	/**
	 * @param objs
	 * @return
	 */
	private static String buildKey(String base, Object... objs) {
		// the getCaller thing is relatively expensive, as it might take
		// hundreds of us to complete.

		String caller = base;
		if (base == null)
			caller = StackTraceUtils.getCaller2(); // tricky and expensive
		for (Object o : objs) {
			caller += "-" + String.valueOf(o);
		}
		return caller;
	}

	/**
	 * render a text in a RenderResult so it can work with invoke tag in
	 * templates.
	 * 
	 * @param s
	 */
	protected static JapidResult renderText(String s) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "text/plain; charset=utf-8");
		return render(new RenderResult(headers, new StringBuilder(s), -1L));
	}

	protected static Result renderText(Object o) {
		String str = o == null ? "" : o.toString();
		return renderText(str);
	}

	protected static Result renderText(int o) {
		return renderText(new Integer(o));
	}

	protected static Result renderText(long o) {
		return renderText(new Long(o));
	}

	protected static Result renderText(float o) {
		return renderText(new Float(o));
	}

	protected static Result renderText(double o) {
		return renderText(new Double(o));
	}

	protected static Result renderText(boolean o) {
		return renderText(new Boolean(o));
	}

	protected static Result renderText(char o) {
		return renderText(new String(new char[] { o }));
	}

	protected static NamedArgRuntime named(String name, Object val) {
		return new NamedArgRuntime(name, val);
	}

	static String runnerName = CacheablePlayActionRunner.class.getName();

	/**
	 * determine if the current stack frame is a descendant of
	 * CacheablePlayActionRunner which is used when invoking actions from Japid
	 * views
	 * 
	 * @return
	 */
	public static boolean isInvokedfromJapidView() {
		Throwable t = new Throwable();
		t.printStackTrace();
		final StackTraceElement[] ste = t.getStackTrace();
		for (int i = 0; i < ste.length; i++) {
			StackTraceElement st = ste[i];
			String className = st.getClassName();
			if (className.equals(runnerName)) {
				return true;
			}
		}
		return false;
	}

	/**
     * Instantiates a new authenticity checking form that wraps the specified class.
     */
    public static <T> Form<T> form(Class<T> clazz) {
        return new AuthenticForm<T>(clazz);
    }
    
    /**
     * Instantiates a new form that wraps the specified class.
     */
    public static <T> Form<T> form(String name, Class<T> clazz) {
        return new AuthenticForm<T>(name, clazz);
    }

    /**
     * wrap a RenderResult in an OK result.
     * 
     * @author Bing Ran (bing.ran@gmail.com)
     * @param rr
     * @return
     */
    public static Result ok(RenderResult rr) {
    	try {
			return Results.ok(new JapidResult(rr));
		} catch (Exception e) {
			return Results.ok(generateErrorResult(e));
		}
    }

	/**
	 * find out if a Japid class denoted by the template name is available
	 * 
	 * @author Bing Ran (bing.ran@gmail.com)
	 * @param templateName
	 * @return
	 */
	public static boolean hasTemplate(String templateName) {
		return JapidRenderer.hasTemplate(templateName);
	}
	
	public static 	Class<? extends JapidTemplateBaseWithoutPlay> getTemplateClass(String templateName) {
		return JapidRenderer.getTemplateClass(templateName);
	}
}
