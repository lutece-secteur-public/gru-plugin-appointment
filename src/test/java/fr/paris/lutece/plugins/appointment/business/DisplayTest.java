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
import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.display.DisplayHome;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.test.LuteceTestCase;

import java.util.List;

/**
 * Test Class for the Display
 *
 * @author Laurent Payen
 *
 */
public final class DisplayTest extends LuteceTestCase
{

    public static final boolean DISPLAY_TITLE_FO_1 = true;
    public static final boolean DISPLAY_TITLE_FO_2 = false;
    public static final byte [ ] BYTES_1 = "BlaBlaBla".getBytes( );
    public static final byte [ ] BYTES_2 = "BloBloBlo".getBytes( );
    public static final String ICON_FORM_MIME_TYPE_1 = "ICON_FORM_MIME_TYPE_1";
    public static final String ICON_FORM_MIME_TYPE_2 = "ICON_FORM_MIME_TYPE_2";
    public static final int NB_WEEKS_TO_DISPLAY_1 = 10;
    public static final int NB_WEEKS_TO_DISPLAY_2 = 20;
    private Form form;
    private CalendarTemplate calendarTemplate;

    /**
     * Test method for the Display (CRUD)
     */
    public void testDisplay( )
    {
        // Initialize a Display
        Display display = buildDisplay( );
        display.setIdForm( this.form.getIdForm( ) );
        display.setIdCalendarTemplate( this.calendarTemplate.getIdCalendarTemplate( ) );
        // Create the Display in database
        DisplayHome.create( display );
        // Find the Display created in database
        Display displayStored = DisplayHome.findByPrimaryKey( display.getIdDisplay( ) );
        // Check Asserts
        checkAsserts( displayStored, display );

        // Update the Display
        display.setDisplayTitleFo( DISPLAY_TITLE_FO_2 );
        ImageResource imageResource = new ImageResource( );
        imageResource.setImage( BYTES_2 );
        imageResource.setMimeType( ICON_FORM_MIME_TYPE_2 );
        display.setIcon( imageResource );
        display.setNbWeeksToDisplay( NB_WEEKS_TO_DISPLAY_2 );
        // Update the Display in database
        DisplayHome.update( display );
        // Find the Display updated in database
        displayStored = DisplayHome.findByPrimaryKey( display.getIdDisplay( ) );
        // Check Asserts
        checkAsserts( displayStored, display );

        // Delete the Display
        DisplayHome.delete( display.getIdDisplay( ) );
        displayStored = DisplayHome.findByPrimaryKey( display.getIdDisplay( ) );
        // Check the Display has been removed from database
        assertNull( displayStored );

    }

    /**
     * Test delete cascade
     */
    public void testDeleteCascade( )
    {
        // Initialize a Display
        Display display = buildDisplay( );
        display.setIdForm( this.form.getIdForm( ) );
        display.setIdCalendarTemplate( this.calendarTemplate.getIdCalendarTemplate( ) );
        // Create the Display in database
        DisplayHome.create( display );
        // Find the Display created in database
        Display displayStored = DisplayHome.findByPrimaryKey( display.getIdDisplay( ) );
        assertNotNull( displayStored );
        // Delete the Form and by cascade the Display
        FormHome.delete( this.form.getIdForm( ) );
        displayStored = DisplayHome.findByPrimaryKey( display.getIdDisplay( ) );
        // Check the Display has been removed from database
        assertNull( displayStored );
    }

    /**
     * Test findByIdForm method
     */
    public void testFindByIdForm( )
    {
        // Initialize a Display
        Display display = buildDisplay( );
        display.setIdForm( this.form.getIdForm( ) );
        display.setIdCalendarTemplate( this.calendarTemplate.getIdCalendarTemplate( ) );
        // Create the Display in database
        DisplayHome.create( display );
        // Find the Display created in database
        Display displayStored = DisplayHome.findByIdForm( this.form.getIdForm( ) );
        // Check Asserts
        checkAsserts( displayStored, display );

    }

    /**
     * Build a Display Business Object
     *
     * @return the display
     */
    public Display buildDisplay( )
    {
        Display display = new Display( );
        display.setDisplayTitleFo( DISPLAY_TITLE_FO_1 );

        ImageResource img = new ImageResource( );
        img.setImage( BYTES_1 );
        img.setMimeType( ICON_FORM_MIME_TYPE_1 );
        display.setIcon( img );

        display.setNbWeeksToDisplay( NB_WEEKS_TO_DISPLAY_1 );
        return display;
    }

    /**
     * Check that all the asserts are true
     *
     * @param displayStored
     *            the Display stored
     * @param display
     *            the Display created
     */
    public void checkAsserts( Display displayStored, Display display )
    {
        assertEquals( displayStored.isDisplayTitleFo( ), display.isDisplayTitleFo( ) );
        assertEquals( displayStored.getIcon( ).getMimeType( ), display.getIcon( ).getMimeType( ) );
        assertEquals( displayStored.getIdCalendarTemplate( ), display.getIdCalendarTemplate( ) );
        assertEquals( displayStored.getIdForm( ), display.getIdForm( ) );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.form = FormTest.buildForm1( );
        FormHome.create( this.form );

        this.calendarTemplate = CalendarTemplateTest.buildCalendarTemplate( );
        CalendarTemplateHome.create( this.calendarTemplate );

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        //delete all the forms left over from tests
        for (Form f : FormHome.findAllForms()) {
            FormHome.delete(f.getIdForm());
            assertNull(FormHome.findByPrimaryKey(f.getIdForm()));
        }

        for(CalendarTemplate cal : CalendarTemplateHome.findAll()) {
            CalendarTemplateHome.delete(cal.getIdCalendarTemplate());
            assertNull(CalendarTemplateHome.findByPrimaryKey(cal.getIdCalendarTemplate()));
        }
        this.form = null;
        this.calendarTemplate = null;
    }
}
