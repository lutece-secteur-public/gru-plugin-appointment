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

import java.sql.Date;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Closing Day objects
 * 
 * @author Laurent Payen
 *
 */
public final class ClosingDayDAO implements IClosingDayDAO
{

    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_closing_day ( date_of_closing_day, id_form) VALUES (?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_closing_day SET date_of_closing_day = ?, id_form = ? WHERE id_closing_day = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_closing_day WHERE id_closing_day = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_closing_day, date_of_closing_day, id_form FROM appointment_closing_day";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_closing_day = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT_COLUMNS + " WHERE id_form = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_OF_CLOSING_DAY = SQL_QUERY_SELECT_BY_ID_FORM + " AND date_of_closing_day = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_RANGE = SQL_QUERY_SELECT_BY_ID_FORM
            + " AND date_of_closing_day >= ? AND date_of_closing_day <= ?";

    @Override
    public void insert( ClosingDay closingDay, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, closingDay, plugin, true ) )
        {
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                closingDay.setIdClosingDay( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    @Override
    public void update( ClosingDay closingDay, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, closingDay, plugin, false ) )
        {
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void delete( int nIdClosingDay, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdClosingDay );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public ClosingDay select( int nIdClosingDay, Plugin plugin )
    {
        ClosingDay closingDay = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nIdClosingDay );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                closingDay = buildClosingDay( daoUtil );
            }
        }
        return closingDay;
    }

    @Override
    public ClosingDay findByIdFormAndDateOfClosingDay( int nIdForm, LocalDate dateOfCLosingDay, Plugin plugin )
    {
        ClosingDay closingDay = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_OF_CLOSING_DAY, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.setDate( 2, Date.valueOf( dateOfCLosingDay ) );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                closingDay = buildClosingDay( daoUtil );
            }
        }
        return closingDay;
    }

    @Override
    public List<ClosingDay> findByIdForm( int nIdForm, Plugin plugin )
    {
        List<ClosingDay> listClosingDay = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listClosingDay.add( buildClosingDay( daoUtil ) );
            }
        }
        return listClosingDay;
    }

    @Override
    public List<ClosingDay> findByIdFormAndDateRange( int nIdForm, LocalDate startingDate, LocalDate endingDate, Plugin plugin )
    {
        List<ClosingDay> listClosingDay = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_RANGE, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.setDate( 2, Date.valueOf( startingDate ) );
            daoUtil.setDate( 3, Date.valueOf( endingDate ) );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listClosingDay.add( buildClosingDay( daoUtil ) );
            }
        }
        return listClosingDay;
    }

    /**
     * Build a Closing Day business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new Closing Day with all its attributes assigned
     */
    private ClosingDay buildClosingDay( DAOUtil daoUtil )
    {
        int nIndex = 1;
        ClosingDay closingDay = new ClosingDay( );
        closingDay.setIdClosingDay( daoUtil.getInt( nIndex++ ) );
        closingDay.setSqlDateOfClosingDay( daoUtil.getDate( nIndex++ ) );
        closingDay.setIdForm( daoUtil.getInt( nIndex ) );
        return closingDay;
    }

    /**
     * Build a daoUtil object with the CLosingDay business object
     * 
     * @param query
     *            the query
     * @param closingDay
     *            the closingDay
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, ClosingDay closingDay, Plugin plugin, boolean isInsert )
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
        daoUtil.setDate( nIndex++, closingDay.getSqlDateOfClosingDay( ) );
        daoUtil.setInt( nIndex++, closingDay.getIdForm( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, closingDay.getIdClosingDay( ) );
        }
        return daoUtil;
    }
}
