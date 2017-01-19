package fr.paris.lutece.plugins.appointment.business.form;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Business class of the form
 * 
 * @author Laurent Payen
 *
 */
public class Form implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 4742702767509625292L;

	/**
	 * Id of the form
	 */
	private int _nIdForm;

	/**
	 * Title of the form
	 */
	private String _strTitle;

	/**
	 * Description of the form
	 */
	private String _strDescription;

	/**
	 * Reference of the form
	 */
	private String _strReference;

	/**
	 * Starting validity date of the form
	 */
	private LocalDate _startingValidityDate;

	/**
	 * Ending validity date of the form
	 */
	private LocalDate _endingValidityDate;

	/**
	 * Indicates whether the form is active or not
	 */
	private boolean _bIsActive;

	/**
	 * Gets the id of the form
	 * 
	 * @return the id of the form
	 */
	public int getIdForm() {
		return _nIdForm;
	}

	/**
	 * Sets the id of the form
	 * 
	 * @param nIdForm
	 *            the id to set
	 */
	public void setIdForm(int nIdForm) {
		this._nIdForm = nIdForm;
	}

	/**
	 * Gets the title of the form
	 * 
	 * @return the title of the form
	 */
	public String getTitle() {
		return _strTitle;
	}

	/**
	 * Sets the title of the form
	 * 
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String strTitle) {
		this._strTitle = strTitle;
	}

	/**
	 * Gets the description of the form
	 * 
	 * @return the description of the form
	 */
	public String getDescription() {
		return _strDescription;
	}

	/**
	 * Sets the description of the form
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String strDescription) {
		this._strDescription = strDescription;
	}

	/**
	 * Gets the reference of the form
	 * 
	 * @return the reference of the form
	 */
	public String getReference() {
		return _strReference;
	}

	/**
	 * Sets the reference of the form
	 * 
	 * @param reference
	 *            the reference to set
	 */
	public void setReference(String strReference) {
		this._strReference = strReference;
	}

	/**
	 * Gets the start date of the validity of the form
	 * 
	 * @return the start validity date of the form
	 */
	public LocalDate getStartingValidityDate() {
		return _startingValidityDate;
	}

	/**
	 * Sets the start date of the validity of the form
	 * 
	 * @param startValidity
	 *            the start validity date to set
	 */
	public void setStartingValidityDate(LocalDate startingValidityDate) {
		this._startingValidityDate = startingValidityDate;
	}

	/**
	 * Gets the end date of the validity of the form
	 * 
	 * @return the end validity date of the form
	 */
	public LocalDate getEndingValidityDate() {
		return _endingValidityDate;
	}

	/**
	 * Sets the end date of the validity of the form
	 * 
	 * @param endValidity
	 *            the end validity date to set
	 */
	public void setEndValidity(LocalDate endingValidityDate) {
		this._endingValidityDate = endingValidityDate;
	}

	/**
	 * Indicates if the form is active or not
	 * 
	 * @return true if the form is open
	 */
	public boolean isActive() {
		return _bIsActive;
	}

	/**
	 * Sets the active boolean value of the form
	 * 
	 * @param isActive
	 *            the boolean active value to set
	 */
	public void setIsActive(boolean bIsActive) {
		this._bIsActive = bIsActive;
	}

}
