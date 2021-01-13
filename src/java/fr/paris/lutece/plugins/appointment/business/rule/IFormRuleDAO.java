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

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * FormRule DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface IFormRuleDAO
{
    /**
     * Insert a new record in the table
     * 
     * @param formRule
     *            instance of the FormRule object to insert
     * @param plugin
     *            the plugin
     */
    void insert( FormRule formRule, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param formRule
     *            the reference of the FormRule
     * @param plugin
     *            the plugin
     */
    void update( FormRule formRule, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdFormRule
     *            identifier of the FormRule to delete
     * @param plugin
     *            the plugin
     */
    void delete( int nIdFormRule, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdForm
     *            identifier of the Form
     * @param plugin
     *            the plugin
     */
    void deleteByIdFom( int nIdForm, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdFormRule
     *            the identifier of the FormRule
     * @param plugin
     *            the plugin
     * @return the instance of the FormRule
     */
    FormRule select( int nIdFormRule, Plugin plugin );

    /**
     * Returns the form rule of a form
     * 
     * @param nIdForm
     *            the form id
     * @param plugin
     *            the plugin
     * @return the form rule of the form
     */
    FormRule findByIdForm( int nIdForm, Plugin plugin );
}
