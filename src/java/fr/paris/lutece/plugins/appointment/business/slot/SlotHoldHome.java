/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import jakarta.enterprise.inject.spi.CDI;

/**
 * This class provides instances management methods for SlotHold objects
 *
 * @author City of Paris
 */
public final class SlotHoldHome
{
    private static ISlotHoldDAO _dao = CDI.current( ).select( ISlotHoldDAO.class ).get( );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private SlotHoldHome( )
    {
    }

    /**
     * Create a hold
     *
     * @param slotHold
     *            the hold to create
     * @return the created hold
     */
    public static SlotHold create( SlotHold slotHold )
    {
        _dao.insert( slotHold, _plugin );
        return slotHold;
    }

    /**
     * Delete a hold by slot id and token
     *
     * @param nIdSlot
     *            the slot id
     * @param strHoldToken
     *            the hold token
     */
    public static void delete( int nIdSlot, String strHoldToken )
    {
        _dao.delete( nIdSlot, strHoldToken, _plugin );
    }

    /**
     * Delete every hold of a token
     *
     * @param strHoldToken
     *            the hold token
     */
    public static void deleteByToken( String strHoldToken )
    {
        _dao.deleteByToken( strHoldToken, _plugin );
    }

    /**
     * Get every hold of a token
     *
     * @param strHoldToken
     *            the hold token
     * @return the list of holds of the token
     */
    public static List<SlotHold> findByToken( String strHoldToken )
    {
        return _dao.selectByToken( strHoldToken, _plugin );
    }

    /**
     * Delete every hold of a slot (called when the slot is deleted)
     *
     * @param nIdSlot
     *            the slot id
     */
    public static void deleteByIdSlot( int nIdSlot )
    {
        _dao.deleteByIdSlot( nIdSlot, _plugin );
    }

    /**
     * Delete every hold of the slots of a form (called when the form is deleted)
     *
     * @param nIdForm
     *            the form id
     */
    public static void deleteByIdForm( int nIdForm )
    {
        _dao.deleteByIdForm( nIdForm, _plugin );
    }

    /**
     * Get every expired hold
     *
     * @return the list of expired holds
     */
    public static List<SlotHold> findExpired( )
    {
        return _dao.selectExpired( _plugin );
    }

    /**
     * Delete every expired hold
     */
    public static void deleteExpired( )
    {
        _dao.deleteExpired( _plugin );
    }

    /**
     * Tell whether an active hold exists for a slot under a given token.
     *
     * @param nIdSlot
     *            the slot id
     * @param strHoldToken
     *            the hold token
     * @return true if an active hold exists for that slot and token
     */
    public static boolean hasActiveHold( int nIdSlot, String strHoldToken )
    {
        return _dao.hasActiveHold( nIdSlot, strHoldToken, _plugin );
    }
}
