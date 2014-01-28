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
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.util.LocalizedDelegatePaginator;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
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
    private static final String TEMPLATE_TASKS_FORM_WORKFLOW = "admin/plugins/appointment/appointment/tasks_form_workflow.html";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS = "appointment.manage_appointments.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS_CALENDAR = "appointment.manage_appointment_calendar.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_APPOINTMENT = "appointment.create_appointment.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_VIEW_APPOINTMENT = "appointment.view_appointment.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_APPOINTMENT_CALENDAR = "appointment.appointmentCalendar.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_RECAP_APPOINTMENT = "appointment.appointmentApp.recap.title";
    private static final String PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW = "appointment.taskFormWorkflow.pageTitle";

    // Parameters
    private static final String PARAMETER_ID_APPOINTMENT = "id_appointment";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_EMAIL = "email";
    private static final String PARAMETER_FIRST_NAME = "firstname";
    private static final String PARAMETER_LAST_NAME = "lastname";
    private static final String PARAMETER_NB_WEEK = "nb_week";
    private static final String PARAMETER_ID_SLOT = "idSlot";
    private static final String PARAMETER_BACK = "back";
    private static final String PARAMETER_ID_ACTION = "id_action";
    private static final String PARAMETER_NEW_STATUS = "new_status";
    private static final String PARAMETER_ORDER_BY = "orderBy";
    private static final String PARAMETER_ORDER_ASC = "orderAsc";

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
    private static final String MARK_FILTER = "filter";
    private static final String MARK_REF_LIST_STATUS = "refListStatus";
    private static final String MARK_FILTER_FROM_SESSION = "loadFilterFromSession";
    private static final String MARK_TASKS_FORM = "tasks_form";
    private static final String MARK_STATUS_VALIDATED = "status_validated";
    private static final String MARK_STATUS_REJECTED = "status_rejected";

    // JSP
    private static final String JSP_MANAGE_APPOINTMENTS = "jsp/admin/plugins/appointment/ManageAppointments.jsp";

    //    private static final String VALIDATION_ATTRIBUTES_PREFIX = "appointment.model.entity.appointment.attribute.";
    private static final String ERROR_MESSAGE_SLOT_FULL = "appointment.message.error.slotFull";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_APPOINTMENT = "appointment.message.confirmRemoveAppointment";
    private static final String MESSAGE_LABEL_STATUS_VALIDATED = "appointment.message.labelStatusValidated";
    private static final String MESSAGE_LABEL_STATUS_NOT_VALIDATED = "appointment.message.labelStatusNotValidated";
    private static final String MESSAGE_LABEL_STATUS_REJECTED = "appointment.message.labelStatusRejected";

    // Properties
    private static final String PROPERTY_DEFAULT_LIST_APPOINTMENT_PER_PAGE = "appointment.listAppointments.itemsPerPage";

    //    private static final String VALIDATION_ATTRIBUTES_PREFIX = "appointment.model.entity.appointment.attribute.";

    // Views
    private static final String VIEW_MANAGE_APPOINTMENTS = "manageAppointments";
    private static final String VIEW_CREATE_APPOINTMENT = "createAppointment";
    private static final String VIEW_GET_APPOINTMENT_CALENDAR = "getAppointmentCalendar";
    private static final String VIEW_MODIFY_APPOINTMENT = "modifyAppointment";
    private static final String VIEW_VIEW_APPOINTMENT = "viewAppointment";
    private static final String VIEW_DISPLAY_RECAP_APPOINTMENT = "displayRecapAppointment";
    private static final String VIEW_CALENDAR_MANAGE_APPOINTMENTS = "viewCalendarManageAppointment";
    private static final String VIEW_WORKFLOW_ACTION_FORM = "viewWorkflowActionForm";

    // Actions
    //    private static final String ACTION_CREATE_APPOINTMENT = "createAppointment";
    private static final String ACTION_DO_VALIDATE_FORM = "doValidateForm";

    //    private static final String ACTION_MODIFY_APPOINTMENT = "modifyAppointment";
    private static final String ACTION_REMOVE_APPOINTMENT = "removeAppointment";
    private static final String ACTION_CONFIRM_REMOVE_APPOINTMENT = "confirmRemoveAppointment";
    private static final String ACTION_DO_MAKE_APPOINTMENT = "doMakeAppointment";
    private static final String ACTION_DO_PROCESS_WORKFLOW_ACTION = "doProcessWorkflowAction";
    private static final String ACTION_DO_CHANGE_APPOINTMENT_STATUS = "doChangeAppointmentStatus";

    // Infos
    private static final String INFO_APPOINTMENT_CREATED = "appointment.info.appointment.created";

    //    private static final String INFO_APPOINTMENT_UPDATED = "appointment.info.appointment.updated";
    private static final String INFO_APPOINTMENT_REMOVED = "appointment.info.appointment.removed";

    // Session keys
    private static final String SESSION_ATTRIBUTE_APPOINTMENT = "appointment.session.appointment";
    private static final String SESSION_CURRENT_PAGE_INDEX = "appointment.session.currentPageIndex";
    private static final String SESSION_ITEMS_PER_PAGE = "appointment.session.itemsPerPage";
    private static final String SESSION_APPOINTMENT_FORM_ERRORS = "appointment.session.formErrors";

    // Messages
    private static final String[] MESSAGE_LIST_DAYS_OF_WEEK = AppointmentService.getListDaysOfWeek(  );

    // Constant
    private static final String CONSTANT_MINUS = "-";
    private static final String DEFAULT_CURRENT_PAGE = "1";
    private final AppointmentFormService _appointmentFormService = SpringContextService.getBean( AppointmentFormService.BEAN_NAME );

    // Session variable to store working values
    private int _nDefaultItemsPerPage;
    private AppointmentFilter _filter;

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
            _appointmentFormService.removeAppointmentFromSession( request.getSession(  ) );
            _appointmentFormService.removeValidatedAppointmentFromSession( request.getSession(  ) );

            int nIdForm = Integer.parseInt( strIdForm );

            request.getSession(  ).removeAttribute( SESSION_ATTRIBUTE_APPOINTMENT );

            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

            String strNbWeek = request.getParameter( PARAMETER_NB_WEEK );
            int nNbWeek = 0;

            if ( StringUtils.isNotEmpty( strNbWeek ) )
            {
                nNbWeek = parseInt( strNbWeek );
            }

            List<AppointmentDay> listDays = AppointmentService.getService(  ).computeDayList( form, nNbWeek, false );

            for ( AppointmentDay day : listDays )
            {
                if ( nNbWeek < 0 )
                {
                    if ( day.getIdDay(  ) > 0 )
                    {
                        List<AppointmentSlot> listSlots = AppointmentSlotHome.findByIdDayWithFreePlaces( day.getIdDay(  ) );

                        for ( AppointmentSlot slotFromDb : listSlots )
                        {
                            for ( AppointmentSlot slotComputed : day.getListSlots(  ) )
                            {
                                if ( ( slotFromDb.getStartingHour(  ) == slotComputed.getStartingHour(  ) ) &&
                                        ( slotFromDb.getStartingMinute(  ) == slotComputed.getStartingMinute(  ) ) )
                                {
                                    slotComputed.setNbFreePlaces( slotFromDb.getNbFreePlaces(  ) );
                                    slotComputed.setNbPlaces( slotFromDb.getNbPlaces(  ) );
                                    slotComputed.setIdSlot( slotFromDb.getIdSlot(  ) );
                                }
                            }
                        }

                        for ( AppointmentSlot slotComputed : day.getListSlots(  ) )
                        {
                            if ( slotComputed.getIdSlot(  ) == 0 )
                            {
                                slotComputed.setIsEnabled( false );
                            }
                        }
                    }
                    else
                    {
                        day.setIsOpen( false );
                    }
                }
                else
                {
                    // If the day has not been loaded from the database, we load its slots
                    // Otherwise, we use default computed slots
                    if ( day.getIdDay(  ) > 0 )
                    {
                        day.setListSlots( AppointmentSlotHome.findByIdDayWithFreePlaces( day.getIdDay(  ) ) );
                    }
                }
            }

            List<String> listTimeBegin = new ArrayList<String>(  );
            int nMinAppointmentDuration = AppointmentService.getService(  )
                                                            .getListTimeBegin( listDays, form, listTimeBegin );

            Map<String, Object> model = getModel(  );

            model.put( MARK_FORM, form );
            model.put( MARK_LIST_DAYS, listDays );
            model.put( PARAMETER_NB_WEEK, nNbWeek );
            model.put( MARK_LIST_TIME_BEGIN, listTimeBegin );
            model.put( MARK_MIN_DURATION_APPOINTMENT, nMinAppointmentDuration );
            model.put( MARK_LIST_DAYS_OF_WEEK, MESSAGE_LIST_DAYS_OF_WEEK );

            return getPage( PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS_CALENDAR, TEMPLATE_MANAGE_APPOINTMENTS_CALENDAR,
                model );
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
            _appointmentFormService.removeAppointmentFromSession( request.getSession(  ) );
            _appointmentFormService.removeValidatedAppointmentFromSession( request.getSession(  ) );

            int nIdForm = Integer.parseInt( strIdForm );

            String strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                    (String) request.getSession(  ).getAttribute( SESSION_CURRENT_PAGE_INDEX ) );

            if ( strCurrentPageIndex == null )
            {
                strCurrentPageIndex = DEFAULT_CURRENT_PAGE;
            }

            request.getSession(  ).setAttribute( SESSION_CURRENT_PAGE_INDEX, strCurrentPageIndex );

            int nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                    getIntSessionAttribute( request.getSession(  ), SESSION_ITEMS_PER_PAGE ), _nDefaultItemsPerPage );
            request.getSession(  ).setAttribute( SESSION_ITEMS_PER_PAGE, nItemsPerPage );

            request.getSession(  ).removeAttribute( SESSION_ATTRIBUTE_APPOINTMENT );

            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );
            UrlItem url = new UrlItem( JSP_MANAGE_APPOINTMENTS );
            url.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_APPOINTMENTS );
            url.addParameter( PARAMETER_ID_FORM, strIdForm );
            url.addParameter( MARK_FILTER_FROM_SESSION, Boolean.TRUE.toString(  ) );

            String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );
            AppointmentDay day = null;
            AppointmentSlot slot = null;
            AppointmentFilter filter;

            if ( ( _filter != null ) && Boolean.parseBoolean( request.getParameter( MARK_FILTER_FROM_SESSION ) ) )
            {
                filter = _filter;
                if ( filter == null )
                {
                    filter = new AppointmentFilter( );
                }
                String strOrderBy = request.getParameter( PARAMETER_ORDER_BY );
                if ( StringUtils.isNotEmpty( strOrderBy ) )
                {
                    filter.setOrderBy( strOrderBy );
                    filter.setOrderAsc( Boolean.parseBoolean( request.getParameter( PARAMETER_ORDER_ASC ) ) );
                }
            }
            else
            {
                filter = new AppointmentFilter(  );

                populate( filter, request );

                if ( StringUtils.isNotEmpty( strIdSlot ) && StringUtils.isNumeric( strIdSlot ) )
                {
                    int nIdSlot = Integer.parseInt( strIdSlot );
                    slot = AppointmentSlotHome.findByPrimaryKey( nIdSlot );
                    day = AppointmentDayHome.findByPrimaryKey( slot.getIdDay(  ) );
                    filter.setIdSlot( nIdSlot );
                    url.addParameter( PARAMETER_ID_SLOT, strIdSlot );
                }

                _filter = filter;
            }

            String strUrl = url.getUrl(  );

            List<Integer> listIdAppointments = AppointmentHome.getAppointmentIdByFilter( filter );

            LocalizedPaginator<Integer> paginator = new LocalizedPaginator<Integer>( listIdAppointments, nItemsPerPage,
                    strUrl, PARAMETER_PAGE_INDEX, strCurrentPageIndex, getLocale( ) );

            List<Appointment> listAppointments = AppointmentHome.getAppointmentListById( paginator.getPageItems( ),
                    filter.getOrderBy( ), filter.getOrderAsc( ) );

            LocalizedDelegatePaginator<Appointment> delegatePaginator = new LocalizedDelegatePaginator<Appointment>(
                    listAppointments, nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX, strCurrentPageIndex,
                    listIdAppointments.size( ), getLocale( ) );

            // PAGINATOR

            ReferenceList refListStatus = new ReferenceList( 3 );
            refListStatus.addItem( AppointmentFilter.NO_STATUS_FILTER, StringUtils.EMPTY );
            refListStatus.addItem( Appointment.STATUS_VALIDATED,
                I18nService.getLocalizedString( MESSAGE_LABEL_STATUS_VALIDATED, getLocale(  ) ) );
            refListStatus.addItem( Appointment.STATUS_NOT_VALIDATED,
                I18nService.getLocalizedString( MESSAGE_LABEL_STATUS_NOT_VALIDATED, getLocale(  ) ) );
            refListStatus.addItem( Appointment.STATUS_REJECTED,
                I18nService.getLocalizedString( MESSAGE_LABEL_STATUS_REJECTED, getLocale(  ) ) );

            Map<String, Object> model = getModel(  );

            model.put( MARK_FORM, form );
            model.put( MARK_FORM_MESSAGES, AppointmentFormMessagesHome.findByPrimaryKey( nIdForm ) );
            model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( nItemsPerPage ) );
            model.put( MARK_PAGINATOR, delegatePaginator );
            model.put( MARK_STATUS_VALIDATED, Appointment.STATUS_VALIDATED );
            model.put( MARK_STATUS_REJECTED, Appointment.STATUS_REJECTED );
            if ( ( form.getIdWorkflow(  ) > 0 ) && WorkflowService.getInstance(  ).isAvailable(  ) )
            {
                for ( Appointment appointment : delegatePaginator.getPageItems( ) )
                {
                    appointment.setListWorkflowActions( WorkflowService.getInstance(  )
                                                                       .getActions( appointment.getIdAppointment(  ),
                            Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow(  ), getUser(  ) ) );
                }
            }

            model.put( MARK_APPOINTMENT_LIST, delegatePaginator.getPageItems( ) );
            model.put( MARK_SLOT, slot );
            model.put( MARK_DAY, day );
            model.put( MARK_FILTER, filter );
            model.put( MARK_REF_LIST_STATUS, refListStatus );
            model.put( MARK_RIGHT_CREATE,
                RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                    AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT, AdminUserService.getAdminUser( request ) ) );
            model.put( MARK_RIGHT_MODIFY,
                RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                    AppointmentResourceIdService.PERMISSION_MODIFY_APPOINTMENT, AdminUserService.getAdminUser( request ) ) );
            model.put( MARK_RIGHT_DELETE,
                RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                    AppointmentResourceIdService.PERMISSION_DELETE_APPOINTMENT, AdminUserService.getAdminUser( request ) ) );
            model.put( MARK_RIGHT_VIEW,
                RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
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
        int nIdForm;

        if ( ( strIdForm != null ) && StringUtils.isNumeric( strIdForm ) )
        {
            nIdForm = Integer.parseInt( strIdForm );
        }
        else
        {
            String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );

            if ( StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) )
            {
                int nIdAppointment = Integer.parseInt( strIdAppointment );
                Appointment appointment = AppointmentHome.findByPrimaryKey( nIdAppointment );
                AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );
                nIdForm = slot.getIdForm(  );
            }
            else
            {
                return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
            }
        }

        AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

        if ( ( form == null ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );

        Appointment appointment = _appointmentFormService.getValidatedAppointmentFromSession( request.getSession(  ) );

        if ( appointment != null )
        {
            AppointmentDTO appointmentDTO = new AppointmentDTO(  );
            appointmentDTO.setEmail( appointment.getEmail(  ) );
            appointmentDTO.setFirstName( appointment.getFirstName(  ) );
            appointmentDTO.setLastName( appointment.getLastName(  ) );
            appointmentDTO.setIdAppointment( appointment.getIdAppointment(  ) );

            Map<Integer, List<Response>> mapResponsesByIdEntry = appointmentDTO.getMapResponsesByIdEntry(  );

            for ( Response response : appointment.getListResponse(  ) )
            {
                List<Response> listResponse = mapResponsesByIdEntry.get( response.getEntry(  ).getIdEntry(  ) );

                if ( listResponse == null )
                {
                    listResponse = new ArrayList<Response>(  );
                    mapResponsesByIdEntry.put( response.getEntry(  ).getIdEntry(  ), listResponse );
                }

                listResponse.add( response );
            }

            _appointmentFormService.saveAppointmentInSession( request.getSession(  ), appointmentDTO );
        }

        AppointmentFormMessages formMessages = AppointmentFormMessagesHome.findByPrimaryKey( nIdForm );
        model.put( MARK_FORM_HTML,
            _appointmentFormService.getHtmlForm( form, formMessages, getLocale(  ), false, request ) );

        List<GenericAttributeError> listErrors = (List<GenericAttributeError>) request.getSession(  )
                                                                                      .getAttribute( SESSION_APPOINTMENT_FORM_ERRORS );

        if ( listErrors != null )
        {
            model.put( MARK_FORM_ERRORS, listErrors );
            request.getSession(  ).removeAttribute( SESSION_APPOINTMENT_FORM_ERRORS );
        }

        _appointmentFormService.removeAppointmentFromSession( request.getSession(  ) );
        _appointmentFormService.removeValidatedAppointmentFromSession( request.getSession(  ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_APPOINTMENT, TEMPLATE_CREATE_APPOINTMENT, model );
    }

    /**
     * Get the page to modify an appointment
     * @param request The request
     * @return The HTML content to display or the next URL to redirect to
     */
    @View( VIEW_MODIFY_APPOINTMENT )
    public String getModifyAppointment( HttpServletRequest request )
    {
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );

        if ( StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) )
        {
            int nIdAppointment = Integer.parseInt( strIdAppointment );

            Appointment appointment = AppointmentHome.findByPrimaryKey( nIdAppointment );
            List<Integer> listIdResponse = AppointmentHome.findListIdResponse( appointment.getIdAppointment(  ) );
            List<Response> listResponses = new ArrayList<Response>( listIdResponse.size(  ) );

            for ( int nIdResponse : listIdResponse )
            {
                Response response = ResponseHome.findByPrimaryKey( nIdResponse );

                if ( response.getField(  ) != null )
                {
                    response.setField( FieldHome.findByPrimaryKey( response.getField(  ).getIdField(  ) ) );
                }

                listResponses.add( response );
            }

            appointment.setListResponse( listResponses );

            _appointmentFormService.saveValidatedAppointmentForm( request.getSession(  ), appointment );

            return getCreateAppointment( request );
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

            EntryFilter filter = new EntryFilter(  );
            filter.setIdResource( nIdForm );
            filter.setResourceType( AppointmentForm.RESOURCE_TYPE );
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
            filter.setIdIsComment( EntryFilter.FILTER_FALSE );

            List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );

            _appointmentFormService.removeAppointmentFromSession( request.getSession(  ) );

            List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>(  );
            Locale locale = request.getLocale(  );

            AppointmentDTO appointment;
            String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );

            if ( StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) )
            {
                appointment = new AppointmentDTO( AppointmentHome.findByPrimaryKey( Integer.parseInt( strIdAppointment ) ) );
            }
            else
            {
                appointment = new AppointmentDTO(  );
                appointment.setStatus( Appointment.STATUS_NOT_VALIDATED );

                if ( SecurityService.isAuthenticationEnable(  ) )
                {
                    LuteceUser luteceUser = SecurityService.getInstance(  ).getRegisteredUser( request );

                    if ( luteceUser != null )
                    {
                        appointment.setIdUser( luteceUser.getName(  ) );
                    }
                }
            }

            appointment.setEmail( request.getParameter( PARAMETER_EMAIL ) );
            appointment.setFirstName( request.getParameter( PARAMETER_FIRST_NAME ) );
            appointment.setLastName( request.getParameter( PARAMETER_LAST_NAME ) );

            // We save the appointment in session. The appointment object will contain responses of the user to the form
            _appointmentFormService.saveAppointmentInSession( request.getSession(  ), appointment );

            Set<ConstraintViolation<AppointmentDTO>> listErrors = BeanValidationUtil.validate( appointment );

            if ( !listErrors.isEmpty(  ) )
            {
                for ( ConstraintViolation<AppointmentDTO> constraintViolation : listErrors )
                {
                    GenericAttributeError genAttError = new GenericAttributeError(  );
                    genAttError.setErrorMessage( constraintViolation.getMessage(  ) );
                    listFormErrors.add( genAttError );
                }
            }

            for ( Entry entry : listEntryFirstLevel )
            {
                listFormErrors.addAll( _appointmentFormService.getResponseEntry( request, entry.getIdEntry(  ), locale,
                        appointment ) );
            }

            // If there is some errors, we redirect the user to the form page
            if ( listFormErrors.size(  ) > 0 )
            {
                request.getSession(  ).setAttribute( SESSION_APPOINTMENT_FORM_ERRORS, listFormErrors );

                return redirect( request, VIEW_CREATE_APPOINTMENT, PARAMETER_ID_FORM, nIdForm );
            }

            _appointmentFormService.convertMapResponseToList( appointment );
            _appointmentFormService.saveValidatedAppointmentForm( request.getSession(  ), appointment );

            if ( appointment.getIdAppointment(  ) > 0 )
            {
                AppointmentHome.update( appointment );

                List<Integer> listIdResponse = AppointmentHome.findListIdResponse( appointment.getIdAppointment(  ) );

                for ( int nIdResponse : listIdResponse )
                {
                    ResponseHome.remove( nIdResponse );
                }

                AppointmentHome.removeAppointmentResponse( appointment.getIdAppointment(  ) );

                for ( Response response : appointment.getListResponse(  ) )
                {
                    ResponseHome.create( response );
                    AppointmentHome.insertAppointmentResponse( appointment.getIdAppointment(  ),
                        response.getIdResponse(  ) );
                }
            }

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

            if ( _appointmentFormService.getValidatedAppointmentFromSession( request.getSession(  ) ) == null )
            {
                return redirect( request, VIEW_CREATE_APPOINTMENT, PARAMETER_ID_FORM, nIdForm );
            }

            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

            Map<String, Object> model = new HashMap<String, Object>(  );

            String strNbWeek = request.getParameter( PARAMETER_NB_WEEK );
            int nNbWeek = 0;

            if ( StringUtils.isNotEmpty( strNbWeek ) && StringUtils.isNumeric( strNbWeek ) )
            {
                nNbWeek = Integer.parseInt( strNbWeek );

                if ( nNbWeek > ( form.getNbWeeksToDisplay(  ) - 1 ) )
                {
                    nNbWeek = form.getNbWeeksToDisplay(  ) - 1;
                }
            }

            List<AppointmentDay> listDays = AppointmentService.getService(  ).getDayListForCalendar( form, nNbWeek );

            List<String> listTimeBegin = new ArrayList<String>(  );
            int nMinAppointmentDuration = AppointmentService.getService(  )
                                                            .getListTimeBegin( listDays, form, listTimeBegin );

            model.put( MARK_FORM, form );
            model.put( MARK_FORM_MESSAGES, AppointmentFormMessagesHome.findByPrimaryKey( nIdForm ) );
            model.put( MARK_LIST_DAYS, listDays );
            model.put( MARK_LIST_TIME_BEGIN, listTimeBegin );
            model.put( MARK_MIN_DURATION_APPOINTMENT, nMinAppointmentDuration );
            model.put( PARAMETER_NB_WEEK, nNbWeek );
            model.put( MARK_LIST_DAYS_OF_WEEK, MESSAGE_LIST_DAYS_OF_WEEK );

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
                AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );

                if ( WorkflowService.getInstance(  ).isAvailable(  ) )
                {
                    WorkflowService.getInstance(  ).doRemoveWorkFlowResource( nId, Appointment.APPOINTMENT_RESOURCE_TYPE );
                }

                AppointmentHome.remove( nId );
                addInfo( INFO_APPOINTMENT_REMOVED, getLocale(  ) );

                return redirect( request, VIEW_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, slot.getIdForm(  ) );
            }
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

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
            Appointment appointment = _appointmentFormService.getValidatedAppointmentFromSession( request.getSession(  ) );

            int nIdSlot = Integer.parseInt( strIdSlot );
            AppointmentSlot appointmentSlot = AppointmentSlotHome.findByPrimaryKey( nIdSlot );

            if ( appointment != null )
            {
                appointment.setIdSlot( nIdSlot );

                Map<String, Object> model = new HashMap<String, Object>(  );
                model.put( MARK_APPOINTMENT, appointment );
                model.put( MARK_SLOT, appointmentSlot );

                AppointmentForm form = AppointmentFormHome.findByPrimaryKey( appointmentSlot.getIdForm(  ) );
                AppointmentDay day = AppointmentDayHome.findByPrimaryKey( appointmentSlot.getIdDay(  ) );
                appointment.setDateAppointment( (Date) day.getDate(  ).clone(  ) );
                model.put( MARK_DAY, day );
                model.put( MARK_FORM, form );
                model.put( MARK_FORM_MESSAGES,
                    AppointmentFormMessagesHome.findByPrimaryKey( appointmentSlot.getIdForm(  ) ) );
                fillCommons( model );

                return getPage( PROPERTY_PAGE_TITLE_RECAP_APPOINTMENT, TEMPLATE_APPOINTMENT_FORM_RECAP, model );
            }

            return redirect( request, VIEW_GET_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, appointmentSlot.getIdForm(  ) );
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
        Appointment appointment = _appointmentFormService.getValidatedAppointmentFromSession( request.getSession(  ) );
        AppointmentSlot appointmentSlot = AppointmentSlotHome.findByPrimaryKeyWithFreePlaces( appointment.getIdSlot(  ),
                appointment.getDateAppointment(  ) );
        AppointmentForm form = AppointmentFormHome.findByPrimaryKey( appointmentSlot.getIdForm(  ) );

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_BACK ) ) )
        {
            return redirect( request, VIEW_GET_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, appointmentSlot.getIdForm(  ) );
        }

        if ( appointmentSlot.getNbFreePlaces(  ) <= 0 )
        {
            addError( ERROR_MESSAGE_SLOT_FULL, getLocale(  ) );

            return redirect( request, VIEW_GET_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, appointmentSlot.getIdForm(  ) );
        }

        if ( appointment.getIdAppointment(  ) == 0 )
        {
            AppointmentHome.create( appointment );

            for ( Response response : appointment.getListResponse(  ) )
            {
                ResponseHome.create( response );
                AppointmentHome.insertAppointmentResponse( appointment.getIdAppointment(  ), response.getIdResponse(  ) );
            }
        }
        else
        {
            AppointmentHome.update( appointment );
        }

        if ( form.getIdWorkflow(  ) > 0 )
        {
            WorkflowService.getInstance(  )
                           .getState( appointment.getIdAppointment(  ), Appointment.APPOINTMENT_RESOURCE_TYPE,
                form.getIdWorkflow(  ), form.getIdForm(  ) );
            WorkflowService.getInstance(  )
                           .executeActionAutomatic( appointment.getIdAppointment(  ),
                Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow(  ), form.getIdForm(  ) );
        }

        _appointmentFormService.removeValidatedAppointmentFromSession( request.getSession(  ) );

        addInfo( INFO_APPOINTMENT_CREATED, getLocale(  ) );

        return redirect( request, VIEW_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, appointmentSlot.getIdForm(  ) );
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

        List<Response> listResponse = AppointmentHome.findListResponse( nId );

        appointment.setListResponse( listResponse );

        Map<String, Object> model = getModel(  );
        model.put( MARK_APPOINTMENT, appointment );

        AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );
        model.put( MARK_SLOT, slot );
        model.put( MARK_FORM, AppointmentFormHome.findByPrimaryKey( slot.getIdForm(  ) ) );
        model.put( MARK_FORM_MESSAGES, AppointmentFormMessagesHome.findByPrimaryKey( slot.getIdForm(  ) ) );

        return getPage( PROPERTY_PAGE_TITLE_VIEW_APPOINTMENT, TEMPLATE_VIEW_APPOINTMENT, model );
    }

    /**
     * Do change the status of an appointment
     * @param request The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_CHANGE_APPOINTMENT_STATUS )
    public String doChangeAppointmentStatus( HttpServletRequest request )
    {
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );
        if ( StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) )
        {
            int nIdAppointment = Integer.parseInt( strIdAppointment );
            String strNewStatus = request.getParameter( PARAMETER_NEW_STATUS );
            int nNewStatus = parseInt( strNewStatus );
            Appointment appointment = AppointmentHome.findByPrimaryKey( nIdAppointment );

            // We check that the status has changed to avoid doing unnecessary updates.
            // Also, it is not permitted to set the status of an appointment to not validated.
            if ( appointment.getStatus( ) != nNewStatus && nNewStatus != Appointment.STATUS_NOT_VALIDATED )
            {
                appointment.setStatus( nNewStatus );
                AppointmentHome.update( appointment );
            }
            AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot( ) );

            UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_APPOINTMENTS );
            url.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_APPOINTMENTS );
            url.addParameter( PARAMETER_ID_FORM, slot.getIdForm( ) );
            url.addParameter( MARK_FILTER_FROM_SESSION, Boolean.TRUE.toString( ) );

            return redirect( request, url.getUrl( ) );
        }
        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Get the workflow action form before processing the action. If the action
     * does not need to display any form, then redirect the user to the workflow
     * action processing page.
     * @param request The request
     * @return The HTML content to display, or the next URL to redirect the user
     *         to
     */
    @View( VIEW_WORKFLOW_ACTION_FORM )
    public String getWorkflowActionForm( HttpServletRequest request )
    {
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) &&
                StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdAppointment = Integer.parseInt( strIdAppointment );

            if ( WorkflowService.getInstance(  ).isDisplayTasksForm( nIdAction, getLocale(  ) ) )
            {
                String strHtmlTasksForm = WorkflowService.getInstance(  )
                                                         .getDisplayTasksForm( nIdAppointment,
                        Appointment.APPOINTMENT_RESOURCE_TYPE, nIdAction, request, getLocale(  ) );

                Map<String, Object> model = new HashMap<String, Object>(  );

                model.put( MARK_TASKS_FORM, strHtmlTasksForm );
                model.put( PARAMETER_ID_ACTION, nIdAction );
                model.put( PARAMETER_ID_APPOINTMENT, nIdAppointment );

                return getPage( PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW, TEMPLATE_TASKS_FORM_WORKFLOW, model );
            }

            return doProcessWorkflowAction( request );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Do process a workflow action over an appointment
     * @param request The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_PROCESS_WORKFLOW_ACTION )
    public String doProcessWorkflowAction( HttpServletRequest request )
    {
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) &&
                StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdAppointment = Integer.parseInt( strIdAppointment );

            Appointment appointment = AppointmentHome.findByPrimaryKey( nIdAppointment );
            AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );
            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( slot.getIdForm(  ) );

            if ( request.getParameter( PARAMETER_BACK ) == null )
            {
                if ( WorkflowService.getInstance(  ).isDisplayTasksForm( nIdAction, getLocale(  ) ) )
                {
                    String strError = WorkflowService.getInstance(  )
                                                     .doSaveTasksForm( nIdAppointment,
                            Appointment.APPOINTMENT_RESOURCE_TYPE, nIdAction, form.getIdForm(  ), request, getLocale(  ) );

                    if ( strError != null )
                    {
                        return redirect( request, strError );
                    }
                }

                WorkflowService.getInstance(  )
                               .doProcessAction( nIdAppointment, Appointment.APPOINTMENT_RESOURCE_TYPE, nIdAction,
                    form.getIdForm(  ), request, getLocale(  ), false );

                Map<String, String> mapParams = new HashMap<String, String>(  );
                mapParams.put( PARAMETER_ID_FORM, Integer.toString( form.getIdForm(  ) ) );
                mapParams.put( MARK_FILTER_FROM_SESSION, Boolean.TRUE.toString(  ) );

                return redirect( request, VIEW_MANAGE_APPOINTMENTS, mapParams );
            }
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
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

    /**
     * Parse a string representing a positive or negative integer
     * @param strNumber The string to parse
     * @return The integer value of the number represented by the string, or 0
     *         if the string could not be parsed
     */
    private int parseInt( String strNumber )
    {
        int nNumber = 0;
        if ( StringUtils.isEmpty( strNumber ) )
        {
            return nNumber;
        }
        if ( strNumber.startsWith( CONSTANT_MINUS ) )
        {
            String strParseableNumber = strNumber.substring( 1 );
            if ( StringUtils.isNumeric( strParseableNumber ) )
            {
                nNumber = Integer.parseInt( strParseableNumber ) * -1;
            }
        }
        else if ( StringUtils.isNumeric( strNumber ) )
        {
            nNumber = Integer.parseInt( strNumber );
        }
        return nNumber;
    }
}
