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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.appointment.service.comment.IRedirectComment;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.appointment.business.comment.Comment;
import fr.paris.lutece.plugins.appointment.business.comment.CommentHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.service.CommentService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.appointment.web.dto.CommentDTO;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * This class provides the user interface to manage CommentForm features ( manage, create, modify, copy, remove )
 *
 * @author rdeniel
 *
 */
@SessionScoped
@Named( "appointmentCommentJspBean" )
@Controller( controllerJsp = CommentJspBean.JSP_MANAGE_COMMENTS, controllerPath = "jsp/admin/plugins/appointment/", right = CommentJspBean.RIGHT_MANAGECOMMENTTFORM )
public class CommentJspBean extends AbstractAppointmentFormAndSlotJspBean
{
    /**
     * 
     */
    private static final long serialVersionUID = 9120042889405463752L;
    public static final String RIGHT_MANAGECOMMENTTFORM = "APPOINTMENT_COMMENT_MANAGEMENT";
    /**
     * JSP of this JSP Bean
     */
    public static final String JSP_MANAGE_COMMENTS = "Comments.jsp";

    // Templates
    public static final String TEMPLATE_CREATE_COMMENT = "/admin/plugins/appointment/comment/create_comment.html";
    public static final String TEMPLATE_MANAGE_COMMENT = "/admin/plugins/appointment/comment/manage_comment.html";
    public static final String TEMPLATE_MODIFY_COMMENT = "/admin/plugins/appointment/comment/modify_comment.html";
    public static final String TEMPLATE_COMMENT_INFO = "/admin/plugins/appointment/comment/comment_infos.html";

    // Messages
    private static final String MESSAGE_COMMENT_PAGE_TITLE = "appointment.comment.name";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "appointment.model.entity.appointmentform.attribute";

    // Parameters
    private static final String PARAMETER_ID_COMMENT = "id_comment";
    private static final String PARAMETER_COMMENT = "comment";
    private static final String PARAMETER_STARTING_VALIDITY_DATE = "startingValidityDate";
    private static final String PARAMETER_ENDING_VALIDITY_DATE = "endingValidityDate";
    private static final String PARAMETER_STARTING_VALIDITY_TIME = "startingValidityTime";
    private static final String PARAMETER_ENDING_VALIDITY_TIME = "endingValidityTime";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_FROM = "from";
    private static final String PARAMETER_ADDITIONAL_PARAMETERS = "additional_parameters";
    private static final String REFERER = "referer";
    private static final String PARAMETER_ID_MAILING_LIST = "idMailingList";

    // Marks
    private static final String MARK_COMMENT = "comment";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_COMMENT_LIST = "comment_list";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_MAILING_LIST = "mailing_list";

    // Views
    private static final String VIEW_ADD_COMMENT = "viewAddComment";
    private static final String VIEW_MODIFY_COMMENT = "viewModifyComment";
    private static final String VIEW_MANAGE_COMMENT = "manageComment";

    // Actions
    private static final String ACTION_DO_ADD_COMMENT = "doAddComment";
    private static final String ACTION_DO_REMOVE_COMMENT = "doRemoveComment";
    private static final String ACTION_DO_MODIFY_COMMENT = "doModifyComment";
    private static final String ACTION_CONFIRM_REMOVE_COMMENT = "confirmRemoveComment";

    // Properties
    private static final String PROPERTY_PAGE_TITLE_MANAGE_COMMENTS = "appointment.manage_comments.pageTitle";
    private static final String MESSAGE_CONFIRM_REMOVE_COMMENT = "appointment.message.confirmRemoveComment";

    // Infos
    private static final String INFO_COMMENT_CREATED = "appointment.info.comment.created";
    private static final String INFO_COMMENT_UPDATED = "appointment.info.comment.updated";
    private static final String INFO_COMMENT_REMOVED = "appointment.info.comment.removed";
    private static final String INFO_COMMENT_ERROR = "appointment.info.comment.error";

    // Session variable to store working values
    private Comment _comment;

    @Inject
    private Models _models;
    @Inject
    private Instance<IRedirectComment> _redirectComments;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     * @throws AccessDeniedException
     */
    @View( value = VIEW_MANAGE_COMMENT, defaultView = true )
    public String getManageComment( HttpServletRequest request )
    {
        _comment = null;
        List<CommentDTO> listComments = CommentService.buildCommentDTO( CommentHome.getCommentsList( ) );
        Map<String, Object> model = getPaginatedListModel( request, MARK_COMMENT_LIST, listComments, JSP_MANAGE_COMMENTS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_COMMENTS, TEMPLATE_MANAGE_COMMENT, model );
    }

