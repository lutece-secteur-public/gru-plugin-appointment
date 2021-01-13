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

import java.util.List;

import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFilterDTO;
import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Appointment DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface IAppointmentDAO
{

    /**
     * Insert a new record in the table
     * 
     * @param appointment
     *            instance of the appointment object to insert
     * @param plugin
     *            the plugin
     */
    void insert( Appointment appointment, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param appointment
     *            the reference of the appointment
     * @param plugin
     *            the plugin
     */
    void update( Appointment appointment, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdAppointment
     *            int identifier of the appointment to delete
     * @param plugin
     *            the plugin
     */
    void delete( int nIdAppointment, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdAppointment
     *            the identifier of the appointment
     * @param plugin
     *            the plugin
     * @return the instance of the appointment
     */
    Appointment select( int nIdAppointment, Plugin plugin );

    /**
     * Returns all the appointments of a user
     * 
     * @param nIdUser
     *            the User Id
     * @param plugin
     *            the Plugin
     * @return a list of the appointments of the user
     */
    List<Appointment> findByIdUser( int nIdUser, Plugin plugin );

    /**
     * Returns all the appointments of a user by Guid
     * 
     * @param strGuidUser
     *            the User Guid
     * @param plugin
     *            the Plugin
     * @return a list of the appointments of the user by Guid
     */
    List<Appointment> findByGuidUser( String strGuidUser, Plugin plugin );

    /**
     * Returns the appointments of a slot
     * 
     * @param nIdSlot
     *            the Slot Id
     * @param plugin
     *            the plugin
     * @return a list of the appointments
     */
    List<Appointment> findByIdSlot( int nIdSlot, Plugin plugin );

    /**
     * Returns the list of appointments of a slot
     * 
     * @param listIdSlot
     *            the list Slot Id
     * @param plugin
     *            the plugin
     * @return a list of the appointments
     */
    List<Appointment> findByListIdSlot( List<Integer> listIdSlot, Plugin plugin );

    /**
     * Returns the appointment with its reference
     * 
     * @param strReference
     *            the appointment reference
     * @param plugin
     *            the plugin
     * @return the appointment
     */
    Appointment findByReference( String strReference, Plugin plugin );

    /**
     * Returns a list of all the appointment of a form
     * 
     * @param nIdForm
     *            the form id
     * @param plugin
     *            the plugin
     * @return a list of the appointments
     */
    List<Appointment> findByIdForm( int nIdForm, Plugin plugin );

    /**
     * Returns a list of appointments matching the filter
     * 
     * @param appointmentFilter
     *            the filter
     * @param plugin
     *            the plugin
     * @return a list of appointments
     */
    List<Appointment> findByFilter( AppointmentFilterDTO appointmentFilter, Plugin plugin );
}
