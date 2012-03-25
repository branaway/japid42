package controllers.more;

import play.mvc.Result;
import cn.bran.play.JapidController;
import models.DataModel;

// make sure you have 
// 		module.japid=${play.path}/modules/japid-head
// in your application.conf file, and "play eclipsify"
// if you notice the JapidController is not found.

public class Perf extends JapidController {
	public static Result perf() {
		DataModel model = DataModel.dataModel;
		DataModel.User loggedInUser = model.getLoggedInUser();

//		renderArgs.put("title", "Entries");
//		renderArgs.put("loggedInUser", loggedInUser);
//		renderArgs.put("entries", model.getEntries());

		return renderJapid("Entries", loggedInUser, model.getEntries());
	}
}
