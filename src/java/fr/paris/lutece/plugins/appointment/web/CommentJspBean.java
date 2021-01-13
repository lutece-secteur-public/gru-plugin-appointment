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
package fr.paris.lutece.plugins.appointment.web;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.appointment.business.comment.Comment;
import fr.paris.lutece.plugins.appointment.business.comment.CommentHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage CommentForm features ( manage, create, modify, copy, remove )
 * 
 * @author rdeniel
 * 
 */
@Controller( controllerJsp = CommentJspBean.JSP_MANAGE_COMMENTS, controllerPath = "jsp/admin/plugins/appointment/", right = CommentJspBean.RIGHT_MANAGECOMMENTTFORM )
public class CommentJspBean extends AbstractAppointmentFormAndSlotJspBean
{
    /**
     * 
     */
    private static final long serialVersionUID = 9120042889405463752L;
    public static final String RIGHT_MANAGECOMMENTTFORM = "APPOINTMENT_FORM_MANAGEMENT";
    /**
     * JSP of this JSP Bean
     */
    public static final String JSP_MANAGE_APPOINTMENT_SLOTS = "ManageAppointmentSlots.jsp";
    public static final String JSP_MANAGE_COMMENTS = "Comments.jsp";

    // Templates
    public static final String TEMPLATE_CREATE_COMMENT = "/admin/plugins/appointment/comment/create_comment.html";
    public static final String TEMPLATE_MANAGE_COMMENT = "/admin/plugins/appointment/comment/manage_comment.html";
    public static final String TEMPLATE_MODIFY_COMMENT = "/admin/plugins/appointment/comment/modify_comment.html";
    public static final String TEMPLATE_COMMENT_INFO = "/admin/plugins/appointment/comment/comment_infos.html";

    // Messages
    private static final String MESSAGE_COMMENT_PAGE_TITLE = "appointment.comment.pageTitle";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "appointment.model.entity.appointmentform.attribute";

    // Parameters
    private static final String PARAMETER_ID_COMMENT = "id_comment";
    private static final String PARAMETER_COMMENT = "comment";
    private static final String PARAMETER_STARTING_VALIDITY_DATE = "startingValidityDate";
    private static final String PARAMETER_ENDING_VALIDITY_DATE = "endingValidityDate";
    private static final String PARAMETER_STARTING_VALIDITY_TIME = "startingValidityTime";
    private static final String PARAMETER_ENDING_VALIDITY_TIME = "endingValidityTime";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String REFERER = "referer";

    // Marks
    private static final String MARK_COMMENT = "comment";
    private static final String MARK_COMMENT_LIST = "comment_list";
    private static final String MARK_LOCALE = "locale";

    // Views
    private static final String VIEW_ADD_COMMENT = "viewAddComment";
    private static final String VIEW_MODIFY_COMMENT = "viewModifyComment";
    private static final String VIEW_MANAGE_COMMENT = "manageComment";
    private static final String VIEW_CALENDAR_MANAGE_APPOINTMENTS = "viewCalendarManageAppointment";

    // Actions
    private static final String ACTION_DO_ADD_COMMENT = "doAddComment";
    private static final String ACTION_DO_REMOVE_COMMENT = "doRemoveComment";
    private static final String ACTION_DO_MODIFY_COMMENT = "doModifyComment";
    private static final String ACTION_CONFIRM_REMOVE_COMMENT = "confirmRemoveComment";

    // Properties
    private static final String PROPERTY_PAGE_TITLE_MANAGE_COMMENTS = "appointment-comment.manage_comments.pageTitle";
    private static final String MESSAGE_CONFIRM_REMOVE_COMMENT = "appointment.message.confirmRemoveComment";

    // Infos
    private static final String INFO_COMMENT_CREATED = "appointment.info.comment.created";
    private static final String INFO_COMMENT_UPDATED = "appointment.info.comment.updated";
    private static final String INFO_COMMENT_REMOVED = "appointment.info.comment.removed";
    private static final String INFO_COMMENT_ERROR = "appointment.info.comment.error";
    private static final String INFO_COMMENT_RIGHTS = "info.comment.rights";

