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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import fr.paris.lutece.plugins.appointment.business.AppointmentFilter;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.business.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDay;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDayHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentHoliDaysHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentFormService;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.AppointmentSlotService;
import fr.paris.lutece.plugins.appointment.service.upload.AppointmentAsynchronousUploadHandler;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.url.UrlItem;

/**
 * JspBean to manage calendar slots
 */
@Controller( controllerJsp = AppointmentSlotJspBean.JSP_MANAGE_APPOINTMENT_SLOTS, controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM )
public class AppointmentSlotJspBean extends MVCAdminJspBean
{
    /**
     * JSP of this JSP Bean
     */
    public static final String JSP_MANAGE_APPOINTMENT_SLOTS = "ManageAppointmentSlots.jsp";

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 2376721852596997810L;

    // Messages
    private static final String MESSAGE_MANAGE_SLOTS_PAGE_TITLE = "appointment.manageCalendarSlots.pageTitle";
    private static final String MESSAGE_MODIFY_SLOT_PAGE_TITLE = "appointment.modifyCalendarSlots.pageTitle";
    private static final String MESSAGE_WARNING_CHANGES_APPLY_TO_ALL = "appointment.modifyCalendarSlots.warningModifiyingEndingTime";
    private static final String MESSAGE_ERROR_DAY_HAS_APPOINTMENT = "appointment.modifyCalendarSlots.errorDayHasAppointment";
    private static final String MESSAGE_ERROR_FORM_NOT_ACTIVE = "appointment.message.error.formNotActive";
    private static final String MESSAGE_ERROR_TIME_END_BEFORE_TIME_START = "appointment.modifyCalendarSlots.errorTimeEndBeforeTimeStart";
    private static final String MESSAGE_ERROR_DURATION_MUST_BE_MULTIPLE_OF_REF_DURATION = "appointment.message.error.durationAppointmentSlotNotMultipleRef";
    private static final String MESSAGE_SLOT_CAN_NOT_END_AFTER_DAY_OR_FORM = "appointment.message.error.slotCanNotEndAfterDayOrForm";
    private static final String MESSAGE_INFO_SLOT_UPDATED = "appointment.modifyCalendarSlots.messageSlotUpdated";
    private static final String MESSAGE_MANAGE_HOLIDAYS_PAGE_TITLE = "appointment.manageHolidays.pageTitle";
    private static final String MESSAGE_ERROR_DATE_EXIST = "appointment.message.error.closeDayExist";
    private static final String MESSAGE_ERROR_EMPTY_DATE = "appointment.message.error.EmptycloseDay";
    private static final String MESSAGE_INFO_REMOVE_DATE = "appointment.info.appointmentform.closingDayRemoved";
    private static final String MESSAGE_INFO_ADD_DATE = "appointment.info.appointmentform.closingDayAdded";
    private static final String MESSAGE_INFO_IMPORTED_CLOSING_DAYS = "appointment.info.appointmentform.closingDayImport";
    private static final String MESSAGE_ERROR_EMPTY_FILE = "appointment.message.error.closingDayErrorImport";
    private static final String MESSAGE_ERROR_EXISTING_DATES = "appointment.message.error.closingDayErrorDaysExists";

    // Parameters
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_ID_DAY = "id_day";
    private static final String PARAMETER_ID_SLOT = "id_slot";
    private static final String PARAMETER_NB_PLACES = "nbPlaces";
    private static final String PARAMETER_ENDING_TIME = "timeEnd";
    private static final String PARAMETER_APPOINTMENT_FORM = "appointmentform";
    private static final String PARAMETER_ID_TIME = "time";
    private static final String PARAMETER_NB_WEEK = "nb_week";
    private static final String PARAMETER_MAX_WEEK = "max_week";
    private static final String PARAMETER_LIM_DATES = "bornDates";

    // Marks
    private static final String MARK_LIST_SLOTS = "listSlots";
    private static final String MARK_DAY = "day";
    private static final String MARK_SLOT = "slot";
    private static final String MARK_MIN_SLOT_DURATION = "minDuration";
    private static final String MARK_MIN_STARTING_HOUR = "minStartingHour";
    private static final String MARK_MIN_STARTING_MINUTE = "minStartingMinute";
    private static final String MARK_MAX_ENDING_HOUR = "maxEndingHour";
    private static final String MARK_MAX_ENDING_MINUTE = "maxEndingMinute";
    private static final String MARK_READ_ONLY = "readonly";
    private static final String MARK_LOCALE = "language";
    private static final String MARK_BORN_DATE = "bornDates";
    private static final String MARK_HOLIDAY = "dateHoliday";
    private static final String MARK_LIST_DAYS = "listDays";
    private static final String MARK_COLUMN = "column";
    private static final String MARK_ROW = "row";
    private static final String MARK_EXCEL_EXTENSION_XLSX = "xlsx";
    private static final String MARK_FORMAT_DATE = "dd/MM/yyyy";
    private static final String MARK_FILE_CLOSING_DAYS = "fileHolidays";
    private static final String MARK_COLUMN_DAY = "appointment.manageHolidays.export_holidays.columnDay";
    private static final String MARK_COLUMN_MONTH = "appointment.manageHolidays.export_holidays.columnMonth";
    private static final String MARK_COLUMN_YEAR = "appointment.manageHolidays.export_holidays.columnYeay";
    private static final String MARK_COLUMN_DATE = "appointment.manageHolidays.export_holidays.columnShortDate";
    private static final String MARK_FORMAT_DATE_REGEX = "([0-9]{2})/([0-9]{2})/([0-9]{4})";
    private static final String MARK_ERROR_MSG = "appointment.manageHolidays.error.formatDate";
    private static final String MARK_ERROR_FORMAT_DATE = "appointment.manageHolidays.error.typeFormatNotValid";
    private static final String MARK_FORM = "form";
    private static final String MARK_LIST_TIME_BEGIN = "list_time_begin";
    private static final String MARK_MIN_DURATION_APPOINTMENT = "min_duration_appointments";
    private static final String MARK_LIST_DAYS_OF_WEEK = "list_days_of_week";
    private static final String MARK_LANGUAGE = "language";
    private static final String MARK_NB_SLOT_DAY = "nbrSlotDay";
    private static final String MARK_LIST_OPEN_DAY = "listOpenDay";

    // Views
    private static final String VIEW_MANAGE_APPOINTMENT_SLOTS = "manageAppointmentSlots";
    private static final String VIEW_MANAGE_APPOINTMENT_SLOTS_ = "manageAppointmentSlotss";
    private static final String VIEW_MODIFY_APPOINTMENT_SLOT = "viewModifySlots";
    private static final String VIEW_MANAGE_HOLIDAYS = "viewManageHolidays";

    // Actions
    private static final String ACTION_DO_CHANGE_SLOT_ENABLING = "doChangeSlotEnabling";
    private static final String ACTION_DO_MODIFY_SLOT = "doModifySlot";
    private static final String ACTION_DO_MODIFY_HOLIDAYS = "doModifyHolidays";

    // JSP URL
    private static final String JSP_URL_MANAGE_APPOINTMENT_SLOT = "jsp/admin/plugins/appointment/" + JSP_MANAGE_APPOINTMENT_SLOTS;

