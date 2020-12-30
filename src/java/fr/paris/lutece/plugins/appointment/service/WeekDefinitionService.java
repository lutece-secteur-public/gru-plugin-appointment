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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinitionHome;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.service.listeners.WeekDefinitionManagerListener;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * Service class of a week definition
 * 
 * @author Laurent Payen
 *
 */
public final class WeekDefinitionService
{

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private WeekDefinitionService( )
    {
    }

    /**
     * Create a week definition in database
     * 
     * @param nIdReservationRule
     *            the nIdReservationRule
     * @param dateOfApply
     *            the date of the week definition
     * @return the week definition created
     */
    public static WeekDefinition createWeekDefinition( int nIdReservationRule, LocalDate dateOfApply, LocalDate endingDateOfApply )
    {
        WeekDefinition weekDefinition = new WeekDefinition( );
        fillInWeekDefinition( weekDefinition, nIdReservationRule, dateOfApply, endingDateOfApply );
        WeekDefinitionHome.create( weekDefinition );
        WeekDefinitionManagerListener.notifyListenersWeekDefinitionAssigned( weekDefinition.getIdWeekDefinition( ) );
        return weekDefinition;
    }

    /**
     * Remove the weekdefinition (and by cascade the working days and the time slots)
     * 
     * @param nIdWeekDefinition
     *            the id of the week definition to delete
     */
    public static void removeWeekDefinition( int nIdWeekDefinition )
    {
        WeekDefinitionHome.delete( nIdWeekDefinition );
        WeekDefinitionManagerListener.notifyListenersWeekDefinitionUnassigned( nIdWeekDefinition );
    }

    /**
     * Save a week definition
     * 
     * @param weekDefinition
     *            the week definition to save
     * @return the week definition saved
     */
    public static WeekDefinition saveWeekDefinition( WeekDefinition weekDefinition )
    {
        WeekDefinitionHome.create( weekDefinition );
        WeekDefinitionManagerListener.notifyListenersWeekDefinitionAssigned( weekDefinition.getIdWeekDefinition( ) );
        return weekDefinition;
    }

    /**
     * Fill the week definition object with the given parameters
     * 
     * @param weekDefinition
     *            the week definition to fill in
     * @param nIdForm
     *            the form id
     * @param dateOfApply
     *            the date of the week definition
     */
    public static void fillInWeekDefinition( WeekDefinition weekDefinition, int nIdReservationRule, LocalDate dateOfApply, LocalDate endingDateOfApply )
    {
        weekDefinition.setDateOfApply( dateOfApply );
        weekDefinition.setEndingDateOfApply( endingDateOfApply );
        weekDefinition.setIdReservationRule( nIdReservationRule );
    }

    /**
     * Fin all the week definition of a form
     * 
     * @param nIdForm
     *            the form Id
     * @return the list of all the week definition of the form
     */
    public static List<WeekDefinition> findListWeekDefinition( int nIdForm )
    {
        return WeekDefinitionHome.findByIdForm( nIdForm );

    }

    /**
     * Find a week definition of a form and a date of apply
     * 
     * @param nIdForm
     *            the form Id
     * @param dateOfApply
     *            the date of apply of the week definition
     * @return the week definition with the closest date of apply
     */
    public static WeekDefinition findWeekDefinitionByIdFormAndClosestToDateOfApply( int nIdForm, LocalDate dateOfApply )
    {
        // Get all the week definitions
        List<WeekDefinition> listWeekDefinition = WeekDefinitionHome.findByIdForm( nIdForm );
        List<LocalDate> listDate = new ArrayList<>( );
        for ( WeekDefinition weekDefinition : listWeekDefinition )
        {
            listDate.add( weekDefinition.getDateOfApply( ) );
        }
        // Try to get the closest date in past of the date of apply
        LocalDate closestDate = Utilities.getClosestDateInPast( listDate, dateOfApply );
        WeekDefinition weekDefinition = null;
        // If there is no closest date in past
        if ( closestDate == null )
        {
            // if the list of week definitions is not null
            if ( CollectionUtils.isNotEmpty( listWeekDefinition ) )
            {
                // Get the next week definition in the future
                weekDefinition = listWeekDefinition.stream( ).min( ( w1, w2 ) -> w1.getDateOfApply( ).compareTo( w2.getDateOfApply( ) ) ).orElse( null );
            }
        }
        else
        {
            // There is a closest date in past
            if ( CollectionUtils.isNotEmpty( listWeekDefinition ) )
            {
                // Get the corresponding week definition
                weekDefinition = listWeekDefinition.stream( ).filter( x -> closestDate.isEqual( x.getDateOfApply( ) ) ).findAny( ).orElse( null );
            }
        }

        return weekDefinition;
    }

