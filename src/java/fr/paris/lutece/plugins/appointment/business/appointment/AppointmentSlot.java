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
package fr.paris.lutece.plugins.appointment.business.appointment;

import java.io.Serializable;

public class AppointmentSlot implements Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 2706298728160930488L;
    // Variables declarations
    private int _nIdAppointment;
    private int _nIdSlot;
    private int _nNbPlaces;

    /**
     * Returns the IdAppointment
     * 
     * @return The IdAppointment
     */
    public int getIdAppointment( )
    {
        return _nIdAppointment;
    }

    /**
     * Sets the IdAppointment
     * 
     * @param nIdAppointment
     *            The IdAppointment
     */
    public void setIdAppointment( int nIdAppointment )
    {
        _nIdAppointment = nIdAppointment;
    }

    /**
     * Returns the IdSlot
     * 
     * @return The IdSlot
     */
    public int getIdSlot( )
    {
        return _nIdSlot;
    }

    /**
     * Sets the IdSlot
     * 
     * @param nIdSlot
     *            The IdSlot
     */
    public void setIdSlot( int nIdSlot )
    {
        _nIdSlot = nIdSlot;
    }

    /**
     * Returns the NbPlaces
     * 
     * @return The NbPlaces
     */
    public int getNbPlaces( )
    {
        return _nNbPlaces;
    }

    /**
     * Sets the NbPlaces
     * 
     * @param nNbPlaces
     *            The NbPlaces
     */
    public void setNbPlaces( int nNbPlaces )
    {
        _nNbPlaces = nNbPlaces;
    }
}
