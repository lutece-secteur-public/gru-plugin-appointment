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

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for User objects
 * 
 * @author Laurent Payen
 *
 */
public final class UserHome
{
	
    // Static variable pointed at the DAO instance
    private static IUserDAO _dao = SpringContextService.getBean( "appointment.userDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private UserHome( )
    {
    }

    /**
     * Create an instance of the User class
     * 
     * @param user
     *            The instance of the User which contains the informations to store
     * @return The instance of the User which has been created with its primary key.
     */
    public static User create( User user )
    {
        _dao.insert( user, _plugin );

        return user;
    }

    /**
     * Update of the User which is specified in parameter
     * 
     * @param user
     *            The instance of the User which contains the data to store
     * @return The instance of the User which has been updated
     */
    public static User update( User user )
    {
        _dao.update( user, _plugin );

        return user;
    }

    /**
     * Delete the User whose identifier is specified in parameter
     * 
     * @param nKey
     *            The User Id
     */
    public static void delete( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of the User whose identifier is specified in parameter
     * 
     * @param nKey
     *            The User primary key
     * @return an instance of the User
     */
    public static User findByPrimaryKey( int nKey )
    {
        return _dao.select( nKey, _plugin );
    }

    /**
     * Returns an instance of the User whose identifier is specified in parameter
     * 
     * @param strGuid
     *            The User guid key
     * @return an instance of the User
     */
    public static User findByGuid( String strGuid )
    {
        return _dao.selectByGuid( strGuid, _plugin );
    }

    /**
     * return a user by its firstname, lastname and email
     * 
     * @param strFirstName
     *            the user first name
     * @param strLastName
     *            the user last name
     * @param strEmail
     *            the user email
     * @return the user if it exists
     */
    public static User findByFirstNameLastNameAndEmail( String strFirstName, String strLastName, String strEmail )
    {
        return _dao.findByFirstNameLastNameAndEmail( strFirstName, strLastName, strEmail, _plugin );
    }

    /**
     * return user(s) by its email
     * 
     * @param strEmail
     *            the user email
     * @return the user(s) if it exists
     */
    public static List<User> findByEmail( String strEmail )
    {
        return _dao.findByEmail( strEmail, _plugin );
    }
}
