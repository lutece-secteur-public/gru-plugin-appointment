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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * The DTO for an appointment in front office
 * 
 * @author Laurent Payen
 *
 */
public final class AppointmentDTO extends Appointment
{

    private static final String PROPERTY_EMPTY_FIELD_FIRST_NAME = "appointment.validation.appointment.FirstName.notEmpty";
    private static final String PROPERTY_EMPTY_FIELD_LAST_NAME = "appointment.validation.appointment.LastName.notEmpty";
    private static final String PROPERTY_UNVAILABLE_EMAIL = "appointment.validation.appointment.Email.email";
    private static final String PROPERTY_MESSAGE_EMPTY_EMAIL = "appointment.validation.appointment.Email.notEmpty";
    private static final String PROPERTY_EMPTY_CONFIRM_EMAIL = "appointment.validation.appointment.EmailConfirmation.email";
    private static final String PROPERTY_UNVAILABLE_CONFIRM_EMAIL = "appointment.message.error.confirmEmail";
    private static final String PROPERTY_EMPTY_NB_SEATS = "appointment.validation.appointment.NbBookedSeat.notEmpty";
    private static final String PROPERTY_UNVAILABLE_NB_SEATS = "appointment.validation.appointment.NbBookedSeat.error";
    private static final String PROPERTY_MAX_APPOINTMENT_PERIODE = "appointment.message.error.MaxAppointmentPeriode";
    private static final String PROPERTY_MAX_APPOINTMENT_PERIODE_BACK = "appointment.info.appointment.emailerror";
    private static final String PROPERTY_NB_DAY_BETWEEN_TWO_APPOINTMENTS = "appointment.validation.appointment.NbMinDaysBetweenTwoAppointments.error";
    public static final String PROPERTY_APPOINTMENT_STATUS_UNRESERVED = "appointment.message.labelStatusUnreserved";
    public static final String PROPERTY_APPOINTMENT_STATUS_RESERVED = "appointment.message.labelStatusReserved";

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 703930649594406505L;

    /**
     * The Date of the appointment in string format
     */
    private String _strDateOfTheAppointment;

    /**
     * The name of the admin user
     */
    private String _strAdminUser;

    /**
     * The starting date in LocalDateTime
     */
    private LocalDateTime _startingDateTime;

    /**
     * The ending date in LocalDateTime
     */
    private LocalDateTime _endingDateTime;

    /**
     * The starting time
     */
    private LocalTime _startingTime;

    /**
     * The ending time
     */
    private LocalTime _endingTime;

    /**
     * The state of the appointment
     */
    private transient State _state;

    /**
     * The Form Id
     */
    private int _nIdForm;

    /**
     * the number of booked seats for this appointment
     */
    private int _nNbBookedSeats = 1;

    /**
     * The maximum number of seats the user can book
     */
    private int _nNbMaxPotentialBookedSeats;

    /**
     * The Map of the responses for the additional entries of the form
     */
    private Map<Integer, List<Response>> _mapResponsesByIdEntry = new HashMap<>( );

    /**
     * The list of the responses for the additional entries of the form
     */
    private List<Response> _listResponse;

    /**
     * List of the available action of the workflow for this appointment
     */
    private transient Collection<Action> _listWorkflowActions;
    /**
     * The appointment is saved
     */
    private boolean _bIsSaved;
    /**
     * The overbooking is allowed
     */
    private boolean _bOverbookingAllowed;

    /**
     * Get the name of the admin user
     * 
     * @return the admin user name
     */
    public String getAdminUser( )
    {
        return _strAdminUser;
    }

    /**
     * Set the name of the admin user
     * 
     * @param _strAdminUser
     *            the admin user name to set
     */
    public void setAdminUser( String strAdminUser )
    {
        this._strAdminUser = strAdminUser;
    }

    /**
     * Get the state of the appointment
     * 
     * @return the state of the appointment
     */
    public State getState( )
    {
        return _state;
    }

    /**
     * Set the state of the appointment
     * 
     * @param state
     *            the state to set
     */
    public void setState( State state )
    {
        this._state = state;
    }

    /**
     * Get the starting date time of the appointment
     * 
     * @return the starting date time
     */
    public LocalDateTime getStartingDateTime( )
    {
        return _startingDateTime;
    }

    /**
     * Set the starting date time of the appointment
     * 
     * @param startingDateTime
     *            the starting date time
     */
    public void setStartingDateTime( LocalDateTime startingDateTime )
    {
        this._startingDateTime = startingDateTime;
    }

    /**
     * Get the ending date time of the appointment
     * 
     * @return the ending date time
     */
    public LocalDateTime getEndingDateTime( )
    {
        return _endingDateTime;
    }

    /**
     * Set the ending date time of the appointment
     * 
     * @param endingDateTime
     *            the ending date time
     */
    public void setEndingDateTime( LocalDateTime endingDateTime )
    {
        this._endingDateTime = endingDateTime;
    }

    /**
     * Get the starting time of the appointment
     * 
     * @return the starting time
     */
    public LocalTime getStartingTime( )
    {
        return _startingTime;
    }

    /**
     * Set the starting time of the appointment
     * 
     * @param startingTime
     *            the starting time to set
     */
    public void setStartingTime( LocalTime startingTime )
    {
        this._startingTime = startingTime;
    }

    /**
     * Get the ending time of the appointment
     * 
     * @return the ending time
     */
    public LocalTime getEndingTime( )
    {
        return _endingTime;
    }

