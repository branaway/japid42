/**
 * 
 */
package cn.bran.play.exceptions;

import org.apache.commons.mail.EmailException;


/**
 * @author bran
 *
 */
public class MailException extends RuntimeException {
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public MailException(String msg) {
		super(msg);
	}

	/**
	 * @param string
	 * @param ex
	 */
	public MailException(String string, Throwable ex) {
		super(string, ex);
	}

	private static final long serialVersionUID = -6643871086991262388L;

}
