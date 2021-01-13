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
package fr.paris.lutece.plugins.appointment.business.planning;

import java.time.LocalDate;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * WeekDefinition DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface IWeekDefinitionDAO
{

    /**
     * Insert a new record in the table
     * 
     * @param appointment
     *            instance of the WeekDefinition object to insert
     * @param plugin
     *            the plugin
     */
    void insert( WeekDefinition weekDefinition, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param weekDefinition
     *            the reference of the WeekDefinition
     * @param plugin
     *            the plugin
     */
    void update( WeekDefinition weekDefinition, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdWeekDefinition
     *            identifier of the WeekDefinition to delete
     * @param plugin
     *            the plugin
     */
    void delete( int nIdWeekDefinition, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdReservationRule
     *            identifier of the reservation rule
     * @param plugin
     *            the plugin
     */
    void deleteByIdReservationRule( int nIdReservationRule, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdWeekDefinition
     *            the identifier of the weekDefinition
     * @param plugin
     *            the plugin
     * @return the instance of the weekDefinition
     */
    WeekDefinition select( int nIdWeekDefinition, Plugin plugin );

    /**
     * Get all the week definitions of a form
     * 
     * @param nIdForm
     *            the form id
     * @param plugin
     *            the plugin
     * @return a list of all the weekdefinitions of the form given
     */
    List<WeekDefinition> findByIdForm( int nIdForm, Plugin plugin );

    /**
     * Get the week definitions of a form for reservation rule
     * 
     * @param nIdReservationRule
     * @param plugin
     *            the plugin
     * @return list of week definition
     */
    List<WeekDefinition> findByReservationRule( int nIdReservationRule, Plugin plugin );

    /**
     * Get the week definitions of a form for the date of apply
     * 
     * @param nIdForm
     *            the form id
     * @param dateOfApply
     *            the date of apply
     * @param plugin
     *            the plugin
     * @return the week definition
     */
    WeekDefinition findByIdFormAndDateOfApply( int nIdForm, LocalDate dateOfApply, Plugin plugin );

    /**
     * Get the week definitions of a form for the date of apply
     * 
     * @param nIdReservationRule
     *            the reservationRule id
     * @param dateOfApply
     *            the date of apply
     * @param plugin
     *            the plugin
     * @return the week definition
     */
    WeekDefinition findByIdReservationRuleAndDateOfApply( int nIdReservationRule, LocalDate dateOfApply, Plugin plugin );

}
