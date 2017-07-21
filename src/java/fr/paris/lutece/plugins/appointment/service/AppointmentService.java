package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.business.AppointmentFilter;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.service.listeners.AppointmentListenerManager;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.util.CryptoService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

/**
 * Service class for an appointment
 * 
 * @author Laurent Payen
 *
 */
public final class AppointmentService
{

    private static final String PROPERTY_REF_ENCRYPTION_ALGORITHM = "appointment.refEncryptionAlgorithm";
    private static final String CONSTANT_SHA256 = "SHA-256";
    private static final String PROPERTY_REF_SIZE_RANDOM_PART = "appointment.refSizeRandomPart";
    /**
     * Get the number of characters of the random part of appointment reference
     */
    private static final int CONSTANT_REF_SIZE_RANDOM_PART = 5;

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private AppointmentService( )
    {
    }

    /**
     * Find all the appointments of the slots given in parameter
     * 
     * @param listSlot
     *            the list of slots
     * @return a list of the appointments on these slots
     */
    public static List<Appointment> findListAppointmentByListSlot( List<Slot> listSlot )
    {
        List<Appointment> listAppointment = new ArrayList<>( );
        for ( Slot slot : listSlot )
        {
            listAppointment.addAll( AppointmentService.findListAppointmentBySlot( slot.getIdSlot( ) ) );
        }
        return listAppointment;
    }

    /**
     * Find the appointments of a slot
     * 
     * @param nIdSlot
     *            the slot Id
     * @return the appointments of the slot
     */
    public static List<Appointment> findListAppointmentBySlot( int nIdSlot )
    {
        return AppointmentHome.findByIdSlot( nIdSlot );
    }

    /**
     * Find the appointments of a user
     * 
     * @param nIdUser
     *            the user Id
     * @return the appointment of the user
     */
    public static List<Appointment> findListAppointmentByUserId( int nIdUser )
    {
        return AppointmentHome.findByIdUser( nIdUser );
    }

    /**
     * Find the appointments by form and that will be after a given date
     * 
     * @param nIdForm
     *            the form Id
     * @param startingDateTime
     *            the starting date
     * @return the appointments that matches the criteria
     */
    public static List<Appointment> findListAppointmentByIdFormAndAfterADateTime( int nIdForm, LocalDateTime startingDateTime )
    {
        return AppointmentHome.findByIdFormAndAfterADateTime( nIdForm, startingDateTime );
    }

    /**
     * Find the appointments by form
     * 
     * @param nIdForm
     *            the form Id
     * 
     * @return the appointments that matches the criteria
     */
    public static List<Appointment> findListAppointmentByIdForm( int nIdForm )
    {
        return AppointmentHome.findByIdForm( nIdForm );
    }

