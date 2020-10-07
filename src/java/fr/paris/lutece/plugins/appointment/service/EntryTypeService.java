/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

import java.util.HashMap;
import java.util.Map;

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
    private static volatile EntryTypeService _instance;
    private Map<Integer, EntryType> _mapEntryTypes;

    /**
     * Private constructor
     */
    private EntryTypeService( )
    {
        initMapEntryTypes( );
    }

    /**
     * Get the instance of the service
     * 
     * @return The instance of the service
     */
    public static EntryTypeService getInstance( )
    {
        if ( _instance == null )
        {
            _instance = new EntryTypeService( );
        }

        return _instance;
    }

    /**
     * Get the map of entry types
     * 
     * @return the map of entry types
     */
    public Map<Integer, EntryType> getMapEntryTypes( )
    {
        return new HashMap<Integer, EntryType>( _mapEntryTypes );
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
        return _mapEntryTypes.get( nId );
    }

    /**
     * Init the map of entry types
     */
    private void initMapEntryTypes( )
    {
        _mapEntryTypes = new HashMap<Integer, EntryType>( );

        for ( EntryType entryType : EntryTypeHome.getList( AppointmentPlugin.PLUGIN_NAME ) )
        {
            _mapEntryTypes.put( entryType.getIdType( ), entryType );
        }
    }

    /**
     * Get a reference list containing entry types. This is used to display EntryTypes in the UI. Since we need to
     * be able to do this in different languages, we use the bean name instead of the title. The bean name is used to
     * reference a property in the resource bundle. For example, the bean name "appointment.entryTypeComment", when
     * internationalized in a template, will access the property entryTypeComment in the resource bundle.
     * 
     * @return A reference list containing entry types
     */
    public ReferenceList getEntryTypeReferenceList( )
    {
        ReferenceList refListEntryType = new ReferenceList( );

        for ( EntryType entryType : _mapEntryTypes.values( ) )
        {
            refListEntryType.addItem( entryType.getIdType( ), entryType.getBeanName( ) );
        }

        return refListEntryType;
    }
}
