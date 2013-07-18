/**
 * 
 */
package cn.bran.japid.template;

/**
 * @author bran
 *
 */
public class JapidTemplateNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -9156139486804972090L;
	private String templateName;
	private String searchingPath;
	public JapidTemplateNotFoundException(String templateName, String searchingPath) {
		super("Japid template not found: " + templateName + ". Searching path was: " + searchingPath);
		this.templateName = templateName;
		this.searchingPath = searchingPath;
	}
	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}
	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	/**
	 * @return the searchingPath
	 */
	public String getSearchingPath() {
		return searchingPath;
	}
	/**
	 * @param searchingPath the searchingPath to set
	 */
	public void setSearchingPath(String searchingPath) {
		this.searchingPath = searchingPath;
	}
	
}
