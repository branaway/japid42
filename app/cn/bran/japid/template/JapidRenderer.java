package cn.bran.japid.template;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import play.Application;
import cn.bran.japid.compiler.JapidTemplateTransformer;
import cn.bran.japid.compiler.OpMode;
import cn.bran.japid.compiler.TranslateTemplateTask;
import cn.bran.japid.exceptions.JapidTemplateException;
import cn.bran.japid.rendererloader.RendererClass;
import cn.bran.japid.rendererloader.RendererCompiler;
import cn.bran.japid.rendererloader.TemplateClassLoader;
import cn.bran.japid.util.DirUtil;
import cn.bran.japid.util.JapidFlags;
import cn.bran.japid.util.PlayDirUtil;
import cn.bran.japid.util.StackTraceUtils;
import cn.bran.japid.util.WebUtils;

public class JapidRenderer {
	public static String version = "0.5.1";
	/**
	 * 
	 */
	private static final String DEV_ERROR = "japidviews.devError";
	/**
	 * 
	 */
	private static final String DEV_ERROR_FILE = "/japidviews/devError.html";
	static HashSet<String> imports;
	private static ClassLoader parentClassLoader;
	private static boolean classesInited;
	static {
		imports = new HashSet<String>();
		addImportStatic(WebUtils.class);
	}
	
