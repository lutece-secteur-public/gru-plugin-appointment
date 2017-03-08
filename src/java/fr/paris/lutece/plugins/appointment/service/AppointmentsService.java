package fr.paris.lutece.plugins.appointment.service;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;

public class AppointmentsService {

	public static List<Appointment> findAppointmentByListSlot(List<Slot> listSlot) {
		List<Appointment> listAppointment = new ArrayList<>();
		for (Slot slot : listSlot) {
			listAppointment.addAll(AppointmentsService.findAppointmentBySlot(slot.getIdSlot()));
		}
		return listAppointment;
	}

	public static List<Appointment> findAppointmentBySlot(int nIdSlot) {
		return AppointmentHome.findByIdSlot(nIdSlot);
	}

}
