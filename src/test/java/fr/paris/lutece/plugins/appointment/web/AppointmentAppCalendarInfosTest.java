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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jboss.weld.proxy.WeldClientProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.message.FormMessage;
import fr.paris.lutece.plugins.appointment.service.FormMessageService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.FormServiceTest;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.util.ErrorMessage;
import fr.paris.lutece.util.html.exception.LuteceFreemarkerException;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Front office calendar of a form with a calendar description, when the model already holds infos added by the core
 * (lutece-core LUT-30580: the front office addInfo stores plain strings in a Set, also restored there after a redirect):
 * the description is added to those infos, no ClassCastException.
 */
public class AppointmentAppCalendarInfosTest extends LuteceTestCase
{
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String MARK_INFOS = "infos";
    private static final String DESCRIPTION = "bh calendar description";
    private static final String CORE_INFO = "bh info added by the core";

    private int _nIdForm = -1;
    private Object _infos;

    @AfterEach
    public void removeForm( )
    {
        LocalVariables.remove( );
        FormServiceTest.cleanForm( _nIdForm );
    }

    /**
     * Infos already in the model as a Set of strings: the calendar is displayed and keeps both messages.
     *
     * @throws Exception
     *             if the calendar cannot be displayed
     */
    @Test
    public void testCalendarDescriptionAddedToTheInfosOfTheCore( ) throws Exception
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setActiveAuthentication( false );
        _nIdForm = FormService.createAppointmentForm( appointmentForm );
        FormMessage formMessage = FormMessageService.findFormMessageByIdForm( _nIdForm );
        formMessage.setCalendarDescription( DESCRIPTION );
        FormMessageService.updateFormMessage( formMessage );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );
        request.addParameter( PARAMETER_ID_FORM, String.valueOf( _nIdForm ) );
        AppointmentApp instance = CDI.current( ).select( AppointmentApp.class ).get( );
        Models models = getModels( instance );
        Set<String> coreInfos = new LinkedHashSet<>( );
        coreInfos.add( CORE_INFO );
        models.put( MARK_INFOS, coreInfos );

        try
        {
            instance.getViewAppointmentCalendar( request );
        }
        catch( LuteceFreemarkerException e )
        {
            // The test form has no calendar template file: the infos are computed before the page is rendered
        }
        finally
        {
            _infos = models.get( MARK_INFOS );
            models.put( MARK_INFOS, new LinkedHashSet<String>( ) );
        }

        Object infos = _infos;
        assertTrue( "infos must stay a Set, the type the core adds its messages to: " + infos, infos instanceof Set );
        StringBuilder sbInfos = new StringBuilder( );
        for ( Object info : (Collection<?>) infos )
        {
            sbInfos.append( info instanceof ErrorMessage ? ( (ErrorMessage) info ).getMessage( ) : String.valueOf( info ) ).append( '|' );
        }
        String strInfos = sbInfos.toString( );
        assertTrue( "the info of the core is kept: " + strInfos, strInfos.contains( CORE_INFO ) );
        assertTrue( "the calendar description is added: " + strInfos, strInfos.contains( DESCRIPTION ) );
    }

    private static Models getModels( AppointmentApp instance ) throws ReflectiveOperationException
    {
        // The application is a client proxy of the session scope: read the field of its contextual instance
        Field field = AppointmentApp.class.getDeclaredField( "_models" );
        field.setAccessible( true );
        return (Models) field.get( ( (WeldClientProxy) instance ).getMetadata( ).getContextualInstance( ) );
    }
}
