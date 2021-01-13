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
import java.sql.Date;
import java.time.LocalDate;

/**
 * Business class of the closing day
 * 
 * @author Laurent Payen
 *
 */
public final class ClosingDay implements Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -7399500588272139256L;

    /**
     * Id of the closing day
     */
    private int _nIdClosingDay;

    /**
     * Date of the closing day
     */
    private LocalDate _dateOfClosingDay;

    /**
     * Id of the form the closing day belongs to
     */
    private int _nIdForm;

    /**
     * Get the id of the closing day
     * 
     * @return the id of the closing day
     */
    public int getIdClosingDay( )
    {
        return _nIdClosingDay;
    }

    /**
     * Set the id of the closing day
     * 
     * @param nIdClosingDay
     *            the id to set
     */
    public void setIdClosingDay( int nIdClosingDay )
    {
        this._nIdClosingDay = nIdClosingDay;
    }

    /**
     * Get the date of the closing day
     * 
     * @return the date of the closing day
     */
    public LocalDate getDateOfClosingDay( )
    {
        return _dateOfClosingDay;
    }

    /**
     * Get the date of the closing day (in sql date format)
     * 
     * @return the date of the closing day
     */
    public Date getSqlDateOfClosingDay( )
    {
        Date date = null;
        if ( _dateOfClosingDay != null )
        {
            date = Date.valueOf( _dateOfClosingDay );
        }
        return date;
    }

    /**
     * Set the date of the closing day
     * 
     * @param dateOfClosingDay
     *            the date to set
     */
    public void setDateOfClosingDay( LocalDate dateOfClosingDay )
    {
        this._dateOfClosingDay = dateOfClosingDay;
    }

    /**
     * Set the date of the closing day
     * 
     * @param dateOfClosingDay
     *            the date to set (in sql date format)
     */
    public void setSqlDateOfClosingDay( Date dateOfClosingDay )
    {
        if ( dateOfClosingDay != null )
        {
            this._dateOfClosingDay = dateOfClosingDay.toLocalDate( );
        }
        else
        {
            this._dateOfClosingDay = null;
        }
    }

    /**
     * Get the id of the form the closing day belongs to
     * 
     * @return the id of the form the closing day belongs to
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the form the closing day belongs to
     * 
     * @param nIdForm
     *            the if form to set
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

}
