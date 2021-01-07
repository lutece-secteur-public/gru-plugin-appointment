/*
 * Copyright (c) 2002-2020, City of Paris
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.slot.SlotHome;
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
public final class AppointmentDAO implements IAppointmentDAO
{

    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_appointment (reference, nb_places, is_cancelled, id_action_cancelled, notification, id_admin_user, admin_access_code_create, id_user, date_appointment_create) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_appointment SET reference = ?, nb_places = ?, is_cancelled = ?, id_action_cancelled = ?, notification = ?, id_admin_user = ?, admin_access_code_create = ?, id_user = ?, date_appointment_create = ? WHERE id_appointment = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_appointment WHERE id_appointment = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT appointment.id_appointment, appointment.reference, appointment.nb_places, appointment.is_cancelled, appointment.id_action_cancelled, appointment.notification, appointment.id_admin_user, appointment.admin_access_code_create, appointment.id_user, appointment.date_appointment_create ";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + "FROM appointment_appointment appointment WHERE id_appointment = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_USER = SQL_QUERY_SELECT_COLUMNS + "FROM appointment_appointment appointment WHERE id_user = ?";
    private static final String SQL_QUERY_SELECT_BY_GUID_USER = SQL_QUERY_SELECT_COLUMNS
            + "FROM appointment_appointment appointment join appointment_user user on (user.id_user = appointment.id_user and user.guid = ?)";
    private static final String SQL_QUERY_SELECT_BY_ID_SLOT = SQL_QUERY_SELECT_COLUMNS
            + ",appt_slot.nb_places FROM appointment_appointment appointment INNER JOIN appointment_appointment_slot appt_slot on ( appt_slot.id_appointment = appointment.id_appointment and appt_slot.id_slot= ? )";
    private static final String SQL_QUERY_SELECT_BY_REFERENCE = SQL_QUERY_SELECT_COLUMNS + "FROM appointment_appointment appointment WHERE reference = ?";

    private static final String SQL_QUERY_SELECT_DISTINCT_COLUMNS = "SELECT DISTINCT appointment.id_appointment, appointment.reference, appointment.nb_places, appointment.is_cancelled, appointment.id_action_cancelled, appointment.notification, appointment.id_admin_user, appointment.admin_access_code_create, appointment.id_user, appointment.date_appointment_create FROM appointment_appointment appointment";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT_DISTINCT_COLUMNS
            + " INNER JOIN appointment_appointment_slot appt_slot on(appt_slot.id_appointment = appointment.id_appointment) INNER JOIN appointment_slot slot ON (appt_slot.id_slot = slot.id_slot) WHERE slot.id_form = ?";
    private static final String SQL_QUERY_SELECT_BY_FILTER = "SELECT "
            + " app.id_appointment, app.reference, app.nb_places, app.is_cancelled, app.id_action_cancelled, app.notification, app.id_admin_user, app.admin_access_code_create, app.id_user, app.date_appointment_create, "
            + " user.id_user, user.guid, user.first_name, user.last_name, user.email, user.phone_number, "
            + " slot.id_slot, slot.starting_date_time, slot.ending_date_time, slot.is_open, slot.is_specific, slot.max_capacity, slot.nb_remaining_places, slot.nb_potential_remaining_places, slot.nb_places_taken, slot.id_form "
            + " FROM appointment_appointment app " + "INNER JOIN appointment_user user ON app.id_user = user.id_user "
            + " INNER JOIN appointment_appointment_slot app_slot ON app.id_appointment = app_slot.id_appointment"
            + " INNER JOIN appointment_slot slot ON app_slot.id_slot = slot.id_slot WHERE id_form != 0";

    private static final String SQL_QUERY_INSERT_APPT_SLT = "INSERT INTO appointment_appointment_slot (id_appointment, id_slot, nb_places) VALUES ( ?, ?, ?)";
    private static final String SQL_QUERY_DELETE_APPT_SLT = "DELETE FROM appointment_appointment_slot WHERE id_appointment = ?";
    private static final String SQL_QUERY_SELECT_APPT_SLT = "SELECT id_appointment, id_slot, nb_places FROM appointment_appointment_slot where id_appointment = ?";

    private static final String SQL_QUERY_SELECT_BY_LIST_ID_SLOT = SQL_QUERY_SELECT_COLUMNS
            + ",appt_slot.nb_places FROM appointment_appointment appointment INNER JOIN appointment_appointment_slot appt_slot on ( appt_slot.id_appointment = appointment.id_appointment ) where appt_slot.id_slot IN(";
    private static final String SQL_FILTER_FIRST_NAME = "UPPER(user.first_name) LIKE ?";
    private static final String SQL_FILTER_LAST_NAME = "UPPER(user.last_name) LIKE ?";
    private static final String SQL_FILTER_EMAIL = "UPPER(user.email) LIKE ?";
    private static final String SQL_FILTER_ID_FORM = "slot.id_form = ?";
    private static final String SQL_FILTER_GUID = "user.guid = ?";
    private static final String SQL_FILTER_STATUS = "app.is_cancelled = ?";
    private static final String SQL_FILTER_DATE_APPOINTMENT_MIN = "slot.starting_date_time >= ?";
    private static final String SQL_FILTER_DATE_APPOINTMENT_MAX = "slot.starting_date_time < ?";

    private static final String CONSTANT_AND = " AND ";
    private static final String CONSTANT_PERCENT = "%";

    @Override
    public void insert( Appointment appointment, Plugin plugin )
    {
        appointment.setDateAppointmentTaken( LocalDateTime.now( ) );

        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, appointment, plugin, true ) )
        {
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                appointment.setIdAppointment( daoUtil.getGeneratedKeyInt( 1 ) );
            }

        }
        for ( AppointmentSlot apptSlot : appointment.getListAppointmentSlot( ) )
        {

            apptSlot.setIdAppointment( appointment.getIdAppointment( ) );
            insertAppointmentSlot( apptSlot, plugin );
        }
    }

    private List<AppointmentSlot> selectAppointmentSlot( int nIdAppointment, Plugin plugin )
    {
        List<AppointmentSlot> listAppointmentSlot = new ArrayList<>( );
        AppointmentSlot appointmentSlot = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_APPT_SLT, plugin ) )
        {
            daoUtil.setInt( 1, nIdAppointment );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                appointmentSlot = new AppointmentSlot( );
                appointmentSlot.setIdAppointment( daoUtil.getInt( 1 ) );
                appointmentSlot.setIdSlot( daoUtil.getInt( 2 ) );
                appointmentSlot.setNbPlaces( daoUtil.getInt( 3 ) );

                listAppointmentSlot.add( appointmentSlot );
            }
        }
        return listAppointmentSlot;
    }

    private void insertAppointmentSlot( AppointmentSlot apptSlot, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_APPT_SLT, plugin ) )
        {
            daoUtil.setInt( 1, apptSlot.getIdAppointment( ) );
            daoUtil.setInt( 2, apptSlot.getIdSlot( ) );
            daoUtil.setInt( 3, apptSlot.getNbPlaces( ) );

            daoUtil.executeUpdate( );
        }
    }

    private void deleteAppointmentSlot( int nIdAppointment, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_APPT_SLT, plugin ) )
        {
            daoUtil.setInt( 1, nIdAppointment );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void update( Appointment appointment, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, appointment, plugin, false ) )
        {
            daoUtil.executeUpdate( );
        }
        if ( appointment.getListAppointmentSlot( ) != null && !appointment.getListAppointmentSlot( ).isEmpty( ) )
        {
            deleteAppointmentSlot( appointment.getIdAppointment( ), plugin );
            for ( AppointmentSlot apptSlot : appointment.getListAppointmentSlot( ) )
            {
                insertAppointmentSlot( apptSlot, plugin );
            }
        }
    }

    @Override
    public void delete( int nIdAppointment, Plugin plugin )
    {
        deleteAppointmentSlot( nIdAppointment, plugin );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdAppointment );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public Appointment select( int nIdAppointment, Plugin plugin )
    {
        Appointment appointment = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nIdAppointment );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                appointment = buildAppointment( daoUtil );
            }
        }
        if ( appointment != null )
        {

            appointment.setListAppointmentSlot( selectAppointmentSlot( nIdAppointment, plugin ) );
        }
        return appointment;
    }

    @Override
    public List<Appointment> findByIdUser( int nIdUser, Plugin plugin )
    {
        List<Appointment> listAppointment = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_USER, plugin ) )
        {
            daoUtil.setInt( 1, nIdUser );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                Appointment appt = buildAppointment( daoUtil );
                appt.setListAppointmentSlot( selectAppointmentSlot( appt.getIdAppointment( ), plugin ) );

                listAppointment.add( appt );
            }
        }
        return listAppointment;
    }

    @Override
    public List<Appointment> findByGuidUser( String strGuidUser, Plugin plugin )
    {
        List<Appointment> listAppointment = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_GUID_USER, plugin ) )
        {
            daoUtil.setString( 1, strGuidUser );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                Appointment appt = buildAppointment( daoUtil );
                appt.setListAppointmentSlot( selectAppointmentSlot( appt.getIdAppointment( ), plugin ) );

                listAppointment.add( appt );
            }
        }
        return listAppointment;
    }

    @Override
    public List<Appointment> findByIdSlot( int nIdSlot, Plugin plugin )
    {
        List<Appointment> listAppointment = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_SLOT, plugin ) )
        {
            daoUtil.setInt( 1, nIdSlot );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                Appointment appointment = buildAppointment( daoUtil );
                appointment.setListAppointmentSlot( selectAppointmentSlot( appointment.getIdAppointment( ), plugin ) );
                listAppointment.add( appointment );
            }
        }
        return listAppointment;
    }
    @Override
    public List<Appointment> findByListIdSlot( List<Integer> listIdSlot, Plugin plugin )
    {
        List<Appointment> list = new ArrayList<>( );
        
        if(CollectionUtils.isEmpty(listIdSlot)) {
        	
        	return list;
        }
        String query = SQL_QUERY_SELECT_BY_LIST_ID_SLOT + listIdSlot.stream( ).distinct( ).map( i -> "?" ).collect( Collectors.joining( "," ) ) + " )";

        try ( DAOUtil daoUtil = new DAOUtil( query, plugin ) )
        {
            for ( int i = 0; i < listIdSlot.size( ); i++ )
            {
                daoUtil.setInt( i + 1, listIdSlot.get( i ) );
            }
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
            	Appointment appointment = buildAppointment( daoUtil );
            	if( list.stream().noneMatch( appt -> appt.getIdAppointment() == appointment.getIdAppointment( ))) {
            		
	                appointment.setListAppointmentSlot( selectAppointmentSlot( appointment.getIdAppointment( ), plugin ) );
	                list.add( appointment );
            	}
            }
        }
        return list;
    }

    @Override
    public Appointment findByReference( String strReference, Plugin plugin )
    {
        Appointment appointment = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_REFERENCE, plugin ) )
        {
            daoUtil.setString( 1, strReference );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                appointment = buildAppointment( daoUtil );
                appointment.setListAppointmentSlot( selectAppointmentSlot( appointment.getIdAppointment( ), plugin ) );
            }
        }
        return appointment;
    }

    @Override
    public List<Appointment> findByFilter( AppointmentFilterDTO appointmentFilter, Plugin plugin )
    {
        Map<Integer, Appointment> mapAppointment = new HashMap< >();
        boolean isFirst= true;
        try ( DAOUtil daoUtil = new DAOUtil( getSqlQueryFromFilter( appointmentFilter ), plugin ) )
        {
            addFilterParametersToDAOUtil( appointmentFilter, daoUtil );
            daoUtil.executeQuery( );
            
            while ( daoUtil.next( ) )
            {
                Appointment appt = buildAppointment( daoUtil );

                Slot slot = builSlot( daoUtil, 17 );
                User user = buildUser( daoUtil, 11 );

                
                if( isFirst || daoUtil.isLast( ) ) {
                	
                	appt.setSlot(SlotHome.findByIdAppointment( appt.getIdAppointment() ));
                	
                }else {
                
                	appt.addSlot( slot );
                }
                appt.setUser( user );

                Appointment apptAdded = mapAppointment.get(appt.getIdAppointment( ));
                if ( apptAdded == null )
                {
                	mapAppointment.put(appt.getIdAppointment( ), appt);
                }
                else
                {
                    apptAdded.addSlot( slot );
                }
                
                isFirst= false;
            }
            
            
        }
        return new ArrayList< >( mapAppointment.values( ));
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
        int nIndex = 0;
        if ( appointmentFilter.getIdForm( ) != 0 )
        {
            daoUtil.setInt( ++nIndex, appointmentFilter.getIdForm( ) );
        }
        if ( appointmentFilter.getFirstName( ) != null )
        {
            daoUtil.setString( ++nIndex, CONSTANT_PERCENT + appointmentFilter.getFirstName( ).toUpperCase( ) + CONSTANT_PERCENT );
        }
        if ( appointmentFilter.getLastName( ) != null )
        {
            daoUtil.setString( ++nIndex, CONSTANT_PERCENT + appointmentFilter.getLastName( ).toUpperCase( ) + CONSTANT_PERCENT );
        }
        if ( appointmentFilter.getEmail( ) != null )
        {
            daoUtil.setString( ++nIndex, CONSTANT_PERCENT + appointmentFilter.getEmail( ).toUpperCase( ) + CONSTANT_PERCENT );
        }
        if ( appointmentFilter.getGuid( ) != null )
        {
            daoUtil.setString( ++nIndex, appointmentFilter.getGuid( ) );
        }
        if ( appointmentFilter.getStatus( ) != -1 )
        {
            daoUtil.setInt( ++nIndex, appointmentFilter.getStatus( ) );
        }
        if ( appointmentFilter.getStartingDateOfSearch( ) != null )
        {
            Timestamp startingTimestamp;
            if ( StringUtils.isNotEmpty( appointmentFilter.getStartingTimeOfSearch( ) ) )
            {
                startingTimestamp = Timestamp.valueOf(
                        appointmentFilter.getStartingDateOfSearch( ).toLocalDate( ).atTime( LocalTime.parse( appointmentFilter.getStartingTimeOfSearch( ) ) ) );
            }
            else
            {
                startingTimestamp = Timestamp.valueOf( appointmentFilter.getStartingDateOfSearch( ).toLocalDate( ).atStartOfDay( ) );
            }
            daoUtil.setTimestamp( ++nIndex, startingTimestamp );
        }
        if ( appointmentFilter.getEndingDateOfSearch( ) != null )
        {
            Timestamp endingTimestamp;
            if ( StringUtils.isNotEmpty( appointmentFilter.getEndingTimeOfSearch( ) ) )
            {
                endingTimestamp = Timestamp.valueOf(
                        appointmentFilter.getEndingDateOfSearch( ).toLocalDate( ).atTime( LocalTime.parse( appointmentFilter.getEndingTimeOfSearch( ) ) ) );
            }
            else
            {
                endingTimestamp = Timestamp.valueOf( appointmentFilter.getEndingDateOfSearch( ).toLocalDate( ).atTime( LocalTime.MAX ) );
            }
            daoUtil.setTimestamp( ++nIndex, endingTimestamp );
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

        if ( appointmentFilter.getIdForm( ) != 0 )
        {
            sbSql.append( CONSTANT_AND );
            sbSql.append( SQL_FILTER_ID_FORM );
        }
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
        if ( appointmentFilter.getGuid( ) != null )
        {
            sbSql.append( CONSTANT_AND );
            sbSql.append( SQL_FILTER_GUID );
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
        List<Appointment> listAppointment = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listAppointment.add( buildAppointment( daoUtil ) );
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
        appointment.setAppointmentTakenSqlDate( daoUtil.getTimestamp( nIndex ) );
        return appointment;
    }

    /**
     * Build an Slot business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new Slot business object with all its attributes assigned
     */
    private Slot builSlot( DAOUtil daoUtil, int nIndex )
    {
        Slot slot = new Slot( );
        slot.setIdSlot( daoUtil.getInt( nIndex++ ) );
        slot.setStartingTimeStampDate( daoUtil.getTimestamp( nIndex++ ) );
        slot.setEndingTimeStampDate( daoUtil.getTimestamp( nIndex++ ) );
        slot.setIsOpen( daoUtil.getBoolean( nIndex++ ) );
        slot.setIsSpecific( daoUtil.getBoolean( nIndex++ ) );
        slot.setMaxCapacity( daoUtil.getInt( nIndex++ ) );
        slot.setNbRemainingPlaces( daoUtil.getInt( nIndex++ ) );
        slot.setNbPotentialRemainingPlaces( daoUtil.getInt( nIndex++ ) );
        slot.setNbPlacestaken( daoUtil.getInt( nIndex++ ) );
        slot.setIdForm( daoUtil.getInt( nIndex ) );
        return slot;
    }

    /**
     * Build a User business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new User with all its attributes assigned
     */
    private User buildUser( DAOUtil daoUtil, int nIndex )
    {

        User user = new User( );
        user.setIdUser( daoUtil.getInt( nIndex++ ) );
        user.setGuid( daoUtil.getString( nIndex++ ) );
        user.setFirstName( daoUtil.getString( nIndex++ ) );
        user.setLastName( daoUtil.getString( nIndex++ ) );
        user.setEmail( daoUtil.getString( nIndex++ ) );
        user.setPhoneNumber( daoUtil.getString( nIndex ) );
        return user;
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
        DAOUtil daoUtil = null;
        if ( isInsert )
        {
            daoUtil = new DAOUtil( query, Statement.RETURN_GENERATED_KEYS, plugin );
        }
        else
        {
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
        daoUtil.setTimestamp( nIndex++, appointment.getAppointmentTakenSqlDate( ) );

        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, appointment.getIdAppointment( ) );
        }
        return daoUtil;
    }
}
