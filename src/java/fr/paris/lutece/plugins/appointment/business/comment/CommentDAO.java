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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Date;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for Comment objects
 */
public final class CommentDAO implements ICommentDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_comment, id_form, starting_validity_date, starting_validity_time, ending_validity_date, ending_validity_time, comment, comment_creation_date, comment_user_creator FROM appointment_comment WHERE id_comment = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_comment ( id_form, starting_validity_date, starting_validity_time, ending_validity_date, ending_validity_time, comment, comment_creation_date, comment_user_creator ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_comment WHERE id_comment = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_comment SET id_comment = ?, id_form = ?, starting_validity_date = ?, starting_validity_time = ?, ending_validity_date = ?, ending_validity_time = ?, comment = ?, comment_creation_date = ?, comment_user_creator = ? WHERE id_comment = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_comment, id_form, starting_validity_date, starting_validity_time, ending_validity_date, ending_validity_time, comment, comment_creation_date, comment_user_creator FROM appointment_comment";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_comment FROM appointment_comment";
    private static final String SQL_QUERY_SELECT_BETWEEN = "SELECT id_comment, id_form, starting_validity_date, starting_validity_time, ending_validity_date, ending_validity_time, comment, comment_creation_date, comment_user_creator FROM appointment_comment WHERE starting_validity_date >= ? and ending_validity_date <= ? and id_form = ?";
    private static final String SQL_QUERY_SELECT_INCLUSIVE = "SELECT id_comment, id_form, starting_validity_date, starting_validity_time, ending_validity_date, ending_validity_time, comment, comment_creation_date, comment_user_creator FROM appointment_comment WHERE starting_validity_date <= ? and ending_validity_date >= ? and id_form = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Comment comment, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, comment.getIdForm( ) );
            daoUtil.setDate( nIndex++, Date.valueOf( comment.getStartingValidityDate( ) ) );
            if ( comment.getStartingValidityTime( ) != null )
            {
                daoUtil.setTime( nIndex++, Time.valueOf( comment.getStartingValidityTime( ) ) );
            }
            else
            {
                daoUtil.setTime( nIndex++, null );
            }
            daoUtil.setDate( nIndex++, Date.valueOf( comment.getEndingValidityDate( ) ) );
            if ( comment.getEndingValidityTime( ) != null )
            {
                daoUtil.setTime( nIndex++, Time.valueOf( comment.getEndingValidityTime( ) ) );
            }
            else
            {
                daoUtil.setTime( nIndex++, null );
            }
            daoUtil.setString( nIndex++, comment.getComment( ) );
            daoUtil.setDate( nIndex++, Date.valueOf( comment.getCreationDate( ) ) );
            daoUtil.setString( nIndex++, comment.getCreatorUserName( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                comment.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Comment load( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            Comment comment = null;

            if ( daoUtil.next( ) )
            {
                comment = new Comment( );
                int nIndex = 1;

                comment.setId( daoUtil.getInt( nIndex++ ) );
                comment.setIdForm( daoUtil.getInt( nIndex++ ) );
                comment.setStartingValidityDate( daoUtil.getDate( nIndex++ ).toLocalDate( ) );
                Time startingTime = daoUtil.getTime( nIndex++ );
                if ( startingTime != null )
                {
                    comment.setStartingValidityTime( startingTime.toLocalTime( ) );
                }
                comment.setEndingValidityDate( daoUtil.getDate( nIndex++ ).toLocalDate( ) );
                Time endingTime = daoUtil.getTime( nIndex++ );
                if ( endingTime != null )
                {
                    comment.setEndingValidityTime( endingTime.toLocalTime( ) );
                }
                comment.setComment( daoUtil.getString( nIndex++ ) );
                comment.setCreationDate( daoUtil.getDate( nIndex++ ).toLocalDate( ) );
                comment.setCreatorUserName( daoUtil.getString( nIndex ) );

            }
            return comment;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Comment comment, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setInt( nIndex++, comment.getId( ) );
            daoUtil.setInt( nIndex++, comment.getIdForm( ) );
            daoUtil.setDate( nIndex++, Date.valueOf( comment.getStartingValidityDate( ) ) );
            daoUtil.setTime( nIndex++, Time.valueOf( comment.getStartingValidityTime( ) ) );
            daoUtil.setDate( nIndex++, Date.valueOf( comment.getEndingValidityDate( ) ) );
            daoUtil.setTime( nIndex++, Time.valueOf( comment.getEndingValidityTime( ) ) );
            daoUtil.setString( nIndex++, comment.getComment( ) );
            daoUtil.setDate( nIndex++, Date.valueOf( comment.getCreationDate( ) ) );
            daoUtil.setString( nIndex++, comment.getCreatorUserName( ) );
            daoUtil.setInt( nIndex++, comment.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Comment> selectCommentsList( Plugin plugin )
    {
        List<Comment> commentList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Comment comment = new Comment( );
                int nIndex = 1;

                comment.setId( daoUtil.getInt( nIndex++ ) );
                comment.setIdForm( daoUtil.getInt( nIndex++ ) );
                comment.setStartingValidityDate( daoUtil.getDate( nIndex++ ).toLocalDate( ) );
                Time startingTime = daoUtil.getTime( nIndex++ );
                if ( startingTime != null )
                {
                    comment.setStartingValidityTime( startingTime.toLocalTime( ) );
                }
                comment.setEndingValidityDate( daoUtil.getDate( nIndex++ ).toLocalDate( ) );
                Time endingTime = daoUtil.getTime( nIndex++ );
                if ( endingTime != null )
                {
                    comment.setEndingValidityTime( endingTime.toLocalTime( ) );
                }
                comment.setComment( daoUtil.getString( nIndex ) );
                comment.setCreationDate( daoUtil.getDate( nIndex++ ).toLocalDate( ) );
                comment.setCreatorUserName( daoUtil.getString( nIndex ) );

                commentList.add( comment );
            }

            return commentList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Comment> selectCommentsList( Plugin plugin, Date startingDate, Date endingDate, int nIdForm )
    {
        List<Comment> commentList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BETWEEN, plugin ) )
        {
            daoUtil.setDate( 1, startingDate );
            daoUtil.setDate( 2, endingDate );
            daoUtil.setInt( 3, nIdForm );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Comment comment = new Comment( );
                int nIndex = 1;

                comment.setId( daoUtil.getInt( nIndex++ ) );
                comment.setIdForm( daoUtil.getInt( nIndex++ ) );
                comment.setStartingValidityDate( daoUtil.getDate( nIndex++ ).toLocalDate( ) );
                Time startingTime = daoUtil.getTime( nIndex++ );
                if ( startingTime != null )
                {
                    comment.setStartingValidityTime( startingTime.toLocalTime( ) );
                }
                comment.setEndingValidityDate( daoUtil.getDate( nIndex++ ).toLocalDate( ) );
                Time endingTime = daoUtil.getTime( nIndex++ );
                if ( endingTime != null )
                {
                    comment.setEndingValidityTime( endingTime.toLocalTime( ) );
                }
                comment.setComment( daoUtil.getString( nIndex++ ) );
                comment.setCreationDate( daoUtil.getDate( nIndex++ ).toLocalDate( ) );
                comment.setCreatorUserName( daoUtil.getString( nIndex++ ) );

                commentList.add( comment );
            }

            return commentList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Comment> selectCommentsListInclusive( Plugin plugin, Date startingDate, Date endingDate, int nIdForm )
    {
        List<Comment> commentList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_INCLUSIVE, plugin ) )
        {
            daoUtil.setDate( 1, startingDate );
            daoUtil.setDate( 2, endingDate );
            daoUtil.setInt( 3, nIdForm );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Comment comment = new Comment( );
                int nIndex = 1;

                comment.setId( daoUtil.getInt( nIndex++ ) );
                comment.setIdForm( daoUtil.getInt( nIndex++ ) );
                comment.setStartingValidityDate( daoUtil.getDate( nIndex++ ).toLocalDate( ) );
                Time startingTime = daoUtil.getTime( nIndex++ );
                if ( startingTime != null )
                {
                    comment.setStartingValidityTime( startingTime.toLocalTime( ) );
                }
                comment.setEndingValidityDate( daoUtil.getDate( nIndex++ ).toLocalDate( ) );
                Time endingTime = daoUtil.getTime( nIndex++ );
                if ( endingTime != null )
                {
                    comment.setEndingValidityTime( endingTime.toLocalTime( ) );
                }
                comment.setComment( daoUtil.getString( nIndex++ ) );
                comment.setCreationDate( daoUtil.getDate( nIndex++ ).toLocalDate( ) );
                comment.setCreatorUserName( daoUtil.getString( nIndex++ ) );

                commentList.add( comment );
            }

            return commentList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdCommentsList( Plugin plugin )
    {
        List<Integer> commentList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                commentList.add( daoUtil.getInt( 1 ) );
            }

            return commentList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectCommentsReferenceList( Plugin plugin )
    {
        ReferenceList commentList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                commentList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

            return commentList;
        }
    }

}
