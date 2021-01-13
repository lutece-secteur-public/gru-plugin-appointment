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
package fr.paris.lutece.plugins.appointment.business.planning;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Business class of the time slot
 * 
 * @author Laurent Payen
 *
 */
@JsonIgnoreProperties( ignoreUnknown = true )
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
    public void setSqlStartingTime( Time startingTime )
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
    public void setSqlEndingTime( Time endingTime )
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
