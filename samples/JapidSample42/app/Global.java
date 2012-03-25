import java.lang.reflect.Method;

import play.Application;
import play.GlobalSettings;
import play.Play;
import play.mvc.Action;
import play.mvc.Http.Request;
import play.mvc.Result;
import cn.bran.japid.compiler.OpMode;
import cn.bran.japid.template.JapidRenderer;
import cn.bran.japid.util.StringUtils;

public class Global extends GlobalSettings {
	@Override
	public void onStart(Application app) {
		JapidRenderer.setParentClassLoader(app.classloader());
		JapidRenderer.init(Play.isDev()? OpMode.dev:  OpMode.prod, "japidroot", 2);
		JapidRenderer.addImportStatic(StringUtils.class);
		JapidRenderer.gen();
	}

	@Override
	public void onStop(Application app) {
		super.onStop(app);
	}

	/* (non-Javadoc)
	 * @see play.GlobalSettings#beforeStart(play.Application)
	 */
	@Override
	public void beforeStart(Application app) {
		super.beforeStart(app);
	}

	/* (non-Javadoc)
	 * @see play.GlobalSettings#onError(java.lang.Throwable)
	 */
	@Override
	public Result onError(Throwable t) {
		return super.onError(t);
	}

	/* (non-Javadoc)
	 * @see play.GlobalSettings#onRequest(play.mvc.Http.Request, java.lang.reflect.Method)
	 */
	@Override
	public Action onRequest(Request request, Method actionMethod) {
		return super.onRequest(request, actionMethod);
	}

	/* (non-Javadoc)
	 * @see play.GlobalSettings#onHandlerNotFound(java.lang.String)
	 */
	@Override
	public Result onHandlerNotFound(String uri) {
		return super.onHandlerNotFound(uri);
	}

	/* (non-Javadoc)
	 * @see play.GlobalSettings#onBadRequest(java.lang.String, java.lang.String)
	 */
	@Override
	public Result onBadRequest(String uri, String error) {
		return super.onBadRequest(uri, error);
	}

}