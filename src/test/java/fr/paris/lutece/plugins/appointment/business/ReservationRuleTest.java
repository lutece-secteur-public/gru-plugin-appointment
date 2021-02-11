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

import java.time.LocalDate;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRuleHome;
import fr.paris.lutece.plugins.appointment.service.ReservationRuleService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the ReservationRule
 *
 * @author Laurent Payen
 *
 */
public final class ReservationRuleTest extends LuteceTestCase
{

    public static final LocalDate DATE_OF_APPLY_1 = LocalDate.parse( "2017-01-27" );
    public static final LocalDate DATE_OF_APPLY_2 = LocalDate.parse( "2017-02-25" );
    public static final int MAX_CAPACITY_PER_SLOT_1 = 1;
    public static final int MAX_CAPACITY_PER_SLOT_2 = 2;
    public static final int MAX_PEOPLE_PER_APPOINTMENT_1 = 1;
    public static final int MAX_PEOPLE_PER_APPOINTMENT_2 = 2;
    private Form form;

    /**
     * Test method for the ReservationRule (CRUD)
     */
    public void testReservationRule( )
    {
        // Initialize a ReservationRule
        ReservationRule reservationRule = buildReservationRule( );
        reservationRule.setIdForm( this.form.getIdForm( ) );
        // Create the ReservationRule in database
        ReservationRuleHome.create( reservationRule );
        // Find the ReservationRule created in database
        ReservationRule reservationRuleStored = ReservationRuleHome.findByPrimaryKey( reservationRule.getIdReservationRule( ) );
        // Check Asserts
        checkAsserts( reservationRuleStored, reservationRule );

        // Update the ReservationRule
        reservationRule.setDateOfApply( DATE_OF_APPLY_2 );
        reservationRule.setMaxCapacityPerSlot( MAX_CAPACITY_PER_SLOT_2 );
        reservationRule.setMaxPeoplePerAppointment( MAX_PEOPLE_PER_APPOINTMENT_2 );
        // Update the ReservationRule in database
        ReservationRuleHome.update( reservationRule );
        // Find the ReservationRule updated in database
        reservationRuleStored = ReservationRuleHome.findByPrimaryKey( reservationRule.getIdReservationRule( ) );
        // Check Asserts
        checkAsserts( reservationRuleStored, reservationRule );

        // Delete the ReservationRule
        ReservationRuleHome.delete( reservationRule.getIdReservationRule( ) );
        reservationRuleStored = ReservationRuleHome.findByPrimaryKey( reservationRule.getIdReservationRule( ) );
        // Check the ReservationRule has been removed from database
        assertNull( reservationRuleStored );
    }

    /**
     * Test delete cascade
     */
    public void testDeleteCascade( )
    {
        // Initialize a ReservationRule
        ReservationRule reservationRule = buildReservationRule( );
        reservationRule.setIdForm( this.form.getIdForm( ) );
        // Create the ReservationRule in database
        ReservationRuleHome.create( reservationRule );
        // Find the ReservationRule created in database
        ReservationRule reservationRuleStored = ReservationRuleHome.findByPrimaryKey( reservationRule.getIdReservationRule( ) );
        assertNotNull( reservationRuleStored );
        // Delete the form and by cascade the ReservationRule
        FormHome.delete( this.form.getIdForm( ) );
        reservationRuleStored = ReservationRuleHome.findByPrimaryKey( reservationRule.getIdReservationRule( ) );
        // Check the ReservationRule has been removed from database
        assertNull( reservationRuleStored );
    }

    /**
     * Test of findByIdForm method
     */
    public void testFindByIdForm( )
    {
        // Initialize a ReservationRule
        ReservationRule reservationRule = buildReservationRule( );
        reservationRule.setIdForm( this.form.getIdForm( ) );
        // Create the ReservationRule in database
        ReservationRuleHome.create( reservationRule );
        // Find the ReservationRule created in database
        List<ReservationRule> listReservationRuleStored = ReservationRuleHome.findByIdForm( this.form.getIdForm( ) );
        // Check Asserts
        assertEquals( listReservationRuleStored.size( ), 1 );
        checkAsserts( listReservationRuleStored.get( 0 ), reservationRule );

    }

    /**
     * Test of findByIdFormAndDateOfApply method
     */
    public void findByIdFormAndDateOfApply( )
    {
        // Initialize a ReservationRule
        ReservationRule reservationRule1 = buildReservationRule( );
        reservationRule1.setIdForm( this.form.getIdForm( ) );
        // Create the ReservationRule in database
        ReservationRuleHome.create( reservationRule1 );
        // Find the ReservationRule created in database
        ReservationRule reservationRuleStored = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( this.form.getIdForm( ), DATE_OF_APPLY_1 );
        // Check Asserts
        checkAsserts( reservationRuleStored, reservationRule1 );
    }

    /**
     * Build a ReservationRule Business Object
     *
     * @return the reservationRule
     */
    public ReservationRule buildReservationRule( )
    {
        ReservationRule reservationRule = new ReservationRule( );
        reservationRule.setDateOfApply( DATE_OF_APPLY_1 );
        reservationRule.setMaxCapacityPerSlot( MAX_CAPACITY_PER_SLOT_1 );
        reservationRule.setMaxPeoplePerAppointment( MAX_PEOPLE_PER_APPOINTMENT_1 );
        return reservationRule;
    }

    /**
     * Build a ReservationRule Business Object
     *
     * @return the reservationRule
     */
    public ReservationRule buildReservationRule2( )
    {
        ReservationRule reservationRule = new ReservationRule( );
        reservationRule.setDateOfApply( DATE_OF_APPLY_2 );
        reservationRule.setMaxCapacityPerSlot( MAX_CAPACITY_PER_SLOT_2 );
        reservationRule.setMaxPeoplePerAppointment( MAX_PEOPLE_PER_APPOINTMENT_2 );
        return reservationRule;
    }

    /**
     * Check that all the asserts are true
     *
     * @param reservationRuleStored
     *            the ReservationRule stored
     * @param reservationRule
     *            the ReservationRule created
     */
    public void checkAsserts( ReservationRule reservationRuleStored, ReservationRule reservationRule )
    {
        assertEquals( reservationRuleStored.getDateOfApply( ), reservationRule.getDateOfApply( ) );
        assertEquals( reservationRuleStored.getMaxCapacityPerSlot( ), reservationRule.getMaxCapacityPerSlot( ) );
        assertEquals( reservationRuleStored.getMaxPeoplePerAppointment( ), reservationRule.getMaxPeoplePerAppointment( ) );
        assertEquals( reservationRuleStored.getIdForm( ), reservationRule.getIdForm( ) );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.form = FormTest.buildForm1( );
        FormHome.create( this.form );

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        //delete all the forms left over from tests
        for (Form f : FormHome.findAllForms()) {
            FormHome.delete(f.getIdForm());
        }
        this.form = null;
    }

}