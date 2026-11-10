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

import fr.paris.lutece.plugins.appointment.business.message.FormMessage;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.service.FormServiceTest;
import fr.paris.lutece.plugins.appointment.web.dto.ResponseRecapDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * The front office summaries of an appointment show every answer of a multiple choice question
 */
public class AppointmentRecapTemplateTest extends LuteceTestCase
{
    private static final String TEMPLATE_CREATED = "skin/plugins/appointment/appointment_created.html";

    @Test
    public void testEveryCheckedAnswerIsShown( )
    {
        Entry colors = entry( 1, "Favourite colours" );
        Entry comment = entry( 2, "Comment" );
        Map<String, Object> model = new HashMap<>( );
        model.put( "form", FormServiceTest.buildAppointmentForm( ) );
        FormMessage formMessages = new FormMessage( );
        formMessages.setTextAppointmentCreated( "Appointment created" );
        model.put( "formMessages", formMessages );
        model.put( "dateAppointment", "13/11/2026" );
        model.put( "startingTimeAppointment", "10:00" );
        model.put( "endingTimeAppointment", "10:30" );
        model.put( "user", user( ) );
        model.put( "nbplaces", 1 );
        model.put( "listResponseRecapDTO", List.of( recap( colors, "Red" ), recap( colors, "Blue" ), recap( comment, "None" ) ) );

        String strHtml = AppTemplateService.getTemplate( TEMPLATE_CREATED, Locale.FRENCH, model ).getHtml( );

        assertTrue( strHtml, strHtml.contains( "Favourite colours : Red, Blue" ) );
        assertTrue( strHtml, strHtml.contains( "Comment : None" ) );
    }

    private static Entry entry( int nIdEntry, String strTitle )
    {
        Entry entry = new Entry( );
        entry.setIdEntry( nIdEntry );
        entry.setTitle( strTitle );
        return entry;
    }

    private static ResponseRecapDTO recap( Entry entry, String strValue )
    {
        Response response = new Response( );
        response.setEntry( entry );
        return new ResponseRecapDTO( response, strValue );
    }

    private static User user( )
    {
        User user = new User( );
        user.setFirstName( "Jean" );
        user.setLastName( "Dupont" );
        user.setEmail( "jean.dupont@test.fr" );
        return user;
    }
}
