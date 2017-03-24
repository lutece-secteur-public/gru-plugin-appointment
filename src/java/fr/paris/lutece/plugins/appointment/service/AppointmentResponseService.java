package fr.paris.lutece.plugins.appointment.service;

import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentResponseHome;

/**
 * Service Class for the appointment Response
 * 
 * @author Laurent Payen
 *
 */
public class AppointmentResponseService {

	/**
	 * Remove the responses for the given entry
	 * @param nIdEntry the entry
	 */
	public static void removeResponsesByIdEntry(int nIdEntry) {
		AppointmentResponseHome.removeResponsesByIdEntry(nIdEntry);
	}
}
