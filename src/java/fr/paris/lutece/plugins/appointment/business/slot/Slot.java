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
package fr.paris.lutece.plugins.appointment.business.slot;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The business class of the slot
 * 
 * @author Laurent Payen
 *
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public final class Slot implements Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 9054234926836931062L;

    /**
     * Id of the slot
     */
    private int _nIdSlot;

    /**
     * Date of the slot
     */
    private LocalDate _date;

    /**
     * Starting Time of The Slot
     */
    private LocalTime _startingTime;

    /**
     * Ending Time Of The Slot
     */
    private LocalTime _endingTime;

    /**
     * Starting date (Date + Time) of the slot
     */
    private LocalDateTime _startingDateTime;

    /**
     * Ending date (Date + Time) of the slot
     */
    private LocalDateTime _endingDateTime;

    /**
     * Indicate whether the slot is open or not
     */
    private boolean _bIsOpen;

    /**
     * Indicate whether the slot has been created with the specific week configuration
     */
    private boolean _bIsSpecific;

    /**
     * Indicate whether the slot is passed or not
     */
    private boolean _bIsPassed = Boolean.FALSE;

    /**
     * Max Capacity of the Slot
     */
    private int _nMaxCapacity;

    /**
     * Nb Remaining Places of the slot
     */
    private int _nNbRemainingPlaces;

    /**
     * Nb Potential Remaining Places of the slot
     */
    private int _nNbPotentialRemainingPlaces;

    /**
     * Nb Places Taken
     */
    private int _nNbPlacesTaken;

    /**
     * Form Id the slot belongs to (foreign key)
     */
    private int _nIdForm;

    /**
     * Get the id of the slot
     * 
     * @return the id of the slot
     */
    public int getIdSlot( )
    {
        return _nIdSlot;
    }

    /**
     * Get the date of the slot
     * 
     * @return the date of the slot
     */
    public LocalDate getDate( )
    {
        return _date;
    }

    /**
     * Set the date of the slot
     * 
     * @param date
     *            the date to set
     */
    public void setDate( LocalDate date )
    {
        this._date = date;
    }

    /**
     * Get the starting time of the slot
     * 
     * @return the starting time of the slot
     */
    public LocalTime getStartingTime( )
    {
        return _startingTime;
    }

    /**
     * Set the starting time of the slot
     * 
     * @param startingTime
     *            the starting time to set
     */
    public void setStartingTime( LocalTime startingTime )
    {
        this._startingTime = startingTime;
    }

    /**
     * Get the ending time of the slot
     * 
     * @return the ending time of the slot
     */
    public LocalTime getEndingTime( )
    {
        return _endingTime;
    }

    /**
     * Set the ending time of the slot
     * 
     * @param endingTime
     *            the ending time to set
     */
    public void setEndingTime( LocalTime endingTime )
    {
        this._endingTime = endingTime;
    }

    /**
     * Set the id of the slot
     * 
     * @param nIdSlot
     *            the id to set
     */
    public void setIdSlot( int nIdSlot )
    {
        this._nIdSlot = nIdSlot;
    }

    /**
     * Get the starting date of the slot
     * 
     * @return the starting date of the slot
     */
    public LocalDateTime getStartingDateTime( )
    {
        return _startingDateTime;
    }

    /**
     * Get the starting date of the slot
     * 
     * @return the starting date of the slot (in Sql Timestamp format)
     */
    public Timestamp getStartingTimestampDate( )
    {
        Timestamp timestamp = null;
        if ( this._startingDateTime != null )
        {
            timestamp = Timestamp.valueOf( this._startingDateTime );
        }
        return timestamp;
    }

    /**
     * Set the starting date of the slot
     * 
     * @param startingDateTime
     *            the starting date to set
     */
    public void setStartingDateTime( LocalDateTime startingDateTime )
    {
        this._startingDateTime = startingDateTime;
    }

    /**
     * Set the starting date of the slot
     * 
     * @param startingTimeStampDate
     *            the starting date to set (in Timestamp format)
     */
    public void setStartingTimeStampDate( Timestamp startingTimeStampDate )
    {
        if ( startingTimeStampDate != null )
        {
            this._startingDateTime = startingTimeStampDate.toLocalDateTime( );
        }
    }

    /**
     * Get the ending date of the slot
     * 
     * @return the ending date of the slot
     */
    public LocalDateTime getEndingDateTime( )
    {
        return _endingDateTime;
    }

    /**
     * Get the ending date of the slot
     * 
     * @return the ending date of the slot (in Sql Timestamp format)
     */
    public Timestamp getEndingTimestampDate( )
    {
        Timestamp timestamp = null;
        if ( this._endingDateTime != null )
        {
            timestamp = Timestamp.valueOf( _endingDateTime );
        }
        return timestamp;
    }

    /**
     * Set the ending date of the slot
     * 
     * @param endingDateTime
     *            the ending date of the slot (in LocalDateTime format)
     */
    public void setEndingDateTime( LocalDateTime endingDateTime )
    {
        this._endingDateTime = endingDateTime;
    }

    /**
     * Set the ending date of the slot
     * 
     * @param endingTimeStampDate
     *            the ending date of the slot (in Timestamp format)
     */
    public void setEndingTimeStampDate( Timestamp endingTimeStampDate )
    {
        if ( endingTimeStampDate != null )
        {
            this._endingDateTime = endingTimeStampDate.toLocalDateTime( );
        }
    }

    /**
     * Indicate if the slot is open or not
     * 
     * @return true if the slot is open
     */
    public boolean getIsOpen( )
    {
        return _bIsOpen;
    }

    /**
     * Set the boolean open value of the slot
     * 
     * @param bIsOpen
     *            the boolean open value to set
     */
    public void setIsOpen( boolean bIsOpen )
    {
        this._bIsOpen = bIsOpen;
    }

    /**
     * Indicate if the slot is specific or not
     * 
     * @return true if the slot is specific
     */
    public boolean getIsSpecific( )
    {
        return _bIsSpecific;
    }

    /**
     * Set the boolean specific value of the slot
     * 
     * @param bIsSpecific
     *            the boolean open value to set
     */
    public void setIsSpecific( boolean bIsSpecific )
    {
        this._bIsSpecific = bIsSpecific;
    }

    /**
     * Indicate if the slot is passed or not
     * 
     * @return true if the slot is specific
     */
    public boolean getIsPassed( )
    {
        return _bIsPassed;
    }

    /**
     * Set the boolean passed value of the slot
     * 
     * @param bIsSpecific
     *            the boolean open value to set
     */
    public void setIsPassed( boolean bIsPassed )
    {
        this._bIsPassed = bIsPassed;
    }

    /**
     * Get number of remaining places of the slot
     * 
     * @return the number of remaining places of the slot
     */
    public int getNbRemainingPlaces( )
    {
        return _nNbRemainingPlaces;
    }

    /**
     * Set the number of remaining places of the slot
     * 
     * @param nNbRemainingPlaces
     *            the number of remaining places
     */
    public void setNbRemainingPlaces( int nNbRemainingPlaces )
    {
        this._nNbRemainingPlaces = nNbRemainingPlaces;
    }

    /**
     * Get the potential remaining places on the slot
     * 
     * @return the number of potential ramaining places
     */
    public int getNbPotentialRemainingPlaces( )
    {
        return _nNbPotentialRemainingPlaces;
    }

    /**
     * Set the potential number of remaining places on the slot
     * 
     * @param nNbPotentialRemainingPlaces
     *            the number to set
     */
    public void setNbPotentialRemainingPlaces( int nNbPotentialRemainingPlaces )
    {
        this._nNbPotentialRemainingPlaces = nNbPotentialRemainingPlaces;
    }

    /**
     * Get the nb places taken on the slot
     * 
     * @return the number of places taken
     */
    public int getNbPlacesTaken( )
    {
        return _nNbPlacesTaken;
    }

    /**
     * Set the number of places taken on the slot
     * 
     * @param nNbPlacesTaken
     *            the number to set
     */
    public void setNbPlacestaken( int nNbPlacestaken )
    {
        this._nNbPlacesTaken = nNbPlacestaken;
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
     *            the maximum capacity to set
     */
    public void setMaxCapacity( int nMaxCapacity )
    {
        this._nMaxCapacity = nMaxCapacity;
    }

    /**
     * Get the Form Id the slot belongs to
     * 
     * @return the FOrm Id
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the Form Id the Slot belongs to
     * 
     * @param nIdForm
     *            the Form Id to set
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

}
