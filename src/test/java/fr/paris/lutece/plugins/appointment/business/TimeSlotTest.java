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

import java.time.LocalTime;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlotHome;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinitionHome;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDayHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test class for the TimeSlot
 * 
 * @author Laurent Payen
 *
 */
public final class TimeSlotTest extends LuteceTestCase
{

    public final static LocalTime STARTING_TIME_1 = LocalTime.parse( "09:00" );
    public final static LocalTime STARTING_TIME_2 = LocalTime.parse( "09:30" );
    public final static LocalTime ENDING_TIME_1 = LocalTime.parse( "09:30" );
    public final static LocalTime ENDING_TIME_2 = LocalTime.parse( "10:00" );
    public final static boolean IS_OPEN_1 = true;
    public final static boolean IS_OPEN_2 = false;
    public final static int MAX_CAPACITY_1 = 1;
    public final static int MAX_CAPACITY_2 = 2;

    /**
     * Test method for the TimeSlot (CRUD)
     */
    public void testTimeSlot( )
    {
        Form form = FormTest.buildForm1( );
        FormHome.create( form );

        WeekDefinition weekDefinition = WeekDefinitionTest.buildWeekDefinition( );
        weekDefinition.setIdForm( form.getIdForm( ) );
        WeekDefinitionHome.create( weekDefinition );

        WorkingDay workingDay = WorkingDayTest.buildWorkingDay( );
        workingDay.setIdWeekDefinition( weekDefinition.getIdWeekDefinition( ) );
        WorkingDayHome.create( workingDay );

        // Initialize a TimeSlot
        TimeSlot timeSlot = buildTimeSlot( STARTING_TIME_1, ENDING_TIME_1, IS_OPEN_1, MAX_CAPACITY_1, workingDay.getIdWorkingDay( ) );
        // Create the TimeSlot in database
        TimeSlotHome.create( timeSlot );

        // Find the TimeSlot created in database
        TimeSlot timeSlotStored = TimeSlotHome.findByPrimaryKey( timeSlot.getIdTimeSlot( ) );
        // Check Asserts
        checkAsserts( timeSlotStored, timeSlot );

        // Update the timeSlot
        timeSlot.setStartingTime( STARTING_TIME_2 );
        timeSlot.setEndingTime( ENDING_TIME_2 );
        timeSlot.setIsOpen( IS_OPEN_2 );
        timeSlot.setMaxCapacity( MAX_CAPACITY_2 );
        // Update the timeSlot in database
        TimeSlotHome.update( timeSlot );
        // Find the timeSlot updated in database
        timeSlotStored = TimeSlotHome.findByPrimaryKey( timeSlot.getIdTimeSlot( ) );
        // Check Asserts
        checkAsserts( timeSlotStored, timeSlot );

        // Delete the timeSlot
        TimeSlotHome.delete( timeSlot.getIdTimeSlot( ) );
        timeSlotStored = TimeSlotHome.findByPrimaryKey( timeSlot.getIdTimeSlot( ) );
        // Check the timeSlot has been removed from database
        assertNull( timeSlotStored );

        // Clean
        FormHome.delete( form.getIdForm( ) );
    }

    /**
     * Test delete cascade
     */
    public void testDeleteCascade( )
    {
        Form form = FormTest.buildForm1( );
        FormHome.create( form );

        WeekDefinition weekDefinition = WeekDefinitionTest.buildWeekDefinition( );
        weekDefinition.setIdForm( form.getIdForm( ) );
        WeekDefinitionHome.create( weekDefinition );

        WorkingDay workingDay = WorkingDayTest.buildWorkingDay( );
        workingDay.setIdWeekDefinition( weekDefinition.getIdWeekDefinition( ) );
        WorkingDayHome.create( workingDay );

        // Initialize a TimeSlot
        TimeSlot timeSlot = buildTimeSlot( STARTING_TIME_1, ENDING_TIME_1, IS_OPEN_1, MAX_CAPACITY_1, workingDay.getIdWorkingDay( ) );
        // Create the TimeSlot in database
        TimeSlotHome.create( timeSlot );

        // Find the TimeSlot created in database
        TimeSlot timeSlotStored = TimeSlotHome.findByPrimaryKey( timeSlot.getIdTimeSlot( ) );
        assertNotNull( timeSlotStored );

        // Delete the Form and by cascade the timeSlot
        TimeSlotHome.delete( timeSlot.getIdTimeSlot( ) );
        timeSlotStored = TimeSlotHome.findByPrimaryKey( timeSlot.getIdTimeSlot( ) );
        // Check the timeSlot has been removed from database
        assertNull( timeSlotStored );

        // Clean
        FormHome.delete( form.getIdForm( ) );

    }

    /**
     * Test of findByIdWorkingDay
     */
    public void testFindByIdWorkingDay( )
    {
        Form form = FormTest.buildForm1( );
        FormHome.create( form );

        WeekDefinition weekDefinition = WeekDefinitionTest.buildWeekDefinition( );
        weekDefinition.setIdForm( form.getIdForm( ) );
        WeekDefinitionHome.create( weekDefinition );

        WorkingDay workingDay = WorkingDayTest.buildWorkingDay( );
        workingDay.setIdWeekDefinition( weekDefinition.getIdWeekDefinition( ) );
        WorkingDayHome.create( workingDay );

        // Initialize a TimeSlot
        TimeSlot timeSlot = buildTimeSlot( STARTING_TIME_1, ENDING_TIME_1, IS_OPEN_1, MAX_CAPACITY_1, workingDay.getIdWorkingDay( ) );
        // Create the TimeSlot in database
        TimeSlotHome.create( timeSlot );

        // Find the TimeSlot created in database
        List<TimeSlot> listTimeSlotStored = TimeSlotHome.findByIdWorkingDay( workingDay.getIdWorkingDay( ) );
        assertEquals( listTimeSlotStored.size( ), 1 );
        checkAsserts( listTimeSlotStored.get( 0 ), timeSlot );

        // Clean
        FormHome.delete( form.getIdForm( ) );

    }

    /**
     * build a TimeSlot Business Object
     * 
     * @return the timeSlot
     */
    public static TimeSlot buildTimeSlot( LocalTime startingTime, LocalTime endingTime, boolean bIsOpen, int nMaxCapacity, int nIdWorkingDay )
    {
        TimeSlot timeSlot = new TimeSlot( );
        timeSlot.setStartingTime( startingTime );
        timeSlot.setEndingTime( endingTime );
        timeSlot.setIsOpen( bIsOpen );
        timeSlot.setMaxCapacity( nMaxCapacity );
        timeSlot.setIdWorkingDay( nIdWorkingDay );
        return timeSlot;
    }

    /**
     * Check that all the asserts are true
     * 
     * @param timeSlotStored
     *            the timeSlot stored
     * @param timeSlot
     *            the timeSlot created
     */
    public void checkAsserts( TimeSlot timeSlotStored, TimeSlot timeSlot )
    {
        assertEquals( timeSlotStored.getStartingTime( ), timeSlot.getStartingTime( ) );
        assertEquals( timeSlotStored.getEndingTime( ), timeSlot.getEndingTime( ) );
        assertEquals( timeSlotStored.getIsOpen( ), timeSlot.getIsOpen( ) );
        assertEquals( timeSlotStored.getMaxCapacity( ), timeSlot.getMaxCapacity( ) );
        assertEquals( timeSlotStored.getIdWorkingDay( ), timeSlot.getIdWorkingDay( ) );
    }

}
