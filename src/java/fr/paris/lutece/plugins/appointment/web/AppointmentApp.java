/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.Strings;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.calendar.CalendarTemplate;
import fr.paris.lutece.plugins.appointment.business.calendar.CalendarTemplateHome;
import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.message.FormMessage;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.rule.FormRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.slot.Period;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.exception.AppointmentSavedException;
import fr.paris.lutece.plugins.appointment.exception.SlotFullException;
import fr.paris.lutece.plugins.appointment.log.LogUtilities;
import fr.paris.lutece.plugins.appointment.service.AppointmentResponseService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.AppointmentUtilities;
import fr.paris.lutece.plugins.appointment.service.DisplayService;
import fr.paris.lutece.plugins.appointment.service.EntryService;
import fr.paris.lutece.plugins.appointment.service.FormMessageService;
import fr.paris.lutece.plugins.appointment.service.FormRuleService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.ReservationRuleService;
import fr.paris.lutece.plugins.appointment.service.SlotSafeService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.UserService;
import fr.paris.lutece.plugins.appointment.service.Utilities;
import fr.paris.lutece.plugins.appointment.service.WeekDefinitionService;
import fr.paris.lutece.plugins.appointment.service.listeners.AppointmentListenerManager;
import fr.paris.lutece.plugins.appointment.service.upload.AppointmentAsynchronousUploadHandler;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.ErrorMessage;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides a simple implementation of an Appointment XPage (On Front Office)
 * 
 * @author Laurent Payen
 *
 */
@Controller( xpageName = AppointmentApp.XPAGE_NAME, pageTitleI18nKey = AppointmentApp.MESSAGE_DEFAULT_PAGE_TITLE, pagePathI18nKey = AppointmentApp.MESSAGE_DEFAULT_PATH )
public class AppointmentApp extends MVCApplication
{

    /**
     * Default page of XPages of this app
     */
    public static final String MESSAGE_DEFAULT_PATH = "appointment.appointment.name";

    /**
     * Default page title of XPages of this app
     */
    public static final String MESSAGE_DEFAULT_PAGE_TITLE = "appointment.appointmentApp.defaultTitle";

    /**
     * The name of the XPage
     */
    protected static final String XPAGE_NAME = "appointment";

    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = 5741361182728887387L;

    // Templates
    private static final String TEMPLATE_APPOINTMENT_FORM_LIST = "/skin/plugins/appointment/appointment_form_list.html";
    private static final String TEMPLATE_APPOINTMENT_FORM = "/skin/plugins/appointment/appointment_form.html";
    private static final String TEMPLATE_APPOINTMENT_FORM_RECAP = "/skin/plugins/appointment/appointment_form_recap.html";
    private static final String TEMPLATE_APPOINTMENT_CREATED = "skin/plugins/appointment/appointment_created.html";
    private static final String TEMPLATE_CANCEL_APPOINTMENT = "skin/plugins/appointment/cancel_appointment.html";
    private static final String TEMPLATE_APPOINTMENT_CANCELED = "skin/plugins/appointment/appointment_canceled.html";
    private static final String TEMPLATE_MY_APPOINTMENTS = "skin/plugins/appointment/my_appointments.html";
    private static final String TEMPLATE_HTML_CODE_FORM = "skin/plugins/appointment/html_code_form.html";

    // Views
    public static final String VIEW_APPOINTMENT_FORM = "getViewAppointmentForm";
    public static final String VIEW_APPOINTMENT_CALENDAR = "getViewAppointmentCalendar";
    private static final String VIEW_APPOINTMENT_FORM_LIST = "getViewFormList";
    private static final String VIEW_DISPLAY_RECAP_APPOINTMENT = "displayRecapAppointment";
    private static final String VIEW_GET_APPOINTMENT_CREATED = "getAppointmentCreated";
    private static final String VIEW_APPOINTMENT_CANCELED = "getAppointmentCanceled";
    private static final String VIEW_GET_MY_APPOINTMENTS = "getMyAppointments";
    private static final String VIEW_GET_VIEW_CANCEL_APPOINTMENT = "getViewCancelAppointment";

    // Actions
    private static final String ACTION_DO_VALIDATE_FORM = "doValidateForm";
    private static final String ACTION_DO_MAKE_APPOINTMENT = "doMakeAppointment";
    private static final String ACTION_DO_CANCEL_APPOINTMENT = "doCancelAppointment";

    // Parameters
    private static final String PARAMETER_STARTING_DATE_TIME = "starting_date_time";
    private static final String PARAMETER_ENDING_DATE_TIME = "ending_date_time";
    private static final String PARAMETER_IS_OPEN = "is_open";
    private static final String PARAMETER_IS_SPECIFIC = "is_specific";
    private static final String PARAMETER_MAX_CAPACITY = "max_capacity";
    private static final String PARAMETER_ENDING_DATE_OF_DISPLAY = "ending_date_of_display";
    private static final String PARAMETER_STR_ENDING_DATE_OF_DISPLAY = "str_ending_date_of_display";
    private static final String PARAMETER_DATE_OF_DISPLAY = "date_of_display";
    private static final String PARAMETER_DAY_OF_WEEK = "dow";
    private static final String PARAMETER_HIDDEN_DAYS = "hidden_days";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_EVENTS = "events";
    private static final String PARAMETER_MIN_DURATION = "min_duration";
    private static final String PARAMETER_MIN_TIME = "min_time";
    private static final String PARAMETER_MAX_TIME = "max_time";
    private static final String PARAMETER_EMAIL = "email";
    private static final String PARAMETER_EMAIL_CONFIRMATION = "emailConfirm";
    private static final String PARAMETER_FIRST_NAME = "firstname";
    private static final String PARAMETER_LAST_NAME = "lastname";
    private static final String PARAMETER_NUMBER_OF_BOOKED_SEATS = "nbBookedSeats";
    private static final String PARAMETER_ID_SLOT = "id_slot";
    private static final String PARAMETER_ID_APPOINTMENT = "id_appointment";
    private static final String PARAMETER_BACK = "back";
    private static final String PARAMETER_REF_APPOINTMENT = "refAppointment";
    private static final String PARAMETER_FROM_MY_APPOINTMENTS = "fromMyappointments";
    private static final String PARAMETER_REFERER = "referer";
    private static final String PARAMETER_WEEK_VIEW = "week_view";
    private static final String PARAMETER_DAY_VIEW = "day_view";
    private static final String PARAMETER_ANCHOR = "anchor";
    private static final String PARAMETER_MODIFICATION_FORM = "mod";
    private static final String PARAMETER_MIN_DATE_OF_OPEN_DAY = "min_date_of_open_day";
    private static final String PARAMETER_MAX_DATE_OF_OPEN_DAY = "max_date_of_open_day";
    // Mark
    private static final String MARK_INFOS = "infos";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_LANGUAGE = "language";
    private static final String MARK_FORM = "form";
    private static final String MARK_USER = "user";
    private static final String MARK_FORM_MESSAGES = "formMessages";
    private static final String MARK_STR_ENTRY = "str_entry";
    private static final String MARK_APPOINTMENT = "appointment";
    private static final String MARK_LIST_ERRORS = "listAllErrors";
    private static final String MARK_PLACES = "nbplaces";
    private static final String MARK_DATE_APPOINTMENT = "dateAppointment";
    private static final String MARK_STARTING_TIME_APPOINTMENT = "startingTimeAppointment";
    private static final String MARK_ENDING_TIME_APPOINTMENT = "endingTimeAppointment";
    private static final String MARK_FORM_LIST = "form_list";
    private static final String MARK_FORM_HTML = "form_html";
    private static final String MARK_FORM_ERRORS = "form_errors";
    private static final String MARK_FORM_CALENDAR_ERRORS = "formCalendarErrors";
    private static final String MARK_CAPTCHA = "captcha";
    private static final String MARK_REF = "%%REF%%";
    private static final String MARK_DATE_APP = "%%DATE%%";
    private static final String MARK_TIME_BEGIN = "%%HEURE_DEBUT%%";
    private static final String MARK_TIME_END = "%%HEURE_FIN%%";
    private static final String MARK_LIST_APPOINTMENTS = "list_appointments";
    private static final String MARK_BACK_URL = "backUrl";
    private static final String MARK_FROM_URL = "fromUrl";
    private static final String MARK_LIST_RESPONSE_RECAP_DTO = "listResponseRecapDTO";
    private static final String MARK_DATA = "data";
    private static final String MARK_BASE_64 = "base64";
    private static final String MARK_SEMI_COLON = ";";
    private static final String MARK_COMMA = ",";
    private static final String MARK_COLON = ":";
    private static final String MARK_ICONS = "icons";
    private static final String MARK_ICON_NULL = "NULL";
    private static final String MARK_ANCHOR = "#";
    private static final String MARK_APPOINTMENT_ALREADY_CANCELLED = "alreadyCancelled";
    private static final String MARK_NO_APPOINTMENT_WITH_THIS_REFERENCE = "noAppointmentWithThisReference";
    private static final String MARK_APPOINTMENT_PASSED = "appointmentPassed";

