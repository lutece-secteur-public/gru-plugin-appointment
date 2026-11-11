/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.business.message.FormMessage;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.service.FormServiceTest;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.appointment.web.dto.ResponseRecapDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Renders, with the HTML auto-escaping of the core (LUT-33153), the appointment templates that write HTML built in Java:
 * the form, the entries, the workflow task forms, the captcha and the recap of an image are marked with HtmlMarkup where
 * they are put in the model and keep their tags, while the text typed by a user is escaped. The templates whose captured
 * blocks became string expressions (step titles, step links, field values) render too.
 */
public class AppointmentHtmlFragmentsRenderTest extends LuteceTestCase
{
    private static final String FRAGMENT = "<div class=\"fragment\"><input name=\"x\" value=\"a &lt;b&gt;\"></div>";
    private static final String FRAGMENT_ESCAPED = "&lt;div class=&quot;fragment&quot;&gt;";
    private static final String TEXT = "Dupont <b>\"&'";
    private static final String TEXT_ESCAPED = "Dupont &lt;b&gt;&quot;&amp;&#39;";
    private static final String TEXT_UNESCAPED = "Dupont <b>";
    private static final int ID_FIELD = 4321;

    /**
     * Renders a template with the core configuration.
     *
     * @param strTemplate
     *            the template path
     * @param model
     *            the model
     * @return the HTML
     */
    private static String render( String strTemplate, Map<String, Object> model )
    {
        return AppTemplateService.getTemplate( strTemplate, Locale.FRENCH, model ).getHtml( );
    }

    /**
     * An appointment of one slot, whose user typed markup characters
     *
     * @param nIdForm
     *            the form
     * @return the appointment
     */
    private static AppointmentDTO appointment( int nIdForm )
    {
        AppointmentDTO appointment = new AppointmentDTO( );
        appointment.setLastName( TEXT );
        appointment.setFirstName( TEXT );
        appointment.setEmail( "jean.dupont@test.fr" );
        appointment.setNbBookedSeats( 1 );
        appointment.setReference( "REF123" );
        appointment.setDateOfTheAppointment( "13/11/2026" );
        LocalDateTime start = LocalDate.of( 2026, 11, 13 ).atTime( 10, 0 );
        Slot slot = SlotTest.buildSlot( nIdForm, start, start.plusMinutes( 30 ), 1, 1, 0, 1, true, false );
        slot.setStartingTime( start.toLocalTime( ) );
        slot.setEndingTime( start.plusMinutes( 30 ).toLocalTime( ) );
        appointment.setSlot( List.of( slot ) );
        return appointment;
    }

    /**
     * The form HTML of the booking page is written as markup, a String would print as text
     */
    @Test
    public void testFormHtmlKeepsItsTags( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "form_html", HtmlMarkup.of( FRAGMENT ) );
        String strHtml = render( "skin/plugins/appointment/appointment_form.html", model );
        assertTrue( strHtml, strHtml.contains( FRAGMENT ) );

