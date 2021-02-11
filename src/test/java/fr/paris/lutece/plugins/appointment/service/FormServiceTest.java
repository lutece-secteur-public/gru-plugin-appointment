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

package fr.paris.lutece.plugins.appointment.service;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.calendar.CalendarTemplate;
import fr.paris.lutece.plugins.appointment.business.calendar.CalendarTemplateHome;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the Form Service
 *
 * @author Laurent Payen
 *
 */
public class FormServiceTest extends LuteceTestCase
{

    public static final String TITLE_FORM = "Title Form";
    public static final String COPY = "Copie";

    /**
     * Test method for the creation of a form
     */
    public void testCreateAppointmentForm( )
    {

        int nIdForm = FormService.createAppointmentForm( buildAppointmentForm( ) );

        List<Form> listForms = FormService.findFormsByTitle( TITLE_FORM );

        assertEquals( 1, listForms.size( ) );

        FormService.removeForm( nIdForm );

        listForms = FormService.findAllForms( );

        assertEquals( 0, listForms.size( ) );
    }

    /**
     * Build an AppointmentForm DTO
     *
     * @return a form
     */
    public static AppointmentFormDTO buildAppointmentForm( )
    {
        AppointmentFormDTO appointmentForm = new AppointmentFormDTO( );

        appointmentForm.setTitle( TITLE_FORM );
        // appointmentForm.setIdCategory(nIdCategory);
        appointmentForm.setDescription( "Description Form" );

        appointmentForm.setTimeStart( "09:00" );
        appointmentForm.setTimeEnd( "18:00" );
        appointmentForm.setDurationAppointments( 30 );

        appointmentForm.setMinTimeBeforeAppointment( 30 );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.now( ) ) );
        appointmentForm.setDateEndValidity( Date.valueOf( Constants.DATE_5 ) );

        appointmentForm.setDisplayTitleFo( Boolean.TRUE );
        appointmentForm.setIsDisplayedOnPortlet( Boolean.TRUE );
        appointmentForm.setNbWeeksToDisplay( 3 );

        appointmentForm.setNbDaysBeforeNewAppointment( 2 );
        appointmentForm.setNbMaxAppointmentsPerUser( 2 );
        appointmentForm.setNbDaysForMaxAppointmentsPerUser( 7 );

        appointmentForm.setMaxPeoplePerAppointment( 2 );
        appointmentForm.setMaxCapacityPerSlot( 3 );

        appointmentForm.setReference( "Référence Form" );

        appointmentForm.setIsOpenMonday( Boolean.TRUE );
        appointmentForm.setIsOpenTuesday( Boolean.TRUE );
        appointmentForm.setIsOpenWednesday( Boolean.TRUE );
        appointmentForm.setIsOpenThursday( Boolean.TRUE );
        appointmentForm.setIsOpenFriday( Boolean.TRUE );
        appointmentForm.setIsOpenSaturday( Boolean.FALSE );
        appointmentForm.setIsOpenSunday( Boolean.FALSE );

        // appointmentForm.setCalendarTemplateId(nCalendarTemplateId);

        appointmentForm.setEnableCaptcha( Boolean.TRUE );
        appointmentForm.setEnableMandatoryEmail( Boolean.TRUE );
        appointmentForm.setActiveAuthentication( Boolean.TRUE );

        appointmentForm.setAddress( "paris Viaduc des Arts, 75012 PARIS" );
        appointmentForm.setLatitude( 48.848 );
        appointmentForm.setLongitude( 2.3755 );

        ImageResource img = new ImageResource( );
        img.setImage( "BlaBlaBla".getBytes( ) );
        img.setMimeType( "ICON_FORM_MIME_TYPE_1" );
        appointmentForm.setIcon( img );

        CalendarTemplate calendarTemplate = new CalendarTemplate( );
        calendarTemplate.setTitle( "TITLE_1" );
        calendarTemplate.setDescription( "DESCRIPTION_1" );
        calendarTemplate.setTemplatePath( "TEMPLATE_PATH_1" );
        CalendarTemplateHome.create( calendarTemplate );
        appointmentForm.setCalendarTemplateId( CalendarTemplateHome.findAll( ).get( 0 ).getIdCalendarTemplate( ) );

        appointmentForm.setIsActive( Boolean.TRUE );

        return appointmentForm;
    }

    /**
     * Make a copy of form, with all its values
     */
    public void testCopyForm( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        int nIdCopyForm = FormService.copyForm( nIdForm, COPY );
        List<DayOfWeek> listopenDays = WorkingDayService.getOpenDays( appointmentForm );
        AppointmentFormDTO copyAppointmentForm = FormService.buildAppointmentForm( nIdCopyForm, 0, 0 );
        assertEquals( WeekDefinitionService.findListWeekDefinition( nIdForm ).size( ), WeekDefinitionService.findListWeekDefinition( nIdCopyForm ).size( ) );
        assertEquals( WorkingDayService.getOpenDays( appointmentForm ), WorkingDayService.getOpenDays( copyAppointmentForm ) );
        assertEquals( COPY, copyAppointmentForm.getTitle( ) );

        FormService.removeForm( nIdCopyForm );
        assertEquals( listopenDays, WorkingDayService.getOpenDays( appointmentForm ) );
        FormService.removeForm( nIdForm );
    }

}