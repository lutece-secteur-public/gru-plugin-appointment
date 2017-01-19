package fr.paris.lutece.plugins.appointment.business.slot;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * The business class of the slot
 * 
 * @author Laurent Payen
 *
 */
public class Slot implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 9054234926836931062L;

	/**
	 * Id of the slot
	 */
	private int _nIdSlot;

	/**
	 * Starting date (Date + Time) of the slot
	 */
	private LocalDateTime _startingDate;

	/**
	 * Ending date (Date + Time) of the slot
	 */
	private LocalDateTime _endingDate;

	/**
	 * Indicate whether the slot is open or not
	 */
	private boolean _bIsOpen;

	/**
	 * Maximum capacity of the slot
	 */
	private int _nMaxCapacityPerSlot;

	/**
	 * Get the id of the slot
	 * 
	 * @return the id of the slot
	 */
	public int getIdSlot() {
		return _nIdSlot;
	}

	/**
	 * Set the id of the slot
	 * 
	 * @param nIdSlot
	 *            the id to set
	 */
	public void setIdSlot(int nIdSlot) {
		this._nIdSlot = nIdSlot;
	}

	/**
	 * Get the starting date of the slot
	 * 
	 * @return the starting date of the slot
	 */
	public LocalDateTime getStartingDate() {
		return _startingDate;
	}

	/**
	 * Set the starting date of the slot
	 * 
	 * @param startingDate
	 *            the starting date to set
	 */
	public void setStartingDate(LocalDateTime startingDate) {
		this._startingDate = startingDate;
	}

	/**
	 * Set the starting date of the slot
	 * 
	 * @param startingDate
	 *            the starting date to set (in Timestamp format)
	 */
	public void setStartingDate(Timestamp startingDate) {
		if (startingDate != null) {
			this._startingDate = startingDate.toLocalDateTime();
		}
	}

	/**
	 * Get the ending date of the slot
	 * 
	 * @return the ending date of the slot
	 */
	public LocalDateTime getEndingDate() {
		return _endingDate;
	}

	/**
	 * Set the ending date of the slot
	 * 
	 * @param endingDate
	 *            the ending date of the slot (in LocalDateTime format)
	 */
	public void setEndingDate(LocalDateTime endingDate) {
		this._endingDate = endingDate;
	}

	/**
	 * Set the ending date of the slot
	 * 
	 * @param endingDate
	 *            the ending date of the slot (in Timestamp format)
	 */
	public void setEndingDate(Timestamp endingDate) {
		if (endingDate != null) {
			this._endingDate = endingDate.toLocalDateTime();
		}
	}

	/**
	 * Indicate if the slot is open or not
	 * 
	 * @return true if the slot is open
	 */
	public boolean isOpen() {
		return _bIsOpen;
	}

	/**
	 * Set the boolean open value of the slot
	 * 
	 * @param bIsOpen
	 *            the boolean open value to set
	 */
	public void setIsOpen(boolean bIsOpen) {
		this._bIsOpen = bIsOpen;
	}

	/**
	 * Get the maximum capacity of the slot
	 * 
	 * @return the maximum capacity of the slot
	 */
	public int getMaxCapacityPerSlot() {
		return _nMaxCapacityPerSlot;
	}

	/**
	 * Set the maximum capacity of the slot
	 * 
	 * @param maxCapacityPerSlot
	 *            the maximum capacity to set
	 */
	public void setMaxCapacityPerSlot(int nMaxCapacityPerSlot) {
		this._nMaxCapacityPerSlot = nMaxCapacityPerSlot;
	}

}
