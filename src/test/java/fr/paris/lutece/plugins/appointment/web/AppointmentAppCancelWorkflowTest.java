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

import java.io.IOException;
import java.util.Locale;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.AppointmentTest;
import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.FormServiceTest;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Cancellation from the front office of an appointment whose cancellation is a workflow action
 */
public class AppointmentAppCancelWorkflowTest extends LuteceTestCase
{
    private static final String ACTION_DO_CANCEL_APPOINTMENT = "doCancelAppointment";
    private static final int ID_ACTION_UNKNOWN = 987654;

    private int _nIdForm;
    private AppointmentDTO _appointmentDTO;

    @AfterEach
    public void cleanUp( )
    {
        LocalVariables.setLocal( null, null, null );
        if ( _appointmentDTO != null )
        {
            AppointmentHome.delete( _appointmentDTO.getIdAppointment( ) );
            User user = UserHome.findByFirstNameLastNameAndEmail( _appointmentDTO.getFirstName( ), _appointmentDTO.getLastName( ), _appointmentDTO.getEmail( ) );
            if ( user != null )
            {
                UserHome.delete( user.getIdUser( ) );
            }
        }
        FormServiceTest.cleanForm( _nIdForm );
    }

    /**
     * The workflow cannot process the cancel action of the appointment: the user is told so, the appointment is not reported as cancelled
     */
    @Test
    public void testCancelActionThatCannotBeProcessed( ) throws Exception
    {
        _nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        LocalDate day = LocalDate.now( ).plusDays( 10 );
        Slot slot = SlotService.saveSlot( SlotTest.buildSlot( _nIdForm, day.atTime( 10, 0 ), day.atTime( 10, 30 ), 3, 3, 0, 3, Boolean.TRUE, Boolean.TRUE ) );
        _appointmentDTO = AppointmentTest.buildAppointmentDTO( _nIdForm, slot, "bh.cancel@mdp.fr", "Jean", "Dupont", LocalTime.of( 10, 0 ), LocalTime.of( 10, 30 ), 1 );
        AppointmentService.saveAppointment( _appointmentDTO );
        Appointment appointment = AppointmentHome.findByPrimaryKey( _appointmentDTO.getIdAppointment( ) );
        appointment.setIdActionCancelled( ID_ACTION_UNKNOWN );
        AppointmentHome.update( appointment );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        RedirectResponse response = new RedirectResponse( );
        LocalVariables.setLocal( null, request, response );
        request.addParameter( "refAppointment", appointment.getReference( ) );
        request.addParameter( SecurityTokenService.PARAMETER_TOKEN,
                CDI.current( ).select( ISecurityTokenService.class ).get( ).getToken( request, ACTION_DO_CANCEL_APPOINTMENT ) );

        CDI.current( ).select( AppointmentApp.class ).get( ).doCancelAppointment( request );

        assertNotNull( response._strLocation );
        assertTrue( response._strLocation.contains( "view=getViewCancelAppointment" ), response._strLocation );
        assertFalse( AppointmentHome.findByPrimaryKey( appointment.getIdAppointment( ) ).getIsCancelled( ) );

        // the cancel page the visitor is sent back to shows why the cancellation was refused
        XPage page = CDI.current( ).select( AppointmentApp.class ).get( ).getViewCancelAppointment( request );
        assertTrue( page.getContent( ).contains( I18nService.getLocalizedString( "appointment.message.error.cancelNotPossible", Locale.FRENCH ) )
                || page.getContent( ).contains( I18nService.getLocalizedString( "appointment.message.error.cancelNotPossible", Locale.ENGLISH ) ),
                "message of the refused cancellation shown" );
        // the common model (errors) does not overwrite the values of the view, such as the user of the appointment
        assertTrue( page.getContent( ).contains( "Dupont" ), "user of the appointment shown" );
    }

    /**
     * Response that keeps the location of the redirect
     */
    private static class RedirectResponse extends MockHttpServletResponse
    {
        private String _strLocation;

        @Override
        public void sendRedirect( String strLocation ) throws IOException
        {
            _strLocation = strLocation;
        }
    }
}
