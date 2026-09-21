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

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Soft-hold of places on a slot during a booking in progress.
 *
 * @author City of Paris
 */
public class SlotHold implements Serializable
{
    private static final long serialVersionUID = 1L;

    private int _nIdSlot;
    private String _strHoldToken;
    private int _nNbPlaces;
    private LocalDateTime _expiredDateTime;

    /**
     * Get the slot id
     *
     * @return the slot id
     */
    public int getIdSlot( )
    {
        return _nIdSlot;
    }

    /**
     * Set the slot id
     *
     * @param nIdSlot
     *            the slot id
     */
    public void setIdSlot( int nIdSlot )
    {
        _nIdSlot = nIdSlot;
    }

    /**
     * Get the hold token (unique per booking session)
     *
     * @return the hold token
     */
    public String getHoldToken( )
    {
        return _strHoldToken;
    }

    /**
     * Set the hold token
     *
     * @param strHoldToken
     *            the hold token
     */
    public void setHoldToken( String strHoldToken )
    {
        _strHoldToken = strHoldToken;
    }

    /**
     * Get the number of places held
     *
     * @return the number of places held
     */
    public int getNbPlaces( )
    {
        return _nNbPlaces;
    }

    /**
     * Set the number of places held
     *
     * @param nNbPlaces
     *            the number of places held
     */
    public void setNbPlaces( int nNbPlaces )
    {
        _nNbPlaces = nNbPlaces;
    }

    /**
     * Get the expiry date time of the hold
     *
     * @return the expiry date time
     */
    public LocalDateTime getExpiredDateTime( )
    {
        return _expiredDateTime;
    }

    /**
     * Set the expiry date time of the hold
     *
     * @param expiredDateTime
     *            the expiry date time
     */
    public void setExpiredDateTime( LocalDateTime expiredDateTime )
    {
        _expiredDateTime = expiredDateTime;
    }
}
