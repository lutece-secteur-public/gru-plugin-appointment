package fr.paris.lutece.plugins.appointment.business.form;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.portal.service.rbac.RBACResource;

/**
 * Business class of the Form
 * 
 * @author Laurent Payen
 *
 */
public class Form implements RBACResource, Cloneable, Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 4742702767509625292L;

	/**
	 * Name of the resource type of Appointment Forms
	 */
	public static final String RESOURCE_TYPE = "APPOINTMENT_FORM";

	/**
	 * Form Id
	 */
	private int _nIdForm;

	/**
	 * Title of the form
	 */
	@NotBlank(message = "#i18n{appointment.validation.appointmentform.Title.notEmpty}")
	@Size(max = 255, message = "#i18n{appointment.validation.appointmentform.Title.size}")
	private String _strTitle;

	/**
	 * Description of the form
	 */
	@NotBlank(message = "#i18n{appointment.validation.appointmentform.Description.notEmpty}")
	private String _strDescription;

	/**
	 * Reference of the form
	 */
	private String _strReference;

	/**
	 * Category of the form
	 */
	private String _strCategory;

	/**
	 * Starting validity date of the form
	 */
	private LocalDate _startingValidityDate;

	/**
	 * Ending validity date of the form
	 */
	private LocalDate _endingValidityDate;

	/**
	 * Indicate whether the form is active or not
	 */
	private boolean _bIsActive;

	/**
	 * Workflow Id
	 */
	private int _nIdWorkflow;

	/**
	 * List of the week definitions of the form
	 */
	private List<WeekDefinition> _listWeekDefinition;

	/**
	 * Get the form Id
	 * 
	 * @return the form Id
	 */
	public int getIdForm() {
		return _nIdForm;
	}

	/**
	 * Set the form Id
	 * 
	 * @param nIdForm
	 *            the Id to set
	 */
	public void setIdForm(int nIdForm) {
		this._nIdForm = nIdForm;
	}

	/**
	 * Get the title of the form
	 * 
	 * @return the form title
	 */
	public String getTitle() {
		return _strTitle;
	}

	/**
	 * Set the form title
	 * 
	 * @param title
	 *            the Title to set
	 */
	public void setTitle(String strTitle) {
		this._strTitle = strTitle;
	}

	/**
	 * Get the description of the form
	 * 
	 * @return the description of the form
	 */
	public String getDescription() {
		return _strDescription;
	}

	/**
	 * Set the description of the form
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String strDescription) {
		this._strDescription = strDescription;
	}

	/**
	 * Get the reference of the form
	 * 
	 * @return the reference of the form
	 */
	public String getReference() {
		return _strReference;
	}

	/**
	 * Set the reference of the form
	 * 
	 * @param reference
	 *            the reference to set
	 */
	public void setReference(String strReference) {
		this._strReference = strReference;
	}

	/**
	 * Get the category of the form
	 * 
	 * @return the category of the form
	 */
	public String getCategory() {
		return _strCategory;
	}

	/**
	 * Set the category of the form
	 * 
	 * @param strCategory
	 *            the category to set
	 */
	public void setCategory(String strCategory) {
		this._strCategory = strCategory;
	}

	/**
	 * Get the starting validity date of the form (in LocalDate format)
	 * 
	 * @return the starting validity date of the form
	 */
	public LocalDate getStartingValidityDate() {
		return _startingValidityDate;
	}

	/**
	 * Get the starting validity date of the form (in sql date format)
	 * 
	 * @return the starting validity date
	 */
	public Date getStartingValiditySqlDate() {
		Date date = null;
		if (_startingValidityDate != null) {
			date = Date.valueOf(_startingValidityDate);
		}
		return date;
	}

	/**
	 * Set the starting date of the validity of the form
	 * 
	 * @param startValidity
	 *            the starting validity date to set
	 */
	public void setStartingValidityDate(LocalDate startingValidityDate) {
		this._startingValidityDate = startingValidityDate;
	}

	/**
	 * Set the starting validity date of the form
	 * 
	 * @param startingValidityDate
	 *            the starting validity date to set (in sql Date format)
	 */
	public void setStartingValiditySqlDate(Date startingValidityDate) {
		if (startingValidityDate != null) {
			this._startingValidityDate = startingValidityDate.toLocalDate();
		} else {
			this._startingValidityDate = null;
		}
	}

	/**
	 * Get the end date of the validity of the form
	 * 
	 * @return the end validity date of the form
	 */
	public LocalDate getEndingValidityDate() {
		return _endingValidityDate;
	}

	/**
	 * Get the ending validity date of the form (in sql date format)
	 * 
	 * @return the ending validity date
	 */
	public Date getEndingValiditySqlDate() {
		Date date = null;
		if (_endingValidityDate != null) {
			date = Date.valueOf(_endingValidityDate);
		}
		return date;
	}

	/**
	 * Set the end date of the validity of the form
	 * 
	 * @param endValidity
	 *            the end validity date to set
	 */
	public void setEndingValidityDate(LocalDate endingValidityDate) {
		this._endingValidityDate = endingValidityDate;
	}

	/**
	 * Set the ending validity date of the form
	 * 
	 * @param endingValidityDate
	 *            the ending validity date to set (in sql Date format)
	 */
	public void setEndingValiditySqlDate(Date endingValidityDate) {
		if (endingValidityDate != null) {
			this._endingValidityDate = endingValidityDate.toLocalDate();
		} else {
			this._endingValidityDate = null;
		}
	}

	/**
	 * Returns the IsActive
	 * 
	 * @return The IsActive
	 */
	public boolean isActive() {
		return _bIsActive;
	}

	/**
	 * Set the active boolean value of the form
	 * 
	 * @param isActive
	 *            the boolean active value to set
	 */
	public void setIsActive(boolean bIsActive) {
		this._bIsActive = bIsActive;
	}

	/**
	 * Get the workflow id
	 * 
	 * @return the workflow id
	 */
	public int getIdWorkflow() {
		return _nIdWorkflow;
	}

	/**
	 * Set the workflow Id
	 * 
	 * @param nIdWorkflow
	 *            the workflow id to set
	 */
	public void setIdWorkflow(int nIdWorkflow) {
		this._nIdWorkflow = nIdWorkflow;
	}

	/**
	 * Get all the week definition of the form
	 * 
	 * @return a list of the week definitions
	 */
	public List<WeekDefinition> getListWeekDefinition() {
		return _listWeekDefinition;
	}

	/**
	 * Set all the week definitions of the form
	 * 
	 * @param listWeekDefinitions
	 *            the list to set
	 */
	public void setListWeekDefinition(List<WeekDefinition> listWeekDefinition) {
		this._listWeekDefinition = listWeekDefinition;
	}

	@Override
	public String getResourceTypeCode() {
		return RESOURCE_TYPE;
	}

	@Override
	public String getResourceId() {
		return Integer.toString(getIdForm());
	}

}
