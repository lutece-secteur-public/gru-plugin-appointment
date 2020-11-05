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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.slot.Period;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.slot.SlotHome;
import fr.paris.lutece.plugins.appointment.service.listeners.SlotListenerManager;

/**
 * Service class of a slot
 * 
 * @author Laurent Payen
 *
 */
public final class SlotService
{

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private SlotService( )
    {
    }

    /**
     * Find slots of a form on a given period of time
     * 
     * @param nIdForm
     *            the form Id
     * @param startingDateTime
     *            the starting date time to search
     * @param endingDateTime
     *            the ending date time to search
     * @return a list of the slots found
     */
    public static List<Slot> findSlotsByIdFormAndDateRange( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime )
    {
        List<Slot> listSlots = SlotHome.findByIdFormAndDateRange( nIdForm, startingDateTime, endingDateTime );
        for ( Slot slot : listSlots )
        {
            addDateAndTimeToSlot( slot );
        }
        return listSlots;
    }

    /**
     * Find specific slots of a form
     * 
     * @param nIdForm
     *            the form Id
     * @return a list of the slots found
     */
    public static List<Slot> findSpecificSlotsByIdForm( int nIdForm )
    {
        List<Slot> listSpecificSlots = SlotHome.findIsSpecificByIdForm( nIdForm );
        for ( Slot slot : listSpecificSlots )
        {
            addDateAndTimeToSlot( slot );
        }
        return listSpecificSlots;
    }

    /**
     * Build a map (Date, Slot) of all the slots found between the two dates
     * 
     * @param nIdForm
     *            the form id
     * @param startingDateTime
     *            the starting date time
     * @param endingDateTime
     *            the ending date time
     * @return the map
     */
    public static HashMap<LocalDateTime, Slot> buildMapSlotsByIdFormAndDateRangeWithDateForKey( int nIdForm, LocalDateTime startingDateTime,
            LocalDateTime endingDateTime )
    {
        HashMap<LocalDateTime, Slot> mapSlots = new HashMap<>( );
        for ( Slot slot : findSlotsByIdFormAndDateRange( nIdForm, startingDateTime, endingDateTime ) )
        {
            mapSlots.put( slot.getStartingDateTime( ), slot );
        }
        return mapSlots;
    }

    /**
     * Fins all the slots of a form
     * 
     * @param nIdForm
     *            the form id
     * @return a list of all the slots of a form
     */
    public static List<Slot> findListSlot( int nIdForm )
    {
        return SlotHome.findByIdForm( nIdForm );
    }

    /**
     * Find the open slots of a form on a given period of time
     * 
     * @param nIdForm
     *            the form Id
     * @param startingDateTime
     *            the starting Date time to search
     * @param endingDateTime
     *            the ending Date time to search
     * @return a list of open slots whose matches the criteria
     */
    public static List<Slot> findListOpenSlotByIdFormAndDateRange( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime )
    {
        return SlotHome.findOpenSlotsByIdFormAndDateRange( nIdForm, startingDateTime, endingDateTime );
    }

    /**
     * Find a slot with its primary key
     * 
     * @param nIdSlot
     *            the slot Id
     * @return the Slot object
     */
    public static Slot findSlotById( int nIdSlot )
    {
        Slot slot = SlotHome.findByPrimaryKey( nIdSlot );
        if ( slot != null )
        {
            SlotService.addDateAndTimeToSlot( slot );
        }
        return slot;
    }

