/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.appointment.web;

import java.lang.reflect.Field;

import org.jboss.weld.proxy.WeldClientProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.FormServiceTest;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The appointment of a reschedule must not stay in the session for a later new booking
 */
public class AppointmentAppRescheduleSessionTest extends LuteceTestCase
{
    private static final String PARAMETER_ID_FORM = "id_form";

    private int _nIdForm = -1;

    @AfterEach
    public void removeForm( )
    {
        LocalVariables.remove( );
        FormServiceTest.cleanForm( _nIdForm );
    }

    /**
     * A calendar opened without a reference forgets the appointment of a previous reschedule
     *
     * @throws Exception
     *             if the calendar cannot be displayed
     */
    @Test
    public void testCalendarWithoutReferenceForgetsTheRescheduledAppointment( ) throws Exception
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setActiveAuthentication( false );
        _nIdForm = FormService.createAppointmentForm( appointmentForm );

        AppointmentApp instance = CDI.current( ).select( AppointmentApp.class ).get( );
        AppointmentDTO rescheduled = new AppointmentDTO( );
        rescheduled.setIdAppointment( 42 );
        rescheduled.setIdForm( _nIdForm );
        setValidatedAppointment( instance, rescheduled );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );
        request.addParameter( PARAMETER_ID_FORM, String.valueOf( _nIdForm ) );
        try
        {
            instance.getViewAppointmentCalendar( request );
        }
        catch( RuntimeException e )
        {
            // The calendar template of the test form does not exist: the session state is set before the rendering
        }

        assertNull( getField( instance ).get( contextual( instance ) ), "the calendar of a new booking must not keep the appointment to move" );
    }

    private static Object contextual( AppointmentApp instance )
    {
        // The application is a client proxy of the session scope: use its contextual instance
        return ( (WeldClientProxy) instance ).getMetadata( ).getContextualInstance( );
    }

    private static Field getField( AppointmentApp instance ) throws ReflectiveOperationException
    {
        Field field = AppointmentApp.class.getDeclaredField( "_validatedAppointment" );
        field.setAccessible( true );
        return field;
    }

    private static void setValidatedAppointment( AppointmentApp instance, AppointmentDTO appointment ) throws ReflectiveOperationException
    {
        getField( instance ).set( contextual( instance ), appointment );
    }
}
