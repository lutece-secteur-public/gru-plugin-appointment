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

import java.io.Serializable;

import fr.paris.lutece.plugins.appointment.business.AbstractDateConversion;

/**
 * Business class of the definition week
 * 
 * @author Laurent Payen
 *
 */
public final class WeekDefinition extends AbstractDateConversion implements Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 4292654762871322318L;

    /**
     * Id of the week definition
     */
    private int _nIdWeekDefinition;
    /**
     * Id of the reservation rule.
     */
    private int _nIdReservationRule;

    /**
     * Get the id of the week definition
     * 
     * @return the id of the week definition
     */
    public int getIdWeekDefinition( )
    {
        return _nIdWeekDefinition;
    }

    /**
     * Set the id of the week definition
     * 
     * @param _nIdWeekDefinition
     *            the id to set
     */
    public void setIdWeekDefinition( int nIdWeekDefinition )
    {
        this._nIdWeekDefinition = nIdWeekDefinition;
    }

    /**
     * Get the id of the rule of the reservation
     * 
     * @return the id of the rule of the reservation
     */
    public int getIdReservationRule( )
    {
        return _nIdReservationRule;
    }

    /**
     * Set the id of the rule of the reservation
     * 
     * @param nIdReservationRule
     *            the id to set
     */
    public void setIdReservationRule( int nIdReservationRule )
    {
        this._nIdReservationRule = nIdReservationRule;
    }

}
