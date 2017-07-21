package fr.paris.lutece.plugins.appointment.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.slot.SlotHome;

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
     * @return a HashMap with the starting Date Time in Key and the corresponding slot in value
     */
    public static HashMap<LocalDateTime, Slot> findSlotsByIdFormAndDateRange( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime )
    {
        return SlotHome.findByIdFormAndDateRange( nIdForm, startingDateTime, endingDateTime );
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
        SlotService.addDateAndTimeToSlot( slot );
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
    public static List<Slot> buildListSlot( int nIdForm, HashMap<LocalDate, WeekDefinition> mapWeekDefinition, LocalDate startingDate, int nNbWeeksToDisplay )
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
        if ( startingDate.isBefore( firstDateOfReservationRule ) )
        {
            startingDateToUse = firstDateOfReservationRule;
        }
        // Add the nb weeks to display to have the ending date (and get the last
        // day of the week : Sunday)
        LocalDate endingDateToDisplay = startingDateToUse.plusWeeks( nNbWeeksToDisplay ).with( DayOfWeek.SUNDAY );
        // Get all the closing day of this period
        List<LocalDate> listDateOfClosingDay = ClosingDayService.findListDateOfClosingDayByIdFormAndDateRange( nIdForm, startingDateToUse, endingDateToDisplay );
        // Get all the slot between these two dates
        HashMap<LocalDateTime, Slot> mapSlot = SlotService.findSlotsByIdFormAndDateRange( nIdForm, startingDateToUse.atStartOfDay( ),
                endingDateToDisplay.atTime( LocalTime.MAX ) );

        // Get or build all the event for the period
        while ( dateTemp.isBefore( endingDateToDisplay ) && !dateTemp.isAfter( endingDateToDisplay ) )
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
            nMaxCapacity = reservationRuleToApply.getMaxCapacityPerSlot( );
            // Get the day of week of the date
            dayOfWeek = dateTemp.getDayOfWeek( );
            // Get the working day of this day of week
            workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( weekDefinitionToApply.getListWorkingDay( ), dayOfWeek );
            // if there is no working day, it's because it is not a working day,
            // so nothing to add in the list of slots
            if ( workingDay != null )
            {
                minTimeForThisDay = WorkingDayService.getMinStartingTimeOfAWorkingDay( workingDay );
                maxTimeForThisDay = WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDay );
                // Check if this day is a closing day
                if ( listDateOfClosingDay.contains( dateTemp ) )
                {
                    listSlot.add( buildSlot( nIdForm, dateTemp.atTime( minTimeForThisDay ), dateTemp.atTime( maxTimeForThisDay ), nMaxCapacity, nMaxCapacity,
                            nMaxCapacity, Boolean.FALSE ) );
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
                                slotToAdd = buildSlot( nIdForm, dateTimeTemp, dateTemp.atTime( timeTemp ), nMaxCapacity, nMaxCapacity, nMaxCapacity,
                                        timeSlot.getIsOpen( ) );
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
            dateTemp = dateTemp.plusDays( 1 );
        }
        return listSlot;
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
    public static Slot buildSlot( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime, int nMaxCapacity, int nNbRemainingPlaces,
            int nNbPotentialRemainingPlaces, boolean bIsOpen )
    {
        Slot slot = new Slot( );
        slot.setIdSlot( 0 );
        slot.setIdForm( nIdForm );
        slot.setStartingDateTime( startingDateTime );
        slot.setEndingDateTime( endingDateTime );
        slot.setMaxCapacity( nMaxCapacity );
        slot.setNbRemainingPlaces( nNbRemainingPlaces );
        slot.setNbPotentialRemainingPlaces( nNbPotentialRemainingPlaces );
        slot.setIsOpen( bIsOpen );
        addDateAndTimeToSlot( slot );
        return slot;
    }

    /**
     * Update a slot in database and possibly all the slots after (if the ending hour has changed, all the next slots are impacted in case of the user decide to
     * shift the next slots)
     * 
     * @param slot
     *            the slot to update
     * @param bEndingTimeHasChanged
     *            true if the ending time has changed
     * @param bShifSlot
     *            true if the user has decided to shift the next slots
     */
    public static void updateSlot( Slot slot, boolean bEndingTimeHasChanged, boolean bShifSlot )
    {
        List<Slot> listSlotToCreate = new ArrayList<>( );
        if ( bEndingTimeHasChanged )
        {
            LocalDate dateOfSlot = slot.getDate( );
            if ( !bShifSlot )
            {
                // Need to get all the slots until the new end of this slot
                List<Slot> listSlotToDelete = new ArrayList<>( SlotService.findSlotsByIdFormAndDateRange( slot.getIdForm( ),
                        slot.getStartingDateTime( ).plusMinutes( 1 ), slot.getEndingDateTime( ) ).values( ) );
                deleteListSlot( listSlotToDelete );
                // Get the list of slot after the modified slot
                HashMap<LocalDateTime, Slot> mapNextSlot = SlotService.findSlotsByIdFormAndDateRange( slot.getIdForm( ), slot.getEndingDateTime( ), slot
                        .getDate( ).atTime( LocalTime.MAX ) );
                List<LocalDateTime> listStartingDateTimeNextSlot = new ArrayList<>( mapNextSlot.keySet( ) );
                // Get the next date time slot
                LocalDateTime nextStartingDateTime = null;
                if ( CollectionUtils.isNotEmpty( listStartingDateTimeNextSlot ) )
                {
                    nextStartingDateTime = Utilities.getClosestDateTimeInFuture( listStartingDateTimeNextSlot, slot.getEndingDateTime( ) );
                }
                else
                {
                    // No slot after this one.
                    // Need to compute between the end of this slot and the next
                    // time slot
                    WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( slot.getIdForm( ), dateOfSlot );
                    WorkingDay workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( weekDefinition.getListWorkingDay( ), dateOfSlot.getDayOfWeek( ) );
                    List<TimeSlot> sortedListTimeSlotAfterThisSlot = TimeSlotService.getSortedListTimeSlotAfterALocalTime( workingDay.getListTimeSlot( ),
                            slot.getEndingTime( ) );
                    TimeSlot nextTimeSlot = sortedListTimeSlotAfterThisSlot.get( 0 );
                    nextStartingDateTime = nextTimeSlot.getStartingTime( ).atDate( dateOfSlot );
                }
                // Need to create a slot between these two dateTime
                if ( !slot.getEndingDateTime( ).isEqual( nextStartingDateTime ) )
                {
                    Slot slotToCreate = buildSlot( slot.getIdForm( ), slot.getEndingDateTime( ), nextStartingDateTime, slot.getMaxCapacity( ),
                            slot.getMaxCapacity( ), slot.getMaxCapacity( ), Boolean.FALSE );
                    listSlotToCreate.add( slotToCreate );
                }
            }
            else
            {
                // Need to delete the slot until the end of the day
                List<Slot> listSlotToDelete = new ArrayList<>( SlotService.findSlotsByIdFormAndDateRange( slot.getIdForm( ),
                        slot.getStartingDateTime( ).plus( 1, ChronoUnit.MINUTES ), slot.getDate( ).atTime( LocalTime.MAX ) ).values( ) );
                deleteListSlot( listSlotToDelete );
                // Generated the new slots at the end of the modified slot
                listSlotToCreate.addAll( generateListSlotToCreateAfterASlot( slot ) );
            }
        }
        saveSlot( slot );
        createListSlot( listSlotToCreate );
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
            slotSaved = SlotHome.create( slot );
        }
        else
        {
            slotSaved = SlotHome.update( slot );
        }
        return slotSaved;
    }

    /**
     * Update a slot
     * 
     * @param slot
     *            the slot updated
     */
    public static void updateSlot( Slot slot )
    {
        SlotHome.update( slot );
    }

    /**
     * Generate the list of slot to create after a slot (taking into account the week definition and the rules to apply)
     * 
     * @param slot
     *            the slot
     * @return the list of next slots
     */
    private static List<Slot> generateListSlotToCreateAfterASlot( Slot slot )
    {
        List<Slot> listSlotToCreate = new ArrayList<>( );
        LocalDate dateOfSlot = slot.getDate( );
        ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( slot.getIdForm( ), dateOfSlot );
        int nMaxCapacity = reservationRule.getMaxCapacityPerSlot( );
        WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( slot.getIdForm( ), dateOfSlot );
        WorkingDay workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( weekDefinition.getListWorkingDay( ), dateOfSlot.getDayOfWeek( ) );
        LocalTime endingTimeOfTheDay = WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDay );
        LocalDateTime endingDateTimeOfTheDay = endingTimeOfTheDay.atDate( dateOfSlot );
        int nDurationSlot = WorkingDayService.getMinDurationTimeSlotOfAWorkingDay( workingDay );
        LocalDateTime startingDateTime = slot.getEndingDateTime( );
        LocalDateTime endingDateTime = startingDateTime.plusMinutes( nDurationSlot );
        int nIdForm = slot.getIdForm( );
        while ( !endingDateTime.isAfter( endingDateTimeOfTheDay ) )
        {
            Slot slotToCreate = buildSlot( nIdForm, startingDateTime, endingDateTime, nMaxCapacity, nMaxCapacity, nMaxCapacity, Boolean.TRUE );
            startingDateTime = endingDateTime;
            endingDateTime = startingDateTime.plusMinutes( nDurationSlot );
            listSlotToCreate.add( slotToCreate );
        }
        return listSlotToCreate;
    }

    /**
     * Form the DTO, adding the date and the time to the slot
     * 
     * @param slot
     *            the slot on which to add values
     */
    public static void addDateAndTimeToSlot( Slot slot )
    {
        slot.setDate( slot.getStartingDateTime( ).toLocalDate( ) );
        slot.setStartingTime( slot.getStartingDateTime( ).toLocalTime( ) );
        slot.setEndingTime( slot.getEndingDateTime( ).toLocalTime( ) );
    }

    /**
     * Create in database the slots given
     * 
     * @param listSlotToCreate
     *            the list of slots to create in database
     */
    private static void createListSlot( List<Slot> listSlotToCreate )
    {
        for ( Slot slotTemp : listSlotToCreate )
        {
            SlotHome.create( slotTemp );
        }
    }

    /**
     * Delete a list of slots
     * 
     * @param listSlotToDelete
     *            the lost of slots to delete
     */
    private static void deleteListSlot( List<Slot> listSlotToDelete )
    {
        for ( Slot slotToDelete : listSlotToDelete )
        {
            SlotHome.delete( slotToDelete.getIdSlot( ) );
        }
    }

    /**
     * Find the first open slot with free places
     * 
     * @param nIdForm
     *            the form Id
     * @param startingDate
     *            the starting date to search
     * @param endingDate
     *            the ending date to search
     * @return the date of the slot found
     */
    public static LocalDate findFirstDateOfFreeOpenSlot( int nIdForm, LocalDate startingDate, LocalDate endingDate )
    {
        boolean bFreeSlotFound = false;
        LocalDate localDateFound = null;
        LocalDate currentDateOfSearch = startingDate;
        List<LocalDate> listClosingDate = ClosingDayService.findListDateOfClosingDayByIdFormAndDateRange( nIdForm, startingDate, endingDate );
        // Get all the slot between these two dates
        HashMap<LocalDateTime, Slot> mapSlot = SlotService.findSlotsByIdFormAndDateRange( nIdForm, startingDate.atStartOfDay( ),
                endingDate.atTime( LocalTime.MAX ) );
        while ( !bFreeSlotFound && ( currentDateOfSearch.isBefore( endingDate ) || !currentDateOfSearch.equals( endingDate ) ) )
        {
            if ( !listClosingDate.contains( currentDateOfSearch ) )
            {
                WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( nIdForm, currentDateOfSearch );
                WorkingDay workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( weekDefinition.getListWorkingDay( ), currentDateOfSearch.getDayOfWeek( ) );
                if ( workingDay != null )
                {
                    LocalTime minTimeForThisDay = WorkingDayService.getMinStartingTimeOfAWorkingDay( workingDay );
                    LocalTime maxTimeForThisDay = WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDay );
                    LocalTime timeTemp = minTimeForThisDay;
                    // For each slot of this day
                    while ( timeTemp.isBefore( maxTimeForThisDay ) || !timeTemp.equals( maxTimeForThisDay ) )
                    {
                        // Get the LocalDateTime
                        LocalDateTime dateTimeTemp = currentDateOfSearch.atTime( timeTemp );
                        // Search if there is a slot for this datetime
                        if ( mapSlot.containsKey( dateTimeTemp ) )
                        {
                            Slot slot = mapSlot.get( dateTimeTemp );
                            if ( slot.getIsOpen( ) && slot.getNbRemainingPlaces( ) > 0 )
                            {
                                bFreeSlotFound = true;
                                localDateFound = currentDateOfSearch;
                                break;
                            }
                            else
                            {
                                timeTemp = slot.getEndingDateTime( ).toLocalTime( );
                            }
                        }
                        else
                        {
                            // Search the timeslot
                            TimeSlot timeSlot = TimeSlotService.getTimeSlotInListOfTimeSlotWithStartingTime( workingDay.getListTimeSlot( ), timeTemp );
                            if ( timeSlot != null )
                            {
                                if ( timeSlot.getIsOpen( ) && timeSlot.getMaxCapacity( ) > 0 )
                                {
                                    bFreeSlotFound = true;
                                    localDateFound = currentDateOfSearch;
                                    break;
                                }
                                else
                                {
                                    timeTemp = timeSlot.getEndingTime( );
                                }
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }
            }
            currentDateOfSearch = currentDateOfSearch.plusDays( 1 );
        }
        return localDateFound;
    }

}
