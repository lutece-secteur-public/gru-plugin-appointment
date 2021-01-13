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

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Display objects
 * 
 * @author Laurent Payen
 *
 */
public final class DisplayHome
{
    // Static variable pointed at the DAO instance
    private static IDisplayDAO _dao = SpringContextService.getBean( "appointment.displayDAO");
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private DisplayHome( )
    {
    }

    /**
     * Create an instance of the Display class
     * 
     * @param display
     *            The instance of the Display which contains the informations to store
     * @return The instance of Display which has been created with its primary key.
     */
    public static Display create( Display display )
    {
        _dao.insert( display, _plugin );

        return display;
    }

    /**
     * Update of the Display which is specified in parameter
     * 
     * @param display
     *            The instance of the Display which contains the data to store
     * @return The instance of the Display which has been updated
     */
    public static Display update( Display display )
    {
        _dao.update( display, _plugin );

        return display;
    }

    /**
     * Delete the Display whose identifier is specified in parameter
     * 
     * @param nKey
     *            The Display Id
     */
    public static void delete( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Delete the Display whose id form is specified in parameter
     * 
     * @param nIdForm
     *            The form Id
     */
    public static void deleteByIdForm( int nIdForm )
    {
        _dao.deleteByIdForm( nIdForm, _plugin );
    }

    /**
     * Returns an instance of the Display whose identifier is specified in parameter
     * 
     * @param nKey
     *            The Display primary key
     * @return an instance of the Display
     */
    public static Display findByPrimaryKey( int nKey )
    {
        return _dao.select( nKey, _plugin );
    }

    /**
     * Returns the form display
     * 
     * @param nIdForm
     *            the form id
     * @return the form display
     */
    public static Display findByIdForm( int nIdForm )
    {
        return _dao.findByIdForm( nIdForm, _plugin );
    }

}
