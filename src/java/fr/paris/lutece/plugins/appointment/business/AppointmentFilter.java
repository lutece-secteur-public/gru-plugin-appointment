/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

import java.sql.Date;


/**
 * Class to filter appointments
 */
public class AppointmentFilter implements Serializable
{
    /**
     * Value for status to ignore filter
     */
    public static final int NO_STATUS_FILTER = -1525;

    /**
     * Default order by
     */
    public static final String CONSTANT_DEFAULT_ORDER_BY = "id_appointment";

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 7458206872870171709L;
    private static final String[] LIST_ORDER_BY = 
        {
            CONSTANT_DEFAULT_ORDER_BY, "id_slot", "first_name", "last_name", "id_user", "authentication_service",
            "date_appointment", "status",
        };
    private int _nIdSlot;
    private int _nIdForm;
    private String _strFirstName;
    private String _strLastName;
    private String _strEmail;
    private String _strIdUser;
    private String _strAuthenticationService;
    private int _nIdAdminUser = -1;
    private Date _dateAppointment;
    private Date _dateAppointmentMin;
    private Date _dateAppointmentMax;
    private int _nStatus = NO_STATUS_FILTER;
    private String _strOrderBy = CONSTANT_DEFAULT_ORDER_BY;
    private boolean _bOrderAsc;

    /**
     * Get the id of the form
     * @return The id of the form
     */
    public int getIdForm(  )
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
     * Get the id of the slot
     * @return The id of the slot
     */
    public int getIdSlot(  )
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
    public String getFirstName(  )
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
    public String getLastName(  )
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
    public String getEmail(  )
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
    public String getIdUser(  )
    {
        return _strIdUser;
    }

    /**
     * Sets the id of the LuteceUser
     * @param strIdUser The IdUser
     */
    public void setIdUser( String strIdUser )
    {
        _strIdUser = strIdUser;
    }

    /**
     * Returns the authentication service used by the lutece user that made this
     * appointment, if any
     * @return The authentication service used by the lutece user that made this
     *         appointment, or null if this appointment is not associated with a
     *         lutece user
     */
    public String getAuthenticationService(  )
    {
        return _strAuthenticationService;
    }

    /**
     * Sets the authentication service used by the lutece user that made this
     * appointment, if any
     * @param strAuthenticationService The authentication service used by the
     *            lutece user that made this appointment, or null if this
     *            appointment is not associated with a lutece user
     */
    public void setAuthenticationService( String strAuthenticationService )
    {
        _strAuthenticationService = strAuthenticationService;
    }

    /**
     * Sets the id of the admin user
     * @param nIdAdminUser The id of the admin user
     */
    public void setIdAdminUser( int nIdAdminUser )
    {
        _nIdAdminUser = nIdAdminUser;
    }

    /**
     * Returns the id of the admin user
     * @return The id of the admin user
     */
    public int getIdAdminUser(  )
    {
        return _nIdAdminUser;
    }

    /**
     * Get the date of the appointment
     * @return The date of the appointment
     */
    public Date getDateAppointment(  )
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
     * Get the minimum value of the date of the appointment
     * @return The minimum value of the date of the appointment
     */
    public Date getDateAppointmentMin(  )
    {
        return _dateAppointmentMin;
    }

    /**
     * Set the minimum value of the date of the appointment
     * @param dateAppointmentMin The minimum value of the date of the
     *            appointment
     */
    public void setDateAppointmentMin( Date dateAppointmentMin )
    {
        this._dateAppointmentMin = dateAppointmentMin;
    }

    /**
     * Get the minimum value of the date of the appointment
     * @return The minimum value of the date of the appointment
     */
    public Date getDateAppointmentMax(  )
    {
        return _dateAppointmentMax;
    }

    /**
     * Set the value value of the date of the appointment
     * @param dateAppointmentMax The maximum value of the date of the
     *            appointment
     */
    public void setDateAppointmentMax( Date dateAppointmentMax )
    {
        this._dateAppointmentMax = dateAppointmentMax;
    }

    /**
     * Get the status of the appointment
     * @return The status of the appointment
     */
    public int getStatus(  )
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

    /**
     * Get the order by attribute of this filter
     * @return The order by attribute of this filter
     */
    public String getOrderBy(  )
    {
        return _strOrderBy;
    }

    /**
     * Set the order by attribute of this filter.
     * @param strOrderBy The order by attribute of this filter. If the specified
     *            order does not match with column names of the appointment
     *            table of the database, then the order by is reinitialized.
     */
    public void setOrderBy( String strOrderBy )
    {
        boolean bValidOrderBy = false;

        for ( String strOrder : LIST_ORDER_BY )
        {
            if ( StringUtils.equals( strOrder, strOrderBy ) )
            {
                bValidOrderBy = true;

                break;
            }
        }

        if ( bValidOrderBy )
        {
            this._strOrderBy = strOrderBy;
        }
        else
        {
            _strOrderBy = LIST_ORDER_BY[0];
        }
    }

    /**
     * Get the order of the sort of this filter
     * @return The _bOrderAsc
     */
    public boolean getOrderAsc(  )
    {
        return _bOrderAsc;
    }

    /**
     * Set the order of the sort of this filter
     * @param bOrderAsc True to sort ascending, false to sort descending,
     */
    public void setOrderAsc( boolean bOrderAsc )
    {
        this._bOrderAsc = bOrderAsc;
    }
}
