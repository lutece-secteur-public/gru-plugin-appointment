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
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinitionHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test class of the WeekDefinition
 * 
 * @author Laurent Payen
 *
 */
public final class WeekDefinitionTest extends LuteceTestCase
{

    public final static LocalDate DATE_OF_APPLY_1 = LocalDate.parse( "2017-01-26" );
    public final static LocalDate DATE_OF_APPLY_2 = LocalDate.parse( "2017-01-27" );

    /**
     * Test method for the weekDefinition (CRUD)
     */
    public void testWeekDefinition( )
    {
        // Initialize a WeekDefinition
        Form form = FormTest.buildForm1( );
        FormHome.create( form );
        WeekDefinition weekDefinition = buildWeekDefinition( );
        weekDefinition.setIdForm( form.getIdForm( ) );
        // Insert the WeekDefinition in database
        WeekDefinitionHome.create( weekDefinition );
        // Find the weekDefinition created in database
        WeekDefinition weekDefinitionStored = WeekDefinitionHome.findByPrimaryKey( weekDefinition.getIdWeekDefinition( ) );
        // Check Asserts
        checkAsserts( weekDefinitionStored, weekDefinition );

        // Update the weekDefinition
        weekDefinition.setDateOfApply( DATE_OF_APPLY_2 );
        // Update the weekDefinition in database
        WeekDefinitionHome.update( weekDefinition );
        // Find the weekDefinition updated in database
        weekDefinitionStored = WeekDefinitionHome.findByPrimaryKey( weekDefinition.getIdWeekDefinition( ) );
        // Check Asserts
        checkAsserts( weekDefinitionStored, weekDefinition );

        // Delete the weekDefinition
        WeekDefinitionHome.delete( weekDefinition.getIdWeekDefinition( ) );
        weekDefinitionStored = WeekDefinitionHome.findByPrimaryKey( weekDefinition.getIdWeekDefinition( ) );
        // Check the weekDefinition has been removed from database
        assertNull( weekDefinitionStored );

        // Clean
        FormHome.delete( form.getIdForm( ) );
    }

    /**
     * Test delete cascade
     */
    public void testDeleteCascade( )
    {
        // Initialize a WeekDefinition
        Form form = FormTest.buildForm1( );
        FormHome.create( form );
        WeekDefinition weekDefinition = buildWeekDefinition( );
        weekDefinition.setIdForm( form.getIdForm( ) );
        // Insert the WeekDefinition in database
        WeekDefinitionHome.create( weekDefinition );
        // Find the weekDefinition created in database
        WeekDefinition weekDefinitionStored = WeekDefinitionHome.findByPrimaryKey( weekDefinition.getIdWeekDefinition( ) );
        assertNotNull( weekDefinitionStored );
        // Delete the Form and by cascade the weekDefinition
        FormHome.delete( form.getIdForm( ) );
        weekDefinitionStored = WeekDefinitionHome.findByPrimaryKey( weekDefinition.getIdWeekDefinition( ) );
        // Check the weekDefinition has been removed from database
        assertNull( weekDefinitionStored );
    }

    /**
     * Test findByIdForm
     */
    public void testFindByIdForm( )
    {
        // Initialize a WeekDefinition
        Form form = FormTest.buildForm1( );
        FormHome.create( form );
        WeekDefinition weekDefinition = buildWeekDefinition( );
        weekDefinition.setIdForm( form.getIdForm( ) );
        // Insert the WeekDefinition in database
        WeekDefinitionHome.create( weekDefinition );
        // Find the weekDefinition created in database
        List<WeekDefinition> listWeekDefinitionStored = WeekDefinitionHome.findByIdForm( form.getIdForm( ) );
        // Check Asserts
        assertEquals( listWeekDefinitionStored.size( ), 1 );
        checkAsserts( listWeekDefinitionStored.get( 0 ), weekDefinition );

        // Clean
        FormHome.delete( form.getIdForm( ) );
    }

    /**
     * Build a WeekDefinition Business Object
     * 
     * @return the weekDefinition
     */
    public static WeekDefinition buildWeekDefinition( )
    {
        WeekDefinition weekDefinition = new WeekDefinition( );
        weekDefinition.setDateOfApply( DATE_OF_APPLY_1 );
        return weekDefinition;
    }

    /**
     * Check that all the asserts are true
     * 
     * @param weekDefinitionStored
     *            the weekDefinition stored
     * @param weekDefinition
     *            the week definition created
     */
    public void checkAsserts( WeekDefinition weekDefinitionStored, WeekDefinition weekDefinition )
    {
        assertEquals( weekDefinitionStored.getDateOfApply( ), weekDefinition.getDateOfApply( ) );
        assertEquals( weekDefinitionStored.getIdForm( ), weekDefinition.getIdForm( ) );
    }
}
