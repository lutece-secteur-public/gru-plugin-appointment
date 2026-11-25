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

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Data access of the comment notifications
 */
public interface ICommentNotificationLogDAO
{
    /**
     * Insert a notification
     *
     * @param notification
     *            the notification, its id is set
     * @param plugin
     *            the plugin
     */
    void insert( CommentNotificationLog notification, Plugin plugin );

    /**
     * Load a notification
     *
     * @param nId
     *            the id of the notification
     * @param plugin
     *            the plugin
     * @return the notification, or null
     */
    CommentNotificationLog load( int nId, Plugin plugin );

    /**
     * Mark a notification as read by an admin user
     *
     * @param nIdNotification
     *            the id of the notification
     * @param nIdUser
     *            the id of the admin user
     * @param plugin
     *            the plugin
     */
    void insertRead( int nIdNotification, int nIdUser, Plugin plugin );

    /**
     * Delete the notifications of a form
     *
     * @param nIdForm
     *            the id of the form
     * @param plugin
     *            the plugin
     */
    void deleteByIdForm( int nIdForm, Plugin plugin );

    /**
     * Delete the notifications sent before a date
     *
     * @param date
     *            the date
     * @param plugin
     *            the plugin
     */
    void deleteBefore( Timestamp date, Plugin plugin );

    /**
     * Select the notifications of some forms sent since a date that an admin user has not read, the most recent first
     *
     * @param listIdForms
     *            the ids of the forms, not empty
     * @param dateSince
     *            the date
     * @param nIdUser
     *            the id of the admin user
     * @param plugin
     *            the plugin
     * @return the notifications
     */
    List<CommentNotificationLog> selectUnreadByIdFormsSince( Collection<Integer> listIdForms, Timestamp dateSince, int nIdUser, Plugin plugin );

    /**
     * Load, in one query, the unread notifications since a date of the forms of some workgroups, with the title of their form, most recent first.
     * A form without workgroup belongs to every workgroup.
     *
     * @param listWorkgroups
     *            the keys of the workgroups, the key of all the workgroups included
     * @param dateSince
     *            the date of the oldest notification
     * @param nIdUser
     *            the id of the admin user
     * @param plugin
     *            the plugin
     * @return the notifications
     */
    List<CommentNotificationLog> selectUnreadByWorkgroupsSince( Collection<String> listWorkgroups, Timestamp dateSince, int nIdUser, Plugin plugin );
}