    // Templates
    private static final String TEMPLATE_MANAGE_SLOTS = "admin/plugins/appointment/slots/manage_slots.html";
    private static final String TEMPLATE_MANAGE_SLOTSS = "admin/plugins/appointment/slots/manage_slotss.html";
    private static final String TEMPLATE_MODIFY_SLOT = "admin/plugins/appointment/slots/modify_slot.html";
    private static final String TEMPLATE_MANAGE_HOLIDAYS = "admin/plugins/appointment/slots/modify_appointmentform_holidays.html";
    private static final String PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT = "appointment.form.nbWeekToCreate";

    // Messages
    private static final String [ ] MESSAGE_LIST_DAYS_OF_WEEK = AppointmentService.getListDaysOfWeek( );
    private AppointmentSlot _slotInSession;

    // services
    private final AppointmentFormService _appointmentFormService = SpringContextService.getBean( AppointmentFormService.BEAN_NAME );
    private transient AppointmentDay _appointmentDay;

    /**
     * Get an Appointment day Copied
     * 
     * @param day
     * @throws AccessDeniedException
     */
    private AppointmentDay fillAppoinmentDay( int nIdDay, String strPermission ) throws AccessDeniedException
    {
        // Check if isOpen or not to put form value default
        AppointmentDay day = null;

        if ( ( _appointmentDay != null ) && ( _appointmentDay.getIdDay( ) == nIdDay ) )
        {
            day = _appointmentDay;
            _appointmentDay = null;
        }
        else
        {
            day = AppointmentDayHome.findByPrimaryKey( nIdDay );
        }

        if ( ( day != null ) && !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, Integer.toString( day.getIdForm( ) ), strPermission, getUser( ) ) )
        {
            throw new AccessDeniedException( strPermission );
        }

