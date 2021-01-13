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
package fr.paris.lutece.plugins.appointment.business.comment;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.sql.Date;
import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for Comment objects
 */
public final class CommentHome
{
    // Static variable pointed at the DAO instance
    private static ICommentDAO _dao = SpringContextService.getBean( "appointment.commentDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class need not be instantiated
     */
    private CommentHome( )
    {
    }

    /**
     * Create an instance of the comment class
     * 
     * @param comment
     *            The instance of the Comment which contains the informations to store
     * @return The instance of comment which has been created with its primary key.
     */
    public static Comment create( Comment comment )
    {
        _dao.insert( comment, _plugin );

        return comment;
    }

    /**
     * Update of the comment which is specified in parameter
     * 
     * @param comment
     *            The instance of the Comment which contains the data to store
     * @return The instance of the comment which has been updated
     */
    public static Comment update( Comment comment )
    {
        _dao.store( comment, _plugin );

        return comment;
    }

    /**
     * Remove the comment whose identifier is specified in parameter
     * 
     * @param nKey
     *            The comment Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a comment whose identifier is specified in parameter
     * 
     * @param nKey
     *            The comment primary key
     * @return an instance of Comment
     */
    public static Comment findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the comment objects and returns them as a list
     * 
     * @return the list which contains the data of all the comment objects
     */
    public static List<Comment> getCommentsList( )
    {
        return _dao.selectCommentsList( _plugin );
    }

    /**
     * Load the data from the table
     * 
     * @param plugin
     *            the plugin
     * @param startingDate
     *            the date start
     * @param endingDate
     *            the date end
     * @returnThe list of the comments
     */
    public static List<Comment> selectCommentsList( Date startingDate, Date endingDate, int nIdForm )
    {

        return _dao.selectCommentsList( _plugin, startingDate, endingDate, nIdForm );
    }

    /**
     * Load the data from the table
     * 
     * @param plugin
     *            the plugin
     * @param startingDate
     *            the date start
     * @param endingDate
     *            the date end
     * @returnThe list of the comments
     */
    public static List<Comment> selectCommentsListInclusive( Date startingDate, Date endingDate, int nIdForm )
    {

        return _dao.selectCommentsListInclusive( _plugin, startingDate, endingDate, nIdForm );
    }

    /**
     * Load the id of all the comment objects and returns them as a list
     * 
     * @return the list which contains the id of all the comment objects
     */
    public static List<Integer> getIdCommentsList( )
    {
        return _dao.selectIdCommentsList( _plugin );
    }

    /**
     * Load the data of all the comment objects and returns them as a referenceList
     * 
     * @return the referenceList which contains the data of all the comment objects
     */
    public static ReferenceList getCommentsReferenceList( )
    {
        return _dao.selectCommentsReferenceList( _plugin );
    }
}