    // Errors
    private static final String ERROR_MESSAGE_SLOT_FULL = "appointment.message.error.slotFull";
    private static final String ERROR_MESSAGE_CAPTCHA = "portal.admin.message.wrongCaptcha";
    private static final String ERROR_MESSAGE_NB_MIN_DAYS_BETWEEN_TWO_APPOINTMENTS = "appointment.validation.appointment.NbMinDaysBetweenTwoAppointments.error";
    private static final String ERROR_MESSAGE_NB_MAX_APPOINTMENTS_ON_A_PERIOD = "appointment.validation.appointment.NbMaxAppointmentsOnAPeriod.error";
    private static final String ERROR_MESSAGE_FORM_NOT_ACTIVE = "appointment.validation.appointment.formNotActive";
    private static final String ERROR_MESSAGE_NO_STARTING_VALIDITY_DATE = "appointment.validation.appointment.noStartingValidityDate";
    private static final String ERROR_MESSAGE_FORM_NO_MORE_VALID = "appointment.validation.appointment.formNoMoreValid";
    private static final String ERROR_MESSAGE_NO_AVAILABLE_SLOT = "appointment.validation.appointment.noAvailableSlot";

    // Session keys
    private static final String SESSION_APPOINTMENT_FORM_ERRORS = "appointment.session.formErrors";
    private static final String SESSION_NOT_VALIDATED_APPOINTMENT = "appointment.appointmentFormService.notValidatedAppointment";
    private static final String SESSION_VALIDATED_APPOINTMENT = "appointment.appointmentFormService.validatedAppointment";
    private static final String SESSION_ATTRIBUTE_APPOINTMENT_FORM = "appointment.session.appointmentForm";

    // Messages
    private static final String MESSAGE_CANCEL_APPOINTMENT_PAGE_TITLE = "appointment.cancelAppointment.pageTitle";
    private static final String MESSAGE_MY_APPOINTMENTS_PAGE_TITLE = "appointment.myAppointments.name";

    // Local variables
    private transient CaptchaSecurityService _captchaSecurityService;

    // Properties
    private static final String PROPERTY_USER_ATTRIBUTE_FIRST_NAME = "appointment.userAttribute.firstName";
    private static final String PROPERTY_USER_ATTRIBUTE_LAST_NAME = "appointment.userAttribute.lastName";
    private static final String PROPERTY_USER_ATTRIBUTE_PREFERED_NAME = "appointment.userAttribute.preferred_username";
    private static final String PROPERTY_USER_ATTRIBUTE_EMAIL = "appointment.userAttribute.email";
    private static final String PROPERTY_USER_ATTRIBUTE_GUID = "appointment.userAttribute.guid";

    private static final String AGENDA_WEEK = "agendaWeek";
    private static final String BASIC_WEEK = "basicWeek";
    private static final String AGENDA_DAY = "agendaDay";
    private static final String BASIC_DAY = "basicDay";

    private static final String STEP_3 = "step3";

