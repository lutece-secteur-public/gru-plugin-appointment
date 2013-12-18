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

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentFormService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * This class provides a simple implementation of an XPage
 */
@Controller( xpageName = "appointment", pageTitleProperty = "appointment.appointmentApp.defaultTitle", pagePathProperty = "appointment.appointmentApp.defaultPath" )
public class AppointmentApp extends MVCApplication
{
    private static final String TEMPLATE_APPOINTMENT_FORM_LIST = "/skin/plugins/appointment/appointment_form_list.html";
    private static final String TEMPLATE_APPOINTMENT_FORM = "/skin/plugins/appointment/appointment_form.html";

    private static final String VIEW_APPOINTMENT_FORM_LIST = "getViewFormList";
    private static final String VIEW_GET_FORM = "viewForm";
    private static final String VIEW_GET_APPOINTMENT_CALENDAR = "getAppointmentCalendar";

    private static final String ACTION_DO_VALIDATE_FORM = "doValidateForm";

    private static final String PARAMETER_ID_FORM = "id_form";

    private static final String MARK_FORM_LIST = "form_list";
    private static final String MARK_FORM_HTML = "form_html";
    private static final String MARK_FORM_ERRORS = "form_errors";

    private static final String SESSION_APPOINTMENT_FORM_ERRORS = "appointment.session.formErrors";

    private final AppointmentFormService _appointmentFormService = SpringContextService
            .getBean( AppointmentFormService.BEAN_NAME );

    /**
     * Get the list of appointment form list
     * @param request The request
     * @return The XPage to display
     */
    @View( value = VIEW_APPOINTMENT_FORM_LIST, defaultView = true )
    public XPage getFormList( HttpServletRequest request )
    {
        _appointmentFormService.removeResponsesFromSession( request.getSession( ) );
        Map<String, Object> model = new HashMap<String, Object>( );

        Collection<AppointmentForm> listAppointmentForm = AppointmentFormHome.getAppointmentFormsList( );
        model.put( MARK_FORM_LIST, listAppointmentForm );
        return getXPage( TEMPLATE_APPOINTMENT_FORM_LIST, request.getLocale( ), model );
    }

    /**
     * Get the page to complete a form
     * @param request The request
     * @return The XPage to display
     */
    @View( VIEW_GET_FORM )
    public XPage getViewForm( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( strIdForm != null && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

            if ( form == null || !form.getIsActive( ) )
            {
                return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
            }

            Map<String, Object> model = new HashMap<String, Object>( );

            model.put( MARK_FORM_HTML, _appointmentFormService.getHtmlForm( form, getLocale( request ), true, request ) );
            List<GenericAttributeError> listErrors = (List<GenericAttributeError>) request.getSession( ).getAttribute(
                    SESSION_APPOINTMENT_FORM_ERRORS );
            if ( listErrors != null )
            {
                model.put( MARK_FORM_ERRORS, listErrors );
                request.getSession( ).removeAttribute( SESSION_APPOINTMENT_FORM_ERRORS );
            }

            _appointmentFormService.removeResponsesFromSession( request.getSession( ) );

            XPage page = getXPage( TEMPLATE_APPOINTMENT_FORM, request.getLocale( ), model );
            if ( form.getDisplayTitleFo( ) )
            {
                page.setTitle( form.getTitle( ) );
            }
            return page;
        }
        return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
    }

    /**
     * Do validate data entered by a user to fill a form
     * @param request The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_VALIDATE_FORM )
    public XPage doValidateForm( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( strIdForm != null && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            EntryFilter filter = new EntryFilter( );
            filter.setIdResource( nIdForm );
            filter.setResourceType( AppointmentForm.RESOURCE_TYPE );
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
            filter.setIdIsComment( EntryFilter.FILTER_FALSE );

            List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );

            _appointmentFormService.removeResponsesFromSession( request.getSession( ) );

            List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
            Locale locale = request.getLocale( );
            for ( Entry entry : listEntryFirstLevel )
            {
                listFormErrors.addAll( _appointmentFormService.getResponseEntry( request, entry.getIdEntry( ), false,
                        locale ) );
            }

            // If there is some errors, we redirect the user to the form page
            if ( listFormErrors.size( ) > 0 )
            {
                request.getSession( ).setAttribute( SESSION_APPOINTMENT_FORM_ERRORS, listFormErrors );
                return redirect( request, VIEW_GET_FORM, PARAMETER_ID_FORM, nIdForm );
            }

            return redirect( request, VIEW_GET_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, nIdForm );
        }
        return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
    }

    /**
     * Get the page with the calendar with opened and closed days for an
     * appointment form
     * @param request The request
     * @return The XPage to display
     */
    @View( VIEW_GET_APPOINTMENT_CALENDAR )
    public XPage getAppointmentCalendar( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( strIdForm != null && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );
            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

        }
        return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
    }
}