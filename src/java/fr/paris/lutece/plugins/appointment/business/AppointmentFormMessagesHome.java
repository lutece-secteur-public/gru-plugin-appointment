/*
 * Copyright (c) 2002-2013, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *         and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *         and the following disclaimer in the documentation and/or other materials
 *         provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *         contributors may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
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
package fr.paris.lutece.plugins.appointment.business;

import fr.paris.lutece.plugins.appointment.service.AppointmentFormCacheService;
import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;


/**
 * This class provides instances management methods (create, find, ...) for
 * AppointmentForm objects
 */
public final class AppointmentFormMessagesHome
{
    // Static variable pointed at the DAO instance
    private static IAppointmentFormMessagesDAO _dao = SpringContextService.getBean( 
            "appointment.appointmentFormMessagesDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );
    private static AppointmentFormCacheService _cacheService = AppointmentFormCacheService.getInstance(  );

    /**
     * Private constructor - this class need not be instantiated
     */
    private AppointmentFormMessagesHome(  )
    {
    }

    /**
     * Create a form message
     * @param formMessage The instance of the form message to create
     */
    public static void create( AppointmentFormMessages formMessage )
    {
        _dao.insert( formMessage, _plugin );
        _cacheService.putInCache( AppointmentFormCacheService.getFormMessageCacheKey( formMessage.getIdForm(  ) ),
            formMessage.clone(  ) );
    }

    /**
     * Update a form message
     * @param formMessage The form message to update
     */
    public static void update( AppointmentFormMessages formMessage )
    {
        _dao.store( formMessage, _plugin );
        _cacheService.putInCache( AppointmentFormCacheService.getFormMessageCacheKey( formMessage.getIdForm(  ) ),
            formMessage.clone(  ) );
    }

    /**
     * Remove a form message from its primary key
     * @param nAppointmentFormId The id of the form
     */
    public static void remove( int nAppointmentFormId )
    {
        _dao.delete( nAppointmentFormId, _plugin );
        _cacheService.removeKey( AppointmentFormCacheService.getFormMessageCacheKey( nAppointmentFormId ) );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Get a form message from its primary key
     * @param nAppointmentFormId The id of the form message
     * @return The form message, or null if no form message has the given
     *         primary key
     */
    public static AppointmentFormMessages findByPrimaryKey( int nAppointmentFormId )
    {
        String strCacheKey = AppointmentFormCacheService.getFormMessageCacheKey( nAppointmentFormId );
        AppointmentFormMessages formMessage = (AppointmentFormMessages) _cacheService.getFromCache( strCacheKey );

        if ( formMessage == null )
        {
            formMessage = _dao.load( nAppointmentFormId, _plugin );
            _cacheService.putInCache( strCacheKey, formMessage.clone(  ) );
        }
        else
        {
            formMessage = (AppointmentFormMessages) formMessage.clone(  );
        }

        return formMessage;
    }
}
