package fr.paris.lutece.plugins.appointment.business.appointment;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.AppointmentFilter;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Appointment objects
 * 
 * @author Laurent Payen
 *
 */
public final class AppointmentDAO implements IAppointmentDAO
{

    private static final String SQL_QUERY_NEW_PK = "SELECT max(id_appointment) FROM appointment_appointment";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_appointment (id_appointment, reference, nb_places, is_cancelled, id_action_cancelled, notification, id_user, id_slot) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_appointment SET reference = ?, nb_places = ?, is_cancelled = ?, id_action_cancelled = ?, notification = ?, id_user = ?, id_slot = ? WHERE id_appointment = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_appointment WHERE id_appointment = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT appointment.id_appointment, appointment.reference, appointment.nb_places, appointment.is_cancelled, appointment.id_action_cancelled, appointment.notification, appointment.id_user, appointment.id_slot FROM appointment_appointment appointment";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_appointment = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_USER = SQL_QUERY_SELECT_COLUMNS + " WHERE id_user = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_SLOT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_slot = ?";
    private static final String SQL_QUERY_SELECT_BY_REFERENCE = SQL_QUERY_SELECT_COLUMNS + " WHERE reference = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT_COLUMNS
            + " INNER JOIN appointment_slot slot ON appointment.id_slot = slot.id_slot WHERE slot.id_form = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM_AND_AFTER_A_DATE = SQL_QUERY_SELECT_BY_ID_FORM + " AND slot.starting_date_time >= ?";
    private static final String SQL_QUERY_SELECT_BY_FILTER = "SELECT "
            + "app.id_appointment, app.reference, app.nb_places, app.is_cancelled, app.id_action_cancelled, app.notification, app.id_user, app.id_slot, "
            + "user.id_user, user.id_lutece_user, user.first_name, user.last_name, user.email, user.phone_number, "
            + "slot.id_slot, slot.starting_date_time, slot.ending_date_time, slot.is_open, slot.max_capacity, slot.nb_remaining_places, slot.id_form "
            + "FROM appointment_appointment app " + "INNER JOIN appointment_user user ON app.id_user = user.id_user "
            + "INNER JOIN appointment_slot slot ON app.id_slot = slot.id_slot " + "WHERE slot.id_form = ?";

    private static final String SQL_FILTER_FIRST_NAME = "user.first_name LIKE ?";
    private static final String SQL_FILTER_LAST_NAME = "user.last_name LIKE ?";
    private static final String SQL_FILTER_EMAIL = "user.email LIKE ?";
    private static final String SQL_FILTER_REFERENCE = "app.reference LIKE ?";
    private static final String SQL_FILTER_DATE_APPOINTMENT_MIN = "slot.starting_date_time >= ?";
    private static final String SQL_FILTER_DATE_APPOINTMENT_MAX = "slot.starting_date_time < ?";

    private static final String CONSTANT_AND = " AND ";
    private static final String CONSTANT_PERCENT = "%";

