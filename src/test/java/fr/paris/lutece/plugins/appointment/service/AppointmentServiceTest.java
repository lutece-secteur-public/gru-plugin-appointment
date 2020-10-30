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

import java.time.LocalDateTime;
import java.time.LocalTime;

import fr.paris.lutece.plugins.appointment.business.AppointmentTest;
import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the Appointment Service
 * 
 * @author Laurent Payen
 *
 */
public class AppointmentServiceTest extends LuteceTestCase
{

    public void testAppointmentAndNbRemainingPlaces( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 1, 1, 0, 1, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
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

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbRemainingPlaces2( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 2, 2, 0, 2, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
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

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbRemainingPlaces3( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 2, 2, 0, 2, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
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

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPotentialRemainingPlaces( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 1, 1, 0, 1, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
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

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPotentialRemainingPlaces2( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 2, 2, 0, 2, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );

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

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPotentialRemainingPlaces3( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 2, 2, 0, 2, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
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

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPlacesTaken( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 1, 1, 0, 1, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
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

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPlacesTaken2( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 2, 2, 0, 2, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
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

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPlacesTaken3( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 2, 2, 0, 2, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
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

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testMultipleAppointmentsOnSameSlot( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 2, 2, 0, 2, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, "gerard.durand@mdp.fr", "Gérard", "Durand", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
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

        FormService.removeForm( nIdForm );
    }

    public void testMultipleAppointmentsOnSameSlot2( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, "gerard.durand@mdp.fr", "Gérard", "Durand", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
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

        FormService.removeForm( nIdForm );
    }

    public void testMultipleAppointmentsOnSameSlot3( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, "gerard.durand@mdp.fr", "Gérard", "Durand", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
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

        FormService.removeForm( nIdForm );
    }

    public void testRemoveAppointmentAndCheckNbRemainingPlaces( )
    {
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 1, 1, 0, 1, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
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

        FormService.removeForm( nIdForm );
    }

    public void testRemoveAppointmentAndCheckNbRemainingPlaces2( )
    {
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 2, 2, 0, 2, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
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

        FormService.removeForm( nIdForm );
    }

    public void testRemoveAppointmentAndCheckNbRemainingPlaces3( )
    {
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 2, 2, 0, 2, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment1 = -1;
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, "gerard.durand@mdp.fr", "Gérard", "Durand", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
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

        FormService.removeForm( nIdForm );
    }

    public void testRemoveAppointmentAndCheckNbRemainingPlaces4( )
    {
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        int nIdAppointment1 = -1;
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, "gerard.durand@mdp.fr", "Gérard", "Durand", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
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

        FormService.removeForm( nIdForm );
    }

    public void testCancelAppointment( )
    {
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 2, 2, 0, 2, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment1 = -1;
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
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

        FormService.removeForm( nIdForm );

    }

    public void testCancelAppointment2( )
    {
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 2, 2, 0, 2, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment1 = -1;
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, "gerard.durand@mdp.fr", "Gérard", "Durand", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
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

        FormService.removeForm( nIdForm );

    }

    public void testCancelAppointment3( )
    {
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-12-03T10:00" ), LocalDateTime.parse( "2018-12-03T10:30" ), 3, 3, 0, 3, Boolean.TRUE,
                Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        int nIdAppointment1 = -1;
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, "gerard.durand@mdp.fr", "Gérard", "Durand", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
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

        FormService.removeForm( nIdForm );

    }

}
