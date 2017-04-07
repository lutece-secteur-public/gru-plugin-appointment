package fr.paris.lutece.plugins.appointment.business.appointment;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Appointment Response DAO Interface
 * @author Laurent Payen
 *
 */
public interface IAppointmentResponseDAO {

	/**
	 * The name of the bean of the DAO
	 */
	static String BEAN_NAME = "appointment.appointmentResponseDAO";
	
	/**
	 * Remove an appointment responses from the id of a response.
	 * 
	 * @param nIdResponse
	 *            The id of the response
	 * @param plugin
	 *            The plugin
	 */
	void removeAppointmentResponsesByIdResponse(int nIdResponse, Plugin plugin);

	/**
     * Get the list of id of responses associated with an appointment
     * 
     * @param nIdAppointment
     *            the id of the appointment
     * @param plugin
     *            the plugin
     * @return the list of responses, or an empty list if no response was found
     */
    List<Integer> findListIdResponse( int nIdAppointment, Plugin plugin );
    
}
