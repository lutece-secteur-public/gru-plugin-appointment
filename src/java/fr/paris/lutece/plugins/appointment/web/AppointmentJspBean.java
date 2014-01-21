/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.appointment.business.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.business.AppointmentFilter;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormMessages;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormMessagesHome;
import fr.paris.lutece.plugins.appointment.business.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDay;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDayHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentFormService;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.service.CalendarService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.beanvalidation.BeanValidationUtil;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;

import org.apache.commons.lang.StringUtils;


/**
 * This class provides the user interface to manage Appointment features (
 * manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageAppointments.jsp", controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM )
public class AppointmentJspBean extends MVCAdminJspBean
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 1978001810468444844L;
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_APPOINTMENTS_CALENDAR = "/admin/plugins/appointment/appointment/manage_appointments_calendar.html";
    private static final String TEMPLATE_CREATE_APPOINTMENT = "/admin/plugins/appointment/appointment/create_appointment.html";
    private static final String TEMPLATE_MANAGE_APPOINTMENTS = "/admin/plugins/appointment/appointment/manage_appointments.html";
    private static final String TEMPLATE_VIEW_APPOINTMENT = "/admin/plugins/appointment/appointment/view_appointment.html";
    private static final String TEMPLATE_APPOINTMENT_FORM_CALENDAR = "/admin/plugins/appointment/appointment/appointment_form_calendar.html";
    private static final String TEMPLATE_APPOINTMENT_FORM_RECAP = "/admin/plugins/appointment/appointment/appointment_form_recap.html";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS = "appointment.manage_appointments.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_APPOINTMENT = "appointment.create_appointment.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_VIEW_APPOINTMENT = "appointment.view_appointment.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_APPOINTMENT_CALENDAR = "appointment.appointmentCalendar.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_RECAP_APPOINTMENT = "appointment.appointmentApp.recap.title";

    // Parameters
    private static final String PARAMETER_ID_APPOINTMENT = "id_appointment";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_EMAIL = "email";
    private static final String PARAMETER_FIRST_NAME = "firstname";
    private static final String PARAMETER_LAST_NAME = "lastname";
    private static final String PARAMETER_NB_WEEK = "nb_week";
    private static final String PARAMETER_ID_SLOT = "idSlot";
    private static final String PARAMETER_ID_DAY = "id_day";
    private static final String PARAMETER_BACK = "back";

    // Markers
    private static final String MARK_APPOINTMENT_LIST = "appointment_list";
    private static final String MARK_APPOINTMENT = "appointment";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_FORM_MESSAGES = "formMessages";
    private static final String MARK_FORM_HTML = "form_html";
    private static final String MARK_FORM = "form";
    private static final String MARK_FORM_ERRORS = "form_errors";
    private static final String MARK_LIST_DAYS = "listDays";
    private static final String MARK_LIST_TIME_BEGIN = "list_time_begin";
    private static final String MARK_MIN_DURATION_APPOINTMENT = "min_duration_appointments";
    private static final String MARK_SLOT = "slot";
    private static final String MARK_LIST_DAYS_OF_WEEK = "list_days_of_week";
    private static final String MARK_RIGHT_CREATE = "rightCreate";
    private static final String MARK_RIGHT_MODIFY = "rightModify";
    private static final String MARK_RIGHT_DELETE = "rightDelete";
    private static final String MARK_RIGHT_VIEW = "rightView";
    private static final String MARK_DAY = "day";

    // JSP
    private static final String JSP_MANAGE_APPOINTMENTS = "jsp/admin/plugins/appointment/ManageAppointments.jsp";

    //    private static final String VALIDATION_ATTRIBUTES_PREFIX = "appointment.model.entity.appointment.attribute.";

    private static final String ERROR_MESSAGE_SLOT_FULL = "appointment.message.error.slotFull";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_APPOINTMENT = "appointment.message.confirmRemoveAppointment";

    // Properties
    private static final String PROPERTY_DEFAULT_LIST_APPOINTMENT_PER_PAGE = "appointment.listAppointments.itemsPerPage";

    //    private static final String VALIDATION_ATTRIBUTES_PREFIX = "appointment.model.entity.appointment.attribute.";

    // Views
    private static final String VIEW_MANAGE_APPOINTMENTS = "manageAppointments";
    private static final String VIEW_CREATE_APPOINTMENT = "createAppointment";
    private static final String VIEW_GET_APPOINTMENT_CALENDAR = "getAppointmentCalendar";
    //    private static final String VIEW_MODIFY_APPOINTMENT = "modifyAppointment";
    private static final String VIEW_VIEW_APPOINTMENT = "viewAppointment";
    private static final String VIEW_DISPLAY_RECAP_APPOINTMENT = "displayRecapAppointment";
    private static final String VIEW_CALENDAR_MANAGE_APPOINTMENTS = "viewCalendarManageAppointment";

    // Actions
    private static final String ACTION_CREATE_APPOINTMENT = "createAppointment";
    private static final String ACTION_DO_VALIDATE_FORM = "doValidateForm";
    //    private static final String ACTION_MODIFY_APPOINTMENT = "modifyAppointment";
    private static final String ACTION_REMOVE_APPOINTMENT = "removeAppointment";
    private static final String ACTION_CONFIRM_REMOVE_APPOINTMENT = "confirmRemoveAppointment";
    private static final String ACTION_DO_MAKE_APPOINTMENT = "doMakeAppointment";

    // Infos
    private static final String INFO_APPOINTMENT_CREATED = "appointment.info.appointment.created";
    //    private static final String INFO_APPOINTMENT_UPDATED = "appointment.info.appointment.updated";
    private static final String INFO_APPOINTMENT_REMOVED = "appointment.info.appointment.removed";

    // Session keys
    private static final String SESSION_ATTRIBUTE_APPOINTMENT = "appointment.session.appointment";
    private static final String SESSION_CURRENT_PAGE_INDEX = "appointment.session.currentPageIndex";
    private static final String SESSION_ITEMS_PER_PAGE = "appointment.session.itemsPerPage";
    private static final String SESSION_APPOINTMENT_FORM_ERRORS = "appointment.session.formErrors";

    // Constant
    private static final String DEFAULT_CURRENT_PAGE = "1";

    private final AppointmentFormService _appointmentFormService = SpringContextService
            .getBean( AppointmentFormService.BEAN_NAME );

    // Session variable to store working values
    private int _nDefaultItemsPerPage;

    /**
     * Default constructor
     */
    public AppointmentJspBean(  )
    {
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_APPOINTMENT_PER_PAGE, 50 );
    }

    /**
     * Get the page to manage appointments. Appointments are displayed in a
     * calendar.
     * @param request The request
     * @return The HTML code to display
     */
    @View( value = VIEW_CALENDAR_MANAGE_APPOINTMENTS, defaultView = true )
    public String getCalendarManageAppointments( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            request.getSession( ).removeAttribute( SESSION_ATTRIBUTE_APPOINTMENT );

            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

            String strNbWeek = request.getParameter( PARAMETER_NB_WEEK );
            int nNbWeek = 0;

            if ( StringUtils.isNotEmpty( strNbWeek ) && StringUtils.isNumeric( strNbWeek ) )
            {
                nNbWeek = Integer.parseInt( strNbWeek );

                if ( nNbWeek > ( form.getNbWeeksToDisplay( ) - 1 ) )
                {
                    nNbWeek = form.getNbWeeksToDisplay( ) - 1;
                }
            }

            List<AppointmentDay> listDays = CalendarService.getService( ).getDayListForCalendar( form, nNbWeek );

            List<String> listTimeBegin = new ArrayList<String>( );
            int nMinAppointmentDuration = CalendarService.getService( )
                    .getListTimeBegin( listDays, form, listTimeBegin );

            Map<String, Object> model = getModel( );

            model.put( MARK_FORM, form );
            model.put( MARK_LIST_DAYS, listDays );
            model.put( PARAMETER_NB_WEEK, nNbWeek );
            model.put( MARK_LIST_TIME_BEGIN, listTimeBegin );
            model.put( MARK_MIN_DURATION_APPOINTMENT, nMinAppointmentDuration );
            model.put( MARK_LIST_DAYS_OF_WEEK, CalendarService.MESSAGE_LIST_DAYS_OF_WEEK );

            return getPage( PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS, TEMPLATE_MANAGE_APPOINTMENTS_CALENDAR, model );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Get the page to manage appointments
     * @param request The request
     * @return The HTML code to display
     */
    @View( value = VIEW_MANAGE_APPOINTMENTS )
    public String getManageAppointments( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            String strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                    (String) request.getSession( ).getAttribute( SESSION_CURRENT_PAGE_INDEX ) );

            if ( strCurrentPageIndex == null )
            {
                strCurrentPageIndex = DEFAULT_CURRENT_PAGE;
            }

            request.getSession( ).setAttribute( SESSION_CURRENT_PAGE_INDEX, strCurrentPageIndex );

            int nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                    getIntSessionAttribute( request.getSession( ), SESSION_ITEMS_PER_PAGE ), _nDefaultItemsPerPage );
            request.getSession( ).setAttribute( SESSION_ITEMS_PER_PAGE, nItemsPerPage );

            request.getSession( ).removeAttribute( SESSION_ATTRIBUTE_APPOINTMENT );

            UrlItem url = new UrlItem( JSP_MANAGE_APPOINTMENTS );
            String strUrl = url.getUrl( );

            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

            String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );
            String strIdDay = request.getParameter( PARAMETER_ID_DAY );
            List<Appointment> listAppointments;
            AppointmentDay day = null;
            AppointmentSlot slot = null;
            AppointmentFilter filter = new AppointmentFilter( );
            if ( StringUtils.isNotEmpty( strIdSlot ) && StringUtils.isNumeric( strIdSlot ) )
            {
                int nIdSlot = Integer.parseInt( strIdSlot );
                slot = AppointmentSlotHome.findByPrimaryKey( nIdSlot );
                day = AppointmentDayHome.findByPrimaryKey( slot.getIdDay( ) );
                filter.setIdSlot( nIdSlot );
            }
            else if ( StringUtils.isNotEmpty( strIdDay ) && StringUtils.isNumeric( strIdDay ) )
            {
                int nIdDay = Integer.parseInt( strIdDay );
                day = AppointmentDayHome.findByPrimaryKey( nIdDay );
                filter.setDateAppointment( day.getDate( ) );
                listAppointments = AppointmentHome.getAppointmentListByFilter( filter );
            }
            else
            {
                populate( filter, request );
            }
            listAppointments = AppointmentHome.getAppointmentListByFilter( filter );
            // PAGINATOR
            LocalizedPaginator<Appointment> paginator = new LocalizedPaginator<Appointment>( listAppointments,
                    nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX, strCurrentPageIndex, getLocale( ) );

            Map<String, Object> model = getModel( );

            model.put( MARK_FORM, form );
            model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( nItemsPerPage ) );
            model.put( MARK_PAGINATOR, paginator );
            model.put( MARK_APPOINTMENT_LIST, paginator.getPageItems( ) );
            model.put( MARK_SLOT, slot );
            model.put( MARK_DAY, day );
            model.put( MARK_RIGHT_CREATE,
                    RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                            AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT,
                            AdminUserService.getAdminUser( request ) ) );
            model.put( MARK_RIGHT_MODIFY,
                    RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                            AppointmentResourceIdService.PERMISSION_MODIFY_APPOINTMENT,
                            AdminUserService.getAdminUser( request ) ) );
            model.put( MARK_RIGHT_DELETE,
                    RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                            AppointmentResourceIdService.PERMISSION_DELETE_APPOINTMENT,
                            AdminUserService.getAdminUser( request ) ) );
            model.put( MARK_RIGHT_VIEW, RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                    AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, AdminUserService.getAdminUser( request ) ) );

            return getPage( PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS, TEMPLATE_MANAGE_APPOINTMENTS, model );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Returns the form to create a appointment
     * @param request The HTTP request
     * @return the HTML code of the appointment form
     */
    @View( VIEW_CREATE_APPOINTMENT )
    public String getCreateAppointment( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( ( strIdForm != null ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

            if ( ( form == null ) )
            {
                return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
            }

            Map<String, Object> model = new HashMap<String, Object>( );

            Appointment appointment = _appointmentFormService
                    .getValidatedAppointmentFromSession( request.getSession( ) );
            if ( appointment != null )
            {
                AppointmentDTO appointmentDTO = new AppointmentDTO( );
                appointmentDTO.setEmail( appointment.getEmail( ) );
                appointmentDTO.setFirstName( appointment.getEmail( ) );
                appointmentDTO.setLastName( appointment.getLastName( ) );

                Map<Integer, List<Response>> mapResponsesByIdEntry = appointmentDTO.getMapResponsesByIdEntry( );
                for ( Response response : appointment.getListResponse( ) )
                {
                    List<Response> listResponse = mapResponsesByIdEntry.get( response.getEntry( ).getIdEntry( ) );
                    if ( listResponse == null )
                    {
                        listResponse = new ArrayList<Response>( );
                        mapResponsesByIdEntry.put( response.getEntry( ).getIdEntry( ), listResponse );
                    }
                    listResponse.add( response );
                }

                _appointmentFormService.saveAppointmentInSession( request.getSession( ), appointmentDTO );
            }

            AppointmentFormMessages formMessages = AppointmentFormMessagesHome.findByPrimaryKey( nIdForm );
            model.put( MARK_FORM_HTML,
                    _appointmentFormService.getHtmlForm( form, formMessages, getLocale( ), false, request ) );

            List<GenericAttributeError> listErrors = (List<GenericAttributeError>) request.getSession( ).getAttribute(
                    SESSION_APPOINTMENT_FORM_ERRORS );

            if ( listErrors != null )
            {
                model.put( MARK_FORM_ERRORS, listErrors );
                request.getSession( ).removeAttribute( SESSION_APPOINTMENT_FORM_ERRORS );
            }

            _appointmentFormService.removeAppointmentFromSession( request.getSession( ) );
            _appointmentFormService.removeValidatedAppointmentFromSession( request.getSession( ) );

            return getPage( PROPERTY_PAGE_TITLE_CREATE_APPOINTMENT, TEMPLATE_CREATE_APPOINTMENT, model );
        }
        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Do validate data entered by a user to fill a form
     * @param request The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_VALIDATE_FORM )
    public String doValidateForm( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( ( strIdForm != null ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            EntryFilter filter = new EntryFilter( );
            filter.setIdResource( nIdForm );
            filter.setResourceType( AppointmentForm.RESOURCE_TYPE );
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
            filter.setIdIsComment( EntryFilter.FILTER_FALSE );

            List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );

            _appointmentFormService.removeAppointmentFromSession( request.getSession( ) );

            List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
            Locale locale = request.getLocale( );

            AppointmentDTO appointment = new AppointmentDTO( );
            appointment.setEmail( request.getParameter( PARAMETER_EMAIL ) );
            appointment.setFirstName( request.getParameter( PARAMETER_FIRST_NAME ) );
            appointment.setLastName( request.getParameter( PARAMETER_LAST_NAME ) );
            appointment.setStatus( Appointment.STATUS_NOT_VALIDATED );

            if ( SecurityService.isAuthenticationEnable( ) )
            {
                LuteceUser luteceUser = SecurityService.getInstance( ).getRegisteredUser( request );

                if ( luteceUser != null )
                {
                    appointment.setIdUser( luteceUser.getName( ) );
                }
            }

            // We save the appointment in session. The appointment object will contain responses of the user to the form
            _appointmentFormService.saveAppointmentInSession( request.getSession( ), appointment );

            Set<ConstraintViolation<AppointmentDTO>> listErrors = BeanValidationUtil.validate( appointment );

            if ( !listErrors.isEmpty( ) )
            {
                for ( ConstraintViolation<AppointmentDTO> constraintViolation : listErrors )
                {
                    GenericAttributeError genAttError = new GenericAttributeError( );
                    genAttError.setErrorMessage( constraintViolation.getMessage( ) );
                    listFormErrors.add( genAttError );
                }
            }

            for ( Entry entry : listEntryFirstLevel )
            {
                listFormErrors.addAll( _appointmentFormService.getResponseEntry( request, entry.getIdEntry( ), locale,
                        appointment ) );
            }

            // If there is some errors, we redirect the user to the form page
            if ( listFormErrors.size( ) > 0 )
            {
                request.getSession( ).setAttribute( SESSION_APPOINTMENT_FORM_ERRORS, listFormErrors );

                return redirect( request, VIEW_CREATE_APPOINTMENT, PARAMETER_ID_FORM, nIdForm );
            }

            _appointmentFormService.convertMapResponseToList( appointment );
            _appointmentFormService.saveValidatedAppointmentForm( request.getSession( ), appointment );

            return redirect( request, VIEW_GET_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, nIdForm );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Get the page with the calendar with opened and closed days for an
     * appointment form
     * @param request The request
     * @return The XPage to display
     */
    @View( VIEW_GET_APPOINTMENT_CALENDAR )
    public String getAppointmentCalendar( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( ( strIdForm != null ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            if ( _appointmentFormService.getValidatedAppointmentFromSession( request.getSession( ) ) == null )
            {
                return redirect( request, VIEW_CREATE_APPOINTMENT, PARAMETER_ID_FORM, nIdForm );
            }

            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

            Map<String, Object> model = new HashMap<String, Object>( );

            String strNbWeek = request.getParameter( PARAMETER_NB_WEEK );
            int nNbWeek = 0;

            if ( StringUtils.isNotEmpty( strNbWeek ) && StringUtils.isNumeric( strNbWeek ) )
            {
                nNbWeek = Integer.parseInt( strNbWeek );

                if ( nNbWeek > ( form.getNbWeeksToDisplay( ) - 1 ) )
                {
                    nNbWeek = form.getNbWeeksToDisplay( ) - 1;
                }
            }

            List<AppointmentDay> listDays = CalendarService.getService( ).getDayListForCalendar( form, nNbWeek );

            List<String> listTimeBegin = new ArrayList<String>( );
            int nMinAppointmentDuration = CalendarService.getService( )
                    .getListTimeBegin( listDays, form, listTimeBegin );

            model.put( MARK_FORM, form );
            model.put( MARK_FORM_MESSAGES, AppointmentFormMessagesHome.findByPrimaryKey( nIdForm ) );
            model.put( MARK_LIST_DAYS, listDays );
            model.put( MARK_LIST_TIME_BEGIN, listTimeBegin );
            model.put( MARK_MIN_DURATION_APPOINTMENT, nMinAppointmentDuration );
            model.put( PARAMETER_NB_WEEK, nNbWeek );
            model.put( MARK_LIST_DAYS_OF_WEEK, CalendarService.MESSAGE_LIST_DAYS_OF_WEEK );

            return getPage( PROPERTY_PAGE_TITLE_APPOINTMENT_CALENDAR, TEMPLATE_APPOINTMENT_FORM_CALENDAR, model );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Manages the removal form of a appointment whose identifier is in the HTTP
     * request
     * @param request The HTTP request
     * @return the HTML code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_APPOINTMENT )
    public String getConfirmRemoveAppointment( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_APPOINTMENT ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_APPOINTMENT ) );
        url.addParameter( PARAMETER_ID_APPOINTMENT, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_APPOINTMENT,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a appointment
     * @param request The HTTP request
     * @return the JSP URL to display the form to manage appointments
     */
    @Action( ACTION_REMOVE_APPOINTMENT )
    public String doRemoveAppointment( HttpServletRequest request )
    {
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );
        if ( StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) )
        {
            int nId = Integer.parseInt( strIdAppointment );
            Appointment appointment = AppointmentHome.findByPrimaryKey( nId );
            if ( appointment != null )
            {
                AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot( ) );
                if ( WorkflowService.getInstance( ).isAvailable( ) )
                {
                    WorkflowService.getInstance( )
                            .doRemoveWorkFlowResource( nId, Appointment.APPOINTMENT_RESOURCE_TYPE );
                }
                AppointmentHome.remove( nId );
                addInfo( INFO_APPOINTMENT_REMOVED, getLocale( ) );
                return redirect( request, VIEW_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, slot.getIdForm( ) );
            }
        }
        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    //        /**
    //         * Returns the form to update info about a appointment
    //         * @param request The HTTP request
    //         * @return The HTML form to update info
    //         */
    //        @View( VIEW_MODIFY_APPOINTMENT )
    //        public String getModifyAppointment( HttpServletRequest request )
    //        {
    //            Appointment appointment = (Appointment) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT );
    //    
    //            if ( appointment == null )
    //            {
    //                int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_APPOINTMENT ) );
    //                appointment = AppointmentHome.findByPrimaryKey( nId );
    //                request.getSession( ).setAttribute( SESSION_ATTRIBUTE_APPOINTMENT, appointment );
    //            }
    //    
    //            Map<String, Object> model = getModel( );
    //            model.put( MARK_APPOINTMENT, appointment );
    //    
    //            return getPage( PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENT, TEMPLATE_MODIFY_APPOINTMENT, model );
    //        }

    //    /**
    //     * Process the change form of a appointment
    //     * @param request The HTTP request
    //     * @return The JSP URL of the process result
    //     */
    //    @Action( ACTION_MODIFY_APPOINTMENT )
    //    public String doModifyAppointment( HttpServletRequest request )
    //    {
    //        Appointment appointment = (Appointment) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT );
    //        populate( appointment, request );
    //
    //        // Check constraints
    //        if ( !validateBean( appointment, VALIDATION_ATTRIBUTES_PREFIX ) )
    //        {
    //            return redirect( request, VIEW_MODIFY_APPOINTMENT, PARAMETER_ID_APPOINTMENT, appointment.getIdAppointment( ) );
    //        }
    //
    //        AppointmentHome.update( appointment );
    //        request.getSession( ).removeAttribute( SESSION_ATTRIBUTE_APPOINTMENT );
    //        addInfo( INFO_APPOINTMENT_UPDATED, getLocale( ) );
    //
    //        return redirectView( request, VIEW_MANAGE_APPOINTMENTS );
    //    }

    /**
     * Display the recap before validating an appointment
     * @param request The request
     * @return The HTML content to display or the next URL to redirect to
     */
    @View( VIEW_DISPLAY_RECAP_APPOINTMENT )
    public String displayRecapAppointment( HttpServletRequest request )
    {
        String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );

        if ( StringUtils.isNotEmpty( strIdSlot ) && StringUtils.isNumeric( strIdSlot ) )
        {
            Appointment appointment = _appointmentFormService
                    .getValidatedAppointmentFromSession( request.getSession( ) );

            int nIdSlot = Integer.parseInt( strIdSlot );
            AppointmentSlot appointmentSlot = AppointmentSlotHome.findByPrimaryKey( nIdSlot );

            if ( appointment != null )
            {
                appointment.setIdSlot( nIdSlot );

                Map<String, Object> model = new HashMap<String, Object>( );
                model.put( MARK_APPOINTMENT, appointment );
                model.put( MARK_SLOT, appointmentSlot );

                AppointmentForm form = AppointmentFormHome.findByPrimaryKey( appointmentSlot.getIdForm( ) );
                AppointmentDay day = AppointmentDayHome.findByPrimaryKey( appointmentSlot.getIdDay( ) );
                appointment.setDateAppointment( (Date) day.getDate( ).clone( ) );
                model.put( MARK_DAY, day );
                model.put( MARK_FORM, form );
                model.put( MARK_FORM_MESSAGES,
                        AppointmentFormMessagesHome.findByPrimaryKey( appointmentSlot.getIdForm( ) ) );
                fillCommons( model );

                return getPage( PROPERTY_PAGE_TITLE_RECAP_APPOINTMENT, TEMPLATE_APPOINTMENT_FORM_RECAP, model );
            }

            return redirect( request, VIEW_GET_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, appointmentSlot.getIdForm( ) );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Do save an appointment into the database if it is valid
     * @param request The request
     * @return The XPage to display
     */
    @Action( ACTION_DO_MAKE_APPOINTMENT )
    public String doMakeAppointment( HttpServletRequest request )
    {
        Appointment appointment = _appointmentFormService.getValidatedAppointmentFromSession( request.getSession( ) );
        AppointmentSlot appointmentSlot = AppointmentSlotHome.findByPrimaryKeyWithFreePlaces( appointment.getIdSlot( ),
                appointment.getDateAppointment( ) );
        AppointmentForm form = AppointmentFormHome.findByPrimaryKey( appointmentSlot.getIdForm( ) );

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_BACK ) ) )
        {
            return redirect( request, VIEW_GET_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, appointmentSlot.getIdForm( ) );
        }

        if ( appointmentSlot.getNbFreePlaces( ) <= 0 )
        {
            addError( ERROR_MESSAGE_SLOT_FULL, getLocale( ) );
            return redirect( request, VIEW_GET_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, appointmentSlot.getIdForm( ) );
        }

        AppointmentHome.create( appointment );

        for ( Response response : appointment.getListResponse( ) )
        {
            ResponseHome.create( response );
            AppointmentHome.insertAppointmentResponse( appointment.getIdAppointment( ), response.getIdResponse( ) );
        }

        if ( form.getIdWorkflow( ) > 0 )
        {
            WorkflowService.getInstance( ).getState( appointment.getIdAppointment( ),
                    Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow( ), form.getIdForm( ) );
            WorkflowService.getInstance( ).executeActionAutomatic( appointment.getIdAppointment( ),
                    Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow( ), form.getIdForm( ) );
        }

        _appointmentFormService.removeValidatedAppointmentFromSession( request.getSession( ) );

        addInfo( INFO_APPOINTMENT_CREATED, getLocale( ) );

        return redirect( request, VIEW_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, appointmentSlot.getIdForm( ) );
    }

    /**
     * View details of an appointment
     * @param request The request
     * @return The HTML content to display
     */
    @View( VIEW_VIEW_APPOINTMENT )
    public String getViewAppointment( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_APPOINTMENT ) );
        Appointment appointment = AppointmentHome.findByPrimaryKey( nId );

        List<Integer> listIdResponse = AppointmentHome.findListResponse( nId );
        List<Response> listResponse = new ArrayList<Response>( listIdResponse.size( ) );
        for ( int nIdResponse : listIdResponse )
        {
            listResponse.add( ResponseHome.findByPrimaryKey( nIdResponse ) );
        }
        appointment.setListResponse( listResponse );
        Map<String, Object> model = getModel(  );
        model.put( MARK_APPOINTMENT, appointment );
        AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot( ) );
        model.put( MARK_SLOT, slot );
        model.put( MARK_FORM, AppointmentFormHome.findByPrimaryKey( slot.getIdForm( ) ) );
        model.put( MARK_FORM_MESSAGES, AppointmentFormMessagesHome.findByPrimaryKey( slot.getIdForm( ) ) );
        return getPage( PROPERTY_PAGE_TITLE_VIEW_APPOINTMENT, TEMPLATE_VIEW_APPOINTMENT, model );
    }

    /**
     * Get an integer attribute from the session
     * @param session The session
     * @param strSessionKey The session key of the item
     * @return The value of the attribute, or 0 if the key is not associated
     *         with any value
     */
    private int getIntSessionAttribute( HttpSession session, String strSessionKey )
    {
        Integer nAttr = (Integer) session.getAttribute( strSessionKey );

        if ( nAttr != null )
        {
            return nAttr;
        }

        return 0;
    }
}
