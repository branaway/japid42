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
		JapidRenderer.init(app);
		JapidRenderer.addImportStatic(StringUtils.class);
		JapidRenderer.setLogVerbose(true);
//		JapidRenderer.gen();
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
	 * @see play.GlobalSettings#onRequest(play.mvc.Http.Request, java.lang.reflect.Method)
	 */
	@Override
	public Action onRequest(Request request, Method actionMethod) {
		return super.onRequest(request, actionMethod);
	}

}