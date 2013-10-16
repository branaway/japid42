/**
 * 
 */
package cn.bran.play.routing;

import java.util.regex.Pattern;

/**
 * The format of the expression is: "{" variable-name [ ":"
 * regular-expression ] "}" The regular-expression part is optional. When
 * the expression is not provided, it defaults to a wildcard matching of one
 * particular segment. In regular-expression terms, the expression defaults
 * to "([]*)"
 * 
 * @author bran
 * 
 */
public class ParamSpec {
	Pattern formatPattern;
	String name;
	String format = "[^/]+"; // the default regex
	Class<?> type;

	/**
	 * @param s
	 */
	public ParamSpec(String s) {
		int i = s.indexOf(':');
		if (i > 0) {
			name = s.substring(0, i).trim();
			format = s.substring(++i).trim();
		} else {
			name = s.trim();
		}
		formatPattern = Pattern.compile(format);
	}
	
	@Override
	public String toString() {
		return type.getSimpleName() + " " + name;
	}
}