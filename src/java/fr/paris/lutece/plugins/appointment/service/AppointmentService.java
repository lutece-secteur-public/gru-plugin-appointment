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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;

import fr.paris.lutece.plugins.appointment.service.listeners.AppointmentListenerManager;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFilterDTO;

import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

/**
 * Service class for an appointment
 * 
 * @author Laurent Payen
 *
 */
public final class AppointmentService
{


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
    public static Appointment buildAndCreateAppointment( AppointmentDTO appointmentDTO, User user, Slot slot )
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
        appointment.setNbPlaces( appointmentDTO.getNbBookedSeats( ) );
        appointment.setIdSlot( slot.getIdSlot( ) );
        appointment.setIdUser( user.getIdUser( ) );
        if ( appointment.getIdAppointment( ) == 0 )
        {
            appointment = AppointmentHome.create( appointment );
            AppointmentListenerManager.notifyListenersAppointmentCreated( appointment.getIdAppointment( ) );
        }
        else
        {
            AppLogService.info( "Update Appointment: " + appointment.getIdAppointment( ) + " on Slot: " + appointment.getIdSlot( ) );
            appointment = AppointmentHome.update( appointment );
            AppointmentListenerManager.notifyListenersAppointmentUpdated(appointment.getIdAppointment( ));

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
        appointmentDTO.setIdForm( appointment.getSlot( ).getIdForm( ) );
        appointmentDTO.setIdUser( appointment.getIdUser( ) );
        appointmentDTO.setIdSlot( appointment.getIdSlot( ) );
        appointmentDTO.setIdAppointment( appointment.getIdAppointment( ) );
        appointmentDTO.setFirstName( appointment.getUser( ).getFirstName( ) );
        appointmentDTO.setLastName( appointment.getUser( ).getLastName( ) );
        appointmentDTO.setEmail( appointment.getUser( ).getEmail( ) );
        appointmentDTO.setGuid( appointment.getUser( ).getGuid( ) );
        appointmentDTO.setReference( appointment.getReference( ) );
        LocalDateTime startingDateTime = appointment.getSlot( ).getStartingDateTime( );
        appointmentDTO.setStartingDateTime( startingDateTime );
        appointmentDTO.setDateOfTheAppointment( startingDateTime.toLocalDate( ).format( Utilities.getDateFormatter( ) ) );
        appointmentDTO.setStartingTime( startingDateTime.toLocalTime( ) );
        appointmentDTO.setEndingTime( appointment.getSlot( ).getEndingDateTime( ).toLocalTime( ) );
        appointmentDTO.setIsCancelled( appointment.getIsCancelled( ) );
        appointmentDTO.setNbBookedSeats( appointment.getNbPlaces( ) );
        SlotService.addDateAndTimeToSlot( appointment.getSlot( ) );
        appointmentDTO.setSlot( appointment.getSlot( ) );
        appointmentDTO.setUser( appointment.getUser( ) );
        if ( appointment.getIdAdminUser( ) != 0 )
        {
            AdminUser adminUser = AdminUserHome.findByPrimaryKey( appointment.getIdAdminUser( ) );
            if ( adminUser != null )
            {
                appointmentDTO.setAdminUser( new StringBuilder( adminUser.getFirstName( ) + org.apache.commons.lang3.StringUtils.SPACE
                        + adminUser.getLastName( ) ).toString( ) );
            }
        }
        else
        {
            appointmentDTO.setAdminUser( StringUtils.EMPTY );
        }
        appointmentDTO.setAdminUserCreate( appointment.getAdminUserCreate( ) );
        return appointmentDTO;
    }
    
    /**
     * Fill the appointment data transfer object with complementary appointment responses
     * @param appointmentDto
     *              The appointmentDTO object
     */
    public static void addAppointmentResponses( AppointmentDTO appointmentDto )
    {
        //Load the response list in the DTO
        appointmentDto.setListResponse( AppointmentResponseService.findListResponse( appointmentDto.getIdAppointment( ) ) );
    }
    
    

    /**
     * Delete an appointment (and update the number of remaining places of the related slot)
     * 
     * @param nIdAppointment
     *            the id of the appointment to delete
     */
    public static void deleteAppointment( int nIdAppointment )
    {
        Appointment appointmentToDelete = AppointmentHome.findByPrimaryKey( nIdAppointment );
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
        if ( !appointmentToDelete.getIsCancelled( ) )
        {
            SlotSafeService.updateRemaningPlacesWithAppointmentMovedDeletedOrCanceled( appointmentToDelete.getNbPlaces( ), appointmentToDelete.getIdSlot( ) );
        }
        // Need to delete also the responses linked to this appointment
        AppointmentResponseService.removeResponsesByIdAppointment( nIdAppointment );
        AppointmentService.deleteAppointment( appointmentToDelete );
    }

    /**
     * Delete an appointment
     * 
     * @param appointment
     *            the appointment to delete
     */
    private static void deleteAppointment( Appointment appointment )
    {
        AppointmentListenerManager.notifyListenersAppointmentRemoval( appointment.getIdAppointment( ) );
        AppointmentHome.delete( appointment.getIdAppointment( ) );
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
        // Get the old appointment in db
        Appointment oldAppointment = AppointmentService.findAppointmentById( appointment.getIdAppointment( ) );
        // If the update concerns a cancellation of the appointment
        if ( !oldAppointment.getIsCancelled( ) && appointment.getIsCancelled( ) )
        {
            // Need to update the nb remaining places of the related slot
            SlotSafeService.updateRemaningPlacesWithAppointmentMovedDeletedOrCanceled( appointment.getNbPlaces( ), appointment.getIdSlot( ) );
        }
        AppointmentHome.update( appointment );
        AppointmentListenerManager.notifyListenersAppointmentUpdated(appointment.getIdAppointment( ));

    }

    /**
     * Set the new number of remaining places (and potential) when an appointment is deleted or cancelled This new value must take in account the capacity of
     * the slot, in case of the slot was already over booked
     * 
     * @param nbPlaces
     *            the nb places taken of the appointment that we want to delete (or cancel, or move)
     * @param slot
     *            the related slot
     */
    @Deprecated
    public static void updateRemaningPlacesWithAppointmentMovedDeletedOrCanceled( int nbPlaces, Slot slot )
    {
    	SlotSafeService.updateRemaningPlacesWithAppointmentMovedDeletedOrCanceled( nbPlaces, slot.getIdSlot( ) );
       
    } 
    	
    /**
     * Save an appointment in database
     * 
     * @param appointmentDTO
     *            the appointment dto
     * @return the id of the appointment saved
     * @throws Exception 
     */
      public  static synchronized int saveAppointment( AppointmentDTO appointmentDTO ) 
      {
    	  return SlotSafeService.saveAppointment(appointmentDTO, null);
      
      }
     
}
