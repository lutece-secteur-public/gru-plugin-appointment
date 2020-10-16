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
package fr.paris.lutece.plugins.appointment.service;

import static java.lang.Math.toIntExact;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.service.lock.SlotEditTask;
import fr.paris.lutece.plugins.appointment.service.lock.TimerForLockOnSlot;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFilterDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.appointment.web.dto.ResponseRecapDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.beanvalidation.BeanValidationUtil;

/**
 * Utility class for Appointment Mutualize methods between MVCApplication and MVCAdminJspBean
 * 
 * @author Laurent Payen
 *
 */
public final class AppointmentUtilities
{

    private static final String ERROR_MESSAGE_EMPTY_CONFIRM_EMAIL = "appointment.validation.appointment.EmailConfirmation.email";
    private static final String ERROR_MESSAGE_CONFIRM_EMAIL = "appointment.message.error.confirmEmail";
    private static final String ERROR_MESSAGE_DATE_APPOINTMENT = "appointment.message.error.dateAppointment";
    private static final String ERROR_MESSAGE_EMPTY_EMAIL = "appointment.validation.appointment.Email.notEmpty";
    private static final String ERROR_MESSAGE_EMPTY_NB_BOOKED_SEAT = "appointment.validation.appointment.NbBookedSeat.notEmpty";
    private static final String ERROR_MESSAGE_FORMAT_NB_BOOKED_SEAT = "appointment.validation.appointment.NbBookedSeat.notNumberFormat";
    private static final String ERROR_MESSAGE_ERROR_NB_BOOKED_SEAT = "appointment.validation.appointment.NbBookedSeat.error";

    private static final String KEY_RESOURCE_TYPE = "appointment.appointment.name";
    private static final String KEY_COLUMN_LAST_NAME = "appointment.manageAppointments.columnLastName";
    private static final String KEY_COLUMN_FISRT_NAME = "appointment.manageAppointments.columnFirstName";
    private static final String KEY_COLUMN_EMAIL = "appointment.manageAppointments.columnEmail";
    private static final String KEY_COLUMN_DATE_APPOINTMENT = "appointment.dateAppointment.title";
    private static final String KEY_TIME_START = "appointment.model.entity.appointmentform.attribute.timeStart";
    private static final String KEY_TIME_END = "appointment.model.entity.appointmentform.attribute.timeEnd";
    private static final String KEY_COLUMN_ADMIN = "appointment.manageAppointments.columnAdmin";
    private static final String KEY_COLUMN_STATUS = "appointment.labelStatus";
    private static final String KEY_COLUMN_STATE = "appointment.manageAppointments.columnState";
    private static final String KEY_COLUMN_NB_BOOKED_SEATS = "appointment.manageAppointments.columnNumberOfBookedseatsPerAppointment";

    private static final String CONSTANT_COMMA = ",";
    private static final String EXCEL_FILE_EXTENSION = ".xlsx";
    private static final String EXCEL_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static final String SESSION_TIMER_SLOT = "appointment.session.timer.slot";

    public static final String PROPERTY_DEFAULT_EXPIRED_TIME_EDIT_APPOINTMENT = "appointment.edit.expired.time";

    public static final int THIRTY_MINUTES = 30;

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private AppointmentUtilities( )
    {
    }

    /**
     * Check that the email is correct and matches the confirm email
     * 
     * @param strEmail
     *            the email
     * @param strConfirmEmail
     *            the confirm email
     * @param form
     *            the form
     * @param locale
     *            the local
     * @param listFormErrors
     *            the list of errors that can be fill in with the errors found for the email
     */
    public static void checkEmail( String strEmail, String strConfirmEmail, AppointmentFormDTO form, Locale locale, List<GenericAttributeError> listFormErrors )
    {
        if ( form.getEnableMandatoryEmail( ) )
        {
            if ( StringUtils.isEmpty( strEmail ) )
            {
                GenericAttributeError genAttError = new GenericAttributeError( );
                genAttError.setErrorMessage( I18nService.getLocalizedString( ERROR_MESSAGE_EMPTY_EMAIL, locale ) );
                listFormErrors.add( genAttError );
            }
            if ( StringUtils.isEmpty( strConfirmEmail ) )
            {
                GenericAttributeError genAttError = new GenericAttributeError( );
                genAttError.setErrorMessage( I18nService.getLocalizedString( ERROR_MESSAGE_EMPTY_CONFIRM_EMAIL, locale ) );
                listFormErrors.add( genAttError );
            }
        }
        if ( !StringUtils.equals( strEmail, strConfirmEmail ) )
        {
            GenericAttributeError genAttError = new GenericAttributeError( );
            genAttError.setErrorMessage( I18nService.getLocalizedString( ERROR_MESSAGE_CONFIRM_EMAIL, locale ) );
            listFormErrors.add( genAttError );
        }
    }

    /**
     * Check that the date of the appointment we try to take is not in the past
     * 
     * @param appointmentDTO
     *            the appointment
     * @param locale
     *            the local
     * @param listFormErrors
     *            the list of errors that can be fill in with the error found with the date
     */
    public static void checkDateOfTheAppointmentIsNotBeforeNow( AppointmentDTO appointmentDTO, Locale locale, List<GenericAttributeError> listFormErrors )
    {
        if ( appointmentDTO.getSlot( ).getStartingDateTime( ).toLocalDate( ).isBefore( LocalDate.now( ) ) )
        {
            GenericAttributeError genAttError = new GenericAttributeError( );
            genAttError.setErrorMessage( I18nService.getLocalizedString( ERROR_MESSAGE_DATE_APPOINTMENT, locale ) );
            listFormErrors.add( genAttError );
        }
    }

