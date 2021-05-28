/*
 * Copyright (c) 2002-2021, City of Paris
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

import fr.paris.lutece.plugins.appointment.business.calendar.CalendarTemplate;
import fr.paris.lutece.plugins.appointment.business.calendar.CalendarTemplateHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the CalendarTemplate
 * 
 * @author Laurent Payen
 *
 */
public final class CalendarTemplateTest extends LuteceTestCase
{

    public static final String TITLE_1 = "Title1";
    public static final String TITLE_2 = "Title2";
    public static final String DESCRIPTION_1 = "Description1";
    public static final String DESCRIPTION_2 = "Description2";
    public static final String TEMPLATE_PATH_1 = "TemplatePath1";
    public static final String TEMPLATE_PATH_2 = "TemplatePath2";

    /**
     * Test method for the CalendarTemplate (CRUD)
     */
    public void testCalendarTemplate( )
    {
        // Initialize a CalendarTemplate
        CalendarTemplate calendarTemplate = buildCalendarTemplate( );
        // Create the CalendarTemplate in database
        CalendarTemplateHome.create( calendarTemplate );
        // Find the CalendarTemplate created in database
        CalendarTemplate calendarTemplateStored = CalendarTemplateHome.findByPrimaryKey( calendarTemplate.getIdCalendarTemplate( ) );
        // Check Asserts
        checkAsserts( calendarTemplateStored, calendarTemplate );

        // Update the CalendarTemplate
        calendarTemplate.setTitle( TITLE_2 );
        calendarTemplate.setDescription( DESCRIPTION_2 );
        calendarTemplate.setTemplatePath( TEMPLATE_PATH_2 );
        // Update the CalendarTemplate in database
        CalendarTemplateHome.update( calendarTemplate );
        // Find the CalendarTemplate updated in database
        calendarTemplateStored = CalendarTemplateHome.findByPrimaryKey( calendarTemplate.getIdCalendarTemplate( ) );
        // Check Asserts
        checkAsserts( calendarTemplateStored, calendarTemplate );

        // Delete the CalendarTemplate
        CalendarTemplateHome.delete( calendarTemplate.getIdCalendarTemplate( ) );
        calendarTemplateStored = CalendarTemplateHome.findByPrimaryKey( calendarTemplate.getIdCalendarTemplate( ) );
        // Check the CalendarTemplate has been removed from database
        assertNull( calendarTemplateStored );
    }

    /**
     * Build a CalendarTemplate Business Object
     * 
     * @return the calendarTemplate
     */
    public static CalendarTemplate buildCalendarTemplate( )
    {
        CalendarTemplate calendarTemplate = new CalendarTemplate( );
        calendarTemplate.setTitle( TITLE_1 );
        calendarTemplate.setDescription( DESCRIPTION_1 );
        calendarTemplate.setTemplatePath( TEMPLATE_PATH_1 );

        return calendarTemplate;
    }

    /**
     * Check that all the asserts are true
     * 
     * @param calendarTemplateStored
     *            the CalendarTemplate stored
     * @param calendarTemplate
     *            the CalendarTemplate created
     */
    public void checkAsserts( CalendarTemplate calendarTemplateStored, CalendarTemplate calendarTemplate )
    {
        assertEquals( calendarTemplateStored.getTitle( ), calendarTemplate.getTitle( ) );
        assertEquals( calendarTemplateStored.getDescription( ), calendarTemplate.getDescription( ) );
        assertEquals( calendarTemplateStored.getTemplatePath( ), calendarTemplate.getTemplatePath( ) );
    }

}
