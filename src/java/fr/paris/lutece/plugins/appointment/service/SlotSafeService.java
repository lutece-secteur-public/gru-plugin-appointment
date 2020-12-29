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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.slot.Period;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.slot.SlotHome;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.exception.AppointmentSavedException;
import fr.paris.lutece.plugins.appointment.exception.SlotFullException;
import fr.paris.lutece.plugins.appointment.service.listeners.SlotListenerManager;
import fr.paris.lutece.plugins.appointment.service.lock.TimerForLockOnSlot;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.sql.TransactionManager;

public final class SlotSafeService
{

    private static final ConcurrentMap<Integer, Lock> _listSlot = new ConcurrentHashMap<>( );
    private static final ConcurrentMap<Integer, Object> _lockFormId = new ConcurrentHashMap<>( );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private SlotSafeService( )
    {
    }

    /**
     * Get the slot in memory
     * 
     * @return Map of slot
     */
    public static Map<Integer, Lock> getListSlotInMemory( )
    {

        return _listSlot;
    }

    /**
     * get lock for slot
     * 
     * @param nIdSlot
     *            the Id Slot
     * @return return the lock
     */
    public static Lock getLockOnSlot( int nIdSlot )
    {
        if ( nIdSlot == 0 )
        {
            return new ReentrantLock( );
        }
        _listSlot.putIfAbsent( nIdSlot, new ReentrantLock( ) );
        return _listSlot.get( nIdSlot );
    }

    /**
     * remove slot in map memory
     * 
     * @param nIdSlot
     *            the Id Slot
     */
    public static void removeSlotInMemory( int nIdSlot )
    {

        _listSlot.remove( nIdSlot );
    }

    /**
     * get lock for form
     * 
     * @param nIdform
     *            Id from
     * @return return lock
     */
    private static Object getLockOnForm( int nIdform )
    {
        _lockFormId.putIfAbsent( nIdform, new Object( ) );
        return _lockFormId.get( nIdform );
    }

    /**
     * Create slot
     * 
     * @param slot
     * @return slot
     */
    public static Slot createSlot( Slot slot )
    {
        Object formLock = getLockOnForm( slot.getIdForm( ) );
        synchronized( formLock )
        {
            Slot slotSaved = null;
            HashMap<LocalDateTime, Slot> slotInDbMap = SlotService.buildMapSlotsByIdFormAndDateRangeWithDateForKey( slot.getIdForm( ),
                    slot.getStartingDateTime( ), slot.getEndingDateTime( ) );
            if ( !slotInDbMap.isEmpty( ) )
            {
                slotSaved = slotInDbMap.get( slot.getStartingDateTime( ) );
            }
            else
            {

                slotSaved = SlotHome.create( slot );
                SlotListenerManager.notifyListenersSlotCreation( slot.getIdSlot( ) );
            }

            return slotSaved;

        }
    }

    /**
     * 
     * Increment max capacity
     * 
     * @param nIdForm
     *            the Id form
     * @param nIncrementingValue
     *            the incrementing value
     * @param startindDateTime
     *            the starting date Time
     * @param endingDateTime
     *            the ending Date time
     * @param lace
     *            the lace
     */
    public static void incrementMaxCapacity( int nIdForm, int nIncrementingValue, LocalDateTime startindDateTime, LocalDateTime endingDateTime, boolean lace )
    {
        int index = 0;
        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        Map<WeekDefinition, ReservationRule> mapReservationRule = ReservationRuleService.findAllReservationRule( nIdForm, listWeekDefinition );
           List<Slot> listSlot = SlotService.buildListSlot( nIdForm, mapReservationRule, startindDateTime.toLocalDate( ), endingDateTime.toLocalDate( ) );
        listSlot = listSlot.stream( )
                .filter( slt -> slt.getEndingDateTime( ).isBefore( endingDateTime ) && slt.getEndingDateTime( ).isAfter( startindDateTime ) )
                .collect( Collectors.toList( ) );

        TransactionManager.beginTransaction( AppointmentPlugin.getPlugin( ) );
        try
        {
            for ( Slot slot : listSlot )
            {
                if ( !lace )
                {
                    incrementMaxCapacity( nIncrementingValue, slot );
                }
                else
                {
                    if ( index % 2 == 0 )
                    {
                        incrementMaxCapacity( nIncrementingValue, slot );
                    }
                    index++;
                }
            }
            TransactionManager.commitTransaction( AppointmentPlugin.getPlugin( ) );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( AppointmentPlugin.getPlugin( ) );
            AppLogService.error( "Error update max capacity " + e.getMessage( ), e );
            throw new AppException( e.getMessage( ), e );
        }

    }

