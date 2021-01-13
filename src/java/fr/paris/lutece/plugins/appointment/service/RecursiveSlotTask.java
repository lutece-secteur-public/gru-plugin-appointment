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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.slot.Period;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;

public class RecursiveSlotTask extends RecursiveTask<List<Slot>>
{

    private int _nIdForm;
    private Map<WeekDefinition, ReservationRule> _mapReservationRule;
    private LocalDate _startingDate;
    private LocalDate _endingDate;
    private int _nNbPlaces;

    RecursiveSlotTask( int nIdForm, Map<WeekDefinition, ReservationRule> mapReservationRule, LocalDate startingDate, LocalDate endingDate, int nNbPlaces )
    {

        this._nIdForm = nIdForm;
        this._mapReservationRule = mapReservationRule;
        this._startingDate = startingDate;
        this._endingDate = endingDate;
        this._nNbPlaces = nNbPlaces;

    }

    @Override
    protected List<Slot> compute( )
    {

        List<RecursiveSlotTask> forks = new LinkedList<>( );
        LocalDate tmpDate = _startingDate;
        LocalDate tmpCompareDate = _startingDate.plusMonths( 1 );
        List<Slot> listSlot = new ArrayList<>( );

        if ( tmpCompareDate.isAfter( _endingDate ) || tmpCompareDate.isEqual( _endingDate ) )
        {

            if ( _nNbPlaces < 1 )
            {

                listSlot.addAll( buildListSlot( _nIdForm, _mapReservationRule, _startingDate, _endingDate ) );

            }
            else
            {

                listSlot.addAll( buildListSlot( _nIdForm, _mapReservationRule, _startingDate, _endingDate, _nNbPlaces ) );

            }

        }
        else
        {

            while ( tmpDate.isBefore( _endingDate ) )
            {

                RecursiveSlotTask subtask = null;
                if ( tmpCompareDate.isAfter( _endingDate ) || tmpCompareDate.isEqual( _endingDate ) )
                {

                    subtask = new RecursiveSlotTask( _nIdForm, _mapReservationRule, tmpDate, _endingDate, _nNbPlaces );

                }
                else
                {

                    subtask = new RecursiveSlotTask( _nIdForm, _mapReservationRule, tmpDate, tmpCompareDate, _nNbPlaces );

                }

                subtask.fork( );
                forks.add( subtask );
                tmpDate = tmpCompareDate.plusDays( 1 );
                tmpCompareDate = tmpCompareDate.plusMonths( 1 );
            }

            for ( RecursiveSlotTask task : forks )
            {

                listSlot.addAll( task.join( ) );
            }
        }

        return listSlot;
    }

