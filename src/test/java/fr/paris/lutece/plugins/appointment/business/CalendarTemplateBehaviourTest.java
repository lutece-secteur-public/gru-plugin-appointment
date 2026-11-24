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
package fr.paris.lutece.plugins.appointment.business;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.calendar.CalendarTemplate;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * The front office behaviour of a calendar template comes from its file, not from its title
 */
public class CalendarTemplateBehaviourTest extends LuteceTestCase
{
    private static CalendarTemplate template( String strTitle, String strFile )
    {
        CalendarTemplate template = new CalendarTemplate( );
        template.setTitle( strTitle );
        template.setTemplatePath( "skin/plugins/appointment/calendar/" + strFile );
        return template;
    }

    /**
     * A delivered template renamed in the back office keeps its behaviour
     */
    @Test
    public void testRenamedDeliveredTemplate( ) throws Exception
    {
        assertEquals( CalendarTemplate.CALENDAR_OPEN_DAYS,
                behaviour( template( "Agenda des jours d'ouverture", "appointment_form_calendar_opendays.html" ) ) );
        assertEquals( CalendarTemplate.FREE_SLOTS, behaviour( template( "Créneaux libres", "appointment_form_list_open_slots.html" ) ) );
        assertEquals( CalendarTemplate.FREE_SLOTS_ON_OPEN_DAYS,
                behaviour( template( "Créneaux libres en semaine", "appointment_form_list_open_slots_opendays.html" ) ) );
        assertEquals( CalendarTemplate.FREE_SLOTS_GROUPED, behaviour( template( "Regroupés", "appointment_form_list_open_slots_grouped.html" ) ) );
        assertEquals( CalendarTemplate.CALENDAR, behaviour( template( "Agenda", "appointment_form_calendar.html" ) ) );
    }

    /**
     * A template of another file keeps the behaviour of its title
     */
    @Test
    public void testOtherTemplateKeepsItsTitle( ) throws Exception
    {
        assertEquals( CalendarTemplate.FREE_SLOTS, behaviour( template( CalendarTemplate.FREE_SLOTS, "my_template.html" ) ) );
    }

    // by reflection: the method is added by the fix
    private static String behaviour( CalendarTemplate template ) throws Exception
    {
        return (String) CalendarTemplate.class.getMethod( "getBehaviour" ).invoke( template );
    }
}
