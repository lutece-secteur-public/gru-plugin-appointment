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
package fr.paris.lutece.plugins.appointment.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.EntryTypeHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * The questions of the back office only are left out of the front office at every level: in a group and under an answer
 */
public class EntryServiceBackOnlyTest extends LuteceTestCase
{
    private static final int ID_RESOURCE = 987670;
    private static final int ID_TYPE_TEXT = 128;
    private static final int ID_TYPE_GROUP = 131;
    private static final int ID_TYPE_MYLUTECE_USER = 135;

    private final List<Integer> _listIdEntry = new ArrayList<>( );

    @AfterEach
    public void removeQuestions( )
    {
        for ( int nIdEntry : _listIdEntry )
        {
            EntryHome.remove( nIdEntry );
        }
        _listIdEntry.clear( );
    }

    @Test
    public void testBackOnlyChildOfAGroupIsNotReadInFrontOffice( )
    {
        int nIdGroup = createEntry( ID_TYPE_GROUP, "bh Bloc", null, false );
        int nIdChild = createEntry( ID_TYPE_TEXT, "bh Note agent", nIdGroup, true );

        assertTrue( readResponses( nIdGroup, nIdChild, true ).isEmpty( ) );

        StringBuilder sbHtml = new StringBuilder( );
        EntryService.getHtmlEntry( new HashMap<>( ), nIdGroup, sbHtml, Locale.FRENCH, true, new AppointmentDTO( ) );
        assertFalse( sbHtml.toString( ), sbHtml.toString( ).contains( "attribute" + nIdChild ) );
    }

    /**
     * The back office does not read the question « Utilisateur MyLutece »: an agent has no front office user
     */
    @Test
    public void testMyLuteceUserQuestionIsNotReadInBackOffice( )
    {
        int nIdEntry = createEntry( ID_TYPE_MYLUTECE_USER, "bh MyLutece", null, false );

        assertTrue( EntryService.getResponseEntry( new MockHttpServletRequest( ), nIdEntry, Locale.FRENCH, new AppointmentDTO( ) ).isEmpty( ) );
    }

    /**
     * Reads the answers of a group whose mandatory child is posted empty (method looked up by reflection: it is new)
     */
    @SuppressWarnings( "unchecked" )
    private static List<GenericAttributeError> readResponses( int nIdGroup, int nIdChild, boolean bFront )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "attribute" + nIdChild, "" );
        try
        {
            return (List<GenericAttributeError>) EntryService.class
                    .getMethod( "getResponseEntry", jakarta.servlet.http.HttpServletRequest.class, int.class, Locale.class, AppointmentDTO.class, boolean.class )
                    .invoke( null, request, nIdGroup, Locale.FRENCH, new AppointmentDTO( ), bFront );
        }
        catch( ReflectiveOperationException e )
        {
            fail( "getResponseEntry( …, boolean bFront ): " + e + " " + e.getCause( ) );
            return null;
        }
    }

    private int createEntry( int nIdType, String strTitle, Integer nIdParent, boolean bOnlyBackMandatory )
    {
        Entry entry = new Entry( );
        entry.setIdResource( ID_RESOURCE );
        entry.setResourceType( AppointmentFormDTO.RESOURCE_TYPE );
        entry.setTitle( strTitle );
        entry.setEntryType( EntryTypeHome.findByPrimaryKey( nIdType ) );
        entry.setOnlyDisplayInBack( bOnlyBackMandatory );
        entry.setMandatory( bOnlyBackMandatory );
        if ( nIdParent != null )
        {
            entry.setParent( EntryHome.findByPrimaryKey( nIdParent ) );
        }
        int nIdEntry = EntryHome.create( entry );
        _listIdEntry.add( nIdEntry );
        Field disabled = new Field( );
        disabled.setParentEntry( EntryHome.findByPrimaryKey( nIdEntry ) );
        disabled.setCode( IEntryTypeService.FIELD_DISABLED );
        disabled.setValue( "false" );
        FieldHome.create( disabled );
        return nIdEntry;
    }
}
