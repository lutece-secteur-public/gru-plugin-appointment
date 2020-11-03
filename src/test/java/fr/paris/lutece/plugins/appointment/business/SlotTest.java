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
package fr.paris.lutece.plugins.appointment.business;

import java.time.LocalDateTime;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.slot.SlotHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test class for the Slot
 * 
 * @author Laurent Payen
 *
 */
public final class SlotTest extends LuteceTestCase
{

    /**
     * Test method for the Slot (CRUD)
     */
    public void testSlot( )
    {
        Form form = FormTest.buildForm1( );
        FormHome.create( form );

        // Initialize a Slot
        Slot slot = buildSlot( form.getIdForm( ), Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_1,
                Constants.NB_REMAINING_PLACES_1, 0, Constants.NB_REMAINING_PLACES_1, Boolean.TRUE, Boolean.TRUE );
        // Create the Slot in database
        SlotHome.create( slot );
        // Find the Slot created in database
        Slot slotStored = SlotHome.findByPrimaryKey( slot.getIdSlot( ) );
        // Check Asserts
        checkAsserts( slotStored, slot );

        // Update the Slot
        slot.setStartingDateTime( Constants.STARTING_DATE_2 );
        slot.setEndingDateTime( Constants.ENDING_DATE_2 );
        slot.setIsOpen( Constants.IS_OPEN_2 );
        slot.setNbRemainingPlaces( Constants.NB_REMAINING_PLACES_2 );
        // Update the Slot in database
        SlotHome.update( slot );
        // Find the Slot updated in database
        slotStored = SlotHome.findByPrimaryKey( slot.getIdSlot( ) );
        // Check Asserts
        checkAsserts( slotStored, slot );

        // Delete the Slot
        SlotHome.delete( slot.getIdSlot( ) );
        slotStored = SlotHome.findByPrimaryKey( slot.getIdSlot( ) );
        // Check the Slot has been removed from database
        assertNull( slotStored );

        // Clean
        FormHome.delete( form.getIdForm( ) );
    }

    /**
     * Test of findByIdFormAndDateRange
     */
    public void testFindByIdFormAndDateRange( )
    {
        Form form = FormTest.buildForm1( );
        FormHome.create( form );

        // Initialize a first Slot that matches
        Slot slot1 = buildSlot( form.getIdForm( ), Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_1,
                Constants.NB_REMAINING_PLACES_1, 0, Constants.NB_REMAINING_PLACES_1, Boolean.TRUE, Boolean.TRUE );
        // Create the Slot in database
        SlotHome.create( slot1 );

        // Initialize a second slot that doesn't matche
        Slot slot2 = buildSlot( form.getIdForm( ), Constants.STARTING_DATE_2, Constants.ENDING_DATE_2, Constants.NB_REMAINING_PLACES_2,
                Constants.NB_REMAINING_PLACES_2, 0, Constants.NB_REMAINING_PLACES_2, Boolean.TRUE, Boolean.TRUE );
        // Create the Slot in database
        SlotHome.create( slot2 );

        // Find the Slot created in database
        List<Slot> listSlotStored = SlotHome.findByIdFormAndDateRange( form.getIdForm( ), Constants.STARTING_DATE_1, Constants.ENDING_DATE_1 );
        assertEquals( listSlotStored.size( ), 1 );

        // Clean
        SlotHome.delete( slot1.getIdSlot( ) );
        SlotHome.delete( slot2.getIdSlot( ) );
        FormHome.delete( form.getIdForm( ) );
    }

    /**
     * Test of findOpenSlotsByIdFormAndDateRange
     */
    public void testFindOpenSlotsByIdFormAndDateRange( )
    {
        Form form = FormTest.buildForm1( );
        FormHome.create( form );

        // Initialize a first Slot
        Slot slot1 = buildSlot( form.getIdForm( ), Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_1,
                Constants.NB_REMAINING_PLACES_1, 0, Constants.NB_REMAINING_PLACES_1, Boolean.TRUE, Boolean.TRUE );
        // Create the Slot in database
        SlotHome.create( slot1 );

        // Initialize a second Slot closed
        Slot slotClosed = buildSlot( form.getIdForm( ), Constants.STARTING_DATE_2, Constants.ENDING_DATE_2, Constants.NB_REMAINING_PLACES_2,
                Constants.NB_REMAINING_PLACES_2, 0, Constants.NB_REMAINING_PLACES_2, Boolean.FALSE, Boolean.TRUE );
        // Create the Slot in database
        SlotHome.create( slotClosed );
        // Find the Slot created in database
        List<Slot> listSlotStored = SlotHome.findOpenSlotsByIdFormAndDateRange( form.getIdForm( ), Constants.STARTING_DATE_1, Constants.ENDING_DATE_2 );
        assertEquals( listSlotStored.size( ), 1 );

        // Clean
        SlotHome.delete( slot1.getIdSlot( ) );
        SlotHome.delete( slotClosed.getIdSlot( ) );
        FormHome.delete( form.getIdForm( ) );
    }

