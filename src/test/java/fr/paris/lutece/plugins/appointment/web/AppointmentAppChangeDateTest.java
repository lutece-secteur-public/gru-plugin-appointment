/*
 * Copyright (c) 2002-2026, City of Paris
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
import java.lang.reflect.Field;

import org.jboss.weld.proxy.WeldClientProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Change of the date of a validated appointment from the front office
 */
public class AppointmentAppChangeDateTest extends LuteceTestCase
{
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_STARTING_DATE_TIME = "starting_date_time";

    @AfterEach
    public void clearLocalVariables( )
    {
        LocalVariables.setLocal( null, null, null );
    }

    /**
     * A malformed starting date time goes back to the list of the forms instead of failing
     *
     * @throws ReflectiveOperationException
     *             if the validated appointment cannot be set
     */
    @Test
    public void testMalformedStartingDateTimeGoesBackToTheFormList( ) throws ReflectiveOperationException
    {
        for ( String strStartingDateTime : new String [ ] {
                "null", "2026-10-14T9:00", "2026-13-14T09:00"
        } )
        {
            MockHttpServletRequest request = new MockHttpServletRequest( );
            RedirectResponse response = new RedirectResponse( );
            LocalVariables.setLocal( null, request, response );
            request.addParameter( PARAMETER_ID_FORM, "1" );
            request.addParameter( PARAMETER_STARTING_DATE_TIME, strStartingDateTime );
            AppointmentApp instance = CDI.current( ).select( AppointmentApp.class ).get( );
            setValidatedAppointment( instance, new AppointmentDTO( ) );

            XPage page = instance.getViewChangeDateAppointment( request );

            assertTrue( strStartingDateTime, page.isSendRedirect( ) );
            assertNotNull( response._strLocation );
            assertTrue( response._strLocation, response._strLocation.contains( "view=getViewFormList" ) );
        }
    }

    private static void setValidatedAppointment( AppointmentApp instance, AppointmentDTO appointment ) throws ReflectiveOperationException
    {
        // The application is a client proxy of the session scope: set the field of its contextual instance
        Field field = AppointmentApp.class.getDeclaredField( "_validatedAppointment" );
        field.setAccessible( true );
        field.set( ( (WeldClientProxy) instance ).getMetadata( ).getContextualInstance( ), appointment );
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