    /**
     * Check that the delay between two appointments for the same use has been respected
     * 
     * @param appointmentDTO
     *            the appointment
     * @param strEmail
     *            the email
     * @param form
     *            the form
     * @return false if the delay is not respected
     */
    public static boolean checkNbDaysBetweenTwoAppointments( AppointmentDTO appointmentDTO, String strFirstName, String strLastName, String strEmail,
            AppointmentFormDTO form )
    {
        boolean bCheckPassed = true;
        int nbDaysBetweenTwoAppointments = form.getNbDaysBeforeNewAppointment( );
        if ( nbDaysBetweenTwoAppointments != 0 )
        {
            List<Slot> listSlots = getSlotsByEmail( strEmail, appointmentDTO.getIdAppointment( ) );
            if ( CollectionUtils.isNotEmpty( listSlots ) )
            {
                // Get the last appointment date for this form
                listSlots = listSlots.stream( ).filter( s -> s.getIdForm( ) == form.getIdForm( ) ).collect( Collectors.toList( ) );
                if ( CollectionUtils.isNotEmpty( listSlots ) )
                {
                    LocalDate dateOfTheLastAppointment = listSlots.stream( ).map( Slot::getStartingDateTime ).max( LocalDateTime::compareTo ).get( )
                            .toLocalDate( );
                    // Check the number of days between this appointment and
                    // the last appointment the user has taken
                    LocalDate dateOfTheAppointment = appointmentDTO.getSlot( ).getStartingDateTime( ).toLocalDate( );
                    if ( Math.abs( dateOfTheLastAppointment.until( dateOfTheAppointment, ChronoUnit.DAYS ) ) <= nbDaysBetweenTwoAppointments )
                    {
                        bCheckPassed = false;
                    }
                }
            }
        }
        return bCheckPassed;
    }
    
    /**
     * Check that the delay between two appointments for the same use has been respected
     * 
     * @param appointmentDTO
     *            the appointment
     * @param strEmail
     *            the email
     * @param form
     *            the form
     * @return false if the delay is not respected
     */
    public static boolean checkNbDaysBetweenTwoAppointmentsTaken( AppointmentDTO appointmentDTO, String strEmail,
            AppointmentFormDTO form )
    {
        boolean bCheckPassed = true;
        int nbDaysBetweenTwoAppointments = form.getNbDaysBeforeNewAppointment( );
        if ( nbDaysBetweenTwoAppointments != 0 )
        {
        	AppointmentFilterDTO filter= new AppointmentFilterDTO();
        	filter.setEmail(strEmail);
        	filter.setStatus( 0 );
        	filter.setIdForm(form.getIdForm());
        	List<Appointment> listAppointment = AppointmentService.findListAppointmentsByFilter(filter);
        	 // If we modify an appointment, we remove the
            // appointment that we currently edit
            if ( appointmentDTO.getIdAppointment( ) != 0 )
            {
                listAppointment = listAppointment.stream( ).filter( a -> a.getIdAppointment( ) != appointmentDTO.getIdAppointment( ) ).collect( Collectors.toList( ) );
            }

            if ( CollectionUtils.isNotEmpty( listAppointment ) )
            {

            	LocalDateTime dateOfTheLastAppointmentTaken = listAppointment.stream( ).map( Appointment:: getDateAppointmentTaken ).max( LocalDateTime::compareTo ).get( );
               
                    if ( Math.abs( dateOfTheLastAppointmentTaken.until( LocalDateTime.now( ), ChronoUnit.DAYS ) ) <= nbDaysBetweenTwoAppointments )
                    {
                        bCheckPassed = false;
                    }
               
            }
        }
        return bCheckPassed;
    }

    /**
     * Get the appointment of a user appointment
     * 
     * @param strEmail
     *            the user's email
     * @param idAppointment
     *            the id of the appointment
     * @return the list of appointment
     */
    private static List<Appointment> getAppointmentByEmail( String strEmail, int idAppointment )
    {
        List<Appointment> listAppointment = new ArrayList<>( );
        if ( StringUtils.isNotEmpty( strEmail ) )
        {
            // Looking for existing users with this email
            List<User> listUsers = UserService.findUsersByEmail( strEmail );
            if ( listUsers != null )
            {
                // For each User
                for ( User user : listUsers )
                {
                    // looking for its appointment
                    listAppointment.addAll( AppointmentService.findListAppointmentByUserId( user.getIdUser( ) ) );
                }

                // If we modify an appointment, we remove the
                // appointment that we currently edit
                if ( idAppointment != 0 )
                {
                    listAppointment = listAppointment.stream( ).filter( a -> a.getIdAppointment( ) != idAppointment ).collect( Collectors.toList( ) );
                }
                
            }
            }
        return listAppointment;
    }
    
    /**
     * Get the slot of a user appointment
     * 
     * @param strEmail
     *            the user's email
     * @param idAppointment
     *            the id of the appointment
     * @return the list of slots
     */
    private static List<Slot> getSlotsByEmail( String strEmail, int idAppointment )
    {
        List<Slot> listSlots = new ArrayList<>( );
        if ( StringUtils.isNotEmpty( strEmail ) )
        {
        	    List<Appointment> listAppointment = getAppointmentByEmail( strEmail, idAppointment );
                if ( CollectionUtils.isNotEmpty( listAppointment ) )
                {
                    // I know we could have a join sql query, but I don't
                    // want to join the appointment table with the slot
                    // table, it's too big and not efficient

                    for ( Appointment appointment : listAppointment )
                    {
                        if ( !appointment.getIsCancelled( ) )
                        {
                            listSlots.add( SlotService.findSlotById( appointment.getIdSlot( ) ) );
                        }
                    }

                }
            
        }
        return listSlots;
    }

