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

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Form objects
 * 
 * @author Laurent Payen
 *
 */
public final class FormHome
{
    // Static variable pointed at the DAO instance
    private static IFormDAO _dao = SpringContextService.getBean( "appointment.formDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private FormHome( )
    {
    }

    /**
     * Create an instance of the Form class
     * 
     * @param form
     *            The instance of the Form which contains the informations to store
     * @return The instance of the Form which has been created with its primary key.
     */
    public static Form create( Form form )
    {
        _dao.insert( form, _plugin );

        return form;
    }

    /**
     * Update of the Form which is specified in parameter
     * 
     * @param form
     *            The instance of the Form which contains the data to store
     * @return The instance of the Form which has been updated
     */
    public static Form update( Form form )
    {
        _dao.update( form, _plugin );

        return form;
    }

    /**
     * Delete the Form whose identifier is specified in parameter
     * 
     * @param nKey
     *            The Form Id
     */
    public static void delete( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of the Form whose identifier is specified in parameter
     * 
     * @param nKey
     *            The Form primary key
     * @return an instance of the Form
     */
    public static Form findByPrimaryKey( int nKey )
    {
        return _dao.select( nKey, _plugin );
    }

    /**
     * Returns an instance of the Form by its title
     * 
     * @param strTitle
     *            The Form title
     * @return a list of the forms with this title
     */
    public static List<Form> findByTitle( String strTitle )
    {
        return _dao.findByTitle( strTitle, _plugin );
    }

    /**
     * Returns all the active forms
     * 
     * @return a list of all the active forms
     */
    public static List<Form> findActiveForms( )
    {
        return _dao.findActiveForms( _plugin );

    }

    /**
     * Returns all the active and displayd on portlet forms
     * 
     * @return a list of all the active and displayed on portlet forms
     */
    public static List<Form> findActiveAndDisplayedOnPortletForms( )
    {
        return _dao.findActiveAndDisplayedOnPortletForms( _plugin );

    }

    /**
     * Returns all the forms
     * 
     * @return a list of all the forms
     */
    public static List<Form> findAllForms( )
    {
        return _dao.findAllForms( _plugin );

    }

}