    /**
     * Incrementing max capacity
     * 
     * @param nIncrementingValue
     *            the incrementing value
     * @param slot
     *            the slot
     */
    private static void incrementMaxCapacity( int nIncrementingValue, Slot slot )
    {
        Slot editSlot = null;

        if ( slot.getIdSlot( ) == 0 )
        {
            editSlot = createSlot( slot );
        }
        else
        {
            editSlot = slot;
        }
        Lock lock = getLockOnSlot( editSlot.getIdSlot( ) );
        lock.lock( );
        try
        {
            editSlot = SlotService.findSlotById( editSlot.getIdSlot( ) );

            editSlot.setMaxCapacity( editSlot.getMaxCapacity( ) + nIncrementingValue );
            editSlot.setNbPotentialRemainingPlaces( editSlot.getNbPotentialRemainingPlaces( ) + nIncrementingValue );
            editSlot.setNbRemainingPlaces( editSlot.getNbRemainingPlaces( ) + nIncrementingValue );
            editSlot.setIsSpecific( SlotService.isSpecificSlot( editSlot ) );
            saveSlot( editSlot );
        }
        finally
        {

            lock.unlock( );
        }

    }

    /**
     * Update potential remaining places
     * 
     * @param nbPotentialRemainingPlaces
     *            the nbPotentialRemainingPlaces
     * @param nIdSlot
     *            the is Slot
     * @param timer
     *            the timer
     */
    public static void incrementPotentialRemainingPlaces( int nbPotentialRemainingPlaces, int nIdSlot, TimerForLockOnSlot timer )
    {

        Lock lock = getLockOnSlot( nIdSlot );
        lock.lock( );
        try
        {
            Slot slot = SlotService.findSlotById( nIdSlot );
            if ( timer != null && !timer.isCancelled( ) && slot != null )
            {

                int nNewPotentialRemainingPlaces = slot.getNbPotentialRemainingPlaces( ) + nbPotentialRemainingPlaces;
                slot.setNbPotentialRemainingPlaces( nNewPotentialRemainingPlaces );
                SlotHome.updatePotentialRemainingPlaces( nNewPotentialRemainingPlaces, nIdSlot );
                SlotListenerManager.notifyListenersSlotChange( slot.getIdSlot( ) );

            }
        }
        finally
        {

            lock.unlock( );
        }
    }

    /**
     * Update potential remaining places
     * 
     * @param nbPotentialRemainingPlaces
     *            the nbPotentialRemainingPlaces
     * @param nIdSlot
     *            the is Slot
     */
    public static void decrementPotentialRemainingPlaces( int nbPotentialRemainingPlaces, int nIdSlot )
    {

        Lock lock = getLockOnSlot( nIdSlot );
        lock.lock( );
        try
        {
            Slot slot = SlotService.findSlotById( nIdSlot );
            if ( slot != null )
            {
                int nNewPotentialRemainingPlaces = slot.getNbPotentialRemainingPlaces( ) - nbPotentialRemainingPlaces;
                slot.setNbPotentialRemainingPlaces( nNewPotentialRemainingPlaces );
                SlotHome.updatePotentialRemainingPlaces( nNewPotentialRemainingPlaces, nIdSlot );
                SlotListenerManager.notifyListenersSlotChange( slot.getIdSlot( ) );

            }

        }
        finally
        {

            lock.unlock( );
        }

    }

