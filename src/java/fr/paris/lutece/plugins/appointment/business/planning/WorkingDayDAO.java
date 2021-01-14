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

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Working Day objects
 * 
 * @author Laurent Payen
 *
 */
public final class WorkingDayDAO implements IWorkingDayDAO
{

    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_working_day ( day_of_week, id_reservation_rule) VALUES ( ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_working_day SET day_of_week = ?, id_reservation_rule = ? WHERE id_working_day = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_working_day WHERE id_working_day = ? ";
    private static final String SQL_QUERY_DELETE_BY_RESERVATION_RULE = "DELETE FROM appointment_working_day WHERE id_reservation_rule = ? ";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_working_day, day_of_week, id_reservation_rule FROM appointment_working_day";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_working_day = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_WEEK_DEFINITION_RULE = SQL_QUERY_SELECT_COLUMNS + " WHERE id_reservation_rule = ?";

    @Override
    public void insert( WorkingDay workingDay, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, workingDay, plugin, true ) )
        {
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                workingDay.setIdWorkingDay( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    @Override
    public void update( WorkingDay workingDay, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, workingDay, plugin, false ) )
        {
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void delete( int nIdWorkingDay, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdWorkingDay );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void deleteByIdReservationRule( int nIdReservationRule, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_RESERVATION_RULE, plugin ) )
        {
            daoUtil.setInt( 1, nIdReservationRule );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public WorkingDay select( int nIdWorkingDay, Plugin plugin )
    {
        WorkingDay workingDay = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nIdWorkingDay );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                workingDay = buildWorkingDay( daoUtil );
            }
        }
        return workingDay;
    }

    @Override
    public List<WorkingDay> findByIdReservationRule( int nIdReservationRuleRule, Plugin plugin )
    {
        List<WorkingDay> listWorkingDays = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_WEEK_DEFINITION_RULE, plugin ) )
        {
            daoUtil.setInt( 1, nIdReservationRuleRule );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listWorkingDays.add( buildWorkingDay( daoUtil ) );
            }
        }
        return listWorkingDays;
    }

    /**
     * Build a WorkingDay business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new WorkingDay with all its attributes assigned
     */
    private WorkingDay buildWorkingDay( DAOUtil daoUtil )
    {
        int nIndex = 1;
        WorkingDay workingDay = new WorkingDay( );
        workingDay.setIdWorkingDay( daoUtil.getInt( nIndex++ ) );
        workingDay.setDayOfWeek( daoUtil.getInt( nIndex++ ) );
        workingDay.setIdReservationRule( daoUtil.getInt( nIndex ) );
        return workingDay;
    }

    /**
     * Build a daoUtil object with the working day business object
     * 
     * @param query
     *            the query
     * @param workingDay
     *            the WorkingDay
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, WorkingDay workingDay, Plugin plugin, boolean isInsert )
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
        daoUtil.setInt( nIndex++, workingDay.getDayOfWeek( ) );
        daoUtil.setInt( nIndex++, workingDay.getIdReservationRule( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, workingDay.getIdWorkingDay( ) );
        }
        return daoUtil;
    }
}
