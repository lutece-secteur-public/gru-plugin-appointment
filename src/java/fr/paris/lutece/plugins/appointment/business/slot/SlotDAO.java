package fr.paris.lutece.plugins.appointment.business.slot;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.UtilDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Slot objects
 * 
 * @author Laurent Payen
 *
 */
public final class SlotDAO extends UtilDAO implements ISlotDAO
{

    private static final String SQL_QUERY_NEW_PK = "SELECT max(id_slot) FROM appointment_slot";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_slot (id_slot, starting_date_time, ending_date_time, is_open, is_specific, max_capacity, nb_remaining_places, nb_potential_remaining_places, id_form) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_slot SET starting_date_time = ?, ending_date_time = ?, is_open = ?, is_specific = ?, max_capacity = ?, nb_remaining_places = ?, nb_potential_remaining_places = ?, id_form = ? WHERE id_slot = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_slot WHERE id_slot = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_slot, starting_date_time, ending_date_time, is_open, is_specific, max_capacity, nb_remaining_places, nb_potential_remaining_places, id_form ";
    private static final String SQL_FROM_APPOINTMENT_SLOT = "FROM appointment_slot";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + SQL_FROM_APPOINTMENT_SLOT + " WHERE id_slot = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT_COLUMNS + SQL_FROM_APPOINTMENT_SLOT + " WHERE id_form = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_RANGE = SQL_QUERY_SELECT_COLUMNS + SQL_FROM_APPOINTMENT_SLOT
            + " WHERE id_form = ? AND starting_date_time >= ? AND ending_date_time <= ?";
    private static final String SQL_QUERY_SELECT_OPEN_SLOTS_BY_ID_FORM_AND_DATE_RANGE = SQL_QUERY_SELECT_COLUMNS + SQL_FROM_APPOINTMENT_SLOT
            + " WHERE id_form = ? AND starting_date_time >= ? AND ending_date_time <= ? AND is_open = 1";
    private static final String SQL_QUERY_SELECT_OPEN_SLOTS_BY_ID_FORM = SQL_QUERY_SELECT_COLUMNS + SQL_FROM_APPOINTMENT_SLOT
            + " WHERE id_form = ? AND is_open = 1";
    private static final String SQL_QUERY_SELECT_SLOT_WITH_MAX_DATE = SQL_QUERY_SELECT_COLUMNS + ", MAX(slot.starting_date_time) FROM appointment_slot slot"
            + " WHERE slot.id_form = ?";

    @Override
    public synchronized void insert( Slot slot, Plugin plugin )
    {
        slot.setIdSlot( getNewPrimaryKey( SQL_QUERY_NEW_PK, plugin ) );
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, slot, plugin, true );
        executeUpdate( daoUtil );
    }

    @Override
    public void update( Slot slot, Plugin plugin )
    {
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, slot, plugin, false );
        executeUpdate( daoUtil );
    }

    @Override
    public void delete( int nIdSlot, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdSlot );
        executeUpdate( daoUtil );
    }

    @Override
    public Slot select( int nIdSlot, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        Slot slot = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
            daoUtil.setInt( 1, nIdSlot );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                slot = buildSlot( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return slot;
    }

    @Override
    public List<Slot> findByIdFormAndDateRange( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        List<Slot> listSlots = new ArrayList<>( );
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_RANGE, plugin );
            daoUtil.setInt( 1, nIdForm );
            daoUtil.setTimestamp( 2, Timestamp.valueOf( startingDateTime ) );
            daoUtil.setTimestamp( 3, Timestamp.valueOf( endingDateTime ) );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listSlots.add( buildSlot( daoUtil ) );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return listSlots;
    }

    @Override
    public List<Slot> findByIdForm( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        List<Slot> listSlot = new ArrayList<>( );
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin );
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listSlot.add( buildSlot( daoUtil ) );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return listSlot;
    }

    @Override
    public List<Slot> findOpenSlotsByIdFormAndDateRange( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        List<Slot> listSLot = new ArrayList<>( );
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_OPEN_SLOTS_BY_ID_FORM_AND_DATE_RANGE, plugin );
            daoUtil.setInt( 1, nIdForm );
            daoUtil.setTimestamp( 2, Timestamp.valueOf( startingDateTime ) );
            daoUtil.setTimestamp( 3, Timestamp.valueOf( endingDateTime ) );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listSLot.add( buildSlot( daoUtil ) );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return listSLot;
    }

    @Override
    public List<Slot> findOpenSlotsByIdForm( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        List<Slot> listSLot = new ArrayList<>( );
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_OPEN_SLOTS_BY_ID_FORM, plugin );
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listSLot.add( buildSlot( daoUtil ) );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return listSLot;
    }

    @Override
    public Slot findSlotWithMaxDate( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        Slot slot = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_SLOT_WITH_MAX_DATE, plugin );
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                slot = buildSlot( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return slot;
    }

    /**
     * Build a Slot business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new Slot with all its attributes assigned
     */
    private Slot buildSlot( DAOUtil daoUtil )
    {
        int nIndex = 1;
        Slot slot = new Slot( );
        slot.setIdSlot( daoUtil.getInt( nIndex++ ) );
        slot.setStartingTimeStampDate( daoUtil.getTimestamp( nIndex++ ) );
        slot.setEndingTimeStampDate( daoUtil.getTimestamp( nIndex++ ) );
        slot.setIsOpen( daoUtil.getBoolean( nIndex++ ) );
        slot.setIsSpecific( daoUtil.getBoolean( nIndex++ ) );
        slot.setMaxCapacity( daoUtil.getInt( nIndex++ ) );
        slot.setNbRemainingPlaces( daoUtil.getInt( nIndex++ ) );
        slot.setNbPotentialRemainingPlaces( daoUtil.getInt( nIndex++ ) );
        slot.setIdForm( daoUtil.getInt( nIndex ) );

        return slot;
    }

    /**
     * Build a daoUtil object with the Slot business object
     * 
     * @param query
     *            the query
     * @param slot
     *            the SLot
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, Slot slot, Plugin plugin, boolean isInsert )
    {
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( query, plugin );
        if ( isInsert )
        {
            daoUtil.setInt( nIndex++, slot.getIdSlot( ) );
        }
        daoUtil.setTimestamp( nIndex++, slot.getStartingTimestampDate( ) );
        daoUtil.setTimestamp( nIndex++, slot.getEndingTimestampDate( ) );
        daoUtil.setBoolean( nIndex++, slot.getIsOpen( ) );
        daoUtil.setBoolean( nIndex++, slot.getIsSpecific( ) );
        daoUtil.setInt( nIndex++, slot.getMaxCapacity( ) );
        daoUtil.setInt( nIndex++, slot.getNbRemainingPlaces( ) );
        daoUtil.setInt( nIndex++, slot.getNbPotentialRemainingPlaces( ) );
        daoUtil.setInt( nIndex++, slot.getIdForm( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, slot.getIdSlot( ) );
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
