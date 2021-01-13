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

import java.util.List;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;

/**
 * Service class of a user
 * 
 * @author Laurent Payen
 *
 */
public final class UserService
{

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private UserService( )
    {
    }

    /**
     * Save a user in database / A user is defined by its email (unique)
     * 
     * @param appointment
     *            the appointment DTO
     * @return the user saved
     */
    public static User saveUser( AppointmentDTO appointment )
    {
        User user = new User( );
        user.setGuid( appointment.getGuid( ) );
        user.setFirstName( appointment.getFirstName( ) );
        user.setLastName( appointment.getLastName( ) );
        user.setEmail( appointment.getEmail( ) );
        user.setPhoneNumber( appointment.getPhoneNumber( ) );
        return UserHome.create( user );

    }

    /**
     * Find a User by its primary key
     * 
     * @param nIdUser
     *            the primary key
     * @return the User found
     */
    public static User findUserById( int nIdUser )
    {
        return UserHome.findByPrimaryKey( nIdUser );
    }

    /**
     * Find a user by its first name, last name and email
     * 
     * @param strFirstName
     *            the first name
     * @param strLastName
     *            the last name
     * @param strEmail
     *            the email
     * @return the user
     */
    public static User findUserByFirstNameLastNameAndEmail( String strFirstName, String strLastName, String strEmail )
    {
        return UserHome.findByFirstNameLastNameAndEmail( strFirstName, strLastName, strEmail );
    }

    /**
     * Find users by its email
     * 
     * @param strEmail
     *            the email
     * @return the user(s)
     */
    public static List<User> findUsersByEmail( String strEmail )
    {
        return UserHome.findByEmail( strEmail );
    }

}
