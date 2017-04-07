package fr.paris.lutece.plugins.appointment.business;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalTime;

import org.apache.commons.lang.StringUtils;

public class AppointmentFilter implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -8087511361613314595L;	

	private int _nIdForm;
	private String _strFirstName;
	private String _strLastName;
	private String _strEmail;
	private Date _startingDateOfSearch;
	private Date _endingDateOfSearch;
	private LocalTime _startingTimeOfSearch;
	private LocalTime _endingTimeOfSearch;
	private String _strReference;
	private String _statusFilter;
	
	public int getIdForm() {
		return _nIdForm;
	}

	public void setIdForm(int nIdForm) {
		this._nIdForm = nIdForm;
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

	public Date getStartingDateOfSearch() {
		return _startingDateOfSearch;
	}

	public void setStartingDateOfSearch(Date startingDateOfSearch) {
		this._startingDateOfSearch = startingDateOfSearch;
	}

	public Date getEndingDateOfSearch() {
		return _endingDateOfSearch;
	}

	public void setEndingDateOfSearch(Date endingDateOfSearch) {
		this._endingDateOfSearch = endingDateOfSearch;
	}

	
	
	public LocalTime getStartingTimeOfSearch() {
		return _startingTimeOfSearch;
	}

	public void setStartingTimeOfSearch(LocalTime startingTimeOfSearch) {
		this._startingTimeOfSearch = startingTimeOfSearch;
	}

	public LocalTime getEndingTimeOfSearch() {
		return _endingTimeOfSearch;
	}

	public void setEndingTimeOfSearch(LocalTime endingTimeOfSearch) {
		this._endingTimeOfSearch = endingTimeOfSearch;
	}

	public String getReference() {
		return _strReference;
	}

	public void setReference(String strReference) {
		this._strReference = strReference;
	}

	/**
	 * Get the order of the sort of this filter
	 * 
	 * @return The _bOrderAsc
	 */
	public String getStatusFilter() {
		return _statusFilter;
	}

	/**
	 * Set the order of the sort of this filter
	 * 
	 * @param bOrderAsc
	 *            True to sort ascending, false to sort descending,
	 */
	public void setStatusFilter(String status) {
		this._statusFilter = status;
	}
	
}
