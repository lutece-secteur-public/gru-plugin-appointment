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

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.planning.ClosingDay;
import fr.paris.lutece.plugins.appointment.business.planning.ClosingDayHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the ClosingDay
 * 
 * @author Laurent Payen
 *
 */
public final class ClosingDayTest extends LuteceTestCase
{

    public static final LocalDate DATE_OF_CLOSING_DAY_1 = LocalDate.parse( "2017-01-26" );
    public static final LocalDate DATE_OF_CLOSING_DAY_2 = LocalDate.parse( "2017-02-27" );

    /**
     * Test method for the ClosingDay (CRUD)
     */
    public void testClosingDay( )
    {
        Form form = FormTest.buildForm1( );
        FormHome.create( form );

        // Initialize a ClosingDay
        ClosingDay closingDay = buildClosingDay( );
        closingDay.setIdForm( form.getIdForm( ) );
        // Create the ClosingDay in database
        ClosingDayHome.create( closingDay );
        // Find the ClosingDay created in database
        ClosingDay closingDayStored = ClosingDayHome.findByPrimaryKey( closingDay.getIdClosingDay( ) );
        // Check Asserts
        checkAsserts( closingDayStored, closingDay );

        // Update the ClosingDay
        closingDay.setDateOfClosingDay( DATE_OF_CLOSING_DAY_2 );
        // Update the ClosingDay in database
        ClosingDayHome.update( closingDay );
        // Find the ClosingDay updated in database
        closingDayStored = ClosingDayHome.findByPrimaryKey( closingDay.getIdClosingDay( ) );
        // Check Asserts
        checkAsserts( closingDayStored, closingDay );

        // Delete the ClosingDay
        ClosingDayHome.delete( closingDay.getIdClosingDay( ) );
        closingDayStored = ClosingDayHome.findByPrimaryKey( closingDay.getIdClosingDay( ) );
        // Check the ClosingDay has been removed from database
        assertNull( closingDayStored );

        // Clean
        FormHome.delete( form.getIdForm( ) );
    }

    /**
     * Test method for findByIdFormAndDateOfCLosingDay
     */
    public void testFindByIdFormAndDateOfCLosingDay( )
    {
        Form form = FormTest.buildForm1( );
        FormHome.create( form );

        // Initialize a ClosingDay
        ClosingDay closingDay = buildClosingDay( );
        closingDay.setIdForm( form.getIdForm( ) );
        // Create the ClosingDay in database
        ClosingDayHome.create( closingDay );

        // Find the ClosingDay
        ClosingDay closingDayStored = ClosingDayHome.findByIdFormAndDateOfCLosingDay( form.getIdForm( ), DATE_OF_CLOSING_DAY_1 );
        assertNotNull( closingDayStored );
        checkAsserts( closingDayStored, closingDay );

        // Clean
        ClosingDayHome.delete( closingDay.getIdClosingDay( ) );
        FormHome.delete( form.getIdForm( ) );
    }

    /**
     * Build a ClosingDay Business Object
     * 
     * @return the closingDay
     */
    public ClosingDay buildClosingDay( )
    {
        ClosingDay closingDay = new ClosingDay( );
        closingDay.setDateOfClosingDay( DATE_OF_CLOSING_DAY_1 );
        return closingDay;
    }

    /**
     * Check that all the asserts are true
     * 
     * @param closingDayStored
     *            the ClosingDay stored
     * @param closingDay
     *            the ClosingDay created
     */
    public void checkAsserts( ClosingDay closingDayStored, ClosingDay closingDay )
    {
        assertEquals( closingDayStored.getDateOfClosingDay( ), closingDay.getDateOfClosingDay( ) );
        assertEquals( closingDayStored.getIdForm( ), closingDay.getIdForm( ) );
    }
}
