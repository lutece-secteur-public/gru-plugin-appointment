/*
 * Copyright (c) 2002-2021, City of Paris
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

import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFilterDTO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Appointment objects
 * 
 * @author Laurent Payen
 *
 */
public final class AppointmentHome
{

    // Static variable pointed at the DAO instance
    private static IAppointmentDAO _dao = SpringContextService.getBean( "appointment.appointmentDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private AppointmentHome( )
    {
    }

    /**
     * Create an instance of the Appointment class
     * 
     * @param appointment
     *            The instance of the Appointment which contains the informations to store
     * @return The instance of the Appointment which has been created with its primary key.
     */
    public static Appointment create( Appointment appointment )
    {
        _dao.insert( appointment, _plugin );

        return appointment;
    }

    /**
     * Update of the Appointment which is specified in parameter
     * 
     * @param appointment
     *            The instance of the Appointment which contains the data to store
     * @return The instance of the Appointment which has been updated
     */
    public static Appointment update( Appointment appointment )
    {
        _dao.update( appointment, _plugin );

        return appointment;
    }

    /**
     * Delete the Appointment whose identifier is specified in parameter
     * 
     * @param nKey
     *            The appointment Id
     */
    public static void delete( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Return an instance of the Appointment whose identifier is specified in parameter
     * 
     * @param nKey
     *            The Appointment primary key
     * @return an instance of the Appointment
     */
    public static Appointment findByPrimaryKey( int nKey )
    {
        return _dao.select( nKey, _plugin );
    }

    /**
     * Return an instance of the Appointment whose reference is specified in parameter
     * 
     * @param strReference
     *            The Appointment reference
     * @return an instance of the Appointment
     */
    public static Appointment findByReference( String strReference )
    {
        return _dao.findByReference( strReference, _plugin );
    }

    /**
     * Return the appointments of a user
     * 
     * @param nIdUser
     *            the User Id
     * @return a list of the user appointments
     */
    public static List<Appointment> findByIdUser( int nIdUser )
    {
        return _dao.findByIdUser( nIdUser, _plugin );
    }

    /**
     * Return the appointments of a user by Guid
     * 
     * @param nIdUser
     *            the User Guid
     * @return a list of the user appointments
     */
    public static List<Appointment> findByGuidUser( String strGuidUser )
    {
        return _dao.findByGuidUser( strGuidUser, _plugin );
    }

    /**
     * Return the appointments of a slot
     * 
     * @param nIdSlot
     * @return a list of the appointments of the slot
     */
    public static List<Appointment> findByIdSlot( int nIdSlot )
    {
        return _dao.findByIdSlot( nIdSlot, _plugin );
    }

    /**
     * Returns the list of appointments of a slot
     * 
     * @param listIdSlot
     *            the list Slot Id
     * @param plugin
     *            the plugin
     * @return a list of the appointments
     */
    public static List<Appointment> findByListIdSlot( List<Integer> listIdSlot )
    {

        return _dao.findByListIdSlot( listIdSlot, _plugin );

    }

    /**
     * Return a list of appointment of a form
     * 
     * @param nIdForm
     *            the form id
     * @return the list of the appointments
     */
    public static List<Appointment> findByIdForm( int nIdForm )
    {
        return _dao.findByIdForm( nIdForm, _plugin );
    }

    /**
     * Returns a list of appointment matching the filter
     * 
     * @param appointmentFilter
     *            the filter
     * @return a list of appointments
     */
    public static List<Appointment> findByFilter( AppointmentFilterDTO appointmentFilter )
    {
        return _dao.findByFilter( appointmentFilter, _plugin );
    }
}
