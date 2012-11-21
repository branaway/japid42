package cn.bran.japid.template;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import play.Application;
import play.GlobalSettings;
import play.api.mvc.Handler;
import play.cache.Cache;
import play.cache.Cached;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
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
import cn.bran.japid.util.StringUtils;
import cn.bran.japid.util.WebUtils;
import cn.bran.play.RenderResultCache;

public class JapidRenderer extends GlobalSettings {
	private static final String RENDER_JAPID_WITH = "/renderJapidWith";
	private static final String NO_CACHE = "no-cache";
	private static AtomicLong lastTimeChecked = new AtomicLong(0);
	// can be used to cache a plugin scoped valules
	private static Map<String, Object> japidCache = new ConcurrentHashMap<String, Object>();

	private static final String DEV_ERROR = "japidviews.devError";

	private static final String JAPID_CLASSES_CACHE = ".japidClasses.cache";

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

	// last time that something in the Japid root was changed
	private static long lastChanged = System.currentTimeMillis();

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
	public static Class<? extends JapidTemplateBaseWithoutPlay> getClass(String name) {
		refreshClasses();
		return getClassWithoutRefresh(name);

	}

	public static RendererClass getRendererClass(String name) {
		refreshClasses();
		return getRendererClassWithoutRefresh(name);
	}

	private static Class<? extends JapidTemplateBaseWithoutPlay> getClassWithoutRefresh(String name) {
		RendererClass rc = getRendererClassWithoutRefresh(name);
		return rc.getClz();
	}

	private static RendererClass getRendererClassWithoutRefresh(String name) {
		RendererClass rc = japidClasses.get(name);
		if (rc == null)
			throw new RuntimeException("renderer class not found: " + name
					+ ". Consider creating the Japid script file @: " + flattern(templateRoots) + SEP
					+ name.replace('.', File.separatorChar) + ".html");
		else {
			if (rc.getClz() == null || playClassloaderChanged()) {
				compileAndLoad(name, rc);
				// try {
				// new TemplateClassLoader(parentClassLoader).loadClass(name);
				// } catch (java.lang.NoClassDefFoundError e) {
				// // the class presented when the class was compiled but it
				// could not be found at runtime.
				// // we need to recompile the class
				// compileAndLoad(name, rc);
				// } catch (ClassNotFoundException e) {
				// compileAndLoad(name, rc);
				// } catch (Exception e) {
				// throw new RuntimeException(e);
				// }
			}
		}

		return rc;
	}

	private static void compileAndLoad(String name, RendererClass rc) {
		// long t = System.currentTimeMillis();
		// if (rc.getBytecode() == null || t - rc.getLastCompiled() > 2000)
		// compiler.compile(new String[] { rc.getClassName()});
		// try {
		// if (rc.getClz() == null || t - rc.getLastDefined() > 2000)
		// new TemplateClassLoader(parentClassLoader).loadClass(name);
		// } catch (ClassNotFoundException e1) {
		// throw new RuntimeException(e1);
		// }
		// code recompiling is now in class loading so the above code is
		// deprecated
		try {
			getClassLoader().loadClass(name);
		} catch (ClassNotFoundException e1) {
			throw new RuntimeException(e1);
		}

	}

	// cache the classloader for a delay to buffer consecutive requests
	// applicable to debug mode only
	synchronized private static TemplateClassLoader getClassLoader() {
		long now = System.currentTimeMillis();
		if (now - newClassLoaderCreated > 2000) {
			newClassLoaderCreated = now;
			lastClassLoader = new TemplateClassLoader(parentClassLoader);
		}
		return lastClassLoader;
	}

