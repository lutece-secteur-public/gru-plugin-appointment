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
package fr.paris.lutece.plugins.appointment.web;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationConfig;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;

/**
 * This class provides the user interface to manage calendar templates
 * 
 * @author Laurent Payen
 *
 */
@Controller( controllerJsp = CommentNotificationJspBean.CONTROLLER_JSP, controllerPath = CommentNotificationJspBean.CONTROLLER_PATH, right = CommentJspBean.RIGHT_MANAGECOMMENTTFORM )
public class CommentNotificationJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = 1995349998868975731L;

    /**
     * Folder of the JSP of this controller
     */
    public static final String CONTROLLER_PATH = "jsp/admin/plugins/appointment/";
    /**
     * Name of the JSP of this controller
     */
    public static final String CONTROLLER_JSP = "NotificationCommentConfig.jsp";
    /**
     * URL of the JSP of this controller
     */
    public static final String JSP_URL_MANAGE_CALENDAR_TEMPLATE = CONTROLLER_PATH + CONTROLLER_JSP;
    // templates
    private static final String TEMPLATE_NOTIFICATION_CONFIG = "/admin/plugins/appointment/notification/task_notification_config.html";
    private static final String TEMPLATE_MANAGE_NOTIFICATION_CONFIG = "/admin/plugins/appointment/notification/manage_notification_config.html";

    // Marks
    private static final String MARK_CONFIG = "config";
    private static final String MARK_LIST_CONFIG = "list_config";

    // Parameters
    private static final String PARAMETER_TYPE = "type";

    // Messages
    private static final String INFO_COMMENT_UPDATED = "appointment.info.comment.updated";
    // Properties
    private static final String PROPERTY_PAGE_TITLE_MANAGE_COMMENTS = "task_notify_appointment_comment_config.title";
    // View
    private static final String VIEW_NOTIFIACTION_CONFIG = "notificationConfig";
    private static final String VIEW_MODIFY_NOTIFIACTION_CONFIG = "modifyNotificationCommentConfig";
    // Actions
    private static final String ACTION_DO_UPDATE_NOTIFICATION_CONFIG = "updateNotificationCommentConfig";
    // Session variables
    private CommentNotificationConfig _commentNotificationConfig;

    /**
     * Get the page to manage comment notification.
     * 
     * @param request
     *            The request
     * @return The HTML code to display
     * @throws AccessDeniedException
     */
    @View( value = VIEW_NOTIFIACTION_CONFIG, defaultView = true )
    public String getNotificationCommentConfig( HttpServletRequest request )
    {
        _commentNotificationConfig = null;
        Map<String, Object> model = getModel( );
        model.put( MARK_LIST_CONFIG, CommentNotificationHome.loadCommentNotificationConfig( ) );
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_COMMENTS, TEMPLATE_MANAGE_NOTIFICATION_CONFIG, model );

    }

    /**
     * Get the page to edite comment notification.
     * 
     * @param request
     *            The request
     * @return The HTML code to display
     * @throws AccessDeniedException
     */
    @View( value = VIEW_MODIFY_NOTIFIACTION_CONFIG )
    public String getModifyNotificationCommentConfig( HttpServletRequest request )
    {
        String type = request.getParameter( PARAMETER_TYPE );
        _commentNotificationConfig = CommentNotificationHome.loadCommentNotificationConfigByType( type );
        Map<String, Object> model = getModel( );
        model.put( MARK_CONFIG, _commentNotificationConfig );
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_COMMENTS, TEMPLATE_NOTIFICATION_CONFIG, model );

    }

    /**
     * Update the notification config
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     * @throws AccessDeniedException
     */
    @Action( ACTION_DO_UPDATE_NOTIFICATION_CONFIG )
    public String doUpdateNotificationCommentConfig( HttpServletRequest request )
    {
        populate( _commentNotificationConfig, request );
        CommentNotificationHome.update( _commentNotificationConfig );
        addInfo( INFO_COMMENT_UPDATED, getLocale( ) );
        return redirectView( request, VIEW_NOTIFIACTION_CONFIG );
    }

}
