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
package fr.paris.lutece.plugins.appointment.service.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * Manager for appointment listeners
 */
public final class AppointmentListenerManager
{
    /**
     * Private default constructor
     */
    private AppointmentListenerManager( )
    {
        // Nothing to do
    }

    /**
     * Notify listeners that an appointment is about to be removed
     * 
     * @param nIdAppointment
     *            The id of the appointment that will be removed
     */
    public static void notifyListenersAppointmentRemoval( int nIdAppointment )
    {
        new Thread( ( ) -> {
            for ( IAppointmentListener appointmentRemovalListener : SpringContextService.getBeansOfType( IAppointmentListener.class ) )
            {
                appointmentRemovalListener.notifyAppointmentRemoval( nIdAppointment );
            }
        } ).start( );
    }

    /**
     * Notify listeners that an appointment is about to be created
     * 
     * @param nIdAppointment
     *            The id of the appointment that will be update
     */
    public static void notifyListenersAppointmentCreated( int nIdAppointment )
    {
        new Thread( ( ) -> {

            for ( IAppointmentListener appointmentListener : SpringContextService.getBeansOfType( IAppointmentListener.class ) )
            {
                appointmentListener.notifyAppointmentCreated( nIdAppointment );
            }

        } ).start( );

    }

    /**
     * Notify listeners that an appointment is about to be updated
     * 
     * @param nIdAppointment
     *            The id of the appointment that will be update
     */
    public static void notifyListenersAppointmentUpdated( int nIdAppointment )
    {
        new Thread( ( ) -> {
            for ( IAppointmentListener appointmentListener : SpringContextService.getBeansOfType( IAppointmentListener.class ) )
            {
                appointmentListener.notifyAppointmentUpdated( nIdAppointment );
            }
        } ).start( );
    }

    /**
     * Notify listeners that the date of an appointment has been modified
     * 
     * @param nIdAppointment
     *            The id of the appointment that will be removed
     * @param nIdSlot
     *            The id of the slot
     * @param locale
     *            The locale
     * @return The list of messages to display
     */
    public static List<String> notifyListenersAppointmentDateChanged( int nIdAppointment, List<Integer> listIdSlot, Locale locale )
    {
        List<String> listMessages = new ArrayList<>( );

        for ( IAppointmentListener appointmentRemovalListener : SpringContextService.getBeansOfType( IAppointmentListener.class ) )
        {
            String strMessage = appointmentRemovalListener.appointmentDateChanged( nIdAppointment, listIdSlot, locale );

            if ( StringUtils.isNotEmpty( strMessage ) )
            {
                listMessages.add( strMessage );
            }
        }

        return listMessages;
    }

    /**
     * Notify users that an appointment form has been removed
     * 
     * @param nIdAppointmentForm
     *            the id of the removed appointment form
     */
    public static void notifyListenersAppointmentFormRemoval( int nIdAppointmentForm )
    {
        new Thread( ( ) -> {
            for ( IAppointmentFormRemovalListener appointmentRemovalListener : SpringContextService.getBeansOfType( IAppointmentFormRemovalListener.class ) )
            {
                appointmentRemovalListener.notifyAppointmentFormRemoval( nIdAppointmentForm );
            }
        } ).start( );
    }

    /**
     * Notify users that an Notify the listener that an appointment Workflow action has been Triggred
     * 
     * @param nIdAppointment
     *            the id of the appointment
     * @param nIdAction
     *            The id of the wf action
     */
    public static void notifyAppointmentWFActionTriggered( int nIdAppointment, int nIdAction )
    {
        new Thread( ( ) -> {
            for ( IAppointmentWorkflowActionListener appointmentListener : SpringContextService.getBeansOfType( IAppointmentWorkflowActionListener.class ) )
            {
                appointmentListener.notifyAppointmentWFActionTriggered( nIdAppointment, nIdAction );
            }
        } ).start( );
    }

}
