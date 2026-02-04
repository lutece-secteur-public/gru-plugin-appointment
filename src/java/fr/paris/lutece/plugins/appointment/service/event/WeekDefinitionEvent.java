/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.appointment.service.event;

import java.util.List;

import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;

/**
 * CDI event fired when a week definition is assigned, unassigned, or when the list of week definitions changes.
 */
public class WeekDefinitionEvent
{
    private final int _nIdForm;
    private final WeekDefinition _weekDefinition;
    private final List<WeekDefinition> _listWeekDefinition;

    /**
     * Constructor for assigned / unassigned events (CREATE / REMOVE)
     *
     * @param weekDefinition
     *            The week definition
     */
    public WeekDefinitionEvent( WeekDefinition weekDefinition )
    {
        _weekDefinition = weekDefinition;
        _nIdForm = 0;
        _listWeekDefinition = null;
    }

    /**
     * Constructor for list changed event (UPDATE)
     *
     * @param nIdForm
     *            The form id
     * @param listWeekDefinition
     *            The list of week definitions
     */
    public WeekDefinitionEvent( int nIdForm, List<WeekDefinition> listWeekDefinition )
    {
        _nIdForm = nIdForm;
        _listWeekDefinition = listWeekDefinition;
        _weekDefinition = null;
    }

    /**
     * @return the form id (set for UPDATE events)
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * @return the week definition (set for CREATE / REMOVE events)
     */
    public WeekDefinition getWeekDefinition( )
    {
        return _weekDefinition;
    }

    /**
     * @return the list of week definitions (set for UPDATE events)
     */
    public List<WeekDefinition> getListWeekDefinition( )
    {
        return _listWeekDefinition;
    }
}