    /**
     * Check that the number of appointments on a defined period is not above the maximum authorized
     * 
     * @param appointmentDTO
     *            the appointment
     * @param strEmail
     *            the email of the user
     * @param form
     *            the form
     * @return false if the number of appointments is above the maximum authorized on the defined period
     */
    public static boolean checkNbMaxAppointmentsOnAGivenPeriod( AppointmentDTO appointmentDTO, String strEmail, AppointmentFormDTO form )
    {
        boolean bCheckPassed = true;
        int nbMaxAppointmentsPerUser = form.getNbMaxAppointmentsPerUser( );
        int nbDaysForMaxAppointmentsPerUser = form.getNbDaysForMaxAppointmentsPerUser( );
        if ( nbMaxAppointmentsPerUser != 0 )
        {
            List<Slot> listSlots = getSlotsByEmail( strEmail, appointmentDTO.getIdAppointment( ) );
            if ( CollectionUtils.isNotEmpty( listSlots ) )
            {
                // Filter fot the good form
                listSlots = listSlots.stream( ).filter( s -> s.getIdForm( ) == form.getIdForm( ) ).collect( Collectors.toList( ) );
                if ( CollectionUtils.isNotEmpty( listSlots ) )
                {
                    // Get the date of the future appointment
                    LocalDate dateOfTheAppointment = appointmentDTO.getSlot( ).getStartingDateTime( ).toLocalDate( );
                    // Min starting date of the period
                    LocalDate minStartingDateOfThePeriod = dateOfTheAppointment.minusDays( nbDaysForMaxAppointmentsPerUser );
                    // Max ending date of the period
                    LocalDate maxEndingDateOfThePeriod = dateOfTheAppointment.plusDays( nbDaysForMaxAppointmentsPerUser );
                    // Keep only the slots that are in the min-max period
                    listSlots = listSlots
                            .stream( )
                            .filter(
                                    s -> s.getStartingDateTime( ).toLocalDate( ).isEqual( minStartingDateOfThePeriod )
                                            || s.getStartingDateTime( ).toLocalDate( ).isAfter( minStartingDateOfThePeriod ) )
                            .filter(
                                    s -> s.getStartingDateTime( ).toLocalDate( ).isEqual( maxEndingDateOfThePeriod )
                                            || s.getStartingDateTime( ).toLocalDate( ).isBefore( maxEndingDateOfThePeriod ) ).collect( Collectors.toList( ) );
                    LocalDate startingDateOfThePeriod = null;
                    LocalDate endingDateOfThePeriod = null;
                    // For each slot
                    for ( Slot slot : listSlots )
                    {
                        if ( slot.getStartingDateTime( ).toLocalDate( ).isBefore( dateOfTheAppointment ) )
                        {
                            startingDateOfThePeriod = slot.getStartingDateTime( ).toLocalDate( );
                            endingDateOfThePeriod = startingDateOfThePeriod.plusDays( nbDaysForMaxAppointmentsPerUser );
                        }
                        if ( slot.getStartingDateTime( ).toLocalDate( ).isAfter( dateOfTheAppointment ) )
                        {
                            endingDateOfThePeriod = slot.getStartingDateTime( ).toLocalDate( );
                            startingDateOfThePeriod = endingDateOfThePeriod.minusDays( nbDaysForMaxAppointmentsPerUser );
                        }
                        if ( slot.getStartingDateTime( ).toLocalDate( ).isEqual( dateOfTheAppointment ) )
                        {
                            startingDateOfThePeriod = endingDateOfThePeriod = slot.getStartingDateTime( ).toLocalDate( );
                        }
                        // Check the number of slots on the period
                        final LocalDate startingDateOfPeriodToSearch = startingDateOfThePeriod;
                        final LocalDate endingDateOfPeriodToSearch = endingDateOfThePeriod;
                        int nbSlots = toIntExact( listSlots
                                .stream( )
                                .filter(
                                        s -> ( s.getStartingDateTime( ).toLocalDate( ).equals( startingDateOfPeriodToSearch ) || s.getStartingDateTime( )
                                                .toLocalDate( ).isAfter( startingDateOfPeriodToSearch ) )
                                                && ( s.getStartingDateTime( ).toLocalDate( ).equals( endingDateOfPeriodToSearch ) || s.getStartingDateTime( )
                                                        .toLocalDate( ).isBefore( endingDateOfPeriodToSearch ) ) ).count( ) );
                        if ( nbSlots >= nbMaxAppointmentsPerUser )
                        {
                            bCheckPassed = false;
                            break;
                        }
                    }
                }
            }
        }
        return bCheckPassed;
    }

