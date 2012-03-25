package controllers.more;
import java.util.Date;

import play.mvc.Result;
import cn.bran.play.JapidController;

public class Portlets extends JapidController {
//	@CacheFor("20s")
	public static Result index() {
		return renderJapid("a", "b");
	}

	public static Result panel1(String a) {
		System.out.println("panel1 called");
		return renderJapid(a);
	}

	public static Result panel2(String b) {
		return renderJapid(b);
	}

//	@CacheFor("5s")
	public static Result panel3(String whatever) {
		System.out.println("panel3 called");
		return renderText("<div>" + new Date() + "</div>");
	}
}
