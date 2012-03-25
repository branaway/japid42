package controllers.more;

import cn.bran.play.JapidController;

public class BaseController extends JapidController {
    static void beforeHandler() {
    	System.out.println("in before handler");
    }
	
}
