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
package fr.paris.lutece.plugins.appointment.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFrontDTO;
import fr.paris.lutece.plugins.appointment.business.message.FormMessage;
import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeUpload;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Service for appointment forms
 */
public class AppointmentFormService implements Serializable {
	/**
	 * Name of the bean of the service
	 */
	public static final String BEAN_NAME = "appointment.appointmentFormService";

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 6197939507943704211L;
	private static final String PARAMETER_ID_FORM = "id_form";
	private static final String PREFIX_ATTRIBUTE = "attribute";

	// marks
	private static final String MARK_LOCALE = "locale";
	private static final String MARK_ENTRY = "entry";
	private static final String MARK_FIELD = "field";
	private static final String MARK_STR_LIST_CHILDREN = "str_list_entry_children";
	private static final String MARK_FORM = "form";
	private static final String MARK_FORM_MESSAGES = "form_messages";
	private static final String MARK_FORM_ERRORS = "form_errors";
	private static final String MARK_STR_ENTRY = "str_entry";
	private static final String MARK_USER = "user";
	private static final String MARK_LIST_RESPONSES = "list_responses";
	private static final String MARK_APPOINTMENT = "appointment";
	private static final String MARK_ADDON = "addon";
	private static final String MARK_IS_FORM_FIRST_STEP = "isFormFirstStep";
	private static final String MARK_UPLOAD_HANDLER = "uploadHandler";
	private static final String MARK_APPOINTMENTSLOT = "appointmentSlot";
	private static final String MARK_APPOINTMENTSLOTDAY = "appointmentSlotDay";
	private static final String MARK_WEEK = "nWeek";
	private static final String MARK_LIST_ERRORS = "listAllErrors";
	private static final String MARK_CUSTOMER_ID = "cid";
	private static final String MARK_USER_ID_OPAM = "guid";
	private static final String MARK_PLACES = "nbplaces";

	// Session keys
	private static final String SESSION_NOT_VALIDATED_APPOINTMENT = "appointment.appointmentFormService.notValidatedAppointment";
	private static final String SESSION_VALIDATED_APPOINTMENT = "appointment.appointmentFormService.validatedAppointment";
	private static final String SESSION_APPOINTMENT_FORM_ERRORS = "appointment.session.formErrors";

	// Templates
	private static final String TEMPLATE_DIV_CONDITIONAL_ENTRY = "skin/plugins/appointment/html_code_div_conditional_entry.html";
	private static final String TEMPLATE_HTML_CODE_FORM = "skin/plugins/appointment/html_code_form.html";
	private static final String TEMPLATE_HTML_CODE_FORM_ADMIN = "admin/plugins/appointment/html_code_form.html";

	// Properties

	private static final String PROPERTY_USER_ATTRIBUTE_FIRST_NAME = "appointment.userAttribute.firstName";
	private static final String PROPERTY_USER_ATTRIBUTE_LAST_NAME = "appointment.userAttribute.lastName";
	private static final String PROPERTY_USER_ATTRIBUTE_EMAIL = "appointment.userAttribute.email";
	private static final String PROPERTY_EMPTY_FIELD_FIRST_NAME = "appointment.validation.appointment.FirstName.notEmpty";
	private static final String PROPERTY_EMPTY_FIELD_LAST_NAME = "appointment.validation.appointment.LastName.notEmpty";
	private static final String PROPERTY_UNVAILABLE_EMAIL = "appointment.validation.appointment.Email.email";
	private static final String PROPERTY_MESSAGE_EMPTY_EMAIL = "appointment.validation.appointment.Email.notEmpty";
	private static final String PROPERTY_EMPTY_CONFIRM_EMAIL = "appointment.validation.appointment.EmailConfirmation.email";
	private static final String PROPERTY_UNVAILABLE_CONFIRM_EMAIL = "appointment.message.error.confirmEmail";
	private static final String PROPERTY_EMPTY_NB_SEATS = "appointment.validation.appointment.NbBookedSeat.notEmpty";
	private static final String PROPERTY_UNVAILABLE_NB_SEATS = "appointment.validation.appointment.NbBookedSeat.error";
	private static final String PROPERTY_MAX_APPOINTMENT_PERIODE = "appointment.message.error.MaxAppointmentPeriode";
	private static final String PROPERTY_MAX_APPOINTMENT_PERIODE_BACK = "appointment.info.appointment.emailerror";

