package fr.paris.lutece.plugins.appointment.business.display;

import java.io.Serializable;

import fr.paris.lutece.portal.service.image.ImageResource;

/**
 * Business class of the display of the form
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
	 * Id of the display
	 */
	private int _nIdDisplay;

	/**
	 * Indicates whether the title is displayed in the front office or not
	 */
	private boolean _bIsDisplayTitleFo;

	/**
	 * Icon of the form
	 */
	private ImageResource _icon;

	/**
	 * Number of weeks during which the form is displayed to the user
	 */
	private int _nbWeeksToDisplay;

	/**
	 * Calendar template id of the form to display
	 */
	private int _nIdCalendarTemplate;

	/**
	 * Gets the id of the display of the form
	 * 
	 * @return the id of the display
	 */
	public int getNIdDisplay() {
		return _nIdDisplay;
	}

	/**
	 * Sets the id of the display of the form
	 * 
	 * @param _nIdDisplay
	 *            the id to set
	 */
	public void setNIdDisplay(int nIdDisplay) {
		this._nIdDisplay = nIdDisplay;
	}

	/**
	 * Gets the display title value for the front office form
	 * 
	 * @return true if the title has to be displayed
	 */
	public boolean isDisplayTitleFo() {
		return _bIsDisplayTitleFo;
	}

	/**
	 * Sets the display title boolean value
	 * 
	 * @param displayTitleFo
	 *            the boolean display title value to set
	 */
	public void setDisplayTitleFo(boolean bIsDisplayTitleFo) {
		this._bIsDisplayTitleFo = bIsDisplayTitleFo;
	}

	/**
	 * Gets the icon of the form
	 * 
	 * @return the icon of the form
	 */
	public ImageResource getIcon() {
		return _icon;
	}

	/**
	 * Sets the icon of the form
	 * 
	 * @param _icon
	 *            the icon to set
	 */
	public void setIcon(ImageResource _icon) {
		this._icon = _icon;
	}

	/**
	 * Gets the number of weeks during which the form is displayed to the user
	 * 
	 * @return the number of weeks
	 */
	public int getNbWeeksToDisplay() {
		return _nbWeeksToDisplay;
	}

	/**
	 * Sets the number of weeks during which the form is displayed to the user
	 * 
	 * @param nbWeeksToDisplay
	 *            the number of weeks to set
	 */
	public void setNbWeeksToDisplay(int nbWeeksToDisplay) {
		this._nbWeeksToDisplay = nbWeeksToDisplay;
	}

	/**
	 * Gets the calendar template id of the form
	 * 
	 * @return the calendar template id of the form
	 */
	public int getIdCalendarTemplate() {
		return _nIdCalendarTemplate;
	}

	/**
	 * Sets the calendar template id of the form
	 * 
	 * @param nIdCalendarTemplate
	 *            the calendar template id to set
	 */
	public void setIdCalendarTemplate(int nIdCalendarTemplate) {
		this._nIdCalendarTemplate = nIdCalendarTemplate;
	}

}
