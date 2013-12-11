/*
 * Copyright (c) 2002-2013, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *	 and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *	 and the following disclaimer in the documentation and/or other materials
 *	 provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *	 contributors may be used to endorse or promote products derived from
 *	 this software without specific prior written permission.
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

import java.util.ArrayList;
import java.util.Collection;


/**
 * This class provides Data Access methods for Appointment objects
 */

public final class AppointmentDAO implements IAppointmentDAO
{

    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_appointment ) FROM appointment_appointment";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_appointment, first_name, last_name, email, id_user, time_appointment, date_appointment FROM appointment_appointment";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE id_appointment = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_appointment ( id_appointment, first_name, last_name, email, id_user, time_appointment, date_appointment ) VALUES ( ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_appointment WHERE id_appointment = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_appointment SET first_name = ?, last_name = ?, email = ?, id_user = ?, time_appointment = ?, date_appointment = ? WHERE id_appointment = ?";

    /**
     * Generates a new primary key
     * @param plugin The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery( );

        int nKey = 1;

        if ( daoUtil.next( ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free( );

        return nKey;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Appointment appointment, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        appointment.setIdAppointment( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, appointment.getIdAppointment( ) );
        daoUtil.setString( 2, appointment.getFirstName( ) );
        daoUtil.setString( 3, appointment.getLastName( ) );
        daoUtil.setString( 4, appointment.getEmail( ) );
        daoUtil.setString( 5, appointment.getIdUser( ) );
        daoUtil.setString( 6, appointment.getTimeAppointment( ) );
        daoUtil.setDate( 7, appointment.getDateAppointment( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Appointment load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );

        Appointment appointment = null;

        if ( daoUtil.next( ) )
        {
            appointment = getAppointmentFormValues( daoUtil );
        }

        daoUtil.free( );
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
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Appointment appointment, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setString( 1, appointment.getFirstName( ) );
        daoUtil.setString( 2, appointment.getLastName( ) );
        daoUtil.setString( 3, appointment.getEmail( ) );
        daoUtil.setString( 4, appointment.getIdUser( ) );
        daoUtil.setString( 5, appointment.getTimeAppointment( ) );
        daoUtil.setDate( 6, appointment.getDateAppointment( ) );
        daoUtil.setInt( 7, appointment.getIdAppointment( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Appointment> selectAppointmentsList( Plugin plugin )
    {
        Collection<Appointment> appointmentList = new ArrayList<Appointment>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            appointmentList.add( getAppointmentFormValues( daoUtil ) );
        }

        daoUtil.free( );
        return appointmentList;
    }

    /**
     * Get data of an appointment from a daoUtil
     * @param daoUtil The daoUtil to get data from
     * @return The appointment with data of the current row of the daoUtil
     */
    private Appointment getAppointmentFormValues( DAOUtil daoUtil )
    {
        Appointment appointment = new Appointment( );

        appointment.setIdAppointment( daoUtil.getInt( 1 ) );
        appointment.setFirstName( daoUtil.getString( 2 ) );
        appointment.setLastName( daoUtil.getString( 3 ) );
        appointment.setEmail( daoUtil.getString( 4 ) );
        appointment.setIdUser( daoUtil.getString( 5 ) );
        appointment.setTimeAppointment( daoUtil.getString( 6 ) );
        appointment.setDateAppointment( daoUtil.getDate( 7 ) );

        return appointment;
    }
}
