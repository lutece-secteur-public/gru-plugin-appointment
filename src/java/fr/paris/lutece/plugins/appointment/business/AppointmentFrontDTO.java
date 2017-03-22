package fr.paris.lutece.plugins.appointment.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.i18n.I18nService;

public class AppointmentFrontDTO implements Serializable {

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
	
	/**
	 * Appointment resource type
	 */
	public static final String APPOINTMENT_RESOURCE_TYPE = "appointment";
	
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 703930649594406505L;
	
	private int _nIdAppointment;
	private String _strDateOfTheAppointment;
	private int _nIdForm;
	private int _nIdUser;	
	@NotBlank(message = "appointment.validation.appointment.FirstName.notEmpty")
	@Size(max = 255, message = "appointment.validation.appointment.FirstName.size")
	private String _strFirstName;
	
	@NotBlank(message = "appointment.validation.appointment.LastName.notEmpty")
	@Size(max = 255, message = "appointment.validation.appointment.LastName.size")
	private String _strLastName;
	
	@Size(max = 255, message = "appointment.validation.appointment.Email.size")
	@Email(message = "appointment.validation.appointment.Email.email")
	private String _strEmail;	
	
	private int _nNbBookedSeats;	
	private Slot _slot;
			
	private Map<Integer, List<Response>> _mapResponsesByIdEntry = new HashMap<Integer, List<Response>>();	
	private List<Response> _listResponse;		

	public String getDateOfTheAppointment() {
		return _strDateOfTheAppointment;
	}

	public void setDateOfTheAppointment(String strDateOfTheAppointment) {
		this._strDateOfTheAppointment = strDateOfTheAppointment;
	}
	
	public List<Response> getListResponse() {
		return _listResponse;
	}

	public void setListResponse(List<Response> listResponse) {
		this._listResponse = listResponse;
	}

	public int getIdForm() {
		return _nIdForm;
	}

	public void setIdForm(int nIdForm) {
		this._nIdForm = nIdForm;
	}

	public int getIdAppointment() {
		return _nIdAppointment;
	}

	public void setIdAppointment(int nIdAppointment) {
		this._nIdAppointment = nIdAppointment;
	}

	public int getIdUser() {
		return _nIdUser;
	}

	public void setIdUser(int nIdUser) {
		this._nIdUser = nIdUser;
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

	public int getNbBookedSeats() {
		return _nNbBookedSeats;
	}

	public void setNbBookedSeats(int nNumberOfPlacesReserved) {
		this._nNbBookedSeats = nNumberOfPlacesReserved;
	}

	public Slot getSlot() {
		return _slot;
	}

	public void setSlot(Slot slot) {
		this._slot = slot;
	}

	public Map<Integer, List<Response>> getMapResponsesByIdEntry() {
		return _mapResponsesByIdEntry;
	}

	public void setMapResponsesByIdEntry(Map<Integer, List<Response>> mapResponsesByIdEntry) {
		this._mapResponsesByIdEntry = mapResponsesByIdEntry;
	}	
	
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
