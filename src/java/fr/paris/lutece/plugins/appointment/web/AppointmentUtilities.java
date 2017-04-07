package fr.paris.lutece.plugins.appointment.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.EntryService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.UserService;
import fr.paris.lutece.plugins.appointment.service.Utilities;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.util.beanvalidation.BeanValidationUtil;

/**
 * Utility class for Appointment Mutualize methods between MVCApplication and
 * MVCAdminJspBean
 * 
 * @author Laurent Payen
 *
 */
public class AppointmentUtilities {

	private static final String ERROR_MESSAGE_EMPTY_CONFIRM_EMAIL = "appointment.validation.appointment.EmailConfirmation.email";
	private static final String ERROR_MESSAGE_CONFIRM_EMAIL = "appointment.message.error.confirmEmail";
	private static final String ERROR_MESSAGE_EMPTY_EMAIL = "appointment.validation.appointment.Email.notEmpty";
	private static final String ERROR_MESSAGE_EMPTY_NB_BOOKED_SEAT = "appointment.validation.appointment.NbBookedSeat.notEmpty";
	private static final String ERROR_MESSAGE_ERROR_NB_BOOKED_SEAT = "appointment.validation.appointment.NbBookedSeat.error";

	/**
	 * Check that the email is correct and matches the confirm email
	 * 
	 * @param strEmail
	 *            the email
	 * @param strConfirmEmail
	 *            the confirm email
	 * @param form
	 *            the form
	 * @param locale
	 *            the local
	 * @param listFormErrors
	 *            the list of errors that can be fill in with the errors found
	 *            for the email
	 */
	public static void checkEmail(String strEmail, String strConfirmEmail, AppointmentForm form, Locale locale,
			List<GenericAttributeError> listFormErrors) {
		if (form.getEnableMandatoryEmail()) {
			if (StringUtils.isEmpty(strEmail)) {
				GenericAttributeError genAttError = new GenericAttributeError();
				genAttError.setErrorMessage(I18nService.getLocalizedString(ERROR_MESSAGE_EMPTY_EMAIL, locale));
				listFormErrors.add(genAttError);
			}
			if (StringUtils.isEmpty(strConfirmEmail)) {
				GenericAttributeError genAttError = new GenericAttributeError();
				genAttError.setErrorMessage(I18nService.getLocalizedString(ERROR_MESSAGE_EMPTY_CONFIRM_EMAIL, locale));
				listFormErrors.add(genAttError);
			}
		}
		if (!StringUtils.equals(strEmail, strConfirmEmail)) {
			GenericAttributeError genAttError = new GenericAttributeError();
			genAttError.setErrorMessage(I18nService.getLocalizedString(ERROR_MESSAGE_CONFIRM_EMAIL, locale));
			listFormErrors.add(genAttError);
		}
	}

	/**
	 * Check that the user has no previous appointment or that the previous
	 * appointment respect the delay
	 * 
	 * @param dateOfTheAppointment
	 *            date of the new appointment
	 * @param strEmail
	 *            the email of the user
	 * @param form
	 *            the form
	 * @param locale
	 *            the locale
	 * @param listFormErrors
	 *            the list of errors that can be fill in with the errors found
	 * @return
	 */
	public static boolean checkUserAndAppointment(LocalDate dateOfTheAppointment, String strEmail, AppointmentForm form,
			Locale locale, List<GenericAttributeError> listFormErrors) {
		boolean bCheckPassed = true;
		if (StringUtils.isNotEmpty(strEmail)) {
			int nbDaysBetweenTwoAppointments = form.getNbDaysBeforeNewAppointment();
			if (nbDaysBetweenTwoAppointments != 0) {
				// Looking for existing user with this email
				User user = UserService.findUserByEmail(strEmail);
				if (user != null) {
					// looking for its appointment
					List<Appointment> listAppointment = AppointmentService
							.findListAppointmentByUserId(user.getIdUser());
					if (CollectionUtils.isNotEmpty(listAppointment)) {
						// I know we could have a join sql query, but I don't
						// want to join the appointment table with the slot
						// table, it's too big and not efficient
						List<Slot> listSlot = new ArrayList<>();
						for (Appointment appointment : listAppointment) {
							listSlot.add(SlotService.findSlotById(appointment.getIdSlot()));
						}
						// Get the last appointment date for this form
						LocalDate dateOfTheLastAppointment = listSlot.stream()
								.filter(s -> s.getIdForm() == form.getIdForm()).map(Slot::getStartingDateTime)
								.max(LocalDateTime::compareTo).get().toLocalDate();
						// Check the number of days between this appointment and
						// the last appointment the user has taken
						if ((dateOfTheLastAppointment.isBefore(dateOfTheAppointment)
								|| dateOfTheLastAppointment.equals(dateOfTheAppointment))
								&& dateOfTheLastAppointment.until(dateOfTheAppointment,
										ChronoUnit.DAYS) <= nbDaysBetweenTwoAppointments) {
							bCheckPassed = false;
						}
					}
				}
			}
		}
		return bCheckPassed;
	}