    /**
     * Find the weekdefinition of a form on a specific date
     * 
     * @param nIdForm
     *            the form Id
     * @param dateOfApply
     *            the date of the weekdefinition
     * @return the weekdefinition object
     */
    public static WeekDefinition findWeekDefinitionByIdFormAndDateOfApply( int nIdForm, LocalDate dateOfApply )
    {
        return WeekDefinitionHome.findByIdFormAndDateOfApply( nIdForm, dateOfApply );
    }

    /**
     * Return, if it exists, the next week definition after a given date
     * 
     * @param nIdForm
     *            the form id
     * @param previousDateOfApply
     *            the previous date of the previous week definition
     * @return the next week definition if it exists, null otherwise
     */
    public static WeekDefinition findNextWeekDefinition( int nIdForm, LocalDate previousDateOfApply )
    {
        WeekDefinition nextWeekDefinition = new WeekDefinition();
        List<WeekDefinition> listWeekDefinition = WeekDefinitionHome.findByIdForm( nIdForm );
        if ( CollectionUtils.isNotEmpty( listWeekDefinition ) )
        {
            listWeekDefinition = listWeekDefinition.stream( ).filter( x -> x.getDateOfApply( ).isAfter( previousDateOfApply ) ).collect( Collectors.toList( ) );
            if ( CollectionUtils.isNotEmpty( listWeekDefinition ) )
            {
                nextWeekDefinition = listWeekDefinition.stream( ).min( ( w1, w2 ) -> w1.getDateOfApply( ).compareTo( w2.getDateOfApply( ) ) ).orElse( null );
            }
        }
        return nextWeekDefinition;
    }

    /**
     * Find a week definition with its primary key
     * 
     * @param nIdWeekDefinition
     *            the week definition id
     * @return the week definition found
     */
    public static WeekDefinition findWeekDefinitionLightById( int nIdWeekDefinition )
    {
        return WeekDefinitionHome.findByPrimaryKey( nIdWeekDefinition );
    }

    /**
     * Find a week definition by its primary key and set its working days
     * 
     * @param nIdWeekDefinition
     *            the week definition id
     * @return the week definition and its working days
     */
    public static WeekDefinition findWeekDefinitionById( int nIdWeekDefinition )
    {
        return WeekDefinitionHome.findByPrimaryKey( nIdWeekDefinition );
    }

    /**
     * Build a reference list of all the week definitions of a form
     * 
     * @param nIdForm
     *            the form Id
     * @return a reference list of all the week definitions of a form (Id of the week definition / date of apply of the week definition
     */
    public static ReferenceList findAllDateOfWeekDefinition( int nIdForm )
    {
        ReferenceList listDate = new ReferenceList( );
        List<WeekDefinition> listWeekDefinition = WeekDefinitionHome.findByIdForm( nIdForm );
        for ( WeekDefinition weekDefinition : listWeekDefinition )
        {
            listDate.addItem( weekDefinition.getIdWeekDefinition( ), weekDefinition.getDateOfApply( ).format( Utilities.getFormatter( ) ) );
        }
        return listDate;
    }

    /**
     * Find all the week definition of a form
     * 
     * @param nIdForm
     *            the form id
     * @return a HashMap with the date of apply in key and the week definition in value
     */
    public static HashMap<LocalDate, WeekDefinition> findAllWeekDefinition( int nIdForm )
    {
        HashMap<LocalDate, WeekDefinition> mapWeekDefinition = new HashMap<>( );
        List<WeekDefinition> listWeekDefinition = WeekDefinitionHome.findByIdForm( nIdForm );
        for ( WeekDefinition weekDefinition : listWeekDefinition )
        {
            mapWeekDefinition.put( weekDefinition.getDateOfApply( ), weekDefinition );
        }
        return mapWeekDefinition;
    }

    /**
     * Return the min starting time of a list of week definitions
     * 
     * @param listWeekDefinition
     *            the list of week definitions
     * @return the mini starting time
     */
    public static LocalTime getMinStartingTimeOfAListOfWeekDefinition( List<ReservationRule> listReservationRules )
    {
        LocalTime minStartingTime = null;
        LocalTime startingTimeTemp;
        for ( ReservationRule reservation : listReservationRules )
        {
            startingTimeTemp = getMinStartingTimeOfAWeekDefinition( reservation );
            if ( minStartingTime == null || startingTimeTemp.isBefore( minStartingTime ) )
            {
                minStartingTime = startingTimeTemp;
            }
        }
        return minStartingTime;
    }

