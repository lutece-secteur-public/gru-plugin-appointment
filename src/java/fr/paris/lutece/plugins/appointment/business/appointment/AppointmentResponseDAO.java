package fr.paris.lutece.plugins.appointment.business.appointment;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.UtilDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Appointment Response DAO
 * 
 * @author Laurent Payen
 *
 */
public final class AppointmentResponseDAO extends UtilDAO implements IAppointmentResponseDAO
{

    private static final String SQL_QUERY_NEW_PK = "SELECT max(id_appointment_response) FROM appointment_appointment_response";
    private static final String SQL_QUERY_INSERT_APPOINTMENT_RESPONSE = "INSERT INTO appointment_appointment_response (id_appointment_response, id_appointment, id_response) VALUES (?,?,?)";
    private static final String SQL_QUERY_REMOVE_FROM_ID_RESPONSE = "DELETE FROM appointment_appointment_response WHERE id_response = ?";
    private static final String SQL_QUERY_SELECT_APPOINTMENT_RESPONSE_LIST = "SELECT id_response FROM appointment_appointment_response WHERE id_appointment = ?";    

    @Override
    public void insertAppointmentResponse( int nIdAppointment, int nIdResponse, Plugin plugin )
    {
        int nIndex = 1;
        DAOUtil daoUtil = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_INSERT_APPOINTMENT_RESPONSE, plugin );
            daoUtil.setInt( nIndex++, getNewPrimaryKey( SQL_QUERY_NEW_PK, plugin ) );
            daoUtil.setInt( nIndex++, nIdAppointment );
            daoUtil.setInt( nIndex++, nIdResponse );
            daoUtil.executeUpdate( );
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
    }

    @Override
    public void removeAppointmentResponseByIdResponse( int nIdResponse, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_REMOVE_FROM_ID_RESPONSE, plugin );
            daoUtil.setInt( 1, nIdResponse );
            daoUtil.executeUpdate( );
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
    }

    @Override
    public List<Integer> findListIdResponse( int nIdAppointment, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        List<Integer> listIdResponse = new ArrayList<Integer>( );
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_APPOINTMENT_RESPONSE_LIST, plugin );
            daoUtil.setInt( 1, nIdAppointment );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listIdResponse.add( daoUtil.getInt( 1 ) );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return listIdResponse;
    }

}
