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
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import java.util.Collection;

/**
 * This class provides instances management methods (create, find, ...) for AppointmentForm objects
 */

public final class AppointmentFormHome
{

	// Static variable pointed at the DAO instance

	private static IAppointmentFormDAO _dao = SpringContextService.getBean( "appointment.appointmentFormDAO" );
	private static Plugin _plugin = PluginService.getPlugin( "appointment" );

	/**
	 * Private constructor - this class need not be instantiated
	 */
	private AppointmentFormHome(  )
	{
	}

	/**
	 * Create an instance of the appointmentForm class
	 * @param appointmentForm The instance of the AppointmentForm which contains the informations to store
	 * @return The  instance of appointmentForm which has been created with its primary key.
	 */
	public static AppointmentForm create( AppointmentForm appointmentForm )
	{
		_dao.insert( appointmentForm, _plugin );

		return appointmentForm;
	}


	/**
	 * Update of the appointmentForm which is specified in parameter
	 * @param appointmentForm The instance of the AppointmentForm which contains the data to store
	 * @return The instance of the  appointmentForm which has been updated
	 */
	public static AppointmentForm update( AppointmentForm appointmentForm )
	{
		_dao.store( appointmentForm, _plugin );

		return appointmentForm;
	}


	/**
	 * Remove the appointmentForm whose identifier is specified in parameter
	 * @param nAppointmentFormId The appointmentForm Id
	 */
	public static void remove( int nAppointmentFormId )
	{
		_dao.delete( nAppointmentFormId, _plugin );
	}


	///////////////////////////////////////////////////////////////////////////
	// Finders

	/**
	 * Returns an instance of a appointmentForm whose identifier is specified in parameter
	 * @param nKey The appointmentForm primary key
	 * @return an instance of AppointmentForm
	 */
	public static AppointmentForm findByPrimaryKey( int nKey )
	{
		return _dao.load( nKey, _plugin);
	}


	/**
	 * Load the data of all the appointmentForm objects and returns them in form of a collection
	 * @return the collection which contains the data of all the appointmentForm objects
	 */
	public static Collection<AppointmentForm> getAppointmentFormsList( )
	{
		return _dao.selectAppointmentFormsList( _plugin );
	}
}

