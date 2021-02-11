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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.paris.lutece.plugins.appointment.business.AppointmentTest;
import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.business.TimeSlotTest;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.AppointmentUtilities;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.WeekDefinitionService;
import fr.paris.lutece.plugins.appointment.service.WorkingDayService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.test.LuteceTestCase;

public class AppointmentUtilitiesTest extends LuteceTestCase
{
    private int nIdForm;
    private AppointmentFormDTO appointmentForm;
    /**
     * Try to get another appointment which does not match the rule of the number of days between two appointments for the same user
     */
    public void testNbDaysBetweenTwoAppointments( )
    {
        this.appointmentForm.setNbDaysBeforeNewAppointment( 2 );

        Slot slot1 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_2, Constants.ENDING_DATE_2, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_3, Constants.ENDING_DATE_3, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );

        assertFalse( AppointmentUtilities.checkNbDaysBetweenTwoAppointments( appointmentDTO2, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.EMAIL_3, this.appointmentForm ) );

    }

    /**
     * Try to get another appointment which matches the rule of the number of days between two appointments for the same user
     */
    public void testNbDaysBetweenTwoAppointments2( )
    {
        this.appointmentForm.setNbDaysBeforeNewAppointment( 2 );

        Slot slot1 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_2, Constants.ENDING_DATE_2, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_4, Constants.ENDING_DATE_4, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );

        assertTrue( AppointmentUtilities.checkNbDaysBetweenTwoAppointments( appointmentDTO2, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.EMAIL_3, this.appointmentForm ) );

    }

    /**
     * Try to get a third appointment which does not match the rule of the number of days between two appointments for the same user
     */
    public void testNbDaysBetweenTwoAppointments3( )
    {
        this.appointmentForm.setNbDaysBeforeNewAppointment( 2 );

        Slot slot1 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_2, Constants.ENDING_DATE_2, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_4, Constants.ENDING_DATE_4, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot3 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_7, Constants.ENDING_DATE_7, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot3 = SlotService.saveSlot( slot3 );

        AppointmentDTO appointmentDTO3 = AppointmentTest.buildAppointmentDTO( slot3, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );

        assertFalse( AppointmentUtilities.checkNbDaysBetweenTwoAppointments( appointmentDTO3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.EMAIL_3, this.appointmentForm ) );

    }

    /**
     * Try to get a third appointment which matches the rule of the number of days between two appointments for the same user
     */
    public void testNbDaysBetweenTwoAppointments4( )
    {

        this.appointmentForm.setNbDaysBeforeNewAppointment( 2 );

        Slot slot1 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_2, Constants.ENDING_DATE_2, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_4, Constants.ENDING_DATE_4, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot3 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_5, Constants.ENDING_DATE_5, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot3 = SlotService.saveSlot( slot3 );

        AppointmentDTO appointmentDTO3 = AppointmentTest.buildAppointmentDTO( slot3, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );

        assertTrue( AppointmentUtilities.checkNbDaysBetweenTwoAppointments( appointmentDTO3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.EMAIL_3, this.appointmentForm ) );

    }

    /**
     * Try to get a third appointment which doas not match the rule of the number of days between two appointments for the same user
     */
    public void testNbDaysBetweenTwoAppointments5( )
    {

        this.appointmentForm.setNbDaysBeforeNewAppointment( 2 );

        Slot slot1 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_2, Constants.ENDING_DATE_2, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_4, Constants.ENDING_DATE_4, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot3 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_6, Constants.ENDING_DATE_6, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot3 = SlotService.saveSlot( slot3 );

        AppointmentDTO appointmentDTO3 = AppointmentTest.buildAppointmentDTO( slot3, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );

        assertFalse( AppointmentUtilities.checkNbDaysBetweenTwoAppointments( appointmentDTO3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.EMAIL_3, this.appointmentForm ) );

    }

    /**
     * Check that the user can not take more than 2 appointments on 7 days
     */
    public void testCheckNbMaxAppointmentsOnAGivenPeriod( )
    {
        this.appointmentForm.setNbMaxAppointmentsPerUser( 2 );
        this.appointmentForm.setNbDaysForMaxAppointmentsPerUser( 7 );


        Slot slot1 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_2, Constants.ENDING_DATE_2, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_3, Constants.ENDING_DATE_3, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot3 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_5, Constants.ENDING_DATE_5, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot3 = SlotService.saveSlot( slot3 );

        AppointmentDTO appointmentDTO3 = AppointmentTest.buildAppointmentDTO( slot3, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );

        assertFalse( AppointmentUtilities.checkNbMaxAppointmentsOnAGivenPeriod( appointmentDTO3, Constants.EMAIL_3, this.appointmentForm ) );

    }

    /**
     * Check that the user can take another appointment
     */
    public void testCheckNbMaxAppointmentsOnAGivenPeriod2( )
    {

        this.appointmentForm.setNbMaxAppointmentsPerUser( 3 );
        this.appointmentForm.setNbDaysForMaxAppointmentsPerUser( 7 );

        Slot slot1 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_2, Constants.ENDING_DATE_2, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_3, Constants.ENDING_DATE_3, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot3 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_5, Constants.ENDING_DATE_5, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot3 = SlotService.saveSlot( slot3 );

        AppointmentDTO appointmentDTO3 = AppointmentTest.buildAppointmentDTO( slot3, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );

        assertTrue( AppointmentUtilities.checkNbMaxAppointmentsOnAGivenPeriod( appointmentDTO3, Constants.EMAIL_3, this.appointmentForm ) );

    }

    /**
     * Check and validate all the rules for the number of booked seats asked
     */
    public void testCheckAndReturnNbBookedSeats( )
    {

        this.appointmentForm.setMaxPeoplePerAppointment( 2 );
        this.appointmentForm.setMaxCapacityPerSlot( 3 );
        // Build the form

        Slot slot1 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_2, Constants.ENDING_DATE_2, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot1, Constants.EMAIL_4, Constants.FIRST_NAME_2, Constants.LAST_NAME_2, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        appointmentDTO2.setNbMaxPotentialBookedSeats( 1 );
        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
        assertEquals( 1, AppointmentUtilities.checkAndReturnNbBookedSeats( "1", this.appointmentForm, appointmentDTO2, Locale.FRANCE, listFormErrors ) );
        assertEquals( 0, listFormErrors.size( ) );

    }

    /**
     * Try to get an appointment with 2 places on a slot that have only 1 remaining place
     */
    public void testCheckAndReturnNbBookedSeats2( )
    {
        this.appointmentForm.setMaxPeoplePerAppointment( 2 );
        this.appointmentForm.setMaxCapacityPerSlot( 3 );
        // Build the form

        Slot slot1 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_2, Constants.ENDING_DATE_2, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }
        slot1 = SlotService.findSlotById( slot1.getIdSlot( ) );
        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot1, Constants.EMAIL_4, Constants.FIRST_NAME_2, Constants.LAST_NAME_2, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );

        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
        assertEquals( 2, AppointmentUtilities.checkAndReturnNbBookedSeats( "2", this.appointmentForm, appointmentDTO2, Locale.FRANCE, listFormErrors ) );
        assertEquals( 1, listFormErrors.size( ) );


    }

    /**
     * Return the min starting time to display
     */
    public void testGetMinTimeToDisplay( )
    {
        assertEquals( Constants.STARTING_TIME_3, AppointmentUtilities.getMinTimeToDisplay( Constants.TIME_1 ) );
        assertEquals( Constants.ENDING_TIME_1, AppointmentUtilities.getMinTimeToDisplay( Constants.TIME_2 ) );
    }

    /**
     * Return the max ending time to display
     */
    public void testGetMaxTimeToDisplay( )
    {
        assertEquals( Constants.ENDING_TIME_3, AppointmentUtilities.getMaxTimeToDisplay( Constants.TIME_3 ) );
        assertEquals( Constants.STARTING_TIME_4, AppointmentUtilities.getMaxTimeToDisplay( Constants.TIME_4 ) );
    }

    /**
     * Check if there are appointments impacted by the new week definition
     */
    public void testCheckNoAppointmentsImpacted( )
    {
        AppointmentFormDTO appointmentForm1 = this.appointmentForm;
        Slot slot1 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_8, Constants.ENDING_DATE_8, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );

        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_9, Constants.ENDING_DATE_9, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_2,
                Constants.ENDING_TIME_2, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setDateStartValidity( Date.valueOf( Constants.DATE_1 ) );
        appointmentForm2.setDateEndValidity( Date.valueOf( Constants.DATE_2 ) );
        appointmentForm2.setIsOpenMonday( Boolean.FALSE );
        appointmentForm2.setIsOpenTuesday( Boolean.TRUE );
        appointmentForm2.setIsOpenWednesday( Boolean.TRUE );
        appointmentForm2.setIsOpenThursday( Boolean.TRUE );
        appointmentForm2.setIsOpenFriday( Boolean.TRUE );
        appointmentForm2.setIsOpenSaturday( Boolean.FALSE );
        appointmentForm2.setIsOpenSunday( Boolean.FALSE );

        LocalDate dateOfModification = Constants.DATE_4;
        LocalDateTime endingDateTimeOfSearch = LocalDateTime.of( LocalDate.of( 9999, 12, 31 ), LocalTime.of( 23, 59 ) );
        List<Slot> listSlotsImpacted = SlotService.findSlotsByIdFormAndDateRange( this.nIdForm, dateOfModification.atStartOfDay( ), endingDateTimeOfSearch );
        List<Appointment> listAppointmentsImpacted = AppointmentService.findListAppointmentByListSlot( listSlotsImpacted );

        assertFalse( AppointmentUtilities.checkNoAppointmentsImpacted( listAppointmentsImpacted, this.nIdForm, dateOfModification, appointmentForm2 ) );

    }

    public void testCheckNoAppointmentsImpacted2( )
    {
        Slot slot1 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_10, Constants.ENDING_DATE_10, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_11, Constants.ENDING_DATE_11, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setDateStartValidity( Date.valueOf( Constants.DATE_1 ) );
        appointmentForm2.setDateEndValidity( Date.valueOf( Constants.DATE_2 ) );
        appointmentForm2.setIsOpenMonday( Boolean.FALSE );
        appointmentForm2.setIsOpenTuesday( Boolean.TRUE );
        appointmentForm2.setIsOpenWednesday( Boolean.TRUE );
        appointmentForm2.setIsOpenThursday( Boolean.TRUE );
        appointmentForm2.setIsOpenFriday( Boolean.TRUE );
        appointmentForm2.setIsOpenSaturday( Boolean.FALSE );
        appointmentForm2.setIsOpenSunday( Boolean.FALSE );

        LocalDate dateOfModification = Constants.DATE_3;
        LocalDateTime endingDateTimeOfSearch = LocalDateTime.of( LocalDate.of( 9999, 12, 31 ), LocalTime.of( 23, 59 ) );
        List<Slot> listSlotsImpacted = SlotService.findSlotsByIdFormAndDateRange( this.nIdForm, dateOfModification.atStartOfDay( ), endingDateTimeOfSearch );
        List<Appointment> listAppointmentsImpacted = AppointmentService.findListAppointmentByListSlot( listSlotsImpacted );

        assertTrue( AppointmentUtilities.checkNoAppointmentsImpacted( listAppointmentsImpacted, this.nIdForm, dateOfModification, this.appointmentForm ) );
    }

    /**
     * Check that there is no validated appointments on a slot
     */
    public void testCheckNoValidatedAppointmentsOnThisSlot( )
    {

        Slot slot1 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_11, Constants.ENDING_DATE_11, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        assertFalse( AppointmentUtilities.checkNoValidatedAppointmentsOnThisSlot( slot1 ) );

    }

    /**
     * Check that there is no validated appointments on a slot (there is an appointment on the slot but it has been cancelled)
     */
    public void testCheckNoValidatedAppointmentsOnThisSlot2( )
    {
        Slot slot1 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_11, Constants.ENDING_DATE_11, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }
        Appointment appointment1 = AppointmentService.findAppointmentById( nIdAppointment1 );
        appointment1.setIsCancelled( true );
        AppointmentService.updateAppointment( appointment1 );

        assertTrue( AppointmentUtilities.checkNoValidatedAppointmentsOnThisSlot( slot1 ) );

    }

    /**
     * Return the slots impacted by the modification of this time slot
     */
    public void testFindSlotsImpactedByThisTimeSlot( )
    {
        List<WeekDefinition> allWeekDefinition = WeekDefinitionService.findListWeekDefinition( this.nIdForm );

        WeekDefinition weekDefinition = allWeekDefinition.get( 0 );

        Slot slot1 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_8, Constants.ENDING_DATE_8, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        List<WorkingDay> listWorkingDay = WorkingDayService.findListWorkingDayByWeekDefinition( weekDefinition.getIdWeekDefinition( ) );

        WorkingDay mondayWorkingDay = listWorkingDay.stream( ).filter( w -> w.getDayOfWeek( ) == DayOfWeek.MONDAY.getValue( ) ).findFirst( ).get( );

        TimeSlot timeSlot = TimeSlotTest.buildTimeSlot( Constants.STARTING_TIME_1, Constants.ENDING_TIME_1, false, Constants.NB_REMAINING_PLACES_3, mondayWorkingDay.getIdWorkingDay( ) );

        assertEquals( 1, AppointmentUtilities.findSlotsImpactedByThisTimeSlot( timeSlot, this.nIdForm, weekDefinition.getIdWeekDefinition( ), false ).size( ) );

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        this.appointmentForm = FormServiceTest.buildAppointmentForm( );
        this.appointmentForm.setDateStartValidity( Date.valueOf( Constants.DATE_1 ) );
        this.appointmentForm.setDateEndValidity( Date.valueOf( Constants.DATE_2 ) );
        this.appointmentForm.setIdForm( this.nIdForm );

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        //delete all the forms left over from tests
        for (Form f : FormService.findAllForms()) {
            FormService.removeForm(f.getIdForm());
        }
        this.appointmentForm = null;
        this.nIdForm = 0;
    }
}