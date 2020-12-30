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

import java.time.LocalDate;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlotHome;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinitionHome;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDayHome;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRuleHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the Form
 * 
 * @author Laurent Payen
 *
 */
public final class FormTest extends LuteceTestCase
{

    public final static String TITLE_FORM_1 = "TitreForm1";
    public final static String TITLE_FORM_2 = "TitreForm2";
    public final static String DESCRIPTION_FORM_1 = "DescriptionForm1";
    public final static String DESCRIPTION_FORM_2 = "DescriptionForm2";
    public final static LocalDate STARTING_VALIDITY_DATE_1 = LocalDate.parse( "2017-01-24" );
    public final static LocalDate STARTING_VALIDITY_DATE_2 = LocalDate.parse( "2017-01-25" );
    public final static LocalDate ENDING_VALIDITY_DATE_1 = LocalDate.parse( "2017-02-28" );
    public final static LocalDate ENDING_VALIDITY_DATE_2 = LocalDate.parse( "2017-03-01" );
    public final static boolean IS_ACTIVE1 = true;
    public final static boolean IS_ACTIVE2 = false;
    public final static int ID_WORKFLOW_1 = 1;
    public final static int ID_WORKFLOW_2 = 2;

    /**
     * Test method for the Form (CRUD)
     */
    public void testForm( )
    {
        // Initialize a Form
        Form form = buildForm1( );
        // Create the Form in database
        FormHome.create( form );
        // Find the Form created in database
        Form formStored = FormHome.findByPrimaryKey( form.getIdForm( ) );
        // Check Asserts
        checkAsserts( formStored, form );

        // Update the form
        form.setTitle( TITLE_FORM_2 );
        form.setDescription( DESCRIPTION_FORM_2 );
        form.setStartingValidityDate( STARTING_VALIDITY_DATE_2 );
        form.setEndingValidityDate( ENDING_VALIDITY_DATE_2 );
        form.setIsActive( IS_ACTIVE2 );
        form.setIdWorkflow( ID_WORKFLOW_2 );
        // Update the form in database
        FormHome.update( form );
        // Find the form updated in database
        formStored = FormHome.findByPrimaryKey( form.getIdForm( ) );
        // Check Asserts
        checkAsserts( formStored, form );

        // Delete the form
        FormHome.delete( form.getIdForm( ) );
        formStored = FormHome.findByPrimaryKey( form.getIdForm( ) );
        // Check the form has been removed from database
        assertNull( formStored );
    }

