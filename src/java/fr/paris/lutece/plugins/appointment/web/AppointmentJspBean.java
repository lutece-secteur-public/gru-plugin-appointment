/*
 * Copyright (c) 2002-2020, City of Paris
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
import java.io.OutputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.comment.CommentHome;
import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.message.FormMessage;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.rule.FormRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.exception.AppointmentSavedException;
import fr.paris.lutece.plugins.appointment.exception.SlotFullException;
import fr.paris.lutece.plugins.appointment.log.LogUtilities;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.service.AppointmentResponseService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.AppointmentUtilities;
import fr.paris.lutece.plugins.appointment.service.DisplayService;
import fr.paris.lutece.plugins.appointment.service.EntryService;
import fr.paris.lutece.plugins.appointment.service.FormMessageService;
import fr.paris.lutece.plugins.appointment.service.FormRuleService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.ReservationRuleService;
import fr.paris.lutece.plugins.appointment.service.SlotSafeService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.Utilities;
import fr.paris.lutece.plugins.appointment.service.WeekDefinitionService;
import fr.paris.lutece.plugins.appointment.service.addon.AppointmentAddOnManager;
import fr.paris.lutece.plugins.appointment.service.listeners.AppointmentListenerManager;
import fr.paris.lutece.plugins.appointment.service.lock.SlotEditTask;
import fr.paris.lutece.plugins.appointment.service.lock.TimerForLockOnSlot;
import fr.paris.lutece.plugins.appointment.service.upload.AppointmentAsynchronousUploadHandler;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFilterDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage Appointment features ( manage, create, modify, remove )
 * 
 * @author Laurent Payen
 * 
 */
