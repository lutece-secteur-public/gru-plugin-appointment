package fr.paris.lutece.plugins.appointment.service;

import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentResponseHome;

public class AppointmentResponseService {

	public static void removeResponsesByIdEntry(int nIdEntry) {
		AppointmentResponseHome.removeResponsesByIdEntry(nIdEntry);
	}
}
