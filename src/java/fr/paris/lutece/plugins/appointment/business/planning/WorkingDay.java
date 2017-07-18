package fr.paris.lutece.plugins.appointment.business.planning;

import java.io.Serializable;
import java.util.List;

/**
 * Business class of the working day
 * 
 * @author Laurent Payen
 *
 */
public class WorkingDay implements Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 2628086182559019071L;

    /**
     * Id of the working day
     */
    private int _nIdWorkingDay;

    /**
     * Day of the week
     */
    private int _nDayOfWeek;

    /**
     * Week id the working day belongs to
     */
    private int _nIdWeekDefinition;

    /**
     * List of the time slots of the working day
     */
    private List<TimeSlot> _listTimeSlots;

    /**
     * Get the id of the working day
     * 
     * @return the id of the working day
     */
    public int getIdWorkingDay( )
    {
        return _nIdWorkingDay;
    }

    /**
     * Set the id of the working day
     * 
     * @param nIdWorkingDay
     *            the id to set
     */
    public void setIdWorkingDay( int nIdWorkingDay )
    {
        this._nIdWorkingDay = nIdWorkingDay;
    }

    /**
     * Get the day of week of the working day
     * 
     * @return the day of week
     */
    public int getDayOfWeek( )
    {
        return _nDayOfWeek;
    }

    /**
     * Set the day of week of the working day
     * 
     * @param nDayOfWeek
     *            the day of week to set
     */
    public void setDayOfWeek( int nDayOfWeek )
    {
        this._nDayOfWeek = nDayOfWeek;
    }

    /**
     * Get the id of the week definition the working day belongs to
     * 
     * @return the id of the week definition the working day belongs to
     */
    public int getIdWeekDefinition( )
    {
        return _nIdWeekDefinition;
    }

    /**
     * Set the id of the week definition the working day belongs to
     * 
     * @param nIdWeekDefinition
     *            the id to set
     */
    public void setIdWeekDefinition( int nIdWeekDefinition )
    {
        this._nIdWeekDefinition = nIdWeekDefinition;
    }

    /**
     * Get the time slots of the working day
     * 
     * @return the list of the time slots of the working day
     */
    public List<TimeSlot> getListTimeSlot( )
    {
        return _listTimeSlots;
    }

    /**
     * Set the time slots of the working day
     * 
     * @param listTimeSlots
     *            the list of time slots to set
     */
    public void setListTimeSlot( List<TimeSlot> listTimeSlots )
    {
        this._listTimeSlots = listTimeSlots;
    }

}
