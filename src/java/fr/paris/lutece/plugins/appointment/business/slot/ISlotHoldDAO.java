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

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * SlotHold DAO Interface
 *
 * @author City of Paris
 */
public interface ISlotHoldDAO
{
    /**
     * Insert a new hold in the table
     *
     * @param slotHold
     *            the hold to insert
     * @param plugin
     *            the plugin
     */
    void insert( SlotHold slotHold, Plugin plugin );

    /**
     * Delete a hold by its slot id and token
     *
     * @param nIdSlot
     *            the slot id
     * @param strHoldToken
     *            the hold token
     * @param plugin
     *            the plugin
     */
    void delete( int nIdSlot, String strHoldToken, Plugin plugin );

    /**
     * Delete every hold of a token (a booking session may hold several slots)
     *
     * @param strHoldToken
     *            the hold token
     * @param plugin
     *            the plugin
     */
    void deleteByToken( String strHoldToken, Plugin plugin );

    /**
     * Select every hold of a token
     *
     * @param strHoldToken
     *            the hold token
     * @param plugin
     *            the plugin
     * @return the list of holds of the token
     */
    List<SlotHold> selectByToken( String strHoldToken, Plugin plugin );

    /**
     * Delete every hold of a slot (called when the slot itself is deleted)
     *
     * @param nIdSlot
     *            the slot id
     * @param plugin
     *            the plugin
     */
    void deleteByIdSlot( int nIdSlot, Plugin plugin );

    /**
     * Delete every hold of the slots of a form (called when the form is deleted)
     *
     * @param nIdForm
     *            the form id
     * @param plugin
     *            the plugin
     */
    void deleteByIdForm( int nIdForm, Plugin plugin );

    /**
     * Select every expired hold
     *
     * @param plugin
     *            the plugin
     * @return the list of expired holds
     */
    List<SlotHold> selectExpired( Plugin plugin );

    /**
     * Delete every expired hold
     *
     * @param plugin
     *            the plugin
     */
    void deleteExpired( Plugin plugin );

    /**
     * Tell whether an active hold exists for a slot under a given token.
     *
     * @param nIdSlot
     *            the slot id
     * @param strHoldToken
     *            the hold token
     * @param plugin
     *            the plugin
     * @return true if an active hold exists for that slot and token
     */
    boolean hasActiveHold( int nIdSlot, String strHoldToken, Plugin plugin );
}