    // Session variable to store working values
    private Comment _comment;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( VIEW_MANAGE_COMMENT )
    public String getManageComment( HttpServletRequest request )
    {

        _comment = null;
        List<Comment> listComments = CommentHome.getCommentsList( );
        Map<String, Object> model = getPaginatedListModel( request, MARK_COMMENT_LIST, listComments, JSP_MANAGE_APPOINTMENT_SLOTS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_COMMENTS, TEMPLATE_MANAGE_COMMENT, model );
    }

    /**
     * Returns the form to create a comment
     *
     * @param request
     *            The Http request
     * @return the html code of the comment form
     */
    @View( VIEW_ADD_COMMENT )
    public String getViewAddComment( HttpServletRequest request )
    {
        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        _comment = new Comment( );

        Map<String, Object> model = getModel( );
        model.put( MARK_COMMENT, _comment );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( PARAMETER_ID_FORM, nIdForm );
        return getPage( MESSAGE_COMMENT_PAGE_TITLE, TEMPLATE_CREATE_COMMENT, model );

    }

    /**
     * Process the data capture form of a new comment
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_DO_ADD_COMMENT )
    public String doAddComment( HttpServletRequest request )
    {
        User user = (User) getUser( );
        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        String strReferer = request.getHeader( REFERER );

        _comment = ( _comment == null ) ? new Comment( ) : _comment;

        _comment.setIdForm( nIdForm );
        _comment.setCreationDate( LocalDate.now( ) );
        _comment.setCreatorUserName( user.getAccessCode( ) );
        _comment.setComment( request.getParameter( PARAMETER_COMMENT ) );
        _comment.setStartingValidityDate( DateUtil.formatDate( request.getParameter( PARAMETER_STARTING_VALIDITY_DATE ), getLocale( ) ).toInstant( )
                .atZone( ZoneId.systemDefault( ) ).toLocalDate( ) );
        _comment.setEndingValidityDate( DateUtil.formatDate( request.getParameter( PARAMETER_ENDING_VALIDITY_DATE ), getLocale( ) ).toInstant( )
                .atZone( ZoneId.systemDefault( ) ).toLocalDate( ) );
        if ( !request.getParameter( PARAMETER_STARTING_VALIDITY_TIME ).isEmpty( ) )
        {
            _comment.setStartingValidityTime( LocalTime.parse( request.getParameter( PARAMETER_STARTING_VALIDITY_TIME ) ) );
        }

        if ( !request.getParameter( PARAMETER_ENDING_VALIDITY_TIME ).isEmpty( ) )
        {
            _comment.setEndingValidityTime( LocalTime.parse( request.getParameter( PARAMETER_ENDING_VALIDITY_TIME ) ) );
        }

        // Check constraints
        if ( !validateBean( _comment, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            addError( INFO_COMMENT_ERROR, getLocale( ) );

        }
        else
        {

            addInfo( INFO_COMMENT_CREATED, getLocale( ) );
            CommentHome.create( _comment );
        }

        if ( StringUtils.isNotBlank( strReferer ) )
        {
            return redirect( request, strReferer );
        }

        return redirect( request, VIEW_MANAGE_COMMENT );
    }

    /**
     * Returns the form to modify a comment
     *
     * @param request
     *            The Http request
     * @return the html code of the comment form
     */
    @View( VIEW_MODIFY_COMMENT )
    public String getViewModifyComment( HttpServletRequest request )
    {
        User user = (User) getUser( );
        int nIdComment = Integer.parseInt( request.getParameter( PARAMETER_ID_COMMENT ) );
        _comment = CommentHome.findByPrimaryKey( nIdComment );

        if ( _comment.getCreatorUserName( ).equals( user.getAccessCode( ) ) )
        {
            Map<String, Object> model = getModel( );
            model.put( MARK_COMMENT, _comment );
            model.put( MARK_LOCALE, getLocale( ) );
            model.put( PARAMETER_ID_FORM, _comment.getIdForm( ) );
            return getPage( MESSAGE_COMMENT_PAGE_TITLE, TEMPLATE_MODIFY_COMMENT, model );
        }

        return getPage( MESSAGE_COMMENT_PAGE_TITLE, VIEW_CALENDAR_MANAGE_APPOINTMENTS );

    }

