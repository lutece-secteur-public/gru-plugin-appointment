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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * The address of an appointment form: an address typed without picking a suggestion is kept, without coordinates
 */
public class AppointmentFormAddressTest
{
    private static AppointmentFormDTO populate( String strAddress, String strLatitude, String strLongitude )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        if ( strAddress != null )
        {
            request.addParameter( "geoloc_address", strAddress );
        }
        request.addParameter( "geoloc_latitude", strLatitude );
        request.addParameter( "geoloc_longitude", strLongitude );
        AppointmentFormDTO form = new AppointmentFormDTO( );
        form.setAddress( "1 Rue de Rivoli 75001 Paris" );
        form.setLatitude( 48.85 );
        form.setLongitude( 2.35 );
        AppointmentFormJspBean.populateAddress( form, request );
        return form;
    }

    @Test
    public void testPickedAddressKeepsItsCoordinates( )
    {
        AppointmentFormDTO form = populate( "10 Rue de Rivoli 75004 Paris", "48.8553", "2.3561" );
        assertEquals( "10 Rue de Rivoli 75004 Paris", form.getAddress( ) );
        assertEquals( 48.8553, form.getLatitude( ) );
        assertEquals( 2.3561, form.getLongitude( ) );
    }

    @Test
    public void testTypedAddressIsKeptWithoutCoordinates( )
    {
        AppointmentFormDTO form = populate( "12 avenue Foch 75016 Paris", "", "" );
        assertEquals( "12 avenue Foch 75016 Paris", form.getAddress( ) );
        assertNull( form.getLatitude( ) );
        assertNull( form.getLongitude( ) );
    }

    @Test
    public void testErasedAddressIsRemoved( )
    {
        AppointmentFormDTO form = populate( "", "", "" );
        assertNull( form.getAddress( ) );
        assertNull( form.getLatitude( ) );
        assertNull( form.getLongitude( ) );
    }
}
