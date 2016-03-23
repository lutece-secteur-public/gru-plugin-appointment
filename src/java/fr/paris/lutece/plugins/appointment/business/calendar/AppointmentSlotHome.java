/*
 * Copyright (c) 2002-2015, Mairie de Paris
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
package fr.paris.lutece.plugins.appointment.business.calendar;

import fr.paris.lutece.plugins.appointment.service.AppointmentFormCacheService;
import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.sql.Date;

import java.util.List;


/**
 * Home for appointment slots
 */
public final class AppointmentSlotHome
{
    private static IAppointmentSlotDAO _dao = SpringContextService.getBean( "appointment.appointmentSlotDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Default constructor
     */
    private AppointmentSlotHome(  )
    {
        // Private constructor
    }

    /**
     * Create a new appointment slot
     * @param slot The appointment slot to create
     */
    public static void create( AppointmentSlot slot )
    {
        _dao.create( slot, _plugin );
        AppointmentFormCacheService.getInstance(  )
                                   .putInCache( AppointmentFormCacheService.getAppointmentSlotKey( slot.getIdSlot(  ) ),
            slot.clone(  ) );
    }

    /**
     * Update an appointment slot
     * @param slot The appointment slot to update
     */
    public static void update( AppointmentSlot slot )
    {
        AppointmentSlot slotFromDb = findByPrimaryKey( slot.getIdSlot(  ) );

        _dao.update( slot, _plugin );

        AppointmentFormCacheService.getInstance(  )
                                   .putInCache( AppointmentFormCacheService.getAppointmentSlotKey( slot.getIdSlot(  ) ),
            slot.clone(  ) );

        if ( slot.getIdDay(  ) > 0 )
        {
            if ( slotFromDb.getIsEnabled(  ) ^ slot.getIsEnabled(  ) )
            {
                AppointmentDay day = AppointmentDayHome.findByPrimaryKey( slot.getIdDay(  ) );
                day.setFreePlaces( slot.getIsEnabled(  ) ? ( day.getFreePlaces(  ) + slot.getNbPlaces(  ) )
                                                         : ( day.getFreePlaces(  ) - slot.getNbPlaces(  ) ) );
                AppointmentDayHome.update( day );
            }

            if ( slotFromDb.getNbPlaces(  ) != slot.getNbPlaces(  ) )
            {
                AppointmentDay day = AppointmentDayHome.findByPrimaryKey( slot.getIdDay(  ) );
                day.setFreePlaces( ( day.getFreePlaces(  ) + slot.getNbPlaces(  ) ) - slotFromDb.getNbPlaces(  ) );
                AppointmentDayHome.update( day );
            }
        }
    }

    /**
     * Remove an appointment slot from its id
     * @param nIdSlot The id of the slot to remove
     */
    public static void delete( int nIdSlot )
    {
        _dao.delete( nIdSlot, _plugin );
        AppointmentFormCacheService.getInstance(  )
                                   .removeKey( AppointmentFormCacheService.getAppointmentSlotKey( nIdSlot ) );
    }

    /**
     * Remove an appointment slot associated with a given day
     * @param nIdDay The id of the day to remove slots from
     */
    public static void deleteByIdDay( int nIdDay )
    {
        _dao.deleteByIdDay( nIdDay, _plugin );
    }

    /**
     * Remove appointment slots associated with a given form. <b>Slots that are
     * associated with a day are also removed</b>
     * @param nIdForm The id of the form to remove slots from
     */
    public static void deleteAllByIdForm( int nIdForm )
    {
        _dao.deleteAllByIdForm( nIdForm, _plugin );
    }

    /**
     * Remove appointment slots associated with a given form. <b>Slots that are
     * associated with a day are NOT removed</b>
     * @param nIdForm The id of the form to remove slots from
     */
    public static void deleteByIdForm( int nIdForm )
    {
        _dao.deleteByIdForm( nIdForm, _plugin );
    }

    /**
     * Delete every slots associated with a given form and a given day of week
     * @param nIdForm The id of the form
     * @param nDayOfWeek The day of the week
     */
    public static void deleteByIdFormAndDayOfWeek( int nIdForm, int nDayOfWeek )
    {
        _dao.deleteByIdFormAndDayOfWeek( nIdForm, nDayOfWeek, _plugin );
    }

    /**
     * Delete every slots that are associated with a day before a given day.
     * @param dateMonday The date of the day to remove slots
     */
    public static void deleteOldSlots( Date dateMonday )
    {
        _dao.deleteOldSlots( dateMonday, _plugin );
    }

    /**
     * Find a slot from its primary key
     * @param nIdSlot the id of the slot to remove
     * @return The appointment slot
     */
    public static AppointmentSlot findByPrimaryKey( int nIdSlot )
    {
        String strKey = AppointmentFormCacheService.getAppointmentSlotKey( nIdSlot );

        AppointmentSlot slot = (AppointmentSlot) AppointmentFormCacheService.getInstance(  ).getFromCache( strKey );

        if ( slot != null )
        {
            return slot.clone(  );
        }

        slot = _dao.findByPrimaryKey( nIdSlot, _plugin );

        if ( slot != null )
        {
            AppointmentFormCacheService.getInstance(  ).putInCache( strKey, slot.clone(  ) );
        }

        return slot;
    }

    /**
     * Find a slot from its primary key
     * @param nIdSlot the id of the slot to remove
     * @return The appointment slot
     */
    public static AppointmentSlot findByPrimaryKeyWithFreePlace( int nIdSlot )
    {
        AppointmentSlot slot = _dao.findByPrimaryKeyWithFreePlace( nIdSlot, _plugin );

        return slot;
    }

    /**
     * Find a slot from its primary key
     * @param nIdSlot the id of the slot to remove
     * @param date The date of the day of the slot
     * @return The appointment slot
     */
    public static AppointmentSlot findByPrimaryKeyWithFreePlaces( int nIdSlot, Date date )
    {
        return _dao.findByPrimaryKeyWithFreePlaces( nIdSlot, date, _plugin );
    }

    /**
     * Find every slots associated with a given form and not associated with any
     * day
     * @param nIdForm the id of the form
     * @return The list of slots
     */
    public static List<AppointmentSlot> findByIdForm( int nIdForm )
    {
        return _dao.findByIdForm( nIdForm, _plugin );
    }

    /**
     * Find every slots associated with a given day
     * @param nIdDay the id of the day
     * @return The list of slots
     */
    public static List<AppointmentSlot> findByIdDay( int nIdDay )
    {
        return _dao.findByIdDay( nIdDay, _plugin );
    }

    /**
     * Get the list of slots associated with a given form for a given day of
     * week.
     * @param nIdForm the if of the form
     * @param nDayOfWeek The day of the week (1 for Monday, 2 for Tuesday, ...)
     * @return the list of slots
     */
    public static List<AppointmentSlot> findByIdFormAndDayOfWeek( int nIdForm, int nDayOfWeek )
    {
        return _dao.findByIdFormAndDayOfWeek( nIdForm, nDayOfWeek, _plugin );
    }

    /**
     * Get the list of slots associated with a given day, and compute for each
     * slot the number of free places
     * @param nIdDay The id of the day
     * @return The list of slots
     */
    public static List<AppointmentSlot> findByIdDayWithFreePlaces( int nIdDay )
    {
        // TODO : save list slots in cache
        return _dao.findByIdDayWithFreePlaces( nIdDay, _plugin );
    }

    /**
     * Get appointments slot associated unavailable with a given day
     * @param nIdDay The id of the day to remove slots from
     */
    public static List<AppointmentSlot> getSlotsUnavailable( int nIdDay, int nIdForm )
    {
        return _dao.getSlotsUnavailable( nIdDay, nIdForm, _plugin );
    }

    /**
     *
     * @param nIdForm
     * @return
     */
    public static List<AppointmentSlot> findByIdFormAll( int nIdForm )
    {
        return _dao.findByIdFormAll( nIdForm, _plugin );
    }
}
