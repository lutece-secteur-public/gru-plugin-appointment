/*
 * Copyright (c) 2002-2022, City of Paris
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

import java.util.List;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinitionHome;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDayHome;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRuleHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test class of the WorkingDay
 * 
 * @author Laurent Payen
 *
 */
public final class WorkingDayTest extends LuteceTestCase
{

    public final static int DAY_OF_WEEK_1 = 1;
    public final static int DAY_OF_WEEK_2 = 2;

    /**
     * Test method for a working day (CRUD)
     */
    public void testWorkingDay( )
    {
        // Initialize a WorkingDay
        Form form = FormTest.buildForm1( );
        FormHome.create( form );

        ReservationRule reservationRule1 = Commons.buildReservationRule( form.getIdForm( ) );
        ReservationRuleHome.create( reservationRule1 );

        WeekDefinition weekDefinition = WeekDefinitionTest.buildWeekDefinition( reservationRule1.getIdReservationRule( ) );
        // weekDefinition.setIdForm( form.getIdForm( ) );
        WeekDefinitionHome.create( weekDefinition );
        WorkingDay workingDay = buildWorkingDay( );
        workingDay.setIdReservationRule( reservationRule1.getIdReservationRule( ) );
        // Insert the WorkingDay in database
        WorkingDayHome.create( workingDay );
        // Find the workingDay created in database
        WorkingDay workingDayStored = WorkingDayHome.findByPrimaryKey( workingDay.getIdWorkingDay( ) );
        // Check Asserts
        checkAsserts( workingDayStored, workingDay );

        // Update the WorkingDay
        workingDay.setDayOfWeek( DAY_OF_WEEK_2 );
        // Update the WorkingDay in database
        WorkingDayHome.update( workingDay );
        // Get the workingDay in database
        workingDayStored = WorkingDayHome.findByPrimaryKey( workingDay.getIdWorkingDay( ) );
        // Check asserts
        checkAsserts( workingDayStored, workingDay );

        // Delete the workingDay
        WorkingDayHome.delete( workingDay.getIdWorkingDay( ) );
        workingDayStored = WorkingDayHome.findByPrimaryKey( workingDay.getIdWorkingDay( ) );
        // Check the workingDay has been removed from database
        assertNull( workingDayStored );

        // Clean
        WeekDefinitionHome.delete( weekDefinition.getIdWeekDefinition( ) );
        ReservationRuleHome.delete( reservationRule1.getIdReservationRule( ) );
        FormHome.delete( form.getIdForm( ) );
    }

    /**
     * Test of findByIdWeekDefinition
     */
    public void testFindByIdWeekDefinition( )
    {
        // Initialize a WorkingDay
        Form form = FormTest.buildForm1( );
        FormHome.create( form );

        ReservationRule reservationRule1 = Commons.buildReservationRule( form.getIdForm( ) );
        ReservationRuleHome.create( reservationRule1 );

        WeekDefinition weekDefinition = WeekDefinitionTest.buildWeekDefinition( reservationRule1.getIdReservationRule( ) );
        // weekDefinition.setIdForm( form.getIdForm( ) );
        WeekDefinitionHome.create( weekDefinition );
        WorkingDay workingDay = buildWorkingDay( );
        workingDay.setIdReservationRule( reservationRule1.getIdReservationRule( ) );
        // Insert the WorkingDay in database
        WorkingDayHome.create( workingDay );
        // Find the workingDay created in database
        List<WorkingDay> listWorkingDayStored = WorkingDayHome.findByIdReservationRule( reservationRule1.getIdReservationRule( ) );
        // Check Asserts
        assertEquals( listWorkingDayStored.size( ), 1 );
        checkAsserts( listWorkingDayStored.get( 0 ), workingDay );

        // Clean
        WorkingDayHome.delete( workingDay.getIdWorkingDay( ) );
        WeekDefinitionHome.delete( weekDefinition.getIdWeekDefinition( ) );
        ReservationRuleHome.delete( reservationRule1.getIdReservationRule( ) );
        FormHome.delete( form.getIdForm( ) );
    }

    /**
     * Build a WorkingDay Business Object
     * 
     * @return the working day
     */
    public static WorkingDay buildWorkingDay( )
    {
        WorkingDay workingDay = new WorkingDay( );
        workingDay.setDayOfWeek( DAY_OF_WEEK_1 );
        return workingDay;
    }

    /**
     * Check that all the asserts are true
     * 
     * @param workingDayStored
     *            the working day stored
     * @param workingDay
     *            the working day created
     */
    public static void checkAsserts( WorkingDay workingDayStored, WorkingDay workingDay )
    {
        assertEquals( workingDayStored.getDayOfWeek( ), workingDay.getDayOfWeek( ) );
        assertEquals( workingDayStored.getIdReservationRule( ), workingDay.getIdReservationRule( ) );
    }
}
