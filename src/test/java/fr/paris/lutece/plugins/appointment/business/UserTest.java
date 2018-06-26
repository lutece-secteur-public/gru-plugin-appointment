/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test class for the User
 * 
 * @author Laurent Payen
 *
 */
public final class UserTest extends LuteceTestCase
{

    /**
     * Test method for the User (CRUD)
     */
    public void testUser( )
    {
        // Initialize a User
        User user = buildUser( Constants.GUID_1, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.EMAIL_1, Constants.PHONE_NUMBER_1 );
        // Insert the User in database
        UserHome.create( user );
        // Find the user created in database
        User userStored = UserHome.findByPrimaryKey( user.getIdUser( ) );
        // Check Asserts
        checkAsserts( userStored, user );

        // Update the user
        user.setGuid( Constants.GUID_2 );
        user.setFirstName( Constants.FIRST_NAME_2 );
        user.setLastName( Constants.LAST_NAME_2 );
        user.setEmail( Constants.EMAIL_2 );
        user.setPhoneNumber( Constants.PHONE_NUMBER_2 );
        // Update the user in database
        UserHome.update( user );
        // Find the user updated in database
        userStored = UserHome.findByPrimaryKey( user.getIdUser( ) );
        // Check Asserts
        checkAsserts( userStored, user );

        // Delete the user
        UserHome.delete( user.getIdUser( ) );
        userStored = UserHome.findByPrimaryKey( user.getIdUser( ) );
        // Check the user has been removed from database
        assertNull( userStored );
    }

    /**
     * Build a User Business Object
     * 
     * @return the User
     */
    public static User buildUser( String strGuid, String strFirstName, String strLastName, String strEmail, String strPhoneNumber )
    {
        User user = new User( );
        user.setGuid( strGuid );
        user.setFirstName( strFirstName );
        user.setLastName( strLastName );
        user.setEmail( strEmail );
        user.setPhoneNumber( strPhoneNumber );
        return user;
    }

    /**
     * Check that all the asserts are true
     * 
     * @param userStored
     *            the user stored
     * @param user
     *            the user created
     */
    public void checkAsserts( User userStored, User user )
    {
        assertEquals( userStored.getGuid( ), user.getGuid( ) );
        assertEquals( userStored.getFirstName( ), user.getFirstName( ) );
        assertEquals( userStored.getLastName( ), user.getLastName( ) );
        assertEquals( userStored.getEmail( ), user.getEmail( ) );
        assertEquals( userStored.getPhoneNumber( ), user.getPhoneNumber( ) );
    }
}
