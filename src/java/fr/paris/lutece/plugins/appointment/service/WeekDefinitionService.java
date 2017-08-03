package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinitionHome;
import fr.paris.lutece.util.ReferenceList;

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
     * @param nIdForm
     *            the form Id
     * @param dateOfApply
     *            the date of the week definition
     * @return the week definition created
     */
    public static WeekDefinition createWeekDefinition( int nIdForm, LocalDate dateOfApply )
    {
        WeekDefinition weekDefinition = new WeekDefinition( );
        fillInWeekDefinition( weekDefinition, nIdForm, dateOfApply );
        WeekDefinitionHome.create( weekDefinition );
        return weekDefinition;
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
        return WeekDefinitionHome.create( weekDefinition );
    }

    /**
     * Update in database a week definition
     * 
     * @param nIdForm
     *            the form Id
     * @param dateOfApply
     *            the date of the week definition
     * @return the week definition updated
     */
    public static WeekDefinition updateWeekDefinition( int nIdForm, LocalDate dateOfApply )
    {
        WeekDefinition weekDefinition = WeekDefinitionHome.findByIdFormAndDateOfApply( nIdForm, dateOfApply );
        if ( weekDefinition == null )
        {
            weekDefinition = createWeekDefinition( nIdForm, dateOfApply );
        }
        else
        {
            fillInWeekDefinition( weekDefinition, nIdForm, dateOfApply );
            WeekDefinitionHome.update( weekDefinition );
        }
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
    public static void fillInWeekDefinition( WeekDefinition weekDefinition, int nIdForm, LocalDate dateOfApply )
    {
        weekDefinition.setDateOfApply( dateOfApply );
        weekDefinition.setIdForm( nIdForm );
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
        List<WeekDefinition> listWeekDefinition = WeekDefinitionHome.findByIdForm( nIdForm );
        fillInListWeekDefinition( listWeekDefinition );
        return listWeekDefinition;
    }

    /**
     * Fill all the week definitions with their working days
     * 
     * @param listWeekDefinition
     *            the list of week definition
     */
    private static void fillInListWeekDefinition( List<WeekDefinition> listWeekDefinition )
    {
        for ( WeekDefinition weekDefinition : listWeekDefinition )
        {
            fillInWeekDefinition( weekDefinition );
        }
    }

    /**
     * Fill a week definition with its working days
     * 
     * @param weekDefinition
     *            the week definition to fill in
     */
    private static void fillInWeekDefinition( WeekDefinition weekDefinition )
    {
        weekDefinition.setListWorkingDay( WorkingDayService.findListWorkingDayByWeekDefinition( weekDefinition.getIdWeekDefinition( ) ) );
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
        List<WeekDefinition> listWeekDefinition = WeekDefinitionHome.findByIdForm( nIdForm );
        List<LocalDate> listDate = new ArrayList<>( );
        for ( WeekDefinition weekDefinition : listWeekDefinition )
        {
            listDate.add( weekDefinition.getDateOfApply( ) );
        }
        LocalDate closestDate = Utilities.getClosestDateInPast( listDate, dateOfApply );
        WeekDefinition weekDefinition = listWeekDefinition.stream( ).filter( x -> closestDate.isEqual( x.getDateOfApply( ) ) ).findAny( ).orElse( null );
        weekDefinition.setListWorkingDay( WorkingDayService.findListWorkingDayByWeekDefinition( weekDefinition.getIdWeekDefinition( ) ) );
        return weekDefinition;
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
        WeekDefinition weekDefinition = WeekDefinitionHome.findByPrimaryKey( nIdWeekDefinition );
        weekDefinition.setListWorkingDay( WorkingDayService.findListWorkingDayByWeekDefinition( weekDefinition.getIdWeekDefinition( ) ) );
        return weekDefinition;
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
            weekDefinition.setListWorkingDay( WorkingDayService.findListWorkingDayByWeekDefinition( weekDefinition.getIdWeekDefinition( ) ) );
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
    public static LocalTime getMinStartingTimeOfAListOfWeekDefinition( List<WeekDefinition> listWeekDefinition )
    {
        LocalTime minStartingTime = null;
        LocalTime startingTimeTemp;
        for ( WeekDefinition weekDefinition : listWeekDefinition )
        {
            startingTimeTemp = getMinStartingTimeOfAWeekDefinition( weekDefinition );
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
    public static LocalTime getMinStartingTimeOfAWeekDefinition( WeekDefinition weekDefinition )
    {
        return WorkingDayService.getMinStartingTimeOfAListOfWorkingDay( weekDefinition.getListWorkingDay( ) );
    }

    /**
     * Return the max ending time of a list of week definitions
     * 
     * @param listWeekDefinition
     *            the list of week definitions
     * @return the max ending time of the list of week definitions
     */
    public static LocalTime getMaxEndingTimeOfAListOfWeekDefinition( List<WeekDefinition> listWeekDefinition )
    {
        LocalTime maxEndingTime = null;
        LocalTime endingTimeTemp;
        for ( WeekDefinition weekDefinition : listWeekDefinition )
        {
            endingTimeTemp = getMaxEndingTimeOfAWeekDefinition( weekDefinition );
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
    public static LocalTime getMaxEndingTimeOfAWeekDefinition( WeekDefinition weekDefinition )
    {
        return WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay( weekDefinition.getListWorkingDay( ) );
    }

    /**
     * Get the min duration of a time slot of a list of week definition
     * 
     * @param listWeekDefinition
     *            the list of the week definitions
     * @return the min duration time slot
     */
    public static int getMinDurationTimeSlotOfAListOfWeekDefinition( List<WeekDefinition> listWeekDefinition )
    {
        int nMinDuration = 0;
        int nDurationTemp;
        for ( WeekDefinition weekDefinition : listWeekDefinition )
        {
            nDurationTemp = getMinDurationTimeSlotOfAWeekDefinition( weekDefinition );
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
    public static int getMinDurationTimeSlotOfAWeekDefinition( WeekDefinition weekDefinition )
    {
        return WorkingDayService.getMinDurationTimeSlotOfAListOfWorkingDay( weekDefinition.getListWorkingDay( ) );
    }

    /**
     * Get the working days integer enum values of a list of week definitions
     * 
     * @param listWeekDefinition
     *            the list of week deifnitions
     * @return a set of the working days (integer value in a week : 1-> Monday ...)
     */
    public static HashSet<String> getSetDayOfWeekOfAListOfWeekDefinition( List<WeekDefinition> listWeekDefinition )
    {
        HashSet<String> setDayOfWeek = new HashSet<>( );
        for ( WeekDefinition weekDefinition : listWeekDefinition )
        {
            setDayOfWeek.addAll( WorkingDayService.getSetDayOfWeekOfAListOfWorkingDay( weekDefinition.getListWorkingDay( ) ) );
        }
        return setDayOfWeek;
    }

}
