package fr.paris.lutece.plugins.appointment.business.appointment;

import java.io.Serializable;

/**
 * Business class of the appointment
 * 
 * @author Laurent Payen
 *
 */
public class Appointment implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -132212832777629802L;

	/**
	 * Appointment Id
	 */
	private int _nIdAppointment;

	/**
	 * User id
	 */
	private int _nIdUser;

	/**
	 * Slot id
	 */
	private int _nIdSlot;

	/**
	 * Get the Appointment Id
	 * 
	 * @return the Appointment Id
	 */
	public int getIdAppointment() {
		return _nIdAppointment;
	}

	/**
	 * Set the Appointment Id
	 * 
	 * @param nIdAppointment
	 *            the Appointment Id to set
	 */
	public void setIdAppointment(int nIdAppointment) {
		this._nIdAppointment = nIdAppointment;
	}

	/**
	 * Get the User Id of the Appointment
	 * 
	 * @return the User Id of the Appointment
	 */
	public int getIdUser() {
		return _nIdUser;
	}

	/**
	 * Set the User Id of the Appointment
	 * 
	 * @param nIdUser
	 *            the User Id of the Appointment
	 */
	public void setUser(int nIdUser) {
		this._nIdUser = nIdUser;
	}

	/**
	 * Get the Slot Id of the Appointment
	 * 
	 * @return the Slot Id of the Appointment
	 */
	public int getIdSlot() {
		return _nIdSlot;
	}

	/**
	 * Set the slot id of the appointment
	 * 
	 * @param nIdSlot
	 *            the slot id of the appointment
	 */
	public void setIdSlot(int nIdSlot) {
		this._nIdSlot = nIdSlot;
	}

}
