package fr.paris.lutece.plugins.appointment.business.appointment;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Appointment Response DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface IAppointmentResponseDAO
{

    /**
     * The name of the bean of the DAO
     */
    static String BEAN_NAME = "appointment.appointmentResponseDAO";

    /**
     * Generate a new primary key
     * 
     * @param plugin
     *            the Plugin
     * @return the new primary key
     */
    int getNewPrimaryKey( Plugin plugin );

    /**
     * Associates a response to an appointment
     * 
     * @param nIdAppointment
     *            The id of the appointment
     * @param nIdResponse
     *            The id of the response
     * @param plugin
     *            The plugin
     */
    void insertAppointmentResponse( int nIdAppointment, int nIdResponse, Plugin plugin );

    /**
     * Remove an appointment responses from the id of a response.
     * 
     * @param nIdResponse
     *            The id of the response
     * @param plugin
     *            The plugin
     */
    void removeAppointmentResponseByIdResponse( int nIdResponse, Plugin plugin );

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
