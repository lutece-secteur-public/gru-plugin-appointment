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
package fr.paris.lutece.plugins.appointment.business.calendar;

import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

/**
 * This class provides instances management methods for Calendar Template objects
 * 
 * @author Laurent Payen
 *
 */
public final class CalendarTemplateHome
{
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );
    private static ICalendarTemplateDAO _dao = SpringContextService.getBean( "appointment.calendarTemplateDAO" );

    /**
     * Default constructor
     */
    private CalendarTemplateHome( )
    {
        // Private constructor
    }

    /**
     * Create a new calendar template
     * 
     * @param template
     *            The template to create
     */
    public static void create( CalendarTemplate template )
    {
        _dao.insert( template, _plugin );
    }

    /**
     * Update an existing template
     * 
     * @param template
     *            The template
     */
    public static void update( CalendarTemplate template )
    {
        _dao.update( template, _plugin );
    }

    /**
     * Delete a template from its primary key
     * 
     * @param nId
     *            The id of the template to remove
     */
    public static void delete( int nId )
    {
        _dao.delete( nId, _plugin );
    }

    /**
     * Find a template by its primary key
     * 
     * @param nId
     *            The id of the template
     * @return The calendar template found, or null if no calendar template has the given primary key
     */
    public static CalendarTemplate findByPrimaryKey( int nId )
    {
        return _dao.select( nId, _plugin );
    }

    /**
     * Get the list of calendar templates
     * 
     * @return The list of calendar templates
     */
    public static List<CalendarTemplate> findAll( )
    {
        return _dao.selectAll( _plugin );
    }

    /**
     * Get the list of calendar templates in a reference list
     * 
     * @return The list of calendar templates in a reference list
     */
    public static ReferenceList findAllInReferenceList( )
    {
        List<CalendarTemplate> listCalendarTemplates = findAll( );
        ReferenceList refListTemplates = new ReferenceList( listCalendarTemplates.size( ) );
        for ( CalendarTemplate template : listCalendarTemplates )
        {
            refListTemplates.addItem( template.getIdCalendarTemplate( ), template.getTitle( ) );
        }
        return refListTemplates;
    }

}
