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
package fr.paris.lutece.plugins.appointment.business.form;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Form DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface IFormDAO
{
    /**
     * 
     * Insert a new record in the table
     * 
     * @param form
     *            instance of the form object to insert
     * @param plugin
     *            the plugin
     */
    void insert( Form form, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param form
     *            the reference of the form
     * @param plugin
     *            the plugin
     */
    void update( Form form, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdFrom
     *            identifier of the form to delete
     * @param plugin
     *            the plugin
     */
    void delete( int nIdForm, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdForm
     *            the identifier of the form
     * @param plugin
     *            the plugin
     * @return the instance of the Form
     */
    Form select( int nIdForm, Plugin plugin );

    /**
     * Get all the forms that are active
     * 
     * @param plugin
     *            the plugin
     * @return all the active forms
     */
    List<Form> findActiveForms( Plugin plugin );

    /**
     * Get all the forms that are active on displayed on portlet
     * 
     * @param plugin
     *            the plugin
     * @return all the active and displayed on portlet forms
     */
    List<Form> findActiveAndDisplayedOnPortletForms( Plugin plugin );

    /**
     * Get all the forms with a given title
     * 
     * @param strTitle
     *            the Title of the form
     * @param plugin
     *            the plugin
     * @return all the forms with this title
     */
    List<Form> findByTitle( String strTitle, Plugin plugin );

    /**
     * Get all the forms
     * 
     * @param plugin
     *            the plugin
     * @return all the forms
     */
    List<Form> findAllForms( Plugin plugin );

}
