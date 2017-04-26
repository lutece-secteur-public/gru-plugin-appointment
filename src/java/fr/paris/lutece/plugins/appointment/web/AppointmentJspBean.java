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

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

import fr.paris.lutece.plugins.appointment.business.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.business.AppointmentFilter;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
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
import fr.paris.lutece.plugins.appointment.service.Utilities;
import fr.paris.lutece.plugins.appointment.service.WeekDefinitionService;
import fr.paris.lutece.plugins.appointment.service.addon.AppointmentAddOnManager;
import fr.paris.lutece.plugins.appointment.service.upload.AppointmentAsynchronousUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
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
	private static final String TEMPLATE_APPOINTMENT_FORM_RECAP = "/admin/plugins/appointment/appointment/appointment_form_recap.html";
	private static final String TEMPLATE_TASKS_FORM_WORKFLOW = "admin/plugins/appointment/appointment/tasks_form_workflow.html";

	// Properties for page titles
	private static final String PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS = "appointment.manage_appointments.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS_CALENDAR = "appointment.manage_appointment_calendar.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_CREATE_APPOINTMENT = "appointment.create_appointment.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_VIEW_APPOINTMENT = "appointment.view_appointment.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_RECAP_APPOINTMENT = "appointment.appointmentApp.recap.title";
	private static final String PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW = "appointment.taskFormWorkflow.pageTitle";

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
	private static final String PARAMETER_NUMBER_OF_BOOKED_SEATS = "numberPlacesReserved";

	// Markers
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
	private static final String MARK_FILTER = "filter";
	private static final String MARK_RESOURCE_HISTORY = "resource_history";
	private static final String MARK_ADDON = "addon";
	private static final String MARK_LIST_RESPONSE_RECAP_DTO = "listResponseRecapDTO";
	private static final String MARK_LANGUAGE = "language";
	private static final String MARK_ACTIVATE_WORKFLOW = "activateWorkflow";

	private static final String JSP_MANAGE_APPOINTMENTS = "jsp/admin/plugins/appointment/ManageAppointments.jsp";
	private static final String ERROR_MESSAGE_SLOT_FULL = "appointment.message.error.slotFull";

	// Messages
	private static final String MESSAGE_CONFIRM_REMOVE_APPOINTMENT = "appointment.message.confirmRemoveAppointment";
	private static final String MESSAGE_CONFIRM_REMOVE_MASSAPPOINTMENT = "appointment.message.confirmRemoveMassAppointment";

	// Properties
	private static final String PROPERTY_DEFAULT_LIST_APPOINTMENT_PER_PAGE = "appointment.listAppointments.itemsPerPage";

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
	public static final String ACTIVATEWORKFLOW = AppPropertiesService.getProperty("appointment.activate.workflow");
	public static final String PREVIOUS_FORM = "calendar";
	private static final String LAST_NAME = "last_name";
	private static final String FIRST_NAME = "first_name";
	private static final String EMAIL = "email";
	private static final String DATE_APPOINTMENT = "date_appointment";
	private static final String STATUS = "status";

	// services
	private final StateService _stateService = SpringContextService.getBean(StateService.BEAN_SERVICE);

	// Session variable to store working values
	private int _nDefaultItemsPerPage;

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
		String strIdAppointment = request.getParameter(PARAMETER_ID_APPOINTMENT);
		AppointmentDTO appointmentDTO = null;
		if (StringUtils.isNotEmpty(strIdAppointment)) {
			// If we want to change the date of an appointment
			int nIdAppointment = Integer.parseInt(strIdAppointment);
			appointmentDTO = AppointmentService.buildAppointmentDTOFromIdAppointment(nIdAppointment);
		}
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
				nNbWeeksToDisplay = Math.toIntExact(startingDateOfDisplay.until(endingDateOfDisplay, ChronoUnit.WEEKS));
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
		// If we change the date of an appointment
		// filter the list of slot with only the ones that have enough places
		if (appointmentDTO != null) {
			int nbBookedSeats = appointmentDTO.getNbBookedSeats();
			listSlot = listSlot.stream().filter(s -> s.getNbRemainingPlaces() >= nbBookedSeats && s.getIsOpen())
					.collect(Collectors.toList());
			request.getSession().setAttribute(SESSION_VALIDATED_APPOINTMENT, appointmentDTO);
			model.put(MARK_MODIFICATION_DATE_APPOINTMENT, Boolean.TRUE.toString());
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
				filter.setStartingTimeOfSearch(startingDateTime.toLocalTime().toString());
				filter.setEndingDateOfSearch(Date.valueOf(endingDateTime.toLocalDate()));
				filter.setEndingTimeOfSearch(endingDateTime.toLocalTime().toString());
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
		orderList(listAppointmentsDTO, strOrderBy, strOrderAsc);
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
		model.put(MARK_LIST_RESPONSE_RECAP_DTO,
				AppointmentUtilities.buildListResponse(appointmentDTO, request, locale));
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
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		if (StringUtils.isEmpty(strIdForm) || !StringUtils.isNumeric(strIdForm)) {
			return redirect(request, AppointmentFormJspBean.getURLManageAppointmentForms(request));
		}
		if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, getUser())) {
			throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT);
		}
		Locale locale = getLocale();
		List<AppointmentDTO> listAppointmentsDTO = (List<AppointmentDTO>) request.getSession()
				.getAttribute(SESSION_LIST_APPOINTMENTS);
		AppointmentUtilities.buildExcelFileWithAppointments(strIdForm, response, locale, listAppointmentsDTO,
				_stateService);
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
		session.setAttribute(SESSION_NOT_VALIDATED_APPOINTMENT, appointmentDTO);
		Slot slot = appointmentDTO.getSlot();
		int nIdForm = slot.getIdForm();
		LocalDate dateOfSlot = slot.getDate();
		ReservationRule reservationRule = ReservationRuleService
				.findReservationRuleByIdFormAndClosestToDateOfApply(nIdForm, dateOfSlot);
		WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply(nIdForm,
				dateOfSlot);
		AppointmentForm form = FormService.buildAppointmentForm(nIdForm, reservationRule.getIdReservationRule(),
				weekDefinition.getIdWeekDefinition());
		request.getSession().setAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM, form);
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
			if (appointmentDTO != null) {
				request.getSession().removeAttribute(SESSION_VALIDATED_APPOINTMENT);
				request.getSession().setAttribute(SESSION_NOT_VALIDATED_APPOINTMENT, appointmentDTO);
			} else {
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
		int nbBookedSeats = AppointmentUtilities.checkAndReturnNbBookedSeats(
				request.getParameter(PARAMETER_NUMBER_OF_BOOKED_SEATS), form, appointmentDTO, locale, listFormErrors);
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
	 * Return to the display recap view withthe new date slected on the calendar
	 * 
	 * @param request
	 *            the request
	 * @return to the display recap view
	 * @throws AccessDeniedException
	 * @throws SiteMessageException
	 */
	@View(VIEW_CHANGE_DATE_APPOINTMENT)
	public String getViewChangeDateAppointment(HttpServletRequest request)
			throws AccessDeniedException, SiteMessageException {
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		int nIdForm = Integer.parseInt(strIdForm);
		if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_CREATE_APPOINTMENT, getUser())) {
			throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_MODIFY_APPOINTMENT);
		}
		AppointmentDTO appointmentDTO = (AppointmentDTO) request.getSession()
				.getAttribute(SESSION_VALIDATED_APPOINTMENT);
		int nIdSlot = Integer.parseInt(request.getParameter(PARAMETER_ID_SLOT));
		Slot slot = null;
		// If nIdSlot == 0, the slot has not been created yet
		if (nIdSlot == 0) {
			// Need to get all the informations to create the slot
			LocalDateTime startingDateTime = LocalDateTime.parse(request.getParameter(PARAMETER_STARTING_DATE_TIME));
			LocalDateTime endingDateTime = LocalDateTime.parse(request.getParameter(PARAMETER_ENDING_DATE_TIME));
			boolean bIsOpen = Boolean.parseBoolean(request.getParameter(PARAMETER_IS_OPEN));
			int nMaxCapacity = Integer.parseInt(request.getParameter(PARAMETER_MAX_CAPACITY));
			slot = SlotService.buildSlot(nIdForm, startingDateTime, endingDateTime, nMaxCapacity, nMaxCapacity,
					bIsOpen);
		} else {
			slot = SlotService.findSlotById(nIdSlot);
		}
		appointmentDTO.setSlot(slot);
		appointmentDTO.setDateOfTheAppointment(slot.getStartingDateTime().toLocalDate().format(Utilities.formatter));
		request.getSession().setAttribute(SESSION_VALIDATED_APPOINTMENT, appointmentDTO);
		LocalDate dateOfSlot = slot.getDate();
		ReservationRule reservationRule = ReservationRuleService
				.findReservationRuleByIdFormAndClosestToDateOfApply(nIdForm, dateOfSlot);
		WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply(nIdForm,
				dateOfSlot);
		AppointmentForm form = FormService.buildAppointmentForm(nIdForm, reservationRule.getIdReservationRule(),
				weekDefinition.getIdWeekDefinition());
		request.getSession().setAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM, form);
		Map<String, String> additionalParameters = new HashMap<>();
		additionalParameters.put(PARAMETER_ID_FORM, Integer.toString(form.getIdForm()));
		additionalParameters.put(PARAMETER_COME_FROM_CALENDAR, Boolean.TRUE.toString());
		return redirect(request, VIEW_DISPLAY_RECAP_APPOINTMENT, additionalParameters);
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
		String strComeFromCalendar = request.getParameter(PARAMETER_COME_FROM_CALENDAR);
		if (StringUtils.isNotEmpty(strComeFromCalendar)) {
			model.put(PARAMETER_COME_FROM_CALENDAR, strComeFromCalendar);
			model.put(PARAMETER_DATE_OF_DISPLAY, appointmentDTO.getSlot().getDate());
		}
		model.put(MARK_FORM_MESSAGES, FormMessageService.findFormMessageByIdForm(appointmentDTO.getIdForm()));
		fillCommons(model);
		model.put(MARK_APPOINTMENT, appointmentDTO);
		Locale locale = getLocale();
		model.put(MARK_ADDON,
				AppointmentAddOnManager.getAppointmentAddOn(appointmentDTO.getIdAppointment(), getLocale()));
		model.put(MARK_LIST_RESPONSE_RECAP_DTO,
				AppointmentUtilities.buildListResponse(appointmentDTO, request, locale));
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
		SlotService.addDateAndTimeToSlot(slot);
		// If it's a modification need to get the old number of booked seats
		// if the reservation is on the same slot, if not, the check has been
		// already done before
		if (appointmentDTO.getIdAppointment() != 0) {
			Appointment oldAppointment = AppointmentService.findAppointmentById(appointmentDTO.getIdAppointment());
			if (oldAppointment.getIdSlot() == appointmentDTO.getSlot().getIdSlot() && appointmentDTO
					.getNbBookedSeats() > (slot.getNbRemainingPlaces() + oldAppointment.getNbPlaces())) {
				addError(ERROR_MESSAGE_SLOT_FULL, getLocale());
				return redirect(request, VIEW_CALENDAR_MANAGE_APPOINTMENTS, PARAMETER_ID_FORM,
						appointmentDTO.getIdForm());
			}
		} else if (appointmentDTO.getNbBookedSeats() > slot.getNbRemainingPlaces()) {
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

	/**
	 * Order the list of the appointment in the result tab with the order by and
	 * order asc given
	 * 
	 * @param listAppointmentsDTO
	 *            the llist of appointments
	 * @param strOrderBy
	 *            the order by
	 * @param strOrderAsc
	 *            the order asc
	 */
	private void orderList(List<AppointmentDTO> listAppointmentsDTO, String strOrderBy, String strOrderAsc) {
		if (strOrderBy != null && strOrderAsc != null) {
			boolean bAsc = Boolean.parseBoolean(strOrderAsc);
			Stream<AppointmentDTO> stream = null;
			switch (strOrderBy) {
			case LAST_NAME:
				stream = listAppointmentsDTO.stream().sorted((a1, a2) -> a1.getLastName().compareTo(a2.getLastName()));
				break;
			case FIRST_NAME:
				stream = listAppointmentsDTO.stream()
						.sorted((a1, a2) -> a1.getFirstName().compareTo(a2.getFirstName()));
				break;
			case EMAIL:
				stream = listAppointmentsDTO.stream().sorted((a1, a2) -> a1.getEmail().compareTo(a2.getEmail()));
				break;
			case DATE_APPOINTMENT:
				stream = listAppointmentsDTO.stream()
						.sorted((a1, a2) -> a1.getStartingDateTime().compareTo(a2.getStartingDateTime()));
				break;
			case STATUS:
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
	}

}
