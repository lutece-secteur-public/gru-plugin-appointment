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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Cancellation of an appointment from the front office
 */
public class AppointmentAppCancelTest extends LuteceTestCase
{
    private static final String ACTION_DO_CANCEL_APPOINTMENT = "doCancelAppointment";
    private static final String PARAMETER_REF_APPOINTMENT = "refAppointment";
    private static final String UNKNOWN_REFERENCE = "AppointmentAppCancelTestUnknown";

    @AfterEach
    public void clearLocalVariables( )
    {
        LocalVariables.setLocal( null, null, null );
    }

    /**
     * A valid token with a reference that matches no appointment: back to the cancel page, which says that no appointment has this reference
     *
     * @throws AccessDeniedException
     *             if the token is refused
     */
    @Test
    public void testCancelUnknownReference( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        RedirectResponse response = new RedirectResponse( );
        LocalVariables.setLocal( null, request, response );
        request.addParameter( PARAMETER_REF_APPOINTMENT, UNKNOWN_REFERENCE );
        request.addParameter( SecurityTokenService.PARAMETER_TOKEN,
                CDI.current( ).select( ISecurityTokenService.class ).get( ).getToken( request, ACTION_DO_CANCEL_APPOINTMENT ) );

        XPage page = CDI.current( ).select( AppointmentApp.class ).get( ).doCancelAppointment( request );

        assertTrue( page.isSendRedirect( ) );
        assertNotNull( response._strLocation );
        assertTrue( response._strLocation, response._strLocation.contains( "view=getViewCancelAppointment" ) );
        assertTrue( response._strLocation, response._strLocation.contains( PARAMETER_REF_APPOINTMENT + "=" + UNKNOWN_REFERENCE ) );
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
