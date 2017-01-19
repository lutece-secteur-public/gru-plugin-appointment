package fr.paris.lutece.plugins.appointment.business.rule;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Business class of the rules of the reservation
 * 
 * @author Laurent Payen
 *
 */
public class ReservationRule implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -5154752950203822668L;

	/**
	 * Id of the reservation rule.
	 */
	private int _nIdReservationRule;

	/**
	 * Date from which the rule has to be applied
	 */
	private LocalDate _dateOfApply;

	/**
	 * Maximum capacity for a slot
	 */
	private int _nMaxCapacityPerSlot;

	/**
	 * Maximum number of people authorized for an appointment
	 */
	private int _nMaxPeoplePerAppointment;

	/**
	 * Gets the id of the rule of the reservation
	 * 
	 * @return the id of the rule of the reservation
	 */
	public int getIdReservationRule() {
		return _nIdReservationRule;
	}

	/**
	 * Sets the id of the rule of the reservation
	 * 
	 * @param nIdReservationRule
	 *            the id to set
	 */
	public void setIdReservationRule(int nIdReservationRule) {
		this._nIdReservationRule = nIdReservationRule;
	}

	/**
	 * Gets the date from which the rule has to be applied
	 * 
	 * @return the date from which the rule has to be applied
	 */
	public LocalDate getDateOfApply() {
		return _dateOfApply;
	}

	/**
	 * Sets the date from which the rule has to be applied
	 * 
	 * @param dateOfApply
	 *            the date to set
	 */
	public void setDateOfApply(LocalDate dateOfApply) {
		this._dateOfApply = dateOfApply;
	}

	/**
	 * Gets the maximum capacity for a slot
	 * 
	 * @return the maximum capacity for a slot
	 */
	public int getMaxCapacityPerSlot() {
		return _nMaxCapacityPerSlot;
	}

	/**
	 * Sets the maximum capacity for a slot
	 * 
	 * @param nMaxCapacityPerSlot
	 *            the maximum capacity for a slot
	 */
	public void setMaxCapacityPerSlot(int nMaxCapacityPerSlot) {
		this._nMaxCapacityPerSlot = nMaxCapacityPerSlot;
	}

	/**
	 * Gets the maximum number of people authorized for an appointment
	 * 
	 * @return the maximum number of people authorized for an appointment
	 */
	public int getMaxPeoplePerAppointment() {
		return _nMaxPeoplePerAppointment;
	}

	/**
	 * Sets the maximum number of people authorized for an appointment
	 * 
	 * @param nMaxPeoplePerAppointment
	 *            the maximum of people to set
	 */
	public void setMaxPeoplePerAppointment(int nMaxPeoplePerAppointment) {
		this._nMaxPeoplePerAppointment = nMaxPeoplePerAppointment;
	}

}
