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
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDay;
import fr.paris.lutece.plugins.appointment.service.AppointmentFormService;
import fr.paris.lutece.plugins.appointment.service.CalendarService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.beanvalidation.BeanValidationUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;

import org.apache.commons.lang.StringUtils;


/**
 * This class provides a simple implementation of an XPage
 */
@Controller( xpageName = "appointment", pageTitleI18nKey = "appointment.appointmentApp.defaultTitle", pagePathI18nKey = "appointment.appointmentApp.defaultPath" )
public class AppointmentApp extends MVCApplication
{
    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = 5741361182728887387L;

    private static final String TEMPLATE_APPOINTMENT_FORM_LIST = "/skin/plugins/appointment/appointment_form_list.html";
    private static final String TEMPLATE_APPOINTMENT_FORM = "/skin/plugins/appointment/appointment_form.html";
    private static final String TEMPLATE_APPOINTMENT_FORM_CALENDAR = "/skin/plugins/appointment/appointment_form_calendar.html";

    private static final String VIEW_APPOINTMENT_FORM_LIST = "getViewFormList";
    private static final String VIEW_GET_FORM = "viewForm";
    private static final String VIEW_GET_APPOINTMENT_CALENDAR = "getAppointmentCalendar";
    private static final String VIEW_DISPLAY_RECAP_APPOINTMENT = "displayRecapAppointment";

    private static final String ACTION_DO_VALIDATE_FORM = "doValidateForm";

    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_NB_WEEK = "nb_week";
    private static final String PARAMETER_EMAIL = "email";
    private static final String PARAMETER_FIRST_NAME = "firstname";
    private static final String PARAMETER_LAST_NAME = "lastname";

    private static final String MARK_FORM_LIST = "form_list";
    private static final String MARK_FORM_HTML = "form_html";
    private static final String MARK_FORM_ERRORS = "form_errors";
    private static final String MARK_LIST_DAYS = "listDays";
    private static final String MARK_FORM = "form";
    private static final String MARK_LIST_TIME_BEGIN = "list_time_begin";
    private static final String MARK_MIN_DURATION_APPOINTMENT = "min_duration_appointments";

    private static final String SESSION_APPOINTMENT_FORM_ERRORS = "appointment.session.formErrors";
    private final AppointmentFormService _appointmentFormService = SpringContextService
            .getBean( AppointmentFormService.BEAN_NAME );
    private CaptchaSecurityService _captchaSecurityService = new CaptchaSecurityService( );

