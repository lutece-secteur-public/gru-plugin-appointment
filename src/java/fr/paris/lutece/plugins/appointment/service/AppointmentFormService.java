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

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.io.Serializable;
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

    // marks
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_FIELD = "field";
    private static final String MARK_STR_LIST_CHILDREN = "str_list_entry_children";
    private static final String MARK_FORM = "form";
    private static final String MARK_STR_ENTRY = "str_entry";
    private static final String MARK_USER = "user";

    private static final String SESSION_KEY_RESPONSES = "appointment.appointmentFormService.responses";

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
        List<Entry> listEntryFirstLevel;
        Map<String, Object> model = new HashMap<String, Object>( );
        HtmlTemplate template;
        EntryFilter filter;
        StringBuffer strBuffer = new StringBuffer( );
        filter = new EntryFilter( );
        filter.setIdResource( form.getIdForm( ) );
        filter.setResourceType( AppointmentForm.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        listEntryFirstLevel = EntryHome.getEntryList( filter );

        for ( Entry entry : listEntryFirstLevel )
        {
            getHtmlEntry( entry.getIdEntry( ), strBuffer, locale, bDisplayFront, request );
        }

        model.put( MARK_FORM, form );
        model.put( MARK_STR_ENTRY, strBuffer.toString( ) );
        model.put( MARK_LOCALE, locale );

        template = AppTemplateService.getTemplate( TEMPLATE_HTML_CODE_FORM, locale, model );

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

        //        if ( ( request != null ) && ( request.getSession( ) != null ) )
        //        {
        //            Map<Integer, List<Response>> listSubmittedResponses = getResponses( request.getSession( ) );
        //
        //            if ( listSubmittedResponses != null )
        //            {
        //                List<Response> listResponses = listSubmittedResponses.get( entry.getIdEntry( ) );
        //
        //                if ( listResponses != null )
        //                {
        //                    model.put( MARK_LIST_RESPONSES, listResponses );
        //                }
        //            }
        //        }

        template = AppTemplateService
                .getTemplate( EntryTypeServiceManager.getEntryTypeService( entry ).getHtmlCode( entry, bDisplayFront ),
                        locale, model );
        stringBuffer.append( template.getHtml( ) );
    }

    /**
     * Save appointment form responses in the session of the user
     * @param session The session
     * @param listResponses The list of responses to save
     */
    public void saveResponseInSession( HttpSession session, List<Response> listResponses )
    {
        session.setAttribute( SESSION_KEY_RESPONSES, listResponses );
    }

    /**
     * Remove any appointment form responses stored in the session of the user
     * @param session The session
     */
    public void removeResponsesFromSession( HttpSession session )
    {
        session.removeAttribute( SESSION_KEY_RESPONSES );
    }
}
