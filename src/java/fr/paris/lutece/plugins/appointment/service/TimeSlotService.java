package fr.paris.lutece.plugins.appointment.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlotHome;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.service.listeners.WeekDefinitionManagerListener;

/**
 * Service class for the time slot
 * 
 * @author Laurent Payen
 *
 */
public final class TimeSlotService
{

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private TimeSlotService( )
    {
    }

    /**
     * Build a list of timeSlot Object from a starting time to an endingTime
     * 
     * @param nIdWorkingDay
     *            the workingDay Id
     * @param startingTime
     *            the starting time
     * @param endingTime
     *            the ending time
     * @param nDuration
     *            the duration of the slot
     * @param nMaxCapacity
     *            the maximum capacity of the slot
     * @return the list of TimeSlot built
     */
    public static List<TimeSlot> generateListTimeSlot( int nIdWorkingDay, LocalTime startingTime, LocalTime endingTime, int nDuration, int nMaxCapacity,
            boolean forceTimeSlotCreationWithMinTime )
    {
        List<TimeSlot> listTimeSlot = new ArrayList<>( );
        LocalTime tempStartingTime = startingTime;
        LocalTime tempEndingTime = startingTime.plusMinutes( nDuration );
        while ( !tempEndingTime.isAfter( endingTime ) )
        {
            listTimeSlot.add( generateTimeSlot( nIdWorkingDay, tempStartingTime, tempEndingTime, Boolean.TRUE.booleanValue( ), nMaxCapacity ) );
            tempStartingTime = tempEndingTime;
            tempEndingTime = tempEndingTime.plusMinutes( nDuration );
        }
        if ( forceTimeSlotCreationWithMinTime )
        {
            tempStartingTime = tempEndingTime.minusMinutes( nDuration );
            if ( tempStartingTime.isBefore( endingTime ) )
            {
                listTimeSlot.add( generateTimeSlot( nIdWorkingDay, tempStartingTime, endingTime, Boolean.FALSE, nMaxCapacity ) );
            }
        }
        return listTimeSlot;
    }

    /**
     * Save a time slot
     * 
     * @param timeSlot
     *            the time slot to save
     * @return the time slot saved
     */
    public static TimeSlot saveTimeSlot( TimeSlot timeSlot )
    {
        return TimeSlotHome.create( timeSlot );
    }

    /**
     * Build a timeSlot with all its values
     * 
     * @param nIdWorkingDay
     *            the workingDay Id
     * @param startingTime
     *            the starting time
     * @param endingTime
     *            the ending time
     * @param isOpen
     *            true if the slot is open
     * @param nMaxCapacity
     *            the maximum capacity of the slot
     * @return the timeSLot built
     */
    public static TimeSlot generateTimeSlot( int nIdWorkingDay, LocalTime startingTime, LocalTime endingTime, boolean isOpen, int nMaxCapacity )
    {
        TimeSlot timeSlot = new TimeSlot( );
        timeSlot.setIdWorkingDay( nIdWorkingDay );
        timeSlot.setIsOpen( isOpen );
        timeSlot.setStartingTime( startingTime );
        timeSlot.setEndingTime( endingTime );
        timeSlot.setMaxCapacity( nMaxCapacity );
        return timeSlot;
    }

    /**
     * Find the time slots of a working day
     * 
     * @param nIdWorkingDay
     *            the working day Id
     * @return the list of the timeSlot of this workingDay
     */
    public static List<TimeSlot> findListTimeSlotByWorkingDay( int nIdWorkingDay )
    {
        return TimeSlotHome.findByIdWorkingDay( nIdWorkingDay );
    }

    /**
     * Find a timeSlot with its primary key
     * 
     * @param nIdTimeSlot
     *            the timeSlot Id
     * @return the timeSlot found
     */
    public static TimeSlot findTimeSlotById( int nIdTimeSlot )
    {
        return TimeSlotHome.findByPrimaryKey( nIdTimeSlot );
    }

    /**
     * Update a timeSLot in database
     * 
     * @param timeSlot
     *            the timeSlot to update
     * @param bEndingTimeHasChanged
     *            if the ending time has changed, need to regenerate and update all the next time slots
     * @param previousEndingTime
     *            the previous ending time of the current time slot
     * @param bShifSlot
     *            true if the user has decided to shift the next slots
     */
    public static void updateTimeSlot( TimeSlot timeSlot, boolean bEndingTimeHasChanged, LocalTime previousEndingTime, boolean bShifSlot )
    {
        WorkingDay workingDay = WorkingDayService.findWorkingDayById( timeSlot.getIdWorkingDay( ) );
        WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionLightById( workingDay.getIdWeekDefinition( ) );
        ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( weekDefinition.getIdForm( ),
                weekDefinition.getDateOfApply( ) );
        int nDuration = WorkingDayService.getMinDurationTimeSlotOfAWorkingDay( workingDay );
        if ( bEndingTimeHasChanged )
        {
            if ( !bShifSlot )
            {
                updateTimeSlotWithoutShift( timeSlot, workingDay, reservationRule, nDuration );
            }
            else
            {
                updateTimeSlotWithShift( timeSlot, workingDay, reservationRule, nDuration, previousEndingTime );
            }

        }

        WeekDefinitionManagerListener.notifyListenersWeekDefinitionChange( workingDay.getIdWeekDefinition( ) );
    }

