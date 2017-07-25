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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.Strings;

import fr.paris.lutece.plugins.appointment.business.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.calendar.CalendarTemplate;
import fr.paris.lutece.plugins.appointment.business.calendar.CalendarTemplateHome;
import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.message.FormMessage;
import fr.paris.lutece.plugins.appointment.business.message.FormMessageHome;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.rule.FormRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.AppointmentUtilities;
import fr.paris.lutece.plugins.appointment.service.DisplayService;
import fr.paris.lutece.plugins.appointment.service.EntryService;
import fr.paris.lutece.plugins.appointment.service.FormMessageService;
import fr.paris.lutece.plugins.appointment.service.FormRuleService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.ReservationRuleService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.Utilities;
import fr.paris.lutece.plugins.appointment.service.WeekDefinitionService;
import fr.paris.lutece.plugins.appointment.service.upload.AppointmentAsynchronousUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
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
    private static final String PARAMETER_MAX_CAPACITY = "max_capacity";
    private static final String PARAMETER_NB_WEEKS_TO_DISPLAY = "nb_weeks_to_display";
    private static final String PARAMETER_DATE_OF_DISPLAY = "date_of_display";
    private static final String PARAMETER_DAY_OF_WEEK = "dow";
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

    // Mark
    private static final String MARK_INFOS = "infos";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_FORM = "form";
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

    // Errors
    private static final String ERROR_MESSAGE_SLOT_FULL = "appointment.message.error.slotFull";
    private static final String ERROR_MESSAGE_CAPTCHA = "portal.admin.message.wrongCaptcha";
    private static final String ERROR_MESSAGE_CAN_NOT_CANCEL_APPOINTMENT = "appointment.message.error.canNotCancelAppointment";
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
        clearSession( request );
        AppointmentUtilities.killTimer( request );
        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        Map<String, Object> model = getModel( );
        Form form = FormService.findFormLightByPrimaryKey( nIdForm );
        boolean bError = false;
        if ( !form.isActive( ) )
        {
            addError( ERROR_MESSAGE_FORM_NOT_ACTIVE, getLocale( request ) );
            bError = true;
        }
        FormMessage formMessages = FormMessageHome.findByPrimaryKey( nIdForm );
        // Check if the date of display and the endDateOfDisplay are in the
        // validity date range of the form
        LocalDate startingValidityDate = form.getStartingValidityDate( );
        if ( startingValidityDate == null )
        {
            addError( ERROR_MESSAGE_NO_STARTING_VALIDITY_DATE, getLocale( request ) );
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
        LocalDate endingDateOfDisplay = startingDateOfDisplay.plusWeeks( nNbWeeksToDisplay );
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
                nNbWeeksToDisplay = Math.toIntExact( startingDateOfDisplay.until( endingDateOfDisplay, ChronoUnit.WEEKS ) );
            }
            if ( startingDateOfDisplay.isAfter( endingDateOfDisplay ) )
            {
                addError( ERROR_MESSAGE_FORM_NO_MORE_VALID, getLocale( request ) );
                bError = true;
            }
        }
        LocalDate firstDateOfFreeOpenSlot = SlotService.findFirstDateOfFreeOpenSlot( nIdForm, startingDateOfDisplay, endingDateOfDisplay );
        if ( firstDateOfFreeOpenSlot == null )
        {
            addError( ERROR_MESSAGE_NO_AVAILABLE_SLOT, getLocale( request ) );
            bError = true;
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
        List<WeekDefinition> listWeekDefinition = new ArrayList<WeekDefinition>( mapWeekDefinition.values( ) );
        // Get the min time of all the week definitions
        LocalTime minStartingTime = WeekDefinitionService.getMinStartingTimeOfAListOfWeekDefinition( listWeekDefinition );
        // Get the max time of all the week definitions
        LocalTime maxEndingTime = WeekDefinitionService.getMaxEndingTimeOfAListOfWeekDefinition( listWeekDefinition );
        // Get the min duration of an appointment of all the week definitions
        int nMinDuration = WeekDefinitionService.getMinDurationTimeSlotOfAListOfWeekDefinition( listWeekDefinition );
        // Get all the working days of all the week definitions
        List<String> listDayOfWeek = new ArrayList<>( WeekDefinitionService.getSetDayOfWeekOfAListOfWeekDefinition( listWeekDefinition ) );
        // Build the slots if no errors
        List<Slot> listSlot = new ArrayList<>( );
        if ( !bError )
        {
            listSlot = SlotService.buildListSlot( nIdForm, mapWeekDefinition, startingDateOfDisplay, nNbWeeksToDisplay );
            // Get the min time from now before a user can take an appointment
            // (in hours)
            FormRule formRule = FormRuleService.findFormRuleWithFormId( nIdForm );
            int minTimeBeforeAppointment = formRule.getMinTimeBeforeAppointment( );
            LocalDateTime dateTimeBeforeAppointment = LocalDateTime.now( ).plusHours( minTimeBeforeAppointment );
            // Filter the list of slots
            listSlot = listSlot.stream( ).filter( s -> s.getStartingDateTime( ).isAfter( dateTimeBeforeAppointment ) ).collect( Collectors.toList( ) );
        }
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
        model.put( PARAMETER_ID_FORM, nIdForm );
        model.put( MARK_FORM_MESSAGES, formMessages );
        model.put( PARAMETER_NB_WEEKS_TO_DISPLAY, nNbWeeksToDisplay );
        model.put( PARAMETER_DATE_OF_DISPLAY, dateOfDisplay );
        model.put( PARAMETER_DAY_OF_WEEK, listDayOfWeek );
        model.put( PARAMETER_EVENTS, listSlot );
        model.put( PARAMETER_MIN_TIME, minStartingTime );
        model.put( PARAMETER_MAX_TIME, maxEndingTime );
        model.put( PARAMETER_MIN_DURATION, LocalTime.MIN.plusMinutes( nMinDuration ) );

        Locale locale = getLocale( request );
        CalendarTemplate calendarTemplate = CalendarTemplateHome.findByPrimaryKey( display.getIdCalendarTemplate( ) );
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
    public XPage getViewAppointmentForm( HttpServletRequest request ) throws UserNotSignedException
    {
        AppointmentForm form = (AppointmentForm) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = Integer.parseInt( strIdForm );
        if ( form == null )
        {
            form = FormService.buildAppointmentForm( nIdForm, 0, 0 );
        }
        checkMyLuteceAuthentication( form, request );
        // Get the not validated appointment in session if it exists
        AppointmentDTO appointmentDTO = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_NOT_VALIDATED_APPOINTMENT );
        if ( appointmentDTO == null )
        {
            // Try to get the validated appointment in session
            // (in case the user click on back button in the recap view
            appointmentDTO = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_VALIDATED_APPOINTMENT );
            if ( appointmentDTO != null )
            {
                request.getSession( ).removeAttribute( SESSION_VALIDATED_APPOINTMENT );
                request.getSession( ).setAttribute( SESSION_NOT_VALIDATED_APPOINTMENT, appointmentDTO );
            }
            else
            {
                appointmentDTO = new AppointmentDTO( );
                int nIdSlot = Integer.parseInt( request.getParameter( PARAMETER_ID_SLOT ) );
                Slot slot = null;
                // If nIdSlot == 0, the slot has not been created yet
                if ( nIdSlot == 0 )
                {
                    // Need to get all the informations to create the slot
                    LocalDateTime startingDateTime = LocalDateTime.parse( request.getParameter( PARAMETER_STARTING_DATE_TIME ) );
                    LocalDateTime endingDateTime = LocalDateTime.parse( request.getParameter( PARAMETER_ENDING_DATE_TIME ) );
                    // Need to check if the slot has not been already created
                    HashMap<LocalDateTime, Slot> slotInDbMap = SlotService.findSlotsByIdFormAndDateRange( nIdForm, startingDateTime, endingDateTime );
                    if ( !slotInDbMap.isEmpty( ) )
                    {
                        slot = slotInDbMap.get( startingDateTime );
                    }
                    else
                    {
                        boolean bIsOpen = Boolean.parseBoolean( request.getParameter( PARAMETER_IS_OPEN ) );
                        int nMaxCapacity = Integer.parseInt( request.getParameter( PARAMETER_MAX_CAPACITY ) );
                        slot = SlotService.buildSlot( nIdForm, startingDateTime, endingDateTime, nMaxCapacity, nMaxCapacity, nMaxCapacity, bIsOpen );
                        slot = SlotService.saveSlot( slot );
                    }
                }
                else
                {
                    slot = SlotService.findSlotById( nIdSlot );
                }
                appointmentDTO.setSlot( slot );
                appointmentDTO.setDateOfTheAppointment( slot.getDate( ).format( Utilities.getFormatter( ) ) );
                appointmentDTO.setIdForm( nIdForm );
                LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
                if ( user != null )
                {
                    AppLogService.info( "user email :" + user.getUserInfo( "user.business-info.online.email" ) );
                    AppLogService.info( "user first name :" + user.getUserInfo( "user.name.given" ) );
                    AppLogService.info( "user last name :" + user.getUserInfo( "user.name.family" ) );
                    appointmentDTO.setEmail( user.getUserInfo( "user.business-info.online.email" ) );
                    appointmentDTO.setFirstName( user.getUserInfo( "user.name.given" ) );
                    appointmentDTO.setLastName( user.getUserInfo( "user.name.family" ) );
                }
                request.getSession( ).setAttribute( SESSION_NOT_VALIDATED_APPOINTMENT, appointmentDTO );
                ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( nIdForm, slot.getDate( ) );
                WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( nIdForm, slot.getDate( ) );
                form = FormService.buildAppointmentForm( nIdForm, reservationRule.getIdReservationRule( ), weekDefinition.getIdWeekDefinition( ) );
                request.getSession( ).setAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM, form );
                AppointmentUtilities.putTimerInSession( request, slot, appointmentDTO, form.getMaxPeoplePerAppointment( ) );
            }
        }
        Map<String, Object> model = getModel( );
        Locale locale = getLocale( request );
        StringBuffer strBuffer = new StringBuffer( );
        List<Entry> listEntryFirstLevel = EntryService.getFilter( form.getIdForm( ), true );
        for ( Entry entry : listEntryFirstLevel )
        {
            EntryService.getHtmlEntry( model, entry.getIdEntry( ), strBuffer, locale, true, request );
        }
        FormMessage formMessages = FormMessageService.findFormMessageByIdForm( nIdForm );
        List<GenericAttributeError> listErrors = (List<GenericAttributeError>) request.getSession( ).getAttribute( SESSION_APPOINTMENT_FORM_ERRORS );
        HtmlTemplate templateForm = AppTemplateService.getTemplate( TEMPLATE_HTML_CODE_FORM, locale, model );
        model.put( MARK_APPOINTMENT, appointmentDTO );
        model.put( PARAMETER_DATE_OF_DISPLAY, appointmentDTO.getSlot( ).getDate( ) );
        model.put( MARK_FORM, form );
        model.put( MARK_FORM_MESSAGES, formMessages );
        model.put( MARK_STR_ENTRY, strBuffer.toString( ) );
        model.put( MARK_LOCALE, locale );
        model.put( MARK_PLACES, appointmentDTO.getNbMaxPotentialBookedSeats( ) );        
        model.put( MARK_FORM_ERRORS, listErrors );
        model.put( MARK_LIST_ERRORS, AppointmentDTO.getAllErrors( locale ) );        
        model.put( MARK_FORM_HTML, templateForm.getHtml( ) );
        if ( listErrors != null )
        {
            model.put( MARK_FORM_ERRORS, listErrors );
            request.getSession( ).removeAttribute( SESSION_APPOINTMENT_FORM_ERRORS );
        }
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_APPOINTMENT_FORM, getLocale( request ), model );
        XPage page = new XPage( );
        page.setContent( template.getHtml( ) );
        page.setPathLabel( getDefaultPagePath( getLocale( request ) ) );
        if ( form.getDisplayTitleFo( ) )
        {
            page.setTitle( form.getTitle( ) );
        }
        return page;
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
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        AppointmentDTO appointmentDTO = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_NOT_VALIDATED_APPOINTMENT );
        AppointmentForm form = (AppointmentForm) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        checkMyLuteceAuthentication( form, request );
        int nIdForm = Integer.parseInt( strIdForm );
        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
        Locale locale = request.getLocale( );
        String strEmail = request.getParameter( PARAMETER_EMAIL );
        AppointmentUtilities.checkEmail( strEmail, request.getParameter( PARAMETER_EMAIL_CONFIRMATION ), form, locale, listFormErrors );
        int nbBookedSeats = AppointmentUtilities.checkAndReturnNbBookedSeats( request.getParameter( PARAMETER_NUMBER_OF_BOOKED_SEATS ), form, appointmentDTO,
                locale, listFormErrors );
        AppointmentUtilities.fillAppointmentDTO( appointmentDTO, nbBookedSeats, strEmail, request.getParameter( PARAMETER_FIRST_NAME ),
                request.getParameter( PARAMETER_LAST_NAME ) );
        AppointmentUtilities.validateFormAndEntries( appointmentDTO, request, listFormErrors );
        AppointmentUtilities.fillInListResponseWithMapResponse( appointmentDTO );
        if ( CollectionUtils.isNotEmpty( listFormErrors ) )
        {
            request.getSession( ).setAttribute( SESSION_APPOINTMENT_FORM_ERRORS, listFormErrors );
            return redirect( request, VIEW_APPOINTMENT_FORM, PARAMETER_ID_FORM, nIdForm, PARAMETER_ID_SLOT, appointmentDTO.getSlot( ).getIdSlot( ) );
        }
        if ( !AppointmentUtilities.checkNbDaysBetweenTwoAppointments( appointmentDTO, strEmail, form ) )
        {
            addError( ERROR_MESSAGE_NB_MIN_DAYS_BETWEEN_TWO_APPOINTMENTS, locale );
            return redirect( request, VIEW_APPOINTMENT_FORM, PARAMETER_ID_FORM, nIdForm, PARAMETER_ID_SLOT, appointmentDTO.getSlot( ).getIdSlot( ) );
        }
        if ( !AppointmentUtilities.checkNbMaxAppointmentsOnAGivenPeriod( appointmentDTO, strEmail, form ) )
        {
            addError( ERROR_MESSAGE_NB_MAX_APPOINTMENTS_ON_A_PERIOD, locale );
            return redirect( request, VIEW_APPOINTMENT_FORM, PARAMETER_ID_FORM, nIdForm, PARAMETER_ID_SLOT, appointmentDTO.getSlot( ).getIdSlot( ) );
        }
        request.getSession( ).removeAttribute( SESSION_NOT_VALIDATED_APPOINTMENT );
        request.getSession( ).setAttribute( SESSION_VALIDATED_APPOINTMENT, appointmentDTO );
        return redirectView( request, VIEW_DISPLAY_RECAP_APPOINTMENT );

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
        AppointmentDTO appointment = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_VALIDATED_APPOINTMENT );
        if ( appointment == null )
        {
            return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
        }
        AppointmentForm form = (AppointmentForm) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
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
        return getXPage( TEMPLATE_APPOINTMENT_FORM_RECAP, locale, model );
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
    public XPage doMakeAppointment( HttpServletRequest request ) throws UserNotSignedException
    {
        AppointmentDTO appointment = (AppointmentDTO) request.getSession( ).getAttribute( SESSION_VALIDATED_APPOINTMENT );
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_BACK ) ) )
        {
            return redirect( request, VIEW_APPOINTMENT_FORM, PARAMETER_ID_FORM, appointment.getIdForm( ) );
        }
        AppointmentForm form = (AppointmentForm) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        checkMyLuteceAuthentication( form, request );
        if ( form.getEnableCaptcha( ) && getCaptchaService( ).isAvailable( ) )
        {
            if ( !getCaptchaService( ).validate( request ) )
            {
                addError( ERROR_MESSAGE_CAPTCHA, getLocale( request ) );
                return redirect( request, VIEW_DISPLAY_RECAP_APPOINTMENT, PARAMETER_ID_FORM, appointment.getIdForm( ) );
            }
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
            HashMap<LocalDateTime, Slot> mapSlot = SlotService.findSlotsByIdFormAndDateRange( appointment.getIdForm( ), appointment.getSlot( )
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
            addError( ERROR_MESSAGE_SLOT_FULL, getLocale( request ) );
            return redirect( request, VIEW_APPOINTMENT_CALENDAR, PARAMETER_ID_FORM, appointment.getIdForm( ) );
        }
        int nIdAppointment = AppointmentService.saveAppointment( appointment );
        request.getSession( ).removeAttribute( AppointmentUtilities.SESSION_SLOT_EDIT_TASK );
        AppointmentUtilities.killTimer( request );
        request.getSession( ).removeAttribute( SESSION_VALIDATED_APPOINTMENT );
        AppointmentAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );
        return redirect( request, VIEW_GET_APPOINTMENT_CREATED, PARAMETER_ID_FORM, appointment.getIdForm( ), PARAMETER_ID_APPOINTMENT, nIdAppointment );
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
        Appointment appointment = AppointmentService.findAppointmentById( nIdAppointment );
        FormMessage formMessages = FormMessageHome.findByPrimaryKey( nIdForm );
        Slot slot = SlotService.findSlotById( appointment.getIdSlot( ) );
        Form form = FormService.findFormLightByPrimaryKey( nIdForm );
        String strTimeBegin = slot.getStartingDateTime( ).toLocalTime( ).toString( );
        String strTimeEnd = slot.getEndingDateTime( ).toLocalTime( ).toString( );
        String strReference = StringUtils.EMPTY;
        if ( !StringUtils.isEmpty( form.getReference( ) ) )
        {
            strReference = Strings.toUpperCase( form.getReference( ).trim( ) ) + " - ";
        }
        strReference += appointment.getReference( );
        formMessages.setTextAppointmentCreated( formMessages.getTextAppointmentCreated( ).replaceAll( MARK_REF, strReference )
                .replaceAll( MARK_DATE_APP, slot.getStartingDateTime( ).toLocalDate( ).format( Utilities.getFormatter( ) ) )
                .replaceAll( MARK_TIME_BEGIN, strTimeBegin ).replaceAll( MARK_TIME_END, strTimeEnd ) );
        Map<String, Object> model = new HashMap<String, Object>( );
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
            Slot slot = SlotService.findSlotById( appointment.getIdSlot( ) );
            model.put( MARK_DATE_APPOINTMENT, slot.getDate( ).format( Utilities.getFormatter( ) ) );
            model.put( MARK_STARTING_TIME_APPOINTMENT, slot.getStartingTime( ) );
            model.put( MARK_ENDING_TIME_APPOINTMENT, slot.getEndingTime( ) );
        }
        else
        {
            SiteMessageService.setMessage( request, ERROR_MESSAGE_CAN_NOT_CANCEL_APPOINTMENT, SiteMessage.TYPE_STOP );
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
            Slot slot = SlotService.findSlotById( appointment.getIdSlot( ) );
            if ( appointment.getIdActionCancelled( ) > 0 )
            {
                boolean automaticUpdate = ( AdminUserService.getAdminUser( request ) == null ) ? true : false;
                WorkflowService.getInstance( ).doProcessAction( appointment.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE,
                        appointment.getIdActionCancelled( ), slot.getIdForm( ), request, request.getLocale( ), automaticUpdate );
            }
            else
            {
                appointment.setIsCancelled( Boolean.TRUE );
                AppointmentService.updateAppointment( appointment );
            }
            slot.setNbRemainingPlaces( slot.getNbRemainingPlaces( ) + appointment.getNbPlaces( ) );
            slot.setNbPotentialRemainingPlaces( slot.getNbPotentialRemainingPlaces( ) + appointment.getNbPlaces( ) );
            SlotService.updateSlot( slot );
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
            model.put( MARK_FORM_MESSAGES, FormMessageHome.findByPrimaryKey( nIdForm ) );
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
        Map<String, Object> model = new HashMap<String, Object>( );
        List<AppointmentForm> listAppointmentForm = FormService.buildAllActiveAppointmentForm( );
        // We keep only the active
        if ( CollectionUtils.isNotEmpty( listAppointmentForm ) )
        {
            listAppointmentForm = listAppointmentForm
                    .stream( )
                    .filter(
                            a -> a.getDateStartValidity( ).toLocalDate( ).isBefore( LocalDate.now( ) )
                                    || a.getDateStartValidity( ).toLocalDate( ).equals( LocalDate.now( ) ) )
                    .sorted( ( a1, a2 ) -> a1.getTitle( ).compareTo( a2.getTitle( ) ) ).collect( Collectors.toList( ) );
        }
        List<String> icons = new ArrayList<String>( );
        for ( AppointmentForm form : listAppointmentForm )
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
     * Clear the user's session
     * 
     * @param request
     *            the request
     */
    private void clearSession( HttpServletRequest request )
    {
        request.getSession( ).removeAttribute( SESSION_APPOINTMENT_FORM_ERRORS );
        request.getSession( ).removeAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        request.getSession( ).removeAttribute( SESSION_NOT_VALIDATED_APPOINTMENT );
        request.getSession( ).removeAttribute( SESSION_VALIDATED_APPOINTMENT );
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
     * check if authentication
     * 
     * @param form
     *            Form
     * @param request
     *            HttpServletRequest
     * @throws UserNotSignedException
     *             exception if the form requires an authentication and the user is not logged
     */
    private void checkMyLuteceAuthentication( AppointmentForm form, HttpServletRequest request ) throws UserNotSignedException
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
