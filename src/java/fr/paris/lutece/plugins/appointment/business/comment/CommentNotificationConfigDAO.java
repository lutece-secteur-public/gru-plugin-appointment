/*
 * Copyright (c) 2002-2022, City of Paris
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

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 *
 * TaskNotificationConfigDAO
 *
 */
public class CommentNotificationConfigDAO implements ICommentNotificationConfigDAO
{
    private static final String SQL_QUERY_FIND = "SELECT notify_type, sender_name,subject,message FROM appointment_comment_notification_cf";
    private static final String SQL_QUERY_FIND_BY_TYPE = "SELECT notify_type, sender_name,subject,message FROM appointment_comment_notification_cf WHERE notify_type= ? ";

    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_comment_notification_cf SET sender_name=?,subject=?,message=? WHERE notify_type= ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( CommentNotificationConfig config, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nPos = 0;

            daoUtil.setString( ++nPos, config.getSenderName( ) );
            daoUtil.setString( ++nPos, config.getSubject( ) );
            daoUtil.setString( ++nPos, config.getMessage( ) );

            daoUtil.setString( ++nPos, config.getType( ).name( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentNotificationConfig loadByType( String strType, Plugin plugin )
    {
        CommentNotificationConfig config = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_TYPE, plugin ) )
        {
            daoUtil.setString( 1, strType );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                int nPos = 0;
                config = new CommentNotificationConfig( );
                config.setType( CommentNotificationConfig.NotificationType.valueOf( ( daoUtil.getString( ++nPos ) ) ) );
                config.setSenderName( daoUtil.getString( ++nPos ) );
                config.setSubject( daoUtil.getString( ++nPos ) );
                config.setMessage( daoUtil.getString( ++nPos ) );
            }
        }
        return config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommentNotificationConfig> load( Plugin plugin )
    {
        List<CommentNotificationConfig> listCommentNotificationConfig = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                int nPos = 0;
                CommentNotificationConfig config = new CommentNotificationConfig( );
                config.setType( CommentNotificationConfig.NotificationType.valueOf( ( daoUtil.getString( ++nPos ) ) ) );
                config.setSenderName( daoUtil.getString( ++nPos ) );
                config.setSubject( daoUtil.getString( ++nPos ) );
                config.setMessage( daoUtil.getString( ++nPos ) );
                listCommentNotificationConfig.add( config );
            }
        }
        return listCommentNotificationConfig;
    }

}