    /**
     * Update a time slot with shifting the next
     * 
     * @param timeSlot
     *            the time slot modified
     * @param workingDay
     *            the working day
     * @param reservationRule
     *            the reservation rule
     * @param nDuration
     *            the duration of a time slot
     * @param previousEndingTime
     *            the previous ending time
     */
    private static void updateTimeSlotWithShift( TimeSlot timeSlot, WorkingDay workingDay, ReservationRule reservationRule, int nDuration,
            LocalTime previousEndingTime )
    {
        // We want to shift all the next time slots
        // Get all the time slots of the day
        List<TimeSlot> listOfAllTimeSlotsOfThisWorkingDay = findListTimeSlotByWorkingDay( workingDay.getIdWorkingDay( ) );
        // Remove the current time slot and all the time slots before it
        listOfAllTimeSlotsOfThisWorkingDay = listOfAllTimeSlotsOfThisWorkingDay.stream( )
                .filter( timeSlotToKeep -> timeSlotToKeep.getStartingTime( ).isAfter( timeSlot.getStartingTime( ) ) ).collect( Collectors.toList( ) );
        // Need to delete all the time slots until the new end of this
        // time slot
        List<TimeSlot> listTimeSlotToDelete = listOfAllTimeSlotsOfThisWorkingDay
                .stream( )
                .filter(
                        timeSlotToDelete -> timeSlotToDelete.getStartingTime( ).isAfter( timeSlot.getStartingTime( ) )
                                && !timeSlotToDelete.getEndingTime( ).isAfter( timeSlot.getEndingTime( ) ) ).collect( Collectors.toList( ) );
        deleteListTimeSlot( listTimeSlotToDelete );
        listOfAllTimeSlotsOfThisWorkingDay.removeAll( listTimeSlotToDelete );
        // Need to order the list of time slot to shift according to the
        // shift
        // if the new ending time is before the previous ending time,
        // the list has to be ordered in chronological order ascending
        // and the first time slot to shift is the closest to the
        // current
        // time slot
        // (because we have an integrity constraint for the time slot,
        // it
        // can't have the same starting or ending time as another time
        // slot
        List<TimeSlot> listTimeSlotToShift = new ArrayList<>( );
        listTimeSlotToShift.addAll( listOfAllTimeSlotsOfThisWorkingDay );
        listTimeSlotToShift = listTimeSlotToShift.stream( )
                .sorted( ( timeSlot1, timeSlot2 ) -> timeSlot1.getStartingTime( ).compareTo( timeSlot2.getStartingTime( ) ) ).collect( Collectors.toList( ) );
        boolean bNewEndingTimeIsAfterThePreviousTime = false;
        // Need to know the ending time of the day
        LocalTime endingTimeOfTheDay = WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDay );

        long timeToAdd = 0;
        long timeToSubstract = 0;
        if ( previousEndingTime.isBefore( timeSlot.getEndingTime( ) ) )
        {
            bNewEndingTimeIsAfterThePreviousTime = true;
            // Need to find the next available time slot, to know how to
            // add to the starting time of the next time slot to match
            // with
            // the new end of the current time slot
            if ( CollectionUtils.isNotEmpty( listTimeSlotToShift ) )
            {
                TimeSlot nextTimeSlot = listTimeSlotToShift.stream( ).min( ( t1, t2 ) -> t1.getStartingTime( ).compareTo( t2.getStartingTime( ) ) ).get( );
                if ( timeSlot.getEndingTime( ).isAfter( nextTimeSlot.getStartingTime( ) ) )
                {
                    timeToAdd = nextTimeSlot.getStartingTime( ).until( timeSlot.getEndingTime( ), ChronoUnit.MINUTES );
                }
                else
                {
                    timeToAdd = timeSlot.getEndingTime( ).until( nextTimeSlot.getStartingTime( ), ChronoUnit.MINUTES );
                }
                Collections.reverse( listTimeSlotToShift );
            }
            else
            {
                timeToAdd = previousEndingTime.until( timeSlot.getEndingTime( ), ChronoUnit.MINUTES );
            }

        }
        else
        {
            timeToSubstract = timeSlot.getEndingTime( ).until( previousEndingTime, ChronoUnit.MINUTES );
        }
        updateTimeSlot( timeSlot );
        // Need to set the new starting and ending time of all the time
        // slots
        // to shift and update them
        for ( TimeSlot timeSlotToShift : listTimeSlotToShift )
        {
            // If the new ending time is after the previous time
            if ( bNewEndingTimeIsAfterThePreviousTime )
            {
                // If the starting time + the time to add is before the
                // ending time of the day
                if ( timeSlotToShift.getStartingTime( ).plus( timeToAdd, ChronoUnit.MINUTES ).isBefore( endingTimeOfTheDay ) )
                {
                    timeSlotToShift.setStartingTime( timeSlotToShift.getStartingTime( ).plus( timeToAdd, ChronoUnit.MINUTES ) );
                    // if the ending time is after the ending time of
                    // the day, we set the new ending time to the ending
                    // time of the day
                    if ( timeSlotToShift.getEndingTime( ).plus( timeToAdd, ChronoUnit.MINUTES ).isAfter( endingTimeOfTheDay ) )
                    {
                        timeSlotToShift.setEndingTime( endingTimeOfTheDay );
                    }
                    else
                    {
                        timeSlotToShift.setEndingTime( timeSlotToShift.getEndingTime( ).plus( timeToAdd, ChronoUnit.MINUTES ) );
                    }
                    updateTimeSlot( timeSlotToShift );
                }
                else
                {
                    // Delete this slot (the slot can not be after the
                    // ending time of the day)
                    deleteTimeSlot( timeSlotToShift );
                }
            }
            else
            {
                // The new ending time is before the previous ending
                // time
                timeSlotToShift.setStartingTime( timeSlotToShift.getStartingTime( ).minus( timeToSubstract, ChronoUnit.MINUTES ) );
                timeSlotToShift.setEndingTime( timeSlotToShift.getEndingTime( ).minus( timeToSubstract, ChronoUnit.MINUTES ) );
                updateTimeSlot( timeSlotToShift );
            }
        }

