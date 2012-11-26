package cn.bran.japid.rendererloader;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;

import cn.bran.japid.template.JapidTemplateBaseWithoutPlay;

public class RendererClass implements Serializable{
	private static final long serialVersionUID = -2039838560731729110L;
	String className;
	String sourceCode; // the java file
	String oriSourceCode;
	
	private long lastUpdated;
	byte[] bytecode;
	transient Class<? extends JapidTemplateBaseWithoutPlay> clz;
//	ClassLoader cl;
	private File scriptFile; // the original template source file
	// the constructor cache
	transient private Constructor<? extends JapidTemplateBaseWithoutPlay> constructor;
	private long lastCompiled;
	private long lastDefined;
	private long scripTimestamp;
	
//	public ClassLoader getCl() {
//		return cl;
//	}
//	public void setCl(ClassLoader cl) {
//		this.cl = cl;
//	}
	public Class<? extends JapidTemplateBaseWithoutPlay> getClz() {
		return clz;
	}
	public void setClz(Class<? extends JapidTemplateBaseWithoutPlay> clz) {
		this.clz = clz;
		if (clz == null)
			this.setConstructor(null);
		else 
			try {
				if (!clz.getName().contains("$"))
					this.setConstructor(clz.getConstructor(StringBuilder.class));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getSourceCode() {
		return sourceCode;
	}
	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public void setLastUpdated(long lastUpdates) {
		this.lastUpdated = lastUpdates;
	}
	public byte[] getBytecode() {
		return bytecode;
	}
	public void setBytecode(byte[] bytecode) {
		this.bytecode = bytecode;
	}
	
	public void clear() {
		this.bytecode = null;
	}
	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param srcFile
	 */
	public void setScriptFile(File srcFile) {
		if (srcFile.exists()) {
			this.scriptFile = srcFile;
			this.scripTimestamp = srcFile.lastModified();
		}
		else
			throw new RuntimeException("japid script does not exist: " + srcFile.getAbsolutePath());
	}
	/**
	 * @return the srcFile
	 */
	public File getScriptFile() {
		return scriptFile;
	}
	/**
	 * @return the javaSourceCode
	 */
	public String getOriSourceCode() {
		return oriSourceCode;
	}
	/**
	 * @param javaSourceCode the javaSourceCode to set
	 */
	public void setOriSourceCode(String oriSourceCode) {
		this.oriSourceCode = oriSourceCode;
	}
	/**
	 * @return the lastUpdated
	 */
	public long getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param lineNumber
	 * @return
	 */
	public int mapJavaLineToJapidScriptLine(int lineNumber) {
		String jsrc = getSourceCode();
		String[] splitSrc = jsrc.split("\n");
		String line = splitSrc[lineNumber - 1];
		// can we have a line marker?
		int lineMarker = line.lastIndexOf("// line ");
		if (lineMarker > 0) 
			return Integer.parseInt(line.substring(lineMarker + 8).trim());
		else
			return -1;	

	}
	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param ctor
	 */
	public  void setConstructor(Constructor<? extends JapidTemplateBaseWithoutPlay> ctor) {
		this.constructor = ctor;
	}
	/**
	 * @return the constructor
	 */
	public Constructor<? extends JapidTemplateBaseWithoutPlay> getConstructor() {
		return constructor;
	}
	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param currentTimeMillis
	 */
	public void setLastCompiled(long currentTimeMillis) {
		this.lastCompiled = currentTimeMillis;
	}
	/**
	 * @return the lastCompiled
	 */
	public long getLastCompiled() {
		return lastCompiled;
	}
	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param currentTimeMillis
	 */
	public void setLastDefined(long currentTimeMillis) {
		this.lastDefined = currentTimeMillis;
	}
	/**
	 * @return the lastDefined
	 */
	public long getLastDefined() {
		return lastDefined;
	}

	@Override
	public String toString() {
		return "Japid Renderer class wrapper for: " + this.getClassName() + ". Source file: " + this.scriptFile;
	}
	/**
	 * @return the scripTimestamp
	 */
	public long getScripTimestamp() {
		return scripTimestamp;
	}
	/**
	 * @param scripTimestamp the scripTimestamp to set
	 */
	public void setScripTimestamp(long scripTimestamp) {
		this.scripTimestamp = scripTimestamp;
	}

}