        return copyDayFromDB( day );
    }

    /**
     * Copy fields from Days
     * 
     * @param day
     */
    private AppointmentDay copyDayFromDB( AppointmentDay day )
    {
        AppointmentDay objRetour = day;

        if ( ( objRetour != null ) && !objRetour.getIsOpen( ) )
        {
            AppointmentForm formFromDb = AppointmentFormHome.findByPrimaryKey( day.getIdForm( ) );
            objRetour.setOpeningMinutes( formFromDb.getOpeningMinutes( ) );
            objRetour.setOpeningHour( formFromDb.getOpeningHour( ) );
            objRetour.setClosingMinutes( formFromDb.getClosingMinutes( ) );
            objRetour.setClosingHour( formFromDb.getClosingHour( ) );
            objRetour.setAppointmentDuration( formFromDb.getDurationAppointments( ) );
            objRetour.setPeoplePerAppointment( formFromDb.getPeoplePerAppointment( ) );
        }

        return objRetour;
    }

    /**
     * Get the page to manage slots of a form or a day
     * 
     * @param request
     *            The request
     * @return The HTML content to display or the next URL to redirect to
     * @throws AccessDeniedException
     */
    @View( defaultView = true, value = VIEW_MANAGE_APPOINTMENT_SLOTS )
    public String getManageSlots( HttpServletRequest request ) throws AccessDeniedException
    {
        AppointmentAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );

        int nIdForm = 0;
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String strTimeMilli = request.getParameter( PARAMETER_ID_TIME );
        String strIdDay = request.getParameter( PARAMETER_ID_DAY );

        if ( StringUtils.isNotEmpty( strIdDay ) && StringUtils.isNumeric( strIdDay ) )
        {
            int nIdDay = Integer.parseInt( strIdDay );

            AppointmentDay day = fillAppoinmentDay( nIdDay, AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT );

            if ( StringUtils.isEmpty( strIdForm ) )
            {
                nIdForm = day.getIdForm( );
            }
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model = getModel( );

        if ( ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) ) || ( nIdForm != 0 ) )
        {
            _appointmentFormService.removeAppointmentFromSession( request.getSession( ) );
            _appointmentFormService.removeValidatedAppointmentFromSession( request.getSession( ) );

            if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
            {
                nIdForm = Integer.parseInt( strIdForm );
            }

            String strTime = request.getParameter( PARAMETER_ID_TIME );

            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );

            int nNbWeeksToCreate = AppPropertiesService.getPropertyInt( PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT, 1 ) + form.getNbWeeksToDisplay( );
            String strNbWeek = request.getParameter( PARAMETER_NB_WEEK );
            int nNbWeek = 0;

            if ( !StringUtils.isEmpty( strTimeMilli ) || StringUtils.isNumeric( strTimeMilli ) )
            {
                Date objMyTime = new Date( Long.valueOf( strTimeMilli ) );
                // Compute difference in week beetween now and date picked for the calendar button
                nNbWeek = computeWeek( objMyTime );
            }

            if ( StringUtils.isNotEmpty( strNbWeek ) )
            {
                nNbWeek = AppointmentService.getService( ).parseInt( strNbWeek );

                if ( Math.abs( nNbWeek ) > nNbWeeksToCreate && form.getDateLimit( ) == null )
                {
                    return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
                }
            }

            List<AppointmentDay> listDays = AppointmentService.getService( ).findAndComputeDayList( form, nNbWeek, false );

            for ( AppointmentDay day : listDays )
            {
                if ( nNbWeek < 0 )
                {
                    if ( day.getIdDay( ) > 0 )
                    {
                        List<AppointmentSlot> listSlots = AppointmentSlotHome.findByIdDayWithFreePlaces( day.getIdDay( ) );

                        for ( AppointmentSlot slotFromDb : listSlots )
                        {
                            for ( AppointmentSlot slotComputed : day.getListSlots( ) )
                            {
                                if ( ( slotFromDb.getStartingHour( ) == slotComputed.getStartingHour( ) )
                                        && ( slotFromDb.getStartingMinute( ) == slotComputed.getStartingMinute( ) ) )
                                {
                                    slotComputed.setNbFreePlaces( slotFromDb.getNbFreePlaces( ) );
                                    slotComputed.setNbPlaces( slotFromDb.getNbPlaces( ) );
                                    slotComputed.setIdSlot( slotFromDb.getIdSlot( ) );
                                }
                            }
                        }

                        for ( AppointmentSlot slotComputed : day.getListSlots( ) )
                        {
                            if ( slotComputed.getIdSlot( ) == 0 )
                            {
                                slotComputed.setIsEnabled( false );
                            }
                        }
                    }
                    else
                    {
                        day.setIsOpen( false );
                    }
                }
                else
                {
                    // If the day has not been loaded from the database, we load its slots
                    // Otherwise, we use default computed slots
                    if ( day.getIdDay( ) > 0 )
                    {
                        day.setListSlots( AppointmentSlotHome.findByIdDayWithFreePlaces( day.getIdDay( ) ) );
                    }
                }
            }

            listDays = computeUnavailableDays( form.getIdForm( ), listDays, false );

            List<String> listTimeBegin = new ArrayList<String>( );
            int nMinAppointmentDuration = AppointmentService.getService( ).getListTimeBegin( listDays, form, listTimeBegin );

            // int nIdForm = Integer.parseInt( strIdForm );
            // listSlots = AppointmentSlotHome.findByIdForm( nIdForm );
            // form = AppointmentFormHome.findByPrimaryKey( nIdForm );
            // nNbWeeks = form.getNbWeeksToDisplay( );
            //
            // boolean[] bArrayListDays =
            // {
            // form.getIsOpenMonday( ), form.getIsOpenTuesday( ), form.getIsOpenWednesday( ),
            // form.getIsOpenThursday( ), form.getIsOpenFriday( ), form.getIsOpenSaturday( ),
            // form.getIsOpenSunday( ),
            // };
            // AppointmentDay day = AppointmentService.getService( ).getAppointmentDayFromForm( form );
            // day.setIsOpen( false );
            // model.put( MARK_READ_ONLY, false );
            //
            // boolean bHasClosedDay = false;
            //
            // for ( int i = 0; i < bArrayListDays.length; i++ )
            // {
            // if ( !bArrayListDays[i] )
            // {
            // listSlots.addAll( AppointmentService.getService( ).computeDaySlots( day, i + 1 ) );
            // bHasClosedDay = true;
            // }
            // }
            //
            // if ( bHasClosedDay )
            // {
            // Collections.sort( listSlots );
            // }
            // }
            // else
            // {
            // String strIdDay = request.getParameter( PARAMETER_ID_DAY );
            //
            // if ( StringUtils.isNotBlank( strIdDay ) && StringUtils.isNumeric( strIdDay ) )
            // {
            // int nIdDay = Integer.parseInt( strIdDay );
            // AppointmentDay day = AppointmentDayHome.findByPrimaryKey( nIdDay );
            //
            // if ( !day.getIsOpen( ) )
            // {
            // return redirect( request,
            // AppointmentFormDayJspBean.getURLManageAppointmentFormDays( request, strIdDay ) );
            // }
            //
            // listSlots = AppointmentSlotHome.findByIdDay( nIdDay );
            // model.put( MARK_DAY, day );
            // model.put( MARK_READ_ONLY, isReadonly( day.getDate( ) ) );
            // form = AppointmentFormHome.findByPrimaryKey( day.getIdForm( ) );
            // }
            // }
            //
            // if ( listSlots == null )
            // {
            // return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
            // }
            //
            // int nDuration = 0;
            // int nMinStartingTime = 0;
            // int nMaxEndingTime = 0;
            // int nMinStartingHour = 0;
            // int nMinStartingMinute = 0;
            // int nMaxEndingHour = 0;
            // int nMaxEndingMinute = 0;
            //
            // for ( AppointmentSlot slot : listSlots )
            // {
            // int nSlotStartingTime = ( slot.getStartingHour( ) * 60 ) + slot.getStartingMinute( );
            // int nSlotEndingTime = ( slot.getEndingHour( ) * 60 ) + slot.getEndingMinute( );
            // int nSlotDuration = nSlotEndingTime - nSlotStartingTime;
            //
            // if ( nSlotDuration < 0 )
            // {
            // nSlotDuration = -1 * nSlotDuration;
            // }
            //
            // if ( ( nDuration == 0 ) || ( nSlotDuration < nDuration ) )
            // {
            // nDuration = nSlotDuration;
            // }
            //
            // if ( ( nMinStartingTime == 0 ) || ( nSlotStartingTime < nMinStartingTime ) )
            // {
            // nMinStartingTime = nSlotStartingTime;
            // nMinStartingHour = slot.getStartingHour( );
            // nMinStartingMinute = slot.getStartingMinute( );
            // }
            //
            // if ( nSlotEndingTime > nMaxEndingTime )
            // {
            // nMaxEndingTime = nSlotEndingTime;
            // nMaxEndingHour = slot.getEndingHour( );
            // nMaxEndingMinute = slot.getEndingMinute( );
            // }
            // }
            //
            // int nNbWeeksToCreate = AppPropertiesService.getPropertyInt( PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT, 1 );

            // model.put( MARK_LIST_SLOTS, listSlots );
            // model.put( MARK_MIN_SLOT_DURATION, nDuration );
            // model.put( MARK_MIN_STARTING_HOUR, nMinStartingHour );
            // model.put( MARK_MIN_STARTING_MINUTE, nMinStartingMinute );
            // model.put( MARK_MAX_ENDING_HOUR, nMaxEndingHour );
            // model.put( MARK_MAX_ENDING_MINUTE, nMaxEndingMinute );
            // model.put( MARK_LOCALE, getLocale( ) );
            // model.put( MARK_BORN_DATE, getLimitedDate( nNbWeeks + nNbWeeksToCreate ) );
            // AppointmentFormJspBean.addElementsToModelForLeftColumn( request, form, getUser( ), getLocale( ), model );
            int nbSlotDay = 0;

            List<AppointmentSlot> listSlots = new ArrayList<AppointmentSlot>( );
            List<Boolean> listOpenDay = new ArrayList<Boolean>( );

            for ( AppointmentDay day : listDays )
            {
                List<AppointmentSlot> listSlotsDay = AppointmentSlotHome.findByIdDayWithFreePlaces( day.getIdDay( ) );

                if ( listSlotsDay.isEmpty( ) )
                {
                    listOpenDay.add( false );

                    for ( int i = 0; i < nbSlotDay; i++ )
                    {
                        AppointmentSlot appointmentSlot = new AppointmentSlot( );
                        listSlots.add( appointmentSlot );
                    }
                }
                else
                {
                    listOpenDay.add( true );
                    listSlots.addAll( listSlotsDay );
                    nbSlotDay = listSlotsDay.size( );
                }
            }

            Collections.sort( listSlots );
            model.put( MARK_LIST_OPEN_DAY, listOpenDay );
            model.put( MARK_NB_SLOT_DAY, nbSlotDay );
            model.put( MARK_MIN_STARTING_HOUR, form.getOpeningHour( ) );
            model.put( MARK_MIN_STARTING_MINUTE, form.getOpeningMinutes( ) );
            model.put( MARK_MAX_ENDING_HOUR, form.getClosingHour( ) );
            model.put( MARK_MAX_ENDING_MINUTE, form.getClosingMinutes( ) );
            model.put( MARK_LIST_SLOTS, listSlots );
            model.put( PARAMETER_APPOINTMENT_FORM, form );
            model.put( MARK_LIST_DAYS, listDays );
            model.put( PARAMETER_NB_WEEK, nNbWeek );
            model.put( PARAMETER_MAX_WEEK, getMaxWeek( nNbWeeksToCreate - 1, form ) );
            model.put( PARAMETER_LIM_DATES, getLimitedDate( nNbWeeksToCreate ) );
            model.put( MARK_LIST_TIME_BEGIN, listTimeBegin );
            model.put( MARK_MIN_DURATION_APPOINTMENT, nMinAppointmentDuration );
            model.put( MARK_LIST_DAYS_OF_WEEK, MESSAGE_LIST_DAYS_OF_WEEK );
            model.put( MARK_LANGUAGE, getLocale( ) );
            
            AppointmentFormJspBean.addElementsToModelForLeftColumn( request, form, getUser( ), getLocale( ), model );
            return getPage( MESSAGE_MANAGE_SLOTS_PAGE_TITLE, TEMPLATE_MANAGE_SLOTS, model );
        }
        // return redirect( request,
        // AppointmentSlotJspBean.getUrlManageSlotsByIdDay( request, strIdDay ) );
        return getPage( MESSAGE_MANAGE_SLOTS_PAGE_TITLE, TEMPLATE_MANAGE_SLOTS, model );
    }

    @View( value = VIEW_MANAGE_APPOINTMENT_SLOTS_ )
    public String getManageSlotss( HttpServletRequest request ) throws AccessDeniedException
    {
        _slotInSession = null;
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        List<AppointmentSlot> listSlots = null;
        Map<String, Object> model = getModel( );
        int nNbWeeks = 0;
        AppointmentForm form = null;

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );
            listSlots = AppointmentSlotHome.findByIdForm( nIdForm );
            form = AppointmentFormHome.findByPrimaryKey( nIdForm );
            nNbWeeks = form.getNbWeeksToDisplay( );

            boolean [ ] bArrayListDays = {
                    form.getIsOpenMonday( ), form.getIsOpenTuesday( ), form.getIsOpenWednesday( ), form.getIsOpenThursday( ), form.getIsOpenFriday( ),
                    form.getIsOpenSaturday( ), form.getIsOpenSunday( ),
            };
            AppointmentDay day = AppointmentService.getService( ).getAppointmentDayFromForm( form );
            day.setIsOpen( false );
            model.put( MARK_READ_ONLY, false );

            boolean bHasClosedDay = false;

            for ( int i = 0; i < bArrayListDays.length; i++ )
            {
                if ( !bArrayListDays [i] )
                {
                    listSlots.addAll( AppointmentService.getService( ).computeDaySlots( day, i + 1 ) );
                    bHasClosedDay = true;
                }
            }

            if ( bHasClosedDay )
            {
                Collections.sort( listSlots );
            }
        }
        else
        {
            String strIdDay = request.getParameter( PARAMETER_ID_DAY );

            if ( StringUtils.isNotBlank( strIdDay ) && StringUtils.isNumeric( strIdDay ) )
            {
                int nIdDay = Integer.parseInt( strIdDay );
                AppointmentDay day = AppointmentDayHome.findByPrimaryKey( nIdDay );

                if ( !day.getIsOpen( ) )
                {
                    return redirect( request, AppointmentFormDayJspBean.getURLManageAppointmentFormDays( request, strIdDay ) );
                }

                listSlots = AppointmentSlotHome.findByIdDay( nIdDay );
                model.put( MARK_DAY, day );
                model.put( MARK_READ_ONLY, isReadonly( day.getDate( ) ) );
                form = AppointmentFormHome.findByPrimaryKey( day.getIdForm( ) );
            }
        }

        if ( listSlots == null )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        int nDuration = 0;
        int nMinStartingTime = 0;
        int nMaxEndingTime = 0;
        int nMinStartingHour = 0;
        int nMinStartingMinute = 0;
        int nMaxEndingHour = 0;
        int nMaxEndingMinute = 0;

        for ( AppointmentSlot slot : listSlots )
        {
            int nSlotStartingTime = ( slot.getStartingHour( ) * 60 ) + slot.getStartingMinute( );
            int nSlotEndingTime = ( slot.getEndingHour( ) * 60 ) + slot.getEndingMinute( );
            int nSlotDuration = nSlotEndingTime - nSlotStartingTime;

            if ( nSlotDuration < 0 )
            {
                nSlotDuration = -1 * nSlotDuration;
            }

            if ( ( nDuration == 0 ) || ( nSlotDuration < nDuration ) )
            {
                nDuration = nSlotDuration;
            }

            if ( ( nMinStartingTime == 0 ) || ( nSlotStartingTime < nMinStartingTime ) )
            {
                nMinStartingTime = nSlotStartingTime;
                nMinStartingHour = slot.getStartingHour( );
                nMinStartingMinute = slot.getStartingMinute( );
            }

            if ( nSlotEndingTime > nMaxEndingTime )
            {
                nMaxEndingTime = nSlotEndingTime;
                nMaxEndingHour = slot.getEndingHour( );
                nMaxEndingMinute = slot.getEndingMinute( );
            }
        }

        int nNbWeeksToCreate = AppPropertiesService.getPropertyInt( PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT, 1 );

        model.put( MARK_LIST_SLOTS, listSlots );
        model.put( PARAMETER_APPOINTMENT_FORM, form );
        model.put( MARK_MIN_SLOT_DURATION, nDuration );
        model.put( MARK_MIN_STARTING_HOUR, nMinStartingHour );
        model.put( MARK_MIN_STARTING_MINUTE, nMinStartingMinute );
        model.put( MARK_MAX_ENDING_HOUR, nMaxEndingHour );
        model.put( MARK_MAX_ENDING_MINUTE, nMaxEndingMinute );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_BORN_DATE, getLimitedDate( nNbWeeks + nNbWeeksToCreate ) );
        AppointmentFormJspBean.addElementsToModelForLeftColumn( request, form, getUser( ), getLocale( ), model );

        return getPage( MESSAGE_MANAGE_SLOTS_PAGE_TITLE, TEMPLATE_MANAGE_SLOTSS, model );
    }

    /**
     * Transform Date to Calendar
     * 
     * @param objTime
     * @param iHour
     * @param iMinute
     * @return
     */
    private static Calendar getCalendarTime( Date objTime, int iHour, int iMinute )
    {
        Calendar calendar = GregorianCalendar.getInstance( Locale.FRENCH );

        if ( objTime != null )
        {
            calendar.setTime( objTime );
        }

        calendar.set( Calendar.HOUR_OF_DAY, iHour );
        calendar.set( Calendar.MINUTE, iMinute );
        calendar.set( Calendar.SECOND, 0 );

        return calendar;
    }

    /**
     * Compute Days beetween date
     * 
     * @param nStart
     * @param nEnd
     * @return
     */
    private static int getNumbersDay( Date nStart, Date nEnd )
    {
        long timeDiff = nEnd.getTime( ) - nStart.getTime( );
        timeDiff = timeDiff / 1000 / ( 24 * 60 * 60 );

        return Integer.valueOf( String.valueOf( timeDiff ) );
    }

    /**
     * Erase unavailable slots
     * 
     * @param mySlots
     * @return
     */
    private static List<AppointmentSlot> setSlotToErase( Calendar precisedDateFromNow, List<AppointmentSlot> mySlots, boolean bCheck )
    {
        if ( mySlots != null )
        {
            for ( int ni = 0; ni < mySlots.size( ); ni++ )
            {
                if ( precisedDateFromNow == null )
                {
                    mySlots.get( ni ).setIsEnabled( bCheck );
                }
                else
                {
                    Calendar now = new GregorianCalendar( Locale.FRENCH );
                    precisedDateFromNow = getCalendarTime( new Date( precisedDateFromNow.getTimeInMillis( ) ),
                            Integer.valueOf( mySlots.get( ni ).getStartingHour( ) ), Integer.valueOf( mySlots.get( ni ).getStartingMinute( ) ) );

                    if ( precisedDateFromNow.before( now ) )
                    {
                        mySlots.get( ni ).setIsEnabled( bCheck );
                    }
                }
            }
        }

        return mySlots;
    }

    /**
     * Compute unavailable Days
     * 
     * @param form
     * @param listDays
     */
    private static List<AppointmentDay> computeUnavailableDays( int nIdform, List<AppointmentDay> listDays, boolean bCheck )
    {
        if ( listDays != null )
        {
            Calendar nMaxSlots = null;

            for ( int i = 0; i < listDays.size( ); i++ )
            {
                if ( ( nMaxSlots == null )
                        || nMaxSlots.before( getCalendarTime( null, listDays.get( i ).getClosingHour( ), listDays.get( i ).getClosingMinutes( ) ) ) )
                {
                    nMaxSlots = getCalendarTime( null, listDays.get( i ).getClosingHour( ), listDays.get( i ).getClosingMinutes( ) );
                    nMaxSlots.setTimeInMillis( nMaxSlots.getTimeInMillis( ) + TimeUnit.MINUTES.toMillis( listDays.get( i ).getAppointmentDuration( ) ) );
                }

                if ( getNumbersDay( new Date( GregorianCalendar.getInstance( ).getTimeInMillis( ) ), listDays.get( i ).getDate( ) ) < 0 )
                {
                    listDays.get( i ).setListSlots( setSlotToErase( null, listDays.get( i ).getListSlots( ), bCheck ) );
                }

                if ( getNumbersDay( new Date( GregorianCalendar.getInstance( ).getTimeInMillis( ) ), listDays.get( i ).getDate( ) ) == 0 )
                {
                    Calendar tmpCalendar = new GregorianCalendar( );
                    tmpCalendar.setTime( listDays.get( i ).getDate( ) );
                    listDays.get( i ).setListSlots( setSlotToErase( tmpCalendar, listDays.get( i ).getListSlots( ), bCheck ) );
                }
            }

            for ( int i = 0; i < listDays.size( ); i++ )
            {
                if ( listDays.get( i ).getIsOpen( ) && ( listDays.get( i ).getListSlots( ) != null ) && ( listDays.get( i ).getListSlots( ).size( ) > 0 ) )
                {
                    AppointmentSlot tmpSlot = listDays.get( i ).getListSlots( ).get( listDays.get( i ).getListSlots( ).size( ) - 1 ).clone( );
                    Calendar tmpMich = getCalendarTime( null, tmpSlot.getEndingHour( ), tmpSlot.getEndingMinute( ) );
                    tmpMich.setTimeInMillis( tmpMich.getTimeInMillis( ) + TimeUnit.MINUTES.toMillis( listDays.get( i ).getAppointmentDuration( ) ) );

                    if ( tmpMich.before( nMaxSlots ) )
                    {
                        tmpSlot.setStartingHour( tmpMich.get( Calendar.HOUR_OF_DAY ) );
                        tmpSlot.setStartingMinute( tmpMich.get( Calendar.MINUTE ) );
                        tmpSlot.setEndingHour( nMaxSlots.get( Calendar.HOUR_OF_DAY ) );
                        tmpSlot.setEndingMinute( nMaxSlots.get( Calendar.MINUTE ) );
                        tmpSlot.setIdSlot( -1 );
                        tmpSlot.setIsEnabled( false );
                        listDays.get( i ).getListSlots( ).add( listDays.get( i ).getListSlots( ).size( ), tmpSlot );
                    }
                }
            }
        }

        return listDays;
    }

    /**
     * ComputeWeek in time
     * 
     * @param objMyTime
     * @return
     */
    private static int computeWeek( Date objMyTime )
    {
        int nNbWeek;
        Calendar objNow = new GregorianCalendar( );
        Calendar objAfter = new GregorianCalendar( );
        objAfter.setTime( objMyTime );

        int startWeek = objNow.get( Calendar.WEEK_OF_YEAR );
        int endWeek = objAfter.get( Calendar.WEEK_OF_YEAR );
        int idiff = objNow.get( Calendar.YEAR ) - objAfter.get( Calendar.YEAR );

        if ( ( idiff < 0 ) & ( endWeek < startWeek ) )
        {
            int ideltaYears = 0;
            Calendar objTmp = objNow.after( objAfter ) ? objAfter : objNow;
            ideltaYears += objTmp.getWeeksInWeekYear( );
            nNbWeek = ( endWeek + ideltaYears ) - startWeek;
        }
        else
        {
            int ideltaYears = 0;
            Calendar objTmp = objNow.after( objAfter ) ? objAfter : objNow;

            for ( int i = 0; i < idiff; i++ )
            {
                ideltaYears += objTmp.getWeeksInWeekYear( );
                objTmp.add( Calendar.YEAR, 1 );
            }

            nNbWeek = ( endWeek + ideltaYears ) - startWeek;
        }

        return nNbWeek;
    }

    @View( VIEW_MANAGE_HOLIDAYS )
    public String getManageHolidays( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_MODIFY_FORM, getUser( ) ) )
            {
                throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_FORM );
            }

            int nIdForm = Integer.parseInt( strIdForm );

            if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
            {
                nIdForm = Integer.parseInt( strIdForm );
            }

            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );
            List<Date> listDays = AppointmentHoliDaysHome.findByIdForm( Integer.parseInt( strIdForm ) );

            Map<String, Object> model = getModel( );
            model.put( MARK_LOCALE, getLocale( ) );
            model.put( PARAMETER_APPOINTMENT_FORM, form );
            model.put( MARK_LIST_DAYS, listDays );
            AppointmentFormJspBean.addElementsToModelForLeftColumn( request, form, getUser( ), getLocale( ), model );

            return getPage( MESSAGE_MANAGE_HOLIDAYS_PAGE_TITLE, TEMPLATE_MANAGE_HOLIDAYS, model );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    @SuppressWarnings( "deprecation" )
    @Action( ACTION_DO_MODIFY_HOLIDAYS )
    public String doModifyHolidays( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isEmpty( strIdForm ) || !StringUtils.isNumeric( strIdForm ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        int nIdForm = Integer.parseInt( strIdForm );
        List<Date> listHolidaysDb = AppointmentHoliDaysHome.findByIdForm( nIdForm );

        String strHoliday = ( request.getParameter( MARK_HOLIDAY ) == null ) ? StringUtils.EMPTY : request.getParameter( MARK_HOLIDAY );
        String strDateDay = ( request.getParameter( "dateDay" ) == null ) ? StringUtils.EMPTY : request.getParameter( "dateDay" );
        String strPathFile = StringUtils.EMPTY;

        MultipartHttpServletRequest mRequest;
        FileItem item = null;

        if ( strDateDay.isEmpty( ) && strHoliday.isEmpty( ) )
        {
            mRequest = (MultipartHttpServletRequest) request;
            item = mRequest.getFile( MARK_FILE_CLOSING_DAYS );

            if ( item != null )
            {
                if ( StringUtils.isNotEmpty( item.getName( ) ) )
                {
                    strPathFile = item.getName( );
                }
            }
        }

        if ( strHoliday.isEmpty( ) && strDateDay.isEmpty( ) && strPathFile.isEmpty( ) )
        {
            addError( MESSAGE_ERROR_EMPTY_DATE, getLocale( ) );

            return redirect( request, VIEW_MANAGE_HOLIDAYS, PARAMETER_ID_FORM, nIdForm );
        }
        else
            if ( StringUtils.isNotEmpty( strHoliday ) )
            {
                Date date = new Date( DateUtil.getDate( strHoliday ).getTime( ) );

                if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
                {
                    if ( listHolidaysDb.contains( date ) )
                    {
                        addError( MESSAGE_ERROR_DATE_EXIST, getLocale( ) );

                        return redirect( request, VIEW_MANAGE_HOLIDAYS, PARAMETER_ID_FORM, nIdForm );
                    }
                    else
                    {
                        AppointmentHoliDaysHome.create( date, nIdForm );
                        addInfo( MESSAGE_INFO_ADD_DATE, getLocale( ) );
                    }
                }
            }

        if ( StringUtils.isNotEmpty( strDateDay ) )
        {
            Date dateDay = new Date( DateUtil.getDate( strDateDay ).getTime( ) );
            AppointmentHoliDaysHome.remove( dateDay, Integer.parseInt( strIdForm ) );
            addInfo( MESSAGE_INFO_REMOVE_DATE, getLocale( ) );
        }

        if ( StringUtils.isNotEmpty( strPathFile ) )
        {
            List<Date> listImported = getImportClosingDays( item );

            if ( listImported.size( ) == 0 )
            {
                addError( MESSAGE_ERROR_EMPTY_FILE, getLocale( ) );

                return redirect( request, VIEW_MANAGE_HOLIDAYS, PARAMETER_ID_FORM, nIdForm );
            }
            else
            {
                if ( listHolidaysDb.equals( listImported ) )
                {
                    addError( MESSAGE_ERROR_EXISTING_DATES, getLocale( ) );

                    return redirect( request, VIEW_MANAGE_HOLIDAYS, PARAMETER_ID_FORM, nIdForm );
                }
                else
                {
                    for ( Date d : listImported )
                    {
                        if ( !listHolidaysDb.contains( d ) )
                        {
                            AppointmentHoliDaysHome.create( d, nIdForm );
                        }
                    }

                    addInfo( MESSAGE_INFO_IMPORTED_CLOSING_DAYS, getLocale( ) );
                }
            }
        }

        return redirect( request, VIEW_MANAGE_HOLIDAYS, PARAMETER_ID_FORM, Integer.parseInt( strIdForm ) );
    }

    public String getExportClosingDays( HttpServletRequest request, HttpServletResponse response ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isEmpty( strIdForm ) || !StringUtils.isNumeric( strIdForm ) )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT );
        }

        int nIdForm = Integer.parseInt( strIdForm );
        AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );
        List<Date> listHolidays = AppointmentHoliDaysHome.findByIdForm( nIdForm );
        XSSFWorkbook workbook = new XSSFWorkbook( );
        XSSFSheet sheet = workbook.createSheet( I18nService.getLocalizedString( "appointment.permission.label.resourceType", getLocale( ) ) );

        List<Object [ ]> tmpObj = new ArrayList<Object [ ]>( );

        if ( listHolidays != null )
        {
            Object [ ] strWriter = new String [ 1];
            strWriter [0] = form.getTitle( );
            tmpObj.add( strWriter );

            Object [ ] strInfos = new String [ 4];
            strInfos [0] = I18nService.getLocalizedString( MARK_COLUMN_DAY, getLocale( ) );
            strInfos [1] = I18nService.getLocalizedString( MARK_COLUMN_MONTH, getLocale( ) );
            strInfos [2] = I18nService.getLocalizedString( MARK_COLUMN_YEAR, getLocale( ) );
            strInfos [3] = I18nService.getLocalizedString( MARK_COLUMN_DATE, getLocale( ) );

            tmpObj.add( strInfos );
        }

        if ( listHolidays.size( ) > 0 )
        {
            for ( Date date : listHolidays )
            {
                Calendar cal = GregorianCalendar.getInstance( Locale.FRENCH );
                cal.setTime( date );

                int year = cal.get( Calendar.YEAR );
                String strmonth = cal.getDisplayName( Calendar.MONTH, Calendar.LONG, Locale.FRENCH );
                int day = cal.get( Calendar.DAY_OF_MONTH );

                Object [ ] strWriter = new String [ 4];
                strWriter [0] = String.valueOf( day );
                strWriter [1] = strmonth;
                strWriter [2] = String.valueOf( year );
                strWriter [3] = DateUtil.getDateString( date, getLocale( ) );
                tmpObj.add( strWriter );
            }
        }

        int nRownum = 0;

        for ( Object [ ] myObj : tmpObj )
        {
            Row row = sheet.createRow( nRownum++ );
            int nCellnum = 0;

            for ( Object strLine : myObj )
            {
                Cell cell = row.createCell( nCellnum++ );

                if ( strLine instanceof String )
                {
                    cell.setCellValue( (String) strLine );
                }
                else
                    if ( strLine instanceof Boolean )
                    {
                        cell.setCellValue( (Boolean) strLine );
                    }
                    else
                        if ( strLine instanceof Date )
                        {
                            cell.setCellValue( (Date) strLine );
                        }
                        else
                            if ( strLine instanceof Double )
                            {
                                cell.setCellValue( (Double) strLine );
                            }
            }
        }

        try
        {
            String now = new SimpleDateFormat( "ddMMyyyy-hhmm" ).format( GregorianCalendar.getInstance( getLocale( ) ).getTime( ) ) + "_"
                    + I18nService.getLocalizedString( "appointment.permission.label.resourceType", getLocale( ) ) + DownloadConstants.EXCEL_FILE_EXTENSION;
            response.setContentType( DownloadConstants.EXCEL_MIME_TYPE );
            response.setHeader( "Content-Disposition", "attachment; filename=\"" + now + "\";" );
            response.setHeader( "Pragma", "public" );
            response.setHeader( "Expires", "0" );
            response.setHeader( "Cache-Control", "must-revalidate,post-check=0,pre-check=0" );

            OutputStream os = response.getOutputStream( );
            workbook.write( os );
            os.close( );
            workbook.close( );
        }
        catch( IOException e )
        {
            AppLogService.error( e );
        }

        return null;
    }

    @SuppressWarnings( "deprecation" )
    private List<Date> getImportClosingDays( FileItem item ) throws AccessDeniedException
    {
        List<Date> listDays = new ArrayList<Date>( );
        FileInputStream fis = null;
        String strExtension = FilenameUtils.getExtension( item.getName( ) );
        DateFormat dateFormat = new SimpleDateFormat( MARK_FORMAT_DATE );

        if ( strExtension.equals( MARK_EXCEL_EXTENSION_XLSX ) )
        {
            try
            {
                fis = (FileInputStream) item.getInputStream( );

                // Using XSSF for xlsx format, for xls use HSSF
                Workbook workbook = new XSSFWorkbook( fis );

                int numberOfSheets = workbook.getNumberOfSheets( );

                // looping over each workbook sheet
                for ( int i = 0; i < numberOfSheets; i++ )
                {
                    Sheet sheet = workbook.getSheetAt( i );
                    Iterator<Row> rowIterator = sheet.iterator( );

                    // iterating over each row
                    while ( rowIterator.hasNext( ) )
                    {
                        Row row = (Row) rowIterator.next( );

                        if ( row.getRowNum( ) > 1 )
                        {
                            Iterator<Cell> cellIterator = row.cellIterator( );

                            // Iterating over each cell (column wise) in a particular row.
                            while ( cellIterator.hasNext( ) )
                            {
                                Cell cell = (Cell) cellIterator.next( );

                                // The Cell Containing String will is name.
                                if ( cell.getColumnIndex( ) == 3 )
                                {
                                    String strdate = StringUtils.EMPTY;

                                    if ( cell.getCellType( ) == 0 )
                                    {
                                        java.util.Date date = cell.getDateCellValue( );

                                        strdate = dateFormat.format( date );
                                    }

                                    if ( cell.getCellType( ) == 1 )
                                    {
                                        strdate = cell.getStringCellValue( );
                                    }
                                    else
                                    {
                                        AppLogService.error( MARK_ERROR_FORMAT_DATE + MARK_COLUMN + " : " + ( cell.getColumnIndex( ) + 1 ) + MARK_ROW + " : "
                                                + row.getRowNum( ) );
                                    }

                                    if ( StringUtils.isNotEmpty( strdate ) )
                                    {
                                        if ( strdate.matches( MARK_FORMAT_DATE_REGEX ) )
                                        {
                                            Date date = new Date( DateUtil.getDate( strdate ).getTime( ) );
                                            listDays.add( date );
                                        }
                                        else
                                        {
                                            AppLogService.error( MARK_ERROR_MSG );
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                fis.close( );
                workbook.close( );
            }
            catch( FileNotFoundException e )
            {
                e.printStackTrace( );
            }
            catch( IOException e )
            {
                e.printStackTrace( );
            }
        }

        return listDays;
    }

    /**
     * Test if date is anterior or not for an readlony
     * 
     * @param objdate
     * @return
     */
    private static boolean isReadonly( Date objdate )
    {
        Date dateMin = AppointmentService.getService( ).getDateMonday( 0 );

        return ( objdate == null ) ? false : objdate.before( dateMin );
    }

    /**
     * Do change the enabling of a slot
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_CHANGE_SLOT_ENABLING )
    public String doChangeSlotEnabling( HttpServletRequest request )
    {
        String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String nb_week = request.getParameter( PARAMETER_NB_WEEK );

        int nNb_week = 0;

        int nIdForm = Integer.parseInt( strIdForm );

        if ( StringUtils.isNotEmpty( strIdSlot ) && StringUtils.isNumeric( strIdSlot ) )
        {
            if ( StringUtils.isNotEmpty( nb_week ) && StringUtils.isNumeric( nb_week ) )
            {
                nNb_week = Integer.parseInt( nb_week );
            }

            int nIdSlot = Integer.parseInt( strIdSlot );
            AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( nIdSlot );
            
            if ( slot != null )
            {
	            if ( slot.getIdDay( ) > 0 )
	            {
	                AppointmentDay day = AppointmentDayHome.findByPrimaryKey( slot.getIdDay( ) );
	
	                if ( day.getIsOpen( ) )
	                {
	                    // we can only change enabling of opened days
	                    slot.setIsEnabled( !slot.getIsEnabled( ) );
	                }
	            }
	            else
	            {
	                AppointmentForm form = AppointmentFormHome.findByPrimaryKey( slot.getIdForm( ) );
	
	                if ( form.isDayOfWeekOpened( slot.getDayOfWeek( ) ) )
	                {
	                    // we can only change enabling of opened days
	                    slot.setIsEnabled( !slot.getIsEnabled( ) );
	                }
	            }
	
	            AppointmentSlotHome.update( slot );
	
	            // even though only this slot has been modified
	            // Notify for the whole form for simplicity
	            AppointmentService.getService( ).notifyAppointmentFormModified( slot.getIdForm( ) );
	
	            if ( slot.getIdDay( ) > 0 )
	            {
	                return redirect( request, VIEW_MANAGE_APPOINTMENT_SLOTS, PARAMETER_ID_FORM, slot.getIdForm( ), PARAMETER_NB_WEEK, nNb_week );
	            }
	
	            return redirect( request, VIEW_MANAGE_APPOINTMENT_SLOTS_, PARAMETER_ID_FORM, slot.getIdForm( ) );
            } 
            else 
            {
            	// form null
            	// vérifier si le formulaire est activé
            	AppointmentForm form = AppointmentFormHome.findByPrimaryKey( nIdForm );
            	if ( ! form.getIsActive( ) )
            	{            		
                    addInfo( MESSAGE_ERROR_FORM_NOT_ACTIVE, getLocale( ) );                   
                    return redirect(request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );            		 
            	}
            }
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Get the slot modification page
     * 
     * @param request
     *            The request
     * @return The HTML content to display
     */
    @View( VIEW_MODIFY_APPOINTMENT_SLOT )
    public String getViewModifySlot( HttpServletRequest request )
    {
        String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String nb_week = request.getParameter( PARAMETER_NB_WEEK );
        AppointmentSlot slot;

        Map<String, Object> model = getModel( );

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );
            model.put( PARAMETER_APPOINTMENT_FORM, nIdForm );
        }

        if ( StringUtils.isNotEmpty( strIdSlot ) && StringUtils.isNumeric( strIdSlot ) )
        {
            int nIdSlot = Integer.parseInt( strIdSlot );
            slot = AppointmentSlotHome.findByPrimaryKey( nIdSlot );
        }
        else
        {
            slot = _slotInSession;
        }

        if ( slot != null )
        {
            addInfo( MESSAGE_WARNING_CHANGES_APPLY_TO_ALL, getLocale( ) );

            model.put( MARK_SLOT, slot );
            model.put( PARAMETER_NB_WEEK, nb_week );
            model.put( MARK_LOCALE, getLocale( ) );

            AppointmentForm appointmentForm = AppointmentFormHome.findByPrimaryKey( slot.getIdForm( ) );
            AppointmentFormJspBean.addElementsToModelForLeftColumn( request, appointmentForm, getUser( ), getLocale( ), model );

            return getPage( MESSAGE_MODIFY_SLOT_PAGE_TITLE, TEMPLATE_MODIFY_SLOT, model );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Do modify a slot
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_MODIFY_SLOT )
    public String doModifySlot( HttpServletRequest request )
    {
        String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String nb_week = request.getParameter( PARAMETER_NB_WEEK );

        int nNb_week = 0;
        int nIdForm = 0;

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            nIdForm = Integer.parseInt( strIdForm );
        }

        if ( StringUtils.isNotEmpty( nb_week ) && StringUtils.isNumeric( nb_week ) )
        {
            nNb_week = Integer.parseInt( nb_week );
        }

        if ( StringUtils.isNotEmpty( strIdSlot ) && StringUtils.isNumeric( strIdSlot ) )
        {
            int nIdSlot = Integer.parseInt( strIdSlot );
            AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( nIdSlot );

            String strNbPlaces = request.getParameter( PARAMETER_NB_PLACES );

            if ( StringUtils.isNotBlank( strNbPlaces ) && StringUtils.isNumeric( strNbPlaces ) )
            {
                int nNbPlaces = Integer.parseInt( strNbPlaces );

                if ( nNbPlaces > 0 )
                {
                    slot.setNbPlaces( nNbPlaces );
                }
            }

            String strEndingTime = request.getParameter( PARAMETER_ENDING_TIME );

            if ( StringUtils.isNotEmpty( strEndingTime ) && strEndingTime.matches( AppointmentForm.CONSTANT_TIME_REGEX ) )
            {
                String [ ] strSplitedEndingTime = strEndingTime.split( AppointmentForm.CONSTANT_H );
                int nEndingHour = Integer.parseInt( strSplitedEndingTime [0] );
                int nEndingMinute = Integer.parseInt( strSplitedEndingTime [1] );

                // If the slot is associated to a day, we check that no slot of this day has an appointment, as they may be removed.
                boolean bHasEndingTimeBeenModified = ( slot.getEndingHour( ) != nEndingHour ) || ( slot.getEndingMinute( ) != nEndingMinute );

                if ( bHasEndingTimeBeenModified )
                {
                    int nRefDuration;

                    int nRefEndingTime;

                    if ( slot.getIdDay( ) > 0 )
                    {
                        AppointmentDay day = AppointmentDayHome.findByPrimaryKey( slot.getIdDay( ) );
                        AppointmentFilter filter = new AppointmentFilter( );
                        filter.setIdForm( slot.getIdForm( ) );
                        filter.setDateAppointment( day.getDate( ) );

                        List<Integer> listIdAppointments = AppointmentHome.getAppointmentIdByFilter( filter );

                        if ( ( listIdAppointments != null ) && ( listIdAppointments.size( ) > 0 ) )
                        {
                            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_DAY_HAS_APPOINTMENT ) );
                        }

                        nRefDuration = day.getAppointmentDuration( );
                        nRefEndingTime = ( day.getClosingHour( ) * 60 ) + day.getClosingMinutes( );
                    }
                    else
                    {
                        AppointmentForm form = AppointmentFormHome.findByPrimaryKey( slot.getIdForm( ) );
                        nRefDuration = form.getDurationAppointments( );
                        nRefEndingTime = ( form.getClosingHour( ) * 60 ) + form.getClosingMinutes( );
                    }

                    int nSlotDuration = ( ( nEndingHour * 60 ) + nEndingMinute ) - ( ( slot.getStartingHour( ) * 60 ) + slot.getStartingMinute( ) );

                    if ( ( ( nSlotDuration % nRefDuration ) != 0 ) && ( ( nRefDuration % nSlotDuration ) != 0 ) )
                    {
                        addError( MESSAGE_ERROR_DURATION_MUST_BE_MULTIPLE_OF_REF_DURATION, getLocale( ) );
                        _slotInSession = slot;

                        return redirectView( request, VIEW_MODIFY_APPOINTMENT_SLOT );
                    }

                    if ( ( ( nEndingHour * 60 ) + nEndingMinute ) > nRefEndingTime )
                    {
                        addError( MESSAGE_SLOT_CAN_NOT_END_AFTER_DAY_OR_FORM, getLocale( ) );
                        _slotInSession = slot;

                        return redirectView( request, VIEW_MODIFY_APPOINTMENT_SLOT );
                    }
                }

                slot.setEndingHour( nEndingHour );
                slot.setEndingMinute( nEndingMinute );

                if ( ( ( slot.getEndingHour( ) * 60 ) + slot.getEndingMinute( ) ) <= ( ( slot.getStartingHour( ) * 60 ) + slot.getStartingMinute( ) ) )
                {
                    addError( MESSAGE_ERROR_TIME_END_BEFORE_TIME_START, getLocale( ) );
                    _slotInSession = slot;

                    return redirectView( request, VIEW_MODIFY_APPOINTMENT_SLOT );
                }

                AppointmentSlotHome.update( slot );

                if ( bHasEndingTimeBeenModified )
                {
                    AppointmentSlotService.getInstance( ).updateSlotsOfDayAfterSlotModification( slot );
                }

                // even though only this slot has been modified
                // or other slots from this days have been modified
                // Notify for the whole form for simplicity
                AppointmentService.getService( ).notifyAppointmentFormModified( slot.getIdForm( ) );

                addInfo( MESSAGE_INFO_SLOT_UPDATED, getLocale( ) );

                if ( slot.getIdDay( ) > 0 )
                {
                    return redirect( request, VIEW_MANAGE_APPOINTMENT_SLOTS, PARAMETER_ID_FORM, slot.getIdForm( ), PARAMETER_NB_WEEK, nNb_week );
                }

                return redirect( request, VIEW_MANAGE_APPOINTMENT_SLOTS_, PARAMETER_ID_FORM, slot.getIdForm( ) );

                // return redirect( request, getUrlManageSlotsByIdForm( request, slot.getIdForm( ) ) );
            }
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Get the URL to manage slots associated with a form
     * 
     * @param request
     *            The request
     * @param nIdForm
     *            The id of the form
     * @return The URL to manage slots
     */
    public static String getUrlManageSlotsByIdForm( HttpServletRequest request, int nIdForm )
    {
        return getUrlManageSlotsByIdForm( request, Integer.toString( nIdForm ) );
    }

    /**
     * Get the URL to manage slots associated with a form
     * 
     * @param request
     *            The request
     * @param strIdForm
     *            The id of the form
     * @return The URL to manage slots
     */
    public static String getUrlManageSlotsByIdForm( HttpServletRequest request, String strIdForm )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL_MANAGE_APPOINTMENT_SLOT );
        urlItem.addParameter( PARAMETER_ID_FORM, strIdForm );

        return urlItem.getUrl( );
    }

    /**
     * Get the URL to manage slots associated with a day
     * 
     * @param request
     *            The request
     * @param nIdDay
     *            The id of the day
     * @return The URL to manage slots
     */
    public static String getUrlManageSlotsByIdDay( HttpServletRequest request, int nIdDay )
    {
        return getUrlManageSlotsByIdDay( request, Integer.toString( nIdDay ) );
    }

    /**
     * Get the URL to manage slots associated with a day
     * 
     * @param request
     *            The request
     * @param strIdDay
     *            The id of the day
     * @return The URL to manage slots
     */
    public static String getUrlManageSlotsByIdDay( HttpServletRequest request, String strIdDay )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL_MANAGE_APPOINTMENT_SLOT );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_APPOINTMENT_SLOTS );
        urlItem.addParameter( PARAMETER_ID_DAY, strIdDay );

        return urlItem.getUrl( );
    }

    /**
     * Get Limited Date
     * 
     * @param nBWeeks
     * @return
     */
    private String [ ] getLimitedDate( int nBWeeks )
    {
        Calendar startCal = GregorianCalendar.getInstance( Locale.FRENCH );
        Calendar endCal = GregorianCalendar.getInstance( Locale.FRENCH );
        startCal.set( Calendar.WEEK_OF_YEAR, startCal.get( Calendar.WEEK_OF_YEAR ) - nBWeeks );
        startCal.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
        endCal.set( Calendar.WEEK_OF_YEAR, endCal.get( Calendar.WEEK_OF_YEAR ) + nBWeeks );
        endCal.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
        endCal.add( Calendar.DATE, -1 );

        String [ ] retour = {
                DateUtil.getDateString( startCal.getTime( ), getLocale( ) ), DateUtil.getDateString( endCal.getTime( ), getLocale( ) )
        };

        return retour;
    }

    public static String getUrlManageHolidays( HttpServletRequest request, String strIdForm )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL_MANAGE_APPOINTMENT_SLOT );
        urlItem.addParameter( PARAMETER_ID_FORM, strIdForm );

        return urlItem.getUrl( );
    }

    public static int getMaxWeek( int nbWeekToCreate, AppointmentForm form )
    {
        if ( form.getDateLimit( ) != null )
        {
            Date dateMin = null;
            List<AppointmentDay> listDays = AppointmentDayHome.findByIdForm( form.getIdForm( ) );
            if ( !listDays.isEmpty( ) )
            {
                dateMin = listDays.get( 0 ).getDate( );
            }
            if ( dateMin == null )
            {
                Calendar c = Calendar.getInstance( );
                dateMin = new Date( c.getTimeInMillis( ) );
            }
            long diff = form.getDateLimit( ).getTime( ) - dateMin.getTime( );
            long diffDays = diff / ( 24 * 60 * 60 * 1000 );
            int maxWeek = (int) diffDays / 7;
            Calendar cal = GregorianCalendar.getInstance( Locale.FRANCE );
            int nCurrentDayOfWeek = cal.get( cal.DAY_OF_WEEK );
            cal.add( Calendar.DAY_OF_WEEK, Calendar.MONDAY - nCurrentDayOfWeek );
            Date datMax = null;
            do
            {
                cal.add( Calendar.WEEK_OF_YEAR, maxWeek );
                datMax = new Date( cal.getTimeInMillis( ) );
                if ( datMax.before( form.getDateLimit( ) ) )
                {
                    maxWeek = maxWeek + 1;
                }
                cal = GregorianCalendar.getInstance( Locale.FRANCE );
                cal.add( Calendar.DAY_OF_WEEK, Calendar.MONDAY - nCurrentDayOfWeek );
            }
            while ( datMax.before( form.getDateLimit( ) ) );

            return maxWeek;

        }
        else
        {
            return nbWeekToCreate;

        }
    }

}