    /**
     * 
     */
    public void testWeekDefinition( )
    {
        Form form = buildForm1( );
        FormHome.create( form );
        
        ReservationRule reservationRule = buildReservationRule( form.getIdForm() );
        ReservationRuleHome.create( reservationRule );
        
        ReservationRule reservationRule2 = buildReservationRule( form.getIdForm() );
        ReservationRuleHome.create( reservationRule2 );

        WeekDefinition weekDefinition1 = new WeekDefinition( );
        weekDefinition1.setDateOfApply( WeekDefinitionTest.DATE_OF_APPLY_1 );
        weekDefinition1.setEndingDateOfApply( WeekDefinitionTest.DATE_OF_APPLY_2 );
        weekDefinition1.setIdReservationRule( reservationRule.getIdReservationRule( ) );
        WeekDefinitionHome.create( weekDefinition1 );

        WorkingDay workingDay1 = new WorkingDay( );
        workingDay1.setDayOfWeek( WorkingDayTest.DAY_OF_WEEK_1 );
        workingDay1.setIdReservationRule( reservationRule.getIdReservationRule( ) );
        WorkingDayHome.create( workingDay1 );

        TimeSlot timeSlot1 = new TimeSlot( );
        timeSlot1.setStartingTime( TimeSlotTest.STARTING_TIME_1 );
        timeSlot1.setEndingTime( TimeSlotTest.ENDING_TIME_1 );
        timeSlot1.setIsOpen( TimeSlotTest.IS_OPEN_1 );
        timeSlot1.setIdWorkingDay( workingDay1.getIdWorkingDay( ) );
        TimeSlotHome.create( timeSlot1 );

        TimeSlot timeSlot2 = new TimeSlot( );
        timeSlot2.setStartingTime( TimeSlotTest.STARTING_TIME_2 );
        timeSlot2.setEndingTime( TimeSlotTest.ENDING_TIME_2 );
        timeSlot2.setIsOpen( TimeSlotTest.IS_OPEN_2 );
        timeSlot2.setIdWorkingDay( workingDay1.getIdWorkingDay( ) );
        TimeSlotHome.create( timeSlot2 );

        WorkingDay workingDay2 = new WorkingDay( );
        workingDay2.setDayOfWeek( WorkingDayTest.DAY_OF_WEEK_2 );
        workingDay2.setIdReservationRule( reservationRule.getIdReservationRule( ) );
        WorkingDayHome.create( workingDay2 );

        TimeSlot timeSlot3 = new TimeSlot( );
        timeSlot3.setStartingTime( TimeSlotTest.STARTING_TIME_1 );
        timeSlot3.setEndingTime( TimeSlotTest.ENDING_TIME_1 );
        timeSlot3.setIsOpen( TimeSlotTest.IS_OPEN_1 );
        timeSlot3.setIdWorkingDay( workingDay2.getIdWorkingDay( ) );
        TimeSlotHome.create( timeSlot3 );

        TimeSlot timeSlot4 = new TimeSlot( );
        timeSlot4.setStartingTime( TimeSlotTest.STARTING_TIME_2 );
        timeSlot4.setEndingTime( TimeSlotTest.ENDING_TIME_2 );
        timeSlot4.setIsOpen( TimeSlotTest.IS_OPEN_2 );
        timeSlot4.setIdWorkingDay( workingDay2.getIdWorkingDay( ) );
        TimeSlotHome.create( timeSlot4 );

        WeekDefinition weekDefinition2 = new WeekDefinition( );
        weekDefinition2.setIdWeekDefinition( 0 );
        weekDefinition2.setDateOfApply( WeekDefinitionTest.DATE_OF_APPLY_2 );
        weekDefinition2.setEndingDateOfApply( WeekDefinitionTest.DATE_OF_APPLY_2 );
        weekDefinition2.setIdReservationRule( reservationRule.getIdReservationRule( ) );
        WeekDefinitionHome.create( weekDefinition2 );

        WorkingDay workingDay3 = new WorkingDay( );
        workingDay3.setDayOfWeek( WorkingDayTest.DAY_OF_WEEK_1 );
        workingDay3.setIdReservationRule( reservationRule2.getIdReservationRule( ) );
        WorkingDayHome.create( workingDay3 );

        TimeSlot timeSlot5 = new TimeSlot( );
        timeSlot5.setStartingTime( TimeSlotTest.STARTING_TIME_1 );
        timeSlot5.setEndingTime( TimeSlotTest.ENDING_TIME_1 );
        timeSlot5.setIsOpen( TimeSlotTest.IS_OPEN_1 );
        timeSlot5.setIdWorkingDay( workingDay3.getIdWorkingDay( ) );
        TimeSlotHome.create( timeSlot5 );

        TimeSlot timeSlot6 = new TimeSlot( );
        timeSlot6.setStartingTime( TimeSlotTest.STARTING_TIME_2 );
        timeSlot6.setEndingTime( TimeSlotTest.ENDING_TIME_2 );
        timeSlot6.setIsOpen( TimeSlotTest.IS_OPEN_2 );
        timeSlot6.setIdWorkingDay( workingDay3.getIdWorkingDay( ) );
        TimeSlotHome.create( timeSlot6 );

        WorkingDay workingDay4 = new WorkingDay( );
        workingDay4.setDayOfWeek( WorkingDayTest.DAY_OF_WEEK_2 );
        workingDay4.setIdReservationRule( reservationRule2.getIdReservationRule( ) );
        WorkingDayHome.create( workingDay4 );

        TimeSlot timeSlot7 = new TimeSlot( );
        timeSlot7.setStartingTime( TimeSlotTest.STARTING_TIME_1 );
        timeSlot7.setEndingTime( TimeSlotTest.ENDING_TIME_1 );
        timeSlot7.setIsOpen( TimeSlotTest.IS_OPEN_1 );
        timeSlot7.setIdWorkingDay( workingDay4.getIdWorkingDay( ) );
        TimeSlotHome.create( timeSlot7 );

        TimeSlot timeSlot8 = new TimeSlot( );
        timeSlot8.setStartingTime( TimeSlotTest.STARTING_TIME_2 );
        timeSlot8.setEndingTime( TimeSlotTest.ENDING_TIME_2 );
        timeSlot8.setIsOpen( TimeSlotTest.IS_OPEN_2 );
        timeSlot8.setIdWorkingDay( workingDay4.getIdWorkingDay( ) );
        TimeSlotHome.create( timeSlot8 );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionHome.findByIdForm( form.getIdForm( ) );
        assertEquals( listWeekDefinition.size( ), 2 );

        // Clean
        TimeSlotHome.delete( timeSlot1.getIdTimeSlot( ) );
        TimeSlotHome.delete( timeSlot2.getIdTimeSlot( ) );
        TimeSlotHome.delete( timeSlot3.getIdTimeSlot( ) );
        TimeSlotHome.delete( timeSlot4.getIdTimeSlot( ) );
        TimeSlotHome.delete( timeSlot5.getIdTimeSlot( ) );
        TimeSlotHome.delete( timeSlot6.getIdTimeSlot( ) );
        TimeSlotHome.delete( timeSlot7.getIdTimeSlot( ) );
        TimeSlotHome.delete( timeSlot8.getIdTimeSlot( ) );
        WorkingDayHome.delete( workingDay1.getIdWorkingDay( ) );
        WorkingDayHome.delete( workingDay2.getIdWorkingDay( ) );
        WorkingDayHome.delete( workingDay3.getIdWorkingDay( ) );
        WorkingDayHome.delete( workingDay4.getIdWorkingDay( ) );
        WeekDefinitionHome.delete( weekDefinition1.getIdWeekDefinition( ) );
        WeekDefinitionHome.delete( weekDefinition2.getIdWeekDefinition( ) );
        ReservationRuleHome.delete( reservationRule.getIdReservationRule( ) );
        ReservationRuleHome.delete( reservationRule2.getIdReservationRule( ) );
        FormHome.delete( form.getIdForm( ) );

    }

