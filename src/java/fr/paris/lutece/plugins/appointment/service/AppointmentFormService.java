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

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.appointment.business.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormMessages;
import fr.paris.lutece.plugins.appointment.business.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.plugins.appointment.service.addon.AppointmentAddOnManager;
import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeUpload;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.sql.TransactionManager;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * Service for appointment forms
 */
public class AppointmentFormService implements Serializable
{
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
    
    private static final String PARAMETER_CUSTOMER_ID = "cuid";
    private static final String PARAMETER_USER_ID_OPAM = "guid";

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
    private static final String  MARK_CUSTOMER_ID = "cuid";
    private static final String  MARK_USER_ID_OPAM = "guid";


    // Session keys
    private static final String SESSION_NOT_VALIDATED_APPOINTMENT = "appointment.appointmentFormService.notValidatedAppointment";
    private static final String SESSION_VALIDATED_APPOINTMENT = "appointment.appointmentFormService.validatedAppointment";
    private static final String SESSION_APPOINTMENT_FORM_ERRORS = "appointment.session.formErrors";

    // Templates
    private static final String TEMPLATE_DIV_CONDITIONAL_ENTRY = "skin/plugins/appointment/html_code_div_conditional_entry.html";
    private static final String TEMPLATE_HTML_CODE_FORM = "skin/plugins/appointment/html_code_form.html";
    private static final String TEMPLATE_HTML_CODE_FORM_ADMIN = "admin/plugins/appointment/html_code_form.html";

    // Properties
    private static final String PROPERTY_DEFAULT_CALENDAR_TITLE = "appointment.formMessages.defaultCalendarTitle";
    private static final String PROPERTY_DEFAULT_FIELD_FIRST_NAME_TITLE = "appointment.formMessages.defaultFieldFirstNameTitle";
    private static final String PROPERTY_DEFAULT_FIELD_FIRST_NAME_HELP = "appointment.formMessages.defaultFieldFirstNameHelp";
    private static final String PROPERTY_DEFAULT_FIELD_LAST_NAME_TITLE = "appointment.formMessages.defaultFieldLastNameTitle";
    private static final String PROPERTY_DEFAULT_FIELD_LAST_NAME_HELP = "appointment.formMessages.defaultFieldLastNameHelp";
    private static final String PROPERTY_DEFAULT_FIELD_EMAIL_TITLE = "appointment.formMessages.defaultFieldEmailTitle";
    private static final String PROPERTY_DEFAULT_FIELD_EMAIL_HELP = "appointment.formMessages.defaultFieldEmailHelp";
    private static final String PROPERTY_DEFAULT_FIELD_CONFIRMATION_EMAIL_TITLE = "appointment.formMessages.defaultFieldConfirmationEmailTitle";
    private static final String PROPERTY_DEFAULT_FIELD_CONFIRMATION_EMAIL_HELP = "appointment.formMessages.defaultFieldConfirmationEmailHelp";
    private static final String PROPERTY_DEFAULT_URL_REDIRECTION = "appointment.formMessages.defaultUrlRedirection";
    private static final String PROPERTY_DEFAULT_LABEL_BUTTON_REDIRECT = "appointment.formMessages.defaultLabelButtonRedirect";
    private static final String PROPERTY_DEFAULT_TEXT_APPOINTMENT_CREATED = "appointment.formMessages.defaultTextAppointmentCreated";
    private static final String PROPERTY_DEFAULT_TEXT_APPOINTMENT_CANCELED = "appointment.formMessages.defaultTextAppointmentCanceled";
    private static final String PROPERTY_DEFAULT_NO_AVAILABLE_SLOT = "appointment.formMessages.defaultNoAvailableSlot";
    private static final String PROPERTY_DEFAULT_CALENDAR_DESCRIPTION = "appointment.formMessages.defaultCalendarDescription";
    private static final String PROPERTY_DEFAULT_CALENDAR_RESERVE_LABEL = "appointment.formMessages.defaultCalendarReserveLabel";
    private static final String PROPERTY_DEFAULT_CALENDAR_FULL_LABEL = "appointment.formMessages.defaultCalendarFullLabel";
    private static final String PROPERTY_USER_ATTRIBUTE_FIRST_NAME = "appointment.userAttribute.firstName";
    private static final String PROPERTY_USER_ATTRIBUTE_LAST_NAME = "appointment.userAttribute.lastName";
    private static final String PROPERTY_USER_ATTRIBUTE_EMAIL = "appointment.userAttribute.email";
    private static final String PROPERTY_EMPTY_FIELD_FIRST_NAME = "appointment.validation.appointment.FirstName.notEmpty";
    private static final String PROPERTY_EMPTY_FIELD_LAST_NAME = "appointment.validation.appointment.LastName.notEmpty";
    private static final String PROPERTY_UNVAILABLE_EMAIL = "appointment.validation.appointment.Email.email";
    private static final String PROPERTY_MESSAGE_EMPTY_EMAIL = "appointment.validation.appointment.Email.notEmpty";
    private static final String PROPERTY_EMPTY_CONFIRM_EMAIL = "appointment.validation.appointment.EmailConfirmation.email";
    private static final String PROPERTY_UNVAILABLE_CONFIRM_EMAIL = "appointment.message.error.confirmEmail";
    private transient volatile Boolean _bIsFormFirstStep;