        if ( !bNewEndingTimeIsAfterThePreviousTime )
        {
            // If the slots have been shift earlier,
            // there is no slot(s) between the last slot created
            // and the ending time of the day, need to create it(them)
            List<TimeSlot> listTimeSlotToAdd = generateListTimeSlot( timeSlot.getIdWorkingDay( ), endingTimeOfTheDay.minusMinutes( timeToSubstract ),
                    endingTimeOfTheDay, nDuration, reservationRule.getMaxCapacityPerSlot( ), Boolean.TRUE );
            createListTimeSlot( listTimeSlotToAdd );
        }

    }

    /**
     * Update a time slot without shifting the next time slots
     * 
     * @param timeSlot
     *            the time slot modified
     * @param workingDay
     *            the working day
     * @param reservationRule
     *            the reservation rule
     * @param nDuration
     *            the duration of a time slot
     */
    private static void updateTimeSlotWithoutShift( TimeSlot timeSlot, WorkingDay workingDay, ReservationRule reservationRule, int nDuration )
    {
        List<TimeSlot> listTimeSlotToCreate = new ArrayList<>( );
        LocalTime maxEndingTime = WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDay );
        // Find all the time slot after the starting time of the new
        // time
        // slot
        List<TimeSlot> listAllTimeSlotsAfterThisTimeSlot = findListTimeSlotAfterThisTimeSlot( timeSlot );
        // Need to delete all the time slots impacted (the ones with the
        // starting time before the ending time of the new time slot)
        List<TimeSlot> listAllTimeSlotsToDelete = listAllTimeSlotsAfterThisTimeSlot.stream( )
                .filter( x -> x.getStartingTime( ).isBefore( timeSlot.getEndingTime( ) ) ).collect( Collectors.toList( ) );
        deleteListTimeSlot( listAllTimeSlotsToDelete );
        // Need to find the next time slot (the one with the closest
        // starting time of the ending time of the new time slot)
        listAllTimeSlotsAfterThisTimeSlot.removeAll( listAllTimeSlotsToDelete );
        TimeSlot nextTimeSlot = null;
        if ( CollectionUtils.isNotEmpty( listAllTimeSlotsAfterThisTimeSlot ) )
        {
            nextTimeSlot = listAllTimeSlotsAfterThisTimeSlot.stream( ).min( ( t1, t2 ) -> t1.getStartingTime( ).compareTo( t2.getStartingTime( ) ) ).get( );
        }
        if ( nextTimeSlot != null )
        {
            maxEndingTime = nextTimeSlot.getStartingTime( );
        }
        // and to regenerate time slots between this two ones, with the
        // good
        // rules
        // for the slot capacity
        listTimeSlotToCreate.addAll( generateListTimeSlot( timeSlot.getIdWorkingDay( ), timeSlot.getEndingTime( ), maxEndingTime, nDuration,
                reservationRule.getMaxCapacityPerSlot( ), Boolean.TRUE ) );
        TimeSlotHome.update( timeSlot );
        createListTimeSlot( listTimeSlotToCreate );

    }

    /**
     * Update a time slot
     * 
     * @param timeSlot
     *            the time slot to update
     */
    public static void updateTimeSlot( TimeSlot timeSlot )
    {
        TimeSlotHome.update( timeSlot );
    }

    /**
     * Create in database the slots given
     * 
     * @param listSlotToCreate
     *            the list of slots to create in database
     */
    public static void createListTimeSlot( List<TimeSlot> listTimeSlotToCreate )
    {
        if ( CollectionUtils.isNotEmpty( listTimeSlotToCreate ) )
        {
            for ( TimeSlot timeSlotTemp : listTimeSlotToCreate )
            {
                TimeSlotHome.create( timeSlotTemp );
            }
        }
    }

    /**
     * Find the next time slots of a given time slot
     * 
     * @param timeSlot
     *            the time slot
     * @return a list of the next time slots
     */
    public static List<TimeSlot> findListTimeSlotAfterThisTimeSlot( TimeSlot timeSlot )
    {
        return TimeSlotService.findListTimeSlotByWorkingDay( timeSlot.getIdWorkingDay( ) ).stream( )
                .filter( x -> x.getStartingTime( ).isAfter( timeSlot.getStartingTime( ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * Delete in database time slots
     * 
     * @param listTimeSlot
     *            the list of time slots to delete
     */
    public static void deleteListTimeSlot( List<TimeSlot> listTimeSlot )
    {
        for ( TimeSlot timeSlot : listTimeSlot )
        {
            deleteTimeSlot( timeSlot );
        }
    }

    /**
     * Delete in database time slot
     * 
     * @param timeSlot
     *            the time slot to delete
     */
    public static void deleteTimeSlot( TimeSlot timeSlot )
    {
        TimeSlotHome.delete( timeSlot.getIdTimeSlot( ) );
    }

    /**
     * Get the time slots of a list of working days
     * 
     * @param listWorkingDay
     *            the list of the working days
     * @param dateInWeek
     *            the date in the week
     * @return the list of the time slots
     */
    public static List<TimeSlot> getListTimeSlotOfAListOfWorkingDay( List<WorkingDay> listWorkingDay, LocalDate dateInWeek )
    {
        List<TimeSlot> listTimeSlot = new ArrayList<>( );
        for ( WorkingDay workingDay : listWorkingDay )
        {
            for ( TimeSlot timeSlot : workingDay.getListTimeSlot( ) )
            {
                // Need to add the current date to the hour
                timeSlot.setStartingDateTime( dateInWeek.with( DayOfWeek.of( workingDay.getDayOfWeek( ) ) ).atTime( timeSlot.getStartingTime( ) ) );
                timeSlot.setEndingDateTime( dateInWeek.with( DayOfWeek.of( workingDay.getDayOfWeek( ) ) ).atTime( timeSlot.getEndingTime( ) ) );
                listTimeSlot.add( timeSlot );
            }
        }
        return listTimeSlot;
    }

    /**
     * Return an ordered and filtered list of time slots after a given time
     * 
     * @param listTimeSlot
     *            the list of time slot to sort and filter
     * @param time
     *            the time
     * @return the list ordered and filtered
     */
    public static List<TimeSlot> getNextTimeSlotsInAListOfTimeSlotAfterALocalTime( List<TimeSlot> listTimeSlot, LocalTime time )
    {
        return listTimeSlot.stream( ).filter( x -> x.getStartingTime( ).isAfter( time ) || x.getStartingTime( ).equals( time ) ).collect( Collectors.toList( ) );
    }

    /**
     * Returns the time slot in a list of time slot with the given starting time
     * 
     * @param listTimeSlot
     *            the list of time slots
     * @param timeToSearch
     *            the starting time to search
     * @return the time slot found
     */
    public static TimeSlot getTimeSlotInListOfTimeSlotWithStartingTime( List<TimeSlot> listTimeSlot, LocalTime timeToSearch )
    {
        return listTimeSlot.stream( ).filter( x -> timeToSearch.equals( x.getStartingTime( ) ) ).findFirst( ).orElse( null );
    }
}
