/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.appointment.business.template;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * DAO for calendar templates
 */
public class CalendarTemplateDAO implements ICalendarTemplateDAO
{
    private static final String SQL_QUERY_NEW_PRIMARY_KEY = "SELECT max(id) FROM appointment_calendar_template";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_calendar_template (id, title, description, template_path) VALUES (?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_calendar_template SET title = ?, description = ?, template_path = ? WHERE id = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_calendar_template WHERE id = ?";
    private static final String SQL_QUERY_FIND_ALL = "SELECT id, title, description, template_path FROM appointment_calendar_template";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = SQL_QUERY_FIND_ALL + " WHERE id = ?";

    /**
     * Get a new primary key
     * @param plugin The plugin
     * @return The new value of the primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PRIMARY_KEY, plugin );
        daoUtil.executeQuery(  );

        int nRes = 1;

        if ( daoUtil.next(  ) )
        {
            nRes = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free(  );

        return nRes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void create( CalendarTemplate template, Plugin plugin )
    {
        template.setId( newPrimaryKey( plugin ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, template.getId(  ) );
        daoUtil.setString( nIndex++, template.getTitle(  ) );
        daoUtil.setString( nIndex++, template.getDescription(  ) );
        daoUtil.setString( nIndex, template.getTemplatePath(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update( CalendarTemplate template, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;

        daoUtil.setString( nIndex++, template.getTitle(  ) );
        daoUtil.setString( nIndex++, template.getDescription(  ) );
        daoUtil.setString( nIndex++, template.getTemplatePath(  ) );
        daoUtil.setInt( nIndex++, template.getId(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalendarTemplate findByPrimaryKey( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        CalendarTemplate template = null;

        if ( daoUtil.next(  ) )
        {
            template = getCalendarTemplate( daoUtil );
        }

        daoUtil.free(  );

        return template;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CalendarTemplate> findAll( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_ALL, plugin );
        daoUtil.executeQuery(  );

        List<CalendarTemplate> listTemplates = new ArrayList<CalendarTemplate>(  );

        while ( daoUtil.next(  ) )
        {
            listTemplates.add( getCalendarTemplate( daoUtil ) );
        }

        daoUtil.free(  );

        return listTemplates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Get a calendar template from a DAOUtil
     * @param daoUtil the daoUtil to read data from. The call to the
     *            daoUtil.next( ) must have been made before this method is
     *            called. This method will NOT call the daoUtil.free( ) method.
     * @return The calendar template
     */
    private CalendarTemplate getCalendarTemplate( DAOUtil daoUtil )
    {
        int nIndex = 1;
        CalendarTemplate template = new CalendarTemplate(  );
        template.setId( daoUtil.getInt( nIndex++ ) );
        template.setTitle( daoUtil.getString( nIndex++ ) );
        template.setDescription( daoUtil.getString( nIndex++ ) );
        template.setTemplatePath( daoUtil.getString( nIndex ) );

        return template;
    }
}
