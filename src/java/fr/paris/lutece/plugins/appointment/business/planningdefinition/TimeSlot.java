package fr.paris.lutece.plugins.appointment.business.planningdefinition;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * Business class of the time slot
 * 
 * @author Laurent Payen
 *
 */
public class TimeSlot implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 3543470088706843432L;

	/**
	 * Id of the time slot
	 */
	private int _nIdTimeSlot;

	/**
	 * Starting hour of the slot
	 */
	private LocalTime _startingHour;

	/**
	 * Ending hour of the slot
	 */
	private LocalTime _endingHour;

	/**
	 * Indicates whether the time slot is open or not
	 */
	private boolean _bIsOpen;

	/**
	 * The working day id the time slot belongs to
	 */
	private int _nIdWorkingDay;

	/**
	 * Gets the id of the time slot
	 * 
	 * @return
	 */
	public int getIdTimeSlot() {
		return _nIdTimeSlot;
	}

	/**
	 * Sets the id of the time slot
	 * 
	 * @param nIdTimeSlot
	 *            the id to set
	 */
	public void setIdTimeSlot(int nIdTimeSlot) {
		this._nIdTimeSlot = nIdTimeSlot;
	}

	/**
	 * Gets the starting hour of the time slot
	 * 
	 * @return the starting hour of the time slot
	 */
	public LocalTime getStartingHour() {
		return _startingHour;
	}

	/**
	 * Sets the starting hour of the time slot
	 * 
	 * @param _startingHour
	 *            the starting hour to set
	 */
	public void setStartingHour(LocalTime startingHour) {
		this._startingHour = startingHour;
	}

	/**
	 * Gets the ending hour of the time slot
	 * 
	 * @return the ending hour of the time slot
	 */
	public LocalTime getEndingHour() {
		return _endingHour;
	}

	/**
	 * Sets the ending hour of the time slot
	 * 
	 * @param _endingHour
	 *            the ending hour to set
	 */
	public void setEndingHour(LocalTime endingHour) {
		this._endingHour = endingHour;
	}

	/**
	 * Indicates whether the time slot is open or not
	 * 
	 * @return true if the time slot is open
	 */
	public boolean isOpen() {
		return _bIsOpen;
	}

	/**
	 * Sets the opening boolean value of the time slot
	 * 
	 * @param _bIsOpen
	 *            the opening boolean value
	 */
	public void setIsOpen(boolean bIsOpen) {
		this._bIsOpen = bIsOpen;
	}

	/**
	 * Gets the working day id the time slot belongs to
	 * 
	 * @return the working day id the time slot belongs to
	 */
	public int getIdWorkingDay() {
		return _nIdWorkingDay;
	}

	/**
	 * Sets the working day id the time slot belongs to
	 * 
	 * @param nIdWorkingDay
	 *            the working day id to set
	 */
	public void setIdWorkingDay(int nIdWorkingDay) {
		this._nIdWorkingDay = nIdWorkingDay;
	}

}
