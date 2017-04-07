/*
 * Copyright (c) 2002-2015, Mairie de Paris
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import fr.paris.lutece.plugins.appointment.business.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.business.AppointmentFilter;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.ResponseRecapDTO;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.message.FormMessage;
import fr.paris.lutece.plugins.appointment.business.message.FormMessageHome;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.service.AppointmentResponseService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.DisplayService;
import fr.paris.lutece.plugins.appointment.service.EntryService;
import fr.paris.lutece.plugins.appointment.service.FormMessageService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.ReservationRuleService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.WeekDefinitionService;
import fr.paris.lutece.plugins.appointment.service.addon.AppointmentAddOnManager;
import fr.paris.lutece.plugins.appointment.service.upload.AppointmentAsynchronousUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
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
 * This class provides the user interface to manage Appointment features (
 * manage, create, modify, remove )
 * 
 * @author Laurent Payen
 * 
 */
@Controller(controllerJsp = "ManageAppointments.jsp", controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM)
public class AppointmentJspBean extends MVCAdminJspBean {
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 1978001810468444844L;
	private static final String PARAMETER_PAGE_INDEX = "page_index";

	// //////////////////////////////////////////////////////////////////////////
	// Constants

	// templates
	private static final String TEMPLATE_MANAGE_APPOINTMENTS_CALENDAR = "/admin/plugins/appointment/appointment/manage_appointments_calendar.html";
	private static final String TEMPLATE_CREATE_APPOINTMENT = "/admin/plugins/appointment/appointment/create_appointment.html";
	private static final String TEMPLATE_MANAGE_APPOINTMENTS = "/admin/plugins/appointment/appointment/manage_appointments.html";
	private static final String TEMPLATE_VIEW_APPOINTMENT = "/admin/plugins/appointment/appointment/view_appointment.html";
	private static final String TEMPLATE_HTML_CODE_FORM_ADMIN = "admin/plugins/appointment/html_code_form.html";
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
	private static final String PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT = "appointment.form.nbWeekToCreate";

	// Parameters
	private static final String PARAMETER_IS_OPEN = "is_open";
	private static final String PARAMETER_MAX_CAPACITY = "max_capacity";
	private static final String PARAMETER_STARTING_DATE_TIME = "starting_date_time";
	private static final String PARAMETER_ENDING_DATE_TIME = "ending_date_time";
	private static final String PARAMETER_NB_WEEKS_TO_DISPLAY = "nb_weeks_to_display";
	private static final String PARAMETER_DATE_OF_DISPLAY = "date_of_display";
	private static final String PARAMETER_DAY_OF_WEEK = "dow";
	private static final String PARAMETER_EVENTS = "events";
	private static final String PARAMETER_MIN_DURATION = "min_duration";
	private static final String PARAMETER_MIN_TIME = "min_time";
	private static final String PARAMETER_MAX_TIME = "max_time";
	private static final String PARAMETER_ID_APPOINTMENT = "id_appointment";
	private static final String PARAMETER_ID_FORM = "id_form";
	private static final String PARAMETER_EMAIL = "email";
	private static final String PARAMETER_EMAIL_CONFIRMATION = "emailConfirm";
	private static final String PARAMETER_DATE_MIN = "allDates";
	private static final String PARAMETER_FIRST_NAME = "firstname";
	private static final String PARAMETER_LAST_NAME = "lastname";
	private static final String PARAMETER_NB_WEEK = "nb_week";
	private static final String PARAMETER_MAX_WEEK = "max_week";
	private static final String PARAMETER_ID_SLOT = "id_slot";
	private static final String PARAMETER_BACK = "back";
	private static final String PARAMETER_ID_ACTION = "id_action";
	private static final String PARAMETER_NEW_STATUS = "new_status";
	private static final String PARAMETER_ORDER_BY = "orderBy";
	private static final String PARAMETER_ORDER_ASC = "orderAsc";
	private static final String PARAMETER_SAVE_AND_BACK = "saveAndBack";
	private static final String PARAMETER_ID_ADMIN_USER = "idAdminUser";
	private static final String PARAMETER_ID_RESPONSE = "idResponse";
	private static final String PARAMETER_ID_TIME = "time";
	private static final String PARAMETER_ID_APPOINTMENT_DELETE = "apmt";
	private static final String PARAMETER_DELETE_AND_BACK = "eraseAll";
	private static final String PARAMETER_LIM_DATES = "bornDates";
	private static final String PARAMETER_SEARCH = "Search";
	private static final String PARAMETER_RESET = "reset";
	private static final String PARAMETER_MARK_FORCE = "force";
	private static final String PARAMETER_ID_SLOT_ACTIVE = "idSlotActive";
	private static final String PARAMETER_SLOT_LIST_DISPONIBILITY = "slotListDisponibility";
	private static final String PARAMETER_NUMBER_OF_BOOKED_SEATS = "numberPlacesReserved";
	private static final String PARAMETER_FIRSTNAME = "fn";
	private static final String PARAMETER_LASTNAME = "ln";
	private static final String PARAMETER_PHONE = "ph";
	private static final String PARAMETER_EMAILM = "em";
	private static final String PARAMETER_CUSTOMER_ID = "cid";
	private static final String PARAMETER_USER_ID_OPAM = "guid";
	private static final String PARAMETER_PREVIOUS_FORM = "previousForm";

	// Markers
	private static final String MARK_APPOINTMENT_LIST = "appointment_list";
	private static final String MARK_APPOINTMENT = "appointment";
	private static final String MARK_PAGINATOR = "paginator";
	private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
	private static final String MARK_FORM_MESSAGES = "formMessages";
	private static final String MARK_FORM_HTML = "form_html";
	private static final String MARK_FORM = "form";
	private static final String MARK_FORM_CALENDAR_ERRORS = "formCalendarErrors";
	private static final String MARK_FORM_ERRORS = "form_errors";
	private static final String MARK_LIST_ERRORS = "listAllErrors";
	private static final String MARK_LOCALE = "locale";
	private static final String MARK_PLACES = "nbplaces";
	private static final String MARK_STR_ENTRY = "str_entry";
	private static final String MARK_LIST_DAYS = "listDays";
	private static final String MARK_LIST_TIME_BEGIN = "list_time_begin";
	private static final String MARK_MIN_DURATION_APPOINTMENT = "min_duration_appointments";
	private static final String MARK_SLOT = "slot";
	private static final String MARK_LIST_DAYS_OF_WEEK = "list_days_of_week";
	private static final String MARK_RIGHT_CREATE = "rightCreate";
	private static final String MARK_RIGHT_MODIFY = "rightModify";
	private static final String MARK_RIGHT_DELETE = "rightDelete";
	private static final String MARK_RIGHT_VIEW = "rightView";
	private static final String MARK_RIGHT_CHANGE_STATUS = "rightChangeStatus";
	private static final String MARK_DAY = "day";
	private static final String MARK_FILTER = "filter";
	private static final String MARK_REF_LIST_STATUS = "refListStatus";
	private static final String MARK_REF_LIST_EXPORT = "refListExports";
	private static final String MARK_FILTER_FROM_SESSION = "loadFilterFromSession";
	private static final String MARK_TASKS_FORM = "tasks_form";
	private static final String MARK_STATUS_RESERVED = "status_reserved";
	private static final String MARK_STATUS_UNRESERVED = "status_unreserved";
	private static final String MARK_RESOURCE_HISTORY = "resource_history";
	private static final String MARK_LIST_ADMIN_USERS = "list_admin_users";
	private static final String MARK_ADMIN_USER = "admin_user";
	private static final String MARK_ADDON = "addon";
	private static final String MARK_LIST_RESPONSE_RECAP_DTO = "listResponseRecapDTO";
	private static final String MARK_LANGUAGE = "language";
	private static final String MARK_ALLDATES = "allDates";
	private static final String MARK_ACTIVATE_WORKFLOW = "activateWorkflow";
	private static final String MARK_PREVIOUS_FORM = "previousForm";

