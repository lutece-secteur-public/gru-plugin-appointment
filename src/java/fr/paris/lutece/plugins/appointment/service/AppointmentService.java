/*
 * Copyright (c) 2002-2021, City of Paris
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.plugins.appointment.service.listeners.AppointmentListenerManager;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFilterDTO;

import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.util.CryptoService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.sql.TransactionManager;

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
    private static final String CONSTANT_SEPARATOR = "$";
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
        List<Integer> listIdSlot = listSlot.stream( ).map( Slot::getIdSlot ).collect( Collectors.toList( ) );

        return AppointmentHome.findByListIdSlot( listIdSlot );
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
     * Find the appointments of a user by guid
     * 
     * @param nIdUser
     *            the user guid
     * @return the appointment of the guid
     */
    public static List<Appointment> findListAppointmentByUserGuid( String strGuidUser )
    {
        List<Appointment> listAppointment = AppointmentHome.findByGuidUser( strGuidUser );
        for ( Appointment appointment : listAppointment )
        {
            List<Slot> listSlot = SlotService.findListSlotByIdAppointment( appointment.getIdAppointment( ) );
            appointment.setSlot( listSlot );

        }
        return listAppointment;

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
    public static Appointment buildAndCreateAppointment( AppointmentDTO appointmentDTO, User user )
    {
        Appointment appointment = new Appointment( );
        if ( appointmentDTO.getIdAppointment( ) != 0 )
        {
            appointment = AppointmentService.findAppointmentById( appointmentDTO.getIdAppointment( ) );
        }
        if ( appointmentDTO.getIdAdminUser( ) != 0 )
        {
            appointment.setIdAdminUser( appointmentDTO.getIdAdminUser( ) );
        }
        if ( appointmentDTO.getAdminUserCreate( ) != null )
        {
            appointment.setAdminUserCreate( appointmentDTO.getAdminUserCreate( ) );
        }
        appointment.setListAppointmentSlot( appointmentDTO.getListAppointmentSlot( ) );
        appointment.setNbPlaces( appointmentDTO.getNbBookedSeats( ) );
        appointment.setIdUser( user.getIdUser( ) );

        if ( appointment.getIdAppointment( ) == 0 )
        {
            String strEmailLastNameFirstName = new StringJoiner( StringUtils.SPACE ).add( user.getEmail( ) ).add( CONSTANT_SEPARATOR )
                    .add( user.getLastName( ) ).add( CONSTANT_SEPARATOR ).add( user.getFirstName( ) ).toString( );
            String strReference = appointment.getIdAppointment( ) + CryptoService
                    .encrypt( appointment.getIdAppointment( ) + strEmailLastNameFirstName,
                            AppPropertiesService.getProperty( PROPERTY_REF_ENCRYPTION_ALGORITHM, CONSTANT_SHA256 ) )
                    .substring( 0, AppPropertiesService.getPropertyInt( PROPERTY_REF_SIZE_RANDOM_PART, CONSTANT_REF_SIZE_RANDOM_PART ) );

            Form form = FormService.findFormLightByPrimaryKey( appointmentDTO.getIdForm( ) );
            if ( StringUtils.isNotEmpty( form.getReference( ) ) )
            {
                strReference = form.getReference( ) + strReference;
            }
            appointment.setReference( strReference );
            appointment = AppointmentHome.create( appointment );
            AppointmentListenerManager.notifyListenersAppointmentCreated( appointment.getIdAppointment( ) );
        }
        else
        {
            appointment = AppointmentHome.update( appointment );
            AppointmentListenerManager.notifyListenersAppointmentUpdated( appointment.getIdAppointment( ) );

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
    public static List<AppointmentDTO> findListAppointmentsDTOByFilter( AppointmentFilterDTO appointmentFilter )
    {
        List<AppointmentDTO> listAppointmentsDTO = new ArrayList<>( );
        for ( Appointment appointment : AppointmentHome.findByFilter( appointmentFilter ) )
        {
            listAppointmentsDTO.add( buildAppointmentDTO( appointment ) );
        }
        return listAppointmentsDTO;
    }

    /**
     * Find a list of appointments matching the filter
     * 
     * @param appointmentFilter
     * @return a list of appointments
     */
    public static List<Appointment> findListAppointmentsByFilter( AppointmentFilterDTO appointmentFilter )
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
        appointmentDTO.setIdForm( appointment.getSlot( ).get( 0 ).getIdForm( ) );
        appointmentDTO.setIdUser( appointment.getIdUser( ) );
        appointmentDTO.setListAppointmentSlot( appointment.getListAppointmentSlot( ) );
        appointmentDTO.setIdAppointment( appointment.getIdAppointment( ) );
        appointmentDTO.setFirstName( appointment.getUser( ).getFirstName( ) );
        appointmentDTO.setLastName( appointment.getUser( ).getLastName( ) );
        appointmentDTO.setEmail( appointment.getUser( ).getEmail( ) );
        appointmentDTO.setGuid( appointment.getUser( ).getGuid( ) );
        appointmentDTO.setReference( appointment.getReference( ) );
        LocalDateTime startingDateTime = AppointmentUtilities.getStartingDateTime( appointment );
        LocalDateTime endingDateTime = AppointmentUtilities.getEndingDateTime( appointment );
        appointmentDTO.setStartingDateTime( startingDateTime );
        appointmentDTO.setEndingDateTime( endingDateTime );
        appointmentDTO.setDateOfTheAppointment( startingDateTime.toLocalDate( ).format( Utilities.getFormatter( ) ) );
        appointmentDTO.setStartingTime( startingDateTime.toLocalTime( ) );
        appointmentDTO.setEndingTime( endingDateTime.toLocalTime( ) );
        appointmentDTO.setIsCancelled( appointment.getIsCancelled( ) );
        appointmentDTO.setNbBookedSeats( appointment.getNbPlaces( ) );
        for ( Slot slt : appointment.getSlot( ) )
        {

            SlotService.addDateAndTimeToSlot( slt );
        }
        appointmentDTO.setSlot( appointment.getSlot( ) );
        appointmentDTO.setUser( appointment.getUser( ) );
        if ( appointment.getIdAdminUser( ) != 0 )
        {
            AdminUser adminUser = AdminUserHome.findByPrimaryKey( appointment.getIdAdminUser( ) );
            if ( adminUser != null )
            {
                appointmentDTO.setAdminUser(
                        new StringBuilder( adminUser.getFirstName( ) + org.apache.commons.lang3.StringUtils.SPACE + adminUser.getLastName( ) ).toString( ) );
            }
        }
        else
        {
            appointmentDTO.setAdminUser( StringUtils.EMPTY );
        }
        appointmentDTO.setAdminUserCreate( appointment.getAdminUserCreate( ) );
        appointmentDTO.setDateAppointmentTaken( appointment.getDateAppointmentTaken( ) );
        return appointmentDTO;
    }

    /**
     * Fill the appointment data transfer object with complementary appointment responses
     * 
     * @param appointmentDto
     *            The appointmentDTO object
     */
    public static void addAppointmentResponses( AppointmentDTO appointmentDto )
    {
        // Load the response list in the DTO
        appointmentDto.setListResponse( AppointmentResponseService.findListResponse( appointmentDto.getIdAppointment( ) ) );
    }

    /**
     * Delete an appointment (and update the number of remaining places of the related slot)
     * 
     * @param nIdAppointment
     *            the id of the appointment to delete
     * @throws Exception
     *             the exception
     */
    public static void deleteAppointment( int nIdAppointment )
    {
        TransactionManager.beginTransaction( AppointmentPlugin.getPlugin( ) );
        try
        {
            Appointment appointmentToDelete = AppointmentHome.findByPrimaryKey( nIdAppointment );
            deleteWorkflowResource( nIdAppointment );
            if ( !appointmentToDelete.getIsCancelled( ) )
            {
                for ( AppointmentSlot appSlot : appointmentToDelete.getListAppointmentSlot( ) )
                {
                    // Need to update the nb remaining places of the related slot
                    SlotSafeService.updateRemaningPlacesWithAppointmentMovedDeletedOrCanceled( appSlot.getNbPlaces( ), appSlot.getIdSlot( ) );
                }

            }
            // Need to delete also the responses linked to this appointment
            AppointmentResponseService.removeResponsesByIdAppointment( nIdAppointment );
            AppointmentService.deleteAppointment( appointmentToDelete );
            UserHome.delete( appointmentToDelete.getIdUser( ) );
            TransactionManager.commitTransaction( AppointmentPlugin.getPlugin( ) );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( AppointmentPlugin.getPlugin( ) );
            AppLogService.error( "Error delete appointment " + e.getMessage( ), e );
            throw new AppException( e.getMessage( ), e );
        }
    }

    private static void deleteWorkflowResource( int nIdAppointment )
    {
        if ( WorkflowService.getInstance( ).isAvailable( ) )
        {
            try
            {
                WorkflowService.getInstance( ).doRemoveWorkFlowResource( nIdAppointment, Appointment.APPOINTMENT_RESOURCE_TYPE );
            }
            catch( Exception e )
            {
                AppLogService.error( "Error Workflow", e );
            }
        }
    }

    /**
     * Delete an appointment
     * 
     * @param appointment
     *            the appointment to delete
     */
    private static void deleteAppointment( Appointment appointment )
    {
        AppointmentHome.delete( appointment.getIdAppointment( ) );
        AppointmentListenerManager.notifyListenersAppointmentRemoval( appointment.getIdAppointment( ) );

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
        List<Slot> listSlot = SlotService.findListSlotByIdAppointment( appointment.getIdAppointment( ) );
        appointment.setSlot( listSlot );
        appointment.setUser( user );
        return buildAppointmentDTO( appointment );
    }

    /**
     * Build an appointment DTO from the id of an appointment business object
     * 
     * @param nIdAppointment
     *            the id of the appointment
     * @return the appointment DTO
     */
    public static AppointmentDTO buildAppointmentDTOFromRefAppointment( String refAppointment )
    {
        Appointment appointment = AppointmentService.findAppointmentByReference( refAppointment );

        User user = UserService.findUserById( appointment.getIdUser( ) );
        List<Slot> listSlot = SlotService.findListSlotByIdAppointment( appointment.getIdAppointment( ) );
        appointment.setSlot( listSlot );
        appointment.setUser( user );

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

        // Get the old appointment in db
        Appointment oldAppointment = AppointmentService.findAppointmentById( appointment.getIdAppointment( ) );
        // If the update concerns a cancellation of the appointment
        TransactionManager.beginTransaction( AppointmentPlugin.getPlugin( ) );
        try
        {
            if ( !oldAppointment.getIsCancelled( ) && appointment.getIsCancelled( ) )
            {
                for ( AppointmentSlot appSlot : appointment.getListAppointmentSlot( ) )
                {
                    // Need to update the nb remaining places of the related slot
                    SlotSafeService.updateRemaningPlacesWithAppointmentMovedDeletedOrCanceled( appSlot.getNbPlaces( ), appSlot.getIdSlot( ) );
                }
            }
            AppointmentHome.update( appointment );
            TransactionManager.commitTransaction( AppointmentPlugin.getPlugin( ) );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( AppointmentPlugin.getPlugin( ) );
            AppLogService.error( "Error update appointment " + e.getMessage( ), e );
            throw new AppException( e.getMessage( ), e );
        }
        AppointmentListenerManager.notifyListenersAppointmentUpdated( appointment.getIdAppointment( ) );

    }

    /**
     * Save an appointment in database
     * 
     * @param appointmentDTO
     *            the appointment dto
     * @return the id of the appointment saved
     * @throws Exception
     */
    public static int saveAppointment( AppointmentDTO appointmentDTO )
    {
        return SlotSafeService.saveAppointment( appointmentDTO, null );

    }

    /**
     * Save an appointment in database
     * 
     * @param appointmentDTO
     *            the appointment dto
     * @return the id of the appointment saved
     * @throws Exception
     */
    public static int saveAppointment( AppointmentDTO appointmentDTO, HttpServletRequest request )
    {
        return SlotSafeService.saveAppointment( appointmentDTO, request );

    }

    public static void buildListAppointmentSlot( AppointmentDTO appointmentDTO )
    {

        List<AppointmentSlot> listApptSlot = new ArrayList<>( );
        int nIdAppointment = appointmentDTO.getIdAppointment( );
        int nNumberPlace = appointmentDTO.getNbBookedSeats( );
        List<Slot> listSlot = appointmentDTO.getSlot( );

        if ( listSlot.size( ) > 1 )
        {

            nNumberPlace = 1;
            listSlot.sort( ( slot1, slot2 ) -> slot1.getStartingDateTime( ).compareTo( slot2.getStartingDateTime( ) ) );
        }
        for ( Slot slot : listSlot )
        {

            AppointmentSlot apptSlot = new AppointmentSlot( );
            apptSlot.setIdAppointment( nIdAppointment );
            apptSlot.setIdSlot( slot.getIdSlot( ) );

            apptSlot.setNbPlaces( nNumberPlace );
            listApptSlot.add( apptSlot );
        }
        appointmentDTO.setListAppointmentSlot( listApptSlot );
    }

}