    /**
     * Save a slot in database
     * 
     * @param slot
     *            the slot to save
     * @return the slot saved
     */
    public static int saveAppointment( AppointmentDTO appointmentDTO, HttpServletRequest request )
    {
        boolean bIsUpdate = false;
        if ( appointmentDTO.getIsSaved( ) )
        {

            throw new AppointmentSavedException( "Appointment is already saved " );
        }

        AppointmentService.buildListAppointmentSlot( appointmentDTO );
        TransactionManager.beginTransaction( AppointmentPlugin.getPlugin( ) );
        try
        {
            User user = UserService.saveUser( appointmentDTO );
            saveSlots( appointmentDTO, bIsUpdate );
            // Create or update the appointment
            Appointment appointment = AppointmentService.buildAndCreateAppointment( appointmentDTO, user );                       
            if( appointmentDTO.getIdAppointment( ) != 0 )
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

            Form form = FormService.findFormLightByPrimaryKey( appointmentDTO.getIdForm( ) );
            if ( form.getIdWorkflow( ) > 0 )
            {
                WorkflowService.getInstance( ).getState( appointment.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow( ),
                        form.getIdForm( ) );
                WorkflowService.getInstance( ).executeActionAutomatic( appointment.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE,
                        form.getIdWorkflow( ), form.getIdForm( ), null );
            }

            TransactionManager.commitTransaction( AppointmentPlugin.getPlugin( ) );
            appointmentDTO.setIdAppointment( appointment.getIdAppointment( ) );
            appointmentDTO.setIsSaved( true );
            if ( request != null )
            {
                for ( AppointmentSlot apptSlot : appointmentDTO.getListAppointmentSlot( ) )
                {

                    AppointmentUtilities.killTimer( request, apptSlot.getIdSlot( ) );
                }
            }
            return appointment.getIdAppointment( );

        }
        catch( Exception e )
        {
            TransactionManager.rollBack( AppointmentPlugin.getPlugin( ) );
            AppLogService.error( "Error Save appointment " + e.getMessage( ), e );
            throw new SlotFullException( e.getMessage( ), e );
        }
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
    public static void updateRemaningPlacesWithAppointmentMovedDeletedOrCanceled( int nbPlaces, int nIdSlot )
    {
        // The capacity of the slot (that can be less than the number of places
        // taken on the slot --> overbook)

        Lock lock = getLockOnSlot( nIdSlot );
        lock.lock( );
        try
        {
            Slot slot = SlotService.findSlotById( nIdSlot );
            if ( slot != null )
            {
                int nMaxCapacity = slot.getMaxCapacity( );
                // The old remaining places of the slot (before we delete or cancel or move the
                // appointment
                int nOldRemainingPlaces = slot.getNbRemainingPlaces( );
                int nOldPotentialRemaningPlaces = slot.getNbPotentialRemainingPlaces( );
                int nOldPlacesTaken = slot.getNbPlacesTaken( );
                int nNewPlacesTaken = nOldPlacesTaken - nbPlaces;
                // The new value of the remaining places of the slot is the minimal
                // value between :
                // - the minimal value between the potentially new max capacity and the old remaining places plus the number of places released by the
                // appointment
                // - and the capacity of the slot minus the new places taken on the slot (0 if negative)
                int nNewRemainingPlaces = Math.min( Math.min( nMaxCapacity, nOldRemainingPlaces + nbPlaces ), ( nMaxCapacity - nNewPlacesTaken ) );

                int nNewPotentialRemainingPlaces = Math.min( Math.min( nMaxCapacity, nOldPotentialRemaningPlaces + nbPlaces ),
                        ( nMaxCapacity - nNewPlacesTaken ) );

                slot.setNbRemainingPlaces( nNewRemainingPlaces );
                slot.setNbPotentialRemainingPlaces( nNewPotentialRemainingPlaces );
                slot.setNbPlacestaken( nNewPlacesTaken );
                updateSlot( slot );
            }
        }
        finally
        {

            lock.unlock( );
        }

    }

    /**
     * Update a slot in database and possibly all the slots after (if the ending hour has changed, all the next slots are impacted in case of the user decide to
     * shift the next slots)
     * 
     * @param slot
     *            the slot to update
     * @param bEndingTimeHasChanged
     *            true if the ending time has changed
     * @param previousEndingTime
     *            the previous ending time
     * @param bShifSlot
     *            true if the user has decided to shift the next slots
     */
    public static void updateSlot( Slot slot, boolean bEndingTimeHasChanged, LocalTime previousEndingTime, boolean bShifSlot )
    {

        slot.setIsSpecific( SlotService.isSpecificSlot( slot ) );
        if ( bEndingTimeHasChanged )
        {
            // If we don't want to shift the next slots
            if ( !bShifSlot )
            {
                updateSlotWithoutShift( slot );
            }
            else
            {
                // We want to shift the next slots at the end of the current
                // slot
                updateSlotWithShift( slot, previousEndingTime );
            }
        }
        else
        {
            // The ending time of the slot has not changed
            // If it's an update of an existing slot
            if ( slot.getIdSlot( ) != 0 )
            {
                updateRemainingPlaces( slot );
            }
            saveSlot( slot );
        }

    }

    /**
     * Update the current slot and don't shift the next slots
     * 
     * @param slot
     *            the current slot
     */
    private static void updateSlotWithoutShift( Slot slot )
    {
        List<Slot> listSlotToCreate = new ArrayList<>( );
        // Need to get all the slots until the new end of this slot
        List<Slot> listSlotToDelete = SlotService.findSlotsByIdFormAndDateRange( slot.getIdForm( ), slot.getStartingDateTime( ).plusMinutes( 1 ),
                slot.getEndingDateTime( ) );
        SlotService.deleteListSlots( listSlotToDelete );
        // Get the list of slot after the modified slot
        HashMap<LocalDateTime, Slot> mapNextSlot = SlotService.buildMapSlotsByIdFormAndDateRangeWithDateForKey( slot.getIdForm( ), slot.getEndingDateTime( ),
                slot.getDate( ).atTime( LocalTime.MAX ) );
        List<LocalDateTime> listStartingDateTimeNextSlot = new ArrayList<>( mapNextSlot.keySet( ) );
        // Get the next date time slot
        LocalDateTime nextStartingDateTime = null;
        if ( CollectionUtils.isNotEmpty( listStartingDateTimeNextSlot ) )
        {
            nextStartingDateTime = Utilities.getClosestDateTimeInFuture( listStartingDateTimeNextSlot, slot.getEndingDateTime( ) );
        }
        else
        {
            LocalDate dateOfSlot = slot.getDate( );
            ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( slot.getIdForm( ), dateOfSlot );
            WorkingDay workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( reservationRule.getListWorkingDay( ), dateOfSlot.getDayOfWeek( ) );
            // No slot after this one.
            // Need to compute between the end of this slot and the next
            // time slot
            if ( workingDay != null )
            {
                List<TimeSlot> nextTimeSlots = TimeSlotService.getNextTimeSlotsInAListOfTimeSlotAfterALocalTime( workingDay.getListTimeSlot( ),
                        slot.getEndingTime( ) );
                if ( CollectionUtils.isNotEmpty( nextTimeSlots ) )
                {
                    Optional<TimeSlot> optTimeSlot = nextTimeSlots.stream( ).min( ( t1, t2 ) -> t1.getStartingTime( ).compareTo( t2.getStartingTime( ) ) );
                    if ( optTimeSlot.isPresent( ) )
                    {
                        nextStartingDateTime = optTimeSlot.get( ).getStartingTime( ).atDate( dateOfSlot );
                    }
                }
            }
            else
            {
                // This is not a working day
                // Generated the new slots at the end of the modified
                // slot
                listSlotToCreate.addAll( generateListSlotToCreateAfterATime( slot.getEndingDateTime( ), slot.getIdForm( ) ) );
            }
        }
        // Need to create a slot between these two dateTime
        if ( nextStartingDateTime != null && !slot.getEndingDateTime( ).isEqual( nextStartingDateTime ) )
        {
            Slot slotToCreate = SlotService.buildSlot( slot.getIdForm( ), new Period( slot.getEndingDateTime( ), nextStartingDateTime ), slot.getMaxCapacity( ),
                    slot.getMaxCapacity( ), slot.getMaxCapacity( ), 0, Boolean.FALSE, Boolean.TRUE );
            listSlotToCreate.add( slotToCreate );
        }
        // If it's an update of an existing slot
        if ( slot.getIdSlot( ) != 0 )
        {
            updateRemainingPlaces( slot );
        }
        saveSlot( slot );
        createListSlot( listSlotToCreate );
    }

    /**
     * update the current slot and shift the next slots at the end of the current slot
     * 
     * @param slot
     *            the current slot
     * @param previousEndingTime
     *            the previous ending time of the current slot
     */
    private static void updateSlotWithShift( Slot slot, LocalTime previousEndingTime )
    {
        // We want to shift all the next slots
        LocalDate dateOfSlot = slot.getDate( );
        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( slot.getIdForm( ) );
        Map<WeekDefinition, ReservationRule> mapReservationRule = ReservationRuleService.findAllReservationRule( slot.getIdForm( ), listWeekDefinition );
       
        // Build or get all the slots of the day
        List<Slot> listAllSlotsOfThisDayToBuildOrInDb = SlotService.buildListSlot( slot.getIdForm( ), mapReservationRule, dateOfSlot, dateOfSlot );
        // Remove the current slot and all the slot before it
        listAllSlotsOfThisDayToBuildOrInDb = listAllSlotsOfThisDayToBuildOrInDb.stream( )
                .filter( slotToKeep -> slotToKeep.getStartingDateTime( ).isAfter( slot.getStartingDateTime( ) ) ).collect( Collectors.toList( ) );
        // Need to delete all the slots until the new end of this slot
        List<Slot> listSlotToDelete = listAllSlotsOfThisDayToBuildOrInDb.stream( )
                .filter( slotToDelete -> slotToDelete.getStartingDateTime( ).isAfter( slot.getStartingDateTime( ) )
                        && !slotToDelete.getEndingDateTime( ).isAfter( slot.getEndingDateTime( ) ) && slotToDelete.getIdSlot( ) != 0 )
                .collect( Collectors.toList( ) );
        SlotService.deleteListSlots( listSlotToDelete );
        listAllSlotsOfThisDayToBuildOrInDb.removeAll( listSlotToDelete );
        // Need to find all the existing slots
        List<Slot> listExistingSlots = listAllSlotsOfThisDayToBuildOrInDb.stream( ).filter( existingSlot -> existingSlot.getIdSlot( ) != 0 )
                .collect( Collectors.toList( ) );
        // Remove them from the list of slot to build
        listAllSlotsOfThisDayToBuildOrInDb.removeAll( listExistingSlots );
        // Save this list
        createListSlot( listAllSlotsOfThisDayToBuildOrInDb );
        List<Slot> listSlotToShift = new ArrayList<>( );
        listSlotToShift.addAll( listExistingSlots );
        listSlotToShift.addAll( listAllSlotsOfThisDayToBuildOrInDb );
        // Need to order the list of slot to shift according to the shift
        // if the new ending time is before the previous ending time,
        // the list has to be ordered in chronological order ascending
        // and the first slot to shift is the closest to the current
        // slot
        // (because we have an integrity constraint for the slot, it
        // can't have the same starting or ending time as another slot
        listSlotToShift = listSlotToShift.stream( ).sorted( ( slot1, slot2 ) -> slot1.getStartingDateTime( ).compareTo( slot2.getStartingDateTime( ) ) )
                .collect( Collectors.toList( ) );
        boolean bNewEndingTimeIsAfterThePreviousTime = false;
        // Need to know the ending time of the day
        LocalDateTime endingDateTimeOfTheDay = null;
        ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( slot.getIdForm( ), dateOfSlot );
        WorkingDay workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( reservationRule.getListWorkingDay( ), dateOfSlot.getDayOfWeek( ) );
        LocalTime endingTimeOfTheDay;
        if ( workingDay != null )
        {
            endingTimeOfTheDay = WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDay );
        }
        else
        {
            endingTimeOfTheDay = WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay( reservationRule.getListWorkingDay( ) );
        }
        endingDateTimeOfTheDay = endingTimeOfTheDay.atDate( dateOfSlot );
        long timeToAdd = 0;
        long timeToSubstract = 0;
        if ( previousEndingTime.isBefore( slot.getEndingTime( ) ) )
        {
            bNewEndingTimeIsAfterThePreviousTime = true;
            // Need to find the next available slot, to know how to
            // add to the starting time of the next slot to match
            // with the new end of the current slot
            if ( CollectionUtils.isNotEmpty( listSlotToShift ) )
            {
                Slot nextSlot = listSlotToShift.stream( ).min( ( s1, s2 ) -> s1.getStartingDateTime( ).compareTo( s2.getStartingDateTime( ) ) ).orElse( slot );
                if ( slot.getEndingDateTime( ).isAfter( nextSlot.getStartingDateTime( ) ) )
                {
                    timeToAdd = nextSlot.getStartingDateTime( ).until( slot.getEndingDateTime( ), ChronoUnit.MINUTES );
                }
                else
                {
                    timeToAdd = slot.getEndingDateTime( ).until( nextSlot.getStartingDateTime( ), ChronoUnit.MINUTES );
                }
                Collections.reverse( listSlotToShift );
            }
            else
            {
                timeToAdd = previousEndingTime.until( slot.getEndingTime( ), ChronoUnit.MINUTES );
            }
        }
        else
        {
            timeToSubstract = slot.getEndingTime( ).until( previousEndingTime, ChronoUnit.MINUTES );
        }
        // If it's an update of an existing slot
        if ( slot.getIdSlot( ) != 0 )
        {
            updateRemainingPlaces( slot );
        }
        saveSlot( slot );
        // Need to set the new starting and ending time of all the slots
        // to shift and update them
        for ( Slot slotToShift : listSlotToShift )
        {
            // If the new ending time is after the previous time
            if ( bNewEndingTimeIsAfterThePreviousTime )
            {
                // If the starting time + the time to add is before the
                // ending time of the day
                if ( slotToShift.getStartingDateTime( ).plus( timeToAdd, ChronoUnit.MINUTES ).isBefore( endingDateTimeOfTheDay ) )
                {
                    slotToShift.setStartingDateTime( slotToShift.getStartingDateTime( ).plus( timeToAdd, ChronoUnit.MINUTES ) );
                    // if the ending time is after the ending time of
                    // the day, we set the new ending time to the ending
                    // time of the day
                    if ( slotToShift.getEndingDateTime( ).plus( timeToAdd, ChronoUnit.MINUTES ).isAfter( endingDateTimeOfTheDay ) )
                    {
                        slotToShift.setEndingDateTime( endingDateTimeOfTheDay );
                    }
                    else
                    {
                        slotToShift.setEndingDateTime( slotToShift.getEndingDateTime( ).plus( timeToAdd, ChronoUnit.MINUTES ) );
                    }
                    slotToShift.setIsSpecific( SlotService.isSpecificSlot( slotToShift ) );
                    saveSlot( slotToShift );
                }
                else
                {
                    // Delete this slot (the slot can not be after the
                    // ending time of the day)
                    SlotService.deleteSlot( slotToShift );
                }
            }
            else
            {
                // The new ending time is before the previous ending
                // time
                slotToShift.setStartingDateTime( slotToShift.getStartingDateTime( ).minus( timeToSubstract, ChronoUnit.MINUTES ) );
                slotToShift.setEndingDateTime( slotToShift.getEndingDateTime( ).minus( timeToSubstract, ChronoUnit.MINUTES ) );
                slotToShift.setIsSpecific( SlotService.isSpecificSlot( slotToShift ) );
                saveSlot( slotToShift );
            }
        }
        if ( !bNewEndingTimeIsAfterThePreviousTime )
        {
            // If the slots have been shift earlier,
            // there is no slot(s) between the last slot created
            // and the ending time of the day, need to create it(them)
            List<Slot> listSlotsToAdd = generateListSlotToCreateAfterATime( endingDateTimeOfTheDay.minusMinutes( timeToSubstract ), slot.getIdForm( ) );
            createListSlot( listSlotsToAdd );
        }

    }

