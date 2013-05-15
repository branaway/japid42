package cn.bran.japid.rendererloader;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

//import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;

import cn.bran.japid.exceptions.JapidTemplateException;
import cn.bran.japid.template.JapidRenderer;
import cn.bran.japid.util.DirUtil;
import cn.bran.japid.util.JapidFlags;
import cn.bran.japid.util.StringUtils;

/**
 * Java compiler (uses eclipse JDT)
 * 
 * based on
 */
public class RendererCompiler {

	Map<String, Boolean> packagesCache = new HashMap<String, Boolean>();
//	Map<String, RendererClass> classes;// = new HashMap<String,
										// RendererClass>();

	TemplateClassLoader crlr;

	Map<String, String> settings;
	{
		/**
		 * Try to guess the magic configuration options
		 */
		this.settings = new HashMap<String, String>();
		this.settings.put(CompilerOptions.OPTION_ReportMissingSerialVersion, CompilerOptions.IGNORE);
		this.settings.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.GENERATE);
		this.settings.put(CompilerOptions.OPTION_SourceFileAttribute, CompilerOptions.GENERATE);
		this.settings.put(CompilerOptions.OPTION_ReportDeprecation, CompilerOptions.IGNORE);
		this.settings.put(CompilerOptions.OPTION_ReportUnusedImport, CompilerOptions.IGNORE);
		this.settings.put(CompilerOptions.OPTION_Encoding, "UTF-8");
		this.settings.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.GENERATE);
		String javaVersion = CompilerOptions.VERSION_1_6;
		try {
		String version = System.getProperty("java.version");
		if (version.startsWith("1.6"))
			javaVersion = CompilerOptions.VERSION_1_6;
		else if(version.startsWith("1.7"))
			javaVersion = CompilerOptions.VERSION_1_7;
		else if(version.startsWith("1.5"))
			javaVersion = CompilerOptions.VERSION_1_5;
		} catch (Exception e) {}
		JapidFlags.log("compile Japid for JDK: " + javaVersion);
		this.settings.put(CompilerOptions.OPTION_Source, javaVersion);
		this.settings.put(CompilerOptions.OPTION_TargetPlatform, javaVersion);
		this.settings.put(CompilerOptions.OPTION_PreserveUnusedLocal, CompilerOptions.PRESERVE);
		this.settings.put(CompilerOptions.OPTION_Compliance, javaVersion);
	}

	/**
	 * supposed to have a single instance for the entire application
	 * 
	 * @param classes
	 * @param cl
	 */
	public RendererCompiler(Map<String, RendererClass> classes, TemplateClassLoader cl) {
//		this.classes = classes;
		this.crlr = cl;
	}

	/**
	 * @author bran
	 * 
	 */
	private final class CompilerRequestor implements ICompilerRequestor {
		@Override
		public void acceptResult(CompilationResult result) {
			// If error
			if (result.hasErrors()) {
				// bran: sort the problems and report the first one
				CategorizedProblem[] errors = result.getErrors();
				Arrays.sort(errors, new Comparator<CategorizedProblem>() {
					@Override
					public int compare(CategorizedProblem o1, CategorizedProblem o2) {
						return o1.getSourceLineNumber() - o2.getSourceLineNumber();
					}
				});

				for (IProblem problem : errors) {
					String className = new String(problem.getOriginatingFileName()).replace("/", ".");
					className = className.substring(0, className.length() - 5);
					String message = problem.getMessage();
					int line = problem.getSourceLineNumber();
					String srcFile = new String(problem.getOriginatingFileName());
					if (problem.getID() == IProblem.CannotImportPackage) {
						// Non sense !
						message = problem.getArguments()[0] + " cannot be resolved";
					}

					RendererClass rc = JapidRenderer.japidClasses.get(className);

//					if (rc.getScriptFile() == null)
//						throw new RuntimeException("no source file for compiling " + className);

					if (rc.getJapidSourceCode() == null)
						throw new RuntimeException("no original source code for compiling " + className);

					String descr = srcFile + "(" + line + "): " + message;

					int oriSrcLineNum = DirUtil.mapJavaLineToSrcLine(rc.getJavaSourceCode(), problem.getSourceLineNumber());
					String scriptPath = rc.getScriptPath();
					if (oriSrcLineNum > 0) {
						// has a original script marker
						descr = scriptPath + "(line " + oriSrcLineNum + "): " + message;
						JapidTemplateException te = new JapidTemplateException("Japid Compilation Error", descr,
								oriSrcLineNum, scriptPath, rc.getJapidSourceCode());
						throw te;
					} else {
						JapidTemplateException te = new JapidTemplateException("Japid Compilation Error", descr, line,
								srcFile, rc.getJavaSourceCode());
						throw te;

					}
				}
			}

			// Something has been compiled
			ClassFile[] clazzFiles = result.getClassFiles();
			for (int i = 0; i < clazzFiles.length; i++) {
				final ClassFile clazzFile = clazzFiles[i];
				final char[][] compoundName = clazzFile.getCompoundName();
				final StringBuffer clazzName = new StringBuffer();
				for (int j = 0; j < compoundName.length; j++) {
					if (j != 0) {
						clazzName.append('.');
					}
					clazzName.append(compoundName[j]);
				}
				byte[] bytes = clazzFile.getBytes();
				JapidFlags.log("compiled: " + clazzName);
				// XXX address anonymous inner class issue!! ....$1...
				String cname = clazzName.toString();
				RendererClass rc = JapidRenderer.japidClasses.get(cname);
				if (rc == null) {
					if (cname.contains("$")) {
						// inner class
						rc = new RendererClass();
						rc.className = cname;
						JapidRenderer.japidClasses.put(cname, rc);
					} else {
						throw new RuntimeException("name not in the classes container: " + cname);
					}
				}
				rc.setBytecode(bytes);
				rc.setClz(null);
				rc.setLastCompiled(System.currentTimeMillis());
			}
		}
	}

	/**
	 * @author bran
	 * 
	 */
	private final class NameEnv implements INameEnvironment {
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
//						System.out.println("japid: byecode found: " + name);
						return new NameEnvironmentAnswer(new ClassFileReader(bytes, fileName, true), null);
					}
					else {
//						System.out.println("japid: bytes not found: " + name);
					}
				} else { // japidviews
					RendererClass applicationClass = JapidRenderer.japidClasses.get(name);

					// ApplicationClass exists
					if (applicationClass != null) {

						byte[] bytecode = applicationClass.getBytecode();
						if (bytecode != null) {
							return new NameEnvironmentAnswer(new ClassFileReader(bytecode, fileName, true), null);
						}
						else
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

	/**
	 * Something to compile
	 */
	final class CompilationUnit implements ICompilationUnit {

		final private String clazzName;
		final private String fileName;
		final private char[] typeName;
		final private char[][] packageName;

		CompilationUnit(String pClazzName) {
			clazzName = pClazzName;
			if (pClazzName.contains("$")) {
				pClazzName = pClazzName.substring(0, pClazzName.indexOf("$"));
			}
			fileName = pClazzName.replace('.', '/') + ".java";
			int dot = pClazzName.lastIndexOf('.');
			if (dot > 0) {
				typeName = pClazzName.substring(dot + 1).toCharArray();
			} else {
				typeName = pClazzName.toCharArray();
			}
			StringTokenizer izer = new StringTokenizer(pClazzName, ".");
			packageName = new char[izer.countTokens() - 1][];
			for (int i = 0; i < packageName.length; i++) {
				packageName[i] = izer.nextToken().toCharArray();
			}
		}

		@Override
		public boolean ignoreOptionalProblems() {
			return false;
		}

		@Override
		public char[] getFileName() {
			return fileName.toCharArray();
		}

		@Override
		public char[] getContents() {
				return JapidRenderer.japidClasses.get(clazzName).getJavaSourceCode().toCharArray();
		}

		@Override
		public char[] getMainTypeName() {
			return typeName;
		}

		@Override
		public char[][] getPackageName() {
			return packageName;
		}
	}

	@SuppressWarnings("deprecation")
	public void compile(String[] classNames) {

		ICompilationUnit[] compilationUnits = new CompilationUnit[classNames.length];
		for (int i = 0; i < classNames.length; i++) {
			compilationUnits[i] = new CompilationUnit(classNames[i]);
		}

		IErrorHandlingPolicy policy = DefaultErrorHandlingPolicies.exitOnFirstError();
		IProblemFactory problemFactory = new DefaultProblemFactory(Locale.ENGLISH);
		INameEnvironment nameEnvironment = new NameEnv();
		ICompilerRequestor compilerRequestor = new CompilerRequestor();

		Compiler jdtCompiler = new Compiler(nameEnvironment, policy, settings, compilerRequestor, problemFactory) {

			@Override
			protected void handleInternalException(Throwable e, CompilationUnitDeclaration ud, CompilationResult result) {
			}
		};

		jdtCompiler.compile(compilationUnits);

	}
}
