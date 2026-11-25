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

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationLog;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationLogHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.service.CommentService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.SecurityTokenHandler;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The comment notifications in the notification zone of the admin header: the comments added, modified or removed
 * lately on the forms whose comments the user may see (the forms of his workgroups he may view), that he has not read.
 * Each entry is a small form posting the markRead action, so a clicked notification is marked as read and leaves the
 * zone. Renders nothing when nothing is unread, so the core shows neither the entry nor the badge of the bell.
 */
public class CommentNotificationDashboardComponent extends DashboardComponent
{
    private static final int MAX_NOTIFICATIONS = 5;
    private static final int MAX_TEXT_LENGTH = 80;

    // TEMPLATES
    private static final String TEMPLATE_DASHBOARD = "/admin/plugins/appointment/notification/comment_notification_dashboard.html";

    // MARKS
    private static final String MARK_NOTIFICATION_LIST = "notification_list";
    private static final String MARK_NB_NOTIFICATIONS = "nb_notifications";
    private static final String MARK_LABEL = "label";
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ID_FORM = "id_form";
    private static final String KEY_FORM_TITLE = "form_title";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_TEXT = "text";
    private static final String KEY_DATE = "date";

    // MESSAGES
    private static final String MESSAGE_LABEL_ONE = "appointment.commentNotification.labelOne";
    private static final String MESSAGE_LABEL_MANY = "appointment.commentNotification.labelMany";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDashboardData( AdminUser user, HttpServletRequest request )
    {
        Map<Integer, String> mapFormTitles = getAuthorizedFormTitles( user );
        List<CommentNotificationLog> listNotifications = CommentNotificationLogHome.findUnreadByIdFormsSince( mapFormTitles.keySet( ),
                CommentService.getNotificationStartDate( ), user.getUserId( ) );

        if ( listNotifications.isEmpty( ) )
        {
            return StringUtils.EMPTY;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT ).withLocale( user.getLocale( ) );
        Map<String, String> mapAuthors = new HashMap<>( );
        List<Map<String, Object>> listItems = new ArrayList<>( );

        for ( CommentNotificationLog notification : listNotifications.subList( 0, Math.min( MAX_NOTIFICATIONS, listNotifications.size( ) ) ) )
        {
            Map<String, Object> mapItem = new LinkedHashMap<>( );
            mapItem.put( KEY_ID, notification.getId( ) );
            mapItem.put( KEY_TYPE, notification.getType( ).name( ) );
            mapItem.put( KEY_ID_FORM, notification.getIdForm( ) );
            mapItem.put( KEY_FORM_TITLE, mapFormTitles.get( notification.getIdForm( ) ) );
            mapItem.put( KEY_AUTHOR, mapAuthors.computeIfAbsent( StringUtils.defaultString( notification.getCreatorUserName( ) ), this::getUserName ) );
            // the comment is rich text: its text, without the tags, is shortened
            mapItem.put( KEY_TEXT, StringUtils.abbreviate( Jsoup.parse( StringUtils.defaultString( notification.getComment( ) ) ).text( ), MAX_TEXT_LENGTH ) );
            mapItem.put( KEY_DATE, notification.getDateNotification( ).toLocalDateTime( ).format( formatter ) );
            listItems.add( mapItem );
        }

        int nCount = listNotifications.size( );
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_NOTIFICATION_LIST, listItems );
        model.put( MARK_NB_NOTIFICATIONS, nCount );
        model.put( MARK_LABEL, I18nService.getLocalizedString( ( nCount == 1 ) ? MESSAGE_LABEL_ONE : MESSAGE_LABEL_MANY, new Object [ ] {
                nCount
        }, user.getLocale( ) ) );
        // the token of the forms of the markRead action: the template service writes it in each of them
        model.put( SecurityTokenHandler.MARK_CSRF_TOKEN, getMarkReadToken( request ) );

        return AppTemplateService.getTemplate( TEMPLATE_DASHBOARD, user.getLocale( ), model ).getHtml( );
    }

    /**
     * Returns a token accepted by the markRead action. The token service also writes it in the models of the request,
     * which the page being rendered may use for its own forms: their value is put back.
     *
     * @param request
     *            the request
     * @return the token
     */
    private static String getMarkReadToken( HttpServletRequest request )
    {
        Instance<Models> instanceModels = CDI.current( ).select( Models.class );
        Models models = instanceModels.isResolvable( ) ? instanceModels.get( ) : null;
        Object previousToken = ( models != null ) ? models.get( SecurityTokenHandler.MARK_CSRF_TOKEN ) : null;

        String strToken = CDI.current( ).select( ISecurityTokenService.class ).get( ).getToken( request, AbstractAppointmentJspBean.SECURITY_TOKEN_ACTION );

        if ( models != null )
        {
            models.put( SecurityTokenHandler.MARK_CSRF_TOKEN, previousToken );
        }

        return strToken;
    }

    /**
     * The forms whose comments the user may see, as on their calendar: those of his workgroups he may view
     *
     * @param user
     *            the user
     * @return the titles of the forms, by id
     */
    private static Map<Integer, String> getAuthorizedFormTitles( AdminUser user )
    {
        List<AppointmentFormDTO> listForms = FormService.buildAllAppointmentFormLight( );
        listForms = (List<AppointmentFormDTO>) AdminWorkgroupService.getAuthorizedCollection( listForms, (User) user );

        return RBACService.getAuthorizedCollection( listForms, AppointmentResourceIdService.PERMISSION_VIEW_FORM, (User) user ).stream( )
                .collect( Collectors.toMap( AppointmentFormDTO::getIdForm, AppointmentFormDTO::getTitle, ( strTitle1, strTitle2 ) -> strTitle1 ) );
    }

    /**
     * The name of an admin user
     *
     * @param strLogin
     *            the login of the user
     * @return his first and last names, his login when he no longer exists
     */
    private String getUserName( String strLogin )
    {
        AdminUser user = StringUtils.isEmpty( strLogin ) ? null : AdminUserHome.findUserByLogin( strLogin );

        if ( user == null )
        {
            return strLogin;
        }

        return StringUtils.trim( StringUtils.defaultString( user.getFirstName( ) ) + StringUtils.SPACE + StringUtils.defaultString( user.getLastName( ) ) );
    }
}
