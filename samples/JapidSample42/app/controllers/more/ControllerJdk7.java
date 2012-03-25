package controllers.more;

import play.mvc.Result;
import models.DataModel;

// make sure you have 
// 		module.japid=${play.path}/modules/japid-head
// in your application.conf file, and "play eclipsify"
// if you notice the JapidController is not found.

public class ControllerJdk7 extends BaseController {

	public static Result index() {
//		String a = "a";
//		switch (a) {
//		case "a":
//			System.out.println("a");
//			break;
//		default:
//			System.out.println("b");
//			break;
//		}
		return renderJapid("b");
	}
	
	public static Result a() {
		return renderJapid();
	}
	public static Result b() {
		return renderJapid();
	}
	public static Result c() {
		return renderJapid();
	}
	public static Result d() {
		return renderJapid();
	}
	public static Result e() {
		return renderJapid();
	}
}