	private static long newClassLoaderCreated = 0;
	private static TemplateClassLoader lastClassLoader;

	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param templateRoots2
	 * @return
	 */
	private static String flattern(Object[] templateRoots2) {
		String re = "[" + StringUtils.join(templateRoots2, ",") + "]";
		return re;
	}

	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @return
	 */
	private static boolean playClassloaderChanged() {
		// for some reason even if the classloader remains the same, the classes
		// are new
		if (opMode == OpMode.prod)
			return false;

		parentClassLoader = _app.classloader();
		return true;
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
		} else {
			if (!timeToRefresh())
				return;
		}
		try {
			// there are two passes of directory scanning. XXX

			String[] allTemps = DirUtil.getAllTemplateFiles(templateRoots);
			Set<String> currentClassesOnDir = createNameSet(allTemps);
			Set<String> allScriptNames = new HashSet<String>(currentClassesOnDir);

			Set<String> keySet = japidClasses.keySet();

			if (!keySet.equals(currentClassesOnDir)) {
				Set<String> classNamesRegistered = new HashSet<String>(keySet);
				Set<String> classNamesDir = new HashSet<String>(currentClassesOnDir);
				if (classNamesRegistered.containsAll(classNamesDir)) {
					classNamesRegistered.removeAll(classNamesDir);
					if (!classNamesRegistered.isEmpty()) {
						for (String n : classNamesRegistered) {
							if (!n.contains("$")) {
								touch();
								break;
							}
						}
					}
				} else {
					touch();
				}
			} else {
				// no name changes
			}

			allScriptNames.removeAll(keySet); // got new templates
			removeRemoved(currentClassesOnDir, keySet);

			for (String c : allScriptNames) {
				RendererClass rc = newRendererClass(c);
				japidClasses.put(c, rc);
			}
			// now all the class set size is up to date

			// now update any Java source code
			// second disk scanning.
			List<File> gen = gen(templateRoots);

			// this would include both new and updated java
			Set<String> updatedClasses = new HashSet<String>();
			if (gen.size() > 0) {
				for (File f : gen) {
					String className = getClassName(f);
					updatedClasses.add(className);
					RendererClass rendererClass = japidClasses.get(className);
					if (rendererClass == null) {
						// this should not happen, since
						throw new RuntimeException("any new class names should have been in the classes container: "
								+ className);
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
						setSources(rc, k);
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
				// newly compiled class bytecode bodies are set in the global
				// classes set ready for defining
				compiler.compile(names);
				howlong("compile time for " + names.length + " classes", t);

				for (String k : japidClasses.keySet()) {
					japidClasses.get(k).setClz(null);
				}

				TemplateClassLoader loader = getClassLoader();
				for (String cname : updatedClasses) {
					loader.loadClass(cname);
				}
			}
		} catch (Exception e) {
			if (e instanceof JapidTemplateException)
				throw (JapidTemplateException) e;
			throw new RuntimeException(e);
		}
	}

	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 */
	private static void touch() {
		lastChanged = System.currentTimeMillis();
	}

	/**
	 * transform a Japid script file to Java code which is then compiled to
	 * bytecode stored in the global japidClasses object.
	 * 
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param srcFileName
	 * @param scriptSrc
	 */
	static synchronized void compileDevErroView(String srcFileName, String scriptSrc) {
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

			compiler.compile(new String[] { DEV_ERROR });
		} catch (Exception e) {
			if (e instanceof JapidTemplateException)
				throw (JapidTemplateException) e;
			throw new RuntimeException(e);
		}
	}

	private static void setSources(RendererClass rc, String className) {
		boolean found = false;

		for (String r : templateRoots) {
			String pathname = r + SEP + className;
			pathname = pathname.replace(".", SEP);
			File f = new File(pathname + ".java");

			try {
				setSources(rc, f);
				found = true;
			} catch (IOException e) {
			}
		}

		if (!found) {
			throw new RuntimeException("could not find the source for: " + className);
		}
	}

