package fr.paris.lutece.plugins.appointment.business.planningdefinition;

import java.io.Serializable;
import java.sql.Date;
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
	 * Get the id of the closing day
	 * 
	 * @return the id of the closing day
	 */
	public int getIdClosingDay() {
		return _nIdClosingDay;
	}

	/**
	 * Set the id of the closing day
	 * 
	 * @param nIdClosingDay
	 *            the id to set
	 */
	public void setIdClosingDay(int nIdClosingDay) {
		this._nIdClosingDay = nIdClosingDay;
	}

	/**
	 * Get the date of the closing day
	 * 
	 * @return the date of the closing day
	 */
	public LocalDate getDateOfClosingDay() {
		return _dateOfClosingDay;
	}
	
	/**
	 * Get the date of the closing day (in sql date format)
	 * 
	 * @return the date of the closing day
	 */
	public Date getSqlDateOfClosingDay() {
		Date date = null;
		if (_dateOfClosingDay != null){
			date = Date.valueOf(_dateOfClosingDay);
		}
		return date;
	}

	/**
	 * Set the date of the closing day
	 * 
	 * @param dateOfClosingDay
	 *            the date to set
	 */
	public void setDateOfClosingDay(LocalDate dateOfClosingDay) {
		this._dateOfClosingDay = dateOfClosingDay;
	}

	/**
	 * Set the date of the closing day
	 * 
	 * @param dateOfClosingDay
	 *            the date to set (in sql date format)
	 */
	public void setDateOfClosingDay(Date dateOfClosingDay) {
		if (dateOfClosingDay != null) {
			this._dateOfClosingDay = dateOfClosingDay.toLocalDate();
		}
	}

	/**
	 * Get the id of the form the closing day belongs to
	 * 
	 * @return the id of the form the closing day belongs to
	 */
	public int getIdForm() {
		return _nIdForm;
	}

	/**
	 * Set the form the closing day belongs to
	 * 
	 * @param nIdForm
	 *            the if form to set
	 */
	public void setIdForm(int nIdForm) {
		this._nIdForm = nIdForm;
	}

}