    /**
     * Generate the list of slot to create after a slot (taking into account the week definition and the rules to apply)
     * 
     * @param slot
     *            the slot
     * @return the list of next slots
     */
    private static List<Slot> generateListSlotToCreateAfterATime( LocalDateTime dateTimeToStartCreation, int nIdForm )
    {
        List<Slot> listSlotToCreate = new ArrayList<>( );
        LocalDate dateOfCreation = dateTimeToStartCreation.toLocalDate( );
        ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( nIdForm, dateOfCreation );
        int nMaxCapacity = reservationRule.getMaxCapacityPerSlot( );

        WorkingDay workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( reservationRule.getListWorkingDay( ), dateOfCreation.getDayOfWeek( ) );
        LocalTime endingTimeOfTheDay = null;
        List<TimeSlot> listTimeSlot = new ArrayList<>( );
        int nDurationSlot = 0;
        if ( workingDay != null )
        {
            endingTimeOfTheDay = WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDay );
            nDurationSlot = WorkingDayService.getMinDurationTimeSlotOfAWorkingDay( workingDay );
            listTimeSlot = TimeSlotService.findListTimeSlotByWorkingDay( workingDay.getIdWorkingDay( ) );
        }
        else
        {
            endingTimeOfTheDay = WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay( reservationRule.getListWorkingDay( ) );
            nDurationSlot = WorkingDayService.getMinDurationTimeSlotOfAListOfWorkingDay( reservationRule.getListWorkingDay( ) );
        }
        LocalDateTime endingDateTimeOfTheDay = endingTimeOfTheDay.atDate( dateOfCreation );
        LocalDateTime startingDateTime = dateTimeToStartCreation;
        LocalDateTime endingDateTime = startingDateTime.plusMinutes( nDurationSlot );
        while ( !endingDateTime.isAfter( endingDateTimeOfTheDay ) )
        {
            Slot slotToCreate = SlotService.buildSlot( nIdForm, new Period( startingDateTime, endingDateTime ), nMaxCapacity, nMaxCapacity, nMaxCapacity, 0,
                    Boolean.FALSE, Boolean.TRUE );
            slotToCreate.setIsSpecific( SlotService.isSpecificSlot( slotToCreate, workingDay, listTimeSlot, nMaxCapacity ) );
            startingDateTime = endingDateTime;
            endingDateTime = startingDateTime.plusMinutes( nDurationSlot );
            listSlotToCreate.add( slotToCreate );
        }
        if ( startingDateTime.isBefore( endingDateTimeOfTheDay ) && endingDateTime.isAfter( endingDateTimeOfTheDay ) )
        {
            Slot slotToCreate = SlotService.buildSlot( nIdForm, new Period( startingDateTime, endingDateTimeOfTheDay ), nMaxCapacity, nMaxCapacity,
                    nMaxCapacity, 0, Boolean.FALSE, Boolean.TRUE );
            slotToCreate.setIsSpecific( SlotService.isSpecificSlot( slotToCreate, workingDay, listTimeSlot, nMaxCapacity ) );
            listSlotToCreate.add( slotToCreate );
        }
        return listSlotToCreate;
    }

    /**
     * Build a Slot object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * 
     */
    /**
     * Update the capacity of the slot
     * 
     * @param slot
     *            the slot to update
     */
    public static void updateRemainingPlaces( Slot slot )
    {
        Slot oldSlot = SlotHome.findByPrimaryKey( slot.getIdSlot( ) );
        int nNewNbMaxCapacity = slot.getMaxCapacity( );
        int nOldBnMaxCapacity = oldSlot.getMaxCapacity( );
        // If the max capacity has been modified
        if ( nNewNbMaxCapacity != nOldBnMaxCapacity )
        {
            // Need to update the remaining places

            // Need to add the diff between the old value and the new value
            // to the remaining places (if the new is higher)
            if ( nNewNbMaxCapacity > nOldBnMaxCapacity )
            {
                int nValueToAdd = nNewNbMaxCapacity - nOldBnMaxCapacity;
                slot.setNbPotentialRemainingPlaces( oldSlot.getNbPotentialRemainingPlaces( ) + nValueToAdd );
                slot.setNbRemainingPlaces( oldSlot.getNbRemainingPlaces( ) + nValueToAdd );
            }
            else
            {
                // the new value is lower than the previous capacity
                // !!!! If there are appointments on this slot and if the
                // slot is already full, the slot will be surbooked !!!!
                int nValueToSubstract = nOldBnMaxCapacity - nNewNbMaxCapacity;
                slot.setNbPotentialRemainingPlaces(  oldSlot.getNbPotentialRemainingPlaces( ) - nValueToSubstract  );
                slot.setNbRemainingPlaces(  oldSlot.getNbRemainingPlaces( ) - nValueToSubstract  );
            }
        }else {
        	
        	slot.setNbPotentialRemainingPlaces(  oldSlot.getNbPotentialRemainingPlaces( )  );
            slot.setNbRemainingPlaces(  oldSlot.getNbRemainingPlaces( ) );
        }
    }

    /**
     * Save a slot in database
     * 
     * @param slot
     *            the slot to save
     * @return the slot saved
     */
    public static Slot saveSlot( Slot slot )
    {
        Slot slotSaved = null;
        if ( slot.getIdSlot( ) == 0 )
        {
            slotSaved = createSlot( slot );
        }
        else
        {
            slotSaved = updateSlot( slot );
        }
        return slotSaved;
    }

    /**
     * Update a slot
     * 
     * @param slot
     *            the slot updated
     */
    public static Slot updateSlot( Slot slot )
    {
        Slot slotToReturn = SlotHome.update( slot );
        SlotListenerManager.notifyListenersSlotChange( slot.getIdSlot( ) );
        return slotToReturn;

    }

    /**
     * Create in database the slots given
     * 
     * @param listSlotToCreate
     *            the list of slots to create in database
     */
    private static void createListSlot( List<Slot> listSlotToCreate )
    {
        if ( CollectionUtils.isNotEmpty( listSlotToCreate ) )
        {
            for ( Slot slotTemp : listSlotToCreate )
            {
                createSlot( slotTemp );
            }
        }
    }

    /**
     * Clean slotlist
     */
    public static void cleanSlotlist( )
    {

        Iterator<Integer> it = _listSlot.keySet( ).iterator( );
        int idSlot;
        while ( it.hasNext( ) )
        {

            idSlot = it.next( );
            Slot slot = SlotService.findSlotById( idSlot );
            if ( slot == null || slot.getStartingDateTime( ).isBefore( LocalDateTime.now( ) ) || slot.getMaxCapacity( ) <= slot.getNbPlacesTaken( ) )
            {
                _listSlot.remove( idSlot );
            }

        }
    }

    /**
     * lock slots and put the lock object in the list passed on the param
     * 
     * @param appointmentDTO
     *            the appoitmentDTO
     * @param listLock
     *            The list of lock object
     * @throws InterruptedException
     */

    private static void lockSlot( AppointmentDTO appointmentDTO, List<Lock> listLock ) throws InterruptedException
    {

        for ( AppointmentSlot appSlot : appointmentDTO.getListAppointmentSlot( ) )
        {

            Lock lock = getLockOnSlot( appSlot.getIdSlot( ) );
            if ( lock.tryLock( 2, TimeUnit.SECONDS ) )
            {

                listLock.add( lock );

            }
            else
            {

                throw new SlotFullException( "ERROR SLOT LOCKED" );
            }

        }

    }

    /**
     * Save and update slots
     * 
     * @param appointmentDTO
     *            the appointmentDTO
     * @param bIsUpdate
     *            the boolean if is an update
     * @return list slot updated
     */
    private static List<Slot> saveSlots( AppointmentDTO appointmentDTO, boolean bIsUpdate )
    {

        List<Slot> listSlotToUpdate = appointmentDTO.getSlot( );
        Appointment oldAppointment = null;
        List<Slot> listSlot = new ArrayList<>( );
        List<Lock> listLock = new ArrayList<>( );

        if ( appointmentDTO.getIdAppointment( ) != 0 )
        {
            oldAppointment = AppointmentService.findAppointmentById( appointmentDTO.getIdAppointment( ) );
        }
        try
        {
            lockSlot( appointmentDTO, listLock );
            int nbRemainingPlaces = listSlotToUpdate.stream( ).map( Slot::getNbRemainingPlaces ).reduce( 0, Integer::sum );
            for ( AppointmentSlot appSlot : appointmentDTO.getListAppointmentSlot( ) )
            {

                Slot slt = null;
                if ( appSlot.getIdSlot( ) != 0 )
                {
                    // recovery of the slot in the bdd to manage the concurrent access
                    slt = SlotService.findSlotById( appSlot.getIdSlot( ) );

                }
                else
                {

                    slt = listSlotToUpdate.stream( ).filter( s -> s.getIdSlot( ) == appSlot.getIdSlot( ) ).findAny( ).orElse( null );
                }
                // update appointment
                if ( ( appointmentDTO.getIdAppointment( ) != 0 && ( appointmentDTO.getNbBookedSeats( ) > nbRemainingPlaces + oldAppointment.getNbPlaces( )
                        && !appointmentDTO.getOverbookingAllowed( ) ) ) || slt.getEndingDateTime( ).isBefore( LocalDateTime.now( ) ) )
                {
                    throw new SlotFullException( "ERROR SLOT FULL" );
                }

                else
                    if ( appointmentDTO.getIdAppointment( ) == 0 && ( slt == null
                            || ( ( appSlot.getNbPlaces( ) > slt.getNbRemainingPlaces( ) || ( appointmentDTO.getNbBookedSeats( ) > nbRemainingPlaces ) )
                                    && !appointmentDTO.getOverbookingAllowed( ) )
                            || slt.getEndingDateTime( ).isBefore( LocalDateTime.now( ) ) ) )

                    {
                        AppLogService.error( "ERROR SLOT FULL, ID SLOT: "+ slt.getDate().toString( ) );
                        throw new SlotFullException( "ERROR SLOT FULL" );

                    }

                // if it's an update for modification of the date of the appointment
                if ( !bIsUpdate && appointmentDTO.getIdAppointment( ) != 0
                        && oldAppointment.getListAppointmentSlot( ).stream( ).noneMatch( p -> p.getIdSlot( ) == appSlot.getIdSlot( ) ) )
                {

                    // Need to update the old slot
                    for ( AppointmentSlot appointmentSlot : oldAppointment.getListAppointmentSlot( ) )
                    {

                        updateRemaningPlacesWithAppointmentMovedDeletedOrCanceled( appointmentSlot.getNbPlaces( ), appointmentSlot.getIdSlot( ) );
                    }
                    // Need to remove the workflow resource to reload again the workflow
                    // at the first step
                    bIsUpdate = true;
                }

                // Update of the remaining places of the slot

                int oldNbRemainingPLaces = slt.getNbRemainingPlaces( );
                int nbMaxPotentialBookedSeats = appointmentDTO.getNbMaxPotentialBookedSeats( );
                int oldNbPotentialRemaningPlaces = slt.getNbPotentialRemainingPlaces( );
                int oldNbPlacesTaken = slt.getNbPlacesTaken( );
                int effectiveBookedSeats = appSlot.getNbPlaces( );

                int newNbRemainingPlaces = 0;
                int newPotentialRemaningPlaces = 0;
                int newNbPlacesTaken = 0;

                if ( appointmentDTO.getIdAppointment( ) != 0
                        && oldAppointment.getListAppointmentSlot( ).stream( ).anyMatch( p -> p.getIdSlot( ) == appSlot.getIdSlot( ) ) )
                {
                    // It is an update of the appointment
                	AppointmentSlot oldApptSlot=  oldAppointment.getListAppointmentSlot( ).stream( ).filter( p -> p.getIdSlot( ) == appSlot.getIdSlot( ) ).findAny( ).orElse( null );
                    int nOldTakenPlaces = (oldApptSlot != null)?oldApptSlot.getNbPlaces():0;                    		
                    newNbRemainingPlaces = oldNbRemainingPLaces + nOldTakenPlaces - effectiveBookedSeats;
                    newPotentialRemaningPlaces = oldNbPotentialRemaningPlaces + nbMaxPotentialBookedSeats - effectiveBookedSeats;
                    newNbPlacesTaken = oldNbPlacesTaken - nOldTakenPlaces + effectiveBookedSeats;
                }
                else
                {
                    newNbRemainingPlaces = oldNbRemainingPLaces - effectiveBookedSeats;
                    newPotentialRemaningPlaces = oldNbPotentialRemaningPlaces + nbMaxPotentialBookedSeats - effectiveBookedSeats;
                    newNbPlacesTaken = oldNbPlacesTaken + effectiveBookedSeats;
                }
                slt.setNbRemainingPlaces( newNbRemainingPlaces );
                slt.setNbPlacestaken( newNbPlacesTaken );
                slt.setNbPotentialRemainingPlaces( Math.min( newPotentialRemaningPlaces, newNbRemainingPlaces ) );

                if ( slt.getNbPlacesTaken( ) > slt.getMaxCapacity( ) && !appointmentDTO.getOverbookingAllowed( ) )
                {

                    throw new SlotFullException( "case of overbooking" );
                }

                listSlot.add( slt );
                slt = saveSlot( slt );
                Lock lock = getLockOnSlot( slt.getIdSlot( ) );
                lock.unlock( );
                listLock.remove( lock );
            }

            return listSlot;

        }
        catch( Exception e )
        {
            AppLogService.error( "Error Save slot " + e.getMessage( ), e );
            throw new SlotFullException( e.getMessage( ), e );

        }
        finally
        {

            for ( Lock lk : listLock )
            {

                lk.unlock( );
            }

        }
    }

}
