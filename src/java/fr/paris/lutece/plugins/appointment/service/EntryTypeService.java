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
package fr.paris.lutece.plugins.appointment.service;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.EntryTypeHome;
import fr.paris.lutece.util.ReferenceList;

/**
 * EntryTypeService
 * 
 * @author Laurent Payen
 *
 */
public final class EntryTypeService
{
    private static EntryTypeService _instance = new EntryTypeService( );

    /**
     * Private constructor
     */
    private EntryTypeService( )
    {
    }

    /**
     * Get the instance of the service
     * 
     * @return The instance of the service
     */
    public static EntryTypeService getInstance( )
    {
        return _instance;
    }

    /**
     * Get the entry type given the class name
     * 
     * @param nId
     *            the id of the entry type
     * @return an {@link EntryType}
     */
    public EntryType getEntryType( int nId )
    {
        return EntryTypeHome.findByPrimaryKey( nId );
    }

    /**
     * Get a reference list containing entry types
     * 
     * @return A reference list containing entry types
     */
    public ReferenceList getEntryTypeReferenceList( )
    {
        ReferenceList refListEntryType = new ReferenceList( );

        for ( EntryType entryType : EntryTypeHome.getList( AppointmentPlugin.PLUGIN_NAME ) )
        {
            if ( !entryType.isInactive( ) )
            {
                refListEntryType.addItem( entryType.getIdType( ), entryType.getTitle( ) );
            }
        }
        return refListEntryType;
    }

    /**
     * Get a list containing the active EntryType elements
     * 
     * @return a List of currently active EntryTypes
     */
    public List<EntryType> getListActiveEntryType( )
    {
        List<EntryType> listAvailableEntryType = new ArrayList<>( );

        for ( EntryType entryType : EntryTypeHome.getList( AppointmentPlugin.PLUGIN_NAME ) )
        {
            if ( !entryType.isInactive( ) )
            {
                listAvailableEntryType.add( entryType );
            }
        }
        return listAvailableEntryType;
    }
}
