package fr.paris.lutece.plugins.appointment.business.appointment;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class AppointmentResponseDAO implements IAppointmentResponseDAO{

	private static final String SQL_QUERY_REMOVE_FROM_ID_RESPONSE = "DELETE FROM appointment_appointment_response WHERE id_response = ?";
	
	@Override
    public void removeAppointmentResponsesByIdResponse( int nIdResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REMOVE_FROM_ID_RESPONSE, plugin );
        daoUtil.setInt( 1, nIdResponse );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }
	
}
