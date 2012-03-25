package controllers.t3;

import play.mvc.Result;
import cn.bran.play.JapidController;
public class App extends JapidController {
	public static Result foo() {
		return renderText("hi foo  ");
	}
}
