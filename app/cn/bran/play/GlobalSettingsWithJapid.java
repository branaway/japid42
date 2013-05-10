/**
 * 
 */
package cn.bran.play;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import play.Application;
import play.GlobalSettings;
import play.api.mvc.Handler;
import play.cache.Cache;
import play.cache.Cached;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import cn.bran.japid.template.JapidRenderer;
import cn.bran.japid.util.JapidFlags;
import cn.bran.japid.util.StringUtils;


/**
 * @author bran
 *
 */
public class GlobalSettingsWithJapid extends GlobalSettings {
	private static final String NO_CACHE = "no-cache";
	private static String dumpRequest;
	public static Application _app;

	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param app
	 */
	@Override
	public void onStop(Application app) {
		if (app.isDev())
			JapidRenderer.persistJapidClasses();
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
		JapidRenderer.init(app.isDev(), app.configuration().asMap(), app.classloader());
		super.onStart(app);
		onStartJapid();
		if (JapidFlags.verbose) {
			JapidFlags.log("You can turn off Japid logging in the console by calling JapidRenderer.setLogVerbose(false) in the Global's onStartJapid() method.");
		}
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
		return new Action<Cached>() {
			public Result call(Context ctx) {
				try {
					beforeActionInvocation(ctx, actionMethod);

					Result result = null;
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
						result = (Result) Cache.get(key);
					}
					if (result == null) {
						result = delegate.call(ctx);
						if (!StringUtils.isEmpty(key) && duration > 0) {
							Cache.set(key, result, duration);
						}
					}
					onActionInvocationResult(ctx);
					return result;
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
					System.out.println("--- action ->: " + actionMethod.toString());
				} else {
					if (request.uri().matches(property)) {
						System.out.println("--- action ->: " + actionMethod.toString());
					}
				}
			}
		}

		String string = flash.get(RenderResultCache.READ_THRU_FLASH);
		if (string != null) {
			RenderResultCache.setIgnoreCache(true);
		} else {
			// cache-control in lower case, lowercase for some reason
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

	static private void getDumpRequest() {
		String property = _app.configuration().getString("japid.dump.request");
		dumpRequest = property;
	}

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
}

