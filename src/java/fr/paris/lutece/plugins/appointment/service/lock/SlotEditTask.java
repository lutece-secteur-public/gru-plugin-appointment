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
package fr.paris.lutece.plugins.appointment.service.lock;

import java.io.Serializable;
import java.util.TimerTask;

import fr.paris.lutece.plugins.appointment.service.SlotSafeService;

/**
 * Timer Task for a slot (Manage a lock the time the user fill the form
 * 
 * @author Laurent Payen
 *
 */
public final class SlotEditTask extends TimerTask implements Serializable
{

    /**
     * UID
     */
    private static final long serialVersionUID = 2397343851302139337L;

    /**
     * Potentially number of places taken
     */
    private int _nbPlacesTaken;

    /**
     * Id of the slot on which the user is taking an appointment
     */
    private int _idSlot;
    /**
     * Is Cancelled
     */
    private boolean _bIsCancelled;

    public SlotEditTask( )
    {
        super( );
    }

    @Override
    public void run( )
    {
        SlotSafeService.incrementPotentialRemainingPlaces( _nbPlacesTaken, _idSlot, _bIsCancelled );
        this.cancel( );
        _bIsCancelled = true;

    }

    /**
     * Get the number of places potentially taken
     * 
     * @return the number of places
     */
    public int getNbPlacesTaken( )
    {
        return _nbPlacesTaken;
    }

    /**
     * Set the number of places potentially taken
     * 
     * @param nbPlacesTaken
     */
    public void setNbPlacesTaken( int nbPlacesTaken )
    {
        this._nbPlacesTaken = nbPlacesTaken;
    }

    /**
     * Get the id of the slot
     * 
     * @return the id of the slot
     */
    public int getIdSlot( )
    {
        return _idSlot;
    }

    /**
     * Set the id of the slot
     * 
     * @param nIdSlot
     *            the id of the slot
     */
    public void setIdSlot( int nIdSlot )
    {
        this._idSlot = nIdSlot;
    }

    /**
     * If the task is cancelled
     * 
     * @return true if task is cancelled
     */
    public boolean isCancelled( )
    {

        return _bIsCancelled;

    }

    /**
     * set If the task is cancelled
     * 
     * @param isCancelled
     */
    public void setIsCancelled( boolean isCancelled )
    {

        _bIsCancelled = isCancelled;
    }

}
