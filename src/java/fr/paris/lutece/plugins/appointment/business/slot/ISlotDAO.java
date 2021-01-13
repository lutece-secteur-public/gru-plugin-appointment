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
package fr.paris.lutece.plugins.appointment.business.slot;

import java.time.LocalDateTime;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Slot DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface ISlotDAO
{
    /**
     * Insert a new record in the table
     * 
     * @param slot
     *            instance of the Slot object to insert
     * @param plugin
     *            the plugin
     */
    void insert( Slot slot, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param slot
     *            the reference of the Slot
     * @param plugin
     *            the plugin
     */
    void update( Slot slot, Plugin plugin );

    /**
     * Delete a appointment from the table
     * 
     * @param nIdSlot
     *            identifier of the Slot to delete
     * @param plugin
     *            the plugin
     */
    void delete( int nIdSlot, Plugin plugin );

    /**
     * Delete a appointment from the table
     * 
     * @param nIdForm
     *            identifier of the form
     * @param plugin
     *            the plugin
     */
    public void deleteByIdForm( int nIdForm, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdSlot
     *            the identifier of the Slot
     * @param plugin
     *            the plugin
     * @return the instance of the Slot
     */
    Slot select( int nIdSlot, Plugin plugin );

    /**
     * Returns all the slot for the date range
     * 
     * @param nIdForm
     *            the Form Id
     * @param startingDateTime
     *            the starting date
     * @param endingDateTime
     *            the ending date
     * @param plugin
     *            the plugin
     * @return a list of slots whose dates are included in the given period
     */
    List<Slot> findByIdFormAndDateRange( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime, Plugin plugin );

    /**
     * Returns all the slot containing an appointment for the date range
     * 
     * @param nIdForm
     *            the Form Id
     * @param startingDateTime
     *            the starting date
     * @param endingDateTime
     *            the ending date
     * @param plugin
     *            the plugin
     * @return a list of slots whose dates are included in the given period and is containing an appointment
     */
    List<Slot> findSlotWithAppointmentByDateRange( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime, Plugin plugin );

    /**
     * Returns all the specific slot for the form
     * 
     * @param nIdForm
     *            the Form Id
     * @param plugin
     *            the plugin
     * @return a list of specific slots
     */
    List<Slot> findIsSpecificByIdForm( int nIdForm, Plugin plugin );

    /**
     * Returns all the slots of a form
     * 
     * @param nIdForm
     *            the form id
     * @param plugin
     *            the plugin
     * @return a list of all the slots of the form
     */
    List<Slot> findByIdForm( int nIdForm, Plugin plugin );

    /**
     * Returns all the open slots for the given date range
     * 
     * @param nIdForm
     *            the Form Id
     * @param startingDateTime
     *            the starting date
     * @param endingDateTime
     *            the ending date
     * @param plugin
     *            the plugin
     * @return a list of open slots whose dates are included in the given period
     */
    List<Slot> findOpenSlotsByIdFormAndDateRange( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime, Plugin plugin );

    /**
     * Returns all the open slots
     * 
     * @param nIdForm
     *            the Form Id
     * @param plugin
     *            the plugin
     * @return a list of open slots
     */
    List<Slot> findOpenSlotsByIdForm( int nIdForm, Plugin plugin );

    /**
     * Return the slot with the max date
     * 
     * @param nIdForm
     *            the form id
     * @param plugin
     *            the plugin
     * @return the slot
     */
    Slot findSlotWithMaxDate( int nIdForm, Plugin plugin );

    /**
     * Returns all the slots of a form
     * 
     * @param nIdAppointment
     *            the appointment id
     * @param plugin
     *            the plugin
     * @return a list of all the slots of the form
     */
    List<Slot> findByIdAppointment( int nIdAppointment, Plugin plugin );

    /**
     * update Potential Remaining Places
     * 
     * @param nbPotentialRemainingPlaces
     * @param nIdSlot
     * @param plugin
     */
    void updatePotentialRemainingPlaces( int nbPotentialRemainingPlaces, int nIdSlot, Plugin plugin );

    /**
     * update the availabilities metrics
     * 
     * @param plugin
     *            the plugin
     */
    void resetPotentialRemainingPlaces( Plugin plugin );

}