    /**
     * Check and validate all the rules for the number of booked seats asked
     * 
     * @param strNbBookedSeats
     *            the number of booked seats
     * @param form
     *            the form
     * @param nbRemainingPlaces
     *            the number of remaining places on the slot asked
     * @param locale
     *            the locale
     * @param listFormErrors
     *            the list of errors that can be fill in with the errors found for the number of booked seats
     * @return
     */
    public static int checkAndReturnNbBookedSeats( String strNbBookedSeats, AppointmentFormDTO form, AppointmentDTO appointmentDTO, Locale locale,
            List<GenericAttributeError> listFormErrors )
    {
        int nbBookedSeats = 1;
        if ( StringUtils.isEmpty( strNbBookedSeats ) && form.getMaxPeoplePerAppointment( ) > 1 )
        {
            GenericAttributeError genAttError = new GenericAttributeError( );
            genAttError.setErrorMessage( I18nService.getLocalizedString( ERROR_MESSAGE_EMPTY_NB_BOOKED_SEAT, locale ) );
            listFormErrors.add( genAttError );
        }
        if ( StringUtils.isNotEmpty( strNbBookedSeats ) )
        {
            try
            {
                nbBookedSeats = Integer.parseInt( strNbBookedSeats );
            }
            catch( NumberFormatException | NullPointerException e )
            {
                GenericAttributeError genAttError = new GenericAttributeError( );
                genAttError.setErrorMessage( I18nService.getLocalizedString( ERROR_MESSAGE_FORMAT_NB_BOOKED_SEAT, locale ) );
                listFormErrors.add( genAttError );
            }
        }
        // if it's a new appointment, need to check if the number of booked
        // seats is under or equal to the number of remaining places
        // if it's a modification, need to check if the new number of booked
        // seats is under or equal to the number of the remaining places + the
        // previous number of booked seats of the appointment
        if ( nbBookedSeats > appointmentDTO.getNbMaxPotentialBookedSeats( ) )
        {
            GenericAttributeError genAttError = new GenericAttributeError( );
            genAttError.setErrorMessage( I18nService.getLocalizedString( ERROR_MESSAGE_ERROR_NB_BOOKED_SEAT, locale ) );
            listFormErrors.add( genAttError );
        }

        if ( nbBookedSeats == 0 )
        {
            GenericAttributeError genAttError = new GenericAttributeError( );
            genAttError.setErrorMessage( I18nService.getLocalizedString( ERROR_MESSAGE_EMPTY_NB_BOOKED_SEAT, locale ) );
            listFormErrors.add( genAttError );
        }
        return nbBookedSeats;
    }

    /**
     * Fill the appoinmentFront DTO with the given parameters
     * 
     * @param appointmentDTO
     *            the appointmentFront DTO
     * @param nbBookedSeats
     *            the number of booked seats
     * @param strEmail
     *            the email of the user
     * @param strFirstName
     *            the first name of the user
     * @param strLastName
     *            the last name of the user
     */
    public static void fillAppointmentDTO( AppointmentDTO appointmentDTO, int nbBookedSeats, String strEmail, String strFirstName, String strLastName )
    {
        appointmentDTO.setDateOfTheAppointment( appointmentDTO.getSlot( ).getDate( ).format( Utilities.getDateFormatter( ) ) );
        appointmentDTO.setNbBookedSeats( nbBookedSeats );
        appointmentDTO.setEmail( strEmail );
        appointmentDTO.setFirstName( strFirstName );
        appointmentDTO.setLastName( strLastName );
    }

    /**
     * Validate the form and the additional entries of the form
     * 
     * @param appointmentDTO
     *            the appointmentFron DTo to validate
     * @param request
     *            the request
     * @param listFormErrors
     *            the list of errors that can be fill with the errors found at the validation
     */
    public static void validateFormAndEntries( AppointmentDTO appointmentDTO, HttpServletRequest request, List<GenericAttributeError> listFormErrors )
    {
        Set<ConstraintViolation<AppointmentDTO>> listErrors = BeanValidationUtil.validate( appointmentDTO );
        if ( CollectionUtils.isNotEmpty( listErrors ) )
        {
            for ( ConstraintViolation<AppointmentDTO> constraintViolation : listErrors )
            {
                GenericAttributeError genAttError = new GenericAttributeError( );
                genAttError.setErrorMessage( I18nService.getLocalizedString( constraintViolation.getMessageTemplate( ), request.getLocale( ) ) );
                listFormErrors.add( genAttError );
            }
        }
        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( EntryService.buildEntryFilter( appointmentDTO.getIdForm( ) ) );
        for ( Entry entry : listEntryFirstLevel )
        {
            listFormErrors.addAll( EntryService.getResponseEntry( request, entry.getIdEntry( ), request.getLocale( ), appointmentDTO ) );
        }
    }

    public static void fillInListResponseWithMapResponse( AppointmentDTO appointmentDTO )
    {
        Map<Integer, List<Response>> mapResponses = appointmentDTO.getMapResponsesByIdEntry( );
        if ( mapResponses != null && !mapResponses.isEmpty( ) )
        {
            List<Response> listResponse = new ArrayList<Response>( );
            for ( List<Response> listResponseByEntry : mapResponses.values( ) )
            {
                listResponse.addAll( listResponseByEntry );
            }
            // appointmentDTO.clearMapResponsesByIdEntry();
            appointmentDTO.setListResponse( listResponse );
        }
    }

