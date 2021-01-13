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
package fr.paris.lutece.plugins.appointment.business.appointment;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;

/**
 * Business class of the Appointment
 * 
 * @author Laurent Payen
 *
 */
public class Appointment extends User
{

    /**
     * Appointment resource type
     */
    public static final String APPOINTMENT_RESOURCE_TYPE = "appointment";

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -132212832777629802L;

    /**
     * Appointment Id
     */
    private int _nIdAppointment;

    /**
     * Reference of the Appointment
     */
    private String _strReference;

    /**
     * Number of places for the appointment
     */
    private int _nNbPlaces;
    /**
     * Tell if the appointment is cancelled or not
     */
    private boolean _bIsCancelled;

    /**
     * Id for a cancelled appointment
     */
    private int _nIdActionCancelled;

    /**
     * The rank for the notification (0 : no notification)
     */
    private int _notification;

    /**
     * The Admin User Id assigned to the appointment
     */
    private int _nIdAdminUser;

    /**
     * The Admin User who created the appointment (if not created by the user himself)
     */
    private String _strAdminUserCreate;

    /**
     * The slots on which the appointment is
     */
    private List<Slot> _listSlot;

    /**
     * The user of the appointment
     */
    private User _user;
    /**
     * The date appointment taken
     */
    private LocalDateTime _dateAppointmentTaken;

    /**
     * The appointment slots
     */
    private List<AppointmentSlot> _listAppointmentSlot;

    /**
     * Get the reference of the appointment
     * 
     * @return the reference
     */
    public String getReference( )
    {
        return _strReference;
    }

    /**
     * Set the reference of the appointment
     * 
     * @param strReference
     *            the reference to set
     */
    public void setReference( String strReference )
    {
        this._strReference = strReference;
    }

    /**
     * Get the number of places of the appointment
     * 
     * @return the number of places
     */
    public int getNbPlaces( )
    {
        return _nNbPlaces;
    }

    /**
     * Set the number of places for the appointment
     * 
     * @param nNbPlaces
     *            the number of places to set
     */
    public void setNbPlaces( int nNbPlaces )
    {
        this._nNbPlaces = nNbPlaces;
    }

    /**
     * Get if the appointment is cancelled
     * 
     * @return true if the appointment is cancelled
     */
    public boolean getIsCancelled( )
    {
        return _bIsCancelled;
    }

    /**
     * Set if the appointment is cancelled
     * 
     * @param bIsCancelled
     *            the boolean value to set
     */
    public void setIsCancelled( boolean bIsCancelled )
    {
        this._bIsCancelled = bIsCancelled;
    }

    /**
     * Get the id for the cancelled appointment
     * 
     * @return the id
     */
    public int getIdActionCancelled( )
    {
        return _nIdActionCancelled;
    }

    /**
     * Set the id for the cancelled action
     * 
     * @param nIdActionCancelled
     *            the id to set
     */
    public void setIdActionCancelled( int nIdActionCancelled )
    {
        this._nIdActionCancelled = nIdActionCancelled;
    }

    /**
     * Get the rank for the notification (0 = no notification)
     * 
     * @return the rank
     */
    public int getNotification( )
    {
        return _notification;
    }

    /**
     * Set the rank for the notification
     * 
     * @param notification
     *            the rank (default : 0, no notification)
     */
    public void setNotification( int notification )
    {
        this._notification = notification;
    }

    /**
     * Get the Appointment Id
     * 
     * @return the Appointment Id
     */
    public int getIdAppointment( )
    {
        return _nIdAppointment;
    }

    /**
     * Set the Appointment Id
     * 
     * @param nIdAppointment
     *            the Appointment Id to set
     */
    public void setIdAppointment( int nIdAppointment )
    {
        this._nIdAppointment = nIdAppointment;
    }

    /**
     * get the admin user assigned to the appointment
     * 
     * @return the assigned admin user id (if exists)
     */
    public int getIdAdminUser( )
    {
        return _nIdAdminUser;
    }

    /**
     * set admin user id assigned to the appointment
     * 
     * @param nIdAdminUser
     */
    public void setIdAdminUser( int nIdAdminUser )
    {
        this._nIdAdminUser = nIdAdminUser;
    }

    /**
     * get the admin user who created the appointment (if exists)
     * 
     * @return the admin user (if exists, null otherwise)
     */
    public String getAdminUserCreate( )
    {
        return _strAdminUserCreate;
    }

    /**
     * set admin user id who created the appointment
     * 
     * @param strAdminUser
     */
    public void setAdminUserCreate( String strAdminUser )
    {
        this._strAdminUserCreate = strAdminUser;
    }

    /**
     * Get the list slot of the appointment
     * 
     * @return the list of slot
     */
    public List<Slot> getSlot( )
    {
        return _listSlot;
    }

    /**
     * Set the listslot of the appointment
     * 
     * @param listSlot
     *            the slot to set
     */
    public void setSlot( List<Slot> listSlot )
    {
        _listSlot = listSlot;
    }

    public void addAllSlot( List<Slot> listSlot )
    {

        if ( _listSlot == null )
        {

            _listSlot = new ArrayList<>( );
        }
        _listSlot.addAll( listSlot );

    }

    public void addSlot( Slot slot )
    {

        if ( _listSlot == null )
        {

            _listSlot = new ArrayList<>( );
        }

        if ( _listSlot.stream( ).noneMatch( ( slt -> slt.getIdSlot( ) == slot.getIdSlot( ) ) ) )
        {

            _listSlot.add( slot );
        }
    }

    /**
     * Get the user of the appointment
     * 
     * @return the user
     */
    public User getUser( )
    {
        return _user;
    }

    /**
     * Set the user of the appointment
     * 
     * @param user
     *            the user
     */
    public void setUser( User user )
    {
        this._user = user;
    }

    /**
     * Get the list of appointment slot
     * 
     * @return the list of appointmentslot
     */
    public List<AppointmentSlot> getListAppointmentSlot( )
    {
        return _listAppointmentSlot;
    }

    /**
     * Set the list of listAppointmentSlot
     * 
     * @param listAppointmentSlot
     *            the appointment slot to set
     */
    public void setListAppointmentSlot( List<AppointmentSlot> listAppointmentSlot )
    {
        _listAppointmentSlot = listAppointmentSlot;
    }

    /**
     * Returns the DateAppointmentTaken
     * 
     * @return The DateAppointmentTaken
     */
    public LocalDateTime getDateAppointmentTaken( )
    {
        return _dateAppointmentTaken;
    }

    /**
     * Sets the DateAppointmentTaken
     * 
     * @param dateAppointmentTaken
     *            The DateAppointmentTaken
     */
    public void setDateAppointmentTaken( LocalDateTime dateAppointmentTaken )
    {
        _dateAppointmentTaken = dateAppointmentTaken;
    }

    /**
     * Get the date appointment taken (in sql date format)
     * 
     * @return The DateAppointmentTaken
     */
    public Timestamp getAppointmentTakenSqlDate( )
    {
        Timestamp date = null;
        if ( _dateAppointmentTaken != null )
        {
            date = Timestamp.valueOf( _dateAppointmentTaken );
        }
        return date;
    }

    /**
     * Set the date appointment taken (in sql date format)
     * 
     * @param endingValidityDate
     *            The DateAppointmentTaken to set (in sql Date format)
     */
    public void setAppointmentTakenSqlDate( Timestamp dateAppointmentTaken )
    {
        if ( dateAppointmentTaken != null )
        {
            this._dateAppointmentTaken = dateAppointmentTaken.toLocalDateTime( );
        }
        else
        {
            this._dateAppointmentTaken = null;
        }
    }
}
