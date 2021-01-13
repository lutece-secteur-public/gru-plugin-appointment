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
package fr.paris.lutece.plugins.appointment.web.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;

/**
 * Business class of the rules of the reservation
 * 
 * @author Laurent Payen
 *
 */
public class ReservationRuleDTO implements Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -5154752950203822668L;

    /**
     * Id of the reservation rule.
     */
    private int _nIdReservationRule;

    @NotBlank( message = "#i18n{appointment.validation.week.name.notEmpty}" )
    @Size( max = 255, message = "#i18n{appointment.validation.week.Title.size}" )
    private String _strName;

    @NotBlank( message = "#i18n{appointment.validation.week.description.notEmpty}" )
    @Size( max = 255, message = "#i18n{appointment.validation.week.description.size}" )
    private String _strDescriptionRule;

    @NotBlank( message = "#i18n{appointment.validation.week.color.notEmpty}" )
    @Size( max = 255, message = "#i18n{appointment.validation.week.color.size}" )
    private String _strColor;

    private boolean _bEnable = true;

    /**
     * The starting time of a working day
     */
    @NotBlank( message = "#i18n{portal.validation.message.notEmpty}" )
    private String _strTimeStart;

    /**
     * The ending time of a working day
     */
    @NotBlank( message = "#i18n{portal.validation.message.notEmpty}" )
    private String _strTimeEnd;

    /**
     * The duration of an appointment
     */
    @NotNull( message = "#i18n{portal.validation.message.notEmpty}" )
    @Min( value = 1, message = "#i18n{portal.validation.message.notEmpty}" )
    private int _nDurationAppointments;

    /**
     * Maximum capacity for a slot
     */
    @Min( value = 1, message = "#i18n{portal.validation.message.notEmpty}" )
    private int _nMaxCapacityPerSlot = 1;

    /**
     * Maximum number of people authorized for an appointment
     */
    @Min( value = 1, message = "#i18n{portal.validation.message.notEmpty}" )
    private int _nMaxPeoplePerAppointment = 1;

    /**
     * The Form Id the Reservation Rule belongs to (foreign key)
     */
    private int _nIdForm;

    /**
     * List of the working days that define the week definition
     */
    private List<WorkingDay> _listWorkingDays;

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
     * Returns the Name
     * 
     * @return The Name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * Sets the Name
     * 
     * @param strName
     *            The Name
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * Returns the Description
     * 
     * @return The Description
     */
    public String getDescriptionRule( )
    {
        return _strDescriptionRule;
    }

    /**
     * Sets the Description
     * 
     * @param strDescription
     *            The Description
     */
    public void setDescriptionRule( String strDescription )
    {
        _strDescriptionRule = strDescription;
    }

    /**
     * Returns the Color
     * 
     * @return The Color
     */
    public String getColor( )
    {
        return _strColor;
    }

    /**
     * Sets the Color
     * 
     * @param strColor
     *            The Color
     */
    public void setColor( String strColor )
    {
        _strColor = strColor;
    }

    /**
     * Returns the Enable
     * 
     * @return The Enable
     */
    public boolean getEnable( )
    {
        return _bEnable;
    }

    /**
     * Sets the Enable
     * 
     * @param bEnable
     *            The Enable
     */
    public void setEnable( boolean bEnable )
    {
        _bEnable = bEnable;
    }

    /**
     * Returns the starting time of the working day of the form
     * 
     * @return The starting time
     */
    public String getTimeStart( )
    {
        return _strTimeStart;
    }

    /**
     * Sets the starting time of the working day of the form
     * 
     * @param the
     *            starting time to set The TimeStart
     */
    public void setTimeStart( String timeStart )
    {
        _strTimeStart = timeStart;
    }

    /**
     * Returns the ending time of the working day of the form
     * 
     * @return The ending time
     */
    public String getTimeEnd( )
    {
        return _strTimeEnd;
    }

    /**
     * Sets the ending time of the working day of the form
     * 
     * @param the
     *            ending time to set
     */
    public void setTimeEnd( String timeEnd )
    {
        _strTimeEnd = timeEnd;
    }

    /**
     * Returns the duration of an appointment
     * 
     * @return The duration of an appointment
     */
    public int getDurationAppointments( )
    {
        return _nDurationAppointments;
    }

    /**
     * Sets the duration of an appointment
     * 
     * @param nDurationAppointments
     *            The Duration of an Appointments
     */
    public void setDurationAppointments( int nDurationAppointments )
    {
        _nDurationAppointments = nDurationAppointments;
    }

    /**
     * Get the maximum capacity for a slot
     * 
     * @return the maximum capacity for a slot
     */
    public int getMaxCapacityPerSlot( )
    {
        return _nMaxCapacityPerSlot;
    }

    /**
     * Set the maximum capacity for a slot
     * 
     * @param nMaxCapacityPerSlot
     *            the maximum capacity for a slot
     */
    public void setMaxCapacityPerSlot( int nMaxCapacityPerSlot )
    {
        this._nMaxCapacityPerSlot = nMaxCapacityPerSlot;
    }

    /**
     * Get the maximum number of people authorized for an appointment
     * 
     * @return the maximum number of people authorized for an appointment
     */
    public int getMaxPeoplePerAppointment( )
    {
        return _nMaxPeoplePerAppointment;
    }

    /**
     * Set the maximum number of people authorized for an appointment
     * 
     * @param nMaxPeoplePerAppointment
     *            the maximum of people to set
     */
    public void setMaxPeoplePerAppointment( int nMaxPeoplePerAppointment )
    {
        this._nMaxPeoplePerAppointment = nMaxPeoplePerAppointment;
    }

    /**
     * Get the Form Id the Reservation Rule belongs to
     * 
     * @return the Form Id
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the Form Id the Reservation Rule belongs to
     * 
     * @param nIdForm
     *            the Form Id tp set
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

    /**
     * Get the list of the working days of the week
     * 
     * @return the list of the working days for the week
     */
    public List<WorkingDay> getListWorkingDay( )
    {
        return _listWorkingDays;
    }

    /**
     * Set the working days for the week
     * 
     * @param _listWorkingDays
     *            the list o f working days to set
     */
    public void setListWorkingDay( List<WorkingDay> listWorkingDays )
    {
        this._listWorkingDays = listWorkingDays;
    }

}