@Controller( controllerJsp = "ManageAppointments.jsp", controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM )
public class AppointmentJspBean extends MVCAdminJspBean
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 1978001810468444844L;
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    // //////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_APPOINTMENTS_CALENDAR = "/admin/plugins/appointment/appointment/manage_appointments_calendar.html";
    private static final String TEMPLATE_MANAGE_APPOINTMENTS_CALENDAR_GROUPED  = "/admin/plugins/appointment/appointment/appointment_form_list_open_slots_grouped.html";
    private static final String TEMPLATE_CREATE_APPOINTMENT = "/admin/plugins/appointment/appointment/create_appointment.html";
    private static final String TEMPLATE_MANAGE_APPOINTMENTS = "/admin/plugins/appointment/appointment/manage_appointments.html";
    private static final String TEMPLATE_VIEW_APPOINTMENT = "/admin/plugins/appointment/appointment/view_appointment.html";
    private static final String TEMPLATE_HTML_CODE_FORM_ADMIN = "admin/plugins/appointment/html_code_form.html";
    private static final String TEMPLATE_APPOINTMENT_FORM_RECAP = "/admin/plugins/appointment/appointment/appointment_form_recap.html";
    private static final String TEMPLATE_TASKS_FORM_WORKFLOW = "admin/plugins/appointment/appointment/tasks_form_workflow.html";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS = "appointment.manageAppointments.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS_CALENDAR = "appointment.manageAppointmentCalendar.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_APPOINTMENT = "appointment.name.create";
    private static final String PROPERTY_PAGE_TITLE_VIEW_APPOINTMENT = "appointment.viewAppointment.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_RECAP_APPOINTMENT = "appointment.appointmentApp.recap.title";
    private static final String PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW = "appointment.taskFormWorkflow.pageTitle";

    private static final String UNRESERVED = "appointment.message.labelStatusUnreserved";
    private static final String RESERVED = "appointment.message.labelStatusReserved";

    // Connected User
    private static final String PROPERTY_USER_EMAIL = "user.business-info.online.email";
    private static final String PROPERTY_USER_FIRST_NAME = "user.name.given";
    private static final String PROPERTY_USER_LAST_NAME = "user.name.family";

    // Parameters
    private static final String PARAMETER_ID_RESPONSE = "idResponse";
    private static final String PARAMETER_IS_OPEN = "is_open";
    private static final String PARAMETER_IS_SPECIFIC = "is_specific";
    private static final String PARAMETER_MAX_CAPACITY = "max_capacity";
    private static final String PARAMETER_STARTING_DATE_TIME = "starting_date_time";
    private static final String PARAMETER_ENDING_DATE_TIME = "ending_date_time";
    private static final String PARAMETER_STARTING_DATE_OF_DISPLAY = "starting_date_of_display";
    private static final String PARAMETER_STR_STARTING_DATE_OF_DISPLAY = "str_starting_date_of_display";
    private static final String PARAMETER_ENDING_DATE_OF_DISPLAY = "ending_date_of_display";
    private static final String PARAMETER_STR_ENDING_DATE_OF_DISPLAY = "str_ending_date_of_display";
    private static final String PARAMETER_DATE_OF_DISPLAY = "date_of_display";
    private static final String PARAMETER_DAY_OF_WEEK = "dow";
    private static final String PARAMETER_EVENTS = "events";
    private static final String PARAMETER_EVENTS_COMMENTS = "comment_events";
    private static final String PARAMETER_MIN_DURATION = "min_duration";
    private static final String PARAMETER_MIN_TIME = "min_time";
    private static final String PARAMETER_MAX_TIME = "max_time";
    private static final String PARAMETER_ID_APPOINTMENT = "id_appointment";
    private static final String PARAMETER_ID_ACTION = "id_action";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_COME_FROM_CALENDAR = "comeFromCalendar";
    private static final String PARAMETER_EMAIL = "email";
    private static final String PARAMETER_EMAIL_CONFIRMATION = "emailConfirm";
    private static final String PARAMETER_FIRST_NAME = "firstname";
    private static final String PARAMETER_LAST_NAME = "lastname";
    private static final String PARAMETER_ID_SLOT = "id_slot";
    private static final String PARAMETER_BACK = "back";
    private static final String PARAMETER_ORDER_BY = "orderBy";
    private static final String PARAMETER_ORDER_ASC = "orderAsc";
    private static final String PARAMETER_ID_APPOINTMENT_DELETE = "apmt";
    private static final String PARAMETER_DELETE_AND_BACK = "eraseAll";
    private static final String PARAMETER_SEARCH = "Search";
    private static final String PARAMETER_RESET = "reset";
    private static final String PARAMETER_NUMBER_OF_BOOKED_SEATS = "nbBookedSeats";
    private static final String PARAMETER_STATUS_CANCELLED = "status_cancelled";
    private static final String PARAMETER_MODIF_DATE = "modif_date";
    private static final String PARAMETER_IS_MODIFICATION = "is_modification";
    private static final String PARAMETER_NB_PLACE_TO_TAKE = "nbPlacesToTake";

    // Markers
    private static final String MARK_TASKS_FORM = "tasks_form";
    private static final String MARK_APPOINTMENT_LIST = "appointment_list";
    private static final String MARK_APPOINTMENT = "appointment";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_FORM_MESSAGES = "formMessages";
    private static final String MARK_FORM_HTML = "form_html";
    private static final String MARK_FORM = "form";
    private static final String MARK_MODIFICATION_DATE_APPOINTMENT = "modifDateAppointment";
    private static final String MARK_FORM_CALENDAR_ERRORS = "formCalendarErrors";
    private static final String MARK_FORM_ERRORS = "form_errors";
    private static final String MARK_LIST_ERRORS = "listAllErrors";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_PLACES = "nbplaces";
    private static final String MARK_STR_ENTRY = "str_entry";
    private static final String MARK_RIGHT_CREATE = "rightCreate";
    private static final String MARK_RIGHT_MODIFY = "rightModify";
    private static final String MARK_RIGHT_DELETE = "rightDelete";
    private static final String MARK_RIGHT_VIEW = "rightView";
    private static final String MARK_RIGHT_CHANGE_STATUS = "rightChangeStatus";
    private static final String MARK_RIGHT_CHANGE_DATE = "rightChangeDate";
    private static final String MARK_FILTER = "filter";
    private static final String MARK_LIST_STATUS = "listStatus";
    private static final String MARK_RESOURCE_HISTORY = "resource_history";
    private static final String MARK_ADDON = "addon";
    private static final String MARK_LIST_RESPONSE_RECAP_DTO = "listResponseRecapDTO";
    private static final String MARK_LANGUAGE = "language";
    private static final String MARK_ACTIVATE_WORKFLOW = "activateWorkflow";
    private static final String MARK_FORM_OVERBOOKING_ALLOWED = "overbookingAllowed";

    private static final String JSP_MANAGE_APPOINTMENTS = "jsp/admin/plugins/appointment/ManageAppointments.jsp";
    private static final String ERROR_MESSAGE_SLOT_FULL = "appointment.message.error.slotFull";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_APPOINTMENT = "appointment.message.confirmRemoveAppointment";
    private static final String MESSAGE_CONFIRM_REMOVE_MASSAPPOINTMENT = "appointment.message.confirmRemoveMassAppointment";

    // Properties
    private static final String PROPERTY_DEFAULT_LIST_APPOINTMENT_PER_PAGE = "appointment.listAppointments.itemsPerPage";
    private static final String PROPERTY_NB_WEEKS_TO_DISPLAY_IN_BO = "appointment.nbWeeksToDisplayInBO";
    private static final String PROPERTY_NB_MAX_APPOINTMENTS_TO_EXPORT = "appointment.nbMaxAppointmentsToExport";
    // Views
    private static final String VIEW_MANAGE_APPOINTMENTS = "manageAppointments";
    private static final String VIEW_CREATE_APPOINTMENT = "createAppointment";
    private static final String VIEW_MODIFY_APPOINTMENT = "modifyAppointment";
    private static final String VIEW_VIEW_APPOINTMENT = "viewAppointment";
    private static final String VIEW_DISPLAY_RECAP_APPOINTMENT = "displayRecapAppointment";
    private static final String VIEW_CALENDAR_MANAGE_APPOINTMENTS = "viewCalendarManageAppointment";
    private static final String VIEW_WORKFLOW_ACTION_FORM = "viewWorkflowActionForm";
    private static final String VIEW_CHANGE_DATE_APPOINTMENT = "viewChangeDateAppointment";

    // Actions
    private static final String ACTION_DO_VALIDATE_FORM = "doValidateForm";
    private static final String ACTION_REMOVE_APPOINTMENT = "removeAppointment";
    private static final String ACTION_REMOVE_MASSAPPOINTMENT = "removeMassAppointment";
    private static final String ACTION_CONFIRM_REMOVE_APPOINTMENT = "confirmRemoveAppointment";
    private static final String ACTION_CONFIRM_REMOVE_MASS_APPOINTMENT = "confirmRemoveMassAppointment";
    private static final String ACTION_DO_MAKE_APPOINTMENT = "doMakeAppointment";
    private static final String ACTION_DO_PROCESS_WORKFLOW_ACTION = "doProcessWorkflowAction";
    private static final String ACTION_DO_CHANGE_APPOINTMENT_STATUS = "doChangeAppointmentStatus";

    // Infos
    private static final String INFO_APPOINTMENT_CREATED = "appointment.info.appointment.created";
    private static final String INFO_APPOINTMENT_REMOVED = "appointment.info.appointment.removed";
    private static final String INFO_APPOINTMENT_MASSREMOVED = "appointment.info.appointment.removed";

    // Error
    private static final String ERROR_MESSAGE_FORM_NOT_ACTIVE = "appointment.validation.appointment.formNotActive";
    private static final String ERROR_MESSAGE_NO_STARTING_VALIDITY_DATE = "appointment.validation.appointment.noStartingValidityDate";
    private static final String ERROR_MESSAGE_FORM_NO_MORE_VALID = "appointment.validation.appointment.formNoMoreValid";
    private static final String MESSAGE_UNVAILABLE_SLOT = "appointment.slot.unvailable";
    private static final String ERROR_MESSAGE_NB_MAX_APPOINTMENTS_FOR_EXPORT = "appointment.manageAppointments.nbMaxAppointmentsForExport";

    // Session keys
    private static final String SESSION_CURRENT_PAGE_INDEX = "appointment.session.currentPageIndex";
    private static final String SESSION_ITEMS_PER_PAGE = "appointment.session.itemsPerPage";
    private static final String SESSION_NOT_VALIDATED_APPOINTMENT = "appointment.appointmentFormService.notValidatedAppointment";
    private static final String SESSION_VALIDATED_APPOINTMENT = "appointment.appointmentFormService.validatedAppointment";
    private static final String SESSION_APPOINTMENT_FORM_ERRORS = "appointment.session.formErrors";
    private static final String SESSION_ATTRIBUTE_APPOINTMENT_FORM = "appointment.session.appointmentForm";
    private static final String SESSION_APPOINTMENT_FILTER = "appointment.session.filter";
    private static final String SESSION_LIST_APPOINTMENTS = "appointment.session.listAppointments";

    // Constants
    private static final String DEFAULT_CURRENT_PAGE = "1";
    public static final String ACTIVATEWORKFLOW = AppPropertiesService.getProperty( "appointment.activate.workflow" );
    public static final String PREVIOUS_FORM = "calendar";
    private static final String LAST_NAME = "last_name";
    private static final String FIRST_NAME = "first_name";
    private static final String EMAIL = "email";
    private static final String NB_BOOKED_SEATS = "nbBookedSeats";
    private static final String DATE_APPOINTMENT = "date_appointment";
    private static final String ADMIN = "admin";
    private static final String STATUS = "status";
    private static final int MAX_NB_APPOINTMENTS_TO_EXPORT = 8000;
    // services
    private final transient StateService _stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );
    private final transient ITaskService _taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );

    // Session variable to store working values
    private int _nDefaultItemsPerPage;
    private int nNbPlacesToTake;

    /**
     * Get the page to manage appointments. Appointments are displayed in a calendar.
     * 
     * @param request
     *            The request
     * @return The HTML code to display
     * @throws AccessDeniedException
     */
    @View( value = VIEW_CALENDAR_MANAGE_APPOINTMENTS, defaultView = true )
    public String getViewCalendarManageAppointments( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, "0", AppointmentResourceIdService.PERMISSION_VIEW_FORM, getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_VIEW_FORM );
        }

        cleanSession( request.getSession( ) );
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );
        String nbPlacesToTake = request.getParameter( PARAMETER_NB_PLACE_TO_TAKE );
        AppointmentDTO appointmentDTO = null;
        if ( StringUtils.isNotEmpty( strIdAppointment ) )
        {
            // If we want to change the date of an appointment
            int nIdAppointment = Integer.parseInt( strIdAppointment );
            appointmentDTO = AppointmentService.buildAppointmentDTOFromIdAppointment( nIdAppointment );
            AppointmentService.addAppointmentResponses( appointmentDTO );
        }
        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        Form form = FormService.findFormLightByPrimaryKey( nIdForm );
        AppointmentFormDTO appointmentForm = FormService.buildAppointmentForm( nIdForm, 0, 0 );

        boolean bError = false;
        if ( !form.getIsActive( ) )
        {
            addError( ERROR_MESSAGE_FORM_NOT_ACTIVE, getLocale( ) );
            bError = true;
        }
        FormMessage formMessages = FormMessageService.findFormMessageByIdForm( nIdForm );
        // Check if the date of display and the endDateOfDisplay are in the
        // validity date range of the form
        LocalDate startingValidityDate = form.getStartingValidityDate( );
        if ( startingValidityDate == null )
        {
            addError( ERROR_MESSAGE_NO_STARTING_VALIDITY_DATE, getLocale( ) );
            bError = true;
        }
        Display display = DisplayService.findDisplayWithFormId( nIdForm );
        int nNbWeeksToDisplay = AppPropertiesService.getPropertyInt( PROPERTY_NB_WEEKS_TO_DISPLAY_IN_BO, display.getNbWeeksToDisplay( ) );
        LocalDate startingDateOfDisplay = LocalDate.now( ).minusWeeks( nNbWeeksToDisplay );
        LocalDate endingDateOfDisplay = LocalDate.now( ).plusWeeks( nNbWeeksToDisplay );
        LocalDate endingValidityDate = form.getEndingValidityDate( );
        if ( endingValidityDate != null )
        {
            if ( startingDateOfDisplay.isAfter( endingDateOfDisplay ) )
            {
                addError( ERROR_MESSAGE_FORM_NO_MORE_VALID, getLocale( ) );
                bError = true;
            }
            if ( endingDateOfDisplay.isAfter( endingValidityDate ) )
            {
                endingDateOfDisplay = endingValidityDate;
            }
        }
        String strDateOfDisplay = request.getParameter( PARAMETER_DATE_OF_DISPLAY );
        LocalDate dateOfDisplay = LocalDate.now( );
        if ( StringUtils.isNotEmpty( strDateOfDisplay ) )
        {
            dateOfDisplay = LocalDate.parse( strDateOfDisplay );
        }
        List<Slot> listSlot = new ArrayList<>( );
        HashMap<LocalDate, WeekDefinition> mapWeekDefinition = WeekDefinitionService.findAllWeekDefinition( nIdForm );
        List<WeekDefinition> listWeekDefinition = new ArrayList<>( mapWeekDefinition.values( ) );
        LocalTime maxEndingTime = WeekDefinitionService.getMaxEndingTimeOfAListOfWeekDefinition( listWeekDefinition );
        LocalTime minStartingTime = WeekDefinitionService.getMinStartingTimeOfAListOfWeekDefinition( listWeekDefinition );
        List<String> listDayOfWeek = new ArrayList<>( WeekDefinitionService.getSetDaysOfWeekOfAListOfWeekDefinitionForFullCalendar( listWeekDefinition ) );
        if ( !bError )
        {

            boolean isNewNbPlacesToTake = ( nbPlacesToTake != null && StringUtils.isNumeric( nbPlacesToTake ) );
            if ( appointmentForm.getIsMultislotAppointment( ) && (( nNbPlacesToTake != 0 || isNewNbPlacesToTake ) && nbPlacesToTake != null ))
            {

                nNbPlacesToTake = isNewNbPlacesToTake ? Integer.parseInt( nbPlacesToTake ) : nNbPlacesToTake;
                listSlot = SlotService.buildListSlot( nIdForm, mapWeekDefinition, startingDateOfDisplay, endingDateOfDisplay, nNbPlacesToTake );

            }
            else
            {

                nNbPlacesToTake = 0;
                listSlot = SlotService.buildListSlot( nIdForm, mapWeekDefinition, startingDateOfDisplay, endingDateOfDisplay );
            }

            // listSlot = SlotService.buildListSlot( nIdForm, mapWeekDefinition, startingDateOfDisplay, endingDateOfDisplay );
            // Tag as passed the slots passed
            List<Slot> listSlotsPassed = listSlot.stream( ).filter( s -> s.getEndingDateTime( ).isBefore( LocalDateTime.now( ) ) )
                    .collect( Collectors.toList( ) );
            for ( Slot slotPassed : listSlotsPassed )
            {
                slotPassed.setIsPassed( Boolean.TRUE );
            }
        }
        Map<String, Object> model = getModel( );
        if ( bError )
        {
            model.put( MARK_FORM_CALENDAR_ERRORS, bError );
        }
        // If we change the date of an appointment
        // filter the list of slot with only the ones that have enough places at
        // the moment of the edition
        if ( appointmentDTO != null )
        {
            int nbBookedSeats = appointmentDTO.getNbBookedSeats( );
            listSlot = listSlot.stream( ).filter( s -> s.getNbPotentialRemainingPlaces( ) >= nbBookedSeats && s.getIsOpen( ) ).collect( Collectors.toList( ) );
            request.getSession( ).setAttribute( SESSION_VALIDATED_APPOINTMENT, appointmentDTO );
            model.put( MARK_MODIFICATION_DATE_APPOINTMENT, true );
        }
        else
        {
            model.put( MARK_MODIFICATION_DATE_APPOINTMENT, false );
        }

        model.put( MARK_FORM, appointmentForm );
        model.put( PARAMETER_ID_FORM, nIdForm );
        model.put( MARK_FORM_MESSAGES, formMessages );
        model.put( PARAMETER_STARTING_DATE_OF_DISPLAY, startingDateOfDisplay );
        model.put( PARAMETER_STR_STARTING_DATE_OF_DISPLAY, startingDateOfDisplay.format( Utilities.getFormatter( ) ) );
        model.put( PARAMETER_ENDING_DATE_OF_DISPLAY, endingDateOfDisplay );
        model.put( PARAMETER_STR_ENDING_DATE_OF_DISPLAY, endingDateOfDisplay.format( Utilities.getFormatter( ) ) );
        model.put( PARAMETER_DATE_OF_DISPLAY, dateOfDisplay );
        model.put( PARAMETER_DAY_OF_WEEK, listDayOfWeek );
        model.put( PARAMETER_EVENTS, listSlot );
        model.put( PARAMETER_EVENTS_COMMENTS, CommentHome.selectCommentsList( (Date) Date.valueOf(startingDateOfDisplay ), (Date) Date.valueOf( endingDateOfDisplay ), nIdForm ) );
        model.put( PARAMETER_MIN_TIME, minStartingTime );
        model.put( PARAMETER_MAX_TIME, maxEndingTime );
        model.put( PARAMETER_MIN_DURATION, LocalTime.MIN.plusMinutes( AppointmentUtilities.THIRTY_MINUTES ) );
        model.put( MARK_FORM_OVERBOOKING_ALLOWED, appointmentForm.getBoOverbooking( ) );

        if( appointmentForm.getIsMultislotAppointment( ) && nNbPlacesToTake > 1) {
        	
        	return getPage( PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS_CALENDAR, TEMPLATE_MANAGE_APPOINTMENTS_CALENDAR_GROUPED, model );

        }else {
        	return getPage( PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS_CALENDAR, TEMPLATE_MANAGE_APPOINTMENTS_CALENDAR, model );
        }
    }

    /**
     * Get the page to manage appointments
     * 
     * @param request
     *            The request
     * @return The HTML code to display
     * @throws AccessDeniedException
     * @throws SiteMessageException
     */
    @SuppressWarnings( "unchecked" )
    @View( value = VIEW_MANAGE_APPOINTMENTS )
    public String getManageAppointments( HttpServletRequest request ) throws AccessDeniedException, SiteMessageException
    {
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, "0", AppointmentResourceIdService.PERMISSION_VIEW_FORM, getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_VIEW_FORM );
        }
        String strModifDateAppointment = request.getParameter( PARAMETER_MODIF_DATE );
        if ( strModifDateAppointment != null && Boolean.parseBoolean( strModifDateAppointment ) )
        {
            return getViewChangeDateAppointment( request ) ;
        }
        // Clean session
        AppointmentAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ) );
        request.getSession( ).removeAttribute( SESSION_NOT_VALIDATED_APPOINTMENT );
        request.getSession( ).removeAttribute( SESSION_VALIDATED_APPOINTMENT );

        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = Integer.parseInt( strIdForm );
        // If it is a new search
        if ( request.getParameter( PARAMETER_RESET ) != null )
        {
            request.getSession( ).removeAttribute( SESSION_APPOINTMENT_FILTER );
            request.getSession( ).removeAttribute( SESSION_LIST_APPOINTMENTS );
        }
        // Get the appointment filter in session
        AppointmentFilterDTO filter = (AppointmentFilterDTO) request.getSession( ).getAttribute( SESSION_APPOINTMENT_FILTER );
        if ( filter == null )
        {
            filter = new AppointmentFilterDTO( );
            filter.setIdForm( nIdForm );
            // if we come from the calendar, need to get the starting and ending
            // time of the slot
            String strStartingDateTime = request.getParameter( PARAMETER_STARTING_DATE_TIME );
            String strEndingDateTime = request.getParameter( PARAMETER_ENDING_DATE_TIME );
            if ( strStartingDateTime != null && strEndingDateTime != null )
            {
                LocalDateTime startingDateTime = LocalDateTime.parse( strStartingDateTime );
                LocalDateTime endingDateTime = LocalDateTime.parse( strEndingDateTime );
                filter.setStartingDateOfSearch( Date.valueOf( startingDateTime.toLocalDate( ) ) );
                filter.setStartingTimeOfSearch( startingDateTime.toLocalTime( ).toString( ) );
                filter.setEndingDateOfSearch( Date.valueOf( endingDateTime.toLocalDate( ) ) );
                filter.setEndingTimeOfSearch( endingDateTime.toLocalTime( ).toString( ) );
            }
            request.getSession( ).setAttribute( SESSION_APPOINTMENT_FILTER, filter );
        }
        // Get the list in session
        // If it is an order by or a navigation page, no need to search again
        List<AppointmentDTO> listAppointmentsDTO = (List<AppointmentDTO>) request.getSession( ).getAttribute( SESSION_LIST_APPOINTMENTS );
        if ( listAppointmentsDTO == null )
        {
            listAppointmentsDTO = AppointmentService.findListAppointmentsDTOByFilter( filter );
        }
        // If it is a new search
        if ( request.getParameter( PARAMETER_SEARCH ) != null )
        {
            // Populate the filter
            populate( filter, request );
            listAppointmentsDTO = AppointmentService.findListAppointmentsDTOByFilter( filter );
        }
        // If it is an order by
        String strOrderBy = request.getParameter( PARAMETER_ORDER_BY );
        String strOrderAsc = request.getParameter( PARAMETER_ORDER_ASC );
        listAppointmentsDTO = orderList( listAppointmentsDTO, strOrderBy, strOrderAsc );
        request.getSession( ).setAttribute( SESSION_LIST_APPOINTMENTS, listAppointmentsDTO );
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_DELETE_AND_BACK ) ) )
        {
            String [ ] tabIdAppointmentToDelete = request.getParameterValues( PARAMETER_ID_APPOINTMENT_DELETE );
            if ( tabIdAppointmentToDelete != null )
            {
                request.getSession( ).setAttribute( PARAMETER_ID_APPOINTMENT_DELETE, tabIdAppointmentToDelete );
                return getConfirmRemoveMassAppointment( request, nIdForm );
            }
        }
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
        UrlItem url = new UrlItem( JSP_MANAGE_APPOINTMENTS );
        url.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_APPOINTMENTS );
        url.addParameter( PARAMETER_ID_FORM, strIdForm );
        String strUrl = url.getUrl( );
        LocalizedPaginator<AppointmentDTO> paginator = new LocalizedPaginator<AppointmentDTO>( listAppointmentsDTO, nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX,
                strCurrentPageIndex, getLocale( ) );
        AppointmentFormDTO form = FormService.buildAppointmentFormLight( nIdForm );
        Map<String, Object> model = getModel( );
        model.put( MARK_FORM, form );
        model.put( MARK_FORM_MESSAGES, FormMessageService.findFormMessageByIdForm( nIdForm ) );
        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_LANGUAGE, getLocale( ) );
        model.put( MARK_ACTIVATE_WORKFLOW, ACTIVATEWORKFLOW );
        if ( ( form.getIdWorkflow( ) > 0 ) && WorkflowService.getInstance( ).isAvailable( ) )
        {
            int nIdWorkflow = form.getIdWorkflow( );
            StateFilter stateFilter = new StateFilter( );
            stateFilter.setIdWorkflow( nIdWorkflow );
            for ( AppointmentDTO appointment : paginator.getPageItems( ) )
            {
                State stateAppointment = _stateService.findByResource( appointment.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE, nIdWorkflow );
                if ( stateAppointment != null )
                {
                    appointment.setState( stateAppointment );
                }
                appointment.setListWorkflowActions( WorkflowService.getInstance( ).getActions( appointment.getIdAppointment( ),
                        Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow( ), getUser( ) ) );
            }
        }
        AdminUser user = getUser( );
        model.put( MARK_APPOINTMENT_LIST, paginator.getPageItems( ) );
        model.put( MARK_FILTER, filter );
        model.put( MARK_LIST_STATUS, getListStatus( ) );
        model.put( MARK_RIGHT_CREATE,
                RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT, user ) );
        model.put( MARK_RIGHT_MODIFY,
                RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_MODIFY_APPOINTMENT, user ) );
        model.put( MARK_RIGHT_DELETE,
                RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_DELETE_APPOINTMENT, user ) );
        model.put( MARK_RIGHT_VIEW,
                RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, user ) );
        model.put( MARK_RIGHT_CHANGE_STATUS, RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm,
                AppointmentResourceIdService.PERMISSION_CHANGE_APPOINTMENT_STATUS, user ) );
        model.put( MARK_RIGHT_CHANGE_DATE, RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm,
                AppointmentResourceIdService.PERMISSION_CHANGE_APPOINTMENT_DATE, user ) );
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS, TEMPLATE_MANAGE_APPOINTMENTS, model );
    }

    /**
     * Manages the removal form of a appointment whose identifier is in the HTTP request
     * 
     * @param request
     *            The HTTP request
     * @return the HTML code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_APPOINTMENT )
    public String getConfirmRemoveAppointment( HttpServletRequest request )
    {
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_APPOINTMENT ) );
        url.addParameter( PARAMETER_ID_APPOINTMENT, request.getParameter( PARAMETER_ID_APPOINTMENT ) );
        url.addParameter( PARAMETER_ID_FORM, request.getParameter( PARAMETER_ID_FORM ) );
        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_APPOINTMENT, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a appointment
     * 
     * @param request
     *            The HTTP request
     * @return the JSP URL to display the form to manage appointments
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    @SuppressWarnings( {
            "unchecked"
    } )
    @Action( ACTION_REMOVE_APPOINTMENT )
    public String doRemoveAppointment( HttpServletRequest request ) throws AccessDeniedException
    {
        int nIdAppointment = Integer.parseInt( request.getParameter( PARAMETER_ID_APPOINTMENT ) );
        Integer idForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, Integer.toString( idForm ),
                AppointmentResourceIdService.PERMISSION_DELETE_APPOINTMENT, getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_DELETE_APPOINTMENT );
        }
        AppointmentService.deleteAppointment( nIdAppointment );
        AppLogService.info( LogUtilities.buildLog( ACTION_REMOVE_APPOINTMENT, Integer.toString( nIdAppointment ), getUser( ) ) );
        addInfo( INFO_APPOINTMENT_REMOVED, getLocale( ) );
        // Need to update the list of the appointments in session
        List<AppointmentDTO> listAppointmentsDTO = (List<AppointmentDTO>) request.getSession( ).getAttribute( SESSION_LIST_APPOINTMENTS );
        if ( listAppointmentsDTO != null )
        {
            listAppointmentsDTO = listAppointmentsDTO.stream( ).filter( a -> a.getIdAppointment( ) != nIdAppointment ).collect( Collectors.toList( ) );
            request.getSession( ).setAttribute( SESSION_LIST_APPOINTMENTS, listAppointmentsDTO );
        }
        return redirect( request, VIEW_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, idForm );
    }

    /**
     * Manages the removal form of a appointment whose identifier is in the HTTP request
     * 
     * @param request
     *            The HTTP request
     * @return the HTML code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_MASS_APPOINTMENT )
    public String getConfirmRemoveMassAppointment( HttpServletRequest request, int nIdForm )
    {
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_MASSAPPOINTMENT ) );
        url.addParameter( PARAMETER_ID_FORM, nIdForm );
        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_MASSAPPOINTMENT, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );
        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a appointment
     * 
     * @param request
     *            The HTTP request
     * @return the JSP URL to display the form to manage appointments
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    @SuppressWarnings( "unchecked" )
    @Action( ACTION_REMOVE_MASSAPPOINTMENT )
    public String doRemoveMassAppointment( HttpServletRequest request ) throws AccessDeniedException
    {
        String [ ] tabIdAppointmentToDelete = (String [ ]) request.getSession( ).getAttribute( PARAMETER_ID_APPOINTMENT_DELETE );
        request.getSession( ).removeAttribute( PARAMETER_ID_APPOINTMENT_DELETE );
        Integer idForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, Integer.toString( idForm ),
                AppointmentResourceIdService.PERMISSION_DELETE_APPOINTMENT, getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_DELETE_APPOINTMENT );
        }
        ArrayList<String> listStringIdAppointment = new ArrayList<>( );
        if ( tabIdAppointmentToDelete != null )
        {
            for ( String strIdAppointment : tabIdAppointmentToDelete )
            {
                AppointmentService.deleteAppointment( Integer.valueOf( strIdAppointment ) );
                AppLogService.info( LogUtilities.buildLog( ACTION_REMOVE_APPOINTMENT, strIdAppointment, getUser( ) ) );
            }
            addInfo( INFO_APPOINTMENT_MASSREMOVED, getLocale( ) );
            listStringIdAppointment.addAll( Arrays.asList( tabIdAppointmentToDelete ) );
        }
        // Need to update the list of the appointments in session
        List<AppointmentDTO> listAppointmentsDTO = (List<AppointmentDTO>) request.getSession( ).getAttribute( SESSION_LIST_APPOINTMENTS );
        if ( listAppointmentsDTO != null )
        {
            listAppointmentsDTO = listAppointmentsDTO.stream( ).filter( a -> !listStringIdAppointment.contains( Integer.toString( a.getIdAppointment( ) ) ) )
                    .collect( Collectors.toList( ) );
            request.getSession( ).setAttribute( SESSION_LIST_APPOINTMENTS, listAppointmentsDTO );
        }
        return redirect( request, VIEW_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, idForm );
    }

    /**
     * View details of an appointment
     * 
     * @param request
     *            The request
     * @return The HTML content to display
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    @View( VIEW_VIEW_APPOINTMENT )
    public String getViewAppointment( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = Integer.parseInt( strIdForm );
        Form form = FormService.findFormLightByPrimaryKey( nIdForm );
        int nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                getIntSessionAttribute( request.getSession( ), SESSION_ITEMS_PER_PAGE ), _nDefaultItemsPerPage );
        int nIdAppointment = Integer.parseInt( strIdAppointment );
        AppointmentDTO appointmentDTO = AppointmentService.buildAppointmentDTOFromIdAppointment( nIdAppointment );
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT );
        }
        Map<String, Object> model = getModel( );
        model.put( MARK_APPOINTMENT, appointmentDTO );
        model.put( MARK_FORM_MESSAGES, FormMessageService.findFormMessageByIdForm( nIdForm ) );
        model.put( MARK_FORM, form );
        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( nItemsPerPage ) );
        if ( ( form.getIdWorkflow( ) > 0 ) && WorkflowService.getInstance( ).isAvailable( ) )
        {
            model.put( MARK_RESOURCE_HISTORY, WorkflowService.getInstance( ).getDisplayDocumentHistory( nIdAppointment, Appointment.APPOINTMENT_RESOURCE_TYPE,
                    form.getIdWorkflow( ), request, getLocale( ) ) );
        }
        if ( ( form.getIdWorkflow( ) > 0 ) && WorkflowService.getInstance( ).isAvailable( ) )
        {
            int nIdWorkflow = form.getIdWorkflow( );
            StateFilter stateFilter = new StateFilter( );
            stateFilter.setIdWorkflow( nIdWorkflow );
            State stateAppointment = _stateService.findByResource( appointmentDTO.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE, nIdWorkflow );
            if ( stateAppointment != null )
            {
                appointmentDTO.setState( stateAppointment );
            }
            appointmentDTO.setListWorkflowActions( WorkflowService.getInstance( ).getActions( appointmentDTO.getIdAppointment( ),
                    Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow( ), getUser( ) ) );
        }
        Locale locale = getLocale( );
        List<Response> listResponse = AppointmentResponseService.findListResponse( nIdAppointment );
        for ( Response response : listResponse )
        {
            if ( response.getFile( ) != null )
            {
                response.setFile( FileHome.findByPrimaryKey( response.getFile( ).getIdFile( ) ) );
            }
            if ( response.getEntry( ) != null )
            {
                response.setEntry( EntryHome.findByPrimaryKey( response.getEntry( ).getIdEntry( ) ) );
            }
        }
        appointmentDTO.setListResponse( listResponse );
        model.put( MARK_LIST_RESPONSE_RECAP_DTO, AppointmentUtilities.buildListResponse( appointmentDTO, request, locale ) );
        model.put( MARK_ADDON, AppointmentAddOnManager.getAppointmentAddOn( appointmentDTO.getIdAppointment( ), getLocale( ) ) );
        AdminUser user = getUser( );
        model.put( MARK_RIGHT_CREATE,
                RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT, user ) );
        model.put( MARK_RIGHT_MODIFY,
                RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_MODIFY_APPOINTMENT, user ) );
        model.put( MARK_RIGHT_DELETE,
                RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_DELETE_APPOINTMENT, user ) );
        model.put( MARK_RIGHT_VIEW,
                RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, user ) );
        model.put( MARK_RIGHT_CHANGE_STATUS, RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm,
                AppointmentResourceIdService.PERMISSION_CHANGE_APPOINTMENT_STATUS, user ) );
        model.put( MARK_RIGHT_CHANGE_DATE, RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm,
                AppointmentResourceIdService.PERMISSION_CHANGE_APPOINTMENT_DATE, user ) );
        model.put( MARK_LANGUAGE, getLocale( ) );
        model.put( MARK_ACTIVATE_WORKFLOW, ACTIVATEWORKFLOW );
        return getPage( PROPERTY_PAGE_TITLE_VIEW_APPOINTMENT, TEMPLATE_VIEW_APPOINTMENT, model );
    }

    /**
     * Do download a file from an appointment response
     * 
     * @param request
     *            The request
     * @param response
     *            The response
     * @return nothing.
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    @SuppressWarnings( "unchecked" )
    public String getDownloadFileAppointment( HttpServletRequest request, HttpServletResponse response ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( StringUtils.isEmpty( strIdForm ) || !StringUtils.isNumeric( strIdForm ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT );
        }
        Locale locale = getLocale( );
        List<AppointmentDTO> listAppointmentsDTO = (List<AppointmentDTO>) request.getSession( ).getAttribute( SESSION_LIST_APPOINTMENTS );
        if ( listAppointmentsDTO.size( ) > AppPropertiesService.getPropertyInt( PROPERTY_NB_MAX_APPOINTMENTS_TO_EXPORT, MAX_NB_APPOINTMENTS_TO_EXPORT ) )
        {
            addError( ERROR_MESSAGE_NB_MAX_APPOINTMENTS_FOR_EXPORT, locale );
            UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_APPOINTMENTS );
            urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_APPOINTMENTS );
            urlItem.addParameter( PARAMETER_ID_FORM, strIdForm );
            return redirect( request, urlItem.getUrl( ) );
        }
        else
        {
            AppointmentUtilities.buildExcelFileWithAppointments( strIdForm, response, locale, listAppointmentsDTO, _stateService );
        }
        return null;
    }

    /**
     * Get the page to modify an appointment
     * 
     * @param request
     *            The request
     * @return The HTML content to display or the next URL to redirect to
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    @View( VIEW_MODIFY_APPOINTMENT )
    public synchronized String getModifyAppointment( HttpServletRequest request ) throws AccessDeniedException
    {
        HttpSession session = request.getSession( );
        clearUploadFilesIfNeeded( session );
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdAppointment, AppointmentResourceIdService.PERMISSION_MODIFY_APPOINTMENT,
                getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_APPOINTMENT );
        }
        int nIdAppointment = Integer.parseInt( strIdAppointment );
        AppointmentDTO appointmentDTO = AppointmentService.buildAppointmentDTOFromIdAppointment( nIdAppointment );
        appointmentDTO.setListResponse( AppointmentResponseService.findAndBuildListResponse( nIdAppointment, request ) );
        appointmentDTO.setMapResponsesByIdEntry( AppointmentResponseService.buildMapFromListResponse( appointmentDTO.getListResponse( ) ) );
        session.removeAttribute( SESSION_NOT_VALIDATED_APPOINTMENT );
        List<Slot> listSlot = appointmentDTO.getSlot( );
        Slot firstSlot = listSlot.get( 0 );
        int nIdForm = firstSlot.getIdForm( );
        LocalDate dateOfSlot = firstSlot.getDate( );
        ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( nIdForm, dateOfSlot );
        WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( nIdForm, dateOfSlot );
        AppointmentFormDTO form = FormService.buildAppointmentForm( nIdForm, reservationRule.getIdReservationRule( ), weekDefinition.getIdWeekDefinition( ) );
        request.getSession( ).setAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM, form );

        int nbAlreadyBookedSeats = appointmentDTO.getNbBookedSeats( );
        int nbMaxPeoplePerAppointment = form.getMaxPeoplePerAppointment( );
        int nbToTake = nbAlreadyBookedSeats;
        if ( ( nbAlreadyBookedSeats < nbMaxPeoplePerAppointment ) )
        {
            for ( Slot slt : listSlot )
            {

                if ( slt.getNbPotentialRemainingPlaces( ) > 0 && nbToTake < nbMaxPeoplePerAppointment )
                {

                    int nbPotentialPlacesToTake = form.getMaxPeoplePerAppointment( ) - nbAlreadyBookedSeats;

                    nbToTake = nbToTake + nbPotentialPlacesToTake;

                    // int nbPotentialRemainingPlaces = slot.getNbPotentialRemainingPlaces( );
                    appointmentDTO.setNbMaxPotentialBookedSeats( nbAlreadyBookedSeats + nbPotentialPlacesToTake );
                    // slot.setNbPotentialRemainingPlaces( nbPotentialRemainingPlaces - nbPotentialPlacesToTake );
                    SlotSafeService.decrementPotentialRemainingPlaces( nbPotentialPlacesToTake, slt.getIdSlot( ) );
                    // SlotService.updateSlot( slot );

                    TimerForLockOnSlot timer = new TimerForLockOnSlot( );
                    SlotEditTask slotEditTask = new SlotEditTask( timer );
                    slotEditTask.setNbPlacesTaken( nbPotentialPlacesToTake );
                    slotEditTask.setIdSlot( slt.getIdSlot( ) );
                    long delay = TimeUnit.MINUTES
                            .toMillis( AppPropertiesService.getPropertyInt( AppointmentUtilities.PROPERTY_DEFAULT_EXPIRED_TIME_EDIT_APPOINTMENT, 1 ) );
                    timer.schedule( slotEditTask, delay );
                    request.getSession( ).setAttribute( AppointmentUtilities.SESSION_TIMER_SLOT + slotEditTask.getIdSlot( ), timer );

                }
            }
        }
        else
        {
            appointmentDTO.setNbMaxPotentialBookedSeats( nbAlreadyBookedSeats );
        }
        session.setAttribute( SESSION_NOT_VALIDATED_APPOINTMENT, appointmentDTO );
        return getViewCreateAppointment( request );
    }

    /**
     * Returns the form to create an appointment
     * 
     * @param request
     *            The HTTP request
     * @return the HTML code of the appointment form
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    @SuppressWarnings( "unchecked" )
    @View( VIEW_CREATE_APPOINTMENT )
    public synchronized String getViewCreateAppointment( HttpServletRequest request ) throws AccessDeniedException
    {
        clearUploadFilesIfNeeded( request.getSession( ) );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = Integer.parseInt( strIdForm );
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT, getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT );
        }
        FormRule formRule = FormRuleService.findFormRuleWithFormId( nIdForm );
        AppointmentFormDTO form = (AppointmentFormDTO) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        // Get the not validated appointment in session if it exists
        AppointmentDTO appointmentDTO = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_NOT_VALIDATED_APPOINTMENT );
        Locale locale = getLocale( );

        String isModification = request.getParameter( PARAMETER_IS_MODIFICATION );
        boolean bModificationForm = false;
        List<Slot> listSlot = null;
        if ( isModification != null )

        {
            bModificationForm = true;

        }
        else
        {

            LocalDateTime startingDateTime = LocalDateTime.parse( request.getParameter( PARAMETER_STARTING_DATE_TIME ) );
            LocalDateTime endingDateTime = LocalDateTime.parse( request.getParameter( PARAMETER_ENDING_DATE_TIME ) );

            // Get all the week definitions
            HashMap<LocalDate, WeekDefinition> mapWeekDefinition = WeekDefinitionService.findAllWeekDefinition( nIdForm );
            listSlot = SlotService.buildListSlot( nIdForm, mapWeekDefinition, startingDateTime.toLocalDate( ), endingDateTime.toLocalDate( ) );

            if ( !formRule.getBoOverbooking( ) )
            {

                listSlot = listSlot.stream( )
                        .filter( s -> ( ( startingDateTime.compareTo( s.getStartingDateTime( ) ) <= 0 )
                                && ( endingDateTime.compareTo( s.getEndingDateTime( ) ) >= 0 ) && ( s.getNbRemainingPlaces( ) > 0 ) && ( s.getIsOpen( ) ) ) )
                        .collect( Collectors.toList( ) );

            }
            else
            {

                listSlot = listSlot.stream( ).filter( s -> ( ( startingDateTime.compareTo( s.getStartingDateTime( ) ) <= 0 )
                        && ( endingDateTime.compareTo( s.getEndingDateTime( ) ) >= 0 ) && ( s.getIsOpen( ) ) ) ).collect( Collectors.toList( ) );

            }

        }

        AppointmentDTO oldAppointmentDTO = null;
        // Get the not validated appointment in session if it exists
        // AppointmentDTO appointmentDTO = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_NOT_VALIDATED_APPOINTMENT );

        if ( appointmentDTO == null )
        {
            // Try to get the validated appointment in session
            // (in case the user click on back button in the recap view (or
            // modification)
            appointmentDTO = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_VALIDATED_APPOINTMENT );
            if ( appointmentDTO != null )
            {
                request.getSession( ).removeAttribute( SESSION_VALIDATED_APPOINTMENT );
                request.getSession( ).setAttribute( SESSION_NOT_VALIDATED_APPOINTMENT, appointmentDTO );
                if ( !bModificationForm && listSlot != null && isEquals( appointmentDTO.getSlot( ), listSlot ) )

                {
                    oldAppointmentDTO = appointmentDTO;
                }
            }
        }
        else
        {
            // Appointment DTO not validated in session
            // Need to verify if the slot has not changed
            if ( !bModificationForm && listSlot != null && isEquals( appointmentDTO.getSlot( ), listSlot ) )

            {
                oldAppointmentDTO = appointmentDTO;
            }
            else
                if ( nNbPlacesToTake == 0 && bModificationForm )
                {

                    nNbPlacesToTake = appointmentDTO.getNbBookedSeats( );

                }
        }

        if ( appointmentDTO == null || oldAppointmentDTO != null )
        {
            // Need to get back the informations the user has entered
            appointmentDTO = new AppointmentDTO( );
            if ( oldAppointmentDTO != null )
            {
                appointmentDTO.setFirstName( oldAppointmentDTO.getFirstName( ) );
                appointmentDTO.setLastName( oldAppointmentDTO.getLastName( ) );
                appointmentDTO.setEmail( oldAppointmentDTO.getEmail( ) );
                appointmentDTO.setPhoneNumber( oldAppointmentDTO.getPhoneNumber( ) );
                appointmentDTO.setNbBookedSeats( oldAppointmentDTO.getNbBookedSeats( ) );
                appointmentDTO.setListResponse( oldAppointmentDTO.getListResponse( ) );
                appointmentDTO.setMapResponsesByIdEntry( oldAppointmentDTO.getMapResponsesByIdEntry( ) );
            }
        }

        if ( !bModificationForm )
        {

            Boolean bool = true;

            // Slot slot = null;
            // If nIdSlot == 0, the slot has not been created yet
            appointmentDTO.setSlot( null );
            appointmentDTO.setNbMaxPotentialBookedSeats( 0 );
            for ( Slot slot : listSlot )
            {

                if ( slot.getIdSlot( ) == 0 )
                {

                    slot = SlotSafeService.createSlot( slot );

                }
                else
                {

                    slot = SlotService.findSlotById( slot.getIdSlot( ) );
                }

                // Need to check competitive access
                // May be the slot is already taken at the same time
                if ( slot.getNbPotentialRemainingPlaces( ) == 0 && !formRule.getBoOverbooking( ) )
                {
                    addError( ERROR_MESSAGE_SLOT_FULL, locale );
                    return redirect( request, VIEW_CALENDAR_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, nIdForm );
                }

                appointmentDTO.addSlot( slot );

                if ( bool )
                {

                    LocalDateTime startingDateTime = LocalDateTime.parse( request.getParameter( PARAMETER_STARTING_DATE_TIME ) );
                    LocalDateTime endingDateTime = LocalDateTime.parse( request.getParameter( PARAMETER_ENDING_DATE_TIME ) );

                    appointmentDTO.setDateOfTheAppointment( slot.getDate( ).format( Utilities.getFormatter( ) ) );
                    appointmentDTO.setIdForm( nIdForm );
                    appointmentDTO.setEndingDateTime( endingDateTime );
                    appointmentDTO.setStartingDateTime( startingDateTime );
                    LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
                    if ( user != null )
                    {
                        Map<String, String> map = user.getUserInfos( );
                        appointmentDTO.setEmail( map.get( PROPERTY_USER_EMAIL ) );
                        appointmentDTO.setFirstName( map.get( PROPERTY_USER_FIRST_NAME ) );
                        appointmentDTO.setLastName( map.get( PROPERTY_USER_LAST_NAME ) );
                    }

                    ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( nIdForm, slot.getDate( ) );
                    WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( nIdForm, slot.getDate( ) );
                    form = FormService.buildAppointmentForm( nIdForm, reservationRule.getIdReservationRule( ), weekDefinition.getIdWeekDefinition( ) );
                    bool = false;
                }
                AppointmentUtilities.putTimerInSession( request, slot.getIdSlot( ), appointmentDTO, form.getMaxPeoplePerAppointment( ) );
            }

            if ( appointmentDTO.getNbMaxPotentialBookedSeats( ) == 0 && !formRule.getBoOverbooking( ) )

            {
                addError( ERROR_MESSAGE_SLOT_FULL, locale );
                return redirect( request, VIEW_CALENDAR_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, nIdForm );
            }
            request.getSession( ).setAttribute( SESSION_NOT_VALIDATED_APPOINTMENT, appointmentDTO );
            request.getSession( ).setAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM, form );
        }

        Map<String, Object> model = getModel( );
        List<Entry> listEntryFirstLevel = EntryService.getFilter( form.getIdForm( ), true );
        StringBuilder strBuffer = new StringBuilder( );
        for ( Entry entry : listEntryFirstLevel )
        {
            EntryService.getHtmlEntry( model, entry.getIdEntry( ), strBuffer, locale, false, request );
        }
        model.put( MARK_STR_ENTRY, strBuffer.toString( ) );
        model.put( MARK_FORM, form );
        model.put( MARK_APPOINTMENT, appointmentDTO );
        model.put( PARAMETER_DATE_OF_DISPLAY, appointmentDTO.getSlot( ).get( 0 ).getDate( ) );
        model.put( MARK_PLACES, ( formRule.getBoOverbooking( ) ) ? 20 : appointmentDTO.getNbMaxPotentialBookedSeats( ) );
        FormMessage formMessages = FormMessageService.findFormMessageByIdForm( nIdForm );
        model.put( MARK_FORM_MESSAGES, formMessages );
        model.put( MARK_LOCALE, locale );
        List<GenericAttributeError> listErrors = (List<GenericAttributeError>) request.getSession( ).getAttribute( SESSION_APPOINTMENT_FORM_ERRORS );
        model.put( MARK_FORM_ERRORS, listErrors );
        model.put( MARK_LIST_ERRORS, AppointmentDTO.getAllErrors( locale ) );
        HtmlTemplate templateForm = AppTemplateService.getTemplate( TEMPLATE_HTML_CODE_FORM_ADMIN, getLocale( ), model );
        model.put( MARK_FORM_HTML, templateForm.getHtml( ) );
        if ( listErrors != null )
        {
            model.put( MARK_FORM_ERRORS, listErrors );
            request.getSession( ).removeAttribute( SESSION_APPOINTMENT_FORM_ERRORS );
        }
        return getPage( PROPERTY_PAGE_TITLE_CREATE_APPOINTMENT, TEMPLATE_CREATE_APPOINTMENT, model );
    }

    /**
     * Do validate data entered by a user to fill a form
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     * @throws SiteMessageExcamoreption
     */
    @Action( ACTION_DO_VALIDATE_FORM )
    public String doValidateForm( HttpServletRequest request ) throws AccessDeniedException, SiteMessageException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = Integer.parseInt( strIdForm );
        AppointmentFormDTO form = (AppointmentFormDTO) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        AppointmentDTO appointmentDTO = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_NOT_VALIDATED_APPOINTMENT );
        String strEmail = request.getParameter( PARAMETER_EMAIL );
        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
        Locale locale = getLocale( );
        AppointmentUtilities.checkDateOfTheAppointmentIsNotBeforeNow( appointmentDTO, locale, listFormErrors );
        AppointmentUtilities.checkEmail( strEmail, request.getParameter( PARAMETER_EMAIL_CONFIRMATION ), form, locale, listFormErrors );

        if ( form.getBoOverbooking( ) )
        {

            appointmentDTO.setOverbookingAllowed( true );
        }

        int nbBookedSeats = nNbPlacesToTake;
        if ( nNbPlacesToTake == 0 )
        {

            nbBookedSeats = AppointmentUtilities.checkAndReturnNbBookedSeats( request.getParameter( PARAMETER_NUMBER_OF_BOOKED_SEATS ), form, appointmentDTO,
                    locale, listFormErrors );

        }

        AppointmentUtilities.fillAppointmentDTO( appointmentDTO, nbBookedSeats, strEmail, request.getParameter( PARAMETER_FIRST_NAME ),
                request.getParameter( PARAMETER_LAST_NAME ) );
        AppointmentUtilities.validateFormAndEntries( appointmentDTO, request, listFormErrors );
        AppointmentUtilities.fillInListResponseWithMapResponse( appointmentDTO );
        if ( CollectionUtils.isNotEmpty( listFormErrors ) )
        {
            LinkedHashMap<String, String> additionalParameters = new LinkedHashMap<>( );
            additionalParameters.put( PARAMETER_ID_FORM, strIdForm );
            additionalParameters.put( PARAMETER_STARTING_DATE_TIME, appointmentDTO.getStartingDateTime( ).toString( ) );
            additionalParameters.put( PARAMETER_ENDING_DATE_TIME, appointmentDTO.getEndingDateTime( ).toString( ) );
            request.getSession( ).setAttribute( SESSION_APPOINTMENT_FORM_ERRORS, listFormErrors );
            return redirect( request, VIEW_CREATE_APPOINTMENT, additionalParameters );
        }
        request.getSession( ).removeAttribute( SESSION_NOT_VALIDATED_APPOINTMENT );
        request.getSession( ).setAttribute( SESSION_VALIDATED_APPOINTMENT, appointmentDTO );
        return redirect( request, VIEW_DISPLAY_RECAP_APPOINTMENT, PARAMETER_ID_FORM, nIdForm );
    }

    /**
     * Return to the display recap view with the new date selected on the calendar
     * 
     * @param request
     *            the request
     * @return to the display recap view
     * @throws AccessDeniedException
     * @throws SiteMessageException
     */
    @View( VIEW_CHANGE_DATE_APPOINTMENT )
    public String getViewChangeDateAppointment( HttpServletRequest request ) throws AccessDeniedException, SiteMessageException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        Locale locale = getLocale( );

        int nIdForm = Integer.parseInt( strIdForm );
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_CHANGE_APPOINTMENT_DATE,
                getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_CHANGE_APPOINTMENT_DATE );
        }
        AppointmentDTO appointmentDTO = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_VALIDATED_APPOINTMENT );

        AppointmentFormDTO form = null;
        LocalDateTime startingDateTime = LocalDateTime.parse( request.getParameter( PARAMETER_STARTING_DATE_TIME ) );
        LocalDateTime endingDateTime = LocalDateTime.parse( request.getParameter( PARAMETER_ENDING_DATE_TIME ) );
        // Get all the week definitions
        HashMap<LocalDate, WeekDefinition> mapWeekDefinition = WeekDefinitionService.findAllWeekDefinition( nIdForm );
        List<Slot> listSlot = SlotService.buildListSlot( nIdForm, mapWeekDefinition, startingDateTime.toLocalDate( ), endingDateTime.toLocalDate( ) );
        listSlot = listSlot
                .stream( ).filter( s -> ( ( startingDateTime.compareTo( s.getStartingDateTime( ) ) <= 0 )
                        && ( endingDateTime.compareTo( s.getEndingDateTime( ) ) >= 0 ) && ( s.getNbRemainingPlaces( ) > 0 ) && ( s.getIsOpen( ) ) ) )
                .collect( Collectors.toList( ) );

        Boolean bool = true;

        // Slot slot = null;
        // If nIdSlot == 0, the slot has not been created yet
        appointmentDTO.setSlot( null );
        appointmentDTO.setNbMaxPotentialBookedSeats( 0 );
        for ( Slot slot : listSlot )
        {

            if ( slot.getIdSlot( ) == 0 )
            {

                slot = SlotSafeService.createSlot( slot );

            }
            else
            {

                slot = SlotService.findSlotById( slot.getIdSlot( ) );
            }

            // Need to check competitive access
            // May be the slot is already taken at the same time
            if ( slot.getNbPotentialRemainingPlaces( ) == 0 && !form.getBoOverbooking( ) )
            {
                addError( ERROR_MESSAGE_SLOT_FULL, locale );
                return redirect( request, VIEW_CALENDAR_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, nIdForm );
            }

            appointmentDTO.addSlot( slot );

            if ( bool )
            {

                appointmentDTO.setDateOfTheAppointment( slot.getDate( ).format( Utilities.getFormatter( ) ) );
                appointmentDTO.setIdForm( nIdForm );
                LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
                if ( user != null )
                {
                    Map<String, String> map = user.getUserInfos( );
                    appointmentDTO.setEmail( map.get( PROPERTY_USER_EMAIL ) );
                    appointmentDTO.setFirstName( map.get( PROPERTY_USER_FIRST_NAME ) );
                    appointmentDTO.setLastName( map.get( PROPERTY_USER_LAST_NAME ) );
                }

                ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( nIdForm, slot.getDate( ) );
                WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( nIdForm, slot.getDate( ) );
                form = FormService.buildAppointmentForm( nIdForm, reservationRule.getIdReservationRule( ), weekDefinition.getIdWeekDefinition( ) );
                bool = false;
            }
            AppointmentUtilities.putTimerInSession( request, slot.getIdSlot( ), appointmentDTO, form.getMaxPeoplePerAppointment( ) );
        }

        if ( appointmentDTO.getNbMaxPotentialBookedSeats( ) == 0 && !form.getBoOverbooking( ) )
        {
            addError( ERROR_MESSAGE_SLOT_FULL, locale );
            return redirect( request, VIEW_CALENDAR_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, nIdForm );
        }

        /*
         * 
         * 
         * int nIdSlot = Integer.parseInt( request.getParameter( PARAMETER_ID_SLOT ) );
         * 
         * Slot slot = null; // If nIdSlot == 0, the slot has not been created yet if ( nIdSlot == 0 ) { // Need to get all the informations to create the slot
         * LocalDateTime startingDateTime = LocalDateTime.parse( request.getParameter( PARAMETER_STARTING_DATE_TIME ) ); LocalDateTime endingDateTime =
         * LocalDateTime.parse( request.getParameter( PARAMETER_ENDING_DATE_TIME ) );
         * 
         * boolean bIsOpen = Boolean.parseBoolean( request.getParameter( PARAMETER_IS_OPEN ) ); boolean bIsSpecific = Boolean.parseBoolean(
         * request.getParameter( PARAMETER_IS_SPECIFIC ) ); int nMaxCapacity = Integer.parseInt( request.getParameter( PARAMETER_MAX_CAPACITY ) ); slot =
         * SlotService.buildSlot( nIdForm, new Period( startingDateTime, endingDateTime ), nMaxCapacity, nMaxCapacity, nMaxCapacity, 0, bIsOpen, bIsSpecific );
         * slot = SlotService.saveSlot( slot );
         * 
         * } else { slot = SlotService.findSlotById( nIdSlot ); } appointmentDTO.setSlot( slot ); appointmentDTO.setDateOfTheAppointment(
         * slot.getStartingDateTime( ).toLocalDate( ).format( Utilities.getFormatter( ) ) );
         */

        request.getSession( ).setAttribute( SESSION_VALIDATED_APPOINTMENT, appointmentDTO );
        // LocalDate dateOfSlot = slot.getDate( );
        request.getSession( ).setAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM, form );
        Map<String, String> additionalParameters = new HashMap<>( );
        additionalParameters.put( PARAMETER_ID_FORM, Integer.toString( nIdForm ) );
        additionalParameters.put( PARAMETER_COME_FROM_CALENDAR, Boolean.TRUE.toString( ) );
        return redirect( request, VIEW_DISPLAY_RECAP_APPOINTMENT, additionalParameters );
    }

    /**
     * Display the recap before validating an appointment
     * 
     * @param request
     *            The request
     * @return The HTML content to display or the next URL to redirect to
     */
    @View( VIEW_DISPLAY_RECAP_APPOINTMENT )
    public String displayRecapAppointment( HttpServletRequest request )
    {
        AppointmentDTO appointmentDTO = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_VALIDATED_APPOINTMENT );
        AppointmentFormDTO form = (AppointmentFormDTO) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );

        Map<String, Object> model = new HashMap<String, Object>( );
        String strComeFromCalendar = request.getParameter( PARAMETER_COME_FROM_CALENDAR );
        if ( StringUtils.isNotEmpty( strComeFromCalendar ) )
        {
            model.put( PARAMETER_COME_FROM_CALENDAR, strComeFromCalendar );
            model.put( PARAMETER_DATE_OF_DISPLAY, appointmentDTO.getSlot( ).get( 0 ).getDate( ) );
        }
        model.put( MARK_FORM_MESSAGES, FormMessageService.findFormMessageByIdForm( appointmentDTO.getIdForm( ) ) );
        fillCommons( model );
        model.put( MARK_APPOINTMENT, appointmentDTO );
        Locale locale = getLocale( );
        model.put( MARK_ADDON, AppointmentAddOnManager.getAppointmentAddOn( appointmentDTO.getIdAppointment( ), getLocale( ) ) );
        model.put( MARK_LIST_RESPONSE_RECAP_DTO, AppointmentUtilities.buildListResponse( appointmentDTO, request, locale ) );
        model.put( MARK_FORM, form );
        return getPage( PROPERTY_PAGE_TITLE_RECAP_APPOINTMENT, TEMPLATE_APPOINTMENT_FORM_RECAP, model );
    }

    /**
     * Do save an appointment into the database if it is valid
     * 
     * @param request
     *            The request
     * @return The XPage to display
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    @Action( ACTION_DO_MAKE_APPOINTMENT )
    public String doMakeAppointment( HttpServletRequest request ) throws AccessDeniedException
    {
        AppointmentDTO appointmentDTO = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_VALIDATED_APPOINTMENT );
        boolean overbookingAllowed = false;

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_BACK ) ) )
        {
            return redirect( request, VIEW_CREATE_APPOINTMENT, PARAMETER_ID_FORM, appointmentDTO.getIdForm( ) );
        }
        AppointmentFormDTO form = (AppointmentFormDTO) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        if ( form.getBoOverbooking( ) )
        {
            overbookingAllowed = true;
        }
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, Integer.toString( form.getIdForm( ) ),
                AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT, getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT );
        }

        List<Slot> listSlot = new ArrayList<>( );
        int nbRemainingPlaces = 0;
        for ( Slot slt : appointmentDTO.getSlot( ) )
        {

            Slot slot = null;
            // Reload the slot from the database
            // The slot could have been taken since the beginning of the entry of
            // the form
            if ( slt.getIdSlot( ) != 0 )
            {
                slot = SlotService.findSlotById( slt.getIdSlot( ) );
            }

            else
            {
                HashMap<LocalDateTime, Slot> mapSlot = SlotService.buildMapSlotsByIdFormAndDateRangeWithDateForKey( appointmentDTO.getIdForm( ),
                        slt.getStartingDateTime( ), slt.getEndingDateTime( ) );
                if ( !mapSlot.isEmpty( ) )
                {
                    slot = mapSlot.get( slt.getStartingDateTime( ) );
                }
                else
                {
                    slot = slt;
                }
            }
            nbRemainingPlaces = nbRemainingPlaces + slot.getNbRemainingPlaces( );
            listSlot.add( slot );

        }

        // If it's a modification need to get the old number of booked seats
        // if the reservation is on the same slot, if not, the check has been
        // already done before
        if ( appointmentDTO.getIdAppointment( ) != 0 )
        {
            // If it's a modification of the date of the appointment
            if ( isEquals( appointmentDTO.getSlot( ), listSlot ) /* appointmentDTO.getSlot( ).getIdSlot( ) != appointmentDTO.getIdSlot( ) */ )
            {
                List<String> listMessages = AppointmentListenerManager.notifyListenersAppointmentDateChanged( appointmentDTO.getIdAppointment( ),
                        appointmentDTO.getListAppointmentSlot( ).stream( ).map( apptSlt -> apptSlt.getIdSlot( ) ).collect( Collectors.toList( ) ),
                        getLocale( ) );
                for ( String strMessage : listMessages )
                {
                    addInfo( strMessage );
                }
            }
            Appointment oldAppointment = AppointmentService.findAppointmentById( appointmentDTO.getIdAppointment( ) );

            if ( isEqualSlot( oldAppointment.getListAppointmentSlot( ), appointmentDTO.getSlot( ) )/*
                                                                                                    * oldAppointment.getIdSlot( ) == appointmentDTO.getSlot(
                                                                                                    * ).getIdSlot( )
                                                                                                    */
                    && appointmentDTO.getNbBookedSeats( ) > ( nbRemainingPlaces + oldAppointment.getNbPlaces( ) ) && !overbookingAllowed )
            {
                addError( ERROR_MESSAGE_SLOT_FULL, getLocale( ) );
                return redirect( request, VIEW_CALENDAR_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, appointmentDTO.getIdForm( ) );
            }
        }
        else
            if ( appointmentDTO.getNbBookedSeats( ) > nbRemainingPlaces && !overbookingAllowed )
            {
                addInfo( ERROR_MESSAGE_SLOT_FULL, getLocale( ) );
                return redirect( request, VIEW_CALENDAR_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, appointmentDTO.getIdForm( ) );
            }

        /*
         * if ( appointmentDTO.getNbBookedSeats( ) > slot.getNbRemainingPlaces( ) && !overbookingAllowed) { addError( ERROR_MESSAGE_SLOT_FULL, getLocale( ) );
         * return redirect( request, VIEW_CALENDAR_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, appointmentDTO.getIdForm( ) ); }
         */
        int nIdAppointment;
        if ( appointmentDTO.getIdAppointment( ) == 0 )
        {
            // set the admin user who is creating the appointment
            AdminUser adminLuteceUser = AdminAuthenticationService.getInstance( ).getRegisteredUser( request );
            appointmentDTO.setAdminUserCreate( adminLuteceUser.getAccessCode( ) );
        }
        try
        {
            appointmentDTO.setOverbookingAllowed( overbookingAllowed );
            nIdAppointment = SlotSafeService.saveAppointment( appointmentDTO, request );

        }
        catch( SlotFullException e )
        {
            addError( ERROR_MESSAGE_SLOT_FULL, getLocale( ) );
            return redirect( request, VIEW_CALENDAR_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, appointmentDTO.getIdForm( ) );
        }
        catch( AppointmentSavedException e )
        {

            nIdAppointment = appointmentDTO.getIdAppointment( );
            AppLogService.error( "Error Save appointment: " + e.getMessage( ), e );
        }
        nNbPlacesToTake = 0;
        AppLogService.info( LogUtilities.buildLog( ACTION_DO_MAKE_APPOINTMENT, Integer.toString( nIdAppointment ), getUser( ) ) );
        request.getSession( ).removeAttribute( SESSION_VALIDATED_APPOINTMENT );
        addInfo( INFO_APPOINTMENT_CREATED, getLocale( ) );
        AppointmentAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ) );
        Map<String, String> additionalParameters = new HashMap<>( );
        additionalParameters.put( PARAMETER_ID_FORM, Integer.toString( form.getIdForm( ) ) );
        additionalParameters.put( PARAMETER_DATE_OF_DISPLAY, appointmentDTO.getSlot( ).get( 0 ).getDate( ).toString( ) );
        return redirect( request, VIEW_CALENDAR_MANAGE_APPOINTMENTS, additionalParameters );
    }

    /**
     * Do download a file from an appointment response stored in session and not yet on server fs
     * 
     * @param request
     *            The request
     * @param httpResponse
     *            The response
     * @return nothing.
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    public String getDownloadFileFromSession( HttpServletRequest request, HttpServletResponse httpResponse ) throws AccessDeniedException
    {
        String strIdResponse = request.getParameter( PARAMETER_ID_RESPONSE );
        File respfile = null;
        if ( StringUtils.isEmpty( strIdResponse ) || !StringUtils.isNumeric( strIdResponse ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        int nIdResponse = Integer.parseInt( strIdResponse );
        AppointmentDTO appointmentDTO = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_VALIDATED_APPOINTMENT );

        List<Response> lResponse = appointmentDTO.getListResponse( );

        for ( Response response : lResponse )
        {
            if ( response.getEntry( ).getIdEntry( ) == nIdResponse && response.getFile( ) != null )
            {
                respfile = response.getFile( );
                break;
            }
        }

        if ( respfile == null )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        httpResponse.setHeader( "Content-Disposition", "attachment; filename=\"" + respfile.getTitle( ) + "\";" );
        httpResponse.setHeader( "Content-type", respfile.getMimeType( ) );
        httpResponse.addHeader( "Content-Encoding", "UTF-8" );
        httpResponse.addHeader( "Pragma", "public" );
        httpResponse.addHeader( "Expires", "0" );
        httpResponse.addHeader( "Cache-Control", "must-revalidate,post-check=0,pre-check=0" );

        try
        {
            OutputStream os = httpResponse.getOutputStream( );
            os.write( respfile.getPhysicalFile( ).getValue( ) );
            // We do not close the output stream in finally clause because it is
            // the response stream,
            // and an error message needs to be displayed if an exception occurs
            os.close( );
        }
        catch( IOException e )
        {
            AppLogService.error( e.getStackTrace( ), e );
        }

        return StringUtils.EMPTY;
    }

    /**
     * Do download a file from an appointment response
     * 
     * @param request
     *            The request
     * @param httpResponse
     *            The response
     * @return nothing.
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    public String getDownloadFile( HttpServletRequest request, HttpServletResponse httpResponse ) throws AccessDeniedException
    {
        String strIdResponse = request.getParameter( PARAMETER_ID_RESPONSE );

        if ( StringUtils.isEmpty( strIdResponse ) || !StringUtils.isNumeric( strIdResponse ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        int nIdResponse = Integer.parseInt( strIdResponse );
        Response response = ResponseHome.findByPrimaryKey( nIdResponse );
        File file = FileHome.findByPrimaryKey( response.getFile( ).getIdFile( ) );
        PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile( ).getIdPhysicalFile( ) );

        httpResponse.setHeader( "Content-Disposition", "attachment; filename=\"" + file.getTitle( ) + "\";" );
        httpResponse.setHeader( "Content-type", file.getMimeType( ) );
        httpResponse.addHeader( "Content-Encoding", "UTF-8" );
        httpResponse.addHeader( "Pragma", "public" );
        httpResponse.addHeader( "Expires", "0" );
        httpResponse.addHeader( "Cache-Control", "must-revalidate,post-check=0,pre-check=0" );

        try
        {
            OutputStream os = httpResponse.getOutputStream( );
            os.write( physicalFile.getValue( ) );
            // We do not close the output stream in finnaly clause because it is
            // the response stream,
            // and an error message needs to be displayed if an exception occurs
            os.close( );
        }
        catch( IOException e )
        {
            AppLogService.error( e.getStackTrace( ), e );
        }

        return StringUtils.EMPTY;
    }

    /**
     * Get an integer attribute from the session
     * 
     * @param session
     *            The session
     * @param strSessionKey
     *            The session key of the item
     * @return The value of the attribute, or 0 if the key is not associated with any value
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
     * Default constructor
     */
    public AppointmentJspBean( )
    {
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_APPOINTMENT_PER_PAGE, 10 );
    }

    private void cleanSession( HttpSession session )
    {
        session.removeAttribute( SESSION_APPOINTMENT_FILTER );
        session.removeAttribute( SESSION_LIST_APPOINTMENTS );
        session.removeAttribute( SESSION_CURRENT_PAGE_INDEX );
        session.removeAttribute( SESSION_CURRENT_PAGE_INDEX );
        session.removeAttribute( SESSION_NOT_VALIDATED_APPOINTMENT );
        session.removeAttribute( SESSION_VALIDATED_APPOINTMENT );
        session.removeAttribute( SESSION_APPOINTMENT_FORM_ERRORS );
        AppointmentAsynchronousUploadHandler.getHandler( ).removeSessionFiles( session );
    }

    /**
     * Clear uploaded files if needed.
     * 
     * @param session
     *            The session of the current user
     */
    private void clearUploadFilesIfNeeded( HttpSession session )
    {
        // If we do not reload an appointment, we clear uploaded files.
        if ( session.getAttribute( SESSION_NOT_VALIDATED_APPOINTMENT ) == null && session.getAttribute( SESSION_VALIDATED_APPOINTMENT ) == null )
        {
            AppointmentAsynchronousUploadHandler.getHandler( ).removeSessionFiles( session );
        }
    }

    /**
     * Order the list of the appointment in the result tab with the order by and order asc given
     * 
     * @param listAppointmentsDTO
     *            the llist of appointments
     * @param strOrderBy
     *            the order by
     * @param strOrderAsc
     *            the order asc
     */
    private List<AppointmentDTO> orderList( List<AppointmentDTO> listAppointmentsDTO, String strOrderBy, String strOrderAsc )
    {
        List<AppointmentDTO> sortedList = new ArrayList<>( );
        if ( CollectionUtils.isNotEmpty( listAppointmentsDTO ) )
        {
            sortedList.addAll( listAppointmentsDTO );
        }
        if ( strOrderBy == null )
        {
            strOrderBy = DATE_APPOINTMENT;
        }
        boolean bAsc = Boolean.FALSE;
        if ( strOrderAsc != null )
        {
            bAsc = Boolean.parseBoolean( strOrderAsc );
        }
        Stream<AppointmentDTO> stream = null;
        switch( strOrderBy )
        {
            case LAST_NAME:
                stream = sortedList.stream( ).sorted( ( a1, a2 ) -> a1.getLastName( ).compareTo( a2.getLastName( ) ) );
                break;
            case FIRST_NAME:
                stream = sortedList.stream( ).sorted( ( a1, a2 ) -> a1.getFirstName( ).compareTo( a2.getFirstName( ) ) );
                break;
            case EMAIL:
                stream = sortedList.stream( ).sorted( ( a1, a2 ) -> a1.getEmail( ).compareTo( a2.getEmail( ) ) );
                break;
            case NB_BOOKED_SEATS:
                stream = sortedList.stream( ).sorted( ( a1, a2 ) -> Integer.compare( a1.getNbBookedSeats( ), a2.getNbBookedSeats( ) ) );
                break;
            case DATE_APPOINTMENT:
                stream = sortedList.stream( ).sorted( ( a1, a2 ) -> a1.getStartingDateTime( ).compareTo( a2.getStartingDateTime( ) ) );
                break;
            case ADMIN:
                stream = sortedList.stream( ).sorted( ( a1, a2 ) -> a1.getAdminUser( ).compareTo( a2.getAdminUser( ) ) );
                break;
            case STATUS:
                stream = sortedList.stream( ).sorted( ( a1, a2 ) -> Boolean.compare( a1.getIsCancelled( ), a2.getIsCancelled( ) ) );
                break;
            default:
                stream = sortedList.stream( ).sorted( ( a1, a2 ) -> a1.getStartingDateTime( ).compareTo( a2.getStartingDateTime( ) ) );
        }
        sortedList = stream.collect( Collectors.toList( ) );
        if ( !bAsc )
        {
            Collections.reverse( sortedList );
        }
        return sortedList;
    }

    /**
     * Get the URL to display the form of a workflow action. If the action has no form, then the user is redirected to the page to execute the workflow action
     * 
     * @param request
     *            The request
     * @param strIdAppointment
     *            The id of the appointment
     * @param strIdAction
     *            The id of the workflow action
     * @return The URL
     */
    public static String getUrlExecuteWorkflowAction( HttpServletRequest request, String strIdAppointment, String strIdAction )
    {
        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_APPOINTMENTS );
        url.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_WORKFLOW_ACTION_FORM );
        url.addParameter( PARAMETER_ID_APPOINTMENT, strIdAppointment );
        url.addParameter( PARAMETER_ID_ACTION, strIdAction );

        return url.getUrl( );
    }

    /**
     * Get the workflow action form before processing the action. If the action does not need to display any form, then redirect the user to the workflow action
     * processing page.
     * 
     * @param request
     *            The request
     * @return The HTML content to display, or the next URL to redirect the user to
     */
    @View( VIEW_WORKFLOW_ACTION_FORM )
    public String getWorkflowActionForm( HttpServletRequest request )
    {
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );
        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) && StringUtils.isNotEmpty( strIdAppointment )
                && StringUtils.isNumeric( strIdAppointment ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdAppointment = Integer.parseInt( strIdAppointment );
            if ( WorkflowService.getInstance( ).isDisplayTasksForm( nIdAction, getLocale( ) ) )
            {
                String strHtmlTasksForm = WorkflowService.getInstance( ).getDisplayTasksForm( nIdAppointment, Appointment.APPOINTMENT_RESOURCE_TYPE, nIdAction,
                        request, getLocale( ) );
                Map<String, Object> model = new HashMap<String, Object>( );
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
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_PROCESS_WORKFLOW_ACTION )
    public String doProcessWorkflowAction( HttpServletRequest request )
    {
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );
        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) && StringUtils.isNotEmpty( strIdAppointment )
                && StringUtils.isNumeric( strIdAppointment ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdAppointment = Integer.parseInt( strIdAppointment );
            Appointment appointment = AppointmentService.findAppointmentById( nIdAppointment );

            List<AppointmentSlot> listApptSlot = appointment.getListAppointmentSlot( );
            Slot slot = SlotService.findSlotById( listApptSlot.get( 0 ).getIdSlot( ) );

            if ( request.getParameter( PARAMETER_BACK ) == null )
            {
                try
                {
                    if ( WorkflowService.getInstance( ).isDisplayTasksForm( nIdAction, getLocale( ) ) )
                    {
                        String strError = WorkflowService.getInstance( ).doSaveTasksForm( nIdAppointment, Appointment.APPOINTMENT_RESOURCE_TYPE, nIdAction,
                                slot.getIdForm( ), request, getLocale( ) );
                        if ( strError != null )
                        {
                            return redirect( request, strError );
                        }
                    }
                    else
                    {
                        List<ITask> listActionTasks = _taskService.getListTaskByIdAction( nIdAction, getLocale( ) );
                        for ( ITask task : listActionTasks )
                        {
                            if ( task.getTaskType( ).getKey( ).equals( "taskChangeAppointmentStatus" ) && ( appointment.getIsCancelled( ) ) )
                            {
                                for ( AppointmentSlot apptSlt : listApptSlot )
                                {

                                    Slot slt = SlotService.findSlotById( apptSlt.getIdSlot( ) );

                                    if ( apptSlt.getNbPlaces( ) > slt.getNbRemainingPlaces( ) )
                                    {

                                        return redirect( request,
                                                AdminMessageService.getMessageUrl( request, MESSAGE_UNVAILABLE_SLOT, AdminMessage.TYPE_STOP ) );

                                    }
                                }
                            }
                        }

                        WorkflowService.getInstance( ).doProcessAction( nIdAppointment, Appointment.APPOINTMENT_RESOURCE_TYPE, nIdAction, slot.getIdForm( ),
                                request, getLocale( ), false );
                        AppointmentListenerManager.notifyAppointmentWFActionTriggered( nIdAppointment, nIdAction );
                    }
                }
                catch( Exception e )
                {
                    AppLogService.error( "Error Workflow", e );
                }
                Map<String, String> mapParams = new HashMap<>( );
                mapParams.put( PARAMETER_ID_FORM, Integer.toString( slot.getIdForm( ) ) );
                request.getSession( ).removeAttribute( SESSION_LIST_APPOINTMENTS );
                return redirect( request, VIEW_MANAGE_APPOINTMENTS, mapParams );
            }
            return redirect( request, VIEW_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, slot.getIdForm( ) );
        }
        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Do change the status of an appointment
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    @Action( ACTION_DO_CHANGE_APPOINTMENT_STATUS )
    public String doChangeAppointmentStatus( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );
        String strStatusCancelled = request.getParameter( PARAMETER_STATUS_CANCELLED );
        if ( StringUtils.isNotEmpty( strIdAppointment ) && StringUtils.isNumeric( strIdAppointment ) && StringUtils.isNotEmpty( strStatusCancelled ) )
        {
            int nIdAppointment = Integer.parseInt( strIdAppointment );
            boolean bStatusCancelled = Boolean.parseBoolean( strStatusCancelled );
            Appointment appointment = AppointmentService.findAppointmentById( nIdAppointment );
            int idSlot = appointment.getListAppointmentSlot( ).get( 0 ).getIdSlot( );
            Slot slot = SlotService.findSlotById( idSlot );
            if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, Integer.toString( slot.getIdForm( ) ),
                    AppointmentResourceIdService.PERMISSION_CHANGE_APPOINTMENT_STATUS, getUser( ) ) )
            {
                throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_CHANGE_APPOINTMENT_STATUS );
            }
            if ( ( slot.getNbRemainingPlaces( ) == 0 ) && appointment.getIsCancelled( ) && !bStatusCancelled )
            {
                return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_UNVAILABLE_SLOT, AdminMessage.TYPE_STOP ) );
            }
            if ( appointment.getIsCancelled( ) != bStatusCancelled )
            {
                appointment.setIsCancelled( bStatusCancelled );
                AppointmentService.updateAppointment( appointment );
                AppLogService.info( LogUtilities.buildLog( ACTION_DO_CHANGE_APPOINTMENT_STATUS, strIdAppointment, getUser( ) ) );
            }
            return redirect( request, VIEW_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, slot.getIdForm( ) );
        }
        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * List of all the available status of an appointment
     * 
     * @return the list of the status
     */
    private ReferenceList getListStatus( )
    {
        ReferenceList refListStatus = new ReferenceList( );
        refListStatus.addItem( -1, StringUtils.EMPTY );
        refListStatus.addItem( 0, I18nService.getLocalizedString( RESERVED, getLocale( ) ) );
        refListStatus.addItem( 1, I18nService.getLocalizedString( UNRESERVED, getLocale( ) ) );
        return refListStatus;
    }

    private boolean isEquals( List<Slot> listSlot1, List<Slot> listSlot2 )
    {

        if ( listSlot1.size( ) == listSlot2.size( ) )
        {
            return false;
        }

        for ( Slot slot : listSlot1 )
        {

            if ( !listSlot2.stream( ).anyMatch( s -> s.getIdSlot( ) == slot.getIdSlot( ) ) )
            {

                return false;
            }
        }

        return true;
    }

    private boolean isEqualSlot( List<AppointmentSlot> listApptSlot, List<Slot> listSlot2 )
    {

        if ( listApptSlot.size( ) == listSlot2.size( ) )
        {
            return false;
        }

        for ( AppointmentSlot slt : listApptSlot )
        {

            if ( !listSlot2.stream( ).anyMatch( s -> s.getIdSlot( ) == slt.getIdSlot( ) ) )
            {

                return false;
            }
        }

        return true;
    }

}