	private static File setSources(RendererClass rc, File f) throws IOException {
		rc.setSourceCode(readSource(f));
		File srcFile = DirUtil.mapJavatoSrc(f);
		rc.setOriSourceCode(readSource(srcFile));
		rc.setScriptFile(srcFile);
		return srcFile;
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
	 * @param currentClassesOnDir
	 *            what classes on the disc.
	 * @param classSetInMemory
	 *            original set of class names
	 */
	public static void removeRemoved(Set<String> currentClassesOnDir, Set<String> classSetInMemory) {
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
				i.remove();// changes to the keyset will result in change in the
							// backing map.
			}
		}
	}

	static Set<String> specialClasses = new HashSet<String>();
	static {
		specialClasses.add(DEV_ERROR);
	}

	// <classname RendererClass>
	public static Map<String, RendererClass> japidClasses = new ConcurrentHashMap<String, RendererClass>();
	public static TemplateClassLoader crlr;

	public static TemplateClassLoader getCrlr() {
		return crlr;
	}

	public static RendererCompiler compiler;
	public static String[] templateRoots = { "plainjapid" };
	public static final String JAPIDVIEWS = "japidviews";
	public static final String SEP = File.separator;
	// public static String[] japidviews;
	// static {
	// initJapidViews();
	// }
	//
	// private static void initJapidViews() {
	// japidviews = new String[templateRoot.length];
	// int i = 0;
	// for (String r : templateRoot) {
	// japidviews[i++] = r + SEP + JAPIDVIEWS + SEP;
	// }
	// }

	// such as java.utils.*
	// public static List<String> importlines = new ArrayList<String>();
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
			System.out.println(string + ":" + (System.currentTimeMillis() - t) + "ms");
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

	//
	// static String getSourceCode(String k) {
	// String pathname = templateRoots + SEP + k;
	// pathname = pathname.replace(".", SEP);
	// File f = new File(pathname + ".java");
	// return readSource(f);
	// }

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

	static String readSource(File f) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		BufferedInputStream bis = new BufferedInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));
		StringBuilder b = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			b.append(line + "\n");
		}
		br.close();
		return b.toString();
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

	/**
	 * set the paths where to look for japid scripts.
	 * 
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param root
	 */
	public static void setTemplateRoot(String... root) {
		templateRoots = root;
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
				gen(templateRoots);
			} else if ("regen".equals(arg0)) {
				regen(templateRoots);
			} else if ("clean".equals(arg0)) {
				for (String r : templateRoots)
					delAllGeneratedJava(getJapidviewsDir(r));
			} else if ("mkdir".equals(arg0)) {
				mkdir(templateRoots);
				// } else if ("changed".equals(arg0)) {
				// changed(japidviews);
			} else {
				System.err.println("help:  optionas are: gen, regen, mkdir and clean");
			}
		} else {
			System.err.println("help:  optionas are: gen, regen, mkdir and clean");
		}
	}

	private static void changed(String root) {
		List<File> changedFiles = DirUtil.findChangedSrcFiles(new File(root));
		for (File f : changedFiles) {
			System.out.println("changed: " + f.getPath());
		}

	}

	/**
	 * not: create the basic layout: app/japidviews/_layouts
	 * app/japidviews/_tags
	 * 
	 * then create a dir for each controller. //TODO
	 * 
	 * @throws IOException
	 * 
	 */
	static List<File> mkdir(String... root) throws IOException {
		List<File> files = new ArrayList<File>();
		for (String r : root) {
			files.addAll(PlayDirUtil.mkdir(r));
		}
		return files;
	}

	/**
	 * @param root
	 * @return
	 */
	private static String getJapidviewsDir(String root) {
		return root + SEP + JAPIDVIEWS + SEP;
	}

	public static void regen() throws IOException {
		regen(templateRoots);
	}

	public static void regen(String... roots) throws IOException {
		for (String root : roots) {
			delAllGeneratedJava(getJapidviewsDir(root));
		}
		gen(roots);
	}

	static void delAllGeneratedJava(String pathname) {
		String[] javas = DirUtil.getAllFileNames(new File(pathname), new String[] { "java" });

		for (String j : javas) {
			log("removed: " + pathname + j);
			boolean delete = new File(pathname + File.separatorChar + j).delete();
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
	static List<File> gen(String... packageRoots) throws IOException {
		List<File> changedFiles = reloadChanged(packageRoots);
		for (String p : packageRoots) {
			rmOrphanJava(p);
		}
		return changedFiles;
	}

	/**
	 * @param roots
	 *            the package root
	 * @return the updated Java files.
	 */
	static List<File> reloadChanged(String... roots) {
		try {
			mkdir(roots);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		List<File> files = new ArrayList<File>();
		for (String r : roots) {
			TranslateTemplateTask t = new TranslateTemplateTask();
			t.addImport("controllers.*");
			t.addImport("models.*");
			t.addImport("japidviews.*");
			t.addImport("play.data.validation.Validation");
			t.addImport("play.i18n.Lang");
			t.addImport("play.mvc.Http.Context.Implicit");
			t.addImport("play.mvc.Http.Flash");
			t.addImport("play.mvc.Http.Request");
			t.addImport("play.mvc.Http.Response");
			t.addImport("play.mvc.Http.Session");
			t.addImport(play.data.Form.class);
			t.addImport(play.data.Form.Field.class);
			t.addImport("java.util.*");
			for (String imp : imports) {
				t.addImport(imp);
			}
			t.setUsePlay(true);

			t.setPackageRoot(new File(r));
			t.setInclude(new File(r + SEP + JAPIDVIEWS + SEP));
			// _layouts and _tags are deprecated
			// if (DirUtil.hasLayouts(r))
			// t.addImport("japidviews._layouts.*");
			// if (DirUtil.hasTags(r))
			// t.addImport("japidviews._tags.*");
			t.execute();
			files.addAll(t.getChangedTargetFiles());
		}
		return files;
	}

	/**
	 * get all the java files in a dir with the "java" removed
	 * 
	 * @return
	 */
	static File[] getAllJavaFilesInDir(String root) {
		// from source files only
		String[] allFiles = DirUtil.getAllFileNames(new File(root), new String[] { ".java" });
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
				// if (path.contains(DirUtil.JAVATAGS))
				// continue;
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
		return reloadChanged(templateRoots);
	}

	static void log(String m) {
		if (JapidFlags.verbose)
			System.out.println("[JapidRender]: " + m);
	}

	public static void gen() {
		if (templateRoots == null) {
			throw new RuntimeException("the template root directory must be set");
		} else {
			try {
				gen(templateRoots);
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
	// public static boolean isDevMode() {
	// return opMode == OpMode.dev;
	// }

	static String removeSemi(String imp) {
		imp = imp.trim();
		if (imp.endsWith(";")) {
			imp = imp.substring(0, imp.length() - 1);
		}
		return imp;
	}

	/**
	 * A shorter version init() that takes default arguments. The mode matches
	 * that of the app; the japidviews folder is located in the "japidroot"
	 * directory in the application; the no-change-detection peroid is 3
	 * seconds.
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
	 *            the Play application instance
	 * @throws IOException
	 */
	public static void init(OpMode opMode, String templateRoot, int refreshInterval, Application app)
			throws IOException {
		_app = app;
		inited = true;
		JapidRenderer.opMode = opMode;
		setTemplateRoot(templateRoot);
		setRefreshInterval(refreshInterval);

		String property = app.configuration().getString("japid.trace.file.html");
		property = property == null ? "false" : property;
		if ("on".equalsIgnoreCase(property) || "yes".equalsIgnoreCase(property))
			property = "true";
		JapidTemplateBaseWithoutPlay.globalTraceFileHtml = new Boolean(property);

		property = app.configuration().getString("japid.trace.file.json");
		property = property == null ? "false" : property;
		if ("on".equalsIgnoreCase(property) || "yes".equalsIgnoreCase(property))
			property = "true";
		JapidTemplateBaseWithoutPlay.globalTraceFileJson = new Boolean(property);

		getDumpRequest();

		parentClassLoader = app.classloader();
		lastClassLoader = new TemplateClassLoader(parentClassLoader);
		crlr = getClassLoader();
		compiler = new RendererCompiler(japidClasses, crlr);

		initErrorRenderer();
		touch();
		JapidFlags.log("[Japid] initialized");
		if (app.isDev())
			recoverClasses();
	}

	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 */
	@SuppressWarnings("unchecked")
	private static void recoverClasses() {
		String templateRoot = getTemplateRoot()[0];
		FileInputStream fos;
		try {
			File file = new File(new File(templateRoot), JAPID_CLASSES_CACHE);
			if (file.exists()) {
				fos = new FileInputStream(file);
				BufferedInputStream bos = new BufferedInputStream(fos);
				ObjectInputStream ois = new ObjectInputStream(bos);
				JapidRenderer.japidClasses = (Map<String, RendererClass>) ois.readObject();
				ois.close();
				file.delete();
				JapidFlags.log("recovered Japid classes from cache");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			File file = new File(new File(templateRoot), JAPID_CLASSES_CACHE);
			if (file.exists()) {
				file.delete();
			}
		}

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
	}

	@SuppressWarnings("unchecked")
	public static void initErrorRenderer() throws IOException {
		InputStream devErr = PlayDirUtil.class.getResourceAsStream(DEV_ERROR_FILE);
		ByteArrayOutputStream out = new ByteArrayOutputStream(8000);
		DirUtil.copyStreamClose(devErr, out);
		String devErrorSrc = out.toString("UTF-8");

		compileDevErroView(DEV_ERROR_FILE, devErrorSrc);

		try {
			devErrorClass = (Class<JapidTemplateBaseWithoutPlay>) getClassLoader().loadClass(DEV_ERROR);
			// japidClasses.get(DEV_ERROR).setClz(loadClass);
			japidClasses.remove(DEV_ERROR);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the lastChanged
	 */
	public static long getLastChanged() {
		return lastChanged;
	}

	static Class<JapidTemplateBaseWithoutPlay> devErrorClass;
	private static String dumpRequest;

	/**
	 * compile/recompile the class if disk change detected. Should later do the
	 * compiling based on dependency graph.
	 * 
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param rc
	 */
	public static void recompile(RendererClass rc) {
		if (rc.getBytecode() == null || rc.getLastCompiled() < getLastChanged()) {
			compiler.compile(new String[] { rc.getClassName() });
		}
	}

	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @return
	 */
	public static String[] getTemplateRoot() {
		return templateRoots;
	}

	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param app
	 */
	@Override
	public void onStop(Application app) {
		if (app.isDev())
			try {
				// save for future reloading
				String templateRoot = JapidRenderer.getTemplateRoot()[0];
				FileOutputStream fos = new FileOutputStream(new File(new File(templateRoot), JAPID_CLASSES_CACHE));
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				ObjectOutputStream oos = null;
				try {
					oos = new ObjectOutputStream(bos);
					oos.writeObject(JapidRenderer.japidClasses);
				} catch (Exception e) {
					System.out.println(e);
					if (oos != null) {
						try {
							oos.close();
						} catch (Exception ex) {
						}
					}
				}
				oos.close();
			} catch (IOException e) {
				System.out.println(e);
			} finally {

			}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see play.GlobalSettings#onStart(play.Application)
	 */
	@Override
	public void onStart(Application app) {
		JapidRenderer.init(app);
		super.onStart(app);
		onStartJapid();
	}

	/**
	 * sub classes do more customization of Japid here
	 * 
	 * @author Bing Ran (bing.ran@hotmail.com)
	 */
	public void onStartJapid() {
	};

	public static Map<String, Object> getCache() {
		return japidCache;
	}

	static private void getDumpRequest() {
		String property = _app.configuration().getString("japid.dump.request");
		dumpRequest = property;
	}

	public static void beforeActionInvocation(Context ctx, Method actionMethod) {
		Request request = ctx.request();
		play.mvc.Http.Flash flash = ctx.flash();
		Map<String, String[]> headers = request.headers();

		String property = dumpRequest;
		if (property != null && property.length() > 0) {
			if (!"false".equals(property) && !"no".equals(property)) {
				if ("yes".equals(property) || "true".equals(property)) {
					System.out.println("--- action ->: " + actionMethod.toString());
				} else {
					if (request.uri().matches(property)) {
						System.out.println("--- action ->: " + actionMethod.toString());
					}
				}
			}
		}

		String string = flash.get(RenderResultCache.READ_THRU_FLASH);
		if (string != null) {
			RenderResultCache.setIgnoreCache(true);
		} else {
			// cache-control in lower case, lowercase for some reason
			String[] header = headers.get("cache-control");
			if (header != null) {
				@SuppressWarnings("unchecked")
				List<String> list = Arrays.asList(header);
				if (list.contains(NO_CACHE)) {
					RenderResultCache.setIgnoreCache(true);
				}
			} else {
				header = headers.get("pragma");
				if (header != null) {
					@SuppressWarnings("unchecked")
					List<String> list = Arrays.asList(header);
					if (list.contains(NO_CACHE)) {
						RenderResultCache.setIgnoreCache(true);
					}
				} else {
					// just in case
					RenderResultCache.setIgnoreCacheInCurrentAndNextReq(false);
				}
			}
		}
		
		// check for authenticity token
		// I cannot tell if I really need to check this 
		// not a good idea doing it here
//		Session session = ctx.session();
//		String atoken = session.get(AuthenticityToken.AUTH_TOKEN);
//		session.remove(AuthenticityToken.AUTH_TOKEN);
//		
//		String method = request.method();
//		if (atoken != null
//		if (method.equals("GET")) {
//			
//		}
//		
//		if (atoken == null || uuid == null)
//			return false;
//
//		String sign = Crypto.sign(uuid.toString());
//		return atoken.equals(sign);
	}

	@Override
	public Action<?> onRequest(Request request, final Method actionMethod) {
		return new Action<Cached>() {
			public Result call(Context ctx) {
				try {
					beforeActionInvocation(ctx, actionMethod);
					
					Result result = null;
					Request req = ctx.request();
					String method = req.method();
					int duration = 0;
					String key = null;
					Cached cachAnno = actionMethod.getAnnotation(Cached.class);
					// Check the cache (only for GET or HEAD)
					if ((method.equals("GET") || method.equals("HEAD")) && cachAnno != null) {
						key = cachAnno.key();
						if ("".equals(key) || key == null) {
							key = "urlcache:" + req.uri() + ":" + req.queryString();
						}
						duration = cachAnno.duration();
						result = (Result) Cache.get(key);
					}
					if (result == null) {
						result = delegate.call(ctx);
						if (!StringUtils.isEmpty(key) && duration > 0) {
							Cache.set(key, result, duration);
						}
					}
					onActionInvocationResult(ctx);
					return result;
				} catch (RuntimeException e) {
					throw e;
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
			}

		};

		// return new Action.Simple() {
		// public Result call(Context ctx) throws Throwable {
		// beforeActionInvocation(ctx, actionMethod);
		// dumpIt(ctx.request(), actionMethod);
		// Result call = delegate.call(ctx);
		// onActionInvocationResult(ctx);
		// return call;
		// }
		// };
	}

	public void onActionInvocationResult(Context ctx) {
		play.mvc.Http.Flash fl = ctx.flash();
		if (RenderResultCache.shouldIgnoreCacheInCurrentAndNextReq()) {
			fl.put(RenderResultCache.READ_THRU_FLASH, "yes");
		} else {
			fl.remove(RenderResultCache.READ_THRU_FLASH);
		}

		// always reset the flag since the thread may be reused for another
		// request processing
		RenderResultCache.setIgnoreCacheInCurrentAndNextReq(false);
	}

	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param request
	 * @param actionMethod
	 */
	private void dumpIt(Request req, Method actionMethod) {
		// if (dumpRequest != null && dumpRequest.length() > 0) {
		// if ("yes".equals(dumpRequest) || "true".equals(dumpRequest)) {
		// System.out.println("---->>" + req.method + " : " + req.url + " [" +
		// req.action + "]");
		// // System.out.println("request.controller:" +
		// // current.controller);
		// } else if ("false".equals(dumpRequest) || "no".equals(dumpRequest)) {
		// // do nothing
		// } else if (req.uri().matches(dumpRequest)) {
		// String querystring = req.uri();
		// if (querystring != null && querystring.length() > 0)
		// querystring = "?" + querystring;
		// else
		// querystring = "";
		// System.out.println("---->>" + actionMethod + " : " + req.uri());
		// String contentType = req.accepts(mediaType)contentType;
		// if (contentType == null || contentType.length() == 0) {
		// contentType = "";
		// }
		//
		// for (String k : req.headers.keySet()) {
		// Header h = req.headers.get(k);
		// System.out.println("... " + h.name + ":" +
		// URLDecoder.decode(h.value(), "utf-8"));
		// }
		// cookie is already in the headers
		// for (String ck : req.cookies.keySet()) {
		// Cookie c = req.cookies.get(ck);
		// System.out.println("... cookie --> " + c.name + ":" +
		// c.value);
		// }
		// if ("POST".equals(req.method)) {
		// if (contentType.contains("application/x-www-form-urlencoded")) {
		// dumpReqBody(req, true);
		// } else if (contentType.startsWith("text")) {
		// dumpReqBody(req, false);
		// } else if (contentType.contains("multipart/form-data")) {
		// // cannot dump it, since it may contain binary
		// } else if (contentType.contains("xml")) {
		// dumpReqBody(req, false);
		// } else if (contentType.contains("javascript")) {
		// dumpReqBody(req, false);
		// }
		// }
		// }
		//
		// }
	}

	@Override
	public Handler onRouteRequest(RequestHeader request) {
		return super.onRouteRequest(request);
	}
}
