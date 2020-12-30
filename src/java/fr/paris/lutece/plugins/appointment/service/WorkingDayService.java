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

import static java.lang.Math.toIntExact;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlotHome;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDayHome;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;

/**
 * Service class of a working day
 * 
 * @author Laurent Payen
 *
 */
public final class WorkingDayService
{

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private WorkingDayService( )
    {
    }

    /**
     * Create in database a working day object with the given parameters
     * 
     * @param nIdReservationRule
     *            the week rule Id
     * @param dayOfWeek
     *            the day of week
     * @return the working day object built
     */
    public static WorkingDay generateWorkingDay( int nIdReservationRule, DayOfWeek dayOfWeek )
    {
        WorkingDay workingDay = new WorkingDay( );
        workingDay.setIdReservationRule( nIdReservationRule );
        workingDay.setDayOfWeek( dayOfWeek.getValue( ) );
        WorkingDayHome.create( workingDay );
        return workingDay;
    }

    /**
     * Save a working day
     * 
     * @param workingDay
     *            the working day to save
     * @return the working day saved
     */
    public static WorkingDay saveWorkingDay( WorkingDay workingDay )
    {
        return WorkingDayHome.create( workingDay );
    }

    /**
     * Create in database a working day and its time slots
     * 
     * @param nIdReservationRule
     *            the week rule Id
     * @param dayOfWeek
     *            the day of week of the woking day
     * @param startingTime
     *            the starting time of the working day
     * @param endingTime
     *            the ending time of the working day
     * @param nDuration
     *            the duration of the time slot of the working day
     * @param nMaxCapacity
     *            the max capacity for the slots of the working day
     */
    public static void generateWorkingDayAndListTimeSlot( int nIdReservationRule, DayOfWeek dayOfWeek, LocalTime startingTime, LocalTime endingTime,
            int nDuration, int nMaxCapacity )
    {
        WorkingDay workingDay = generateWorkingDay( nIdReservationRule, dayOfWeek );
        TimeSlotService.createListTimeSlot(
                TimeSlotService.generateListTimeSlot( workingDay.getIdWorkingDay( ), startingTime, endingTime, nDuration, nMaxCapacity, Boolean.FALSE ) );
    }

    /**
     * Get the open days of an appointmentForm DTO
     * 
     * @param appointmentForm
     *            the appointmentForm DTO
     * @return the list of day of week opened
     */
    public static List<DayOfWeek> getOpenDays( AppointmentFormDTO appointmentForm )
    {
        List<DayOfWeek> openDays = new ArrayList<>( );
        if ( appointmentForm.getIsOpenMonday( ) )
        {
            openDays.add( DayOfWeek.MONDAY );
        }
        if ( appointmentForm.getIsOpenTuesday( ) )
        {
            openDays.add( DayOfWeek.TUESDAY );
        }
        if ( appointmentForm.getIsOpenWednesday( ) )
        {
            openDays.add( DayOfWeek.WEDNESDAY );
        }
        if ( appointmentForm.getIsOpenThursday( ) )
        {
            openDays.add( DayOfWeek.THURSDAY );
        }
        if ( appointmentForm.getIsOpenFriday( ) )
        {
            openDays.add( DayOfWeek.FRIDAY );
        }
        if ( appointmentForm.getIsOpenSaturday( ) )
        {
            openDays.add( DayOfWeek.SATURDAY );
        }
        if ( appointmentForm.getIsOpenSunday( ) )
        {
            openDays.add( DayOfWeek.SUNDAY );
        }
        return openDays;
    }

    /**
     * Find the working days of a week definition
     * 
     * @param nIdWeekDefinition
     *            the week definition Id
     * @return a list of the working days of the week definition
     */
    public static List<WorkingDay> findListWorkingDayByWeekDefinitionRule( int nIdWeekDefinitionRule )
    {
        List<WorkingDay> listWorkingDay = WorkingDayHome.findByIdReservationRule( nIdWeekDefinitionRule );
        for ( WorkingDay workingDay : listWorkingDay )
        {
            workingDay.setListTimeSlot( TimeSlotService.findListTimeSlotByWorkingDay( workingDay.getIdWorkingDay( ) ) );
        }
        return listWorkingDay;
    }

    /**
     * Delete a list of working days
     * 
     * @param listWorkingDay
     *            the list of working days to delete
     */
    public static void deleteListWorkingDay( List<WorkingDay> listWorkingDay )
    {
        for ( WorkingDay workingDay : listWorkingDay )
        {
            TimeSlotHome.deleteByIdWorkingDay( workingDay.getIdWorkingDay( ) );
            WorkingDayHome.delete( workingDay.getIdWorkingDay( ) );
        }
    }

    /**
     * Find a working day with its primary key
     * 
     * @param nIdWorkingDay
     *            the working day Id
     * @return the working day found
     */
    public static WorkingDay findWorkingDayLightById( int nIdWorkingDay )
    {
        return WorkingDayHome.findByPrimaryKey( nIdWorkingDay );
    }

    /**
     * Find a working day and set its time slots
     * 
     * @param nIdWorkingDay
     *            the working day Id
     * @return the working day found with its time slots
     */
    public static WorkingDay findWorkingDayById( int nIdWorkingDay )
    {
        WorkingDay workingDay = WorkingDayHome.findByPrimaryKey( nIdWorkingDay );
        workingDay.setListTimeSlot( TimeSlotService.findListTimeSlotByWorkingDay( nIdWorkingDay ) );
        return workingDay;
    }

