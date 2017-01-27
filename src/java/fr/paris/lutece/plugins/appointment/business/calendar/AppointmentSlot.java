/*
 * Copyright (c) 2002-2015, Mairie de Paris
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

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;

/**
 * Slot for an appointment in a day. The slot may have 3 states : free, partially free or occupied.
 */
public class AppointmentSlot implements Comparable<AppointmentSlot>, Serializable, Cloneable
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -3143858870219992098L;
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
    private int _NbRDV;
    private LocalDate _dateTest;
    private LocalTime _timeTest;
    private LocalDateTime _timestampTest;
    private AppointmentForm form;
        
    
    public AppointmentForm getForm() {
		return form;
	}
	public void setForm(AppointmentForm form) {
		this.form = form;
	}
	
	public Date getSqlDateTest() {
    	Date sqlDate = null;
    	if (_dateTest != null ) {
    		sqlDate = Date.valueOf(_dateTest);
    	}
    	return sqlDate;
    }
    public LocalDate getDateTest() {
		return _dateTest;
	}

	public void setDateTest(LocalDate dateTest) {
		this._dateTest = dateTest;
		this._dateTest.atStartOfDay();
	}

	public void setDateTest(Date date) {
		if (date != null) {
			this._dateTest = date.toLocalDate();
		}
	}
	
	public LocalTime getTimeTest() {
		return _timeTest;
	}
	
	public Time getSqlTimeTest() {
		Time sqlTime = null;
		if (_timeTest != null) {
			sqlTime = Time.valueOf(_timeTest);
		}
		return sqlTime;
	}

	public void setTimeTest(LocalTime timeTest) {
		this._timeTest = timeTest;		
	}
	
	public void setTimeTest(Time time) {
		if (time != null) {
			this._timeTest = time.toLocalTime();
		}		
	}

	public LocalDateTime getTimestampTest() {
		return _timestampTest;
	}

	public Timestamp getSqlTimeStampTest() {
		Timestamp sqlTimestamp = null;
		if (_timestampTest != null){
			sqlTimestamp = Timestamp.valueOf(_timestampTest);
		}
		return sqlTimestamp;
	}
	
	public void setTimestampTest(LocalDateTime timestampTest) {		
		this._timestampTest = timestampTest;
	}
	
	public void setTimestampTest(Timestamp timestampTest) {
		if (timestampTest != null){
			this._timestampTest = timestampTest.toLocalDateTime();
		}
	}

	/**
	 * Get the id of the slot
	 * 
	 * @return The id of the slot
	 */
    public int getIdSlot( )
    {
        return _nIdSlot;
    }

    /**
     * Set the id of the slot
     * 
     * @param nIdSlot
     *            The id of the slot
     */
    public void setIdSlot( int nIdSlot )
    {
        this._nIdSlot = nIdSlot;
    }

    /**
     * Get the id of the form
     * 
     * @return The id of the form
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the id of the form
     * 
     * @param nIdForm
     *            The id of the form
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

    /**
     * Get the id of the day, or 0 if this slot is not associated with a day but only with a form
     * 
     * @return The of the day
     */
    public int getIdDay( )
    {
        return _nIdDay;
    }

    /**
     * Set the id of the day
     * 
     * @param nIdDay
     *            The of the day, or 0 if this slot is not associated with a day but only with a form
     */
    public void setIdDay( int nIdDay )
    {
        this._nIdDay = nIdDay;
    }

    /**
     * Get the number of the day in the week.
     * 
     * @return The number of the day in the week. Returns 1 for Monday, 2 for Tuesday, ..., 7 for Sunday.
     */
    public int getDayOfWeek( )
    {
        return _nDayOfWeek;
    }

    /**
     * Set the day of the week
     * 
     * @param nDayOfWeek
     *            The number of the day in the week : 1 for Monday, 2 for Tuesday, ..., 7 for Sunday.
     */
    public void setDayOfWeek( int nDayOfWeek )
    {
        this._nDayOfWeek = nDayOfWeek;
    }

    /**
     * Get the number of places for this slot
     * 
     * @return The number of places for this slot
     */
    public int getNbPlaces( )
    {
        return _nNbPlaces;
    }

    /**
     * Set the number of places for this slot
     * 
     * @param nNbPlaces
     *            The number of places for this slot
     */
    public void setNbPlaces( int nNbPlaces )
    {
        this._nNbPlaces = nNbPlaces;
    }

    /**
     * Get the number of free places for this slot
     * 
     * @return The number of free places for this slot
     */
    public int getNbFreePlaces( )
    {
        return _nNbFreePlaces;
    }

    /**
     * Set the number of free places for this slot
     * 
     * @param nNbFreePlaces
     *            The number of free places for this slot
     */
    public void setNbFreePlaces( int nNbFreePlaces )
    {
        this._nNbFreePlaces = nNbFreePlaces;
    }

    /**
     * Get the starting hour of this slot
     * 
     * @return The starting hour of this slot
     */
    public int getStartingHour( )
    {
        return _nStartingHour;
    }

    /**
     * Set the starting hour of this slot
     * 
     * @param nStartingHour
     *            The starting hour of this slot
     */
    public void setStartingHour( int nStartingHour )
    {
        this._nStartingHour = nStartingHour;
    }

    /**
     * Get the starting minute of this slot
     * 
     * @return The starting minute of this slot
     */
    public int getStartingMinute( )
    {
        return _nStartingMinute;
    }

    /**
     * Set the starting minute of this slot
     * 
     * @param nStartingMinute
     *            The starting minute of this slot
     */
    public void setStartingMinute( int nStartingMinute )
    {
        this._nStartingMinute = nStartingMinute;
    }

    /**
     * Get the ending hour of this slot
     * 
     * @return The ending hour of this slot
     */
    public int getEndingHour( )
    {
        return _nEndingHour;
    }

    /**
     * Set the ending hour of this slot
     * 
     * @param nEndingHour
     *            The ending hour of this slot
     */
    public void setEndingHour( int nEndingHour )
    {
        this._nEndingHour = nEndingHour;
    }

    /**
     * Get the ending minute of this slot
     * 
     * @return The ending minute of this slot
     */
    public int getEndingMinute( )
    {
        return _nEndingMinute;
    }

    /**
     * Set the ending minute of this slot
     * 
     * @param nEndingMinute
     *            The ending minute of this slot
     */
    public void setEndingMinute( int nEndingMinute )
    {
        this._nEndingMinute = nEndingMinute;
    }

    /**
     * Check if this slot is enabled for appointments or not
     * 
     * @return True if this slot is enabled, false otherwise
     */
    public boolean getIsEnabled( )
    {
        return _bIsEnabled;
    }

    /**
     * Enable or disable this slot for appointments
     * 
     * @param bIsEnabled
     *            True to enable this slot for appointments, false otherwise
     */
    public void setIsEnabled( boolean bIsEnabled )
    {
        this._bIsEnabled = bIsEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o )
    {
        if ( !( o instanceof AppointmentSlot ) )
        {
            return false;
        }

        AppointmentSlot other = (AppointmentSlot) o;

        return compareTo( other ) == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode( )
    {
        return ( ( ( ( ( ( getStartingHour( ) * 60 ) + getStartingMinute( ) ) * 10 ) + getDayOfWeek( ) ) * 1000 ) + ( getEndingHour( ) * 60 ) + getEndingMinute( ) );
    }

    /**
     * Compare a slot to another one. Slots are compared by theire staring hour, starting minute, ending hour, ending minute and day of week.
     * 
     * @param o
     *            Slot to compare this slot to
     * @return 1 of the given slot is greater that the current one, -1 if the other slot is lower than the current one, 0 if they are equals
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

    /**
     * {@inheritDoc}
     */
    @Override
    public AppointmentSlot clone( )
    {
        try
        {
            return (AppointmentSlot) super.clone( );
        }
        catch( CloneNotSupportedException e )
        {
            // Do nothing
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString( )
    {
        return "AppointmentSlot [_nIdSlot=" + _nIdSlot + ", _nIdForm=" + _nIdForm + ", _nIdDay=" + _nIdDay + ", _nDayOfWeek=" + _nDayOfWeek + ", _nNbPlaces="
                + _nNbPlaces + ", _nNbFreePlaces=" + _nNbFreePlaces + ", _nStartingHour=" + _nStartingHour + ", _nStartingMinute=" + _nStartingMinute
                + ", _nEndingHour=" + _nEndingHour + ", _nEndingMinute=" + _nEndingMinute + ", _bIsEnabled=" + _bIsEnabled + "]";
    }

    public int getNbRDV( )
    {
        return _NbRDV;
    }

    public void setNbRDV( int nbRDV )
    {
        this._NbRDV = nbRDV;
    }
}
