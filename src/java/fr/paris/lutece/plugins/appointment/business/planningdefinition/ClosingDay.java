package fr.paris.lutece.plugins.appointment.business.planningdefinition;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Business class of the closing day
 * 
 * @author Laurent Payen
 *
 */
public class ClosingDay implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -7399500588272139256L;

	/**
	 * Id of the closing day
	 */
	private int _nIdClosingDay;

	/**
	 * Date of the closing day
	 */
	private LocalDate _dateOfClosingDay;

	/**
	 * Id of the form the closing day belongs to
	 */
	private int _nIdForm;

	/**
	 * Gets the id of the closing day
	 * 
	 * @return the id of the closing day
	 */
	public int getIdClosingDay() {
		return _nIdClosingDay;
	}

	/**
	 * Sets the id of the closing day
	 * 
	 * @param _nIdClosingDay
	 *            the id to set
	 */
	public void setIdClosingDay(int nIdClosingDay) {
		this._nIdClosingDay = nIdClosingDay;
	}

	/**
	 * Gets the date of the closing day
	 * 
	 * @return the date of the closing day
	 */
	public LocalDate getDateOfClosingDay() {
		return _dateOfClosingDay;
	}

	/**
	 * Sets the date of the closing day
	 * 
	 * @param _dateOfClosingDay
	 *            the date to set
	 */
	public void setDateOfClosingDay(LocalDate dateOfClosingDay) {
		this._dateOfClosingDay = dateOfClosingDay;
	}

	/**
	 * Gets the id of the form the closing day belongs to
	 * 
	 * @return the id of the form the closing day belongs to
	 */
	public int getIdForm() {
		return _nIdForm;
	}

	/**
	 * Sets the form the closing day belongs to
	 * 
	 * @param _nIdForm
	 *            the if form to set
	 */
	public void setIdForm(int nIdForm) {
		this._nIdForm = nIdForm;
	}

}
