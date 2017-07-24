package fr.paris.lutece.plugins.appointment.business.planning;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Business class of the time slot
 * 
 * @author Laurent Payen
 *
 */
public final class TimeSlot implements Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3543470088706843432L;

    /**
     * Id of the time slot
     */
    private int _nIdTimeSlot;

    /**
     * Starting time of the slot
     */
    private LocalTime _startingTime;

    /**
     * The starting time + date Need to have the date for the display
     */
    private LocalDateTime _startingDateTime;

    /**
     * Ending time of the slot
     */
    private LocalTime _endingTime;

    /**
     * The endind time + date need to have the date for the display
     */
    private LocalDateTime _endingDateTime;

    /**
     * Indicate whether the time slot is open or not
     */
    private boolean _bIsOpen;

    /**
     * Max capacity for this slot
     */
    private int _nMaxCapacity;
    /**
     * Working day id the time slot belongs to
     */
    private int _nIdWorkingDay;

    /**
     * Get the id of the time slot
     * 
     * @return
     */
    public int getIdTimeSlot( )
    {
        return _nIdTimeSlot;
    }

    /**
     * Set the id of the time slot
     * 
     * @param nIdTimeSlot
     *            the id to set
     */
    public void setIdTimeSlot( int nIdTimeSlot )
    {
        this._nIdTimeSlot = nIdTimeSlot;
    }

    /**
     * Get the starting time of the time slot
     * 
     * @return the starting time of the time slot
     */
    public LocalTime getStartingTime( )
    {
        return _startingTime;
    }

    /**
     * Get the starting time of the time slot (in sql time)
     * 
     * @return the starting time
     */
    public Time getStartingTimeSqlTime( )
    {
        Time time = null;
        if ( _startingTime != null )
        {
            time = Time.valueOf( _startingTime );
        }
        return time;
    }

    /**
     * Set the starting time of the time slot
     * 
     * @param startingTime
     *            the starting time to set
     */
    public void setStartingTime( LocalTime startingTime )
    {
        this._startingTime = startingTime;
    }

    /**
     * Set the starting time of the time slot
     * 
     * @param startingTime
     *            the starting time (in sql time)
     */
    public void setStartingTime( Time startingTime )
    {
        if ( startingTime != null )
        {
            this._startingTime = startingTime.toLocalTime( );
        }
    }

    /**
     * Get the ending time of the time slot
     * 
     * @return the ending time of the time slot
     */
    public LocalTime getEndingTime( )
    {
        return _endingTime;
    }

    /**
     * Get the ending time in sql time
     * 
     * @return the ending time in sql time
     */
    public Time getEndingTimeSqlTime( )
    {
        Time time = null;
        if ( this._endingTime != null )
        {
            time = Time.valueOf( _endingTime );
        }
        return time;
    }

    /**
     * Set the ending time of the time slot
     * 
     * @param endingTime
     *            the ending time to set
     */
    public void setEndingTime( LocalTime endingTime )
    {
        this._endingTime = endingTime;
    }

    /**
     * Set the ending time of the time slot
     * 
     * @param endingTime
     *            the ending time (in sql time format)
     */
    public void setEndingTime( Time endingTime )
    {
        if ( endingTime != null )
        {
            this._endingTime = endingTime.toLocalTime( );
        }
    }

    /**
     * Indicate whether the time slot is open or not
     * 
     * @return true if the time slot is open
     */
    public boolean getIsOpen( )
    {
        return _bIsOpen;
    }

    /**
     * Set the opening boolean value of the time slot
     * 
     * @param bIsOpen
     *            the opening boolean value
     */
    public void setIsOpen( boolean bIsOpen )
    {
        this._bIsOpen = bIsOpen;
    }

    /**
     * Get the maximum capacity of the slot
     * 
     * @return the maximum capacity
     */
    public int getMaxCapacity( )
    {
        return _nMaxCapacity;
    }

    /**
     * Set the maximum capacity of the slot
     * 
     * @param nMaxCapacity
     *            the maximum capacity
     */
    public void setMaxCapacity( int nMaxCapacity )
    {
        this._nMaxCapacity = nMaxCapacity;
    }

    /**
     * Get the working day id the time slot belongs to
     * 
     * @return the working day id the time slot belongs to
     */
    public int getIdWorkingDay( )
    {
        return _nIdWorkingDay;
    }

    /**
     * Set the working day id the time slot belongs to
     * 
     * @param nIdWorkingDay
     *            the working day id to set
     */
    public void setIdWorkingDay( int nIdWorkingDay )
    {
        this._nIdWorkingDay = nIdWorkingDay;
    }

    public LocalDateTime getStartingDateTime( )
    {
        return _startingDateTime;
    }

    public void setStartingDateTime( LocalDateTime startingDateTime )
    {
        this._startingDateTime = startingDateTime;
    }

    public LocalDateTime getEndingDateTime( )
    {
        return _endingDateTime;
    }

    public void setEndingDateTime( LocalDateTime endingDateTime )
    {
        this._endingDateTime = endingDateTime;
    }

}
