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

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.message.FormMessage;
import fr.paris.lutece.plugins.appointment.business.message.FormMessageHome;
import fr.paris.lutece.plugins.appointment.business.template.CalendarTemplateHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.service.EntryService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.ReservationRuleService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
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
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage AppointmentForm features (
 * manage, create, modify, copy, remove )
 * 
 * @author L.Payen
 * 
 */
@Controller(controllerJsp = "ManageAppointmentForms.jsp", controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM)
public class AppointmentFormJspBean extends MVCAdminJspBean {

	/**
	 * Right to manage appointment forms
	 */
	public static final String RIGHT_MANAGEAPPOINTMENTFORM = "APPOINTMENT_FORM_MANAGEMENT";
	private static final long serialVersionUID = -615061018633136997L;
	private static final CaptchaSecurityService _captchaSecurityService = new CaptchaSecurityService();
	private final EntryService _entryService = EntryService.getService();
	private int _nDefaultItemsPerPage;

	// templates
	private static final String TEMPLATE_MANAGE_APPOINTMENTFORMS = "/admin/plugins/appointment/appointmentform/manage_appointmentforms.html";
	private static final String TEMPLATE_CREATE_APPOINTMENTFORM = "/admin/plugins/appointment/appointmentform/create_appointmentform.html";
	private static final String TEMPLATE_MODIFY_APPOINTMENTFORM = "/admin/plugins/appointment/appointmentform/modify_appointmentform.html";
	private static final String TEMPLATE_MODIFY_APPOINTMENTFORM_MESSAGES = "/admin/plugins/appointment/appointmentform/modify_appointmentform_messages.html";
	private static final String TEMPLATE_ADVANCED_MODIFY_APPOINTMENTFORM = "/admin/plugins/appointment/appointmentform/modify_advanced_appointmentform.html";
	private static final String TEMPLATE_MODIFY_APPOINTMENT_FORM = "/admin/plugins/appointment/appointmentform/modify_form_appointmentform.html";

	// Parameters
	private static final String PARAMETER_ID_FORM = "id_form";
	private static final String PARAMETER_ID_RESERVATION_RULE = "id_reservation_rule";
	private static final String PARAMETER_BACK = "back";
	private static final String PARAMETER_PAGE_INDEX = "page_index";
	private static final String PARAMETER_FROM_DASHBOARD = "fromDashboard";
	private static final String PARAMETER_FORCE_RELOAD = "forceReload";
	private static final String PARAMETER_ICON_RESSOURCE = "image_resource";
	private static final String PARAMETER_NAME_FORM = "formName";
	private static final String PARAMETER_DELETE_ICON = "deleteIcon";
	private static final String PARAMETER_FIRST_FORM = "first_form";
	private static final String PARAMETER_SECOND_FORM = "second_form";
	private static final String PARAMETER_FORM_RDV = "form_rdv";

	// Properties for page titles
	private static final String PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTFORMS = "appointment.manage_appointmentforms.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_GENERAL_SETTINGS = "appointment.modify_appointmentForm.titleAlterablesParameters";
	private static final String PROPERTY_PAGE_TITLE_ADVANCED_SETTINGS = "appointment.modify_appointmentForm.titleStructuralsParameters";
	private static final String PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENT_FORM = "appointment.modify_appointmentform.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_CREATE_APPOINTMENTFORM = "appointment.create_appointmentform.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENTFORM_MESSAGES = "appointment.modify_appointmentform_messages.pageTitle";

	// Markers
	private static final String MARK_APPOINTMENTFORM_LIST = "appointmentform_list";
	private static final String MARK_APPOINTMENT_FORM = "appointmentform";
	private static final String MARK_PAGINATOR = "paginator";
	private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
	private static final String MARK_LIST_WORKFLOWS = "listWorkflows";
	private static final String MARK_LIST_DATE_OF_MODIFICATION = "listDateOfModification";
	private static final String MARK_IS_CAPTCHA_ENABLED = "isCaptchaEnabled";
	private static final String MARK_FORM_MESSAGE = "formMessage";
	private static final String MARK_WEBAPP_URL = "webapp_url";
	private static final String MARK_LOCALE = "language";
	private static final String MARK_LOCALE_TINY = "locale";
	private static final String MARK_APPOINTMENT_RESOURCE_ENABLED = "isResourceInstalled";
	private static final String MARK_REF_LIST_CALENDAR_TEMPLATES = "refListCalendarTemplates";
	private static final String MARK_NULL = "NULL";
	private static final String MARK_PAGE = "page";
	private static final String MARK_FALSE = "false";

