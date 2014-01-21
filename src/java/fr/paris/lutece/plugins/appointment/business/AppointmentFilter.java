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
package fr.paris.lutece.plugins.appointment.business;

import java.sql.Date;


/**
 * Class to filter appointments
 */
public class AppointmentFilter
{
    /**
     * Value for status to ignore filter
     */
    public static final int NO_STATUS_FILTER = -1525;

    private int _nIdSlot;
    private int _nIdForm;
    private String _strFirstName;
    private String _strLastName;
    private String _strEmail;
    private String _strIdUser;
    private Date _dateAppointment;
    private int _nStatus = NO_STATUS_FILTER;

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
    public void getIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

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
     * Returns the FirstName
     * @return The FirstName
     */
    public String getFirstName( )
    {
        return _strFirstName;
    }

    /**
     * Sets the FirstName
     * @param strFirstName The FirstName
     */
    public void setFirstName( String strFirstName )
    {
        _strFirstName = strFirstName;
    }

    /**
     * Returns the LastName
     * @return The LastName
     */
    public String getLastName( )
    {
        return _strLastName;
    }

    /**
     * Sets the LastName
     * @param strLastName The LastName
     */
    public void setLastName( String strLastName )
    {
        _strLastName = strLastName;
    }

    /**
     * Returns the Email
     * @return The Email
     */
    public String getEmail( )
    {
        return _strEmail;
    }

    /**
     * Sets the Email
     * @param strEmail The Email
     */
    public void setEmail( String strEmail )
    {
        _strEmail = strEmail;
    }

    /**
     * Returns the IdUser
     * @return The IdUser
     */
    public String getIdUser( )
    {
        return _strIdUser;
    }

    /**
     * Sets the IdUser
     * @param strIdUser The IdUser
     */
    public void setIdUser( String strIdUser )
    {
        _strIdUser = strIdUser;
    }

    /**
     * Get the date of the appointment
     * @return The date of the appointment
     */
    public Date getDateAppointment( )
    {
        return _dateAppointment;
    }

    /**
     * Set the date of the appointment
     * @param dateAppointment The date of the appointment
     */
    public void setDateAppointment( Date dateAppointment )
    {
        this._dateAppointment = dateAppointment;
    }

    /**
     * Get the status of the appointment
     * @return The status of the appointment
     */
    public int getStatus( )
    {
        return _nStatus;
    }

    /**
     * Set the status of the appointment
     * @param nStatus The status of the appointment
     */
    public void setStatus( int nStatus )
    {
        _nStatus = nStatus;
    }
}
