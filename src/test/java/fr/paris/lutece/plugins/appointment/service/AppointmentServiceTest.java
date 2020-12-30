/*
 * Copyright (c) 2002-2020, City of Paris
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import fr.paris.lutece.plugins.appointment.business.AppointmentTest;
import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the Appointment Service
 * 
 * @author Laurent Payen
 *
 */
public class AppointmentServiceTest extends LuteceTestCase
{

    private LocalTime _timeStart = LocalTime.of( 10, 0 );
    private LocalTime _timeEnd = LocalTime.of( 10, 30 );
    private LocalDateTime _slotStart = LocalDate.now( ).plusDays( 1 ).atTime( _timeStart );
    private LocalDateTime _slotEnd = LocalDate.now( ).plusDays( 1 ).atTime( _timeEnd );

    public void testAppointmentAndNbRemainingPlaces( )
    {

        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        app.setName("appointment_form");
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 1, 1, 0, 1, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "mdp@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 1 );
        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        cleanUp( nIdForm, app, appointmentDTO );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbRemainingPlaces2( )
    {

        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 2, 2, 0, 2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "mdp@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 0, slot.getNbRemainingPlaces( ) );
        cleanUp( nIdForm, app, appointmentDTO );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbRemainingPlaces3( )
    {

        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 2, 2, 0, 2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "mdp@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 1 );
        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 1, slot.getNbRemainingPlaces( ) );
        cleanUp( nIdForm, app, appointmentDTO );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPotentialRemainingPlaces( )
    {

        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 1, 1, 0, 1, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "mdp@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 1 );
        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        cleanUp( nIdForm, app, appointmentDTO );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPotentialRemainingPlaces2( )
    {

        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 2, 2, 0, 2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "mdp@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );

        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        cleanUp( nIdForm, app, appointmentDTO );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPotentialRemainingPlaces3( )
    {

        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 2, 2, 0, 2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "mdp@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 1 );
        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 1, slot.getNbPotentialRemainingPlaces( ) );
        cleanUp( nIdForm, app, appointmentDTO );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPlacesTaken( )
    {

        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 1, 1, 0, 1, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "mdp@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 1 );
        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 1, slot.getNbPlacesTaken( ) );
        cleanUp( nIdForm, app, appointmentDTO );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPlacesTaken2( )
    {

        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 2, 2, 0, 2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "mdp@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 1 );
        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 1, slot.getNbPlacesTaken( ) );
        cleanUp( nIdForm, app, appointmentDTO );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPlacesTaken3( )
    {

        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 2, 2, 0, 2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "mdp@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 2, slot.getNbPlacesTaken( ) );
        cleanUp( nIdForm, app, appointmentDTO );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testMultipleAppointmentsOnSameSlot( )
    {

        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 2, 2, 0, 2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 1 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "gerard.durand@mdp.fr", "Gérard", "Durand", _timeStart, _timeEnd,
                1 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 2, slot.getNbPlacesTaken( ) );
        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 0, slot.getNbRemainingPlaces( ) );
        cleanUp( nIdForm, app, appointmentDTO1, appointmentDTO2 );
    }

    public void testMultipleAppointmentsOnSameSlot2( )
    {

        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 3, 3, 0, 3, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 1 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "gerard.durand@mdp.fr", "Gérard", "Durand", _timeStart, _timeEnd,
                1 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 2, slot.getNbPlacesTaken( ) );
        assertEquals( 1, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 1, slot.getNbRemainingPlaces( ) );
        cleanUp( nIdForm, app, appointmentDTO1, appointmentDTO2 );
    }

    public void testMultipleAppointmentsOnSameSlot3( )
    {

        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 3, 3, 0, 3, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 1 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "gerard.durand@mdp.fr", "Gérard", "Durand", _timeStart, _timeEnd,
                2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 3, slot.getNbPlacesTaken( ) );
        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 0, slot.getNbRemainingPlaces( ) );
        cleanUp( nIdForm, app, appointmentDTO1, appointmentDTO2 );
    }

