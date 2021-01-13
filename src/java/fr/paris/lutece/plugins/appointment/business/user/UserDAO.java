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

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for User objects
 * 
 * @author Laurent Payen
 *
 */
public final class UserDAO implements IUserDAO
{

    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_user ( guid, first_name, last_name, email, phone_number) VALUES ( ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_user SET guid = ?, first_name = ?, last_name = ?, email = ?, phone_number = ? WHERE id_user = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_user WHERE id_user = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_user, guid, first_name, last_name, email, phone_number FROM appointment_user";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_user = ?";
    private static final String SQL_QUERY_SELECT_BY_GUID = SQL_QUERY_SELECT_COLUMNS + " WHERE guid = ?";
    private static final String SQL_QUERY_SELECT_BY_EMAIL = SQL_QUERY_SELECT_COLUMNS + " WHERE email = ?";
    private static final String SQL_QUERY_SELECT_BY_FIRSTNAME_LASTNAME_AND_EMAIL = SQL_QUERY_SELECT_COLUMNS
            + " WHERE UPPER(first_name) = ? and UPPER(last_name) = ? and UPPER(email) = ?";

    @Override
    public void insert( User user, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, user, plugin, true ) )
        {
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                user.setIdUser( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    @Override
    public void update( User user, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, user, plugin, false ) )
        {
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void delete( int nIdUser, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdUser );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public User select( int nIdUser, Plugin plugin )
    {
        User user = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nIdUser );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                user = buildUser( daoUtil );
            }
        }
        return user;
    }

    @Override
    public User selectByGuid( String strGuid, Plugin plugin )
    {
        User user = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_GUID, plugin ) )
        {
            daoUtil.setString( 1, strGuid );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                user = buildUser( daoUtil );
            }
        }
        return user;
    }

    @Override
    public List<User> findByEmail( String strEmail, Plugin plugin )
    {
        List<User> listUsers = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_EMAIL, plugin ) )
        {
            daoUtil.setString( 1, strEmail );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listUsers.add( buildUser( daoUtil ) );
            }
        }
        return listUsers;
    }

    @Override
    public User findByFirstNameLastNameAndEmail( String strFirstName, String strLastName, String strEmail, Plugin plugin )
    {
        User user = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_FIRSTNAME_LASTNAME_AND_EMAIL, plugin ) )
        {
            daoUtil.setString( 1, strFirstName.toUpperCase( ) );
            daoUtil.setString( 2, strLastName.toUpperCase( ) );
            daoUtil.setString( 3, strEmail.toUpperCase( ) );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                user = buildUser( daoUtil );
            }
        }
        return user;
    }

    /**
     * Build a User business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new User with all its attributes assigned
     */
    private User buildUser( DAOUtil daoUtil )
    {
        int nIndex = 1;
        User user = new User( );
        user.setIdUser( daoUtil.getInt( nIndex++ ) );
        user.setGuid( daoUtil.getString( nIndex++ ) );
        user.setFirstName( daoUtil.getString( nIndex++ ) );
        user.setLastName( daoUtil.getString( nIndex++ ) );
        user.setEmail( daoUtil.getString( nIndex++ ) );
        user.setPhoneNumber( daoUtil.getString( nIndex ) );
        return user;
    }

    /**
     * Build a daoUtil object with the User business object
     * 
     * @param query
     *            the query
     * @param user
     *            the User
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, User user, Plugin plugin, boolean isInsert )
    {
        int nIndex = 1;
        DAOUtil daoUtil = null;
        if ( isInsert )
        {
            daoUtil = new DAOUtil( query, Statement.RETURN_GENERATED_KEYS, plugin );
        }
        else
        {
            daoUtil = new DAOUtil( query, plugin );
        }
        daoUtil.setString( nIndex++, user.getGuid( ) );
        daoUtil.setString( nIndex++, user.getFirstName( ) );
        daoUtil.setString( nIndex++, user.getLastName( ) );
        daoUtil.setString( nIndex++, user.getEmail( ) );
        daoUtil.setString( nIndex++, user.getPhoneNumber( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, user.getIdUser( ) );
        }
        return daoUtil;
    }
}