    /**
     * Returns the form to create a comment
     *
     * @param request
     *            The Http request
     * @return the html code of the comment form
     * @throws AccessDeniedException
     */
    @View( VIEW_ADD_COMMENT )
    public String getViewAddComment( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = Integer.parseInt( strIdForm );

        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_ADD_COMMENT_FORM,
                (User) getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_ADD_COMMENT_FORM );
        }
        _comment = new Comment( );

        _models.put( MARK_COMMENT, _comment );
        _models.put( MARK_LOCALE, getLocale( ) );
        _models.put( PARAMETER_ID_FORM, nIdForm );
        _models.put( MARK_MAILING_LIST, AdminMailingListService.getMailingLists( getUser( ) ) );

        _models.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        return getPage( MESSAGE_COMMENT_PAGE_TITLE, TEMPLATE_CREATE_COMMENT );

    }

    /**
     * Process the data capture form of a new comment
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_DO_ADD_COMMENT )
    public String doAddComment( HttpServletRequest request ) throws AccessDeniedException
    {
        User user = getUser( );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = NumberUtils.toInt( strIdForm, -1 );
        if ( nIdForm < 0 )
        {
            return redirectView( request, VIEW_MANAGE_COMMENT );
        }
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_ADD_COMMENT_FORM,
                (User) getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_ADD_COMMENT_FORM );
        }
        _comment = new Comment( );
        _comment.setIdForm( nIdForm );
        _comment.setCreationDate( LocalDate.now( ) );
        _comment.setCreatorUserName( user.getAccessCode( ) );
        _comment.setComment( request.getParameter( PARAMETER_COMMENT ) );

        if ( !fillPeriod( _comment, request ) || !validateBean( _comment, VALIDATION_ATTRIBUTES_PREFIX ) || !validateDateStartEndValidity( _comment ) )
        {
            addError( INFO_COMMENT_ERROR, getLocale( ) );
        }
        else
        {
            CommentService.createAndNotifyMailingList( _comment, NumberUtils.toInt( request.getParameter( PARAMETER_ID_MAILING_LIST ), -1 ), getLocale( ) );
            addInfo( INFO_COMMENT_CREATED, getLocale( ) );
        }

        return redirectBack( request );
    }

    /**
     * Returns the form to modify a comment
     *
     * @param request
     *            The Http request
     * @return the html code of the comment form
     * @throws AccessDeniedException
     */
    @View( VIEW_MODIFY_COMMENT )
    public String getViewModifyComment( HttpServletRequest request ) throws AccessDeniedException
    {
        _comment = findAuthorizedComment( request );
        if ( _comment == null )
        {
            return redirectView( request, VIEW_MANAGE_COMMENT );
        }
        _models.put( MARK_COMMENT, _comment );
        _models.put( MARK_LOCALE, getLocale( ) );
        _models.put( PARAMETER_ID_FORM, _comment.getIdForm( ) );
        _models.put( MARK_MAILING_LIST, AdminMailingListService.getMailingLists( getUser( ) ) );
        _models.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );

        return getPage( MESSAGE_COMMENT_PAGE_TITLE, TEMPLATE_MODIFY_COMMENT );
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
        _comment = findAuthorizedComment( request );
        if ( _comment == null )
        {
            return redirectView( request, VIEW_MANAGE_COMMENT );
        }
        _comment.setComment( request.getParameter( PARAMETER_COMMENT ) );

        if ( !fillPeriod( _comment, request ) || !validateBean( _comment, VALIDATION_ATTRIBUTES_PREFIX ) || !validateDateStartEndValidity( _comment ) )
        {
            addError( INFO_COMMENT_ERROR, getLocale( ) );
        }
        else
        {
            CommentService.updateAndNotifyMailingList( _comment, NumberUtils.toInt( request.getParameter( PARAMETER_ID_MAILING_LIST ), -1 ), getLocale( ) );
            addInfo( INFO_COMMENT_UPDATED, getLocale( ) );
        }

        return redirectBack( request );
    }

    /**
     * Manages the removal form of a comment whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     * @throws AccessDeniedException
     */
    @Action( ACTION_CONFIRM_REMOVE_COMMENT )
    public String getConfirmRemoveComment( HttpServletRequest request ) throws AccessDeniedException
    {
        _comment = findAuthorizedComment( request );
        if ( _comment == null )
        {
            return redirectView( request, VIEW_MANAGE_COMMENT );
        }
        UrlItem url = new UrlItem( getActionUrl( ACTION_DO_REMOVE_COMMENT ) );
        url.addParameter( PARAMETER_ID_COMMENT, _comment.getId( ) );
        url.addParameter( PARAMETER_ID_MAILING_LIST, request.getParameter( PARAMETER_ID_MAILING_LIST ) );
        url.addParameter( PARAMETER_FROM, request.getParameter( PARAMETER_FROM ) );
        url.addParameter( PARAMETER_ADDITIONAL_PARAMETERS, request.getParameter( PARAMETER_ADDITIONAL_PARAMETERS ) );
        url.addParameter( PARAMETER_ID_FORM, _comment.getIdForm( ) );
        url.addParameter( REFERER, request.getHeader( REFERER ) );

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
        Comment comment = findAuthorizedComment( request );
        if ( comment == null )
        {
            return redirectView( request, VIEW_MANAGE_COMMENT );
        }
        CommentService.removeAndNotifyMailingList( comment.getId( ), NumberUtils.toInt( request.getParameter( PARAMETER_ID_MAILING_LIST ), -1 ), getLocale( ) );
        addInfo( INFO_COMMENT_REMOVED, getLocale( ) );

        return redirectBack( request );
    }

    /**
     * build The infos/warnings/Errors
     * 
     * @return The infos/warnings/Errors
     */
    public String getCommentInfos( )
    {

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_COMMENT_INFO, getLocale( ), _models );

        return template.getHtml( );
    }

    /**
     * Validate Comment date
     * 
     * @param comment
     *            the comment
     * @return boolean
     */
    public boolean validateDateStartEndValidity( Comment comment )
    {
        return !( comment.getStartingValidityDate( ).isAfter( comment.getEndingValidityDate( ) )
                || ( comment.getStartingValidityDate( ).isEqual( comment.getEndingValidityDate( ) ) && comment.getStartingValidityTime( ) != null
                        && comment.getStartingValidityTime( ).isAfter( comment.getEndingValidityTime( ) ) ) );

    }

    /**
     * Builds the back url: the page named by the `from` parameter, else the referer when it is a page of this site
     * outside the comment screens
     *
     * @param request the HttpServletRequest
     * @return the back url, or null
     */
    private String makeStringBackUrl( HttpServletRequest request )
    {
        String from = request.getParameter( PARAMETER_FROM );

        if ( StringUtils.isNotBlank( from ) )
        {
            IRedirectComment redirect = _redirectComments.stream( ).filter( ra -> from.equals( ra.getCodeFrom( ) ) ).findFirst( ).orElse( null );
            if ( redirect != null )
            {
                return redirect.makeBackUrl( request );
            }
        }

        String strReferer = request.getHeader( REFERER );

        return ( strReferer != null && strReferer.startsWith( AppPathService.getBaseUrl( request ) ) && !strReferer.contains( getControllerJsp( ) ) ) ? strReferer : null;
    }

    /**
     * Redirects to the page the comment was managed from, or to the list of comments
     *
     * @param request
     *            The request
     * @return The URL to redirect to
     */
    private String redirectBack( HttpServletRequest request )
    {
        String strBackUrl = makeStringBackUrl( request );

        return StringUtils.isNotBlank( strBackUrl ) ? redirect( request, strBackUrl ) : redirectView( request, VIEW_MANAGE_COMMENT );
    }

    /**
     * Loads the comment named by the request, checking that the user may moderate it or wrote it
     *
     * @param request
     *            The request
     * @return The comment, or null when the request names none that exists
     * @throws AccessDeniedException
     *             If the user may neither moderate the comments of its form nor is its author
     */
    private Comment findAuthorizedComment( HttpServletRequest request ) throws AccessDeniedException
    {
        Comment comment = CommentHome.findByPrimaryKey( NumberUtils.toInt( request.getParameter( PARAMETER_ID_COMMENT ), -1 ) );
        if ( comment == null )
        {
            return null;
        }
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, Integer.toString( comment.getIdForm( ) ),
                AppointmentResourceIdService.PERMISSION_MODERATE_COMMENT_FORM, (User) getUser( ) )
                && !StringUtils.equals( comment.getCreatorUserName( ), getUser( ).getAccessCode( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODERATE_COMMENT_FORM );
        }
        return comment;
    }

    /**
     * Sets the validity period of a comment from the request
     *
     * @param comment
     *            The comment
     * @param request
     *            The request
     * @return false when a date is missing or a date or time cannot be read
     */
    private static boolean fillPeriod( Comment comment, HttpServletRequest request )
    {
        String strStartDate = StringUtils.left( request.getParameter( PARAMETER_STARTING_VALIDITY_DATE ), 10 );
        String strEndDate = StringUtils.left( request.getParameter( PARAMETER_ENDING_VALIDITY_DATE ), 10 );
        String strStartTime = request.getParameter( PARAMETER_STARTING_VALIDITY_TIME );
        String strEndTime = request.getParameter( PARAMETER_ENDING_VALIDITY_TIME );
        if ( StringUtils.isAnyBlank( strStartDate, strEndDate ) )
        {
            return false;
        }
        try
        {
            comment.setStartingValidityDate( LocalDate.parse( strStartDate ) );
            comment.setEndingValidityDate( LocalDate.parse( strEndDate ) );
            if ( StringUtils.isNotBlank( strStartTime ) )
            {
                comment.setStartingValidityTime( LocalTime.parse( strStartTime ) );
            }
            if ( StringUtils.isNotBlank( strEndTime ) )
            {
                comment.setEndingValidityTime( LocalTime.parse( strEndTime ) );
            }
        }
        catch( DateTimeParseException e )
        {
            return false;
        }
        return true;
    }

}
