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

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.calendar.CalendarTemplateHome;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.message.FormMessage;
import fr.paris.lutece.plugins.appointment.business.message.FormMessageHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.log.LogUtilities;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.AppointmentUtilities;
import fr.paris.lutece.plugins.appointment.service.CategoryService;
import fr.paris.lutece.plugins.appointment.service.ClosingDayService;
import fr.paris.lutece.plugins.appointment.service.FormMessageService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.Utilities;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage AppointmentForm features ( manage, create, modify, copy, remove )
 * 
 * @author L.Payen
 * 
 */
@Controller( controllerJsp = "ManageAppointmentForms.jsp", controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM )
public class AppointmentFormJspBean extends AbstractAppointmentFormAndSlotJspBean
{

    /**
     * Right to manage appointment forms
     */
    public static final String RIGHT_MANAGEAPPOINTMENTFORM = "APPOINTMENT_FORM_MANAGEMENT";
    private static final long serialVersionUID = -615061018633136997L;

    // templates
    private static final String TEMPLATE_MANAGE_APPOINTMENTFORMS = "/admin/plugins/appointment/appointmentform/manage_appointmentforms.html";
    private static final String TEMPLATE_CREATE_APPOINTMENTFORM = "/admin/plugins/appointment/appointmentform/create_appointmentform.html";
    private static final String TEMPLATE_MODIFY_APPOINTMENTFORM = "/admin/plugins/appointment/appointmentform/modify_appointmentform.html";
    private static final String TEMPLATE_MODIFY_APPOINTMENTFORM_MESSAGES = "/admin/plugins/appointment/appointmentform/modify_appointmentform_messages.html";

    // Parameters
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_BACK = "back";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_FROM_DASHBOARD = "fromDashboard";
    private static final String PARAMETER_ICON_RESSOURCE = "image_resource";
    private static final String PARAMETER_DELETE_ICON = "deleteIcon";
    private static final String PARAMETER_GEOLOC_ADDRESS = "geoloc_address";
    private static final String PARAMETER_GEOLOC_LATITUDE = "geoloc_latitude";
    private static final String PARAMETER_GEOLOC_LONGITUDE = "geoloc_longitude";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTFORMS = "appointment.manage.appointmentforms.title";
    private static final String PROPERTY_PAGE_TITLE_GENERAL_SETTINGS = "appointment.modifyAppointmentForm.titleAlterablesParameters";
    private static final String PROPERTY_PAGE_TITLE_CREATE_APPOINTMENTFORM = "appointment.manage.appointmentforms.title";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENTFORM_MESSAGES = "appointment.modifyAppointmentFormMessages.pageTitle";

    // Markers
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_APPOINTMENTFORM_LIST = "appointmentform_list";
    private static final String MARK_APPOINTMENT_FORM = "appointmentform";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_LIST_WORKFLOWS = "listWorkflows";
    private static final String MARK_IS_CAPTCHA_ENABLED = "isCaptchaEnabled";
    private static final String MARK_FORM_MESSAGE = "formMessage";
    private static final String MARK_REF_LIST_CALENDAR_TEMPLATES = "refListCalendarTemplates";
    private static final String MARK_REF_LIST_ROLES = "refListRoles";
    private static final String MARK_LIST_CATEGORIES = "listCategories";
    private static final String MARK_NULL = "NULL";
    private static final String MARK_FALSE = "false";
    private static final String MARK_LOCALE = "language";
    private static final String MARK_LOCALE_TINY = "locale";
    private static final String MARK_FILE_CLOSING_DAYS = "fileClosingDays";
    private static final String MARK_USER_WORKGROUP_REF_LIST = "user_workgroup_list";
    private static final String MARK_APPOINTMENT_RESOURCE_ENABLED = "isResourceInstalled";
    private static final String MARK_APPOINTMENT_DESK_ENABLED = "isDeskInstalled";

    private static final String MARK_PERMISSION_CREATE = "permission_create";

