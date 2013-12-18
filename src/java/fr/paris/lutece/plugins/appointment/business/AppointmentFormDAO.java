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
 * This class provides Data Access methods for AppointmentForm objects
 */

public final class AppointmentFormDAO implements IAppointmentFormDAO
{

    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_form ) FROM appointment_form";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_form, title, time_start, time_end, duration_appointments, is_open_monday, is_open_tuesday, is_open_wednesday, is_open_thursday, is_open_friday, is_open_saturday, is_open_sunday, date_start_validity, date_end_validity, is_active, dispolay_title_fo, nb_weeks_to_display, people_per_appointment, id_workflow FROM appointment_form";
    private static final String SQL_QUERY_SELECTALL_ENABLED = SQL_QUERY_SELECTALL + " WHERE is_active = 1";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE id_form = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_form ( id_form, title, time_start, time_end, duration_appointments, is_open_monday, is_open_tuesday, is_open_wednesday, is_open_thursday, is_open_friday, is_open_saturday, is_open_sunday, date_start_validity, date_end_validity, is_active, dispolay_title_fo, nb_weeks_to_display, people_per_appointment, id_workflow ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_form WHERE id_form = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_form SET title = ?, time_start = ?, time_end = ?, duration_appointments = ?, is_open_monday = ?, is_open_tuesday = ?, is_open_wednesday = ?, is_open_thursday = ?, is_open_friday = ?, is_open_saturday = ?, is_open_sunday = ?, date_start_validity = ?, date_end_validity = ?, is_active = ?, dispolay_title_fo = ?, nb_weeks_to_display = ?, people_per_appointment = ?, id_workflow = ? WHERE id_form = ?";

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
    public void insert( AppointmentForm appointmentForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        appointmentForm.setIdForm( newPrimaryKey( plugin ) );
        int nIndex = 1;
        daoUtil.setInt( nIndex++, appointmentForm.getIdForm( ) );
        daoUtil.setString( nIndex++, appointmentForm.getTitle( ) );
        daoUtil.setString( nIndex++, appointmentForm.getTimeStart( ) );
        daoUtil.setString( nIndex++, appointmentForm.getTimeEnd( ) );
        daoUtil.setInt( nIndex++, appointmentForm.getDurationAppointments( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenMonday( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenTuesday( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenWednesday( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenThursday( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenFriday( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenSaturday( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenSunday( ) );
        daoUtil.setDate( nIndex++, appointmentForm.getDateStartValidity( ) );
        daoUtil.setDate( nIndex++, appointmentForm.getDateEndValidity( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsActive( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getDisplayTitleFo( ) );
        daoUtil.setInt( nIndex++, appointmentForm.getNbWeeksToDisplay( ) );
        daoUtil.setInt( nIndex++, appointmentForm.getPeoplePerAppointment( ) );
        daoUtil.setInt( nIndex, appointmentForm.getIdWorkflow( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AppointmentForm load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );

        AppointmentForm appointmentForm = null;

        if ( daoUtil.next( ) )
        {
            appointmentForm = getAppointmentFormData( daoUtil );
        }

        daoUtil.free( );
        return appointmentForm;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nAppointmentFormId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nAppointmentFormId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( AppointmentForm appointmentForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        int nIndex = 1;

        daoUtil.setString( nIndex++, appointmentForm.getTitle( ) );
        daoUtil.setString( nIndex++, appointmentForm.getTimeStart( ) );
        daoUtil.setString( nIndex++, appointmentForm.getTimeEnd( ) );
        daoUtil.setInt( nIndex++, appointmentForm.getDurationAppointments( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenMonday( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenTuesday( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenWednesday( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenThursday( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenFriday( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenSaturday( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenSunday( ) );
        daoUtil.setDate( nIndex++, appointmentForm.getDateStartValidity( ) );
        daoUtil.setDate( nIndex++, appointmentForm.getDateEndValidity( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsActive( ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getDisplayTitleFo( ) );
        daoUtil.setInt( nIndex++, appointmentForm.getNbWeeksToDisplay( ) );
        daoUtil.setInt( nIndex++, appointmentForm.getPeoplePerAppointment( ) );
        daoUtil.setInt( nIndex++, appointmentForm.getIdWorkflow( ) );
        daoUtil.setInt( nIndex, appointmentForm.getIdForm( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<AppointmentForm> selectAppointmentFormsList( Plugin plugin )
    {
        Collection<AppointmentForm> appointmentFormList = new ArrayList<AppointmentForm>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            appointmentFormList.add( getAppointmentFormData( daoUtil ) );
        }

        daoUtil.free( );
        return appointmentFormList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AppointmentForm> selectActiveAppointmentFormsList( Plugin plugin )
    {
        Collection<AppointmentForm> appointmentFormList = new ArrayList<AppointmentForm>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ENABLED, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            appointmentFormList.add( getAppointmentFormData( daoUtil ) );
        }

        daoUtil.free( );
        return appointmentFormList;
    }

    /**
     * Get data of an appointment form from a daoUtil
     * @param daoUtil The daoUtil to get data from
     * @return The appointment form with data of the current row of the daoUtil
     */
    private AppointmentForm getAppointmentFormData( DAOUtil daoUtil )
    {
        AppointmentForm appointmentForm = new AppointmentForm( );
        int nIndex = 1;
        appointmentForm.setIdForm( daoUtil.getInt( nIndex++ ) );
        appointmentForm.setTitle( daoUtil.getString( nIndex++ ) );
        appointmentForm.setTimeStart( daoUtil.getString( nIndex++ ) );
        appointmentForm.setTimeEnd( daoUtil.getString( nIndex++ ) );
        appointmentForm.setDurationAppointments( daoUtil.getInt( nIndex++ ) );
        appointmentForm.setIsOpenMonday( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setIsOpenTuesday( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setIsOpenWednesday( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setIsOpenThursday( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setIsOpenFriday( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setIsOpenSaturday( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setIsOpenSunday( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setDateStartValidity( daoUtil.getDate( nIndex++ ) );
        appointmentForm.setDateEndValidity( daoUtil.getDate( nIndex++ ) );
        appointmentForm.setIsActive( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setDisplayTitleFo( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setNbWeeksToDisplay( daoUtil.getInt( nIndex++ ) );
        appointmentForm.setPeoplePerAppointment( daoUtil.getInt( nIndex++ ) );
        appointmentForm.setIdWorkflow( daoUtil.getInt( nIndex ) );

        return appointmentForm;
    }
}
