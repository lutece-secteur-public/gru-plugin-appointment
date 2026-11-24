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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * A "Géolocalisation" question displayed again after a refused validation keeps the address typed
 */
public class AppointmentGeolocationRedisplayRenderTest extends LuteceTestCase
{
    private static final String ADDRESS = "12 rue de Rivoli";

    @Test
    public void testAddressKeptWhenTheGeometryComesAfter( )
    {
        Entry entry = AppointmentEntryTemplatesRenderTest.entry( "Géolocalisation" );
        entry.setHelpMessage( "" );
        List<Field> listFields = AppointmentEntryTemplatesRenderTest.fields( entry, new String [ ] [ ] {
                { "idAddress", "" }, { "address", "" }, { "X", "" }, { "Y", "" }, { "geometry", "geometry" }
        } );
        entry.setFields( listFields );
        // order of the responses built by the entry type: idAddress, address, X, Y, geometry
        String [ ] values = {
                "", ADDRESS, "", "", ""
        };
        List<Response> listResponses = new ArrayList<>( );
        for ( int i = 0; i < listFields.size( ); i++ )
        {
            Response response = new Response( );
            response.setEntry( entry );
            response.setField( listFields.get( i ) );
            response.setToStringValueResponse( values [i] );
            listResponses.add( response );
        }
        Map<String, Object> model = new HashMap<>( );
        model.put( "entry", entry );
        model.put( "list_responses", listResponses );

        String strHtml = AppointmentEntryTemplatesRenderTest.render( "skin/plugins/appointment/entries/html_code_entry_type_geolocation.html", model );

        assertTrue( strHtml, strHtml.matches( "(?s).*<input[^>]*name=\"12_address\"[^>]*value=\"" + ADDRESS + "\".*" )
                || strHtml.matches( "(?s).*<input[^>]*value=\"" + ADDRESS + "\"[^>]*name=\"12_address\".*" ) );
    }
}