    /**
     * Build a list of response of the appointment
     * 
     * @param appointment
     *            the appointment
     * @param request
     *            the request
     * @param locale
     *            the local
     * @return a list of response
     */
    public static List<ResponseRecapDTO> buildListResponse( AppointmentDTO appointment, HttpServletRequest request, Locale locale )
    {
        List<ResponseRecapDTO> listResponseRecapDTO = new ArrayList<ResponseRecapDTO>( );
        HashMap<Integer, List<ResponseRecapDTO>> mapResponse = new HashMap<>( );
        if ( CollectionUtils.isNotEmpty( appointment.getListResponse( ) ) )
        {
            listResponseRecapDTO = new ArrayList<ResponseRecapDTO>( appointment.getListResponse( ).size( ) );
            for ( Response response : appointment.getListResponse( ) )
            {
                int nIndex = response.getEntry( ).getPosition( );
                IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( response.getEntry( ) );
                ResponseRecapDTO responseRecapDTO = new ResponseRecapDTO( response, entryTypeService.getResponseValueForRecap( response.getEntry( ), request,
                        response, locale ) );
                List<ResponseRecapDTO> listResponse = mapResponse.get( nIndex );
                if ( listResponse == null )
                {
                    listResponse = new ArrayList<>( );
                    mapResponse.put( nIndex, listResponse );
                }
                listResponse.add( responseRecapDTO );
            }
        }
        for ( List<ResponseRecapDTO> listResponse : mapResponse.values( ) )
        {
            listResponseRecapDTO.addAll( listResponse );
        }
        return listResponseRecapDTO;
    }

