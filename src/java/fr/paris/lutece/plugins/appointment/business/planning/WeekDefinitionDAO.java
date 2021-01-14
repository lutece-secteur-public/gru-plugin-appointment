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
 * This class provides Data Access methods for Week Definition objects
 * 
 * @author Laurent Payen
 *
 */
public final class WeekDefinitionDAO implements IWeekDefinitionDAO
{

    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_week_definition ( date_of_apply, ending_date_of_apply, id_reservation_rule) VALUES ( ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_week_definition SET date_of_apply = ?, ending_date_of_apply = ?,  id_reservation_rule = ? WHERE id_week_definition = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_week_definition WHERE id_week_definition = ?";
    private static final String SQL_QUERY_DELETE_BY_ID_RESERVATION_RULE = "DELETE FROM appointment_week_definition WHERE id_reservation_rule = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_week_definition, date_of_apply, ending_date_of_apply, id_reservation_rule FROM appointment_week_definition ";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_week_definition = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = " SELECT appw.id_week_definition, appw.date_of_apply, appw.ending_date_of_apply, appw.id_reservation_rule FROM appointment_week_definition appw INNER JOIN appointment_reservation_rule rule on ( rule.id_reservation_rule = appw.id_reservation_rule ) where rule.id_form = ? ";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_OF_APPLY = SQL_QUERY_SELECT_BY_ID_FORM + " AND appw.date_of_apply = ? ";
    private static final String SQL_QUERY_SELECT_BY_ID_RESERVATION_RULE_AND_DATE_OF_APPLY = SQL_QUERY_SELECT_COLUMNS
            + " where id_reservation_rule = ? AND date_of_apply = ? ";
    private static final String SQL_SELECT_BY_RULE = SQL_QUERY_SELECT_COLUMNS + " WHERE id_reservation_rule = ?";

    @Override
    public void insert( WeekDefinition weekDefinition, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, weekDefinition, plugin, true ) )
        {
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                weekDefinition.setIdWeekDefinition( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    @Override
    public void update( WeekDefinition weekDefinition, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, weekDefinition, plugin, false ) )
        {
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void delete( int nIdWeekDefinition, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdWeekDefinition );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void deleteByIdReservationRule( int nIdReservationRule, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_RESERVATION_RULE, plugin ) )
        {
            daoUtil.setInt( 1, nIdReservationRule );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public WeekDefinition select( int nIdWeekDefinition, Plugin plugin )
    {
        WeekDefinition weekDefinition = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nIdWeekDefinition );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                weekDefinition = buildWeekDefinition( daoUtil );
            }
        }
        return weekDefinition;
    }

    @Override
    public List<WeekDefinition> findByIdForm( int nIdForm, Plugin plugin )
    {
        List<WeekDefinition> listWeekDefinition = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listWeekDefinition.add( buildWeekDefinition( daoUtil ) );
            }
        }
        return listWeekDefinition;
    }

    @Override
    public List<WeekDefinition> findByReservationRule( int nIdReservationRule, Plugin plugin )
    {
        List<WeekDefinition> listWeekDefinition = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_SELECT_BY_RULE, plugin ) )
        {
            daoUtil.setInt( 1, nIdReservationRule );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listWeekDefinition.add( buildWeekDefinition( daoUtil ) );
            }
        }
        return listWeekDefinition;
    }

    @Override
    public WeekDefinition findByIdFormAndDateOfApply( int nIdForm, LocalDate dateOfApply, Plugin plugin )
    {
        WeekDefinition weekDefinition = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_OF_APPLY, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.setDate( 2, Date.valueOf( dateOfApply ) );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                weekDefinition = buildWeekDefinition( daoUtil );
            }
        }
        return weekDefinition;
    }

    @Override
    public WeekDefinition findByIdReservationRuleAndDateOfApply( int nIdReservationRule, LocalDate dateOfApply, Plugin plugin )
    {

        WeekDefinition weekDefinition = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_RESERVATION_RULE_AND_DATE_OF_APPLY, plugin ) )
        {
            daoUtil.setInt( 1, nIdReservationRule );
            daoUtil.setDate( 2, Date.valueOf( dateOfApply ) );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                weekDefinition = buildWeekDefinition( daoUtil );
            }
        }
        return weekDefinition;

    }

    /**
     * Build a WeekDefinition business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new WeekDefinition with all its attributes assigned
     */
    private WeekDefinition buildWeekDefinition( DAOUtil daoUtil )
    {
        int nIndex = 1;
        WeekDefinition weekDefinition = new WeekDefinition( );
        weekDefinition.setIdWeekDefinition( daoUtil.getInt( nIndex++ ) );
        weekDefinition.setSqlDateOfApply( daoUtil.getDate( nIndex++ ) );
        weekDefinition.setSqlEndingDateOfApply( daoUtil.getDate( nIndex++ ) );
        weekDefinition.setIdReservationRule( daoUtil.getInt( nIndex ) );
        return weekDefinition;
    }

    /**
     * Build a daoUtil object with the WeekDefinition business object
     * 
     * @param query
     *            the query
     * @param weekDefinition
     *            the Week Definition
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, WeekDefinition weekDefinition, Plugin plugin, boolean isInsert )
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
        daoUtil.setDate( nIndex++, weekDefinition.getSqlDateOfApply( ) );
        daoUtil.setDate( nIndex++, weekDefinition.getSqlEndingDateOfApply( ) );
        daoUtil.setInt( nIndex++, weekDefinition.getIdReservationRule( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, weekDefinition.getIdWeekDefinition( ) );
        }
        return daoUtil;
    }
}