    /**
     * Test of FindOpenSlotsByIdForm
     */
    public void testFindOpenSlotsByIdForm( )
    {
        Form form = FormTest.buildForm1( );
        FormHome.create( form );

        // Initialize a Slot
        Slot slot1 = buildSlot( form.getIdForm( ), Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_1,
                Constants.NB_REMAINING_PLACES_1, 0, Constants.NB_REMAINING_PLACES_1, Boolean.TRUE, Boolean.TRUE );
        // Create the Slot in database
        SlotHome.create( slot1 );

        // Initialize a 2nd Slot
        Slot slot2 = buildSlot( form.getIdForm( ), Constants.STARTING_DATE_2, Constants.ENDING_DATE_2, Constants.NB_REMAINING_PLACES_2,
                Constants.NB_REMAINING_PLACES_2, 0, Constants.NB_REMAINING_PLACES_2, Boolean.TRUE, Boolean.TRUE );
        // Create the Slot in database
        SlotHome.create( slot2 );

        // Initialize a 3th slot closed
        Slot slot3 = buildSlot( form.getIdForm( ), Constants.STARTING_DATE_3, Constants.ENDING_DATE_3, Constants.NB_REMAINING_PLACES_3,
                Constants.NB_REMAINING_PLACES_3, 0, Constants.NB_REMAINING_PLACES_3, Boolean.FALSE, Boolean.TRUE );

        // Create the Slot in database
        SlotHome.create( slot3 );

        // Find the Slot created in database
        List<Slot> listSlotStored = SlotHome.findOpenSlotsByIdForm( form.getIdForm( ) );
        assertEquals( listSlotStored.size( ), 2 );

        // Clean
        SlotHome.delete( slot1.getIdSlot( ) );
        SlotHome.delete( slot2.getIdSlot( ) );
        SlotHome.delete( slot3.getIdSlot( ) );
        FormHome.delete( form.getIdForm( ) );

    }

    /**
     * Build a SLot Business Object
     * 
     * @return a slot
     */
    public static Slot buildSlot( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime, int nbRemainingPlaces,
            int nbPotentialRemaningPlaces, int nbPlacesTaken, int nMaxCapacity, boolean isOpen, boolean isSpecific )
    {
        Slot slot = new Slot( );
        slot.setIdForm( nIdForm );
        slot.setMaxCapacity( nMaxCapacity );
        slot.setStartingDateTime( startingDateTime );
        slot.setEndingDateTime( endingDateTime );
        slot.setIsOpen( isOpen );
        slot.setNbRemainingPlaces( nbRemainingPlaces );
        slot.setNbPotentialRemainingPlaces( nbPotentialRemaningPlaces );
        slot.setNbPlacestaken( nbPlacesTaken );
        slot.setIsSpecific( isSpecific );
        return slot;
    }

    /**
     * Check that all the asserts are true
     * 
     * @param slotStored
     *            the Slot stored
     * @param slot
     *            the Slot created
     */
    public void checkAsserts( Slot slotStored, Slot slot )
    {
        assertEquals( slotStored.getStartingDateTime( ), slot.getStartingDateTime( ) );
        assertEquals( slotStored.getEndingDateTime( ), slot.getEndingDateTime( ) );
        assertEquals( slotStored.getIsOpen( ), slot.getIsOpen( ) );
        assertEquals( slotStored.getNbRemainingPlaces( ), slot.getNbRemainingPlaces( ) );
        assertEquals( slotStored.getIdForm( ), slot.getIdForm( ) );
    }

}