    public void testRemoveAppointmentAndCheckNbRemainingPlaces( )
    {
        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 1, 1, 0, 1, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 1 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 1, slot.getNbPlacesTaken( ) );
        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        AppointmentService.deleteAppointment( nIdAppointment1 );

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 0, slot.getNbPlacesTaken( ) );
        assertEquals( 1, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 1, slot.getNbRemainingPlaces( ) );
        cleanUp( nIdForm, app, appointmentDTO1 );
    }

    public void testRemoveAppointmentAndCheckNbRemainingPlaces2( )
    {
        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 2, 2, 0, 2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 1 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 1, slot.getNbPlacesTaken( ) );
        assertEquals( 1, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 1, slot.getNbRemainingPlaces( ) );

        AppointmentService.deleteAppointment( nIdAppointment1 );

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 0, slot.getNbPlacesTaken( ) );
        assertEquals( 2, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 2, slot.getNbRemainingPlaces( ) );
        cleanUp( nIdForm, app, appointmentDTO1 );
    }

    public void testRemoveAppointmentAndCheckNbRemainingPlaces3( )
    {
        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 2, 2, 0, 2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 1 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "gerard.durand@mdp.fr", "Gérard", "Durand", _timeStart, _timeEnd,
                1 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 2, slot.getNbPlacesTaken( ) );
        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        AppointmentService.deleteAppointment( nIdAppointment1 );

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 1, slot.getNbPlacesTaken( ) );
        assertEquals( 1, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 1, slot.getNbRemainingPlaces( ) );
        cleanUp( nIdForm, app, appointmentDTO1, appointmentDTO2 );
    }

    public void testRemoveAppointmentAndCheckNbRemainingPlaces4( )
    {
        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 3, 3, 0, 3, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "gerard.durand@mdp.fr", "Gérard", "Durand", _timeStart, _timeEnd,
                1 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 3, slot.getNbPlacesTaken( ) );
        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        AppointmentService.deleteAppointment( nIdAppointment1 );

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 1, slot.getNbPlacesTaken( ) );
        assertEquals( 2, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 2, slot.getNbRemainingPlaces( ) );
        cleanUp( nIdForm, app, appointmentDTO1, appointmentDTO2 );
    }

    public void testCancelAppointment( )
    {
        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 2, 2, 0, 2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 1 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 1, slot.getNbPlacesTaken( ) );
        assertEquals( 1, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 1, slot.getNbRemainingPlaces( ) );

        Appointment appointmentToCancel = AppointmentService.findAppointmentById( nIdAppointment1 );
        appointmentToCancel.setIsCancelled( true );
        AppointmentService.updateAppointment( appointmentToCancel );

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 0, slot.getNbPlacesTaken( ) );
        assertEquals( 2, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 2, slot.getNbRemainingPlaces( ) );
        cleanUp( nIdForm, app, appointmentDTO1 );

    }

    public void testCancelAppointment2( )
    {
        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 2, 2, 0, 2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 1 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "gerard.durand@mdp.fr", "Gérard", "Durand", _timeStart, _timeEnd,
                1 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 2, slot.getNbPlacesTaken( ) );
        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        Appointment appointmentToCancel = AppointmentService.findAppointmentById( nIdAppointment1 );
        appointmentToCancel.setIsCancelled( true );
        AppointmentService.updateAppointment( appointmentToCancel );

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 1, slot.getNbPlacesTaken( ) );
        assertEquals( 1, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 1, slot.getNbRemainingPlaces( ) );
        cleanUp( nIdForm, app, appointmentDTO1, appointmentDTO2 );

    }

    public void testCancelAppointment3( )
    {
        // Build the form
        AppointmentFormDTO app = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( app );

        Slot slot = SlotTest.buildSlot( nIdForm, _slotStart, _slotEnd, 3, 3, 0, 3, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "jean.dupont@mdp.fr", "Jean", "Dupont", _timeStart, _timeEnd, 2 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "gerard.durand@mdp.fr", "Gérard", "Durand", _timeStart, _timeEnd,
                1 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 3, slot.getNbPlacesTaken( ) );
        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        Appointment appointmentToCancel = AppointmentService.findAppointmentById( nIdAppointment1 );
        appointmentToCancel.setIsCancelled( true );
        AppointmentService.updateAppointment( appointmentToCancel );

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 1, slot.getNbPlacesTaken( ) );
        assertEquals( 2, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 2, slot.getNbRemainingPlaces( ) );
        cleanUp( nIdForm, app, appointmentDTO1, appointmentDTO2 );

    }

    private void cleanUp( int nIdForm, AppointmentFormDTO formDto, AppointmentDTO... appDtoArray )
    {
        Set<Integer> userToDelete = new HashSet<>( );
        for ( AppointmentDTO appDto : appDtoArray )
        {
            AppointmentHome.delete( appDto.getIdAppointment( ) );
            User user = UserHome.findByFirstNameLastNameAndEmail( appDto.getFirstName( ), appDto.getLastName( ), appDto.getEmail( ) );
            if ( user != null )
            {
                userToDelete.add( user.getIdUser( ) );
            }
        }
        for ( Integer id : userToDelete )
        {
            UserHome.delete( id );
        }
        FormServiceTest.cleanForm( nIdForm );
    }
}