    /**
     * Save an appointment in database
     * 
     * @param appointmentDTO
     *            the appointment dto
     * @return the id of the appointment saved
     */
    public static int saveAppointment( AppointmentDTO appointmentDTO )
    {
        // if it's an update for modification of the date of the appointment
        if ( appointmentDTO.getIdAppointment( ) != 0 && appointmentDTO.getSlot( ).getIdSlot( ) != appointmentDTO.getIdSlot( ) )
        {
            // Need to update the old slot
            Slot oldSlot = SlotService.findSlotById( appointmentDTO.getIdSlot( ) );
            int oldNbRemainingPlaces = oldSlot.getNbRemainingPlaces( );
            oldSlot.setNbRemainingPlaces( oldNbRemainingPlaces + appointmentDTO.getNbBookedSeats( ) );
            int oldNbPotentialRemainingPlaces = oldSlot.getNbPotentialRemainingPlaces( );
            oldSlot.setNbPotentialRemainingPlaces( oldNbPotentialRemainingPlaces + appointmentDTO.getNbBookedSeats( ) );
            SlotService.updateSlot( oldSlot );
            // Need to remove the workflow resource to reload again the workflow
            // at the first step
            WorkflowService.getInstance( ).doRemoveWorkFlowResource( appointmentDTO.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE );
        }
        // Update of the remaining places of the slot
        Slot slot = appointmentDTO.getSlot( );
        int oldNbRemainingPLaces = slot.getNbRemainingPlaces( );
        int newNbRemainingPlaces = 0;
        if ( appointmentDTO.getIdAppointment( ) == 0 || appointmentDTO.getSlot( ).getIdSlot( ) != appointmentDTO.getIdSlot( ) )
        {
            newNbRemainingPlaces = oldNbRemainingPLaces - appointmentDTO.getNbBookedSeats( );
        }
        else
        {
            Appointment appointment = AppointmentService.findAppointmentById( appointmentDTO.getIdAppointment( ) );
            newNbRemainingPlaces = oldNbRemainingPLaces - appointmentDTO.getNbBookedSeats( ) + appointment.getNbPlaces( );
        }
        slot.setNbRemainingPlaces( newNbRemainingPlaces );

        int nbMaxPotentialBookedSeats = appointmentDTO.getNbMaxPotentialBookedSeats( );
        int oldNbPotentialRemaningPlaces = slot.getNbPotentialRemainingPlaces( );
        int effectiveBookedSeats = appointmentDTO.getNbBookedSeats( );
        int newPotentialRemaningPlaces = oldNbPotentialRemaningPlaces + nbMaxPotentialBookedSeats - effectiveBookedSeats;
        slot.setNbPotentialRemainingPlaces( newPotentialRemaningPlaces );

        slot = SlotService.saveSlot( slot );
        // Create or update the user
        User user = UserService.saveUser( appointmentDTO );
        // Create or update the appointment
        Appointment appointment = buildAndCreateAppointment( appointmentDTO, user, slot );
        String strEmailOrLastNamePlusFirstName = StringUtils.EMPTY;
        if ( StringUtils.isEmpty( user.getEmail( ) ) )
        {
            strEmailOrLastNamePlusFirstName = user.getLastName( ) + user.getFirstName( );
        }
        // Create a unique reference for a new appointment
        if ( appointmentDTO.getIdAppointment( ) == 0 )
        {
            String strReference = appointment.getIdAppointment( )
                    + CryptoService.encrypt( appointment.getIdAppointment( ) + strEmailOrLastNamePlusFirstName,
                            AppPropertiesService.getProperty( PROPERTY_REF_ENCRYPTION_ALGORITHM, CONSTANT_SHA256 ) ).substring( 0,
                            AppPropertiesService.getPropertyInt( PROPERTY_REF_SIZE_RANDOM_PART, CONSTANT_REF_SIZE_RANDOM_PART ) );
            appointment.setReference( strReference );
            AppointmentHome.update( appointment );
        }
        else
        {
            AppointmentResponseService.removeResponsesByIdAppointment( appointment.getIdAppointment( ) );
        }
        if ( CollectionUtils.isNotEmpty( appointmentDTO.getListResponse( ) ) )
        {
            for ( Response response : appointmentDTO.getListResponse( ) )
            {
                ResponseHome.create( response );
                AppointmentResponseService.insertAppointmentResponse( appointment.getIdAppointment( ), response.getIdResponse( ) );
            }
        }
        Form form = FormService.findFormLightByPrimaryKey( slot.getIdForm( ) );
        if ( form.getIdWorkflow( ) > 0 )
        {
            WorkflowService.getInstance( ).getState( appointment.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow( ),
                    form.getIdForm( ) );
            WorkflowService.getInstance( ).executeActionAutomatic( appointment.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE,
                    form.getIdWorkflow( ), form.getIdForm( ) );
        }
        return appointment.getIdAppointment( );
    }

    /**
     * Build and create in database an appointment from the dto
     * 
     * @param appointmentDTO
     *            the appointment dto
     * @param user
     *            the user
     * @param slot
     *            the slot
     * @return the appointment created
     */
    private static Appointment buildAndCreateAppointment( AppointmentDTO appointmentDTO, User user, Slot slot )
    {
        Appointment appointment = new Appointment( );
        if ( appointmentDTO.getIdAppointment( ) != 0 )
        {
            appointment = AppointmentService.findAppointmentById( appointmentDTO.getIdAppointment( ) );
        }
        appointment.setNbPlaces( appointmentDTO.getNbBookedSeats( ) );
        appointment.setIdSlot( slot.getIdSlot( ) );
        appointment.setIdUser( user.getIdUser( ) );
        if ( appointment.getIdAppointment( ) == 0 )
        {
            appointment = AppointmentHome.create( appointment );
        }
        else
        {
            appointment = AppointmentHome.update( appointment );
        }
        return appointment;
    }

    /**
     * Find an appointment by its primary key
     * 
     * @param nIdAppointment
     *            the appointment Id
     * @return the appointment
     */
    public static Appointment findAppointmentById( int nIdAppointment )
    {
        return AppointmentHome.findByPrimaryKey( nIdAppointment );
    }

    /**
     * Find an appointment by its reference
     * 
     * @param strReference
     *            the appointment Reference
     * @return the appointment
     */
    public static Appointment findAppointmentByReference( String strReference )
    {
        return AppointmentHome.findByReference( strReference );
    }

    /**
     * Find a list of appointments matching the filter
     * 
     * @param appointmentFilter
     *            the filter
     * @return a list of appointments
     */
    public static List<AppointmentDTO> findListAppointmentsDTOByFilter( AppointmentFilter appointmentFilter )
    {
        List<AppointmentDTO> listAppointmentsDTO = new ArrayList<>( );
        for ( Appointment appointment : AppointmentHome.findByFilter( appointmentFilter ) )
        {
            listAppointmentsDTO.add( buildAppointmentDTO( appointment ) );
        }
        return listAppointmentsDTO;
    }

