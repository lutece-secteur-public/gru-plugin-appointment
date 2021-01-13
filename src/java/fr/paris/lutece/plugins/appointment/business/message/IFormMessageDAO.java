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
package fr.paris.lutece.plugins.appointment.business.message;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Form Message DAO Interface
 * 
 * @author Laurent Payen
 */
public interface IFormMessageDAO
{
    /**
     * Create a form message
     * 
     * @param formMessage
     *            The instance of the form message to create
     * @param plugin
     *            The plugin
     */
    void insert( FormMessage formMessage, Plugin plugin );

    /**
     * Update a form message
     * 
     * @param formMessage
     *            The form message to update
     * @param plugin
     *            The plugin
     */
    void update( FormMessage formMessage, Plugin plugin );

    /**
     * Remove a form message from its primary key
     * 
     * @param nAppointmentFormId
     *            The id of the form
     * @param plugin
     *            The plugin
     */
    void delete( int nAppointmentFormId, Plugin plugin );

    /**
     * Remove a form message from
     * 
     * @param nFormId
     *            The id of the form
     * @param plugin
     *            The plugin
     */
    void deleteByIdForm( int nIdForm, Plugin plugin );

    /**
     * Get a form message from its primary key
     * 
     * @param nIdFormMessage
     *            The id of the form message
     * @param plugin
     *            The plugin
     * @return The form message, or null if no form message has the given primary key
     */
    FormMessage select( int nIdFormMessage, Plugin plugin );

    /**
     * Returns the formMessage of the form given
     * 
     * @param nIdForm
     *            the form id
     * @param plugin
     *            the plugin
     * @return the formMessage of the form
     */
    FormMessage findByIdForm( int nIdForm, Plugin plugin );

}
