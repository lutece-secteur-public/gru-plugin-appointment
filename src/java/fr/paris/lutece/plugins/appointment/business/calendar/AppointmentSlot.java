/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.appointment.business.calendar;

/**
 * Slot for an appointment in a day. The slot may have 3 states : free,
 * partially free or occupied.
 */
public class AppointmentSlot implements Comparable<AppointmentSlot>
{
    private int _nIdSlot;
    private int _nIdForm;
    private int _nIdDay;
    private int _nDayOfWeek;
    private int _nNbPlaces;
    private int _nNbFreePlaces;
    private int _nStartingHour;
    private int _nStartingMinute;
    private int _nEndingHour;
    private int _nEndingMinute;
    private boolean _bIsEnabled;

    /**
     * Get the id of the slot
     * @return The id of the slot
     */
    public int getIdSlot( )
    {
        return _nIdSlot;
    }

    /**
     * Set the id of the slot
     * @param nIdSlot The id of the slot
     */
    public void setIdSlot( int nIdSlot )
    {
        this._nIdSlot = nIdSlot;
    }

    /**
     * Get the id of the form
     * @return The id of the form
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the id of the form
     * @param nIdForm The id of the form
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

    /**
     * Get the id of the day, or 0 if this slot is not associated with a day but
     * only with a form
     * @return The of the day
     */
    public int getIdDay( )
    {
        return _nIdDay;
    }

    /**
     * Set the id of the day
     * @param nIdDay The of the day, or 0 if this slot is not associated with a
     *            day but only with a form
     */
    public void setIdDay( int nIdDay )
    {
        this._nIdDay = nIdDay;
    }

    /**
     * Get the number of the day in the week.
     * @return The number of the day in the week. Returns 1 for Monday, 2 for
     *         Tuesday, ..., 7 for Sunday.
     */
    public int getDayOfWeek( )
    {
        return _nDayOfWeek;
    }

    /**
     * Set the day of the week
     * @param nDayOfWeek The number of the day in the week : 1 for Monday, 2 for
     *            Tuesday, ..., 7 for Sunday.
     */
    public void setDayOfWeek( int nDayOfWeek )
    {
        this._nDayOfWeek = nDayOfWeek;
    }

    /**
     * Get the number of places for this slot
     * @return The number of places for this slot
     */
    public int getNbPlaces( )
    {
        return _nNbPlaces;
    }

    /**
     * Set the number of places for this slot
     * @param nNbPlaces The number of places for this slot
     */
    public void setNbPlaces( int nNbPlaces )
    {
        this._nNbPlaces = nNbPlaces;
    }

    /**
     * Get the number of free places for this slot
     * @return The number of free places for this slot
     */
    public int getNbFreePlaces( )
    {
        return _nNbFreePlaces;
    }

    /**
     * Set the number of free places for this slot
     * @param nNbFreePlaces The number of free places for this slot
     */
    public void setNbFreePlaces( int nNbFreePlaces )
    {
        this._nNbFreePlaces = nNbFreePlaces;
    }

    /**
     * Get the starting hour of this slot
     * @return The starting hour of this slot
     */
    public int getStartingHour( )
    {
        return _nStartingHour;
    }

    /**
     * Set the starting hour of this slot
     * @param nStartingHour The starting hour of this slot
     */
    public void setStartingHour( int nStartingHour )
    {
        this._nStartingHour = nStartingHour;
    }

    /**
     * Get the starting minute of this slot
     * @return The starting minute of this slot
     */
    public int getStartingMinute( )
    {
        return _nStartingMinute;
    }

    /**
     * Set the starting minute of this slot
     * @param nStartingMinute The starting minute of this slot
     */
    public void setStartingMinute( int nStartingMinute )
    {
        this._nStartingMinute = nStartingMinute;
    }

    /**
     * Get the ending hour of this slot
     * @return The ending hour of this slot
     */
    public int getEndingHour( )
    {
        return _nEndingHour;
    }

    /**
     * Set the ending hour of this slot
     * @param nEndingHour The ending hour of this slot
     */
    public void setEndingHour( int nEndingHour )
    {
        this._nEndingHour = nEndingHour;
    }

    /**
     * Get the ending minute of this slot
     * @return The ending minute of this slot
     */
    public int getEndingMinute( )
    {
        return _nEndingMinute;
    }

    /**
     * Set the ending minute of this slot
     * @param nEndingMinute The ending minute of this slot
     */
    public void setEndingMinute( int nEndingMinute )
    {
        this._nEndingMinute = nEndingMinute;
    }

    /**
     * Check if this slot is enabled for appointments or not
     * @return True if this slot is enabled, false otherwise
     */
    public boolean getIsEnabled( )
    {
        return _bIsEnabled;
    }

    /**
     * Enable or disable this slot for appointments
     * @param bIsEnabled True to enable this slot for appointments, false
     *            otherwise
     */
    public void setIsEnabled( boolean bIsEnabled )
    {
        this._bIsEnabled = bIsEnabled;
    }

    /**
     * Compare a slot to another one. Slots are compared by theire staring hour,
     * starting minute, ending hour, ending minute and day of week.
     * @param o Slot to compare this slot to
     */
    @Override
    public int compareTo( AppointmentSlot o )
    {
        if ( getStartingHour( ) != o.getStartingHour( ) )
        {
            return ( getStartingHour( ) > o.getStartingHour( ) ) ? 1 : ( -1 );
        }

        if ( getStartingMinute( ) != o.getStartingMinute( ) )
        {
            return ( getStartingMinute( ) > o.getStartingMinute( ) ) ? 1 : ( -1 );
        }

        if ( getDayOfWeek( ) != o.getDayOfWeek( ) )
        {
            return ( getDayOfWeek( ) > o.getDayOfWeek( ) ) ? 1 : ( -1 );
        }

        if ( getEndingHour( ) != o.getEndingHour( ) )
        {
            return ( getEndingHour( ) > o.getEndingHour( ) ) ? 1 : ( -1 );
        }

        if ( getEndingMinute( ) != o.getEndingMinute( ) )
        {
            return ( getEndingMinute( ) > o.getEndingMinute( ) ) ? 1 : ( -1 );
        }

        return 0;
    }
}
