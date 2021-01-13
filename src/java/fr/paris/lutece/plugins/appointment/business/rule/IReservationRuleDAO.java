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
package fr.paris.lutece.plugins.appointment.business.rule;

import java.time.LocalDate;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * ReservationRule DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface IReservationRuleDAO
{
	
    /**
     * Insert a new record in the table
     * 
     * @param reservationRule
     *            instance of the ReservationRule object to insert
     * @param plugin
     *            the plugin
     */
    void insert( ReservationRule reservationRule, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param reservationRule
     *            the reference of the ReservationRule
     * @param plugin
     *            the plugin
     */
    void update( ReservationRule appointment, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdReservationRule
     *            int identifier of the ReservationRule to delete
     * @param plugin
     *            the plugin
     */
    void delete( int nIdReservationRule, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdReservationRule
     *            the identifier of the ReservationRule
     * @param plugin
     *            the plugin
     * @return the instance of the ReservationRule
     */
    ReservationRule select( int nIdReservationRule, Plugin plugin );

    /**
     * Returns all the Reservation Rule of the form given
     * 
     * @param nIdForm
     *            the Form Id
     * @param plugin
     *            the plugin
     * @return a list of reservation rule of the form
     */
    List<ReservationRule> findByIdForm( int nIdForm, Plugin plugin );

    /**
     * Returns the Reservation Rule with the given search parameters
     * 
     * @param nIdForm
     *            the Form Id
     * @param dateOfApply
     *            the date of apply
     * @param plugin
     *            the plugin
     * @return the reservation rule that matches
     */
    ReservationRule findByIdFormAndDateOfApply( int nIdForm, LocalDate dateOfApply, Plugin plugin );

    /**
     * Find in database a reservation rule of a form closest to a date
     * 
     * @param nIdForm
     *            the Form Id
     * @param dateOfApply
     *            the date of apply
     * @param plugin
     *            the plugin
     * @return the reservation rule that matches
     */
    ReservationRule findReservationRuleByIdFormAndClosestToDateOfApply( int nIdForm, LocalDate dateOfApply, Plugin plugin );

}
