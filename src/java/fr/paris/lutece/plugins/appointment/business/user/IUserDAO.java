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
package fr.paris.lutece.plugins.appointment.business.user;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * User DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface IUserDAO
{
    /**
     * Insert a new record in the table.
     * 
     * @param user
     *            instance of the user object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( User user, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param user
     *            the reference of the user
     * @param plugin
     *            the Plugin
     */
    void update( User user, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdUser
     *            int identifier of the user to delete
     * @param plugin
     *            the Plugin
     */
    void delete( int nIdUser, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdUser
     *            The identifier of the user
     * @param plugin
     *            the Plugin
     * @return The instance of the user
     */
    User select( int nIdUser, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param strGuid
     *            The identifier of the user
     * @param plugin
     *            the Plugin
     * @return The instance of the user
     */
    User selectByGuid( String strGuid, Plugin plugin );

    /**
     * Return the users by its email
     * 
     * @param strEmail
     *            the email of the user
     * @param plugin
     *            the plugin
     * @return The Users found
     */
    List<User> findByEmail( String strEmail, Plugin plugin );

    /**
     * Return the user by its first name, last name and email
     * 
     * @param strFirstName
     *            the first name of the user
     * @param strLastName
     *            the last name of the user
     * @param strEmail
     *            the email of the user
     * @param plugin
     *            the plugin
     * @return the user found
     */
    User findByFirstNameLastNameAndEmail( String strFirstName, String strLastName, String strEmail, Plugin plugin );
}