    /**
     * Return the HTML code of the form
     * @param form the form which HTML code must be return
     * @param formMessages The form messages associated with the form
     * @param locale the locale
     * @param
     * @param bDisplayFront True if the entry will be displayed in Front Office,
     *            false if it will be displayed in Back Office.
     * @param request HttpServletRequest
     * @return the HTML code of the form
     */
    public String getHtmlForm( int nWeek, String strDay, String strSlot, AppointmentForm form,
        AppointmentFormMessages formMessages, Locale locale, boolean bDisplayFront, HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        StringBuffer strBuffer = new StringBuffer(  );

        List<Entry> listEntryFirstLevel = getFilter( form.getIdForm(  ) );

        for ( Entry entry : listEntryFirstLevel )
        {
            getHtmlEntry( entry.getIdEntry(  ), strBuffer, locale, bDisplayFront, request );
        }

        List<GenericAttributeError> listErrors = (List<GenericAttributeError>) request.getSession(  )
                                                                                      .getAttribute( SESSION_APPOINTMENT_FORM_ERRORS );

        model.put( MARK_FORM_ERRORS, listErrors );
        model.put( MARK_APPOINTMENTSLOT, strSlot );
        model.put( MARK_APPOINTMENTSLOTDAY, strDay );
        model.put( MARK_FORM, form );
        model.put( MARK_FORM_MESSAGES, formMessages );
        model.put( MARK_STR_ENTRY, strBuffer.toString(  ) );
        model.put( MARK_LOCALE, locale );
        model.put( MARK_WEEK, nWeek );
        
        String strCustomerId = (String) request.getAttribute( PARAMETER_CUSTOMER_ID );
        String strUserIdOpam = (String) request.getAttribute( PARAMETER_USER_ID_OPAM );
       AppLogService.info("Appintment To GRU : strCustomerId "+strCustomerId);
       AppLogService.info("Appintment To GRU : strUserIdOpam "+strUserIdOpam);
       
        
        model.put(MARK_CUSTOMER_ID,strCustomerId );
        model.put( MARK_USER_ID_OPAM,strUserIdOpam);

        AppointmentDTO appointment = getAppointmentFromSession( request.getSession(  ) );

        if ( appointment == null )
        {
            appointment = new AppointmentDTO(  );

            setUserInfo( request, appointment );
        }

        model.put( MARK_APPOINTMENT, appointment );
        model.put( MARK_LIST_ERRORS, getAllErrors( request ) );

        if ( bDisplayFront )
        {
            model.put( MARK_IS_FORM_FIRST_STEP, isFormFirstStep( form.getIdForm(  ) ) );
        }

        if ( !bDisplayFront && ( appointment.getIdAppointment(  ) > 0 ) )
        {
            model.put( MARK_ADDON,
                AppointmentAddOnManager.getAppointmentAddOn( appointment.getIdAppointment(  ), locale ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( bDisplayFront ? TEMPLATE_HTML_CODE_FORM
                                                                              : TEMPLATE_HTML_CODE_FORM_ADMIN, locale,
                model );

        return template.getHtml(  );
    }

    /**
     * Get an Entry Filter
     * @param iform the id form
     * @return List a filter Entry
     */
    private static List<Entry> getFilter( int iform )
    {
        EntryFilter filter = new EntryFilter(  );
        filter.setIdResource( iform );
        filter.setResourceType( AppointmentForm.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );

        return listEntryFirstLevel;
    }

    /**
     * Return the HTML code of the form
     * @param form the form which HTML code must be return
     * @param formMessages The form messages associated with the form
     * @param locale the locale
     * @param bDisplayFront True if the entry will be displayed in Front Office,
     *            false if it will be displayed in Back Office.
     * @param request HttpServletRequest
     * @return the HTML code of the form
     */
    public String getHtmlForm( AppointmentForm form, AppointmentFormMessages formMessages, Locale locale,
        boolean bDisplayFront, HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        StringBuffer strBuffer = new StringBuffer(  );

        List<Entry> listEntryFirstLevel = getFilter( form.getIdForm(  ) );

        for ( Entry entry : listEntryFirstLevel )
        {
            getHtmlEntry( entry.getIdEntry(  ), strBuffer, locale, bDisplayFront, request );
        }

        model.put( MARK_FORM, form );
        model.put( MARK_FORM_MESSAGES, formMessages );
        model.put( MARK_STR_ENTRY, strBuffer.toString(  ) );
        model.put( MARK_LOCALE, locale );

        AppointmentDTO appointment = getAppointmentFromSession( request.getSession(  ) );

        if ( appointment == null )
        {
            appointment = new AppointmentDTO(  );

            setUserInfo( request, appointment );
        }

        model.put( MARK_APPOINTMENT, appointment );

        if ( bDisplayFront )
        {
            model.put( MARK_IS_FORM_FIRST_STEP, isFormFirstStep( form.getIdForm(  ) ) );
        }

        if ( !bDisplayFront && ( appointment.getIdAppointment(  ) > 0 ) )
        {
            model.put( MARK_ADDON,
                AppointmentAddOnManager.getAppointmentAddOn( appointment.getIdAppointment(  ), locale ) );
        }

        List<GenericAttributeError> listErrors = (List<GenericAttributeError>) request.getSession(  )
                                                                                      .getAttribute( SESSION_APPOINTMENT_FORM_ERRORS );

        model.put( MARK_FORM_ERRORS, listErrors );
        model.put( MARK_LIST_ERRORS, getAllErrors( request ) );

        HtmlTemplate template = AppTemplateService.getTemplate( bDisplayFront ? TEMPLATE_HTML_CODE_FORM
                                                                              : TEMPLATE_HTML_CODE_FORM_ADMIN, locale,
                model );

        return template.getHtml(  );
    }

    private List<String> getAllErrors( HttpServletRequest request )
    {
        List<String> listAllErrors = new ArrayList<String>(  );

        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_EMPTY_FIELD_LAST_NAME, request.getLocale(  ) ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_EMPTY_FIELD_FIRST_NAME, request.getLocale(  ) ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_UNVAILABLE_EMAIL, request.getLocale(  ) ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_MESSAGE_EMPTY_EMAIL, request.getLocale(  ) ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_EMPTY_CONFIRM_EMAIL, request.getLocale(  ) ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_UNVAILABLE_CONFIRM_EMAIL, request.getLocale(  ) ) );

        return listAllErrors;
    }

    /**
     * Set the info of the current LuteceUser to an appointment. If there is no
     * current lutece user, then do nothing
     * @param request The request
     * @param appointment The appointment to set user info
     */
    public void setUserInfo( HttpServletRequest request, Appointment appointment )
    {
        if ( SecurityService.isAuthenticationEnable(  ) && ( appointment != null ) )
        {
            LuteceUser user = SecurityService.getInstance(  ).getRegisteredUser( request );

            if ( user != null )
            {
                appointment.setFirstName( user.getUserInfo( AppPropertiesService.getProperty( 
                            PROPERTY_USER_ATTRIBUTE_FIRST_NAME, StringUtils.EMPTY ) ) );
                appointment.setLastName( user.getUserInfo( AppPropertiesService.getProperty( 
                            PROPERTY_USER_ATTRIBUTE_LAST_NAME, StringUtils.EMPTY ) ) );
                appointment.setEmail( user.getUserInfo( AppPropertiesService.getProperty( 
                            PROPERTY_USER_ATTRIBUTE_EMAIL, StringUtils.EMPTY ) ) );
            }
        }
    }

    /**
     * Insert in the string buffer the content of the HTML code of the entry
     * @param nIdEntry the key of the entry which HTML code must be insert in
     *            the stringBuffer
     * @param stringBuffer the buffer which contains the HTML code
     * @param locale the locale
     * @param bDisplayFront True if the entry will be displayed in Front Office,
     *            false if it will be displayed in Back Office.
     * @param request HttpServletRequest
     */
    public void getHtmlEntry( int nIdEntry, StringBuffer stringBuffer, Locale locale, boolean bDisplayFront,
        HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        StringBuffer strConditionalQuestionStringBuffer = null;
        HtmlTemplate template;
        Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

        if ( entry.getEntryType(  ).getGroup(  ) )
        {
            StringBuffer strGroupStringBuffer = new StringBuffer(  );

            for ( Entry entryChild : entry.getChildren(  ) )
            {
                getHtmlEntry( entryChild.getIdEntry(  ), strGroupStringBuffer, locale, bDisplayFront, request );
            }

            model.put( MARK_STR_LIST_CHILDREN, strGroupStringBuffer.toString(  ) );
        }
        else
        {
            if ( entry.getNumberConditionalQuestion(  ) != 0 )
            {
                for ( Field field : entry.getFields(  ) )
                {
                    field.setConditionalQuestions( FieldHome.findByPrimaryKey( field.getIdField(  ) )
                                                            .getConditionalQuestions(  ) );
                }
            }
        }

        if ( entry.getNumberConditionalQuestion(  ) != 0 )
        {
            strConditionalQuestionStringBuffer = new StringBuffer(  );

            for ( Field field : entry.getFields(  ) )
            {
                if ( field.getConditionalQuestions(  ).size(  ) != 0 )
                {
                    StringBuffer strGroupStringBuffer = new StringBuffer(  );

                    for ( Entry entryConditional : field.getConditionalQuestions(  ) )
                    {
                        getHtmlEntry( entryConditional.getIdEntry(  ), strGroupStringBuffer, locale, bDisplayFront,
                            request );
                    }

                    model.put( MARK_STR_LIST_CHILDREN, strGroupStringBuffer.toString(  ) );
                    model.put( MARK_FIELD, field );
                    template = AppTemplateService.getTemplate( TEMPLATE_DIV_CONDITIONAL_ENTRY, locale, model );
                    strConditionalQuestionStringBuffer.append( template.getHtml(  ) );
                }
            }

            model.put( MARK_STR_LIST_CHILDREN, strConditionalQuestionStringBuffer.toString(  ) );
        }

        model.put( MARK_ENTRY, entry );
        model.put( MARK_LOCALE, locale );

        LuteceUser user = SecurityService.getInstance(  ).getRegisteredUser( request );

        if ( ( user == null ) && SecurityService.isAuthenticationEnable(  ) &&
                SecurityService.getInstance(  ).isExternalAuthentication(  ) )
        {
            try
            {
                user = SecurityService.getInstance(  ).getRemoteUser( request );
            }
            catch ( UserNotSignedException e )
            {
                // Nothing to do : lutece user is not mandatory
            }
        }

        model.put( MARK_USER, user );

        if ( request != null )
        {
            AppointmentDTO appointment = getAppointmentFromSession( request.getSession(  ) );

            if ( ( appointment != null ) && ( appointment.getMapResponsesByIdEntry(  ) != null ) )
            {
                List<Response> listResponses = appointment.getMapResponsesByIdEntry(  ).get( entry.getIdEntry(  ) );
                model.put( MARK_LIST_RESPONSES, listResponses );
            }
        }

        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );

        // If the entry type is a file, we add the 
        if ( entryTypeService instanceof AbstractEntryTypeUpload )
        {
            model.put( MARK_UPLOAD_HANDLER,
                ( (AbstractEntryTypeUpload) entryTypeService ).getAsynchronousUploadHandler(  ) );
        }

        template = AppTemplateService.getTemplate( entryTypeService.getTemplateHtmlForm( entry, bDisplayFront ),
                locale, model );
        stringBuffer.append( template.getHtml(  ) );
    }

