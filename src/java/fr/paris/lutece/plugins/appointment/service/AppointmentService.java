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

public class AppointmentService {

	private static final String PROPERTY_REF_ENCRYPTION_ALGORITHM = "appointment.refEncryptionAlgorithm";
	private static final String CONSTANT_SHA256 = "SHA-256";
	private static final String PROPERTY_REF_SIZE_RANDOM_PART = "appointment.refSizeRandomPart";
	/**
	 * Get the number of characters of the random part of appointment reference
	 */
	private static final int CONSTANT_REF_SIZE_RANDOM_PART = 5;

	public static List<Appointment> findListAppointmentByListSlot(List<Slot> listSlot) {
		List<Appointment> listAppointment = new ArrayList<>();
		for (Slot slot : listSlot) {
			listAppointment.addAll(AppointmentService.findListAppointmentBySlot(slot.getIdSlot()));
		}
		return listAppointment;
	}

	public static List<Appointment> findListAppointmentBySlot(int nIdSlot) {
		return AppointmentHome.findByIdSlot(nIdSlot);
	}

	public static List<Appointment> findListAppointmentByUserId(int nIdUser) {
		return AppointmentHome.findByIdUser(nIdUser);
	}
	
	public static List<Appointment> findListAppointmentByIdFormAndAfterADateTime(int nIdForm,
			LocalDateTime startingDateTime) {
		return AppointmentHome.findByIdFormAndAfterADateTime(nIdForm, startingDateTime);
	}

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

	public static Appointment findAppointmentById(int nIdAppointment) {
		return AppointmentHome.findByPrimaryKey(nIdAppointment);
	}
}
