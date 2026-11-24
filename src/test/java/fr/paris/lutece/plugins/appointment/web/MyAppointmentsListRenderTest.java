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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.ReferenceList;

/**
 * "My appointments" says that there is no item when every appointment of the user is past or cancelled
 */
public class MyAppointmentsListRenderTest extends LuteceTestCase
{
    private static final String TEMPLATE = "skin/plugins/appointment/my_appointments_list.html";

    private static String render( List<AppointmentDTO> list )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "list_appointments", list );
        model.put( "form_list", new ReferenceList( ) );
        model.put( "localeDateTime", LocalDateTime.now( ) );
        return AppTemplateService.getTemplate( TEMPLATE, Locale.FRENCH, model ).getHtml( );
    }

    private static AppointmentDTO appointment( LocalDateTime start, boolean bCancelled )
    {
        AppointmentDTO appointment = new AppointmentDTO( );
        appointment.setStartingDateTime( start );
        appointment.setIsCancelled( bCancelled );
        return appointment;
    }

    @Test
    public void testOnlyCancelledAndPastAppointments( )
    {
        String strHtml = render( List.of( appointment( LocalDateTime.now( ).plusDays( 3 ), true ), appointment( LocalDateTime.now( ).minusDays( 3 ), false ) ) );

        // the message "no item" is a warning alert
        assertTrue( strHtml, strHtml.contains( "alert-warning" ) );
    }

    @Test
    public void testNoAppointment( )
    {
        assertTrue( render( List.of( ) ).contains( "alert-warning" ) );
    }
}
