/*
 * Copyright (c) 2002-2018, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.appointment.business.appointment;

import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.UtilDAO;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFilterDTO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Appointment objects
 * 
 * @author Laurent Payen
 *
 */
public final class AppointmentDAO extends UtilDAO implements IAppointmentDAO
{

    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_appointment (reference, nb_places, is_cancelled, id_action_cancelled, notification, id_admin_user, admin_access_code_create, id_user, id_slot, date_appointment_create) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_appointment SET reference = ?, nb_places = ?, is_cancelled = ?, id_action_cancelled = ?, notification = ?, id_admin_user = ?, admin_access_code_create = ?, id_user = ?, id_slot = ?, date_appointment_create = ? WHERE id_appointment = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_appointment WHERE id_appointment = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT appointment.id_appointment, appointment.reference, appointment.nb_places, appointment.is_cancelled, appointment.id_action_cancelled, appointment.notification, appointment.id_admin_user, appointment.admin_access_code_create, appointment.id_user, appointment.id_slot, appointment.date_appointment_create FROM appointment_appointment appointment";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_appointment = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_USER = SQL_QUERY_SELECT_COLUMNS + " WHERE id_user = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_SLOT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_slot = ?";
    private static final String SQL_QUERY_SELECT_BY_REFERENCE = SQL_QUERY_SELECT_COLUMNS + " WHERE reference = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT_COLUMNS
            + " INNER JOIN appointment_slot slot ON appointment.id_slot = slot.id_slot WHERE slot.id_form = ?";
    private static final String SQL_QUERY_SELECT_BY_FILTER = "SELECT "
            + "app.id_appointment, app.reference, app.nb_places, app.is_cancelled, app.id_action_cancelled, app.notification, app.id_admin_user, app.admin_access_code_create, app.id_user, app.id_slot, app.date_appointment_create, "
            + "user.id_user, user.guid, user.first_name, user.last_name, user.email, user.phone_number, "
            + "slot.id_slot, slot.starting_date_time, slot.ending_date_time, slot.is_open, slot.is_specific, slot.max_capacity, slot.nb_remaining_places, slot.id_form "
            + "FROM appointment_appointment app " + "INNER JOIN appointment_user user ON app.id_user = user.id_user "
            + "INNER JOIN appointment_slot slot ON app.id_slot = slot.id_slot " + "WHERE slot.id_form = ?";

    private static final String SQL_FILTER_FIRST_NAME = "UPPER(user.first_name) LIKE ?";
    private static final String SQL_FILTER_LAST_NAME = "UPPER(user.last_name) LIKE ?";
    private static final String SQL_FILTER_EMAIL = "UPPER(user.email) LIKE ?";
    private static final String SQL_FILTER_STATUS = "app.is_cancelled = ?";
    private static final String SQL_FILTER_DATE_APPOINTMENT_MIN = "slot.starting_date_time >= ?";
    private static final String SQL_FILTER_DATE_APPOINTMENT_MAX = "slot.starting_date_time < ?";

    private static final String CONSTANT_AND = " AND ";
    private static final String CONSTANT_PERCENT = "%";

    @Override
    public void insert( Appointment appointment, Plugin plugin )
    {
        appointment.setDateAppointmentTaken(LocalDateTime.now( ));
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, appointment, plugin, true );

