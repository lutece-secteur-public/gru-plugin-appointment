package fr.paris.lutece.plugins.appointment.business.localization;

import java.io.Serializable;

/**
 * Business class of the Form Display
 * 
 * @author Laurent Payen
 *
 */
public class Localization implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 611153651324369773L;

	/**
	 * Localization Id
	 */
	private int _nIdLocalization;

	/**
	 * Longitude
	 */
	private Double _dLongitude;

	/**
	 * Latitude
	 */
	private Double _dLatitude;

	/**
	 * Address
	 */
	private String _strAddress;

	/**
	 * Form id (foreign key)
	 */
	private int _nIdForm;

	/**
	 * Get the Id Localization
	 * 
	 * @return the id of the localization
	 */
	public int getIdLocalization() {
		return _nIdLocalization;
	}

	/**
	 * Set the id of the localization
	 * 
	 * @param nIdLocalization
	 *            the of the localization
	 */
	public void setIdLocalization(int nIdLocalization) {
		this._nIdLocalization = nIdLocalization;
	}

	/**
	 * Get the longitude
	 * 
	 * @return the longitude
	 */
	public Double getLongitude() {
		return _dLongitude;
	}

	/**
	 * Set the longitude
	 * 
	 * @param dLongitude
	 *            the longitude
	 */
	public void setLongitude(Double dLongitude) {
		this._dLongitude = dLongitude;
	}

	/**
	 * Get the latitude
	 * 
	 * @return the latitude
	 */
	public Double getLatitude() {
		return _dLatitude;
	}

	/**
	 * Set the latitude
	 * 
	 * @param dLatitude
	 *            the latitude
	 */
	public void setLatitude(Double dLatitude) {
		this._dLatitude = dLatitude;
	}

	/**
	 * Get the address of the form
	 * 
	 * @return the address
	 */
	public String getAddress() {
		return _strAddress;
	}

	/**
	 * Set the address of the form
	 * 
	 * @param strAddress
	 */
	public void setAddress(String strAddress) {
		this._strAddress = strAddress;
	}

	/**
	 * Get the Form Id
	 * 
	 * @return the Form Id
	 */
	public int getIdForm() {
		return _nIdForm;
	}

	/**
	 * Set the Form Id
	 * 
	 * @param nIdForm
	 *            the Form Id to set
	 */
	public void setIdForm(int nIdForm) {
		this._nIdForm = nIdForm;
	}

}