    // Jsp
    private static final String JSP_MANAGE_APPOINTMENTFORMS = "jsp/admin/plugins/appointment/ManageAppointmentForms.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_APPOINTMENTFORM = "appointment.message.confirmRemoveAppointmentForm";
    public static final String PROPERTY_DEFAULT_LIST_APPOINTMENTFORM_PER_PAGE = "appointment.listAppointmentForms.itemsPerPage";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "appointment.model.entity.appointmentform.attribute.";
    private static final String PROPERTY_MODULE_APPOINTMENT_RESOURCE_NAME = "appointment.moduleAppointmentResource.name";
    private static final String PROPERTY_MODULE_APPOINTMENT_DESK_NAME = "appointment.moduleAppointmentDesk.name";

    private static final String PROPERTY_COPY_OF_FORM = "appointment.manageAppointmentForms.Copy";
    private static final String MESSAGE_ERROR_EMPTY_FILE = "appointment.message.error.closingDayErrorImport";
    private static final String MESSAGE_ERROR_OPEN_SLOTS = "appointment.message.error.openSlots";
    private static final String MESSAGE_INFO_IMPORTED_CLOSING_DAYS = "appointment.info.appointmentform.closingDayImport";
    private static final String MESSAGE_ERROR_CANT_REMOVE_FORM = "appointment.message.cantRemove.form";
    private static final String MESSAGE_ERROR_CANT_REMOVE_ACTIVE_FORM = "appointment.message.cantRemove.activate.form";

    // Views
    private static final String VIEW_MANAGE_APPOINTMENTFORMS = "manageAppointmentForms";
    private static final String VIEW_CREATE_APPOINTMENTFORM = "createAppointmentForm";
    private static final String VIEW_MODIFY_APPOINTMENTFORM = "modifyAppointmentForm";
    private static final String VIEW_MODIFY_FORM_MESSAGES = "modifyAppointmentFormMessages";
    private static final String VIEW_PERMISSIONS_FORM = "permissions";

    // Actions
    private static final String ACTION_CREATE_APPOINTMENTFORM = "createAppointmentForm";
    private static final String ACTION_MODIFY_APPOINTMENTFORM = "modifyAppointmentForm";
    private static final String ACTION_REMOVE_APPOINTMENTFORM = "removeAppointmentForm";
    private static final String ACTION_CONFIRM_REMOVE_APPOINTMENTFORM = "confirmRemoveAppointmentForm";
    private static final String ACTION_DO_CHANGE_FORM_ACTIVATION = "doChangeFormActivation";
    private static final String ACTION_DO_MODIFY_FORM_MESSAGES = "doModifyAppointmentFormMessages";
    private static final String ACTION_DO_COPY_FORM = "doCopyAppointmentForm";

    // Infos
    private static final String INFO_APPOINTMENTFORM_CREATED = "appointment.info.appointmentform.created";
    private static final String INFO_APPOINTMENTFORM_UPDATED = "appointment.info.appointmentform.updated";
    private static final String INFO_APPOINTMENTFORM_REMOVED = "appointment.info.appointmentform.removed";
    private static final String INFO_APPOINTMENTFORM_MESSAGES_MODIFIED = "appointment.info.appointmentFormMessages.updated";
    private static final String ERROR_APPOINTMENTFORM_ENDING_VALIDITY_DATE_BEFORE_NOW = "appointment.error.appointmentform.endingValidityDateBeforeNow";

    private static final String DEFAULT_CURRENT_PAGE = "1";

