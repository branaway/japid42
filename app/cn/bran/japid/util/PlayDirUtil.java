package cn.bran.japid.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import play.Play;

public class PlayDirUtil {


		/**
		 * not any more: create the basic layout: app/japidviews/_javatags app/japidviews/_layouts
		 * app/japidviews/_tags
		 * 
		 * then create a dir for each controller. //TODO
		 * 
		 * @throws IOException
		 * 
		 */
		public static List<File> mkdir(String root) throws IOException {
			String sep = File.separator;
			String japidViews = root + sep + DirUtil.JAPIDVIEWS_ROOT + sep;
			new File(japidViews).mkdir();
			
//			File javatags = new File(japidViews + DirUtil.JAVATAGS);
//			if (!javatags.exists()) {
//				boolean mkdirs = javatags.mkdirs();
//				assert mkdirs;
//				JapidFlags.log("created: " + japidViews + DirUtil.JAVATAGS);
//			}

			// deprecated
//			File layouts = new File(japidViews + DirUtil.LAYOUTDIR);
//			if (!layouts.exists()) {
//				boolean mkdirs = layouts.mkdirs();
//				assert mkdirs;
//				JapidFlags.log("created: " + japidViews + DirUtil.LAYOUTDIR);
//			}
//	
//			File tags = new File(japidViews + DirUtil.TAGSDIR);
//			if (!tags.exists()) {
//				boolean mkdirs = tags.mkdirs();
//				assert mkdirs;
//				JapidFlags.log("created: " + japidViews + DirUtil.TAGSDIR);
//			}
			
			// email notifiers
			File notifiers = new File(japidViews + DirUtil.NOTIFIERS);
			if (!notifiers.exists()) {
				boolean mkdirs = notifiers.mkdirs();
				assert mkdirs;
				JapidFlags.log("created: " + japidViews + DirUtil.NOTIFIERS);
			}

			// let hide this from user for now
			
//			// error renderer
//			File errors = new File(japidViews + DirUtil.ERRORS);
//			if (!errors.exists()) {
//				boolean mkdirs = errors.mkdirs();
//				assert mkdirs;
//				JapidFlags.log("created: " + japidViews + DirUtil.ERRORS);
//			}
//			// add devError.html
//			InputStream devErr = PlayDirUtil.class.getResourceAsStream("/devError.html"); // file in the conf folder
//			File target = new File(japidViews + DirUtil.ERRORS + "/devError.html");
//			if (!target.exists()) {
//				BufferedOutputStream bf = new BufferedOutputStream(new FileOutputStream(target));
//				DirUtil.copyStream(devErr, bf);
//				devErr.close();
//				bf.close();
//			}
			
			List<File> res = new ArrayList<File>();
			
//			File[] dirs = new File[] { /*javatags,*/ layouts, tags };
//			res.addAll(Arrays.asList(dirs));

	
			// create dirs for controllers
	
			File rootFile = getAppRootDir(root);
					
	//		JapidFlags.log("JapidCommands: check default template packages for controllers.");
			if (rootFile != null && rootFile.exists()) {
				try {
					File controllerPathFile = new File(rootFile,  "app" + sep + "controllers");
	//				JapidFlags.log("PlayDirUtil: controller path: " + controllerPathFile.getAbsolutePath());
					if (controllerPathFile.exists()) {
						String[] controllers = DirUtil.getAllJavaFilesInDir(controllerPathFile);
						for (String f : controllers) {
							String cp = japidViews + f;
							File ff = new File(cp);
							if (!ff.exists()) {
								boolean mkdirs = ff.mkdirs();
								assert mkdirs == true;
								res.add(ff);
								JapidFlags.log("created: " + cp);
							}
						}
					}
				} catch (Exception e) {
					JapidFlags.log(e.toString());
				}
			}
	
	//		JapidFlags.log("JapidCommands:  check default template packages for email notifiers.");
			try {
				String notifiersDir = Play.application().path()  + sep + "app" + sep + "notifiers";
				File notifiersDirFile = new File(notifiersDir);
				if (!notifiersDirFile.exists()) {
					if (notifiersDirFile.mkdir()) {
						JapidFlags.log("created the email notifiers directory. ");
					}
					else {
						JapidFlags.log("email notifiers directory did not exist and could not be created for unknow reason. ");
					}
				}
				
				String[] controllers = DirUtil.getAllJavaFilesInDir(notifiersDirFile);
				for (String f : controllers) {
					// note: we keep the notifiers dir to differentiate those from the controller
					// however this means we cannot have a controller with package like "controllers.notifiers"
					// so we now use "_notifiers"
					String cp = japidViews + DirUtil.NOTIFIERS + sep + f;
					File ff = new File(cp);
					if (!ff.exists()) {
						boolean mkdirs = ff.mkdirs();
						assert mkdirs == true;
						res.add(ff);
						JapidFlags.log("created: " + cp);
					}
				}
			} catch (Exception e) {
				JapidFlags.log(e.toString());
			}
			return res;
		}

		static File getAppRootDir(String root) {
			File rootFile = new File(root).getAbsoluteFile();
			while(rootFile != null && !isAppRoot(rootFile)) {
				rootFile = rootFile.getParentFile();
			}
			return rootFile;
		}

		/**
		 * @author Bing Ran (bing.ran@hotmail.com)
		 * @param rootFile
		 * @return
		 */
		private static boolean isAppRoot(File rootFile) {
			File[] list = rootFile.listFiles();
			for (File l : list) {
				if ("app".equals(l.getName())) {
					if (l.isDirectory()) {
						File controllerDir = new File(l, "controllers");
						if (controllerDir.exists() && controllerDir.isDirectory()) {
							return true;
						}
					}
				}
			}
			
			return false;
		}

}
