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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.service.upload.AppointmentAsynchronousUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The file and image questions of the booking form load the upload script of the appointment handler only: the script of another
 * handler on the same page binds a second "delete" listener (two files removed for one click) and lists the files with its own handler
 */
public class AppointmentUploadEntryScriptsTest extends LuteceTestCase
{
    @Test
    public void testUploadEntriesLoadTheScriptOfTheAppointmentHandler( )
    {
        AppointmentAsynchronousUploadHandler handler = CDI.current( ).select( AppointmentAsynchronousUploadHandler.class ).get( );
        Entry entry = new Entry( );
        entry.setIdEntry( 12 );
        entry.setTitle( "File" );
        Map<String, Object> model = new HashMap<>( );
        model.put( "entry", entry );
        model.put( "uploadHandler", handler );

        for ( String strType : List.of( "file", "image" ) )
        {
            String strHtml = AppTemplateService
                    .getTemplate( "skin/plugins/appointment/entries/html_code_entry_type_" + strType + ".html", Locale.FRENCH, model ).getHtml( );

            assertTrue( strHtml, strHtml.contains( "GetMainUploadJs.jsp?handler=" + handler.getHandlerName( ) ) );
            assertFalse( strHtml, strHtml.contains( "GetMainUploadJs.jsp?handler=formsAsynchronousUploadHandler" ) );
        }
    }
}
