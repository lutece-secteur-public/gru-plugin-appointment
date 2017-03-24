package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.AppointmentFrontDTO;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.util.CryptoService;

/**
 * Service class for an appointment
 * 
 * @author Laurent Payen
 *
 */
public class AppointmentService {

	private static final String PROPERTY_REF_ENCRYPTION_ALGORITHM = "appointment.refEncryptionAlgorithm";
	private static final String CONSTANT_SHA256 = "SHA-256";
	private static final String PROPERTY_REF_SIZE_RANDOM_PART = "appointment.refSizeRandomPart";
	/**
	 * Get the number of characters of the random part of appointment reference
	 */
	private static final int CONSTANT_REF_SIZE_RANDOM_PART = 5;

	/**
	 * Find all the appointments of the slots given in parameter
	 * 
	 * @param listSlot
	 *            the list of slots
	 * @return a list of the appointments on these slots
	 */
	public static List<Appointment> findListAppointmentByListSlot(List<Slot> listSlot) {
		List<Appointment> listAppointment = new ArrayList<>();
		for (Slot slot : listSlot) {
			listAppointment.addAll(AppointmentService.findListAppointmentBySlot(slot.getIdSlot()));
		}
		return listAppointment;
	}

	/**
	 * Find the appointments of a slot
	 * 
	 * @param nIdSlot
	 *            the slot Id
	 * @return the appointments of the slot
	 */
	public static List<Appointment> findListAppointmentBySlot(int nIdSlot) {
		return AppointmentHome.findByIdSlot(nIdSlot);
	}

	/**
	 * Find the appointments of a user
	 * 
	 * @param nIdUser
	 *            the user Id
	 * @return the apointment of the user
	 */
	public static List<Appointment> findListAppointmentByUserId(int nIdUser) {
		return AppointmentHome.findByIdUser(nIdUser);
	}

	/**
	 * Find the appointments by form and that will be after a given date
	 * 
	 * @param nIdForm
	 *            the form Id
	 * @param startingDateTime
	 *            the starting date
	 * @return the appointments that matches the criteria
	 */
	public static List<Appointment> findListAppointmentByIdFormAndAfterADateTime(int nIdForm,
			LocalDateTime startingDateTime) {
		return AppointmentHome.findByIdFormAndAfterADateTime(nIdForm, startingDateTime);
	}

	/**
	 * Save an appointment in database
	 * 
	 * @param appointmentDTO
	 *            the appointment dto
	 * @return the id of the appointment saved
	 */
	public static int saveAppointment(AppointmentFrontDTO appointmentDTO) {
		Slot slot = appointmentDTO.getSlot();
		int oldNbRemainingPLaces = slot.getNbRemainingPlaces();
		slot.setNbRemainingPlaces(oldNbRemainingPLaces - appointmentDTO.getNbBookedSeats());
		slot = SlotService.saveSlot(slot);
		User user = UserService.saveUser(appointmentDTO);
		Appointment appointment = new Appointment();
		appointment.setIdSlot(slot.getIdSlot());
		appointment.setIdUser(user.getIdUser());
		appointment = AppointmentHome.create(appointment);
		String strEmailOrLastNamePlusFirstName = StringUtils.EMPTY;
		if (StringUtils.isEmpty(user.getEmail())) {
			strEmailOrLastNamePlusFirstName = user.getLastName() + user.getFirstName();
		}
		String strReference = appointment.getIdAppointment() + CryptoService
				.encrypt(appointment.getIdAppointment() + strEmailOrLastNamePlusFirstName,
						AppPropertiesService.getProperty(PROPERTY_REF_ENCRYPTION_ALGORITHM, CONSTANT_SHA256))
				.substring(0, AppPropertiesService.getPropertyInt(PROPERTY_REF_SIZE_RANDOM_PART,
						CONSTANT_REF_SIZE_RANDOM_PART));
		appointment.setReference(strReference);
		AppointmentHome.update(appointment);
		return appointment.getIdAppointment();
	}

	/**
	 * Find an appointment by ots primary key
	 * 
	 * @param nIdAppointment
	 *            the appointment Id
	 * @return the appointment
	 */
	public static Appointment findAppointmentById(int nIdAppointment) {
		return AppointmentHome.findByPrimaryKey(nIdAppointment);
	}
}
