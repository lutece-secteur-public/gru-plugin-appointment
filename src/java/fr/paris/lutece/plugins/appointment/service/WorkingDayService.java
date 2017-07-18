package fr.paris.lutece.plugins.appointment.service;

import static java.lang.Math.toIntExact;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDayHome;

/**
 * Service class of a working day
 * 
 * @author Laurent Payen
 *
 */
public class WorkingDayService
{

    /**
     * Create in database a working day object with the given parameters
     * 
     * @param nIdWeekDefinition
     *            the week definition Id
     * @param dayOfWeek
     *            the day of week
     * @return the working day object built
     */
    public static WorkingDay generateWorkingDay( int nIdWeekDefinition, DayOfWeek dayOfWeek )
    {
        WorkingDay workingDay = new WorkingDay( );
        workingDay.setIdWeekDefinition( nIdWeekDefinition );
        workingDay.setDayOfWeek( dayOfWeek.getValue( ) );
        WorkingDayHome.create( workingDay );
        return workingDay;
    }

    /**
     * Create in database a working day and its time slots
     * 
     * @param nIdWeekDefinition
     *            the week definition Id
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
    public static void generateWorkingDayAndListTimeSlot( int nIdWeekDefinition, DayOfWeek dayOfWeek, LocalTime startingTime, LocalTime endingTime,
            int nDuration, int nMaxCapacity )
    {
        WorkingDay workingDay = generateWorkingDay( nIdWeekDefinition, dayOfWeek );
        TimeSlotService.generateListTimeSlot( workingDay.getIdWorkingDay( ), startingTime, endingTime, nDuration, nMaxCapacity );
    }

    /**
     * Get the open days of an appointmentForm DTO
     * 
     * @param appointmentForm
     *            the appointmentForm DTO
     * @return the list of day of week opened
     */
    public static List<DayOfWeek> getOpenDays( AppointmentForm appointmentForm )
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
    public static List<WorkingDay> findListWorkingDayByWeekDefinition( int nIdWeekDefinition )
    {
        List<WorkingDay> listWorkingDay = WorkingDayHome.findByIdWeekDefinition( nIdWeekDefinition );
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
        WorkingDay workingDay = WorkingDayHome.findByPrimaryKey( nIdWorkingDay );
        return workingDay;
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
        return workingDay.getListTimeSlot( ).stream( ).map( TimeSlot::getEndingTime ).max( LocalTime::compareTo ).get( );
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
        LocalTime endingTimeTemp;
        for ( WorkingDay workingDay : listWorkingDay )
        {
            endingTimeTemp = getMaxEndingTimeOfAWorkingDay( workingDay );
            if ( maxEndingTime == null || endingTimeTemp.isAfter( maxEndingTime ) )
            {
                maxEndingTime = endingTimeTemp;
            }
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
        return workingDay.getListTimeSlot( ).stream( ).map( TimeSlot::getStartingTime ).min( LocalTime::compareTo ).get( );
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
    public static HashSet<String> getSetDayOfWeekOfAListOfWorkingDay( List<WorkingDay> listWorkingDay )
    {
        HashSet<String> setDayOfWeek = new HashSet<>( );
        for ( WorkingDay workingDay : listWorkingDay )
        {
            setDayOfWeek.add( new Integer( workingDay.getDayOfWeek( ) ).toString( ) );
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
