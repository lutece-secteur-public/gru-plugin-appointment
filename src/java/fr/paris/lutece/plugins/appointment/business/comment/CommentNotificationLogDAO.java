/*
 * Copyright (c) 2002-2025, City of Paris
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

import java.sql.Date;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationConfig.NotificationType;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Data access of the comment notifications
 */
@ApplicationScoped
public class CommentNotificationLogDAO implements ICommentNotificationLogDAO
{
    private static final String SQL_COLUMNS = "SELECT id_notification, notify_type, id_form, id_comment, comment, comment_user_creator, starting_validity_date, ending_validity_date, date_notification FROM appointment_comment_notification_log";
    private static final String SQL_QUERY_SELECT = SQL_COLUMNS + " WHERE id_notification = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_comment_notification_log ( notify_type, id_form, id_comment, comment, comment_user_creator, starting_validity_date, ending_validity_date, date_notification ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE_BY_ID_FORM = "DELETE FROM appointment_comment_notification_log WHERE id_form = ?";
    private static final String SQL_QUERY_DELETE_BEFORE = "DELETE FROM appointment_comment_notification_log WHERE date_notification < ?";
    private static final String SQL_QUERY_SELECT_UNREAD_BY_ID_FORMS_SINCE = SQL_COLUMNS
            + " n WHERE date_notification >= ? AND NOT EXISTS ( SELECT 1 FROM appointment_comment_notification_read r WHERE r.id_notification = n.id_notification AND r.id_user = ? ) AND id_form IN ( ";
    private static final String SQL_ORDER_BY_DATE_DESC = " ) ORDER BY date_notification DESC, id_notification DESC";
    private static final String SQL_QUERY_INSERT_READ = "INSERT INTO appointment_comment_notification_read ( id_notification, id_user ) SELECT ?, ? FROM appointment_comment_notification_log WHERE id_notification = ? AND NOT EXISTS ( SELECT 1 FROM appointment_comment_notification_read WHERE id_notification = ? AND id_user = ? )";
    private static final String SQL_QUERY_DELETE_READ_BY_ID_FORM = "DELETE FROM appointment_comment_notification_read WHERE id_notification IN ( SELECT id_notification FROM appointment_comment_notification_log WHERE id_form = ? )";
    private static final String SQL_QUERY_DELETE_READ_BEFORE = "DELETE FROM appointment_comment_notification_read WHERE id_notification IN ( SELECT id_notification FROM appointment_comment_notification_log WHERE date_notification < ? )";

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert( CommentNotificationLog notification, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, notification.getType( ).name( ) );
            daoUtil.setInt( nIndex++, notification.getIdForm( ) );
            daoUtil.setInt( nIndex++, notification.getIdComment( ) );
            daoUtil.setString( nIndex++, notification.getComment( ) );
            daoUtil.setString( nIndex++, notification.getCreatorUserName( ) );
            daoUtil.setDate( nIndex++, notification.getStartingValidityDate( ) != null ? Date.valueOf( notification.getStartingValidityDate( ) ) : null );
            daoUtil.setDate( nIndex++, notification.getEndingValidityDate( ) != null ? Date.valueOf( notification.getEndingValidityDate( ) ) : null );
            daoUtil.setTimestamp( nIndex, notification.getDateNotification( ) );
            daoUtil.executeUpdate( );

            if ( daoUtil.nextGeneratedKey( ) )
            {
                notification.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentNotificationLog load( int nId, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeQuery( );

            return daoUtil.next( ) ? dataToObject( daoUtil ) : null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertRead( int nIdNotification, int nIdUser, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_READ, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, nIdNotification );
            daoUtil.setInt( nIndex++, nIdUser );
            daoUtil.setInt( nIndex++, nIdNotification );
            daoUtil.setInt( nIndex++, nIdNotification );
            daoUtil.setInt( nIndex, nIdUser );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByIdForm( int nIdForm, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_READ_BY_ID_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeUpdate( );
        }

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteBefore( Timestamp date, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_READ_BEFORE, plugin ) )
        {
            daoUtil.setTimestamp( 1, date );
            daoUtil.executeUpdate( );
        }

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BEFORE, plugin ) )
        {
            daoUtil.setTimestamp( 1, date );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommentNotificationLog> selectUnreadByIdFormsSince( Collection<Integer> listIdForms, Timestamp dateSince, int nIdUser, Plugin plugin )
    {
        if ( listIdForms == null || listIdForms.isEmpty( ) )
        {
            return new ArrayList<>( );
        }

        String strQuery = SQL_QUERY_SELECT_UNREAD_BY_ID_FORMS_SINCE + String.join( ",", Collections.nCopies( listIdForms.size( ), "?" ) ) + SQL_ORDER_BY_DATE_DESC;
        List<CommentNotificationLog> listNotifications = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( strQuery, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setTimestamp( nIndex++, dateSince );
            daoUtil.setInt( nIndex++, nIdUser );

            for ( Integer nIdForm : listIdForms )
            {
                daoUtil.setInt( nIndex++, nIdForm );
            }

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listNotifications.add( dataToObject( daoUtil ) );
            }
        }

        return listNotifications;
    }

    /**
     * Build a notification from the current row
     *
     * @param daoUtil
     *            the DAOUtil on the row
     * @return the notification
     */
    private static CommentNotificationLog dataToObject( DAOUtil daoUtil )
    {
        int nIndex = 1;
        CommentNotificationLog notification = new CommentNotificationLog( );
        notification.setId( daoUtil.getInt( nIndex++ ) );
        notification.setType( NotificationType.valueOf( daoUtil.getString( nIndex++ ) ) );
        notification.setIdForm( daoUtil.getInt( nIndex++ ) );
        notification.setIdComment( daoUtil.getInt( nIndex++ ) );
        notification.setComment( daoUtil.getString( nIndex++ ) );
        notification.setCreatorUserName( daoUtil.getString( nIndex++ ) );
        Date dateStarting = daoUtil.getDate( nIndex++ );
        notification.setStartingValidityDate( dateStarting != null ? dateStarting.toLocalDate( ) : null );
        Date dateEnding = daoUtil.getDate( nIndex++ );
        notification.setEndingValidityDate( dateEnding != null ? dateEnding.toLocalDate( ) : null );
        notification.setDateNotification( daoUtil.getTimestamp( nIndex ) );

        return notification;
    }
}
