/**
 * 
 */
package cn.bran.play.routing;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import static org.reflections.ReflectionUtils.*;

import play.libs.F.Tuple;
import play.mvc.Result;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * @author bran
 * 
 */
public class RouterClass {
	String absPath;
	Pattern absPathPatternForValues;

	/**
	 * @param cl
	 */
	public RouterClass(Class<?> cl, String appPath) {
		clz = cl;
		path = cl.getAnnotation(Path.class).value();
		if (path.length() == 0) {
			// auto-pathing. using the class full name minus the "controller." part as the path
			String cname = cl.getName();
			if (cname.startsWith(JaxrsRouter.routerPackage + "."))
				cname = cname.substring((JaxrsRouter.routerPackage + ".").length());
			path = cname;
		}
		absPath = appPath + JaxrsRouter.prefixSlash(path);

		String r = absPath.replaceAll(JaxrsRouter.urlParamCapture, "(.*)");
		absPathPatternForValues = Pattern.compile(r);

		@SuppressWarnings("unchecked")
		Predicate<AnnotatedElement> and = Predicates.or(withAnnotation(GET.class),
				withAnnotation(POST.class), withAnnotation(PUT.class),
				withAnnotation(DELETE.class), withAnnotation(HEAD.class),
				withAnnotation(OPTIONS.class));
//		Set<Method> allMethods = getAllMethods(cl, and);
		// let's allow any methods
		Set<Method> allMethods = getAllMethods(cl, Predicates.and(withModifier(Modifier.STATIC), withReturnType(Result.class)));
		for (Method m : allMethods) {
			if (m.getDeclaringClass() == cl)
				routerMethods.add(new RouterMethod(m, absPath));
		}
	}

	public Tuple<Method,Object[]> findMethodAndGenerateArgs(play.mvc.Http.RequestHeader r) {
		String uri = r.path();

		String contentType = "";
		String[] ct = r.headers().get("Content-Type");
		if (ct != null && ct.length > 0)
			contentType = ct[0];
		
		for (RouterMethod m : routerMethods) {
			if (m.containsConsumeType(contentType) 
					&& m.supportHttpMethod(r.method())
					&& m.matchURI(uri)) {
				return new Tuple<Method, Object[]>(m.meth, m.extractArguments(r));
			}
		}
		return null;
	}

	Class<?> clz;
	private String path;
	List<RouterMethod> routerMethods = new ArrayList<RouterMethod>();
}
