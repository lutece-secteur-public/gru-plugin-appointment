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
package fr.paris.lutece.plugins.appointment.service;

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.appointment.business.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

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
    private static final long serialVersionUID = 6197939507943704211L;
    private static final String VIEW_GET_FORM = "viewForm";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PREFIX_ATTRIBUTE = "attribute";

    // marks
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_FIELD = "field";
    private static final String MARK_STR_LIST_CHILDREN = "str_list_entry_children";
    private static final String MARK_FORM = "form";
    private static final String MARK_STR_ENTRY = "str_entry";
    private static final String MARK_USER = "user";
    private static final String MARK_LIST_RESPONSES = "list_responses";
    private static final String MARK_APPOINTMENT = "appointment";

    // Session keys
    private static final String SESSION_NOT_VALIDATED_APPOINTMENT = "appointment.appointmentFormService.notValidatedAppointment";
    private static final String SESSION_VALIDATED_APPOINTMENT = "appointment.appointmentFormService.validatedAppointment";
    // Templates
    private static final String TEMPLATE_DIV_CONDITIONAL_ENTRY = "skin/plugins/appointment/html_code_div_conditional_entry.html";
    private static final String TEMPLATE_HTML_CODE_FORM = "skin/plugins/appointment/html_code_form.html";

    /**
     * Return the HTML code of the form
     * @param form the form which HTML code must be return
     * @param locale the locale
     * @param bDisplayFront True if the entry will be displayed in Front Office,
     *            false if it will be displayed in Back Office.
     * @param request HttpServletRequest
     * @return the HTML code of the form
     */
    public String getHtmlForm( AppointmentForm form, Locale locale, boolean bDisplayFront, HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>( );
        StringBuffer strBuffer = new StringBuffer( );
        EntryFilter filter = new EntryFilter( );
        filter.setIdResource( form.getIdForm( ) );
        filter.setResourceType( AppointmentForm.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );

        for ( Entry entry : listEntryFirstLevel )
        {
            getHtmlEntry( entry.getIdEntry( ), strBuffer, locale, bDisplayFront, request );
        }

        model.put( MARK_FORM, form );
        model.put( MARK_STR_ENTRY, strBuffer.toString( ) );
        model.put( MARK_LOCALE, locale );
        model.put( MARK_APPOINTMENT, getAppointmentFromSession( request.getSession( ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_HTML_CODE_FORM, locale, model );

        return template.getHtml( );
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
        Map<String, Object> model = new HashMap<String, Object>( );
        StringBuffer strConditionalQuestionStringBuffer = null;
        HtmlTemplate template;
        Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

        if ( entry.getEntryType( ).getGroup( ) )
        {
            StringBuffer strGroupStringBuffer = new StringBuffer( );

            for ( Entry entryChild : entry.getChildren( ) )
            {
                getHtmlEntry( entryChild.getIdEntry( ), strGroupStringBuffer, locale, bDisplayFront, request );
            }

            model.put( MARK_STR_LIST_CHILDREN, strGroupStringBuffer.toString( ) );
        }
        else
        {
            if ( entry.getNumberConditionalQuestion( ) != 0 )
            {
                for ( Field field : entry.getFields( ) )
                {
                    field.setConditionalQuestions( FieldHome.findByPrimaryKey( field.getIdField( ) )
                            .getConditionalQuestions( ) );
                }
            }
        }

        if ( entry.getNumberConditionalQuestion( ) != 0 )
        {
            strConditionalQuestionStringBuffer = new StringBuffer( );

            for ( Field field : entry.getFields( ) )
            {
                if ( field.getConditionalQuestions( ).size( ) != 0 )
                {
                    StringBuffer strGroupStringBuffer = new StringBuffer( );

                    for ( Entry entryConditional : field.getConditionalQuestions( ) )
                    {
                        getHtmlEntry( entryConditional.getIdEntry( ), strGroupStringBuffer, locale, bDisplayFront,
                                request );
                    }

                    model.put( MARK_STR_LIST_CHILDREN, strGroupStringBuffer.toString( ) );
                    model.put( MARK_FIELD, field );
                    template = AppTemplateService.getTemplate( TEMPLATE_DIV_CONDITIONAL_ENTRY, locale, model );
                    strConditionalQuestionStringBuffer.append( template.getHtml( ) );
                }
            }

            model.put( MARK_STR_LIST_CHILDREN, strConditionalQuestionStringBuffer.toString( ) );
        }

        model.put( MARK_ENTRY, entry );
        model.put( MARK_LOCALE, locale );

        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( ( user == null ) && SecurityService.isAuthenticationEnable( )
                && SecurityService.getInstance( ).isExternalAuthentication( ) )
        {
            try
            {
                user = SecurityService.getInstance( ).getRemoteUser( request );
            }
            catch ( UserNotSignedException e )
            {
                // Nothing to do : lutece user is not mandatory
            }
        }

        model.put( MARK_USER, user );

        if ( request != null )
        {
            AppointmentDTO appointment = getAppointmentFromSession( request.getSession( ) );

            if ( appointment != null && appointment.getMapResponsesByIdEntry( ) != null )
            {
                List<Response> listResponses = appointment.getMapResponsesByIdEntry( ).get( entry.getIdEntry( ) );
                model.put( MARK_LIST_RESPONSES, listResponses );
            }
        }

        template = AppTemplateService
                .getTemplate( EntryTypeServiceManager.getEntryTypeService( entry ).getHtmlCode( entry, bDisplayFront ),
                        locale, model );
        stringBuffer.append( template.getHtml( ) );
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
        List<Response> listResponse = new ArrayList<Response>( );
        appointment.getMapResponsesByIdEntry( ).put( nIdEntry, listResponse );
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
        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
        Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

        List<Field> listField = new ArrayList<Field>( );

        for ( Field field : entry.getFields( ) )
        {
            field = FieldHome.findByPrimaryKey( field.getIdField( ) );
            listField.add( field );
        }

        entry.setFields( listField );

        if ( entry.getEntryType( ).getGroup( ) )
        {
            for ( Entry entryChild : entry.getChildren( ) )
            {
                List<Response> listResponseChild = new ArrayList<Response>( );
                appointment.getMapResponsesByIdEntry( ).put( entryChild.getIdEntry( ), listResponseChild );

                listFormErrors.addAll( getResponseEntry( request, entryChild.getIdEntry( ), listResponseChild, false,
                        locale, appointment ) );
            }
        }
        else if ( !entry.getEntryType( ).getComment( ) )
        {
            GenericAttributeError formError = null;

            if ( !bResponseNull )
            {
                formError = EntryTypeServiceManager.getEntryTypeService( entry ).getResponseData( entry, request,
                        listResponse, locale );

                if ( formError != null )
                {
                    formError.setUrl( getEntryUrl( entry ) );
                }
            }
            else
            {
                Response response = new Response( );
                response.setEntry( entry );
                listResponse.add( response );
            }

            if ( formError != null )
            {
                entry.setError( formError );
                listFormErrors.add( formError );
            }

            if ( entry.getNumberConditionalQuestion( ) != 0 )
            {
                for ( Field field : entry.getFields( ) )
                {
                    boolean bIsFieldInResponseList = isFieldInTheResponseList( field.getIdField( ), listResponse );

                    for ( Entry conditionalEntry : field.getConditionalQuestions( ) )
                    {
                        listFormErrors.addAll( getResponseEntry( request, conditionalEntry.getIdEntry( ), listResponse,
                                !bIsFieldInResponseList, locale, appointment ) );
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
            if ( ( response.getField( ) != null ) && ( response.getField( ).getIdField( ) == nIdField ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the URL to modify an entry of the form in front office
     * @param entry the entry
     * @return The URL to modify the entry in front office
     */
    public String getEntryUrl( Entry entry )
    {
        UrlItem url = new UrlItem( AppPathService.getPortalUrl( ) );
        url.addParameter( XPageAppService.PARAM_XPAGE_APP, AppointmentPlugin.PLUGIN_NAME );
        url.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_GET_FORM );

        if ( ( entry != null ) && ( entry.getIdResource( ) > 0 ) )
        {
            url.addParameter( PARAMETER_ID_FORM, entry.getIdResource( ) );
            url.setAnchor( PREFIX_ATTRIBUTE + entry.getIdEntry( ) );
        }

        return url.getUrl( );
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
}