    // Session variable to store working values
    private static final CaptchaSecurityService _captchaSecurityService = new CaptchaSecurityService( );
    private int _nDefaultItemsPerPage;
    private AppointmentFormDTO _appointmentFormDTO;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    /**
     * Default constructor
     */
    public AppointmentFormJspBean( )
    {
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_APPOINTMENTFORM_PER_PAGE, 50 );
    }

    /**
     * Get the page to manage appointment forms
     * 
     * @param request
     *            the request
     * @return The HTML content to display
     */
    @View( value = VIEW_MANAGE_APPOINTMENTFORMS, defaultView = true )
    public String getManageAppointmentForms( HttpServletRequest request )
    {
        AdminUser adminUser = getUser( );
        _appointmentFormDTO = null;
        _strCurrentPageIndex = AbstractPaginator.getPageIndex( request, AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        if ( _strCurrentPageIndex == null )
        {
            _strCurrentPageIndex = DEFAULT_CURRENT_PAGE;
        }
        _nItemsPerPage = AbstractPaginator.getItemsPerPage( request, AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( JSP_MANAGE_APPOINTMENTFORMS );
        String strUrl = url.getUrl( );
        List<AppointmentFormDTO> listAppointmentForm = FormService.buildAllAppointmentFormLight( );
        listAppointmentForm = (List<AppointmentFormDTO>) AdminWorkgroupService.getAuthorizedCollection( listAppointmentForm, (User) adminUser );
        listAppointmentForm = listAppointmentForm.stream( ).sorted( ( a1, a2 ) -> a1.getTitle( ).compareTo( a2.getTitle( ) ) ).collect( Collectors.toList( ) );
        LocalizedPaginator<AppointmentFormDTO> paginator = new LocalizedPaginator<>( listAppointmentForm, _nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex, getLocale( ) );
        AdminUser user = AdminUserService.getAdminUser( request );
        Map<String, Object> model = getModel( );
        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( _nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_APPOINTMENTFORM_LIST, RBACService.getAuthorizedCollection( paginator.getPageItems( ), AppointmentResourceIdService.PERMISSION_VIEW_FORM,
                (User) AdminUserService.getAdminUser( request ) ) );
        model.put( VIEW_PERMISSIONS_FORM, AppointmentUtilities.getPermissions( paginator.getPageItems( ), user ) );
        model.put( MARK_PERMISSION_CREATE, String.valueOf(
                RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE_CREATE, "0", AppointmentResourceIdService.PERMISSION_CREATE_FORM, (User) user ) ) );
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTFORMS, TEMPLATE_MANAGE_APPOINTMENTFORMS, model );
    }

    /**
     * Returns the form to create an appointment form
     *
     * @param request
     *            The HTTP request
     * @return the HTML code of the appointment form
     * @throws AccessDeniedException
     *             If the user is not authorized to create appointment forms
     */
    @View( VIEW_CREATE_APPOINTMENTFORM )
    public String getCreateAppointmentForm( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE_CREATE, "0", AppointmentResourceIdService.PERMISSION_CREATE_FORM,
                (User) AdminUserService.getAdminUser( request ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_CREATE_FORM );
        }
        if ( _appointmentFormDTO == null || _appointmentFormDTO.getIdForm( ) != 0 )
        {
            _appointmentFormDTO = new AppointmentFormDTO( );
        }

        Map<String, Object> model = getModel( );
        addElementsToModel( _appointmentFormDTO, getUser( ), getLocale( ), model );
        return getPage( PROPERTY_PAGE_TITLE_CREATE_APPOINTMENTFORM, TEMPLATE_CREATE_APPOINTMENTFORM, model );
    }

    /**
     * Process the data capture form of a new appointment form
     * 
     * @param request
     *            The HTTP Request
     * @return The JSP URL of the process result
     * @throws AccessDeniedException
     *             If the user is not authorized to create appointment forms
     */
    @Action( ACTION_CREATE_APPOINTMENTFORM )
    public String doCreateAppointmentForm( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE_CREATE, "0", AppointmentResourceIdService.PERMISSION_CREATE_FORM,
                (User) AdminUserService.getAdminUser( request ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_CREATE_FORM );
        }
        if ( _appointmentFormDTO == null )
        {
            _appointmentFormDTO = new AppointmentFormDTO( );
        }
        populate( _appointmentFormDTO, request );
        populateAddress( _appointmentFormDTO, request );
        if ( !validateBean( _appointmentFormDTO, VALIDATION_ATTRIBUTES_PREFIX ) || !validateReservationRuleBean( request, VALIDATION_ATTRIBUTES_PREFIX )
                || !checkConstraints( _appointmentFormDTO ) )
        {
            return redirectView( request, VIEW_CREATE_APPOINTMENTFORM );
        }
        _appointmentFormDTO.setIcon( buildImageResource( (MultipartHttpServletRequest) request ) );
        int nIdForm = FormService.createAppointmentForm( _appointmentFormDTO );
        AppLogService.info( LogUtilities.buildLog( ACTION_CREATE_APPOINTMENTFORM, Integer.toString( nIdForm ), getUser( ) ) );
        addInfo( INFO_APPOINTMENTFORM_CREATED, getLocale( ) );
        return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
    }

    /**
     * Manages the removal form of a appointment form whose identifier is in the HTTP request
     * 
     * @param request
     *            The HTTP request
     * @return the HTML code to confirm
     * @throws AccessDeniedException
     *             If the user is not authorized to delete this appointment form
     */
    @Action( ACTION_CONFIRM_REMOVE_APPOINTMENTFORM )
    public String getConfirmRemoveAppointmentForm( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( StringUtils.isEmpty( strIdForm ) )
        {
            return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
        }
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_DELETE_FORM,
                (User) AdminUserService.getAdminUser( request ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_DELETE_FORM );
        }
        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_APPOINTMENTFORM ) );
        url.addParameter( PARAMETER_ID_FORM, nIdForm );
        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_APPOINTMENTFORM, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );
        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of an appointment form
     * 
     * @param request
     *            The HTTP request
     * @return the JSP URL to display the form to manage appointment forms
     * @throws AccessDeniedException
     *             If the user is not authorized to delete this appointment form
     */
    @Action( ACTION_REMOVE_APPOINTMENTFORM )
    public String doRemoveAppointmentForm( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( StringUtils.isEmpty( strIdForm ) )
        {
            return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
        }
        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        Form form = FormHome.findByPrimaryKey( nIdForm );

        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, request.getParameter( PARAMETER_ID_FORM ),
                AppointmentResourceIdService.PERMISSION_DELETE_FORM, (User) AdminUserService.getAdminUser( request ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_DELETE_FORM );
        }

        if ( form.getIsActive( ) )
        {

            addError( MESSAGE_ERROR_CANT_REMOVE_ACTIVE_FORM, getLocale( ) );
            return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
        }
        if ( CollectionUtils.isNotEmpty( AppointmentService.findListAppointmentByIdForm( nIdForm ) ) )
        {
            addError( MESSAGE_ERROR_CANT_REMOVE_FORM, getLocale( ) );
            return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
        }

        FormService.removeForm( nIdForm );
        AppLogService.info( LogUtilities.buildLog( ACTION_REMOVE_APPOINTMENTFORM, strIdForm, getUser( ) ) );
        addInfo( INFO_APPOINTMENTFORM_REMOVED, getLocale( ) );
        return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
    }

    /**
     * Returns the form to update info about a appointment form
     * 
     * @param request
     *            The HTTP request
     * @return The HTML form to update info
     * @throws AccessDeniedException
     *             If the user is not authorized to modify this appointment form
     */
    @View( VIEW_MODIFY_APPOINTMENTFORM )
    public String getModifyAppointmentForm( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_MODIFY_FORM,
                (User) AdminUserService.getAdminUser( request ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_FORM );
        }
        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        if ( ( _appointmentFormDTO == null ) || ( nIdForm != _appointmentFormDTO.getIdForm( ) ) )
        {
            _appointmentFormDTO = FormService.buildAppointmentForm( nIdForm, 0 );
        }
        Map<String, Object> model = getModel( );
        addElementsToModel( _appointmentFormDTO, getUser( ), getLocale( ), model );
        return getPage( PROPERTY_PAGE_TITLE_GENERAL_SETTINGS, TEMPLATE_MODIFY_APPOINTMENTFORM, model );
    }

    /**
     * Process the change form of a appointment form
     * 
     * @param request
     *            The HTTP request
     * @return The JSP URL of the process result
     * @throws AccessDeniedException
     *             If the user is not authorized to modify this appointment form
     */
    @Action( ACTION_MODIFY_APPOINTMENTFORM )
    public String doModifyAppointmentForm( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_MODIFY_FORM,
                (User) AdminUserService.getAdminUser( request ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_FORM );
        }
        int nIdForm = Integer.parseInt( strIdForm );
        if ( ( _appointmentFormDTO == null ) || ( nIdForm != _appointmentFormDTO.getIdForm( ) ) )
        {
            _appointmentFormDTO = FormService.buildAppointmentFormLight( nIdForm );
        }
        populate( _appointmentFormDTO, request );
        populateAddress( _appointmentFormDTO, request );
        AppointmentFormDTO appointmentFormDb = FormService.buildAppointmentForm( nIdForm, 0 );
        String strDeleteIcon = ( request.getParameter( PARAMETER_DELETE_ICON ) == null ) ? MARK_FALSE : request.getParameter( PARAMETER_DELETE_ICON );
        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        if ( Boolean.parseBoolean( strDeleteIcon ) && ( _appointmentFormDTO.getIcon( ).getImage( ) != null ) )
        {
            ImageResource img = new ImageResource( );
            img.setImage( null );
            img.setMimeType( null );
            _appointmentFormDTO.setIcon( img );
        }
        else
        {
            ImageResource imageResource = buildImageResource( mRequest );
            if ( !StringUtils.equals( MARK_NULL, imageResource.getMimeType( ) ) )
            {
                _appointmentFormDTO.setIcon( imageResource );
            }
        }
        // Import of the closing days file
        importClosingDayFile( mRequest, nIdForm );
        setParametersDays( _appointmentFormDTO, appointmentFormDb );
        if ( !validateBean( _appointmentFormDTO, VALIDATION_ATTRIBUTES_PREFIX ) || !checkStartingAndEndingValidityDate( _appointmentFormDTO )
                || checkControlMaxAppointmentsPerUser( _appointmentFormDTO ) )
        {
            return redirect( request, VIEW_MODIFY_APPOINTMENTFORM, PARAMETER_ID_FORM, nIdForm );
        }
        _appointmentFormDTO.setIsActive( appointmentFormDb.getIsActive( ) );
        FormService.updateGlobalParameters( _appointmentFormDTO );
        _appointmentFormDTO = null;
        AppLogService.info( LogUtilities.buildLog( ACTION_MODIFY_APPOINTMENTFORM, strIdForm, getUser( ) ) );
        addInfo( INFO_APPOINTMENTFORM_UPDATED, getLocale( ) );
        return redirect( request, VIEW_MODIFY_APPOINTMENTFORM, PARAMETER_ID_FORM, nIdForm );
    }

    /**
     * Change the enabling of an appointment form
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     * @throws AccessDeniedException
     *             If the user is not authorized to change the activation of this appointment form
     */
    @Action( ACTION_DO_CHANGE_FORM_ACTIVATION )
    public String doChangeFormActivation( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( StringUtils.isEmpty( strIdForm ) )
        {
            return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
        }
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_CHANGE_STATE,
                (User) AdminUserService.getAdminUser( request ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_CHANGE_STATE );
        }
        int nIdForm = Integer.parseInt( strIdForm );
        Form form = FormService.findFormLightByPrimaryKey( nIdForm );
        if ( form != null )
        {
            if ( !form.getIsActive( ) )
            {
                if ( form.getStartingValidityDate( ) == null )
                {
                    form.setStartingValidityDate( LocalDate.now( ) );
                }
                if ( ( form.getEndingValidityDate( ) != null ) && ( form.getEndingValidityDate( ).isBefore( LocalDate.now( ) ) ) )
                {
                    addError( ERROR_APPOINTMENTFORM_ENDING_VALIDITY_DATE_BEFORE_NOW, getLocale( ) );
                    if ( Boolean.TRUE.equals( Boolean.valueOf( request.getParameter( PARAMETER_FROM_DASHBOARD ) ) ) )
                    {
                        return redirect( request, AppPathService.getBaseUrl( request ) + AppPathService.getAdminMenuUrl( ) );
                    }
                    else
                    {
                        return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
                    }
                }
            }
            else
            {
                form.setStartingValidityDate( null );
            }
            form.setIsActive( !form.getIsActive( ) );
            FormService.updateForm( form );
            AppLogService.info( LogUtilities.buildLog( ACTION_DO_CHANGE_FORM_ACTIVATION, strIdForm, getUser( ) ) );
        }
        if ( Boolean.TRUE.equals( Boolean.valueOf( request.getParameter( PARAMETER_FROM_DASHBOARD ) ) ) )
        {
            return redirect( request, AppPathService.getBaseUrl( request ) + AppPathService.getAdminMenuUrl( ) );
        }
        return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
    }

    /**
     * Do copy a form
     * 
     * @param request
     *            the request
     * @return the JSP URL to display the form to manage appointment forms
     * @throws AccessDeniedException
     */
    @Action( ACTION_DO_COPY_FORM )
    public String doCopyAppointmentForm( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE_CREATE, "0", AppointmentResourceIdService.PERMISSION_CREATE_FORM,
                (User) AdminUserService.getAdminUser( request ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_CREATE_FORM );
        }
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( StringUtils.isEmpty( strIdForm ) )
        {
            return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
        }
        int nIdForm = Integer.parseInt( strIdForm );
        Form formToCopy = FormService.findFormLightByPrimaryKey( nIdForm );
        if ( formToCopy != null )
        {
            String newNameForCopy = I18nService.getLocalizedString( PROPERTY_COPY_OF_FORM, request.getLocale( ) ) + formToCopy.getTitle( );
            int nIdCopyForm = FormService.copyForm( nIdForm, newNameForCopy );
            AppLogService.info( LogUtilities.buildLog( ACTION_DO_COPY_FORM, String.valueOf( nIdCopyForm ), getUser( ) ) );
        }
        return getManageAppointmentForms( request );
    }

    /**
     * Get the page to modify an appointment form message
     * 
     * @param request
     *            The request
     * @return The HTML content to display
     * @throws AccessDeniedException
     *             If the user is not authorized to modify this appointment form
     */
    @View( VIEW_MODIFY_FORM_MESSAGES )
    public String getModifyAppointmentFormMessages( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( StringUtils.isEmpty( strIdForm ) )
        {
            return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
        }
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_MODIFY_FORM,
                (User) AdminUserService.getAdminUser( request ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_FORM );
        }
        int nIdForm = Integer.parseInt( strIdForm );
        FormMessage formMessage = FormMessageService.findFormMessageByIdForm( nIdForm );
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_FORM_MESSAGE, formMessage );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_LOCALE_TINY, getLocale( ) );
        return getPage( PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENTFORM_MESSAGES, TEMPLATE_MODIFY_APPOINTMENTFORM_MESSAGES, model );
    }

    /**
     * Do modify an appointment form messages
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     * @throws AccessDeniedException
     *             If the user is not authorized to modify this appointment form
     */
    @Action( ACTION_DO_MODIFY_FORM_MESSAGES )
    public String doModifyAppointmentFormMessages( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) && ( request.getParameter( PARAMETER_BACK ) == null ) )
        {
            if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_MODIFY_FORM,
                    (User) AdminUserService.getAdminUser( request ) ) )
            {
                throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_FORM );
            }
            int nIdForm = Integer.parseInt( strIdForm );
            UrlItem url = new UrlItem( getViewFullUrl( VIEW_MODIFY_FORM_MESSAGES ) );
            url.addParameter( PARAMETER_ID_FORM, nIdForm );
            FormMessage formMessage = FormMessageService.findFormMessageByIdForm( nIdForm );
            populate( formMessage, request );
            FormMessageHome.update( formMessage );
            AppLogService.info( LogUtilities.buildLog( ACTION_DO_MODIFY_FORM_MESSAGES, strIdForm, getUser( ) ) );
            addInfo( INFO_APPOINTMENTFORM_MESSAGES_MODIFIED, getLocale( ) );
        }
        return redirectView( request, VIEW_MANAGE_APPOINTMENTFORMS );
    }

    /**
     * Get the URL to manage appointment forms
     * 
     * @param request
     *            The request
     * @return The URL to manage appointment forms
     */
    public static String getURLManageAppointmentForms( HttpServletRequest request )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_APPOINTMENTFORMS );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_APPOINTMENTFORMS );
        return urlItem.getUrl( );
    }

    /**
     * Add elements to the model to display the left column to modify an appointment form
     * 
     * @param request
     *            The request to store the appointment form in session
     * @param appointmentForm
     *            The appointment form
     * @param user
     *            The user
     * @param locale
     *            The locale
     * @param model
     *            the model to add elements in
     */
    public static void addElementsToModel( AppointmentFormDTO appointmentForm, AdminUser user, Locale locale, Map<String, Object> model )
    {
        Plugin pluginAppointmentResource = PluginService.getPlugin( AppPropertiesService.getProperty( PROPERTY_MODULE_APPOINTMENT_RESOURCE_NAME ) );
        Plugin moduleAppointmentDesk = PluginService.getPlugin( AppPropertiesService.getProperty( PROPERTY_MODULE_APPOINTMENT_DESK_NAME ) );
        ReferenceList listRoles = RoleHome.getRolesList( user );
        model.put( MARK_APPOINTMENT_FORM, appointmentForm );
        model.put( MARK_LOCALE, locale );
        model.put( MARK_LIST_WORKFLOWS, WorkflowService.getInstance( ).getWorkflowsEnabled( (User) user, locale ) );
        model.put( MARK_IS_CAPTCHA_ENABLED, _captchaSecurityService.isAvailable( ) );
        model.put( MARK_REF_LIST_CALENDAR_TEMPLATES, CalendarTemplateHome.findAllInReferenceList( ) );
        model.put( MARK_LIST_CATEGORIES, CategoryService.findAllInReferenceList( ) );
        model.put( MARK_USER_WORKGROUP_REF_LIST, AdminWorkgroupService.getUserWorkgroups( user, locale ) );
        model.put( MARK_APPOINTMENT_RESOURCE_ENABLED, ( pluginAppointmentResource != null ) && pluginAppointmentResource.isInstalled( ) );
        model.put( MARK_APPOINTMENT_DESK_ENABLED, ( moduleAppointmentDesk != null ) && moduleAppointmentDesk.isInstalled( ) );
        model.put( MARK_REF_LIST_ROLES, listRoles );
    }

    /**
     * Build an image resource (icon)
     * 
     * @param mRequest
     *            the request
     * @return the image resource
     */
    public static ImageResource buildImageResource( MultipartHttpServletRequest mRequest )
    {
        ImageResource img = new ImageResource( );
        byte [ ] bytes = new byte [ ] { };
        String strMimeType = MARK_NULL;
        FileItem item = mRequest.getFile( PARAMETER_ICON_RESSOURCE );
        if ( ( item != null ) && StringUtils.isNotEmpty( item.getName( ) ) )
        {
            bytes = item.get( );
            strMimeType = item.getContentType( );
        }
        img.setImage( bytes );
        img.setMimeType( strMimeType );
        return img;
    }

    /**
     * Set the days to the appointment form DTO
     * 
     * @param appointmentForm
     *            the appointmentForm DTO
     * @param appointmentFormTmp
     *            the appointmentForm temp DTO
     */
    private void setParametersDays( AppointmentFormDTO appointmentForm, AppointmentFormDTO appointmentFormTmp )
    {
        appointmentForm.setIsOpenMonday( appointmentFormTmp.getIsOpenMonday( ) );
        appointmentForm.setIsOpenTuesday( appointmentFormTmp.getIsOpenTuesday( ) );
        appointmentForm.setIsOpenWednesday( appointmentFormTmp.getIsOpenWednesday( ) );
        appointmentForm.setIsOpenThursday( appointmentFormTmp.getIsOpenThursday( ) );
        appointmentForm.setIsOpenFriday( appointmentFormTmp.getIsOpenFriday( ) );
        appointmentForm.setIsOpenSaturday( appointmentFormTmp.getIsOpenSaturday( ) );
        appointmentForm.setIsOpenSunday( appointmentFormTmp.getIsOpenSunday( ) );
    }

    /**
     * Import the file of the closing days
     * 
     * @param mRequest
     *            the request
     * @param nIdForm
     *            the form Id
     * @return false if there is an error during the import
     */
    private void importClosingDayFile( MultipartHttpServletRequest mRequest, int nIdForm )
    {
        String strPathFile = StringUtils.EMPTY;
        FileItem item = mRequest.getFile( MARK_FILE_CLOSING_DAYS );
        if ( item != null && StringUtils.isNotEmpty( item.getName( ) ) )
        {
            strPathFile = item.getName( );
        }
        if ( StringUtils.isEmpty( strPathFile ) )
        {
            return;
        }

        List<LocalDate> listClosingDaysDb = ClosingDayService.findListDateOfClosingDayByIdForm( nIdForm );
        List<LocalDate> listDateImported = new ArrayList<>( );
        List<LocalDate> listDateToSave = new ArrayList<>( );
        try
        {
            listDateImported = ClosingDayService.getImportClosingDays( item );
        }
        catch( IOException e )
        {
            AppLogService.error( "Error importing file", e );
        }
        if ( CollectionUtils.isEmpty( listDateImported ) )
        {
            addError( MESSAGE_ERROR_EMPTY_FILE, getLocale( ) );
        }
        else
        {
            List<Slot> listSlotsImpacted;
            List<LocalDate> listDateWithError = new ArrayList<>( );
            List<Slot> listSlotsToDelete = new ArrayList<>( );
            List<Appointment> listAppointmentsImpacted;
            for ( LocalDate closingDate : listDateImported )
            {
                if ( !listClosingDaysDb.contains( closingDate ) )
                {
                    listSlotsImpacted = SlotService.findSlotsByIdFormAndDateRange( nIdForm, closingDate.atStartOfDay( ), closingDate.atTime( LocalTime.MAX ) );
                    // Check if there is appointments on this slots
                    listAppointmentsImpacted = AppointmentService.findListAppointmentByListSlot( listSlotsImpacted );
                    if ( CollectionUtils.isNotEmpty( listAppointmentsImpacted ) )
                    {
                        listDateWithError.add( closingDate );
                    }
                    else
                    {
                        listSlotsToDelete.addAll( listSlotsImpacted );
                        listDateToSave.add( closingDate );
                    }
                }
            }
            if ( CollectionUtils.isNotEmpty( listDateWithError ) )
            {
                StringJoiner stbListDate = new StringJoiner( StringUtils.SPACE );
                for ( LocalDate dateWithError : listDateWithError )
                {
                    stbListDate.add( Utilities.getFormatter( ).format( dateWithError ) ).add( "," );
                }
                String strListdate = stbListDate.toString( );
                strListdate = strListdate.substring( 0, strListdate.length( ) - 1 );
                Object [ ] tabEntryErrorDate = {
                        strListdate
                };
                String strErrorMessageDateWithAppointments = I18nService.getLocalizedString( MESSAGE_ERROR_OPEN_SLOTS, tabEntryErrorDate, getLocale( ) );
                addError( strErrorMessageDateWithAppointments );
            }
            SlotService.deleteListSlots( listSlotsToDelete );
            ClosingDayService.saveListClosingDay( nIdForm, listDateToSave );
            addInfo( MESSAGE_INFO_IMPORTED_CLOSING_DAYS, getLocale( ) );
        }
    }

    private void populateAddress( AppointmentFormDTO appointmentForm, HttpServletRequest request )
    {
        String strGeolocAddress = request.getParameter( PARAMETER_GEOLOC_ADDRESS );
        String strGeolocLatitude = request.getParameter( PARAMETER_GEOLOC_LATITUDE );
        String strGeolocLongitude = request.getParameter( PARAMETER_GEOLOC_LONGITUDE );
        if ( StringUtils.isNotBlank( strGeolocAddress ) && StringUtils.isNotBlank( strGeolocLatitude ) && StringUtils.isNotBlank( strGeolocLongitude ) )
        {
            appointmentForm.setAddress( strGeolocAddress );
            appointmentForm.setLatitude( Double.valueOf( strGeolocLatitude ) );
            appointmentForm.setLongitude( Double.valueOf( strGeolocLongitude ) );
        }
        else
        {
            appointmentForm.setAddress( null );
            appointmentForm.setLatitude( null );
            appointmentForm.setLongitude( null );
        }
    }

}
