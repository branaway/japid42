package controllers.more;

import play.mvc.Result;
import models.DataModel;

// make sure you have 
// 		module.japid=${play.path}/modules/japid-head
// in your application.conf file, and "play eclipsify"
// if you notice the JapidController is not found.

public class MyController extends BaseController {

	public static Result index() {
		return renderJapid("Hello world!", 123);
	}

	public static Result echo(String m) {
//		validation.required("m", m);
		return renderJapid("m", 123);
	}

	public static Result subview() {
		return renderJapid("subviews....");
	}

	public static Result quickview() {
		return renderJapid();
	}

	public static Result scriptline() {
		return renderJapid();
	}

	public static Result doBodyInDef() {
		return renderJapid();
	}

	
	public static Result dobodytest() {
		return renderJapid();
	}
	
	
}