	/**
	 * Check and validate all the rules for the number of booked seats asked
	 * 
	 * @param strNbBookedSeats
	 *            the number of booked seats
	 * @param form
	 *            the form
	 * @param nbRemainingPlaces
	 *            the number of remaining places on the slot asked
	 * @param locale
	 *            the locale
	 * @param listFormErrors
	 *            the list of errors that can be fill in with the errors found
	 *            for the number of booked seats
	 * @return
	 */
	public static int checkAndReturnNbBookedSeats(String strNbBookedSeats, AppointmentForm form, int nbRemainingPlaces,
			Locale locale, List<GenericAttributeError> listFormErrors) {
		int nbBookedSeats = 1;
		if (StringUtils.isEmpty(strNbBookedSeats) && form.getMaxPeoplePerAppointment() > 1) {
			GenericAttributeError genAttError = new GenericAttributeError();
			genAttError.setErrorMessage(I18nService.getLocalizedString(ERROR_MESSAGE_EMPTY_NB_BOOKED_SEAT, locale));
			listFormErrors.add(genAttError);
		}
		if (StringUtils.isNotEmpty(strNbBookedSeats)) {
			nbBookedSeats = Integer.parseInt(strNbBookedSeats);
		}
		if (nbBookedSeats > nbRemainingPlaces) {
			GenericAttributeError genAttError = new GenericAttributeError();
			genAttError.setErrorMessage(I18nService.getLocalizedString(ERROR_MESSAGE_ERROR_NB_BOOKED_SEAT, locale));
			listFormErrors.add(genAttError);
		}
		if (nbBookedSeats == 0) {
			GenericAttributeError genAttError = new GenericAttributeError();
			genAttError.setErrorMessage(I18nService.getLocalizedString(ERROR_MESSAGE_EMPTY_NB_BOOKED_SEAT, locale));
			listFormErrors.add(genAttError);
		}
		return nbBookedSeats;
	}

	/**
	 * Fill the appoinmentFront DTO with the given parameters
	 * 
	 * @param appointmentDTO
	 *            the appointmentFront DTO
	 * @param nbBookedSeats
	 *            the number of booked seats
	 * @param strEmail
	 *            the email of the user
	 * @param strFirstName
	 *            the first name of the user
	 * @param strLastName
	 *            the last name of the user
	 */
	public static void fillAppointmentDTO(AppointmentDTO appointmentDTO, int nbBookedSeats, String strEmail,
			String strFirstName, String strLastName) {
		appointmentDTO.setDateOfTheAppointment(appointmentDTO.getSlot().getDate().format(Utilities.formatter));
		appointmentDTO.setNbBookedSeats(nbBookedSeats);
		appointmentDTO.setEmail(strEmail);
		appointmentDTO.setFirstName(strFirstName);
		appointmentDTO.setLastName(strLastName);
		Map<Integer, List<Response>> mapResponses = appointmentDTO.getMapResponsesByIdEntry();
		if (mapResponses != null) {
			List<Response> listResponse = new ArrayList<Response>();
			for (List<Response> listResponseByEntry : mapResponses.values()) {
				listResponse.addAll(listResponseByEntry);
			}
			appointmentDTO.setMapResponsesByIdEntry(null);
			appointmentDTO.setListResponse(listResponse);
		}
	}

	/**
	 * Validate the form and the additional entries of the form
	 * 
	 * @param appointmentDTO
	 *            the appointmentFron DTo to validate
	 * @param request
	 *            the request
	 * @param listFormErrors
	 *            the list of errors that can be fill with the errors found at
	 *            the validation
	 */
	public static void validateFormAndEntries(AppointmentDTO appointmentDTO, HttpServletRequest request,
			List<GenericAttributeError> listFormErrors) {
		Set<ConstraintViolation<AppointmentDTO>> listErrors = BeanValidationUtil.validate(appointmentDTO);
		if (CollectionUtils.isNotEmpty(listErrors)) {
			for (ConstraintViolation<AppointmentDTO> constraintViolation : listErrors) {
				GenericAttributeError genAttError = new GenericAttributeError();
				genAttError.setErrorMessage(
						I18nService.getLocalizedString(constraintViolation.getMessageTemplate(), request.getLocale()));
				listFormErrors.add(genAttError);
			}
		}
		List<Entry> listEntryFirstLevel = EntryHome
				.getEntryList(EntryService.buildEntryFilter(appointmentDTO.getIdForm()));
		for (Entry entry : listEntryFirstLevel) {
			listFormErrors.addAll(
					EntryService.getResponseEntry(request, entry.getIdEntry(), request.getLocale(), appointmentDTO));
		}
	}
}
