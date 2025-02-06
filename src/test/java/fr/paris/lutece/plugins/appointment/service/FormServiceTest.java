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

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.calendar.CalendarTemplate;
import fr.paris.lutece.plugins.appointment.business.calendar.CalendarTemplateHome;
import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.display.DisplayHome;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.localization.Localization;
import fr.paris.lutece.plugins.appointment.business.localization.LocalizationHome;
import fr.paris.lutece.plugins.appointment.business.message.FormMessage;
import fr.paris.lutece.plugins.appointment.business.message.FormMessageHome;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlotHome;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinitionHome;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDayHome;
import fr.paris.lutece.plugins.appointment.business.rule.FormRule;
import fr.paris.lutece.plugins.appointment.business.rule.FormRuleHome;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRuleHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Test Class for the Form Service
 * 
 * @author Laurent Payen
 *
 */
public class FormServiceTest extends LuteceTestCase
{

    public static final String TITLE_FORM = "Title Form";

    /**
     * Test method for the creation of a form
     */
    public void testCreateAppointmentForm( )
    {
        // Remove all former forms with same title mistaking this test
        cleanFormByTitle( TITLE_FORM );

        int nIdForm = FormService.createAppointmentForm( buildAppointmentForm( ) );

        List<Form> listForms = FormService.findFormsByTitle( TITLE_FORM );

        assertEquals( 1, listForms.size( ) );

        cleanForm( nIdForm );

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

        appointmentForm.setName( "appointment_form" );
        appointmentForm.setTitle( TITLE_FORM );
        appointmentForm.setColor( "gray" );
        appointmentForm.setDescription( "Description Form" );
        appointmentForm.setDescriptionRule( "Description Rule" );

        appointmentForm.setTimeStart( "09:00" );
        appointmentForm.setTimeEnd( "18:00" );
        appointmentForm.setDurationAppointments( 30 );

        appointmentForm.setMinTimeBeforeAppointment( 30 );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.now( ) ) );
        appointmentForm.setDateEndValidity( Date.valueOf( LocalDate.parse( "2025-12-25" ) ) );

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
        appointmentForm.setListWorkingDay( WorkingDayService.findListWorkingDayByWeekDefinitionRule( appointmentForm.getIdReservationRule( ) ) );

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
        appointmentForm.setCalendarTemplateId( calendarTemplate.getIdCalendarTemplate( ) );

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
        int nIdCopyForm = FormService.copyForm( nIdForm, "Copie" );
        List<DayOfWeek> listopenDays = WorkingDayService.getOpenDays( appointmentForm );
        AppointmentFormDTO copyAppointmentForm = FormService.buildAppointmentForm( nIdCopyForm, 0 );
        assertEquals( WeekDefinitionService.findListWeekDefinition( nIdForm ).size( ), WeekDefinitionService.findListWeekDefinition( nIdCopyForm ).size( ) );
        assertEquals( WorkingDayService.getOpenDays( appointmentForm ), WorkingDayService.getOpenDays( copyAppointmentForm ) );
        assertEquals( "Copie", copyAppointmentForm.getTitle( ) );

        cleanForm( nIdCopyForm );
        assertEquals( listopenDays, WorkingDayService.getOpenDays( appointmentForm ) );
        cleanForm( nIdForm );
    }

    public static void cleanForm( int nIdForm )
    {
        if ( nIdForm != -1 )
        {
            for ( Slot s : SlotService.findListSlot( nIdForm ) )
            {
                try ( DAOUtil daoUtil = new DAOUtil( "DELETE FROM appointment_appointment_slot WHERE id_appointment = ?" ) )
                {
                    daoUtil.setInt( 1, s.getIdSlot( ) );
                    daoUtil.executeUpdate( );
                }
                // AppointmentDAO.deleteAppointmentSlot( AppointmentHome.findByIdSlot( s.getIdSlot( ) ), PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME)
                // );
                SlotService.deleteSlot( s );
            }
            Display display = DisplayHome.findByIdForm( nIdForm );
            if ( display != null )
            {
                DisplayHome.delete( display.getIdDisplay( ) );
                int nbDisplay = 0;
                try ( DAOUtil daoUtil = new DAOUtil( "select count(*) FROM appointment_display WHERE id_calendar_template=?" ) )
                {
                    daoUtil.setInt( 1, display.getIdCalendarTemplate( ) );
                    daoUtil.executeQuery( );
                    if ( daoUtil.next( ) )
                    {
                        nbDisplay = daoUtil.getInt( 1 );
                    }
                }
                if ( nbDisplay == 0 )
                {
                    CalendarTemplateHome.delete( display.getIdCalendarTemplate( ) );
                }
            }
            FormMessage formMessage = FormMessageHome.findByIdForm( nIdForm );
            if ( formMessage != null )
            {
                FormMessageHome.delete( formMessage.getIdFormMessage( ) );
            }
            Localization localization = LocalizationHome.findByIdForm( nIdForm );
            if ( localization != null )
            {
                LocalizationHome.delete( localization.getIdLocalization( ) );
            }

            FormRule formRule = FormRuleHome.findByIdForm( nIdForm );
            if ( formRule != null )
            {
                FormRuleHome.delete( formRule.getIdFormRule( ) );
            }

            for ( ReservationRule rr : ReservationRuleHome.findByIdForm( nIdForm ) )
            {
                for ( WeekDefinition wd : WeekDefinitionHome.findByIdForm( nIdForm ) )
                {
                    for ( WorkingDay wda : WorkingDayHome.findByIdReservationRule( rr.getIdReservationRule( ) ) )
                    {
                        TimeSlotHome.deleteByIdWorkingDay( wda.getIdWorkingDay( ) );

                    }

                }

                WeekDefinitionHome.deleteByIdReservationRule( rr.getIdReservationRule( ) );
                WorkingDayHome.deleteByIdReservationRule( rr.getIdReservationRule( ) );
                ReservationRuleHome.delete( rr.getIdReservationRule( ) );
            }
            FormService.removeForm( nIdForm );
        }
    }

    public static void cleanFormByTitle( String strFormsTitle )
    {
        List<Form> formsByTitle = FormService.findFormsByTitle( strFormsTitle );
        for ( Form form : formsByTitle )
        {
            cleanForm( form.getIdForm( ) );
        }
    }
}
