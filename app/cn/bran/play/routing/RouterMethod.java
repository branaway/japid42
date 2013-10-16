/**
 * 
 */
package cn.bran.play.routing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import cn.bran.japid.util.StringUtils;

public class RouterMethod {
	private boolean autoRouting;
	private String withExtension = ""; // the artificial url extension such as .html
	List<Annotation> httpMethodAnnotations = new ArrayList<Annotation>();
	Method meth;
	String pathSpec;
	Pattern pathSpecPattern;
	public Pattern valueExtractionPattern;
	String produce;
	String[] consumeTypes = new String[] {};
	List<ParamSpec> paramSpecList = new ArrayList<ParamSpec>();

	/**
	 * 
	 * no method annotation means taking "any"
	 * 
	 * @param m
	 */
	public RouterMethod(Method m, String pathPrefix) {
		Annotation[] annotations = m.getAnnotations();
		for (Annotation a : annotations) {
			if (a instanceof GET || a instanceof POST || a instanceof PUT || a instanceof DELETE || a instanceof HEAD
					|| a instanceof OPTIONS)
				httpMethodAnnotations.add(a);
		}
		meth = m;
		Consumes consumes = m.getAnnotation(Consumes.class);
		if (consumes != null) {
			consumeTypes = consumes.value();
		}

		OptionalExt artExt = m.getAnnotation(OptionalExt.class);
		if (artExt != null)
			this.withExtension = artExt.value();

		Annotation[][] parameterAnnotations = m.getParameterAnnotations();
		Class<?>[] paramTypes = m.getParameterTypes();
		// now parse the path spec
		Path p = m.getAnnotation(Path.class);
		if (p != null && p.value().length() > 0) {
			pathSpec = pathPrefix + JaxrsRouter.prefixSlash(p.value());
			pathSpecPattern = Pattern.compile(pathSpec.replaceAll(JaxrsRouter.urlParamCapture, "\\\\{(.*)\\\\}"));

			if (pathSpec.contains("{") && pathSpec.contains("}")) {
				List<RegMatch> rootParamNameMatches = RegMatch.findAllMatchesIn(pathSpecPattern, pathSpec);
				List<String> rootParamNames = new ArrayList<String>();
				for (RegMatch rm : rootParamNameMatches) {
					rootParamNames.addAll(rm.subgroups);
				}
				for (String s : rootParamNames) {
					if (s != null)
						paramSpecList.add(new ParamSpec(s));
				}

				if (parameterAnnotations.length < paramSpecList.size()) {
					throw new RuntimeException(
							"param number does not match that of the param captures in the path annotation pattern");
				}
				int pc = 0;
				for (Annotation[] paramAnnos : parameterAnnotations) {
					if (paramAnnos.length == 0 && !autoRouting) {
						throw new RuntimeException(
								"in none-auto-routing mode: no capturing annotations (@PathParam/@QueryParam) for the parameter at the method position: "
										+ m.getName() + ":" + pc);
					}

					boolean hasCapture = false;
					for (Annotation ann : paramAnnos) {
						if (ann instanceof PathParam) {
							String pname = ((PathParam) ann).value();
							boolean hasName = false;
							// fill the path param type
							for (ParamSpec pspec : paramSpecList) {
								if (pspec.name.equals(pname)) {
									Class<?> type = paramTypes[pc];
									pspec.type = type;
									hasName = true;
									hasCapture = true;
									break;
								}
							}
							if (!hasName) {
								throw new RuntimeException(
										"no capturing spec on the Path annotation for the parameter at the method position: "
												+ m.getName() + ":" + pc);
							}
						} else if (ann instanceof QueryParam) {
							hasCapture = true;
						} else {
							// hmm something unknown
						}
					}
					if (!hasCapture) {
						throw new RuntimeException(
								"no capturing annotations(@PathParam/@QueryParam) for the parameter at the method position: "
										+ m.getName() + ":" + pc);
					}
					pc++;
				}
				// check that all path param has been set up with proper types
				for (ParamSpec pspec : paramSpecList) {
					if (pspec.type == null) {
						throw new RuntimeException("cannot match the path param with the parameter list of method: "
								+ this.meth);
					}
				}

			}
		} else {
			// auto-routing mechanism:
			// 1. use method name as the first part
			this.autoRouting = true;
			pathSpec = pathPrefix + "\\." + m.getName();

			int pos = 0; // path param position
			int ppos = 0; // natural parameter
			for (Annotation[] pa : parameterAnnotations) {
				boolean isQueryParam = false;
				for (Annotation a : pa) {
					if (a instanceof PathParam) {
						error("cannot take @PathParam when no @Path specified to the method: " + m.getName());
					} else if (a instanceof QueryParam) {
						isQueryParam = true;
						break;
					} else {
					}
				}
				if (!isQueryParam) {
					// decorate a PathParam
					String s = "_" + pos++;
					ParamSpec ps = new ParamSpec(s);
					ps.type = paramTypes[ppos];
					paramSpecList.add(ps);
					pathSpec += "/" + "{" + s + "}";
				}
				ppos++;
			}
			pathSpecPattern = Pattern.compile(pathSpec.replaceAll(JaxrsRouter.urlParamCapture, "\\\\{(.*)\\\\}"));
		}

		valueExtractionPattern = Pattern.compile(pathSpec.replaceAll(JaxrsRouter.urlParamCapture, "(.*)"));

		Produces produces = m.getAnnotation(Produces.class);
		if (produces == null)
			produce = null;

		else if (produces.value().length != 1) {
			throw new RuntimeException("Currently the PRODUCES annotation can only take one type of result.");
		} else {
			produce = produces.value()[0];
		}

	}

