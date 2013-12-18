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

import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDay;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDayHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.dozer.converters.DateConverter;


/**
 * JspBean to manage days
 */
@Controller( controllerJsp = "ManageAppointmentFormDays.jsp", controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM )
public class AppointmentFormDayJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = -4951787792196104967L;

    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_ID_DAY = "id_day";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_IS_OPEN = "isOpen";
    private static final String PARAMETER_OPENING_TIME = "openingTime";
    private static final String PARAMETER_CLOSING_TIME = "closingTime";
    private static final String PARAMETER_DATE = "date";
    private static final String PARAMETER_APPOINTMENT_DURATION = "appointmentDuration";
    private static final String PARAMETER_PEOPLE_PER_APPOINTMENT = "peoplePerAppointment";

    private static final String MESSAGE_CONFIRM_REMOVE_DAY = "appointment.message.confirmRemoveDay";
    private static final String MESSAGE_ERROR_OPENING_TIME_FORMAT = "appointment.modify_appointmentForm.patternTimeStart";
    private static final String MESSAGE_ERROR_CLOSING_TIME_FORMAT = "appointment.modify_appointmentForm.patternTimeEnd";
    private static final String MESSAGE_ERROR_DATE_FORMAT = "appointment.message.error.dayDateFormat";
    private static final String MESSAGE_ERROR_FORMAT_APPOINTMENT_DURATION = "appointment.message.error.formatNumberAppointmentDuration";
    private static final String MESSAGE_ERROR_FORMAT_PEOPLE_PER_APPOINTMENT = "appointment.message.error.formatPeoplePerAppointmentDuration";
    private static final String MESSAGE_ERROR_DAY_ALREADY_EXIST = "appointment.message.error.dayAlreadyExist";

    private static final String PROPERTY_CREATE_DAY_TITLE = "appointment.createDay.pageTitle";
    private static final String PROPERTY_MODIFY_DAY_TITLE = "appointment.modifyDay.pageTitle";

    private static final String VIEW_GET_CREATE_DAY = "getCreateDay";
    private static final String VIEW_GET_MODIFY_DAY = "getModifyDay";
    private static final String VIEW_CONFIRM_REMOVE_DAY = "confirmRemoveDay";

    private static final String ACTION_DO_CREATE_DAY = "doCreateDay";
    private static final String ACTION_DO_MODIFY_DAY = "doModifyDay";
    private static final String ACTION_DO_REMOVE_DAY = "doRemoveDay";

    private static final String MARK_DAY = "day";

    private static final String TEMPLATE_CREATE_DAY = "/admin/plugins/appointment/create_days.html";
    private static final String TEMPLATE_MODIFY_DAY = "/admin/plugins/appointment/modify_days.html";

    private static final String CONSTANT_H = "h";
    private static final String CONSTANT_TIME_REGEX = "^[0-2][0-9]h[0-5][0-9]$";

    private DateConverter _dateConverter = new DateConverter( DateFormat.getDateInstance( DateFormat.SHORT,
            Locale.FRANCE ) );
    private AppointmentDay _appointmentDay;

    /**
     * Get the HTML code to create an entry
     * @param request The request
     * @return The HTML code to display or the next URL to redirect to
     */
    @View( value = VIEW_GET_CREATE_DAY )
    public String getCreateDay( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( StringUtils.isEmpty( strIdForm ) || !StringUtils.isNumeric( strIdForm ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        // Default Values
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( PARAMETER_ID_FORM, strIdForm );
        if ( _appointmentDay != null )
        {
            model.put( MARK_DAY, _appointmentDay );
            _appointmentDay = null;
        }
        fillCommons( model );
        return getPage( PROPERTY_CREATE_DAY_TITLE, TEMPLATE_CREATE_DAY, model );
    }

    /**
     * Do create a day
     * @param request the request
     * @return The HTML code to display or the next URL to redirect to
     */
    @Action( ACTION_DO_CREATE_DAY )
    public String doCreateDay( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_CANCEL ) ) )
            {
                return redirect( request, AppointmentFormJspBean.getURLManageAppointmentFormDays( request, strIdForm ) );
            }

            AppointmentDay day = new AppointmentDay( );
            day.setIdForm( Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) ) );
            List<String> listErrors = populateDay( day, request );

            if ( listErrors != null && listErrors.size( ) > 0 )
            {
                Locale locale = request.getLocale( );
                for ( String strError : listErrors )
                {
                    addError( strError, locale );
                }
                _appointmentDay = day;
                return redirect( request, VIEW_GET_CREATE_DAY, PARAMETER_ID_FORM, day.getIdForm( ) );
            }
            AppointmentDayHome.create( day );

            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentFormDays( request, strIdForm ) );
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
            if ( _appointmentDay != null && _appointmentDay.getIdDay( ) == nIdDay )
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

            Map<String, Object> model = new HashMap<String, Object>( );
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
                List<String> listErrors = populateDay( day, request );
                if ( listErrors != null && listErrors.size( ) > 0 )
                {
                    Locale locale = request.getLocale( );
                    for ( String strError : listErrors )
                    {
                        addError( strError, locale );
                    }
                    _appointmentDay = day;
                    return redirect( request, VIEW_GET_MODIFY_DAY, PARAMETER_ID_DAY, day.getIdDay( ) );
                }
                AppointmentDayHome.update( day );
            }
            return redirect( request,
                    AppointmentFormJspBean.getURLManageAppointmentFormDays( request, day.getIdForm( ) ) );
        }
        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Gets the confirmation page of delete entry
     * @param request The HTTP request
     * @return the confirmation page of delete entry
     */
    @View( VIEW_CONFIRM_REMOVE_DAY )
    public String getConfirmRemoveDay( HttpServletRequest request )
    {
        String strIdDay = request.getParameter( PARAMETER_ID_DAY );
        UrlItem url = new UrlItem( getActionUrl( ACTION_DO_REMOVE_DAY ) );
        url.addParameter( PARAMETER_ID_DAY, strIdDay );

        return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_DAY,
                url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Perform the entry removal
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_DO_REMOVE_DAY )
    public String doRemoveDay( HttpServletRequest request )
    {
        String strIdDay = request.getParameter( PARAMETER_ID_DAY );
        AppointmentDay day;
        int nIdDay = -1;

        if ( StringUtils.isNotEmpty( strIdDay ) && StringUtils.isNumeric( strIdDay ) )
        {
            nIdDay = Integer.parseInt( strIdDay );

            if ( nIdDay <= 0 )
            {
                return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
            }

            day = AppointmentDayHome.findByPrimaryKey( nIdDay );
            if ( day != null )
            {
                AppointmentDayHome.remove( nIdDay );
                return redirect( request,
                        AppointmentFormJspBean.getURLManageAppointmentFormDays( request, day.getIdForm( ) ) );
            }
        }
        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Populate a day from data in an HTTP request
     * @param day The day to populate
     * @param request The request
     */
    private List<String> populateDay( AppointmentDay day, HttpServletRequest request )
    {
        List<String> listErrors = new ArrayList<String>( );
        String strDate = request.getParameter( PARAMETER_DATE );
        Date date = (Date) _dateConverter.convert( java.sql.Date.class, strDate );

        if ( date == null )
        {
            listErrors.add( MESSAGE_ERROR_DATE_FORMAT );
        }
        else
        {
            // We check that the day does not already exist
            List<AppointmentDay> listDays = AppointmentDayHome.getDaysBetween( day.getIdForm( ), date, date );
            if ( listDays != null && listDays.size( ) > 0 )
            {
                if ( day.getIdDay( ) == 0 )
                {
                    listErrors.add( MESSAGE_ERROR_DAY_ALREADY_EXIST );
                }
                else
                {
                    // If there is a day for this form that has the same date but a different id, we add an error
                    for ( AppointmentDay dayFound : listDays )
                    {
                        if ( dayFound.getIdDay( ) != day.getIdDay( ) )
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
            if ( strOpeningTime != null && strOpeningTime.matches( CONSTANT_TIME_REGEX ) )
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

            if ( strClosingTime != null && strClosingTime.matches( CONSTANT_TIME_REGEX ) )
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
}