    /**
     * Set the ending time of the appointment
     * 
     * @param endingTime
     *            the ending time
     */
    public void setEndingTime( LocalTime endingTime )
    {
        this._endingTime = endingTime;
    }

    /**
     * Get the date of the appointment
     * 
     * @return the date of the appointment
     */
    public String getDateOfTheAppointment( )
    {
        return _strDateOfTheAppointment;
    }

    /**
     * Set the date of the appointment
     * 
     * @param strDateOfTheAppointment
     *            the date to set
     */
    public void setDateOfTheAppointment( String strDateOfTheAppointment )
    {
        this._strDateOfTheAppointment = strDateOfTheAppointment;
    }

    /**
     * Get the list of the responses of the additional entries of the form
     * 
     * @return the list of the responses
     */
    public List<Response> getListResponse( )
    {
        return _listResponse;
    }

    /**
     * Set the list of the responses of the additional entries of the form
     * 
     * @param listResponse
     *            the list of the responses to set
     */
    public void setListResponse( List<Response> listResponse )
    {
        this._listResponse = listResponse;
    }

    /**
     * Get the form Id
     * 
     * @return the form Id
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the Form Id
     * 
     * @param nIdForm
     *            the form Id to set
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

    /**
     * Get the number of booked seats for the appointment
     * 
     * @return the number of booked seats
     */
    public int getNbBookedSeats( )
    {
        return _nNbBookedSeats;
    }

    /**
     * Set the number of booked seats for the appointment
     * 
     * @param nNumberOfPlacesReserved
     *            the number to set
     */
    public void setNbBookedSeats( int nNumberOfPlacesReserved )
    {
        this._nNbBookedSeats = nNumberOfPlacesReserved;
    }

    /**
     * Get the maximum number of booked seats the user can take
     * 
     * @return the max number of booked seats
     */
    public int getNbMaxPotentialBookedSeats( )
    {
        return _nNbMaxPotentialBookedSeats;
    }

    /**
     * Set the maximum number of booked seats the user can take
     * 
     * @param nNbMaxPotentialBookedSeats
     *            the maximum number to set
     */
    public void setNbMaxPotentialBookedSeats( int nNbMaxPotentialBookedSeats )
    {
        this._nNbMaxPotentialBookedSeats = nNbMaxPotentialBookedSeats;
    }

    /**
     * Get the available actions of the workflow for this appointment
     * 
     * @return the actions
     */
    public Collection<Action> getListWorkflowActions( )
    {
        return _listWorkflowActions;
    }

    /**
     * Set the available actions of the workflow for this appointment
     * 
     * @param listWorkflowActions
     */
    public void setListWorkflowActions( Collection<Action> listWorkflowActions )
    {
        this._listWorkflowActions = listWorkflowActions;
    }

    /**
     * Get the map of the responses of the additional entries of the form
     * 
     * @return the map of the responses
     */
    public Map<Integer, List<Response>> getMapResponsesByIdEntry( )
    {
        return _mapResponsesByIdEntry;
    }

    /**
     * Set the map of the responses of the addtional entries of the form to the appointment
     * 
     * @param mapResponsesByIdEntry
     *            the map to set
     */
    public void setMapResponsesByIdEntry( Map<Integer, List<Response>> mapResponsesByIdEntry )
    {
        this._mapResponsesByIdEntry = mapResponsesByIdEntry;
    }

    public void clearMapResponsesByIdEntry( )
    {
        this._mapResponsesByIdEntry.clear( );
    }

    /**
     * Returns the IsSaved
     * 
     * @return The IsSaved
     */
    public boolean getIsSaved( )
    {
        return _bIsSaved;
    }

    /**
     * Sets the IsSaved
     * 
     * @param bIsSaved
     *            The IsSaved
     */
    public void setIsSaved( boolean bIsSaved )
    {
        _bIsSaved = bIsSaved;
    }

    /**
     * Returns the OverbookingAllowed
     * 
     * @return The OverbookingAllowed
     */
    public boolean getOverbookingAllowed( )
    {
        return _bOverbookingAllowed;
    }

    /**
     * Sets the OverbookingAllowed
     * 
     * @param bOverbookingAllowed
     *            The OverbookingAllowed
     */
    public void setOverbookingAllowed( boolean bOverbookingAllowed )
    {
        _bOverbookingAllowed = bOverbookingAllowed;
    }

    /**
     * Get all the possible errors of the form
     * 
     * @param locale
     *            the locale
     * @return a list of all the possible errors of the form
     */
    public static List<String> getAllErrors( Locale locale )
    {
        List<String> listAllErrors = new ArrayList<>( );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_EMPTY_FIELD_LAST_NAME, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_EMPTY_FIELD_FIRST_NAME, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_UNVAILABLE_EMAIL, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_MESSAGE_EMPTY_EMAIL, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_EMPTY_CONFIRM_EMAIL, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_UNVAILABLE_CONFIRM_EMAIL, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_EMPTY_NB_SEATS, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_UNVAILABLE_NB_SEATS, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_MAX_APPOINTMENT_PERIODE, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_MAX_APPOINTMENT_PERIODE_BACK, locale ) );
        listAllErrors.add( I18nService.getLocalizedString( PROPERTY_NB_DAY_BETWEEN_TWO_APPOINTMENTS, locale ) );
        return listAllErrors;
    }

}
