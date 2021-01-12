/*
 * Copyright (c) 2002-2020, City of Paris
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

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentSlot;
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
import fr.paris.lutece.plugins.appointment.web.dto.ReservationRuleDTO;
import fr.paris.lutece.plugins.appointment.web.dto.ResponseRecapDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.RBACService;
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

	public static final String ERROR_MESSAGE_EMPTY_CONFIRM_EMAIL = "appointment.validation.appointment.EmailConfirmation.email";
    public static final String ERROR_MESSAGE_CONFIRM_EMAIL = "appointment.message.error.confirmEmail";
    public static final String ERROR_MESSAGE_DATE_APPOINTMENT = "appointment.message.error.dateAppointment";
    public static final String ERROR_MESSAGE_EMPTY_EMAIL = "appointment.validation.appointment.Email.notEmpty";
    public static final String ERROR_MESSAGE_EMPTY_NB_BOOKED_SEAT = "appointment.validation.appointment.NbBookedSeat.notEmpty";
    public static final String ERROR_MESSAGE_FORMAT_NB_BOOKED_SEAT = "appointment.validation.appointment.NbBookedSeat.notNumberFormat";
    public static final String ERROR_MESSAGE_ERROR_NB_BOOKED_SEAT = "appointment.validation.appointment.NbBookedSeat.error";

    public static final String SESSION_TASK_TIMER_SLOT = "appointment.session.task.timer.slot";

    public static final String PROPERTY_DEFAULT_EXPIRED_TIME_EDIT_APPOINTMENT = "appointment.edit.expired.time";

    public static final int THIRTY_MINUTES = 30;
    private static TimerForLockOnSlot _timer;

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
        if ( getStartingDateTime( appointmentDTO ).toLocalDate( ).isBefore( LocalDate.now( ) ) )
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
                    LocalDateTime dateOfTheLastAppointment = listSlots.stream( ).map( Slot::getStartingDateTime ).max( LocalDateTime::compareTo ).orElse( null );
                    
                    // Check the number of days between this appointment and
                    // the last appointment the user has taken
                    LocalDate dateOfTheAppointment = getStartingDateTime( appointmentDTO ).toLocalDate( );
                    if ( dateOfTheLastAppointment!=null && Math.abs( dateOfTheLastAppointment.toLocalDate( ).until( dateOfTheAppointment, ChronoUnit.DAYS ) ) <= nbDaysBetweenTwoAppointments )
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
    public static boolean checkNbDaysBetweenTwoAppointmentsTaken( AppointmentDTO appointmentDTO, String strEmail, AppointmentFormDTO form )
    {
        boolean bCheckPassed = true;
        int nbDaysBetweenTwoAppointments = form.getNbDaysBeforeNewAppointment( );
        if ( nbDaysBetweenTwoAppointments != 0 )
        {
            AppointmentFilterDTO filter = new AppointmentFilterDTO( );
            filter.setEmail( strEmail );
            filter.setStatus( 0 );
            filter.setIdForm( form.getIdForm( ) );
            List<Appointment> listAppointment = AppointmentService.findListAppointmentsByFilter( filter );
            // If we modify an appointment, we remove the
            // appointment that we currently edit
            if ( appointmentDTO.getIdAppointment( ) != 0 )
            {
               listAppointment.removeIf( a -> a.getIdAppointment( ) != appointmentDTO.getIdAppointment( ) );
            }

            if ( CollectionUtils.isNotEmpty( listAppointment ) )
            {

                LocalDateTime dateOfTheLastAppointmentTaken = listAppointment.stream( ).map( Appointment::getDateAppointmentTaken )
                        .max( LocalDateTime::compareTo ).orElse( null );

                if ( dateOfTheLastAppointmentTaken != null && Math.abs( dateOfTheLastAppointmentTaken.until( LocalDateTime.now( ), ChronoUnit.DAYS ) ) <= nbDaysBetweenTwoAppointments )
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
                    	   listSlots = SlotService.findListSlotByIdAppointment( appointment.getIdAppointment( ) );
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
    	if ( form.getNbMaxAppointmentsPerUser() > 0 )
        {
	        // Get the date of the future appointment
	        LocalDate dateOfTheAppointment = getStartingDateTime( appointmentDTO ).toLocalDate( );        
	        AppointmentFilterDTO filter= new AppointmentFilterDTO();
	        filter.setEmail(strEmail);
	        filter.setIdForm( form.getIdForm( ) );
	        filter.setStatus( 0 );
	        if( form.getNbDaysForMaxAppointmentsPerUser( ) > 0 ) {
	        	
	        	filter.setStartingDateOfSearch(Date.valueOf( dateOfTheAppointment.minusDays((long)  form.getNbDaysForMaxAppointmentsPerUser( ) - 1 )));
	        	filter.setEndingDateOfSearch(Date.valueOf( dateOfTheAppointment.plusDays((long)  form.getNbDaysForMaxAppointmentsPerUser( ) - 1 )) );
	        }
	        List<AppointmentDTO>  listAppointmentsDTO = AppointmentService.findListAppointmentsDTOByFilter( filter );
	        // If we modify an appointment, we remove the
	        // appointment that we currently edit
	        if( appointmentDTO.getIdAppointment( ) != 0 ) {
	        	
	        	listAppointmentsDTO.removeIf(appt -> appt.getIdAppointment() != appointmentDTO.getIdAppointment( ) );
	        }
	        
	        if ( CollectionUtils.isNotEmpty( listAppointmentsDTO ) )
	        {
	        	if( form.getNbDaysForMaxAppointmentsPerUser( ) > 0) {
	        		
			        List<AppointmentDTO>  listAppointmentsBefore= listAppointmentsDTO.stream().filter(appt -> appt.getStartingDateTime( ).toLocalDate( ).isBefore( dateOfTheAppointment ) || appt.getStartingDateTime( ).toLocalDate( ).isEqual( dateOfTheAppointment )).collect(Collectors.toList( ));
			        List<AppointmentDTO>  listAppointmentsAfter= listAppointmentsDTO.stream().filter(appt -> appt.getStartingDateTime( ).toLocalDate( ).isAfter( dateOfTheAppointment ) || appt.getStartingDateTime( ).toLocalDate( ).isEqual( dateOfTheAppointment )).collect(Collectors.toList( ));
			        
			        if ( listAppointmentsBefore.size( ) >= form.getNbMaxAppointmentsPerUser( ) || listAppointmentsAfter.size( ) >= form.getNbMaxAppointmentsPerUser( ) )
			        {
			                return false;
			        }
	        	}else if(listAppointmentsDTO.size( ) >= form.getNbMaxAppointmentsPerUser( ) ) {
	        		
	        		return false;
	        	}
	        }
        }
       return true;
        
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
        if ( nbBookedSeats > appointmentDTO.getNbMaxPotentialBookedSeats( ) && !appointmentDTO.getOverbookingAllowed( ) )

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
        appointmentDTO.setDateOfTheAppointment( appointmentDTO.getSlot( ).get( 0 ).getDate( ).format( Utilities.getFormatter( ) ) );
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
    public static void validateFormAndEntries( AppointmentDTO appointmentDTO, HttpServletRequest request, List<GenericAttributeError> listFormErrors, boolean allEntries )
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
        EntryFilter filter = EntryService.buildEntryFilter( appointmentDTO.getIdForm( ) );
        if ( allEntries )
        {
            filter.setIsOnlyDisplayInBack( GenericAttributesUtils.CONSTANT_ID_NULL );
        }
        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );
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
            List<Response> listResponse = new ArrayList<>( );
            for ( List<Response> listResponseByEntry : mapResponses.values( ) )
            {
                listResponse.addAll( listResponseByEntry );
            }
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
        List<ResponseRecapDTO> listResponseRecapDTO = new ArrayList<>( );
        HashMap<Integer, List<ResponseRecapDTO>> mapResponse = new HashMap<>( );
        if ( CollectionUtils.isNotEmpty( appointment.getListResponse( ) ) )
        {
            listResponseRecapDTO = new ArrayList<>( appointment.getListResponse( ).size( ) );
            for ( Response response : appointment.getListResponse( ) )
            {
                int nIndex = response.getEntry( ).getPosition( );
                IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( response.getEntry( ) );
                ResponseRecapDTO responseRecapDTO = new ResponseRecapDTO( response,
                        entryTypeService.getResponseValueForRecap( response.getEntry( ), request, response, locale ) );
                
                
                List<ResponseRecapDTO> listResponse = mapResponse.computeIfAbsent( nIndex, ArrayList::new );
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
     * cancel the task timer on a slot
     * 
     * @param request
     *            the request
     */
    public static void cancelTaskTimer( HttpServletRequest request, int idSlot )
    {
    	SlotEditTask task = (SlotEditTask) request.getSession( ).getAttribute( SESSION_TASK_TIMER_SLOT + idSlot );
        if ( task != null )
        {
        	task.cancel( );
        	task.setIsCancelled( true );
            request.getSession( ).removeAttribute( SESSION_TASK_TIMER_SLOT + idSlot );
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
    public static Timer putTimerInSession( HttpServletRequest request, int nIdSlot, AppointmentDTO appointmentDTO, int maxPeoplePerAppointment )
    {
    	 Lock lock = SlotSafeService.getLockOnSlot( nIdSlot );
         lock.lock( );
         try
         {
	        Slot slot = SlotService.findSlotById( nIdSlot );
	
	        int nbPotentialRemainingPlaces = slot.getNbPotentialRemainingPlaces( );
	        int nbPotentialPlacesTaken = Math.min( nbPotentialRemainingPlaces, maxPeoplePerAppointment );
	        int nNewNbMaxPotentialBookedSeats = Math.min( nbPotentialPlacesTaken + appointmentDTO.getNbMaxPotentialBookedSeats( ), maxPeoplePerAppointment );
	
	        if ( slot.getNbPotentialRemainingPlaces( ) > 0 )
	        {
		
	            _timer = (_timer != null )?_timer:new TimerForLockOnSlot( );
	            _timer.purge();
	            SlotEditTask slotEditTask = new SlotEditTask( );
	            slotEditTask.setNbPlacesTaken( nbPotentialPlacesTaken );
	            slotEditTask.setIdSlot( slot.getIdSlot( ) );
	            long delay = TimeUnit.MINUTES.toMillis( AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_EXPIRED_TIME_EDIT_APPOINTMENT, 1 ) );
	            _timer.schedule( slotEditTask, delay );
	            
	            appointmentDTO.setNbMaxPotentialBookedSeats( nNewNbMaxPotentialBookedSeats );
	            SlotSafeService.decrementPotentialRemainingPlaces( nbPotentialPlacesTaken, slot.getIdSlot( ) );
	            
	            request.getSession( ).setAttribute( SESSION_TASK_TIMER_SLOT + slotEditTask.getIdSlot( ), slotEditTask );
	            return _timer;
	        }
	        appointmentDTO.setNbMaxPotentialBookedSeats( 0 );
         }catch ( IllegalStateException e){  
        	 
        	 _timer= new TimerForLockOnSlot( );
        	 throw e;
         }
         finally
         {

             lock.unlock( );
         }
        return null;
    }

    /**
     * Get Form Permissions
     * 
     * @param listForms
     * @param request
     * @return
     */
    public static String [ ] [ ] getPermissions( List<AppointmentFormDTO> listForms, AdminUser user )
    {
        String [ ] [ ] retour = new String [ listForms.size( )] [ 6];
        int nI = 0;

        for ( AppointmentFormDTO tmpForm : listForms )
        {
            
            String [ ] strRetour = new String [ 7];
            strRetour [0] = String.valueOf( RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, String.valueOf( tmpForm.getIdForm( ) ),
                    AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT, (fr.paris.lutece.api.user.User) user ) );
            strRetour [1] = String.valueOf( RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, String.valueOf( tmpForm.getIdForm( ) ),
                    AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM, (fr.paris.lutece.api.user.User) user ) );
            strRetour [2] = String.valueOf( RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, String.valueOf( tmpForm.getIdForm( ) ),
                    AppointmentResourceIdService.PERMISSION_MODIFY_FORM, (fr.paris.lutece.api.user.User) user ) );
            strRetour [3] = String.valueOf( RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, String.valueOf( tmpForm.getIdForm( ) ),
                    AppointmentResourceIdService.PERMISSION_MODIFY_FORM, (fr.paris.lutece.api.user.User) user ) );
            strRetour [4] = String.valueOf( RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, String.valueOf( tmpForm.getIdForm( ) ),
                    AppointmentResourceIdService.PERMISSION_CHANGE_STATE, (fr.paris.lutece.api.user.User) user ) );
            strRetour [5] = String.valueOf( RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, String.valueOf( tmpForm.getIdForm( ) ),
                    AppointmentResourceIdService.PERMISSION_DELETE_FORM, (fr.paris.lutece.api.user.User) user ) );
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
    	ReservationRule previousReservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( nIdForm, dateOfModification );
    	return checkNoAppointmentsImpacted (listAppointment, nIdForm,previousReservationRule.getIdReservationRule( ), appointmentForm );
    }
    /**
     * Check if there are appointments impacted by the new week definition
     * 
     * @param listAppointment
     *            the list of appointments
     * @param nIdForm
     *            the form Id
     * @param nIdreservationRule
     *            the reservationRule id
     * @param appointmentForm
     *            the appointment form
     * @return true if there are appointments impacted
     */
    public static boolean checkNoAppointmentsImpacted( List<Appointment> listAppointment, int nIdForm, int nIdreservationRule,
            AppointmentFormDTO appointmentForm )
    {
        boolean bNoAppointmentsImpacted = true;
        // Build the previous appointment form with the previous week
        // definition and the previous reservation rule
        AppointmentFormDTO previousAppointmentForm = FormService.buildAppointmentForm( nIdForm, nIdreservationRule );
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
                for ( AppointmentSlot appSlot : appointment.getListAppointmentSlot( ) )
                {

                    Slot tempSlot = SlotService.findSlotById( appSlot.getIdSlot( ) );
                    if ( previousOpenDays.contains( tempSlot.getStartingDateTime( ).getDayOfWeek( ) ) )
                    {
                        bAppointmentOnOpenDays = true;
                        break;
                    }
                }
                if ( bAppointmentOnOpenDays )
                {
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
        // If we have change the open hours
    
        if ( !newStartingTime.equals( oldStartingTime )  || !newEndingTime.equals( oldEndingTime )  )
        {
            bNoAppointmentsImpacted = false;
        }

        return bNoAppointmentsImpacted;
    }
    
    /**
     * Check if there are appointments impacted by the new week definition
     * @param listSlotsImpacted the list of slot impacted
     * @param appointmentForm
     *            the appointment form
     * @return true if there are appointments impacted
     */
    public static boolean checkNoAppointmentsImpacted( List<Slot> listSlotWithAppointment, AppointmentFormDTO appointmentForm )
    {
        // Build the previous appointment form with the previous week
        // definition and the previous reservation rule
        AppointmentFormDTO previousAppointmentForm = FormService.buildAppointmentForm( appointmentForm.getIdForm( ), appointmentForm.getIdReservationRule( ) );
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
           
            for ( Slot tempSlot : listSlotWithAppointment )
            {
              if ( previousOpenDays.contains( tempSlot.getStartingDateTime( ).getDayOfWeek( ) ) )
              {
                  return false;
               }
             }
                
        }
        LocalTime newStartingTime = LocalTime.parse( appointmentForm.getTimeStart( ) );
        LocalTime newEndingTime = LocalTime.parse( appointmentForm.getTimeEnd( ) );
        LocalTime oldStartingTime = LocalTime.parse( previousAppointmentForm.getTimeStart( ) );
        LocalTime oldEndingTime = LocalTime.parse( previousAppointmentForm.getTimeEnd( ) );
        // If we have changed the duration of an appointment
        if ( appointmentForm.getDurationAppointments( ) != previousAppointmentForm.getDurationAppointments( ) )
        {
            return false;
        }
        // If we have change the open hours
    
        if ( !newStartingTime.equals( oldStartingTime )  || !newEndingTime.equals( oldEndingTime )  )
        {
            return false;
        }

        return true;
    }
    
    /**
     * Check if there are appointments impacted by the new week definition
     * @param listSlotsImpacted the list of slot impacted
     * @param newReservationRule the reservation rule
     * @return true if there are no appointments impacted
     */
    public static boolean checkNoAppointmentsImpacted( List<Slot> listSlotsImpacted, int idReservationRule  )
    {
    	
    	  List<WorkingDay> listWorkingDay=  WorkingDayService.findListWorkingDayByWeekDefinitionRule( idReservationRule );
          for ( Slot slot : listSlotsImpacted )
          {        	  
        	  WorkingDay workingDay= listWorkingDay.stream().filter(day -> day.getDayOfWeek() == slot.getDate().getDayOfWeek().getValue( ) ).findFirst().orElse(null);
        	  if (workingDay != null ) {
        		  
        		 if( workingDay.getListTimeSlot().stream().noneMatch(  time -> slot.getStartingTime().equals(time.getStartingTime()) && slot.getEndingTime().equals(time.getEndingTime()))){
        	  		  
        		   return false;
        		 }
        	  
        	  }else {
        		  
        		  return false;
        	  }         	 
          }

       
        return true;
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
            listAppointmentsOnThisSlot = listAppointmentsOnThisSlot.stream( ).filter( a -> !a.getIsCancelled( ) ).collect( Collectors.toList( ) );
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
        List<Slot> listSlotsImpacted = null;
        // Get the weekDefinition that is currently modified
        WeekDefinition currentModifiedWeekDefinition = WeekDefinitionService.findWeekDefinitionById( nIdWeekDefinition );    
       // We have an upper bound to search with
        List<Slot> listSlots = SlotService.findSlotsByIdFormAndDateRange( nIdForm, currentModifiedWeekDefinition.getDateOfApply( ).atStartOfDay( ),
            		currentModifiedWeekDefinition.getEndingDateOfApply( ).atTime( LocalTime.MAX ) );
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
           listSlotsImpacted = listSlots.stream( )
                        .filter( slot -> ( ( slot.getStartingDateTime( ).getDayOfWeek( ) == DayOfWeek.of( workingDay.getDayOfWeek( ) ) )
                                && ( !slot.getStartingTime( ).isBefore( timeSlot.getStartingTime( ) )
                                        || ( slot.getStartingTime( ).isBefore( timeSlot.getStartingTime( ) )
                                                && ( slot.getEndingTime( ).isAfter( timeSlot.getStartingTime( ) ) ) ) ) ) )
                        .collect( Collectors.toList( ) );
        }
        else
        {
                listSlotsImpacted = listSlots.stream( )
                        .filter( slot -> ( slot.getStartingDateTime( ).getDayOfWeek( ) == DayOfWeek.of( workingDay.getDayOfWeek( ) ) )
                                && ( slot.getStartingTime( ).equals( timeSlot.getStartingTime( ) )
                                        || ( slot.getStartingTime( ).isBefore( timeSlot.getStartingTime( ) )
                                                && ( slot.getEndingTime( ).isAfter( timeSlot.getStartingTime( ) ) ) )
                                        || ( slot.getStartingTime( ).isAfter( timeSlot.getStartingTime( ) )
                                                && ( !slot.getEndingTime( ).isAfter( timeSlot.getEndingTime( ) ) ) ) ) )
                        .collect( Collectors.toList( ) );
        }
        
        return listSlotsImpacted;
    }

    public static LocalDateTime getStartingDateTime( Appointment appointmentDTO )
    {

        List<Slot> listSlot = appointmentDTO.getSlot( );
        if ( CollectionUtils.isNotEmpty( listSlot ) )
        {
            Slot slot = listSlot.stream( ).min( Comparator.comparing( Slot::getStartingDateTime ) ).orElse( listSlot.get( 0 ) );
            return slot.getStartingDateTime( );
        }

        return null;
    }

    public static LocalDateTime getEndingDateTime( Appointment appointmentDTO )
    {

        List<Slot> listSlot = appointmentDTO.getSlot( );
        if ( listSlot != null && !listSlot.isEmpty( ) )
        {

            Slot slot = listSlot.stream( ).max( Comparator.comparing( Slot::getStartingDateTime ) ).orElse( listSlot.get( 0 ) );
            return slot.getEndingDateTime( );
        }

        return null;
    }

    /**
     * return true if all slots are consecutive
     * 
     * @param allSlots
     *            the list of slots
     * @return true if all slots are consecutive
     */
    public static boolean isConsecutiveSlots( List<Slot> allSlots )
    {
        Slot slot = null;
        for ( Slot nextSlot : allSlots )
        {
            if ( nextSlot == null || ( slot != null && !Objects.equals( slot.getEndingDateTime( ), nextSlot.getStartingDateTime( ) ) ) )
            {
                return false;
            }
            slot = nextSlot;
        }
        return true;
    }
    
    /**
     * Fill the reservation rule object with the corresponding values of an appointmentForm DTO
     * 
     * @param reservationRuleDTO
     *            the reservation rule object to fill in
     * @param appointmentForm
     *            the appointmentForm DTO

     */
    public static void fillInReservationRuleAdvancedParam( ReservationRuleDTO reservationRuleDTO, AppointmentFormDTO appointmentForm )
    {
    	reservationRuleDTO.setMaxCapacityPerSlot( appointmentForm.getMaxCapacityPerSlot( ) );
    	reservationRuleDTO.setMaxPeoplePerAppointment( appointmentForm.getMaxPeoplePerAppointment( ) );
    	reservationRuleDTO.setName( appointmentForm.getName( ));
    	reservationRuleDTO.setDescriptionRule( appointmentForm.getDescriptionRule( ));
    	reservationRuleDTO.setColor( appointmentForm.getColor( ));
    	reservationRuleDTO.setDurationAppointments(appointmentForm.getDurationAppointments( ));
    	reservationRuleDTO.setTimeEnd(appointmentForm.getTimeEnd( ));
    	reservationRuleDTO.setTimeStart(appointmentForm.getTimeStart( ));
    	
    	reservationRuleDTO.setIdForm( appointmentForm.getIdForm( ) );
    }
    /**
     * check if one of the weeks in the list is open on the FO calendar 
     * @param appointmentFormDTO the appointment form
     * @param listWeek the list of weekDefinition
     * @param locale the locale 
     * @return true if one of the weeks in the list is open on the FO calendar 
     */
    public static boolean weekIsOpenInFO( AppointmentFormDTO appointmentFormDTO, List<WeekDefinition> listWeek, Locale locale) {
    	        
       	if( appointmentFormDTO.getIsActive( ) ) 
       	{
	        Date startingValidityDate = appointmentFormDTO.getDateStartValidity( );
	    	LocalDate startingDateOfDisplay = LocalDate.now( );
	    		
	         if ( startingValidityDate != null && startingValidityDate.toLocalDate( ).isAfter( startingDateOfDisplay ) )
	         {
	             startingDateOfDisplay = startingValidityDate.toLocalDate( );
	         }
	         // Calculate the ending date of display with the nb weeks to display
	         // since today
	         // We calculate the number of weeks including the current week, so it
	         // will end to the (n) next sunday
	         LocalDate dateOfSunday = startingDateOfDisplay.with( WeekFields.of( locale ).dayOfWeek( ), DayOfWeek.SUNDAY.getValue( ) );
	         LocalDate endingDateOfDisplay = dateOfSunday.plusWeeks( (long) appointmentFormDTO.getNbWeeksToDisplay() - 1 );
	         Date endingValidityDate = appointmentFormDTO.getDateEndValidity( );
	         if (endingValidityDate != null && endingDateOfDisplay.isAfter( endingValidityDate.toLocalDate( ) ) )
	         {
	             endingDateOfDisplay = endingValidityDate.toLocalDate( );
	         }
	         if( startingDateOfDisplay.isAfter(endingDateOfDisplay)) {
	        	 
	        	 return false;
	         }
	    			    	
		    for( WeekDefinition week:listWeek ) {
		    		
		    	if( (week.getDateOfApply().isBefore( endingDateOfDisplay ) || week.getDateOfApply().isEqual( endingDateOfDisplay ))
		    		&& (week.getEndingDateOfApply().isAfter( startingDateOfDisplay ) || week.getEndingDateOfApply().isEqual( startingDateOfDisplay ))) {
		    			
		    		return true;
		    	}
		    }
    	  }
       	 	
       	return false;
    }
}