        try
        {
            daoUtil.executeUpdate( );       
	        if ( daoUtil.nextGeneratedKey( ) )
	        {
	        	appointment.setIdAppointment( daoUtil.getGeneratedKeyInt( 1 ) );
	        }
        }finally
        {
                daoUtil.free();       
        }
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
    public List<Appointment> findByFilter( AppointmentFilterDTO appointmentFilter, Plugin plugin )
    {
        List<Appointment> listAppointment = new ArrayList<>();
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
    private void addFilterParametersToDAOUtil( AppointmentFilterDTO appointmentFilter, DAOUtil daoUtil )
    {
        int nIndex = 1;
        daoUtil.setInt( nIndex++, appointmentFilter.getIdForm( ) );
        if ( appointmentFilter.getFirstName( ) != null )
        {
            daoUtil.setString( nIndex++, CONSTANT_PERCENT + appointmentFilter.getFirstName( ).toUpperCase( ) + CONSTANT_PERCENT );
        }
        if ( appointmentFilter.getLastName( ) != null )
        {
            daoUtil.setString( nIndex++, CONSTANT_PERCENT + appointmentFilter.getLastName( ).toUpperCase( ) + CONSTANT_PERCENT );
        }
        if ( appointmentFilter.getEmail( ) != null )
        {
            daoUtil.setString( nIndex++, CONSTANT_PERCENT + appointmentFilter.getEmail( ).toUpperCase( ) + CONSTANT_PERCENT );
        }
        if ( appointmentFilter.getStatus( ) != -1 )
        {
            daoUtil.setInt( nIndex++, appointmentFilter.getStatus( ) );
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
    private String getSqlQueryFromFilter( AppointmentFilterDTO appointmentFilter )
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
        if ( appointmentFilter.getStatus( ) != -1 )
        {
            sbSql.append( CONSTANT_AND );
            sbSql.append( SQL_FILTER_STATUS );
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
        appointment.setIdAdminUser( daoUtil.getInt( nIndex++ ) );
        appointment.setAdminUserCreate( daoUtil.getString( nIndex++ ) );
        appointment.setIdUser( daoUtil.getInt( nIndex++ ) );
        appointment.setIdSlot( daoUtil.getInt( nIndex++ ) );
        appointment.setAppointmentTakenSqlDate(daoUtil.getTimestamp( nIndex ) );
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
        appointment.setIdAdminUser( daoUtil.getInt( nIndex++ ) );
        appointment.setAdminUserCreate( daoUtil.getString( nIndex++ ) );
        appointment.setIdUser( daoUtil.getInt( nIndex++ ) );
        appointment.setIdSlot( daoUtil.getInt( nIndex++ ) );
        appointment.setAppointmentTakenSqlDate( daoUtil.getTimestamp( nIndex++ ) );
        User user = new User( );
        user.setIdUser( daoUtil.getInt( nIndex++ ) );
        user.setGuid( daoUtil.getString( nIndex++ ) );
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
        slot.setIsSpecific( daoUtil.getBoolean( nIndex++ ) );
        slot.setMaxCapacity( daoUtil.getInt( nIndex++ ) );
        slot.setNbRemainingPlaces( daoUtil.getInt( nIndex++ ) );
        slot.setIdForm( daoUtil.getInt( nIndex ) );
        appointment.setSlot( slot );
        return appointment;
    }

    /**
     * Build a daoUtil object with the query and all the attributes of the Appointment
     * 
     * @param query
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
        DAOUtil daoUtil;
        if ( isInsert )
        {
        	daoUtil = new DAOUtil( query, Statement.RETURN_GENERATED_KEYS, plugin );
        }else{
        	daoUtil = new DAOUtil( query, plugin );
        }
        daoUtil.setString( nIndex++, appointment.getReference( ) );
        daoUtil.setInt( nIndex++, appointment.getNbPlaces( ) );
        daoUtil.setBoolean( nIndex++, appointment.getIsCancelled( ) );
        daoUtil.setInt( nIndex++, appointment.getIdActionCancelled( ) );
        daoUtil.setInt( nIndex++, appointment.getNotification( ) );
        daoUtil.setInt( nIndex++, appointment.getIdAdminUser( ) );
        daoUtil.setString( nIndex++, appointment.getAdminUserCreate( ) );
        daoUtil.setInt( nIndex++, appointment.getIdUser( ) );
        daoUtil.setInt( nIndex++, appointment.getIdSlot( ) );
        daoUtil.setTimestamp(nIndex++, appointment.getAppointmentTakenSqlDate( ));
        
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
