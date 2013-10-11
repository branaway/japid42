/**
 * 
 */
package cn.bran.play;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import play.Application;
import play.GlobalSettings;
import play.Play;
import play.api.mvc.Handler;
import play.cache.Cache;
import play.cache.Cached;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Http.RequestHeader;
import play.mvc.SimpleResult;
import cn.bran.japid.template.JapidRenderer;
import cn.bran.japid.util.JapidFlags;
import cn.bran.japid.util.StringUtils;
import cn.bran.play.routing.JaxrsRouter;
import play.libs.F.Promise;

/**
 * @author bran
 * 
 */
public class GlobalSettingsWithJapid extends GlobalSettings {
	/**
	 * 
	 */
	public static final String ACTION_METHOD = "__actionMethod";
	private static final String NO_CACHE = "no-cache";
	private static String dumpRequest;
	public static Application _app;
	private boolean cacheResponse = true; // support @Cache annotation
	private boolean useJaxrs = true;

	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param app
	 */
	@Override
	public void onStop(Application app) {
		if (app.isDev())
			JapidRenderer.persistJapidClasses();
		JapidRenderer.shutdown();
		JapidFlags.debug("Cache persister shut down and recycled.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see play.GlobalSettings#onStart(play.Application)
	 */
	@Override
	public void onStart(Application app) {
		_app = app;
		JapidRenderer.usePlay = true;
		JapidRenderer.setAppPath(Play.application().path().getPath());
		super.onStart(app);
		onStartJapid();
		JapidRenderer.init(app.isDev(), app.configuration().asMap(), app.classloader());
		JaxrsRouter.setClassLoader(app.classloader());
		// if (JapidFlags.verbose) {
		// JapidFlags.log("You can turn off Japid logging in the console by calling JapidRenderer.setLogVerbose(false) in the Global's onStartJapid() method.");
		// }

		JaxrsRouter.init(app, this);
		JapidFlags.printLogLevel();
		JapidFlags.warn("==== Route table derived from jaxRS annotations: ====");
		String tab = JaxrsRouter.getRouteTable();
		String[] tabs = tab.split("\n");
		for (String t : tabs) {
			JapidFlags.warn(t);
		}
		JapidFlags.warn("====================================================");
	}

	/**
	 * sub classes do more customization of Japid here
	 * 
	 * @author Bing Ran (bing.ran@hotmail.com)
	 */
	public void onStartJapid() {
	};

	@Override
	public Action<?> onRequest(Request request, final Method actionMethod) {
		final String actionName = actionMethod.getDeclaringClass().getName() + "." + actionMethod.getName();
		final Map<String, String> threadData = JapidController.threadData.get();
		if (!cacheResponse) {
			return new Action.Simple() {
				public Promise<SimpleResult> call(Context ctx) throws Throwable {
					// pass the FQN to the japid controller to determine the
					// template to use
					// will be cleared right when the value is retrieved in the
					// japid controller
					// assuming the delegate call will take place in the same
					// thread
					threadData.put(ACTION_METHOD, actionName);
					Promise<SimpleResult> call = delegate.call(ctx);
					threadData.remove(ACTION_METHOD);
					return call;
				}
			};
		}

		return new Action<Cached>() {
			public Promise<SimpleResult> call(Context ctx) {
				try {
					beforeActionInvocation(ctx, actionMethod);

					SimpleResult result = null;
					Request req = ctx.request();
					String method = req.method();
					int duration = 0;
					String key = null;
					Cached cachAnno = actionMethod.getAnnotation(Cached.class);
					// Check the cache (only for GET or HEAD)
					if ((method.equals("GET") || method.equals("HEAD")) && cachAnno != null) {
						key = cachAnno.key();
						if ("".equals(key) || key == null) {
							key = "urlcache:" + req.uri() + ":" + req.queryString();
						}
						duration = cachAnno.duration();
						result = (SimpleResult) Cache.get(key);
					}
					if (result == null) {
						// pass the action name hint to japid controller
						threadData.put(ACTION_METHOD, actionName);
						Promise<SimpleResult> ps = delegate.call(ctx);
						threadData.remove(ACTION_METHOD);

						if (!StringUtils.isEmpty(key) && duration > 0) {
							result = ps.get(1, TimeUnit.MILLISECONDS);
							Cache.set(key, result, duration);
						}
						onActionInvocationResult(ctx);
						return ps;
					} else {
						onActionInvocationResult(ctx);
						return Promise.pure(result);
					}

				} catch (RuntimeException e) {
					throw e;
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
			}

		};

		// return new Action.Simple() {
		// public Result call(Context ctx) throws Throwable {
		// beforeActionInvocation(ctx, actionMethod);
		// dumpIt(ctx.request(), actionMethod);
		// Result call = delegate.call(ctx);
		// onActionInvocationResult(ctx);
		// return call;
		// }
		// };
	}

	@Override
	public Handler onRouteRequest(RequestHeader request) {
		if (useJaxrs) {
			if (_app.isDev())
				JapidFlags.info("route with Japid router");

			Handler handlerFor = JaxrsRouter.handlerFor(request);
			if (handlerFor == null) {
				handlerFor = super.onRouteRequest(request);
				if (handlerFor == null && _app.isDev()) {
					JapidFlags.warn("Japid router could not route the request: " + request.toString());
				}
			}
			return handlerFor;
		} else
			return super.onRouteRequest(request);
	}

	public void onActionInvocationResult(Context ctx) {

		play.mvc.Http.Flash fl = ctx.flash();
		if (RenderResultCache.shouldIgnoreCacheInCurrentAndNextReq()) {
			fl.put(RenderResultCache.READ_THRU_FLASH, "yes");
		} else {
			fl.remove(RenderResultCache.READ_THRU_FLASH);
		}

		// always reset the flag since the thread may be reused for another
		// request processing
		RenderResultCache.setIgnoreCacheInCurrentAndNextReq(false);
	}

	public static void beforeActionInvocation(Context ctx, Method actionMethod) {
		Request request = ctx.request();
		play.mvc.Http.Flash flash = ctx.flash();
		Map<String, String[]> headers = request.headers();

		String property = dumpRequest;
		if (property != null && property.length() > 0) {
			if (!"false".equals(property) && !"no".equals(property)) {
				if ("yes".equals(property) || "true".equals(property)) {
					JapidFlags.log("action ->: " + actionMethod.toString());
				} else {
					if (request.uri().matches(property)) {
						JapidFlags.log("action ->: " + actionMethod.toString());
					}
				}
			}
		}

		String string = flash.get(RenderResultCache.READ_THRU_FLASH);
		if (string != null) {
			RenderResultCache.setIgnoreCache(true);
		} else {
			// cache-control in lower case, lower-case for some reason
			String[] header = headers.get("cache-control");
			if (header != null) {
				List<String> list = Arrays.asList(header);
				if (list.contains(NO_CACHE)) {
					RenderResultCache.setIgnoreCache(true);
				}
			} else {
				header = headers.get("pragma");
				if (header != null) {
					List<String> list = Arrays.asList(header);
					if (list.contains(NO_CACHE)) {
						RenderResultCache.setIgnoreCache(true);
					}
				} else {
					// just in case
					RenderResultCache.setIgnoreCacheInCurrentAndNextReq(false);
				}
			}
		}
	}

	// static private void getDumpRequest() {
	// String property = _app.configuration().getString("japid.dump.request");
	// dumpRequest = property;
	// }

	public void addImport(String imp) {
		JapidRenderer.addImport(imp);
	}

	public void addImport(Class<?> cls) {
		JapidRenderer.addImport(cls);
	}

	public void addImportStatic(String imp) {
		JapidRenderer.addImportStatic(imp);
	}

	public void addImportStatic(Class<?> cls) {
		JapidRenderer.addImportStatic(cls);
	}

	public void setKeepJavaFiles(boolean keepJava) {
		JapidRenderer.setKeepJavaFiles(keepJava);
	}

	public void setLogVerbose(boolean verb) {
		JapidRenderer.setLogVerbose(verb);
	}

	public static void setTemplateRoot(String... root) {
		JapidRenderer.setTemplateRoot(root);
	}

	public static void setRefreshInterval(int i) {
		JapidRenderer.setRefreshInterval(i);
	}

	public void setCacheResponse(boolean c) {
		this.cacheResponse = c;
	}

	/**
	 * set the switch to present a Japid error in pretty HTML page, or it throws
	 * an exception. The default is true;
	 * 
	 * @author Bing Ran (bing.ran@gmail.com)
	 * @param presentErrorInHtml
	 */
	public void setPresentErrorInHtml(boolean presentErrorInHtml) {
		JapidRenderer.setPresentErrorInHtml(presentErrorInHtml);
	}

	/**
	 * controls if Just-In-Time persisting of the Japid classes is enabled.
	 * 
	 * @param enableJITCachePersistence
	 *            the enableJITCachePersistence to set
	 */
	public static void setEnableJITCachePersistence(boolean enableJITCachePersistence) {
		JapidRenderer.setEnableJITCachePersistence(enableJITCachePersistence);
	}

	/**
	 * set the folder to save the japid classes cache file
	 * 
	 * @param classCacheRoot
	 *            the classCacheRoot to set
	 */
	public static void setClassCacheRoot(String classCacheRoot) {
		JapidRenderer.setClassCacheRoot(classCacheRoot);
	}

	/**
	 * @return the useJaxrs
	 */
	public boolean isUseJaxrs() {
		return useJaxrs;
	}

	/**
	 * set to use Jax-RS protocol based routing mechanism
	 * 
	 * @param useJaxrs
	 *            the useJaxrs to set
	 */
	public void setUseJapidRouting(boolean useJaxrs) {
		this.useJaxrs = useJaxrs;
	}

	/**
	 * tell Japid engine to search for Japid scripts from classpath only
	 * 
	 * @author Bing Ran (bing.ran@gmail.com)
	 */
	public void setScanClasspathOnly() {
		setTemplateRoot((String[]) null);
	}
}