	private transient volatile Boolean _bIsFormFirstStep;

	/**
	 * Instance of the service
	 */
	private static volatile AppointmentFormService _instance;

	/**
	 * Get an instance of the service
	 * 
	 * @return An instance of the service
	 */
	public static AppointmentFormService getInstance() {
		if (_instance == null) {
			_instance = SpringContextService.getBean(BEAN_NAME);
		}

		return _instance;
	}

	/**
	 * Get an Entry Filter
	 * 
	 * @param iform
	 *            the id form
	 * @return List a filter Entry
	 */
	public static List<Entry> getFilter(int iform, boolean bDisplayFront) {
		EntryFilter filter = new EntryFilter();
		filter.setIdResource(iform);
		filter.setResourceType(AppointmentForm.RESOURCE_TYPE);
		filter.setEntryParentNull(EntryFilter.FILTER_TRUE);
		filter.setFieldDependNull(EntryFilter.FILTER_TRUE);
		if (bDisplayFront) {
			filter.setIsOnlyDisplayInBack(EntryFilter.FILTER_FALSE);
		}
		List<Entry> listEntryFirstLevel = EntryHome.getEntryList(filter);
		return listEntryFirstLevel;
	}

	/**
	 * Return the HTML code of the form
	 * 
	 * @param form
	 *            the form which HTML code must be return
	 * @param formMessages
	 *            The form messages associated with the form
	 * @param locale
	 *            the locale
	 * @param bDisplayFront
	 *            True if the entry will be displayed in Front Office, false if
	 *            it will be displayed in Back Office.
	 * @param request
	 *            HttpServletRequest
	 * @return the HTML code of the form
	 */
	public String getHtmlForm(AppointmentForm form, FormMessage formMessages, Locale locale, boolean bDisplayFront,
			HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		StringBuffer strBuffer = new StringBuffer();
		List<Entry> listEntryFirstLevel = getFilter(form.getIdForm(), bDisplayFront);
		for (Entry entry : listEntryFirstLevel) {
			getHtmlEntry(entry.getIdEntry(), strBuffer, locale, bDisplayFront, request);
		}
		model.put(MARK_FORM, form);
		model.put(MARK_FORM_MESSAGES, formMessages);
		model.put(MARK_STR_ENTRY, strBuffer.toString());
		model.put(MARK_LOCALE, locale);		
		model.put(MARK_PLACES, 200);
		List<GenericAttributeError> listErrors = (List<GenericAttributeError>) request.getSession()
				.getAttribute(SESSION_APPOINTMENT_FORM_ERRORS);
		model.put(MARK_FORM_ERRORS, listErrors);
		model.put(MARK_LIST_ERRORS, getAllErrors(request));
		HtmlTemplate template = AppTemplateService
				.getTemplate(bDisplayFront ? TEMPLATE_HTML_CODE_FORM : TEMPLATE_HTML_CODE_FORM_ADMIN, locale, model);
		return template.getHtml();
	}