    /**
     * Build a Form Business Object
     * 
     * @return a form
     */
    public static Form buildForm1( )
    {
        Form form = new Form( );
        form.setTitle( TITLE_FORM_1 );
        form.setDescription( DESCRIPTION_FORM_1 );
        form.setStartingValidityDate( STARTING_VALIDITY_DATE_1 );
        form.setEndingValidityDate( ENDING_VALIDITY_DATE_1 );
        form.setIsActive( IS_ACTIVE1 );
        form.setIdWorkflow( ID_WORKFLOW_1 );
        return form;
    }
    
    /**
     * Build a Reservation Rule
     * 
     * @return a Reservation Rule
     */
    public static ReservationRule buildReservationRule( int nIdForm )
    {
    	ReservationRule reservationRule = new ReservationRule( );
    	reservationRule.setName( "ReservationRule" );
    	reservationRule.setDescriptionRule( "A built reservation Rule" );
    	reservationRule.setIdForm( nIdForm );
    	return reservationRule;
    }

    /**
     * Check that all the asserts are true
     * 
     * @param formStored
     *            the Form stored
     * @param form
     *            the Form created
     */
    public void checkAsserts( Form formStored, Form form )
    {
        assertEquals( formStored.getTitle( ), form.getTitle( ) );
        assertEquals( formStored.getDescription( ), form.getDescription( ) );
        assertEquals( formStored.getStartingValidityDate( ), form.getStartingValidityDate( ) );
        assertEquals( formStored.getEndingValidityDate( ), form.getEndingValidityDate( ) );
        assertEquals( formStored.getIsActive( ), form.getIsActive( ) );
        assertEquals( formStored.getIdWorkflow( ), form.getIdWorkflow( ) );
    }

}
