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

import java.sql.Date;

import java.util.List;


/**
 * Describes a day form an appointment calendar
 */
public class AppointmentDay
{
    private int _nIdDay;
    private int _nIdForm;
    private boolean _bIsOpen;
    private Date _date;
    private int _nOpeningHour;
    private int _nOpeningMinutes;
    private int _nClosingHour;
    private int _nClosingMinutes;
    private int _nAppointmentDuration;
    private int _nPeoplePerAppointment;
    private List<AppointmentSlot> _listSlots;

    /**
     * Get the id of the day
     * @return The id of the day
     */
    public int getIdDay(  )
    {
        return _nIdDay;
    }

    /**
     * Set the id of the day
     * @param nIdDay The id of the day
     */
    public void setIdDay( int nIdDay )
    {
        this._nIdDay = nIdDay;
    }

    /**
     * Get the id of the appointment form associated with this day
     * @return The id of the appointment form associated with this day
     */
    public int getIdForm(  )
    {
        return _nIdForm;
    }

    /**
     * Set the id of the appointment form associated with this day
     * @param nIdForm The id of the appointment form associated with this day
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

    /**
     * Check if the day is an opened day or a closed day
     * @return True if the day is open, false otherwise
     */
    public boolean getIsOpen(  )
    {
        return _bIsOpen;
    }

    /**
     * Set this day as opened or closed
     * @param bIsOpen True if the day is opened, false otherwise
     */
    public void setIsOpen( boolean bIsOpen )
    {
        this._bIsOpen = bIsOpen;
    }

    /**
     * Get the date of the day
     * @return The date of the day
     */
    public Date getDate(  )
    {
        return _date;
    }

    /**
     * Set the date of the day
     * @param date The date of the day
     */
    public void setDate( Date date )
    {
        this._date = date;
    }

    //    /**
    //     * Get the year of the day
    //     * @return the year of the day
    //     */
    //    public int getYear( )
    //    {
    //        return _nYear;
    //    }
    //
    //    /**
    //     * Set the year of the day
    //     * @param nYear the year of the day
    //     */
    //    public void setYear( int nYear )
    //    {
    //        this._nYear = nYear;
    //    }
    //
    //    /**
    //     * Get the month of the day
    //     * @return The month of the day
    //     */
    //    public int getMonth( )
    //    {
    //        return _nMonth;
    //    }
    //
    //    /**
    //     * Set the month of the day
    //     * @param nMonth The month of the day
    //     */
    //    public void setMonth( int nMonth )
    //    {
    //        this._nMonth = nMonth;
    //    }
    //
    //    /**
    //     * Get the number of the day in the month
    //     * @return The number of the day in the month
    //     */
    //    public int getDay( )
    //    {
    //        return _nDay;
    //    }
    //
    //    /**
    //     * Set the number of the day in the month
    //     * @param nDay The number of the day in the month
    //     */
    //    public void setDay( int nDay )
    //    {
    //        this._nDay = nDay;
    //    }

    /**
     * Get the opening hour of the day
     * @return The opening hour of the day
     */
    public int getOpeningHour(  )
    {
        return _nOpeningHour;
    }

    /**
     * Set the opening hour of the day
     * @param nOpeningHour The opening hour of the day
     */
    public void setOpeningHour( int nOpeningHour )
    {
        this._nOpeningHour = nOpeningHour;
    }

    /**
     * Get the opening minute of the day
     * @return The opening minute of the day
     */
    public int getOpeningMinutes(  )
    {
        return _nOpeningMinutes;
    }

    /**
     * Set the opening minute of the day
     * @param nOpeningMinutes The opening minute of the day
     */
    public void setOpeningMinutes( int nOpeningMinutes )
    {
        this._nOpeningMinutes = nOpeningMinutes;
    }

    /**
     * Get the closing hour of the day
     * @return The closing hour of the day
     */
    public int getClosingHour(  )
    {
        return _nClosingHour;
    }

    /**
     * Set the closing hour of the day
     * @param nClosingHour The closing hour of the day
     */
    public void setClosingHour( int nClosingHour )
    {
        this._nClosingHour = nClosingHour;
    }

    /**
     * Get the closing minute of the day
     * @return The closing minute of the day
     */
    public int getClosingMinutes(  )
    {
        return _nClosingMinutes;
    }

    /**
     * Set the closing minute of the day
     * @param nClosingMinutes The closing minute of the day
     */
    public void setClosingMinutes( int nClosingMinutes )
    {
        this._nClosingMinutes = nClosingMinutes;
    }

    /**
     * Get the duration of appointments in minutes
     * @return The duration of appointments in minutes
     */
    public int getAppointmentDuration(  )
    {
        return _nAppointmentDuration;
    }

    /**
     * Set the duration of appointments in minutes
     * @param nAppointmentDuration The duration of appointments in minutes
     */
    public void setAppointmentDuration( int nAppointmentDuration )
    {
        this._nAppointmentDuration = nAppointmentDuration;
    }

    /**
     * Returns the number of person per appointment
     * @return The number of person per appointment
     */
    public int getPeoplePerAppointment(  )
    {
        return _nPeoplePerAppointment;
    }

    /**
     * Sets the number of person per appointment
     * @param nPeoplePerAppointment The number of person per appointment
     */
    public void setPeoplePerAppointment( int nPeoplePerAppointment )
    {
        _nPeoplePerAppointment = nPeoplePerAppointment;
    }

    /**
     * Get the list of slots of this day, if any
     * @return The list of slots of this day, if any
     */
    public List<AppointmentSlot> getListSlots(  )
    {
        return _listSlots;
    }

    /**
     * Set the list of slots of this day, if any
     * @param listSlots The list of slots of this day, if any
     */
    public void setListSlots( List<AppointmentSlot> listSlots )
    {
        this._listSlots = listSlots;
    }
}
