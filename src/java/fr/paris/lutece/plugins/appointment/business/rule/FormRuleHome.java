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

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Form Rule objects
 * 
 * @author Laurent Payen
 *
 */
public final class FormRuleHome
{

    // Static variable pointed at the DAO instance
    private static IFormRuleDAO _dao = SpringContextService.getBean(  "appointment.formRuleDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private FormRuleHome( )
    {
    }

    /**
     * Create an instance of the FormRule class
     * 
     * @param formRule
     *            The instance of the FormRule which contains the informations to store
     * @return The instance of the FormRule which has been created with its primary key.
     */
    public static FormRule create( FormRule formRule )
    {
        _dao.insert( formRule, _plugin );

        return formRule;
    }

    /**
     * Update of the FormRule which is specified in parameter
     * 
     * @param formRule
     *            The instance of the FormRule which contains the data to store
     * @return The instance of the FormRule which has been updated
     */
    public static FormRule update( FormRule formRule )
    {
        _dao.update( formRule, _plugin );

        return formRule;
    }

    /**
     * Delete the FormRule whose identifier is specified in parameter
     * 
     * @param nKey
     *            The FormRule Id
     */
    public static void delete( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Delete the FormRule whose id form is specified in parameter
     * 
     * @param nIdForm
     *            The Form Id
     */
    public static void deleteByIdFom( int nIdForm )
    {
        _dao.deleteByIdFom( nIdForm, _plugin );
    }

    /**
     * Returns an instance of the FormRule whose identifier is specified in parameter
     * 
     * @param nKey
     *            The FormRule primary key
     * @return an instance of the FormRule
     */
    public static FormRule findByPrimaryKey( int nKey )
    {
        return _dao.select( nKey, _plugin );
    }

    /**
     * Returns the form rule of a form
     * 
     * @param nIdForm
     *            the form id
     * @return the form rule
     */
    public static FormRule findByIdForm( int nIdForm )
    {
        return _dao.findByIdForm( nIdForm, _plugin );
    }

}