    /**
     * Build the excel fil of the list of the appointments found in the manage appointment viw by filter
     * 
     * @param strIdForm
     *            the form id
     * @param response
     *            the response
     * @param locale
     *            the local
     * @param listAppointmentsDTO
     *            the list of the appointments to input in the excel file
     * @param stateService
     *            the state service
     */
    public static void buildExcelFileWithAppointments( String strIdForm, HttpServletResponse response, Locale locale, List<AppointmentDTO> listAppointmentsDTO,
            StateService stateService )
    {
        AppointmentFormDTO tmpForm = FormService.buildAppointmentFormLight( Integer.parseInt( strIdForm ) );
        XSSFWorkbook workbook = new XSSFWorkbook( );
        XSSFSheet sheet = workbook.createSheet( I18nService.getLocalizedString( KEY_RESOURCE_TYPE, locale ) );
        List<Object [ ]> tmpObj = new ArrayList<Object [ ]>( );
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( Integer.valueOf( strIdForm ) );
        List<Entry> listEntry = EntryHome.getEntryList( entryFilter );
        Map<Integer, String> mapDefaultValueGenAttBackOffice = new HashMap<Integer, String>( );
        for ( Entry e : listEntry )
        {
            if ( e.isOnlyDisplayInBack( ) )
            {
                e = EntryHome.findByPrimaryKey( e.getIdEntry( ) );
                if ( e.getFields( ) != null && e.getFields( ).size( ) == 1 && !StringUtils.isEmpty( e.getFields( ).get( 0 ).getValue( ) ) )
                {
                    mapDefaultValueGenAttBackOffice.put( e.getIdEntry( ), e.getFields( ).get( 0 ).getValue( ) );
                }
                else
                    if ( e.getFields( ) != null )
                    {
                        for ( Field field : e.getFields( ) )
                        {
                            if ( field.isDefaultValue( ) )
                            {
                                mapDefaultValueGenAttBackOffice.put( e.getIdEntry( ), field.getValue( ) );
                            }
                        }
                    }
            }
        }
        int nTaille = 10 + ( listEntry.size( ) + 1 );
        if ( tmpForm != null )
        {
            int nIndex = 0;
            Object [ ] strWriter = new String [ 1];
            strWriter [0] = tmpForm.getTitle( );
            tmpObj.add( strWriter );
            Object [ ] strInfos = new String [ nTaille];
            strInfos [0] = I18nService.getLocalizedString( KEY_COLUMN_LAST_NAME, locale );
            strInfos [1] = I18nService.getLocalizedString( KEY_COLUMN_FISRT_NAME, locale );
            strInfos [2] = I18nService.getLocalizedString( KEY_COLUMN_EMAIL, locale );
            strInfos [3] = I18nService.getLocalizedString( KEY_COLUMN_DATE_APPOINTMENT, locale );
            strInfos [4] = I18nService.getLocalizedString( KEY_TIME_START, locale );
            strInfos [5] = I18nService.getLocalizedString( KEY_TIME_END, locale );
            strInfos [6] = I18nService.getLocalizedString( KEY_COLUMN_ADMIN, locale );
            strInfos [7] = I18nService.getLocalizedString( KEY_COLUMN_STATUS, locale );
            strInfos [8] = I18nService.getLocalizedString( KEY_COLUMN_STATE, locale );
            strInfos [9] = I18nService.getLocalizedString( KEY_COLUMN_NB_BOOKED_SEATS, locale );
            nIndex = 1;
            if ( listEntry.size( ) > 0 )
            {
                for ( Entry e : listEntry )
                {
                    strInfos [10 + nIndex] = e.getTitle( );
                    nIndex++;
                }
            }
            tmpObj.add( strInfos );
        }
        if ( listAppointmentsDTO != null )
        {
            for ( AppointmentDTO appointmentDTO : listAppointmentsDTO )
            {
                int nIndex = 0;
                Object [ ] strWriter = new String [ nTaille];
                strWriter [0] = appointmentDTO.getLastName( );
                strWriter [1] = appointmentDTO.getFirstName( );
                strWriter [2] = appointmentDTO.getEmail( );
                strWriter [3] = appointmentDTO.getDateOfTheAppointment( );
                strWriter [4] = appointmentDTO.getStartingTime( ).toString( );
                strWriter [5] = appointmentDTO.getEndingTime( ).toString( );
                strWriter [6] = appointmentDTO.getAdminUser( );
                String status = I18nService.getLocalizedString( AppointmentDTO.PROPERTY_APPOINTMENT_STATUS_RESERVED, locale );
                if ( appointmentDTO.getIsCancelled( ) )
                {
                    status = I18nService.getLocalizedString( AppointmentDTO.PROPERTY_APPOINTMENT_STATUS_UNRESERVED, locale );
                }
                strWriter [7] = status;
                State stateAppointment = stateService.findByResource( appointmentDTO.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE,
                        tmpForm.getIdWorkflow( ) );
                String strState = StringUtils.EMPTY;
                if ( stateAppointment != null )
                {
                    appointmentDTO.setState( stateAppointment );
                    strState = stateAppointment.getName( );
                }
                strWriter [8] = strState;
                nIndex = 1;
                strWriter [9] = Integer.toString( appointmentDTO.getNbBookedSeats( ) );
                List<Integer> listIdResponse = AppointmentResponseService.findListIdResponse( appointmentDTO.getIdAppointment( ) );
                List<Response> listResponses = new ArrayList<Response>( );
                for ( int nIdResponse : listIdResponse )
                {
                    Response resp = ResponseHome.findByPrimaryKey( nIdResponse );
                    if ( resp != null )
                    {
                        listResponses.add( resp );
                    }
                }
                for ( Entry e : listEntry )
                {
                    Integer key = e.getIdEntry( );
                    StringBuffer strValue = new StringBuffer( StringUtils.EMPTY );
                    String strPrefix = StringUtils.EMPTY;
                    for ( Response resp : listResponses )
                    {
                        String strRes = StringUtils.EMPTY;
                        if ( key.equals( resp.getEntry( ).getIdEntry( ) ) )
                        {
                            Field f = resp.getField( );
                            int nfield = 0;
                            if ( f != null )
                            {
                                nfield = f.getIdField( );
                                Field field = FieldHome.findByPrimaryKey( nfield );
                                if ( field != null )
                                {
                                    strRes = field.getTitle( );
                                }
                            }
                            else
                            {
                                strRes = resp.getResponseValue( );
                            }
                        }
                        if ( ( strRes != null ) && !strRes.isEmpty( ) )
                        {
                            strValue.append( strPrefix + strRes );
                            strPrefix = CONSTANT_COMMA;
                        }
                    }
                    if ( strValue.toString( ).isEmpty( ) && mapDefaultValueGenAttBackOffice.containsKey( key ) )
                    {
                        strValue.append( mapDefaultValueGenAttBackOffice.get( key ) );
                    }
                    if ( !strValue.toString( ).isEmpty( ) )
                    {
                        strWriter [10 + nIndex] = strValue.toString( );
                    }
                    nIndex++;
                }
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
            String now = new SimpleDateFormat( "yyyyMMdd-hhmm" ).format( GregorianCalendar.getInstance( locale ).getTime( ) ) + "_"
                    + I18nService.getLocalizedString( KEY_RESOURCE_TYPE, locale ) + EXCEL_FILE_EXTENSION;
            response.setContentType( EXCEL_MIME_TYPE );
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
    }

    /**
     * Kill the lock timer on a slot
     * 
     * @param request
     *            the request
     */
    public static void killTimer( HttpServletRequest request )
    {
    	TimerForLockOnSlot timer = (TimerForLockOnSlot) request.getSession( ).getAttribute( SESSION_TIMER_SLOT );
        if ( timer != null )
        {
        	timer.setIsCancelled(true);
            timer.cancel( );
            request.getSession( ).removeAttribute( SESSION_TIMER_SLOT );
        }
    }

    /**
     * Create a timer on a slot
     * 
     * @param slot
     *            the slot
     * @param appointmentDTO
     *            the appointment
     * @param maxPeoplePerAppointment
     *            the max people per appointment
     * @return the timer
     */
    public static synchronized  Timer putTimerInSession( HttpServletRequest request, int nIdSlot, AppointmentDTO appointmentDTO, int maxPeoplePerAppointment )
    {
    	Slot slot = SlotService.findSlotById( nIdSlot );
    	
       
        int nbPotentialRemainingPlaces = slot.getNbPotentialRemainingPlaces( );
        int nbPotentialPlacesTaken = Math.min( nbPotentialRemainingPlaces, maxPeoplePerAppointment );
        
        if( slot.getNbPotentialRemainingPlaces() > 0 ){
        	
        	appointmentDTO.setNbMaxPotentialBookedSeats( nbPotentialPlacesTaken );
           // slot.setNbPotentialRemainingPlaces( nbPotentialRemainingPlaces - nbPotentialPlacesTaken );
            SlotSafeService.decrementPotentialRemainingPlaces(nbPotentialPlacesTaken, slot.getIdSlot( ));

            //SlotService.updateSlot( slot );
	     
	        TimerForLockOnSlot timer = new TimerForLockOnSlot( );
	        SlotEditTask slotEditTask = new SlotEditTask( timer );
	        slotEditTask.setNbPlacesTaken( nbPotentialPlacesTaken );
	        slotEditTask.setIdSlot( slot.getIdSlot( ) );
	        long delay = TimeUnit.MINUTES.toMillis( AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_EXPIRED_TIME_EDIT_APPOINTMENT, 1 ) );
	        timer.schedule( slotEditTask, delay );
	        request.getSession( ).setAttribute( AppointmentUtilities.SESSION_TIMER_SLOT, timer );
	        return timer;
    	}
        appointmentDTO.setNbMaxPotentialBookedSeats(0);
    	return null;
    }

    /**
     * Get Form Permissions
     * 
     * @param listForms
     * @param request
     * @return
     */
    public static String [ ][ ] getPermissions( List<AppointmentFormDTO> listForms, AdminUser user )
    {
        String [ ][ ] retour = new String [ listForms.size( )] [ 6];
        int nI = 0;

        for ( AppointmentFormDTO tmpForm : listForms )
        {
            String [ ] strRetour = new String [ 7];
            strRetour [0] = String.valueOf( RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, String.valueOf( tmpForm.getIdForm( ) ),
                    AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, user ) );
            strRetour [1] = String.valueOf( RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, String.valueOf( tmpForm.getIdForm( ) ),
                    AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM, user ) );
            strRetour [2] = String.valueOf( RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, String.valueOf( tmpForm.getIdForm( ) ),
                    AppointmentResourceIdService.PERMISSION_MODIFY_FORM, user ) );
            strRetour [3] = String.valueOf( RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, String.valueOf( tmpForm.getIdForm( ) ),
                    AppointmentResourceIdService.PERMISSION_MODIFY_FORM, user ) );
            strRetour [4] = String.valueOf( RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, String.valueOf( tmpForm.getIdForm( ) ),
                    AppointmentResourceIdService.PERMISSION_CHANGE_STATE, user ) );
            strRetour [5] = String.valueOf( RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, String.valueOf( tmpForm.getIdForm( ) ),
                    AppointmentResourceIdService.PERMISSION_DELETE_FORM, user ) );
            retour [nI++] = strRetour;
        }

        return retour;
    }

    /**
     * Return the min starting time to display
     * 
     * @param minStartingTime
     *            the min starting time
     * @return 00 if the minstarting time is under 30, 30 otherwise
     */
    public static LocalTime getMinTimeToDisplay( LocalTime minStartingTime )
    {
        LocalTime minStartingTimeToDisplay;
        if ( minStartingTime.getMinute( ) < THIRTY_MINUTES )
        {
            minStartingTimeToDisplay = LocalTime.of( minStartingTime.getHour( ), 0 );
        }
        else
        {
            minStartingTimeToDisplay = LocalTime.of( minStartingTime.getHour( ), THIRTY_MINUTES );
        }
        return minStartingTimeToDisplay;
    }

    /**
     * Return the max ending time to display
     * 
     * @param maxEndingTime
     *            the max ending time
     * @return 30 if the max ending time is under 30, otherwise the next hour
     */
    public static LocalTime getMaxTimeToDisplay( LocalTime maxEndingTime )
    {
        LocalTime maxEndingTimeToDisplay;
        if ( maxEndingTime.getMinute( ) < THIRTY_MINUTES )
        {
            maxEndingTimeToDisplay = LocalTime.of( maxEndingTime.getHour( ), THIRTY_MINUTES );
        }
        else
        {
            maxEndingTimeToDisplay = LocalTime.of( maxEndingTime.getHour( ) + 1, 0 );
        }
        return maxEndingTimeToDisplay;
    }

    /**
     * Check if there are appointments impacted by the new week definition
     * 
     * @param listAppointment
     *            the list of appointments
     * @param nIdForm
     *            the form Id
     * @param dateOfModification
     *            the date of modification (date of apply of the new week definition)
     * @param appointmentForm
     *            the appointment form
     * @return true if there are appointments impacted
     */
    public static boolean checkNoAppointmentsImpacted( List<Appointment> listAppointment, int nIdForm, LocalDate dateOfModification,
            AppointmentFormDTO appointmentForm )
    {
        boolean bNoAppointmentsImpacted = true;
        // Find the previous WeekDefinition
        WeekDefinition previousWeekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( nIdForm, dateOfModification );
        // Find the previous reservation rule
        ReservationRule previousReservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( nIdForm, dateOfModification );
        // Build the previous appointment form with the previous week
        // definition and the previous reservation rule
        AppointmentFormDTO previousAppointmentForm = FormService.buildAppointmentForm( nIdForm, previousReservationRule.getIdReservationRule( ),
                previousWeekDefinition.getIdWeekDefinition( ) );
        // Need to check if the new definition week has more open days.
        List<DayOfWeek> previousOpenDays = WorkingDayService.getOpenDays( previousAppointmentForm );
        List<DayOfWeek> newOpenDays = WorkingDayService.getOpenDays( appointmentForm );
        // If new open days
        if ( newOpenDays.containsAll( previousOpenDays ) )
        {
            // Nothing to check
        }
        else
        {
            // Else we remove all the corresponding days
            previousOpenDays.removeAll( newOpenDays );
            // For the remaining days
            // for each appointment, need to check if the appointment is
            // not in the remaining open days
            boolean bAppointmentOnOpenDays = false;
            for ( Appointment appointment : listAppointment )
            {
                Slot tempSlot = SlotService.findSlotById( appointment.getIdSlot( ) );
                if ( previousOpenDays.contains( tempSlot.getStartingDateTime( ).getDayOfWeek( ) ) )
                {
                    bAppointmentOnOpenDays = true;
                    break;
                }
            }
            bNoAppointmentsImpacted = !bAppointmentOnOpenDays;
        }
        LocalTime newStartingTime = LocalTime.parse( appointmentForm.getTimeStart( ) );
        LocalTime newEndingTime = LocalTime.parse( appointmentForm.getTimeEnd( ) );
        LocalTime oldStartingTime = LocalTime.parse( previousAppointmentForm.getTimeStart( ) );
        LocalTime oldEndingTime = LocalTime.parse( previousAppointmentForm.getTimeEnd( ) );
        // If we have changed the duration of an appointment
        if ( appointmentForm.getDurationAppointments( ) != previousAppointmentForm.getDurationAppointments( ) )
        {
            bNoAppointmentsImpacted = false;
        }
        // We have change the open hours
        // if the time slot is reduced
        if ( newStartingTime.isAfter( oldStartingTime ) || newEndingTime.isBefore( oldEndingTime ) )
        {
            bNoAppointmentsImpacted = false;
        }

        return bNoAppointmentsImpacted;
    }

    /**
     * Check that there is no validated appointments on a slot
     * 
     * @param slot
     *            the slot
     * @return true if there are no validated appointments on this slot, false otherwise
     */
    public static boolean checkNoValidatedAppointmentsOnThisSlot( Slot slot )
    {
        boolean bNoValidatedAppointmentsOnThisSlot = true;
        List<Appointment> listAppointmentsOnThisSlot = AppointmentService.findListAppointmentBySlot( slot.getIdSlot( ) );
        if ( CollectionUtils.isNotEmpty( listAppointmentsOnThisSlot ) )
        {
            listAppointmentsOnThisSlot = listAppointmentsOnThisSlot.stream( ).filter( a -> a.getIsCancelled( ) == false ).collect( Collectors.toList( ) );
        }
        if ( CollectionUtils.isNotEmpty( listAppointmentsOnThisSlot ) )
        {
            bNoValidatedAppointmentsOnThisSlot = false;
        }
        return bNoValidatedAppointmentsOnThisSlot;
    }

    /**
     * Return the slots impacted by the modification of this time slot
     * 
     * @param timeSlot
     *            the time slot
     * @param nIdForm
     *            the form id
     * @param nIdWeekDefinition
     *            the week definition id
     * @param bShiftSlot
     *            the boolean value for the shift
     * @return the list of slots impacted
     */
    public static List<Slot> findSlotsImpactedByThisTimeSlot( TimeSlot timeSlot, int nIdForm, int nIdWeekDefinition, boolean bShiftSlot )
    {
        List<Slot> listSlotsImpacted = new ArrayList<>( );
        LocalDate maxDate = null;
        // Get the weekDefinition that is currently modified
        WeekDefinition currentModifiedWeekDefinition = WeekDefinitionService.findWeekDefinitionById( nIdWeekDefinition );
        // Find the next weekDefinition, if exist, to have the max date to
        // search slots with appointments
        WeekDefinition nextWeekDefinition = WeekDefinitionService.findNextWeekDefinition( nIdForm, currentModifiedWeekDefinition.getDateOfApply( ) );
        if ( nextWeekDefinition != null )
        {
            maxDate = nextWeekDefinition.getDateOfApply( );
        }
        else
        {
            // If there is no next weekDefinition
            // Get the ending validity date of the form
            Form form = FormService.findFormLightByPrimaryKey( nIdForm );
            if ( form.getEndingValidityDate( ) != null )
            {
                maxDate = form.getEndingValidityDate( );
            }
            else
            {
                // If there is no ending validity date
                // Find the slot with the max date
                Slot slotWithMaxDate = SlotService.findSlotWithMaxDate( nIdForm );
                if ( slotWithMaxDate != null && slotWithMaxDate.getStartingDateTime( ) != null )
                {
                    maxDate = slotWithMaxDate.getStartingDateTime( ).toLocalDate( );
                }
            }
        }
        if ( maxDate != null )
        {
            // We have an upper bound to search with
            List<Slot> listSlots = SlotService.findSlotsByIdFormAndDateRange( nIdForm, currentModifiedWeekDefinition.getDateOfApply( ).atStartOfDay( ),
                    maxDate.atTime( LocalTime.MAX ) );
            // Need to check if the modification of the time slot or the typical
            // week impacts these slots
            WorkingDay workingDay = WorkingDayService.findWorkingDayLightById( timeSlot.getIdWorkingDay( ) );
            // Filter all the slots with the working day and the starting time
            // ending time of the time slot
            // The begin time of the slot can be before or after the begin time
            // of the time slot
            // and the ending time of the slot can be before or after the ending
            // time of the time slot (specific slot)

            // If shiftTimeSlot is checked, need to check all the slots impacted
            // until the end of the day
            if ( bShiftSlot )
            {
                listSlotsImpacted = listSlots
                        .stream( )
                        .filter(
                                slot -> ( ( slot.getStartingDateTime( ).getDayOfWeek( ) == DayOfWeek.of( workingDay.getDayOfWeek( ) ) ) && ( !slot
                                        .getStartingTime( ).isBefore( timeSlot.getStartingTime( ) ) || ( slot.getStartingTime( ).isBefore(
                                        timeSlot.getStartingTime( ) ) && ( slot.getEndingTime( ).isAfter( timeSlot.getStartingTime( ) ) ) ) ) ) )
                        .collect( Collectors.toList( ) );
            }
            else
            {
                listSlotsImpacted = listSlots
                        .stream( )
                        .filter(
                                slot -> ( slot.getStartingDateTime( ).getDayOfWeek( ) == DayOfWeek.of( workingDay.getDayOfWeek( ) ) )
                                        && ( slot.getStartingTime( ).equals( timeSlot.getStartingTime( ) )
                                                || ( slot.getStartingTime( ).isBefore( timeSlot.getStartingTime( ) ) && ( slot.getEndingTime( )
                                                        .isAfter( timeSlot.getStartingTime( ) ) ) ) || ( slot.getStartingTime( ).isAfter(
                                                timeSlot.getStartingTime( ) ) && ( !slot.getEndingTime( ).isAfter( timeSlot.getEndingTime( ) ) ) ) ) )
                        .collect( Collectors.toList( ) );
            }
        }
        return listSlotsImpacted;
    }
}
