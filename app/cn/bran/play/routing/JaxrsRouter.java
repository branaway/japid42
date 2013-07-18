/**
 * 
 */
package cn.bran.play.routing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Produces;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import play.Application;
import play.GlobalSettings;
import play.api.mvc.Handler;
import play.libs.F.Tuple;
import play.mvc.Result;

/**
 * @author bran
 * 
 */
public class JaxrsRouter {
	static String routerPackage = "controllers";
	static final String ASSET_SERVING = "jaxrc.assets.serving";
	static String[] assetServing = new String[] { "/assets", "/public" }; // format:
																			// "user space asset root, folder on file system"

	static Reflections ref = new Reflections(new ConfigurationBuilder()
			.filterInputsBy(new FilterBuilder().include(routerPackage + ".*"))
			.setScanners(new TypeAnnotationsScanner()).setUrls(ClasspathHelper.forPackage(routerPackage)));

	static String urlParamCapture = "\\{(.*?)\\}";
	static Pattern urlParamCaptureP = Pattern.compile(urlParamCapture);
	private static ClassLoader parentClassloader;
	private static Set<Class<?>> classes;
	private static GlobalSettings global;
	private static String appPath = "";

	static String prefixSlash(String s) {
		return s.startsWith("/") ? s : "/" + s;
	}

	private static List<RouterClass> routerClasses = new ArrayList<RouterClass>();

	public static void init(Application app, play.GlobalSettings g) {
		parentClassloader = app.classloader();
		classes = RouterUtils.classes(parentClassloader);

		global = g;
		ApplicationPath appPathAnno = global.getClass().getAnnotation(ApplicationPath.class);
		if (appPathAnno != null)
			appPath = appPathAnno.value();

		routerClasses = parseRouterClasses(classes);

		String string = app.configuration().getString(ASSET_SERVING);
		if (string != null)
			assetServing = string.split(",");
	}

	/**
	 * @author Bing Ran (bing.ran@gmail.com)
	 * @param classes2
	 * @return
	 */
	private static List<RouterClass> parseRouterClasses(Set<Class<?>> classes2) {
		List<RouterClass> routers = new ArrayList<RouterClass>();

		for (Class<?> cl : classes2) {
			routers.add(new RouterClass(cl, appPath));
		}
		return routers;
	}

	/**
	 * provides a handler for given request
	 * 
	 * @param global
	 *            the global of the current play application
	 * @param r
	 *            request header
	 * @return action handler
	 * 
	 */
	public static Handler handlerFor(final play.mvc.Http.RequestHeader r) {
		if (assetServing.length == 2 && r.path().startsWith(assetServing[0])) {
			return controllers.Assets.at(assetServing[1], r.path().replaceFirst(assetServing[0], ""));// serve
																										// static
																										// asset
		} else {
			// final TargetClassWithPath targetClassWithPath =
			// RouterUtils.findLongestMatch(classes, r, appPath);
			final RouterClass targetRouterClass = RouterUtils.findLongestMatch(routerClasses, r);
			if (targetRouterClass == null)
				return null;

	
			// Set<Method> methods =
			// RouterUtils.relevantMethods(targetClassWithPath._1(),
			// httpMethodClass);
			// final Tuple<Method, Map<String, String>> methodWithArgs =
			// RouterUtils.findMethodAndGenerateContext(
			final Tuple<Method, Object[]> methodWithArgs = targetRouterClass
					.findMethodAndGenerateArgs(r);

			if (methodWithArgs != null) {
				ResultBuilder resultBuilder = new ResultBuilder() {
					@Override
					public Result create() {
						try {
							Method m = methodWithArgs._1;
							Class<?> cl = targetRouterClass.clz;
							Object obj = global.getControllerInstance(cl);
							if (obj == null && (m.getModifiers() & Modifier.STATIC) != Modifier.STATIC) {
								throw new RuntimeException("the action method is not static while the target object is null: " + targetRouterClass.clz + "#" + m.getName());
							}
							Object[] args = methodWithArgs._2;
							Result result = (Result)m.invoke(obj, args);
							Produces produces = methodWithArgs._1.getAnnotation(Produces.class);
							return produces != null ? new WrapProducer(produces.value()[0], result) : result;
						} catch (InvocationTargetException cause) {
							System.err.println("Exception occured while trying to invoke: " + targetRouterClass.clz.getName()
									+ "#" + methodWithArgs._1.getName() + " with " + methodWithArgs._2 + " for uri:" + r.path());
							throw new RuntimeException(cause.getCause());
						} catch (Exception e) {
							e.printStackTrace();
							throw new RuntimeException(e.getCause());
						}
					}
				};

				JavaActionBridge handler = new JavaActionBridge(targetRouterClass.clz, methodWithArgs._1, resultBuilder);
				return handler;
			}
		}
		return null;
	}

	/**
	 * @author Bing Ran (bing.ran@gmail.com)
	 * @param classloader
	 */
	public static void setClassLoader(ClassLoader classloader) {
		parentClassloader = classloader;
	}

}
