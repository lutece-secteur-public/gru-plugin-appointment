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
package fr.paris.lutece.plugins.appointment.business.planning;

import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Time Slot objects
 * 
 * @author Laurent Payen
 *
 */
public final class TimeSlotHome
{
    // Static variable pointed at the DAO instance
    private static ITimeSlotDAO _dao = SpringContextService.getBean( "appointment.timeSlotDAO");
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private TimeSlotHome( )
    {
    }

    /**
     * Create an instance of the TimeSlot class
     * 
     * @param timeSlot
     *            The instance of the TimeSlot which contains the informations to store
     * @return The instance of the TimeSlot which has been created with its primary key.
     */
    public static TimeSlot create( TimeSlot timeSlot )
    {
        _dao.insert( timeSlot, _plugin );

        return timeSlot;
    }

    /**
     * Update of the TimeSlot which is specified in parameter
     * 
     * @param timeSlot
     *            The instance of the TimeSlot which contains the data to store
     * @return The instance of the TimeSlot which has been updated
     */
    public static TimeSlot update( TimeSlot timeSlot )
    {
        _dao.update( timeSlot, _plugin );

        return timeSlot;
    }

    /**
     * Delete the TimeSlot whose identifier is specified in parameter
     * 
     * @param nKey
     *            The TimeSlot Id
     */
    public static void delete( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Delete TimeSlot whose id working day is specified in parameter
     * 
     * @param nIdWorkingDay
     *            identifier of the working day
     */
    public static void deleteByIdWorkingDay( int nIdWorkingDay )
    {

        _dao.deleteByIdWorkingDay( nIdWorkingDay, _plugin );
    }

    /**
     * Returns an instance of the TimeSlot whose identifier is specified in parameter
     * 
     * @param nKey
     *            The TimeSlot primary key
     * @return an instance of the TimeSlot
     */
    public static TimeSlot findByPrimaryKey( int nKey )
    {
        return _dao.select( nKey, _plugin );
    }

    /**
     * Get all the time slots of the working day given
     * 
     * @param nIdWorkingDay
     *            the working day id
     * @return the list of all the time slots of the working day
     */
    public static List<TimeSlot> findByIdWorkingDay( int nIdWorkingDay )
    {
        return _dao.findByIdWorkingDay( nIdWorkingDay, _plugin );
    }

}