    /**
     * Get the calendar view
     * 
     * @param request
     * @return the Xpage
     */
    @SuppressWarnings( "unchecked" )
    @View( VIEW_APPOINTMENT_CALENDAR )
    public XPage getViewAppointmentCalendar( HttpServletRequest request )
    {
        Locale locale = getLocale( request );
        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        Form form = FormService.findFormLightByPrimaryKey( nIdForm );
        AppointmentFormDTO appointmentForm = FormService.buildAppointmentForm( nIdForm, 0, 0 );
        boolean bError = false;
        if ( !form.getIsActive( ) )
        {
            addError( ERROR_MESSAGE_FORM_NOT_ACTIVE, locale );
            bError = true;
        }
        FormMessage formMessages = FormMessageService.findFormMessageByIdForm( nIdForm );
        // Check if the date of display and the endDateOfDisplay are in the
        // validity date range of the form
        LocalDate startingValidityDate = form.getStartingValidityDate( );
        if ( startingValidityDate == null )
        {
            addError( ERROR_MESSAGE_NO_STARTING_VALIDITY_DATE, locale );
            bError = true;
        }
        LocalDate startingDateOfDisplay = LocalDate.now( );
        if ( startingValidityDate != null && startingValidityDate.isAfter( startingDateOfDisplay ) )
        {
            startingDateOfDisplay = startingValidityDate;
        }
        Display display = DisplayService.findDisplayWithFormId( nIdForm );
        // Get the nb weeks to display
        int nNbWeeksToDisplay = display.getNbWeeksToDisplay( );
        // Calculate the ending date of display with the nb weeks to display
        // since today
        // We calculate the number of weeks including the current week, so it
        // will end to the (n) next sunday
        TemporalField fieldISO = WeekFields.of( locale ).dayOfWeek( );
        LocalDate dateOfSunday = startingDateOfDisplay.with( fieldISO, DayOfWeek.SUNDAY.getValue( ) );
        LocalDate endingDateOfDisplay = dateOfSunday.plusWeeks( nNbWeeksToDisplay - 1 );
        // if the ending date of display is after the ending validity date of
        // the form
        // assign the ending date of display with the ending validity date of
        // the form
        LocalDate endingValidityDate = form.getEndingValidityDate( );
        if ( endingValidityDate != null )
        {
            if ( endingDateOfDisplay.isAfter( endingValidityDate ) )
            {
                endingDateOfDisplay = endingValidityDate;
            }
            if ( startingDateOfDisplay.isAfter( endingDateOfDisplay ) )
            {
                addError( ERROR_MESSAGE_FORM_NO_MORE_VALID, locale );
                bError = true;
            }
        }
        // Get the current date of display of the calendar, if it exists
        String strDateOfDisplay = request.getParameter( PARAMETER_DATE_OF_DISPLAY );
        LocalDate dateOfDisplay = startingDateOfDisplay;
        if ( StringUtils.isNotEmpty( strDateOfDisplay ) )
        {
            dateOfDisplay = LocalDate.parse( strDateOfDisplay );
        }
        // Get all the week definitions
        HashMap<LocalDate, WeekDefinition> mapWeekDefinition = WeekDefinitionService.findAllWeekDefinition( nIdForm );
        List<WeekDefinition> listWeekDefinition = new ArrayList<>( mapWeekDefinition.values( ) );
        // Filter on the list of weekdefinition on the starting date and the
        // ending date of display
        if ( listWeekDefinition.size( ) > 1 )
        {
            WeekDefinition weekDefinitionClosest = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( nIdForm, startingDateOfDisplay );
            LocalDate dateOfClosestWeekDefinition = weekDefinitionClosest.getDateOfApply( );
            LocalDate maxEndingDateOfWeekDefinition = endingDateOfDisplay;
            listWeekDefinition = listWeekDefinition
                    .stream( )
                    .filter(
                            w -> ( w.getDateOfApply( ).isEqual( dateOfClosestWeekDefinition ) || w.getDateOfApply( ).isAfter( dateOfClosestWeekDefinition ) )
                                    && ( w.getDateOfApply( ).isBefore( maxEndingDateOfWeekDefinition ) || w.getDateOfApply( ).isEqual(
                                            maxEndingDateOfWeekDefinition ) ) ).collect( Collectors.toList( ) );
        }
        // Get the min time of all the week definitions
        LocalTime minStartingTime = WeekDefinitionService.getMinStartingTimeOfAListOfWeekDefinition( listWeekDefinition );
        // Get the max time of all the week definitions
        LocalTime maxEndingTime = WeekDefinitionService.getMaxEndingTimeOfAListOfWeekDefinition( listWeekDefinition );
        // Get all the working days of all the week definitions
        List<String> listStrBase0OpenDaysOfWeek = new ArrayList<>(
                WeekDefinitionService.getSetDaysOfWeekOfAListOfWeekDefinitionForFullCalendar( listWeekDefinition ) );
        // Build the slots if no errors
        List<Slot> listSlots = new ArrayList<>( );
        if ( !bError )
        {
            listSlots = SlotService.buildListSlot( nIdForm, mapWeekDefinition, startingDateOfDisplay, endingDateOfDisplay );
            // Get the min time from now before a user can take an appointment
            // (in hours)
            FormRule formRule = FormRuleService.findFormRuleWithFormId( nIdForm );
            int minTimeBeforeAppointment = formRule.getMinTimeBeforeAppointment( );
            LocalDateTime dateTimeBeforeAppointment = LocalDateTime.now( ).plusHours( minTimeBeforeAppointment );
            // Filter the list of slots
            if ( CollectionUtils.isNotEmpty( listSlots ) )
            {
                listSlots = listSlots.stream( ).filter( s -> s.getStartingDateTime( ).isAfter( dateTimeBeforeAppointment ) ).collect( Collectors.toList( ) );
            }
            LocalDate firstDateOfFreeOpenSlot = null;
            if ( CollectionUtils.isNotEmpty( listSlots ) )
            {
                // Need to find the first available slot from now (with time)
                List<Slot> listAvailableSlots = listSlots.stream( ).filter( s -> ( s.getNbPotentialRemainingPlaces( ) > 0 && s.getIsOpen( ) == Boolean.TRUE ) )
                        .collect( Collectors.toList( ) );
                if ( CollectionUtils.isNotEmpty( listAvailableSlots ) )
                {
                    firstDateOfFreeOpenSlot = listAvailableSlots.stream( ).min( ( s1, s2 ) -> s1.getStartingDateTime( ).compareTo( s2.getStartingDateTime( ) ) )
                            .get( ).getDate( );
                }
            }
            if ( firstDateOfFreeOpenSlot == null )
            {
                addError( ERROR_MESSAGE_NO_AVAILABLE_SLOT, locale );
                bError = true;
            }
            // Display the week with the first available slot
            if ( firstDateOfFreeOpenSlot != null && firstDateOfFreeOpenSlot.isAfter( dateOfDisplay ) )
            {
                dateOfDisplay = firstDateOfFreeOpenSlot;
            }
        }
        Map<String, Object> model = getModel( );
        if ( bError )
        {
            model.put( MARK_FORM_CALENDAR_ERRORS, bError );
        }
        if ( formMessages != null && StringUtils.isNotEmpty( formMessages.getCalendarDescription( ) ) )
        {
            List<ErrorMessage> listInfos = (List<ErrorMessage>) model.get( MARK_INFOS );
            if ( listInfos == null )
            {
                listInfos = new ArrayList<ErrorMessage>( );
                model.put( MARK_INFOS, listInfos );
            }
            MVCMessage message = new MVCMessage( formMessages.getCalendarDescription( ) );
            listInfos.add( message );
        }

        // Get the min and max date of the open days (for the week navigation on
        // open days calendar templates)
        HashSet<Integer> setOpenDays = WeekDefinitionService.getOpenDaysOfWeek( listWeekDefinition );
        LocalDate minDateOfOpenDay = LocalDate.now( ).with( DayOfWeek.of( setOpenDays.stream( ).min( Comparator.naturalOrder( ) ).get( ) ) );
        LocalDate maxDateOfOpenDay = endingDateOfDisplay.with( DayOfWeek.of( setOpenDays.stream( ).max( Comparator.naturalOrder( ) ).get( ) ) );
        model.put( PARAMETER_MIN_DATE_OF_OPEN_DAY, minDateOfOpenDay );
        model.put( PARAMETER_MAX_DATE_OF_OPEN_DAY, maxDateOfOpenDay );
        model.put( MARK_FORM, appointmentForm );
        model.put( PARAMETER_ID_FORM, nIdForm );
        model.put( MARK_FORM_MESSAGES, formMessages );
        model.put( PARAMETER_ENDING_DATE_OF_DISPLAY, endingDateOfDisplay );
        model.put( PARAMETER_STR_ENDING_DATE_OF_DISPLAY, endingDateOfDisplay.format( Utilities.getDateFormatter( ) ) );
        model.put( PARAMETER_DATE_OF_DISPLAY, dateOfDisplay );
        model.put( PARAMETER_DAY_OF_WEEK, listStrBase0OpenDaysOfWeek );
        model.put( PARAMETER_MIN_TIME, AppointmentUtilities.getMinTimeToDisplay( minStartingTime ) );
        model.put( PARAMETER_MAX_TIME, AppointmentUtilities.getMaxTimeToDisplay( maxEndingTime ) );
        model.put( PARAMETER_MIN_DURATION, LocalTime.MIN.plusMinutes( AppointmentUtilities.THIRTY_MINUTES ) );
        CalendarTemplate calendarTemplate = CalendarTemplateHome.findByPrimaryKey( display.getIdCalendarTemplate( ) );
        List<String> listHiddenDays = new ArrayList<>( );
        String dayView = AGENDA_DAY;
        String weekView = AGENDA_WEEK;
        for ( int i = 0; i < 7; i++ )
        {
            listHiddenDays.add( Integer.toString( i ) );
        }
        /**
         * Calculate the hidden days and set the view (Day and week) with the type of calendar
         */
        switch( calendarTemplate.getTitle( ) )
        {
            case CalendarTemplate.CALENDAR:
                listHiddenDays.clear( );
                dayView = AGENDA_DAY;
                weekView = AGENDA_WEEK;
                break;
            case CalendarTemplate.CALENDAR_OPEN_DAYS:
                // update the list of the days to hide
                listHiddenDays.removeAll( listStrBase0OpenDaysOfWeek );
                dayView = AGENDA_DAY;
                weekView = AGENDA_WEEK;
                break;
            case CalendarTemplate.FREE_SLOTS:
                // Keep only the available slots
                listSlots = listSlots.stream( ).filter( s -> ( ( s.getNbRemainingPlaces( ) > 0 ) && ( s.getIsOpen( ) ) ) ).collect( Collectors.toList( ) );
                listHiddenDays.clear( );
                dayView = BASIC_DAY;
                weekView = BASIC_WEEK;
                break;
            case CalendarTemplate.FREE_SLOTS_ON_OPEN_DAYS:
                // Keep only the available slots
                listSlots = listSlots.stream( ).filter( s -> ( ( s.getNbRemainingPlaces( ) > 0 ) && ( s.getIsOpen( ) ) ) ).collect( Collectors.toList( ) );
                // update the list of the days to hide
                listHiddenDays.removeAll( listStrBase0OpenDaysOfWeek );
                dayView = BASIC_DAY;
                weekView = BASIC_WEEK;
                break;
            default:
                listHiddenDays.clear( );
                dayView = AGENDA_DAY;
                weekView = AGENDA_WEEK;
                break;
        }
        model.put( PARAMETER_EVENTS, listSlots );
        model.put( PARAMETER_HIDDEN_DAYS, listHiddenDays );
        model.put( PARAMETER_DAY_VIEW, dayView );
        model.put( PARAMETER_WEEK_VIEW, weekView );
        HtmlTemplate template = AppTemplateService.getTemplate( calendarTemplate.getTemplatePath( ), locale, model );
        XPage xpage = new XPage( );
        xpage.setContent( template.getHtml( ) );
        xpage.setPathLabel(

        getDefaultPagePath( locale ) );
        xpage.setTitle( getDefaultPageTitle( locale ) );
        return xpage;
    }

