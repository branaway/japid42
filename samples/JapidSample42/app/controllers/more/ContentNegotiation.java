package controllers.more;

import play.mvc.*;

import cn.bran.play.JapidController;

public class ContentNegotiation extends JapidController {

    public static Result index() {
    	return renderJapid();
    }

    public static Result xml() {
    	return renderJapid();
    }
    
    public static Result json() {
    	return renderJapid();
    }
    
    public static Result xmld() {
    	return ok("<a><b>hello</b></a>").as("text/xml");
    }
    
}