	// JSPhttp://localhost:8080/lutece/jsp/site/Portal.jsp?page=appointment&action=doCancelAppointment&dateAppointment=16/04/15&refAppointment=2572c82f
	private static final String JSP_MANAGE_APPOINTMENTS = "jsp/admin/plugins/appointment/ManageAppointments.jsp";
	private static final String ERROR_MESSAGE_SLOT_FULL = "appointment.message.error.slotFull";

	// Messages
	private static final String MESSAGE_CONFIRM_REMOVE_APPOINTMENT = "appointment.message.confirmRemoveAppointment";
	private static final String MESSAGE_CONFIRM_REMOVE_MASSAPPOINTMENT = "appointment.message.confirmRemoveMassAppointment";
	private static final String MESSAGE_APPOINTMENT_WITH_NO_ADMIN_USER = "appointment.manage_appointment.labelAppointmentWithNoAdminUser";
	private static final String MESSAGE_UNVAILABLBLE_SLOT = "appointment.slot.unvailable";

	/** Infos error WorkFlow */

	// Properties
	private static final String PROPERTY_DEFAULT_LIST_APPOINTMENT_PER_PAGE = "appointment.listAppointments.itemsPerPage";
	private static final String PROPERTY_NB_WEEKS_TO_DISPLAY_IN_BO = "appointment.nbWeeksToDisplayInBO";

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
	private static final String INFO_APPOINTMENT_UPDATED = "appointment.info.appointment.updated";
	private static final String INFO_APPOINTMENT_REMOVED = "appointment.info.appointment.removed";
	private static final String INFO_APPOINTMENT_MASSREMOVED = "appointment.info.appointment.removed";
	private static final String INFO_APPOINTMENT_EMAIL_ERROR = "appointment.info.appointment.emailerror";
	private static final String INFO_LAST_NAME_ERROR = "appointment.validation.appointment.LastName.notEmpty";
	private static final String INFO_FIRST_NAME_ERROR = "appointment.validation.appointment.FirstName.notEmpty";
	// Error
	private static final String ERROR_MESSAGE_EMPTY_EMAIL = "appointment.validation.appointment.Email.notEmpty";
	private static final String ERROR_MESSAGE_EMPTY_CONFIRM_EMAIL = "appointment.validation.appointment.EmailConfirmation.email";
	private static final String ERROR_MESSAGE_CONFIRM_EMAIL = "appointment.message.error.confirmEmail";
	private static final String ERROR_MESSAGE_EMPTY_NB_BOOKED_SEAT = "appointment.validation.appointment.NbBookedSeat.notEmpty";
	private static final String ERROR_MESSAGE_ERROR_NB_BOOKED_SEAT = "appointment.validation.appointment.NbBookedSeat.error";
	private static final String ERROR_MESSAGE_NUMERIC_NB_BOOKED_SEAT = "appointment.validation.appointment.NbBookedSeat.numeric";
	private static final String ERROR_MESSAGE_POSITIF_NB_BOOKED_SEAT = "appointment.validation.appointment.NbBookedSeat.positif";
	private static final String ERROR_MESSAGE_FORM_NOT_ACTIVE = "appointment.validation.appointment.formNotActive";
	private static final String ERROR_MESSAGE_NO_STARTING_VALIDITY_DATE = "appointment.validation.appointment.noStartingValidityDate";
	private static final String ERROR_MESSAGE_FORM_NO_MORE_VALID = "appointment.validation.appointment.formNoMoreValid";
	private static final String ERROR_MESSAGE_NB_MIN_DAYS_BETWEEN_TWO_APPOINTMENTS = "appointment.validation.appointment.NbMinDaysBetweenTwoAppointments.error";
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
	private static final int STATUS_CODE_ZERO = 0;
	private static final int STATUS_CODE_ONE = 1;
	private static final int STATUS_CODE_TWO = 2;
	private static final String DEFAULT_CURRENT_PAGE = "1";
	private static final String CONSTANT_SPACE = " ";
	private static final String CONSTANT_ZERO = "0";
	private static final String CONSTANT_COMMA = ",";
	public static final String ACTIVATEWORKFLOW = AppPropertiesService.getProperty("appointment.activate.workflow");
	public static final String PREVIEOUS_FORM = "calendar";
	public static final String EXCEL_FILE_EXTENSION = ".xlsx";
	public static final String EXCEL_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	// services
	private final StateService _stateService = SpringContextService.getBean(StateService.BEAN_SERVICE);
	private final ITaskService _taskService = SpringContextService.getBean(TaskService.BEAN_SERVICE);

	// Session variable to store working values
	private int _nDefaultItemsPerPage;

	private int idSlot;

