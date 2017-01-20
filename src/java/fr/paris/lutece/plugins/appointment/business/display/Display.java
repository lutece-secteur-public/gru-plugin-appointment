package fr.paris.lutece.plugins.appointment.business.display;

import java.io.Serializable;

import fr.paris.lutece.portal.service.image.ImageResource;

/**
 * Business class of the Form Display
 * 
 * @author Laurent Payen
 *
 */
public class Display implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -4827729906365306894L;

	/**
	 * Display Id
	 */
	private int _nIdDisplay;

	/**
	 * Indicate whether the title is displayed in the front office or not
	 */
	private boolean _bIsDisplayTitleFo;

	/**
	 * Form Icon
	 */
	private ImageResource _icon;

	/**
	 * Number of weeks during which the form is displayed to the user
	 */
	private int _nbWeeksToDisplay;

	/**
	 * Calendar Template Id of the Display Form (foreign key)
	 */
	private int _nIdCalendarTemplate;

	/**
	 * Form id (foreign key)
	 */
	private int _nIdForm;
	/**
	 * Get the Display Id
	 * 
	 * @return the Display Id
	 */
	public int getIdDisplay() {
		return _nIdDisplay;
	}

	/**
	 * Set the Display Id
	 * 
	 * @param _nIdDisplay
	 *            the Id to set
	 */
	public void setIdDisplay(int nIdDisplay) {
		this._nIdDisplay = nIdDisplay;
	}

	/**
	 * Get the display title value for the front office form
	 * 
	 * @return true if the title has to be displayed
	 */
	public boolean isDisplayTitleFo() {
		return _bIsDisplayTitleFo;
	}

	/**
	 * Set the display title boolean value
	 * 
	 * @param displayTitleFo
	 *            the boolean display title value to set
	 */
	public void setDisplayTitleFo(boolean bIsDisplayTitleFo) {
		this._bIsDisplayTitleFo = bIsDisplayTitleFo;
	}

	/**
	 * Get the form icon
	 * 
	 * @return the form icon
	 */
	public ImageResource getIcon() {
		return _icon;
	}

	/**
	 * Set the form icon
	 * 
	 * @param _icon
	 *            the icon to set
	 */
	public void setIcon(ImageResource _icon) {
		this._icon = _icon;
	}

	/**
	 * Get the number of weeks during which the form is displayed to the user
	 * 
	 * @return the number of weeks
	 */
	public int getNbWeeksToDisplay() {
		return _nbWeeksToDisplay;
	}

	/**
	 * Set the number of weeks during which the form is displayed to the user
	 * 
	 * @param nbWeeksToDisplay
	 *            the number of weeks to set
	 */
	public void setNbWeeksToDisplay(int nbWeeksToDisplay) {
		this._nbWeeksToDisplay = nbWeeksToDisplay;
	}

	/**
	 * Get the Calendar Template Id
	 * 
	 * @return the Calendar Template Id
	 */
	public int getIdCalendarTemplate() {
		return _nIdCalendarTemplate;
	}

	/**
	 * Set the Calendar Template Id
	 * 
	 * @param nIdCalendarTemplate
	 *            the Calendar Template Id to set
	 */
	public void setIdCalendarTemplate(int nIdCalendarTemplate) {
		this._nIdCalendarTemplate = nIdCalendarTemplate;
	}

	/**
	 * Get the Form Id
	 * @return the Form Id
	 */
	public int getIdForm() {
		return _nIdForm;
	}

	/**
	 * Set the FOrm Id
	 * @param nIdForm the Form Id to set
	 */
	public void setIdForm(int nIdForm) {
		this._nIdForm = nIdForm;
	}

}
