/**
 * 
 */
package cn.bran.japid.exceptions;

import java.util.TreeMap;

import cn.bran.japid.compiler.JapidCompilationException;

/**
 * @author bran
 *
 */
public class JapidTemplateException extends JapidRuntimeException {
	private static final long serialVersionUID = 1L;
	/**
	 * @param string
	 */
	public JapidTemplateException(String title, String description, int errLineNum, String sourceName, String sourceCode) {
		super(description);
		this.title = title;
		this.description = description;
		this.errLineNum = errLineNum;
		this.soureName = sourceName;
		this.srcCode = sourceCode;
		interestingLines = getInterestingLines();
	}
	
	public Throwable e;
	public String title;
	public String description;
	public Integer errLineNum; // 1-based
	public String soureName;
	public String srcCode;
	public TreeMap<Integer, String> interestingLines;
	public Integer errLinePosInInterestingLines;
	
	private TreeMap<Integer, String> getInterestingLines() {
		String[] lines = srcCode.split("\n");
		int size = lines.length;
		int start = errLineNum - 4;
		start = start > 0 ? start : 1;
		errLinePosInInterestingLines = errLineNum - start;
		int end = errLineNum + 4;
		end = end < size ? end : size;
		TreeMap<Integer, String> re = new TreeMap<Integer, String>();
		for (int i = start; i <= end; i++) {
			re.put(i, lines[i - 1]);
		}
		return re;
	}

	/**
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @param jce
	 * @return
	 */
	public static JapidTemplateException from(JapidCompilationException jce) {
		return new JapidTemplateException(
				"Japid Script Compiling Error",
				jce.getMessage(),
				jce.getLineNumber(),
				jce.getTemplateName(),
				jce.getTemplateSrc());
	}
}
