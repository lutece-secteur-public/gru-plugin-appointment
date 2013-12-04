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
	private static final String SQL_QUERY_SELECT = "SELECT id_form, title, time_start, time_end, duration_appointments, is_open_monday, is_open_tuesday, is_open_wednesday, is_open_thursday, is_open_friday, is_open_saturday, is_open_sunday, date_start_validity, date_end_validity, is_active, dispolay_title_fo, nb_weeks_to_display, people_per_appointment FROM appointment_form WHERE id_form = ?";
	private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_form ( id_form, title, time_start, time_end, duration_appointments, is_open_monday, is_open_tuesday, is_open_wednesday, is_open_thursday, is_open_friday, is_open_saturday, is_open_sunday, date_start_validity, date_end_validity, is_active, dispolay_title_fo, nb_weeks_to_display, people_per_appointment ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
	private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_form WHERE id_form = ? ";
	private static final String SQL_QUERY_UPDATE = "UPDATE appointment_form SET id_form = ?, title = ?, time_start = ?, time_end = ?, duration_appointments = ?, is_open_monday = ?, is_open_tuesday = ?, is_open_wednesday = ?, is_open_thursday = ?, is_open_friday = ?, is_open_saturday = ?, is_open_sunday = ?, date_start_validity = ?, date_end_validity = ?, is_active = ?, dispolay_title_fo = ?, nb_weeks_to_display = ?, people_per_appointment = ? WHERE id_form = ?";
	private static final String SQL_QUERY_SELECTALL = "SELECT id_form, title, time_start, time_end, duration_appointments, is_open_monday, is_open_tuesday, is_open_wednesday, is_open_thursday, is_open_friday, is_open_saturday, is_open_sunday, date_start_validity, date_end_validity, is_active, dispolay_title_fo, nb_weeks_to_display, people_per_appointment FROM appointment_form";


	
	/**
	 * Generates a new primary key
	 * @param plugin The Plugin
	 * @return The new primary key
	 */
	public int newPrimaryKey( Plugin plugin)
	{
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK , plugin  );
		daoUtil.executeQuery( );

		int nKey = 1;

		if( daoUtil.next( ) )
		{
			nKey = daoUtil.getInt( 1 ) + 1;
		}

		daoUtil.free();

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
				
		daoUtil.setInt( 1, appointmentForm.getIdForm( ) );
		daoUtil.setString( 2, appointmentForm.getTitle( ) );
		daoUtil.setInt( 3, appointmentForm.getTimeStart( ) );
		daoUtil.setInt( 4, appointmentForm.getTimeEnd( ) );
		daoUtil.setInt( 5, appointmentForm.getDurationAppointments( ) );
		daoUtil.setBoolean( 6, appointmentForm.getIsOpenMonday( ) );
		daoUtil.setBoolean( 7, appointmentForm.getIsOpenTuesday( ) );
		daoUtil.setBoolean( 8, appointmentForm.getIsOpenWednesday( ) );
		daoUtil.setBoolean( 9, appointmentForm.getIsOpenThursday( ) );
		daoUtil.setBoolean( 10, appointmentForm.getIsOpenFriday( ) );
		daoUtil.setBoolean( 11, appointmentForm.getIsOpenSaturday( ) );
		daoUtil.setBoolean( 12, appointmentForm.getIsOpenSunday( ) );
		daoUtil.setDate( 13, appointmentForm.getDateStartValidity( ) );
		daoUtil.setDate( 14, appointmentForm.getDateEndValidity( ) );
		daoUtil.setBoolean( 15, appointmentForm.getIsActive( ) );
		daoUtil.setBoolean( 16, appointmentForm.getDisplayTitleFo( ) );
		daoUtil.setInt( 17, appointmentForm.getNbWeeksToDisplay( ) );
		daoUtil.setInt( 18, appointmentForm.getPeoplePerAppointment( ) );

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
		daoUtil.setInt( 1 , nKey );
		daoUtil.executeQuery( );

		AppointmentForm appointmentForm = null;

		if ( daoUtil.next( ) )
		{
			appointmentForm = new AppointmentForm();
			appointmentForm.setIdForm( daoUtil.getInt(  1 ) );
			appointmentForm.setTitle( daoUtil.getString(  2 ) );
			appointmentForm.setTimeStart( daoUtil.getInt(  3 ) );
			appointmentForm.setTimeEnd( daoUtil.getInt(  4 ) );
			appointmentForm.setDurationAppointments( daoUtil.getInt(  5 ) );
			appointmentForm.setIsOpenMonday( daoUtil.getBoolean(  6 ) );
			appointmentForm.setIsOpenTuesday( daoUtil.getBoolean(  7 ) );
			appointmentForm.setIsOpenWednesday( daoUtil.getBoolean(  8 ) );
			appointmentForm.setIsOpenThursday( daoUtil.getBoolean(  9 ) );
			appointmentForm.setIsOpenFriday( daoUtil.getBoolean(  10 ) );
			appointmentForm.setIsOpenSaturday( daoUtil.getBoolean(  11 ) );
			appointmentForm.setIsOpenSunday( daoUtil.getBoolean(  12 ) );
			appointmentForm.setDateStartValidity( daoUtil.getDate(  13 ) );
			appointmentForm.setDateEndValidity( daoUtil.getDate(  14 ) );
			appointmentForm.setIsActive( daoUtil.getBoolean(  15 ) );
			appointmentForm.setDisplayTitleFo( daoUtil.getBoolean(  16 ) );
			appointmentForm.setNbWeeksToDisplay( daoUtil.getInt(  17 ) );
			appointmentForm.setPeoplePerAppointment( daoUtil.getInt(  18 ) );
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
		daoUtil.setInt( 1 , nAppointmentFormId );
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
				
		daoUtil.setInt( 1, appointmentForm.getIdForm( ) );
		daoUtil.setString( 2, appointmentForm.getTitle( ) );
		daoUtil.setInt( 3, appointmentForm.getTimeStart( ) );
		daoUtil.setInt( 4, appointmentForm.getTimeEnd( ) );
		daoUtil.setInt( 5, appointmentForm.getDurationAppointments( ) );
		daoUtil.setBoolean( 6, appointmentForm.getIsOpenMonday( ) );
		daoUtil.setBoolean( 7, appointmentForm.getIsOpenTuesday( ) );
		daoUtil.setBoolean( 8, appointmentForm.getIsOpenWednesday( ) );
		daoUtil.setBoolean( 9, appointmentForm.getIsOpenThursday( ) );
		daoUtil.setBoolean( 10, appointmentForm.getIsOpenFriday( ) );
		daoUtil.setBoolean( 11, appointmentForm.getIsOpenSaturday( ) );
		daoUtil.setBoolean( 12, appointmentForm.getIsOpenSunday( ) );
		daoUtil.setDate( 13, appointmentForm.getDateStartValidity( ) );
		daoUtil.setDate( 14, appointmentForm.getDateEndValidity( ) );
		daoUtil.setBoolean( 15, appointmentForm.getIsActive( ) );
		daoUtil.setBoolean( 16, appointmentForm.getDisplayTitleFo( ) );
		daoUtil.setInt( 17, appointmentForm.getNbWeeksToDisplay( ) );
		daoUtil.setInt( 18, appointmentForm.getPeoplePerAppointment( ) );
		daoUtil.setInt( 19, appointmentForm.getIdForm( ) );
				
		daoUtil.executeUpdate( );
		daoUtil.free( );
	}



	/**
	 * {@inheritDoc }
	 */
	@Override
	public Collection<AppointmentForm> selectAppointmentFormsList( Plugin plugin )
	{
		Collection<AppointmentForm> appointmentFormList = new ArrayList<AppointmentForm>(  );
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
		daoUtil.executeQuery(  );

		while ( daoUtil.next(  ) )
		{
				AppointmentForm appointmentForm = new AppointmentForm(  );

					appointmentForm.setIdForm( daoUtil.getInt( 1 ) );
					appointmentForm.setTitle( daoUtil.getString( 2 ) );
					appointmentForm.setTimeStart( daoUtil.getInt( 3 ) );
					appointmentForm.setTimeEnd( daoUtil.getInt( 4 ) );
					appointmentForm.setDurationAppointments( daoUtil.getInt( 5 ) );
					appointmentForm.setIsOpenMonday( daoUtil.getBoolean( 6 ) );
					appointmentForm.setIsOpenTuesday( daoUtil.getBoolean( 7 ) );
					appointmentForm.setIsOpenWednesday( daoUtil.getBoolean( 8 ) );
					appointmentForm.setIsOpenThursday( daoUtil.getBoolean( 9 ) );
					appointmentForm.setIsOpenFriday( daoUtil.getBoolean( 10 ) );
					appointmentForm.setIsOpenSaturday( daoUtil.getBoolean( 11 ) );
					appointmentForm.setIsOpenSunday( daoUtil.getBoolean( 12 ) );
					appointmentForm.setDateStartValidity( daoUtil.getDate( 13 ) );
					appointmentForm.setDateEndValidity( daoUtil.getDate( 14 ) );
					appointmentForm.setIsActive( daoUtil.getBoolean( 15 ) );
					appointmentForm.setDisplayTitleFo( daoUtil.getBoolean( 16 ) );
					appointmentForm.setNbWeeksToDisplay( daoUtil.getInt( 17 ) );
					appointmentForm.setPeoplePerAppointment( daoUtil.getInt( 18 ) );

				appointmentFormList.add( appointmentForm );
		}

		daoUtil.free( );
		return appointmentFormList;
	}

}
