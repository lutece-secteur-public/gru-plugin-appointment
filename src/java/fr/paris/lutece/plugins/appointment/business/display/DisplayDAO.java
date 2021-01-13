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
package fr.paris.lutece.plugins.appointment.business.display;

import java.sql.Statement;

import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Display objects
 * 
 * @author Laurent Payen
 *
 */
public final class DisplayDAO implements IDisplayDAO
{

    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_display ( display_title_fo, icon_form_content, icon_form_mime_type, nb_weeks_to_display, is_displayed_on_portlet, id_calendar_template, id_form) VALUES ( ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_display SET display_title_fo = ?, icon_form_content = ?, icon_form_mime_type = ?, nb_weeks_to_display = ?, is_displayed_on_portlet = ?, id_calendar_template = ?, id_form = ? WHERE id_display = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_display WHERE id_display = ?";
    private static final String SQL_QUERY_DELETE_BY_ID_FORM = "DELETE FROM appointment_display WHERE id_form = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_display, display_title_fo, icon_form_content, icon_form_mime_type, nb_weeks_to_display, is_displayed_on_portlet, id_calendar_template, id_form FROM appointment_display";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_display = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT_COLUMNS + " WHERE id_form = ?";

    @Override
    public void insert( Display display, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, display, plugin, true ) )
        {
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                display.setIdDisplay( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    @Override
    public void update( Display display, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, display, plugin, false ) )
        {
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void delete( int nIdDisplay, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdDisplay );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void deleteByIdForm( int nIdForm, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public Display select( int nIdDisplay, Plugin plugin )
    {
        Display display = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nIdDisplay );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                display = buildDisplay( daoUtil );
            }
        }
        return display;
    }

    @Override
    public Display findByIdForm( int nIdForm, Plugin plugin )
    {
        Display display = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                display = buildDisplay( daoUtil );
            }
        }
        return display;
    }

    /**
     * Build a Display business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new display business object with all its attributes assigned
     */
    private Display buildDisplay( DAOUtil daoUtil )
    {
        int nIndex = 1;
        Display display = new Display( );
        display.setIdDisplay( daoUtil.getInt( nIndex++ ) );
        display.setDisplayTitleFo( daoUtil.getBoolean( nIndex++ ) );
        display.setIcon( buildIcon( daoUtil.getBytes( nIndex++ ), daoUtil.getString( nIndex++ ) ) );
        display.setNbWeeksToDisplay( daoUtil.getInt( nIndex++ ) );
        display.setIsDisplayedOnPortlet( daoUtil.getBoolean( nIndex++ ) );
        display.setIdCalendarTemplate( daoUtil.getInt( nIndex++ ) );
        display.setIdForm( daoUtil.getInt( nIndex ) );
        return display;
    }

    /**
     * Build a daoUtil object with the display business object for insert query
     * 
     * @param query
     *            the query
     * @param display
     *            the display
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, Display display, Plugin plugin, boolean isInsert )
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
        daoUtil.setBoolean( nIndex++, display.isDisplayTitleFo( ) );
        daoUtil.setBytes( nIndex++, display.getIcon( ).getImage( ) );
        daoUtil.setString( nIndex++, display.getIcon( ).getMimeType( ) );
        daoUtil.setInt( nIndex++, display.getNbWeeksToDisplay( ) );
        daoUtil.setBoolean( nIndex++, display.isDisplayedOnPortlet( ) );
        daoUtil.setInt( nIndex++, display.getIdCalendarTemplate( ) );
        daoUtil.setInt( nIndex++, display.getIdForm( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, display.getIdDisplay( ) );
        }
        return daoUtil;
    }

    /**
     * Build an icon (imageResource)
     * 
     * @param strImage
     *            the icon form content
     * @param strMimeType
     *            the icon form mime type
     * @return the icon (imageResource) built
     */
    private ImageResource buildIcon( byte [ ] strImage, String strMimeType )
    {
        ImageResource imageResource = new ImageResource( );
        imageResource.setImage( strImage );
        imageResource.setMimeType( strMimeType );
        return imageResource;
    }

}
