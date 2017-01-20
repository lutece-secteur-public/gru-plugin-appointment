package fr.paris.lutece.plugins.appointment.business.planningdefinition;

import java.io.Serializable;
import java.sql.Time;
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
	 * Indicate whether the time slot is open or not
	 */
	private boolean _bIsOpen;

	/**
	 * Working day id the time slot belongs to
	 */
	private int _nIdWorkingDay;

	/**
	 * Get the id of the time slot
	 * 
	 * @return
	 */
	public int getIdTimeSlot() {
		return _nIdTimeSlot;
	}

	/**
	 * Set the id of the time slot
	 * 
	 * @param nIdTimeSlot
	 *            the id to set
	 */
	public void setIdTimeSlot(int nIdTimeSlot) {
		this._nIdTimeSlot = nIdTimeSlot;
	}

	/**
	 * Get the starting hour of the time slot
	 * 
	 * @return the starting hour of the time slot
	 */
	public LocalTime getStartingHour() {
		return _startingHour;
	}

	/**
	 * Get the starting hour of the time slot (in sql time)
	 * 
	 * @return the starting hour
	 */
	public Time getStartingHourSqlTime() {
		Time time = null;
		if (_startingHour != null) {
			time = Time.valueOf(_startingHour);
		}
		return time;
	}

	/**
	 * Set the starting hour of the time slot
	 * 
	 * @param startingHour
	 *            the starting hour to set
	 */
	public void setStartingHour(LocalTime startingHour) {
		this._startingHour = startingHour;
	}

	/**
	 * Set the starting hour of the time slot
	 * 
	 * @param startingHour
	 *            the starting hour (in sql time)
	 */
	public void setStartingHour(Time startingHour) {
		if (startingHour != null) {
			this._startingHour = startingHour.toLocalTime();
		}
	}

	/**
	 * Get the ending hour of the time slot
	 * 
	 * @return the ending hour of the time slot
	 */
	public LocalTime getEndingHour() {
		return _endingHour;
	}

	/**
	 * Get the ending hour in sql time
	 * 
	 * @return the ending hour in sql time
	 */
	public Time getEndingHourSqlTime() {
		Time time = null;
		if (this._endingHour != null) {
			time = Time.valueOf(_endingHour);
		}
		return time;
	}

	/**
	 * Set the ending hour of the time slot
	 * 
	 * @param endingHour
	 *            the ending hour to set
	 */
	public void setEndingHour(LocalTime endingHour) {
		this._endingHour = endingHour;
	}

	/**
	 * Set the ending hour of the time slot
	 * 
	 * @param endingHour
	 *            the ending hour (in sql time format)
	 */
	public void setEndingHour(Time endingHour) {
		if (endingHour != null) {
			this._endingHour = endingHour.toLocalTime();
		}
	}

	/**
	 * Indicate whether the time slot is open or not
	 * 
	 * @return true if the time slot is open
	 */
	public boolean isOpen() {
		return _bIsOpen;
	}

	/**
	 * Set the opening boolean value of the time slot
	 * 
	 * @param bIsOpen
	 *            the opening boolean value
	 */
	public void setIsOpen(boolean bIsOpen) {
		this._bIsOpen = bIsOpen;
	}

	/**
	 * Get the working day id the time slot belongs to
	 * 
	 * @return the working day id the time slot belongs to
	 */
	public int getIdWorkingDay() {
		return _nIdWorkingDay;
	}

	/**
	 * Set the working day id the time slot belongs to
	 * 
	 * @param nIdWorkingDay
	 *            the working day id to set
	 */
	public void setIdWorkingDay(int nIdWorkingDay) {
		this._nIdWorkingDay = nIdWorkingDay;
	}

}
