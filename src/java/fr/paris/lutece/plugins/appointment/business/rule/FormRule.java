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
	 * Indicate whether the captcha is enabled or not
	 */
	private boolean _bIsCaptchaEnabled;

	/**
	 * Indicate whether the email is mandatory or not
	 */
	private boolean _bIsMandatoryEmailEnabled;

	/**
	 * True if the authentication is required
	 */
	private boolean _bIsActiveAuthentication;

	/**
	 * Nb Days before the user can take another appointment
	 */
	private int _nNbDaysBeforeNewAppointment;

	/**
	 * Minimum time from now before the user can take an appointment
	 */
	private int _nMinTimeBeforeAppointment;

	/**
	 * Form id (foreign key)
	 */
	private int _nIdForm;

	/**
	 * Get the id of the form rule
	 * 
	 * @return the id of the form rule
	 */
	public int getIdFormRule() {
		return _nIdFormRule;
	}

	/**
	 * Set the id of the form rule
	 * 
	 * @param nIdFormRule
	 *            the id to set
	 */
	public void setIdFormRule(int nIdFormRule) {
		this._nIdFormRule = nIdFormRule;
	}

	/**
	 * Indicate if the captcha is enabled or not
	 * 
	 * @return true if the captcha is enabled
	 */
	public boolean isCaptchaEnabled() {
		return _bIsCaptchaEnabled;
	}

	/**
	 * Set the boolean captcha value
	 * 
	 * @param bIsCaptchaEnabled
	 *            the boolean captcha value to set
	 */
	public void setIsCaptchaEnabled(boolean bIsCaptchaEnabled) {
		this._bIsCaptchaEnabled = bIsCaptchaEnabled;
	}

	/**
	 * Indicate whether the email is mandatory or not
	 * 
	 * @return true if the email is mandatory
	 */
	public boolean isMandatoryEmailEnabled() {
		return _bIsMandatoryEmailEnabled;
	}

	/**
	 * Set the boolean value for the mandatory email
	 * 
	 * @param bIsMandatoryEmailEnabled
	 *            the boolean value for the mandatory email
	 */
	public void setIsMandatoryEmailEnabled(boolean bIsMandatoryEmailEnabled) {
		this._bIsMandatoryEmailEnabled = bIsMandatoryEmailEnabled;
	}

	/**
	 * Indicate whether the authentication is required or not
	 * 
	 * @return true if the authentication is required
	 */
	public boolean isActiveAuthentication() {
		return _bIsActiveAuthentication;
	}

	/**
	 * Set the boolean value for the authentication
	 * 
	 * @param bIsActiveAuthentication
	 *            the boolean value for the authentication
	 */
	public void setIsActiveAuthentication(boolean bIsActiveAuthentication) {
		this._bIsActiveAuthentication = bIsActiveAuthentication;
	}

	/**
	 * Get the number of days the user has to wait before he can take another
	 * appointment
	 * 
	 * @return the number of days
	 */
	public int getNbDaysBeforeNewAppointment() {
		return _nNbDaysBeforeNewAppointment;
	}

	/**
	 * Set the number of days the user have to wait before he can take another
	 * appointment
	 * 
	 * @param _nNbDaysBeforeNewAppointment
	 *            the number of days
	 */
	public void setNbDaysBeforeNewAppointment(int nNbDaysBeforeNewAppointment) {
		this._nNbDaysBeforeNewAppointment = nNbDaysBeforeNewAppointment;
	}

	/**
	 * Get the minimal time from now before the user can take an appointment
	 * 
	 * @return The minimal time in hours
	 */
	public int getMinTimeBeforeAppointment() {
		return _nMinTimeBeforeAppointment;
	}

	/**
	 * Set the minimal time from now before the user can take an appointment
	 * 
	 * @param nMinTimeBeforeAppointment
	 *            the minimal time in hours
	 */
	public void setMinTimeBeforeAppointment(int nMinTimeBeforeAppointment) {
		this._nMinTimeBeforeAppointment = nMinTimeBeforeAppointment;
	}

	/**
	 * Get the form id the formRule belongs to
	 * 
	 * @return the form id
	 */
	public int getIdForm() {
		return _nIdForm;
	}

	/**
	 * Set the form id the formRule belongs to
	 * 
	 * @param nIdForm
	 *            the form id to set
	 */
	public void setIdForm(int nIdForm) {
		this._nIdForm = nIdForm;
	}

}
