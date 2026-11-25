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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Home of the comment notifications
 */
public final class CommentNotificationLogHome
{
    private static ICommentNotificationLogDAO _dao = CDI.current( ).select( ICommentNotificationLogDAO.class ).get( );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    private CommentNotificationLogHome( )
    {
    }

    /**
     * Create a notification
     *
     * @param notification
     *            the notification
     * @return the notification, with its id
     */
    public static CommentNotificationLog create( CommentNotificationLog notification )
    {
        _dao.insert( notification, _plugin );

        return notification;
    }

    /**
     * Find a notification
     *
     * @param nId
     *            the id of the notification
     * @return the notification, or null
     */
    public static CommentNotificationLog findByPrimaryKey( int nId )
    {
        return _dao.load( nId, _plugin );
    }

    /**
     * Mark a notification as read by an admin user, it is no longer shown to him
     *
     * @param nIdNotification
     *            the id of the notification
     * @param nIdUser
     *            the id of the admin user
     */
    public static void markRead( int nIdNotification, int nIdUser )
    {
        _dao.insertRead( nIdNotification, nIdUser, _plugin );
    }

    /**
     * Remove the notifications of a form
     *
     * @param nIdForm
     *            the id of the form
     */
    public static void removeByIdForm( int nIdForm )
    {
        _dao.deleteByIdForm( nIdForm, _plugin );
    }

    /**
     * Remove the notifications sent before a date
     *
     * @param date
     *            the date
     */
    public static void removeBefore( Timestamp date )
    {
        _dao.deleteBefore( date, _plugin );
    }

    /**
     * Find the notifications of some forms sent since a date that an admin user has not read, the most recent first
     *
     * @param listIdForms
     *            the ids of the forms
     * @param dateSince
     *            the date
     * @param nIdUser
     *            the id of the admin user
     * @return the notifications
     */
    public static List<CommentNotificationLog> findUnreadByIdFormsSince( Collection<Integer> listIdForms, Timestamp dateSince, int nIdUser )
    {
        return _dao.selectUnreadByIdFormsSince( listIdForms, dateSince, nIdUser, _plugin );
    }

    /**
     * Find, in one query, the unread notifications since a date of the forms of some workgroups, with the title of their form, most recent first
     *
     * @param listWorkgroups
     *            the keys of the workgroups, the key of all the workgroups included
     * @param dateSince
     *            the date of the oldest notification
     * @param nIdUser
     *            the id of the admin user
     * @return the notifications
     */
    public static List<CommentNotificationLog> findUnreadByWorkgroupsSince( Collection<String> listWorkgroups, Timestamp dateSince, int nIdUser )
    {
        return _dao.selectUnreadByWorkgroupsSince( listWorkgroups, dateSince, nIdUser, _plugin );
    }
}
