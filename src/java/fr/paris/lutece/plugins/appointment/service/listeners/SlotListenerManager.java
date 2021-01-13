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
package fr.paris.lutece.plugins.appointment.service.listeners;

import fr.paris.lutece.portal.service.spring.SpringContextService;

public final class SlotListenerManager
{

    /**
     * Private default constructor
     */
    private SlotListenerManager( )
    {
        // Nothing to do
    }

    /**
     * Notify listeners that a Slot has been created
     * 
     * @param nIdSlot
     *            The id of the Slot that has been created
     */
    public static void notifyListenersSlotCreation( int nIdSlot )
    {
        new Thread( ( ) -> {
            for ( ISlotListener slotListener : SpringContextService.getBeansOfType( ISlotListener.class ) )
            {
                slotListener.notifySlotCreation( nIdSlot );
            }
        } ).start( );

    }

    /**
     * Notify listeners that a Slot has been changed
     * 
     * @param nIdSlot
     *            The id of the Slot that has been changed
     */
    public static void notifyListenersSlotChange( int nIdSlot )
    {
        new Thread( ( ) -> {
            for ( ISlotListener slotListener : SpringContextService.getBeansOfType( ISlotListener.class ) )
            {
                slotListener.notifySlotChange( nIdSlot );
            }
        } ).start( );
    }

    /**
     * Notify listeners that a Slot is about to be removed
     * 
     * @param nIdSlot
     *            The id of the Slot that will be removed
     */
    public static void notifyListenersSlotRemoval( int nIdSlot )
    {
        new Thread( ( ) -> {
            for ( ISlotListener slotListener : SpringContextService.getBeansOfType( ISlotListener.class ) )
            {
                slotListener.notifySlotRemoval( nIdSlot );
            }
        } ).start( );
    }

}
