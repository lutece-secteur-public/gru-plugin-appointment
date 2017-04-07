package fr.paris.lutece.plugins.appointment.business;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * The DTO for an appointment in front office
 * 
 * @author Laurent Payen
 *
 */
public class AppointmentDTO implements Serializable {

	private static final String PROPERTY_EMPTY_FIELD_FIRST_NAME = "appointment.validation.appointment.FirstName.notEmpty";
	private static final String PROPERTY_EMPTY_FIELD_LAST_NAME = "appointment.validation.appointment.LastName.notEmpty";
	private static final String PROPERTY_UNVAILABLE_EMAIL = "appointment.validation.appointment.Email.email";
	private static final String PROPERTY_MESSAGE_EMPTY_EMAIL = "appointment.validation.appointment.Email.notEmpty";
	private static final String PROPERTY_EMPTY_CONFIRM_EMAIL = "appointment.validation.appointment.EmailConfirmation.email";
	private static final String PROPERTY_UNVAILABLE_CONFIRM_EMAIL = "appointment.message.error.confirmEmail";
	private static final String PROPERTY_EMPTY_NB_SEATS = "appointment.validation.appointment.NbBookedSeat.notEmpty";
	private static final String PROPERTY_UNVAILABLE_NB_SEATS = "appointment.validation.appointment.NbBookedSeat.error";
	private static final String PROPERTY_MAX_APPOINTMENT_PERIODE = "appointment.message.error.MaxAppointmentPeriode";
	private static final String PROPERTY_MAX_APPOINTMENT_PERIODE_BACK = "appointment.info.appointment.emailerror";
	private static final String PROPERTY_NB_DAY_BETWEEN_TWO_APPOINTMENTS = "appointment.validation.appointment.NbMinDaysBetweenTwoAppointments.error";	

	public static final String PROPERTY_APPOINTMENT_STATUS_UNRESERVED = "appointment.message.labelStatusUnreserved";
	public static final String PROPERTY_APPOINTMENT_STATUS_RESERVED = "appointment.message.labelStatusReserved";
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 703930649594406505L;

	/**
	 * The Appointment Id
	 */
	private int _nIdAppointment;

	/**
	 * The Date of the appointment
	 */
	private String _strDateOfTheAppointment;

	private LocalDateTime _startingDateTime;
	
	private LocalTime _startingTime;
	
	private LocalTime _endingTime;
	
	private boolean _isCancelled;
	
	private State _state;
	/**
	 * The Form Id
	 */
	private int _nIdForm;

	/**
	 * The User Id
	 */
	private int _nIdUser;

	/**
	 * The First Name of the User
	 */
	@NotBlank(message = "appointment.validation.appointment.FirstName.notEmpty")
	@Size(max = 255, message = "appointment.validation.appointment.FirstName.size")
	private String _strFirstName;

	/**
	 * The Last Name of the user
	 */
	@NotBlank(message = "appointment.validation.appointment.LastName.notEmpty")
	@Size(max = 255, message = "appointment.validation.appointment.LastName.size")
	private String _strLastName;

	/**
	 * The email of the user
	 */
	@Size(max = 255, message = "appointment.validation.appointment.Email.size")
	@Email(message = "appointment.validation.appointment.Email.email")
	private String _strEmail;

	/**
	 * the number of booked seats for this appointment
	 */
	private int _nNbBookedSeats;

	/**
	 * The slot of the apointment
	 */
	private Slot _slot;
	
	private User user;		

	/**
	 * The Map of the responses for the additional entries of the form
	 */
	private Map<Integer, List<Response>> _mapResponsesByIdEntry = new HashMap<Integer, List<Response>>();

	/**
	 * The list of the responses for the additional entries of the form
	 */
	private List<Response> _listResponse;	
	
	public State getState() {
		return _state;
	}

	public void setState(State state) {
		this._state = state;
	}

	public LocalDateTime getStartingDateTime() {
		return _startingDateTime;
	}

	public void setStartingDateTime(LocalDateTime _startingDateTime) {
		this._startingDateTime = _startingDateTime;
	}

	public LocalTime getStartingTime() {
		return _startingTime;
	}

	public void setStartingTime(LocalTime startingTime) {
		this._startingTime = startingTime;
	}

	public LocalTime getEndingTime() {
		return _endingTime;
	}

	public void setEndingTime(LocalTime endingTime) {
		this._endingTime = endingTime;
	}

	public boolean getIsCancelled() {
		return _isCancelled;
	}

