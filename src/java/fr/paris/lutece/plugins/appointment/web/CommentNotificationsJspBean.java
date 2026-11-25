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

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationLog;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationLogHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.util.url.UrlItem;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The comment notifications of the notification zone of the admin header: a clicked notification is marked as read by
 * the user, so it leaves the zone, and the user is taken to the calendar of the form at the date of the comment.
 */
@RequestScoped
@Named
@Controller( controllerJsp = CommentNotificationsJspBean.CONTROLLER_JSP, controllerPath = CommentNotificationsJspBean.CONTROLLER_PATH, right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM, securityTokenEnabled = true )
public class CommentNotificationsJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = -2617425010733183318L;

    /** The folder of the JSP of this controller */
    public static final String CONTROLLER_PATH = "jsp/admin/plugins/appointment/";
    /** The name of the JSP of this controller */
    public static final String CONTROLLER_JSP = "CommentNotifications.jsp";
    /** The markRead action, its name is also the key of its security token */
    public static final String ACTION_MARK_READ = "markCommentNotificationRead";

    // Parameters
    private static final String PARAMETER_ID_NOTIFICATION = "id_notification";
    private static final String PARAMETER_VIEW = "view";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_DATE_OF_DISPLAY = "date_of_display";

    // Urls
    private static final String JSP_MANAGE_APPOINTMENTS = CONTROLLER_PATH + "ManageAppointments.jsp";
    private static final String VIEW_CALENDAR_MANAGE_APPOINTMENTS = "viewCalendarManageAppointment";

    /**
     * Mark as read the notification whose id is in the request, when the user may see the comments of its form, and
     * go to the calendar of the form at the date of the comment
     *
     * @param request
     *            the request
     * @return the URL of the calendar, or of the admin home when the notification is not found or not visible
     */
    @Action( ACTION_MARK_READ )
    public String doMarkRead( HttpServletRequest request )
    {
        AdminUser user = getUser( );
        CommentNotificationLog notification = CommentNotificationLogHome
                .findByPrimaryKey( NumberUtils.toInt( request.getParameter( PARAMETER_ID_NOTIFICATION ), -1 ) );

        if ( notification == null || !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, Integer.toString( notification.getIdForm( ) ),
                AppointmentResourceIdService.PERMISSION_VIEW_FORM, (User) user ) )
        {
            return redirect( request, AppPathService.getBaseUrl( request ) + AppPathService.getAdminMenuUrl( ) );
        }

        CommentNotificationLogHome.markRead( notification.getId( ), user.getUserId( ) );

        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_APPOINTMENTS );
        url.addParameter( PARAMETER_VIEW, VIEW_CALENDAR_MANAGE_APPOINTMENTS );
        url.addParameter( PARAMETER_ID_FORM, notification.getIdForm( ) );

        if ( notification.getStartingValidityDate( ) != null )
        {
            url.addParameter( PARAMETER_DATE_OF_DISPLAY, notification.getStartingValidityDate( ).toString( ) );
        }

        return redirect( request, url.getUrl( ) );
    }
}
