package fr.paris.lutece.plugins.appointment.business.planningdefinition;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Business class of the definition week
 * 
 * @author Laurent Payen
 *
 */
public class WeekDefinition implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 4292654762871322318L;

	/**
	 * Id of the week definition
	 */
	private int _nIdWeekDefinition;

	/**
	 * Date from which the week definition has to be applied
	 */
	private LocalDate _dateOfApply;

	/**
	 * The id of the form
	 */
	private int _nIdForm;

	/**
	 * List of the working days that define the week definition
	 */
	private List<WorkingDay> _listWorkingDays;

	/**
	 * Gets the id of the week definition
	 * 
	 * @return the id of the week definition
	 */
	public int getIdWeekDefinition() {
		return _nIdWeekDefinition;
	}

	/**
	 * Sets the id of the week definition
	 * 
	 * @param _nIdWeekDefinition
	 *            the id to set
	 */
	public void setIdWeekDefinition(int nIdWeekDefinition) {
		this._nIdWeekDefinition = nIdWeekDefinition;
	}

	/**
	 * Gets the date from which the week definition has to be applied
	 * 
	 * @return the date from which the week definition has to be applied
	 */
	public LocalDate getDateOfApply() {
		return _dateOfApply;
	}

	/**
	 * Sets the date from which the week definition has to be applied
	 * 
	 * @param _dateOfApply
	 *            the date to set
	 */
	public void setDateOfApply(LocalDate dateOfApply) {
		this._dateOfApply = dateOfApply;
	}

	/**
	 * Gets the form id the week definition belongs to
	 * 
	 * @return the form id
	 */
	public int getIdForm() {
		return _nIdForm;
	}

	/**
	 * Sets the form id the week definition belongs to
	 * 
	 * @param nIdForm
	 *            the form id to set
	 */
	public void setIdForm(int nIdForm) {
		this._nIdForm = nIdForm;
	}

	/**
	 * Gets the list of the working days of the week
	 * 
	 * @return the list of the working days for the week
	 */
	public List<WorkingDay> getWorkingDays() {
		return _listWorkingDays;
	}

	/**
	 * Set the working days for the week
	 * 
	 * @param _listWorkingDays
	 *            the list o f working days to set
	 */
	public void setWorkingDays(List<WorkingDay> listWorkingDays) {
		this._listWorkingDays = listWorkingDays;
	}

}