	/**
	 * @author Bing Ran (bing.ran@gmail.com)
	 * @param string
	 */
	private static void error(String string) {
		throw new RuntimeException(string);
	}

	public Object[] extractArguments(play.mvc.Http.RequestHeader r) {
		String path = r.path();
		if (path.endsWith(withExtension))
			path = path.substring(0, path.lastIndexOf(withExtension));

		List<RegMatch> rootParamValueMatches = RegMatch.findAllMatchesIn(valueExtractionPattern, path);
		List<String> rootParamValues = new ArrayList<String>();
		for (RegMatch rm : rootParamValueMatches) {
			rootParamValues.addAll(rm.subgroups);
		}

		if (rootParamValues.size() != paramSpecList.size()) {
			throw new RuntimeException("param spec number does not match that from URI capturing. Spec contains: "
					+ paramSpecList.size() + " while the URI contains: " + rootParamValues.size() + ". The route entry is: " + this.toString());
		}

		Map<String, Object> args = new java.util.HashMap<String, Object>();
		int c = 0;
		for (ParamSpec paramSpec : paramSpecList) {
			String name = paramSpec.name;
			String value = rootParamValues.get(c++);
			if (!paramSpec.formatPattern.matcher(value).matches()) {
				throw new IllegalArgumentException("format mismatch for : (" + name + ")" + value
						+ ". The route entry is: " + this.toString());
			}

			Class<?> type = paramSpec.type;
			Object val = convertArgType(c, name, value, type);

			args.put(name, val);
		}

		//
		Object[] argValues = new Object[0];
		List<Object> argVals = new ArrayList<Object>();
		Annotation[][] annos = meth.getParameterAnnotations();

		c = 0;
		int pos = 0;
		for (Annotation[] ans : annos) {
			PathParam pathParam = null;
			QueryParam queryParam = null;

			for (Annotation an : ans) {
				if (an instanceof PathParam)
					pathParam = (PathParam) an;
				else if (an instanceof QueryParam)
					queryParam = (QueryParam) an;
			}
			if (pathParam != null) {
				Object v = args.get(pathParam.value());
				if (v != null)
					argVals.add(v);
				else
					throw new IllegalArgumentException("can not find annotation value for argument "
							+ pathParam.value() + "in " + meth.getDeclaringClass() + "#" + meth);
			} else if (queryParam != null) {
				String name = queryParam.value();
				String queryString = r.getQueryString(name); // XXX should this
																// be of
																// String[]?
				argVals.add(convertArgType(c, name, queryString, meth.getParameterTypes()[c]));
			} else if (autoRouting) {
				Object v = args.get("_" + pos++);
				if (v != null)
					argVals.add(v);
				else
					throw new IllegalArgumentException("can not find value for param No. " + c + " in "
							+ meth.getDeclaringClass() + "#" + meth);
			} else
				throw new IllegalArgumentException("can not find how to map the value for an argument for method:"
						+ meth.getDeclaringClass() + "#" + meth + ". The parameter position is(0-based): " + c);
			c++;
		}
		argValues = argVals.toArray(argValues);
		return argValues;

	}