        model.put( "form_html", FRAGMENT );
        strHtml = render( "skin/plugins/appointment/appointment_form.html", model );
        assertTrue( strHtml, strHtml.contains( FRAGMENT_ESCAPED ) );
    }

    /**
     * The task forms of a workflow action keep their tags in the front office and in the back office
     */
    @Test
    public void testTasksFormKeepsItsTags( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "tasks_form", HtmlMarkup.of( FRAGMENT ) );
        model.put( "id_action", 1 );
        model.put( "refAppointment", "REF123" );
        model.put( "id_appointment", 1 );
        String strHtml = render( "skin/plugins/appointment/tasks_form_workflow.html", model );
        assertTrue( strHtml, strHtml.contains( FRAGMENT ) );

        strHtml = render( "admin/plugins/appointment/appointment/tasks_form_workflow.html", model );
        assertTrue( strHtml, strHtml.contains( FRAGMENT ) );
    }

    /**
     * The entries of a conditional answer and of a group are written as markup inside their container
     */
    @Test
    public void testConditionalAndGroupEntriesKeepTheirChildren( )
    {
        Field field = new Field( );
        field.setIdField( ID_FIELD );
        Entry entry = new Entry( );
        entry.setTitle( TEXT );
        Map<String, Object> model = new HashMap<>( );
        model.put( "field", field );
        model.put( "entry", entry );
        model.put( "str_list_entry_children", HtmlMarkup.of( FRAGMENT ) );

        for ( String strTemplate : List.of( "skin/plugins/appointment/html_code_div_conditional_entry.html",
                "admin/plugins/appointment/html_code_div_conditional_entry.html" ) )
        {
            String strHtml = render( strTemplate, model );
            assertTrue( strHtml, strHtml.contains( FRAGMENT ) );
            assertTrue( strHtml, strHtml.contains( "id=\"div" + ID_FIELD + "\"" ) );
        }
        String strHtml = render( "skin/plugins/appointment/entries/html_code_entry_type_group.html", model );
        assertTrue( strHtml, strHtml.contains( FRAGMENT ) );
        assertTrue( strHtml, strHtml.contains( TEXT_ESCAPED ) );
        assertFalse( strHtml, strHtml.contains( TEXT_UNESCAPED ) );
    }

    /**
     * The recap writes the image of an image answer and the captcha as markup, and the names and the text answers
     * escaped; its step titles and links are strings
     */
    @Test
    public void testRecapWritesMarkupAndEscapesText( )
    {
        AppointmentFormDTO form = FormServiceTest.buildAppointmentForm( );
        Entry imageEntry = new Entry( );
        imageEntry.setIdEntry( 1 );
        imageEntry.setTitle( "Photo" );
        Entry textEntry = new Entry( );
        textEntry.setIdEntry( 2 );
        textEntry.setTitle( "Comment" );
        ResponseRecapDTO image = new ResponseRecapDTO( response( imageEntry ), FRAGMENT );
        image.setRecapValueHtml( true );
        ResponseRecapDTO text = new ResponseRecapDTO( response( textEntry ), TEXT );

        Map<String, Object> model = new HashMap<>( );
        model.put( "form", form );
        model.put( "formMessages", new FormMessage( ) );
        model.put( "appointment", appointment( form.getIdForm( ) ) );
        model.put( "modifDateAppointment", false );
        model.put( "token", "token" );
        model.put( "captcha", HtmlMarkup.of( "<div class=\"captcha\"></div>" ) );
        model.put( "listResponseRecapDTO", List.of( image, text ) );

        String strHtml = render( "skin/plugins/appointment/appointment_form_recap.html", model );

        assertTrue( strHtml, strHtml.contains( "Photo : " + FRAGMENT ) );
        assertTrue( strHtml, strHtml.contains( "Comment : " + TEXT_ESCAPED ) );
        assertTrue( strHtml, strHtml.contains( "<div class=\"captcha\"></div>" ) );
        assertFalse( strHtml, strHtml.contains( TEXT_UNESCAPED ) );
        assertTrue( strHtml, strHtml.contains( "view=getViewAppointmentCalendar&amp;id_form=" ) );
        assertTrue( strHtml, strHtml.contains( "data-lutece-action=\"back\"" ) );
    }

    /**
     * The booking form writes the entries built in Java as markup and the values typed by the user escaped once
     */
    @Test
    public void testBookingFormWritesEntriesAndEscapesValues( )
    {
        AppointmentFormDTO form = FormServiceTest.buildAppointmentForm( );
        AppointmentDTO appointment = appointment( form.getIdForm( ) );
        appointment.setConfirmEmail( "jean.dupont@test.fr" );
        Map<String, Object> model = new HashMap<>( );
        model.put( "form", form );
        model.put( "formMessages", new FormMessage( ) );
        model.put( "appointment", appointment );
        model.put( "nbPlacesToTake", 0 );
        model.put( "nbplaces", 1 );
        model.put( "date_of_display", LocalDate.of( 2026, 11, 13 ) );
        model.put( "token", "token" );
        model.put( "str_entry", HtmlMarkup.of( FRAGMENT ) );

        String strHtml = render( "skin/plugins/appointment/html_code_form.html", model );

        assertTrue( strHtml, strHtml.contains( FRAGMENT ) );
        assertTrue( strHtml, strHtml.contains( "value=\"" + TEXT_ESCAPED + "\"" ) );
        assertFalse( strHtml, strHtml.contains( TEXT_UNESCAPED ) );
        assertFalse( strHtml, strHtml.contains( "&amp;lt;" ) );
    }

    /**
     * The recap value of an image is markup, the recap value of a text answer stays a String
     */
    @Test
    public void testRecapMarkup( )
    {
        ResponseRecapDTO image = new ResponseRecapDTO( new Response( ), FRAGMENT );
        image.setRecapValueHtml( true );
        ResponseRecapDTO text = new ResponseRecapDTO( new Response( ), TEXT );

        assertTrue( "the image recap is markup", image.getRecapMarkup( ) instanceof freemarker.core.TemplateHTMLOutputModel );
        assertEquals( TEXT, text.getRecapMarkup( ) );
    }

    /**
     * The hidden days of the front office calendar are Strings in the model and are written as JavaScript numbers
     */
    @Test
    public void testCalendarHiddenDaysAreNumbers( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "date_of_display", LocalDate.of( 2026, 11, 13 ) );
        model.put( "min_date_of_open_day", LocalDate.of( 2026, 11, 9 ) );
        model.put( "max_date_of_open_day", LocalDate.of( 2026, 11, 13 ) );
        model.put( "modifDateAppointment", false );
        model.put( "hidden_days", List.of( "0", "6" ) );
        String strHtml = render( "skin/plugins/appointment/calendar/appointment_form_calendar_js.html", model );
        assertTrue( strHtml, strHtml.contains( "const hiddenDays = [0,6,];" ) );
    }

    private static Response response( Entry entry )
    {
        Response response = new Response( );
        response.setEntry( entry );
        return response;
    }
}
