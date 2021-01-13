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
package fr.paris.lutece.plugins.appointment.business.slot;

import java.time.LocalDateTime;
import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Slot objects
 * 
 * @author Laurent Payen
 *
 */
public final class SlotHome
{
    // Static variable pointed at the DAO instance
    private static ISlotDAO _dao = SpringContextService.getBean( "appointment.slotDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private SlotHome( )
    {
    }

    /**
     * Create an instance of the Slot class
     * 
     * @param slot
     *            The instance of the Slot which contains the informations to store
     * @return The instance of the Slot which has been created with its primary key.
     */
    public static Slot create( Slot slot )
    {
        _dao.insert( slot, _plugin );

        return slot;
    }

    /**
     * Update of the Slot which is specified in parameter
     * 
     * @param slot
     *            The instance of the Slot which contains the data to store
     * @return The instance of the Slot which has been updated
     */
    public static Slot update( Slot slot )
    {
        _dao.update( slot, _plugin );

        return slot;
    }

    /**
     * Delete the Slot whose identifier is specified in parameter
     * 
     * @param nKey
     *            The Slot Id
     */
    public static void delete( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Delete a appointment from the table
     * 
     * @param nIdForm
     *            identifier of the form
     */
    public static void deleteByIdForm( int nIdForm )
    {

        _dao.deleteByIdForm( nIdForm, _plugin );
    }

    /**
     * Returns an instance of the Slot whose identifier is specified in parameter
     * 
     * @param nKey
     *            The Slot primary key
     * @return an instance of the Slot
     */
    public static Slot findByPrimaryKey( int nKey )
    {
        return _dao.select( nKey, _plugin );
    }

    /**
     * Returns a list of slots for a date range
     * 
     * @param nIdForm
     *            the Form Id
     * @param startingDateTime
     *            the starting Date
     * @param endingDateTime
     *            the ending Date
     * @return a list of slots whose dates are included in the given period
     */
    public static List<Slot> findByIdFormAndDateRange( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime )
    {
        return _dao.findByIdFormAndDateRange( nIdForm, startingDateTime, endingDateTime, _plugin );
    }

    /**
     * Returns all the slot containing an appointment for the date range
     * 
     * @param nIdForm
     *            the Form Id
     * @param startingDateTime
     *            the starting date
     * @param endingDateTime
     *            the ending date
     * @param plugin
     *            the plugin
     * @return a list of slots whose dates are included in the given period and is containing an appointment
     */
    public static List<Slot> findSlotWithAppointmentByDateRange( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime )
    {

        return _dao.findSlotWithAppointmentByDateRange( nIdForm, startingDateTime, endingDateTime, _plugin );

    }

    /**
     * Returns a list of specific slots for a form
     * 
     * @param nIdForm
     *            the Form Id
     * @return a list of slots found
     */
    public static List<Slot> findIsSpecificByIdForm( int nIdForm )
    {
        return _dao.findIsSpecificByIdForm( nIdForm, _plugin );
    }

    /**
     * Returns a list of slots of a form
     * 
     * @param nIdForm
     *            the form id
     * @return a list of all the slots of the form
     */
    public static List<Slot> findByIdForm( int nIdForm )
    {
        return _dao.findByIdForm( nIdForm, _plugin );
    }

    /**
     * Returns a list of open slots for a date range
     * 
     * @param nIdForm
     *            the Form Id
     * @param startingDateTime
     *            the starting Date
     * @param endingDateTime
     *            the ending Date
     * @return a list of open slots whose dates are included in the given period
     */
    public static List<Slot> findOpenSlotsByIdFormAndDateRange( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime )
    {
        return _dao.findOpenSlotsByIdFormAndDateRange( nIdForm, startingDateTime, endingDateTime, _plugin );
    }

    /**
     * Returns a list of open slots
     * 
     * @param nIdForm
     *            the Form Id
     * @return a list of open slots
     */
    public static List<Slot> findOpenSlotsByIdForm( int nIdForm )
    {
        return _dao.findOpenSlotsByIdForm( nIdForm, _plugin );
    }

    /**
     * Return the Slot that have the max date
     * 
     * @param nIdForm
     *            the form id
     * @return the slot
     */
    public static Slot findSlotWithTheMaxDate( int nIdForm )
    {
        return _dao.findSlotWithMaxDate( nIdForm, _plugin );
    }

    /**
     * Returns a list of slots of a form
     * 
     * @param nIdAppointment
     *            the appointment id
     * @return a list of all the slots of the form
     */
    public static List<Slot> findByIdAppointment( int nIdAppointment )
    {
        return _dao.findByIdAppointment( nIdAppointment, _plugin );
    }

    /**
     * Update Potential Remaining Places
     * 
     * @param nbPotentialRemainingPlaces
     * @param nIdSlot
     */
    public static void updatePotentialRemainingPlaces( int nbPotentialRemainingPlaces, int nIdSlot )
    {

        _dao.updatePotentialRemainingPlaces( nbPotentialRemainingPlaces, nIdSlot, _plugin );

    }

    /**
     * Reset Potential Remaining Places
     * 
     * @param nbPotentialRemainingPlaces
     * @param nIdSlot
     */
    public static void resetPotentialRemainingPlaces( )
    {

        _dao.resetPotentialRemainingPlaces( _plugin );

    }

}
