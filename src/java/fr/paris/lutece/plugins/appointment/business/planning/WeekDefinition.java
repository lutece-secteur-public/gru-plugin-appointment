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
import java.util.List;

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
     * Id of the form the week definition belongs to
     */
    private int _nIdForm;

    /**
     * List of the working days that define the week definition
     */
    private List<WorkingDay> _listWorkingDays;

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
     * @param nIdWeekDefinition
     *            the id to set
     */
    public void setIdWeekDefinition( int nIdWeekDefinition )
    {
        this._nIdWeekDefinition = nIdWeekDefinition;
    }

    /**
     * Get the form id the week definition belongs to
     * 
     * @return the form id
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the form id the week definition belongs to
     * 
     * @param nIdForm
     *            the form id to set
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

    /**
     * Get the list of the working days of the week
     * 
     * @return the list of the working days for the week
     */
    public List<WorkingDay> getListWorkingDay( )
    {
        return _listWorkingDays;
    }

    /**
     * Set the working days for the week
     * 
     * @param listWorkingDays
     *            the list o f working days to set
     */
    public void setListWorkingDay( List<WorkingDay> listWorkingDays )
    {
        this._listWorkingDays = listWorkingDays;
    }

}