	public List<String> getAllErrors(HttpServletRequest request) {
		List<String> listAllErrors = new ArrayList<String>();
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_EMPTY_FIELD_LAST_NAME, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_EMPTY_FIELD_FIRST_NAME, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_UNVAILABLE_EMAIL, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_MESSAGE_EMPTY_EMAIL, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_EMPTY_CONFIRM_EMAIL, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_UNVAILABLE_CONFIRM_EMAIL, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_EMPTY_NB_SEATS, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_UNVAILABLE_NB_SEATS, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_MAX_APPOINTMENT_PERIODE, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_MAX_APPOINTMENT_PERIODE_BACK, request.getLocale()));
		return listAllErrors;
	}

	

	/**
	 * Insert in the string buffer the content of the HTML code of the entry
	 * 
	 * @param nIdEntry
	 *            the key of the entry which HTML code must be insert in the
	 *            stringBuffer
	 * @param stringBuffer
	 *            the buffer which contains the HTML code
	 * @param locale
	 *            the locale
	 * @param bDisplayFront
	 *            True if the entry will be displayed in Front Office, false if
	 *            it will be displayed in Back Office.
	 * @param request
	 *            HttpServletRequest
	 */
	public void getHtmlEntry(int nIdEntry, StringBuffer stringBuffer, Locale locale, boolean bDisplayFront,
			HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		StringBuffer strConditionalQuestionStringBuffer = null;
		HtmlTemplate template;
		Entry entry = EntryHome.findByPrimaryKey(nIdEntry);
		if (entry.getEntryType().getGroup()) {
			StringBuffer strGroupStringBuffer = new StringBuffer();
			for (Entry entryChild : entry.getChildren()) {
				getHtmlEntry(entryChild.getIdEntry(), strGroupStringBuffer, locale, bDisplayFront, request);
			}
			model.put(MARK_STR_LIST_CHILDREN, strGroupStringBuffer.toString());
		} else {
			if (entry.getNumberConditionalQuestion() != 0) {
				for (Field field : entry.getFields()) {
					field.setConditionalQuestions(
							FieldHome.findByPrimaryKey(field.getIdField()).getConditionalQuestions());
				}
			}
		}
		if (entry.getNumberConditionalQuestion() != 0) {
			strConditionalQuestionStringBuffer = new StringBuffer();
			for (Field field : entry.getFields()) {
				if (field.getConditionalQuestions().size() != 0) {
					StringBuffer strGroupStringBuffer = new StringBuffer();
					for (Entry entryConditional : field.getConditionalQuestions()) {
						getHtmlEntry(entryConditional.getIdEntry(), strGroupStringBuffer, locale, bDisplayFront,
								request);
					}
					model.put(MARK_STR_LIST_CHILDREN, strGroupStringBuffer.toString());
					model.put(MARK_FIELD, field);
					template = AppTemplateService.getTemplate(TEMPLATE_DIV_CONDITIONAL_ENTRY, locale, model);
					strConditionalQuestionStringBuffer.append(template.getHtml());
				}
			}
			model.put(MARK_STR_LIST_CHILDREN, strConditionalQuestionStringBuffer.toString());
		}
		model.put(MARK_ENTRY, entry);
		model.put(MARK_LOCALE, locale);
		if (request != null) {
			AppointmentFrontDTO appointmentFrontDTO = (AppointmentFrontDTO) request.getSession().getAttribute(SESSION_NOT_VALIDATED_APPOINTMENT);
			if ((appointmentFrontDTO != null) && (appointmentFrontDTO.getMapResponsesByIdEntry() != null)) {
				List<Response> listResponses = appointmentFrontDTO.getMapResponsesByIdEntry().get(entry.getIdEntry());
				model.put(MARK_LIST_RESPONSES, listResponses);
			}
		}
		IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService(entry);
		// If the entry type is a file, we add the
		if (entryTypeService instanceof AbstractEntryTypeUpload) {
			model.put(MARK_UPLOAD_HANDLER, ((AbstractEntryTypeUpload) entryTypeService).getAsynchronousUploadHandler());
		}
		template = AppTemplateService.getTemplate(entryTypeService.getTemplateHtmlForm(entry, bDisplayFront), locale,
				model);
		stringBuffer.append(template.getHtml());
	}

	/**
	 * Get the responses associated with an entry.<br />
	 * Return null if there is no error in the response, or return the list of
	 * errors Response created are stored the map of {@link AppointmentDTO}. The
	 * key of the map is this id of the entry, and the value the list of
	 * responses
	 * 
	 * @param request
	 *            the request
	 * @param nIdEntry
	 *            the key of the entry
	 * @param locale
	 *            the locale
	 * @param appointment
	 *            The appointment
	 * @return null if there is no error in the response or the list of errors
	 *         found
	 */
	public List<GenericAttributeError> getResponseEntry(HttpServletRequest request, int nIdEntry, Locale locale,
			AppointmentFrontDTO appointment) {
		List<Response> listResponse = new ArrayList<Response>();
		appointment.getMapResponsesByIdEntry().put(nIdEntry, listResponse);

		return getResponseEntry(request, nIdEntry, listResponse, false, locale, appointment);
	}

	
	
	/**
	 * Get the responses associated with an entry.<br />
	 * Return null if there is no error in the response, or return the list of
	 * errors
	 * 
	 * @param request
	 *            the request
	 * @param nIdEntry
	 *            the key of the entry
	 * @param listResponse
	 *            The list of response to add responses found in
	 * @param bResponseNull
	 *            true if the response created must be null
	 * @param locale
	 *            the locale
	 * @param appointment
	 *            The appointment
	 * @return null if there is no error in the response or the list of errors
	 *         found
	 */
	private List<GenericAttributeError> getResponseEntry(HttpServletRequest request, int nIdEntry,
			List<Response> listResponse, boolean bResponseNull, Locale locale, AppointmentFrontDTO appointment) {
		List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>();
		Entry entry = EntryHome.findByPrimaryKey(nIdEntry);

		List<Field> listField = new ArrayList<Field>();

		for (Field field : entry.getFields()) {
			field = FieldHome.findByPrimaryKey(field.getIdField());
			listField.add(field);
		}

		entry.setFields(listField);

		if (entry.getEntryType().getGroup()) {
			for (Entry entryChild : entry.getChildren()) {
				List<Response> listResponseChild = new ArrayList<Response>();
				appointment.getMapResponsesByIdEntry().put(entryChild.getIdEntry(), listResponseChild);

				listFormErrors.addAll(getResponseEntry(request, entryChild.getIdEntry(), listResponseChild, false,
						locale, appointment));
			}
		} else if (!entry.getEntryType().getComment()) {
			GenericAttributeError formError = null;

			if (!bResponseNull) {
				formError = EntryTypeServiceManager.getEntryTypeService(entry).getResponseData(entry, request,
						listResponse, locale);

				if (formError != null) {
					formError.setUrl(getEntryUrl(entry, appointment.getIdForm()));
				}
			} else {
				Response response = new Response();
				response.setEntry(entry);
				listResponse.add(response);
			}

			if (formError != null) {
				entry.setError(formError);
				listFormErrors.add(formError);
			}

			if (entry.getNumberConditionalQuestion() != 0) {
				for (Field field : entry.getFields()) {
					boolean bIsFieldInResponseList = isFieldInTheResponseList(field.getIdField(), listResponse);

					for (Entry conditionalEntry : field.getConditionalQuestions()) {
						List<Response> listResponseChild = new ArrayList<Response>();
						appointment.getMapResponsesByIdEntry().put(conditionalEntry.getIdEntry(), listResponseChild);

						listFormErrors.addAll(getResponseEntry(request, conditionalEntry.getIdEntry(),
								listResponseChild, !bIsFieldInResponseList, locale, appointment));
					}
				}
			}
		}

		return listFormErrors;
	}

	
	/**
	 * Check if a field is in a response list
	 * 
	 * @param nIdField
	 *            the id of the field to search
	 * @param listResponse
	 *            the list of responses
	 * @return true if the field is in the response list, false otherwise
	 */
	public Boolean isFieldInTheResponseList(int nIdField, List<Response> listResponse) {
		for (Response response : listResponse) {
			if ((response.getField() != null) && (response.getField().getIdField() == nIdField)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Get the URL of the anchor of an entry
	 * 
	 * @param entry
	 *            the entry
	 * @return The URL of the anchor of an entry
	 */
	public String getEntryUrl(Entry entry, int nIdform) {
		UrlItem url = new UrlItem(AppPathService.getPortalUrl());
		url.addParameter(XPageAppService.PARAM_XPAGE_APP, AppointmentPlugin.PLUGIN_NAME);
		url.addParameter(MVCUtils.PARAMETER_VIEW, AppointmentApp.VIEW_APPOINTMENT_FORM);

		if ((entry != null) && (entry.getIdResource() > 0)) {
			url.addParameter(PARAMETER_ID_FORM, entry.getIdResource());
			url.setAnchor(PREFIX_ATTRIBUTE + entry.getIdEntry());
		}

		return url.getUrl();
	}
}
