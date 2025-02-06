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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import fr.paris.lutece.plugins.appointment.business.AppointmentTest;
import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.business.TimeSlotTest;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRuleHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.test.LuteceTestCase;

public class AppointmentUtilitiesTest extends LuteceTestCase
{

    private LocalDate _formStart = LocalDate.now( ).plusDays( 1 );
    private LocalDate _formEnd = LocalDate.now( ).plusDays( 10 );
    private LocalDate _formStartPlus1 = _formStart.plusDays( 1 );
    private LocalDate _formStartPlus3 = _formStart.plusDays( 3 );
    private LocalDate _formStartPlus4 = _formStart.plusDays( 4 );
    private LocalDate _formStartPlus6 = _formStart.plusDays( 6 );
    private LocalTime _timeStart = LocalTime.of( 10, 0 );
    private LocalTime _timeEnd = LocalTime.of( 10, 30 );

    /**
     * Try to get another appointment which does not match the rule of the number of days between two appointments for the same user
     */
    public void testNbDaysBetweenTwoAppointments( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setName( "appointment_form" );
        appointmentForm.setDateStartValidity( Date.valueOf( _formStart ) );
        appointmentForm.setDateEndValidity( Date.valueOf( _formEnd ) );

        appointmentForm.setNbDaysBeforeNewAppointment( 2 );

        // Build the form
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        appointmentForm.setIdForm( nIdForm );
        Slot slot1 = SlotTest.buildSlot( nIdForm, _formStart.atTime( _timeStart ), _formStart.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );

        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, _formStartPlus1.atTime( _timeEnd ), LocalDateTime.parse( "2018-06-05T10:30" ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );

        assertFalse( AppointmentUtilities.checkNbDaysBetweenTwoAppointments( appointmentDTO2, "jean.dupont@mdp.fr", appointmentForm ) );

        cleanUp( nIdForm, appointmentForm, appointmentDTO1, appointmentDTO2 );

    }

    /**
     * Try to get another appointment which matches the rule of the number of days between two appointments for the same user
     */
    public void testNbDaysBetweenTwoAppointments2( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setName( "appointment_form" );
        appointmentForm.setDateStartValidity( Date.valueOf( _formStart ) );
        appointmentForm.setDateEndValidity( Date.valueOf( _formEnd ) );

        appointmentForm.setNbDaysBeforeNewAppointment( 2 );

        // Build the form
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        appointmentForm.setIdForm( nIdForm );
        Slot slot1 = SlotTest.buildSlot( nIdForm, _formStart.atTime( _timeStart ), _formStart.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, _formStartPlus3.atTime( _timeStart ), _formStartPlus3.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );

        assertTrue( AppointmentUtilities.checkNbDaysBetweenTwoAppointments( appointmentDTO2, "jean.dupont@mdp.fr", appointmentForm ) );
        cleanUp( nIdForm, appointmentForm, appointmentDTO1, appointmentDTO2 );

    }