    /**
     * Get the max ending time of a working day
     * 
     * @param workingDay
     *            the working day
     * @return the max ending time of the working day
     */
    public static LocalTime getMaxEndingTimeOfAWorkingDay( WorkingDay workingDay )
    {
        return workingDay.getListTimeSlot( ).stream( ).map( TimeSlot::getEndingTime ).max( LocalTime::compareTo ).orElse( null );
    }

    /**
     * Get the max ending time of a list of working days
     * 
     * @param listWorkingDay
     *            the list of working days
     * @return the max ending time of the working days
     */
    public static LocalTime getMaxEndingTimeOfAListOfWorkingDay( List<WorkingDay> listWorkingDay )
    {
        LocalTime maxEndingTime = null;
        for ( WorkingDay workingDay : listWorkingDay )
        {
            LocalTime endingTimeTemp = getMaxEndingTimeOfAWorkingDay( workingDay );

                maxEndingTime = endingTimeTemp;
            
        }
        return maxEndingTime;
    }

    /**
     * Get the min starting time of a working day
     * 
     * @param workingDay
     *            the working day
     * @return the min starting time of the working day
     */
    public static LocalTime getMinStartingTimeOfAWorkingDay( WorkingDay workingDay )
    {
        return workingDay.getListTimeSlot( ).stream( ).map( TimeSlot::getStartingTime ).min( LocalTime::compareTo ).orElse( null );
    }

    /**
     * Get the min starting time of a list of working days
     * 
     * @param listWorkingDay
     *            the list of working days
     * @return the min starting time of the working days
     */
    public static LocalTime getMinStartingTimeOfAListOfWorkingDay( List<WorkingDay> listWorkingDay )
    {
        LocalTime minStartingTime = null;
        LocalTime startingTimeTemp;
        for ( WorkingDay workingDay : listWorkingDay )
        {
            startingTimeTemp = getMinStartingTimeOfAWorkingDay( workingDay );
            if ( minStartingTime == null || startingTimeTemp.isBefore( minStartingTime ) )
            {
                minStartingTime = startingTimeTemp;
            }
        }
        return minStartingTime;
    }

    /**
     * Get the min duration slot of a working day
     * 
     * @param workingDay
     *            the working day
     * @return the min duration slot of a working day
     */
    public static int getMinDurationTimeSlotOfAWorkingDay( WorkingDay workingDay )
    {
        long lMinDuration = 0;
        LocalTime startingTimeTemp;
        LocalTime endingTimeTemp;
        long lDurationTemp;
        for ( TimeSlot timeSlot : workingDay.getListTimeSlot( ) )
        {
            startingTimeTemp = timeSlot.getStartingTime( );
            endingTimeTemp = timeSlot.getEndingTime( );
            lDurationTemp = startingTimeTemp.until( endingTimeTemp, ChronoUnit.MINUTES );
            if ( lMinDuration == 0 || lMinDuration > lDurationTemp )
            {
                lMinDuration = lDurationTemp;
            }
        }
        return toIntExact( lMinDuration );
    }

    /**
     * Get the min duration slot of a list of working days
     * 
     * @param listWorkingDay
     *            the list of working days
     * @return the min duration slot of the working days
     */
    public static int getMinDurationTimeSlotOfAListOfWorkingDay( List<WorkingDay> listWorkingDay )
    {
        long lMinDuration = 0;
        long lDurationTemp;
        for ( WorkingDay workingDay : listWorkingDay )
        {
            lDurationTemp = getMinDurationTimeSlotOfAWorkingDay( workingDay );
            if ( lMinDuration == 0 || lMinDuration > lDurationTemp )
            {
                lMinDuration = lDurationTemp;
            }
        }
        return toIntExact( lMinDuration );
    }

    /**
     * Get all the day of week of working days
     * 
     * @param listWorkingDay
     *            the list of working days
     * @return a set of day of week
     */
    public static HashSet<String> getSetDaysOfWeekOfAListOfWorkingDayForFullCalendar( List<WorkingDay> listWorkingDay )
    {
        HashSet<String> setDayOfWeek = new HashSet<>( );
        for ( WorkingDay workingDay : listWorkingDay )
        {
            int dow = workingDay.getDayOfWeek( );
            if ( dow == DayOfWeek.SUNDAY.getValue( ) )
            {
                // The fullCalendar library is zero-base (Sunday=0)
                dow = 0;
            }
            setDayOfWeek.add( Integer.toString( dow ) );
        }
        return setDayOfWeek;
    }

    /**
     * Find the working day in a list of working day which matches the day of week given
     * 
     * @param listWorkingDay
     *            the list of working days
     * @param dayOfWeek
     *            the day of week to search
     * @return the working day that matches
     */
    public static WorkingDay getWorkingDayOfDayOfWeek( List<WorkingDay> listWorkingDay, DayOfWeek dayOfWeek )
    {
        return listWorkingDay.stream( ).filter( x -> x.getDayOfWeek( ) == dayOfWeek.getValue( ) ).findFirst( ).orElse( null );
    }

}