    /**
     * Build all the slot for a period with all the rules (open hours ...) to apply on each day, for each slot
     * 
     * @param nIdForm
     *            the form Id
     * @param mapReservationRule
     *            the map of the rule week definition
     * @param startingDate
     *            the starting date of the period
     * @param endingDate
     *            the ending date of the periode
     * @returna list of all the slots built
     */
    private List<Slot> buildListSlot( int nIdForm, Map<WeekDefinition, ReservationRule> mapReservationRule, LocalDate startingDate, LocalDate endingDate )
    {
        List<Slot> listSlot = new ArrayList<>( );
        final List<WeekDefinition> listDateReservationRule = new ArrayList<>( mapReservationRule.keySet( ) );
        WeekDefinition closestweeDef;
        ReservationRule reservationRuleToApply = null;
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
        WeekDefinition firsWeek = listDateReservationRule.stream( ).sorted( ( week1, week2 ) -> week1.getDateOfApply( ).compareTo( week2.getDateOfApply( ) ) )
                .findFirst( ).orElse( null );
        final LocalDate firstDateOfReservationRule = firsWeek.getDateOfApply( );
        LocalDate startingDateToUse = startingDate;
        if ( firstDateOfReservationRule != null && startingDate.isBefore( firstDateOfReservationRule ) )
        {
            startingDateToUse = firstDateOfReservationRule;
        }
        // Get all the closing day of this period
        List<LocalDate> listDateOfClosingDay = ClosingDayService.findListDateOfClosingDayByIdFormAndDateRange( nIdForm, startingDateToUse, endingDate );
        // Get all the slot between these two dates
        Map<LocalDateTime, Slot> mapSlot = SlotService.buildMapSlotsByIdFormAndDateRangeWithDateForKey( nIdForm, startingDateToUse.atStartOfDay( ),
                endingDate.atTime( LocalTime.MAX ) );

        // Get or build all the event for the period
        while ( !dateTemp.isAfter( endingDate ) )
        {
            dateToCompare = dateTemp;
            // Find the closest date of apply of reservation rule with the given
            // date
            reservationRuleToApply = null;
            closestweeDef = Utilities.getClosestWeekDefinitionInPast( listDateReservationRule, dateToCompare );
            if ( closestweeDef != null )
            {

                reservationRuleToApply = mapReservationRule.get( closestweeDef );

            }
            nMaxCapacity = 0;
            // Get the day of week of the date
            dayOfWeek = dateTemp.getDayOfWeek( );
            // Get the working day of this day of week
            workingDay = null;
            if ( reservationRuleToApply != null )
            {
                nMaxCapacity = reservationRuleToApply.getMaxCapacityPerSlot( );
                workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( reservationRuleToApply.getListWorkingDay( ), dayOfWeek );

            }
            if ( workingDay != null )
            {
                minTimeForThisDay = WorkingDayService.getMinStartingTimeOfAWorkingDay( workingDay );
                maxTimeForThisDay = WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDay );
                // Check if this day is a closing day
                if ( listDateOfClosingDay.contains( dateTemp ) )
                {
                    listSlot.add( SlotService.buildSlot( nIdForm, new Period( dateTemp.atTime( minTimeForThisDay ), dateTemp.atTime( maxTimeForThisDay ) ),
                            nMaxCapacity, nMaxCapacity, nMaxCapacity, 0, Boolean.FALSE, Boolean.FALSE ) );
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
                                int nMaxCapacityToPut = timeSlot.getMaxCapacity( );
                                slotToAdd = SlotService.buildSlot( nIdForm, new Period( dateTimeTemp, dateTemp.atTime( timeTemp ) ), nMaxCapacityToPut,
                                        nMaxCapacityToPut, nMaxCapacityToPut, 0, timeSlot.getIsOpen( ), Boolean.FALSE );
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
                if ( reservationRuleToApply != null )
                {
                    minTimeForThisDay = WorkingDayService.getMinStartingTimeOfAListOfWorkingDay( reservationRuleToApply.getListWorkingDay( ) );
                    maxTimeForThisDay = WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay( reservationRuleToApply.getListWorkingDay( ) );
                    int nDuration = WorkingDayService.getMinDurationTimeSlotOfAListOfWorkingDay( reservationRuleToApply.getListWorkingDay( ) );
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
                                slotToAdd = SlotService.buildSlot( nIdForm, new Period( dateTimeTemp, dateTemp.atTime( timeTemp ) ), nMaxCapacity, nMaxCapacity,
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

    /**
     * Build all the slot for a period with all the rules (open hours ...) to apply on each day, for each slot
     * 
     * @param nIdForm
     *            the form Id
     * @param mapReservationRule
     *            the map of the rule week definition
     * @param startingDate
     *            the starting date of the period
     * @param endingDate
     *            the ending date of the periode
     * @param nNbPlaces
     *            the number of place to take
     * @returna list of all the slots built
     */
    public static List<Slot> buildListSlot( int nIdForm, Map<WeekDefinition, ReservationRule> mapReservationRule, LocalDate startingDate, LocalDate endingDate,
            int nNbPlaces )
    {
        List<Slot> listSlotToShow = new ArrayList<>( );

        final List<WeekDefinition> listDateReservationRule = new ArrayList<>( mapReservationRule.keySet( ) );
        WeekDefinition closestweeDef;
        ReservationRule reservationRuleToApply = null;
        LocalDate dateTemp = startingDate;
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
        WeekDefinition firsWeek = listDateReservationRule.stream( ).sorted( ( week1, week2 ) -> week1.getDateOfApply( ).compareTo( week2.getDateOfApply( ) ) )
                .findFirst( ).orElse( null );
        final LocalDate firstDateOfReservationRule = firsWeek.getDateOfApply( );
        LocalDate startingDateToUse = startingDate;
        if ( firstDateOfReservationRule != null && startingDate.isBefore( firstDateOfReservationRule ) )
        {
            startingDateToUse = firstDateOfReservationRule;
        }
        // Get all the closing day of this period
        List<LocalDate> listDateOfClosingDay = ClosingDayService.findListDateOfClosingDayByIdFormAndDateRange( nIdForm, startingDateToUse, endingDate );
        // Get all the slot between these two dates
        Map<LocalDateTime, Slot> mapSlot = SlotService.buildMapSlotsByIdFormAndDateRangeWithDateForKey( nIdForm, startingDateToUse.atStartOfDay( ),
                endingDate.atTime( LocalTime.MAX ) );

        // Get or build all the event for the period
        while ( !dateTemp.isAfter( endingDate ) )
        {
            dateToCompare = dateTemp;
            // Find the closest date of apply of reservation rule with the given
            // date
            closestweeDef = Utilities.getClosestWeekDefinitionInPast( listDateReservationRule, dateToCompare );
            if ( closestweeDef != null )
            {

                reservationRuleToApply = mapReservationRule.get( closestweeDef );

            }
            // Get the day of week of the date
            dayOfWeek = dateTemp.getDayOfWeek( );
            // Get the working day of this day of week
            workingDay = null;
            if ( reservationRuleToApply != null )
            {
                workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( reservationRuleToApply.getListWorkingDay( ), dayOfWeek );

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
                                int nMaxCapacityToPut = timeSlot.getMaxCapacity( );
                                slotToAdd = SlotService.buildSlot( nIdForm, new Period( dateTimeTemp, dateTemp.atTime( timeTemp ) ), nMaxCapacityToPut,
                                        nMaxCapacityToPut, nMaxCapacityToPut, 0, timeSlot.getIsOpen( ), Boolean.FALSE );

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
}
