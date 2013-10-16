/**
 * 
 */
package cn.bran.play.routing;

/**
 * @author bran
 *
 */
public class RouteEntry {
	public String verb, path, action;

	public RouteEntry(String verb, String path, String action) {
		super();
		this.verb = verb;
		this.path = path;
		this.action = action;
	}
	
	
}