	private Object convertArgType(int c, String name, String value, Class<?> type) {
		Object val = null;
		if (type == Boolean.class || type == boolean.class) {
			val = Boolean.valueOf(value);
		} else if (type == Byte.class || type == byte.class) {
			val = Byte.valueOf(value);
		} else if (type == Character.class || type == char.class) {
			if (value.length() != 1) {
				throw new IllegalArgumentException("cannot convert to a character: (" + name + ")" + value);
			}
			val = value.charAt(0);
		} else if (type == Double.class || type == double.class) {
			val = Double.valueOf(value);
		} else if (type == Float.class || type == float.class) {
			val = Float.valueOf(value);
		} else if (type == Integer.class || type == int.class) {
			val = Integer.valueOf(value);
		} else if (type == Long.class || type == long.class) {
			val = Long.valueOf(value);
		} else if (type == Short.class || type == short.class) {
			val = Short.valueOf(value);
		} else if (type == String.class) {
			// try {
			// val = URLDecoder.decode(value, "UTF-8");// not necessary. Seems
			// already decoded
			val = value;
			// } catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
			// }
		} else {
			throw new RuntimeException(
					"this version supports capturing primitive parameters, their object wrappers or strings. This param is not of primitive type: "
							+ meth.getName() + ":" + c + "(0-based)");
		}
		return val;
	}


	/**
	 * @author Bing Ran (bing.ran@gmail.com)
	 * @param contentType
	 * @return
	 */
	public boolean containsConsumeType(String contentType) {
		if (consumeTypes.length == 0) {
			return true;
		} else {
			for (String c : consumeTypes) {
				if (c.equals(contentType))
					return true;
			}
		}
		return false;
	}

	/**
	 * @author Bing Ran (bing.ran@gmail.com)
	 * @param uri
	 * @return
	 */
	public boolean matchURI(String uri) {
		if (pathSpec.equals(uri))
			return true;
		else
			return valueExtractionPattern.matcher(uri).matches();
	}

	public boolean supportHttpMethod(String ms) {
		Class<? extends Annotation> httpMethodClass = RouterUtils.findHttpMethodAnnotation(ms.toUpperCase());
		if (httpMethodAnnotations.size() == 0)
			return true; // take any

		for (Annotation a : httpMethodAnnotations) {
			if (httpMethodClass.isInstance(a))
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		String meths = getVerb();
		String path = getPath();
		String action = getAction();
		return meths + " \t" + path + "\t -> \t" + action;
	}

	private String getAction() {
		return meth.getDeclaringClass().getCanonicalName() + "." + meth.getName() + "(" + getParamListString() + ")" + (produce != null? " : " + produce : "");
	}

	private String getPath() {
		return pathSpec.replaceAll("\\\\", "") + withExtension;
	}

	private String getVerb() {
		String meths = "";
		for (Annotation an : httpMethodAnnotations) {
			String string = an.toString();
			// clean it
			for (int i = 0; i < string.length(); i++) {
				if (Character.isUpperCase(string.charAt(i)))
					meths += string.charAt(i);
			}
			meths += "|";
		}
		if (meths.endsWith("|"))
			meths = meths.substring(0, meths.length() - 1);
		else
			meths = "*";
		return meths;
	}
	
	private String getParamListString() {
		return StringUtils.join(paramSpecList, ",");
	}

	/**
	 * @author Bing Ran (bing.ran@gmail.com)
	 * @return
	 */
	public RouteEntry getRouteEntry() {
		return new RouteEntry(getVerb(), getPath(), getAction());
	}
}