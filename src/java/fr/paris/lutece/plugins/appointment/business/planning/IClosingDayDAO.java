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
 * ClosingDay DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface IClosingDayDAO
{
    /**
     * Insert a new record in the table.
     * 
     * @param closingDay
     *            instance of the Closing Day object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( ClosingDay closingDay, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param closingDay
     *            the reference of the Closing Day
     * @param plugin
     *            the Plugin
     */
    void update( ClosingDay closingDay, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdClosingDay
     *            int identifier of the Closing Day to delete
     * @param plugin
     *            the Plugin
     */
    void delete( int nIdClosingDay, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdClosingDay
     *            The identifier of the Closing Day
     * @param plugin
     *            the Plugin
     * @return The instance of the Closing Day
     */
    ClosingDay select( int nIdClosingDay, Plugin plugin );

    /**
     * Return the closing day if exists
     * 
     * @param nIdForm
     *            the Form Id
     * @param dateOfCLosingDay
     *            the date of the closing day
     * @param plugin
     *            the plugin
     * @return the closing day if exists
     */
    ClosingDay findByIdFormAndDateOfClosingDay( int nIdForm, LocalDate dateOfCLosingDay, Plugin plugin );

    /**
     * Returns the closing days of a form
     * 
     * @param nIdForm
     *            the form Id
     * @param plugin
     *            the plugin
     * @return a list of the closing days of the form
     */
    List<ClosingDay> findByIdForm( int nIdForm, Plugin plugin );

    /**
     * Returns the closing days of the form on a period
     * 
     * @param nIdForm
     *            the form Id
     * @param startingDate
     *            the starting date
     * @param endingDate
     *            the ending date
     * @param plugin
     *            the plugin
     * @return the list of the closing days that matches the criteria
     */
    List<ClosingDay> findByIdFormAndDateRange( int nIdForm, LocalDate startingDate, LocalDate endingDate, Plugin plugin );

}
