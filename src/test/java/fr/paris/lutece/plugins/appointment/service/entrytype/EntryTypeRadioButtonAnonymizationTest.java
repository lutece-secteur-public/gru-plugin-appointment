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
package fr.paris.lutece.plugins.appointment.service.entrytype;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * A radio button question has no anonymisation type: it cannot be marked anonymisable (the setting had no effect)
 */
public class EntryTypeRadioButtonAnonymizationTest extends LuteceTestCase
{
    private static final String TEMPLATES = "webapp/WEB-INF/templates/admin/plugins/appointment/entries/";

    @Test
    public void testAnonymizableIsIgnored( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( IEntryTypeService.PARAMETER_TITLE, "radio" );
        request.addParameter( IEntryTypeService.PARAMETER_ANONYMIZABLE, "1" );
        request.addParameter( IEntryTypeService.PARAMETER_ANONYMIZE_PATTERN, "%f" );
        Entry entry = new Entry( );
        entry.setFields( new ArrayList<>( ) );

        assertNull( new EntryTypeRadioButton( ).getRequestData( entry, request, Locale.FRENCH ) );

        assertEquals( "false", entry.getFieldByCode( IEntryTypeService.FIELD_ANONYMIZABLE ).getValue( ) );
    }

    @Test
    public void testScreensDoNotOfferTheAnonymisation( ) throws IOException
    {
        for ( String strTemplate : new String [ ] { "create_entry_type_radio_button.html", "modify_entry_type_radio_button.html" } )
        {
            String strSource = Files.readString( Path.of( TEMPLATES + strTemplate ) );
            assertFalse( strSource.contains( "name='anonymizable'" ), strTemplate );
            assertFalse( strSource.contains( "anonymize_pattern" ), strTemplate );
        }
    }
}