    /**
     * Get the responses associated with an entry.<br />
     * Return null if there is no error in the response, or return the list of
     * errors
     * Response created are stored the map of {@link AppointmentDTO}. The key of
     * the map is this id of the entry, and the value the list of responses
     * @param request the request
     * @param nIdEntry the key of the entry
     * @param locale the locale
     * @param appointment The appointment
     * @return null if there is no error in the response or the list of errors
     *         found
     */
    public List<GenericAttributeError> getResponseEntry( HttpServletRequest request, int nIdEntry, Locale locale,
        AppointmentDTO appointment )
    {
        List<Response> listResponse = new ArrayList<Response>(  );
        appointment.getMapResponsesByIdEntry(  ).put( nIdEntry, listResponse );

        return getResponseEntry( request, nIdEntry, listResponse, false, locale, appointment );
    }

    /**
     * Get the responses associated with an entry.<br />
     * Return null if there is no error in the response, or return the list of
     * errors
     * @param request the request
     * @param nIdEntry the key of the entry
     * @param listResponse The list of response to add responses found in
     * @param bResponseNull true if the response created must be null
     * @param locale the locale
     * @param appointment The appointment
     * @return null if there is no error in the response or the list of errors
     *         found
     */
    private List<GenericAttributeError> getResponseEntry( HttpServletRequest request, int nIdEntry,
        List<Response> listResponse, boolean bResponseNull, Locale locale, AppointmentDTO appointment )
    {
        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>(  );
        Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

        List<Field> listField = new ArrayList<Field>(  );

        for ( Field field : entry.getFields(  ) )
        {
            field = FieldHome.findByPrimaryKey( field.getIdField(  ) );
            listField.add( field );
        }

        entry.setFields( listField );

        if ( entry.getEntryType(  ).getGroup(  ) )
        {
            for ( Entry entryChild : entry.getChildren(  ) )
            {
                List<Response> listResponseChild = new ArrayList<Response>(  );
                appointment.getMapResponsesByIdEntry(  ).put( entryChild.getIdEntry(  ), listResponseChild );

                listFormErrors.addAll( getResponseEntry( request, entryChild.getIdEntry(  ), listResponseChild, false,
                        locale, appointment ) );
            }
        }
        else if ( !entry.getEntryType(  ).getComment(  ) )
        {
            GenericAttributeError formError = null;

            if ( !bResponseNull )
            {
                formError = EntryTypeServiceManager.getEntryTypeService( entry )
                                                   .getResponseData( entry, request, listResponse, locale );

                if ( formError != null )
                {
                    formError.setUrl( getEntryUrl( entry, appointment.getAppointmentForm(  ).getIdForm(  ) ) );
                }
            }
            else
            {
                Response response = new Response(  );
                response.setEntry( entry );
                listResponse.add( response );
            }

            if ( formError != null )
            {
                entry.setError( formError );
                listFormErrors.add( formError );
            }

            if ( entry.getNumberConditionalQuestion(  ) != 0 )
            {
                for ( Field field : entry.getFields(  ) )
                {
                    boolean bIsFieldInResponseList = isFieldInTheResponseList( field.getIdField(  ), listResponse );

                    for ( Entry conditionalEntry : field.getConditionalQuestions(  ) )
                    {
                        List<Response> listResponseChild = new ArrayList<Response>(  );
                        appointment.getMapResponsesByIdEntry(  ).put( conditionalEntry.getIdEntry(  ), listResponseChild );

                        listFormErrors.addAll( getResponseEntry( request, conditionalEntry.getIdEntry(  ),
                                listResponseChild, !bIsFieldInResponseList, locale, appointment ) );
                    }
                }
            }
        }

        return listFormErrors;
    }

