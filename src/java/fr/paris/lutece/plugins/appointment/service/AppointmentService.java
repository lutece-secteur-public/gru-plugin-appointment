package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;

public class AppointmentService {

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

	public static List<Appointment> findListAppointmentByIdFormAndAfterADateTime(int nIdForm,
			LocalDateTime startingDateTime) {
		return AppointmentHome.findByIdFormAndAfterADateTime(nIdForm, startingDateTime);
	}
}