    /**
     * Try to get a third appointment which does not match the rule of the number of days between two appointments for the same user
     */
    public void testNbDaysBetweenTwoAppointments3( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setName( "appointment_form" );
        appointmentForm.setDateStartValidity( Date.valueOf( _formStart ) );
        appointmentForm.setDateEndValidity( Date.valueOf( _formEnd ) );

        appointmentForm.setNbDaysBeforeNewAppointment( 2 );

        // Build the form
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        appointmentForm.setIdForm( nIdForm );
        Slot slot1 = SlotTest.buildSlot( nIdForm, _formStart.atTime( _timeStart ), _formStart.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, _formStartPlus3.atTime( _timeStart ), _formStartPlus3.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot3 = SlotTest.buildSlot( nIdForm, _formStartPlus4.atTime( _timeStart ), _formStartPlus4.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot3 = SlotService.saveSlot( slot3 );

        AppointmentDTO appointmentDTO3 = AppointmentTest.buildAppointmentDTO( nIdForm, slot3, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );

        assertFalse( AppointmentUtilities.checkNbDaysBetweenTwoAppointments( appointmentDTO3, "jean.dupont@mdp.fr", appointmentForm ) );
        cleanUp( nIdForm, appointmentForm, appointmentDTO1, appointmentDTO2, appointmentDTO3 );

    }

    /**
     * Try to get a third appointment which matches the rule of the number of days between two appointments for the same user
     */
    public void testNbDaysBetweenTwoAppointments4( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( _formStart ) );
        appointmentForm.setDateEndValidity( Date.valueOf( _formEnd ) );

        appointmentForm.setNbDaysBeforeNewAppointment( 2 );

        // Build the form
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        appointmentForm.setIdForm( nIdForm );
        Slot slot1 = SlotTest.buildSlot( nIdForm, _formStart.atTime( _timeStart ), _formStart.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, _formStartPlus6.atTime( _timeStart ), _formStartPlus6.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot3 = SlotTest.buildSlot( nIdForm, _formStartPlus3.atTime( _timeStart ), _formStartPlus3.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot3 = SlotService.saveSlot( slot3 );

        AppointmentDTO appointmentDTO3 = AppointmentTest.buildAppointmentDTO( nIdForm, slot3, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );

        assertTrue( AppointmentUtilities.checkNbDaysBetweenTwoAppointments( appointmentDTO3, "jean.dupont@mdp.fr", appointmentForm ) );
        cleanUp( nIdForm, appointmentForm, appointmentDTO1, appointmentDTO2, appointmentDTO3 );
    }

    /**
     * Try to get a third appointment which doas not match the rule of the number of days between two appointments for the same user
     */
    public void testNbDaysBetweenTwoAppointments5( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( _formStart ) );
        appointmentForm.setDateEndValidity( Date.valueOf( _formEnd ) );

        appointmentForm.setNbDaysBeforeNewAppointment( 2 );

        // Build the form
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        appointmentForm.setIdForm( nIdForm );

        Slot slot1 = SlotTest.buildSlot( nIdForm, _formStart.atTime( _timeStart ), _formStart.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, _formStartPlus4.atTime( _timeStart ), _formStartPlus4.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot3 = SlotTest.buildSlot( nIdForm, _formStartPlus3.atTime( _timeStart ), _formStartPlus3.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot3 = SlotService.saveSlot( slot3 );

        AppointmentDTO appointmentDTO3 = AppointmentTest.buildAppointmentDTO( nIdForm, slot3, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );

        assertFalse( AppointmentUtilities.checkNbDaysBetweenTwoAppointments( appointmentDTO3, "jean.dupont@mdp.fr", appointmentForm ) );
        cleanUp( nIdForm, appointmentForm, appointmentDTO1, appointmentDTO2, appointmentDTO3 );
    }

    /**
     * Check that the user can not take more than 2 appointments on 7 days
     */
    public void testCheckNbMaxAppointmentsOnAGivenPeriod( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( _formStart ) );
        appointmentForm.setDateEndValidity( Date.valueOf( _formEnd ) );
        appointmentForm.setNbMaxAppointmentsPerUser( 2 );
        appointmentForm.setNbDaysForMaxAppointmentsPerUser( 7 );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        appointmentForm.setIdForm( nIdForm );

        Slot slot1 = SlotTest.buildSlot( nIdForm, _formStart.atTime( _timeStart ), _formStart.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, _formStartPlus1.atTime( _timeStart ), _formStartPlus1.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot3 = SlotTest.buildSlot( nIdForm, _formStartPlus3.atTime( _timeStart ), _formStartPlus3.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot3 = SlotService.saveSlot( slot3 );

        AppointmentDTO appointmentDTO3 = AppointmentTest.buildAppointmentDTO( nIdForm, slot3, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );

        assertFalse( AppointmentUtilities.checkNbMaxAppointmentsOnAGivenPeriod( appointmentDTO3, "jean.dupont@mdp.fr", appointmentForm ) );
        cleanUp( nIdForm, appointmentForm, appointmentDTO1, appointmentDTO2, appointmentDTO3 );
    }

    /**
     * Check that the user can take another appointment
     */
    public void testCheckNbMaxAppointmentsOnAGivenPeriod2( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( _formStart ) );
        appointmentForm.setDateEndValidity( Date.valueOf( _formEnd ) );
        appointmentForm.setNbMaxAppointmentsPerUser( 3 );
        appointmentForm.setNbDaysForMaxAppointmentsPerUser( 7 );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        appointmentForm.setIdForm( nIdForm );

        Slot slot1 = SlotTest.buildSlot( nIdForm, _formStart.atTime( _timeStart ), _formStart.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, _formStartPlus1.atTime( _timeStart ), _formStartPlus1.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot3 = SlotTest.buildSlot( nIdForm, _formStartPlus3.atTime( _timeStart ), _formStartPlus3.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot3 = SlotService.saveSlot( slot3 );

        AppointmentDTO appointmentDTO3 = AppointmentTest.buildAppointmentDTO( nIdForm, slot3, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );

        assertTrue( AppointmentUtilities.checkNbMaxAppointmentsOnAGivenPeriod( appointmentDTO3, "jean.dupont@mdp.fr", appointmentForm ) );
        cleanUp( nIdForm, appointmentForm, appointmentDTO1, appointmentDTO2, appointmentDTO3 );
    }

    /**
     * Check and validate all the rules for the number of booked seats asked
     */
    public void testCheckAndReturnNbBookedSeats( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( _formStart ) );
        appointmentForm.setDateEndValidity( Date.valueOf( _formEnd ) );
        appointmentForm.setMaxPeoplePerAppointment( 2 );
        appointmentForm.setMaxCapacityPerSlot( 3 );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        appointmentForm.setIdForm( nIdForm );

        Slot slot1 = SlotTest.buildSlot( nIdForm, _formStart.atTime( _timeStart ), _formStart.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot1, "gerard.durand@mdp.fr", "Gérard", "Durand", _timeStart, _timeEnd,
                1 );
        appointmentDTO2.setNbMaxPotentialBookedSeats( 1 );
        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
        assertEquals( 1, AppointmentUtilities.checkAndReturnNbBookedSeats( "1", appointmentForm, appointmentDTO2, Locale.FRANCE, listFormErrors ) );
        assertEquals( 0, listFormErrors.size( ) );
        cleanUp( nIdForm, appointmentForm, appointmentDTO1, appointmentDTO2 );

    }

    /**
     * Try to get an appointment with 2 places on a slot that have only 1 remaining place
     */
    public void testCheckAndReturnNbBookedSeats2( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( _formStart ) );
        appointmentForm.setDateEndValidity( Date.valueOf( _formEnd ) );
        appointmentForm.setMaxPeoplePerAppointment( 2 );
        appointmentForm.setMaxCapacityPerSlot( 3 );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        appointmentForm.setIdForm( nIdForm );

        Slot slot1 = SlotTest.buildSlot( nIdForm, _formStart.atTime( _timeStart ), _formStart.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }
        slot1 = SlotService.findSlotById( slot1.getIdSlot( ) );
        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot1, "gerard.durand@mdp.fr", "Gérard", "Durand", _timeStart, _timeEnd,
                2 );

        List<GenericAttributeError> listFormErrors = new ArrayList<>( );
        assertEquals( 2, AppointmentUtilities.checkAndReturnNbBookedSeats( "2", appointmentForm, appointmentDTO2, Locale.FRANCE, listFormErrors ) );
        assertEquals( 1, listFormErrors.size( ) );
        cleanUp( nIdForm, appointmentForm, appointmentDTO1, appointmentDTO2 );

    }

    /**
     * Return the min starting time to display
     */
    public void testGetMinTimeToDisplay( )
    {
        assertEquals( LocalTime.parse( "09:00" ), AppointmentUtilities.getMinTimeToDisplay( LocalTime.parse( "09:22" ) ) );
        assertEquals( _timeEnd, AppointmentUtilities.getMinTimeToDisplay( LocalTime.parse( "10:47" ) ) );
    }

    /**
     * Return the max ending time to display
     */
    public void testGetMaxTimeToDisplay( )
    {
        assertEquals( LocalTime.parse( "09:30" ), AppointmentUtilities.getMaxTimeToDisplay( LocalTime.parse( "09:01" ) ) );
        assertEquals( LocalTime.parse( "11:00" ), AppointmentUtilities.getMaxTimeToDisplay( LocalTime.parse( "10:42" ) ) );
    }

    /**
     * Check if there are appointments impacted by the new week definition
     */
    public void testCheckNoAppointmentsImpacted( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( _formStart ) );
        appointmentForm.setDateEndValidity( Date.valueOf( _formEnd ) );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        appointmentForm.setIdForm( nIdForm );
        Slot slot1 = SlotTest.buildSlot( nIdForm, _formStartPlus4.atTime( _timeStart ), _formStartPlus4.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );

        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, _formStartPlus6.atTime( _timeStart ), _formStartPlus6.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setDateStartValidity( Date.valueOf( _formStart ) );
        appointmentForm2.setDateEndValidity( Date.valueOf( _formEnd ) );
        appointmentForm2.setIsOpenMonday( Boolean.FALSE );
        appointmentForm2.setIsOpenTuesday( Boolean.FALSE );
        appointmentForm2.setIsOpenWednesday( Boolean.FALSE );
        appointmentForm2.setIsOpenThursday( Boolean.FALSE );
        appointmentForm2.setIsOpenFriday( Boolean.FALSE );
        appointmentForm2.setIsOpenSaturday( Boolean.FALSE );
        appointmentForm2.setIsOpenSunday( Boolean.FALSE );

        LocalDate dateOfModification = _formStartPlus3;
        LocalDateTime endingDateTimeOfSearch = _formEnd.plusYears( 1 ).atTime( _timeEnd );
        List<Slot> listSlotsImpacted = SlotService.findSlotsByIdFormAndDateRange( nIdForm, dateOfModification.atStartOfDay( ), endingDateTimeOfSearch );
        List<Appointment> listAppointmentsImpacted = AppointmentService.findListAppointmentByListSlot( listSlotsImpacted );

        assertFalse( AppointmentUtilities.checkNoAppointmentsImpacted( listAppointmentsImpacted, nIdForm, dateOfModification, appointmentForm2 ) );
        cleanUp( nIdForm, appointmentForm, appointmentDTO1, appointmentDTO2 );
        cleanUp( -1, appointmentForm2 );
    }

    public void testCheckNoAppointmentsImpacted2( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( _formStart ) );
        appointmentForm.setDateEndValidity( Date.valueOf( _formEnd ) );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        appointmentForm.setIdForm( nIdForm );
        Slot slot1 = SlotTest.buildSlot( nIdForm, _formStartPlus1.atTime( _timeStart ), _formStartPlus1.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, _formStartPlus3.atTime( _timeStart ), _formStartPlus3.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setDateStartValidity( Date.valueOf( _formStart ) );
        appointmentForm2.setDateEndValidity( Date.valueOf( _formEnd ) );
        appointmentForm2.setIsOpenMonday( Boolean.FALSE );
        appointmentForm2.setIsOpenTuesday( Boolean.FALSE );
        appointmentForm2.setIsOpenWednesday( Boolean.FALSE );
        appointmentForm2.setIsOpenThursday( Boolean.FALSE );
        appointmentForm2.setIsOpenFriday( Boolean.FALSE );
        appointmentForm2.setIsOpenSaturday( Boolean.FALSE );
        appointmentForm2.setIsOpenSunday( Boolean.FALSE );

        LocalDate dateOfModification = _formStartPlus4;
        LocalDateTime endingDateTimeOfSearch = _formEnd.plusYears( 1 ).atTime( _timeEnd );
        List<Slot> listSlotsImpacted = SlotService.findSlotsByIdFormAndDateRange( nIdForm, dateOfModification.atStartOfDay( ), endingDateTimeOfSearch );
        List<Appointment> listAppointmentsImpacted = AppointmentService.findListAppointmentByListSlot( listSlotsImpacted );

        assertTrue( AppointmentUtilities.checkNoAppointmentsImpacted( listAppointmentsImpacted, nIdForm, dateOfModification, appointmentForm ) );
        cleanUp( nIdForm, appointmentForm, appointmentDTO1, appointmentDTO2 );
        cleanUp( -1, appointmentForm2 );
    }

    /**
     * Check that there is no validated appointments on a slot
     */
    public void testCheckNoValidatedAppointmentsOnThisSlot( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( _formStart ) );
        appointmentForm.setDateEndValidity( Date.valueOf( _formEnd ) );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        appointmentForm.setIdForm( nIdForm );

        Slot slot1 = SlotTest.buildSlot( nIdForm, _formStartPlus1.atTime( _timeStart ), _formStartPlus1.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        assertFalse( AppointmentUtilities.checkNoValidatedAppointmentsOnThisSlot( slot1 ) );
        cleanUp( nIdForm, appointmentForm, appointmentDTO1 );
    }

    /**
     * Check that there is no validated appointments on a slot (there is an appointment on the slot but it has been cancelled)
     */
    public void testCheckNoValidatedAppointmentsOnThisSlot2( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( _formStart ) );
        appointmentForm.setDateEndValidity( Date.valueOf( _formEnd ) );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        appointmentForm.setIdForm( nIdForm );

        Slot slot1 = SlotTest.buildSlot( nIdForm, _formStartPlus1.atTime( _timeStart ), _formStartPlus1.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }
        Appointment appointment1 = AppointmentService.findAppointmentById( nIdAppointment1 );
        appointment1.setIsCancelled( true );
        AppointmentService.updateAppointment( appointment1 );

        assertTrue( AppointmentUtilities.checkNoValidatedAppointmentsOnThisSlot( slot1 ) );
        cleanUp( nIdForm, appointmentForm, appointmentDTO1 );
    }

    /**
     * Return the slots impacted by the modification of this time slot
     */
    public void testFindSlotsImpactedByThisTimeSlot( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( _formStart ) );
        appointmentForm.setDateEndValidity( Date.valueOf( _formEnd ) );

        // Build the form
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        appointmentForm.setIdForm( nIdForm );
        // LocalDate resId = _formStart ;
        appointmentForm.setIdReservationRule( ReservationRuleHome.findByIdFormAndDateOfApply( nIdForm, _formStart ).getIdReservationRule( ) );
        appointmentForm.setListWorkingDay( WorkingDayService.findListWorkingDayByWeekDefinitionRule( appointmentForm.getIdReservationRule( ) ) );
        List<WeekDefinition> allWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );

        WeekDefinition weekDefinition = allWeekDefinition.get( 0 );

        LocalDate nextMonday = _formStart.with( TemporalAdjusters.next( DayOfWeek.MONDAY ) );
        Slot slot1 = SlotTest.buildSlot( nIdForm, nextMonday.atTime( _timeStart ), nextMonday.atTime( _timeEnd ), 3, 3, 0, 3, Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        List<WorkingDay> listWorkingDay = appointmentForm.getListWorkingDay( );

        WorkingDay mondayWorkingDay = listWorkingDay.stream( ).filter( w -> w.getDayOfWeek( ) == DayOfWeek.MONDAY.getValue( ) ).findFirst( ).get( );

        TimeSlot timeSlot = TimeSlotTest.buildTimeSlot( _timeStart, _timeEnd, false, 3, mondayWorkingDay.getIdWorkingDay( ) );

        assertEquals( 1, AppointmentUtilities.findSlotsImpactedByThisTimeSlot( timeSlot, nIdForm, weekDefinition.getIdWeekDefinition( ), false ).size( ) );
        cleanUp( nIdForm, appointmentForm );
    }

    public void testSetAppointmentPhoneNumberValuesFromResponse( )
    {
        AppointmentDTO appointment = AppointmentTest.buildAppointmentDTO(
        		1, null, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 1 );

        // Create Entrytype
        EntryType entryTypePhone = new EntryType( );
        entryTypePhone.setBeanName( "appointment.entryTypePhone" );

        EntryType entryTypeText = new EntryType( );
        entryTypeText.setBeanName( "appointment.entryTypeText" );

        // Create Entry 1
        Entry entry01 = new Entry( );
        entry01.setIdEntry( 1 );
        entry01.setEntryType( entryTypePhone );

        // Create Response 1
        Response response01 = new Response();
        response01.setEntry( entry01 );
        response01.setResponseValue( "  value1 " );

        // Create Entry 2
        Entry entry02 = new Entry( );
        entry02.setIdEntry( 2 );
        entry02.setEntryType( entryTypeText );

        // Create Response 2
        Response response02 = new Response();
        response02.setEntry( entry02 );
        response02.setResponseValue( "value2" );

        // Create Entry 3
        Entry entry03 = new Entry( );
        entry03.setIdEntry( 3 );
        entry03.setEntryType( entryTypePhone );

        // Create Response 3
        Response response03 = new Response();
        response03.setEntry( entry03 );
        response03.setResponseValue( null );

        // Create Entry 4
        Entry entry04 = new Entry( );
        entry04.setIdEntry( 4 );
        entry04.setEntryType( entryTypePhone );

        // Create Response 4
        Response response04 = new Response();
        response04.setEntry( entry04 );
        response04.setResponseValue( "value04 " );

        // Add responses to the appointment
        appointment.setListResponse( new ArrayList<Response>( Arrays.asList( response01, response02, response03, response04 ) ) );

        // Set the appointment's phone number
        AppointmentUtilities.setAppointmentPhoneNumberValuesFromResponse( appointment );
        // Test the expected values of the appointment's phone numbers
        assertEquals( "value1, value04", appointment.getPhoneNumber() );
    }

    private void cleanUp( int nIdForm, AppointmentFormDTO formDto, AppointmentDTO... appDtoArray )
    {
        Set<Integer> userDelete = new HashSet<>( );
        for ( AppointmentDTO appDto : appDtoArray )
        {
            User user = UserHome.findByFirstNameLastNameAndEmail( appDto.getFirstName( ), appDto.getLastName( ), appDto.getEmail( ) );
            AppointmentHome.delete( appDto.getIdAppointment( ) );
            if ( user != null )
            {
                userDelete.add( user.getIdUser( ) );
            }
        }
        for ( Integer id : userDelete )
        {
            UserHome.delete( id );
        }
        FormServiceTest.cleanForm( nIdForm );
    }
}
