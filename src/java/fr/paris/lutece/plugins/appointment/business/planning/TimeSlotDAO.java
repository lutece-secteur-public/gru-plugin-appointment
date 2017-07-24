package fr.paris.lutece.plugins.appointment.business.planning;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.UtilDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Time Slot objects
 * 
 * @author Laurent Payen
 *
 */
public final class TimeSlotDAO extends UtilDAO implements ITimeSlotDAO
{

    private static final String SQL_QUERY_NEW_PK = "SELECT max(id_time_slot) FROM appointment_time_slot";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_time_slot (id_time_slot, starting_time, ending_time, is_open, max_capacity, id_working_day) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_time_slot SET starting_time = ?, ending_time = ?, is_open = ?, max_capacity = ?, id_working_day = ? WHERE id_time_slot = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_time_slot WHERE id_time_slot = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_time_slot, starting_time, ending_time, is_open, max_capacity, id_working_day FROM appointment_time_slot";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_time_slot = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_WORKING_DAY = SQL_QUERY_SELECT_COLUMNS + " WHERE id_working_day = ?";    

    @Override
    public synchronized void insert( TimeSlot timeSlot, Plugin plugin )
    {
        timeSlot.setIdTimeSlot( getNewPrimaryKey( SQL_QUERY_NEW_PK, plugin ) );
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, timeSlot, plugin, true );
        executeUpdate( daoUtil );
    }

    @Override
    public void update( TimeSlot timeSlot, Plugin plugin )
    {
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, timeSlot, plugin, false );
        executeUpdate( daoUtil );
    }

    @Override
    public void delete( int nIdTimeSlot, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdTimeSlot );
        executeUpdate( daoUtil );
    }

    @Override
    public TimeSlot select( int nIdTimeSlot, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        TimeSlot timeSlot = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
            daoUtil.setInt( 1, nIdTimeSlot );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                timeSlot = buildTimeSlot( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return timeSlot;
    }

    @Override
    public List<TimeSlot> findByIdWorkingDay( int nIdWorkingDay, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        List<TimeSlot> listTimeSLots = new ArrayList<>( );
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_WORKING_DAY, plugin );
            daoUtil.setInt( 1, nIdWorkingDay );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listTimeSLots.add( buildTimeSlot( daoUtil ) );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return listTimeSLots;
    }

    /**
     * Build a time slot business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new time slot with all its attributes assigned
     */
    private TimeSlot buildTimeSlot( DAOUtil daoUtil )
    {
        int nIndex = 1;
        TimeSlot timeSlot = new TimeSlot( );
        timeSlot.setIdTimeSlot( daoUtil.getInt( nIndex++ ) );
        timeSlot.setStartingTime( daoUtil.getTime( nIndex++ ) );
        timeSlot.setEndingTime( daoUtil.getTime( nIndex++ ) );
        timeSlot.setIsOpen( daoUtil.getBoolean( nIndex++ ) );
        timeSlot.setMaxCapacity( daoUtil.getInt( nIndex++ ) );
        timeSlot.setIdWorkingDay( daoUtil.getInt( nIndex ) );
        return timeSlot;
    }

    /**
     * Build a daoUtil object with time slot business object
     * 
     * @param query
     *            the query
     * @param timeSlot
     *            the time slot
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, TimeSlot timeSlot, Plugin plugin, boolean isInsert )
    {
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( query, plugin );
        if ( isInsert )
        {
            daoUtil.setInt( nIndex++, timeSlot.getIdTimeSlot( ) );
        }
        daoUtil.setTime( nIndex++, timeSlot.getStartingTimeSqlTime( ) );
        daoUtil.setTime( nIndex++, timeSlot.getEndingTimeSqlTime( ) );
        daoUtil.setBoolean( nIndex++, timeSlot.getIsOpen( ) );
        daoUtil.setInt( nIndex++, timeSlot.getMaxCapacity( ) );
        daoUtil.setInt( nIndex++, timeSlot.getIdWorkingDay( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, timeSlot.getIdTimeSlot( ) );
        }
        return daoUtil;
    }

    /**
     * Execute a safe update (Free the connection in case of error when execute the query)
     * 
     * @param daoUtil
     *            the daoUtil
     */
    private void executeUpdate( DAOUtil daoUtil )
    {
        try
        {
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

}