	/**
	 * Get the page to manage appointments. Appointments are displayed in a
	 * calendar.
	 * 
	 * @param request
	 *            The request
	 * @return The HTML code to display
	 */
	@View(value = VIEW_CALENDAR_MANAGE_APPOINTMENTS, defaultView = true)
	public String getViewCalendarManageAppointments(HttpServletRequest request) {
		cleanSession(request.getSession());
		int nIdForm = Integer.parseInt(request.getParameter(PARAMETER_ID_FORM));
		Map<String, Object> model = getModel();
		Form form = FormService.findFormLightByPrimaryKey(nIdForm);
		boolean bError = false;
		if (!form.isActive()) {
			addError(ERROR_MESSAGE_FORM_NOT_ACTIVE, getLocale());
			bError = true;
		}
		FormMessage formMessages = FormMessageHome.findByPrimaryKey(nIdForm);
		// Check if the date of display and the endDateOfDisplay are in the
		// validity date range of the form
		LocalDate startingValidityDate = form.getStartingValidityDate();
		if (startingValidityDate == null) {
			addError(ERROR_MESSAGE_NO_STARTING_VALIDITY_DATE, getLocale());
			bError = true;
		}
		LocalDate startingDateOfDisplay = LocalDate.now();
		if (startingValidityDate != null && startingValidityDate.isAfter(startingDateOfDisplay)) {
			startingDateOfDisplay = startingValidityDate;
		}
		Display display = DisplayService.findDisplayWithFormId(nIdForm);
		// Get the nb weeks to display
		int nNbWeeksToDisplay = display.getNbWeeksToDisplay();
		// Calculate the ending date of display with the nb weeks to display
		// since today
		LocalDate endingDateOfDisplay = startingDateOfDisplay.plus(nNbWeeksToDisplay, ChronoUnit.WEEKS);
		// if the ending date of display is after the ending validity date of
		// the form
		// assign the ending date of display with the ending validity date of
		// the form
		LocalDate endingValidityDate = form.getEndingValidityDate();
		if (endingValidityDate != null) {
			if (endingDateOfDisplay.isAfter(endingValidityDate)) {
				endingDateOfDisplay = endingValidityDate;
			}
			if (startingDateOfDisplay.isAfter(endingDateOfDisplay)) {
				addError(ERROR_MESSAGE_FORM_NO_MORE_VALID, getLocale());
				bError = true;
			}
		}
		// Get the current date of display of the calendar, if it exists
		String strDateOfDisplay = request.getParameter(PARAMETER_DATE_OF_DISPLAY);
		LocalDate dateOfDisplay = startingDateOfDisplay;
		if (StringUtils.isNotEmpty(strDateOfDisplay)) {
			dateOfDisplay = LocalDate.parse(strDateOfDisplay);
		}
		// Get all the week definitions
		HashMap<LocalDate, WeekDefinition> mapWeekDefinition = WeekDefinitionService.findAllWeekDefinition(nIdForm);
		List<WeekDefinition> listWeekDefinition = new ArrayList<WeekDefinition>(mapWeekDefinition.values());
		// Get the min time of all the week definitions
		LocalTime minStartingTime = WeekDefinitionService.getMinStartingTimeOfAListOfWeekDefinition(listWeekDefinition);
		// Get the max time of all the week definitions
		LocalTime maxEndingTime = WeekDefinitionService.getMaxEndingTimeOfAListOfWeekDefinition(listWeekDefinition);
		// Get the min duration of an appointment of all the week definitions
		int nMinDuration = WeekDefinitionService.getMinDurationTimeSlotOfAListOfWeekDefinition(listWeekDefinition);
		// Get all the working days of all the week definitions
		List<String> listDayOfWeek = new ArrayList<>(
				WeekDefinitionService.getSetDayOfWeekOfAListOfWeekDefinition(listWeekDefinition));
		// Build the slots if no errors
		List<Slot> listSlot = new ArrayList<>();
		if (!bError) {
			listSlot = SlotService.buildListSlot(nIdForm, mapWeekDefinition, startingDateOfDisplay, nNbWeeksToDisplay);
		}
		if (bError) {
			model.put(MARK_FORM_CALENDAR_ERRORS, bError);
		}
		model.put(MARK_FORM, FormService.buildAppointmentFormLight(nIdForm));
		model.put(PARAMETER_ID_FORM, nIdForm);
		model.put(MARK_FORM_MESSAGES, formMessages);
		model.put(PARAMETER_NB_WEEKS_TO_DISPLAY, nNbWeeksToDisplay);
		model.put(PARAMETER_DATE_OF_DISPLAY, dateOfDisplay);
		model.put(PARAMETER_DAY_OF_WEEK, listDayOfWeek);
		model.put(PARAMETER_EVENTS, listSlot);
		model.put(PARAMETER_MIN_TIME, minStartingTime);
		model.put(PARAMETER_MAX_TIME, maxEndingTime);
		model.put(PARAMETER_MIN_DURATION, LocalTime.MIN.plusMinutes(nMinDuration));
		return getPage(PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS_CALENDAR, TEMPLATE_MANAGE_APPOINTMENTS_CALENDAR, model);
	}