    public static List<Appointment> findListAppointmentsByFilter( AppointmentFilter appointmentFilter )
    {
        return AppointmentHome.findByFilter( appointmentFilter );
    }

    /**
     * Build an appointment dto from an appointment business object
     * 
     * @param appointment
     *            the appointment business object
     * @return the appointment DTO
     */
    private static AppointmentDTO buildAppointmentDTO( Appointment appointment )
    {
        AppointmentDTO appointmentDTO = new AppointmentDTO( );
        appointmentDTO.setIdForm( appointment.getSlot( ).getIdForm( ) );
        appointmentDTO.setIdUser( appointment.getIdUser( ) );
        appointmentDTO.setIdSlot( appointment.getIdSlot( ) );
        appointmentDTO.setIdAppointment( appointment.getIdAppointment( ) );
        appointmentDTO.setFirstName( appointment.getUser( ).getFirstName( ) );
        appointmentDTO.setLastName( appointment.getUser( ).getLastName( ) );
        appointmentDTO.setEmail( appointment.getUser( ).getEmail( ) );
        LocalDateTime startingDateTime = appointment.getSlot( ).getStartingDateTime( );
        appointmentDTO.setStartingDateTime( startingDateTime );
        appointmentDTO.setDateOfTheAppointment( startingDateTime.toLocalDate( ).format( Utilities.getFormatter( ) ) );
        appointmentDTO.setStartingTime( startingDateTime.toLocalTime( ) );
        appointmentDTO.setEndingTime( appointment.getSlot( ).getEndingDateTime( ).toLocalTime( ) );
        appointmentDTO.setIsCancelled( appointment.getIsCancelled( ) );
        appointmentDTO.setNbBookedSeats( appointment.getNbPlaces( ) );
        SlotService.addDateAndTimeToSlot( appointment.getSlot( ) );
        appointmentDTO.setSlot( appointment.getSlot( ) );
        appointmentDTO.setUser( appointment.getUser( ) );
        return appointmentDTO;
    }

    /**
     * Delete an appointment (and update the number of remaining places of the related slot)
     * 
     * @param nIdAppointment
     *            the id of the appointment to delete
     */
    public static void deleteAppointment( int nIdAppointment, AdminUser user ) throws AccessDeniedException
    {
        AppointmentListenerManager.notifyListenersAppointmentRemoval( nIdAppointment );
        Appointment appointmentToDelete = AppointmentHome.findByPrimaryKey( nIdAppointment );
        Slot slotOfTheAppointmentToDelete = SlotService.findSlotById( appointmentToDelete.getIdSlot( ) );
        if ( !RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, Integer.toString( slotOfTheAppointmentToDelete.getIdForm( ) ),
                AppointmentResourceIdService.PERMISSION_DELETE_APPOINTMENT, user ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_DELETE_APPOINTMENT );
        }
        if ( WorkflowService.getInstance( ).isAvailable( ) )
        {
            WorkflowService.getInstance( ).doRemoveWorkFlowResource( nIdAppointment, Appointment.APPOINTMENT_RESOURCE_TYPE );
        }
        int nbRemainingPlaces = slotOfTheAppointmentToDelete.getNbRemainingPlaces( );
        int nbPotentialRemaningPlaces = slotOfTheAppointmentToDelete.getNbPotentialRemainingPlaces( );
        int nbNewRemainingPlaces = nbRemainingPlaces + appointmentToDelete.getNbPlaces( );
        slotOfTheAppointmentToDelete.setNbRemainingPlaces( nbNewRemainingPlaces );
        slotOfTheAppointmentToDelete.setNbPotentialRemainingPlaces( nbPotentialRemaningPlaces + appointmentToDelete.getNbPlaces( ) );
        SlotService.updateSlot( slotOfTheAppointmentToDelete );
        // Need to delete also the responses linked to this appointment
        AppointmentResponseService.removeResponsesByIdAppointment( nIdAppointment );
        AppointmentHome.delete( nIdAppointment );
    }

    /**
     * Build an appointment DTO from the id of an appointment business object
     * 
     * @param nIdAppointment
     *            the id of the appointment
     * @return the appointment DTO
     */
    public static AppointmentDTO buildAppointmentDTOFromIdAppointment( int nIdAppointment )
    {
        Appointment appointment = AppointmentService.findAppointmentById( nIdAppointment );
        User user = UserService.findUserById( appointment.getIdUser( ) );
        Slot slot = SlotService.findSlotById( appointment.getIdSlot( ) );
        appointment.setUser( user );
        appointment.setSlot( slot );
        return buildAppointmentDTO( appointment );
    }

    /**
     * Update an appointment in database
     * 
     * @param appointment
     *            the appointment to update
     */
    public static void updateAppointment( Appointment appointment )
    {
        AppointmentHome.update( appointment );
    }
}
