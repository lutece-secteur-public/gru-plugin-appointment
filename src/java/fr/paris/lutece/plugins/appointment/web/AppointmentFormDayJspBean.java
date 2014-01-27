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
import fr.paris.lutece.plugins.appointment.business.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDay;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDayHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.AppointmentSlotService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import org.dozer.converters.DateConverter;

import java.sql.Date;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * JspBean to manage days
 */
@Controller( controllerJsp = "ManageAppointmentFormDays.jsp", controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM )
public class AppointmentFormDayJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = -4951787792196104967L;

    // Parameters
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_ID_DAY = "id_day";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_IS_OPEN = "isOpen";
    private static final String PARAMETER_OPENING_TIME = "openingTime";
    private static final String PARAMETER_CLOSING_TIME = "closingTime";
    private static final String PARAMETER_DATE = "date";
    private static final String PARAMETER_APPOINTMENT_DURATION = "appointmentDuration";
    private static final String PARAMETER_PEOPLE_PER_APPOINTMENT = "peoplePerAppointment";
    private static final String PARAMETER_NB_WEEK = "nb_week";

    // Messages
    //    private static final String MESSAGE_CONFIRM_REMOVE_DAY = "appointment.message.confirmRemoveDay";
    private static final String MESSAGE_ERROR_OPENING_TIME_FORMAT = "appointment.modify_appointmentForm.patternTimeStart";
    private static final String MESSAGE_ERROR_CLOSING_TIME_FORMAT = "appointment.modify_appointmentForm.patternTimeEnd";
    private static final String MESSAGE_ERROR_DATE_FORMAT = "appointment.message.error.dayDateFormat";
    private static final String MESSAGE_ERROR_FORMAT_APPOINTMENT_DURATION = "appointment.message.error.formatNumberAppointmentDuration";
    private static final String MESSAGE_ERROR_FORMAT_PEOPLE_PER_APPOINTMENT = "appointment.message.error.formatPeoplePerAppointmentDuration";
    private static final String MESSAGE_ERROR_DAY_ALREADY_EXIST = "appointment.message.error.dayAlreadyExist";
    private static final String MESSAGE_ERROR_DAY_DURATION_APPOINTMENT_NOT_MULTIPLE_FORM = "appointment.message.error.durationAppointmentDayNotMultipleForm";
    private static final String MESSAGE_ERROR_FORM_HAS_APPOINTMENTS = "appointment.message.error.refreshDays.formHasAppointments";
    private static final String MESSAGE_CONFIRM_REFRESH_DAYS = "appointment.message.confirmRefreshDays";
    private static final String INFO_MODIFY_APPOINTMENTDAY_SLOTS_UPDATED = "appointment.info.appointmentDay.slotsUpdated";

    // Page titles
    private static final String PROPERTY_MODIFY_DAY_TITLE = "appointment.modifyDay.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENTFORM_DAYS = "appointment.modify_appointmentformDays.pageTitle";

    // Views
    private static final String VIEW_MODIFY_APPOINTMENTFORM_DAYS = "modifyAppointmentFormDays";
    private static final String VIEW_CONFIRM_REFRESH_DAYS = "confirmRefreshDays";
    private static final String VIEW_GET_MODIFY_DAY = "getModifyDay";

    // Actions
    private static final String ACTION_DO_MODIFY_DAY = "doModifyDay";
    private static final String ACTION_DO_REFRESH_DAYS = "doRefreshDays";

    // Marks
    private static final String MARK_DAY = "day";
    private static final String MARK_LIST_DAYS = "listDays";
    private static final String MARK_DATE_MIN = "dateMin";
    private static final String MARK_DATE_MAX = "dateMax";

    // Templates
    private static final String TEMPLATE_MODIFY_DAY = "/admin/plugins/appointment/modify_days.html";
    private static final String TEMPLATE_MODIFY_APPOINTMENTFORM_DAYS = "/admin/plugins/appointment/appointmentform/modify_appointmentform_days.html";

    // Urls
    private static final String JSP_MANAGE_APPOINTMENTFORMS_DAYS = "jsp/admin/plugins/appointment/ManageAppointmentFormDays.jsp";

    // Constants
    private static final String CONSTANT_H = "h";
    private static final String CONSTANT_TIME_REGEX = "^[0-2][0-9]h[0-5][0-9]$";

    // Local variables
    private transient DateConverter _dateConverter;
    private transient AppointmentDay _appointmentDay;
    private int _nNbWeek;

    /**
     * Get the page to manage days of an appointment form
     * @param request The request
     * @return the HTML content to display, or the next URL to redirect to
     */
    @View( VIEW_MODIFY_APPOINTMENTFORM_DAYS )
    public String getModifyFormDays( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            String strNbWeek = request.getParameter( PARAMETER_NB_WEEK );

            if ( StringUtils.isNotEmpty( strNbWeek ) )
            {
                try
                {
                    _nNbWeek = Integer.parseInt( strNbWeek );
                }
                catch ( NumberFormatException nfe )
                {
                    _nNbWeek = 0;
                }
            }

            AppointmentForm appointmentForm = AppointmentFormHome.findByPrimaryKey( nIdForm );

            Date dateMin = AppointmentService.getService(  ).getDateMonday( _nNbWeek );
            Calendar calendar = GregorianCalendar.getInstance( Locale.FRANCE );
            calendar.setTime( dateMin );
            calendar.add( Calendar.DAY_OF_MONTH, 6 );

            Date dateMax = new Date( calendar.getTimeInMillis(  ) );

            List<AppointmentDay> listDays = AppointmentDayHome.getDaysBetween( appointmentForm.getIdForm(  ), dateMin,
                    dateMax );

            AppointmentDayHome.getDaysBetween( nIdForm, dateMin, dateMax );

            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_LIST_DAYS, listDays );
            model.put( PARAMETER_NB_WEEK, _nNbWeek );
            model.put( MARK_DATE_MIN, dateMin );
            model.put( MARK_DATE_MAX, dateMax );
            AppointmentFormJspBean.addElementsToModelForLeftColumn( request, appointmentForm, getUser(  ),
                getLocale(  ), model );

            return getPage( PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENTFORM_DAYS, TEMPLATE_MODIFY_APPOINTMENTFORM_DAYS, model );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Gets the entry modification page
     * @param request The HTTP request
     * @return The entry modification page
     */
    @View( VIEW_GET_MODIFY_DAY )
    public String getModifyDay( HttpServletRequest request )
    {
        String strIdDay = request.getParameter( PARAMETER_ID_DAY );

        if ( StringUtils.isNotEmpty( strIdDay ) && StringUtils.isNumeric( strIdDay ) )
        {
            int nIdDay = Integer.parseInt( strIdDay );

            AppointmentDay day;

            if ( ( _appointmentDay != null ) && ( _appointmentDay.getIdDay(  ) == nIdDay ) )
            {
                day = _appointmentDay;
                _appointmentDay = null;
            }
            else
            {
                day = AppointmentDayHome.findByPrimaryKey( nIdDay );
            }

            if ( nIdDay <= 0 )
            {
                return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
            }

            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_DAY, day );
            fillCommons( model );

            return getPage( PROPERTY_MODIFY_DAY_TITLE, TEMPLATE_MODIFY_DAY, model );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Perform the entry modification
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_DO_MODIFY_DAY )
    public String doModifyDay( HttpServletRequest request )
    {
        String strIdDay = request.getParameter( PARAMETER_ID_DAY );

        if ( StringUtils.isNotEmpty( strIdDay ) && StringUtils.isNumeric( strIdDay ) )
        {
            int nIdDay = Integer.parseInt( strIdDay );

            if ( nIdDay <= 0 )
            {
                return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
            }

            AppointmentDay day = AppointmentDayHome.findByPrimaryKey( nIdDay );

            if ( StringUtils.isEmpty( request.getParameter( PARAMETER_CANCEL ) ) )
            {
                AppointmentForm form = AppointmentFormHome.findByPrimaryKey( day.getIdForm(  ) );
                List<String> listErrors = populateDay( day, form, request );

                if ( ( listErrors != null ) && ( listErrors.size(  ) > 0 ) )
                {
                    Locale locale = request.getLocale(  );

                    for ( String strError : listErrors )
                    {
                        addError( strError, locale );
                    }

                    _appointmentDay = day;

                    return redirect( request, VIEW_MODIFY_APPOINTMENTFORM_DAYS, PARAMETER_ID_DAY, day.getIdDay(  ) );
                }

                AppointmentDay dayFromDb = AppointmentDayHome.findByPrimaryKey( day.getIdDay(  ) );
                AppointmentDayHome.update( day );

                if ( AppointmentSlotService.getInstance(  ).checkForDayModification( day, dayFromDb ) )
                {
                    addInfo( INFO_MODIFY_APPOINTMENTDAY_SLOTS_UPDATED, getLocale(  ) );
                }
            }

            return redirect( request, VIEW_MODIFY_APPOINTMENTFORM_DAYS, PARAMETER_ID_FORM, day.getIdForm(  ) );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Get the page to confirm the refreshment of days of a form
     * @param request The request
     * @return The next URL to redirect to
     */
    @View( VIEW_CONFIRM_REFRESH_DAYS )
    public String getConfirmRefreshDays( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            UrlItem urlItem = new UrlItem( getActionUrl( ACTION_DO_REFRESH_DAYS ) );
            urlItem.addParameter( PARAMETER_ID_FORM, strIdForm );

            return redirect( request,
                AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REFRESH_DAYS, urlItem.getUrl(  ),
                    AdminMessage.TYPE_CONFIRMATION ) );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Do remove days of the comings weeks and recreate them
     * @param request The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_REFRESH_DAYS )
    public String doRefreshDays( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );
            int nNbAppointments = AppointmentHome.countAppointmentsByIdForm( nIdForm,
                    AppointmentService.getService(  ).getDateLastMonday(  ) );

            if ( nNbAppointments > 0 )
            {
                return redirect( request,
                    AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_FORM_HAS_APPOINTMENTS,
                        getURLManageAppointmentFormDays( request, strIdForm ), AdminMessage.TYPE_STOP ) );
            }

            AppointmentService.getService(  ).resetFormDays( AppointmentFormHome.findByPrimaryKey( nIdForm ) );

            return redirect( request, VIEW_MODIFY_APPOINTMENTFORM_DAYS, PARAMETER_ID_FORM, nIdForm );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Populate a day from data in an HTTP request
     * @param day The day to populate
     * @param form The appointment form associated with the day to populate
     * @param request The request
     * @return The list of error, or an empty list if no error was found
     */
    private List<String> populateDay( AppointmentDay day, AppointmentForm form, HttpServletRequest request )
    {
        List<String> listErrors = new ArrayList<String>(  );
        String strDate = request.getParameter( PARAMETER_DATE );
        Date date = (Date) getDateConverter(  ).convert( java.sql.Date.class, strDate );

        if ( date == null )
        {
            listErrors.add( MESSAGE_ERROR_DATE_FORMAT );
        }
        else
        {
            // We check that the day does not already exist
            List<AppointmentDay> listDays = AppointmentDayHome.getDaysBetween( day.getIdForm(  ), date, date );

            if ( ( listDays != null ) && ( listDays.size(  ) > 0 ) )
            {
                if ( day.getIdDay(  ) == 0 )
                {
                    listErrors.add( MESSAGE_ERROR_DAY_ALREADY_EXIST );
                }
                else
                {
                    // If there is a day for this form that has the same date but a different id, we add an error
                    for ( AppointmentDay dayFound : listDays )
                    {
                        if ( dayFound.getIdDay(  ) != day.getIdDay(  ) )
                        {
                            listErrors.add( MESSAGE_ERROR_DAY_ALREADY_EXIST );

                            break;
                        }
                    }
                }
            }
        }

        day.setDate( date );

        boolean bIsOpen = Boolean.parseBoolean( request.getParameter( PARAMETER_IS_OPEN ) );
        day.setIsOpen( bIsOpen );

        if ( bIsOpen )
        {
            String strOpeningTime = request.getParameter( PARAMETER_OPENING_TIME );

            if ( ( strOpeningTime != null ) && strOpeningTime.matches( CONSTANT_TIME_REGEX ) )
            {
                String[] strArrayOpeningTime = strOpeningTime.split( CONSTANT_H );
                day.setOpeningHour( Integer.parseInt( strArrayOpeningTime[0] ) );
                day.setOpeningMinutes( Integer.parseInt( strArrayOpeningTime[1] ) );
            }
            else
            {
                listErrors.add( MESSAGE_ERROR_OPENING_TIME_FORMAT );
            }

            String strClosingTime = request.getParameter( PARAMETER_CLOSING_TIME );

            if ( ( strClosingTime != null ) && strClosingTime.matches( CONSTANT_TIME_REGEX ) )
            {
                String[] strArrayClosingTime = strClosingTime.split( CONSTANT_H );
                day.setClosingHour( Integer.parseInt( strArrayClosingTime[0] ) );
                day.setClosingMinutes( Integer.parseInt( strArrayClosingTime[1] ) );
            }
            else
            {
                listErrors.add( MESSAGE_ERROR_CLOSING_TIME_FORMAT );
            }

            String strDuration = request.getParameter( PARAMETER_APPOINTMENT_DURATION );

            if ( StringUtils.isNotEmpty( strDuration ) && StringUtils.isNumeric( strDuration ) )
            {
                day.setAppointmentDuration( Integer.parseInt( strDuration ) );

                if ( ( form.getDurationAppointments(  ) != day.getAppointmentDuration(  ) ) &&
                        ( form.getDurationAppointments(  ) > day.getAppointmentDuration(  ) ) )
                {
                    if ( ( form.getDurationAppointments(  ) % day.getAppointmentDuration(  ) ) != 0 )
                    {
                        listErrors.add( MESSAGE_ERROR_DAY_DURATION_APPOINTMENT_NOT_MULTIPLE_FORM );
                    }
                }
                else
                {
                    if ( ( day.getAppointmentDuration(  ) % form.getDurationAppointments(  ) ) != 0 )
                    {
                        listErrors.add( MESSAGE_ERROR_DAY_DURATION_APPOINTMENT_NOT_MULTIPLE_FORM );
                    }
                }
            }
            else
            {
                listErrors.add( MESSAGE_ERROR_FORMAT_APPOINTMENT_DURATION );
            }

            String strPeoplePerAppointment = request.getParameter( PARAMETER_PEOPLE_PER_APPOINTMENT );

            if ( StringUtils.isNotEmpty( strPeoplePerAppointment ) && StringUtils.isNumeric( strPeoplePerAppointment ) )
            {
                day.setPeoplePerAppointment( Integer.parseInt( strPeoplePerAppointment ) );
            }
            else
            {
                listErrors.add( MESSAGE_ERROR_FORMAT_PEOPLE_PER_APPOINTMENT );
            }
        }
        else
        {
            day.setOpeningHour( 0 );
            day.setOpeningMinutes( 0 );
            day.setClosingHour( 0 );
            day.setClosingMinutes( 0 );
            day.setAppointmentDuration( 0 );
            day.setPeoplePerAppointment( 0 );
        }

        return listErrors;
    }

    /**
     * Get the URL to manage appointment forms
     * @param request The request
     * @param strIdForm The id of the form to manage days of
     * @return The URL to manage appointment forms
     */
    public static String getURLManageAppointmentFormDays( HttpServletRequest request, String strIdForm )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_APPOINTMENTFORMS_DAYS );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MODIFY_APPOINTMENTFORM_DAYS );
        urlItem.addParameter( PARAMETER_ID_FORM, strIdForm );

        return urlItem.getUrl(  );
    }

    /**
     * Get the converter to convert string to java.sql.Date.
     * @return The converter to convert String to java.sql.Date.
     */
    private DateConverter getDateConverter(  )
    {
        if ( _dateConverter == null )
        {
            _dateConverter = new DateConverter( DateFormat.getDateInstance( DateFormat.SHORT, Locale.FRANCE ) );
        }

        return _dateConverter;
    }
}