	public void setIsCancelled(boolean isCancelled) {
		this._isCancelled = isCancelled;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Get the date of the appointment
	 * 
	 * @return the date of the appointment
	 */
	public String getDateOfTheAppointment() {
		return _strDateOfTheAppointment;
	}

	/**
	 * Set the date of the appointment
	 * 
	 * @param strDateOfTheAppointment
	 *            the date to set
	 */
	public void setDateOfTheAppointment(String strDateOfTheAppointment) {
		this._strDateOfTheAppointment = strDateOfTheAppointment;
	}

	/**
	 * Get the list of the responses of the additional entries of the form
	 * 
	 * @return the list of the responses
	 */
	public List<Response> getListResponse() {
		return _listResponse;
	}

	/**
	 * Set the list of the responses of the additional entries of the form
	 * 
	 * @param listResponse
	 *            the list of the responses to set
	 */
	public void setListResponse(List<Response> listResponse) {
		this._listResponse = listResponse;
	}

	
	/**
	 * Get the form Id
	 * 
	 * @return the form Id
	 */
	public int getIdForm() {
		return _nIdForm;
	}

	/**
	 * Set the Form Id
	 * 
	 * @param nIdForm
	 *            the form Id to set
	 */
	public void setIdForm(int nIdForm) {
		this._nIdForm = nIdForm;
	}

	/**
	 * Get the appointment Id
	 * 
	 * @return the appointment Id
	 */
	public int getIdAppointment() {
		return _nIdAppointment;
	}

	/**
	 * Set the appointment Id
	 * 
	 * @param nIdAppointment
	 *            the appointment Id to set
	 */
	public void setIdAppointment(int nIdAppointment) {
		this._nIdAppointment = nIdAppointment;
	}

	/**
	 * Get the User Id of the appointment
	 * 
	 * @return the User Id
	 */
	public int getIdUser() {
		return _nIdUser;
	}

	/**
	 * Set the User Id of the Appointment
	 * 
	 * @param nIdUser
	 *            the user Id to set
	 */
	public void setIdUser(int nIdUser) {
		this._nIdUser = nIdUser;
	}

	/**
	 * Get the first name of the user
	 * 
	 * @return the first name of the user
	 */
	public String getFirstName() {
		return _strFirstName;
	}

	/**
	 * Set the first name of the user
	 * 
	 * @param strFirstName
	 *            the first name of the user
	 */
	public void setFirstName(String strFirstName) {
		this._strFirstName = strFirstName;
	}

	/**
	 * Get the last name of the user
	 * 
	 * @return the last name of the user
	 */
	public String getLastName() {
		return _strLastName;
	}

	/**
	 * Set the last name of the user
	 * 
	 * @param strLastName
	 *            the last name to set
	 */
	public void setLastName(String strLastName) {
		this._strLastName = strLastName;
	}

	/**
	 * Get the email of the user
	 * 
	 * @return the email of the user
	 */
	public String getEmail() {
		return _strEmail;
	}

	/**
	 * Set the email of the user
	 * 
	 * @param strEmail
	 *            the email to set
	 */
	public void setEmail(String strEmail) {
		this._strEmail = strEmail;
	}

	/**
	 * Get the number of booked seats for the appointment
	 * 
	 * @return the number of booked seats
	 */
	public int getNbBookedSeats() {
		return _nNbBookedSeats;
	}

	/**
	 * Set the number of booked seats for the appointment
	 * 
	 * @param nNumberOfPlacesReserved
	 *            the number to set
	 */
	public void setNbBookedSeats(int nNumberOfPlacesReserved) {
		this._nNbBookedSeats = nNumberOfPlacesReserved;
	}

	/**
	 * Get the slot of the appointment
	 * 
	 * @return the slot of the appointment
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
	 * Get the map of the responses of the additional entries of the form
	 * 
	 * @return the map of the responses
	 */
	public Map<Integer, List<Response>> getMapResponsesByIdEntry() {
		return _mapResponsesByIdEntry;
	}

	/**
	 * Set the map of the responses of the addtional entries of the form to the
	 * appointment
	 * 
	 * @param mapResponsesByIdEntry
	 *            the map to set
	 */
	public void setMapResponsesByIdEntry(Map<Integer, List<Response>> mapResponsesByIdEntry) {
		this._mapResponsesByIdEntry = mapResponsesByIdEntry;
	}

	/**
	 * Get all the possible errors of the form
	 * 
	 * @param locale
	 *            the locale
	 * @return a list of all the possible errors of the form
	 */
	public static List<String> getAllErrors(Locale locale) {
		List<String> listAllErrors = new ArrayList<String>();
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_EMPTY_FIELD_LAST_NAME, locale));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_EMPTY_FIELD_FIRST_NAME, locale));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_UNVAILABLE_EMAIL, locale));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_MESSAGE_EMPTY_EMAIL, locale));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_EMPTY_CONFIRM_EMAIL, locale));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_UNVAILABLE_CONFIRM_EMAIL, locale));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_EMPTY_NB_SEATS, locale));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_UNVAILABLE_NB_SEATS, locale));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_MAX_APPOINTMENT_PERIODE, locale));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_MAX_APPOINTMENT_PERIODE_BACK, locale));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_NB_DAY_BETWEEN_TWO_APPOINTMENTS, locale));
		return listAllErrors;
	}

}
