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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Calendar of a form that does not exist
 */
public class AppointmentAppFormListTest extends LuteceTestCase
{
    private static final String PARAMETER_ID_FORM = "id_form";

    @AfterEach
    public void clearLocalVariables( )
    {
        LocalVariables.setLocal( null, null, null );
    }

    /**
     * The calendar of an unknown form shows the list of the forms with the message that the form is no longer valid
     *
     * @throws AccessDeniedException
     *             if the view is refused
     */
    @Test
    public void testCalendarOfAnUnknownFormShowsTheListWithItsMessage( ) throws AccessDeniedException
    {
        assertListWithMessage( "999999" );
    }

    /**
     * Same with an id that is not a number
     *
     * @throws AccessDeniedException
     *             if the view is refused
     */
    @Test
    public void testCalendarOfAnInvalidIdShowsTheListWithItsMessage( ) throws AccessDeniedException
    {
        assertListWithMessage( "abc" );
    }

    private static void assertListWithMessage( String strIdForm ) throws AccessDeniedException
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );
        request.addParameter( PARAMETER_ID_FORM, strIdForm );

        XPage page = CDI.current( ).select( AppointmentApp.class ).get( ).getViewAppointmentCalendar( request );

        String strContent = page.getContent( );
        assertTrue( "Redirected instead of showing the list", strContent != null );
        assertTrue( strContent, strContent.contains( "The form is no longer valid" ) || strContent.contains( "Le formulaire n'est plus valide" ) );
    }
}