    /**
     * Process the data capture form of comment modification
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_DO_MODIFY_COMMENT )
    public String doModifyComment( HttpServletRequest request ) throws AccessDeniedException
    {
        User user = (User) getUser( );
        int nIdComment = Integer.parseInt( request.getParameter( PARAMETER_ID_COMMENT ) );
        String strReferer = request.getHeader( REFERER );

        if ( _comment == null || _comment.getId( ) != nIdComment )
        {
            _comment = CommentHome.findByPrimaryKey( nIdComment );
        }

        _comment.setComment( request.getParameter( PARAMETER_COMMENT ) );
        _comment.setStartingValidityDate( DateUtil.formatDate( request.getParameter( PARAMETER_STARTING_VALIDITY_DATE ), getLocale( ) ).toInstant( )
                .atZone( ZoneId.systemDefault( ) ).toLocalDate( ) );
        _comment.setEndingValidityDate( DateUtil.formatDate( request.getParameter( PARAMETER_ENDING_VALIDITY_DATE ), getLocale( ) ).toInstant( )
                .atZone( ZoneId.systemDefault( ) ).toLocalDate( ) );
        if ( !request.getParameter( PARAMETER_STARTING_VALIDITY_TIME ).isEmpty( ) )
        {
            _comment.setStartingValidityTime( LocalTime.parse( request.getParameter( PARAMETER_STARTING_VALIDITY_TIME ) ) );
        }

        if ( !request.getParameter( PARAMETER_ENDING_VALIDITY_TIME ).isEmpty( ) )
        {
            _comment.setEndingValidityTime( LocalTime.parse( request.getParameter( PARAMETER_ENDING_VALIDITY_TIME ) ) );
        }

        // Check constraints
        if ( !validateBean( _comment, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            addError( INFO_COMMENT_ERROR, getLocale( ) );
        }

        else
            if ( _comment.getCreatorUserName( ).equals( user.getAccessCode( ) ) )
            {
                CommentHome.update( _comment );
                addInfo( INFO_COMMENT_UPDATED, getLocale( ) );
            }
            else
            {

                throw new AccessDeniedException( INFO_COMMENT_RIGHTS );

            }

        if ( StringUtils.isNotBlank( strReferer ) )
        {
            return redirect( request, strReferer );
        }

        return redirect( request, VIEW_MANAGE_COMMENT );

    }

    /**
     * Manages the removal form of a comment whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_COMMENT )
    public String getConfirmRemoveComment( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_COMMENT ) );
        String strReferer = request.getHeader( REFERER );

        UrlItem url = new UrlItem( getActionUrl( ACTION_DO_REMOVE_COMMENT ) );
        url.addParameter( PARAMETER_ID_COMMENT, nId );
        url.addParameter( REFERER, strReferer );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_COMMENT, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Do remove a comment
     * 
     * @param request
     *            the request
     * @return to the page of the comment
     * @throws AccessDeniedException
     */
    @Action( ACTION_DO_REMOVE_COMMENT )
    public String doRemoveComment( HttpServletRequest request ) throws AccessDeniedException
    {
        User user = (User) getUser( );
        int nIdComment = Integer.parseInt( request.getParameter( PARAMETER_ID_COMMENT ) );
        String strReferer = request.getParameter( REFERER );
        UrlItem url = new UrlItem( strReferer );
        url.addParameter( PARAMETER_ID_FORM, _comment.getIdForm( ) );

        _comment = CommentHome.findByPrimaryKey( nIdComment );

        if ( _comment.getCreatorUserName( ).equals( user.getAccessCode( ) ) )
        {
            CommentHome.remove( nIdComment );
            addInfo( INFO_COMMENT_REMOVED, getLocale( ) );
        }
        else
        {
            throw new AccessDeniedException( INFO_COMMENT_RIGHTS );
        }
        if ( StringUtils.isNotBlank( strReferer ) )
        {
            return redirect( request, url.getUrl( ) );
        }

        return redirect( request, VIEW_MANAGE_COMMENT );
    }

    /**
     * build The infos/warnings/Errors
     * 
     * @return The infos/warnings/Errors
     */
    public String getCommentInfos( )
    {

        Map<String, Object> model = getModel( );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_COMMENT_INFO, getLocale( ), model );

        return template.getHtml( );
    }

}