    @Override
    public int getNewPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = null;
        int nKey = 1;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                nKey = daoUtil.getInt( 1 ) + 1;
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return nKey;
    }

    @Override
    public synchronized void insert( Appointment appointment, Plugin plugin )
    {
        appointment.setIdAppointment( getNewPrimaryKey( plugin ) );
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, appointment, plugin, true );
        executeUpdate( daoUtil );
    }

    @Override
    public void update( Appointment appointment, Plugin plugin )
    {
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, appointment, plugin, false );
        executeUpdate( daoUtil );
    }

    @Override
    public void delete( int nIdAppointment, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdAppointment );
        executeUpdate( daoUtil );
    }

    @Override
    public Appointment select( int nIdAppointment, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        Appointment appointment = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
            daoUtil.setInt( 1, nIdAppointment );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                appointment = buildAppointment( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return appointment;
    }

    @Override
    public List<Appointment> findByIdUser( int nIdUser, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        List<Appointment> listAppointment = new ArrayList<>( );
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_USER, plugin );
            daoUtil.setInt( 1, nIdUser );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listAppointment.add( buildAppointment( daoUtil ) );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return listAppointment;
    }

    @Override
    public List<Appointment> findByIdSlot( int nIdSlot, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        List<Appointment> listAppointment = new ArrayList<>( );
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_SLOT, plugin );
            daoUtil.setInt( 1, nIdSlot );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listAppointment.add( buildAppointment( daoUtil ) );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return listAppointment;
    }

    @Override
    public Appointment findByReference( String strReference, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        Appointment appointment = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_REFERENCE, plugin );
            daoUtil.setString( 1, strReference );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                appointment = buildAppointment( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return appointment;
    }

    @Override
    public List<Appointment> findByFilter( AppointmentFilter appointmentFilter, Plugin plugin )
    {
        List<Appointment> listAppointment = new ArrayList<Appointment>( );
        DAOUtil daoUtil = new DAOUtil( getSqlQueryFromFilter( appointmentFilter ), plugin );
        addFilterParametersToDAOUtil( appointmentFilter, daoUtil );
        daoUtil.executeQuery( );
        while ( daoUtil.next( ) )
        {
            listAppointment.add( buildAppointmentHeavy( daoUtil ) );
        }
        daoUtil.free( );
        return listAppointment;
    }

    /**
     * Add all the filters to the daoUtil
     * 
     * @param appointmentFilter
     *            the filter
     * @param daoUtil
     *            the daoutil
     */
    private void addFilterParametersToDAOUtil( AppointmentFilter appointmentFilter, DAOUtil daoUtil )
    {
        int nIndex = 1;
        daoUtil.setInt( nIndex++, appointmentFilter.getIdForm( ) );
        if ( appointmentFilter.getFirstName( ) != null )
        {
            daoUtil.setString( nIndex++, CONSTANT_PERCENT + appointmentFilter.getFirstName( ) + CONSTANT_PERCENT );
        }
        if ( appointmentFilter.getLastName( ) != null )
        {
            daoUtil.setString( nIndex++, CONSTANT_PERCENT + appointmentFilter.getLastName( ) + CONSTANT_PERCENT );
        }
        if ( appointmentFilter.getEmail( ) != null )
        {
            daoUtil.setString( nIndex++, CONSTANT_PERCENT + appointmentFilter.getEmail( ) + CONSTANT_PERCENT );
        }
        if ( appointmentFilter.getReference( ) != null )
        {
            daoUtil.setString( nIndex++, CONSTANT_PERCENT + appointmentFilter.getReference( ) + CONSTANT_PERCENT );
        }
        if ( appointmentFilter.getStartingDateOfSearch( ) != null )
        {
            Timestamp startingTimestamp;
            if ( StringUtils.isNotEmpty( appointmentFilter.getStartingTimeOfSearch( ) ) )
            {
                startingTimestamp = Timestamp.valueOf( appointmentFilter.getStartingDateOfSearch( ).toLocalDate( )
                        .atTime( LocalTime.parse( appointmentFilter.getStartingTimeOfSearch( ) ) ) );
            }
            else
            {
                startingTimestamp = Timestamp.valueOf( appointmentFilter.getStartingDateOfSearch( ).toLocalDate( ).atStartOfDay( ) );
            }
            daoUtil.setTimestamp( nIndex++, startingTimestamp );
        }
        if ( appointmentFilter.getEndingDateOfSearch( ) != null )
        {
            Timestamp endingTimestamp;
            if ( StringUtils.isNotEmpty( appointmentFilter.getEndingTimeOfSearch( ) ) )
            {
                endingTimestamp = Timestamp.valueOf( appointmentFilter.getEndingDateOfSearch( ).toLocalDate( )
                        .atTime( LocalTime.parse( appointmentFilter.getEndingTimeOfSearch( ) ) ) );
            }
            else
            {
                endingTimestamp = Timestamp.valueOf( appointmentFilter.getEndingDateOfSearch( ).toLocalDate( ).atTime( LocalTime.MAX ) );
            }
            daoUtil.setTimestamp( nIndex++, endingTimestamp );
        }
    }

    /**
     * Build the sql query with the elements of the filter
     * 
     * @param appointmentFilter
     *            the filter
     * @return the query
     */
    private String getSqlQueryFromFilter( AppointmentFilter appointmentFilter )
    {
        StringBuilder sbSql = new StringBuilder( SQL_QUERY_SELECT_BY_FILTER );
        if ( appointmentFilter.getFirstName( ) != null )
        {
            sbSql.append( CONSTANT_AND );
            sbSql.append( SQL_FILTER_FIRST_NAME );
        }
        if ( appointmentFilter.getLastName( ) != null )
        {
            sbSql.append( CONSTANT_AND );
            sbSql.append( SQL_FILTER_LAST_NAME );
        }
        if ( appointmentFilter.getEmail( ) != null )
        {
            sbSql.append( CONSTANT_AND );
            sbSql.append( SQL_FILTER_EMAIL );
        }
        if ( appointmentFilter.getReference( ) != null )
        {
            sbSql.append( CONSTANT_AND );
            sbSql.append( SQL_FILTER_REFERENCE );
        }
        if ( appointmentFilter.getStartingDateOfSearch( ) != null )
        {
            sbSql.append( CONSTANT_AND );
            sbSql.append( SQL_FILTER_DATE_APPOINTMENT_MIN );
        }
        if ( appointmentFilter.getEndingDateOfSearch( ) != null )
        {
            sbSql.append( CONSTANT_AND );
            sbSql.append( SQL_FILTER_DATE_APPOINTMENT_MAX );
        }
        return sbSql.toString( );
    }

    @Override
    public List<Appointment> findByIdFormAndAfterADateTime( int nIdForm, LocalDateTime startingDateTime, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        List<Appointment> listAppointment = new ArrayList<>( );
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM_AND_AFTER_A_DATE, plugin );
            daoUtil.setInt( 1, nIdForm );
            daoUtil.setTimestamp( 2, Timestamp.valueOf( startingDateTime ) );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listAppointment.add( buildAppointment( daoUtil ) );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return listAppointment;
    }

    @Override
    public List<Appointment> findByIdForm( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        List<Appointment> listAppointment = new ArrayList<>( );
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin );
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listAppointment.add( buildAppointment( daoUtil ) );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return listAppointment;
    }

    /**
     * Build an Appointment business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new Appointment business object with all its attributes assigned
     */
    private Appointment buildAppointment( DAOUtil daoUtil )
    {
        int nIndex = 1;
        Appointment appointment = new Appointment( );
        appointment.setIdAppointment( daoUtil.getInt( nIndex++ ) );
        appointment.setReference( daoUtil.getString( nIndex++ ) );
        appointment.setNbPlaces( daoUtil.getInt( nIndex++ ) );
        appointment.setIsCancelled( daoUtil.getBoolean( nIndex++ ) );
        appointment.setIdActionCancelled( daoUtil.getInt( nIndex++ ) );
        appointment.setNotification( daoUtil.getInt( nIndex++ ) );
        appointment.setIdUser( daoUtil.getInt( nIndex++ ) );
        appointment.setIdSlot( daoUtil.getInt( nIndex ) );
        return appointment;
    }

    /**
     * Build an appointment business object with its complete slot and its complete user
     * 
     * @param daoUtil
     *            the daoutil
     * @return the appointment
     */
    private Appointment buildAppointmentHeavy( DAOUtil daoUtil )
    {
        int nIndex = 1;
        Appointment appointment = new Appointment( );
        appointment.setIdAppointment( daoUtil.getInt( nIndex++ ) );
        appointment.setReference( daoUtil.getString( nIndex++ ) );
        appointment.setNbPlaces( daoUtil.getInt( nIndex++ ) );
        appointment.setIsCancelled( daoUtil.getBoolean( nIndex++ ) );
        appointment.setIdActionCancelled( daoUtil.getInt( nIndex++ ) );
        appointment.setNotification( daoUtil.getInt( nIndex++ ) );
        appointment.setIdUser( daoUtil.getInt( nIndex++ ) );
        appointment.setIdSlot( daoUtil.getInt( nIndex++ ) );
        User user = new User( );
        user.setIdUser( daoUtil.getInt( nIndex++ ) );
        user.setIdLuteceUser( daoUtil.getInt( nIndex++ ) );
        user.setFirstName( daoUtil.getString( nIndex++ ) );
        user.setLastName( daoUtil.getString( nIndex++ ) );
        user.setEmail( daoUtil.getString( nIndex++ ) );
        user.setPhoneNumber( daoUtil.getString( nIndex++ ) );
        appointment.setUser( user );
        Slot slot = new Slot( );
        slot.setIdSlot( daoUtil.getInt( nIndex++ ) );
        slot.setStartingTimeStampDate( daoUtil.getTimestamp( nIndex++ ) );
        slot.setEndingTimeStampDate( daoUtil.getTimestamp( nIndex++ ) );
        slot.setIsOpen( daoUtil.getBoolean( nIndex++ ) );
        slot.setMaxCapacity( daoUtil.getInt( nIndex++ ) );
        slot.setNbRemainingPlaces( daoUtil.getInt( nIndex++ ) );
        slot.setIdForm( daoUtil.getInt( nIndex ) );
        appointment.setSlot( slot );
        return appointment;
    }

    /**
     * Build a daoUtil object with the query and all the attributes of the Appointment
     * 
     * @param suery
     *            the query
     * @param appointment
     *            the Appointment
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, Appointment appointment, Plugin plugin, boolean isInsert )
    {
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( query, plugin );
        if ( isInsert )
        {
            daoUtil.setInt( nIndex++, appointment.getIdAppointment( ) );
        }
        daoUtil.setString( nIndex++, appointment.getReference( ) );
        daoUtil.setInt( nIndex++, appointment.getNbPlaces( ) );
        daoUtil.setBoolean( nIndex++, appointment.getIsCancelled( ) );
        daoUtil.setInt( nIndex++, appointment.getIdActionCancelled( ) );
        daoUtil.setInt( nIndex++, appointment.getNotification( ) );
        daoUtil.setInt( nIndex++, appointment.getIdUser( ) );
        daoUtil.setInt( nIndex++, appointment.getIdSlot( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, appointment.getIdAppointment( ) );
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
