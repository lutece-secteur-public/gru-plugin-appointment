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
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * FO calendar: the description of the calendar, rich text typed in the back office, is shown formatted
 */
public class AppointmentCalendarDescriptionRenderTest extends LuteceTestCase
{
    @Test
    public void testDescriptionIsRichText( )
    {
        AppointmentFormDTO form = new AppointmentFormDTO( );
        form.setIdForm( 3 );
        form.setTitle( "bh form" );
        form.setDisplayTitleFo( true );
        Map<String, Object> model = new HashMap<>( );
        model.put( "form", form );
        model.put( "formMessages", new FormMessage( ) );
        model.put( "infos", List.of( new MVCMessage( "<p>Apportez une <strong>pièce</strong></p>" ) ) );
        model.put( "formCalendarErrors", Boolean.TRUE );
        // values read by the script of the calendar
        model.put( "date_of_display", java.time.LocalDate.of( 2026, 12, 1 ) );
        model.put( "min_date_of_open_day", java.time.LocalDate.of( 2026, 12, 1 ) );
        model.put( "max_date_of_open_day", java.time.LocalDate.of( 2026, 12, 7 ) );
        model.put( "modifDateAppointment", Boolean.FALSE );
        model.put( "events", List.of( ) );
        model.put( "hidden_days", List.of( ) );

        String strHtml = AppTemplateService.getTemplate( "skin/plugins/appointment/calendar/appointment_form_list_open_slots.html", Locale.FRENCH, model )
                .getHtml( );

        assertTrue( strHtml, strHtml.contains( "<strong>pièce</strong>" ) );
        assertFalse( strHtml, strHtml.contains( "&lt;strong&gt;" ) );
    }
}
