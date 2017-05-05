package fr.paris.lutece.plugins.appointment.business.appointment;

import java.io.Serializable;

import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;

/**
 * Business class of the Appointment
 * 
 * @author Laurent Payen
 *
 */
public class Appointment implements Serializable {

	/**
	 * Appointment resource type
	 */
	public static final String APPOINTMENT_RESOURCE_TYPE = "appointment";

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -132212832777629802L;

	/**
	 * Appointment Id
	 */
	private int _nIdAppointment;

	/**
	 * Reference of the Appointment
	 */
	private String _strReference;

	/**
	 * Number of places for the appointment
	 */
	private int _nNbPlaces;
	/**
	 * Tell if the appointment is cancelled or not
	 */
	private boolean _bIsCancelled;

	/**
	 * Id for a cancelled appointment 
	 */
	private int _nIdActionCancelled;
	
	/**
	 * User id
	 */
	private int _nIdUser;

	/**
	 * Slot id
	 */
	private int _nIdSlot;

	/**
	 * The slot on which the appointment is
	 */
	private Slot _slot;

	/**
	 * The user of the appointment
	 */
	private User _user;

	/**
	 * Get the reference of the appointment
	 * 
	 * @return the reference
	 */
	public String getReference() {
		return _strReference;
	}

	/**
	 * Set the reference of the appointment
	 * 
	 * @param strReference
	 *            the reference to set
	 */
	public void setReference(String strReference) {
		this._strReference = strReference;
	}

	/**
	 * Get the number of places of the appointment
	 * 
	 * @return the number of places
	 */
	public int getNbPlaces() {
		return _nNbPlaces;
	}

	/**
	 * Set the number of places for the appointment
	 * 
	 * @param nNbPlaces
	 *            the number of places to set
	 */
	public void setNbPlaces(int nNbPlaces) {
		this._nNbPlaces = nNbPlaces;
	}

	/**
	 * Get if the appointment is cancelled
	 * 
	 * @return true if the appointment is cancelled
	 */
	public boolean getIsCancelled() {
		return _bIsCancelled;
	}

	/**
	 * Set if the appointment is cancelled
	 * 
	 * @param bIsCancelled
	 *            the boolean value to set
	 */
	public void setIsCancelled(boolean bIsCancelled) {
		this._bIsCancelled = bIsCancelled;
	}

	/**
	 * Get the id for the cancelled appointment
	 * @return the id
	 */
	public int getIdActionCancelled() {
		return _nIdActionCancelled;
	}

	/**
	 * Set the id for the cancelled action
	 * @param _nIdActionCancelled the id to set
	 */
	public void setIdActionCancelled(int _nIdActionCancelled) {
		this._nIdActionCancelled = _nIdActionCancelled;
	}

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
	public void setIdUser(int nIdUser) {
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
	 * Set the Slot Id of the Appointment
	 * 
	 * @param nIdSlot
	 *            the Slot Id of the Appointment
	 */
	public void setIdSlot(int nIdSlot) {
		this._nIdSlot = nIdSlot;
	}

	/**
	 * Get the slot of the appointment
	 * 
	 * @return the slot
	 */
	public Slot getSlot() {
		return _slot;
	}

	/**
	 * Set the slot of the appointment
	 * 
	 * @param slot
	 *            the slot to set
	 */
	public void setSlot(Slot slot) {
		this._slot = slot;
	}

	/**
	 * Get the user of the appointment
	 * 
	 * @return the user
	 */
	public User getUser() {
		return _user;
	}

	/**
	 * Set the user of the appointment
	 * 
	 * @param user
	 *            the user
	 */
	public void setUser(User user) {
		this._user = user;
	}

}
