/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *         and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *         and the following disclaimer in the documentation and/or other materials
 *         provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *         contributors may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
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
package fr.paris.lutece.plugins.appointment.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import org.apache.commons.lang.StringUtils;

import java.sql.Date;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for Appointment objects
 */
public final class AppointmentDAO implements IAppointmentDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_appointment ) FROM appointment_appointment";
    private static final String SQL_QUERY_SELECTALL = "SELECT app.id_appointment, app.first_name, app.last_name, app.email, app.id_user, app.authentication_service, app.localization, app.date_appointment, app.id_slot, app.status, app.id_action_cancel, app.id_admin_user FROM appointment_appointment app ";
    private static final String SQL_QUERY_SELECT_ID = "SELECT app.id_appointment FROM appointment_appointment app ";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE app.id_appointment = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = " INNER JOIN appointment_slot slot ON app.id_slot = slot.id_slot AND slot.id_form = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_appointment ( id_appointment, first_name, last_name, email, id_user, authentication_service, localization, date_appointment, id_slot, status, id_action_cancel, id_admin_user ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_appointment WHERE id_appointment = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_appointment SET first_name = ?, last_name = ?, email = ?, id_user = ?, authentication_service = ?, localization = ?, date_appointment = ?, id_slot = ?, status = ?, id_action_cancel = ?, id_admin_user = ? WHERE id_appointment = ?";
    private static final String SQL_QUERY_COUNT_APPOINTMENTS_BY_ID_FORM = "SELECT COUNT(app.id_appointment) FROM appointment_appointment app INNER JOIN appointment_slot slot ON app.id_slot = slot.id_slot WHERE slot.id_form = ? AND app.date_appointment > ? ";
    private static final String SQL_QUERY_SELECT_BY_LIST_ID = SQL_QUERY_SELECTALL + " WHERE id_appointment IN (";
    private static final String SQL_QUERY_COUNT_APPOINTMENT_BY_DATE_AND_FORM = " SELECT COUNT(app.id_appointment) FROM appointment_appointment app INNER JOIN appointment_slot slot ON app.id_slot = slot.id_slot WHERE date_appointment = ? AND slot.id_form = ? ";

    // SQL commands to manage appointment responses
    private static final String SQL_QUERY_INSERT_APPOINTMENT_RESPONSE = "INSERT INTO appointment_appointment_response (id_appointment, id_response) VALUES (?,?)";
    private static final String SQL_QUERY_SELECT_APPOINTMENT_RESPONSE_LIST = "SELECT id_response FROM appointment_appointment_response WHERE id_appointment = ?";
    private static final String SQL_QUERY_DELETE_APPOINTMENT_RESPONSE = "DELETE FROM appointment_appointment_response WHERE id_appointment = ?";
    private static final String SQL_QUERY_REMOVE_FROM_ID_RESPONSE = "DELETE FROM appointment_appointment_response WHERE id_response = ?";
    private static final String SQL_QUERY_FIND_ID_APPOINTMENT_FROM_ID_RESPONSE = " SELECT id_appointment FROM appointment_appointment_response WHERE id_response = ? ";

    // Filters
    private static final String SQL_FILTER_ID_SLOT = " app.id_slot = ? ";
    private static final String SQL_FILTER_FIRST_NAME = " app.first_name LIKE ? ";
    private static final String SQL_FILTER_LAST_NAME = " app.last_name LIKE ?";
    private static final String SQL_FILTER_EMAIL = " app.email LIKE ? ";
    private static final String SQL_FILTER_ID_USER = " app.id_user = ? ";
    private static final String SQL_FILTER_AUTHENTICATION_SERVICE = " app.authentication_service = ? ";
    private static final String SQL_FILTER_ADMIN_USER = " app.id_admin_user = ? ";
    private static final String SQL_FILTER_DATE_APPOINTMENT = " app.date_appointment = ? ";
    private static final String SQL_FILTER_DATE_APPOINTMENT_MIN = " app.date_appointment >= ? ";
    private static final String SQL_FILTER_DATE_APPOINTMENT_MAX = " app.date_appointment <= ? ";
    private static final String SQL_FILTER_STATUS = " app.status = ? ";

    // Constants
    private static final String CONSTANT_WHERE = " WHERE ";
    private static final String CONSTANT_AND = " AND ";
    private static final String CONSTANT_ORDER_BY = " ORDER BY ";
    private static final String CONSTANT_PERCENT = "%";
    private static final String CONSTANT_QUESTION_MARK = "?";
    private static final String CONSTANT_COMMA = ",";
    private static final String CONSTANT_CLOSE_PARENTHESIS = ")";
    private static final String CONSTANT_ASC = " ASC";
    private static final String CONSTANT_DESC = " DESC";

    /**
     * Generates a new primary key
     * @param plugin The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey = 1;

        if ( daoUtil.next(  ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free(  );

        return nKey;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void insert( Appointment appointment, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        appointment.setIdAppointment( newPrimaryKey( plugin ) );

        int nIndex = 1;
        daoUtil.setInt( nIndex++, appointment.getIdAppointment(  ) );
        daoUtil.setString( nIndex++, appointment.getFirstName(  ) );
        daoUtil.setString( nIndex++, appointment.getLastName(  ) );
        daoUtil.setString( nIndex++, appointment.getEmail(  ) );
        daoUtil.setString( nIndex++, appointment.getIdUser(  ) );
        daoUtil.setString( nIndex++, appointment.getAuthenticationService(  ) );
        daoUtil.setString( nIndex++, appointment.getLocation(  ) );
        daoUtil.setDate( nIndex++, appointment.getDateAppointment(  ) );
        daoUtil.setInt( nIndex++, appointment.getIdSlot(  ) );
        daoUtil.setInt( nIndex++, appointment.getStatus(  ) );
        daoUtil.setInt( nIndex++, appointment.getIdActionCancel(  ) );
        daoUtil.setInt( nIndex, appointment.getIdAdminUser(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Appointment load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        Appointment appointment = null;

        if ( daoUtil.next(  ) )
        {
            appointment = getAppointmentFormValues( daoUtil );
        }

        daoUtil.free(  );

        return appointment;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nAppointmentId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nAppointmentId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Appointment appointment, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;
        daoUtil.setString( nIndex++, appointment.getFirstName(  ) );
        daoUtil.setString( nIndex++, appointment.getLastName(  ) );
        daoUtil.setString( nIndex++, appointment.getEmail(  ) );
        daoUtil.setString( nIndex++, appointment.getIdUser(  ) );
        daoUtil.setString( nIndex++, appointment.getAuthenticationService(  ) );
        daoUtil.setString( nIndex++, appointment.getLocation(  ) );
        daoUtil.setDate( nIndex++, appointment.getDateAppointment(  ) );
        daoUtil.setInt( nIndex++, appointment.getIdSlot(  ) );
        daoUtil.setInt( nIndex++, appointment.getStatus(  ) );
        daoUtil.setInt( nIndex++, appointment.getIdActionCancel(  ) );
        daoUtil.setInt( nIndex++, appointment.getIdAdminUser(  ) );
        daoUtil.setInt( nIndex, appointment.getIdAppointment(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Appointment> selectAppointmentsList( Plugin plugin )
    {
        List<Appointment> appointmentList = new ArrayList<Appointment>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            appointmentList.add( getAppointmentFormValues( daoUtil ) );
        }

        daoUtil.free(  );

        return appointmentList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Appointment> selectAppointmentsListByIdForm( int nIdForm, Plugin plugin )
    {
        List<Appointment> appointmentList = new ArrayList<Appointment>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL + SQL_QUERY_SELECT_BY_ID_FORM, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            appointmentList.add( getAppointmentFormValues( daoUtil ) );
        }

        daoUtil.free(  );

        return appointmentList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Appointment> selectAppointmentListByFilter( AppointmentFilter appointmentFilter, Plugin plugin )
    {
        List<Appointment> appointmentList = new ArrayList<Appointment>(  );

        DAOUtil daoUtil = new DAOUtil( getSqlQueryFromFilter( appointmentFilter, true ), plugin );

        addFilterParametersToDAOUtil( appointmentFilter, daoUtil );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            appointmentList.add( getAppointmentFormValues( daoUtil ) );
        }

        daoUtil.free(  );

        return appointmentList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> selectAppointmentIdByFilter( AppointmentFilter appointmentFilter, Plugin plugin )
    {
        List<Integer> appointmentIdList = new ArrayList<Integer>(  );

        DAOUtil daoUtil = new DAOUtil( getSqlQueryFromFilter( appointmentFilter, true ), plugin );

        addFilterParametersToDAOUtil( appointmentFilter, daoUtil );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            appointmentIdList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return appointmentIdList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Appointment> selectAppointmentListById( List<Integer> listIdAppointments, String strOrderBy,
        boolean bSortAsc, Plugin plugin )
    {
        List<Appointment> appointmentList = new ArrayList<Appointment>(  );

        if ( ( listIdAppointments != null ) && ( listIdAppointments.size(  ) > 0 ) )
        {
            StringBuilder sbSql = new StringBuilder( SQL_QUERY_SELECT_BY_LIST_ID );
            sbSql.append( CONSTANT_QUESTION_MARK );

            for ( int i = 1; i < listIdAppointments.size(  ); i++ )
            {
                sbSql.append( CONSTANT_COMMA );
                sbSql.append( CONSTANT_QUESTION_MARK );
            }

            sbSql.append( CONSTANT_CLOSE_PARENTHESIS );

            if ( StringUtils.isNotEmpty( strOrderBy ) )
            {
                sbSql.append( CONSTANT_ORDER_BY );
                sbSql.append( strOrderBy );
                sbSql.append( bSortAsc ? CONSTANT_ASC : CONSTANT_DESC );
            }

            DAOUtil daoUtil = new DAOUtil( sbSql.toString(  ), plugin );

            int nIndex = 1;

            for ( Integer nIdAppointment : listIdAppointments )
            {
                daoUtil.setInt( nIndex++, nIdAppointment );
            }

            daoUtil.executeQuery(  );

            while ( daoUtil.next(  ) )
            {
                appointmentList.add( getAppointmentFormValues( daoUtil ) );
            }

            daoUtil.free(  );
        }

        return appointmentList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNbAppointmentByIdDay( Date dateAppointment, int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_APPOINTMENT_BY_DATE_AND_FORM, plugin );
        daoUtil.setDate( 1, dateAppointment );
        daoUtil.setInt( 2, nIdForm );
        daoUtil.executeQuery(  );

        int nRes = 0;

        if ( daoUtil.next(  ) )
        {
            nRes = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nRes;
    }

    // ----------------------------------------
    // Appointment response management
    // ----------------------------------------

    /**
     * {@inheritDoc }
     */
    @Override
    public void insertAppointmentResponse( int nIdAppointment, int nIdResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_APPOINTMENT_RESPONSE, plugin );
        daoUtil.setInt( 1, nIdAppointment );
        daoUtil.setInt( 2, nIdResponse );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> findListIdResponse( int nIdAppointment, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_APPOINTMENT_RESPONSE_LIST, plugin );
        daoUtil.setInt( 1, nIdAppointment );
        daoUtil.executeQuery(  );

        List<Integer> listIdResponse = new ArrayList<Integer>(  );

        while ( daoUtil.next(  ) )
        {
            listIdResponse.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return listIdResponse;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteAppointmentResponse( int nIdAppointment, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_APPOINTMENT_RESPONSE, plugin );
        daoUtil.setInt( 1, nIdAppointment );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAppointmentResponsesByIdResponse( int nIdResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REMOVE_FROM_ID_RESPONSE, plugin );
        daoUtil.setInt( 1, nIdResponse );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countAppointmentsByIdForm( int nIdForm, Date date, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_APPOINTMENTS_BY_ID_FORM, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.setDate( 2, date );
        daoUtil.executeQuery(  );

        int nRes = 0;

        if ( daoUtil.next(  ) )
        {
            nRes = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nRes;
    }

    /**
     * Get the SQL string to execute to find appointments matching a given
     * filter
     * @param appointmentFilter The filter appointments must match
     * @param bLoadFields True to load every fields of appointments, false to
     *            only load their ids
     * @return The SQL String to execute
     */
    private String getSqlQueryFromFilter( AppointmentFilter appointmentFilter, boolean bLoadFields )
    {
        StringBuilder sbSql = new StringBuilder( bLoadFields ? SQL_QUERY_SELECTALL : SQL_QUERY_SELECT_ID );

        if ( appointmentFilter.getIdForm(  ) > 0 )
        {
            sbSql.append( SQL_QUERY_SELECT_BY_ID_FORM );
        }

        boolean bHasFilter = false;

        if ( appointmentFilter.getIdSlot(  ) > 0 )
        {
            sbSql.append( CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_ID_SLOT );
            bHasFilter = true;
        }

        if ( appointmentFilter.getFirstName(  ) != null )
        {
            sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_FIRST_NAME );
            bHasFilter = true;
        }

        if ( appointmentFilter.getLastName(  ) != null )
        {
            sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_LAST_NAME );
            bHasFilter = true;
        }

        if ( appointmentFilter.getEmail(  ) != null )
        {
            sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_EMAIL );
            bHasFilter = true;
        }

        if ( appointmentFilter.getIdUser(  ) != null )
        {
            sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_ID_USER );
            bHasFilter = true;
        }

        if ( appointmentFilter.getAuthenticationService(  ) != null )
        {
            sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_AUTHENTICATION_SERVICE );
            bHasFilter = true;
        }

        if ( appointmentFilter.getIdAdminUser(  ) >= 0 )
        {
            sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_ADMIN_USER );
            bHasFilter = true;
        }

        if ( appointmentFilter.getDateAppointment(  ) != null )
        {
            sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_DATE_APPOINTMENT );
            bHasFilter = true;
        }
        else
        {
            if ( appointmentFilter.getDateAppointmentMin(  ) != null )
            {
                sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
                sbSql.append( SQL_FILTER_DATE_APPOINTMENT_MIN );
                bHasFilter = true;
            }

            if ( appointmentFilter.getDateAppointmentMax(  ) != null )
            {
                sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
                sbSql.append( SQL_FILTER_DATE_APPOINTMENT_MAX );
                bHasFilter = true;
            }
        }

        if ( appointmentFilter.getStatus(  ) != AppointmentFilter.NO_STATUS_FILTER )
        {
            sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_STATUS );
            bHasFilter = true;
        }

        if ( StringUtils.isNotBlank( appointmentFilter.getOrderBy(  ) ) )
        {
            sbSql.append( CONSTANT_ORDER_BY );
            sbSql.append( appointmentFilter.getOrderBy(  ) );
            sbSql.append( appointmentFilter.getOrderAsc(  ) ? CONSTANT_ASC : CONSTANT_DESC );
        }

        return sbSql.toString(  );
    }

    /**
     * Add filter parameters to a DAOUtil
     * @param appointmentFilter The filter to add parameters from
     * @param daoUtil The DAOUtil to add parameters
     */
    private void addFilterParametersToDAOUtil( AppointmentFilter appointmentFilter, DAOUtil daoUtil )
    {
        int nIndex = 1;

        if ( appointmentFilter.getIdForm(  ) > 0 )
        {
            daoUtil.setInt( nIndex++, appointmentFilter.getIdForm(  ) );
        }

        if ( appointmentFilter.getIdSlot(  ) > 0 )
        {
            daoUtil.setInt( nIndex++, appointmentFilter.getIdSlot(  ) );
        }

        if ( appointmentFilter.getFirstName(  ) != null )
        {
            daoUtil.setString( nIndex++, CONSTANT_PERCENT + appointmentFilter.getFirstName(  ) + CONSTANT_PERCENT );
        }

        if ( appointmentFilter.getLastName(  ) != null )
        {
            daoUtil.setString( nIndex++, CONSTANT_PERCENT + appointmentFilter.getLastName(  ) + CONSTANT_PERCENT );
        }

        if ( appointmentFilter.getEmail(  ) != null )
        {
            daoUtil.setString( nIndex++, CONSTANT_PERCENT + appointmentFilter.getEmail(  ) + CONSTANT_PERCENT );
        }

        if ( appointmentFilter.getIdUser(  ) != null )
        {
            daoUtil.setString( nIndex++, appointmentFilter.getIdUser(  ) );
        }

        if ( appointmentFilter.getAuthenticationService(  ) != null )
        {
            daoUtil.setString( nIndex++, appointmentFilter.getAuthenticationService(  ) );
        }

        if ( appointmentFilter.getIdAdminUser(  ) >= 0 )
        {
            daoUtil.setInt( nIndex++, appointmentFilter.getIdAdminUser(  ) );
        }

        if ( appointmentFilter.getDateAppointment(  ) != null )
        {
            daoUtil.setDate( nIndex++, appointmentFilter.getDateAppointment(  ) );
        }
        else
        {
            if ( appointmentFilter.getDateAppointmentMin(  ) != null )
            {
                daoUtil.setDate( nIndex++, appointmentFilter.getDateAppointmentMin(  ) );
            }

            if ( appointmentFilter.getDateAppointmentMax(  ) != null )
            {
                daoUtil.setDate( nIndex++, appointmentFilter.getDateAppointmentMax(  ) );
            }
        }

        if ( appointmentFilter.getStatus(  ) != AppointmentFilter.NO_STATUS_FILTER )
        {
            daoUtil.setInt( nIndex++, appointmentFilter.getStatus(  ) );
        }
    }

    /**
     * Get data of an appointment from a daoUtil
     * @param daoUtil The daoUtil to get data from
     * @return The appointment with data of the current row of the daoUtil
     */
    private Appointment getAppointmentFormValues( DAOUtil daoUtil )
    {
        Appointment appointment = new Appointment(  );
        int nIndex = 1;
        appointment.setIdAppointment( daoUtil.getInt( nIndex++ ) );
        appointment.setFirstName( daoUtil.getString( nIndex++ ) );
        appointment.setLastName( daoUtil.getString( nIndex++ ) );
        appointment.setEmail( daoUtil.getString( nIndex++ ) );
        appointment.setIdUser( daoUtil.getString( nIndex++ ) );
        appointment.setAuthenticationService( daoUtil.getString( nIndex++ ) );
        appointment.setLocation( daoUtil.getString( nIndex++ ) );
        appointment.setDateAppointment( daoUtil.getDate( nIndex++ ) );
        appointment.setIdSlot( daoUtil.getInt( nIndex++ ) );
        appointment.setStatus( daoUtil.getInt( nIndex++ ) );
        appointment.setIdActionCancel( daoUtil.getInt( nIndex++ ) );
        appointment.setIdAdminUser( daoUtil.getInt( nIndex ) );

        return appointment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findIdAppointmentByIdResponse( int nIdResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_ID_APPOINTMENT_FROM_ID_RESPONSE, plugin );
        daoUtil.setInt( 1, nIdResponse );
        daoUtil.executeQuery(  );

        int nIdAppointment = 0;

        if ( daoUtil.next(  ) )
        {
            nIdAppointment = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nIdAppointment;
    }
}