    /**
     * Build all the slot for a period with all the rules (open hours ...) to apply on each day, for each slot
     * 
     * @param nIdForm
     *            the form Id
     * @param mapWeekDefinition
     *            the map of the week definition
     * @param startingDate
     *            the starting date of the period
     * @param nNbWeeksToDisplay
     *            the number of weeks to build
     * @return a list of all the slots built
     */
    public static List<Slot> buildListSlot( int nIdForm, HashMap<LocalDate, WeekDefinition> mapWeekDefinition, LocalDate startingDate, LocalDate endingDate )
    {
        List<Slot> listSlot = new ArrayList<>( );
        // Get all the reservation rules
        final HashMap<LocalDate, ReservationRule> mapReservationRule = ReservationRuleService.findAllReservationRule( nIdForm );
        final List<LocalDate> listDateWeekDefinition = new ArrayList<>( mapWeekDefinition.keySet( ) );
        final List<LocalDate> listDateReservationTule = new ArrayList<>( mapReservationRule.keySet( ) );
        LocalDate closestDateWeekDefinition;
        LocalDate closestDateReservationRule;
        WeekDefinition weekDefinitionToApply;
        ReservationRule reservationRuleToApply;
        LocalDate dateTemp = startingDate;
        int nMaxCapacity;
        DayOfWeek dayOfWeek;
        WorkingDay workingDay;
        LocalTime minTimeForThisDay;
        LocalTime maxTimeForThisDay;
        LocalTime timeTemp;
        LocalDateTime dateTimeTemp;
        Slot slotToAdd;
        TimeSlot timeSlot;
        LocalDate dateToCompare;
        // Need to check if this date is not before the form date creation
        final LocalDate firstDateOfReservationRule = new ArrayList<>( mapReservationRule.keySet( ) ).stream( ).sorted( ).findFirst( ).orElse( null );
        LocalDate startingDateToUse = startingDate;
        if ( firstDateOfReservationRule != null && startingDate.isBefore( firstDateOfReservationRule ) )
        {
            startingDateToUse = firstDateOfReservationRule;
        }
        // Get all the closing day of this period
        List<LocalDate> listDateOfClosingDay = ClosingDayService.findListDateOfClosingDayByIdFormAndDateRange( nIdForm, startingDateToUse, endingDate );
        // Get all the slot between these two dates
        HashMap<LocalDateTime, Slot> mapSlot = SlotService.buildMapSlotsByIdFormAndDateRangeWithDateForKey( nIdForm, startingDateToUse.atStartOfDay( ),
                endingDate.atTime( LocalTime.MAX ) );

        // Get or build all the event for the period
        while ( !dateTemp.isAfter( endingDate ) )
        {
            dateToCompare = dateTemp;
            // Find the closest date of apply of week definition with the given
            // date
            closestDateWeekDefinition = Utilities.getClosestDateInPast( listDateWeekDefinition, dateToCompare );
            weekDefinitionToApply = mapWeekDefinition.get( closestDateWeekDefinition );
            // Find the closest date of apply of reservation rule with the given
            // date
            closestDateReservationRule = Utilities.getClosestDateInPast( listDateReservationTule, dateToCompare );
            reservationRuleToApply = mapReservationRule.get( closestDateReservationRule );
            nMaxCapacity = 0;
            if ( reservationRuleToApply != null )
            {
                nMaxCapacity = reservationRuleToApply.getMaxCapacityPerSlot( );
            }
            // Get the day of week of the date
            dayOfWeek = dateTemp.getDayOfWeek( );
            // Get the working day of this day of week
            workingDay = null;
            if ( weekDefinitionToApply != null )
            {
                workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( weekDefinitionToApply.getListWorkingDay( ), dayOfWeek );
            }
            if ( workingDay != null )
            {
                minTimeForThisDay = WorkingDayService.getMinStartingTimeOfAWorkingDay( workingDay );
                maxTimeForThisDay = WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDay );
                // Check if this day is a closing day
                if ( listDateOfClosingDay.contains( dateTemp ) )
                {
                    listSlot.add( buildSlot( nIdForm, new Period( dateTemp.atTime( minTimeForThisDay ), dateTemp.atTime( maxTimeForThisDay ) ), nMaxCapacity,
                            nMaxCapacity, nMaxCapacity, 0, Boolean.FALSE, Boolean.FALSE ) );
                }
                else
                {
                    timeTemp = minTimeForThisDay;
                    // For each slot of this day
                    while ( timeTemp.isBefore( maxTimeForThisDay ) || !timeTemp.equals( maxTimeForThisDay ) )
                    {
                        // Get the LocalDateTime
                        dateTimeTemp = dateTemp.atTime( timeTemp );
                        // Search if there is a slot for this datetime
                        if ( mapSlot.containsKey( dateTimeTemp ) )
                        {
                            slotToAdd = mapSlot.get( dateTimeTemp );
                            timeTemp = slotToAdd.getEndingDateTime( ).toLocalTime( );
                            listSlot.add( slotToAdd );
                        }
                        else
                        {
                            // Search the timeslot
                            timeSlot = TimeSlotService.getTimeSlotInListOfTimeSlotWithStartingTime( workingDay.getListTimeSlot( ), timeTemp );
                            if ( timeSlot != null )
                            {
                                timeTemp = timeSlot.getEndingTime( );
                                int nMaxCapacityToPut = nMaxCapacity;
                                if ( timeSlot.getMaxCapacity( ) != 0 )
                                {
                                    nMaxCapacityToPut = timeSlot.getMaxCapacity( );
                                }
                                slotToAdd = buildSlot( nIdForm, new Period( dateTimeTemp, dateTemp.atTime( timeTemp ) ), nMaxCapacityToPut, nMaxCapacityToPut,
                                        nMaxCapacityToPut, 0, timeSlot.getIsOpen( ), Boolean.FALSE );
                                listSlot.add( slotToAdd );
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }
            }
            else
            {
                // This is not a working day
                // We build all the slots closed for this day
                if ( reservationRuleToApply != null && weekDefinitionToApply != null )
                {
                    minTimeForThisDay = WorkingDayService.getMinStartingTimeOfAListOfWorkingDay( weekDefinitionToApply.getListWorkingDay( ) );
                    maxTimeForThisDay = WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay( weekDefinitionToApply.getListWorkingDay( ) );
                    int nDuration = WorkingDayService.getMinDurationTimeSlotOfAListOfWorkingDay( weekDefinitionToApply.getListWorkingDay( ) );
                    if ( minTimeForThisDay != null && maxTimeForThisDay != null )
                    {
                        timeTemp = minTimeForThisDay;
                        // For each slot of this day
                        while ( timeTemp.isBefore( maxTimeForThisDay ) || !timeTemp.equals( maxTimeForThisDay ) )
                        {
                            // Get the LocalDateTime
                            dateTimeTemp = dateTemp.atTime( timeTemp );
                            // Search if there is a slot for this datetime
                            if ( mapSlot.containsKey( dateTimeTemp ) )
                            {
                                slotToAdd = mapSlot.get( dateTimeTemp );
                                timeTemp = slotToAdd.getEndingDateTime( ).toLocalTime( );
                                listSlot.add( slotToAdd );
                            }
                            else
                            {
                                timeTemp = timeTemp.plusMinutes( nDuration );
                                if ( timeTemp.isAfter( maxTimeForThisDay ) )
                                {
                                    timeTemp = maxTimeForThisDay;
                                }
                                slotToAdd = buildSlot( nIdForm, new Period( dateTimeTemp, dateTemp.atTime( timeTemp ) ), nMaxCapacity, nMaxCapacity,
                                        nMaxCapacity, 0, Boolean.FALSE, Boolean.FALSE );
                                listSlot.add( slotToAdd );
                            }
                        }
                    }
                }
            }
            dateTemp = dateTemp.plusDays( 1 );
        }
        return listSlot;

    }

    public static List<Slot> buildListSlot( int nIdForm, HashMap<LocalDate, WeekDefinition> mapWeekDefinition, LocalDate startingDate, LocalDate endingDate,
            int nNbPlaces )
    {
        List<Slot> listSlotToShow = new ArrayList<>( );

        // Get all the reservation rules
        final HashMap<LocalDate, ReservationRule> mapReservationRule = ReservationRuleService.findAllReservationRule( nIdForm );
        final List<LocalDate> listDateWeekDefinition = new ArrayList<>( mapWeekDefinition.keySet( ) );
        final List<LocalDate> listDateReservationTule = new ArrayList<>( mapReservationRule.keySet( ) );
        LocalDate closestDateWeekDefinition;
        LocalDate closestDateReservationRule;
        WeekDefinition weekDefinitionToApply;
        ReservationRule reservationRuleToApply;
        LocalDate dateTemp = startingDate;
        int nMaxCapacity;
        DayOfWeek dayOfWeek;
        WorkingDay workingDay;
        LocalTime minTimeForThisDay;
        LocalTime maxTimeForThisDay;
        LocalTime timeTemp;
        LocalDateTime dateTimeTemp;
        LocalDateTime endingDateTime;
        LocalDateTime startingDateTime = null;
        LocalTime tempEndingDateTime = null;

        boolean isChanged = true;
        int sumNbPotentialRemainingPlaces;
        int sumNbRemainingPlaces;

        Slot slotToAdd;
        TimeSlot timeSlot;
        LocalDate dateToCompare;
        // Need to check if this date is not before the form date creation
        final LocalDate firstDateOfReservationRule = new ArrayList<>( mapReservationRule.keySet( ) ).stream( ).sorted( ).findFirst( ).orElse( null );
        LocalDate startingDateToUse = startingDate;
        if ( firstDateOfReservationRule != null && startingDate.isBefore( firstDateOfReservationRule ) )
        {
            startingDateToUse = firstDateOfReservationRule;
        }
        // Get all the closing day of this period
        List<LocalDate> listDateOfClosingDay = ClosingDayService.findListDateOfClosingDayByIdFormAndDateRange( nIdForm, startingDateToUse, endingDate );
        // Get all the slot between these two dates
        HashMap<LocalDateTime, Slot> mapSlot = SlotService.buildMapSlotsByIdFormAndDateRangeWithDateForKey( nIdForm, startingDateToUse.atStartOfDay( ),
                endingDate.atTime( LocalTime.MAX ) );

        // Get or build all the event for the period
        while ( !dateTemp.isAfter( endingDate ) )
        {
            dateToCompare = dateTemp;
            // Find the closest date of apply of week definition with the given
            // date
            closestDateWeekDefinition = Utilities.getClosestDateInPast( listDateWeekDefinition, dateToCompare );
            weekDefinitionToApply = mapWeekDefinition.get( closestDateWeekDefinition );
            // Find the closest date of apply of reservation rule with the given
            // date
            closestDateReservationRule = Utilities.getClosestDateInPast( listDateReservationTule, dateToCompare );
            reservationRuleToApply = mapReservationRule.get( closestDateReservationRule );
            nMaxCapacity = 0;
            if ( reservationRuleToApply != null )
            {
                nMaxCapacity = reservationRuleToApply.getMaxCapacityPerSlot( );
            }
            // Get the day of week of the date
            dayOfWeek = dateTemp.getDayOfWeek( );
            // Get the working day of this day of week
            workingDay = null;
            if ( weekDefinitionToApply != null )
            {
                workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( weekDefinitionToApply.getListWorkingDay( ), dayOfWeek );
            }
            if ( workingDay != null )
            {
                minTimeForThisDay = WorkingDayService.getMinStartingTimeOfAWorkingDay( workingDay );
                maxTimeForThisDay = WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDay );
                // Check if this day is a closing day
                if ( !listDateOfClosingDay.contains( dateTemp ) )
                {
                    timeTemp = minTimeForThisDay;
                    sumNbPotentialRemainingPlaces = 0;
                    sumNbRemainingPlaces = 0;
                    isChanged = true;
                    tempEndingDateTime = timeTemp;
                    // For each slot of this day
                    while ( timeTemp.isBefore( maxTimeForThisDay ) || !timeTemp.equals( maxTimeForThisDay ) )
                    {

                        // Get the LocalDateTime
                        dateTimeTemp = dateTemp.atTime( timeTemp );
                        // Search if there is a slot for this datetime
                        if ( isChanged )
                        {

                            startingDateTime = dateTimeTemp;
                            isChanged = false;
                        }
                        if ( mapSlot.containsKey( dateTimeTemp ) )
                        {
                            slotToAdd = mapSlot.get( dateTimeTemp );
                            timeTemp = slotToAdd.getEndingDateTime( ).toLocalTime( );

                        }
                        else
                        {
                            // Search the timeslot
                            timeSlot = TimeSlotService.getTimeSlotInListOfTimeSlotWithStartingTime( workingDay.getListTimeSlot( ), timeTemp );
                            if ( timeSlot != null )
                            {
                                timeTemp = timeSlot.getEndingTime( );
                                int nMaxCapacityToPut = nMaxCapacity;
                                if ( timeSlot.getMaxCapacity( ) != 0 )
                                {
                                    nMaxCapacityToPut = timeSlot.getMaxCapacity( );
                                }
                                slotToAdd = buildSlot( nIdForm, new Period( dateTimeTemp, dateTemp.atTime( timeTemp ) ), nMaxCapacityToPut, nMaxCapacityToPut,
                                        nMaxCapacityToPut, 0, timeSlot.getIsOpen( ), Boolean.FALSE );

                            }
                            else
                            {
                                break;
                            }
                        }

                        if ( sumNbPotentialRemainingPlaces >= nNbPlaces || !slotToAdd.getIsOpen( ) || slotToAdd.getNbPotentialRemainingPlaces( ) <= 0
                                || slotToAdd.getEndingDateTime( ).isBefore( LocalDateTime.now( ) ) )
                        {

                            sumNbPotentialRemainingPlaces = 0;
                            sumNbRemainingPlaces = 0;
                            startingDateTime = slotToAdd.getEndingDateTime( );
                            tempEndingDateTime = slotToAdd.getEndingTime( );
                        }
                        else
                        {
                            sumNbPotentialRemainingPlaces = sumNbPotentialRemainingPlaces + 1;
                            sumNbRemainingPlaces = sumNbRemainingPlaces + 1;
                        }

                        if ( sumNbPotentialRemainingPlaces >= nNbPlaces )
                        {

                            endingDateTime = slotToAdd.getEndingDateTime( );
                            Slot slt = new Slot( );
                            slt.setStartingDateTime( startingDateTime );
                            slt.setEndingDateTime( endingDateTime );
                            slt.setIsOpen( true );
                            slt.setNbPotentialRemainingPlaces( sumNbPotentialRemainingPlaces );
                            slt.setNbRemainingPlaces( sumNbRemainingPlaces );
                            slt.setDate( slotToAdd.getDate( ) );
                            slt.setIdForm( slotToAdd.getIdForm( ) );
                            listSlotToShow.add( slt );
                            isChanged = true;
                            timeTemp = tempEndingDateTime;
                        }

                    }
                }
            }

            dateTemp = dateTemp.plusDays( 1 );
        }
        return listSlotToShow;

    }

    /**
     * Build a slot with all its values
     * 
     * @param nIdForm
     *            the form Id
     * @param startingDateTime
     *            the starting date time
     * @param endingDateTime
     *            the ending date time
     * @param nMaxCapacity
     *            the maximum capacity for the slot
     * @param nNbRemainingPlaces
     *            the number of remaining places of the slot
     * @param bIsOpen
     *            true if the slot is open
     * @return the slot built
     */
    public static Slot buildSlot( int nIdForm, Period period, int nMaxCapacity, int nNbRemainingPlaces, int nNbPotentialRemainingPlaces, int nNbPlacesTaken,
            boolean bIsOpen, boolean bIsSpecific )
    {
        Slot slot = new Slot( );
        slot.setIdSlot( 0 );
        slot.setIdForm( nIdForm );
        slot.setStartingDateTime( period.getStartingDateTime( ) );
        slot.setEndingDateTime( period.getEndingDateTime( ) );
        slot.setMaxCapacity( nMaxCapacity );
        slot.setNbRemainingPlaces( nNbRemainingPlaces );
        slot.setNbPotentialRemainingPlaces( nNbPotentialRemainingPlaces );
        slot.setNbPlacestaken( nNbPlacesTaken );
        slot.setIsOpen( bIsOpen );
        slot.setIsSpecific( bIsSpecific );
        addDateAndTimeToSlot( slot );
        return slot;
    }

    /**
     * To know if it's a specific slot, need to search for a similar time slot
     * 
     * @param slot
     *            the slot
     * @return true if specific
     */
    public static boolean isSpecificSlot( Slot slot )
    {
        LocalDate dateOfSlot = slot.getDate( );
        WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( slot.getIdForm( ), dateOfSlot );
        ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( slot.getIdForm( ), slot.getDate( ) );
        WorkingDay workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( weekDefinition.getListWorkingDay( ), dateOfSlot.getDayOfWeek( ) );
        List<TimeSlot> listTimeSlot = null;
        if ( workingDay != null )
        {
            listTimeSlot = TimeSlotService.findListTimeSlotByWorkingDay( workingDay.getIdWorkingDay( ) );
        }
        return isSpecificSlot( slot, workingDay, listTimeSlot, reservationRule.getMaxCapacityPerSlot( ) );
    }

    /**
     * To know if it's a specific slot, need to search for a similar time slot
     * 
     * @param slot
     *            the slot
     * @param workingDay
     *            the working day
     * @param listTimeSlot
     *            the list of time slots
     * @return true if it's a specific slot
     */
    public static boolean isSpecificSlot( Slot slot, WorkingDay workingDay, List<TimeSlot> listTimeSlot, int nMaxCapacity )
    {
        boolean bIsSpecific = Boolean.TRUE;
        List<TimeSlot> listMatchTimeSlot = null;
        if ( workingDay == null )
        {
            if ( !slot.getIsOpen( ) && slot.getMaxCapacity( ) == nMaxCapacity )
            {
                bIsSpecific = Boolean.FALSE;
            }
        }
        else
        {
            listMatchTimeSlot = listTimeSlot.stream( )
                    .filter( t -> ( t.getStartingTime( ).equals( slot.getStartingDateTime( ).toLocalTime( ) ) )
                            && ( t.getEndingTime( ).equals( slot.getEndingDateTime( ).toLocalTime( ) ) ) && ( t.getIsOpen( ) == slot.getIsOpen( ) )
                            && ( t.getMaxCapacity( ) == slot.getMaxCapacity( ) ) )
                    .collect( Collectors.toList( ) );
            if ( CollectionUtils.isNotEmpty( listMatchTimeSlot ) )
            {
                bIsSpecific = Boolean.FALSE;
            }
        }
        return bIsSpecific;
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

        SlotSafeService.updateSlot( slot, bEndingTimeHasChanged, previousEndingTime, bShifSlot );

    }

    /**
     * Update the capacity of the slot
     * 
     * @param slot
     *            the slot to update
     */
    public static void updateRemainingPlaces( Slot slot )
    {
        SlotSafeService.updateRemainingPlaces( slot );
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
        return SlotSafeService.saveSlot( slot );

    }

    /**
     * Update a slot
     * 
     * @param slot
     *            the slot updated
     */
    public static Slot updateSlot( Slot slot )
    {
        return SlotSafeService.updateSlot( slot );
    }

    /**
     * Form the DTO, adding the date and the time to the slot
     * 
     * @param slot
     *            the slot on which to add values
     */
    public static void addDateAndTimeToSlot( Slot slot )
    {
        if ( slot.getStartingDateTime( ) != null )
        {
            slot.setDate( slot.getStartingDateTime( ).toLocalDate( ) );
            slot.setStartingTime( slot.getStartingDateTime( ).toLocalTime( ) );
        }
        if ( slot.getEndingDateTime( ) != null )
        {
            slot.setEndingTime( slot.getEndingDateTime( ).toLocalTime( ) );
        }
    }

    /**
     * Create a slot in db
     * 
     * @param slot
     *            the slot to create
     * @return the slot created
     */
    public static Slot createSlot( Slot slot )
    {
        return SlotSafeService.createSlot( slot );
    }

    /**
     * Delete a list of slots
     * 
     * @param listSlotToDelete
     *            the lost of slots to delete
     */
    public static void deleteListSlots( List<Slot> listSlotToDelete )
    {
        for ( Slot slotToDelete : listSlotToDelete )
        {
            SlotService.deleteSlot( slotToDelete );
        }
    }

    /**
     * Delete a slot
     * 
     * @param slot
     *            the slot to delete
     */
    public static void deleteSlot( Slot slot )
    {
        int nIdSlot = slot.getIdSlot( );
        SlotListenerManager.notifyListenersSlotRemoval( nIdSlot );
        SlotSafeService.removeSlotInMemory( nIdSlot );
        SlotHome.delete( nIdSlot );
    }

    /**
     * Return the slot with the max Date
     * 
     * @param nIdForm
     *            the form id
     * @return the slot with the max date
     */
    public static Slot findSlotWithMaxDate( int nIdForm )
    {
        return SlotHome.findSlotWithTheMaxDate( nIdForm );
    }

}
