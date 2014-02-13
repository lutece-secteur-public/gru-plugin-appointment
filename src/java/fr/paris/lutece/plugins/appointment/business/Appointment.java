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

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

import java.sql.Date;

import java.util.Collection;
import java.util.List;

import javax.validation.constraints.Size;


/**
 * This is the business class for the object Appointment
 */
public class Appointment implements Serializable
{
    /**
     * Appointment resource type
     */
    public static final String APPOINTMENT_RESOURCE_TYPE = "appointment";

    // If status values change, the template appointment/manage_appointments.html must be updated !
    /**
     * Status of appointments that have been rejected
     */
    public static final int STATUS_REJECTED = -10;

    /**
     * Status of appointments that have not been validated yet
     */
    public static final int STATUS_NOT_VALIDATED = 0;

    /**
     * Status of appointments that have been validated
     */
    public static final int STATUS_VALIDATED = 10;

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -2311528095383408879L;
    private int _nIdAppointment;
    @NotEmpty( message = "#i18n{appointment.validation.appointment.FirstName.notEmpty}" )
    @Size( max = 255, message = "#i18n{appointment.validation.appointment.FirstName.size}" )
    private String _strFirstName;
    @NotEmpty( message = "#i18n{appointment.validation.appointment.LastName.notEmpty}" )
    @Size( max = 255, message = "#i18n{appointment.validation.appointment.LastName.size}" )
    private String _strLastName;
    @NotEmpty( message = "#i18n{appointment.validation.appointment.Email.notEmpty}" )
    @Size( max = 255, message = "#i18n{appointment.validation.appointment.Email.size}" )
    @Email( message = "#i18n{appointment.validation.appointment.Email.email}" )
    private String _strEmail;

    // @Size( max = 255 , message = "#i18n{appointment.validation.appointment.IdUser.size}" ) 
    @Size( max = 255, message = "#i18n{portal.validation.message.sizeMax}" )
    private String _strIdUser;
    private String _strAuthenticationService;
    private Date _dateAppointment;
    private int _nIdSlot;
    private int _nStatus;
    private List<Response> _listResponse;
    private transient Collection<Action> _listWorkflowActions;
    private int _nIdActionCancel;
    private int _idAdminUser;

    /**
     * Returns the IdAppointment
     * @return The IdAppointment
     */
    public int getIdAppointment(  )
    {
        return _nIdAppointment;
    }

    /**
     * Sets the IdAppointment
     * @param nIdAppointment The IdAppointment
     */
    public void setIdAppointment( int nIdAppointment )
    {
        _nIdAppointment = nIdAppointment;
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
     * Returns the id of the lutece user that made this appointment
     * @return The id of the lutece user that made this appointment
     */
    public String getIdUser(  )
    {
        return _strIdUser;
    }

    /**
     * Sets the id of the lutece user that made this appointment
     * @param strIdUser The id of the lutece user that made this appointment
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
     * Get the list of response of this appointment
     * @return the list of response of this appointment
     */
    public List<Response> getListResponse(  )
    {
        return _listResponse;
    }

    /**
     * Set the list of responses of this appointment
     * @param listResponse The list of responses
     */
    public void setListResponse( List<Response> listResponse )
    {
        this._listResponse = listResponse;
    }

    /**
     * Set the list of workflow actions available for this appointment.
     * @param listWorkflowActions The list of workflow actions available for
     *            this
     *            appointment.
     */
    public void setListWorkflowActions( Collection<Action> listWorkflowActions )
    {
        this._listWorkflowActions = listWorkflowActions;
    }

    /**
     * Get the list of workflow actions available for this appointment. Workflow
     * actions are NOT loaded by default, so check that they have been set
     * before calling this method.
     * @return The list of workflow actions available for this appointment.
     */
    public Collection<Action> getListWorkflowActions(  )
    {
        return _listWorkflowActions;
    }

    /**
     * Get the id of the workflow action to execute to cancel the appointment
     * @return The id of the workflow action to execute to cancel the
     *         appointment
     */
    public int getIdActionCancel(  )
    {
        return _nIdActionCancel;
    }

    /**
     * Set the id of the workflow action to execute to cancel the appointment
     * @param nIdActionCancel The id of the workflow action to execute to cancel
     *            the appointment
     */
    public void setIdActionCancel( int nIdActionCancel )
    {
        this._nIdActionCancel = nIdActionCancel;
    }

    /**
     * Get the id of the admin user associated with this appointment
     * @return The id of the admin user associated with this appointment
     */
    public int getIdAdminUser(  )
    {
        return _idAdminUser;
    }

    /**
     * Set the id of the admin user associated with this appointment
     * @param nIdAdminUser The id of the admin user associated with this
     *            appointment
     */
    public void setIdAdminUser( int nIdAdminUser )
    {
        this._idAdminUser = nIdAdminUser;
    }
}
