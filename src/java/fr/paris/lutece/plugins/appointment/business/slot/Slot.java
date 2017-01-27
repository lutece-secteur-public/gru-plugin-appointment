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
	 * Nb Remaining Places of the slot
	 */
	private int _nNb_Remaining_Places;

	/**
	 * Form Id the slot belongs to (foreign key)
	 */
	private int _nIdForm;

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
	 * Get the starting date of the slot
	 * 
	 * @return the starting date of the slot (in Sql Timestamp format)
	 */
	public Timestamp getStartingTimestampDate() {
		Timestamp timestamp = null;
		if (this._startingDate != null) {
			timestamp = Timestamp.valueOf(this._startingDate);
		}
		return timestamp;
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
	 * Get the ending date of the slot
	 * 
	 * @return the ending date of the slot (in Sql Timestamp format)
	 */
	public Timestamp getEndingTimestampDate() {
		Timestamp timestamp = null;
		if (this._endingDate != null) {
			timestamp = Timestamp.valueOf(_endingDate);
		}
		return timestamp;
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
	 * Get number of remaining places of the slot
	 * 
	 * @return the number of remaining places of the slot
	 */
	public int getNbRemainingPlaces() {
		return _nNb_Remaining_Places;
	}

	/**
	 * Set the number of remaining places of the slot
	 * 
	 * @param nNbRemainingPlaces
	 *            the number of remaining places
	 */
	public void setNbRemainingPlaces(int nNbRemainingPlaces) {
		this._nNb_Remaining_Places = nNbRemainingPlaces;
	}

	/**
	 * Get the Form Id the slot belongs to
	 * 
	 * @return the FOrm Id
	 */
	public int getIdForm() {
		return _nIdForm;
	}

	/**
	 * Set the Form Id the Slot belongs to
	 * 
	 * @param nIdForm
	 *            the Form Id to set
	 */
	public void setIdForm(int nIdForm) {
		this._nIdForm = nIdForm;
	}

}
