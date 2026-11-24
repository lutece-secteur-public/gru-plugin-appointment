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
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Renders the back office templates of the questions of an appointment form
 */
public class AppointmentEntryTemplatesRenderTest extends LuteceTestCase
{
    private static final String COMMENT = "interne : verifier le format";

    /**
     * BO entry of an appointment: the confirmation input of a short text question is empty, it does not hold the comment of the question
     */
    @Test
    public void testConfirmFieldIsEmpty( )
    {
        Entry entry = entry( "text" );
        entry.setComment( COMMENT );
        entry.setFields( fields( entry, new String [ ] [ ] {
                { "text_config", "" }, { "confirm_field", "true" }, { "width", "50" }, { "max_size", "100" }
        } ) );
        Map<String, Object> model = new HashMap<>( );
        model.put( "entry", entry );

        String strHtml = render( "admin/plugins/appointment/entries/html_code_entry_type_text.html", model );

        assertTrue( strHtml, strHtml.contains( "attribute12_confirm_field" ) );
        assertFalse( strHtml, strHtml.contains( "value=\"" + COMMENT ) );
    }

    /**
     * Modification of a "Session" question: the switch "mandatory" shows the flag of the question
     */
    @Test
    public void testSessionMandatoryChecked( )
    {
        Entry entry = entry( "Session" );
        entry.setMandatory( true );
        entry.setFields( fields( entry, new String [ ] [ ] {
                { "attribute_name", "bh_attr" }, { "anonymizable", "false" }
        } ) );

        String strHtml = render( "admin/plugins/appointment/entries/modify_entry_type_session.html", modifyModel( entry ) );

        assertTrue( strHtml, strHtml.matches( "(?s).*<input[^>]*name=\"mandatory\"[^>]*checked.*" ) );
    }

    private static Map<String, Object> modifyModel( Entry entry )
    {
        AppointmentFormDTO form = new AppointmentFormDTO( );
        form.setIdForm( 3 );
        form.setTitle( "bh form" );
        entry.setHelpMessage( "" );
        entry.setComment( "" );
        Map<String, Object> model = new HashMap<>( );
        model.put( "entry", entry );
        model.put( "form", form );
        model.put( "anonymization_help_message", "" );
        return model;
    }

    static Entry entry( String strType )
    {
        EntryType entryType = new EntryType( );
        entryType.setIdType( 1 );
        entryType.setTitle( strType );
        Entry entry = new Entry( );
        entry.setIdEntry( 12 );
        entry.setTitle( "bh question" );
        entry.setEntryType( entryType );
        return entry;
    }

    static List<Field> fields( Entry entry, String [ ] [ ] codeValues )
    {
        List<Field> listFields = new ArrayList<>( );
        int nId = 100;
        for ( String [ ] codeValue : codeValues )
        {
            Field field = new Field( );
            field.setIdField( nId++ );
            field.setCode( codeValue [0] );
            field.setValue( codeValue [1] );
            field.setTitle( codeValue [0] );
            field.setParentEntry( entry );
            listFields.add( field );
        }
        return listFields;
    }

    static String render( String strTemplate, Map<String, Object> model )
    {
        return AppTemplateService.getTemplate( strTemplate, Locale.FRENCH, model ).getHtml( );
    }
}
