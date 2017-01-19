package fr.paris.lutece.plugins.appointment.business.rule;

import java.io.Serializable;

/**
 * Business Class of the rules of the form
 * 
 * @author Laurent Payen
 *
 */
public class FormRule implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -737984459576501946L;

	/**
	 * Id of the form rule.
	 */
	private int _nIdFormRule;

	/**
	 * Indicates whether the captcha is enabled or not
	 */
	private boolean _bIsCaptchaEnabled;

	/**
	 * Indicates whether the email is mandatory or not
	 */
	private boolean _bIsMandatoryEmailEnabled;

	/**
	 * Gets the id of the form rule
	 * 
	 * @return the id of the form rule
	 */
	public int getIdFormRule() {
		return _nIdFormRule;
	}

	/**
	 * Sets the id of the form rule
	 * 
	 * @param nIdFormRule
	 *            the id to set
	 */
	public void setIdFormRule(int nIdFormRule) {
		this._nIdFormRule = nIdFormRule;
	}

	/**
	 * Indicates if the captcha is enabled or not
	 * 
	 * @return true if the captcha is enabled
	 */
	public boolean isCaptchaEnabled() {
		return _bIsCaptchaEnabled;
	}

	/**
	 * Sets the boolean captcha value
	 * 
	 * @param bIsCaptchaEnabled
	 *            the boolean captcha value to set
	 */
	public void setIsCaptchaEnabled(boolean bIsCaptchaEnabled) {
		this._bIsCaptchaEnabled = bIsCaptchaEnabled;
	}

	/**
	 * Indicates wether the email is mandatory or not
	 * 
	 * @return true if the email is mandatory
	 */
	public boolean isMandatoryEmailEnabled() {
		return _bIsMandatoryEmailEnabled;
	}

	/**
	 * Sets the boolean value for the mandatory email
	 * 
	 * @param bIsMandatoryEmailEnabled
	 *            the boolean value for the mandatory email
	 */
	public void setIsMandatoryEmailEnabled(boolean bIsMandatoryEmailEnabled) {
		this._bIsMandatoryEmailEnabled = bIsMandatoryEmailEnabled;
	}

}
