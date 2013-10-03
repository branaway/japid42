/**
 * 
 */
package cn.bran.japid.rendererloader;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;

import cn.bran.japid.template.JapidRenderer;
import cn.bran.japid.util.JapidFlags;
import cn.bran.japid.util.StringUtils;

/**
 * @author bran
 * 
 */
final class NameEnv implements INameEnvironment {

	private Map<String, Boolean> packagesCache = new HashMap<String, Boolean>();
	private TemplateClassLoader crlr;

	/**
	 * @param rendererCompiler
	 */
	NameEnv(TemplateClassLoader ldr) {
		this.crlr = ldr;
	}

	@Override
	public NameEnvironmentAnswer findType(final char[][] compoundTypeName) {
		return findType(StringUtils.join(compoundTypeName, "."));
	}

	@Override
	public NameEnvironmentAnswer findType(final char[] typeName, final char[][] packageName) {
		return findType(StringUtils.join(packageName, ".") + "." + new String(typeName));
	}

	private NameEnvironmentAnswer findType(final String name) {
		char[] fileName = name.toCharArray();

		try {
			if (!name.startsWith("japidviews.")) {
				// let super class loader to load the bytecode
				byte[] bytes = crlr.getClassDefinition(name);
				if (bytes != null) {
					// System.out.println("japid: byecode found: " + name);
					return new NameEnvironmentAnswer(new ClassFileReader(bytes, fileName, true), null);
				} else {
					// System.out.println("japid: bytes not found: " + name);
				}
			} else { // japidviews
				RendererClass applicationClass = JapidRenderer.japidClasses.get(name); // I don't like this XXX

				// ApplicationClass exists
				if (applicationClass != null) {

					byte[] bytecode = applicationClass.getBytecode();
					if (bytecode != null) {
						return new NameEnvironmentAnswer(new ClassFileReader(bytecode, fileName, true), null);
					} else
						// Cascade compilation
						return new NameEnvironmentAnswer(new CompilationUnit(name), null);
				}
			}
			return null;
		} catch (ClassFormatException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isPackage(char[][] parentPackageName, char[] packageName) {
		String name = StringUtils.join(parentPackageName, ".") + "." + new String(packageName);
		if (packagesCache.containsKey(name)) {
			return packagesCache.get(name).booleanValue();
		}
		// Check if there is a .java or .class for this resource
		if (crlr.getClassDefinition(name) != null) {
			packagesCache.put(name, false);
			return false;
		}
		if (JapidRenderer.japidClasses.get(name) != null) {
			packagesCache.put(name, false);
			return false;
		}
		packagesCache.put(name, true);
		return true;
	}

	@Override
	public void cleanup() {
	}
}