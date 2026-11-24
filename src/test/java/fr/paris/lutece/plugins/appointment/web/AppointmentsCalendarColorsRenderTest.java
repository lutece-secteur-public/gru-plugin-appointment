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

import fr.paris.lutece.plugins.appointment.service.FormServiceTest;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.ReferenceList;

/**
 * Colours of the slots on the calendars of the appointments: CSS colour values, without the semicolon of a declaration
 */
public class AppointmentsCalendarColorsRenderTest extends LuteceTestCase
{
    private static String render( String strTemplate )
    {
        AppointmentFormDTO form = FormServiceTest.buildAppointmentForm( );
        form.setIdForm( 3 );
        Map<String, Object> model = new HashMap<>( );
        model.put( "form", form );
        model.put( "appointmentform", form );
        model.put( "id_form", 3 );
        model.put( "mailing_list", new ReferenceList( ) );
        model.put( "locale", Locale.FRENCH );
        model.put( "language", "fr" );
        model.put( "min_duration", "00:30" );
        model.put( "min_time", "09:00" );
        model.put( "max_time", "18:00" );
        model.put( "date_of_display", "2026-12-01" );
        model.put( "starting_date_of_display", "2026-12-01" );
        model.put( "ending_date_of_display", "2026-12-21" );
        model.put( "str_starting_date_of_display", "01/12/2026" );
        model.put( "str_ending_date_of_display", "21/12/2026" );
        model.put( "permission_moderate_comment", "false" );
        model.put( "permission_access_code", "admin" );
        model.put( "permission_add_comment", "false" );
        model.put( "infos", List.of( ) );
        model.put( "errors", List.of( ) );
        return AppTemplateService.getTemplate( strTemplate, Locale.FRENCH, model ).getHtml( );
    }

    @Test
    public void testColorsOfTheAppointmentsCalendar( )
    {
        String strHtml = render( "admin/plugins/appointment/appointment/manage_appointments_calendar.html" );

        assertTrue( strHtml, strHtml.contains( "var bgClosedColor = '#bebebe';" ) );
        assertTrue( strHtml, strHtml.contains( "var bgOpenColor = '#fff';" ) );
    }

    @Test
    public void testColorsOfTheMultiSlotCalendar( )
    {
        String strHtml = render( "admin/plugins/appointment/appointment/manage_appointments_multislot_calendar.html" );

        assertTrue( strHtml, strHtml.contains( "var bgClosedColor = '#bebebe';" ) );
        assertTrue( strHtml, strHtml.contains( "var bgOpenColor = '#fff';" ) );
    }
}
