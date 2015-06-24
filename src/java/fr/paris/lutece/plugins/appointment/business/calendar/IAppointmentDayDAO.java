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
package fr.paris.lutece.plugins.appointment.business.calendar;

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.sql.Date;
import java.util.List;


/**
 * Interface of DAO for days
 */
public interface IAppointmentDayDAO
{
    /**
     * Creates a new day in the database
     * @param day The day to create
     * @param plugin The plugin
     */
    void create( AppointmentDay day, Plugin plugin );

    /**
     * Update a day
     * @param day The day to create
     * @param plugin The plugin
     */
    void update( AppointmentDay day, Plugin plugin );

    /**
     * Remove a day from the database
     * @param nIdDay The id of the day to remove
     * @param plugin The plugin
     */
    void remove( int nIdDay, Plugin plugin );

    /**
     * Remove days from the database that are associated with a given form
     * @param nIdForm The id of the form
     * @param plugin The plugin
     */
    void removeByIdForm( int nIdForm, Plugin plugin );

    /**
     * Delete days which date is before a given date and that are not associated
     * with any slot
     * @param dateMonday The date of days
     * @param plugin The plugin
     */
    void removeLonelyDays( Date dateMonday, Plugin plugin );

    /**
     * Returns an instance of a day whose identifier is specified in
     * parameter
     * @param nKey The appointmentDay primary key
     * @param plugin The plugin
     * @return an instance of AppointmentDay
     */
    AppointmentDay findByPrimaryKey( int nKey, Plugin plugin );

    /**
     * Get the list of days associated with a form and which date are between 2
     * given dates.
     * @param nIdForm The id of the form
     * @param dateMin The minimum date
     * @param dateMax The maximum date
     * @param plugin The plugin
     * @return The list of days
     */
    List<AppointmentDay> getDaysBetween( int nIdForm, Date dateMin, Date dateMax, Plugin plugin );

    /**
     * Decrement or increment by 1 the number of free places of an appointment
     * @param day The day to update
     * @param bIncrement True to increment the number of free places by 1, false
     *            to decrement it
     * @param plugin The plugin
     */
    void updateDayFreePlaces( AppointmentDay day, boolean bIncrement, Plugin plugin );
    /**
     * Find every day associated with a given form and not associated with any
     * day
     * @param nIdForm the id of the form
     * @param plugin The plugin
     * @return The list of days
     */
    List<AppointmentDay> findByIdForm( int nIdForm, Plugin plugin );
}