	/**
	 * Get the page to manage appointments
	 * 
	 * @param request
	 *            The request
	 * @return The HTML code to display
	 */
	@SuppressWarnings("unchecked")
	@View(value = VIEW_MANAGE_APPOINTMENTS)
	public String getManageAppointments(HttpServletRequest request) {
		// Clean session
		AppointmentAsynchronousUploadHandler.getHandler().removeSessionFiles(request.getSession().getId());
		request.getSession().removeAttribute(SESSION_NOT_VALIDATED_APPOINTMENT);
		request.getSession().removeAttribute(SESSION_VALIDATED_APPOINTMENT);

		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		int nIdForm = Integer.parseInt(strIdForm);
		// If it is a new search
		if (request.getParameter(PARAMETER_RESET) != null) {
			request.getSession().removeAttribute(SESSION_APPOINTMENT_FILTER);
			request.getSession().removeAttribute(SESSION_LIST_APPOINTMENTS);
		}
		// Get the appointment filter in session
		AppointmentFilter filter = (AppointmentFilter) request.getSession().getAttribute(SESSION_APPOINTMENT_FILTER);
		if (filter == null) {
			filter = new AppointmentFilter();
			filter.setIdForm(nIdForm);
			// if we come from the calendar, need to get the starting and ending
			// time of the slot
			String strStartingDateTime = request.getParameter(PARAMETER_STARTING_DATE_TIME);
			String strEndingDateTime = request.getParameter(PARAMETER_ENDING_DATE_TIME);
			if (strStartingDateTime != null && strEndingDateTime != null) {
				LocalDateTime startingDateTime = LocalDateTime.parse(strStartingDateTime);
				LocalDateTime endingDateTime = LocalDateTime.parse(strEndingDateTime);
				filter.setStartingDateOfSearch(Date.valueOf(startingDateTime.toLocalDate()));
				filter.setStartingTimeOfSearch(startingDateTime.toLocalTime());
				filter.setEndingDateOfSearch(Date.valueOf(endingDateTime.toLocalDate()));
				filter.setEndingTimeOfSearch(endingDateTime.toLocalTime());
			} else {
				// Build it with the current date
				Date dateNow = Date.valueOf(LocalDate.now());
				filter.setStartingDateOfSearch(dateNow);
				filter.setEndingDateOfSearch(dateNow);
			}
			request.getSession().setAttribute(SESSION_APPOINTMENT_FILTER, filter);
		}
		// Get the list in session
		// If it is an order by or a navigation page, no need to search again
		List<AppointmentDTO> listAppointmentsDTO = (List<AppointmentDTO>) request.getSession()
				.getAttribute(SESSION_LIST_APPOINTMENTS);
		// If it is a new search
		if (request.getParameter(PARAMETER_SEARCH) != null) {
			// Populate the filter
			populate(filter, request);
			listAppointmentsDTO = AppointmentService.findListAppointmentsByFilter(filter).stream()
					.sorted((a1, a2) -> Integer.compare(a1.getIdAppointment(), a2.getIdAppointment()))
					.collect(Collectors.toList());
		}
		// If it is an order by
		String strOrderBy = request.getParameter(PARAMETER_ORDER_BY);
		String strOrderAsc = request.getParameter(PARAMETER_ORDER_ASC);
		if (strOrderBy != null && strOrderAsc != null) {
			boolean bAsc = Boolean.parseBoolean(strOrderAsc);
			Stream<AppointmentDTO> stream = null;
			switch (strOrderBy) {
			case "last_name":
				stream = listAppointmentsDTO.stream().sorted((a1, a2) -> a1.getLastName().compareTo(a2.getLastName()));
				break;
			case "first_name":
				stream = listAppointmentsDTO.stream()
						.sorted((a1, a2) -> a1.getFirstName().compareTo(a2.getFirstName()));
				break;
			case "email":
				stream = listAppointmentsDTO.stream().sorted((a1, a2) -> a1.getEmail().compareTo(a2.getEmail()));
				break;
			case "date_appointment":
				stream = listAppointmentsDTO.stream()
						.sorted((a1, a2) -> a1.getStartingDateTime().compareTo(a2.getStartingDateTime()));
				break;
			case "status":
				stream = listAppointmentsDTO.stream()
						.sorted((a1, a2) -> Boolean.compare(a1.getIsCancelled(), a2.getIsCancelled()));
				break;
			default:
				stream = listAppointmentsDTO.stream()
						.sorted((a1, a2) -> Integer.compare(a1.getIdAppointment(), a2.getIdAppointment()));
			}
			listAppointmentsDTO = stream.collect(Collectors.toList());
			if (!bAsc) {
				Collections.reverse(listAppointmentsDTO);
			}
		}
		if (StringUtils.isNotEmpty(request.getParameter(PARAMETER_DELETE_AND_BACK))) {
			String[] tabIdAppointmentToDelete = request.getParameterValues(PARAMETER_ID_APPOINTMENT_DELETE);
			if (tabIdAppointmentToDelete != null) {
				request.getSession().setAttribute(PARAMETER_ID_APPOINTMENT_DELETE, tabIdAppointmentToDelete);
				return getConfirmRemoveMassAppointment(request, nIdForm);
			}
		}
		String strCurrentPageIndex = Paginator.getPageIndex(request, Paginator.PARAMETER_PAGE_INDEX,
				(String) request.getSession().getAttribute(SESSION_CURRENT_PAGE_INDEX));
		if (strCurrentPageIndex == null) {
			strCurrentPageIndex = DEFAULT_CURRENT_PAGE;
		}
		request.getSession().setAttribute(SESSION_CURRENT_PAGE_INDEX, strCurrentPageIndex);
		int nItemsPerPage = Paginator.getItemsPerPage(request, Paginator.PARAMETER_ITEMS_PER_PAGE,
				getIntSessionAttribute(request.getSession(), SESSION_ITEMS_PER_PAGE), _nDefaultItemsPerPage);
		request.getSession().setAttribute(SESSION_ITEMS_PER_PAGE, nItemsPerPage);

		UrlItem url = new UrlItem(JSP_MANAGE_APPOINTMENTS);
		url.addParameter(MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_APPOINTMENTS);
		url.addParameter(PARAMETER_ID_FORM, strIdForm);
		String strUrl = url.getUrl();
		if (listAppointmentsDTO == null) {
			listAppointmentsDTO = AppointmentService.findListAppointmentsByFilter(filter).stream()
					.sorted((a1, a2) -> Integer.compare(a1.getIdAppointment(), a2.getIdAppointment()))
					.collect(Collectors.toList());
		}
		request.getSession().setAttribute(SESSION_LIST_APPOINTMENTS, listAppointmentsDTO);
		LocalizedPaginator<AppointmentDTO> paginator = new LocalizedPaginator<AppointmentDTO>(listAppointmentsDTO,
				nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX, strCurrentPageIndex, getLocale());
		ReferenceList refListExports = new ReferenceList();
		for (ExportFilter tmpFilter : ExportFilter.values())
			refListExports.addItem(tmpFilter.getValeur(),
					I18nService.getLocalizedString(tmpFilter.getLibelle(), getLocale()));
		AppointmentForm form = FormService.buildAppointmentFormLight(nIdForm);
		Map<String, Object> model = getModel();
		model.put(MARK_FORM, form);
		model.put(MARK_FORM_MESSAGES, FormMessageService.findFormMessageByIdForm(nIdForm));
		model.put(MARK_NB_ITEMS_PER_PAGE, Integer.toString(nItemsPerPage));
		model.put(MARK_PAGINATOR, paginator);
		model.put(MARK_LANGUAGE, getLocale());
		model.put(MARK_ACTIVATE_WORKFLOW, ACTIVATEWORKFLOW);
		// if ((form.getIdWorkflow() > 0) &&
		// WorkflowService.getInstance().isAvailable()) {
		// for (Appointment appointment : delegatePaginator.getPageItems()) {
		// int nIdWorkflow = form.getIdWorkflow();
		//
		// StateFilter stateFilter = new StateFilter();
		// stateFilter.setIdWorkflow(nIdWorkflow);
		//
		// State stateAppointment =
		// _stateService.findByResource(appointment.getIdAppointment(),
		// Appointment.APPOINTMENT_RESOURCE_TYPE, nIdWorkflow);
		//
		// if (stateAppointment != null) {
		// appointment.setState(stateAppointment);
		// }
		//
		// appointment.setListWorkflowActions(
		// WorkflowService.getInstance().getActions(appointment.getIdAppointment(),
		// Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow(),
		// getUser()));
		// }
		// }
		//
		// if (bfilterByWorkFlow) {
		// Collections.sort(delegatePaginator.getPageItems(), new
		// AppointmentFilterWorkFlow(filter.getOrderAsc()));
		// }
		AdminUser user = getUser();
		model.put(MARK_APPOINTMENT_LIST, paginator.getPageItems());
		model.put(MARK_FILTER, filter);
		model.put(MARK_REF_LIST_EXPORT, refListExports);
		model.put(MARK_RIGHT_CREATE, RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT, user));
		model.put(MARK_RIGHT_MODIFY, RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_MODIFY_APPOINTMENT, user));
		model.put(MARK_RIGHT_DELETE, RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_DELETE_APPOINTMENT, user));
		model.put(MARK_RIGHT_VIEW, RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, user));
		model.put(MARK_RIGHT_CHANGE_STATUS, RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_CHANGE_APPOINTMENT_STATUS, user));
		return getPage(PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS, TEMPLATE_MANAGE_APPOINTMENTS, model);
	}

	/**
	 * Manages the removal form of a appointment whose identifier is in the HTTP
	 * request
	 * 
	 * @param request
	 *            The HTTP request
	 * @return the HTML code to confirm
	 */
	@Action(ACTION_CONFIRM_REMOVE_APPOINTMENT)
	public String getConfirmRemoveAppointment(HttpServletRequest request) {
		UrlItem url = new UrlItem(getActionUrl(ACTION_REMOVE_APPOINTMENT));
		url.addParameter(PARAMETER_ID_APPOINTMENT, request.getParameter(PARAMETER_ID_APPOINTMENT));
		url.addParameter(PARAMETER_ID_FORM, request.getParameter(PARAMETER_ID_FORM));
		String strMessageUrl = AdminMessageService.getMessageUrl(request, MESSAGE_CONFIRM_REMOVE_APPOINTMENT,
				url.getUrl(), AdminMessage.TYPE_CONFIRMATION);
		return redirect(request, strMessageUrl);
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
	@SuppressWarnings({ "unchecked" })
	@Action(ACTION_REMOVE_APPOINTMENT)
	public String doRemoveAppointment(HttpServletRequest request) throws AccessDeniedException {
		int nIdAppointment = Integer.parseInt(request.getParameter(PARAMETER_ID_APPOINTMENT));
		Integer idForm = Integer.parseInt(request.getParameter(PARAMETER_ID_FORM));
		AppointmentService.deleteAppointment(nIdAppointment, getUser());
		addInfo(INFO_APPOINTMENT_REMOVED, getLocale());
		// Need to update the list of the appointments in session
		List<AppointmentDTO> listAppointmentsDTO = (List<AppointmentDTO>) request.getSession()
				.getAttribute(SESSION_LIST_APPOINTMENTS);
		if (listAppointmentsDTO != null) {
			listAppointmentsDTO = listAppointmentsDTO.stream().filter(a -> a.getIdAppointment() != nIdAppointment)
					.collect(Collectors.toList());
			request.getSession().setAttribute(SESSION_LIST_APPOINTMENTS, listAppointmentsDTO);
		}
		return redirect(request, VIEW_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, idForm);
	}

	/**
	 * Manages the removal form of a appointment whose identifier is in the HTTP
	 * request
	 * 
	 * @param request
	 *            The HTTP request
	 * @return the HTML code to confirm
	 */
	@Action(ACTION_CONFIRM_REMOVE_MASS_APPOINTMENT)
	public String getConfirmRemoveMassAppointment(HttpServletRequest request, int nIdForm) {
		UrlItem url = new UrlItem(getActionUrl(ACTION_REMOVE_MASSAPPOINTMENT));
		url.addParameter(PARAMETER_ID_FORM, nIdForm);
		String strMessageUrl = AdminMessageService.getMessageUrl(request, MESSAGE_CONFIRM_REMOVE_MASSAPPOINTMENT,
				url.getUrl(), AdminMessage.TYPE_CONFIRMATION);
		return redirect(request, strMessageUrl);
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
	@SuppressWarnings("unchecked")
	@Action(ACTION_REMOVE_MASSAPPOINTMENT)
	public String doRemoveMassAppointment(HttpServletRequest request) throws AccessDeniedException {
		String[] tabIdAppointmentToDelete = (String[]) request.getSession()
				.getAttribute(PARAMETER_ID_APPOINTMENT_DELETE);
		request.getSession().removeAttribute(PARAMETER_ID_APPOINTMENT_DELETE);
		Integer idForm = Integer.parseInt(request.getParameter(PARAMETER_ID_FORM));
		if (tabIdAppointmentToDelete != null) {
			for (String strIdAppointment : tabIdAppointmentToDelete) {
				AppointmentService.deleteAppointment(new Integer(strIdAppointment), getUser());
			}
			addInfo(INFO_APPOINTMENT_MASSREMOVED, getLocale());
		}
		// Need to update the list of the appointments in session
		List<AppointmentDTO> listAppointmentsDTO = (List<AppointmentDTO>) request.getSession()
				.getAttribute(SESSION_LIST_APPOINTMENTS);
		ArrayList<String> listStringIdAppointment = new ArrayList<>(Arrays.asList(tabIdAppointmentToDelete));
		if (listAppointmentsDTO != null) {
			listAppointmentsDTO = listAppointmentsDTO.stream()
					.filter(a -> !listStringIdAppointment.contains(new Integer(a.getIdAppointment()).toString()))
					.collect(Collectors.toList());
			request.getSession().setAttribute(SESSION_LIST_APPOINTMENTS, listAppointmentsDTO);
		}
		return redirect(request, VIEW_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, idForm);
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
	@View(VIEW_VIEW_APPOINTMENT)
	public String getViewAppointment(HttpServletRequest request) throws AccessDeniedException {
		String strIdAppointment = request.getParameter(PARAMETER_ID_APPOINTMENT);
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		int nIdForm = Integer.parseInt(strIdForm);
		Form form = FormService.findFormLightByPrimaryKey(nIdForm);
		int nItemsPerPage = Paginator.getItemsPerPage(request, Paginator.PARAMETER_ITEMS_PER_PAGE,
				getIntSessionAttribute(request.getSession(), SESSION_ITEMS_PER_PAGE), _nDefaultItemsPerPage);
		int nIdAppointment = Integer.parseInt(strIdAppointment);
		AppointmentDTO appointmentDTO = AppointmentService.buildAppointmentDTOFromIdAppointment(nIdAppointment);
		if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, getUser())) {
			throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT);
		}
		Map<String, Object> model = getModel();
		model.put(MARK_APPOINTMENT, appointmentDTO);
		model.put(MARK_FORM_MESSAGES, FormMessageService.findFormMessageByIdForm(nIdForm));
		model.put(MARK_FORM, form);
		model.put(MARK_NB_ITEMS_PER_PAGE, Integer.toString(nItemsPerPage));
		if ((form.getIdWorkflow() > 0) && WorkflowService.getInstance().isAvailable()) {
			model.put(MARK_RESOURCE_HISTORY, WorkflowService.getInstance().getDisplayDocumentHistory(nIdAppointment,
					Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow(), request, getLocale()));
		}
		if ((form.getIdWorkflow() > 0) && WorkflowService.getInstance().isAvailable()) {
			int nIdWorkflow = form.getIdWorkflow();
			StateFilter stateFilter = new StateFilter();
			stateFilter.setIdWorkflow(nIdWorkflow);
			State stateAppointment = _stateService.findByResource(appointmentDTO.getIdAppointment(),
					Appointment.APPOINTMENT_RESOURCE_TYPE, nIdWorkflow);
			if (stateAppointment != null) {
				appointmentDTO.setState(stateAppointment);
			}
			// appointmentDTO.setListWorkflowActions(WorkflowService.getInstance().getActions(appointmentDTO.getIdAppointment(),
			// Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow(),
			// getUser()));
		}
		Locale locale = getLocale();
		List<Response> listResponse = AppointmentResponseService.findListResponse(nIdAppointment);
		for (Response response : listResponse) {
			if (response.getFile() != null) {
				response.setFile(FileHome.findByPrimaryKey(response.getFile().getIdFile()));
			}
			if (response.getEntry() != null) {
				response.setEntry(EntryHome.findByPrimaryKey(response.getEntry().getIdEntry()));
			}
		}
		appointmentDTO.setListResponse(listResponse);
		List<ResponseRecapDTO> listResponseRecapDTO = new ArrayList<ResponseRecapDTO>(
				appointmentDTO.getListResponse().size());
		for (Response response : appointmentDTO.getListResponse()) {
			int nIndex = response.getEntry().getPosition();
			IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService(response.getEntry());
			addInPosition(nIndex,
					new ResponseRecapDTO(response,
							entryTypeService.getResponseValueForRecap(response.getEntry(), request, response, locale)),
					listResponseRecapDTO);
			listResponseRecapDTO.add(new ResponseRecapDTO(response,
					entryTypeService.getResponseValueForRecap(response.getEntry(), request, response, locale)));
		}
		model.put(MARK_LIST_RESPONSE_RECAP_DTO, listResponseRecapDTO);
		model.put(MARK_LIST_RESPONSE_RECAP_DTO, listResponseRecapDTO);
		model.put(MARK_ADDON,
				AppointmentAddOnManager.getAppointmentAddOn(appointmentDTO.getIdAppointment(), getLocale()));
		AdminUser user = getUser();
		model.put(MARK_RIGHT_CREATE, RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT, user));
		model.put(MARK_RIGHT_MODIFY, RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_MODIFY_APPOINTMENT, user));
		model.put(MARK_RIGHT_DELETE, RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_DELETE_APPOINTMENT, user));
		model.put(MARK_RIGHT_VIEW, RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, user));
		model.put(MARK_RIGHT_CHANGE_STATUS, RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_CHANGE_APPOINTMENT_STATUS, user));
		model.put(MARK_LANGUAGE, getLocale());
		model.put(MARK_ACTIVATE_WORKFLOW, ACTIVATEWORKFLOW);
		return getPage(PROPERTY_PAGE_TITLE_VIEW_APPOINTMENT, TEMPLATE_VIEW_APPOINTMENT, model);
	}

	/**
	 * add an object in a collection list
	 * 
	 * @param int
	 *            the index
	 * @param ResponseRecapDTO
	 *            the object
	 * @param List<ResponseRecapDTO>
	 *            the collection
	 */
	private void addInPosition(int i, ResponseRecapDTO response, List<ResponseRecapDTO> list) {
		while (list.size() < i) {
			list.add(list.size(), null);
		}
		list.set(i - 1, response);
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
	@SuppressWarnings("unchecked")
	public String getDownloadFileAppointment(HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException {
		String strIdResponse = request.getParameter(PARAMETER_ID_FORM);
		if (StringUtils.isEmpty(strIdResponse) || !StringUtils.isNumeric(strIdResponse)) {
			return redirect(request, AppointmentFormJspBean.getURLManageAppointmentForms(request));
		}
		if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdResponse,
				AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, getUser())) {
			throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT);
		}
		AppointmentForm tmpForm = FormService.buildAppointmentFormLight(Integer.parseInt(strIdResponse));
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook
				.createSheet(I18nService.getLocalizedString("appointment.permission.label.resourceType", getLocale()));
		List<Object[]> tmpObj = new ArrayList<Object[]>();
		EntryFilter entryFilter = new EntryFilter();
		entryFilter.setIdResource(Integer.valueOf(strIdResponse));
		List<Entry> listEntry = EntryHome.getEntryList(entryFilter);
		Map<Integer, String> mapDefaultValueGenAttBackOffice = new HashMap<Integer, String>();
		for (Entry e : listEntry) {
			if (e.isOnlyDisplayInBack()) {
				e = EntryHome.findByPrimaryKey(e.getIdEntry());
				if (e.getFields() != null && e.getFields().size() == 1
						&& !StringUtils.isEmpty(e.getFields().get(0).getValue())) {
					mapDefaultValueGenAttBackOffice.put(e.getIdEntry(), e.getFields().get(0).getValue());
				} else if (e.getFields() != null) {
					for (Field field : e.getFields()) {
						if (field.isDefaultValue()) {
							mapDefaultValueGenAttBackOffice.put(e.getIdEntry(), field.getValue());
						}
					}
				}
			}
		}
		int nTaille = 9 + (listEntry.size() + 1);
		if (tmpForm != null) {
			int nIndex = 0;
			Object[] strWriter = new String[1];
			strWriter[0] = tmpForm.getTitle();
			tmpObj.add(strWriter);
			Object[] strInfos = new String[nTaille];
			strInfos[0] = I18nService.getLocalizedString("appointment.manage_appointments.columnLastName", getLocale());
			strInfos[1] = I18nService.getLocalizedString("appointment.manage_appointments.columnFirstName",
					getLocale());
			strInfos[2] = I18nService.getLocalizedString("appointment.manage_appointments.columnEmail", getLocale());
			strInfos[3] = I18nService.getLocalizedString("appointment.manage_appointments.columnDateAppointment",
					getLocale());
			strInfos[4] = I18nService.getLocalizedString("appointment.model.entity.appointmentform.attribute.timeStart",
					getLocale());
			strInfos[5] = I18nService.getLocalizedString("appointment.model.entity.appointmentform.attribute.timeEnd",
					getLocale());
			strInfos[6] = I18nService.getLocalizedString("appointment.manage_appointments.columnStatus", getLocale());
			strInfos[7] = I18nService.getLocalizedString("appointment.manage_appointments.columnLogin", getLocale());
			strInfos[8] = I18nService.getLocalizedString("appointment.manage_appointments.columnState", getLocale());
			strInfos[9] = I18nService.getLocalizedString(
					"appointment.manage_appointments.columnNumberOfBookedseatsPerAppointment", getLocale());
			nIndex = 1;
			if (listEntry.size() > 0) {
				for (Entry e : listEntry) {
					strInfos[9 + nIndex] = e.getTitle();
					nIndex++;
				}
			}
			tmpObj.add(strInfos);
		}
		List<AppointmentDTO> listAppointmentsDTO = (List<AppointmentDTO>) request.getSession()
				.getAttribute(SESSION_LIST_APPOINTMENTS);
		if (listAppointmentsDTO != null) {
			for (AppointmentDTO appointmentDTO : listAppointmentsDTO) {
				int nIndex = 0;
				Object[] strWriter = new String[nTaille];
				strWriter[0] = appointmentDTO.getLastName();
				strWriter[1] = appointmentDTO.getFirstName();
				strWriter[2] = appointmentDTO.getEmail();
				strWriter[3] = appointmentDTO.getDateOfTheAppointment();
				strWriter[4] = appointmentDTO.getStartingTime().toString();
				strWriter[5] = appointmentDTO.getEndingTime().toString();
				String status = I18nService.getLocalizedString(AppointmentDTO.PROPERTY_APPOINTMENT_STATUS_RESERVED,
						getLocale());
				if (appointmentDTO.getIsCancelled()) {
					status = I18nService.getLocalizedString(AppointmentDTO.PROPERTY_APPOINTMENT_STATUS_UNRESERVED,
							getLocale());
				}
				strWriter[6] = status;
				strWriter[7] = Integer.toString(appointmentDTO.getIdUser());
				State stateAppointment = _stateService.findByResource(appointmentDTO.getIdAppointment(),
						Appointment.APPOINTMENT_RESOURCE_TYPE, tmpForm.getIdWorkflow());
				String strState = StringUtils.EMPTY;
				if (stateAppointment != null) {
					appointmentDTO.setState(stateAppointment);
					strState = stateAppointment.getName();
				}
				strWriter[8] = strState;
				nIndex = 1;
				strWriter[9] = Integer.toString(appointmentDTO.getNbBookedSeats());
				List<Integer> listIdResponse = AppointmentResponseService
						.findListIdResponse(appointmentDTO.getIdAppointment());
				List<Response> listResponses = new ArrayList<Response>();
				for (int nIdResponse : listIdResponse) {
					Response resp = ResponseHome.findByPrimaryKey(nIdResponse);
					if (resp != null) {
						listResponses.add(resp);
					}
				}
				for (Entry e : listEntry) {
					Integer key = e.getIdEntry();
					String strValue = StringUtils.EMPTY;
					String strPrefix = StringUtils.EMPTY;
					for (Response resp : listResponses) {
						String strRes = StringUtils.EMPTY;
						if (key.equals(resp.getEntry().getIdEntry())) {
							Field f = resp.getField();
							int nfield = 0;
							if (f != null) {
								nfield = f.getIdField();
								Field field = FieldHome.findByPrimaryKey(nfield);
								if (field != null) {
									strRes = field.getTitle();
								}
							} else {
								strRes = resp.getResponseValue();
							}
						}
						if ((strRes != null) && !strRes.isEmpty()) {
							strValue += (strPrefix + strRes);
							strPrefix = CONSTANT_COMMA;
						}
					}
					if (strValue.isEmpty() && mapDefaultValueGenAttBackOffice.containsKey(key)) {
						strValue = mapDefaultValueGenAttBackOffice.get(key);
					}
					if (!strValue.isEmpty()) {
						strWriter[9 + nIndex] = strValue;
					}
					nIndex++;
				}
				tmpObj.add(strWriter);
			}
		}
		int nRownum = 0;
		for (Object[] myObj : tmpObj) {
			Row row = sheet.createRow(nRownum++);
			int nCellnum = 0;
			for (Object strLine : myObj) {
				Cell cell = row.createCell(nCellnum++);
				if (strLine instanceof String) {
					cell.setCellValue((String) strLine);
				} else if (strLine instanceof Boolean) {
					cell.setCellValue((Boolean) strLine);
				} else if (strLine instanceof Date) {
					cell.setCellValue((Date) strLine);
				} else if (strLine instanceof Double) {
					cell.setCellValue((Double) strLine);
				}
			}
		}
		try {
			String now = new SimpleDateFormat("yyyyMMdd-hhmm")
					.format(GregorianCalendar.getInstance(getLocale()).getTime()) + "_"
					+ I18nService.getLocalizedString("appointment.permission.label.resourceType", getLocale())
					+ EXCEL_FILE_EXTENSION;
			response.setContentType(EXCEL_MIME_TYPE);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + now + "\";");
			response.setHeader("Pragma", "public");
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate,post-check=0,pre-check=0");
			OutputStream os = response.getOutputStream();
			workbook.write(os);
			os.close();
			workbook.close();
		} catch (IOException e) {
			AppLogService.error(e);
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
	@View(VIEW_MODIFY_APPOINTMENT)
	public String getModifyAppointment(HttpServletRequest request) throws AccessDeniedException {
		HttpSession session = request.getSession();
		clearUploadFilesIfNeeded(session);
		String strIdAppointment = request.getParameter(PARAMETER_ID_APPOINTMENT);
		int nIdAppointment = Integer.parseInt(strIdAppointment);
		AppointmentDTO appointmentDTO = AppointmentService.buildAppointmentDTOFromIdAppointment(nIdAppointment);
		appointmentDTO.setListResponse(AppointmentResponseService.findAndBuildListResponse(nIdAppointment, request));
		session.removeAttribute(SESSION_NOT_VALIDATED_APPOINTMENT);
		session.setAttribute(SESSION_VALIDATED_APPOINTMENT, appointmentDTO);
		return getViewCreateAppointment(request);
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
	@SuppressWarnings("unchecked")
	@View(VIEW_CREATE_APPOINTMENT)
	public String getViewCreateAppointment(HttpServletRequest request) throws AccessDeniedException {
		clearUploadFilesIfNeeded(request.getSession());
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT, getUser())) {
			throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT);
		}
		AppointmentForm form = (AppointmentForm) request.getSession().getAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM);
		int nIdForm = Integer.parseInt(strIdForm);
		// Get the not validated appointment in session if it exists
		AppointmentDTO appointmentDTO = (AppointmentDTO) request.getSession()
				.getAttribute(SESSION_NOT_VALIDATED_APPOINTMENT);
		if (appointmentDTO == null) {
			// Try to get the validated appointment in session
			// (in case the user click on back button in the recap view
			appointmentDTO = (AppointmentDTO) request.getSession().getAttribute(SESSION_VALIDATED_APPOINTMENT);
			if (appointmentDTO == null) {
				appointmentDTO = new AppointmentDTO();
				int nIdSlot = Integer.parseInt(request.getParameter(PARAMETER_ID_SLOT));
				Slot slot = null;
				// If nIdSlot == 0, the slot has not been created yet
				if (nIdSlot == 0) {
					// Need to get all the informations to create the slot
					LocalDateTime startingDateTime = LocalDateTime
							.parse(request.getParameter(PARAMETER_STARTING_DATE_TIME));
					LocalDateTime endingDateTime = LocalDateTime
							.parse(request.getParameter(PARAMETER_ENDING_DATE_TIME));
					boolean bIsOpen = Boolean.parseBoolean(request.getParameter(PARAMETER_IS_OPEN));
					int nMaxCapacity = Integer.parseInt(request.getParameter(PARAMETER_MAX_CAPACITY));
					slot = SlotService.buildSlot(nIdForm, startingDateTime, endingDateTime, nMaxCapacity, nMaxCapacity,
							bIsOpen);
				} else {
					slot = SlotService.findSlotById(nIdSlot);
					SlotService.addDateAndTimeToSlot(slot);
				}

				appointmentDTO.setSlot(slot);
				appointmentDTO.setIdForm(nIdForm);
				LuteceUser user = SecurityService.getInstance().getRegisteredUser(request);
				if (user != null) {
					Map<String, String> map = user.getUserInfos();
					appointmentDTO.setEmail(map.get("user.business-info.online.email"));
					appointmentDTO.setFirstName(map.get("user.name.given"));
					appointmentDTO.setLastName(map.get("user.name.family"));
				}
				request.getSession().setAttribute(SESSION_NOT_VALIDATED_APPOINTMENT, appointmentDTO);
				ReservationRule reservationRule = ReservationRuleService
						.findReservationRuleByIdFormAndClosestToDateOfApply(nIdForm, slot.getDate());
				WeekDefinition weekDefinition = WeekDefinitionService
						.findWeekDefinitionByIdFormAndClosestToDateOfApply(nIdForm, slot.getDate());
				form = FormService.buildAppointmentForm(nIdForm, reservationRule.getIdReservationRule(),
						weekDefinition.getIdWeekDefinition());
				request.getSession().setAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM, form);
			}
		}
		Locale locale = getLocale();
		StringBuffer strBuffer = new StringBuffer();
		List<Entry> listEntryFirstLevel = EntryService.getFilter(form.getIdForm(), true);
		for (Entry entry : listEntryFirstLevel) {
			EntryService.getHtmlEntry(entry.getIdEntry(), strBuffer, locale, true, request);
		}
		FormMessage formMessages = FormMessageService.findFormMessageByIdForm(nIdForm);
		Map<String, Object> model = getModel();
		model.put(MARK_APPOINTMENT, appointmentDTO);
		model.put(PARAMETER_DATE_OF_DISPLAY, appointmentDTO.getSlot().getDate());
		model.put(MARK_FORM, form);
		model.put(MARK_FORM_MESSAGES, formMessages);
		model.put(MARK_STR_ENTRY, strBuffer.toString());
		model.put(MARK_LOCALE, locale);
		model.put(MARK_PLACES, form.getMaxPeoplePerAppointment());
		List<GenericAttributeError> listErrors = (List<GenericAttributeError>) request.getSession()
				.getAttribute(SESSION_APPOINTMENT_FORM_ERRORS);
		model.put(MARK_FORM_ERRORS, listErrors);
		model.put(MARK_LIST_ERRORS, AppointmentDTO.getAllErrors(locale));
		HtmlTemplate templateForm = AppTemplateService.getTemplate(TEMPLATE_HTML_CODE_FORM_ADMIN, getLocale(), model);
		model.put(MARK_FORM_HTML, templateForm.getHtml());
		if (listErrors != null) {
			model.put(MARK_FORM_ERRORS, listErrors);
			request.getSession().removeAttribute(SESSION_APPOINTMENT_FORM_ERRORS);
		}
		return getPage(PROPERTY_PAGE_TITLE_CREATE_APPOINTMENT, TEMPLATE_CREATE_APPOINTMENT, model);
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
	@Action(ACTION_DO_VALIDATE_FORM)
	public String doValidateForm(HttpServletRequest request) throws AccessDeniedException, SiteMessageException {
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		AppointmentDTO appointmentDTO = (AppointmentDTO) request.getSession()
				.getAttribute(SESSION_NOT_VALIDATED_APPOINTMENT);
		AppointmentForm form = (AppointmentForm) request.getSession().getAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM);
		int nIdForm = Integer.parseInt(strIdForm);
		List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>();
		Locale locale = request.getLocale();
		String strEmail = request.getParameter(PARAMETER_EMAIL);
		AppointmentUtilities.checkEmail(strEmail, request.getParameter(PARAMETER_EMAIL_CONFIRMATION), form, locale,
				listFormErrors);
		if (!AppointmentUtilities.checkUserAndAppointment(appointmentDTO.getSlot().getStartingDateTime().toLocalDate(),
				strEmail, form, locale, listFormErrors)) {
			addError(ERROR_MESSAGE_NB_MIN_DAYS_BETWEEN_TWO_APPOINTMENTS, locale);
			return redirect(request, VIEW_CREATE_APPOINTMENT, PARAMETER_ID_FORM, nIdForm, PARAMETER_ID_SLOT,
					appointmentDTO.getSlot().getIdSlot());
		}
		int nbBookedSeats = AppointmentUtilities.checkAndReturnNbBookedSeats(
				request.getParameter(PARAMETER_NUMBER_OF_BOOKED_SEATS), form,
				appointmentDTO.getSlot().getNbRemainingPlaces(), locale, listFormErrors);
		AppointmentUtilities.fillAppointmentDTO(appointmentDTO, nbBookedSeats, strEmail,
				request.getParameter(PARAMETER_FIRST_NAME), request.getParameter(PARAMETER_LAST_NAME));
		AppointmentUtilities.validateFormAndEntries(appointmentDTO, request, listFormErrors);
		if (CollectionUtils.isNotEmpty(listFormErrors)) {
			request.getSession().setAttribute(SESSION_APPOINTMENT_FORM_ERRORS, listFormErrors);
			return redirect(request, VIEW_CREATE_APPOINTMENT, PARAMETER_ID_FORM, nIdForm, PARAMETER_ID_SLOT,
					appointmentDTO.getSlot().getIdSlot());
		}
		request.getSession().removeAttribute(SESSION_NOT_VALIDATED_APPOINTMENT);
		request.getSession().setAttribute(SESSION_VALIDATED_APPOINTMENT, appointmentDTO);
		return redirect(request, VIEW_DISPLAY_RECAP_APPOINTMENT, PARAMETER_ID_FORM, nIdForm);
	}

	/**
	 * Display the recap before validating an appointment
	 * 
	 * @param request
	 *            The request
	 * @return The HTML content to display or the next URL to redirect to
	 */
	@View(VIEW_DISPLAY_RECAP_APPOINTMENT)
	public String displayRecapAppointment(HttpServletRequest request) {
		AppointmentDTO appointmentDTO = (AppointmentDTO) request.getSession()
				.getAttribute(SESSION_VALIDATED_APPOINTMENT);
		AppointmentForm form = (AppointmentForm) request.getSession().getAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(MARK_FORM_MESSAGES, FormMessageService.findFormMessageByIdForm(appointmentDTO.getIdForm()));
		fillCommons(model);
		model.put(MARK_APPOINTMENT, appointmentDTO);
		Locale locale = getLocale();
		List<ResponseRecapDTO> listResponseRecapDTO = new ArrayList<ResponseRecapDTO>(
				appointmentDTO.getListResponse().size());
		for (Response response : appointmentDTO.getListResponse()) {
			IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService(response.getEntry());
			listResponseRecapDTO.add(new ResponseRecapDTO(response,
					entryTypeService.getResponseValueForRecap(response.getEntry(), request, response, locale)));
		}
		model.put(MARK_ADDON,
				AppointmentAddOnManager.getAppointmentAddOn(appointmentDTO.getIdAppointment(), getLocale()));
		model.put(MARK_LIST_RESPONSE_RECAP_DTO, listResponseRecapDTO);
		model.put(MARK_FORM, form);
		return getPage(PROPERTY_PAGE_TITLE_RECAP_APPOINTMENT, TEMPLATE_APPOINTMENT_FORM_RECAP, model);
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
	@Action(ACTION_DO_MAKE_APPOINTMENT)
	public String doMakeAppointment(HttpServletRequest request) throws AccessDeniedException {
		AppointmentDTO appointmentDTO = (AppointmentDTO) request.getSession()
				.getAttribute(SESSION_VALIDATED_APPOINTMENT);
		if (StringUtils.isNotEmpty(request.getParameter(PARAMETER_BACK))) {
			return redirect(request, VIEW_CREATE_APPOINTMENT, PARAMETER_ID_FORM, appointmentDTO.getIdForm());
		}
		AppointmentForm form = (AppointmentForm) request.getSession().getAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM);
		if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, Integer.toString(form.getIdForm()),
				AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT, getUser())) {
			throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT);
		}
		Slot slot = null;
		// Reload the slot from the database
		// The slot could have been taken since the beginning of the entry of
		// the form
		if (appointmentDTO.getSlot().getIdSlot() != 0) {
			slot = SlotService.findSlotById(appointmentDTO.getSlot().getIdSlot());
		} else {
			HashMap<LocalDateTime, Slot> mapSlot = SlotService.findSlotsByIdFormAndDateRange(appointmentDTO.getIdForm(),
					appointmentDTO.getSlot().getStartingDateTime(), appointmentDTO.getSlot().getEndingDateTime());
			if (!mapSlot.isEmpty()) {
				slot = mapSlot.get(appointmentDTO.getSlot().getStartingDateTime());
			} else {
				slot = appointmentDTO.getSlot();
			}
		}
		if (appointmentDTO.getNbBookedSeats() > slot.getNbRemainingPlaces()) {
			addError(ERROR_MESSAGE_SLOT_FULL, getLocale());
			return redirect(request, VIEW_CALENDAR_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM, appointmentDTO.getIdForm());
		}
		AppointmentService.saveAppointment(appointmentDTO);
		request.getSession().removeAttribute(SESSION_VALIDATED_APPOINTMENT);
		addInfo(INFO_APPOINTMENT_CREATED, getLocale());
		AppointmentAsynchronousUploadHandler.getHandler().removeSessionFiles(request.getSession().getId());
		Map<String, String> additionalParameters = new HashMap<>();
		additionalParameters.put(PARAMETER_ID_FORM, Integer.toString(form.getIdForm()));
		additionalParameters.put(PARAMETER_DATE_OF_DISPLAY, slot.getDate().toString());
		return redirect(request, VIEW_CALENDAR_MANAGE_APPOINTMENTS, additionalParameters);
	}

	/**
	 * Get an integer attribute from the session
	 * 
	 * @param session
	 *            The session
	 * @param strSessionKey
	 *            The session key of the item
	 * @return The value of the attribute, or 0 if the key is not associated
	 *         with any value
	 */
	private int getIntSessionAttribute(HttpSession session, String strSessionKey) {
		Integer nAttr = (Integer) session.getAttribute(strSessionKey);
		if (nAttr != null) {
			return nAttr;
		}
		return 0;
	}

	/**
	 * Status of appointments that have not been validated yet, validate or
	 * rejected
	 */
	public enum ExportFilter {
		DAY_ONLY(STATUS_CODE_ZERO, "appointment.manage_appointments.daytitle"), FROM_NOWDAY(STATUS_CODE_ONE,
				"appointment.manage_appointments.lighttitle"), ALL_DAYS(STATUS_CODE_TWO,
						"appointment.manage_appointments.fulltitle");

		private final int _nValue;
		private final String _strLibelle;

		ExportFilter(int nValeur, String strMessage) {
			this._nValue = nValeur;
			this._strLibelle = strMessage;
		}

		public int getValeur() {
			return this._nValue;
		}

		public String getLibelle() {
			return this._strLibelle;
		}
	}

	/**
	 * Default constructor
	 */
	public AppointmentJspBean() {
		_nDefaultItemsPerPage = AppPropertiesService.getPropertyInt(PROPERTY_DEFAULT_LIST_APPOINTMENT_PER_PAGE, 10);
	}

	private void cleanSession(HttpSession session) {
		session.removeAttribute(SESSION_APPOINTMENT_FILTER);
		session.removeAttribute(SESSION_LIST_APPOINTMENTS);
		session.removeAttribute(SESSION_CURRENT_PAGE_INDEX);
		session.removeAttribute(SESSION_CURRENT_PAGE_INDEX);
		session.removeAttribute(SESSION_NOT_VALIDATED_APPOINTMENT);
		session.removeAttribute(SESSION_VALIDATED_APPOINTMENT);
		session.removeAttribute(SESSION_APPOINTMENT_FORM_ERRORS);
		AppointmentAsynchronousUploadHandler.getHandler().removeSessionFiles(session.getId());
	}

	/**
	 * Clear uploaded files if needed.
	 * 
	 * @param session
	 *            The session of the current user
	 */
	private void clearUploadFilesIfNeeded(HttpSession session) {
		// If we do not reload an appointment, we clear uploaded files.
		if (session.getAttribute(SESSION_NOT_VALIDATED_APPOINTMENT) == null
				&& session.getAttribute(SESSION_VALIDATED_APPOINTMENT) == null) {
			AppointmentAsynchronousUploadHandler.getHandler().removeSessionFiles(session.getId());
		}
	}

}