    /**
     * Check if a field is in a response list
     * @param nIdField the id of the field to search
     * @param listResponse the list of responses
     * @return true if the field is in the response list, false otherwise
     */
    public Boolean isFieldInTheResponseList( int nIdField, List<Response> listResponse )
    {
        for ( Response response : listResponse )
        {
            if ( ( response.getField(  ) != null ) && ( response.getField(  ).getIdField(  ) == nIdField ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the URL of the anchor of an entry
     * @param entry the entry
     * @return The URL of the anchor of an entry
     */
    public String getEntryUrl( Entry entry, int nIdform )
    {
        UrlItem url = new UrlItem( AppPathService.getPortalUrl(  ) );
        url.addParameter( XPageAppService.PARAM_XPAGE_APP, AppointmentPlugin.PLUGIN_NAME );
        url.addParameter( MVCUtils.PARAMETER_VIEW,
            isFormFirstStep( nIdform ) ? AppointmentApp.VIEW_APPOINTMENT_FORM_FIRST_STEP
                                       : AppointmentApp.VIEW_APPOINTMENT_FORM_SECOND_STEP );

        if ( ( entry != null ) && ( entry.getIdResource(  ) > 0 ) )
        {
            url.addParameter( PARAMETER_ID_FORM, entry.getIdResource(  ) );
            url.setAnchor( PREFIX_ATTRIBUTE + entry.getIdEntry(  ) );
        }

        return url.getUrl(  );
    }

    /**
     * Save an appointment in the session of the user
     * @param session The session
     * @param appointment The appointment to save
     */
    public void saveAppointmentInSession( HttpSession session, AppointmentDTO appointment )
    {
        session.setAttribute( SESSION_NOT_VALIDATED_APPOINTMENT, appointment );
    }

    /**
     * Get the current appointment form from the session
     * @param session The session of the user
     * @return The appointment form
     */
    public AppointmentDTO getAppointmentFromSession( HttpSession session )
    {
        return (AppointmentDTO) session.getAttribute( SESSION_NOT_VALIDATED_APPOINTMENT );
    }

    /**
     * Remove any appointment form responses stored in the session of the user
     * @param session The session
     */
    public void removeAppointmentFromSession( HttpSession session )
    {
        session.removeAttribute( SESSION_NOT_VALIDATED_APPOINTMENT );
    }

    /**
     * Save a validated appointment into the session of the user
     * @param session The session
     * @param appointment The appointment to save
     */
    public void saveValidatedAppointmentForm( HttpSession session, Appointment appointment )
    {
        removeAppointmentFromSession( session );
        session.setAttribute( SESSION_VALIDATED_APPOINTMENT, appointment );
    }

    /**
     * Get a validated appointment from the session
     * @param session The session of the user
     * @return The appointment
     */
    public Appointment getValidatedAppointmentFromSession( HttpSession session )
    {
        return (Appointment) session.getAttribute( SESSION_VALIDATED_APPOINTMENT );
    }

    /**
     * Remove a validated appointment stored in the session of the user
     * @param session The session
     */
    public void removeValidatedAppointmentFromSession( HttpSession session )
    {
        session.removeAttribute( SESSION_VALIDATED_APPOINTMENT );
    }

    /**
     * Get the default form message with values loaded from properties.
     * @return The default form message. The form message is not associated with
     *         any appointment form
     */
    public AppointmentFormMessages getDefaultAppointmentFormMessage(  )
    {
        AppointmentFormMessages formMessages = new AppointmentFormMessages(  );
        formMessages.setCalendarTitle( AppPropertiesService.getProperty( PROPERTY_DEFAULT_CALENDAR_TITLE,
                StringUtils.EMPTY ) );
        formMessages.setFieldFirstNameTitle( AppPropertiesService.getProperty( 
                PROPERTY_DEFAULT_FIELD_FIRST_NAME_TITLE, StringUtils.EMPTY ) );
        formMessages.setFieldFirstNameHelp( AppPropertiesService.getProperty( PROPERTY_DEFAULT_FIELD_FIRST_NAME_HELP,
                StringUtils.EMPTY ) );
        formMessages.setFieldLastNameTitle( AppPropertiesService.getProperty( PROPERTY_DEFAULT_FIELD_LAST_NAME_TITLE,
                StringUtils.EMPTY ) );
        formMessages.setFieldLastNameHelp( AppPropertiesService.getProperty( PROPERTY_DEFAULT_FIELD_LAST_NAME_HELP,
                StringUtils.EMPTY ) );
        formMessages.setFieldEmailTitle( AppPropertiesService.getProperty( PROPERTY_DEFAULT_FIELD_EMAIL_TITLE,
                StringUtils.EMPTY ) );
        formMessages.setFieldEmailHelp( AppPropertiesService.getProperty( PROPERTY_DEFAULT_FIELD_EMAIL_HELP,
                StringUtils.EMPTY ) );
        formMessages.setFieldConfirmationEmail( AppPropertiesService.getProperty( 
                PROPERTY_DEFAULT_FIELD_CONFIRMATION_EMAIL_TITLE, StringUtils.EMPTY ) );
        formMessages.setFieldConfirmationEmailHelp( AppPropertiesService.getProperty( 
                PROPERTY_DEFAULT_FIELD_CONFIRMATION_EMAIL_HELP, StringUtils.EMPTY ) );
        formMessages.setUrlRedirectAfterCreation( AppPropertiesService.getProperty( PROPERTY_DEFAULT_URL_REDIRECTION,
                StringUtils.EMPTY ) );
        formMessages.setLabelButtonRedirection( AppPropertiesService.getProperty( 
                PROPERTY_DEFAULT_LABEL_BUTTON_REDIRECT, StringUtils.EMPTY ) );
        formMessages.setTextAppointmentCreated( AppPropertiesService.getProperty( 
                PROPERTY_DEFAULT_TEXT_APPOINTMENT_CREATED, StringUtils.EMPTY ) );
        formMessages.setTextAppointmentCanceled( AppPropertiesService.getProperty( 
                PROPERTY_DEFAULT_TEXT_APPOINTMENT_CANCELED, StringUtils.EMPTY ) );
        formMessages.setNoAvailableSlot( AppPropertiesService.getProperty( PROPERTY_DEFAULT_NO_AVAILABLE_SLOT,
                StringUtils.EMPTY ) );
        formMessages.setCalendarDescription( AppPropertiesService.getProperty( PROPERTY_DEFAULT_CALENDAR_DESCRIPTION,
                StringUtils.EMPTY ) );
        formMessages.setCalendarReserveLabel( AppPropertiesService.getProperty( 
                PROPERTY_DEFAULT_CALENDAR_RESERVE_LABEL, StringUtils.EMPTY ) );
        formMessages.setCalendarFullLabel( AppPropertiesService.getProperty( PROPERTY_DEFAULT_CALENDAR_FULL_LABEL,
                StringUtils.EMPTY ) );

        return formMessages;
    }

    /**
     * Convert an AppointmentDTO to an Appointment by transferring response from
     * the map of class AppointmentDTO to the list of class Appointment.
     * @param appointment The appointment to convert
     */
    public void convertMapResponseToList( AppointmentDTO appointment )
    {
        List<Response> listResponse = new ArrayList<Response>(  );

        for ( List<Response> listResponseByEntry : appointment.getMapResponsesByIdEntry(  ).values(  ) )
        {
            listResponse.addAll( listResponseByEntry );
        }

        appointment.setMapResponsesByIdEntry( null );
        appointment.setListResponse( listResponse );
    }

    /**
     * Check if the form is the first step of the creation of appointments, or
     * if it is the calendar.
     * @return True if the form is the first step of the creation of
     *         appointments, false otherwise
     */
    public boolean isFormFirstStep( int nAppointmentFormId )
    {
        AppointmentForm myApmt = AppointmentFormHome.findByPrimaryKey( nAppointmentFormId );
        _bIsFormFirstStep = ( myApmt == null ) ? true : myApmt.getIsFormStep(  );

        return _bIsFormFirstStep;
    }

    /**
     * Convert a time into string
     * @param nHour The hour
     * @param nMinute The minute
     * @return The string representing the given time
     */
    public String convertTimeIntoString( int nHour, int nMinute )
    {
        StringBuilder sbTime = new StringBuilder(  );

        if ( nHour < 10 )
        {
            sbTime.append( 0 );
        }

        sbTime.append( nHour );
        sbTime.append( AppointmentForm.CONSTANT_H );

        if ( nMinute < 10 )
        {
            sbTime.append( 0 );
        }

        sbTime.append( nMinute );

        return sbTime.toString(  );
    }

    /**
     * Do check if an appointment can be made and make an appointment.
     * @param appointment The appointment to make
     * @param form The appointment form associated with the appointment
     * @param bIsAdmin True if this method was called by an admin, false if it
     *            was called by a regular user
     * @return True if the appointment was successfully made, false if the
     *         selected slot is empty or does not exist
     * @throws AppException If an error occurs when processing the creation or
     *             the update of the appointment
     */
    public synchronized boolean doMakeAppointment( Appointment appointment, AppointmentForm form, boolean bIsAdmin )
        throws AppException
    {
        AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKeyWithFreePlaces( appointment.getIdSlot(  ),
                appointment.getDateAppointment(  ) );

        if ( ( slot == null ) || ( slot.getNbFreePlaces(  ) <= 0 ) )
        {
            return false;
        }

        Plugin pluginAppointment = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

        TransactionManager.beginTransaction( pluginAppointment );

        try
        {
            // Only admins can modify appointments
            boolean bCreate = !bIsAdmin || ( appointment.getIdAppointment(  ) == 0 );

            if ( bCreate )
            {
                AppointmentHome.create( appointment );
            }
            else
            {
                AppointmentHome.update( appointment );
            }

            // For modification (by an admin), the update of responses have already been made when this method is called 
            if ( bCreate )
            {
                for ( Response response : appointment.getListResponse(  ) )
                {
                    ResponseHome.create( response );
                    AppointmentHome.insertAppointmentResponse( appointment.getIdAppointment(  ),
                        response.getIdResponse(  ) );
                }
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

            TransactionManager.commitTransaction( pluginAppointment );
        }
        catch ( Exception e )
        {
            TransactionManager.rollBack( pluginAppointment );
            throw new AppException( e.getMessage(  ), e );
        }

        return true;
    }
}
