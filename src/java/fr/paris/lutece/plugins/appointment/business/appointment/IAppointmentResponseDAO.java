package fr.paris.lutece.plugins.appointment.business.appointment;

import fr.paris.lutece.portal.service.plugin.Plugin;

public interface IAppointmentResponseDAO {

    /**
     * Remove an appointment responses from the id of a response.
     * 
     * @param nIdResponse
     *            The id of the response
     * @param plugin
     *            The plugin
     */
    void removeAppointmentResponsesByIdResponse( int nIdResponse, Plugin plugin );
    
}
