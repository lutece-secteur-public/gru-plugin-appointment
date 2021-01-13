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

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Calendar Template objects
 * 
 * @author Laurent Payen
 *
 */
public final class CalendarTemplateDAO implements ICalendarTemplateDAO
{
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_calendar_template ( title, description, template_path) VALUES (?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_calendar_template SET title = ?, description = ?, template_path = ? WHERE id_calendar_template = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_calendar_template WHERE id_calendar_template = ?";
    private static final String SQL_QUERY_SELECT = "SELECT id_calendar_template, title, description, template_path FROM appointment_calendar_template WHERE id_calendar_template = ?";
    private static final String SQL_QUERY_SELECT_ALL = "SELECT id_calendar_template, title, description, template_path FROM appointment_calendar_template";

    @Override
    public void insert( CalendarTemplate calendarTemplate, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, calendarTemplate, plugin, true ) )
        {
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                calendarTemplate.setIdCalendarTemplate( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    @Override
    public void update( CalendarTemplate calendarTemplate, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, calendarTemplate, plugin, false ) )
        {
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void delete( int nIdCalendarTemplate, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdCalendarTemplate );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public CalendarTemplate select( int nIdCalendarTemplate, Plugin plugin )
    {
        CalendarTemplate calendarTemplate = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nIdCalendarTemplate );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                calendarTemplate = buildCalendarTemplate( daoUtil );
            }
        }
        return calendarTemplate;
    }

    @Override
    public List<CalendarTemplate> selectAll( Plugin plugin )
    {
        List<CalendarTemplate> listTemplates = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin ) )
        {
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listTemplates.add( buildCalendarTemplate( daoUtil ) );
            }
        }
        return listTemplates;
    }

    /**
     * Build a Calendar Template business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new Calendar Template with all its attributes assigned
     */
    private CalendarTemplate buildCalendarTemplate( DAOUtil daoUtil )
    {
        int nIndex = 1;
        CalendarTemplate calendarTemplate = new CalendarTemplate( );
        calendarTemplate.setIdCalendarTemplate( daoUtil.getInt( nIndex++ ) );
        calendarTemplate.setTitle( daoUtil.getString( nIndex++ ) );
        calendarTemplate.setDescription( daoUtil.getString( nIndex++ ) );
        calendarTemplate.setTemplatePath( daoUtil.getString( nIndex ) );
        return calendarTemplate;
    }

    /**
     * Build a daoUtil object with the Calendar Template business object
     * 
     * @param query
     *            the query
     * @param calendarTemplate
     *            the CalendarTemplate
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, CalendarTemplate calendarTemplate, Plugin plugin, boolean isInsert )
    {
        int nIndex = 1;
        DAOUtil daoUtil = null;
        if ( isInsert )
        {
            daoUtil = new DAOUtil( query, Statement.RETURN_GENERATED_KEYS, plugin );
        }
        else
        {
            daoUtil = new DAOUtil( query, plugin );
        }

        daoUtil.setString( nIndex++, calendarTemplate.getTitle( ) );
        daoUtil.setString( nIndex++, calendarTemplate.getDescription( ) );
        daoUtil.setString( nIndex++, calendarTemplate.getTemplatePath( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, calendarTemplate.getIdCalendarTemplate( ) );
        }
        return daoUtil;
    }
}
