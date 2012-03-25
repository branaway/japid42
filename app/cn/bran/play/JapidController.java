package cn.bran.play;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.Result;
import cn.bran.japid.compiler.NamedArgRuntime;
import cn.bran.japid.template.JapidRenderer;
import cn.bran.japid.template.JapidTemplateBaseWithoutPlay;
import cn.bran.japid.template.RenderResult;
import cn.bran.japid.util.RenderInvokerUtils;
import cn.bran.japid.util.StackTraceUtils;

/**
 * a helper class. for hiding the template API from user eyes. not really needed
 * since the template invocation API is simple enough.
 * 
 * @author Bing Ran<bing_ran@hotmail.com>
 */
public class JapidController extends Controller {
	/**
	 * 
	 */
	private static final String CONTROLLERS = "controllers.";
	/**
	 * 
	 */
	private static final String JAPIDVIEWS_ROOT = "japidviews";
	private static final char DOT = '.';
	private static final String HTML = ".html";

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
	public static <T extends JapidTemplateBaseWithoutPlay> JapidResult render(
			Class<T> c, Object... args) {
		try {
			RenderResult rr = invokeRender(c, args);
			JapidResult japidResult = new JapidResult(rr);
			applyHeaders(japidResult);
			return japidResult;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void applyHeaders(JapidResult japidResult) {
		// apply headers
		Map<String, String> headers = japidResult.getHeaders();
		for (String k : headers.keySet()) {
			response().setHeader(k, headers.get(k));
		}
	}

	/**
	 * @param <T>
	 * @param c
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private static <T extends JapidTemplateBaseWithoutPlay> RenderResult invokeRender(
			Class<T> c, Object... args) {
		int modifiers = c.getModifiers();
		if (Modifier.isAbstract(modifiers)) {
			throw new RuntimeException(
					"Cannot init the template class since it's an abstract class: "
							+ c.getName());
		}
		try {
			// String methodName = "render";
			Constructor<T> ctor = c.getConstructor(StringBuilder.class);
			StringBuilder sb = new StringBuilder(8000);
			T t = ctor.newInstance(sb);
			RenderResult rr = (RenderResult) RenderInvokerUtils.render(t, args);
			// RenderResult rr = (RenderResult) MethodUtils.invokeMethod(t,
			// methodName, args);
			return rr;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"Could not match the arguments with the template args.");
		} catch (InstantiationException e) {
			// e.printStackTrace();
			throw new RuntimeException(
					"Could not instantiate the template object. Abstract?");
		} catch (InvocationTargetException e) {
			// e.printStackTrace();
			Throwable te = e.getTargetException();
//			if (te instanceof TemplateExecutionException)
//				throw (TemplateExecutionException) te;
			Throwable cause = te.getCause();
			if (cause != null)
				if (cause instanceof RuntimeException)
					throw (RuntimeException) cause;
				else
					throw new RuntimeException(
							"error in running the renderer: "
									+ cause.getMessage(), cause);
			else if (te instanceof RuntimeException)
				throw (RuntimeException) te;
			else
				throw new RuntimeException("error in running the renderer: "
						+ te.getMessage(), te);
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException(
						"Could not invoke the template object: ", e);
			// throw new RuntimeException(e);
		}
	}

	private static <T extends JapidTemplateBaseWithoutPlay> RenderResult invokeNamedArgsRender(
			Class<T> c, NamedArgRuntime[] args) {
		int modifiers = c.getModifiers();
		if (Modifier.isAbstract(modifiers)) {
			throw new RuntimeException(
					"Cannot init the template class since it's an abstract class: "
							+ c.getName());
		}
		try {
			// String methodName = "render";
			Constructor<T> ctor = c.getConstructor(StringBuilder.class);
			StringBuilder sb = new StringBuilder(8000);
			JapidTemplateBaseWithoutPlay t = ctor.newInstance(sb);
			RenderResult rr = (RenderResult) RenderInvokerUtils
					.renderWithNamedArgs(t, args);
			// RenderResult rr = (RenderResult) MethodUtils.invokeMethod(t,
			// methodName, args);
			return rr;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"Could not match the arguments with the template args.");
		} catch (InstantiationException e) {
			// e.printStackTrace();
			throw new RuntimeException(
					"Could not instantiate the template object. Abstract?");
		} catch (InvocationTargetException e) {
			// e.printStackTrace();
			Throwable e1 = e.getTargetException();
			throw new RuntimeException(
					"Could not invoke the template object:  ", e1);
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException(
						"Could not invoke the template object: ", e);
			// throw new RuntimeException(e);
		}
	}

	/**
	 * just hide the result throwing
	 * 
	 * @param rr
	 */
	protected static JapidResult render(RenderResult rr) {
		return new JapidResult(rr);
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
	protected static JapidResult renderJapid(Object... objects) {
		String action = template("renderJapid");
		return renderJapidWith(action, objects);
	}

	protected static JapidResult renderJapidByName(NamedArgRuntime... namedArgs) {
		String action = template("renderJapidByName");
		return renderJapidWith(action, namedArgs);
	}

	public static JapidResult renderJapidWith(String template, Object... args) {
		JapidResult japidResult = new JapidResult(getRenderResultWith(template, args));
		applyHeaders(japidResult);
		return japidResult;
	}

	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param template
	 * @param args
	 * @return
	 */
	private static RenderResult getRenderResultWith(String template,
			Object[] args) {

		if (template == null || template.length() == 0) {
			template = template("getRenderResultWith");
		}

		if (template.endsWith(HTML)) {
			template = template.substring(0, template.length() - HTML.length());
		}

		String templateClassName = getTemplateClassName(template);

		Class<? extends JapidTemplateBaseWithoutPlay> tClass = getTemplateClass(templateClassName);

		if (tClass == null) {
			String templateFileName = templateClassName.replace(DOT, '/') + HTML;
			throw new RuntimeException("Could not find a Japid template with the name of: " + templateFileName);
		} else {
			// render(tClass, args);
			RenderResult rr = invokeRender(tClass, args);
			return rr;
		}
	}

	private static Class<? extends JapidTemplateBaseWithoutPlay> getTemplateClass(
			String templateClassName) {
		Class<? extends JapidTemplateBaseWithoutPlay> tClass = null;

		if (JapidRenderer.isDevMode())
			tClass = JapidRenderer.getClass(templateClassName);
		else
			try {
				tClass = (Class<? extends JapidTemplateBaseWithoutPlay>)
						JapidController.class.getClassLoader().loadClass(templateClassName);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		return tClass;
	}

	private static String getTemplateClassName(String template) {
		String templateClassName = template.startsWith(JAPIDVIEWS_ROOT) ?
					template :
					JAPIDVIEWS_ROOT + File.separator + template;

		templateClassName = templateClassName.replace('/', DOT).replace('\\', DOT);
		return templateClassName;
	}

	
	public static JapidResult renderJapidWith(String template,
			NamedArgRuntime[] namedArgs) {
		JapidResult japidResult = new JapidResult(getRenderResultWith(template, namedArgs));
		applyHeaders(japidResult);
		return japidResult;
	}


	protected static String template(String method) {
//		return StackTraceUtils.getJapidRenderInvoker();
		String japidControllerInvoker = StackTraceUtils.getJapidControllerInvoker(method);
		if (japidControllerInvoker.startsWith(CONTROLLERS))
			japidControllerInvoker = japidControllerInvoker.substring(CONTROLLERS.length());
		return japidControllerInvoker;
	}

	public static RenderResult getRenderResultWith(String template,
			NamedArgRuntime[] args) {
		
		String templateClassName = getTemplateClassName(template);
		Class<? extends JapidTemplateBaseWithoutPlay> tClass = getTemplateClass(templateClassName);

		if (tClass == null) {
			String templateFileName = templateClassName.replace(DOT, '/')
					+ HTML;
			throw new RuntimeException(
					"Could not find a Japid template with the name of: "
							+ templateFileName);
		} else {
			if (JapidTemplateBase.class.isAssignableFrom(tClass)) {
				RenderResult rr;
				// render(tClass, args);
				rr = invokeNamedArgsRender(tClass, args);
				return (rr);
			} else {
				throw new RuntimeException(
						"The found class is not a Japid template class: "
								+ templateClassName);
			}
		}

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
	
	public static String genCacheKey() {
		throw new RuntimeException("not implemented");
//		return "japidcache:" + Request.current().action + ":"
//				+ Request.current().querystring;
	}
	
//	public static Result ok(String s) {
//		return new JapidResult(new RenderResult(new HashMap(), new StringBuilder(s), -1));
//	}
}