    /**
     * Return the min starting time of a week definition
     * 
     * @param weekDefinition
     *            the week definition
     * @return the min starting time of the week definition
     */
    public static LocalTime getMinStartingTimeOfAWeekDefinition( ReservationRule reservationRule )
    {
        return WorkingDayService.getMinStartingTimeOfAListOfWorkingDay( reservationRule.getListWorkingDay( ) );
    }

    /**
     * Return the max ending time of a list of week definitions
     * 
     * @param listWeekDefinition
     *            the list of week definitions
     * @return the max ending time of the list of week definitions
     */
    public static LocalTime getMaxEndingTimeOfAListOfWeekDefinition( List<ReservationRule> listReservationRules )
    {
        LocalTime maxEndingTime = null;
        LocalTime endingTimeTemp;
        for ( ReservationRule reservationRule : listReservationRules )
        {
            endingTimeTemp = getMaxEndingTimeOfAWeekDefinition( reservationRule );
            if ( maxEndingTime == null || endingTimeTemp.isAfter( maxEndingTime ) )
            {
                maxEndingTime = endingTimeTemp;
            }
        }
        return maxEndingTime;
    }

    /**
     * Get the max ending time of a week definition
     * 
     * @param weekDefinition
     *            the week definition
     * @return the max ending time of the week definition
     */
    public static LocalTime getMaxEndingTimeOfAWeekDefinition( ReservationRule reservationRule )
    {
        return WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay( reservationRule.getListWorkingDay( ) );
    }

    /**
     * Get the min duration of a time slot of a list of week definition
     * 
     * @param listWeekDefinition
     *            the list of the week definitions
     * @return the min duration time slot
     */
    public static int getMinDurationTimeSlotOfAListOfWeekDefinition( List<ReservationRule> listReservationRules )
    {
        int nMinDuration = 0;
        int nDurationTemp;
        for ( ReservationRule reservationRule : listReservationRules )
        {
            nDurationTemp = getMinDurationTimeSlotOfAWeekDefinition( reservationRule );
            if ( nMinDuration == 0 || nMinDuration > nDurationTemp )
            {
                nMinDuration = nDurationTemp;
            }
        }
        return nMinDuration;
    }

    /**
     * Get the min duration of a time slot of a week definition
     * 
     * @param weekDefinition
     *            the week definition
     * @return the min duration time slot
     */
    public static int getMinDurationTimeSlotOfAWeekDefinition( ReservationRule reservationRule )
    {
        return WorkingDayService.getMinDurationTimeSlotOfAListOfWorkingDay( reservationRule.getListWorkingDay( ) );
    }

    /**
     * Get the working days integer enum values of a list of week definitions
     * 
     * @param listWeekDefinition
     *            the list of week definitions
     * @return a set of the working days (integer value in a week : 1-> Monday ...) // The fullCalendar library is zero-base (Sunday=0)
     */
    public static Set<String> getSetDaysOfWeekOfAListOfWeekDefinitionForFullCalendar( List<ReservationRule> listReservationRules )
    {
        Set<String> setDayOfWeek = new HashSet<>( );
        for ( ReservationRule reservationRule : listReservationRules )
        {
            setDayOfWeek.addAll( WorkingDayService.getSetDaysOfWeekOfAListOfWorkingDayForFullCalendar( reservationRule.getListWorkingDay( ) ) );
        }
        return setDayOfWeek;
    }

    /**
     * Get the set of the open days of all the week definitons
     * 
     * @param listWeekDefinition
     *            the list of week definitions
     * @return the set of the open days
     */
    public static Set<Integer> getOpenDaysOfWeek( List<ReservationRule> listReservationRules )
    {
        HashSet<Integer> setOpenDays = new HashSet<>( );
        for ( ReservationRule reservation : listReservationRules )
        {
            for ( WorkingDay workingDay : reservation.getListWorkingDay( ) )
            {
                setOpenDays.add( workingDay.getDayOfWeek( ) );
            }
        }
        return setOpenDays;
    }

    /**
     * Get the week definitions of a form for reservation rule
     * 
     * @param nIdReservationRule
     * @return list of week definition
     */
    public static List<WeekDefinition> findByReservationRule( int nIdReservationRule )
    {
        return WeekDefinitionHome.findByReservationRule( nIdReservationRule );
    }

