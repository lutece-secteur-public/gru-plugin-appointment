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
    private  int nIdForm;

    public void testAppointmentAndNbRemainingPlaces( )
    {

        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_1,
                Constants.NB_REMAINING_PLACES_1, 0, Constants.NB_REMAINING_PLACES_1, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_1, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1);
        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbRemainingPlaces2( )
    {

        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_2, Constants.NB_REMAINING_PLACES_2,
                0, Constants.NB_REMAINING_PLACES_2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_1, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbRemainingPlaces3( )
    {

        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_2, Constants.NB_REMAINING_PLACES_2,
                0, Constants.NB_REMAINING_PLACES_2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_1, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 1, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPotentialRemainingPlaces( )
    {


        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_1, Constants.NB_REMAINING_PLACES_1,
                0, Constants.NB_REMAINING_PLACES_1, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_1, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPotentialRemainingPlaces2( )
    {

        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_2, Constants.NB_REMAINING_PLACES_2,
                0, Constants.NB_REMAINING_PLACES_2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_1, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );

        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPotentialRemainingPlaces3( )
    {

        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_2, Constants.NB_REMAINING_PLACES_2,
                0, Constants.NB_REMAINING_PLACES_2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_1, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 1, slot.getNbPotentialRemainingPlaces( ) );

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPlacesTaken( )
    {

        Slot slot = SlotTest.buildSlot( nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_1, Constants.NB_REMAINING_PLACES_1,
                0, Constants.NB_REMAINING_PLACES_1, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_1, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 1, slot.getNbPlacesTaken( ) );

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPlacesTaken2( )
    {

        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_2, Constants.NB_REMAINING_PLACES_2,
                0, Constants.NB_REMAINING_PLACES_2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_1, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 1, slot.getNbPlacesTaken( ) );

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPlacesTaken3( )
    {

        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_2, Constants.NB_REMAINING_PLACES_2,
                0, Constants.NB_REMAINING_PLACES_2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_1, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        int nIdAppointment = -1;
        try
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 2, slot.getNbPlacesTaken( ) );

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testMultipleAppointmentsOnSameSlot( )
    {


        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_2, Constants.NB_REMAINING_PLACES_2,
                0, Constants.NB_REMAINING_PLACES_2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_1, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_2, Constants.FIRST_NAME_2, Constants.LAST_NAME_2, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment2 = -1;
        try
        {
            nIdAppointment2 = AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }


        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 2, slot.getNbPlacesTaken( ) );
        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
    }

    public void testMultipleAppointmentsOnSameSlot2( )
    {


        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 =  AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_4, Constants.FIRST_NAME_2, Constants.LAST_NAME_2, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment2 = -1;
        try
        {
            nIdAppointment2 = AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 2, slot.getNbPlacesTaken( ) );
        assertEquals( 1, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 1, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment2 ) );
    }

    public void testMultipleAppointmentsOnSameSlot3( )
    {


        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot,  Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_4, Constants.FIRST_NAME_2, Constants.LAST_NAME_2, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_2 );
        int nIdAppointment2 = -1;
        try
        {
            nIdAppointment2 = AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 3, slot.getNbPlacesTaken( ) );
        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment2 ) );
    }

    public void testRemoveAppointmentAndCheckNbRemainingPlaces( )
    {
        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_1, Constants.NB_REMAINING_PLACES_1,
                0, Constants.NB_REMAINING_PLACES_1, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
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

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
    }

    public void testRemoveAppointmentAndCheckNbRemainingPlaces2( )
    {

        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_2, Constants.NB_REMAINING_PLACES_2,
                0, Constants.NB_REMAINING_PLACES_2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
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

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
    }

    public void testRemoveAppointmentAndCheckNbRemainingPlaces3( )
    {

        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_2, Constants.NB_REMAINING_PLACES_2,
                0, Constants.NB_REMAINING_PLACES_2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment1 = -1 ;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_4, Constants.FIRST_NAME_2, Constants.LAST_NAME_2, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment2 = -1;
        try
        {
            nIdAppointment2 = AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e)
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

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment2 ) );
    }

    public void testRemoveAppointmentAndCheckNbRemainingPlaces4( )
    {

        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
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

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_4, Constants.FIRST_NAME_2, Constants.LAST_NAME_2, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment2 = -1;
        try
        {
            nIdAppointment2 = AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e)
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

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment2 ) );
    }

    public void testCancelAppointment( )
    {

        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_2, Constants.NB_REMAINING_PLACES_2,
                0, Constants.NB_REMAINING_PLACES_2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
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

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );

    }

    public void testCancelAppointment2( )
    {

        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_2, Constants.NB_REMAINING_PLACES_2,
                0, Constants.NB_REMAINING_PLACES_2, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_4, Constants.FIRST_NAME_2, Constants.LAST_NAME_2, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment2 = -1;
        try
        {
            nIdAppointment2 = AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e)
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

        FormService.removeForm( this.nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment2 ) );

    }

    public void testCancelAppointment3( )
    {

        Slot slot = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_3, Constants.NB_REMAINING_PLACES_3,
                0, Constants.NB_REMAINING_PLACES_3, Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_3, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.STARTING_TIME_1,
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

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, Constants.EMAIL_4, Constants.FIRST_NAME_2, Constants.LAST_NAME_2, Constants.STARTING_TIME_1,
                Constants.ENDING_TIME_1, Constants.NB_REMAINING_PLACES_1 );
        int nIdAppointment2 = -1;
        try
        {
            nIdAppointment2 =  AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e)
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

        FormService.removeForm( this.nIdForm );

        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment2 ) );

    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.nIdForm = 0;
    }
}