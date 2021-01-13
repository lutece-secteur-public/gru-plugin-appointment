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
import java.util.List;

/**
 * Business class of the working day
 * 
 * @author Laurent Payen
 *
 */
public final class WorkingDay implements Serializable
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
     * Id of the reservation rule.
     */
    private int _nIdReservationRule;

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
     * Get the id of the rule of the reservation
     * 
     * @return the id of the rule of the reservation
     */
    public int getIdReservationRule( )
    {
        return _nIdReservationRule;
    }

    /**
     * Set the id of the rule of the reservation
     * 
     * @param nIdReservationRule
     *            the id to set
     */
    public void setIdReservationRule( int nIdReservationRule )
    {
        this._nIdReservationRule = nIdReservationRule;
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