    /**
     * Get the form appointment view (front office)
     * 
     * @param request
     *            the request
     * @return the xpage
     * @throws UserNotSignedException
     */
    @SuppressWarnings( "unchecked" )
    @View( VIEW_APPOINTMENT_FORM )
    public synchronized XPage getViewAppointmentForm( HttpServletRequest request ) throws UserNotSignedException
    {
        AppointmentFormDTO form = (AppointmentFormDTO) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = Integer.parseInt( strIdForm );
        if ( form == null )
        {
            form = FormService.buildAppointmentForm( nIdForm, 0, 0 );
        }
        checkMyLuteceAuthentication( form, request );
        // Patch needed for authentication after being on the form
        String secondAttempt = request.getParameter( "secondAttempt" );
        boolean bTestSecondAttempt = Boolean.FALSE;
        if ( StringUtils.isNotEmpty( secondAttempt ) )
        {
            bTestSecondAttempt = Boolean.TRUE;
        }
        // Need to manage the anchor
        String anchor = request.getParameter( PARAMETER_ANCHOR );
        if ( StringUtils.isNotEmpty( anchor ) )
        {
            LinkedHashMap<String, String> additionalParameters = new LinkedHashMap<String, String>( );
            additionalParameters.put( PARAMETER_ID_FORM, strIdForm );
            additionalParameters.put( PARAMETER_ID_SLOT, request.getParameter( PARAMETER_ID_SLOT ) );
            additionalParameters.put( PARAMETER_STARTING_DATE_TIME, request.getParameter( PARAMETER_STARTING_DATE_TIME ) );
            additionalParameters.put( PARAMETER_ENDING_DATE_TIME, request.getParameter( PARAMETER_ENDING_DATE_TIME ) );
            additionalParameters.put( PARAMETER_IS_OPEN, request.getParameter( PARAMETER_IS_OPEN ) );
            additionalParameters.put( PARAMETER_IS_SPECIFIC, request.getParameter( PARAMETER_IS_SPECIFIC ) );
            additionalParameters.put( PARAMETER_MAX_CAPACITY, request.getParameter( PARAMETER_MAX_CAPACITY ) );
            additionalParameters.put( PARAMETER_ANCHOR, MARK_ANCHOR + anchor );
            return redirect( request, VIEW_APPOINTMENT_FORM, additionalParameters );

        }
        String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );
        boolean bModificationForm = false;
        if ( strIdSlot == null )
        {
            bModificationForm = true;
        }
        Integer nIdSlot = null;
        if ( StringUtils.isNumeric( strIdSlot ) )
        {
            nIdSlot = Integer.parseInt( strIdSlot );
        }
        AppointmentDTO oldAppointmentDTO = null;
        // Get the not validated appointment in session if it exists
        AppointmentDTO appointmentDTO = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_NOT_VALIDATED_APPOINTMENT );
        if ( appointmentDTO == null )
        {
            // Try to get the validated appointment in session
            // (in case the user click on back button in the recap view (or
            // modification)
            appointmentDTO = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_VALIDATED_APPOINTMENT );
            if ( appointmentDTO != null )
            {
                request.getSession( ).removeAttribute( SESSION_VALIDATED_APPOINTMENT );
                request.getSession( ).setAttribute( SESSION_NOT_VALIDATED_APPOINTMENT, appointmentDTO );
                if ( nIdSlot != null && appointmentDTO.getIdSlot( ) != nIdSlot )
                {
                    oldAppointmentDTO = appointmentDTO;
                }
            }
        }
        else
        {
            // Appointment DTO not validated in session
            // Need to verify if the slot has not changed
            if ( nIdSlot != null && appointmentDTO.getIdSlot( ) != nIdSlot )
            {
                oldAppointmentDTO = appointmentDTO;
            }
        }
        if ( appointmentDTO == null || oldAppointmentDTO != null )
        {
            // Need to get back the informations the user has entered
            appointmentDTO = new AppointmentDTO( );
            if ( oldAppointmentDTO != null )
            {
                appointmentDTO.setFirstName( oldAppointmentDTO.getFirstName( ) );
                appointmentDTO.setLastName( oldAppointmentDTO.getLastName( ) );
                appointmentDTO.setEmail( oldAppointmentDTO.getEmail( ) );
                appointmentDTO.setPhoneNumber( oldAppointmentDTO.getPhoneNumber( ) );
                appointmentDTO.setNbBookedSeats( oldAppointmentDTO.getNbBookedSeats( ) );
                appointmentDTO.setListResponse( oldAppointmentDTO.getListResponse( ) );
                appointmentDTO.setMapResponsesByIdEntry( oldAppointmentDTO.getMapResponsesByIdEntry( ) );
            }
        }
        if ( !bModificationForm )
        {
            Slot slot = null;
            // If nIdSlot == 0, the slot has not been created yet
            if ( nIdSlot == 0 )
            {
                // Need to get all the informations to create the slot
                LocalDateTime startingDateTime = LocalDateTime.parse( request.getParameter( PARAMETER_STARTING_DATE_TIME ) );
                LocalDateTime endingDateTime = LocalDateTime.parse( request.getParameter( PARAMETER_ENDING_DATE_TIME ) );
               
                boolean bIsOpen = Boolean.parseBoolean( request.getParameter( PARAMETER_IS_OPEN ) );
                boolean bIsSpecific = Boolean.parseBoolean( request.getParameter( PARAMETER_IS_SPECIFIC ) );
                int nMaxCapacity = Integer.parseInt( request.getParameter( PARAMETER_MAX_CAPACITY ) );
                slot = SlotService.buildSlot( nIdForm, new Period( startingDateTime, endingDateTime ), nMaxCapacity, nMaxCapacity, nMaxCapacity, 0,
                            bIsOpen, bIsSpecific );
                slot = SlotSafeService.createSlot( slot );
                
            }else{
            
            	slot = SlotService.findSlotById( nIdSlot );
            }
            ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( nIdForm, slot.getDate( ) );
            WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( nIdForm, slot.getDate( ) );
            form = FormService.buildAppointmentForm( nIdForm, reservationRule.getIdReservationRule( ), weekDefinition.getIdWeekDefinition( ) );
           
            synchronized(slot){
            	 
                // Need to check competitive access
                // May be the slot is already taken at the same time
	            if ( !bTestSecondAttempt && slot.getNbPotentialRemainingPlaces( ) == 0 )
	            {
	                addError( ERROR_MESSAGE_SLOT_FULL, getLocale( request ) );
	                return redirect( request, VIEW_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, nIdForm );
	            }
	            
            
	            appointmentDTO.setSlot( slot );
	            appointmentDTO.setIdSlot( slot.getIdSlot( ) );
	            appointmentDTO.setDateOfTheAppointment( slot.getDate( ).format( Utilities.getDateFormatter( ) ) );
	            appointmentDTO.setIdForm( nIdForm );
	            LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
	            if ( user != null )
	            {
	                setUserInfo( request, appointmentDTO );
	            }        
	            AppointmentUtilities.putTimerInSession( request, slot.getIdSlot(), appointmentDTO, form.getMaxPeoplePerAppointment( ) );
            }
            if ( appointmentDTO.getNbMaxPotentialBookedSeats() == 0 )
            {
                addError( ERROR_MESSAGE_SLOT_FULL, getLocale( request ) );
                return redirect( request, VIEW_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, nIdForm );
            }
            request.getSession( ).setAttribute( SESSION_NOT_VALIDATED_APPOINTMENT, appointmentDTO );
            request.getSession( ).setAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM, form );
        }
        else
        {
            // Modification of the Form only
            String strModificationForm = request.getParameter( PARAMETER_MODIFICATION_FORM );
            // Need to redirect for the anchor
            if ( StringUtils.isEmpty( strModificationForm ) )
            {
                LinkedHashMap<String, String> additionalParameters = new LinkedHashMap< >( );
                additionalParameters.put( PARAMETER_ID_FORM, strIdForm );
                additionalParameters.put( PARAMETER_MODIFICATION_FORM, String.valueOf( Boolean.TRUE ) );
                additionalParameters.put( PARAMETER_ANCHOR, MARK_ANCHOR + STEP_3 );
                return redirect( request, VIEW_APPOINTMENT_FORM, additionalParameters );
            }
        }
        Map<String, Object> model = getModel( );
        Locale locale = getLocale( request );
        StringBuilder strBuffer = new StringBuilder( );
        List<Entry> listEntryFirstLevel = EntryService.getFilter( form.getIdForm( ), true );
        for ( Entry entry : listEntryFirstLevel )
        {
            EntryService.getHtmlEntry( model, entry.getIdEntry( ), strBuffer, locale, true, request );
        }
        FormMessage formMessages = FormMessageService.findFormMessageByIdForm( nIdForm );
        List<GenericAttributeError> listErrors = (List<GenericAttributeError>) request.getSession( ).getAttribute( SESSION_APPOINTMENT_FORM_ERRORS );
        if ( listErrors != null )
        {
            model.put( MARK_FORM_ERRORS, listErrors );
            request.getSession( ).removeAttribute( SESSION_APPOINTMENT_FORM_ERRORS );
        }
        model.put( MARK_APPOINTMENT, appointmentDTO );
        model.put( PARAMETER_DATE_OF_DISPLAY, appointmentDTO.getSlot( ).getDate( ) );
        model.put( MARK_FORM, form );
        model.put( MARK_FORM_MESSAGES, formMessages );
        model.put( MARK_STR_ENTRY, strBuffer.toString( ) );
	model.put( MARK_LANGUAGE, locale.getLanguage( ) );
        model.put( MARK_LOCALE, locale );	
        model.put( MARK_PLACES, appointmentDTO.getNbMaxPotentialBookedSeats( ) );
        model.put( MARK_FORM_ERRORS, listErrors );
        model.put( MARK_LIST_ERRORS, AppointmentDTO.getAllErrors( locale ) );
        HtmlTemplate templateForm = AppTemplateService.getTemplate( TEMPLATE_HTML_CODE_FORM, locale, model );
        model.put( MARK_FORM_HTML, templateForm.getHtml( ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_APPOINTMENT_FORM, getLocale( request ), model );
        XPage xPage = new XPage( );
        xPage.setContent( template.getHtml( ) );
        xPage.setPathLabel( getDefaultPagePath( getLocale( request ) ) );
        if ( form.getDisplayTitleFo( ) )
        {
            xPage.setTitle( form.getTitle( ) );
        }
        return xPage;
    }

    /**
     * Do validate data entered by a user to fill a form
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     * @throws SiteMessageException
     * @throws UserNotSignedException
     */
    @Action( ACTION_DO_VALIDATE_FORM )
    public XPage doValidateForm( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        AppointmentFormDTO form = (AppointmentFormDTO) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        checkMyLuteceAuthentication( form, request );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        AppointmentDTO appointmentDTO = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_NOT_VALIDATED_APPOINTMENT );
        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
        Locale locale = request.getLocale( );
        String strEmail = request.getParameter( PARAMETER_EMAIL );
        String strFirstName = request.getParameter( PARAMETER_FIRST_NAME );
        String strLastName = request.getParameter( PARAMETER_LAST_NAME );
        AppointmentUtilities.checkDateOfTheAppointmentIsNotBeforeNow( appointmentDTO, locale, listFormErrors );
        AppointmentUtilities.checkEmail( strEmail, request.getParameter( PARAMETER_EMAIL_CONFIRMATION ), form, locale, listFormErrors );
        int nbBookedSeats = AppointmentUtilities.checkAndReturnNbBookedSeats( request.getParameter( PARAMETER_NUMBER_OF_BOOKED_SEATS ), form, appointmentDTO,
                locale, listFormErrors );
        AppointmentUtilities.fillAppointmentDTO( appointmentDTO, nbBookedSeats, strEmail, strFirstName, strLastName );
        AppointmentUtilities.validateFormAndEntries( appointmentDTO, request, listFormErrors );
        AppointmentUtilities.fillInListResponseWithMapResponse( appointmentDTO );
        boolean bErrors = false;
        if ( !AppointmentUtilities.checkNbDaysBetweenTwoAppointmentsTaken( appointmentDTO, strEmail, form ) )
        {
            addError( ERROR_MESSAGE_NB_MIN_DAYS_BETWEEN_TWO_APPOINTMENTS, locale );
            bErrors = true;
        }
        if ( form.getEnableMandatoryEmail( ) && !AppointmentUtilities.checkNbMaxAppointmentsOnAGivenPeriod( appointmentDTO, strEmail, form ) )
        {
            addError( ERROR_MESSAGE_NB_MAX_APPOINTMENTS_ON_A_PERIOD, locale );
            bErrors = true;
        }
        if ( CollectionUtils.isNotEmpty( listFormErrors ) )
        {
            request.getSession( ).setAttribute( SESSION_APPOINTMENT_FORM_ERRORS, listFormErrors );
            bErrors = true;
        }
        if ( bErrors )
        {
            LinkedHashMap<String, String> additionalParameters = new LinkedHashMap<String, String>( );
            additionalParameters.put( PARAMETER_ID_FORM, strIdForm );
            additionalParameters.put( PARAMETER_MODIFICATION_FORM, String.valueOf( Boolean.TRUE ) );
            additionalParameters.put( PARAMETER_ANCHOR, MARK_ANCHOR + STEP_3 );
            return redirect( request, VIEW_APPOINTMENT_FORM, additionalParameters );
        }
        request.getSession( ).removeAttribute( SESSION_NOT_VALIDATED_APPOINTMENT );
        request.getSession( ).setAttribute( SESSION_VALIDATED_APPOINTMENT, appointmentDTO );
        XPage xPage = null;
        String anchor = request.getParameter( PARAMETER_ANCHOR );
        if ( StringUtils.isNotEmpty( anchor ) )
        {
            Map<String, String> additionalParameters = new HashMap<>( );
            additionalParameters.put( PARAMETER_ANCHOR, MARK_ANCHOR + anchor );
            xPage = redirect( request, VIEW_DISPLAY_RECAP_APPOINTMENT, additionalParameters );
        }
        else
        {
            xPage = redirectView( request, VIEW_DISPLAY_RECAP_APPOINTMENT );
        }
        return xPage;
    }

    /**
     * Display the recap before validating an appointment
     * 
     * @param request
     *            The request
     * @return The HTML content to display or the next URL to redirect to
     * @throws UserNotSignedException
     */
    @View( VIEW_DISPLAY_RECAP_APPOINTMENT )
    public XPage displayRecapAppointment( HttpServletRequest request ) throws UserNotSignedException
    {
        AppointmentFormDTO form = (AppointmentFormDTO) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        checkMyLuteceAuthentication( form, request );
        String anchor = request.getParameter( PARAMETER_ANCHOR );
        if ( StringUtils.isNotEmpty( anchor ) )
        {
            Map<String, String> additionalParameters = new HashMap<>( );
            additionalParameters.put( PARAMETER_ANCHOR, MARK_ANCHOR + anchor );
            return redirect( request, VIEW_DISPLAY_RECAP_APPOINTMENT, additionalParameters );
        }
        AppointmentDTO appointment = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_VALIDATED_APPOINTMENT );
        if ( appointment == null )
        {
            return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
        }
        Map<String, Object> model = new HashMap<String, Object>( );
        if ( form.getEnableCaptcha( ) && getCaptchaService( ).isAvailable( ) )
        {
            model.put( MARK_CAPTCHA, getCaptchaService( ).getHtmlCode( ) );
        }
        model.put( MARK_FORM_MESSAGES, FormMessageService.findFormMessageByIdForm( appointment.getIdForm( ) ) );
        fillCommons( model );
        model.put( MARK_APPOINTMENT, appointment );
        Locale locale = getLocale( request );
        model.put( MARK_LIST_RESPONSE_RECAP_DTO, AppointmentUtilities.buildListResponse( appointment, request, locale ) );
        model.put( MARK_FORM, form );
        model.put( PARAMETER_DATE_OF_DISPLAY, appointment.getSlot( ).getDate( ) );
        XPage xPage = new XPage( );
        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_APPOINTMENT_FORM_RECAP, locale, model );
        xPage.setContent( t.getHtml( ) );
        xPage.setTitle( getDefaultPageTitle( locale ) );
        xPage.setPathLabel( getDefaultPagePath( locale ) );
        return xPage;
    }

    /**
     * Do save an appointment into the database if it is valid
     * 
     * @param request
     *            The request
     * @return The XPage to display
     * @throws UserNotSignedException
     */
    @Action( ACTION_DO_MAKE_APPOINTMENT )
    public  XPage doMakeAppointment( HttpServletRequest request ) throws UserNotSignedException
    {
        AppointmentFormDTO form = (AppointmentFormDTO) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        checkMyLuteceAuthentication( form, request );
        AppointmentDTO appointment = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_VALIDATED_APPOINTMENT );
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_BACK ) ) )
        {
            return redirect( request, VIEW_APPOINTMENT_FORM, PARAMETER_ID_FORM, appointment.getIdForm( ) );
        }
        if ( form.getEnableCaptcha( ) && getCaptchaService( ).isAvailable( )  && !getCaptchaService( ).validate( request ))
        {
                addError( ERROR_MESSAGE_CAPTCHA, getLocale( request ) );
                return redirect( request, VIEW_DISPLAY_RECAP_APPOINTMENT, PARAMETER_ID_FORM, appointment.getIdForm( ) );      
        }
        Slot slot = null;
        // Reload the slot from the database
        // The slot could have been taken since the beginning of the entry of
        // the form
        if ( appointment.getSlot( ).getIdSlot( ) != 0 )
        {
            slot = SlotService.findSlotById( appointment.getSlot( ).getIdSlot( ) );
        }
        else
        {
            HashMap<LocalDateTime, Slot> mapSlot = SlotService.buildMapSlotsByIdFormAndDateRangeWithDateForKey( appointment.getIdForm( ), appointment.getSlot( )
                    .getStartingDateTime( ), appointment.getSlot( ).getEndingDateTime( ) );
            if ( !mapSlot.isEmpty( ) )
            {
                slot = mapSlot.get( appointment.getSlot( ).getStartingDateTime( ) );
            }
            else
            {
                slot = appointment.getSlot( );
            }
        }
        appointment.setSlot( slot );
        if ( appointment.getNbBookedSeats( ) > slot.getNbRemainingPlaces( ) )
        {
            addInfo( ERROR_MESSAGE_SLOT_FULL, getLocale( request ) );
            return redirect( request, VIEW_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, appointment.getIdForm( ) );
        }
        
        int nIdAppointment;
		try {
			
			nIdAppointment = SlotSafeService.saveAppointment( appointment, request );

		} catch ( SlotFullException e ) {
			
			 addInfo( ERROR_MESSAGE_SLOT_FULL, getLocale( request ) );
	         return redirect( request, VIEW_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, appointment.getIdForm( ) );
		}catch( AppointmentSavedException e ){
			
			nIdAppointment = appointment.getIdAppointment( );
			AppLogService.error( "Error Save appointment: " + e.getMessage(), e );
		}
        AppLogService.info( LogUtilities.buildLog( ACTION_DO_MAKE_APPOINTMENT, Integer.toString( nIdAppointment ), null ) );
        request.getSession( ).removeAttribute( SESSION_VALIDATED_APPOINTMENT );
        AppointmentAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ) );
        XPage xPage = null;
        String anchor = request.getParameter( PARAMETER_ANCHOR );
        if ( StringUtils.isNotEmpty( anchor ) )
        {
            LinkedHashMap<String, String> additionalParameters = new LinkedHashMap< >( );
            additionalParameters.put( PARAMETER_ID_FORM, String.valueOf( appointment.getIdForm( ) ) );
            additionalParameters.put( PARAMETER_ID_APPOINTMENT, String.valueOf( nIdAppointment ) );
            additionalParameters.put( PARAMETER_ANCHOR, MARK_ANCHOR + anchor );
            xPage = redirect( request, VIEW_GET_APPOINTMENT_CREATED, additionalParameters );
        }
        else
        {
            xPage = redirect( request, VIEW_GET_APPOINTMENT_CREATED, PARAMETER_ID_FORM, appointment.getIdForm( ), PARAMETER_ID_APPOINTMENT, nIdAppointment );
        }
        return xPage;
    }

    /**
     * Get the page to notify the user that the appointment has been created
     * 
     * @param request
     *            The request
     * @return The XPage to display
     */
    @View( VIEW_GET_APPOINTMENT_CREATED )
    public XPage getAppointmentCreated( HttpServletRequest request )
    {
        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        int nIdAppointment = Integer.parseInt( request.getParameter( PARAMETER_ID_APPOINTMENT ) );
        AppLogService.debug( "n Id Appointment :" + nIdAppointment );
        Appointment appointment = AppointmentService.findAppointmentById( nIdAppointment );
        FormMessage formMessages = FormMessageService.findFormMessageByIdForm( nIdForm );
        Slot slot = SlotService.findSlotById( appointment.getIdSlot( ) );
        AppointmentFormDTO form = FormService.buildAppointmentForm( nIdForm, 0, 0 );
        String strTimeBegin = slot.getStartingDateTime( ).toLocalTime( ).toString( );
        String strTimeEnd = slot.getEndingDateTime( ).toLocalTime( ).toString( );
        String strReference = StringUtils.EMPTY;
        if ( !StringUtils.isEmpty( form.getReference( ) ) )
        {
            strReference = Strings.toUpperCase( form.getReference( ).trim( ) ) + " - ";
        }
        strReference += appointment.getReference( );
        formMessages.setTextAppointmentCreated( formMessages.getTextAppointmentCreated( ).replaceAll( MARK_REF, strReference )
                .replaceAll( MARK_DATE_APP, slot.getStartingDateTime( ).toLocalDate( ).format( Utilities.getDateFormatter( ) ) )
                .replaceAll( MARK_TIME_BEGIN, strTimeBegin ).replaceAll( MARK_TIME_END, strTimeEnd ) );
        Map<String, Object> model = new HashMap<String, Object>( );
        AppointmentDTO appointmentDTO = AppointmentService.buildAppointmentDTOFromIdAppointment( nIdAppointment );
        appointmentDTO.setListResponse( AppointmentResponseService.findAndBuildListResponse( nIdAppointment, request ) );
        appointmentDTO.setMapResponsesByIdEntry( AppointmentResponseService.buildMapFromListResponse( appointmentDTO.getListResponse( ) ) );
        model.put( MARK_LIST_RESPONSE_RECAP_DTO, AppointmentUtilities.buildListResponse( appointmentDTO, request, getLocale( request ) ) );
        model.put( MARK_DATE_APPOINTMENT, slot.getDate( ).format( Utilities.getDateFormatter( ) ) );
        model.put( MARK_STARTING_TIME_APPOINTMENT, slot.getStartingTime( ) );
        model.put( MARK_ENDING_TIME_APPOINTMENT, slot.getEndingTime( ) );
        model.put( MARK_USER, UserService.findUserById( appointment.getIdUser( ) ) );
        model.put( MARK_PLACES, appointment.getNbPlaces( ) );
        model.put( MARK_FORM, form );
        model.put( MARK_FORM_MESSAGES, formMessages );
        return getXPage( TEMPLATE_APPOINTMENT_CREATED, getLocale( request ), model );
    }

    /**
     * Get the view of all the forms on front office side
     * 
     * @param request
     *            the request
     * @return the xpage
     */
    @View( value = VIEW_APPOINTMENT_FORM_LIST, defaultView = true )
    public XPage getFormList( HttpServletRequest request )
    {
        Locale locale = getLocale( request );
        String strHtmlContent = getFormListHtml( request, locale );
        XPage xpage = new XPage( );
        xpage.setContent( strHtmlContent );
        xpage.setPathLabel( getDefaultPagePath( locale ) );
        xpage.setTitle( getDefaultPageTitle( locale ) );
        return xpage;
    }

    /**
     * Get the view for he user who wants to cancel its appointment
     * 
     * @param request
     * @return the view
     * @throws SiteMessageException
     */
    @View( VIEW_GET_VIEW_CANCEL_APPOINTMENT )
    public XPage getViewCancelAppointment( HttpServletRequest request ) throws SiteMessageException
    {
        String refAppointment = request.getParameter( PARAMETER_REF_APPOINTMENT );
        Appointment appointment = null;
        if ( StringUtils.isNotEmpty( refAppointment ) )
        {
            appointment = AppointmentService.findAppointmentByReference( refAppointment );
        }
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( PARAMETER_REF_APPOINTMENT, refAppointment );
        if ( appointment != null )
        {

            if ( appointment.getIsCancelled( ) )
            {
                model.put( MARK_APPOINTMENT_ALREADY_CANCELLED, Boolean.TRUE );
            }
            int nIdAppointment = appointment.getIdAppointment( );
            Slot slot = SlotService.findSlotById( appointment.getIdSlot( ) );
            // Check if the appointment is passed
            if ( slot.getStartingDateTime( ).isBefore( LocalDateTime.now( ) ) )
            {
                model.put( MARK_APPOINTMENT_PASSED, Boolean.TRUE );
            }
            model.put( MARK_DATE_APPOINTMENT, slot.getDate( ).format( Utilities.getDateFormatter( ) ) );
            model.put( MARK_STARTING_TIME_APPOINTMENT, slot.getStartingTime( ) );
            model.put( MARK_ENDING_TIME_APPOINTMENT, slot.getEndingTime( ) );
            model.put( MARK_PLACES, appointment.getNbPlaces( ) );
            model.put( MARK_FORM, FormService.buildAppointmentForm( slot.getIdForm( ), 0, 0 ) );
            model.put( MARK_FORM_MESSAGES, FormMessageService.findFormMessageByIdForm( slot.getIdForm( ) ) );
            AppointmentDTO appointmentDTO = AppointmentService.buildAppointmentDTOFromIdAppointment( nIdAppointment );
            appointmentDTO.setListResponse( AppointmentResponseService.findAndBuildListResponse( nIdAppointment, request ) );
            appointmentDTO.setMapResponsesByIdEntry( AppointmentResponseService.buildMapFromListResponse( appointmentDTO.getListResponse( ) ) );
            model.put( MARK_LIST_RESPONSE_RECAP_DTO, AppointmentUtilities.buildListResponse( appointmentDTO, request, getLocale( request ) ) );
            model.put( MARK_USER, UserService.findUserById( appointment.getIdUser( ) ) );

        }
        else
        {
            model.put( MARK_NO_APPOINTMENT_WITH_THIS_REFERENCE, Boolean.TRUE );
        }
        Locale locale = getLocale( request );
        XPage xpage = getXPage( TEMPLATE_CANCEL_APPOINTMENT, locale, model );
        xpage.setTitle( I18nService.getLocalizedString( MESSAGE_CANCEL_APPOINTMENT_PAGE_TITLE, locale ) );
        return xpage;
    }

    /**
     * Cancel an appointment
     * 
     * @param request
     * @return the confirmation view of the appointment cancelled
     * @throws SiteMessageException
     */
    @Action( ACTION_DO_CANCEL_APPOINTMENT )
    public XPage doCancelAppointment( HttpServletRequest request ) throws SiteMessageException
    {
        String strRef = request.getParameter( PARAMETER_REF_APPOINTMENT );
        if ( StringUtils.isNotEmpty( strRef ) )
        {
            Appointment appointment = AppointmentService.findAppointmentByReference( strRef );
            // Accept only one cancel !!!
            if ( appointment != null && !appointment.getIsCancelled( ) )
            {
                Slot slot = SlotService.findSlotById( appointment.getIdSlot( ) );
                // Check if the appointment is passed
                if ( !slot.getStartingDateTime( ).isBefore( LocalDateTime.now( ) ) )
                {
                    if ( appointment.getIdActionCancelled( ) > 0 )
                    {
                        boolean automaticUpdate = ( AdminUserService.getAdminUser( request ) == null ) ? true : false;
                        try
                        {
                            WorkflowService.getInstance( ).doProcessAction( appointment.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE,
                                    appointment.getIdActionCancelled( ), slot.getIdForm( ), request, request.getLocale( ), automaticUpdate );
                            AppointmentListenerManager.notifyAppointmentWFActionTriggered(appointment.getIdAppointment( ), appointment.getIdActionCancelled( ));
                        }
                        catch( Exception e )
                        {
                            AppLogService.error( "Error Workflow", e );
                        }
                    }
                    else
                    {
                        appointment.setIsCancelled( Boolean.TRUE );
                        AppointmentService.updateAppointment( appointment );
                        AppLogService.info( LogUtilities.buildLog( ACTION_DO_CANCEL_APPOINTMENT, Integer.toString( appointment.getIdAppointment( ) ), null ) );
                    }
                    Map<String, String> mapParameters = new HashMap<String, String>( );
                    if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FROM_MY_APPOINTMENTS ) ) )
                    {
                        String strReferer = request.getHeader( PARAMETER_REFERER );
                        if ( StringUtils.isNotEmpty( strReferer ) )
                        {
                            mapParameters.put( MARK_FROM_URL, strReferer );
                        }
                        mapParameters.put( PARAMETER_FROM_MY_APPOINTMENTS, request.getParameter( PARAMETER_FROM_MY_APPOINTMENTS ) );
                    }
                    mapParameters.put( PARAMETER_ID_FORM, Integer.toString( slot.getIdForm( ) ) );
                    return redirect( request, VIEW_APPOINTMENT_CANCELED, mapParameters );
                }
            }
        }
        return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
    }

    /**
     * Get the page to confirm that the appointment has been canceled
     * 
     * @param request
     *            The request
     * @return The XPage to display
     */
    @View( VIEW_APPOINTMENT_CANCELED )
    public XPage getAppointmentCanceled( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );
            Map<String, Object> model = new HashMap<String, Object>( );
            model.put( MARK_FORM_MESSAGES, FormMessageService.findFormMessageByIdForm( nIdForm ) );
            if ( Boolean.parseBoolean( request.getParameter( PARAMETER_FROM_MY_APPOINTMENTS ) ) )
            {
                String strFromUrl = request.getParameter( MARK_FROM_URL );
                model.put( MARK_BACK_URL, StringUtils.isNotEmpty( strFromUrl ) ? strFromUrl : getViewUrl( VIEW_GET_MY_APPOINTMENTS ) );
            }
            return getXPage( TEMPLATE_APPOINTMENT_CANCELED, getLocale( request ), model );
        }
        return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
    }

    /**
     * Get the page to view the appointments of a user
     * 
     * @param request
     *            The request
     * @return The XPage to display
     * @throws UserNotSignedException
     *             If the authentication is enabled and the user has not signed in
     */
    @View( VIEW_GET_MY_APPOINTMENTS )
    public XPage getMyAppointments( HttpServletRequest request ) throws UserNotSignedException
    {
        if ( !SecurityService.isAuthenticationEnable( ) )
        {
            return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
        }
        XPage xpage = new XPage( );
        Locale locale = getLocale( request );
        xpage.setContent( getMyAppointmentsXPage( request, locale ) );
        xpage.setTitle( I18nService.getLocalizedString( MESSAGE_MY_APPOINTMENTS_PAGE_TITLE, locale ) );
        return xpage;
    }

    /**
     * Get the HTML content of the my appointment page of a user
     * 
     * @param request
     *            The request
     * @param locale
     *            The locale
     * @return The HTML content, or null if the
     * @throws UserNotSignedException
     *             If the user has not signed in
     */
    public static String getMyAppointmentsXPage( HttpServletRequest request, Locale locale ) throws UserNotSignedException
    {
        if ( !SecurityService.isAuthenticationEnable( ) )
        {
            return null;
        }
        LuteceUser luteceUser = SecurityService.getInstance( ).getRegisteredUser( request );
        if ( luteceUser == null )
        {
            throw new UserNotSignedException( );
        }
        List<AppointmentDTO> listAppointmentDTO = new ArrayList<>( );
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_LIST_APPOINTMENTS, listAppointmentDTO );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MY_APPOINTMENTS, locale, model );
        return template.getHtml( );
    }

    /**
     * Get the html content of the list of forms
     * 
     * @param appointmentFormService
     *            The service to use
     * @param strTitle
     *            The title to display, or null to display the default title.
     * @param locale
     *            The locale
     * @return The HTML content to display
     */
    public static String getFormListHtml( HttpServletRequest request, Locale locale )
    {
        request.getSession( ).removeAttribute( SESSION_VALIDATED_APPOINTMENT );
        request.getSession( ).removeAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        Map<String, Object> model = new HashMap<String, Object>( );
        List<AppointmentFormDTO> listAppointmentForm = FormService.buildAllActiveAndDisplayedOnPortletAppointmentForm( );
        // We keep only the active
        if ( CollectionUtils.isNotEmpty( listAppointmentForm ) )
        {
            listAppointmentForm = listAppointmentForm
                    .stream( )
                    .filter(
                            a -> ( a.getDateStartValidity( ) != null )
                                    && ( a.getDateStartValidity( ).toLocalDate( ).isBefore( LocalDate.now( ) ) || a.getDateStartValidity( ).toLocalDate( )
                                            .equals( LocalDate.now( ) ) ) ).sorted( ( a1, a2 ) -> a1.getTitle( ).compareTo( a2.getTitle( ) ) )
                    .collect( Collectors.toList( ) );
        }
        List<String> icons = new ArrayList<String>( );
        for ( AppointmentFormDTO form : listAppointmentForm )
        {
            ImageResource img = form.getIcon( );
            if ( img == null || img.getImage( ) == null || StringUtils.isEmpty( img.getMimeType( ) ) || StringUtils.equals( img.getMimeType( ), MARK_ICON_NULL ) )
            {
                icons.add( MARK_ICON_NULL );
            }
            else
            {
                byte [ ] imgBytesAsBase64 = Base64.encodeBase64( img.getImage( ) );
                String imgDataAsBase64 = new String( imgBytesAsBase64 );
                String strMimeType = img.getMimeType( );
                String imgAsBase64 = MARK_DATA + MARK_COLON + strMimeType + MARK_SEMI_COLON + MARK_BASE_64 + MARK_COMMA + imgDataAsBase64;
                icons.add( imgAsBase64 );
            }
        }
        model.put( MARK_ICONS, icons );
        model.put( MARK_FORM_LIST, listAppointmentForm );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_APPOINTMENT_FORM_LIST, locale, model );
        return template.getHtml( );
    }

    /**
     * Get the captcha security service
     * 
     * @return The captcha security service
     */
    private CaptchaSecurityService getCaptchaService( )
    {
        if ( _captchaSecurityService == null )
        {
            _captchaSecurityService = new CaptchaSecurityService( );
        }

        return _captchaSecurityService;
    }

    /**
     * Get the URL
     * 
     * @param request
     *            Get the URL to cancel an appointment in FO
     * @param appointment
     *            The appointment
     * @return The URL to cancel the appointment
     */
    public static String getCancelAppointmentUrl( HttpServletRequest request, Appointment appointment )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getProdUrl( request ) + AppPathService.getPortalUrl( ) );
        urlItem.addParameter( MVCUtils.PARAMETER_PAGE, XPAGE_NAME );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_GET_VIEW_CANCEL_APPOINTMENT );
        urlItem.addParameter( PARAMETER_REF_APPOINTMENT, appointment.getReference( ) );
        return urlItem.getUrl( );
    }

    /**
     * Get the URL
     * 
     * @param request
     *            Get the URL to cancel an appointment in FO
     * @param appointment
     *            The appointment
     * @return The URL to cancel the appointment
     */
    public static String getCancelAppointmentUrl( Appointment appointment )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getProdUrl( StringUtils.EMPTY ) + AppPathService.getPortalUrl( ) );
        urlItem.addParameter( MVCUtils.PARAMETER_PAGE, XPAGE_NAME );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_GET_VIEW_CANCEL_APPOINTMENT );
        urlItem.addParameter( PARAMETER_REF_APPOINTMENT, appointment.getReference( ) );
        return urlItem.getUrl( );
    }

    /**
     * Set the user infos to the appointment DTO
     * 
     * @param request
     *            the request
     * @param appointment
     *            the appointment DTO
     */
    public void setUserInfo( HttpServletRequest request, AppointmentDTO appointment )
    {
        if ( SecurityService.isAuthenticationEnable( ) && ( appointment != null ) )
        {
            LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

            if ( user != null )
            {
                appointment.setGuid( user.getUserInfo( AppPropertiesService.getProperty( PROPERTY_USER_ATTRIBUTE_GUID, StringUtils.EMPTY ) ) );
                appointment.setFirstName( user.getUserInfo( AppPropertiesService.getProperty( PROPERTY_USER_ATTRIBUTE_FIRST_NAME, StringUtils.EMPTY ) ) );
                appointment.setEmail( user.getUserInfo( AppPropertiesService.getProperty( PROPERTY_USER_ATTRIBUTE_EMAIL, StringUtils.EMPTY ) ) );
                String lastName = user.getUserInfo( AppPropertiesService.getProperty( PROPERTY_USER_ATTRIBUTE_PREFERED_NAME, StringUtils.EMPTY ) );
                if ( ( lastName == null ) || lastName.isEmpty( ) )
                {
                    lastName = user.getUserInfo( AppPropertiesService.getProperty( PROPERTY_USER_ATTRIBUTE_LAST_NAME, StringUtils.EMPTY ) );
                }
                appointment.setLastName( lastName );
            }
        }
    }

    /**
     * check if authentication
     * 
     * @param form
     *            Form
     * @param request
     *            HttpServletRequest
     * @throws UserNotSignedException
     *             exception if the form requires an authentication and the user is not logged
     */
    private void checkMyLuteceAuthentication( AppointmentFormDTO form, HttpServletRequest request ) throws UserNotSignedException
    {
        // Try to register the user in case of external authentication
        if ( SecurityService.isAuthenticationEnable( ) )
        {
            if ( SecurityService.getInstance( ).isExternalAuthentication( ) )
            {
                // The authentication is external
                // Should register the user if it's not already done
                if ( SecurityService.getInstance( ).getRegisteredUser( request ) == null )
                {
                    if ( ( SecurityService.getInstance( ).getRemoteUser( request ) == null ) && ( form.getActiveAuthentication( ) ) )
                    {
                        // Authentication is required to access to the portal
                        throw new UserNotSignedException( );
                    }
                }
            }
            else
            {
                // If portal authentication is enabled and user is null and the
                // requested URL
                // is not the login URL, user cannot access to Portal
                if ( ( form.getActiveAuthentication( ) ) && ( SecurityService.getInstance( ).getRegisteredUser( request ) == null )
                        && !SecurityService.getInstance( ).isLoginUrl( request ) )
                {
                    // Authentication is required to access to the portal
                    throw new UserNotSignedException( );
                }
            }
        }
    }
}
