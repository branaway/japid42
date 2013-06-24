package cn.bran.japid.rendererloader;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import static org.eclipse.jdt.internal.compiler.impl.CompilerOptions.*;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;

import cn.bran.japid.template.JapidRenderer;
import cn.bran.japid.util.JapidFlags;

/**
 * Java compiler (uses eclipse JDT)
 * 
 * based on
 */
public class RendererCompiler {


	Compiler jdtCompiler;
	
		

	/**
	 * supposed to have a single instance for the entire application
	 * 
	 * @param classes
	 * @param cl
	 */
	@SuppressWarnings("deprecation")
	public RendererCompiler(Map<String, RendererClass> classes, TemplateClassLoader cl) {
		/**
		 * Try to guess the magic configuration options
		 */
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(OPTION_ReportMissingSerialVersion, IGNORE);
		settings.put(OPTION_LineNumberAttribute, GENERATE);
		settings.put(OPTION_SourceFileAttribute, GENERATE);
		settings.put(OPTION_ReportDeprecation, IGNORE);
		settings.put(OPTION_ReportUnusedImport, IGNORE);
		settings.put(OPTION_Encoding, "UTF-8");
		settings.put(OPTION_LocalVariableAttribute, GENERATE);

		String javaVersion = VERSION_1_6;
		try {
			String version = System.getProperty("java.version");
			if (version.startsWith("1.6"))
				javaVersion = VERSION_1_6;
			else if(version.startsWith("1.7"))
				javaVersion = VERSION_1_7;
			else if(version.startsWith("1.5"))
				javaVersion = VERSION_1_5;
		} catch (Exception e) {}
		JapidFlags.log("would compile Japid for JDK version: " + javaVersion);
		
		
		settings.put(OPTION_Source, javaVersion);
		settings.put(OPTION_TargetPlatform, javaVersion);
		settings.put(OPTION_PreserveUnusedLocal, PRESERVE);
		settings.put(OPTION_Compliance, javaVersion);
		jdtCompiler = new Compiler(
				new NameEnv(cl), 
				DefaultErrorHandlingPolicies.exitOnFirstError(), 
				settings, 
				new CompilerRequestor(), 
				new DefaultProblemFactory(Locale.ENGLISH)) {
			@Override
			protected void handleInternalException(Throwable e, CompilationUnitDeclaration ud, CompilationResult result) {
				e.printStackTrace();
			}
		};
	}

	public void compile(String[] classNames) {

		ICompilationUnit[] compilationUnits = new CompilationUnit[classNames.length];
		for (int i = 0; i < classNames.length; i++) {
			compilationUnits[i] = new CompilationUnit(classNames[i]);
		}


		jdtCompiler.compile(compilationUnits);
		JapidRenderer.persistJapidClassesLater();
	}

	/**
	 * @author Bing Ran (bing.ran@gmail.com)
	 * @param scriptNames
	 */
	public void compile(List<String> scriptNames) {
		String[] names = new String[scriptNames.size()];
		compile(scriptNames.toArray(names));
	}
}
