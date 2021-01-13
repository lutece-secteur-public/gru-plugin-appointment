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

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * Appointment Response Home
 * 
 * @author Laurent Payen
 *
 */
public final class AppointmentResponseHome
{

    // Static variable pointed at the DAO instance
    private static IAppointmentResponseDAO _dao = SpringContextService.getBean( "appointment.appointmentResponseDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private AppointmentResponseHome( )
    {
    }

    /**
     * Associate a response to an appointment
     * 
     * @param nIdAppointment
     *            the appointment
     * @param nIdResponse
     *            the response
     */
    public static void insertAppointmentResponse( int nIdAppointment, int nIdResponse )
    {
        _dao.insertAppointmentResponse( nIdAppointment, nIdResponse, _plugin );
    }

    /**
     * Remove every appointment responses associated with a given entry.
     * 
     * @param nIdEntry
     *            The id of the entry
     */
    public static void removeResponsesById( int nIdResponse )
    {
        _dao.removeAppointmentResponseByIdResponse( nIdResponse, _plugin );
        ResponseHome.remove( nIdResponse );
    }

    /**
     * Get the list of responses associated with an appointment
     * 
     * @param nIdAppointment
     *            the id of the appointment
     * @return the list of responses, or an empty list if no response was found
     */
    public static List<Response> findListResponse( int nIdAppointment )
    {
        List<Integer> listIdResponse = _dao.findListIdResponse( nIdAppointment, _plugin );
        List<Response> listResponse = new ArrayList<>( listIdResponse.size( ) );
        for ( Integer nIdResponse : listIdResponse )
        {
            Response response = ResponseHome.findByPrimaryKey( nIdResponse );
            if ( response.getField( ) != null && response.getField( ).getIdField( ) != 0 )
            {
                response.setField( FieldHome.findByPrimaryKey( response.getField( ).getIdField( ) ) );
            }
            listResponse.add( response );
        }
        return listResponse;
    }

    /**
     * Get the list of the response id of an appointment
     * 
     * @param nIdAppointment
     *            the id of the appointment
     * @return the list of the id.
     */
    public static List<Integer> findListIdResponse( int nIdAppointment )
    {
        return _dao.findListIdResponse( nIdAppointment, _plugin );
    }

}
