package fr.paris.lutece.plugins.appointment.business.slot;

import java.io.Serializable;
import java.time.LocalDateTime;

public final class Period implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 7139913342306166121L;

	/**
	 * Starting Time
	 */
	private LocalDateTime _startingDateTime;

	/**
	 * Ending Time
	 */
	private LocalDateTime _endingDateTime;

	public Period(LocalDateTime startingDateTime, LocalDateTime endingDateTime) {
		this._startingDateTime = startingDateTime;
		this._endingDateTime = endingDateTime;
	}

	/**
	 * Get the Starting Time
	 * 
	 * @return The Starting Time
	 */
	public LocalDateTime getStartingDateTime() {
		return _startingDateTime;
	}

	/**
	 * Set the Starting Time
	 * 
	 * @param startingDateTime
	 *            the Starting Time to Set
	 */
	public void setStartingDateTime(LocalDateTime startingDateTime) {
		this._startingDateTime = startingDateTime;
	}

	/**
	 * Get the Ending Time
	 * 
	 * @return The Ending Time
	 */
	public LocalDateTime getEndingDateTime() {
		return _endingDateTime;
	}

	/**
	 * Set the Ending Time
	 * 
	 * @param endingDateTime
	 *            The Ending Time to Set
	 */
	public void setEndingDateTime(LocalDateTime endingDateTime) {
		this._endingDateTime = endingDateTime;
	}

}
