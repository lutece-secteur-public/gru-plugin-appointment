/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *         and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *         and the following disclaimer in the documentation and/or other materials
 *         provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *         contributors may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
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

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.sql.Date;

import java.util.List;

/**
 * IAppointmentDAO Interface
 */
public interface IAppointmentDAO
{
    /**
     * Insert a new record in the table.
     * 
     * @param appointment
     *            instance of the Appointment object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( Appointment appointment, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param appointment
     *            the reference of the Appointment
     * @param plugin
     *            the Plugin
     */
    void store( Appointment appointment, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdAppointment
     *            int identifier of the Appointment to delete
     * @param plugin
     *            the Plugin
     */
    void delete( int nIdAppointment, Plugin plugin );

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     * 
     * @param nKey
     *            The identifier of the appointment
     * @param plugin
     *            the Plugin
     * @return The instance of the appointment
     */
    Appointment load( int nKey, Plugin plugin );

    /**
     * Load the data of all the appointment objects and returns them as a collection
     * 
     * @param plugin
     *            the Plugin
     * @return The collection which contains the data of all the appointment objects
     */
    List<Appointment> selectAppointmentsList( Plugin plugin );

    /**
     * Load the data of appointment objects associated with a given form and returns them in a collection
     * 
     * @param nIdForm
     *            the id of the form
     * @param plugin
     *            the Plugin
     * @return the collection which contains the data of appointment objects
     */
    List<Appointment> selectAppointmentsListByIdForm( int nIdForm, Plugin plugin );

    /**
     * Get the list of appointments matching a given filter
     * 
     * @param appointmentFilter
     *            The filter appointments must match
     * @param plugin
     *            The plugin
     * @return The list of appointments that match the given filter
     */
    List<Appointment> selectAppointmentListByFilter( AppointmentFilter appointmentFilter, Plugin plugin );

    /**
     * Get the list of ids of appointments matching a given filter
     * 
     * @param appointmentFilter
     *            The filter appointments must match
     * @param plugin
     *            The plugin
     * @return The list of ids of appointments that match the given filter
     */
    List<Integer> selectAppointmentIdByFilter( AppointmentFilter appointmentFilter, Plugin plugin );

    /**
     * Get a list of appointments from their ids
     * 
     * @param listIdAppointments
     *            The list of ids of appointments to get
     * @param strOrderBy
     *            The name of the column to sort rows
     * @param bSortAsc
     *            True to sort ascending, false otherwise
     * @param plugin
     *            The plugin
     * @return The list of appointments which ids are given in parameters
     */
    List<Appointment> selectAppointmentListById( List<Integer> listIdAppointments, String strOrderBy, boolean bSortAsc, Plugin plugin );

    /**
     * Get the number of appointment of a given date and associated with a given form
     * 
     * @param dateAppointment
     *            The date of appointments to count
     * @param nIdForm
     *            The id of the appointment form
     * @param plugin
     *            The plugin
     * @return the number of appointments, or 0 if no appointment was found
     */
    int getNbAppointmentByIdDay( Date dateAppointment, int nIdForm, Plugin plugin );

    // ----------------------------------------
    // Appointment response management
    // ----------------------------------------

    /**
     * Associates a response to an appointment
     * 
     * @param nIdAppointment
     *            The id of the appointment
     * @param nIdResponse
     *            The id of the response
     * @param plugin
     *            The plugin
     */
    void insertAppointmentResponse( int nIdAppointment, int nIdResponse, Plugin plugin );

    /**
     * Get the list of id of responses associated with an appointment
     * 
     * @param nIdAppointment
     *            the id of the appointment
     * @param plugin
     *            the plugin
     * @return the list of responses, or an empty list if no response was found
     */
    List<Integer> findListIdResponse( int nIdAppointment, Plugin plugin );

    /**
     * Remove the association between an appointment and responses
     * 
     * @param nIdAppointment
     *            The id of the appointment
     * @param plugin
     *            The plugin
     */
    void deleteAppointmentResponse( int nIdAppointment, Plugin plugin );

    /**
     * Get the number of appointments associated with a given form and with a date after a given date
     * 
     * @param nIdForm
     *            The id of the form
     * @param date
     *            The minimum date of appointments to consider
     * @return The number of appointments associated with the form
     * @param plugin
     *            The plugin
     */
    int countAppointmentsByIdForm( int nIdForm, Date date, Plugin plugin );

    /**
     * Remove an appointment responses from the id of a response.
     * 
     * @param nIdResponse
     *            The id of the response
     * @param plugin
     *            The plugin
     */
    void removeAppointmentResponsesByIdResponse( int nIdResponse, Plugin plugin );

    /**
     * Find the id of the appointment associated with a given response
     * 
     * @param nIdResponse
     *            The id of the response
     * @param plugin
     *            The plugin
     * @return The id of the appointment, or 0 if no appointment is associated with he given response.
     */
    int findIdAppointmentByIdResponse( int nIdResponse, Plugin plugin );
}
