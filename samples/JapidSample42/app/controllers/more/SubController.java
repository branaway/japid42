package controllers.more;

import play.mvc.Result;
import cn.bran.play.JapidController;

public class SubController extends JapidController {
	public static Result foo(String what) {
		return renderJapid(what);
	}
}
