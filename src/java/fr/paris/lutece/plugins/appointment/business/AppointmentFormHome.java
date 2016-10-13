/*
 * Copyright (c) 2002-2015, Mairie de Paris
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

import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDayHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentHoliDaysHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentFormCacheService;
import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.plugins.appointment.service.listeners.AppointmentListenerManager;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.sql.Date;

import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for AppointmentForm objects
 */
public final class AppointmentFormHome
{
    // Static variable pointed at the DAO instance
    private static IAppointmentFormDAO _dao = SpringContextService.getBean( "appointment.appointmentFormDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );
    private static AppointmentFormCacheService _cacheService = AppointmentFormCacheService.getInstance( );

    /**
     * Private constructor - this class need not be instantiated
     */
    private AppointmentFormHome( )
    {
    }

    /**
     * Create an appointment form and its associated appointment form message
     * 
     * @param appointmentForm
     *            The instance of the AppointmentForm which contains the informations to store
     * @param formMessage
     *            The appointment form message associated with the form to create
     */
    public static void create( AppointmentForm appointmentForm, AppointmentFormMessages formMessage )
    {
        _dao.insert( appointmentForm, _plugin );
        _cacheService.putInCache( AppointmentFormCacheService.getFormCacheKey( appointmentForm.getIdForm( ) ), appointmentForm.clone( ) );
        formMessage.setIdForm( appointmentForm.getIdForm( ) );
        AppointmentFormMessagesHome.create( formMessage );
    }

    /**
     * Update of the appointmentForm which is specified in parameter
     * 
     * @param appointmentForm
     *            The instance of the AppointmentForm which contains the data to store
     */
    public static void update( AppointmentForm appointmentForm )
    {
        _dao.store( appointmentForm, _plugin );
        _cacheService.putInCache( AppointmentFormCacheService.getFormCacheKey( appointmentForm.getIdForm( ) ), appointmentForm.clone( ) );
    }

    /**
     * Remove an appointment form by its primary key. Also remove the associated appointment form message and every associated day and slot.<br />
     * <b>Warning, please check that there is no appointment associated with the form BEFORE removing it!</b>
     * 
     * @param nAppointmentFormId
     *            The appointmentForm Id
     */
    public static void remove( int nAppointmentFormId )
    {
        AppointmentListenerManager.notifyListenersAppointmentFormRemoval( nAppointmentFormId );
        AppointmentDayHome.removeByIdForm( nAppointmentFormId );
        AppointmentSlotHome.deleteAllByIdForm( nAppointmentFormId );
        AppointmentFormMessagesHome.remove( nAppointmentFormId );
        AppointmentHoliDaysHome.remove( nAppointmentFormId );
        _dao.delete( nAppointmentFormId, _plugin );
        _cacheService.removeKey( AppointmentFormCacheService.getFormCacheKey( nAppointmentFormId ) );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a appointmentForm whose identifier is specified in parameter
     * 
     * @param nAppointmentFormId
     *            The appointmentForm primary key
     * @return an instance of AppointmentForm
     */
    public static AppointmentForm findByPrimaryKey( int nAppointmentFormId )
    {
        String strCacheKey = AppointmentFormCacheService.getFormCacheKey( nAppointmentFormId );
        AppointmentForm form = (AppointmentForm) _cacheService.getFromCache( strCacheKey );

        if ( form == null )
        {
            form = _dao.load( nAppointmentFormId, _plugin );

            if ( form != null )
            {
                _cacheService.putInCache( strCacheKey, form.clone( ) );
            }
        }
        else
        {
            form = (AppointmentForm) form.clone( );
        }

        return form;
    }

    /**
     * Load the data of all the appointmentForm objects and returns them in form of a collection
     * 
     * @return the list which contains the data of all the appointmentForm objects
     */
    public static List<AppointmentForm> getAppointmentFormsList( )
    {
        return _dao.selectAppointmentFormsList( _plugin );
    }

    /**
     * Load the data of all the appointmentForm objects and returns them in form of a collection
     * 
     * @return the list which contains the data of all the appointmentForm objects
     */
    public static List<AppointmentForm> getActiveAppointmentFormsList( )
    {
        return _dao.selectActiveAppointmentFormsList( _plugin );
    }

    /**
     * Get Unavailable Date limited by Email of an appointment form from a user
     * 
     * @param startDate
     * @param limitedDate
     * @param nForm
     * @param strEmail
     * @return
     */
    public static List<Date> getLimitedByMail( Date startDate, Date [ ] limitedDate, int nForm, String strEmail )
    {
        return _dao.getUnavailableDatesLimitedByMail( startDate, limitedDate, nForm, strEmail.trim( ), _plugin );
    }
}
