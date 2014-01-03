/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * Home for appointment slots
 */
public class AppointmentSlotHome
{
    private static IAppointmentSlotDAO _dao = SpringContextService.getBean( "appointment.appointmentSlotDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Create a new appointment slot
     * @param slot The appointment slot to create
     */
    public static void create( AppointmentSlot slot )
    {
        _dao.create( slot, _plugin );
    }

    /**
     * Update an appointment slot
     * @param slot The appointment slot to update
     */
    public static void update( AppointmentSlot slot )
    {
        _dao.update( slot, _plugin );
    }

    /**
     * Remove an appointment slot from its id
     * @param nIdSlot The id of the slot to remove
     */
    public static void delete( int nIdSlot )
    {
        _dao.delete( nIdSlot, _plugin );
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
     * Find a slot from its primary key
     * @param nIdSlot the id of the slot to remove
     * @return The appointment slot
     */
    public static AppointmentSlot findByPrimaryKey( int nIdSlot )
    {
        return _dao.findByPrimaryKey( nIdSlot, _plugin );
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

    //    /**
    //     * Change the enabling of slots associated with a given form and a given day
    //     * of the week
    //     * @param nIdForm The id of the form to update
    //     * @param bEnable True to enable slots, false to disable them
    //     * @param nDayOfWeek The day of the week of slots to update
    //     */
    //    public static void updateByIdFormAndDayOfWeek( int nIdForm, boolean bEnable, int nDayOfWeek )
    //    {
    //        _dao.updateByIdFormAndDayOfWeek( nIdForm, bEnable, nDayOfWeek, _plugin );
    //    }

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
     * Delete every slots associated with a given form and a given day of week
     * @param nIdForm The id of the form
     * @param nDayOfWeek The day of the week
     */
    public static void deleteByIdFormAndDayOfWeek( int nIdForm, int nDayOfWeek )
    {
        _dao.deleteByIdFormAndDayOfWeek( nIdForm, nDayOfWeek, _plugin );
    }

    //    /**
    //     * Update the status of slots associated with a given day.
    //     * @param nIdDay The id of the day
    //     * @param bEnable True to enable slots, false to disable them
    //     */
    //    public static void updateByIdDay( int nIdDay, boolean bEnable )
    //    {
    //        _dao.updateByIdDay( nIdDay, bEnable, _plugin );
    //    }
}
