
import play.Application;
import play.GlobalSettings;
import cn.bran.japid.template.JapidRenderer;

public class Global extends GlobalSettings {
	@Override
	public void onStart(Application app) {
		JapidRenderer.init(app);
// there are more customization you can do to Japid
		JapidRenderer.addImport("japidviews._layouts.*");
		JapidRenderer.addImport("japidviews._tags.*");

//		JapidRenderer.setLogVerbose(true);
	}

	
}