	// Jsp
	private static final String JSP_MANAGE_APPOINTMENTFORMS = "jsp/admin/plugins/appointment/ManageAppointmentForms.jsp";

	// Properties
	private static final String MESSAGE_CONFIRM_REMOVE_APPOINTMENTFORM = "appointment.message.confirmRemoveAppointmentForm";
	public static final String PROPERTY_DEFAULT_LIST_APPOINTMENTFORM_PER_PAGE = "appointment.listAppointmentForms.itemsPerPage";
	private static final String VALIDATION_ATTRIBUTES_PREFIX = "appointment.model.entity.appointmentform.attribute.";
	private static final String ERROR_MESSAGE_TIME_START_AFTER_TIME_END = "appointment.message.error.timeStartAfterTimeEnd";
	private static final String ERROR_MESSAGE_TIME_START_AFTER_DATE_END = "appointment.message.error.dateStartAfterTimeEnd";
	private static final String ERROR_MESSAGE_NO_WORKING_DAY_CHECKED = "appointment.message.error.noWorkingDayChecked";
	private static final String PROPERTY_MODULE_APPOINTMENT_RESOURCE_NAME = "appointment.moduleAppointmentResource.name";
	private static final String ERROR_MESSAGE_APPOINTMENT_SUPERIOR_MIDDLE = "appointment.message.error.formatDaysBeforeAppointmentMiddleSuperior";
	private static final String MESSAGE_ERROR_DAY_DURATION_APPOINTMENT_NOT_MULTIPLE_FORM = "appointment.message.error.durationAppointmentDayNotMultipleForm";
	private static final String MESSAGE_ERROR_MODIFY_FORM_HAS_APPOINTMENTS = "appointment.message.error.refreshDays.modifyFormHasAppointments";
	private static final String MESSAGE_ERROR_START_DATE_EMPTY = "appointment.message.error.startDateEmpty";
	private static final String PROPERTY_COPY_OF_FORM = "appointment.manage_appointmentforms.Copy";
	private static final String MESSAGE_ERROR_NUMBER_OF_SEATS_BOOKED = "appointment.message.error.numberOfSeatsBookedAndConcurrentAppointments";

	// Views
	private static final String VIEW_MANAGE_APPOINTMENTFORMS = "manageAppointmentForms";
	private static final String VIEW_CREATE_APPOINTMENTFORM = "createAppointmentForm";
	private static final String VIEW_MODIFY_APPOINTMENTFORM = "modifyAppointmentForm";
	private static final String VIEW_ADVANCED_MODIFY_APPOINTMENTFORM = "modifyAppointmentFormAdvanced";
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

	// Session variable to store working values
	private static final String SESSION_ATTRIBUTE_APPOINTMENT_FORM = "appointment.session.appointmentForm";
	private static final String SESSION_CURRENT_PAGE_INDEX = "appointment.session.appointmentForm.currentPageIndex";
	private static final String SESSION_ITEMS_PER_PAGE = "appointment.session.appointmentForm.itemsPerPage";
	private static final String DEFAULT_CURRENT_PAGE = "1";

	/**
	 * Default constructor
	 */
	public AppointmentFormJspBean() {
		_nDefaultItemsPerPage = AppPropertiesService.getPropertyInt(PROPERTY_DEFAULT_LIST_APPOINTMENTFORM_PER_PAGE, 50);
	}