	public static JapidTemplateBaseWithoutPlay getRenderer(String name) {
		Class<? extends JapidTemplateBaseWithoutPlay> c = getClass(name);
		try {
			return c.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void addImport(String imp) {
		imports.add(imp);
	}

	public static void addImportStatic(String imp) {
		if (imp.startsWith("static"))
			imports.add(imp);
		else
			imports.add("static " + imp);
	}

	public static void addImport(Class<?> cls) {
		imports.add(cls.getName());
	}
	
	public static void addImportStatic(Class<?> cls) {
		imports.add("static " + cls.getName() + ".*");
	}
	
	/**
	 * Get a newly loaded class for the template renderer
	 * 
	 * @param name
	 * @return
	 */
	public static Class<? extends JapidTemplateBaseWithoutPlay> getClass(
			String name) {

		refreshClasses();

		return getClassWithoutRefresh(name);

	}
	
	private static Class<? extends JapidTemplateBaseWithoutPlay> getClassWithoutRefresh(
			String name) {
		RendererClass rc = japidClasses.get(name);
		if (rc == null)
			throw new RuntimeException("renderer class not found: " + name + ". Consider creating the Japid view file @: " + templateRoot + sep + name.replace('.', File.separatorChar) + ".html");
		else {
			if (playClassloaderChanged()) {
				// fall thru to reload all
			}
			else {
				try {
					Class<? extends JapidTemplateBaseWithoutPlay> cls = rc.getClz();
					if (cls != null) {
						return cls;
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		// always clear the mark to reload all
		for (String c : japidClasses.keySet()) {
			RendererClass rendererClass = japidClasses.get(c);
			rendererClass.setLastUpdated(0);
		}
		TemplateClassLoader classReloader = new TemplateClassLoader(parentClassLoader);
		try {
			@SuppressWarnings("unchecked")
			Class<JapidTemplateBaseWithoutPlay> loadClass = (Class<JapidTemplateBaseWithoutPlay>) classReloader
					.loadClass(name);
			rc.setClz(loadClass);
			return loadClass;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @return
	 */
	private static boolean playClassloaderChanged() {
		// for some reason even if the classloader remains the same, the classes are new
		if (opMode ==OpMode.prod)
			return false;
		
		parentClassLoader = _app.classloader();
		return true;
//		if (_app.classloader() != parentClassLoader) {
//			System.out.println("play classloader changed");
//			parentClassLoader = _app.classloader();
//			return true;
//		}
//		else {
//			System.out.println("play classloader not changed");
//			return false;
//		}
	}

	static boolean timeToRefresh() {
		if (opMode == OpMode.prod)
			return false;
		
		long now = System.currentTimeMillis();
		if (now - lastRefreshed > refreshInterval) {
			lastRefreshed = now;
			return true;
		} else
			return false;

	}

	static synchronized void refreshClasses() {
		if (!classesInited) {
			classesInited = true;
		}
		else {
			if (!timeToRefresh())
				return;
		}
		try {
			// find out all removed classes
			// XXX  just html? not xml, css, js?
			// XXX need to add other files as well. The old code is for plain text rendering only
			String[] allTemps = DirUtil.getAllTemplateFiles(new File(templateRoot));
			Set<String> currentClassesOnDir = createNameSet(allTemps);
			Set<String> allScriptNames = new HashSet<String>(currentClassesOnDir);

			Set<String> keySet = japidClasses.keySet();
			allScriptNames.removeAll(keySet); // got new templates

			removeRemoved(currentClassesOnDir, keySet);

			for (String c : allScriptNames) {
				RendererClass rc = newRendererClass(c);
				japidClasses.put(c, rc);
			}
			// now all the class set size is up to date

			// now update any Java source code
			List<File> gen = gen(templateRoot);

			// this would include both new and updated java
			Set<String> updatedClasses = new HashSet<String>();
			if (gen.size() > 0) {
				for (File f : gen) {
					String className = getClassName(f);
					updatedClasses.add(className);
					RendererClass rendererClass = japidClasses.get(className);
					if (rendererClass == null) {
						// this should not happen, since
						throw new RuntimeException("any new class names should have been in the classes container: " + className);
						// rendererClass = newRendererClass(className);
						// japidClasses.put(className, rendererClass);
					}
					
					setSources(rendererClass, f);
					removeInnerClasses(className);
					cleanClassHolder(rendererClass);
				}
			}

			// find all render class without bytecode
			for (Iterator<String> i = japidClasses.keySet().iterator(); i.hasNext();) {
				String k = i.next();
				RendererClass rc = japidClasses.get(k);
				if (rc.getSourceCode() == null) {
					if (!rc.getClassName().contains("$")) {
						String pathname = templateRoot + sep + k;
						pathname = pathname.replace(".", sep);
						File f = new File(pathname + ".java");
						setSources(rc, f);
						cleanClassHolder(rc);
						updatedClasses.add(k);
					} else {
						rc.setLastUpdated(0);
					}
				} else {
					if (rc.getBytecode() == null) {
						cleanClassHolder(rc);
						updatedClasses.add(k);
					}
				}
			}

			// compile all
			if (updatedClasses.size() > 0) {
				String[] names = new String[updatedClasses.size()];
				int i = 0;
				for (String s : updatedClasses) {
					names[i++] = s;
				}
				long t = System.currentTimeMillis();
				// newly compiled class bytecode bodies are set in the global classes set ready for defining
				compiler.compile(names);
				howlong("compile time for " + names.length + " classes", t);

				// clear the global defined class cache
				for (String k : japidClasses.keySet()) {
					RendererClass rc = japidClasses.get(k);
					rc.setClz(null);
				}
			}
		} 
//		catch (JapidCompilationException e) {
//			String tempName = e.getTemplateName();
//			if (tempName.startsWith(templateRoot)) {
//			}else {
//				tempName = templateRoot + File.separator + tempName;
//			}
//			VirtualFile vf = VirtualFile.fromRelativePath(tempName);
//			CompilationException ce = new CompilationException(vf, "\"" + e.getMessage() + "\"", e.getLineNumber(), 0, 0);
//			throw ce;
//		}
		catch (Exception e) {
			if (e instanceof JapidTemplateException)
				throw (JapidTemplateException)e;
			throw new RuntimeException(e);
		}
	}

	/**
	 * transform a Japid script file to Java code which is then compiled to bytecode stored in the global japidClasses
	 * object.
	 * 
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param srcFileName
	 * @param scriptSrc
	 */
	static synchronized void compileThis(String srcFileName, String scriptSrc) {
		try {
			String c = DirUtil.deriveClassName(srcFileName);

			RendererClass rc = newRendererClass(c);
			japidClasses.put(c, rc);

			JapidTemplateTransformer.addImportLine("play.mvc.Http.Context.Implicit");
			JapidTemplateTransformer.addImportLine("play.mvc.Http.Request"); 
			JapidTemplateTransformer.addImportLine("play.mvc.Http.Response"); 
			JapidTemplateTransformer.addImportLine("play.mvc.Http.Session"); 
			JapidTemplateTransformer.addImportLine("play.mvc.Http.Flash"); 
			JapidTemplateTransformer.addImportLine("play.data.validation.Validation");
			JapidTemplateTransformer.addImportLine("play.i18n.Lang");

			String javaCode = JapidTemplateTransformer.generateInMemory(scriptSrc, srcFileName, true);

			rc.setSourceCode(javaCode);
			rc.setOriSourceCode(scriptSrc);
			rc.setScriptFile(new File(srcFileName));
			removeInnerClasses(c);
			cleanClassHolder(rc);
			
			String[] names = {DEV_ERROR};
			compiler.compile(names);
		} 
		catch (Exception e) {
			if (e instanceof JapidTemplateException)
				throw (JapidTemplateException)e;
			throw new RuntimeException(e);
		}
	}

	private static void setSources(RendererClass rc, File f) {
		rc.setSourceCode(readSource(f));
		File srcFile = DirUtil.mapJavatoSrc(f);
		rc.setOriSourceCode(readSource(srcFile));
		rc.setScriptFile(srcFile);
	}

	public static void removeInnerClasses(String className) {
		for (Iterator<String> i = japidClasses.keySet().iterator(); i.hasNext();) {
			String k = i.next();
			if (k.startsWith(className + "$")) {
				i.remove();
			}
		}
	}

	/**
	 * @param currentClassesOnDir what classes on the disc.
	 * @param classSetInMemory original set of class names
	 */
	public static void removeRemoved(Set<String> currentClassesOnDir,
			Set<String> classSetInMemory) {
		// need to consider inner classes
		// keySet.retainAll(currentClassesOnDir);

		for (Iterator<String> i = classSetInMemory.iterator(); i.hasNext();) {
			String k = i.next();
			if (specialClasses.contains(k))
				continue;
			int q = k.indexOf('$');
			if (q > 0) {
				k = k.substring(0, q);
			}
			if (!currentClassesOnDir.contains(k)) {
				i.remove();
			}
		}
	}
	
	static Set<String> specialClasses = new HashSet<String>();
	static {
		specialClasses.add(DEV_ERROR);
	}

	// <classname RendererClass>
	public final static Map<String, RendererClass> japidClasses = new ConcurrentHashMap<String, RendererClass>();
	public static TemplateClassLoader crlr = new TemplateClassLoader(parentClassLoader);

	public static TemplateClassLoader getCrlr() {
		return crlr;
	}

	public static RendererCompiler compiler = new RendererCompiler(japidClasses,
			crlr);
	public static String templateRoot = "plainjapid";
	public static final String JAPIDVIEWS = "japidviews";
	public static String sep = File.separator;
	public static String japidviews = templateRoot + sep + JAPIDVIEWS + sep;
	// such as java.utils.*
//	public static List<String> importlines = new ArrayList<String>();
	public static int refreshInterval;
	public static long lastRefreshed;
	private static boolean inited;

	public static boolean isInited() {
		return inited;
	}

	private static OpMode opMode;
	private static Application _app;

	public static OpMode getOpMode() {
		return opMode;
	}

	static void howlong(String string, long t) {
		if (JapidFlags.verbose)
			System.out.println(string + ":" + (System.currentTimeMillis() - t)
					+ "ms");
	}

	/**
	 * @param rendererClass
	 */
	static void cleanClassHolder(RendererClass rendererClass) {
		rendererClass.setBytecode(null);
		rendererClass.setClz(null);
		rendererClass.setLastUpdated(0);
	}

	static Set<String> createNameSet(String[] allHtml) {
		// the names start with template root
		Set<String> names = new HashSet<String>();
		for (String f : allHtml) {
			if (f.startsWith(JAPIDVIEWS)) {
				names.add(getClassName(new File(f)));
			}
		}
		return names;
	}

	static String getSourceCode(String k) {
		String pathname = templateRoot + sep + k;
		pathname = pathname.replace(".", sep);
		File f = new File(pathname + ".java");
		return readSource(f);
	}

	/**
	 * @param c
	 * @return
	 */
	static RendererClass newRendererClass(String c) {
		RendererClass rc = new RendererClass();
		rc.setClassName(c);
		// the source code of the Java file might not be available yet
		// rc.setSourceCode(getSouceCode(c));
		rc.setLastUpdated(0);
		return rc;
	}

	static String readSource(File f) {
		try {
			FileInputStream fis = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(bis,
					"UTF-8"));
			StringBuilder b = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				b.append(line + "\n");
			}
			br.close();
			return b.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static String getClassName(File f) {
		String path = f.getPath();
		String substring = path.substring(path.indexOf(JAPIDVIEWS));
		return DirUtil.deriveClassName(substring);
	}

	/**
	 * set the interval to check template changes.
	 * 
	 * @param i
	 *            the interval in seconds. Set it to {@link Integer.MAX_VALUE}
	 *            to effectively disable refreshing
	 */
	static void setRefreshInterval(int i) {
		refreshInterval = i * 1000;
	}

	static void setTemplateRoot(String root) {
		templateRoot = root;
		japidviews = templateRoot + sep + JAPIDVIEWS + sep;
	}

	/**
	 * The entry point for the command line tool japid.bat and japid.sh
	 * 
	 * The "gen" and "regen" are probably the most useful ones.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			String arg0 = args[0];

			setTemplateRoot(".");
			if ("gen".equals(arg0)) {
				gen(templateRoot);
			} else if ("regen".equals(arg0)) {
				regen(templateRoot);
			} else if ("clean".equals(arg0)) {
				delAllGeneratedJava(getJapidviewsDir(templateRoot));
			} else if ("mkdir".equals(arg0)) {
				mkdir(templateRoot);
			} else if ("changed".equals(arg0)) {
				changed(japidviews);
			} else {
				System.err
						.println("help:  optionas are: gen, regen, mkdir and clean");
			}
		} else {
			System.err
					.println("help:  optionas are: gen, regen, mkdir and clean");
		}
	}

	private static void changed(String root) {
		List<File> changedFiles = DirUtil.findChangedSrcFiles(new File(root));
		for (File f : changedFiles) {
			System.out.println("changed: " + f.getPath());
		}

	}

	/**
	 * create the basic layout: app/japidviews/_layouts
	 * app/japidviews/_tags
	 * 
	 * then create a dir for each controller. //TODO
	 * 
	 * @throws IOException
	 * 
	 */
	static List<File> mkdir(String root) throws IOException {
		return PlayDirUtil.mkdir(root);
	}

	/**
	 * @param root
	 * @return
	 */
	private static String getJapidviewsDir(String root) {
		return root + sep + JAPIDVIEWS + sep;
	}

	public static void regen() throws IOException {
		regen(templateRoot);
	}

	public static void regen(String root) throws IOException {
		delAllGeneratedJava(getJapidviewsDir(root));
		gen(root);
	}

	static void delAllGeneratedJava(String pathname) {
		String[] javas = DirUtil.getAllFileNames(new File(pathname),
				new String[] { "java" });

		for (String j : javas) {
			log("removed: " + pathname + j);
			boolean delete = new File(pathname + File.separatorChar + j)
					.delete();
			if (!delete)
				throw new RuntimeException("file was not deleted: " + j);
		}
		// log("removed: all none java tag java files in " +
		// JapidPlugin.JAPIDVIEWS_ROOT);
	}

	/**
	 * update the java files from the html files, for the changed only
	 * 
	 * @throws IOException
	 */
	static List<File> gen(String packageRoot) throws IOException {
		List<File> changedFiles = reloadChanged(packageRoot);
		if (changedFiles.size() > 0) {
		} else {
//			if (JapidFlags.verbose) System.out.print(":");
		}

		rmOrphanJava(packageRoot);
		return changedFiles;
	}

	/**
	 * @param root
	 *            the package root "/"
	 * @return the updated Java files.
	 */
	static List<File> reloadChanged(String root) {
		try {
			mkdir(root);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		TranslateTemplateTask t = new TranslateTemplateTask();
		t.setUsePlay(true);
		File rootDir = new File(root);
		t.setPackageRoot(rootDir);
		t.setInclude(new File(japidviews));
		if (DirUtil.hasLayouts(root))
			t.addImport("japidviews._layouts.*");
		if (DirUtil.hasTags(root))
			t.addImport("japidviews._tags.*");		
//		if (DirUtil.hasJavaTags(root))
//			t.addImport("japidviews._javatags.*");		
		t.addImport("controllers.*");
		t.addImport("models.*");
		t.addImport("play.data.validation.Validation");
		t.addImport("play.i18n.Lang");
		t.addImport("play.mvc.Http.Context.Implicit");
		t.addImport("play.mvc.Http.Flash");
		t.addImport("play.mvc.Http.Request");
		t.addImport("play.mvc.Http.Response");
		t.addImport("play.mvc.Http.Session");
		t.addImport(play.data.Form.class);
		t.addImport(play.data.Form.Field.class);
		
		// t.addImport("cn.bran.japid.util.StringUtils");
		t.addImport("java.util.*");
		for (String imp : imports) {
			t.addImport(imp);
		}

		t.execute();
		// List<File> changedFiles = t.getChangedFiles();
		return t.getChangedTargetFiles();
		// return changedFiles;
	}

	/**
	 * get all the java files in a dir with the "java" removed
	 * 
	 * @return
	 */
	static File[] getAllJavaFilesInDir(String root) {
		// from source files only
		String[] allFiles = DirUtil.getAllFileNames(new File(root),
				new String[] { ".java" });
		File[] fs = new File[allFiles.length];
		int i = 0;
		for (String f : allFiles) {
			String path = f.replace(".java", "");
			fs[i++] = new File(path);
		}
		return fs;
	}

	/**
	 * delete orphaned java artifacts from the japidviews directory
	 * 
	 * @param packageRoot
	 * 
	 * @return
	 */
	static boolean rmOrphanJava(String packageRoot) {

		boolean hasRealOrphan = false;
		try {
			String pathname = getJapidviewsDir(packageRoot);
			File src = new File(pathname);
			if (!src.exists()) {
				log("Could not find required Japid root directory: " + pathname);
				return hasRealOrphan;
			}

			Set<File> oj = DirUtil.findOrphanJava(src, null);
			for (File j : oj) {
				String path = j.getPath();
//				if (path.contains(DirUtil.JAVATAGS))
//						continue;
				// log("found: " + path);
				hasRealOrphan = true;
				String realfile = pathname + File.separator + path;
				File file = new File(realfile);
				boolean r = file.delete();
				if (r)
						JapidFlags.log("deleted orphan " + realfile);
				else
						JapidFlags.log("failed to delete: " + realfile);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hasRealOrphan;
	}

	static List<File> reloadChanged() {
		return reloadChanged(templateRoot);
	}

	static void log(String m) {
		if (JapidFlags.verbose)
			System.out.println("[JapidRender]: " + m);
	}

	public static void gen() {
		if (templateRoot == null) {
			throw new RuntimeException(
					"the template root directory must be set");
		} else {
			try {
				gen(templateRoot);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// /**
	// * set to development mode
	// */
	// public static void setDevMode() {
	// devMode = true;
	// }

	// /**
	// * set to production mode
	// */
	// public static void setProdMode() {
	// devMode = false;
	// }
	//
//
//	public static boolean isDevMode() {
//		return opMode == OpMode.dev;
//	}

	static String removeSemi(String imp) {
		imp = imp.trim();
		if (imp.endsWith(";")) {
			imp = imp.substring(0, imp.length() - 1);
		}
		return imp;
	}

	//
	// public <T extends JapidTemplateBaseWithoutPlay> String render(Class<T> c,
	// Object... args) {
	// int modifiers = c.getModifiers();
	// if (Modifier.isAbstract(modifiers)) {
	// throw new
	// RuntimeException("Cannot init the template class since it's an abstract class: "
	// + c.getName());
	// }
	// try {
	// Constructor<T> ctor = c.getConstructor(StringBuilder.class);
	// StringBuilder sb = new StringBuilder(8000);
	// T t = ctor.newInstance(sb);
	// RenderResult rr = RenderInvokerUtils.render(t, args);
	// return rr.getContent().toString();
	// } catch (NoSuchMethodException e) {
	// throw new
	// RuntimeException("Could not match the arguments with the template args.");
	// } catch (InstantiationException e) {
	// throw new
	// RuntimeException("Could not instantiate the template object. Abstract?");
	// } catch (Exception e) {
	// if (e instanceof RuntimeException)
	// throw (RuntimeException) e;
	// else
	// throw new RuntimeException("Could not invoke the template object: " + e);
	// }
	// }

//	public static void addStaticImportLine(String imp) {
//		importlines.add(" static " + removeSemi(imp));
//	}

	/**
	 * 
	 * @param imp
	 *            the part after import, no ";"
	 */
//	public static void addImportLine(String imp) {
//		importlines.add(removeSemi(imp));
//	}

	/**
	 * A shorter version init() that takes default arguments. The mode matches that of the app;
	 *  the japidviews folder is located in the "japidroot" directory in the application; the
	 *  no-change-detection peroid is 3 seconds. 
	 * 
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param app
	 * @throws IOException 
	 */
	public static void init(Application app) {
		try {
			init(app.isDev() ? OpMode.dev : OpMode.prod, "japidroot", 3, app);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * The <em>required</em> initialization step in using the JapidRender.
	 * 
	 * @param opMode
	 *            the operational mode of Japid. When set to OpMode.prod, it's
	 *            assumed that all Java derivatives are already been generated
	 *            and used directly. When set to OpMode.dev, and using
	 *            none-static linking to using the renderer, file system changes
	 *            are detected for every rendering request given the refresh
	 *            interval is respected. New Java files are generated and
	 *            compiled and new classes are loaded to serve the request.
	 * @param templateRoot
	 *            the root directory to contain the "japidviews" directory tree.
	 * @param refreshInterval
	 *            the minimal time, in second, that must elapse before trying to
	 *            detect any changes in the file system.
	 * @param app
	 * 	           the Play application instance 
	 * @throws IOException 
	 */
	public static void init(OpMode opMode, String templateRoot,
			int refreshInterval, Application app) throws IOException {
		inited = true;
		JapidRenderer.opMode = opMode;
		setTemplateRoot(templateRoot);
		setRefreshInterval(refreshInterval);
		
		String  

		property = app.configuration().getString("japid.trace.file.html");
		property = property == null ?  "false" : property;
		if ("on".equalsIgnoreCase(property) || "yes".equalsIgnoreCase(property))
			property = "true";
		JapidTemplateBaseWithoutPlay.globalTraceFileHtml = new Boolean(property);

		property = app.configuration().getString("japid.trace.file.json");
		property = property == null ?  "false" : property;
		if ("on".equalsIgnoreCase(property) || "yes".equalsIgnoreCase(property))
			property = "true";
		JapidTemplateBaseWithoutPlay.globalTraceFileJson = new Boolean(property);
		
		parentClassLoader = app.classloader();
		crlr = new TemplateClassLoader(parentClassLoader);
		compiler = new RendererCompiler(japidClasses, crlr);
		
		_app = app;
		
		initErrorRenderer();
		JapidFlags.log("[Japid] initialized");
	}


	public static String findTemplate() {
		String japidRenderInvoker = StackTraceUtils.getJapidRenderInvoker();
		return japidRenderInvoker;
	}


	/**
	 * If true, allow verbose logging to the console of the Japid activities.
	 * 
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param logVerbose
	 */
	public static void setLogVerbose(boolean logVerbose) {
		JapidFlags.verbose = logVerbose;
	}

	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @return
	 */
	public static Class<? extends JapidTemplateBaseWithoutPlay> getErrorRendererClass() {
		return devErrorClass;
//		RendererClass rc = japidClasses.get(DEV_ERROR);
//		if (rc == null)
//			throw new RuntimeException("dev error renderer class wrapper not found");
//		else {
//			Class<? extends JapidTemplateBaseWithoutPlay> clz = rc.getClz();
//			if (clz == null)
//				throw new RuntimeException("dev error renderer class not found.");
//			else
//				return clz;
//		}
	}
	
	@SuppressWarnings("unchecked")
	public static void initErrorRenderer() throws IOException {
		InputStream devErr = PlayDirUtil.class.getResourceAsStream(DEV_ERROR_FILE); // file in the conf folder

		ByteArrayOutputStream out = new ByteArrayOutputStream(8000);
		DirUtil.copyStreamClose(devErr, out);
		String devErrorSrc  = out.toString("UTF-8");

		compileThis(DEV_ERROR_FILE, devErrorSrc);
		
		TemplateClassLoader classReloader = new TemplateClassLoader(parentClassLoader);
		try {
			devErrorClass = (Class<JapidTemplateBaseWithoutPlay>) classReloader.loadClass(DEV_ERROR);
//			japidClasses.get(DEV_ERROR).setClz(loadClass);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	static Class<JapidTemplateBaseWithoutPlay> devErrorClass;
}
