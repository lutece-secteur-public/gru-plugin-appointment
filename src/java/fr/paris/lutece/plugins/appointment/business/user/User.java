package fr.paris.lutece.plugins.appointment.business.user;

import java.io.Serializable;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;

/**
 * Business class of the user
 * 
 * @author Laurent Payen
 *
 */
public class User implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -5088753000751258184L;

	/**
	 * Id of the user
	 */
	private int _nIdUser;

	/**
	 * Lutece User Id
	 */
	private int _nIdLuteceUser;

	/**
	 * First name of the user
	 */
	private String _strFirstName;

	/**
	 * Last name of the user
	 */
	private String _strLastName;

	/**
	 * Email of the user
	 */
	private String _strEmail;

	/**
	 * Phone number of the user
	 */
	private String _strPhoneNumber;

	/**
	 * Appointments of the user
	 */
	private List<Appointment> _listAppointments;

	public int getIdUser() {
		return _nIdUser;
	}

	public void setIdUser(int nIdUser) {
		this._nIdUser = nIdUser;
	}

	public int getIdLuteceUser() {
		return _nIdLuteceUser;
	}

	public void setIdLuteceUser(int nIdLuteceUser) {
		this._nIdLuteceUser = nIdLuteceUser;
	}

	public String getFirstName() {
		return _strFirstName;
	}

	public void setFirstName(String strFirstName) {
		this._strFirstName = strFirstName;
	}

	public String getLastName() {
		return _strLastName;
	}

	public void setLastName(String strLastName) {
		this._strLastName = strLastName;
	}

	public String getEmail() {
		return _strEmail;
	}

	public void setEmail(String strEmail) {
		this._strEmail = strEmail;
	}

	public String getPhoneNumber() {
		return _strPhoneNumber;
	}

	public void setPhoneNumber(String strPhoneNumber) {
		this._strPhoneNumber = strPhoneNumber;
	}

	public List<Appointment> getAppointments() {
		return _listAppointments;
	}

	public void setAppointments(List<Appointment> listAppointments) {
		this._listAppointments = listAppointments;
	}

}
