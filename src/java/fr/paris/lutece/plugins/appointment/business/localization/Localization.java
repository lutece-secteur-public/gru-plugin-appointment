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
package fr.paris.lutece.plugins.appointment.business.localization;

import java.io.Serializable;

/**
 * Business class of the Form Display
 * 
 * @author Laurent Payen
 *
 */
public final class Localization implements Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 611153651324369773L;

    /**
     * Localization Id
     */
    private int _nIdLocalization;

    /**
     * Longitude
     */
    private Double _dLongitude;

    /**
     * Latitude
     */
    private Double _dLatitude;

    /**
     * Address
     */
    private String _strAddress;

    /**
     * Form id (foreign key)
     */
    private int _nIdForm;

    /**
     * Get the Id Localization
     * 
     * @return the id of the localization
     */
    public int getIdLocalization( )
    {
        return _nIdLocalization;
    }

    /**
     * Set the id of the localization
     * 
     * @param nIdLocalization
     *            the of the localization
     */
    public void setIdLocalization( int nIdLocalization )
    {
        this._nIdLocalization = nIdLocalization;
    }

    /**
     * Get the longitude
     * 
     * @return the longitude
     */
    public Double getLongitude( )
    {
        return _dLongitude;
    }

    /**
     * Set the longitude
     * 
     * @param dLongitude
     *            the longitude
     */
    public void setLongitude( Double dLongitude )
    {
        this._dLongitude = dLongitude;
    }

    /**
     * Get the latitude
     * 
     * @return the latitude
     */
    public Double getLatitude( )
    {
        return _dLatitude;
    }

    /**
     * Set the latitude
     * 
     * @param dLatitude
     *            the latitude
     */
    public void setLatitude( Double dLatitude )
    {
        this._dLatitude = dLatitude;
    }

    /**
     * Get the address of the form
     * 
     * @return the address
     */
    public String getAddress( )
    {
        return _strAddress;
    }

    /**
     * Set the address of the form
     * 
     * @param strAddress
     */
    public void setAddress( String strAddress )
    {
        this._strAddress = strAddress;
    }

    /**
     * Get the Form Id
     * 
     * @return the Form Id
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the Form Id
     * 
     * @param nIdForm
     *            the Form Id to set
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

}