    /**
     * Get the list of appointment form list
     * @param request The request
     * @return The XPage to display
     */
    @View( value = VIEW_APPOINTMENT_FORM_LIST, defaultView = true )
    public XPage getFormList( HttpServletRequest request )
    {
        _appointmentFormService.removeAppointmentFromSession( request.getSession( ) );

        Map<String, Object> model = new HashMap<String, Object>( );

        Collection<AppointmentForm> listAppointmentForm = AppointmentFormHome.getActiveAppointmentFormsList( );
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

        if ( ( strIdForm != null ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

            if ( ( form == null ) || !form.getIsActive( ) )
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

            _appointmentFormService.removeAppointmentFromSession( request.getSession( ) );

            XPage page = getXPage( TEMPLATE_APPOINTMENT_FORM, getLocale( request ), model );

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

        if ( ( strIdForm != null ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            EntryFilter filter = new EntryFilter( );
            filter.setIdResource( nIdForm );
            filter.setResourceType( AppointmentForm.RESOURCE_TYPE );
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
            filter.setIdIsComment( EntryFilter.FILTER_FALSE );

            List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );

            _appointmentFormService.removeAppointmentFromSession( request.getSession( ) );

            List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
            Locale locale = request.getLocale( );

            Appointment appointment = new Appointment( );
            appointment.setEmail( request.getParameter( PARAMETER_EMAIL ) );
            appointment.setFirstName( request.getParameter( PARAMETER_FIRST_NAME ) );
            appointment.setLastName( request.getParameter( PARAMETER_LAST_NAME ) );

            // We save the appointment in session. The appointment object will contain responses of the user to the form
            _appointmentFormService.saveAppointmentInSession( request.getSession( ), appointment );

            Set<ConstraintViolation<Appointment>> listErrors = BeanValidationUtil.validate( appointment );
            if ( !listErrors.isEmpty( ) )
            {
                for ( ConstraintViolation<Appointment> constraintViolation : listErrors )
                {
                    GenericAttributeError genAttError = new GenericAttributeError( );
                    genAttError.setErrorMessage( constraintViolation.getMessage( ) );
                    listFormErrors.add( genAttError );
                }
            }

            for ( Entry entry : listEntryFirstLevel )
            {
                listFormErrors.addAll( _appointmentFormService.getResponseEntry( request, entry.getIdEntry( ), false,
                        locale, appointment ) );
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

        if ( ( strIdForm != null ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            if ( _appointmentFormService.getAppointmentFromSession( request.getSession( ) ) == null )
            {
                return redirect( request, VIEW_GET_FORM, PARAMETER_ID_FORM, nIdForm );
            }

            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

            Map<String, Object> model = new HashMap<String, Object>( );

            String strNbWeek = request.getParameter( PARAMETER_NB_WEEK );
            int nNbWeek = 0;

            if ( StringUtils.isNotEmpty( strNbWeek ) && StringUtils.isNumeric( strNbWeek ) )
            {
                nNbWeek = Integer.parseInt( strNbWeek );

                if ( nNbWeek > form.getNbWeeksToDisplay( ) )
                {
                    nNbWeek = form.getNbWeeksToDisplay( );
                }
            }

            List<AppointmentDay> listDays = CalendarService.getService( ).getDayListforCalendar( form, nNbWeek, true );

            // We compute slots interval
            List<String> listTimeBegin = null;

            int nMinOpeningHour = form.getOpeningHour( );
            int nMinOpeningMinutes = form.getOpeningMinutes( );
            int nMaxClosingHour = form.getClosingHour( );
            int nMaxClosingMinutes = form.getClosingMinutes( );
            int nMinAppointmentDuration = form.getDurationAppointments( );

            for ( AppointmentDay appointmentDay : listDays )
            {
                if ( appointmentDay.getIsOpen( ) && ( appointmentDay.getIdDay( ) > 0 ) )
                {
                    // we check that the day has is not a longer or has a shorter appointment duration 
                    if ( appointmentDay.getAppointmentDuration( ) < nMinAppointmentDuration )
                    {
                        nMinAppointmentDuration = appointmentDay.getAppointmentDuration( );
                    }

                    if ( appointmentDay.getOpeningHour( ) < nMinOpeningHour )
                    {
                        nMinOpeningHour = appointmentDay.getOpeningHour( );
                    }

                    if ( appointmentDay.getOpeningMinutes( ) < nMinOpeningMinutes )
                    {
                        nMinOpeningMinutes = appointmentDay.getOpeningMinutes( );
                    }

                    if ( appointmentDay.getClosingHour( ) > nMaxClosingHour )
                    {
                        nMaxClosingHour = appointmentDay.getClosingHour( );
                    }

                    if ( appointmentDay.getClosingMinutes( ) > nMaxClosingMinutes )
                    {
                        nMaxClosingMinutes = appointmentDay.getClosingMinutes( );
                    }
                }
            }

            listTimeBegin = CalendarService.getService( ).getListAppointmentTimes( nMinAppointmentDuration,
                    nMinOpeningHour, nMinOpeningMinutes, nMaxClosingHour, nMaxClosingMinutes );

            model.put( MARK_FORM, form );
            model.put( MARK_LIST_DAYS, listDays );
            model.put( MARK_LIST_TIME_BEGIN, listTimeBegin );
            model.put( MARK_MIN_DURATION_APPOINTMENT, nMinAppointmentDuration );
            model.put( PARAMETER_NB_WEEK, nNbWeek );

            return getXPage( TEMPLATE_APPOINTMENT_FORM_CALENDAR, getLocale( request ), model );
        }

        return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
    }

    @View( VIEW_DISPLAY_RECAP_APPOINTMENT )
    public String displayRecapAppointment( HttpServletRequest request )
    {
        //        Map<Integer, List<Response>> mapResponses
        Appointment appointment = _appointmentFormService.getAppointmentFromSession( request.getSession( ) );
        return null;
    }
}
