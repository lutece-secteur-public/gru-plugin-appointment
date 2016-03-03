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

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.business.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDay;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDayHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentHoliDaysHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.AppointmentSlotService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.dozer.converters.DateConverter;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private static final String PARAMETER_ID_TIME = "time";
    private static final String PARAMETER_ID_DAY = "id_day";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_IS_OPEN = "isOpen";
    private static final String PARAMETER_OPENING_TIME = "openingTime";
    private static final String PARAMETER_CLOSING_TIME = "closingTime";
    private static final String PARAMETER_DATE = "date";
    private static final String PARAMETER_APPOINTMENT_DURATION = "appointmentDuration";
    private static final String PARAMETER_PEOPLE_PER_APPOINTMENT = "peoplePerAppointment";
    private static final String PARAMETER_NB_WEEK = "nb_week";
    private static final String PARAMETER_MAX_WEEK = "max_week";
    private static final String PARAMETER_LIM_DATES = "bornDates";
    private static final String PARAMETER_DATE_MIN = "dateMin";
    private static final String PARAMETER_APPOINTMENT_FORM = "appointmentform";
    // Messages
    //    private static final String MESSAGE_CONFIRM_REMOVE_DAY = "appointment.message.confirmRemoveDay";
    private static final String MESSAGE_ERROR_OPENING_TIME_FORMAT = "appointment.modify_appointmentForm.patternTimeStart";
    private static final String MESSAGE_ERROR_CLOSING_TIME_FORMAT = "appointment.modify_appointmentForm.patternTimeEnd";
    private static final String MESSAGE_ERROR_DATE_FORMAT = "appointment.message.error.dayDateFormatAnterior";
    private static final String MESSAGE_ERROR_FORMAT_APPOINTMENT_DURATION = "appointment.message.error.formatNumberAppointmentDuration";
    private static final String MESSAGE_ERROR_FORMAT_PEOPLE_PER_APPOINTMENT = "appointment.message.error.formatPeoplePerAppointmentDuration";
    private static final String MESSAGE_ERROR_DAY_ALREADY_EXIST = "appointment.message.error.dayAlreadyExist";
    private static final String MESSAGE_ERROR_DAY_DURATION_APPOINTMENT_NOT_MULTIPLE_FORM = "appointment.message.error.durationAppointmentDayNotMultipleForm";
    private static final String MESSAGE_ERROR_FORM_HAS_APPOINTMENTS = "appointment.message.error.refreshDays.formHasAppointments";
    private static final String MESSAGE_ERROR_DAY_HAS_APPOINTMENT = "appointment.message.error.dayHasAppointment";
    private static final String MESSAGE_ERROR_HOLI_DAY = "appointment.message.error.holiDay";
    private static final String MESSAGE_ERROR_HOUR_APPOINTMENT = "appointment.message.error.timeStartAfterTimeEnd";
    private static final String MESSAGE_ERROR_HOUR_APPOINTMENT_MAXIMALE = "appointment.message.error.formatNumberAppointmentDurationMaximum";
    private static final String MESSAGE_CONFIRM_REFRESH_DAYS = "appointment.message.confirmRefreshDays";
    private static final String INFO_MODIFY_APPOINTMENTDAY_SLOTS_UPDATED = "appointment.info.appointmentDay.slotsUpdated";
    private static final String PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT = "appointment.form.nbWeekToCreate";

    // Page titles
    private static final String PROPERTY_CREATE_DAY_TITLE = "appointment.createDay.pageTitle";
    private static final String PROPERTY_MODIFY_DAY_TITLE = "appointment.modifyDay.pageTitle";
    private static final String PROPERTY_VIEW_DAY_TITLE = "appointment.viewDay.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENTFORM_DAYS = "appointment.modify_appointmentformDays.pageTitle";

    // Views
    private static final String VIEW_MODIFY_APPOINTMENTFORM_DAYS = "modifyAppointmentFormDays";
    private static final String VIEW_MANAGE_APPOINTMENTFORM_SLOT = "manageAppointmentSlots";
    private static final String VIEW_CONFIRM_REFRESH_DAYS = "confirmRefreshDays";
    private static final String VIEW_GET_CREATE_DAY = "getCreateDay";
    private static final String VIEW_GET_MODIFY_DAY = "getModifyDay";
    private static final String VIEW_GET_VIEW_DAY = "viewModifyDay";

    // Actions
    private static final String ACTION_DO_CREATE_DAY = "doCreateDay";
    private static final String ACTION_DO_MODIFY_DAY = "doModifyDay";
    private static final String ACTION_DO_REFRESH_DAYS = "doRefreshDays";

    // Marks
    private static final String MARK_DAY = "day";
    private static final String MARK_LIST_DAYS = "listDays";
    private static final String MARK_DATE_MIN = "dateMin";
    private static final String MARK_DATE_MAX = "dateMax";
    private static final String MARK_LOCALE = "language";
    private static final String MARK_CHECK_ANTERIOR = "anteriorDate";
    private static final String MARK_APPOINTMENTFORM = "appointmentform";

    // Templates
    private static final String TEMPLATE_MODIFY_DAY = "/admin/plugins/appointment/modify_days.html";
    private static final String TEMPLATE_VIEWS_DAY = "/admin/plugins/appointment/view_days.html";
    private static final String TEMPLATE_MODIFY_APPOINTMENTFORM_DAYS = "/admin/plugins/appointment/appointmentform/modify_appointmentform_days.html";
    private static final String TEMPLATE_CREATE_DAY = "/admin/plugins/appointment/create_days.html";

    // Urls
    private static final String JSP_MANAGE_APPOINTMENTFORMS_DAYS = "jsp/admin/plugins/appointment/ManageAppointmentFormDays.jsp";
    private static final String JSP_MANAGE_APPOINTMENTFORMS_SLOT = "jsp/admin/plugins/appointment/ManageAppointmentSlots.jsp";

    // Local variables
    private transient DateConverter _dateConverter;
    private transient AppointmentDay _appointmentDay;
    private int _nNbWeek;

    /**
     * Get the page to manage days of an appointment form
     * @param request The request
     * @return the HTML content to display, or the next URL to redirect to
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature.
     */
    @View( VIEW_MODIFY_APPOINTMENTFORM_DAYS )
    public String getModifyFormDays( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                        AppointmentResourceIdService.PERMISSION_MODIFY_FORM, getUser(  ) ) )
            {
                throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_FORM );
            }

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
            int nNbWeeksToCreate = AppPropertiesService.getPropertyInt( PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT, 1 );

            if ( appointmentForm != null )
            {
                nNbWeeksToCreate += appointmentForm.getNbWeeksToDisplay(  );

                if ( Math.abs( _nNbWeek ) > nNbWeeksToCreate )
                {
                    redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
                }
            }

            Date dateMin = AppointmentService.getService(  ).getDateMonday( _nNbWeek );
            Calendar calendar = GregorianCalendar.getInstance( getLocale(  ) );
            calendar.setTime( dateMin );
            calendar.add( Calendar.DAY_OF_MONTH, 6 );

            Date dateMax = new Date( calendar.getTimeInMillis(  ) );

            List<AppointmentDay> listDays = AppointmentDayHome.getDaysBetween( appointmentForm.getIdForm(  ), dateMin,
                    dateMax );

            AppointmentDayHome.getDaysBetween( nIdForm, dateMin, dateMax );

            Map<String, Object> model = new HashMap<String, Object>(  );
                
            model.put( PARAMETER_APPOINTMENT_FORM, nIdForm );
            
            model.put( MARK_LIST_DAYS, listDays );
            model.put( PARAMETER_NB_WEEK, _nNbWeek );
            model.put( PARAMETER_MAX_WEEK, nNbWeeksToCreate - 1 );
            model.put( PARAMETER_LIM_DATES, getLimitedDate( nNbWeeksToCreate ) );
            model.put( MARK_DATE_MIN, dateMin );
            model.put( MARK_DATE_MAX, dateMax );
            model.put( MARK_LOCALE, getLocale(  ) );
            AppointmentFormJspBean.addElementsToModelForLeftColumn( request, appointmentForm, getUser(  ),
                getLocale(  ), model );

            return getPage( PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENTFORM_DAYS, TEMPLATE_MODIFY_APPOINTMENTFORM_DAYS, model );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Get the HTML code to create an entry
     * @param request The request
     * @return The HTML code to display or the next URL to redirect to
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature.
     */
    @View( VIEW_GET_CREATE_DAY )
    public String getCreateDay( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String strTimeMilli = request.getParameter( PARAMETER_ID_TIME );

        if ( StringUtils.isEmpty( strIdForm ) || !StringUtils.isNumeric( strIdForm ) ||
                StringUtils.isEmpty( strTimeMilli ) || !StringUtils.isNumeric( strTimeMilli ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        int nIdForm = Integer.parseInt( strIdForm );
        Date objMyTime = new Date( Long.valueOf( strTimeMilli ) );
        Calendar tmpCal = GregorianCalendar.getInstance( Locale.FRENCH );
        tmpCal.setTimeInMillis( objMyTime.getTime(  ) );
        tmpCal.set( GregorianCalendar.getInstance( Locale.FRENCH ).get( Calendar.YEAR ),
            GregorianCalendar.getInstance( Locale.FRENCH ).get( Calendar.MONTH ),
            GregorianCalendar.getInstance( Locale.FRENCH ).get( Calendar.DAY_OF_MONTH ) );

        Date nowTime = new Date( tmpCal.getTimeInMillis(  ) );

        AppointmentForm appointmentForm = AppointmentFormHome.findByPrimaryKey( nIdForm );

        if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                    AppointmentResourceIdService.PERMISSION_MODIFY_FORM, getUser(  ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_FORM );
        }

        //Check the id if it s in th DB
        List<AppointmentDay> listDays = AppointmentDayHome.getDaysBetween( appointmentForm.getIdForm(  ), objMyTime,
                objMyTime );

        // Default Values
        Map<String, Object> model = new HashMap<String, Object>(  );

        if ( listDays.size(  ) == 1 )
        {
            _appointmentDay = listDays.get( 0 );
        }
        else
        {
            _appointmentDay = new AppointmentDay(  );
            _appointmentDay.setIdForm( nIdForm );
            _appointmentDay.setDate( objMyTime );
            AppointmentDayHome.create( _appointmentDay );

            if ( !objMyTime.before( nowTime ) )
            {
                _appointmentDay = copyDayFromDB( _appointmentDay );
            }
        }

        if ( objMyTime.before( nowTime ) )
        {
            return redirect( request, VIEW_GET_VIEW_DAY, PARAMETER_ID_DAY, _appointmentDay.getIdDay(  ) );
        }

        model.put( MARK_DAY, _appointmentDay );
        model.put( MARK_LOCALE, getLocale(  ) );
        _appointmentDay = null;
        fillCommons( model );

        return getPage( PROPERTY_CREATE_DAY_TITLE, TEMPLATE_MODIFY_DAY, model );
    }

    /**
     *    Get Limited Date
     *         @param nBWeeks
     *         @return
    */
    private String[] getLimitedDate( int nBWeeks )
    {
        Calendar startCal = GregorianCalendar.getInstance( Locale.FRENCH );
        Calendar endCal = GregorianCalendar.getInstance( Locale.FRENCH );
        startCal.set( Calendar.WEEK_OF_YEAR, startCal.get( Calendar.WEEK_OF_YEAR ) - nBWeeks );
        startCal.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
        endCal.set( Calendar.WEEK_OF_YEAR, endCal.get( Calendar.WEEK_OF_YEAR ) + nBWeeks );
        endCal.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
        endCal.add( Calendar.DATE, -1 );

        String[] retour = 
            {
                DateUtil.getDateString( startCal.getTime(  ), getLocale(  ) ),
                DateUtil.getDateString( endCal.getTime(  ), getLocale(  ) )
            };

        return retour;
    }

    /**
    * Do create a day
    * @param request the request
    * @return The HTML code to display or the next URL to redirect to
    * @throws AccessDeniedException If the user is not authorized to access
    *             this feature.
    */
    @Action( ACTION_DO_CREATE_DAY )
    public String doCreateDay( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_CANCEL ) ) )
            {
                return redirect( request, getURLManageAppointmentFormDays( request, strIdForm ) );
            }

            AppointmentDay day = new AppointmentDay(  );
            day.setIdForm( Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) ) );

            if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, Integer.toString( day.getIdForm(  ) ),
                        AppointmentResourceIdService.PERMISSION_MODIFY_FORM, getUser(  ) ) )
            {
                throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_FORM );
            }

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

                return redirect( request, VIEW_GET_CREATE_DAY, PARAMETER_ID_FORM, day.getIdForm(  ) );
            }

            AppointmentDayHome.create( day );
            AppointmentSlotService.getInstance(  ).computeAndCreateSlotsForDay( day, form );

            if ( day.getIsOpen(  ) )
            {
                return redirect( request, AppointmentSlotJspBean.getUrlManageSlotsByIdDay( request, day.getIdDay(  ) ) );
            }

            return redirect( request, getURLManageAppointmentFormDays( request, Integer.toString( day.getIdForm(  ) ) ) );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Gets the entry modification page
     * @param request The HTTP request
     * @return The entry modification page
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature.
     */
    @View( VIEW_GET_VIEW_DAY )
    public String viewModifyDay( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDay = request.getParameter( PARAMETER_ID_DAY );

        if ( StringUtils.isNotEmpty( strIdDay ) && StringUtils.isNumeric( strIdDay ) )
        {
            int nIdDay = Integer.parseInt( strIdDay );

            AppointmentDay day = fillAppoinmentDay( nIdDay, AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT );
            Map<String, Object> model = fillFreeMarker( day );
            fillCommons( model );

            return getPage( PROPERTY_VIEW_DAY_TITLE, TEMPLATE_VIEWS_DAY, model );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Fullfill freemarker variables
     * @param day
     * @return
     */
    private Map<String, Object> fillFreeMarker( AppointmentDay day )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_LOCALE, getLocale(  ) );
        model.put( MARK_DAY, day );
        fillCommons( model );

        return model;
    }

    /**
     * Get an Appointment day Copied
     * @param day
     * @throws AccessDeniedException
     */
    private AppointmentDay fillAppoinmentDay( int nIdDay, String strPermission )
        throws AccessDeniedException
    {
        // Check if isOpen or not to put form value default
        AppointmentDay day = null;

        if ( ( _appointmentDay != null ) && ( _appointmentDay.getIdDay(  ) == nIdDay ) )
        {
            day = _appointmentDay;
            _appointmentDay = null;
        }
        else
        {
            day = AppointmentDayHome.findByPrimaryKey( nIdDay );
        }

        if ( ( day != null ) &&
                !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, Integer.toString( day.getIdForm(  ) ),
                    strPermission, getUser(  ) ) )
        {
            throw new AccessDeniedException( strPermission );
        }

        return copyDayFromDB( day );
    }

    /**
     * Copy fields from Days
     * @param day
     */
    private AppointmentDay copyDayFromDB( AppointmentDay day )
    {
        AppointmentDay objRetour = day;

        if ( ( objRetour != null ) && !objRetour.getIsOpen(  ) )
        {
            AppointmentForm formFromDb = AppointmentFormHome.findByPrimaryKey( day.getIdForm(  ) );
            objRetour.setOpeningMinutes( formFromDb.getOpeningMinutes(  ) );
            objRetour.setOpeningHour( formFromDb.getOpeningHour(  ) );
            objRetour.setClosingMinutes( formFromDb.getClosingMinutes(  ) );
            objRetour.setClosingHour( formFromDb.getClosingHour(  ) );
            objRetour.setAppointmentDuration( formFromDb.getDurationAppointments(  ) );
            objRetour.setPeoplePerAppointment( formFromDb.getPeoplePerAppointment(  ) );
        }

        return objRetour;
    }

    /**
     * Gets the entry modification page
     * @param request The HTTP request
     * @return The entry modification page
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature.
     */
    @View( VIEW_GET_MODIFY_DAY )
    public String getModifyDay( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDay = request.getParameter( PARAMETER_ID_DAY );
        Map<String, Object> model = getModel(  );
        
        if ( StringUtils.isNotEmpty( strIdDay ) && StringUtils.isNumeric( strIdDay ) )
        {
            int nIdDay = Integer.parseInt( strIdDay );

            AppointmentDay day = fillAppoinmentDay( nIdDay, AppointmentResourceIdService.PERMISSION_MODIFY_FORM );

            if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_CANCEL ) ) )
            {
                return redirect( request,
                    getURLManageAppointmentFormDays( request, Integer.toString( day.getIdForm(  ) ) ) );
            }
            
               model = fillFreeMarker( day );

             
            return getPage( PROPERTY_MODIFY_DAY_TITLE, TEMPLATE_MODIFY_DAY, model );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Perform the entry modification
     * @param request The HTTP request
     * @return The URL to go after performing the action
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature.
     */
    @Action( ACTION_DO_MODIFY_DAY )
    public String doModifyDay( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDay = request.getParameter( PARAMETER_ID_DAY );
        addInfo( MARK_LOCALE, getLocale(  ) );

        if ( StringUtils.isEmpty( strIdDay ) || !StringUtils.isNumeric( strIdDay ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        int nIdDay = Integer.parseInt( strIdDay );

        AppointmentDay day = AppointmentDayHome.findByPrimaryKey( nIdDay );

        if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, Integer.toString( day.getIdForm(  ) ),
                    AppointmentResourceIdService.PERMISSION_MODIFY_FORM, getUser(  ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_FORM );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_CANCEL ) ) )
        {
            return redirect( request, getURLManageAppointmentFormDays( request, Integer.toString( day.getIdForm(  ) ) ) );
        }

        AppointmentForm form = AppointmentFormHome.findByPrimaryKey( day.getIdForm(  ) );
        List<String> listErrors = populateDay( day, form, request );

        AppointmentDay dayFromDb = AppointmentDayHome.findByPrimaryKey( day.getIdDay(  ) );

        // If there were modification on the day, then we check that the day is not associated with any appointment
        // The only attributes that can freely be changed are the opening attribute and the people per appointment attribute
        if ( ( day.getOpeningHour(  ) != dayFromDb.getOpeningHour(  ) ) ||
                ( day.getOpeningMinutes(  ) != dayFromDb.getOpeningMinutes(  ) ) ||
                ( day.getClosingHour(  ) != dayFromDb.getClosingHour(  ) ) ||
                ( day.getClosingMinutes(  ) != dayFromDb.getClosingMinutes(  ) ) ||
                ( day.getAppointmentDuration(  ) != dayFromDb.getAppointmentDuration(  ) ) )
        {
            int nNbAppointment = AppointmentHome.getNbAppointmentByIdDay( day.getDate(  ), day.getIdForm(  ) );

            if ( nNbAppointment > 0 )
            {
                addError( MESSAGE_ERROR_DAY_HAS_APPOINTMENT, getLocale(  ) );

                return redirect( request, VIEW_GET_MODIFY_DAY, PARAMETER_ID_DAY, day.getIdDay(  ) );
            }

            List<Date> listClosingDays = AppointmentHoliDaysHome.findByIdForm( day.getIdForm(  ) );

            // check closing days
            for ( Date closeDay : listClosingDays )
            {
                if ( DateUtils.isSameDay( closeDay, day.getDate(  ) ) )
                {
                    addError( MESSAGE_ERROR_HOLI_DAY, getLocale(  ) );

                    return redirect( request, VIEW_GET_MODIFY_DAY, PARAMETER_ID_DAY, day.getIdDay(  ) );
                }
            }
        }

        if ( ( listErrors != null ) && ( listErrors.size(  ) > 0 ) )
        {
            for ( String strError : listErrors )
            {
                addError( strError, getLocale(  ) );
            }

            _appointmentDay = day;

            return redirect( request, VIEW_GET_MODIFY_DAY, PARAMETER_ID_DAY, day.getIdDay(  ) );
        }
        
        if ( day.getPeoplePerAppointment( )!=dayFromDb.getPeoplePerAppointment( ) )
        {
        	AppointmentDayHome.resetDayPlaces( day, day.getIdForm( ), getDayOfWeek( day.getDate(  ) ) );
        }

        AppointmentDayHome.update( day );

        if ( AppointmentSlotService.getInstance(  ).checkForDayModification( day, dayFromDb, form ) )
        {
            addInfo( INFO_MODIFY_APPOINTMENTDAY_SLOTS_UPDATED, getLocale(  ) );
        }

        if ( day.getIsOpen(  ) )
        {
            return redirect( request, AppointmentSlotJspBean.getUrlManageSlotsByIdDay( request, day.getIdDay(  ) ) );
        }

        return redirect( request, getURLManageAppointmentFormDays( request, Integer.toString( day.getIdForm(  ) ) ) );
    }
    
    
    /**
     * Get the day of the week of a date.
     * @param date The date to get the day of the week of
     * @return 1 for Monday, 2 for Tuesday, ..., 7 for Sunday
     */
    public int getDayOfWeek( Date date )
    {
        Calendar calendar = GregorianCalendar.getInstance( Locale.FRANCE );
        calendar.setTime( date );

        int nDayOfWeek = calendar.get( Calendar.DAY_OF_WEEK ) - 1;

        if ( nDayOfWeek <= 0 )
        {
            nDayOfWeek = nDayOfWeek + 7;
        }

        return nDayOfWeek;
    }

    /**
     * Get the page to confirm the refreshment of days of a form
     * @param request The request
     * @return The next URL to redirect to
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature.
     */
    @View( VIEW_CONFIRM_REFRESH_DAYS )
    public String getConfirmRefreshDays( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String strDateMin = request.getParameter( PARAMETER_DATE_MIN );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                        AppointmentResourceIdService.PERMISSION_MODIFY_FORM, getUser(  ) ) )
            {
                throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_FORM );
            }

            UrlItem urlItem = new UrlItem( getActionUrl( ACTION_DO_REFRESH_DAYS ) );
            urlItem.addParameter( PARAMETER_ID_FORM, strIdForm );
            urlItem.addParameter( PARAMETER_DATE_MIN, strDateMin );

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
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature.
     */
    @Action( ACTION_DO_REFRESH_DAYS )
    public String doRefreshDays( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String strDateMin = request.getParameter( PARAMETER_DATE_MIN );
        Date dateMin = new Date( DateUtil.getDate( strDateMin ).getTime(  ) );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm,
                        AppointmentResourceIdService.PERMISSION_MODIFY_FORM, getUser(  ) ) )
            {
                throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_FORM );
            }

            int nIdForm = Integer.parseInt( strIdForm );
            int nNbAppointments = AppointmentHome.countAppointmentsByIdForm( nIdForm, dateMin );

            if ( nNbAppointments > 0 )
            {
                return redirect( request,
                    AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_FORM_HAS_APPOINTMENTS,
                        getURLManageAppointmentFormDays( request, strIdForm ), AdminMessage.TYPE_STOP ) );
            }

            AppointmentService.getService(  ).resetFormDays( AppointmentFormHome.findByPrimaryKey( nIdForm ), dateMin );

            return redirect( request, getURLAppointmentSlotJspBean( request, nIdForm ) );
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
        SimpleDateFormat actFormatDate = new SimpleDateFormat( "dd/MM/yyyy", Locale.FRENCH );
        Date date = (Date) getDateConverter(  ).convert( java.sql.Date.class, actFormatDate.format( day.getDate(  ) ) );

        if ( isDateAnterior( day.getDate(  ) ) )
        {
            listErrors.add( MESSAGE_ERROR_DATE_FORMAT );

            return listErrors;
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

        boolean bIsOpen = Boolean.parseBoolean( request.getParameter( PARAMETER_IS_OPEN ) );
        day.setIsOpen( bIsOpen );

        if ( bIsOpen )
        {
            String strOpeningTime = request.getParameter( PARAMETER_OPENING_TIME );
            boolean bOpeningTime = false; //Check if we have a good opening time
            boolean bClosingTime = false; //Check if we have a good closing time

            if ( ( strOpeningTime != null ) && strOpeningTime.matches( AppointmentForm.CONSTANT_TIME_REGEX ) )
            {
                String[] strArrayOpeningTime = strOpeningTime.split( AppointmentForm.CONSTANT_H );
                day.setOpeningHour( Integer.parseInt( strArrayOpeningTime[0] ) );
                day.setOpeningMinutes( Integer.parseInt( strArrayOpeningTime[1] ) );
                bOpeningTime = true;
            }
            else
            {
                listErrors.add( MESSAGE_ERROR_OPENING_TIME_FORMAT );
            }

            String strClosingTime = request.getParameter( PARAMETER_CLOSING_TIME );

            if ( ( strClosingTime != null ) && strClosingTime.matches( AppointmentForm.CONSTANT_TIME_REGEX ) )
            {
                String[] strArrayClosingTime = strClosingTime.split( AppointmentForm.CONSTANT_H );
                day.setClosingHour( Integer.parseInt( strArrayClosingTime[0] ) );
                day.setClosingMinutes( Integer.parseInt( strArrayClosingTime[1] ) );
                bClosingTime = true;
            }
            else
            {
                listErrors.add( MESSAGE_ERROR_CLOSING_TIME_FORMAT );
            }

            if ( bOpeningTime && bClosingTime )
            {
                Calendar objCalOpen = getCalendar( day.getOpeningHour(  ), day.getOpeningMinutes(  ), getLocale(  ) );
                Calendar objCalClose = getCalendar( day.getClosingHour(  ), day.getClosingMinutes(  ), getLocale(  ) );

                if ( objCalOpen.compareTo( objCalClose ) >= 0 ) //Check if Opening < Closing time
                {
                    listErrors.add( MESSAGE_ERROR_HOUR_APPOINTMENT );
                }
                else
                {
                    String strDuration = request.getParameter( PARAMETER_APPOINTMENT_DURATION );

                    //Check Now Duration because all Time are checked 
                    if ( StringUtils.isNotEmpty( strDuration ) && StringUtils.isNumeric( strDuration ) &&
                            ( Integer.valueOf( strDuration ) > 0 ) )
                    {
                        day.setAppointmentDuration( Integer.parseInt( strDuration ) );

                        int nDiffHour = ( objCalClose.get( Calendar.HOUR_OF_DAY ) -
                            objCalOpen.get( Calendar.HOUR_OF_DAY ) ) * 60;
                        int nDiffMins = objCalClose.get( Calendar.MINUTE ) - objCalOpen.get( Calendar.MINUTE );
                        int nMaxima = nDiffHour + nDiffMins;

                        if ( day.getAppointmentDuration(  ) > nMaxima ) //Duration can't exceed in minutes closing - opening
                        {
                            listErrors.add( MESSAGE_ERROR_HOUR_APPOINTMENT_MAXIMALE );
                        }
                        else
                        {
                            if ( ( nMaxima % day.getAppointmentDuration(  ) ) != 0 ) //Duration Time must be a modulo
                            {
                                listErrors.add( MESSAGE_ERROR_DAY_DURATION_APPOINTMENT_NOT_MULTIPLE_FORM );
                            }
                        }
                    }
                    else
                    {
                        listErrors.add( MESSAGE_ERROR_FORMAT_APPOINTMENT_DURATION );
                    }
                }
            }

            String strPeoplePerAppointment = request.getParameter( PARAMETER_PEOPLE_PER_APPOINTMENT );

            if ( StringUtils.isNotEmpty( strPeoplePerAppointment ) && StringUtils.isNumeric( strPeoplePerAppointment ) &&
                    ( Integer.valueOf( strPeoplePerAppointment ) > 0 ) )
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

    private static Calendar getCalendar( int nHour, int nMinutes, Locale objPays )
    {
        Calendar objCal = new GregorianCalendar( objPays );
        objCal.set( Calendar.HOUR_OF_DAY, nHour );
        objCal.set( Calendar.MINUTE, nMinutes );
        objCal.set( Calendar.SECOND, 0 );
        objCal.set( Calendar.MILLISECOND, 0 );

        return objCal;
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
     * Get the URL to manage appointment slot
     * @param request The request
     * @param strIdForm The id of the form to manage days of
     * @return The URL to manage appointment forms
     */
    public static String getURLAppointmentSlotJspBean( HttpServletRequest request, int strIdForm )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_APPOINTMENTFORMS_SLOT );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_APPOINTMENTFORM_SLOT );
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
            _dateConverter = new DateConverter( DateFormat.getDateInstance( DateFormat.SHORT, getLocale(  ) ) );
        }

        return _dateConverter;
    }

    /**
     * Is date anterior before
     * @param objDate
     * @return
     */
    private boolean isDateAnterior( Date objDate )
    {
        boolean bBeforeNow = false;
        SimpleDateFormat actFormatDate = new SimpleDateFormat( "dd/MM/yyyy", Locale.FRENCH );

        if ( objDate != null )
        {
            Date date = (Date) getDateConverter(  ).convert( java.sql.Date.class, actFormatDate.format( objDate ) );
            Date actuelle = (Date) getDateConverter(  )
                                       .convert( java.sql.Date.class,
                    actFormatDate.format( Calendar.getInstance(  ).getTime(  ) ) );
            bBeforeNow = date.before( actuelle );
        }

        return bBeforeNow;
    }
}
