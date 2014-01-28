/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * This class provides Data Access methods for Appointment objects
 */
public final class AppointmentDAO implements IAppointmentDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_appointment ) FROM appointment_appointment";
    private static final String SQL_QUERY_SELECTALL = "SELECT app.id_appointment, app.first_name, app.last_name, app.email, app.id_user, app.authentication_service, app.date_appointment, app.id_slot, app.status FROM appointment_appointment app ";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE app.id_appointment = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECTALL +
        " INNER JOIN appointment_slot slot ON app.id_slot = slot.id_slot AND slot.id_form = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_appointment ( id_appointment, first_name, last_name, email, id_user, authentication_service, date_appointment, id_slot, status ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_appointment WHERE id_appointment = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_appointment SET first_name = ?, last_name = ?, email = ?, id_user = ?, authentication_service = ?, date_appointment = ?, id_slot = ?, status = ? WHERE id_appointment = ?";
    private static final String SQL_QUERY_COUNT_APPOINTMENTS_BY_ID_FORM = "SELECT COUNT(app.id_appointment) FROM appointment_appointment app INNER JOIN appointment_slot slot ON app.id_slot = slot.id_slot WHERE slot.id_form = ? AND app.date_appointment > ? ";

    // SQL commands to manage appointment responses
    private static final String SQL_QUERY_INSERT_APPOINTMENT_RESPONSE = "INSERT INTO appointment_appointment_response (id_appointment, id_response) VALUES (?,?)";
    private static final String SQL_QUERY_SELECT_APPOINTMENT_RESPONSE_LIST = "SELECT id_response FROM appointment_appointment_response WHERE id_appointment = ?";
    private static final String SQL_QUERY_DELETE_APPOINTMENT_RESPONSE = "DELETE FROM appointment_appointment_response WHERE id_appointment = ?";

    // Filters
    private static final String SQL_FILTER_ID_SLOT = " id_slot = ? ";
    private static final String SQL_FILTER_FIRST_NAME = " first_name LIKE ? ";
    private static final String SQL_FILTER_LAST_NAME = " last_name LIKE ?";
    private static final String SQL_FILTER_EMAIL = " email LIKE ? ";
    private static final String SQL_FILTER_ID_USER = " id_user = ? ";
    private static final String SQL_FILTER_AUTHENTICATION_SERVICE = " authentication_service = ? ";
    private static final String SQL_FILTER_DATE_APPOINTMENT = " date_appointment = ? ";
    private static final String SQL_FILTER_STATUS = " status = ? ";

    // Constants
    private static final String CONSTANT_WHERE = " WHERE ";
    private static final String CONSTANT_AND = " AND ";
    private static final String CONSTANT_ORDER_BY = " ORDER BY ? ";
    private static final String CONSTANT_PERCENT = "%";

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
        daoUtil.setString( nIndex++, appointment.getAuthenticationService( ) );
        daoUtil.setDate( nIndex++, appointment.getDateAppointment(  ) );
        daoUtil.setInt( nIndex++, appointment.getIdSlot(  ) );
        daoUtil.setInt( nIndex, appointment.getStatus(  ) );

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
        daoUtil.setString( nIndex++, appointment.getAuthenticationService( ) );
        daoUtil.setDate( nIndex++, appointment.getDateAppointment(  ) );
        daoUtil.setInt( nIndex++, appointment.getIdSlot(  ) );
        daoUtil.setInt( nIndex++, appointment.getStatus(  ) );
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
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin );
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
    public List<Appointment> selectAppointmentListByFilter( AppointmentFilter appointmentFiler, Plugin plugin )
    {
        List<Appointment> appointmentList = new ArrayList<Appointment>(  );
        StringBuilder sbSql = new StringBuilder( ( appointmentFiler.getIdForm(  ) > 0 ) ? SQL_QUERY_SELECT_BY_ID_FORM
                                                                                        : SQL_QUERY_SELECTALL );
        boolean bHasFilter = false;

        if ( appointmentFiler.getIdSlot(  ) > 0 )
        {
            sbSql.append( CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_ID_SLOT );
            bHasFilter = true;
        }

        if ( appointmentFiler.getFirstName(  ) != null )
        {
            sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_FIRST_NAME );
            bHasFilter = true;
        }

        if ( appointmentFiler.getLastName(  ) != null )
        {
            sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_LAST_NAME );
            bHasFilter = true;
        }

        if ( appointmentFiler.getEmail(  ) != null )
        {
            sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_EMAIL );
            bHasFilter = true;
        }

        if ( appointmentFiler.getIdUser(  ) != null )
        {
            sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_ID_USER );
            bHasFilter = true;
        }

        if ( appointmentFiler.getAuthenticationService( ) != null )
        {
            sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_AUTHENTICATION_SERVICE );
            bHasFilter = true;
        }

        if ( appointmentFiler.getDateAppointment(  ) != null )
        {
            sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_DATE_APPOINTMENT );
            bHasFilter = true;
        }

        if ( appointmentFiler.getStatus(  ) != AppointmentFilter.NO_STATUS_FILTER )
        {
            sbSql.append( bHasFilter ? CONSTANT_AND : CONSTANT_WHERE );
            sbSql.append( SQL_FILTER_STATUS );
            bHasFilter = true;
        }

        if ( StringUtils.isNotBlank( appointmentFiler.getOrderBy( ) ) )
        {
            sbSql.append( CONSTANT_ORDER_BY );
        }

        DAOUtil daoUtil = new DAOUtil( sbSql.toString(  ), plugin );

        int nIndex = 1;

        if ( appointmentFiler.getIdForm(  ) > 0 )
        {
            daoUtil.setInt( nIndex++, appointmentFiler.getIdForm(  ) );
        }

        if ( bHasFilter )
        {
            if ( appointmentFiler.getIdSlot(  ) > 0 )
            {
                daoUtil.setInt( nIndex++, appointmentFiler.getIdSlot(  ) );
            }

            if ( appointmentFiler.getFirstName(  ) != null )
            {
                daoUtil.setString( nIndex++, CONSTANT_PERCENT + appointmentFiler.getFirstName(  ) + CONSTANT_PERCENT );
            }

            if ( appointmentFiler.getLastName(  ) != null )
            {
                daoUtil.setString( nIndex++, CONSTANT_PERCENT + appointmentFiler.getLastName(  ) + CONSTANT_PERCENT );
            }

            if ( appointmentFiler.getEmail(  ) != null )
            {
                daoUtil.setString( nIndex++, CONSTANT_PERCENT + appointmentFiler.getEmail(  ) + CONSTANT_PERCENT );
            }

            if ( appointmentFiler.getIdUser(  ) != null )
            {
                daoUtil.setString( nIndex++, appointmentFiler.getIdUser( ) );
            }

            if ( appointmentFiler.getAuthenticationService( ) != null )
            {
                daoUtil.setString( nIndex++, appointmentFiler.getAuthenticationService( ) );
            }

            if ( appointmentFiler.getDateAppointment(  ) != null )
            {
                daoUtil.setDate( nIndex++, appointmentFiler.getDateAppointment(  ) );
            }

            if ( appointmentFiler.getStatus(  ) != AppointmentFilter.NO_STATUS_FILTER )
            {
                daoUtil.setInt( nIndex++, appointmentFiler.getStatus(  ) );
            }
        }

        if ( StringUtils.isNotBlank( appointmentFiler.getOrderBy( ) ) )
        {
            daoUtil.setString( nIndex, appointmentFiler.getOrderBy( ) );
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            appointmentList.add( getAppointmentFormValues( daoUtil ) );
        }

        daoUtil.free(  );

        return appointmentList;
    }

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
        appointment.setDateAppointment( daoUtil.getDate( nIndex++ ) );
        appointment.setIdSlot( daoUtil.getInt( nIndex++ ) );
        appointment.setStatus( daoUtil.getInt( nIndex++ ) );

        return appointment;
    }
}
