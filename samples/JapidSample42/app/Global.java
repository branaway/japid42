
import play.Application;
import play.GlobalSettings;
import cn.bran.japid.template.JapidRenderer;

public class Global extends GlobalSettings {
	@Override
	public void onStart(Application app) {
		JapidRenderer.init(app);
// there are more customization you can do to Japid
//		JapidRenderer.addImportStatic(StringUtils.class);
//		JapidRenderer.setLogVerbose(true);
	}

}