    /**
     * Assign a week to the calendar
     * 
     * @param nIdForm
     *            the id from
     * @param newWeek
     *            the week to assign
     */
    public static void assignWeekDefinition( int nIdForm, WeekDefinition newWeek )
    {

        LocalDate startingDate = newWeek.getDateOfApply( );
        LocalDate endingDate = newWeek.getEndingDateOfApply( );
        List<WeekDefinition> listWeek = WeekDefinitionService.findListWeekDefinition( nIdForm );

        List<WeekDefinition> listWeekToRemove = listWeek.stream( )
                .filter( week -> ( week.getDateOfApply( ).isAfter( startingDate ) || week.getDateOfApply( ).isEqual( startingDate ) )
                        && ( week.getEndingDateOfApply( ).isBefore( endingDate ) || week.getEndingDateOfApply( ).isEqual( endingDate ) ) )
                .collect( Collectors.toList( ) );

        listWeek.removeAll( listWeekToRemove );
        listWeek = listWeek.stream( )
                .filter( p -> ( ( p.getDateOfApply( ).isBefore( startingDate ) || p.getDateOfApply( ).isEqual( startingDate ) )
                        && p.getEndingDateOfApply( ).isAfter( startingDate ) || p.getEndingDateOfApply( ).isEqual( startingDate ) )
                        || ( p.getDateOfApply( ).isBefore( endingDate ) || p.getDateOfApply( ).isEqual( endingDate ) )
                                && p.getEndingDateOfApply( ).isAfter( endingDate )
                        || p.getEndingDateOfApply( ).isEqual( endingDate ) )
                .collect( Collectors.toList( ) );
        List<WeekDefinition> buildListWeekToEdit = new ArrayList<>( );

        for ( WeekDefinition week : listWeek )
        {

            if ( week.getDateOfApply( ).isBefore( startingDate ) && week.getEndingDateOfApply( ).isAfter( endingDate ) )
            {

                WeekDefinition weekToAdd = new WeekDefinition( );
                weekToAdd.setDateOfApply( endingDate.plusDays( 1 ) );
                weekToAdd.setEndingDateOfApply( week.getEndingDateOfApply( ) );
                weekToAdd.setIdReservationRule( week.getIdReservationRule( ) );
                buildListWeekToEdit.add( weekToAdd );
                week.setEndingDateOfApply( startingDate.minusDays( 1 ) );

            }
            else
                if ( week.getDateOfApply( ).isEqual( startingDate )
                        || ( week.getDateOfApply( ).isAfter( startingDate ) && week.getEndingDateOfApply( ).isAfter( endingDate ) ) )
                {

                    week.setDateOfApply( endingDate.plusDays( 1 ) );

                }
                else
                    if ( week.getEndingDateOfApply( ).isEqual( endingDate )
                            || ( week.getDateOfApply( ).isBefore( startingDate ) && week.getEndingDateOfApply( ).isBefore( endingDate ) ) )
                    {

                        week.setEndingDateOfApply( startingDate.minusDays( 1 ) );

                    }

            buildListWeekToEdit.add( week );

        }
        if ( newWeek.getIdReservationRule( ) != 0 )
        {

            buildListWeekToEdit.add( newWeek );
        }

        assignWeekDefintion( listWeekToRemove, buildListWeekToEdit, nIdForm );
    }

    private static void assignWeekDefintion( List<WeekDefinition> listWeekTodRemove, List<WeekDefinition> listWeekToEdit, int nIdForm )
    {
        TransactionManager.beginTransaction( AppointmentPlugin.getPlugin( ) );
        try
        {
            for ( WeekDefinition week : listWeekTodRemove )
            {
                WeekDefinitionHome.delete( week.getIdWeekDefinition( ) );
            }
            for ( WeekDefinition week : listWeekToEdit )
            {

                if ( week.getIdWeekDefinition( ) != 0 )
                {

                    WeekDefinitionHome.delete( week.getIdWeekDefinition( ) );
                }

                WeekDefinitionHome.create( week );
            }
            TransactionManager.commitTransaction( AppointmentPlugin.getPlugin( ) );
            WeekDefinitionManagerListener.notifyListenersListWeekDefinitionChanged( nIdForm );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( AppointmentPlugin.getPlugin( ) );
            AppLogService.error( "Error assign week " + e.getMessage( ), e );
            throw new AppException( e.getMessage( ), e );

        }
    }

}
