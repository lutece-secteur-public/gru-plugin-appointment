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

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.sql.Date;
import java.util.List;


/**
 * Interface for DAO for slots
 */
public interface IAppointmentSlotDAO
{
    /**
     * Create a new appointment slot
     * @param slot The appointment slot to create
     * @param plugin The plugin
     */
    void create( AppointmentSlot slot, Plugin plugin );

    /**
     * Update an appointment slot
     * @param slot The appointment slot to update
     * @param plugin The plugin
     */
    void update( AppointmentSlot slot, Plugin plugin );

    /**
     * Remove an appointment slot from its id
     * @param nIdSlot The id of the slot to remove
     * @param plugin The plugin
     */
    void delete( int nIdSlot, Plugin plugin );

    /**
     * Remove an appointment slot associated with a given day
     * @param nIdDay The id of the day to remove slots from
     * @param plugin The plugin
     */
    void deleteByIdDay( int nIdDay, Plugin plugin );

    /**
     * Remove appointment slots associated with a given form. <b>Slots that are
     * associated with a day are also removed</b>
     * @param nIdForm The id of the form to remove slots from
     * @param plugin The plugin
     */
    void deleteAllByIdForm( int nIdForm, Plugin plugin );

    /**
     * Remove appointment slots associated with a given form. <b>Slots that are
     * associated with a day are NOT removed</b>
     * @param nIdForm The id of the form to remove slots from
     * @param plugin The plugin
     */
    void deleteByIdForm( int nIdForm, Plugin plugin );

    /**
     * Find a slot from its primary key
     * @param nIdSlot the id of the slot to remove
     * @param plugin The plugin
     * @return The appointment slot
     */
    AppointmentSlot findByPrimaryKey( int nIdSlot, Plugin plugin );

    /**
     * Find a slot from its primary key. Also load the number of free places for
     * a given date
     * @param nIdSlot The id of the slot
     * @param date The date of the day to check for places
     * @param plugin The plugin
     * @return The slot, or null if no slot was found
     */
    AppointmentSlot findByPrimaryKeyWithFreePlaces( int nIdSlot, Date date, Plugin plugin );

    /**
     * Find every slots associated with a given form and not associated with any
     * day
     * @param nIdForm the id of the form
     * @param plugin The plugin
     * @return The list of slots
     */
    List<AppointmentSlot> findByIdForm( int nIdForm, Plugin plugin );

    /**
     * Find every slots associated with a given day
     * @param nIdDay the id of the day
     * @param plugin The plugin
     * @return The list of slots
     */
    List<AppointmentSlot> findByIdDay( int nIdDay, Plugin plugin );

    /**
     * Get the list of slots associated with a given form for a given day of
     * week.
     * @param nIdForm the if of the form
     * @param nDayOfWeek The day of the week (1 for Monday, 2 for Tuesday, ...)
     * @param plugin The plugin
     * @return the list of slots
     */
    List<AppointmentSlot> findByIdFormAndDayOfWeek( int nIdForm, int nDayOfWeek, Plugin plugin );

    /**
     * Delete every slots associated with a given form and a given day of week
     * @param nIdForm The id of the form
     * @param nDayOfWeek The day of the week
     * @param plugin The plugin
     */
    void deleteByIdFormAndDayOfWeek( int nIdForm, int nDayOfWeek, Plugin plugin );

    /**
     * Get the list of slots associated with a given day, and compute for each
     * slot the number of free places
     * @param nIdDay The id of the day
     * @param plugin The plugin
     * @return The list of slots
     */
    List<AppointmentSlot> findByIdDayWithFreePlaces( int nIdDay, Plugin plugin );

    /**
     * Get the list of slots associated with a given form for a given day of
     * week. Also compute the number of free places for each slot
     * @param nIdForm the if of the form
     * @param nDayOfWeek The day of the week (1 for Monday, 2 for Tuesday, ...)
     * @param dateDay The date of the day
     * @param plugin The plugin
     * @return the list of slots
     */
    List<AppointmentSlot> findByIdFormWithFreePlaces( int nIdForm, int nDayOfWeek, Date dateDay, Plugin _plugin );
}
