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
package fr.paris.lutece.plugins.appointment.business.display;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Display DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface IDisplayDAO
{
    /**
     * Insert a new record in the table.
     * 
     * @param display
     *            instance of the Display object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( Display display, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param display
     *            the reference of the Display
     * @param plugin
     *            the Plugin
     */
    void update( Display display, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdDisplay
     *            identifier of the Display to delete
     * @param plugin
     *            the Plugin
     */
    void delete( int nIdDisplay, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdForm
     *            identifier of the form
     * @param plugin
     *            the Plugin
     */
    void deleteByIdForm( int nIdForm, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdDisplay
     *            The identifier of the Display
     * @param plugin
     *            the Plugin
     * @return The instance of the Display
     */
    Display select( int nIdDisplay, Plugin plugin );

    /**
     * Returns the display of the given form
     * 
     * @param nIdForm
     *            the form id
     * @param plugin
     *            the plugin
     * @return the form display
     */
    Display findByIdForm( int nIdForm, Plugin plugin );
}
