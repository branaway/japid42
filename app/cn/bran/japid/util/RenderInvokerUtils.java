package cn.bran.japid.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import cn.bran.japid.compiler.NamedArgRuntime;
import cn.bran.japid.template.JapidTemplateBaseWithoutPlay;
import cn.bran.japid.template.RenderResult;

public class RenderInvokerUtils {
	// private static final String RENDER_METHOD = "render";

	/**
	 * 
	 */
	private static final String DELI = ", ";

	public static <T extends JapidTemplateBaseWithoutPlay> RenderResult render(T t, Object... args)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (args == null) {
			// treat it as a single null argument
			args = new Object[] { null };
		}

		Method m = t.renderMethodInstance;
		if (m == null) {
			throw new RuntimeException("The render method cache is not initialized for: " + t.getClass().getName());
		}
		
		Object invoke;
		try {
			invoke = m.invoke(t, args);
			return (RenderResult) invoke;
		} catch (IllegalArgumentException e) {
			String msg = e.getMessage();
			String paramTypes = flatArgs(m.getParameterTypes());
			msg = msg + ": the arguments do not match the parameters of the template " + t.getClass().getName()
					+ paramTypes;
			String ss = flatArgs(args);
			throw new RuntimeException(msg + ": " + ss);
			// System.err.println(e + ": " + m + args);
			// throw e;
		}
		// } catch (IllegalArgumentException e) {
		// throw new RuntimeException("Template argument type mismatch: ", e);
		// } catch (InvocationTargetException e) {
		// Throwable te = e.getTargetException();
		// Throwable cause = te.getCause();
		// if (cause != null)
		// throw new RuntimeException("error in running the renderer: ", cause);
		// else
		// throw new RuntimeException("error in running the renderer: ", te);
		// // te.printStackTrace();
		// } catch (Exception e) {
		// e.printStackTrace();
		// throw new RuntimeException(e);
		// }
	}

	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param args
	 * @return
	 */
	private static String flatArgs(Object[] args) {
		if (args == null) {
			return "[]";
		}

		String sb = "";
		for (Object o : args) {
			if (o instanceof Class) {
				sb += ((Class<?>) o).getName() + DELI;
			} else
				sb += o.getClass().getName() + DELI;
		}
		if (sb.endsWith(DELI)) {
			sb = sb.substring(0, sb.length() - 2);
		}
		return "[" + sb + "]";
	}

	public static <T extends JapidTemplateBaseWithoutPlay> RenderResult renderWithNamedArgs(T t,
			NamedArgRuntime... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (args == null) {
			// treat it as a single null argument
			args = new NamedArgRuntime[] { null };
		}
		return render(t, t.buildArgs(args));
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
	public static <T extends JapidTemplateBaseWithoutPlay> RenderResult invokeRender(Class<T> c, Object... args) {
		int modifiers = c.getModifiers();
		if (Modifier.isAbstract(modifiers)) {
			throw new RuntimeException("Cannot init the template class since it's an abstract class: " + c.getName());
		}
		try {
			// String methodName = "render";
			Constructor<T> ctor = c.getConstructor(StringBuilder.class);
			RenderResult rr = invokeRenderer(ctor, args);
			// RenderResult rr = (RenderResult) MethodUtils.invokeMethod(t,
			// methodName, args);
			return rr;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Could not match the arguments with the template args.");
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException("Could not invoke the template object: ", e);
		}
	}

	public static <T extends JapidTemplateBaseWithoutPlay> RenderResult invokeRenderer(Constructor<T> ctor,
			Object... args) {
		try {
			StringBuilder sb = new StringBuilder(8000);
			T t = ctor.newInstance(sb);
			RenderResult rr = render(t, args);
			JapidFlags.logTimeLogs(t);
			return rr;
		} catch (InstantiationException e) {
			// e.printStackTrace();
			throw new RuntimeException("Could not instantiate the template object. Abstract?");
		} catch (InvocationTargetException e) {
			// e.printStackTrace();
			Throwable te = e.getTargetException();
			// if (te instanceof TemplateExecutionException)
			// throw (TemplateExecutionException) te;
			Throwable cause = te.getCause();
			if (cause != null)
				if (cause instanceof RuntimeException)
					throw (RuntimeException) cause;
				else
					throw new RuntimeException("error in running the renderer: " + cause.getMessage(), cause);
			else if (te instanceof RuntimeException)
				throw (RuntimeException) te;
			else
				throw new RuntimeException("error in running the renderer: " + te.getMessage(), te);
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException("Could not invoke the template object: ", e);
			// throw new RuntimeException(e);
		}
	}

	public static <T extends JapidTemplateBaseWithoutPlay> RenderResult invokeNamedArgsRender(Class<T> c,
			NamedArgRuntime[] args) {
		int modifiers = c.getModifiers();
		if (Modifier.isAbstract(modifiers)) {
			throw new RuntimeException("Cannot init the template class since it's an abstract class: " + c.getName());
		}
		try {
			// String methodName = "render";
			Constructor<T> ctor = c.getConstructor(StringBuilder.class);
			StringBuilder sb = new StringBuilder(8000);
			JapidTemplateBaseWithoutPlay t = ctor.newInstance(sb);
			RenderResult rr = renderWithNamedArgs(t, args);
			JapidFlags.logTimeLogs(t);
			return rr;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Could not match the arguments with the template args.");
		} catch (InstantiationException e) {
			// e.printStackTrace();
			throw new RuntimeException("Could not instantiate the template object. Abstract?");
		} catch (InvocationTargetException e) {
			// e.printStackTrace();
			Throwable e1 = e.getTargetException();
			throw new RuntimeException("Could not invoke the template object:  ", e1);
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException("Could not invoke the template object: ", e);
			// throw new RuntimeException(e);
		}
	}
}