	/**
	 * Get the page to manage appointment forms
	 * 
	 * @param request
	 *            the request
	 * @return The HTML content to display
	 */
	@View(value = VIEW_MANAGE_APPOINTMENTFORMS, defaultView = true)
	public String getManageAppointmentForms(HttpServletRequest request) {
		String strCurrentPageIndex = Paginator.getPageIndex(request, Paginator.PARAMETER_PAGE_INDEX,
				(String) request.getSession().getAttribute(SESSION_CURRENT_PAGE_INDEX));
		if (strCurrentPageIndex == null) {
			strCurrentPageIndex = DEFAULT_CURRENT_PAGE;
		}
		request.getSession().setAttribute(SESSION_CURRENT_PAGE_INDEX, strCurrentPageIndex);
		int nItemsPerPage = Paginator.getItemsPerPage(request, Paginator.PARAMETER_ITEMS_PER_PAGE,
				getIntSessionAttribute(request.getSession(), SESSION_ITEMS_PER_PAGE), _nDefaultItemsPerPage);
		request.getSession().setAttribute(SESSION_ITEMS_PER_PAGE, nItemsPerPage);
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM);
		UrlItem url = new UrlItem(JSP_MANAGE_APPOINTMENTFORMS);
		String strUrl = url.getUrl();
		List<AppointmentForm> listAppointmentForm = FormService.buildAllAppointmentFormLight();
		LocalizedPaginator<AppointmentForm> paginator = new LocalizedPaginator<AppointmentForm>(listAppointmentForm,
				nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX, strCurrentPageIndex, getLocale());
		Map<String, Object> model = getModel();
		model.put(MARK_NB_ITEMS_PER_PAGE, Integer.toString(nItemsPerPage));
		model.put(MARK_PAGINATOR, paginator);
		model.put(MARK_APPOINTMENTFORM_LIST, RBACService.getAuthorizedCollection(paginator.getPageItems(),
				AppointmentResourceIdService.PERMISSION_VIEW_FORM, AdminUserService.getAdminUser(request)));
		model.put(VIEW_PERMISSIONS_FORM,
				getPermissions(paginator.getPageItems(), AdminUserService.getAdminUser(request)));
		return getPage(PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTFORMS, TEMPLATE_MANAGE_APPOINTMENTFORMS, model);
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
	@View(VIEW_CREATE_APPOINTMENTFORM)
	public String getCreateAppointmentForm(HttpServletRequest request) throws AccessDeniedException {
		if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, "0",
				AppointmentResourceIdService.PERMISSION_CREATE_FORM, AdminUserService.getAdminUser(request))) {
			throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_CREATE_FORM);
		}
		AppointmentForm appointmentForm = (AppointmentForm) request.getSession()
				.getAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM);
		if (appointmentForm == null) {
			appointmentForm = new AppointmentForm();
		}
		Map<String, Object> model = getModel();
		model.put(MARK_LOCALE, getLocale());
		model.put(MARK_LOCALE_TINY, getLocale());
		addElementsToModelForLeftColumn(request, appointmentForm, getUser(), getLocale(), model);
		return getPage(PROPERTY_PAGE_TITLE_CREATE_APPOINTMENTFORM, TEMPLATE_CREATE_APPOINTMENTFORM, model);
	}

	/**
	 * Process the data capture form of a new appointment form
	 * 
	 * @param request
	 *            The HTTP Request
	 * @return The JSP URL of the process result
	 * @throws AccessDeniedException
	 *             If the user is not authorized to create appointment forms
	 * @throws FileNotFoundException
	 */
	@Action(ACTION_CREATE_APPOINTMENTFORM)
	public String doCreateAppointmentForm(HttpServletRequest request)
			throws AccessDeniedException, FileNotFoundException {
		if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, "0",
				AppointmentResourceIdService.PERMISSION_CREATE_FORM, AdminUserService.getAdminUser(request))) {
			throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_CREATE_FORM);
		}
		AppointmentForm appointmentForm = (AppointmentForm) request.getSession()
				.getAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM);
		if (appointmentForm == null) {
			appointmentForm = new AppointmentForm();
		}
		populate(appointmentForm, request);
		if (!validateBean(appointmentForm, VALIDATION_ATTRIBUTES_PREFIX)) {
			return redirectView(request, VIEW_CREATE_APPOINTMENTFORM);
		}
		if (!checkConstraints(appointmentForm)) {
			return redirect(request, VIEW_CREATE_APPOINTMENTFORM, PARAMETER_ID_FORM, appointmentForm.getIdForm());
		}
		appointmentForm.setIcon(buildImageResource((MultipartHttpServletRequest) request));
		FormService.createAppointmentForm(appointmentForm);
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM);
		addInfo(INFO_APPOINTMENTFORM_CREATED, getLocale());
		return redirectView(request, VIEW_MANAGE_APPOINTMENTFORMS);
	}

	/**
	 * Manages the removal form of a appointment form whose identifier is in the
	 * HTTP request
	 * 
	 * @param request
	 *            The HTTP request
	 * @return the HTML code to confirm
	 * @throws AccessDeniedException
	 *             If the user is not authorized to delete this appointment form
	 */
	@Action(ACTION_CONFIRM_REMOVE_APPOINTMENTFORM)
	public String getConfirmRemoveAppointmentForm(HttpServletRequest request) throws AccessDeniedException {
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		if (StringUtils.isEmpty(strIdForm)) {
			return redirectView(request, VIEW_MANAGE_APPOINTMENTFORMS);
		}
		if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_DELETE_FORM, AdminUserService.getAdminUser(request))) {
			throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_DELETE_FORM);
		}
		int nIdForm = Integer.parseInt(request.getParameter(PARAMETER_ID_FORM));
		UrlItem url = new UrlItem(getActionUrl(ACTION_REMOVE_APPOINTMENTFORM));
		url.addParameter(PARAMETER_ID_FORM, nIdForm);
		String strMessageUrl = AdminMessageService.getMessageUrl(request, MESSAGE_CONFIRM_REMOVE_APPOINTMENTFORM,
				url.getUrl(), AdminMessage.TYPE_CONFIRMATION);
		return redirect(request, strMessageUrl);
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
	@Action(ACTION_REMOVE_APPOINTMENTFORM)
	public String doRemoveAppointmentForm(HttpServletRequest request) throws AccessDeniedException {
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		if (StringUtils.isEmpty(strIdForm)) {
			return redirectView(request, VIEW_MANAGE_APPOINTMENTFORMS);
		}
		if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, request.getParameter(PARAMETER_ID_FORM),
				AppointmentResourceIdService.PERMISSION_DELETE_FORM, AdminUserService.getAdminUser(request))) {
			throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_DELETE_FORM);
		}
		int nIdForm = Integer.parseInt(request.getParameter(PARAMETER_ID_FORM));
		_entryService.removeEntriesByIdAppointmentForm(nIdForm);
		FormService.removeForm(nIdForm);
		addInfo(INFO_APPOINTMENTFORM_REMOVED, getLocale());
		return redirectView(request, VIEW_MANAGE_APPOINTMENTFORMS);
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
	@View(VIEW_MODIFY_APPOINTMENTFORM)
	public String getModifyAppointmentForm(HttpServletRequest request) throws AccessDeniedException {
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		if (StringUtils.isEmpty(strIdForm)) {
			return redirectView(request, VIEW_MANAGE_APPOINTMENTFORMS);
		}
		if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_MODIFY_FORM, AdminUserService.getAdminUser(request))) {
			throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_MODIFY_FORM);
		}
		int nIdForm = Integer.parseInt(request.getParameter(PARAMETER_ID_FORM));
		int nIdReservationRule = 0;
		String strIdReservationRule = request.getParameter(PARAMETER_ID_RESERVATION_RULE);
		if (StringUtils.isNotEmpty(strIdReservationRule) && StringUtils.isNumeric(strIdReservationRule)) {
			nIdReservationRule = Integer.parseInt(strIdReservationRule);
		}
		AppointmentForm appointmentForm = (AppointmentForm) request.getSession()
				.getAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM);
		if ((appointmentForm == null) || (nIdForm != appointmentForm.getIdForm())
				|| Boolean.parseBoolean(request.getParameter(PARAMETER_FORCE_RELOAD))) {
			appointmentForm = FormService.buildAppointmentForm(nIdForm, nIdReservationRule);
			request.getSession().setAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM, appointmentForm);
		}
		Map<String, Object> model = getModel();
		model.put(MARK_LOCALE, getLocale());
		model.put(MARK_LOCALE_TINY, getLocale());
		EntryService.addListEntryToModel(nIdForm, model);
		addElementsToModelForLeftColumn(request, appointmentForm, getUser(), getLocale(), model);
		String strPage = request.getParameter(MARK_PAGE);
		if (PARAMETER_FORM_RDV.equals(strPage)) {
			return getPage(PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENT_FORM, TEMPLATE_MODIFY_APPOINTMENT_FORM, model);
		} else {
			return getPage(PROPERTY_PAGE_TITLE_GENERAL_SETTINGS, TEMPLATE_MODIFY_APPOINTMENTFORM, model);
		}
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
	@View(VIEW_ADVANCED_MODIFY_APPOINTMENTFORM)
	public String getModifyAppointmentFormAdvanced(HttpServletRequest request) throws AccessDeniedException {
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		if (StringUtils.isEmpty(strIdForm)) {
			return redirectView(request, VIEW_MANAGE_APPOINTMENTFORMS);
		}
		if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM,
				AdminUserService.getAdminUser(request))) {
			throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM);
		}
		int nIdForm = Integer.parseInt(strIdForm);
		int nIdReservationRule = 0;
		String strIdReservationRule = request.getParameter(PARAMETER_ID_RESERVATION_RULE);
		if (StringUtils.isNotEmpty(strIdReservationRule) && StringUtils.isNumeric(strIdReservationRule)) {
			nIdReservationRule = Integer.parseInt(strIdReservationRule);
		}
		AppointmentForm appointmentForm = (AppointmentForm) request.getSession()
				.getAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM);
		if ((appointmentForm == null) || (nIdForm != appointmentForm.getIdForm())
				|| Boolean.parseBoolean(request.getParameter(PARAMETER_FORCE_RELOAD))) {
			appointmentForm = FormService.buildAppointmentForm(nIdForm, nIdReservationRule);
			request.getSession().setAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM, appointmentForm);
		}
		Map<String, Object> model = getModel();
		model.put(MARK_LIST_DATE_OF_MODIFICATION, ReservationRuleService.findAllDateOfReservationRule(nIdForm));
		model.put(MARK_LOCALE, getLocale());
		model.put(MARK_LOCALE_TINY, getLocale());
		EntryService.addListEntryToModel(nIdForm, model);
		addElementsToModelForLeftColumn(request, appointmentForm, getUser(), getLocale(), model);
		return getPage(PROPERTY_PAGE_TITLE_ADVANCED_SETTINGS, TEMPLATE_ADVANCED_MODIFY_APPOINTMENTFORM, model);
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
	@Action(ACTION_MODIFY_APPOINTMENTFORM)
	public String doModifyAppointmentForm(HttpServletRequest request) throws AccessDeniedException {
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		if (StringUtils.isEmpty(strIdForm)) {
			return redirectView(request, VIEW_MANAGE_APPOINTMENTFORMS);
		}
		if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_MODIFY_FORM, AdminUserService.getAdminUser(request))) {
			throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_MODIFY_FORM);
		}
		int nIdForm = Integer.parseInt(strIdForm);
		int nIdReservationRule = 0;
		String strIdReservationRule = request.getParameter(PARAMETER_ID_RESERVATION_RULE);
		if (StringUtils.isNotEmpty(strIdReservationRule) && StringUtils.isNumeric(strIdReservationRule)) {
			nIdReservationRule = Integer.parseInt(strIdReservationRule);
		}
		AppointmentForm appointmentForm = (AppointmentForm) request.getSession()
				.getAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM);
		if ((appointmentForm == null) || (nIdForm != appointmentForm.getIdForm())) {
			appointmentForm = FormService.buildAppointmentForm(nIdForm, nIdReservationRule);
		}
		String strForm = request.getParameter(PARAMETER_NAME_FORM);
		populate(appointmentForm, request);
		if (!validateBean(appointmentForm, VALIDATION_ATTRIBUTES_PREFIX)) {
			if (PARAMETER_FIRST_FORM.equals(strForm)) {
				return redirect(request, VIEW_ADVANCED_MODIFY_APPOINTMENTFORM, PARAMETER_ID_FORM,
						appointmentForm.getIdForm());
			} else {
				return redirect(request, VIEW_MODIFY_APPOINTMENTFORM, PARAMETER_ID_FORM, nIdForm);
			}
		}
		if (!checkConstraints(appointmentForm)) {
			return redirect(request, VIEW_MODIFY_APPOINTMENTFORM, PARAMETER_ID_FORM, nIdForm);
		}
		AppointmentForm appointmentFormDb = FormService.buildAppointmentForm(nIdForm, nIdReservationRule);
		if (PARAMETER_SECOND_FORM.equals(strForm)) {
			String strDeleteIcon = (request.getParameter(PARAMETER_DELETE_ICON) == null) ? MARK_FALSE
					: request.getParameter(PARAMETER_DELETE_ICON);
			if (Boolean.parseBoolean(strDeleteIcon) && (appointmentForm.getIcon().getImage() != null)) {
				ImageResource img = new ImageResource();
				img.setImage(null);
				img.setMimeType(null);
				appointmentForm.setIcon(img);
			} else {
				buildImageResource((MultipartHttpServletRequest) request);
			}
			setParametersDays(appointmentForm, appointmentFormDb);
		}
		LocalDate dateOfModification = null;
		if (PARAMETER_FIRST_FORM.equals(strForm) && !strForm.isEmpty()) {
			setAlterablesParameters(appointmentForm, appointmentFormDb);
			if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, Integer.toString(nIdForm),
					AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM, getUser())) {
				throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM);
			}
			if (appointmentForm.getDateOfModification() == null) {
				addError(MESSAGE_ERROR_START_DATE_EMPTY, getLocale());
				return redirect(request, VIEW_ADVANCED_MODIFY_APPOINTMENTFORM, PARAMETER_ID_FORM,
						appointmentForm.getIdForm());
			}
			dateOfModification = appointmentForm.getDateOfModification().toLocalDate();
			// TODO Vérifier s'il existe des rendez-vous après la date de
			// modification
		}
		appointmentForm.setIsActive(appointmentFormDb.getIsActive());
		FormService.updateAppointmentForm(appointmentForm, dateOfModification);
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM);
		addInfo(INFO_APPOINTMENTFORM_UPDATED, getLocale());
		return redirectView(request, VIEW_MANAGE_APPOINTMENTFORMS);
	}

	/**
	 * Change the enabling of an appointment form
	 * 
	 * @param request
	 *            The request
	 * @return The next URL to redirect to
	 * @throws AccessDeniedException
	 *             If the user is not authorized to change the activation of
	 *             this appointment form
	 */
	@Action(ACTION_DO_CHANGE_FORM_ACTIVATION)
	public String doChangeFormActivation(HttpServletRequest request) throws AccessDeniedException {
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		if (StringUtils.isEmpty(strIdForm)) {
			return redirectView(request, VIEW_MANAGE_APPOINTMENTFORMS);
		}
		if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_CHANGE_STATE, AdminUserService.getAdminUser(request))) {
			throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_CHANGE_STATE);
		}
		int nIdForm = Integer.parseInt(strIdForm);
		Form form = FormHome.findByPrimaryKey(nIdForm);
		if (form != null) {
			if (!form.isActive()) {
				if ((form.getEndingValidityDate() != null)
						&& (form.getEndingValidityDate().isBefore(LocalDate.now()))) {
					form.setEndingValidityDate(null);
				}
			} else {
				if ((form.getStartingValidityDate() != null)
						&& (form.getStartingValidityDate().isBefore(LocalDate.now()))) {
					form.setStartingValiditySqlDate(null);
				}
			}
			form.setIsActive(!form.isActive());
			FormHome.update(form);
		}
		if (Boolean.valueOf(request.getParameter(PARAMETER_FROM_DASHBOARD))) {
			return redirect(request, AppPathService.getBaseUrl(request) + AppPathService.getAdminMenuUrl());
		}
		return redirectView(request, VIEW_MANAGE_APPOINTMENTFORMS);
	}

	@Action(ACTION_DO_COPY_FORM)
	public String doCopyAppointmentForm(HttpServletRequest request) {
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		if (StringUtils.isEmpty(strIdForm)) {
			return redirectView(request, VIEW_MANAGE_APPOINTMENTFORMS);
		}
		int nIdForm = Integer.parseInt(strIdForm);
		Form formToCopy = FormService.findFormByPrimaryKey(nIdForm);
		if (formToCopy != null) {
			String newNameForCopy = I18nService.getLocalizedString(PROPERTY_COPY_OF_FORM, request.getLocale())
					+ formToCopy.getTitle();
			FormService.copyForm(nIdForm, newNameForCopy);
			EntryFilter filter = new EntryFilter();
			filter.setIdResource(nIdForm);
			List<Entry> listEntry = EntryHome.getEntryList(filter);
			List<Field> listField = null;
			if (listEntry != null) {
				for (Entry entry : listEntry) {
					entry.setIdResource(nIdForm);
					int oldEntry = entry.getIdEntry();
					EntryHome.create(entry);
					listField = FieldHome.getFieldListByIdEntry(oldEntry);
					if (listField != null) {
						for (Field field : listField) {
							field.setParentEntry(entry);
							FieldHome.create(field);
						}
					}
				}
			}
		}
		return getManageAppointmentForms(request);
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
	@View(VIEW_MODIFY_FORM_MESSAGES)
	public String getModifyAppointmentFormMessages(HttpServletRequest request) throws AccessDeniedException {
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		if (StringUtils.isEmpty(strIdForm)) {
			return redirectView(request, VIEW_MANAGE_APPOINTMENTFORMS);
		}
		if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
				AppointmentResourceIdService.PERMISSION_MODIFY_FORM, AdminUserService.getAdminUser(request))) {
			throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_MODIFY_FORM);
		}
		int nIdForm = Integer.parseInt(strIdForm);
		FormMessage formMessage = FormMessageHome.findByIdForm(nIdForm);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(MARK_FORM_MESSAGE, formMessage);
		model.put(MARK_WEBAPP_URL, AppPathService.getBaseUrl(request));
		model.put(MARK_LOCALE, getLocale());
		model.put(MARK_LOCALE_TINY, getLocale());
		return getPage(PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENTFORM_MESSAGES, TEMPLATE_MODIFY_APPOINTMENTFORM_MESSAGES,
				model);
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
	@Action(ACTION_DO_MODIFY_FORM_MESSAGES)
	public String doModifyAppointmentFormMessages(HttpServletRequest request) throws AccessDeniedException {
		String strIdForm = request.getParameter(PARAMETER_ID_FORM);
		if (StringUtils.isNotEmpty(strIdForm) && StringUtils.isNumeric(strIdForm)
				&& (request.getParameter(PARAMETER_BACK) == null)) {
			if (!RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE, strIdForm,
					AppointmentResourceIdService.PERMISSION_MODIFY_FORM, AdminUserService.getAdminUser(request))) {
				throw new AccessDeniedException(AppointmentResourceIdService.PERMISSION_MODIFY_FORM);
			}
			int nIdForm = Integer.parseInt(strIdForm);
			UrlItem url = new UrlItem(getViewFullUrl(VIEW_MODIFY_FORM_MESSAGES));
			url.addParameter(PARAMETER_ID_FORM, nIdForm);
			FormMessage formMessage = FormMessageHome.findByIdForm(nIdForm);
			populate(formMessage, request);
			FormMessageHome.update(formMessage);
			return redirect(request, VIEW_MODIFY_FORM_MESSAGES, PARAMETER_ID_FORM, nIdForm);
		}
		return redirectView(request, VIEW_MANAGE_APPOINTMENTFORMS);
	}

	/**
	 * Get the URL to manage appointment forms
	 * 
	 * @param request
	 *            The request
	 * @return The URL to manage appointment forms
	 */
	public static String getURLManageAppointmentForms(HttpServletRequest request) {
		UrlItem urlItem = new UrlItem(AppPathService.getBaseUrl(request) + JSP_MANAGE_APPOINTMENTFORMS);
		urlItem.addParameter(MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_APPOINTMENTFORMS);
		return urlItem.getUrl();
	}

	/**
	 * Add elements to the model to display the left column to modify an
	 * appointment form
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
	public static void addElementsToModelForLeftColumn(HttpServletRequest request, AppointmentForm appointmentForm,
			AdminUser user, Locale locale, Map<String, Object> model) {
		model.put(MARK_APPOINTMENT_FORM, appointmentForm);
		model.put(MARK_LIST_WORKFLOWS, WorkflowService.getInstance().getWorkflowsEnabled(user, locale));
		model.put(MARK_IS_CAPTCHA_ENABLED, _captchaSecurityService.isAvailable());
		model.put(MARK_REF_LIST_CALENDAR_TEMPLATES, CalendarTemplateHome.findAllInReferenceList());
		Plugin pluginAppointmentResource = PluginService
				.getPlugin(AppPropertiesService.getProperty(PROPERTY_MODULE_APPOINTMENT_RESOURCE_NAME));
		model.put(MARK_APPOINTMENT_RESOURCE_ENABLED,
				(pluginAppointmentResource != null) && pluginAppointmentResource.isInstalled());
		request.getSession().setAttribute(SESSION_ATTRIBUTE_APPOINTMENT_FORM, appointmentForm);
	}

	/**
	 * Get Form Permissions
	 * 
	 * @param listForms
	 *            the list form
	 * @param user
	 *            the user
	 * @return Form Permissions
	 */
	private static String[][] getPermissions(List<AppointmentForm> listForms, AdminUser user) {
		String[][] retour = new String[listForms.size()][4];
		int nI = 0;
		for (AppointmentForm tmpForm : listForms) {
			String[] strRetour = new String[4];
			strRetour[0] = String.valueOf(RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE,
					String.valueOf(tmpForm.getIdForm()), AppointmentResourceIdService.PERMISSION_CREATE_FORM, user));
			strRetour[1] = String.valueOf(RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE,
					String.valueOf(tmpForm.getIdForm()), AppointmentResourceIdService.PERMISSION_CHANGE_STATE, user));
			strRetour[2] = String.valueOf(RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE,
					String.valueOf(tmpForm.getIdForm()), AppointmentResourceIdService.PERMISSION_MODIFY_FORM, user));
			strRetour[3] = String.valueOf(RBACService.isAuthorized(AppointmentForm.RESOURCE_TYPE,
					String.valueOf(tmpForm.getIdForm()), AppointmentResourceIdService.PERMISSION_DELETE_FORM, user));
			retour[nI++] = strRetour;
		}
		return retour;
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
	 * Build an image resource (icon)
	 * 
	 * @param mRequest
	 *            the request
	 * @return the image resource
	 */
	public static ImageResource buildImageResource(MultipartHttpServletRequest mRequest) {
		ImageResource img = new ImageResource();
		byte[] bytes = new byte[] {};
		String strMimeType = MARK_NULL;
		FileItem item = mRequest.getFile(PARAMETER_ICON_RESSOURCE);
		if ((item != null) && !StringUtils.isEmpty(item.getName())) {
			bytes = item.get();
			strMimeType = item.getContentType();
		}
		img.setImage(bytes);
		img.setMimeType(strMimeType);
		return img;
	}

	/**
	 * 
	 * @param appointmentForm
	 * @param appointmentFormTmp
	 */
	private void setParametersDays(AppointmentForm appointmentForm, AppointmentForm appointmentFormTmp) {
		appointmentForm.setIsOpenMonday(appointmentFormTmp.getIsOpenMonday());
		appointmentForm.setIsOpenTuesday(appointmentFormTmp.getIsOpenTuesday());
		appointmentForm.setIsOpenWednesday(appointmentFormTmp.getIsOpenWednesday());
		appointmentForm.setIsOpenThursday(appointmentFormTmp.getIsOpenThursday());
		appointmentForm.setIsOpenFriday(appointmentFormTmp.getIsOpenFriday());
		appointmentForm.setIsOpenSaturday(appointmentFormTmp.getIsOpenSaturday());
		appointmentForm.setIsOpenSunday(appointmentFormTmp.getIsOpenSunday());
	}

	/**
	 * 
	 * @param appointmentForm
	 * @param appointmentFormTmp
	 */
	private void setAlterablesParameters(AppointmentForm appointmentForm, AppointmentForm appointmentFormTmp) {
		appointmentForm.setDisplayTitleFo(appointmentFormTmp.getDisplayTitleFo());
		appointmentForm.setEnableCaptcha(appointmentFormTmp.getEnableCaptcha());
		appointmentForm.setEnableMandatoryEmail(appointmentFormTmp.getEnableMandatoryEmail());
	}

	/**
	 * Check Constraints
	 * 
	 * @param appointmentForm
	 * @return
	 * @throws ParseException
	 */
	private boolean checkConstraints(AppointmentForm appointmentForm) {
		return checkStartingAndEndingTime(appointmentForm) && checkStartingAndEndingValidityDate(appointmentForm)
				&& checkSlotCapacityAndPeoplePerAppointment(appointmentForm)
				&& checkAtLeastOneWorkingDayOpen(appointmentForm);
	}

	private boolean checkAtLeastOneWorkingDayOpen(AppointmentForm appointmentForm) {
		boolean bReturn = true;
		if (! (appointmentForm.getIsOpenMonday() || appointmentForm.getIsOpenTuesday()
				|| appointmentForm.getIsOpenWednesday() || appointmentForm.getIsOpenThursday()
				|| appointmentForm.getIsOpenFriday() || appointmentForm.getIsOpenSaturday()
				|| appointmentForm.getIsOpenSunday())) {
			bReturn = false;
			addError(ERROR_MESSAGE_NO_WORKING_DAY_CHECKED, getLocale());
		}
		return bReturn;
	}

	/**
	 * 
	 * @param appointmentForm
	 * @return
	 */
	private boolean checkStartingAndEndingTime(AppointmentForm appointmentForm) {
		boolean bReturn = true;
		LocalTime startingTime = LocalTime.parse(appointmentForm.getTimeStart());
		LocalTime endingTime = LocalTime.parse(appointmentForm.getTimeEnd());
		if (startingTime.isAfter(endingTime)) {
			bReturn = false;
			addError(ERROR_MESSAGE_TIME_START_AFTER_TIME_END, getLocale());
		}
		long lMinutes = startingTime.until(endingTime, ChronoUnit.MINUTES);
		if (appointmentForm.getDurationAppointments() > lMinutes) {
			bReturn = false;
			addError(ERROR_MESSAGE_APPOINTMENT_SUPERIOR_MIDDLE, getLocale());
		}
		if ((lMinutes % appointmentForm.getDurationAppointments()) != 0) {
			bReturn = false;
			addError(MESSAGE_ERROR_DAY_DURATION_APPOINTMENT_NOT_MULTIPLE_FORM, getLocale());
		}
		return bReturn;
	}

	/**
	 * 
	 * @param appointmentForm
	 * @return
	 */
	private boolean checkStartingAndEndingValidityDate(AppointmentForm appointmentForm) {
		boolean bReturn = true;
		if ((appointmentForm.getDateStartValidity() != null) && (appointmentForm.getDateEndValidity() != null)) {
			if (appointmentForm.getDateStartValidity().toLocalDate()
					.isAfter(appointmentForm.getDateEndValidity().toLocalDate())) {
				bReturn = false;
				addError(ERROR_MESSAGE_TIME_START_AFTER_DATE_END, getLocale());
			}
		}
		return bReturn;
	}

	/**
	 * 
	 * @param appointmentForm
	 * @return
	 */
	private boolean checkSlotCapacityAndPeoplePerAppointment(AppointmentForm appointmentForm) {
		boolean bReturn = true;
		if (appointmentForm.getMaxPeoplePerAppointment() > appointmentForm.getMaxCapacityPerSlot()) {
			bReturn = false;
			addError(MESSAGE_ERROR_NUMBER_OF_SEATS_BOOKED, getLocale());
		}
		return bReturn;
	}
}
