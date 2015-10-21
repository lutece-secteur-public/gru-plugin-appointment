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
package fr.paris.lutece.plugins.appointment.business.calendar;

import fr.paris.lutece.plugins.appointment.service.AppointmentFormCacheService;
import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.sql.Date;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for
 * AppointmentDay objects
 */
public final class AppointmentDayHome
{
    // Static variable pointed at the DAO instance
    private static IAppointmentDayDAO _dao = SpringContextService.getBean( "appointment.appointmentDayDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class need not be instantiated
     */
    private AppointmentDayHome(  )
    {
    }

    /**
     * Creates a new day in the database
     * @param day The day to create
     */
    public static void create( AppointmentDay day )
    {
        _dao.create( day, _plugin );

        AppointmentDay dayClone = day.clone(  );
        dayClone.setListSlots( null );
        AppointmentFormCacheService.getInstance(  )
                                   .putInCache( AppointmentFormCacheService.getAppointmentDayKey( day.getIdDay(  ) ),
            dayClone );
    }

    /**
     * Update a day
     * @param day The day to create
     */
    public static synchronized void update( AppointmentDay day )
    {
        _dao.update( day, _plugin );

        AppointmentDay dayClone = day.clone(  );
        dayClone.setListSlots( null );
        AppointmentFormCacheService.getInstance(  )
                                   .putInCache( AppointmentFormCacheService.getAppointmentDayKey( day.getIdDay(  ) ),
            dayClone );
    }

    /**
     * Remove a day from the database
     * @param nIdDay The id of the day to remove
     */
    public static void remove( int nIdDay )
    {
        AppointmentSlotHome.deleteByIdDay( nIdDay );
        _dao.remove( nIdDay, _plugin );
        AppointmentFormCacheService.getInstance(  ).removeKey( AppointmentFormCacheService.getAppointmentDayKey( nIdDay ) );
    }

    /**
     * Remove days from the database that are associated with a given form
     * @param nIdForm The id of the form
     */
    public static void removeByIdForm( int nIdForm )
    {
        _dao.removeByIdForm( nIdForm, _plugin );
    }

    /**
     * Delete days which date is before a given date and that are not associated
     * with any slot
     * @param dateMonday The date of days
     */
    public static void removeLonelyDays( Date dateMonday )
    {
        _dao.removeLonelyDays( dateMonday, _plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a day whose identifier is specified in
     * parameter
     * @param nIdDay The appointmentDay primary key
     * @return an instance of AppointmentDay
     */
    public static AppointmentDay findByPrimaryKey( int nIdDay )
    {
        String strKey = AppointmentFormCacheService.getAppointmentDayKey( nIdDay );
        AppointmentDay day = (AppointmentDay) AppointmentFormCacheService.getInstance(  ).getFromCache( strKey );

        if ( day != null )
        {
            return day.clone(  );
        }

        day = _dao.findByPrimaryKey( nIdDay, _plugin );

        if ( day != null )
        {
            AppointmentFormCacheService.getInstance(  ).putInCache( strKey, day.clone(  ) );
        }

        return day;
    }

    /**
     * Get the list of days associated with a form and which date are between 2
     * given dates.
     * @param nIdForm The id of the form
     * @param dateMin The minimum date
     * @param dateMax The maximum date
     * @return The list of days
     */
    public static List<AppointmentDay> getDaysBetween( int nIdForm, Date dateMin, Date dateMax )
    {
        return _dao.getDaysBetween( nIdForm, dateMin, dateMax, _plugin );
    }

    /**
     * Increment by 1 the number of free places of an appointment
     * @param nIdDay The id of the day to update
     */
    public static synchronized void incrementDayFreePlaces( int nIdDay )
    {
        AppointmentDay day = findByPrimaryKey( nIdDay );
        _dao.updateDayFreePlaces( day, true, _plugin );
        AppointmentFormCacheService.getInstance(  )
                                   .putInCache( AppointmentFormCacheService.getAppointmentDayKey( day.getIdDay(  ) ),
            day.clone(  ) );
    }

    /**
     * Decrement by 1 the number of free places of an appointment
     * @param nIdDay The id of the day to update
     */
    public static synchronized void decrementDayFreePlaces( int nIdDay )
    {
        AppointmentDay day = findByPrimaryKey( nIdDay );
        _dao.updateDayFreePlaces( day, false, _plugin );
        AppointmentFormCacheService.getInstance(  )
                                   .putInCache( AppointmentFormCacheService.getAppointmentDayKey( day.getIdDay(  ) ),
            day.clone(  ) );
    }

    /**
     * Reset the number of free places of a day
     * @param nIdDay The id of the day
     */
    public static synchronized void resetDayFreePlaces( int nIdDay )
    {
        AppointmentDay day = findByPrimaryKey( nIdDay );
        List<AppointmentSlot> listAppointmentSlot = AppointmentSlotHome.findByIdDayWithFreePlaces( nIdDay );
        int nFreePlaces = 0;

        for ( AppointmentSlot slot : listAppointmentSlot )
        {
            if ( slot.getIsEnabled(  ) )
            {
                nFreePlaces += slot.getNbFreePlaces(  );
            }
        }

        day.setFreePlaces( nFreePlaces );
        update( day );
    }

    /**
     * Find every day associated with a given form.
     * @param nIdForm the id of the form
     * @return The list of slots
     */
    public static List<AppointmentDay> findByIdForm( int nIdForm )
    {
        return _dao.findByIdForm( nIdForm, _plugin );
    }
}
